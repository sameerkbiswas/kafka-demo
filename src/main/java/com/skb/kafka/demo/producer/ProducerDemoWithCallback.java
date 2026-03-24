package com.skb.kafka.demo.producer;

import com.skb.kafka.demo.constants.Constants;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;

public class ProducerDemoWithCallback {
    private static final Logger logger = LoggerFactory.getLogger(ProducerDemoWithCallback.class);

    static void main() {
        logger.info("Hello from Kafka Producer!");

        KafkaProducer<String, String> kafkaProducer = new KafkaProducer<>(populatePropertiesForKafkaProducer());

        for (int j = 0; j < 10; j++) {
            for (int i = 0; i < 30; i++) {
                ProducerRecord<String, String> producerRecord = new ProducerRecord<>(Constants.TOPIC_NAME, "Record # " + i);

                kafkaProducer.send(producerRecord, new Callback() {
                    @Override
                    public void onCompletion(RecordMetadata metadata, Exception exception) {
                        if (exception == null) {
                            logger.info("Received new metadata. \n" +
                                            "Topic: {}, Partition: {}, Offset: {}, Timestamp: {}",
                                    metadata.topic(), metadata.partition(), metadata.offset(), metadata.timestamp());
                        } else {
                            logger.error("Error while producing", exception);
                        }
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

    private static Properties populatePropertiesForKafkaProducer() {
        Properties properties = new Properties();

        properties.setProperty(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, Constants.BOOTSTRAP_SERVERS);

        properties.setProperty("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        properties.setProperty("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        properties.setProperty("batch.size", "400");    // default is 16KB, setting it to 400 bytes to trigger more frequent sends

        return properties;
    }
}
