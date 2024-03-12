package com.jd.bluedragon.distribution.jy.dto.unload.trust;

import java.io.Serializable;

/**
 * @Author zhengchengfa
 * @Date 2024/3/4 17:37
 * @Description
 */
public class RecycleMaterialAutoInspectionPackageDto implements Serializable {
    static final long serialVersionUID = 1L;

    private String packageCode;
    //箱号
    private String boxCode;
    /**
     * 物资编码
     */
    private String materialCode;
    /**
     * 操作场地ID
     */
    private Integer operateSiteId;
    /**
     * 操作场地名称
     */
    private String operateSiteName;
    /**
     * 操作时间，毫秒级时间戳
     */
    private Long operateTime;
    /**
     * 系统实际发消息时间
     */
    private Long sendTime;


    public String getPackageCode() {
        return packageCode;
    }

    public void setPackageCode(String packageCode) {
        this.packageCode = packageCode;
    }

    public String getBoxCode() {
        return boxCode;
    }

    public void setBoxCode(String boxCode) {
        this.boxCode = boxCode;
    }

    public String getMaterialCode() {
        return materialCode;
    }

    public void setMaterialCode(String materialCode) {
        this.materialCode = materialCode;
    }

    public Integer getOperateSiteId() {
        return operateSiteId;
    }

    public void setOperateSiteId(Integer operateSiteId) {
        this.operateSiteId = operateSiteId;
    }

    public String getOperateSiteName() {
        return operateSiteName;
    }

    public void setOperateSiteName(String operateSiteName) {
        this.operateSiteName = operateSiteName;
    }

    public Long getOperateTime() {
        return operateTime;
    }

    public void setOperateTime(Long operateTime) {
        this.operateTime = operateTime;
    }

    public Long getSendTime() {
        return sendTime;
    }

    public void setSendTime(Long sendTime) {
        this.sendTime = sendTime;
    }
}
