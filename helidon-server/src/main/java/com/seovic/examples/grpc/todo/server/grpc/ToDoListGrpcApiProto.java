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
import io.helidon.microprofile.grpc.core.Grpc;
import io.helidon.microprofile.grpc.core.GrpcMarshaller;
import io.helidon.microprofile.grpc.core.ServerStreaming;
import io.helidon.microprofile.grpc.core.Unary;
import java.util.stream.Stream;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;


/**
 * gRPC facade for To Do List API that uses Protobuf (default) marshaller.
 *
 * @author Aleks Seovic  2021.11.04
 */
@Grpc(name = "examples.proto.ToDoList")
@ApplicationScoped
public class ToDoListGrpcApiProto
    {
    @Inject
    private ToDoListService api;

    @Inject
    private TaskRepository tasks;

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

    @Unary
    public TaskMessage createTask(StringValue description)
        {
        return taskMessage(api.createTask(description.getValue()));
        }

    @ServerStreaming
    public Stream<TaskMessage> getAllTasks()
        {
        return api.getTasks(null).stream()
                    .map(this::taskMessage);
        }

    @ServerStreaming
    public Stream<TaskMessage> getTasks(BoolValue fCompleted)
        {
        return api.getTasks(fCompleted.getValue()).stream()
                    .map(this::taskMessage);
        }

    @Unary
    public TaskMessage findTask(StringValue id)
        {
        return taskMessage(api.findTask(id.getValue()));
        }

    @Unary
    public TaskMessage deleteTask(StringValue id)
        {
        return taskMessage(api.deleteTask(id.getValue()));
        }

    @Unary
    public BoolValue deleteCompletedTasks()
        {
        return BoolValue.of(api.deleteCompletedTasks());
        }

    @Unary
    public TaskMessage updateDescription(UpdateDescriptionRequest request)
        {
        return taskMessage(api.updateDescription(request.getId(), request.getDescription()));
        }

    @Unary
    public TaskMessage updateCompletionStatus(UpdateCompletionStatusRequest request)
        {
        return taskMessage(api.updateCompletionStatus(request.getId(), request.getCompleted()));
        }

    @ServerStreaming
    public void onInsert(StreamObserver<TaskMessage> observer)
        {
        tasks.addListener(
                tasks.listener()
                     .onInsert(task -> observer.onNext(taskMessage(task)))
                     .build());
        }

    @ServerStreaming
    public void onUpdate(StreamObserver<TaskMessage> observer)
        {
        tasks.addListener(
                tasks.listener()
                     .onUpdate(task -> observer.onNext(taskMessage(task)))
                     .build());
        }

    @ServerStreaming
    public void onRemove(StreamObserver<TaskMessage> observer)
        {
        tasks.addListener(
                tasks.listener()
                     .onRemove(task -> observer.onNext(taskMessage(task)))
                     .build());
        }
    }
