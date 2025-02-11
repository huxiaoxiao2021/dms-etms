package com.jd.bluedragon.common.dto.base.response;

/**
 * 返回code定义
 * @author : xumigen
 * @date : 2019/7/22
 */
public class ResponseCodeConstants {

    /**
     * 用于定义com.jd.bluedragon.common.dto.base.response.JdVerifyResponse.MsgBox code
     */
    public enum JdVerifyResponseMsgBox{
        CANCEL_LAST_SEND(4001,"是否取消上次发货"),
        /*
        此枚举在ver中定义了
         */
        SEND_WRONG_SITE(39000,"运单与预分拣站点不一致"),
        ;
        private int code;
        private String text;

        JdVerifyResponseMsgBox(int code, String text) {
            this.code = code;
            this.text = text;
        }

        public int getCode() {
            return code;
        }

        public String getText() {
            return text;
        }
    }
}
