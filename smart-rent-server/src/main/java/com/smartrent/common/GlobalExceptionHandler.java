package com.smartrent.common;

import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 全局异常处理器
 *
 * 作用：把后端抛出的异常，统一转换成 Result 格式返回给前端。
 * 这样无论成功还是失败，前端拿到的数据结构都一样：
 * { "code": 200, "message": "...", "data": ... }
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /** 业务异常：直接把提示信息返回给前端 */
    @ExceptionHandler(BusinessException.class)
    public Result<Void> handleBusiness(BusinessException e) {
        return Result.error(e.getMessage());
    }

    /** 其它未预料到的异常：返回通用提示，不把错误堆栈暴露给前端 */
    @ExceptionHandler(Exception.class)
    public Result<Void> handleOther(Exception e) {
        e.printStackTrace();
        return Result.error("系统开小差了，请稍后再试");
    }
}
