package com.laioffer.staybooking.security;

public class UserAlreadyExistException extends RuntimeException {
    public UserAlreadyExistException() {
        super("Username already exists");
    }
}
