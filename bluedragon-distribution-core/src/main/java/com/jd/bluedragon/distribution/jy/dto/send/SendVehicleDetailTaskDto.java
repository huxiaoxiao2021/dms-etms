package com.jd.bluedragon.distribution.jy.dto.send;

import java.io.Serializable;
import java.util.List;

/**
 * 运输派车发货明细任务MQ消息体
 */
public class SendVehicleDetailTaskDto implements Serializable {

    private static final long serialVersionUID = -8024388953489536391L;

    /**
     * 始发场地ID
	 */
    private Long startSiteId;
    /**
     * 始发场地名称
     */
    private String startSiteName;
    /**
     * 目的场地ID
     */
    private Long endSiteId;
    /**
     * 目的场地名称
     */
    private String endSiteName;
    /**
     * 业务主键 == 派车明细单号
     */
    private String bizId;
    /**
     * 批次号
     */
    private List<String> sendCodes;
    /**
     * 操作类型
     */
    private Integer operateType;

    public Long getStartSiteId() {
        return startSiteId;
    }

    public void setStartSiteId(Long startSiteId) {
        this.startSiteId = startSiteId;
    }

    public String getStartSiteName() {
        return startSiteName;
    }

    public void setStartSiteName(String startSiteName) {
        this.startSiteName = startSiteName;
    }

    public Long getEndSiteId() {
        return endSiteId;
    }

    public void setEndSiteId(Long endSiteId) {
        this.endSiteId = endSiteId;
    }

    public String getEndSiteName() {
        return endSiteName;
    }

    public void setEndSiteName(String endSiteName) {
        this.endSiteName = endSiteName;
    }

    public String getBizId() {
        return bizId;
    }

    public void setBizId(String bizId) {
        this.bizId = bizId;
    }

    public List<String> getSendCodes() {
        return sendCodes;
    }

    public void setSendCodes(List<String> sendCodes) {
        this.sendCodes = sendCodes;
    }

    public Integer getOperateType() {
        return operateType;
    }

    public void setOperateType(Integer operateType) {
        this.operateType = operateType;
    }
}
