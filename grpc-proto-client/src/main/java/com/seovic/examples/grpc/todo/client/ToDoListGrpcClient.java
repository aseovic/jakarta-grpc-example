package com.seovic.examples.grpc.todo.client;

import com.google.protobuf.BoolValue;
import com.google.protobuf.Empty;
import com.google.protobuf.StringValue;

import com.seovic.examples.grpc.todo.TaskMessage;
import com.seovic.examples.grpc.todo.ToDoListServiceGrpc;
import com.seovic.examples.grpc.todo.UpdateDescriptionRequest;
import com.seovic.examples.grpc.todo.UpdateCompletionStatusRequest;

import io.grpc.Channel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import java.util.concurrent.CompletableFuture;

import javax.enterprise.context.ApplicationScoped;

/**
 * gRPC client interface for To Do List API
 *
 * @author Aleks Seovic  2021.02.28
 */
@SuppressWarnings("ALL")
@ApplicationScoped
public class ToDoListGrpcClient
    {
    private final ToDoListServiceGrpc.ToDoListServiceStub stub;

    // ---- constructors ----------------------------------------------------

    public ToDoListGrpcClient()
        {
        Channel channel = ManagedChannelBuilder.forAddress("localhost", 1408).usePlaintext().build();
        this.stub = ToDoListServiceGrpc.newStub(channel);
        }

    // ---- helpers ---------------------------------------------------------

    private Task task(TaskMessage message)
        {
        return new Task(message.getId(), message.getDescription(), message.getCompleted(), message.getCreatedAt());
        }

    // ---- API wrappers ----------------------------------------------------

    Task createTask(String description)
        {
        CompletableFuture<Task> future = new CompletableFuture<>();
        stub.createTask(StringValue.of(description), new StreamObserver<>()
            {
            public void onNext(TaskMessage taskMessage)
                {
                future.complete(task(taskMessage));
                }

            public void onError(Throwable throwable)
                {
                future.completeExceptionally(throwable);
                }

            public void onCompleted()
                {
                }
            });

        return future.join();
        }

    Collection<Task> getAllTasks()
        {
        CompletableFuture<Collection<Task>> future = new CompletableFuture<>();
        stub.getAllTasks(Empty.getDefaultInstance(), new StreamObserver<>()
            {
            private Set<Task> tasks = new HashSet<>();

            public void onNext(TaskMessage taskMessage)
                {
                tasks.add(task(taskMessage));
                }

            public void onError(Throwable throwable)
                {
                future.completeExceptionally(throwable);
                }

            public void onCompleted()
                {
                future.complete(tasks);
                }
            });

        return future.join();
        }

    Collection<Task> getTasks(boolean fCompleted)
        {
        CompletableFuture<Collection<Task>> future = new CompletableFuture<>();
        stub.getTasks(BoolValue.of(fCompleted), new StreamObserver<>()
            {
            private Set<Task> tasks = new HashSet<>();

            public void onNext(TaskMessage taskMessage)
                {
                tasks.add(task(taskMessage));
                }

            public void onError(Throwable throwable)
                {
                future.completeExceptionally(throwable);
                }

            public void onCompleted()
                {
                future.complete(tasks);
                }
            });

        return future.join();
        }

    Task deleteTask(String id)
        {
        CompletableFuture<Task> future = new CompletableFuture<>();
        stub.deleteTask(StringValue.of(id), new StreamObserver<>()
            {
            public void onNext(TaskMessage taskMessage)
                {
                future.complete(task(taskMessage));
                }

            public void onError(Throwable throwable)
                {
                future.completeExceptionally(throwable);
                }

            public void onCompleted()
                {
                }
            });

        return future.join();
        }

    boolean deleteCompletedTasks()
        {
        CompletableFuture<Boolean> future = new CompletableFuture<>();
        stub.deleteCompletedTasks(Empty.getDefaultInstance(), new StreamObserver<>()
            {
            public void onNext(BoolValue fResult)
                {
                future.complete(fResult.getValue());
                }

            public void onError(Throwable throwable)
                {
                future.completeExceptionally(throwable);
                }

            public void onCompleted()
                {
                }
            });

        return future.join();
        }

    Task updateDescription(String id, String description)
        {
        UpdateDescriptionRequest request = UpdateDescriptionRequest.newBuilder()
                .setId(id)
                .setDescription(description)
                .build();

        CompletableFuture<Task> future = new CompletableFuture<>();

        stub.updateDescription(request, new StreamObserver<>()
            {
            public void onNext(TaskMessage taskMessage)
                {
                future.complete(task(taskMessage));
                }

            public void onError(Throwable throwable)
                {
                future.completeExceptionally(throwable);
                }

            public void onCompleted()
                {
                }
            });

        return future.join();
        }

    Task updateCompletionStatus(String id, boolean fCompleted)
        {
        UpdateCompletionStatusRequest request = UpdateCompletionStatusRequest.newBuilder()
                .setId(id)
                .setCompleted(fCompleted)
                .build();

        CompletableFuture<Task> future = new CompletableFuture<>();

        stub.updateCompletionStatus(request, new StreamObserver<>()
            {
            public void onNext(TaskMessage taskMessage)
                {
                future.complete(task(taskMessage));
                }

            public void onError(Throwable throwable)
                {
                future.completeExceptionally(throwable);
                }

            public void onCompleted()
                {
                }
            });

        return future.join();
        }

    void onInsert(StreamObserver<Task> observer)
        {
        stub.onInsert(Empty.getDefaultInstance(), new StreamObserver<>()
            {
            public void onNext(TaskMessage taskMessage)
                {
                observer.onNext(task(taskMessage));
                }

            public void onError(Throwable throwable)
                {
                observer.onError(throwable);
                }

            public void onCompleted()
                {
                observer.onCompleted();
                }
            });
        }

    void onUpdate(StreamObserver<Task> observer)
        {
        stub.onUpdate(Empty.getDefaultInstance(), new StreamObserver<>()
            {
            public void onNext(TaskMessage taskMessage)
                {
                observer.onNext(task(taskMessage));
                }

            public void onError(Throwable throwable)
                {
                observer.onError(throwable);
                }

            public void onCompleted()
                {
                observer.onCompleted();
                }
            });
        }

    void onRemove(StreamObserver<Task> observer)
        {
        stub.onRemove(Empty.getDefaultInstance(), new StreamObserver<>()
            {
            public void onNext(TaskMessage taskMessage)
                {
                observer.onNext(task(taskMessage));
                }

            public void onError(Throwable throwable)
                {
                observer.onError(throwable);
                }

            public void onCompleted()
                {
                observer.onCompleted();
                }
            });
        }
    }
