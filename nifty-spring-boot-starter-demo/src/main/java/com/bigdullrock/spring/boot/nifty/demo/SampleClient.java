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
    TMultiplexedProtocol mp = new TMultiplexedProtocol(protocol, "greeterService");
    Greeter.Client client = new Greeter.Client(mp);
    HelloRequest req = new HelloRequest("world");
    HelloReply rep = client.sayHello(req);
    System.out.println("Message: " + rep.message);
  }

}