package com.cmpe275.wiors.exception;

import org.springframework.http.HttpStatus;

public class CustomException extends Exception {
    HttpStatus status;

    public CustomException() {
        super();
    }


    public CustomException(String message, HttpStatus status) {
        super(message);
        this.status = status;
    }

    public HttpStatus getStatus() {
        return status;
    }

    public void setStatus(HttpStatus status) {
        this.status = status;
    }

}
