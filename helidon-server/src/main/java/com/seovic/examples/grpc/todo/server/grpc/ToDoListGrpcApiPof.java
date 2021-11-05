package com.seovic.examples.grpc.todo.server.grpc;

import com.seovic.examples.grpc.todo.server.Task;
import com.seovic.examples.grpc.todo.server.TaskRepository;
import com.seovic.examples.grpc.todo.server.ToDoListService;
import com.tangosol.io.pof.schema.annotation.Portable;
import com.tangosol.io.pof.schema.annotation.PortableType;
import io.grpc.stub.StreamObserver;
import io.helidon.microprofile.grpc.core.Grpc;
import io.helidon.microprofile.grpc.core.GrpcMarshaller;
import io.helidon.microprofile.grpc.core.ServerStreaming;
import io.helidon.microprofile.grpc.core.Unary;
import java.util.stream.Stream;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

/**
 * gRPC facade for To Do List API that uses POF marshaller.
 *
 * @author Aleks Seovic  2021.11.04
 */
@Grpc(name = "examples.pof.ToDoList")
@GrpcMarshaller("pof")
@ApplicationScoped
public class ToDoListGrpcApiPof
    {
    @Inject
    private ToDoListService api;

    @Inject
    protected TaskRepository tasks;

    // ---- gRPC service API ------------------------------------------------
    
    @Unary
    public Task createTask(String description)
        {
        return api.createTask(description);
        }

    @ServerStreaming
    public Stream<Task> getAllTasks()
        {
        return api.getTasks(null).stream();
        }

    @ServerStreaming
    public Stream<Task> getTasks(boolean completed)
        {
        return api.getTasks(completed).stream();
        }

    @Unary
    public Task findTask(String id)
        {
        return api.findTask(id);
        }

    @Unary
    public Task deleteTask(String id)
        {
        return api.deleteTask(id);
        }

    @Unary
    public boolean deleteCompletedTasks()
        {
        return api.deleteCompletedTasks();
        }

    @Unary
    public Task updateDescription(UpdateDescriptionRequest request)
        {
        return api.updateDescription(request.id, request.description);
        }

    @Unary
    public Task updateCompletionStatus(UpdateCompletionStatusRequest request)
        {
        return api.updateCompletionStatus(request.id, request.completed);
        }

    @ServerStreaming
    public void onInsert(StreamObserver<Task> observer)
        {
        tasks.addListener(
                tasks.listener()
                     .onInsert(observer::onNext)
                     .build());
        }

    @ServerStreaming
    public void onUpdate(StreamObserver<Task> observer)
        {
        tasks.addListener(
                tasks.listener()
                     .onUpdate(observer::onNext)
                     .build());
        }

    @ServerStreaming
    public void onRemove(StreamObserver<Task> observer)
        {
        tasks.addListener(
                tasks.listener()
                     .onRemove(observer::onNext)
                     .build());
        }

    // ---- request messages ------------------------------------------------

    @PortableType(id = 5000)
    public static class UpdateDescriptionRequest
        {
        @Portable public String id;
        @Portable public String description;
        }

    @PortableType(id = 5001)
    public static class UpdateCompletionStatusRequest
        {
        @Portable public String id;
        @Portable public boolean completed;
        }
    }
