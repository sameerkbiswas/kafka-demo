package com.skb.kafka.demo.admin;

import com.skb.kafka.demo.constants.Constants;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.NewTopic;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.Properties;
import java.util.concurrent.ExecutionException;

public class AdminClientDemo {

    private static final Logger LOGGER = LoggerFactory.getLogger(AdminClientDemo.class);

    static void main() {
        Properties properties = new Properties();
        properties.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, Constants.BOOTSTRAP_SERVERS);

        try (var adminClient = AdminClient.create(properties)) {

            try {
                // Create a new topic
//                createTopic(adminClient, Constants.TOPIC_NAME);

                // List all topics
                listTopics(adminClient);

                // Delete the created topic
//              deleteTopic(adminClient, Constants.TOPIC_NAME);
            } catch (ExecutionException e) {
                LOGGER.error("Error executing admin client operation", e);
            } catch (InterruptedException e) {
                LOGGER.error("Error executing admin client operation", e);
            }
        }
    }

    private static void createTopic(AdminClient adminClient, String topicName) throws ExecutionException, InterruptedException {
        int numberOfPartitions = 3; // You can choose the number of partitions based on your use case. More partitions can provide better parallelism but may also increase complexity in managing offsets and consumer groups.
//        short replicationFactor = 3;    // For a multi-node cluster, replication factor can be greater than 1. Ensure that the replication factor does not exceed the number of brokers in the cluster.
        short replicationFactor = 1;    // For a single-node cluster, replication factor must be 1
        var newTopic = new NewTopic(topicName, numberOfPartitions, replicationFactor);
        adminClient.createTopics(Collections.singleton(newTopic)).all().get();
        LOGGER.info("Topic '{}' created successfully.", topicName);
    }

    private static void deleteTopic(AdminClient adminClient, String topicName) throws ExecutionException, InterruptedException {
        adminClient.deleteTopics(Collections.singleton(topicName)).all().get();
        LOGGER.info("Topic '{}' deleted successfully.", topicName);
    }

    private static void listTopics(AdminClient adminClient) throws ExecutionException, InterruptedException {
        var topics = adminClient.listTopics().names().get();
        LOGGER.info("Topics in the cluster: {}", topics);
    }

}
