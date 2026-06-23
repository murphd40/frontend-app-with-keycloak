package com.example.frontendapp.security;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation to require 2FA authentication for accessing a method or class. When applied, the user
 * must have authenticated with OTP (One-Time Password) in addition to their password. Users without
 * 2FA will receive a 403 Forbidden response.
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface Require2FA {}
