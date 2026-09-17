package com.smartrent.common;

import lombok.Data;

/**
 * 统一接口返回格式
 * 所有接口都返回这个格式，前端统一处理：
 * { "code": 200, "message": "成功", "data": 具体数据 }
 */
@Data
public class Result<T> {

    /** 状态码：200 成功，其他失败 */
    private Integer code;

    /** 提示信息 */
    private String message;

    /** 返回的数据 */
    private T data;

    /** 成功，不带数据 */
    public static <T> Result<T> success() {
        return success(null);
    }

    /** 成功，带数据 */
    public static <T> Result<T> success(T data) {
        Result<T> result = new Result<>();
        result.setCode(200);
        result.setMessage("成功");
        result.setData(data);
        return result;
    }

    /** 失败 */
    public static <T> Result<T> error(String message) {
        Result<T> result = new Result<>();
        result.setCode(500);
        result.setMessage(message);
        return result;
    }
}
