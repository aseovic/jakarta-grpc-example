package com.seovic.examples.grpc.todo.client;

import com.google.protobuf.BoolValue;
import com.google.protobuf.StringValue;
import com.seovic.examples.grpc.todo.TaskMessage;
import com.seovic.examples.grpc.todo.UpdateCompletionStatusRequest;
import com.seovic.examples.grpc.todo.UpdateDescriptionRequest;
import io.grpc.stub.StreamObserver;

import io.helidon.microprofile.grpc.core.Grpc;
import io.helidon.microprofile.grpc.core.GrpcMarshaller;
import io.helidon.microprofile.grpc.core.ServerStreaming;
import io.helidon.microprofile.grpc.core.Unary;

import java.util.stream.Stream;

/**
 * gRPC client interface for To Do List API
 *
 * @author Aleks Seovic  2021.02.28
 */
@Grpc(name = "examples.proto.ToDoList")
@GrpcMarshaller("proto")
public interface ToDoListGrpcClient
    {
    @Unary
    TaskMessage createTask(StringValue description);

    @ServerStreaming
    Stream<TaskMessage> getAllTasks();

    @ServerStreaming
    Stream<TaskMessage> getTasks(BoolValue completed);

    @Unary
    TaskMessage deleteTask(StringValue id);

    @Unary
    BoolValue deleteCompletedTasks();

    @Unary
    TaskMessage updateDescription(UpdateDescriptionRequest request);

    @Unary
    TaskMessage updateCompletionStatus(UpdateCompletionStatusRequest request);

    @ServerStreaming
    void onInsert(StreamObserver<TaskMessage> observer);

    @ServerStreaming
    void onUpdate(StreamObserver<TaskMessage> observer);

    @ServerStreaming
    void onRemove(StreamObserver<TaskMessage> observer);
    }
