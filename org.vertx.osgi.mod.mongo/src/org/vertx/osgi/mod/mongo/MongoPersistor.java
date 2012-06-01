/*
 * Copyright 2011-2012 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.vertx.osgi.mod.mongo;

import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.Mongo;
import com.mongodb.MongoException;
import com.mongodb.WriteResult;
import com.mongodb.util.JSON;
import org.vertx.java.busmods.BusModBase;
import org.vertx.java.core.Handler;
import org.vertx.java.core.eventbus.Message;
import org.vertx.java.core.json.JsonArray;
import org.vertx.java.core.json.JsonObject;

import java.net.UnknownHostException;
import java.util.UUID;

/**
 * MongoDB Persistor Bus Module
 * <p>
 * Please see the busmods manual for a full description
 * <p>
 * 
 * @author <a href="http://tfox.org">Tim Fox</a>
 */
public class MongoPersistor extends BusModBase implements Handler<Message<JsonObject>> {

    private final String host;

    private final int port;

    private final String dbName;

    private Mongo mongo;

    private DB db;

    public MongoPersistor(String host, int port, String dbName) throws UnknownHostException, MongoException {
        this.host = host;
        this.port = port;
        this.dbName = dbName;

        this.mongo = new Mongo(host, port);
        this.db = this.mongo.getDB(dbName);
    }

    public void stop() {
        mongo.close();
    }

    public void handle(Message<JsonObject> message) {

        String action = message.body.getString("action");

        if (action == null) {
            sendError(message, "action must be specified");
            return;
        }

        switch (action) {
            case "save":
                doSave(message);
                break;
            case "find":
                doFind(message);
                break;
            case "findone":
                doFindOne(message);
                break;
            case "delete":
                doDelete(message);
                break;
            default:
                sendError(message, "Invalid action: " + action);
                return;
        }
    }

    private void doSave(Message<JsonObject> message) {
        String collection = getMandatoryString("collection", message);
        if (collection == null) {
            return;
        }
        JsonObject doc = getMandatoryObject("document", message);
        if (doc == null) {
            return;
        }
        String genID;
        if (doc.getField("_id") == null) {
            genID = UUID.randomUUID().toString();
            doc.putString("_id", genID);
        } else {
            genID = null;
        }
        DBCollection coll = db.getCollection(collection);
        DBObject obj = jsonToDBObject(doc);
        WriteResult res = coll.save(obj);
        if (res.getError() == null) {
            if (genID != null) {
                JsonObject reply = new JsonObject();
                reply.putString("_id", genID);
                sendOK(message, reply);
            } else {
                sendOK(message);
            }
        } else {
            sendError(message, res.getError());
        }
    }

    private void doFind(Message<JsonObject> message) {
        String collection = getMandatoryString("collection", message);
        if (collection == null) {
            return;
        }
        Integer limit = (Integer) message.body.getNumber("limit");
        if (limit == null) {
            limit = -1;
        }
        Integer batchSize = (Integer) message.body.getNumber("batch_size");
        if (batchSize == null) {
            batchSize = 100;
        }
        JsonObject matcher = getMandatoryObject("matcher", message);
        if (matcher == null) {
            return;
        }
        JsonObject sort = message.body.getObject("sort");
        DBCollection coll = db.getCollection(collection);
        DBCursor cursor = coll.find(jsonToDBObject(matcher));
        if (limit != -1) {
            cursor.limit(limit);
        }
        if (sort != null) {
            cursor.sort(jsonToDBObject(sort));
        }
        sendBatch(message, cursor, batchSize);
    }

    private void sendBatch(Message<JsonObject> message, final DBCursor cursor, final int max) {
        int count = 0;
        JsonArray results = new JsonArray();
        while (cursor.hasNext() && count < max) {
            DBObject obj = cursor.next();
            String s = obj.toString();
            JsonObject m = new JsonObject(s);
            results.add(m);
            count++;
        }
        if (cursor.hasNext()) {
            JsonObject reply = createBatchMessage("more-exist", results);

            // Set a timeout, if the user doesn't reply within 10 secs, close the cursor
            final long timerID = vertx.setTimer(10000, new Handler<Long>() {

                public void handle(Long timerID) {
                    container.getLogger().warn("Closing DB cursor on timeout");
                    try {
                        cursor.close();
                    } catch (Exception ignore) {
                    }
                }
            });

            message.reply(reply, new Handler<Message<JsonObject>>() {

                @SuppressWarnings({ "rawtypes", "unchecked" })
                public void handle(Message msg) {
                    vertx.cancelTimer(timerID);
                    // Get the next batch
                    sendBatch(msg, cursor, max);
                }
            });

        } else {
            JsonObject reply = createBatchMessage("ok", results);
            message.reply(reply);
            cursor.close();
        }
    }

    private JsonObject createBatchMessage(String status, JsonArray results) {
        JsonObject reply = new JsonObject();
        reply.putArray("results", results);
        reply.putString("status", status);
        return reply;
    }

    protected void sendMoreExist(String status, Message<JsonObject> message, JsonObject json) {
        json.putString("status", status);
        message.reply(json, new Handler<Message<JsonObject>>() {

            @SuppressWarnings("rawtypes")
            public void handle(Message msg) {

            }
        });
    }

    private void doFindOne(Message<JsonObject> message) {
        String collection = getMandatoryString("collection", message);
        if (collection == null) {
            return;
        }
        JsonObject matcher = message.body.getObject("matcher");
        DBCollection coll = db.getCollection(collection);
        DBObject res;
        if (matcher == null) {
            res = coll.findOne();
        } else {
            res = coll.findOne(jsonToDBObject(matcher));
        }
        JsonObject reply = new JsonObject();
        if (res != null) {
            String s = res.toString();
            JsonObject m = new JsonObject(s);
            reply.putObject("result", m);
        }
        sendOK(message, reply);
    }

    private void doDelete(Message<JsonObject> message) {
        String collection = getMandatoryString("collection", message);
        if (collection == null) {
            return;
        }
        JsonObject matcher = getMandatoryObject("matcher", message);
        if (matcher == null) {
            return;
        }
        DBCollection coll = db.getCollection(collection);
        DBObject obj = jsonToDBObject(matcher);
        WriteResult res = coll.remove(obj);
        int deleted = res.getN();
        JsonObject reply = new JsonObject().putNumber("number", deleted);
        sendOK(message, reply);
    }

    private DBObject jsonToDBObject(JsonObject object) {
        String str = object.encode();
        return (DBObject) JSON.parse(str);
    }

    @Override
    public String toString() {
        return "MongoPersistor [host=" + host + ", port=" + port + ", dbName=" + dbName + "]";
    }

}
