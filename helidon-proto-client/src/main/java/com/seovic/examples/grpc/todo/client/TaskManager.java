/*
 * Copyright (c) 2020, 2021 Oracle and/or its affiliates.
 *
 * Licensed under the Universal Permissive License v 1.0 as shown at
 * https://oss.oracle.com/licenses/upl.
 */

package com.seovic.examples.grpc.todo.client;

import com.google.protobuf.BoolValue;
import com.google.protobuf.StringValue;

import com.seovic.examples.grpc.todo.TaskMessage;

import com.seovic.examples.grpc.todo.UpdateCompletionStatusRequest;
import com.seovic.examples.grpc.todo.UpdateDescriptionRequest;
import io.grpc.stub.StreamObserver;

import io.helidon.microprofile.grpc.client.GrpcProxy;

import java.lang.annotation.Annotation;
import java.util.Collection;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.inject.Inject;

/**
 * The client used to interact with ToDoList gRPC API.
 *
 * @author Aleks Seovic
 */
@ApplicationScoped
public class TaskManager
    {
    @Inject
    @GrpcProxy
    private ToDoListGrpcClient tasks;

    @Inject
    private Event<Task> taskEvent;

    // ---- helpers ---------------------------------------------------------

    private Task task(TaskMessage message)
        {
        return new Task(message.getId(), message.getDescription(), message.getCompleted(), message.getCreatedAt());
        }

    private final class TaskStreamObserver implements StreamObserver<TaskMessage>
        {
        private final Annotation eventType;

        public TaskStreamObserver(Annotation eventType)
            {
            this.eventType = eventType;
            }

        public void onNext(TaskMessage message)
            {
            taskEvent.select(eventType).fire(task(message));
            }

        public void onError(Throwable t)
            {
            t.printStackTrace();
            }

        public void onCompleted()
            {
            }
        }
    /**
     * Start listening for incoming events and convert them to CDI events.
     */
    @PostConstruct
    void registerEventListeners()
        {
        tasks.onInsert(new TaskStreamObserver(TaskEvent.INSERTED));
        tasks.onUpdate(new TaskStreamObserver(TaskEvent.UPDATED));
        tasks.onRemove(new TaskStreamObserver(TaskEvent.REMOVED));
        }

    /**
     * Add a task with the given description.
     *
     * @param description  the task description
     */
    public void addTodo(String description)
        {
        tasks.createTask(StringValue.of(description));
        }

    /**
     * Get all the tasks.
     *
     * @return all the tasks
     */
    public Collection<Task> getAllTasks()
        {
        return tasks.getAllTasks().map(this::task).collect(Collectors.toSet());
        }

    /**
     * Get the active tasks.
     *
     * @return the active tasks
     */
    public Collection<Task> getActiveTasks()
        {
        return tasks.getTasks(BoolValue.of(false)).map(this::task).collect(Collectors.toSet());
        }

    /**
     * Get the completed tasks.
     *
     * @return the completed tasks
     */
    public Collection<Task> getCompletedTasks()
        {
        return tasks.getTasks(BoolValue.of(true)).map(this::task).collect(Collectors.toSet());
        }

    /**
     * Remove all completed tasks.
     */
    public void removeCompletedTasks()
        {
        tasks.deleteCompletedTasks();
        }

    /**
     * Remove a single task.
     *
     * @param id  the task ID
     */
    public void removeTodo(String id)
        {
        tasks.deleteTask(StringValue.of(id));
        }

    /**
     * Update the completion status of a task.
     *
     * @param id         the task ID
     * @param fCompleted  the completion status to set
     */
    public void updateCompleted(String id, Boolean fCompleted)
        {
        UpdateCompletionStatusRequest request = UpdateCompletionStatusRequest.newBuilder()
                .setId(id)
                .setCompleted(fCompleted)
                .build();
        tasks.updateCompletionStatus(request);
        }

    /**
     * Update the description of a task.
     *
     * @param id           the task ID
     * @param description  the task description
     */
    public void updateText(String id, String description)
        {
        UpdateDescriptionRequest request = UpdateDescriptionRequest.newBuilder()
                .setId(id)
                .setDescription(description)
                .build();
        tasks.updateDescription(request);
        }
    }
