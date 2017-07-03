package com.jd.bluedragon.distribution.sendGroup.dao;

import com.jd.bluedragon.common.dao.BaseDao;
import com.jd.bluedragon.distribution.sendGroup.domain.SortMachineGroupConfig;

import java.util.List;

/**
 * Created by jinjingcheng on 2017/6/28.
 */
public class SortMachineGroupConfigDao extends BaseDao<SortMachineGroupConfig> {
    public static final String namespace = SortMachineSendGroupDao.class.getName();

    //todo
    public List<SortMachineGroupConfig> findSendGroupConfigByGroupId(Integer groupId) {
        return null;
    }
}
