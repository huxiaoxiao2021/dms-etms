package com.jd.bluedragon.distribution.jy.enums;

/**
 * @Author: chenyaguo@jd.com
 * @Date: 2023/8/2 20:14
 * @Description: 客服回传处理结果
 */
public enum CustomerReturnResultEnum {

    UNKNOWN(0,"0", "未知"),
    REPARI_DOWN(1,"","修复下传"),
    DIRECT_DOWN_1(2,"354","直接下传"),
    DIRECT_DOWN_2(2,"355","直接下传"),
    CHANGE_PACKAGE_DOWN(3,"356","更换包装下传"),
    SCRAPPED(4,"353","报废"),
    REVERSE_FALLBACK_1(5,"352","逆向退回"),
    REVERSE_FALLBACK_2(5,"350","逆向退回");

    private Integer type;

    private String code;

    private String desc;

    CustomerReturnResultEnum(Integer type,String code, String desc) {
        this.type = type;
        this.code = code;
        this.desc = desc;
    }

    public static CustomerReturnResultEnum convertApproveEnum(String code){
        for (CustomerReturnResultEnum temp : CustomerReturnResultEnum.values()){
            if(temp.getCode().equals(code) ){
                return temp;
            }
        }
        return CustomerReturnResultEnum.UNKNOWN;
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

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }
}
