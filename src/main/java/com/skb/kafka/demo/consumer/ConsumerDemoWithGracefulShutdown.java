package com.skb.kafka.demo.consumer;

import com.skb.kafka.demo.constants.Constants;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.errors.WakeupException;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.Properties;

public class ConsumerDemoWithGracefulShutdown {
    private static final Logger logger = LoggerFactory.getLogger(ConsumerDemoWithGracefulShutdown.class);

    static void main() {
        logger.info("Hello from Kafka Consumer!");
        KafkaConsumer<String, String> kafkaConsumer = new KafkaConsumer<>(getKafkaConsumerProperties());

        // Add shutdown hook
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            logger.info("Detected shutdown, calling consumer.wakeup()...");
            kafkaConsumer.wakeup(); // This will cause the consumer to throw a WakeupException, which we can catch to exit gracefully

            try {
                Thread.currentThread().join(); // Interrupt the current/main thread to exit the application
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }));

        try {
            kafkaConsumer.subscribe(Collections.singleton(Constants.TOPIC_NAME));
            while (true) {
                ConsumerRecords<String, String> consumerRecords = kafkaConsumer.poll(java.time.Duration.ofSeconds(1));
                for (ConsumerRecord<String, String> consumerRecord : consumerRecords) {
                    logger.info("Key: {}, Value: {}, Partition: {}, Offset: {}", consumerRecord.key(), consumerRecord.value(), consumerRecord.partition(), consumerRecord.offset());
                }
            }
        } catch (WakeupException e) {
            logger.info("WakeupException caught, shutting down gracefully...");
        } finally {
            kafkaConsumer.close(); // This will also commit offsets if needed and clean up resources
            logger.info("Consumer closed, application exiting.");
        }
    }

    private static Properties getKafkaConsumerProperties() {
        Properties properties = new Properties();

        properties.setProperty(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, Constants.BOOTSTRAP_SERVERS);

        properties.setProperty("group.id", "my-java-application");
        properties.setProperty("auto.offset.reset", "earliest");    // none/latest/earliest

        properties.setProperty("key.deserializer", StringDeserializer.class.getName());
        properties.setProperty("value.deserializer", StringDeserializer.class.getName());

        return properties;
    }
}
