package com.jd.bluedragon.utils;

/**
 * 枚举 定义
 * @author : xumigen
 * @date : 2019/10/12
 */
public class ConstantEnums {

    /**
     * Iot business字段枚举
     */
    public enum IotBusiness{
        B2C("B2C"),C2C("C2C");
        private String type;

        IotBusiness(String type) {
            this.type = type;
        }

        public String getType() {
            return type;
        }
    }
}
