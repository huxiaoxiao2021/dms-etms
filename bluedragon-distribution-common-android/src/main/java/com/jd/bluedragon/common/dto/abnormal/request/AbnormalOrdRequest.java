package com.jd.bluedragon.common.dto.abnormal.request;

import com.jd.bluedragon.common.dto.base.request.CurrentOperate;
import com.jd.bluedragon.common.dto.base.request.User;

import java.io.Serializable;

/**
 * @author : xumigen
 * @date : 2019/9/10
 */
public class AbnormalOrdRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    private User user;
    private CurrentOperate currentOperate;
    private String orderId;
    private Integer abnormalCode1;
    private String abnormalReason1;
    private Integer abnormalCode2;
    private String abnormalReason2;
    private String trackContent;  // 全程跟踪显示内容
    private String waveBusinessId;//版次号，路由系统的字段

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

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public Integer getAbnormalCode1() {
        return abnormalCode1;
    }

    public void setAbnormalCode1(Integer abnormalCode1) {
        this.abnormalCode1 = abnormalCode1;
    }

    public String getAbnormalReason1() {
        return abnormalReason1;
    }

    public void setAbnormalReason1(String abnormalReason1) {
        this.abnormalReason1 = abnormalReason1;
    }

    public Integer getAbnormalCode2() {
        return abnormalCode2;
    }

    public void setAbnormalCode2(Integer abnormalCode2) {
        this.abnormalCode2 = abnormalCode2;
    }

    public String getAbnormalReason2() {
        return abnormalReason2;
    }

    public void setAbnormalReason2(String abnormalReason2) {
        this.abnormalReason2 = abnormalReason2;
    }

    public String getTrackContent() {
        return trackContent;
    }

    public void setTrackContent(String trackContent) {
        this.trackContent = trackContent;
    }

    public String getWaveBusinessId() {
        return waveBusinessId;
    }

    public void setWaveBusinessId(String waveBusinessId) {
        this.waveBusinessId = waveBusinessId;
    }
}
