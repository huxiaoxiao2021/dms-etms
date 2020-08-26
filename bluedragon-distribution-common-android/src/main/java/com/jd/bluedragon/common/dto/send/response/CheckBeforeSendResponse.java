package com.jd.bluedragon.common.dto.send.response;

import java.util.ArrayList;
import java.util.List;

/**
 * 快运发货前校验结果，tipMessages初始化为new ArrayList<String>()
 */
public class CheckBeforeSendResponse {
    public CheckBeforeSendResponse(){
        this.tipMessages = new ArrayList<String>();
    }

    /**
     * 批次号
     */
    private String sendCode;
    /**
     * 运单类型
     */
    private Integer waybillType;
    /**
     * 包裹数
     */
    private Integer packageNum;
    /**
     * 提示信息列表
     */
    private List<String> tipMessages;

    public String getSendCode() {
        return sendCode;
    }

    public void setSendCode(String sendCode) {
        this.sendCode = sendCode;
    }

    /**
     * @return the waybillType
     */
    public Integer getWaybillType() {
        return waybillType;
    }
    /**
     * @param waybillType the waybillType to set
     */
    public void setWaybillType(Integer waybillType) {
        this.waybillType = waybillType;
    }
    /**
     * @return the packageNum
     */
    public Integer getPackageNum() {
        return packageNum;
    }
    /**
     * @param packageNum the packageNum to set
     */
    public void setPackageNum(Integer packageNum) {
        this.packageNum = packageNum;
    }
    /**
     * @return the tipMessages
     */
    public List<String> getTipMessages() {
        return tipMessages;
    }
    /**
     * @param tipMessages the tipMessages to set
     */
    public void setTipMessages(List<String> tipMessages) {
        this.tipMessages = tipMessages;
    }
}
