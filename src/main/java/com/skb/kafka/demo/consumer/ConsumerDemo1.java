package com.skb.kafka.demo.consumer;

import com.skb.kafka.demo.constants.Constants;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.Properties;

public class ConsumerDemo1 {
    private static final Logger logger = LoggerFactory.getLogger(ConsumerDemo1.class);

    static void main() {
        logger.info("Hello from Kafka Consumer!");
        try (KafkaConsumer<String, String> kafkaConsumer = new KafkaConsumer<>(getKafkaConsumerProperties())) {
            kafkaConsumer.subscribe(Collections.singleton(Constants.TOPIC_NAME));
            for(int i = 0; i < 70; i++) {
                ConsumerRecords<String, String> consumerRecords = kafkaConsumer.poll(java.time.Duration.ofSeconds(1));
                for (ConsumerRecord<String, String> consumerRecord : consumerRecords) {
                    logger.info("Key: {}, Value: {}, Partition: {}, Offset: {}", consumerRecord.key(), consumerRecord.value(), consumerRecord.partition(), consumerRecord.offset());
                }
            }
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
