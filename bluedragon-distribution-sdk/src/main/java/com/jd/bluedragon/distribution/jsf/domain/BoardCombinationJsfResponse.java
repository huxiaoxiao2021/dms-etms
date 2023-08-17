package com.jd.bluedragon.distribution.jsf.domain;

import com.jd.bluedragon.distribution.base.domain.InvokeWithMsgBoxResult;

import java.util.List;

/**
 * Created by xumei3 on 2018/4/28.
 */
public class BoardCombinationJsfResponse {
    /** 相应状态码 */
    private Integer code;

    /** 响应消息 */
    private String message;

    /**
     * 消息盒子
     */
    private List<InvokeWithMsgBoxResult.MsgBox> msgBoxes;

    public BoardCombinationJsfResponse() {
    }

    public BoardCombinationJsfResponse(Integer code, String message) {
        super();
        this.code = code;
        this.message = message;
    }

    public Integer getCode() {
        return this.code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMessage() {
        return this.message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<InvokeWithMsgBoxResult.MsgBox> getMsgBoxes() {
        return msgBoxes;
    }

    public void setMsgBoxes(List<InvokeWithMsgBoxResult.MsgBox> msgBoxes) {
        this.msgBoxes = msgBoxes;
    }
}
