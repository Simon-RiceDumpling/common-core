package com.aspect;


import com.annotation.LimitRequest;
import com.exception.AssemblyCatServiceException;
import net.jodah.expiringmap.ExpirationPolicy;
import net.jodah.expiringmap.ExpiringMap;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * @ClassNAME LimitRequestAspect
 * @Description TODO
 * @Author renbo.ren@cyberklick.com
 * @Date 2023/2/10 11:23
 */

@Aspect
@Component
public class LimitRequestAspect {

    private static ConcurrentHashMap<String, ExpiringMap<String, Integer>> book = new ConcurrentHashMap<>();

    // 定义切点
    // 让所有有@LimitRequest注解的方法都执行切面方法
    @Pointcut("@annotation(limitRequest)")
    public void excudeService(LimitRequest limitRequest) {
    }

    @Around("excudeService(limitRequest)")
    public Object doAround(ProceedingJoinPoint pjp, LimitRequest limitRequest){
        return Optional.of(RequestContextHolder.getRequestAttributes())
                .map(ra -> (ServletRequestAttributes) ra)
                .map(ServletRequestAttributes::getRequest)
                .map(request -> {
                    // 第一个参数是key， 第二个参数是默认值
                    ExpiringMap<String, Integer> uc = book.getOrDefault(request.getRequestURI(), ExpiringMap.builder().variableExpiration().build());
                    Integer uCount = uc.getOrDefault(request.getRemoteAddr(), 0);
                    if (uCount >= limitRequest.count()) { // 超过次数，不执行目标方法
                        return "接口请求超过次数";
                    } else if (uCount == 0) { // 第一次请求时，设置有效时间
                        uc.put(request.getRemoteAddr(), uCount + 1, ExpirationPolicy.CREATED, limitRequest.time(), TimeUnit.MILLISECONDS);
                    } else { // 未超过次数， 记录加一
                        uc.put(request.getRemoteAddr(), uCount + 1);
                    }
                    book.put(request.getRequestURI(), uc);
                    // result的值就是被拦截方法的返回值
                    try {
                        return pjp.proceed();
                    } catch (Throwable e) {
                        throw new AssemblyCatServiceException(e);
                    }
                }).orElse(null);
    }


}
