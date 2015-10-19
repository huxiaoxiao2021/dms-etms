package com.jd.bluedragon.distribution.worker.dao;

import com.jd.bluedragon.distribution.worker.domain.TBTaskQueue;
import org.mybatis.spring.SqlSessionTemplate;

import java.util.List;

/**
 * Created by wangtingwei on 2015/10/13.
 */
public class TBTaskQueueDao {

    private static final String selectQueueCountSQL=TBTaskQueueDao.class.getName()+".selectQueueCount";

    private static final String inserQueuesSQL=TBTaskQueueDao.class.getName()+".insertQueues";

    /**
     * 获取队列数量
     * @param taskType 任务类型名称
     * @return
     */
    public int selectQueueCount(String taskType){
        return (Integer)sqlSessionTemplate.selectOne(selectQueueCountSQL,taskType);
    }

    /**
     * 插入队列
     * @param domains
     * @return
     */
    public int insertQueues(List<TBTaskQueue> domains) throws Exception{
        if(domains.size()>100){
            throw new RuntimeException("插入队列数不能超过100,当前插入队列数为："+domains.size());
        }
        return (Integer)sqlSessionTemplate.insert(inserQueuesSQL, domains);
    }
    private SqlSessionTemplate sqlSessionTemplate;

    public SqlSessionTemplate getSqlSessionTemplate() {
        return sqlSessionTemplate;
    }

    public void setSqlSessionTemplate(SqlSessionTemplate sqlSessionTemplate) {
        this.sqlSessionTemplate = sqlSessionTemplate;
    }
}
