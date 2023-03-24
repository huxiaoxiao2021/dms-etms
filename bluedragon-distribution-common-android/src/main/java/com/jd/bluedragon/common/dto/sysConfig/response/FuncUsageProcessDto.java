package com.jd.bluedragon.common.dto.sysConfig.response;

import java.io.Serializable;

/**
 * 安卓功能是否可使用结果
 *
 * @author fanggang7
 * @copyright jd.com 京东物流JDL
 * @time 2022-04-11 16:11:59 周一
 */
public class FuncUsageProcessDto implements Serializable {

    private static final long serialVersionUID = 5558432835220167498L;

    /**
     * 是否可以使用功能
     */
    private Integer canUse;

    /**
     * 提示信息
     */
    private String msg;

    /**
     * 提示类型
     */
    private Integer msgType;

    public Integer getCanUse() {
        return canUse;
    }

    public void setCanUse(Integer canUse) {
        this.canUse = canUse;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public Integer getMsgType() {
        return msgType;
    }

    public void setMsgType(Integer msgType) {
        this.msgType = msgType;
    }
}
