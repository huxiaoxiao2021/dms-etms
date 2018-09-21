package com.jd.bluedragon.distribution.send.domain;

/**
 * 确认消息盒子
 * <p>
 * Created by lixin39 on 2018/8/31.
 */
public class ConfirmMsgBox {

    /**
     * 发货确认-是否取消上次发货
     */
    public static final Integer CODE_CONFIRM_CANCEL_LAST_SEND = 4001;

    /**
     * 发货确认-是否强制发货
     */
    public static final Integer CODE_CONFIRM_IS_FORCE_SEND = 4002;

    /**
     * 确认状态码
     */
    private Integer code;

    /**
     * 确认信息
     */
    private String msg;

    public ConfirmMsgBox() {
    }

    public ConfirmMsgBox(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
