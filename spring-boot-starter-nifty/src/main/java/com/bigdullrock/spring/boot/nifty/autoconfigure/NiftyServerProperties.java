package com.bigdullrock.spring.boot.nifty.autoconfigure;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("nifty")
public class NiftyServerProperties {

  private Integer port;

  public Integer getPort() {
    return port;
  }

  public void setPort(final Integer port) {
    this.port = port;
  }
}
