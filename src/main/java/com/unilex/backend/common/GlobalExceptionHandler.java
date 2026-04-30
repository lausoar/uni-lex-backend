package com.unilex.backend.common;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 全局异常处理器，统一处理各类异常并返回标准化响应
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 处理数据库唯一键冲突异常
     *
     * @param e 唯一键冲突异常
     * @return 冲突响应结果
     */
    @ExceptionHandler(DuplicateKeyException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public R<Void> handleDuplicateKey(DuplicateKeyException e) {
        log.error("唯一键冲突: {}", e.getMessage());
        // 传递原始消息，前端可直接使用
        return R.conflict(e.getMessage());
    }

    /**
     * 处理通用系统异常
     *
     * @param e 系统异常
     * @return 错误响应结果
     */
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public R<Void> handleException(Exception e) {
        log.error("系统异常: ", e);
        return R.error(500, e.getMessage());
    }

    /**
     * 处理非法参数异常
     *
     * @param e 非法参数异常
     * @return 错误响应结果
     */
    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public R<Void> handleIllegal(IllegalArgumentException e) {
        return R.error(400, e.getMessage());
    }
}