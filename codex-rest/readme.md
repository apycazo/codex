# Codex-Rest

This is a sample rest service to be used as a feature demo. Obviously APIs do not require everything here, but should 
work as a demo/reference.

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

### Check client SSL certificate

When the property `features.ssl.required` is set to `true`, the clients sending requests to the service needs to provide
a certificate registered in the keystore. 

Check the lines:

```
SslContextFactory.Server sslServer = new SslContextFactory.Server();
sslServer.setNeedClientAuth(appSettings.isSslRequired());
```

## Test certificate

Generated as `keytool -keystore dummy.jks -genkey -keyalg RSA -alias dummy-cert -validity 3650` with pwd: `secret007`.

_Note: Using `-keyalg RSA` is important, since it is the standard required._

For clients, extract the key and crt with:

`keytool -importkeystore -srckeystore dummy.jks -destkeystore dummy.p12 -deststoretype PKCS12`

For apache ssl certificate file you need certificate only:

`openssl pkcs12 -in dummy.p12 -nokeys -out dummy.crt`

For ssl key file you need only keys:

`openssl pkcs12 -in dummy.p12 -nocerts -nodes -out dummy.key`

To test SSL, include the VM options: `-Djavax.net.debug=ssl,handshake`. Including into the logger the config for
jetty also helps:
```xml
<logger name="org.eclipse.jetty.server.HttpConnection" level="debug"/>
```

## Root, server and client certificates

We will generate a root certificate, and then two others, signed with the root, to test SSL requirements over a client.

**NOTE**: When asked for the 'key' file, use the generated 'pem' file. In any case, we have used the same password for
everything, `secret007`, but this is not required.

First, create the root certificate, in the `resources/certificates/root` directory, run:

```bash
openssl req -x509 -newkey rsa:4096 -keyout root.pem -out root.crt -days 3650 -passout pass:"secret007" -subj "/C=ES/ST=Madrid/O=Development/CN=localhost"
openssl pkcs12 -export -out root.p12 -inkey root.pem -in root.crt -password pass:"secret007"
```

Now, let's create the server and client certificates (CSR) and sign them with the root. In the 
`resources/certificates/server` run:

```bash
# Creates the CSR (request)
openssl req -new -newkey rsa:4096 -out server.csr -keyout server.pem -subj "/C=ES/ST=Madrid/O=Development/CN=localhost"
# Create the certificate, from the csr request, and the root CA
openssl x509 -req -in server.csr -CA ../root/root.crt -CAkey ../root/root.pem -CAcreateserial -out server.crt -days 3650 -sha256
# Create the p12 file from the certificate and key
openssl pkcs12 -export -out server.p12 -inkey server.pem -in server.crt -password pass:"secret007"
```

And this at `resources/certificates/client`:

```bash
```bash
# Creates the CSR (request)
openssl req -new -newkey rsa:4096 -out client.csr -keyout client.pem -subj "/C=ES/ST=Madrid/O=Development/CN=localhost"
# Create the certificate, from the csr request, and the root CA
openssl x509 -req -in client.csr -CA ../root/root.crt -CAkey ../root/root.pem -CAcreateserial -out client.crt -days 3650 -sha256
# Create the p12 file from the certificate and key
openssl pkcs12 -export -out client.p12 -inkey client.pem -in client.crt -password pass:"secret007"
```

Now, to test, configure the service to use the `server.p12` keystore:

```properties
features.ssl.keystore.path = classpath:certificates/server/server.p12
features.ssl.keystore.pass = secret007
```

Also include the config to demand a valid certificate:

```properties
features.ssl.required = true
```

And try using the client values on the client (like postman) to check that it is accepted. Notice that postman does not
require the p12 file, click on `settings -> add certificate` and provide the crt, pem and password.

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
