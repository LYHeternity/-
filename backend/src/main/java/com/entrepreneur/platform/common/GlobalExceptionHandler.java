package com.entrepreneur.platform.common;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

/**
 * 全局异常处理，返回统一格式
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BadCredentialsException.class)
    public Result<?> handleBadCredentials(BadCredentialsException e) {
        log.warn("登录失败: {}", e.getMessage());
        return Result.fail(401, "账号或密码错误");
    }

    @ExceptionHandler(AccessDeniedException.class)
    public Result<?> handleAccessDenied(AccessDeniedException e) {
        log.warn("无权限: {}", e.getMessage());
        return Result.fail(403, "无权限执行该操作");
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Result<?> handleValid(MethodArgumentNotValidException e) {
        String msg = e.getBindingResult().getFieldErrors().stream()
                .map(f -> f.getField() + ": " + f.getDefaultMessage())
                .reduce((a, b) -> a + "; " + b).orElse("参数校验失败");
        return Result.fail(400, msg);
    }

    @ExceptionHandler(BindException.class)
    public Result<?> handleBind(BindException e) {
        String msg = e.getFieldErrors().stream()
                .map(f -> f.getField() + ": " + f.getDefaultMessage())
                .reduce((a, b) -> a + "; " + b).orElse("参数错误");
        return Result.fail(400, msg);
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public Result<?> handleMaxUpload(MaxUploadSizeExceededException e) {
        return Result.fail(400, "文件大小超过限制（最大50MB）");
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public Result<?> handleIllegalArg(IllegalArgumentException e) {
        return Result.fail(400, e.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public Result<?> handleException(Exception e) {
        log.error("系统异常", e);
        return Result.fail(500, e.getMessage() != null ? e.getMessage() : "系统繁忙，请稍后重试");
    }
}
