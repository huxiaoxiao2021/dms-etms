package com.jd.bluedragon.distribution.sendGroup.service.impl;

import com.jd.bluedragon.distribution.jsf.domain.InvokeResult;
import com.jd.bluedragon.distribution.sendGroup.dao.SortMachineGroupConfigDao;
import com.jd.bluedragon.distribution.sendGroup.dao.SortMachineSendGroupDao;
import com.jd.bluedragon.distribution.sendGroup.domain.SortMachineGroupConfig;
import com.jd.bluedragon.distribution.sendGroup.domain.SortMachineSendGroup;
import com.jd.bluedragon.distribution.sendGroup.service.SortMachineSendGroupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
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
        InvokeResult saveResult =  addSendGroupCheck();
        //
        SortMachineSendGroup sendGroup = new SortMachineSendGroup();
        sendGroup.setGroupName(groupName);
        sendGroup.setMachineCode(machineCode);
        sendGroup.setCreateUser(userName);
        sendGroup.setCreateUserCode(new Long(staffNo));
        sortMachineSendGroupDao.add(sendGroup);
        List<SortMachineGroupConfig> sortMachineGroupConfigs = initSortMachineGroupConfigList(sendGroup.getId(),
                chuteCodes);

        sortMachineGroupConfigDao.addBatch(sortMachineGroupConfigs);
        return saveResult;
    }

    //修改发货组关联的滑槽：
    //先删除已关联的的 再重新关联
    @Override
    public void updateSendGroup(Long groupId,
                                        String machineCode,
                                        String[] chuteCodes,
                                        Integer staffNo,
                                        String userName) {
        //更新发货组信息
        SortMachineSendGroup oldSendGroup = sortMachineSendGroupDao.get(groupId);
        oldSendGroup.setUpdateTime(new Date());
        oldSendGroup.setUpdateUserCode(new Long(staffNo));
        oldSendGroup.setUpdateUser(userName);
        sortMachineSendGroupDao.update(oldSendGroup);
        //删除已关联的滑道信息
        sortMachineGroupConfigDao.deleteMachineGroupConfigByGroupId(groupId);
        //重新绑定滑道
        List<SortMachineGroupConfig> sortMachineGroupConfigs = initSortMachineGroupConfigList(groupId,
                chuteCodes);

        sortMachineGroupConfigDao.addBatch(sortMachineGroupConfigs);
    }

    @Override
    public void deleteSendGroup(Long groupId) {
        sortMachineSendGroupDao.deleteSendGroupById(groupId);
        sortMachineGroupConfigDao.deleteMachineGroupConfigByGroupId(groupId);
    }


    private List<SortMachineGroupConfig> initSortMachineGroupConfigList(Long sendGroupId, String[] chuteCodes){
        if(chuteCodes != null && chuteCodes.length > 0){
            List<SortMachineGroupConfig> sortMachineGroupConfigs = new ArrayList<SortMachineGroupConfig>(chuteCodes.length);
            for(String chuteCode : chuteCodes){
                sortMachineGroupConfigs.add(initSortMachineGroupConfig(sendGroupId, chuteCode));
            }
            return sortMachineGroupConfigs;
        }
        return null;
    }

    private SortMachineGroupConfig initSortMachineGroupConfig(Long sendGroupId, String chuteCode){
        SortMachineGroupConfig sortMachineGroupConfig = new SortMachineGroupConfig();
        sortMachineGroupConfig.setChuteCode(chuteCode);
        sortMachineGroupConfig.setGroupId(sendGroupId);
        sortMachineGroupConfig.setYn(1);
        return sortMachineGroupConfig;
    }

    //todo
    private InvokeResult addSendGroupCheck(){
        InvokeResult checkResut = new InvokeResult();
        return checkResut;
    }
}
