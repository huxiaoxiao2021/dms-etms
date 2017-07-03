package com.jd.bluedragon.distribution.sendGroup.service.impl;

import com.jd.bluedragon.distribution.jsf.domain.InvokeResult;
import com.jd.bluedragon.distribution.sendGroup.dao.SortMachineGroupConfigDao;
import com.jd.bluedragon.distribution.sendGroup.dao.SortMachineSendGroupDao;
import com.jd.bluedragon.distribution.sendGroup.domain.SortMachineGroupConfig;
import com.jd.bluedragon.distribution.sendGroup.domain.SortMachineSendGroup;
import com.jd.bluedragon.distribution.sendGroup.service.SortMachineSendGroupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * Created by jinjingcheng on 2017/6/29.
 */
@Service("sortMachineSendGroupService")
public class SortMachineSendGroupServiceImpl implements SortMachineSendGroupService {

    @Autowired
    SortMachineSendGroupDao sortMachineSendGroupDao;
    @Autowired
    SortMachineGroupConfigDao sortMachineGroupConfigDao;

    @Override
    public List<SortMachineSendGroup> findSendGroupByMachineCode(String machineCode) {
        return sortMachineSendGroupDao.findSendGroupByMachineCode(machineCode);
    }

    @Override
    public List<SortMachineGroupConfig> findSendGroupConfigByGroupId(Integer groupId) {
        return sortMachineGroupConfigDao.findSendGroupConfigByGroupId(groupId);
    }

    @Override
    public InvokeResult addSendGroup(String machineCode,
                                     String groupName,
                                     String[] chuteCodes,
                                     Integer staffNo,
                                     String userName) {
        //检查
        addSendGroupCheck();
        //
        SortMachineSendGroup sendGroup = new SortMachineSendGroup();
        sendGroup.setGroupName(groupName);
        sendGroup.setMachineCode(machineCode);
        sendGroup.setYn(1);
        sendGroup.setCreateUser(userName);
        sendGroup.setCreateUserCode(new Long(staffNo));
        Date createTime = new Date();
        sendGroup.setCreateTime(createTime);
        sendGroup.setUpdateTime(createTime);
        sendGroup.setUpdateUser(userName);
        sendGroup.setUpdateUserCode(new Long(staffNo));
        sortMachineSendGroupDao.add(sendGroup);


        return null;
    }

    private List<SortMachineGroupConfig> initSortMachineGroupConfigList(Long sendGroupId, String[] chuteCodes){
        if(chuteCodes != null && chuteCodes.length > 0){

        }
        return null;
    }

    private SortMachineGroupConfig initSortMachineGroupConfig(Long sendGroupId, String chuteCode){
        return null;
    }

    //todo
    private InvokeResult addSendGroupCheck(){
        InvokeResult checkResut = new InvokeResult();
        return checkResut;
    }
}
