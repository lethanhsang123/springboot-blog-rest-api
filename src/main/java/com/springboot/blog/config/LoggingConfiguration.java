package com.springboot.blog.config;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Component
@Aspect
public class LoggingConfiguration {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    /**
    * PointCut that matches all repository, services and Web REST endpoints.
    */
    @Pointcut("within(@org.springframework.stereotype.Repository *)"+
        " || within(@org.springframework.stereotype.Service *)" +
        " || within(@org.springframework.web.bind.annotation.RestController *)"
    )
    public void springBeanPointCut() {
        // Method is empty as this is just PointCut, the implementations are in the advices.
    }

    /**
    * Pointcut that matches all Spring beans in the application's main packages.
    */
    @Pointcut("within(com.springboot.blog..*)" +
            "|| within(com.springboot.blog.service..*)" +
            "|| within(com.springboot.blog.controller..*)"
    )
    public void applicationPackagePointCut() {
        // Method is empty as this is just PointCut, the implementations are in the advices.
    }
    /**
     * Advice that logs methods throwing exceptions
     *
     * @param joinPoint join point for advice
     * @param e exception
     * */
    @AfterThrowing(pointcut = "applicationPackagePointCut() && springBeanPointCut()", throwing = "e")
    public void logAfterThrowing(JoinPoint joinPoint, Throwable e) {
        logger.error("Exception in {}.{}() with cause = {}", joinPoint.getSignature().getDeclaringTypeName(),
                joinPoint.getSignature().getName(), e.getCause() != null ? e.getCause() : "NULL");
    }


    /**
     * Advice that logs when a method í entered and exited
     *
     * @param joinPoint join point for advice
     * @return result
     * @throws Throwable throws IllegalArgumentException
     * */
    @Around("applicationPackagePointCut() && springBeanPointCut()")
    public Object logAround(ProceedingJoinPoint joinPoint) throws Throwable{
        if(logger.isDebugEnabled()) {
            logger.debug("Enter: {}.{}() with argument[s] = {}", joinPoint.getSignature().getDeclaringTypeName(),
                    joinPoint.getSignature().getName(), Arrays.toString(joinPoint.getArgs()));
        }
        try {
            Object result = joinPoint.proceed();
            if (logger.isDebugEnabled()) {
                logger.debug("Exit: {}.{}() with result = {}", joinPoint.getSignature().getDeclaringTypeName(),
                        joinPoint.getSignature().getName(), result);
            }
            return  result;
        }catch (IllegalArgumentException e) {
            logger.error("Illegal argument: {} in {}.{}()", Arrays.toString(joinPoint.getArgs()),
                    joinPoint.getSignature().getDeclaringTypeName(), joinPoint.getSignature().getName());
            throw e;
        }
    }

}
