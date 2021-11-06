package com.seovic.examples.grpc.todo.server.grpc;

import io.helidon.microprofile.grpc.core.GrpcInterceptorBinding;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * @author Aleks Seovic  2021.11.06
 */
@GrpcInterceptorBinding
@Target({TYPE, METHOD})
@Retention(RUNTIME)
@Documented
public @interface Logged
    {
    }
