package com.bigdullrock.spring.boot.nifty.autoconfigure;

import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TCompactProtocol;
import org.apache.thrift.protocol.TProtocolFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("nifty")
public class NiftyServerProperties {

  private Integer port;
  private ProtocolType protocolType = ProtocolType.BINARY;

  public Integer getPort() {
    return port;
  }

  public void setPort(final Integer port) {
    this.port = port;
  }

  public ProtocolType getProtocolType() {
    return protocolType;
  }

  public void setProtocolType(ProtocolType protocolType) {
    this.protocolType = protocolType;
  }

  public static enum ProtocolType {
    BINARY, COMPACT;

    public TProtocolFactory getProtocolFactory() {
      switch (this) {
        case COMPACT:
          return new TCompactProtocol.Factory();
        case BINARY:
          return new TBinaryProtocol.Factory();
        default:
          return new TBinaryProtocol.Factory();
      }
    }
  }
}
