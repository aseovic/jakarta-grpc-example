/*
 * Copyright (c) 2020, 2021 Oracle and/or its affiliates.
 *
 * Licensed under the Universal Permissive License v 1.0 as shown at
 * https://oss.oracle.com/licenses/upl.
 */

package com.seovic.examples.grpc.todo.client;

import io.grpc.stub.StreamObserver;

import java.lang.annotation.Annotation;
import java.util.Collection;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.inject.Inject;

/**
 * The client used to interact with raw ToDoList gRPC Java API.
 *
 * @author Aleks Seovic
 */
@ApplicationScoped
public class TaskManager
    {
    @Inject
    private ToDoListGrpcClient tasks;

    @Inject
    private Event<Task> taskEvent;

    private final class TaskStreamObserver implements StreamObserver<Task>
        {
        private final Annotation eventType;

        public TaskStreamObserver(Annotation eventType)
            {
            this.eventType = eventType;
            }

        public void onNext(Task task)
            {
            taskEvent.select(eventType).fire(task);
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
        tasks.createTask(description);
        }

    /**
     * Get all the tasks.
     *
     * @return all the tasks
     */
    public Collection<Task> getAllTasks()
        {
        return tasks.getAllTasks();
        }

    /**
     * Get the active tasks.
     *
     * @return the active tasks
     */
    public Collection<Task> getActiveTasks()
        {
        return tasks.getTasks(false);
        }

    /**
     * Get the completed tasks.
     *
     * @return the completed tasks
     */
    public Collection<Task> getCompletedTasks()
        {
        return tasks.getTasks(true);
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
        tasks.deleteTask(id);
        }

    /**
     * Update the completion status of a task.
     *
     * @param id         the task ID
     * @param completed  the completion status to set
     */
    public void updateCompleted(String id, Boolean completed)
        {
        tasks.updateCompletionStatus(id, completed);
        }

    /**
     * Update the description of a task.
     *
     * @param id           the task ID
     * @param description  the task description
     */
    public void updateText(String id, String description)
        {
        tasks.updateDescription(id, description);
        }
    }
