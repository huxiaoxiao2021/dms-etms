package com.jd.bluedragon.distribution.jy.enums;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @ClassName JyBizTaskSendStatusEnum
 * @Description 发货任务状态枚举
 * @Author wyh
 * @Date 2022/5/26 15:02
 **/
public enum JyBizTaskSendStatusEnum {

    TO_SEND(0, "待发货"),
    SENDING(1, "发货中"),
    TO_SEAL(2, "待封车"),
    SEALED(3, "已封车"),
    CANCEL(4, "已作废")
    ;

    private static final Map<Integer, JyBizTaskSendStatusEnum> codeMap;
    public static final List<Integer> UN_SEALED_STATUS = new ArrayList<Integer>();

    static {
        codeMap = new HashMap<Integer, JyBizTaskSendStatusEnum>();
        for (JyBizTaskSendStatusEnum _enum : JyBizTaskSendStatusEnum.values()) {
            codeMap.put(_enum.getCode(), _enum);
        }
        UN_SEALED_STATUS.add(TO_SEND.getCode());
        UN_SEALED_STATUS.add(SENDING.getCode());
        UN_SEALED_STATUS.add(TO_SEAL.getCode());
    }

    private Integer code;
    private String name;
    JyBizTaskSendStatusEnum(Integer code, String name) {
        this.code = code;
        this.name = name;
    }

    public static String getNameByCode(Integer code) {
        JyBizTaskSendStatusEnum _enum = codeMap.get(code);
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
    public static JyBizTaskSendStatusEnum getEnumByCode(Integer code) {
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
