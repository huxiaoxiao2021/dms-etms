package com.jd.bluedragon.distribution.worker.service;

import com.jd.bluedragon.distribution.worker.dao.TBTaskQueueDao;
import com.jd.bluedragon.distribution.worker.domain.TBTaskQueue;
import com.jd.etms.framework.utils.cache.annotation.Cache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    @Override
    @Cache(key = "TBTaskQueueServiceImpl.findAllQueueSize", memoryEnable = false,
            redisEnable = true, redisExpiredTime = 10 * 60 * 1000)
    public Map<String,Integer> findAllQueueSize(){
        Map<String,Integer> queueSizeMap = new HashMap<String, Integer>();
        List<Map<String,Object>> result = tbTaskQueueDao.findAllQueueSize();
        for(Map<String,Object> column : result){
            queueSizeMap.put(column.get("taskType").toString(),Integer.valueOf(column.get("queueSize").toString()));
        }
        return queueSizeMap;
    }
}
