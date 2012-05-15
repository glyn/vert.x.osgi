package org.vertx.osgi.sample.basic;

import org.vertx.java.core.Handler;
import org.vertx.java.core.http.HttpServerRequest;

public final class HttpServerRequestHandler implements
		Handler<HttpServerRequest> {

	public void handle(HttpServerRequest req) {
		String file = req.path.equals("/") ? "index.html" : req.path;
		req.response.sendFile("webroot/" + file);
	}
}