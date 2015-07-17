package com.jd.bluedragon.utils;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import com.jd.bluedragon.Constants;


/**
 * 
 * 
 * @author libin
 *
 */
public class SimpleCache<E> {

    private ConcurrentMap<String, CacheEntry<E>> cache;

    private long cacheTTL;

    private static class CacheEntry<E> {
        public final long timestamp;
        public final E value;


        public CacheEntry(E value, long timestamp) {
            this.timestamp = timestamp;
            this.value = value;
        }

    }


    public SimpleCache() {
        this(Constants.POLLING_INTERVAL_TIME * 1000L);
    }


    public SimpleCache(long cacheTTL) {
        this.cache = new ConcurrentHashMap<String, CacheEntry<E>>();
        this.cacheTTL = cacheTTL;
    }


    public void put(String key, E e) {
        if (key == null || e == null) {
            return;
        }
        CacheEntry<E> entry = new CacheEntry<E>(e, System.currentTimeMillis() + cacheTTL);
        cache.put(key, entry);
    }


    public E get(String key) {
        E result = null;
        CacheEntry<E> entry = cache.get(key);
        if (entry != null) {
            if (entry.timestamp > System.currentTimeMillis()) {
                result = entry.value;
            }
        }

        return result;
    }
    
}
