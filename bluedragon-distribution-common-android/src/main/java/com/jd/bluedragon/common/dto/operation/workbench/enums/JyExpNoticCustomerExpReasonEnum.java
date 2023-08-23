package com.jd.bluedragon.common.dto.operation.workbench.enums;

/**
 * @Author: chenyaguo@jd.com
 * @Date: 2023/8/23 10:30
 * @Description:
 */
public class JyExpNoticCustomerExpReasonEnum {


    public enum ExpBusinessIDEnum{

        BUSINESS_ID_HK_HO(413,"港澳业务id"),
        BUSINESS_ID_MAIN_LAND(422,"大陆务id"),
        ;



        private Integer code;
        private String name;


        public Integer getCode() {
            return code;
        }

        public void setCode(Integer code) {
            this.code = code;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        ExpBusinessIDEnum(Integer code, String name) {
            this.code = code;
            this.name = name;
        }
    }

}
