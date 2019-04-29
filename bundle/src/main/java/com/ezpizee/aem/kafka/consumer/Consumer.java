package com.ezpizee.aem.kafka.consumer;

public interface Consumer {
    Runnable getRunnable();
    void stop ();
}