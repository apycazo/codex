# Codex-Jersey

## Info

Simple jersey+spring REST API implementation, using the java-default http server.

## Warnings

The warning `A provider java.lang.String registered in SERVER runtime does not implement` is shown
because we are registering an instance instead of a class in the ResourceConfig. 

## Platform

This project could use the jersey platform instead:

```groovy
dependencies {
    implementation plaform("org.glassfish.jersey:jersey-bom:2.30")
}
```

## Jersey

Notice that the `org.glassfish.jersey.media:jersey-media-json-jackson` dependency will register:
* JacksonJaxbJsonProvider
* JsonParseExceptionMapper
* JsonMappingExceptionMapper