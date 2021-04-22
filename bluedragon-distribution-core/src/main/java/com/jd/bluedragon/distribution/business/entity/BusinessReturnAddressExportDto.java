package com.jd.bluedragon.distribution.business.entity;

import java.io.Serializable;
import java.util.Date;

/**
 * @Author: liming522
 * @Description:
 * @Date: create in 2021/4/8 15:19
 */
public class BusinessReturnAddressExportDto implements Serializable {
    private static final long serialVersionUID = 1L;
    /**
     * 分拣中心编码
     */
    private Integer dmsSiteCode;

    /**
     * 分拣中心名称
     */
    private String dmsSiteName;

    /**
     * 商家id
     */
    private Integer businessId;
    /**
     * 商家编码
     */
    private String businessCode;
    /**
     * 商家名称
     */
    private String businessName;

    /**
     * 签约区域
     * */
    private String deptNo;

    /**
     * 上次通知时间
     */
    private Date lastNoticeTime;

    /**
     * 上次操作换单时间
     */
    private String lastOperateTime;

    /**
     * 退货地址维护状态-0-未维护 1-已维护
     */
    private Integer returnAdressStatus;
    /**
     * 退货地址维护状态-0-未维护 1-已维护
     */
    private String returnAdressStatusDesc;

    /**
     * 退货量
     * */
    private Integer returnQuantity;

    /**
     * 1-有效，0-无效
     */
    private Integer yn;

    public Integer getDmsSiteCode() {
        return dmsSiteCode;
    }

    public void setDmsSiteCode(Integer dmsSiteCode) {
        this.dmsSiteCode = dmsSiteCode;
    }

    public String getDmsSiteName() {
        return dmsSiteName;
    }

    public void setDmsSiteName(String dmsSiteName) {
        this.dmsSiteName = dmsSiteName;
    }

    public Integer getBusinessId() {
        return businessId;
    }

    public void setBusinessId(Integer businessId) {
        this.businessId = businessId;
    }

    public String getBusinessCode() {
        return businessCode;
    }

    public void setBusinessCode(String businessCode) {
        this.businessCode = businessCode;
    }

    public String getBusinessName() {
        return businessName;
    }

    public void setBusinessName(String businessName) {
        this.businessName = businessName;
    }

    public String getDeptNo() {
        return deptNo;
    }

    public void setDeptNo(String deptNo) {
        this.deptNo = deptNo;
    }

    public Date getLastNoticeTime() {
        return lastNoticeTime;
    }

    public void setLastNoticeTime(Date lastNoticeTime) {
        this.lastNoticeTime = lastNoticeTime;
    }

    public String getLastOperateTime() {
        return lastOperateTime;
    }

    public void setLastOperateTime(String lastOperateTime) {
        this.lastOperateTime = lastOperateTime;
    }

    public Integer getReturnAdressStatus() {
        return returnAdressStatus;
    }

    public void setReturnAdressStatus(Integer returnAdressStatus) {
        this.returnAdressStatus = returnAdressStatus;
    }

    public String getReturnAdressStatusDesc() {
        return returnAdressStatusDesc;
    }

    public void setReturnAdressStatusDesc(String returnAdressStatusDesc) {
        this.returnAdressStatusDesc = returnAdressStatusDesc;
    }

    public Integer getReturnQuantity() {
        return returnQuantity;
    }

    public void setReturnQuantity(Integer returnQuantity) {
        this.returnQuantity = returnQuantity;
    }

    public Integer getYn() {
        return yn;
    }

    public void setYn(Integer yn) {
        this.yn = yn;
    }
}
    
