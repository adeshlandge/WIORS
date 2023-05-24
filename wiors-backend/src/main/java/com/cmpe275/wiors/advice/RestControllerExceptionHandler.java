package com.cmpe275.wiors.advice;




import com.cmpe275.wiors.exception.CustomException;
import jakarta.validation.ConstraintViolationException;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Order(Ordered.HIGHEST_PRECEDENCE)
@RestControllerAdvice
public class RestControllerExceptionHandler {
    @ExceptionHandler(CustomException.class)
    protected ResponseEntity<Object> handleCustomException(
            CustomException ex) {
        return new ResponseEntity<>(ex.getMessage(),ex.getStatus());
    }
    @ExceptionHandler(ConstraintViolationException.class)
    protected ResponseEntity<Object> handleConstraintViolationException(
            Exception ex) {
        return new ResponseEntity<>(ex.getMessage(),
                HttpStatus.BAD_REQUEST);
    }
    @ExceptionHandler(IllegalArgumentException.class)
    protected ResponseEntity<Object> handleIllegalArgumentException(
            Exception ex) {
        return new ResponseEntity<>(ex.getMessage(),
                HttpStatus.BAD_REQUEST);
    }


}
