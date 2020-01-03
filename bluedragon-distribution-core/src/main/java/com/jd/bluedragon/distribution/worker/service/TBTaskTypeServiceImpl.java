package com.jd.bluedragon.distribution.worker.service;

import com.jd.bluedragon.Pager;
import com.jd.bluedragon.distribution.api.utils.JsonHelper;
import com.jd.bluedragon.distribution.worker.dao.TBTaskTypeDao;
import com.jd.bluedragon.distribution.worker.domain.TBTaskQueue;
import com.jd.bluedragon.distribution.worker.domain.TBTaskType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wangtingwei on 2015/10/8.
 */
@Service("tbTaskTypeService")
public class TBTaskTypeServiceImpl implements TBTaskTypeService{

    private static  final Logger log = LoggerFactory.getLogger(TBTaskTypeServiceImpl.class);

    @Autowired
    private TBTaskTypeDao tbTaskTypeDao;

    @Autowired
    private TBTaskQueueService tbTaskQueueService;

    @Override
    public Pager<List<TBTaskType>> readByName(Pager<String> pagerInnerTaskTypeName) {

        Pager<List<TBTaskType>> result=new Pager<List<TBTaskType>>(pagerInnerTaskTypeName);
        Integer count=tbTaskTypeDao.selectCountByName(pagerInnerTaskTypeName);
        if(log.isInfoEnabled()){
            log.info("查询参数为:{}", JsonHelper.toJson(pagerInnerTaskTypeName));
            log.info("查询记录总数为:{}", count);
        }
        result.setTotalSize(count);
        result.setData(tbTaskTypeDao.selectByNameUsePager(pagerInnerTaskTypeName));
        return result;
    }

    /**
     * 创建单条任务类型，并为其创建队列
     * @param domain
     * @return
     */
    @Transactional(readOnly = false,propagation = Propagation.REQUIRED, value="worker")
    @Override
    public int inserSingle(TBTaskType domain) throws Exception{
        if(domain.getTaskQueueNumber()>100){
            throw new RuntimeException("队列数数不能大于100，当前队列数为："+domain.getTaskQueueNumber());
        }
        int result= tbTaskTypeDao.insertSingle(domain);
        if(1==result){
            List<TBTaskQueue> list=new ArrayList<TBTaskQueue>(domain.getTaskQueueNumber());
            for (int i=domain.getTaskQueueNumber();i>0;){
                TBTaskQueue queue=new TBTaskQueue();
                queue.setId(Integer.valueOf(String.valueOf(domain.getId())+String.valueOf(i)));
                queue.setTaskType(domain.getBaseTaskType());
                queue.setOwnSign("DMS");
                queue.setQueueId(--i);
                list.add(queue);
            }
            result+= tbTaskQueueService.createQueues(list);
        }
        return result;
    }

    @Override
    public int updateSingleById(TBTaskType domain) {
        return tbTaskTypeDao.updateSingleById(domain);
    }

    @Override
    public TBTaskType readById(int id) {
        TBTaskType type= tbTaskTypeDao.selectById(id);
        if(null!=type){
            type.setTaskQueueNumber(tbTaskQueueService.getTaskQueueCount(type.getBaseTaskType()));
        }
        return type;
    }
}
