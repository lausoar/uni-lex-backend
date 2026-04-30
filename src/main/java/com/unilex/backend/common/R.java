package com.unilex.backend.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 通用响应结果封装类
 *
 * @param <T> 响应数据类型
 */
@Data
@NoArgsConstructor
@AllArgsConstructor(staticName = "of")
public class R<T> {
    /** 响应状态码 */
    private int code;
    /** 响应消息 */
    private String msg;
    /** 响应数据 */
    private T data;

    /**
     * 构建成功响应
     *
     * @param data 响应数据
     * @return 成功响应结果
     */
    // 成功
    public static <T> R<T> ok(T data) {
        return R.of(200, "success", data);
    }

    /**
     * 构建错误响应（支持自定义状态码）
     *
     * @param code 错误状态码
     * @param msg  错误消息
     * @return 错误响应结果
     */
    // 错误（支持自定义码）
    public static <T> R<T> error(int code, String msg) {
        return R.of(code, msg, null);
    }

    /**
     * 构建数据冲突响应（409）
     *
     * @param msg 冲突消息
     * @return 冲突响应结果
     */
    // 数据冲突（409）
    public static <T> R<T> conflict(String msg) {
        return R.of(409, msg, null);
    }
}
