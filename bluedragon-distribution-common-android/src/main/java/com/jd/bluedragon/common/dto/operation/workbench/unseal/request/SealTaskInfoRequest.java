package com.jd.bluedragon.common.dto.operation.workbench.unseal.request;

import com.jd.bluedragon.common.dto.base.request.CurrentOperate;
import com.jd.bluedragon.common.dto.base.request.User;

import java.io.Serializable;

/**
 * @ClassName SealTaskInfoRequest
 * @Description
 * @Author wyh
 * @Date 2022/3/5 10:38
 **/
public class SealTaskInfoRequest implements Serializable {

    private static final long serialVersionUID = 6900794010329512223L;

    private User user;

    private CurrentOperate currentOperate;

    private String vehicleNumber;

    private String bizId;

    /**
     * 封车编码
     */
    private String sealCarCode;

    /**
     * 是否查ES明细
     */
    private boolean queryEsSealCarMonitor;

    /**
     * 是否查封签号
     */
    private boolean querySealCode;

    /**
     * 是否查节分排序
     */
    private boolean queryRankOrder;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public CurrentOperate getCurrentOperate() {
        return currentOperate;
    }

    public void setCurrentOperate(CurrentOperate currentOperate) {
        this.currentOperate = currentOperate;
    }

    public String getVehicleNumber() {
        return vehicleNumber;
    }

    public void setVehicleNumber(String vehicleNumber) {
        this.vehicleNumber = vehicleNumber;
    }

    public String getBizId() {
        return bizId;
    }

    public void setBizId(String bizId) {
        this.bizId = bizId;
    }

    public String getSealCarCode() {
        return sealCarCode;
    }

    public void setSealCarCode(String sealCarCode) {
        this.sealCarCode = sealCarCode;
    }

    public boolean getQueryEsSealCarMonitor() {
        return queryEsSealCarMonitor;
    }

    public void setQueryEsSealCarMonitor(boolean queryEsSealCarMonitor) {
        this.queryEsSealCarMonitor = queryEsSealCarMonitor;
    }

    public boolean getQuerySealCode() {
        return querySealCode;
    }

    public void setQuerySealCode(boolean querySealCode) {
        this.querySealCode = querySealCode;
    }

    public boolean getQueryRankOrder() {
        return queryRankOrder;
    }

    public void setQueryRankOrder(boolean queryRankOrder) {
        this.queryRankOrder = queryRankOrder;
    }
}
