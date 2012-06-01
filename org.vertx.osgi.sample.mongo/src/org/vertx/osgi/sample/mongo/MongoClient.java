/*
 * Copyright 2012 the original author or authors.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
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

package org.vertx.osgi.sample.mongo;

import org.vertx.java.core.Handler;
import org.vertx.java.core.eventbus.EventBus;
import org.vertx.java.core.eventbus.Message;
import org.vertx.java.core.json.JsonObject;

public final class MongoClient {

    public MongoClient(EventBus eventBus, String address) {
        JsonObject msg = new JsonObject("{ \"action\": \"save\", \"collection\": \"vertx.osgi\", \"document\": {\"x\": \"y\"}}");
        System.out.println("Sending message : " + msg.toMap().toString());
        eventBus.send(address, msg, new Handler<Message<JsonObject>>(){
            @Override
            public void handle(Message<JsonObject> event) {
                System.out.println("Message response " + event.body.toMap().toString());
            }});
        System.out.println("Message sent");
    }

}