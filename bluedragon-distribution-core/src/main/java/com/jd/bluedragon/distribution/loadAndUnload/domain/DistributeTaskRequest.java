package com.jd.bluedragon.distribution.loadAndUnload.domain;

import java.io.Serializable;
import java.util.List;

/**
 * @author lijie
 * @date 2020/6/25 17:32
 */
public class DistributeTaskRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 卸车人ERP
     * */
    private String unloadUserErp;

    /**
     * 月台号
     * */
    private String railWayPlatForm;

    /**
     * 更新人ERP
     * */
    private String updateUserErp;
    /**
     * 更新人名称
     * */
    private String updateUserName;
    /**
     * 封车编码
     * */
    private List<String> sealCarCodes;
    /**
     * 封车任务主键ID
     * */
    private List<Integer> unloadCarIds;

    public String getUnloadUserErp() {
        return unloadUserErp;
    }

    public void setUnloadUserErp(String unloadUserErp) {
        this.unloadUserErp = unloadUserErp;
    }

    public String getRailWayPlatForm() {
        return railWayPlatForm;
    }

    public void setRailWayPlatForm(String railWayPlatForm) {
        this.railWayPlatForm = railWayPlatForm;
    }

    public String getUpdateUserErp() {
        return updateUserErp;
    }

    public void setUpdateUserErp(String updateUserErp) {
        this.updateUserErp = updateUserErp;
    }

    public String getUpdateUserName() {
        return updateUserName;
    }

    public void setUpdateUserName(String updateUserName) {
        this.updateUserName = updateUserName;
    }

    public List<String> getSealCarCodes() {
        return sealCarCodes;
    }

    public void setSealCarCodes(List<String> sealCarCodes) {
        this.sealCarCodes = sealCarCodes;
    }

    public List<Integer> getUnloadCarIds() {
        return unloadCarIds;
    }

    public void setUnloadCarIds(List<Integer> unloadCarIds) {
        this.unloadCarIds = unloadCarIds;
    }
}
