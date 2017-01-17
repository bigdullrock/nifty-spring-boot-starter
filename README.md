
#Spring boot starter for [Facebook's Nifty.](https://github.com/facebook/nifty)


## Features
Auto-configures and run the embedded Nifty server with @NiftyHandler-enabled beans as part of spring-boot application.

## Setup
```gradle
dependencies {
    compile('com.bigdullrock:nifty-spring-boot-starter:0.8.0')
}
```
```xml
<dependency>
    <groupId>com.bigdullrock</groupId>
    <artifactId>nifty-spring-boot-starter</artifactId>
    <version>0.8.0</version>
    <scope>compile</scope>
</dependency>
```

## Usage
* Start by generating stub and server interface(s) from your `.thrift` file(s).
* Annotate your server interface implementation(s) with `@com.bigdullrock.spring.boot.nifty.NiftyHandler`
* Optionally configure the server port in your `application.yml/properties`. Default port is `8080`

```yaml
 nifty:
    port : 5000
```

## Showcase
In the 'nifty-spring-boot-starter-demo' project you can find fully functional example with integration test.
The service definition from `.thrift` file looks like this :
```thrift
namespace java com.bigdullrock.thrift
// The request message containing the user's name.
struct HelloRequest {
    1: string name;
}

// The response message containing the greetings
struct HelloReply {
    1: string message;
}

service Greeter {
    // Sends a greeting
    HelloReply sayHello(1: HelloRequest req)
}
```
The generated classes were intentionally  committed for demo purposes.

All you need to do is to annotate your service implementation with `@com.bigdullrock.spring.boot.nifty.NiftyHandler`

```java
package com.bigdullrock.spring.boot.nifty.demo;

import com.bigdullrock.spring.boot.nifty.NiftyHandler;
import com.bigdullrock.thrift.Greeter;
import com.bigdullrock.thrift.HelloReply;
import com.bigdullrock.thrift.HelloRequest;

import org.apache.thrift.TException;

@NiftyHandler
public class GreeterService implements Greeter.Iface {

  @Override
  public HelloReply sayHello(HelloRequest req) throws TException {
    return new HelloReply("Hello " + req.getName());
  }
}
```

```java
package com.bigdullrock.spring.boot.nifty.demo;

import com.bigdullrock.thrift.Greeter;
import com.bigdullrock.thrift.HelloReply;
import com.bigdullrock.thrift.HelloRequest;

import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TMultiplexedProtocol;
import org.apache.thrift.transport.TSocket;

public class SampleClient {

  public static void main(final String[] args) throws Exception {
    TSocket transport = new TSocket("localhost", 8000);
    transport.open();
    TBinaryProtocol protocol = new TBinaryProtocol(transport);
    TMultiplexedProtocol mp = new TMultiplexedProtocol(protocol, "Greeter");
    Greeter.Client client = new Greeter.Client(mp);
    HelloRequest req = new HelloRequest("world");
    HelloReply rep = client.sayHello(req);
    System.out.println("Message: " + rep.message);
  }

}
```

## License
Apache 2.0
