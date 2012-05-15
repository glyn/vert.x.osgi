package org.vertx.osgi;

import org.osgi.framework.BundleContext;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;
import org.vertx.java.core.Handler;
import org.vertx.java.core.Vertx;
import org.vertx.java.core.http.HttpServer;
import org.vertx.java.core.http.HttpServerRequest;

class HandlerListener {

	private Vertx vertx;

	private BundleContext bundleContext;

	public void start(BundleContext bundleContext, Vertx vertx) {
		this.bundleContext = bundleContext;
		this.vertx = vertx;
		registerHandlerListener();
		scanForExistingHandlers();
	}

	private void registerHandlerListener() {
		// TODO Auto-generated method stub

	}

	private void scanForExistingHandlers() {
		try {
			ServiceReference<?>[] allServiceReferences = this.bundleContext
					.getAllServiceReferences("org.vertx.java.core.Handler",
							null);
			if (allServiceReferences != null) {
				for (ServiceReference<?> serviceReference : allServiceReferences) {
					@SuppressWarnings({ "rawtypes", "unchecked" })
					ServiceReference<org.vertx.java.core.Handler> handlerServiceReference = (ServiceReference<Handler>) serviceReference;
					Object handlerType = handlerServiceReference
							.getProperty("type");
					if ("HttpServerRequestHandler".equals(handlerType)) {
						Object port = handlerServiceReference
								.getProperty("port");
						if (port instanceof String) {
							int portNumber = Integer.parseInt((String) port);

							@SuppressWarnings("unchecked")
							Handler<HttpServerRequest> handler = (Handler<HttpServerRequest>) this.bundleContext
									.getService(handlerServiceReference);
							try {
								registerHandler(portNumber, handler);
							} finally {
								this.bundleContext
										.ungetService(serviceReference);
							}
						}
					}
				}
			}
		} catch (InvalidSyntaxException e) {
			e.printStackTrace();
		}
	}

	private void registerHandler(int portNumber,
			Handler<HttpServerRequest> handler) {
		HttpServer httpServer = this.vertx.createHttpServer();
		try {
			httpServer.requestHandler(handler).listen(portNumber);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void stop() {

	}

}
