package ltd.Emallix.mall.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class TimerAspect {
    @Around("execution(* ltd.Emallix.mall.dao.*.*(..))")
    public Object around(ProceedingJoinPoint pjp) throws Throwable {
        // 记录起始时间
        long start = System.currentTimeMillis();
        // 执行连接点方法，即切面所在位置对应的方法。本项目中表示执行注册或执行登录等
        Object result = pjp.proceed();
        // 记录结束时间
        long end = System.currentTimeMillis();
        // 计算耗时
        System.err.println(pjp.getSignature().toString()+" 耗时：" + (end - start) + "ms.");
        // 返回连接点方法的返回值
        return result;
    }
}
