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

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.vertx.java.core.Vertx;
import org.vertx.java.core.impl.DefaultVertxFactory;

public class Activator implements BundleActivator {

    private HandlerListener handlerListener;

    @Override
    public void start(BundleContext bundleContext) throws Exception {
        DefaultVertxFactory vertxFactory = new DefaultVertxFactory();
        Vertx vertx = vertxFactory.createVertx();
        this.handlerListener = new HandlerListener();
        this.handlerListener.start(bundleContext, vertx);
    }

    @Override
    public void stop(BundleContext bundleContext) throws Exception {
        if (this.handlerListener != null) {
            this.handlerListener.stop();
            this.handlerListener = null;
        }
    }

}
