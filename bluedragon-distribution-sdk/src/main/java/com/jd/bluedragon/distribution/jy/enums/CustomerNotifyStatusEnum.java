package com.jd.bluedragon.distribution.jy.enums;

/**
 * 客服回传状态枚举
 *
 * @author hujiping
 * @date 2023/3/16 10:47 AM
 */
public enum CustomerNotifyStatusEnum {

    // todo 枚举状态待客服系统同步
    UNKNOWN(0, "未知"),
    AGREE_LP(1, "外单客户同意理赔"),
    NOT_AGREE_LP(2, "外单客户不同意理赔"),
    SELF_REPLENISH(3, "自营补单/补差"),
    CANCEL_ORDER(4, "取消订单"),
    ;

    private Integer code;

    private String desc;

    CustomerNotifyStatusEnum(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static CustomerNotifyStatusEnum convertApproveEnum(Integer code){
        for (CustomerNotifyStatusEnum temp : CustomerNotifyStatusEnum.values()){
            if(temp.getCode() == code){
                return temp;
            }
        }
        return CustomerNotifyStatusEnum.UNKNOWN;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}
