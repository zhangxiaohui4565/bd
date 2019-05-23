package com.gupao.bd.eos.query.vo;

import lombok.*;

/**
 * 返回封装
 */
@Getter
@Setter
public class Response {
    private int code;
    private String message;
    private Object payload;

    public Response() {
        this(0, "OK", null);
    }

    public Response(int code, String message) {
        this(code, message, null);
    }

    public Response(Object payload) {
        this(0, "OK", payload);
    }

    public Response(int code, String message, Object payload) {
        this.code = code;
        this.message = message;
        this.payload = payload;
    }
}
