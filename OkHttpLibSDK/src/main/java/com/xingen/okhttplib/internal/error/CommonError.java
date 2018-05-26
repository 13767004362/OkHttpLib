package com.xingen.okhttplib.internal.error;

/**
 * Created by ${xinGen} on 2018/1/29.
 */

public class CommonError extends RuntimeException {
    private String msg;
    private int code;

    private CommonError() {
    }

    public CommonError(int state, String message) {
        super(message);
        this.msg = message;
        this.code = state;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public static class Builder {
        private CommonError error;

        public Builder() {
            this.error = new CommonError();
        }

        public Builder setMsg(String msg) {
            this.error.msg = msg;
            return this;
        }

        public Builder setCode(int code) {
            this.error.code = code;
            return this;
        }

        public CommonError builder() {
            return error;
        }
    }

    public static final class State {
        /**
         * 空指针
         */
        public static final int error_null = 1;
        /**
         * 网络异常
         */
        public static final int error_net = 2;
        /**
         * 解析异常
         */
        public static final int error_parser = 3;
        /**
         * 类型转换异常
         */
        public static final int error_class = 4;
        /**
         *  IO异常
         */
        public static final  int error_io=5;
        /**
         *  未知异常
         */
        public static final  int error_unknown=5;
    }

}
