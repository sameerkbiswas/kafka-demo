package com.skb.kafka.demo.producer;

import com.skb.kafka.demo.constants.Constants;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;
import org.apache.kafka.clients.producer.KafkaProducer;

public class ProducerDemo {
    private static final Logger logger = LoggerFactory.getLogger(ProducerDemo.class);

    static void main() {
        logger.info("Hello from Kafka Producer!");
        Properties properties = new Properties();

        properties.setProperty(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, Constants.BOOTSTRAP_SERVERS);

        properties.setProperty("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        properties.setProperty("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");

        KafkaProducer<String, String> kafkaProducer = new KafkaProducer<>(properties);

        for(int i = 0; i < 100; i++) {
            ProducerRecord<String, String> producerRecord = new ProducerRecord<>(Constants.TOPIC_NAME, "Record # " + i);

            kafkaProducer.send(producerRecord);
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
