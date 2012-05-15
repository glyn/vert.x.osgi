vert.x.osgi
===========

Experiment running vert.x core as an OSGi bundle in Virgo kernel.

The bundle project org.vertx.osgi listens for Handler instances being published in the OSGi service registry and registers these with a suitable server (according to the service properties in the service registry).

The bundle project org.vertx.osgi.sample.basic uses the OSGi Blueprint service to publish a Handler to the service registry. 

Notes
=====

# The bundling directory shows how the vert.x core JAR was turned into an OSGi bundle - see the README
# The virgo-kernel-3.5.0.M04 directory contains an unzipped download of the Virgo kernel with vert.x core bundle in repository/usr along with netty and jackson dependencies 
# The file virgo-kernel-3.5.0.M04/repository/ext/org.eclipse.gemini.blueprint.extender_1.0.0.RELEASE.jar was patched to work around   
  https://bugs.eclipse.org/bugs/show_bug.cgi?id=379384
