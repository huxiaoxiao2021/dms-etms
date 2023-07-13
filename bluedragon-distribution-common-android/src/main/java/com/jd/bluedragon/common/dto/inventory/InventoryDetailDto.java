package com.jd.bluedragon.common.dto.inventory;

import java.io.Serializable;

/**
 * @Author zhengchengfa
 * @Date 2023/7/13 16:28
 * @Description  找货任务
 */
public class InventoryDetailDto implements Serializable {
    private static final long serialVersionUID = -6051001368608203945L;

    private String packageCode;
    /**
     * 找货方式
     */
    private String findStatus;

    public String getPackageCode() {
        return packageCode;
    }

    public void setPackageCode(String packageCode) {
        this.packageCode = packageCode;
    }


    public String getFindStatus() {
        return findStatus;
    }

    public void setFindStatus(String findStatus) {
        this.findStatus = findStatus;
    }
}
