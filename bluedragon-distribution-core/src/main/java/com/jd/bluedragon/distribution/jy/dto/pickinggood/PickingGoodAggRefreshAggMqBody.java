package com.jd.bluedragon.distribution.jy.dto.pickinggood;

import java.io.Serializable;

/**
 *
 * 处理两个维度统计： 待提。已提。多提、已发、多发
 * 1、 按bizId做提货任务维度统计
 * 2、 sendFlag=true时， 对bizId任务下发货流向nextSiteId 维度做统计
 *
 * @Author zhengchengfa
 * @Date 2023/12/15 10:37
 * @Description
 */
public class PickingGoodAggRefreshAggMqBody implements Serializable {
    private static final long serialVersionUID = 1L;



    private String businessId;

    /**
     * BizSourceEnum
     */
    private Integer bizSource;

    private String bizId;
    /**
     * 操作场地
     */
    private Long siteId;
    /**
     * 发货标识
     */
    private Boolean sendFlag;
    /**
     * 发货流向
     */
    private Long nextSiteId;



    private Long operatorTime;


    public String getBusinessId() {
        return businessId;
    }

    public void setBusinessId(String businessId) {
        this.businessId = businessId;
    }

    public Integer getBizSource() {
        return bizSource;
    }

    public void setBizSource(Integer bizSource) {
        this.bizSource = bizSource;
    }

    public String getBizId() {
        return bizId;
    }

    public void setBizId(String bizId) {
        this.bizId = bizId;
    }

    public Long getOperatorTime() {
        return operatorTime;
    }

    public void setOperatorTime(Long operatorTime) {
        this.operatorTime = operatorTime;
    }

    public Long getSiteId() {
        return siteId;
    }

    public void setSiteId(Long siteId) {
        this.siteId = siteId;
    }

    public Boolean getSendFlag() {
        return sendFlag;
    }

    public void setSendFlag(Boolean sendFlag) {
        this.sendFlag = sendFlag;
    }

    public Long getNextSiteId() {
        return nextSiteId;
    }

    public void setNextSiteId(Long nextSiteId) {
        this.nextSiteId = nextSiteId;
    }

    enum BizSourceEnum{
        COMPLETE(1,  "任务完成触发统计回算"),
        SCAN_EXCEPTION(2, "PDA提货扫描过程中发现数据异常"),
        ;

        private Integer source;
        private String desc;

        BizSourceEnum(Integer source, String desc) {
            this.source = source;
            this.desc = desc;
        }

        public Integer getSource() {
            return source;
        }

        public void setSource(Integer source) {
            this.source = source;
        }

        public String getDesc() {
            return desc;
        }

        public void setDesc(String desc) {
            this.desc = desc;
        }
    }
}
