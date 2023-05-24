package com.cmpe275.wiors.exception;

import org.springframework.http.HttpStatus;

public class RecordDoesNotExistException extends CustomException {
    public RecordDoesNotExistException(String msg) {
        super(msg, HttpStatus.NOT_FOUND);
    }
}
