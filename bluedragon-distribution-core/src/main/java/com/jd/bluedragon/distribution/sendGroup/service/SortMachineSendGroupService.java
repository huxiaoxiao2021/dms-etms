package com.jd.bluedragon.distribution.sendGroup.service;

import com.jd.bluedragon.distribution.jsf.domain.InvokeResult;
import com.jd.bluedragon.distribution.sendGroup.domain.SortMachineGroupConfig;
import com.jd.bluedragon.distribution.sendGroup.domain.SortMachineSendGroup;

import java.util.List;

/**
 * Created by jinjingcheng on 2017/6/29.
 */
public interface SortMachineSendGroupService {
    /**
     *  根据分拣机编码查询分拣机组
     * @param machineCode 分拣机编码
     * @return
     */
    List<SortMachineSendGroup> findSendGroupByMachineCode(String machineCode);

    /**
     * 根据发货分组ID获取该分组关联的滑道
     * @param groupId
     * @return
     */
    List<SortMachineGroupConfig> findSendGroupConfigByGroupId(Integer groupId);

    /**
     * 添加发货组
     * @param machineCode 分拣机编号
     * @param groupName 组名
     * @param chuteCodes 滑道号
     * @param staffNo 操作员ID
     * @param userName 操作员姓名
     * @return 保存结果
     */
    InvokeResult addSendGroup(String machineCode,
                              String groupName,
                              String[] chuteCodes,
                              Integer staffNo,
                              String userName);
}
