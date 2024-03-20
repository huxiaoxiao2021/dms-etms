package com.jd.bluedragon.distribution.capability.send.domain;

import com.jd.bluedragon.distribution.api.request.PackageSendRequest;

import java.util.Date;

import com.jd.bluedragon.distribution.jsf.domain.ValidateIgnore;

/**
 * 天官赐福 ◎ 百无禁忌
 *
 * @Auther: 刘铎（liuduo8）
 * @Date: 2023/9/8
 * @Description: 发货标准服务入参
 */
public class SendRequest extends PackageSendRequest {

    /**
     * 执行发货原子条码
     */
    private String barCode;

    /**
     * 循环集包袋编码
     */
    private String cycleBoxCode;

    /**
     * 操作人ERP
     */
    private String opeUserErp;

    /**
     * 发货链处理模式
     */
    private SendChainModeEnum sendChainModeEnum;

    /**
     * 是否使用自定义操作时间，使用基本类型模式不使用，不使用时操作时间为服务器时间。
     */
    private boolean useCustomOperateTime;

    /**
     * 执行老校验链中可以不在提醒的逻辑使用
     */
    private ValidateIgnore validateIgnore;

    public String getBarCode() {
        return barCode;
    }

    public void setBarCode(String barCode) {
        this.barCode = barCode;
    }

    public String getCycleBoxCode() {
        return cycleBoxCode;
    }

    public void setCycleBoxCode(String cycleBoxCode) {
        this.cycleBoxCode = cycleBoxCode;
    }

    public String getOpeUserErp() {
        return opeUserErp;
    }

    public void setOpeUserErp(String opeUserErp) {
        this.opeUserErp = opeUserErp;
    }

    public SendChainModeEnum getSendChainModeEnum() {
        return sendChainModeEnum;
    }

    public void setSendChainModeEnum(SendChainModeEnum sendChainModeEnum) {
        this.sendChainModeEnum = sendChainModeEnum;
    }

    public boolean getUseCustomOperateTime() {
        return useCustomOperateTime;
    }

    public void setUseCustomOperateTime(boolean useCustomOperateTime) {
        this.useCustomOperateTime = useCustomOperateTime;
    }



    public ValidateIgnore getValidateIgnore() {
        return validateIgnore;
    }

    public void setValidateIgnore(ValidateIgnore validateIgnore) {
        this.validateIgnore = validateIgnore;
    }
}
