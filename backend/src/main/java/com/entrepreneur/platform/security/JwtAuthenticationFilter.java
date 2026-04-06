package com.entrepreneur.platform.security;

import com.entrepreneur.platform.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;

/**
 * JWT 认证过滤器：从请求头解析 token 并设置 SecurityContext
 */
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;

    @Override
<<<<<<< HEAD
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, 
                                    FilterChain filterChain) throws ServletException, IOException {
        String token = getTokenFromRequest(request);
        if (StringUtils.hasText(token)) {
            if (jwtUtil.validateToken(token)) {
                Long userId = jwtUtil.getUserIdFromToken(token);
                String username = jwtUtil.getUsernameFromToken(token);
                String role = jwtUtil.getRoleFromToken(token);
                SimpleGrantedAuthority authority = new SimpleGrantedAuthority("ROLE_" + role.toUpperCase());
                UsernamePasswordAuthenticationToken auth = 
                        new UsernamePasswordAuthenticationToken(userId, null, Collections.singletonList(authority));
                auth.setDetails(username);
                SecurityContextHolder.getContext().setAuthentication(auth);
            } else {
                // 令牌过期，返回401状态码和过期提示
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.setContentType("application/json; charset=utf-8");
                response.getWriter().write("{\"code\": 401, \"message\": \"您的账号已过期请重新登录\"}");
                return;
            }
=======
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String token = getTokenFromRequest(request);
        if (StringUtils.hasText(token) && jwtUtil.validateToken(token)) {
            Long userId = jwtUtil.getUserIdFromToken(token);
            String username = jwtUtil.getUsernameFromToken(token);
            String role = jwtUtil.getRoleFromToken(token);
            SimpleGrantedAuthority authority = new SimpleGrantedAuthority("ROLE_" + role.toUpperCase());
            UsernamePasswordAuthenticationToken auth =
                    new UsernamePasswordAuthenticationToken(userId, null, Collections.singletonList(authority));
            auth.setDetails(username);
            SecurityContextHolder.getContext().setAuthentication(auth);
>>>>>>> 9b64148d98623b06683264281daf3bb34fea7cdd
        }
        filterChain.doFilter(request, response);
    }

    private String getTokenFromRequest(HttpServletRequest request) {
        String bearer = request.getHeader("Authorization");
        if (StringUtils.hasText(bearer) && bearer.startsWith("Bearer ")) {
            return bearer.substring(7);
        }
        return null;
    }
}
