package com.seovic.examples.grpc.todo.client;

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
@Grpc(name = "examples.json.ToDoList")
@GrpcMarshaller("json")
public interface ToDoListGrpcClient
    {
    @Unary
    Task createTask(String description);

    @ServerStreaming
    Stream<Task> getAllTasks();

    @ServerStreaming
    Stream<Task> getTasks(boolean completed);

    @Unary
    Task deleteTask(String id);

    @Unary
    boolean deleteCompletedTasks();

    @Unary
    Task updateDescription(UpdateDescriptionRequest request);

    @Unary
    Task updateCompletionStatus(UpdateCompletionStatusRequest request);

    @ServerStreaming
    void onInsert(StreamObserver<Task> observer);

    @ServerStreaming
    void onUpdate(StreamObserver<Task> observer);

    @ServerStreaming
    void onRemove(StreamObserver<Task> observer);

    // ---- request messages ------------------------------------------------

    class UpdateDescriptionRequest
        {
        public String id;
        public String description;

        public UpdateDescriptionRequest(String id, String description)
            {
            this.id = id;
            this.description = description;
            }
        }

    class UpdateCompletionStatusRequest
        {
        public String id;
        public boolean completed;

        public UpdateCompletionStatusRequest(String id, boolean completed)
            {
            this.id = id;
            this.completed = completed;
            }
        }
    }
