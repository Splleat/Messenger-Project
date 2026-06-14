package me.splleat.messengerproject.common.aop;

import lombok.RequiredArgsConstructor;
import me.splleat.messengerproject.common.annotation.DistributedLock;
import me.splleat.messengerproject.common.exception.BusinessException;
import me.splleat.messengerproject.common.exception.ErrorCode;
import me.splleat.messengerproject.common.parser.CustomSpringELParser;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

@Aspect
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
@RequiredArgsConstructor
public class DistributedLockAspect {
    private final RedissonClient redissonClient;
    private final CustomSpringELParser parser;

    @Around("@annotation(me.splleat.messengerproject.common.annotation.DistributedLock)")
    public Object lock(ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();

        DistributedLock distributedLock = method.getAnnotation(DistributedLock.class);
        String key = parser.getDynamicValue(signature.getParameterNames(), joinPoint.getArgs(), distributedLock.key()).toString();
        RLock lock = redissonClient.getLock(key);

        boolean acquired = lock.tryLock(distributedLock.waitTime(), distributedLock.leaseTime(), distributedLock.timeUnit());

        if (!acquired) {
            if (distributedLock.throwOnFailure()) {
                throw new BusinessException(ErrorCode.LOCK_FAILED);
            }

            return null;
        }

        try {
            return joinPoint.proceed();
        } finally {
            lock.unlock();
        }
    }
}
