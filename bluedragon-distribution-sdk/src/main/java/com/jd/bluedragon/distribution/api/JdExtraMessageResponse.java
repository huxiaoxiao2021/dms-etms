package com.jd.bluedragon.distribution.api;

/**
 * 带额外业务编码的JdResponse
 *
 * @author hujiping
 * @date 2022/7/29 11:20 AM
 */
public class JdExtraMessageResponse extends JdResponse {

    // 提示信息编码
    public static final Integer CODE_HINT = 30002;

    /** 额外的业务编码 */
    private Integer extraBusinessCode;

    /** 额外的业务响应消息 */
    private String extraBusinessMessage;

    public JdExtraMessageResponse() {
    }

    public JdExtraMessageResponse(Integer code, String message) {
        super(code, message);
    }

    public Integer getExtraBusinessCode() {
        return extraBusinessCode;
    }

    public void setExtraBusinessCode(Integer extraBusinessCode) {
        this.extraBusinessCode = extraBusinessCode;
    }

    public String getExtraBusinessMessage() {
        return extraBusinessMessage;
    }

    public void setExtraBusinessMessage(String extraBusinessMessage) {
        this.extraBusinessMessage = extraBusinessMessage;
    }
}
