# Golem

The aim of this module is to implement a simple inversion of control mechanism, based on the default java injection
annotations (extended in some aspects). The library provides two different mechanisms, the IoC container itself, called
'GolemContext', and a REST server extension of it, called 'GolemServer'.

This is more of a learning experiment than anything else, but it should implement a complete solution.

## Context

### Configuration

The context needs at least one class registered to provide beans. Golem context configurations need to be annotated with
`@Configuration`, and provide some package to scan. 

When a configuration class has method returning singletons, or prototypes, the container will need to instance the 
configuration class, for which it will need for the class to have a default constructor.

Additionally, these classes can also be annotated with `@Singleton`, which in turn will instance a different object to
be registered as a normal bean.

The configuration annotation accepts the following parameters:
* **scanPaths**: Provides a list of string paths to be scanned during the context initialization.
* **scanPackages**: Provided a list of classes to be used as the root paths to scan.
* **propertySources**: Lists the property sources that are mandatory to be present. Unresolvable paths will throw an
  exception on failure.
* **optionalPropertySources**: Same as `propertySources`, but these will not throw errors when the files described 
  fail to be resolved.  

*Note*: To declare a classpath-based property file (like one to be found in the `resources` directory), the path can be
prepended with `classpath:`, so the context knows where to look at.

### Singleton

Any class annotated with `@Singleton` and scanned by the context will have its definition registered, and will be 
available to be injected into any other component requiring it. 

#### Lazy singletons

By default, singletons will be eagerly initialized when the component scan phase has been completed. To avoid this, a
singleton can also include the annotation `@Lazy`, which will delay the bean instancing until some other component 
actually demands it.

#### Named singletons

When we have multiple beans able to match a required injection, either because they implement the same interface or
extend a common base class, the context will require an additional element to know which one needs to be injected.

This element is the bean name, which will match the class name (for class-based beans), or the method name (for 
configuration class method beans). A custom name can be set using the annotation `@Named`. This annotation is required
both at the bean declaration and the injection. An example of this would be:

```java
interface A {}

@Singleton @Named("option-1")
class Option1 implements A {}

@Singleton @Named("option-2")
class Option2 implements A {}

@Singleton
class Client {
  @Inject
  @Named("option-2")
  private A elementA;
}
```

#### Non-singleton beans

When a bean needs to be injected, but creating a new instance each type instead of an actual singleton, just include
the annotation `@Prototype` along the `@Singleton` one. This way the bean definition will not store the generated 
instance and thus will create a new one each time an injection requires it.

#### Resolving bean conflicts

When we have two competing beans, and we need to prioritize one of them, we can use the annotation `@Primary` to 
indicate which of them we want to inject. Having multiple `@Primary` annotated beans breaks the mechanism, which should
be reserved to testing and very specific scenarios.

### Injection

To inject a bean, use the annotation `@Inject`, either at the field, or on a given constructor. Notice that only one
constructor can be annotated with `@Inject`.

#### Non required beans

Some times we might have a missing bean (due for a condition, for example). If we do not want injections to fail because
of this, use the annotation `@NonRequired`, either along with the `@Inject` one, or to annotate a constructor parameter.

### Properties

All property sources declared at `@Configuration` beans will be included into the context property map. To inject a 
property value, use the annotation `@PropertyValue`, with the required property key to resolve. A default value can
be provided to use when the property cannot be resolved, using the format: `<key>:<defaultValue>`. An example of this
would be:

```java
class Bean {
  @PropertyValue("my.app.name:default")
  private String appName;
}
```

## Rest server

A simple http server capable or mapping rest resources can be included as an extension of a GolemContext. This server
uses the JDK-available `HttpServer` class to manage resources.

### Configuration

Since the server uses a GolemContext as a base, the properties can be scanned using a `@Configuration` annotated class,
much like the rest. The available properties to use are:

* **golem.server.http.port** [default: 8080]: Defines the port to listen to. Using the value '0' will be interpreted as
  a random port requirement.
* **golem.server.pool.size** [default: 8]: The number of threads to initialize the server thread pool.
* **golem.server.mapping.base** [default: /]: Base mapping path, will be applied to all endpoints.   

### Creating endpoints

The endpoints defined will be scanned like any other bean, and use the same package names provided by existing
`@Configuration` annotated classes, but will be scanned on a second pass, so first the regular beans will be added and
initialized, and then the rest resources will be mapped over them.

This does not mean that rest resources will not behave in many aspects just like regular beans. To declare a class
as a get resource, annotate the class with `@RestResource`. This annotation allows for the following parameters:

* **path**: the path to use as a base for the mapping, will be prepended with the server mapping base, and appended with
  any custom path each method might declare.
* **produces**: the default media type to use for any method when a more specific value cannot be determined. By 
  default, the value is an empty string, which will not return any type. Having a valid value will be sent along with 
  the response as a 'Content-Type' header.
* **consumes**: the media type these endpoints require, by default. Works mostly like the param `produces`, but in this
  case the server will require the 'Content-Type' header from the request to be present and matching the provided value,
  otherwise a 415 error will be returned.
  
Additionally, the specific methods to process requests will need to use the annotation `@Endpoint`, which is very
similar to `@RestResource`, but adds a parameter: `method`, which will determine the http method which will be attended 
by the method.

An example of a simple rest resource, which will attend requests to `/api/hi`:

```java
@RestResource(path = "/api")
class MyResource {

  @Endpoint(path = "hi")
  public String hiWorld() {
    return "Hi world!";
  }
}
```

### Mapping placeholders

To map a dynamic value into an endpoint path, use the notation `:<name>` as part of the path. For example, to fetch
a value by its id might use a resource path like `/api/resource/:id`. This id can be injected later into the method
processing the request. Note that given a path, only one dynamic value can be injected per segment and http method, 
so we cannot have another endpoint mapped like `/api/resource/:test` if both use 'GET' as method, for example.

### Injecting endpoint values

We can inject three different value types into a method processing a request:

* **Path parameters**: Any value included in the path with the notation `:<name>` can be injected using the annotation
  `@PathParam("name")`. The name value is mandatory and must match the notation name.
* **Query parameters**: Any value which can be parsed from the request url query, can be injected with the annotation
  `@QueryParam`, much like the path parameters.
* **Request content**: The request content can be mapped using the `@Body` annotation. 
* **Request header**: The annotation `@RequestHeader` will capture any header present in the request by name.
* **Http exchange**: The current HttpExchange instance can be injected just by adding it to the method parameters, with
  no additional annotations required.
  
### Serializing media types

To (de)serialize request bodies and responses, specific beans, implementing the interface `MediaTypeMapper` are used.
By default, mappers for `text/plain`, `application/json` and `image/*` media types will be registered by default when no 
others can be found.

A custom implementation can be provided, which will be used instead of these, as long as they map the same media type, 
and they are declared as beans (they should NOT be prototypes, though). Mapper beans will be initialized lazily.

The default implementations available are `JsonMediaTypeMapper`, `PlainTextMediaTypeMapper` and `BinaryMediaTypeMapper`.  

### Handling errors

When a method throws an exception, the server handler will try to find a specific `ExceptionMapper` to transform it into
a `Response` type. Failing to find one, the response will be a simple text message with the exception text, and a http
status stating a request error has happened (HTTP status 400).

Exception managers must be singletons implementing the interface `ExceptionMapper`.

### Current request data

A thread local is used to keep data related to the current request (by default, includes the request ID and the 
HttpExchange being resolved).

To use, just call the appropriate static method of the class `CurrentRequest`. For example: 
```java
@Endpoint(method = HttpMethod.GET, path = "/id", produces = MediaType.APPLICATION_JSON)
public Map<String, String> getAttribute() {
  return Map.of("id", CurrentRequest.getId());
}
```

### Request and response filters

A request/response filter can be added just by adding a bean implementing whatever interface is required: 
`RequestFilter` for requests, and `ResponseFilter` for responses. The filters can override the 'getOrder' method
to ensure one filter is applied before another.

Lower values will get higher priority. Default value is 5000. The recommended level groups are:

- 1xxx: Gateway/Reception.
- 2xxx: Authentication.
- 3xxx: Authorization.
- 4xxx: Server core.
- 5xxx: User level.

### Limitations and things to notice

- Rest endpoints can not map simple types, only objects (this is, need to use Integer instead of int, for example).
- The default pool size if fairly small (8), since this is meant to be used for small applications.

# Backlog:

- pre-generated actuator endpoints.
- ssl connections (HttpsServer instead of HttpServer). 
- remote configuration and refreshable properties.
- test fat-jar builds.