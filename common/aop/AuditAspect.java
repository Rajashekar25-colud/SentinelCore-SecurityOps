package com.sentinelcore.secureops.common.aop;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sentinelcore.secureops.audit.model.AuditLog;
import com.sentinelcore.secureops.audit.repository.AuditLogRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.stream.Collectors;

@Aspect
@Component
public class AuditAspect {

    private final AuditLogRepository auditLogRepository;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public AuditAspect(AuditLogRepository auditLogRepository) {
        this.auditLogRepository = auditLogRepository;
    }

    @Around("@annotation(auditable)")
    public Object logAuditActivity(ProceedingJoinPoint joinPoint, Auditable auditable) throws Throwable {
        String username = "system";
        String roles = "NONE";

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !auth.getPrincipal().equals("anonymousUser")) {
            username = auth.getName();
            roles = auth.getAuthorities().stream()
                        .map(a -> a.getAuthority())
                        .collect(Collectors.joining(","));
        }

        String ipAddress = "unknown";
        String userAgent = "unknown";
        ServletRequestAttributes attrs = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attrs != null) {
            HttpServletRequest request = attrs.getRequest();
            ipAddress = request.getRemoteAddr();
            String rawUserAgent = request.getHeader("User-Agent");
            userAgent = rawUserAgent != null && rawUserAgent.length() > 250
                    ? rawUserAgent.substring(0, 250)
                    : rawUserAgent;
        }
        if (roles != null && roles.length() > 250) {
            roles = roles.substring(0, 250);
        }

        Object result = null;
        String outcome = "SUCCESS";
        try {
            result = joinPoint.proceed();
            return result;
        } catch (Throwable e) {
            outcome = "FAILED: " + e.getMessage();
            throw e;
        } finally {
            AuditLog log = new AuditLog();
            log.setUsername(username);
            log.setRole(roles);
            log.setIpAddress(ipAddress);
            log.setDeviceBrowser(userAgent);
            log.setAction(auditable.action());
            log.setResult(outcome);
            auditLogRepository.save(log);
        }
    }
}
