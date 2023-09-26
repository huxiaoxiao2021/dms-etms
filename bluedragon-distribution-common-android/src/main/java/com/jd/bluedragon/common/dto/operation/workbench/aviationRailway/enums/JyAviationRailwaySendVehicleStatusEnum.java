package com.jd.bluedragon.common.dto.operation.workbench.aviationRailway.enums;

import java.util.HashMap;
import java.util.Map;

/**
 * 空铁发货列表状态
 * todo 该枚举状态含业务语义，与发货表中状态不对应，需要研发人员自行转换
 **/
public enum JyAviationRailwaySendVehicleStatusEnum {

    TO_SEND(0,"待发货列表",0,"待发货"),
    SENDING(1,"发货中列表",1,"发货中"),
    SEAL(2,"封车列表",null,null),
    TRUNK_LINE_SEAL_N(21,"干线未封",2,"待封车"),
    TRUNK_LINE_SEAL_Y(22,"干线已封",3,"已封车"),
    SHUTTLE_SEAL_Y(23,"摆渡已封",3,"已封车"),
    ;

    private static final Map<Integer, JyAviationRailwaySendVehicleStatusEnum> codeMap;

    static {
        codeMap = new HashMap<Integer, JyAviationRailwaySendVehicleStatusEnum>();
        for (JyAviationRailwaySendVehicleStatusEnum _enum : JyAviationRailwaySendVehicleStatusEnum.values()) {
            codeMap.put(_enum.getCode(), _enum);
        }
    }

    private Integer code;
    //JyBizTaskSendDetailStatusEnum
    private String name;
    private Integer sendTaskStatus;
    private String sendTaskName;

    JyAviationRailwaySendVehicleStatusEnum(Integer code, String name, Integer sendTaskStatus, String sendTaskName) {
        this.code = code;
        this.name = name;
        this.sendTaskStatus = sendTaskStatus;
        this.sendTaskName = sendTaskName;
    }

    public static String getNameByCode(Integer code) {
        JyAviationRailwaySendVehicleStatusEnum _enum = codeMap.get(code);
        if (_enum != null) {
            return _enum.getName();
        }
        return "未知";
    }

    public static Integer getSendTaskStatusByCode(Integer code) {
        JyAviationRailwaySendVehicleStatusEnum _enum = codeMap.get(code);
        if (_enum != null) {
            return _enum.getSendTaskStatus();
        }
        throw new RuntimeException("没有对应发货任务状态");
    }

    /**
     * 通过编码获取枚举
     *
     * @param code 编码
     * @return 规则类型
     */
    public static JyAviationRailwaySendVehicleStatusEnum getEnumByCode(Integer code) {
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

    public Integer getSendTaskStatus() {
        return sendTaskStatus;
    }

    public void setSendTaskStatus(Integer sendTaskStatus) {
        this.sendTaskStatus = sendTaskStatus;
    }

    public String getSendTaskName() {
        return sendTaskName;
    }

    public void setSendTaskName(String sendTaskName) {
        this.sendTaskName = sendTaskName;
    }
}
