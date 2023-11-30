package com.jd.bluedragon.common.dto.operation.workbench.enums;

/**
 * @Author: chenyaguo@jd.com
 * @Date: 2023/8/2 20:14
 * @Description: 客服回传处理结果
 */
public enum JyExpCustomerReturnResultEnum {





    MAIN_LAND_DIRECT_DOWN_1(2,"534","直接下传"),
    MAIN_LAND_DIRECT_DOWN_2(2,"538","直接下传"),
    MAIN_LAND_DIRECT_DOWN_3(2,"539","直接下传"),
    MAIN_LAND_DIRECT_DOWN_4(2,"532","直接下传"),
    MAIN_LAND_CHANGE_PACKAGE_DOWN(3,"537","更换包装下传"),
    MAIN_LAND_SCRAPPED(4,"536","报废"),
    MAIN_LAND_REVERSE_FALLBACK_1(5,"533","逆向退回"),
    MAIN_LAND_REVERSE_FALLBACK_2(5,"535","逆向退回"),
    MAIN_LAND_REPLENISH_1(6,"530","补单/补差"),
    MAIN_LAND_REPLENISH_2(6,"531","补单/补差"),
    
    HA_MO_DIRECT_DOWN_1(2,"354","直接下传"),
    HA_MO_DIRECT_DOWN_2(2,"355","直接下传"),
    HA_MO_DIRECT_DOWN_3(2,"351","直接下传"),
    HA_MO_CHANGE_PACKAGE_DOWN(3,"356","更换包装下传"),
    HA_MO_SCRAPPED(4,"353","报废"),
    HA_MO_REVERSE_FALLBACK_1(5,"352","逆向退回"),
    HA_MO_REVERSE_FALLBACK_2(5,"350","逆向退回"),

    INTERNATIONAL_DIRECT_DOWN_1(2, "652", "直接下传"),
    INTERNATIONAL_DIRECT_DOWN_2(2, "653", "直接下传"),
    INTERNATIONAL_REVERSE_FALLBACK_1(5,"651","逆向退回"),
    INTERNATIONAL_REVERSE_FALLBACK_2(5,"654","逆向退回");
    private Integer type;

    private String code;

    private String desc;

    JyExpCustomerReturnResultEnum(Integer type, String code, String desc) {
        this.type = type;
        this.code = code;
        this.desc = desc;
    }

    public static JyExpCustomerReturnResultEnum convertApproveEnum(String code){
        for (JyExpCustomerReturnResultEnum temp : JyExpCustomerReturnResultEnum.values()){
            if(temp.getCode().equals(code) ){
                return temp;
            }
        }
        return null;
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
