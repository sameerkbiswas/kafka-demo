package com.skb.kafka.demo.producer;

import com.skb.kafka.demo.constants.Constants;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;

public class ProducerDemoWithKey {
    private static final Logger logger = LoggerFactory.getLogger(ProducerDemoWithKey.class);

    static void main() {
        logger.info("Hello from Kafka Producer!");
        Properties properties = new Properties();

        properties.setProperty(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, Constants.BOOTSTRAP_SERVERS);

        properties.setProperty("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        properties.setProperty("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");

        KafkaProducer<String, String> kafkaProducer = new KafkaProducer<>(properties);

        for(int j = 0; j < 5; j++) {
            for (int i = 0; i < 10; i++) {
                String key = "id_" + i;
                ProducerRecord<String, String> producerRecord = new ProducerRecord<>(Constants.TOPIC_NAME, key, "Record # " + i);

                kafkaProducer.send(producerRecord, (metadata, exception) -> {
                    if (exception == null) {
                        logger.info("Received new metadata.\tTopic: {},\tKey: {},\tPartition: {},\tOffset: {},\tTimestamp: {}",
                                metadata.topic(), key, metadata.partition(), metadata.offset(), metadata.timestamp());
                    } else {
                        logger.error("Error while producing", exception);
                    }
                });
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    logger.error("Producer thread interrupted", e);
                }
            }

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                logger.error("Producer thread interrupted", e);
            }
        }
//        kafkaProducer.flush();

        // TIP: Always close the producer to avoid resource leaks
        // close() will also flush any remaining records before closing the producer
        kafkaProducer.close();
    }
}
