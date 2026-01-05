package com.recyclestudy.common.log;

import org.aspectj.lang.annotation.Pointcut;

public class LoggingPointcuts {

    @Pointcut("execution(* com.recyclestudy..controller..*(..))")
    public void controllerLayer() {
    }

    @Pointcut("execution(* com.recyclestudy..service..*(..))")
    public void serviceLayer() {
    }

    @Pointcut("execution(* com.recyclestudy..repository..*(..))")
    public void repositoryLayer() {
    }

    @Pointcut("controllerLayer() || serviceLayer() || repositoryLayer()")
    public void applicationLayers() {
    }
}
