package com.jd.bluedragon.common.dto.sorting.request;

import com.jd.bluedragon.common.dto.base.request.CurrentOperate;
import com.jd.bluedragon.common.dto.base.request.User;

import java.io.Serializable;

/**
 * 当前操作信息封装
 * @author : xumigen
 * @date : 2019/6/22
 */
public class SortingCheckRequest implements Serializable {

    private static final long serialVersionUID = -1L;


    private CurrentOperate currentOperate;

    private User user;

    /**
     * 操作类型
     */
    private Integer operateType;
    /**
     * 包裹号
     */
    private String packageCode;
    /**
     * 收货站点
     */
    private Integer receiveSiteCode;
    /**
     * 业务类型
     */
    private Integer businessType;
    /**
     * 箱号
     */
    private String boxCode;
    /**
     * 是否报丢 1报丢
     */
    private Integer isLoss;

    public CurrentOperate getCurrentOperate() {
        return currentOperate;
    }

    public void setCurrentOperate(CurrentOperate currentOperate) {
        this.currentOperate = currentOperate;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Integer getOperateType() {
        return operateType;
    }

    public void setOperateType(Integer operateType) {
        this.operateType = operateType;
    }

    public String getPackageCode() {
        return packageCode;
    }

    public void setPackageCode(String packageCode) {
        this.packageCode = packageCode;
    }

    public Integer getReceiveSiteCode() {
        return receiveSiteCode;
    }

    public void setReceiveSiteCode(Integer receiveSiteCode) {
        this.receiveSiteCode = receiveSiteCode;
    }

    public Integer getBusinessType() {
        return businessType;
    }

    public void setBusinessType(Integer businessType) {
        this.businessType = businessType;
    }

    public String getBoxCode() {
        return boxCode;
    }

    public void setBoxCode(String boxCode) {
        this.boxCode = boxCode;
    }

    public Integer getIsLoss() {
        return isLoss;
    }

    public void setIsLoss(Integer isLoss) {
        this.isLoss = isLoss;
    }
}
