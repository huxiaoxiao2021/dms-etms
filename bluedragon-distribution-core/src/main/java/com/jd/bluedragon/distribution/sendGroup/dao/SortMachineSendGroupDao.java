package com.jd.bluedragon.distribution.sendGroup.dao;

import com.jd.bluedragon.common.dao.BaseDao;
import com.jd.bluedragon.distribution.sendGroup.domain.SortMachineSendGroup;

import java.util.List;

/**
 * Created by jinjingcheng on 2017/6/28.
 */
public class SortMachineSendGroupDao extends BaseDao<SortMachineSendGroup>{

    public static final String namespace = SortMachineSendGroupDao.class.getName();
    public List<SortMachineSendGroup> findSendGroupByMachineCode(String machineCode) {
        return super.getSqlSession().selectList(namespace + ".findSendGroupByMachineCode", machineCode);
    }

    public void add(SortMachineSendGroup sendGroup) {
        add(namespace, sendGroup);
    }

    public SortMachineSendGroup get(Long groupId) {
        return get(namespace, groupId);
    }

    public void update(SortMachineSendGroup sendGroup) {
        update(namespace, sendGroup);
    }

    public void deleteSendGroupById(Long groupId) {
        super.getSqlSession().delete(namespace + ".deleteSendGroupById", groupId);
    }
}
