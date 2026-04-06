package com.entrepreneur.platform.util;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * 从 Security 上下文获取当前用户 ID（JWT 解析后放入的 principal 为 userId）
 */
public final class SecurityUtil {

    public static Long getCurrentUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof Long) {
            return (Long) auth.getPrincipal();
        }
        if (auth != null && auth.getPrincipal() instanceof String) {
            try {
                return Long.parseLong((String) auth.getPrincipal());
            } catch (NumberFormatException e) {
                return null;
            }
        }
        return null;
    }

    public static String getCurrentUsername() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getDetails() != null) {
            return auth.getDetails().toString();
        }
        return null;
    }
}
