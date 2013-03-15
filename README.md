vert.x.osgi
===========

Experiment running [vert.x](http://vertx.io/) as OSGi bundles in the Virgo kernel. See [this blog](http://underlap.blogspot.co.uk/2012/06/osgi-case-study-modular-vertx.html) for details.

Getting Started
---------------

0. Read the blog above

0. Clone this [git repository](https://github.com/glyn/vert.x.osgi)

0. Ensure you have Java 7 with JAVA_HOME set appropriately (on the Mac, I did this: `export JAVA7_HOME="/Library/Java/JavaVirtualMachines/jdk1.7.0_17.jdk/Contents/Home/"` and then `export JAVA_HOME=$JAVA7_HOME; export PATH=$JAVA7_HOME/bin:$PATH`)

0. cd to the `virgo-kernel-3.6.0.BUILD-20121001132112` directory of the github clone

0. Start Virgo by issuing `bin/startup.sh -clean` on *ix or `bin\startup.bat -clean` on Windows

0. After it has started, browse to [`http://localhost:8091/`](http://localhost:8091/) enter some text in the box and click the button

Details
-------

The bundle project org.vertx.osgi listens for Handler instances being published in the OSGi service registry and registers these with a suitable server (which it also creates) or the event bus (according to the service properties in the service registry). It also publishes the the event bus to the service registry.

The bundle project org.vertx.osgi.sample.basic uses the OSGi Blueprint service to publish a HTTP request Handler to the service registry.

The bundle project org.vertx.osgi.sample.sockjs uses the OSGi Blueprint service to publish HTTP request and sockjs Handlers to the service registry.

The bundle project org.vertx.osgi.mod.mongodb is the handler part of the mongo-persistor busmod.

The bundle project org.vertx.osgi.sample.mongo uses the event bus (obtained from the service registry) to send a message to the mongo-persistor. 

The source code provided in this project is dual-licensed under the Eclipse Public License 1.0 and the Apache License, Version 2.0.

Binary bundles, ready to use on the OSGi framework of your choice, are provided in the bundles directory.

Notes
-----

*   The bundling directory shows how the vert.x core and platform JARs were turned into OSGi bundles - see the README.
*   The virgo-kernel-3.5.0.xxx directory contains an unzipped download of the Virgo kernel with vert.x core bundle in repository/usr along with netty and jackson dependencies. It contains the fix to [bug 370253](https://bugs.eclipse.org/bugs/show_bug.cgi?id=370253) which removes a minor Java 7 problem in the Virgo kernel. This fix will appear in Virgo 3.5.0.RELEASE.
*   Gemini Blueprint 1.0.1.M01 bundles replace the 1.0.0.RELEASE version in virgo-kernel-3.5.0.xxx/repository/ext to solve [bug 379384](https://bugs.eclipse.org/bugs/show_bug.cgi?id=379384). This fix will appear in Gemini Blueprint 1.0.1.RELEASE.
