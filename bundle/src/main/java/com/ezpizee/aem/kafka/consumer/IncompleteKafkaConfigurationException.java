package com.ezpizee.aem.kafka.consumer;

public class IncompleteKafkaConfigurationException extends Exception {

    public IncompleteKafkaConfigurationException (String message) {
        super (message);
    }
}
