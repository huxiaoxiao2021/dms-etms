package com.jd.bluedragon.distribution.task.service;

/**
 * Created by wangtingwei on 2015/10/28.
 */
public interface RedisTaskQueueService {

    /**
     * 获取REDIS任务队列长度
     * @param queueName 队列名
     * @return
     */
    long getQueueLength(String cacheName,String queueName);

}
