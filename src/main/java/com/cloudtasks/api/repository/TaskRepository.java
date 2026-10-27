
package com.cloudtasks.api.repository;

import com.cloudtasks.api.model.Task;
import org.springframework.stereotype.Repository;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public class TaskRepository {

    private static final String TABLE_NAME = "CloudTasks";

    private final DynamoDbClient dynamoDbClient;

    public TaskRepository(DynamoDbClient dynamoDbClient) {
        this.dynamoDbClient = dynamoDbClient;
    }

    public Task save(Task task) {
        Map<String, AttributeValue> item = Map.of(
                "id", AttributeValue.fromS(task.getId()),
                "title", AttributeValue.fromS(task.getTitle()),
                "description", AttributeValue.fromS(task.getDescription()),
                "completed", AttributeValue.fromBool(task.isCompleted())
        );

        PutItemRequest request = PutItemRequest.builder()
                .tableName(TABLE_NAME)
                .item(item)
                .build();

        dynamoDbClient.putItem(request);

        return task;
    }

    public List<Task> findAll() {
        return dynamoDbClient.scanPaginator(
                        ScanRequest.builder()
                                .tableName(TABLE_NAME)
                                .build()
                ).items().stream()
                .map(this::mapToTask)
                .toList();
    }

    private Task mapToTask(Map<String, AttributeValue> item) {
        Task task = new Task();

        task.setId(item.get("id").s());
        task.setTitle(item.get("title").s());
        task.setDescription(item.get("description").s());
        task.setCompleted(item.get("completed").bool());

        return task;
    }
    public Optional<Task> findById(String id) {

        GetItemRequest request = GetItemRequest.builder()
                .tableName(TABLE_NAME)
                .key(Map.of(
                        "id", AttributeValue.fromS(id)
                ))
                .build();

        Map<String, AttributeValue> item =
                dynamoDbClient.getItem(request).item();

        if (item == null || item.isEmpty()) {
            return Optional.empty();
        }

        return Optional.of(mapToTask(item));
    }
    public void deleteById(String id) {

        DeleteItemRequest request = DeleteItemRequest.builder()
                .tableName(TABLE_NAME)
                .key(Map.of(
                        "id", AttributeValue.fromS(id)
                ))
                .build();

        dynamoDbClient.deleteItem(request);
    }
}
