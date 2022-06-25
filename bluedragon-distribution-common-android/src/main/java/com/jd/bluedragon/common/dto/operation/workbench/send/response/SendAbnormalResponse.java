package com.jd.bluedragon.common.dto.operation.workbench.send.response;

import com.jd.bluedragon.common.dto.operation.workbench.enums.SendAbnormalEnum;

import java.io.Serializable;

/**
 * @ClassName SendAbnormalResponse
 * @Description
 * @Author wyh
 * @Date 2022/5/19 21:57
 **/
public class SendAbnormalResponse implements Serializable {

    private static final long serialVersionUID = -750883851867204846L;

    private SendAbnormalEnum abnormalType;

    /**
     * 发货是否任务正常 : true 正常
     */
    private Boolean normalFlag;

    public SendAbnormalEnum getAbnormalType() {
        return abnormalType;
    }

    public void setAbnormalType(SendAbnormalEnum abnormalType) {
        this.abnormalType = abnormalType;
    }

    public Boolean getNormalFlag() {
        return normalFlag;
    }

    public void setNormalFlag(Boolean normalFlag) {
        this.normalFlag = normalFlag;
    }
}
