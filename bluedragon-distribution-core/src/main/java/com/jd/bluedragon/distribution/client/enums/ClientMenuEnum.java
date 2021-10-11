package com.jd.bluedragon.distribution.client.enums;

import java.util.Objects;

/**
 * 客户端菜单编码枚举
 *
 * @author hujiping
 * @date 2021/10/9 6:02 下午
 */
public enum ClientMenuEnum {

    // 标签打印
    BOX_PRINT(101, "箱号打印"),
    BATCH_PRINT(102, "批次号打印"),
    SITE(103, "站点"),
    VEHICLE(104, "车牌"),
    DRIVER(105, "司机"),
    SEND_CAR(106, "发车"),
    TRANSPORT_SEND_PRINT(107, "运输发车打印"),
    CLIENT_PRINT(108, "平台打印"),
    SITE_CLIENT_PRINT(109, "站点平台打印"),
    PACK_REPRINT(110, "包裹补打"),
    BOARD_PRINT(111, "板标打印"),
    SPARE_PRINT(112, "备件条码打印"),
    RECEIVE_STORAGE_PRINT(113, "收纳暂存打印"),
    SCENE_PRE_SORT(114, "现场预分拣"),
    LINE_UP_PRINT(115, "排队号打印"),
    TRANSPORT_PRINT(116, "运力编码打印"),
    F_PRINT(117, "返单条码打印"),
    BATCH_PACK_PRINT(118, "批量打印包裹标签"),
    KY_PRINT(119, "跨越航空签打印"),

    // 清单打印
    SEND_HANDOVER_LIST_ALL(201, "发货交接清单（全部）"),
    SEND_HANDOVER_LIST_POSITIVE(202, "发货交接清单（正向）"),
    SEND_HANDOVER_LIST_REVERSE(203, "发货交接清单（逆向）"),
    EXPRESS_RECEIVE_HANDOVER_LIST(204, "快递签收交接单"),
    DBD_RECEIVE_HANDOVER_LIST(205, "夺宝岛收货交接清单"),
    XTC_RECEIVE_HANDOVER_LIST(206, "协同仓收货交接清单"),
    LDOP_INSPECTION_HANDOVER_LIST(207, "外单验货交接清单"),
    SOP_BACK_HANDOVER_LIST(208, "SOP退货交接清单"),
    RETURN_WMS_HANDOVER_LIST(209, "返仓包装交接清单"),
    RECEIVE_BOX_HANDOVER_LIST(210, "收箱交接单"),

    // 分拣中心
    WEIGHT(301, "称重量方"),
    PACK_WEIGHT(302, "包裹称重"),
    BATCH_SORT_WEIGHT(303, "批量分拣称重"),

    // 快运中心
    KY_WEIGHT_PRINT(401, "快运称重打印"),

    // 接货中心
    RECEIVE_WAYBILL_PRINT(501, "运单打印"),

    // 驻厂作业
    RESIDENT_TASK_NEW(601, "驻厂作业（新）"),
    RESIDENT_TASK_HANDOVER_LIST(602, "驻厂交接清单"),
    RESIDENT_BATCH_OBTAIN(603, "驻厂批量再取"),

    // 外单逆向作业
    SELF_REVERSE_REPLACE(701, "自营逆向换单"),
    REPLACE_PRINT(702, "换单打印"),
    LDOP_REVERSE_REPLACE(703, "外单逆向换单"),
    SITE_REVERSE_REPLACE(704, "站点逆向打印"),
    F_REVERSE_REPLACE(705, "返单换单打印"),
    F_REVERSE(706, "返单换单"),
    WAYBILL_PRINT(707, "运单打印"),
    SIGN_RETURN_MERGE(708, "签单返回合单"),

    // 系统设置
    PRINTER_SET(801, "打印机设置"),
    SERVE_SET(802, "服务设置"),
    ELECTRONIC_SET(803, "电子秤设置"),
    WEIGHT_SET(804, "称重量方设置"),
    VOICE_SET(805, "声音设置"),
    UPLOAD_TASK(806, "上传任务"),
    ERROR_UPLOAD(807, "错误上报");

    public static String getEnumName(int type) {
        for (ClientMenuEnum clientMenuEnum : ClientMenuEnum.values()) {
            if (Objects.equals(clientMenuEnum.getCode(), type)) {
                return clientMenuEnum.getName();
            }
        }
        return null;
    }


    private Integer code;
    private String name;

    ClientMenuEnum(Integer code, String name) {
        this.code = code;
        this.name = name;
    }

    public Integer getCode() {
        return this.code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
