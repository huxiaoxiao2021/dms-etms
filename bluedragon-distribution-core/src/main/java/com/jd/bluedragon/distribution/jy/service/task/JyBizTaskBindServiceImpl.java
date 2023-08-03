package com.jd.bluedragon.distribution.jy.service.task;

import com.jd.bluedragon.distribution.jy.dao.task.JyBizTaskBindDao;
import com.jd.bluedragon.distribution.jy.task.JyBizTaskBindEntity;
import com.jd.bluedragon.distribution.jy.task.JyBizTaskBindEntityQueryCondition;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

/**
 * @Author zhengchengfa
 * @Date 2023/8/2 20:19
 * @Description
 */
@Service
public class JyBizTaskBindServiceImpl implements JyBizTaskBindService {

    @Autowired
    private JyBizTaskBindDao jyBizTaskBindDao;

    @Override
    public List<JyBizTaskBindEntity> queryBindTaskByBindDetailBizIds(List<String> needDetailBizIdList, Integer type) {
        if(CollectionUtils.isEmpty(needDetailBizIdList) || Objects.isNull(type)) {
            return null;
        }
        JyBizTaskBindEntityQueryCondition queryCondition = new JyBizTaskBindEntityQueryCondition();
        queryCondition.setBindDetailBizIdList(needDetailBizIdList);
        queryCondition.setType(type);
        return jyBizTaskBindDao.queryBindTaskByBindDetailBizIds(queryCondition);
    }

    @Override
    public void taskBinding(List<JyBizTaskBindEntity> jyBizTaskBindEntityList) {
        jyBizTaskBindDao.batchAdd(jyBizTaskBindEntityList);
    }

    @Override
    public void taskUnbinding(JyBizTaskBindEntity entity) {
        jyBizTaskBindDao.taskUnbinding(entity);
    }
}
