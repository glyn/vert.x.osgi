vert.x.osgi
===========

Experiment running [vert.x](http://vertx.io/) as OSGi bundles in the Virgo kernel.

The bundle project org.vertx.osgi listens for Handler instances being published in the OSGi service registry and registers these with a suitable server (which it also creates) or the event bus (according to the service properties in the service registry). It also publishes the the event bus to the service registry.

The bundle project org.vertx.osgi.sample.basic uses the OSGi Blueprint service to publish a HTTP request Handler to the service registry.

The bundle project org.vertx.osgi.sample.sockjs uses the OSGi Blueprint service to publish HTTP request and sockjs Handlers to the service registry.

The bundle project org.vertx.osgi.mod.mongodb is the handler part of the mongo-persistor busmod.

The bundle project org.vertx.osgi.sample.mongo uses the event bus (via the service registry) to send a message to the mongo-persistor. 

The source code provided in this project is dual-licensed under the Eclipse Public License 1.0 and the Apache License, Version 2.0.

Notes
-----

*   The bundling directory shows how the vert.x core and platform JARs were turned into OSGi bundles - see the README.
*   The virgo-kernel-3.5.0.xxx directory contains an unzipped download of the Virgo kernel with vert.x core bundle in repository/usr along with netty and jackson dependencies. It contains the fix to [bug 370253](https://bugs.eclipse.org/bugs/show_bug.cgi?id=370253) which removes a minor Java 7 problem in the Virgo kernel. This fix will appear in Virgo 3.5.0.RELEASE.
*   Gemini Blueprint 1.0.1.M01 bundles replace the 1.0.0.RELEASE version in virgo-kernel-3.5.0.xxx/repository/ext to solve [bug 379384](https://bugs.eclipse.org/bugs/show_bug.cgi?id=379384). This fix will appear in Gemini Blueprint 1.0.1.RELEASE.
