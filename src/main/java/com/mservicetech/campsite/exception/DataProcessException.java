package com.mservicetech.campsite.exception;


public class DataProcessException extends RuntimeException {

    private static final long serialVersionUID = 1L;


    public DataProcessException(String msg, Throwable cause) {
        super(msg, cause);
    }

    public DataProcessException(String msg) {
        super(msg);
    }
}
