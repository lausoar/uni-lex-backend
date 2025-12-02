package com.unilex.backend.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor(staticName = "of")
public class R<T> {
    private int code;
    private String msg;
    private T data;

    // 成功
    public static <T> R<T> ok(T data) {
        return R.of(200, "success", data);
    }

    // 错误（支持自定义码）
    public static <T> R<T> error(int code, String msg) {
        return R.of(code, msg, null);
    }

    // 数据冲突（409）
    public static <T> R<T> conflict(String msg) {
        return R.of(409, msg, null);
    }
}