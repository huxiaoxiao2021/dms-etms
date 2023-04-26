package com.jd.bluedragon.distribution.jy.enums;

/**
 * 客服回传状态枚举
 *
 * @author hujiping
 * @date 2023/3/16 10:47 AM
 */
public enum CustomerNotifyStatusEnum {

    UNKNOWN("0", "未知"),
    SELF_REPAIR_ORDER("170", "自营补单"),
    SELF_REPAIR_PRICE("171", "自营补差"),
    AGREE_LP("172", "外单客户同意理赔"),
    NOT_AGREE_LP("173", "外单客户不同意理赔"),
    CANCEL_ORDER("174", "取消订单"),
    ;

    private String code;

    private String desc;

    CustomerNotifyStatusEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static CustomerNotifyStatusEnum convertApproveEnum(String code){
        for (CustomerNotifyStatusEnum temp : CustomerNotifyStatusEnum.values()){
            if(temp.getCode().equals(code) ){
                return temp;
            }
        }
        return CustomerNotifyStatusEnum.UNKNOWN;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}
