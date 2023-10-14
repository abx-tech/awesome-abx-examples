package com.abx.chat.service;


import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.*;

@Service
public class DynamoDBSetupService {

    private final DynamoDbClient dynamoDbClient;

    public DynamoDBSetupService(DynamoDbClient dynamoDbClient) {
        this.dynamoDbClient = dynamoDbClient;
    }

    public void createTable() {

        String tableName = "ChatMessages";

        try {
            CreateTableRequest request = CreateTableRequest.builder()
                .attributeDefinitions(
                    AttributeDefinition.builder()
                        .attributeName("ThreadId")
                        .attributeType(ScalarAttributeType.S)
                        .build(),
                    AttributeDefinition.builder()
                        .attributeName("Timestamp")
                        .attributeType(ScalarAttributeType.S)
                        .build()
                )
                .keySchema(
                    KeySchemaElement.builder()
                        .attributeName("ThreadId")
                        .keyType(KeyType.HASH)
                        .build(),
                    KeySchemaElement.builder()
                        .attributeName("Timestamp") // Sort key
                        .keyType(KeyType.RANGE)
                        .build()
                )
                .provisionedThroughput(
                    ProvisionedThroughput.builder()
                        .readCapacityUnits(5L)
                        .writeCapacityUnits(5L)
                        .build()
                )
                .tableName(tableName)
                .build();


            dynamoDbClient.createTable(request);
            System.out.println("Table created successfully: " + tableName);

        } catch (DynamoDbException e) {
            System.err.println("Unable to create table: " + tableName);
            System.err.println(e.getMessage());
        }
    }

    private boolean doesTableExist(DynamoDbClient dynamoDbClient, String tableName) {
        try {
            DescribeTableResponse describeTableResponse = dynamoDbClient.describeTable(DescribeTableRequest.builder().tableName(tableName).build());
            return tableName.equals(describeTableResponse.table().tableName());
        } catch (ResourceNotFoundException e) {
            return false;
        }
    }


    @PostConstruct
    public void init() {
        setupTable();
    }

    public void setupTable() {
        if (!doesTableExist(dynamoDbClient, "ChatMessages")) {
            createTable();
        } else {
            System.out.println("Table already exists.");
        }
    }
}
