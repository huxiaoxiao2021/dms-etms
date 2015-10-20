package com.jd.bluedragon.distribution.worker.service;

import com.jd.bluedragon.distribution.worker.domain.TBTaskQueue;

import java.util.List;

/**
 * Created by wangtingwei on 2015/10/13.
 */
public interface TBTaskQueueService {
    /**
     * 获取队列数量
     * @param taskType 任务名称
     * @return
     */
    int getTaskQueueCount(String taskType);

    /**
     * 创建队列
     * @param list 队列列表
     * @return
     */
    int createQueues(List<TBTaskQueue> list) throws Exception;

}
