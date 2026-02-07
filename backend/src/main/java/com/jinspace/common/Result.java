package com.jinspace.common;

/**
 * 统一返回结果类
 */
public class Result<T> {

    private int code;
    private String message;
    private T data;
    private boolean success;

    public Result() {
    }

    public Result(int code, String message, T data, boolean success) {
        this.code = code;
        this.message = message;
        this.data = data;
        this.success = success;
    }

    /**
     * 成功返回
     * @param message 成功消息
     * @return 结果
     */
    public static Result<?> success(String message) {
        return new Result<>(200, message, null, true);
    }

    /**
     * 成功返回
     * @param data 数据
     * @return 结果
     */
    public static <T> Result<T> success(T data) {
        return new Result<>(200, "操作成功", data, true);
    }

    /**
     * 成功返回
     * @param message 成功消息
     * @param data 数据
     * @return 结果
     */
    public static <T> Result<T> success(String message, T data) {
        return new Result<>(200, message, data, true);
    }

    /**
     * 失败返回
     * @param message 失败消息
     * @return 结果
     */
    public static Result<?> error(String message) {
        return new Result<>(500, message, null, false);
    }

    /**
     * 失败返回
     * @param code 错误代码
     * @param message 失败消息
     * @return 结果
     */
    public static Result<?> error(int code, String message) {
        return new Result<>(code, message, null, false);
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

}
