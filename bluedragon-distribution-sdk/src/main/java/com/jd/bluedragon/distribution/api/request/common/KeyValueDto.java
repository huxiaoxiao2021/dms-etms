package com.jd.bluedragon.distribution.api.request.common;

import java.io.Serializable;

/**
 * 键值对对象
 *
 * @author fanggang7
 * @copyright jd.com 京东物流JDL
 * @time 2021-11-16 19:20:50 周二
 */
public class KeyValueDto<K, V> implements Serializable {

    private static final long serialVersionUID = -7686001082147504635L;

    private K key;

    private V value;

    public KeyValueDto() {
    }

    public KeyValueDto(K key, V value) {
        this.key = key;
        this.value = value;
    }

    public K getKey() {
        return key;
    }

    public KeyValueDto<K, V> setKey(K key) {
        this.key = key;
        return this;
    }

    public V getValue() {
        return value;
    }

    public KeyValueDto<K, V> setValue(V value) {
        this.value = value;
        return this;
    }
}
