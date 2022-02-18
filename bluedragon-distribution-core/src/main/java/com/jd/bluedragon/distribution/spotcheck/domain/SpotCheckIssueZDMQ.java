package com.jd.bluedragon.distribution.spotcheck.domain;

import java.io.Serializable;

/**
 * 抽检下发终端消息体
 *
 * @author hujiping
 * @date 2021/12/20 6:49 PM
 */
public class SpotCheckIssueZDMQ implements Serializable {

    private static final long serialVersionUID = 1L;

    // 运单号
    private String waybillCode;

    // 操作站点
    private Integer siteCode;

    // 操作人erp
    private String operateErp;

    // 操作人姓名
    private String operateUserName;

    // 复核重量
    private Double reviewWeight;

    // 复核体积
    private Double reviewVolume;

    // 复核时间戳
    private Long reviewTime;

    public String getWaybillCode() {
        return waybillCode;
    }

    public void setWaybillCode(String waybillCode) {
        this.waybillCode = waybillCode;
    }

    public Integer getSiteCode() {
        return siteCode;
    }

    public void setSiteCode(Integer siteCode) {
        this.siteCode = siteCode;
    }

    public String getOperateErp() {
        return operateErp;
    }

    public void setOperateErp(String operateErp) {
        this.operateErp = operateErp;
    }

    public String getOperateUserName() {
        return operateUserName;
    }

    public void setOperateUserName(String operateUserName) {
        this.operateUserName = operateUserName;
    }

    public Double getReviewWeight() {
        return reviewWeight;
    }

    public void setReviewWeight(Double reviewWeight) {
        this.reviewWeight = reviewWeight;
    }

    public Double getReviewVolume() {
        return reviewVolume;
    }

    public void setReviewVolume(Double reviewVolume) {
        this.reviewVolume = reviewVolume;
    }

    public Long getReviewTime() {
        return reviewTime;
    }

    public void setReviewTime(Long reviewTime) {
        this.reviewTime = reviewTime;
    }
}
