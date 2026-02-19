package com.nouraschool.runtime.aop;

import jakarta.annotation.Priority;
import jakarta.interceptor.AroundInvoke;
import jakarta.interceptor.Interceptor;
import jakarta.interceptor.InvocationContext;
import org.jboss.logging.Logger;

@Logged
@Interceptor
@Priority(2000)
public class LoggedInterceptor {

    @AroundInvoke
    public Object log(InvocationContext ctx) throws Exception {
        Logger log = Logger.getLogger(ctx.getTarget().getClass());
        String method = ctx.getMethod().getDeclaringClass().getSimpleName() + "." + ctx.getMethod().getName();
        log.debugf(">> %s", method);
        try {
            Object result = ctx.proceed();
            log.debugf("<< %s", method);
            return result;
        } catch (Exception e) {
            log.errorf("!! %s failed: %s", method, e.getMessage());
            throw e;
        }
    }
}
