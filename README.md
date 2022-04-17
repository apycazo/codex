# Codex
This is a reference code library for multiple libraries, see the contents below.

## Contents

+ **codex-docker**: using docker and docker compose to package a simple app.
+ **codex-feign**: Simple Http client implementation using open feign.
+ **codex-golem**: an implementation of an IoC container, with http support.
+ **codex-gradleplugin**: create a simple plugin for gradle.
+ **codex-hibernate**: how to use plain hibernate, hibernate+spring, and the embedded h2 database.
+ **codex-java**: basic java language examples.
+ **codex-javalin**: a simple Javalin based rest API.
+ **codex-jersey**: jersey rest reference.
+ **codex-jetty**: jetty server example.  
+ **codex-kotlin**: language references for kotlin.
+ **codex-micronaut**: rest API using the micronaut framework.
+ **codex-minion**: [_legacy_] a lab project on how to implement dependency injection.
+ **codex-picocli**: command line example with PicoCLI.
+ **codex-platform**: project-wide gradle platform (BOM) for dependency management.
+ **codex-rest**: rest service implementation.
+ **codex-testing**: code testing examples.
+ **codex-k8s**: kubernetes documentation and sample app for testing.

## Reference platforms

**codex** (included by default)
```groovy
implementation platform(project(":codex-platform"))
```

**jersey-bom**
```groovy
implementation platform("org.glassfish.jersey:jersey-bom:2.30")
```

**micronaut-bom**
```groovy
implementation platform("io.micronaut:micronaut-bom:2.0.0.M2")
```

**spring**
```groovy
implementation platform("io.spring.platform:platform-bom:Cairo-SR8")
```

## TODO

- Validation API
- OAuth2
- API gateway + security.
