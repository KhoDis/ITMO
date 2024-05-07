package ru.itmo.wp.exception;

import org.springframework.validation.BindingResult;

public class ValidationException extends RuntimeException {
    private BindingResult bindingResult;

    public ValidationException(BindingResult bindingResult) {
        this.bindingResult = bindingResult;
    }

    public ValidationException(String message) {
        super(message);
    }

    public BindingResult getBindingResult() {
        return bindingResult;
    }
}
