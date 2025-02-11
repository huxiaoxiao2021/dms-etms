package com.jd.bluedragon.common.dto.base.response;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 只用于后台服务与android 相关业务交互使用
 * android 端使用gradle 依赖
 * @author lixin39
 * @ClassName JdVerifyResponse
 * @date 2019/6/17
 */
public class JdVerifyResponse<T> implements Serializable {
    private static final long serialVersionUID = -2577538677042007863L;

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



    /**
     * 个性能力标识，返货true是代办使用到了非本系统控制的特殊返回值，需要调用者前端自己感知是否需要有特殊处理逻辑。
     * 如 ，冷链场景的下的验货扫描医药产品需要额外的医药提示音
     * 这样可以解决两个系统code码冲突问题，通用能力返回了10001需要做A逻辑，个性能力返回了10001要做B逻辑，可以独立区分出来
     */
    private Boolean selfDomFlag;


    public JdVerifyResponse() {
    }

    public JdVerifyResponse(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public JdVerifyResponse(int code, String message, T data) {
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

    public void addBox(List<MsgBox> boxs) {
        if (this.getMsgBoxes() == null) {
            msgBoxes = new ArrayList<MsgBox>();
        }
        msgBoxes.addAll(boxs);
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
        /**
         * 个性能力标识，返货true是代办使用到了非本系统控制的特殊返回值，需要调用者前端自己感知是否需要有特殊处理逻辑。
         * 如 ，冷链场景的下的验货扫描医药产品需要额外的医药提示音
         * 这样可以解决两个系统code码冲突问题，通用能力返回了10001需要做A逻辑，个性能力返回了10001要做B逻辑，可以独立区分出来
         */
        private Boolean selfDomFlag;

        public MsgBox() {
            this.selfDomFlag = false;
        }

        public MsgBox(MsgBoxTypeEnum type, Integer code, String msg) {
            this.type = type;
            this.code = code;
            this.msg = msg;
            this.selfDomFlag = false;
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

        public Boolean getSelfDomFlag() {
            return selfDomFlag;
        }

        public void setSelfDomFlag(Boolean selfDomFlag) {
            this.selfDomFlag = selfDomFlag;
        }
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    /**
     * 成功
     * @return
     */
    public boolean codeSuccess() {
        return this.code == CODE_SUCCESS;
    }

    public Boolean getSelfDomFlag() {
        return selfDomFlag;
    }

    public void setSelfDomFlag(Boolean selfDomFlag) {
        this.selfDomFlag = selfDomFlag;
    }
}
