package com.jd.bluedragon.distribution.mergeWaybillCodeReturn.domain;

import java.io.Serializable;
import java.util.List;

/**
 * @ClassName: 123
 * @Description: 123
 * @author: hujiping
 * @date: 2018/9/17 22:00
 */
public class MergeWaybillCodeReturn implements Serializable {
    private String operatorNo;
    private String operatorName;
    private Integer operateUserId;
    private Integer operateUnitId;
    private String siteName;
    private Integer operateUnitType;
    private Long operateTime;
    private List<String> waybillCodeList;

    public String getOperatorNo() {
        return operatorNo;
    }

    public void setOperatorNo(String operatorNo) {
        this.operatorNo = operatorNo;
    }

    public String getOperatorName() {
        return operatorName;
    }

    public void setOperatorName(String operatorName) {
        this.operatorName = operatorName;
    }

    public Integer getOperateUserId() {
        return operateUserId;
    }

    public void setOperateUserId(Integer operateUserId) {
        this.operateUserId = operateUserId;
    }

    public Integer getOperateUnitId() {
        return operateUnitId;
    }

    public void setOperateUnitId(Integer operateUnitId) {
        this.operateUnitId = operateUnitId;
    }

    public String getSiteName() {
        return siteName;
    }

    public void setSiteName(String siteName) {
        this.siteName = siteName;
    }

    public Integer getOperateUnitType() {
        return operateUnitType;
    }

    public void setOperateUnitType(Integer operateUnitType) {
        this.operateUnitType = operateUnitType;
    }

    public Long getOperateTime() {
        return operateTime;
    }

    public void setOperateTime(Long operateTime) {
        this.operateTime = operateTime;
    }

    public List<String> getWaybillCodeList() {
        return waybillCodeList;
    }

    public void setWaybillCodeList(List<String> waybillCodeList) {
        this.waybillCodeList = waybillCodeList;
    }
}
