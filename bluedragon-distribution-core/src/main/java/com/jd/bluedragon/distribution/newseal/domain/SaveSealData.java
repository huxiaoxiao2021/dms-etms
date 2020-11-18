package com.jd.bluedragon.distribution.newseal.domain;
import com.jd.etms.vos.dto.*;

public class SaveSealData extends SealCarDto {
    /**
     * 封车执行结果 1：封车成功 2：空批次剔除 3：封车失败
     */
    private Integer executeType;

    /**
     * 封车执行结果信息
     */
    private String executeMessage;

    public Integer getExecuteType() {
        return executeType;
    }

    public void setExecuteType(Integer executeType) {
        this.executeType = executeType;
    }

    public String getExecuteMessage() {
        return executeMessage;
    }

    public void setExecuteMessage(String executeMessage) {
        this.executeMessage = executeMessage;
    }
}
