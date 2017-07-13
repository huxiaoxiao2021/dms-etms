package com.jd.bluedragon.distribution.sendGroup.dao;

import com.jd.bluedragon.common.dao.BaseDao;
import com.jd.bluedragon.distribution.sendGroup.domain.SortMachineGroupConfig;

import java.util.List;

/**
 * Created by jinjingcheng on 2017/6/28.
 */
public class SortMachineGroupConfigDao extends BaseDao<SortMachineGroupConfig> {
    public static final String namespace = SortMachineGroupConfigDao.class.getName();

    public List<SortMachineGroupConfig> findSendGroupConfigByGroupId(Integer groupId) {
        return super.getSqlSession().selectList(namespace + ".findSendGroupConfigByGroupId", groupId);
    }

    public int addBatch(List<SortMachineGroupConfig> sortMachineGroupConfigs) {
         return super.getSqlSession().insert(namespace + ".addBatch", sortMachineGroupConfigs);
    }
    public int deleteMachineGroupConfigByGroupId(Long groupId) {
        return super.getSqlSession().delete(namespace + ".deleteMachineGroupConfigByGroupId", groupId);
    }
}
