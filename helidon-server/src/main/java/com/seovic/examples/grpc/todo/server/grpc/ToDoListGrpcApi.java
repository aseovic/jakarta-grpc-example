package com.seovic.examples.grpc.todo.server.grpc;

import com.google.protobuf.BoolValue;
import com.google.protobuf.Empty;
import com.google.protobuf.StringValue;

import com.seovic.examples.grpc.todo.TaskMessage;
import com.seovic.examples.grpc.todo.ToDoListServiceGrpc;
import com.seovic.examples.grpc.todo.UpdateCompletionStatusRequest;
import com.seovic.examples.grpc.todo.UpdateDescriptionRequest;

import com.seovic.examples.grpc.todo.server.Task;
import com.seovic.examples.grpc.todo.server.TaskRepository;
import com.seovic.examples.grpc.todo.server.ToDoListService;

import io.grpc.BindableService;
import io.grpc.ServerServiceDefinition;
import io.grpc.stub.StreamObserver;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

/**
 * @author Aleks Seovic  2021.11.05
 */
public class ToDoListGrpcApi
        extends ToDoListServiceGrpc.ToDoListServiceImplBase
    {
    private final ToDoListService api;
    private final TaskRepository tasks;

    // ---- constructors ----------------------------------------------------

    ToDoListGrpcApi(ToDoListService api, TaskRepository tasks)
        {
        this.api = api;
        this.tasks = tasks;
        }

    // ---- helpers ---------------------------------------------------------

    private TaskMessage taskMessage(Task task)
        {
        return TaskMessage.newBuilder()
                .setId(task.getId())
                .setDescription(task.getDescription())
                .setCompleted(task.getCompleted())
                .setCreatedAt(task.getCreatedAt())
                .build();
        }

    // ---- gRPC service API ------------------------------------------------

    @Override
    public void createTask(StringValue description, StreamObserver<TaskMessage> observer)
        {
        try
            {
            Task task = api.createTask(description.getValue());
            observer.onNext(taskMessage(task));
            observer.onCompleted();
            }
        catch (Throwable e)
            {
            observer.onError(e);
            }
        }

    @Override
    public void getAllTasks(Empty ignore, StreamObserver<TaskMessage> observer)
        {
        try
            {
            api.getTasks(null).stream()
                    .map(this::taskMessage)
                    .forEach(observer::onNext);
            observer.onCompleted();
            }
        catch (Throwable e)
            {
            observer.onError(e);
            }
        }

    @Override
    public void getTasks(BoolValue fCompleted, StreamObserver<TaskMessage> observer)
        {
        try
            {
            api.getTasks(fCompleted.getValue()).stream()
                    .map(this::taskMessage)
                    .forEach(observer::onNext);
            observer.onCompleted();
            }
        catch (Throwable e)
            {
            observer.onError(e);
            }
        }

    @Override
    public void findTask(StringValue id, StreamObserver<TaskMessage> observer)
        {
        try
            {
            Task task = api.findTask(id.getValue());
            observer.onNext(taskMessage(task));
            observer.onCompleted();
            }
        catch (Throwable e)
            {
            observer.onError(e);
            }
        }

    @Override
    public void deleteTask(StringValue id, StreamObserver<TaskMessage> observer)
        {
        try
            {
            Task task = api.deleteTask(id.getValue());
            observer.onNext(taskMessage(task));
            observer.onCompleted();
            }
        catch (Throwable e)
            {
            observer.onError(e);
            }
        }

    @Override
    public void deleteCompletedTasks(Empty ignore, StreamObserver<BoolValue> observer)
        {
        try
            {
            boolean fModified = api.deleteCompletedTasks();
            observer.onNext(BoolValue.of(fModified));
            observer.onCompleted();
            }
        catch (Throwable e)
            {
            observer.onError(e);
            }
        }

    @Override
    public void updateDescription(UpdateDescriptionRequest request, StreamObserver<TaskMessage> observer)
        {
        try
            {
            Task task = api.updateDescription(request.getId(), request.getDescription());
            observer.onNext(taskMessage(task));
            observer.onCompleted();
            }
        catch (Throwable e)
            {
            observer.onError(e);
            }
        }

    @Override
    public void updateCompletionStatus(UpdateCompletionStatusRequest request, StreamObserver<TaskMessage> observer)
        {
        try
            {
            Task task = api.updateCompletionStatus(request.getId(), request.getCompleted());
            observer.onNext(taskMessage(task));
            observer.onCompleted();
            }
        catch (Throwable e)
            {
            observer.onError(e);
            }
        }

    @Override
    public void onInsert(Empty ignore, StreamObserver<TaskMessage> observer)
        {
        try
            {
            tasks.addListener(
                    tasks.listener()
                         .onInsert(task -> observer.onNext(taskMessage(task)))
                         .build());
            }
        catch (Throwable e)
            {
            observer.onError(e);
            }
        }

    @Override
    public void onUpdate(Empty ignore, StreamObserver<TaskMessage> observer)
        {
        try
            {
            tasks.addListener(
                    tasks.listener()
                         .onUpdate(task -> observer.onNext(taskMessage(task)))
                         .build());
            }
        catch (Throwable e)
            {
            observer.onError(e);
            }
        }

    @Override
    public void onRemove(Empty ignore, StreamObserver<TaskMessage> observer)
        {
        try
            {
            tasks.addListener(
                    tasks.listener()
                         .onRemove(task -> observer.onNext(taskMessage(task)))
                         .build());
            }
        catch (Throwable e)
            {
            observer.onError(e);
            }
        }

    @ApplicationScoped
    public static class ManagedService
            implements BindableService
        {
        private final ToDoListGrpcApi service;

        // ---- constructors ----------------------------------------------------

        @Inject
        public ManagedService(ToDoListService api, TaskRepository tasks)
            {
            this.service = new ToDoListGrpcApi(api, tasks);
            }

        public ServerServiceDefinition bindService()
            {
            return service.bindService();
            }
        }
    }
