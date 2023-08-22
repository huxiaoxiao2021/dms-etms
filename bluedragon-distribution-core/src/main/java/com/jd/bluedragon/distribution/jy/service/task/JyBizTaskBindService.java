package com.jd.bluedragon.distribution.jy.service.task;

import com.jd.bluedragon.distribution.jy.task.JyBizTaskBindEntity;
import com.jd.bluedragon.distribution.jy.task.JyBizTaskBindEntityQueryCondition;

import java.util.List;

/**
 * @Author zhengchengfa
 * @Date 2023/8/2 20:19
 * @Description
 */
public interface JyBizTaskBindService {

    /**
     * 查询异常绑定的任务（needDetailBizIdList没有绑定到bizId上）
     * @param needDetailBizIdList
     * @return
     */
    List<JyBizTaskBindEntity> queryBindTaskByBindDetailBizIds(List<String> needDetailBizIdList, Integer type);

    void taskBinding(List<JyBizTaskBindEntity> jyBizTaskBindEntityList);

    void taskUnbinding(JyBizTaskBindEntity entity);

    List<JyBizTaskBindEntity> queryBindTaskList(JyBizTaskBindEntityQueryCondition condition);

    Integer countBind(String detailBizId);
}
