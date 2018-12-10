package com.jd.bluedragon.distribution.signAndReturn.domain;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * @ClassName: SignReturnPrintM
 * @Description: 签单返回合单打印信息
 * @author: hujiping
 * @date: 2018/11/23 16:43
 */
public class SignReturnPrintM implements Serializable {

    /**
     * 主键id
     * */
    private Long id;
    /**
     * 签单返回合单运单号
     * */
    private String newWaybillCode;
    /**
     * 商家编码
     * */
    private String busiId;
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
    private String createSiteName;
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
    private List<MergedWaybill> mergedWaybillList;
    /**
     * 是否删除 0-已删除，1-未删除
     * */
    private Integer isDelete;

    public Integer getIsDelete() {
        return isDelete;
    }

    public void setIsDelete(Integer isDelete) {
        this.isDelete = isDelete;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }


    public List<MergedWaybill> getMergedWaybillList() {
        return mergedWaybillList;
    }

    public void setMergedWaybillList(List<MergedWaybill> mergedWaybillList) {
        this.mergedWaybillList = mergedWaybillList;
    }

    public String getNewWaybillCode() {
        return newWaybillCode;
    }

    public void setNewWaybillCode(String newWaybillCode) {
        this.newWaybillCode = newWaybillCode;
    }

    public String getBusiId() {
        return busiId;
    }

    public void setBusiId(String busiId) {
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

    public String getCreateSiteName() {
        return createSiteName;
    }

    public void setCreateSiteName(String createSiteName) {
        this.createSiteName = createSiteName;
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
