package com.m1fonda.service_auth.customexceptions;

public class ActivationCodeException extends RuntimeException {
    public ActivationCodeException(String message) {
        super(message);
    }
}
