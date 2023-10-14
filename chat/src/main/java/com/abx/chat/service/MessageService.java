package com.abx.chat.service;

import com.abx.chat.model.ChatMessage;
import com.abx.chat.model.ImmutableChatMessage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.*;

@Service
public class MessageService {

    private final DynamoDbClient dynamoDbClient;

    private static final String TABLE_NAME = "ChatMessages";

    public MessageService(DynamoDbClient dynamoDbClient) {
        this.dynamoDbClient = dynamoDbClient;
    }

    public void saveMessage(ChatMessage message) {
        HashMap<String, AttributeValue> itemValues = new HashMap<>();

        // [
        // ThreadID 123
        // UserId: 1
        // Content: "Hello"
        // Timestamp: "2021-09-01T12:00:00"


        // ThreadID 123
        // UserId: 2
        // Content: "Hi"
        // Timestamp: "2021-09-01T12:00:01"
        // ]



        itemValues.put("UserId", AttributeValue.builder().s(String.valueOf(message.getUserId())).build());

        itemValues.put("ThreadId", AttributeValue.builder().s(message.getChatThreadId()).build());

        itemValues.put("Content", AttributeValue.builder().s(message.getContent()).build());
        itemValues.put("Timestamp", AttributeValue.builder().s(message.getTimestamp()).build());

        PutItemRequest request = PutItemRequest.builder()
            .tableName(TABLE_NAME)
            .item(itemValues)
            .build();

        try {
            dynamoDbClient.putItem(request);
            System.out.println("Message saved successfully!");
        } catch (ResourceNotFoundException e) {
            System.err.println("Error: The table " + TABLE_NAME + " was not found.");
        } catch (DynamoDbException e) {
            System.err.println("Error: " + e.getMessage());
        }
    }


    public List<ChatMessage> getMessagesForThread(String threadId) {
        HashMap<String, AttributeValue> valueMap = new HashMap<>();
        valueMap.put(":tid", AttributeValue.builder().s(threadId).build());

        QueryRequest queryReq = QueryRequest.builder()
            .tableName(TABLE_NAME)
            .keyConditionExpression("ThreadId = :tid")
            .expressionAttributeValues(valueMap)
            .build();

        List<ChatMessage> messages = new ArrayList<>();
        for (Map<String, AttributeValue> item : dynamoDbClient.query(queryReq).items()) {
            messages.add(ImmutableChatMessage.builder()
                .userId(item.get("UserId").s())
                .chatThreadId(item.get("ThreadId").s())
                .content(item.get("Content").s())
                .timestamp(item.get("Timestamp").s())
                .build());
        }
        return messages;
    }


}
