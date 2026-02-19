package com.nouraschool.runtime.aop;

import jakarta.annotation.Priority;
import jakarta.interceptor.AroundInvoke;
import jakarta.interceptor.Interceptor;
import jakarta.interceptor.InvocationContext;
import org.jboss.logging.Logger;

@Timed
@Interceptor
@Priority(1999)
public class TimedInterceptor {

    @AroundInvoke
    public Object measure(InvocationContext ctx) throws Exception {
        long start = System.currentTimeMillis();
        try {
            return ctx.proceed();
        } finally {
            long duration = System.currentTimeMillis() - start;
            Logger log = Logger.getLogger(ctx.getTarget().getClass());
            String method = ctx.getMethod().getDeclaringClass().getSimpleName() + "." + ctx.getMethod().getName();
            if (duration > 1000) {
                log.warnf("%s took %d ms", method, duration);
            } else {
                log.debugf("%s took %d ms", method, duration);
            }
        }
    }
}
