package com.project.signatureservice.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Aspect
@Component
@Slf4j
public class LoggingAspect {

    @Around("execution(* com.project..*.*(..))")
    public Object logMethod(ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        String methodName = methodSignature.getDeclaringType().getSimpleName() + "." + methodSignature.getName();
        String args = Arrays.toString(joinPoint.getArgs());

        log.info("IN: {} with arguments: {}", methodName, args);

        try {
            Object result = joinPoint.proceed();
            log.info("OUT: {} returned: {}", methodName, result);
            return result;
        } catch (Throwable ex) {
            log.error("EXCEPTION in {}: {}", methodName, ex.getMessage(), ex);
            throw ex;
        }
    }
}
