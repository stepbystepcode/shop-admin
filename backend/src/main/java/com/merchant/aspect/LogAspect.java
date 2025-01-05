package com.merchant.aspect;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.merchant.annotation.Log;
import com.merchant.entity.OperationLog;
import com.merchant.repository.OperationLogRepository;
import com.merchant.security.UserPrincipal;
import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.lang.reflect.Method;

@Aspect
@Component
public class LogAspect {

    @Autowired
    private OperationLogRepository operationLogRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Pointcut("@annotation(com.merchant.annotation.Log)")
    public void logPointcut() {
    }

    @Around("logPointcut()")
    public Object around(ProceedingJoinPoint point) throws Throwable {
        Object result = null;
        long beginTime = System.currentTimeMillis();
        
        try {
            result = point.proceed();
            long endTime = System.currentTimeMillis();
            saveLog(point, result, endTime - beginTime);
        } catch (Exception e) {
            // 记录异常日志
            saveLog(point, e.getMessage(), System.currentTimeMillis() - beginTime);
            throw e;
        }
        
        return result;
    }

    private void saveLog(ProceedingJoinPoint joinPoint, Object result, long time) {
        try {
            MethodSignature signature = (MethodSignature) joinPoint.getSignature();
            Method method = signature.getMethod();
            Log logAnnotation = method.getAnnotation(Log.class);

            OperationLog operationLog = OperationLog.builder()
                .operation(logAnnotation.value())
                .method(signature.getDeclaringTypeName() + "." + signature.getName())
                .params(objectMapper.writeValueAsString(joinPoint.getArgs()))
                .result(result instanceof String ? (String) result : objectMapper.writeValueAsString(result))
                .createTime(java.time.LocalDateTime.now())
                .build();

            // 获取当前用户信息
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null && authentication.getPrincipal() instanceof UserPrincipal) {
                UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
                operationLog.setUsername(userPrincipal.getUsername());
            }

            // 获取请求IP
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attributes != null) {
                HttpServletRequest request = attributes.getRequest();
                operationLog.setIp(getIpAddress(request));
            }

            operationLogRepository.save(operationLog);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String getIpAddress(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_CLIENT_IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }
}
