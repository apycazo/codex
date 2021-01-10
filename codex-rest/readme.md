# Codex-Rest

This is a sample rest service to be used as a feature demo. Obviously APIs do not required everything here, but should 
work as a demo.

## Servlets

This example uses two different servlets, one to serve the Rest-API (using jersey), and another to serve static content.
It is important the namespaces of these two do not collide. For example, mapping the Jersey servlet to `/` and the
static servlet to `/public` will make the Jersey servlet to capture the requests, preventing the other servlet to work.

To have both things to work in parallel it is required to serve Jersey as a filter, rather than a servlet. Using the
default configuration the mapping is:

* Jersey: `/api/*`
* Static: `/*`

Since the jersey servlet is loaded before the static one, there is no collision between the two of them. The idea is
for the service to show some info about itself when accessing the root endpoint (for example, the service documentation
or a sandbox).

The servlets are configured in the `JettyConfig` class. 

## Security

### Master user/password

There is a master user password that can be used to authenticate using basic auth.

TODO: Store password cyphered (or at least encoded).

## Test certificate

Generated as `keytool -keystore dummy.jks -genkey -keyalg RSA -alias dummy-cert` with pwd: `secret007`.

_Note: Using `-keyalg RSA` is important, since it is the standard required._

To test SSL, include the VM options: `-Djavax.net.debug=ssl,handshake`. Including into the logger the config for
jetty also helps:
```xml
<logger name="org.eclipse.jetty.server.HttpConnection" level="debug"/>
```

## Persistence 

TODO

## Others

* ✔ CORS support.
* ✔ SSL support.
* ✔ Documentation (asciidoc).
* ✔ Service properties (gradle build info).
* Persistence (h2 database)
* Monitoring info.
* ✔ Logback & resource info MDC.
* Syslog appender.
* SSL authentication.
