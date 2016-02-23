package com.bigdullrock.spring.boot.nifty.aop;

import org.apache.thrift.TApplicationException;
import org.apache.thrift.TException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.ThrowsAdvice;

import java.lang.reflect.Method;

public class ExceptionsNiftyMethodInterceptor implements ThrowsAdvice {

  private static final Logger LOG =
      LoggerFactory.getLogger(ExceptionsNiftyMethodInterceptor.class);

  public void afterThrowing(final Method method, final Object[] args, final Object target,
      final Exception e) throws Throwable {
    if (e instanceof TException) {
      throw e;
    }
    LOG.warn("unexpected exception in " + target.getClass().getCanonicalName() + "."
        + method.getName(), e);
    throw new TApplicationException(TApplicationException.INTERNAL_ERROR, e.toString());
  }
}
