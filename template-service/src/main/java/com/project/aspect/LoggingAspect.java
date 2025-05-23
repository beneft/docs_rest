package com.project.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Slf4j
public class LoggingAspect {

    @Around("execution(* com.project..*.*(..))")
    public Object logMethod(ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        String methodName = methodSignature.getDeclaringType().getSimpleName() + "." + methodSignature.getName();

        log.info("IN: {}", methodName);

        try {
            Object result = joinPoint.proceed();
            log.info("OUT: {}", methodName);
            return result;
        } catch (Throwable ex) {
            log.error("EXCEPTION in {}: {}", methodName, ex.getMessage(), ex);
            throw ex;
        }
    }
}
