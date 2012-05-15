/*
 * Copyright 2012 the original author or authors.
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

package org.vertx.osgi;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.osgi.framework.BundleContext;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTracker;
import org.osgi.util.tracker.ServiceTrackerCustomizer;
import org.vertx.java.core.Handler;
import org.vertx.java.core.Vertx;
import org.vertx.java.core.http.HttpServer;
import org.vertx.java.core.http.HttpServerRequest;

final class HandlerListener {

    private Vertx vertx;

    private BundleContext bundleContext;

    private ServiceTrackerCustomizer<Handler<?>, Handler<?>> handlerTracker;

    private ServiceTracker<Handler<?>, Handler<?>> serviceTracker;

    private final ConcurrentMap<Integer, HttpServer> servers = new ConcurrentHashMap<Integer, HttpServer>();

    public void start(BundleContext bundleContext, Vertx vertx) {
        this.bundleContext = bundleContext;
        this.vertx = vertx;
        registerHandlerListener();
        scanForExistingHandlers();
    }

    private void registerHandlerListener() {
        this.handlerTracker = new HandlerTracker();
        this.serviceTracker = new ServiceTracker<Handler<?>, Handler<?>>(this.bundleContext, Handler.class.getName(), this.handlerTracker);
        this.serviceTracker.open();
    }

    public void stop() {
        this.serviceTracker.close();
        for (int portNumber : this.servers.keySet()) {
            try {
                deregisterHandler(portNumber);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void scanForExistingHandlers() {
        try {
            ServiceReference<?>[] allServiceReferences = this.bundleContext.getAllServiceReferences("org.vertx.java.core.Handler", null);
            if (allServiceReferences != null) {
                for (ServiceReference<?> serviceReference : allServiceReferences) {
                    registerHandlerIfNecessary(serviceReference);
                }
            }
        } catch (InvalidSyntaxException e) {
            e.printStackTrace();
        }
    }

    private void registerHandlerIfNecessary(ServiceReference<?> serviceReference) {
        @SuppressWarnings({ "unchecked" })
        ServiceReference<Handler<?>> handlerServiceReference = (ServiceReference<Handler<?>>) serviceReference;
        if ("HttpServerRequestHandler".equals(getHandlerType(handlerServiceReference))) {
            int portNumber = getHandlerPortNumber(handlerServiceReference);
            if (portNumber != 0) {
                @SuppressWarnings("unchecked")
                Handler<HttpServerRequest> handler = (Handler<HttpServerRequest>) this.bundleContext.getService(handlerServiceReference);
                try {
                    registerHandler(portNumber, handler);
                } finally {
                    this.bundleContext.ungetService(serviceReference);
                }
            }
        }
    }

    private String getHandlerType(ServiceReference<Handler<?>> serviceReference) {
        Object handlerType = serviceReference.getProperty("type");
        return handlerType instanceof String ? (String) handlerType : null;
    }

    private int getHandlerPortNumber(ServiceReference<Handler<?>> handlerServiceReference) {
        int portNumber = 0;
        Object port = handlerServiceReference.getProperty("port");
        if (port instanceof String) {
            portNumber = Integer.parseInt((String) port);
        }
        return portNumber;
    }

    private void registerHandler(int portNumber, Handler<HttpServerRequest> handler) {
        HttpServer httpServer = getHttpServer(portNumber);
        try {
            httpServer.requestHandler(handler).listen(portNumber);
        } catch (Exception e) {
        }
    }

    private void deregisterHandler(int portNumber) {
        HttpServer httpServer = findHttpServer(portNumber);
        if (httpServer != null) {
            httpServer.close();
            servers.remove(portNumber);
        }
    }

    private HttpServer getHttpServer(int portNumber) {
        HttpServer httpServer = this.servers.get(portNumber);
        if (httpServer == null) {
            httpServer = this.vertx.createHttpServer();
            HttpServer existingHttpServer = this.servers.putIfAbsent(portNumber, httpServer);
            if (existingHttpServer != null) {
                httpServer = existingHttpServer;
            }
        }
        return httpServer;
    }

    private HttpServer findHttpServer(int portNumber) {
        return servers.get(portNumber);
    }

    private final class HandlerTracker implements ServiceTrackerCustomizer<Handler<?>, Handler<?>> {

        @Override
        public Handler<?> addingService(ServiceReference<Handler<?>> serviceReference) {
            Handler<?> handler = bundleContext.getService(serviceReference);
            registerHandlerIfNecessary(serviceReference);
            return handler;
        }

        @Override
        public void modifiedService(ServiceReference<Handler<?>> serviceReference, Handler<?> handler) {
        }

        @Override
        public void removedService(ServiceReference<Handler<?>> serviceReference, Handler<?> handler) {
            unregisterHandlerIfNecessary(serviceReference);
        }

        private void unregisterHandlerIfNecessary(ServiceReference<Handler<?>> serviceReference) {
            ServiceReference<Handler<?>> handlerServiceReference = (ServiceReference<Handler<?>>) serviceReference;
            if ("HttpServerRequestHandler".equals(getHandlerType(handlerServiceReference))) {
                int portNumber = getHandlerPortNumber(handlerServiceReference);
                if (portNumber != 0) {
                    deregisterHandler(portNumber);
                }
            }
        }

    }

}
