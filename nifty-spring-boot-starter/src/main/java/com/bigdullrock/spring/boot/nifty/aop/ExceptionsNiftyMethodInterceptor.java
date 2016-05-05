package com.bigdullrock.spring.boot.nifty.aop;

import org.apache.thrift.TException;
import org.springframework.aop.ThrowsAdvice;

import java.lang.reflect.Method;

public class ExceptionsNiftyMethodInterceptor implements ThrowsAdvice {

  public void afterThrowing(final Method method, final Object[] args, final Object target,
      final Exception ex) throws Throwable {
    if (ex instanceof TException) {
      throw ex;
    }
    throw new TException(ex.getMessage(), ex);
  }
}
