package com.jd.bluedragon.common.dto.goodsLoadingScanning.request;

import com.jd.bluedragon.common.dto.base.request.CurrentOperate;
import com.jd.bluedragon.common.dto.base.request.User;

import java.io.Serializable;
import java.util.List;

/**
 * 不齐异常扫描请求
 *
 */
public class GoodsExceptionScanningReq implements Serializable {
    private static final long serialVersionUID = 1L;

//    任务号
    private Long taskId;
//    运单号集合
    private List<String> waybillCode;
//    包裹号
    private String packageCode;
//    当前网点信息
    private CurrentOperate currentOperate;
//    当前操作用户信息
    private User user;

    public Long getTaskId() {
        return taskId;
    }

    public void setTaskId(Long taskId) {
        this.taskId = taskId;
    }

    public List<String> getWaybillCode() {
        return waybillCode;
    }

    public void setWaybillCode(List<String> waybillCode) {
        this.waybillCode = waybillCode;
    }

    public String getPackageCode() {
        return packageCode;
    }

    public void setPackageCode(String packageCode) {
        this.packageCode = packageCode;
    }

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
}
