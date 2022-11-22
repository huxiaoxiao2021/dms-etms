package com.jd.bluedragon.distribution.base.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 带有消息盒子提示的相关业务交互使用
 * @author lixin39
 * @ClassName InvokeWithMsgBoxResult
 * @date 2019/6/17
 */
public class InvokeWithMsgBoxResult<T> implements Serializable {
    private static final long serialVersionUID = -1;

    /*
    只是调用验证接口是成功;理论上应该看msgBoxes 是否有返回数据。
     */
    public static final Integer CODE_SUCCESS = 200;
    public static final String MESSAGE_SUCCESS = "执行成功";

    public static final Integer CODE_FAIL = 400;
    public static final String MESSAGE_FAIL = "执行失败";

    public static final Integer CODE_ERROR = 500;
    public static final String MESSAGE_ERROR = "服务器异常";

    public static final Integer CODE_BIZ = 1000;
    public static final String MESSAGE_BIZ = "业务异常！";

    /**
     * 状态码
     */
    private int code;

    /**
     * 状态描述
     */
    private String message;

    private T data;

    /**
     * 消息盒子
     */
    private List<MsgBox> msgBoxes;

    public InvokeWithMsgBoxResult() {
    }

    public InvokeWithMsgBoxResult(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public InvokeWithMsgBoxResult(int code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    public void init(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public void toSuccess(){
        this.init(CODE_SUCCESS, MESSAGE_SUCCESS);
    }

    public void toSuccess(String message){
        this.init(CODE_SUCCESS, message);
    }

    public void toFail(){
        this.init(CODE_FAIL, MESSAGE_FAIL);
    }

    public void toFail(String message){
        this.init(CODE_FAIL, message);
    }

    public void toBizError() {
        this.init(CODE_BIZ, MESSAGE_BIZ);
    }

    public void toBizError(String message) {
        this.init(CODE_BIZ, message);
    }

    public void toError(){
        this.init(CODE_ERROR, MESSAGE_ERROR);
    }

    public void toError(String message){
        this.init(CODE_ERROR, message);
    }

    public void toCustomError(int code,String message){
        this.init(code, message);
    }

    public void addBox(MsgBox box) {
        if (this.getMsgBoxes() == null) {
            msgBoxes = new ArrayList<MsgBox>();
        }
        msgBoxes.add(box);
    }

    public void addBox(MsgBoxTypeEnum type, Integer code, String msg) {
        if (this.getMsgBoxes() == null) {
            msgBoxes = new ArrayList<MsgBox>();
        }
        MsgBox box = new MsgBox(type, code, msg);
        msgBoxes.add(box);
    }

    public void addPromptBox(Integer code, String msg) {
        if (this.getMsgBoxes() == null) {
            msgBoxes = new ArrayList<MsgBox>();
        }
        MsgBox box = new MsgBox(MsgBoxTypeEnum.PROMPT, code, msg);
        msgBoxes.add(box);
    }

    public void addWarningBox(Integer code, String msg) {
        if (this.getMsgBoxes() == null) {
            msgBoxes = new ArrayList<MsgBox>();
        }
        MsgBox box = new MsgBox(MsgBoxTypeEnum.WARNING, code, msg);
        msgBoxes.add(box);
    }

    public void addConfirmBox(Integer code, String msg) {
        if (this.getMsgBoxes() == null) {
            msgBoxes = new ArrayList<MsgBox>();
        }
        MsgBox box = new MsgBox(MsgBoxTypeEnum.CONFIRM, code, msg);
        msgBoxes.add(box);
    }

    public void addInterceptBox(Integer code, String msg) {
        if (this.getMsgBoxes() == null) {
            msgBoxes = new ArrayList<MsgBox>();
        }
        MsgBox box = new MsgBox(MsgBoxTypeEnum.INTERCEPT, code, msg);
        msgBoxes.add(box);
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<MsgBox> getMsgBoxes() {
        return msgBoxes;
    }

    public void setMsgBoxes(List<MsgBox> msgBoxes) {
        this.msgBoxes = msgBoxes;
    }

    public static class MsgBox implements Serializable {

        private static final long serialVersionUID = 3024545920925812168L;
        /**
         * 校验结果类型 1-提示框 2-确认框 3-拦截框
         */
        private MsgBoxTypeEnum type;

        /**
         * 状态码
         */
        private Integer code;

        /**
         * 提示语
         */
        private String msg;

        protected Object data;

        public MsgBox() {
        }

        public MsgBox(MsgBoxTypeEnum type, Integer code, String msg) {
            this.type = type;
            this.code = code;
            this.msg = msg;
        }

        public MsgBoxTypeEnum getType() {
            return type;
        }

        public void setType(MsgBoxTypeEnum type) {
            this.type = type;
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

        public Object getData() {
            return data;
        }

        public void setData(Object data) {
            this.data = data;
        }
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }


    public enum MsgBoxTypeEnum {
        /**
         * 提示
         */
        PROMPT,

        /**
         * 警告
         */
        WARNING,

        /**
         * 确认
         */
        CONFIRM,

        /**
         * 拦截
         */
        INTERCEPT
    }
}
