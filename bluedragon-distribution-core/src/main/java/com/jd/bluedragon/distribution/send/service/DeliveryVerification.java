package com.jd.bluedragon.distribution.send.service;

/**
 * 配送发货验证
 *
 * Created by wangtingwei on 2017/4/26.
 */
public interface DeliveryVerification {

    /**
     * 验证发货
     * @param       boxCode                 箱号
     * @param       receiveSiteCode         收货站点
     * @return
     */
    VerificationResult verification(String boxCode,Integer receiveSiteCode,boolean checkPackage);

    /**
     * 验证结果
     */
    public static class VerificationResult{

        /**
         * 验证结果码
         */
        private boolean code;

        /**
         * 验证消息体
         */
        private String message;

        protected VerificationResult(){
            this.code=true;
        }


        public boolean getCode() {
            return code;
        }

        public void setCode(boolean code) {
            this.code = code;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }


    }
}
