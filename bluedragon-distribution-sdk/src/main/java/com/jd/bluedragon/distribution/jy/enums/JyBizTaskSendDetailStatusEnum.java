package com.jd.bluedragon.distribution.jy.enums;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @ClassName JyBizTaskSendDetailStatusEnum
 * @Description
 * @Author wyh
 * @Date 2022/6/3 15:28
 **/
public enum JyBizTaskSendDetailStatusEnum {

    TO_SEND(0, "待发货"),
    SENDING(1, "发货中"),
    TO_SEAL(2, "待封车"),
    SEALED(3, "已封车"),
    CANCEL(4, "已作废"),
    ;

    public static final List<Integer> EFFECTIVE_STATUS;
    private static final Map<Integer, JyBizTaskSendDetailStatusEnum> codeMap;

    static {
        EFFECTIVE_STATUS = new ArrayList<Integer>();
        EFFECTIVE_STATUS.add(TO_SEND.getCode());
        EFFECTIVE_STATUS.add(SENDING.getCode());
        EFFECTIVE_STATUS.add(TO_SEAL.getCode());
        EFFECTIVE_STATUS.add(SEALED.getCode());
        codeMap = new HashMap<Integer, JyBizTaskSendDetailStatusEnum>();
        for (JyBizTaskSendDetailStatusEnum _enum : JyBizTaskSendDetailStatusEnum.values()) {
            codeMap.put(_enum.getCode(), _enum);
        }
    }

    private Integer code;
    private String name;
    JyBizTaskSendDetailStatusEnum(Integer code, String name) {
        this.code = code;
        this.name = name;
    }

    public static String getNameByCode(Integer code) {
        JyBizTaskSendDetailStatusEnum _enum = codeMap.get(code);
        if (_enum != null) {
            return _enum.getName();
        }
        return "未知";
    }

    /**
     * 通过编码获取枚举
     *
     * @param code 编码
     * @return 规则类型
     */
    public static JyBizTaskSendDetailStatusEnum getEnumByCode(Integer code) {
        return codeMap.get(code);
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
}
