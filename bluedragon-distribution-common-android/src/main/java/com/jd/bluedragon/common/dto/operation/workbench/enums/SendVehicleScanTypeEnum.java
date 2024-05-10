package com.jd.bluedragon.common.dto.operation.workbench.enums;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @ClassName SendVehicleScanTypeEnum
 * @Description 发货单号扫描类型
 * @Author wyh
 * @Date 2022/6/3 10:37
 **/
public enum SendVehicleScanTypeEnum {
    /**
     * 按件扫描
     */
    SCAN_ONE(1, "按件扫描", "支持扫描包裹号或箱号"),
    /**
     * 按单扫描
     */
    SCAN_WAYBILL(2, "按单扫描", "扫描包裹号转成运单号，或扫描运单号"),
    // SCAN_BOX(3, "箱号", "扫描包裹号转成箱号，或扫描箱号"),
    /**
     * 扫描板号
     */
    SCAN_BOARD(4, "板号", "扫描包裹号转成板号，或扫描板号"),
    /**
     * 按笼扫描
     */
    SCAN_TABLE_TROLLEY(5, "按笼扫描", "扫描笼车上任意一个包裹号，整笼所有包裹发货")
    ;
    /**
     * 编码
     */
    private Integer code;
    /**
     * 名称
     */
    private String name;
    /**
     * 描述
     */
    private String desc;

    /**
     * 构造函数用于初始化SendVehicleScanTypeEnum对象
     * @param code 代码值
     * @param name 名称
     * @param desc 描述
     */
    SendVehicleScanTypeEnum(Integer code, String name, String desc) {
        this.code = code;
        this.name = name;
        this.desc = desc;
    }

    /**
     * 根据代码获取名称
     * @param code 代码值
     * @return 名称
     */
    public static String getNameByCode(Integer code) {
        for (SendVehicleScanTypeEnum scanTypeEnum : SendVehicleScanTypeEnum.values()) {
            if (scanTypeEnum.code.equals(code)) {
                return scanTypeEnum.name;
            }
        }
        return "";
    }

    /**
     * 获取所有发送车辆扫描类型枚举值的列表。
     * @return 返回包含所有发送车辆扫描类型枚举的列表。
     */
    public static List<SendVehicleScanTypeEnum> getAllEnum() {
        return new ArrayList<SendVehicleScanTypeEnum>(Arrays.asList(SendVehicleScanTypeEnum.values()));
    }

    /**
     * 获取航空铁路发货扫描类型枚举列表的方法。
     * 本方法返回一个包含特定发送车辆扫描类型的列表。
     * @return List<SendVehicleScanTypeEnum> 返回一个包含SCAN_ONE、SCAN_WAYBILL和SCAN_BOARD三种类型的列表    。
     */
    public static List<SendVehicleScanTypeEnum> getAviationRailwaySendEnum() {
        return new ArrayList<SendVehicleScanTypeEnum>(Arrays.asList(SendVehicleScanTypeEnum.SCAN_ONE,
                SendVehicleScanTypeEnum.SCAN_WAYBILL, SendVehicleScanTypeEnum.SCAN_BOARD));
    }

    /**
     * 获取组板发货扫描类型枚举列表的方法。
     * 本方法返回一个包含特定发送车辆扫描类型的列表。
     * @return List<SendVehicleScanTypeEnum> 返回一个包含SCAN_ONE、SCAN_WAYBILL和SCAN_BOARD三种类型的列表    。
     */
    public static List<SendVehicleScanTypeEnum> getComboardSendEnum() {
        return new ArrayList<SendVehicleScanTypeEnum>(Arrays.asList(SendVehicleScanTypeEnum.SCAN_ONE,
                SendVehicleScanTypeEnum.SCAN_WAYBILL));
    }

    /**
     * 获取分拣发货岗位发货扫描类型枚举列表的方法。
     * 本方法返回一个包含特定发送车辆扫描类型的列表。
     * @return List<SendVehicleScanTypeEnum> 返回一个包含SCAN_ONE、SCAN_WAYBILL和SCAN_BOARD三种类型的列表    。
     */
    public static List<SendVehicleScanTypeEnum> getJySendVehicleEnum() {
        return new ArrayList<SendVehicleScanTypeEnum>(Arrays.asList(SendVehicleScanTypeEnum.SCAN_ONE,
                SendVehicleScanTypeEnum.SCAN_WAYBILL));
    }

    /**
     * 获取接货仓发货扫描类型枚举列表的方法。
     * 本方法返回一个包含特定发送车辆扫描类型的列表。
     * @return List<SendVehicleScanTypeEnum> 返回一个包含SCAN_ONE、SCAN_WAYBILL和SCAN_BOARD三种类型的列表    。
     */
    public static List<SendVehicleScanTypeEnum> getWarehouseSendEnum() {
        return new ArrayList<SendVehicleScanTypeEnum>(Arrays.asList(SendVehicleScanTypeEnum.SCAN_ONE,
                SendVehicleScanTypeEnum.SCAN_WAYBILL, SendVehicleScanTypeEnum.SCAN_BOARD));
    }


    /**
     * 获取包含特定枚举值的列表，这些枚举值用于标识车辆扫描的类型    。
     * 该方法返回一个列表，其中包含“SCAN_ONE”、“SCAN_WAYBILL”和“SCAN_BOARD”三种枚举类型    。
     * 这些枚举通常用于指示在物流或运输过程中车辆扫描的不同阶段    。
     *
     * @return List<SendVehicleScanTypeEnum> 返回一个包含“SCAN_ONE”、“SCAN_WAYBILL”和“SCAN_BOARD”枚举值的列表    ，
     *         用于表示车辆扫描的不同类型    。
     */
    public static List<SendVehicleScanTypeEnum> getOneAndWaybillAndBoardEnum() {
        return new ArrayList<SendVehicleScanTypeEnum>(Arrays.asList(SendVehicleScanTypeEnum.SCAN_ONE,
                SendVehicleScanTypeEnum.SCAN_WAYBILL, SendVehicleScanTypeEnum.SCAN_BOARD));
    }

    public Integer getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public String getDesc() {
        return desc;
    }
}
