package com.jd.bluedragon.common.dto.operation.workbench.aviationRailway.picking.res;

import java.io.Serializable;

/**
 * @Author zhengchengfa
 * @Date 2024/4/3 12:12
 * @Description
 */
public class FinishSendTaskRes implements Serializable {
    private static final long serialVersionUID = 4997976038143915037L;
    //批次号
    private String sendCode;
    //已扫件数
    private Integer scanItemNum;

    public String getSendCode() {
        return sendCode;
    }

    public void setSendCode(String sendCode) {
        this.sendCode = sendCode;
    }

    public Integer getScanItemNum() {
        return scanItemNum;
    }

    public void setScanItemNum(Integer scanItemNum) {
        this.scanItemNum = scanItemNum;
    }
}
