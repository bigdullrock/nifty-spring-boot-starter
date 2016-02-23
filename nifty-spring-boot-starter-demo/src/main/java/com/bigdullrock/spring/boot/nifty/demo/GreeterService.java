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
