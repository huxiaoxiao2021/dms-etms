package com.jd.bluedragon.distribution.api.response;


import com.jd.bluedragon.distribution.api.JdResponse;

/**
 * Created by wuzuxiang on 2016/12/21.
 */
public class BatchSendPrintImageResponse extends JdResponse {

    private static final long serialVersionUID = 8138966037028469675L;

    public static final Integer SEND_CODE_NOT_FOUNT_ERROR = 5001;

    public static final String MESSAGE_SEND_CODE_NOT_FOUNT_ERROR = "找不对对应的批次号";

    public BatchSendPrintImageResponse(){
        super();
    }

    public BatchSendPrintImageResponse(Integer code, String message){
        super(code, message);
    }

    /**
     * 批次号标签base64位字符串
     */
    private String sendCodeImgStr;

    /**
     * 批次号字符串
     */
    private String sendCode;

    public String getSendCodeImgStr() {
        return sendCodeImgStr;
    }

    public void setSendCodeImgStr(String sendCodeImgStr) {
        this.sendCodeImgStr = sendCodeImgStr;
    }

    public String getSendCode() {
        return sendCode;
    }

    public void setSendCode(String sendCode) {
        this.sendCode = sendCode;
    }
}
