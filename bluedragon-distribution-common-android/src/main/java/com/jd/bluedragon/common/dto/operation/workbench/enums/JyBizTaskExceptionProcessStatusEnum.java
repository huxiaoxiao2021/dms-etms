package com.jd.bluedragon.common.dto.operation.workbench.enums;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 天官赐福 ◎ 百无禁忌
 *
 * @Auther: 陆庆林（luqinglin3）
 * @Date: 2022/4/6
 * @Description:
 */
public enum JyBizTaskExceptionProcessStatusEnum {

    PENDING_ENTRY(0, "待录入"),
    WAITING_MATCH(1, "匹配中"),
    ON_SHELF(2, "上架"),
    DONE(3, "处理完成"),
    WAITING_PRINT(4, "待打印"),
    APPROVING(5, "审批中"),
    APPROVE_PASS(6, "审批通过"),
    APPROVE_REJECT(7, "审批驳回"),
    WAITER_INTERVENTION(8, "客服介入中"),
    WAITER_EXECUTION(9,"待执行"),
    WAITING_EXCHANGE_PRINT(10,"待换单打印"),
    WAITING_UPLOAD_WEIGHT_VOLUME(11,"待称重"),
    WAITING_REPRINT(12,"待补打"),
    PROCESS_FAIL(-1,"处理失败"),
    INTERCEPT_RECALL(-2,"撤销拦截"),
    ;

    private Integer code;
    private String name;

    public static Map<Integer, String> ENUM_MAP;

    public static List<Integer> ENUM_LIST;


    JyBizTaskExceptionProcessStatusEnum(Integer code, String name) {
        this.code = code;
        this.name = name;
    }

    static {
        // 将所有枚举装载到map中
        ENUM_MAP = new HashMap<Integer, String>();
        ENUM_LIST = new ArrayList<Integer>();
        for (JyBizTaskExceptionProcessStatusEnum enumItem : JyBizTaskExceptionProcessStatusEnum.values()) {
            ENUM_MAP.put(enumItem.getCode(), enumItem.getName());
            ENUM_LIST.add(enumItem.getCode());
        }
    }

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

    public static JyBizTaskExceptionProcessStatusEnum valueOf(int code){
        for (JyBizTaskExceptionProcessStatusEnum e:JyBizTaskExceptionProcessStatusEnum.values()){
            if (e.getCode() == code){
                return e;
            }
        }
        return null;
    }

    /**
     * 通过code获取name
     *
     * @param code 编码
     * @return string
     */
    public static String getEnumNameByCode(Integer code) {
        return ENUM_MAP.get(code);
    }

}
