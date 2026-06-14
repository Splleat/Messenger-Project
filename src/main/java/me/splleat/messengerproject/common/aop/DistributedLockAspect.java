package me.splleat.messengerproject.common.aop;

import lombok.RequiredArgsConstructor;
import me.splleat.messengerproject.common.annotation.DistributedLock;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

@Aspect
@Component
@RequiredArgsConstructor
public class DistributedLockAspect {
    private final RedissonClient redissonClient;

    @Pointcut("@within(me.splleat.messengerproject.common.annotation.DistributedLock)")
    public Object lock(ProceedingJoinPoint joinPoint) {
        DistributedLock distributedLock = (DistributedLock) joinPoint.getSignature().getDeclaringType().getDeclaredAnnotation(DistributedLock.class);
        RLock lock = redissonClient.getLock(distributedLock.key());


    }

    private String getLockKey(ProceedingJoinPoint joinPoint) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        String[] parameterNames = signature.getParameterNames();

        DistributedLock distributedLock = method.getAnnotation(DistributedLock.class);

        return distributedLock.key();
    }
}
