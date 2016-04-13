package com.bigdullrock.spring.boot.nifty.demo;

import static org.junit.Assert.assertEquals;

import com.bigdullrock.thrift.Greeter;
import com.bigdullrock.thrift.HelloReply;
import com.bigdullrock.thrift.HelloRequest;

import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TMultiplexedProtocol;
import org.apache.thrift.transport.TSocket;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(GreeterApplication.class)
@IntegrationTest
public class GreeterServiceTest {

  @Value("${nifty.port}")
  public int port;

  private Greeter.Client client;

  @Before
  public void setUp() throws Exception {
    TSocket transport = new TSocket("localhost", port);
    transport.open();
    TMultiplexedProtocol mp =
        new TMultiplexedProtocol(new TBinaryProtocol(transport), "greeterService");
    client = new Greeter.Client(mp);
  }

  @Test
  public void testSimpleCall() throws Exception {
    HelloRequest msg = new HelloRequest("Dude");
    HelloReply resp = client.sayHello(msg);
    assertEquals(resp.message, "Hello Dude");
  }
}
