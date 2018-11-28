package com.jd.bluedragon.distribution.signAndReturn.domain;

import java.io.Serializable;
import java.util.Date;
import java.util.Map;

/**
 * @ClassName: SignReturnPrintM
 * @Description: 签单返回合单打印信息
 * @author: hujiping
 * @date: 2018/11/23 16:43
 */
public class SignReturnPrintM implements Serializable {

    /**
     * 签单返回合单运单号
     * */
    private String mergedWaybillCode;
    /**
     * 商家编码
     * */
    private Integer busiId;
    /**
     * 商家名称
     * */
    private String busiName;
    /**
     * 返单周期
     * */
    private String returnCycle;
    /**
     * 合单操作日期
     * */
    private Date operateTime;
    /**
     * 合单操作机构
     * */
    private String orgId;
    /**
     * 合单操作人
     * */
    private String operateUser;
    /**
     * 合单运单数
     * */
    private Integer mergeCount;
    /**
     * 旧单号集合
     * */
    private String waybillCodes;
    /**
     * 运单号和妥投时间对应的map集合
     * */
    private Map<String,Date> map;

    public String getWaybillCodes() {
        return waybillCodes;
    }

    public void setWaybillCodes(String waybillCodes) {
        this.waybillCodes = waybillCodes;
    }

    public Map<String, Date> getMap() {
        return map;
    }

    public void setMap(Map<String, Date> map) {
        this.map = map;
    }

    public String getMergedWaybillCode() {
        return mergedWaybillCode;
    }

    public void setMergedWaybillCode(String mergedWaybillCode) {
        this.mergedWaybillCode = mergedWaybillCode;
    }

    public Integer getBusiId() {
        return busiId;
    }

    public void setBusiId(Integer busiId) {
        this.busiId = busiId;
    }

    public String getBusiName() {
        return busiName;
    }

    public void setBusiName(String busiName) {
        this.busiName = busiName;
    }

    public String getReturnCycle() {
        return returnCycle;
    }

    public void setReturnCycle(String returnCycle) {
        this.returnCycle = returnCycle;
    }

    public Date getOperateTime() {
        return operateTime;
    }

    public void setOperateTime(Date operateTime) {
        this.operateTime = operateTime;
    }

    public String getOrgId() {
        return orgId;
    }

    public void setOrgId(String orgId) {
        this.orgId = orgId;
    }

    public String getOperateUser() {
        return operateUser;
    }

    public void setOperateUser(String operateUser) {
        this.operateUser = operateUser;
    }

    public Integer getMergeCount() {
        return mergeCount;
    }

    public void setMergeCount(Integer mergeCount) {
        this.mergeCount = mergeCount;
    }
}
