package com.bigdullrock.spring.boot.nifty;

import com.bigdullrock.spring.boot.nifty.autoconfigure.NiftyAutoConfiguration.NiftyConfigurer;
import com.bigdullrock.spring.boot.nifty.autoconfigure.NiftyServerProperties;
import com.facebook.nifty.core.NettyServerTransport;
import com.facebook.nifty.core.ThriftServerDef;
import com.facebook.nifty.core.ThriftServerDefBuilder;

import org.apache.thrift.TMultiplexedProcessor;
import org.apache.thrift.TProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.aop.target.SingletonTargetSource;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Configuration
public class NiftyServerRunner implements ApplicationRunner, DisposableBean {

  private static final Logger LOG =
      LoggerFactory.getLogger(NiftyServerRunner.class);

  @Autowired
  private ApplicationContext applicationContext;

  @Autowired
  private NiftyServerProperties niftyServerProperties;

  @Autowired
  private NiftyConfigurer niftyConfigurer;

  private NettyServerTransport nettyServer;

  @Override
  public void run(final ApplicationArguments args) throws Exception {
    TMultiplexedProcessor multiplexedProcessor = new TMultiplexedProcessor();

    for (Object niftyHandler : applicationContext.getBeansWithAnnotation(NiftyHandler.class)
        .values()) {
      String processorName = niftyHandler.getClass().getSimpleName();
      TProcessor tproc = getTProcessor(niftyHandler);
      LOG.info("Registering Nifty processor {}: {}", processorName, tproc);
      multiplexedProcessor.registerProcessor(processorName, tproc);
    }

    ThriftServerDefBuilder thriftServerDefBuilder =
        new ThriftServerDefBuilder().withProcessor(multiplexedProcessor);
    if (niftyServerProperties != null && niftyServerProperties.getPort() != null) {
      thriftServerDefBuilder.listen(niftyServerProperties.getPort());
    }
    ThriftServerDef serverDef = thriftServerDefBuilder.build();
    LOG.info("Starting Nifty Server on port {}", niftyServerProperties.getPort());
    if (nettyServer == null) {
      nettyServer = new NettyServerTransport(serverDef);
    }
    nettyServer.start();
  }

  @SuppressWarnings({"unchecked", "rawtypes"})
  private TProcessor getTProcessor(final Object niftyHandler)
      throws NoSuchMethodException, ClassNotFoundException {
    List<Class<?>> handlerInterfaces = findAllInterfaces(niftyHandler.getClass());
    Class ifaceClass = null;
    Class<TProcessor> processorClass = null;

    for (Class<?> handlerInterfaceClass : handlerInterfaces) {
      if (!handlerInterfaceClass.getName().endsWith("$Iface")
          || handlerInterfaceClass.getDeclaringClass() == null) {
        continue;
      }
      for (Class<?> innerClass : handlerInterfaceClass.getDeclaringClass().getDeclaredClasses()) {
        if (!innerClass.getName().endsWith("$Processor")
            || !TProcessor.class.isAssignableFrom(innerClass)) {
          continue;
        }
        if (ifaceClass != null) {
          throw new IllegalStateException("Multiple Thrift Ifaces defined on handler");
        }
        ifaceClass = handlerInterfaceClass;
        processorClass = (Class<TProcessor>) innerClass;
        break;
      }
    }
    if (ifaceClass == null) {
      throw new IllegalStateException("No Thrift Ifaces found on handler: " + ifaceClass);
    }
    return BeanUtils.instantiateClass(processorClass.getConstructor(ifaceClass),
        wrapHandler(ifaceClass, niftyHandler));
  }

  /**
   * proxyTargetClass = true might be set, so we have to work harder to get the Thrift Iface.
   */
  private List<Class<?>> findAllInterfaces(Class<?> handlerClass) throws ClassNotFoundException {
    List<Class<?>> handlerInterfaces = new ArrayList<>();
    handlerInterfaces.addAll(Arrays.asList(handlerClass.getInterfaces()));
    handlerInterfaces.addAll(Arrays
        .asList(Class.forName(handlerClass.getSuperclass().getName()).getInterfaces()));
    handlerInterfaces.add(handlerClass.getSuperclass());
    return handlerInterfaces;
  }

  @SuppressWarnings("unchecked")
  private <T> T wrapHandler(final Class<T> interfaceClass, final T handler) {
    ProxyFactory proxyFactory =
        new ProxyFactory(interfaceClass, new SingletonTargetSource(handler));
    niftyConfigurer.configureProxyFactory(proxyFactory);
    // TODO remove from here?
    proxyFactory.setFrozen(true);
    return (T) proxyFactory.getProxy();
  }

  @Override
  public void destroy() throws Exception {
    if (nettyServer != null) {
      nettyServer.stop();
    }
  }
}
