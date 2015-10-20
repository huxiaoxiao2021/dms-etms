package com.jd.bluedragon.distribution.worker.service;

import com.jd.bluedragon.distribution.worker.dao.TBTaskQueueDao;
import com.jd.bluedragon.distribution.worker.domain.TBTaskQueue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by wangtingwei on 2015/10/13.
 */
@Service("tbTaskQueueService")
public class TBTaskQueueServiceImpl implements TBTaskQueueService {

    @Autowired
    private TBTaskQueueDao tbTaskQueueDao;

    @Override
    public int getTaskQueueCount(String taskType) {
        return tbTaskQueueDao.selectQueueCount(taskType);
    }

    @Override
    public int createQueues(List<TBTaskQueue> list) throws Exception{
        return tbTaskQueueDao.insertQueues(list);
    }
}
