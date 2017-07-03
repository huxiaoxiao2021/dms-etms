package com.jd.bluedragon.distribution.sendGroup.dao;

import com.jd.bluedragon.common.dao.BaseDao;
import com.jd.bluedragon.distribution.sendGroup.domain.SortMachineSendGroup;

import java.util.List;

/**
 * Created by jinjingcheng on 2017/6/28.
 */
public class SortMachineSendGroupDao extends BaseDao<SortMachineSendGroup>{

    public static final String namespace = SortMachineSendGroupDao.class.getName();
    //todo
    public List<SortMachineSendGroup> findSendGroupByMachineCode(String machineCode) {
        return null;
    }

    //todo
    public void add(SortMachineSendGroup sendGroup) {
    }
}
