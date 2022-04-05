package com.jd.bluedragon.distribution.jy.enums;

import java.util.*;

/**
 * 天官赐福 ◎ 百无禁忌
 *
 * @Auther: 刘铎（liuduo8）
 * @Date: 2022/4/1
 * @Description: 到车卸车任务状态
 */
public enum JyBizTaskUnloadStatusEnum {

    INIT(0,"等待初始",0),

    ON_WAY(1,"在途",1),

    WAIT_UN_SEAL(2,"待解",2),

    WAIT_UN_LOAD(3,"待卸",3),

    UN_LOADING(4,"卸车",4),

    UN_LOAD_DONE(5,"已完成",5),

    CANCEL(6,"取消",6);

    private Integer code;
    private String name;
    private Integer order;
    private static Map<Integer, JyBizTaskUnloadStatusEnum> codeMap;
    public static Map<Integer, String> enumMap;

    public static final List<JyBizTaskUnloadStatusEnum> UNLOAD_STATUS_OPTIONS = new ArrayList<JyBizTaskUnloadStatusEnum>();

    static {
        //将所有枚举装载到map中
        codeMap = new HashMap<Integer, JyBizTaskUnloadStatusEnum>();
        enumMap = new HashMap<Integer, String>();
        for (JyBizTaskUnloadStatusEnum _enum : JyBizTaskUnloadStatusEnum.values()) {
            codeMap.put(_enum.getCode(), _enum);
            enumMap.put(_enum.getCode(), _enum.getName());
        }

        UNLOAD_STATUS_OPTIONS.add(JyBizTaskUnloadStatusEnum.WAIT_UN_LOAD);
        UNLOAD_STATUS_OPTIONS.add(JyBizTaskUnloadStatusEnum.UN_LOADING);
        UNLOAD_STATUS_OPTIONS.add(JyBizTaskUnloadStatusEnum.UN_LOAD_DONE);
    }

    /**
     * 通过编码获取枚举
     *
     * @param code 编码
     * @return 规则类型
     */
    public static JyBizTaskUnloadStatusEnum getEnumByCode(Integer code) {
        return codeMap.get(code);
    }

    /**
     * 通过编码获取规则类型名称
     *
     * @param code 编码
     * @return 规则类型
     */
    public static String getNameByCode(Integer code) {
        JyBizTaskUnloadStatusEnum _enum = codeMap.get(code);
        if (_enum != null) {
            return _enum.getName();
        }
        return "未知";
    }

    JyBizTaskUnloadStatusEnum(Integer code, String name, Integer order) {
        this.code = code;
        this.name = name;
        this.order = order;
    }

    public Integer getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public Integer getOrder() { return order; }
}
