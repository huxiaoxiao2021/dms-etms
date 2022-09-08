package com.jd.bluedragon.common.dto.operation.workbench.enums;

/**
 * 天官赐福 ◎ 百无禁忌
 *
 * @Auther: 陆庆林（luqinglin3）
 * @Date: 2022/4/6
 * @Description:
 */
public enum JyBizTaskExceptionTagEnum {

    RELOCATION("relocation","客户改址"),
    UNPAID("unpaid","客户未付款"),
    OVERDUE("overdue", "超期催单"),
    SANWU("sanwu", "三无"),
    UNKNOW("unknow", "无法识别异常"),
    HEAVY("heavy", "重货"),
    ;

    private String code;
    private String name;
    private String style;

    JyBizTaskExceptionTagEnum(String code, String name) {
        this.code = code;
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public String getStyle() {
        return style;
    }

    public static String valueOf(JyBizTaskExceptionTagEnum[] array){
        StringBuilder sb = new StringBuilder();
        for (JyBizTaskExceptionTagEnum tag:array){
            sb.append(tag.getCode()).append(",");
        }
        String result =  sb.toString();
        return result.substring(0,result.length()-1);
    }

    public static JyBizTaskExceptionTagEnum getByCode(String code) {
        for (JyBizTaskExceptionTagEnum value : JyBizTaskExceptionTagEnum.values()) {
            if (value.getCode().equals(code)) {
                return value;
            }
        }
        return null;
    }
}
