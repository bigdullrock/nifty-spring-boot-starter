package com.bigdullrock.spring.boot.nifty.autoconfigure;

import com.bigdullrock.spring.boot.nifty.NiftyHandler;
import com.bigdullrock.spring.boot.nifty.NiftyServerRunner;
import com.bigdullrock.spring.boot.nifty.aop.ExceptionsNiftyMethodInterceptor;

import org.springframework.aop.framework.ProxyFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnClass(NiftyHandler.class)
@EnableConfigurationProperties(NiftyServerProperties.class)
public class NiftyAutoConfiguration {

  public interface NiftyConfigurer {
    void configureProxyFactory(ProxyFactory proxyFactory);
  }

  @Bean
  @ConditionalOnMissingBean(NiftyConfigurer.class)
  public NiftyConfigurer niftyConfigurer() {
    return new DefaultNiftyConfigurer();
  }

  public static class DefaultNiftyConfigurer implements NiftyConfigurer {

    public void configureProxyFactory(final ProxyFactory proxyFactory) {
      proxyFactory.setOptimize(true);
      proxyFactory.addAdvice(new ExceptionsNiftyMethodInterceptor());
    }
  }

  @Bean
  public NiftyServerRunner niftyServerRunner() {
    return new NiftyServerRunner();
  }

}
