package com.seovic.examples.grpc.todo.server.grpc;

import io.grpc.Metadata;
import io.grpc.MethodDescriptor;
import io.grpc.ServerCall;
import io.grpc.ServerCallHandler;
import io.grpc.ServerInterceptor;

import io.helidon.microprofile.grpc.core.GrpcInterceptor;

import java.util.logging.Logger;
import javax.enterprise.context.ApplicationScoped;

/**
 * @author Aleks Seovic  2021.11.06
 */
@ApplicationScoped
@GrpcInterceptor
@Logged
public class LoggingInterceptor
        implements ServerInterceptor
    {
    private static final java.util.logging.Logger LOG = Logger.getLogger("grpc");

    public <ReqT, RespT> ServerCall.Listener<ReqT> interceptCall(ServerCall<ReqT, RespT> call,
                                                                 Metadata metadata,
                                                                 ServerCallHandler<ReqT, RespT> handler)
        {
        MethodDescriptor<ReqT, RespT> md = call.getMethodDescriptor();

        LOG.info(() -> String.format("CALL: %s, type=%s, marshaller=%s",
                                     md.getFullMethodName(),
                                     md.getType(),
                                     md.getRequestMarshaller().getClass().getSimpleName()));
        return handler.startCall(call, metadata);
        }
    }
