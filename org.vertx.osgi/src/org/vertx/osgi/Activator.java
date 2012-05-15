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
		this.handlerListener.stop();
	}

}
