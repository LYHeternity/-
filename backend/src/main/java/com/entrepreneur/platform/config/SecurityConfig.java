package com.entrepreneur.platform.config;

import com.entrepreneur.platform.security.JwtAuthenticationFilter;
import com.entrepreneur.platform.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Spring Security 配置：无状态 JWT + 白名单放行登录注册
 */
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
@RequiredArgsConstructor
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userService).passwordEncoder(passwordEncoder);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .authorizeRequests()
                .antMatchers("/", "/index.html", "/static/**", "/css/**", "/js/**", "/images/**").permitAll()
                .antMatchers("/auth/login", "/auth/register", "/auth/captcha").permitAll()
                .antMatchers(HttpMethod.GET, "/stats/overview").permitAll()
                .antMatchers(HttpMethod.GET, "/files/**").permitAll()
                .antMatchers(HttpMethod.GET, "/project/list").permitAll()
                .antMatchers(HttpMethod.GET, "/project/**").permitAll()
                .antMatchers(HttpMethod.GET, "/resource/list").permitAll()
                .antMatchers(HttpMethod.GET, "/resource/**").permitAll()
                .antMatchers(HttpMethod.GET, "/message/unread-count").permitAll()
                .antMatchers(HttpMethod.GET, "/message/list").permitAll()
                .antMatchers(HttpMethod.GET, "/articles").permitAll()
                .antMatchers(HttpMethod.GET, "/articles/**").permitAll()
                .antMatchers(HttpMethod.GET, "/community/posts").permitAll()
                .antMatchers(HttpMethod.GET, "/community/posts/**").permitAll()
                .antMatchers(HttpMethod.GET, "/community/comments/**").permitAll()

                .antMatchers("/backup/**").permitAll()
                .antMatchers("/ws/**", "/chat-ws/**").permitAll()
                .antMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                .antMatchers("/admin/stats/**").permitAll()

                .antMatchers("/ws/**", "/chat-ws/**").permitAll()
                .antMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                .antMatchers("/admin/stats/**").permitAll()
                .antMatchers("/admin/**").permitAll()

                .anyRequest().authenticated()
                .and()
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
    }

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }
}
