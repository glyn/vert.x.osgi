vert.x.osgi
===========

Experiment running vert.x core as an OSGi bundle in Virgo kernel.

The bundle project org.vertx.osgi listens for Handler instances being published in the OSGi service registry and registers these with a suitable server (according to the service properties in the service registry).

The bundle project org.vertx.osgi.sample.basic uses the OSGi Blueprint service to publish a HTTP request Handler to the service registry.

The bundle project org.vertx.osgi.sample.sockjs uses the OSGi Blueprint service to publish HTTP request and sockjs Handlers to the service registry.

The source code provided in this project is dual-licensed under the Eclipse Public License 1.0 and the Apache License, Version 2.0.

Notes
-----

*   The bundling directory shows how the vert.x core JAR was turned into an OSGi bundle - see the README.
*   The virgo-kernel-3.5.0.xxx directory contains an unzipped download of the Virgo kernel with vert.x core bundle in repository/usr along with netty and jackson dependencies. It contains the fix to [bug 370253](https://bugs.eclipse.org/bugs/show_bug.cgi?id=370253) which removes a minor Java 7 problem in the Virgo kernel.
*   The file virgo-kernel-3.5.0.xxx/repository/ext/org.eclipse.gemini.blueprint.extender_1.0.0.RELEASE.jar was patched to work around [bug 379384](https://bugs.eclipse.org/bugs/show_bug.cgi?id=379384).
