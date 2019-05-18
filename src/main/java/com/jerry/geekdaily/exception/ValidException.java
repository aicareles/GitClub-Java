package com.jerry.geekdaily.exception;

public class ValidException extends RuntimeException {
    public ValidException(String msg) {
        super(msg);
    }

    public ValidException() {
        super();
    }
}
