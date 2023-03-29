package com.jd.bluedragon.distribution.jdq4;

import org.apache.kafka.clients.producer.Callback;

public interface JDQProducer {

    void produce(String key, Object value);
    void produce(String key, Object value, Callback callback);
}
