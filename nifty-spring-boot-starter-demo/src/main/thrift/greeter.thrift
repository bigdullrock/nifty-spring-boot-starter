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
