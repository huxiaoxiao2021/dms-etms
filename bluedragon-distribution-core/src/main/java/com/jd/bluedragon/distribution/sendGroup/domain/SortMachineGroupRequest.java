package com.jd.bluedragon.distribution.sendGroup.domain;

import com.jd.bluedragon.distribution.api.JdRequest;

import java.io.Serializable;

/**
 * Created by jinjingcheng on 2017/7/13.
 */
public class SortMachineGroupRequest extends JdRequest{
    /**
     * 分组id
     */
    private Long groupId;
    /**
     * 分拣机编号
     */
    private String machineCode;
    /**
     * 分组名称
     */
    private String groupName;

    /**
     * 滑槽号
     */
    private String[] chuteCodes;

    public Long getGroupId() {
        return groupId;
    }

    public void setGroupId(Long groupId) {
        this.groupId = groupId;
    }

    public String getMachineCode() {
        return machineCode;
    }

    public void setMachineCode(String machineCode) {
        this.machineCode = machineCode;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String[] getChuteCodes() {
        return chuteCodes;
    }

    public void setChuteCodes(String[] chuteCodes) {
        this.chuteCodes = chuteCodes;
    }
}
