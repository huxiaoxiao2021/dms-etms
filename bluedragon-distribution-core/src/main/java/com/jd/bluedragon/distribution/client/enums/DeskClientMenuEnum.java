package com.jd.bluedragon.distribution.client.enums;

import com.google.common.collect.Lists;

import java.util.List;
import java.util.Objects;

/**
 * 打印客户端菜单枚举
 *
 * @author hujiping
 * @date 2022/1/7 6:37 PM
 */
public enum DeskClientMenuEnum {

    // 标签打印
    BOX_PRINT("0601001", "箱号打印"),
    BATCH_PRINT("0601002", "批次号打印"),
    SITE("0601003", "站点"),
    VEHICLE("0601004", "车牌"),
    DRIVER("0601005", "司机"),
    SEND_CAR("0601006", "发车"),
    TRANSPORT_SEND_PRINT("0601007", "运输发车打印"),
    CLIENT_PRINT("0601008", "平台打印"),
    SITE_CLIENT_PRINT("0601009", "站点平台打印"),
    PACK_REPRINT("0601010", "包裹补打"),
    SPARE_PRINT("0601011", "备件条码打印"),
    SCENE_PRE_SORT("0601012", "现场预分拣"),
    LINE_UP_PRINT("0601013", "排队号打印"),
    TRANSPORT_PRINT("0601014", "运力编码打印"),
    F_PRINT("0601015", "返单条码打印"),
    BATCH_PACK_PRINT("0601020", "批量打印包裹标签"),
    KY_PRINT("0601023", "跨越航空签打印"),
    BOARD_PRINT("0601024", "板标打印"),
    RECEIVE_STORAGE_PRINT("0601025", "收纳暂存打印"),
    RECYCLE_BASKET_PRINT("0601026", "周转筐标签打印"),
    FARMAR_LABEL_PRINT("0601027", "砝码标签打印"),

    // 清单打印
    SEND_HANDOVER_LIST_ALL("0602001", "发货交接清单（全部）"),
    SEND_HANDOVER_LIST_POSITIVE("0602002", "发货交接清单（正向）"),
    SEND_HANDOVER_LIST_REVERSE("0602003", "发货交接清单（逆向）"),
    EXPRESS_RECEIVE_HANDOVER_LIST("0602006", "快递签收交接单"),
    DBD_RECEIVE_HANDOVER_LIST("0602007", "夺宝岛收货交接清单"),
    XTC_RECEIVE_HANDOVER_LIST("0602008", "协同仓收货交接清单"),
    LDOP_INSPECTION_HANDOVER_LIST("0602009", "外单验货交接清单"),
    SOP_BACK_HANDOVER_LIST("0602010", "SOP退货交接清单"),
    RETURN_WMS_HANDOVER_LIST("0602011", "返仓包装交接清单"),
    RECEIVE_BOX_HANDOVER_LIST("0602012", "收箱交接单"),

    // 分拣中心
    PACK_WEIGHT("0603001", "包裹称重"),
    BATCH_SORT_WEIGHT("0603002", "批量分拣称重"),
    WEIGHT("0603007", "称重量方"),

    // 快运中心
    KY_WEIGHT_PRINT("0604001", "快运称重打印"),

    // 接货中心
    RECEIVE_WAYBILL_PRINT("0605001", "运单打印"),

    // 驻厂作业
    RESIDENT_TASK_NEW("0606001", "驻厂作业（新）"),
    RESIDENT_TASK_HANDOVER_LIST("0606002", "驻厂交接清单"),
    RESIDENT_BATCH_OBTAIN("0606003", "驻厂批量再取"),

    // 外单逆向作业
    SELF_REVERSE_REPLACE("0607001", "自营逆向换单"),
    REPLACE_PRINT("0607002", "换单打印"),
    LDOP_REVERSE_REPLACE("0607003", "外单逆向换单"),
    SITE_REVERSE_REPLACE("0607005", "站点逆向打印"),
    F_REVERSE_REPLACE("0607006", "返单换单打印"),
    F_REVERSE("0607007", "返单换单"),
    WAYBILL_PRINT("0607008", "运单打印"),
    SIGN_RETURN_MERGE("0607009", "签单返回合单"),

    // 系统设置
    PRINTER_SET("0608001", "打印机设置"),
    SERVE_SET("0608002", "服务设置"),
    ELECTRONIC_SET("0608003", "电子秤设置"),
    WEIGHT_SET("0608009", "称重量方设置"),
    VOICE_SET("0608004", "声音设置"),
    UPLOAD_TASK("0608005", "上传任务"),
    ERROR_UPLOAD("0608006", "错误上报"),
    NATIVE_IP_LIST("0608007", "本地ip列表"),
    SQL_EXECUTE("0608006", "本地数据执行器");

    public static String getEnumName(String code) {
        for (DeskClientMenuEnum clientMenuEnum : DeskClientMenuEnum.values()) {
            if (Objects.equals(clientMenuEnum.getCode(), code)) {
                return clientMenuEnum.getName();
            }
        }
        return null;
    }

    public static final List<String> allMenuCodes = Lists.newArrayList();
    static {
        for (DeskClientMenuEnum clientMenuEnum : DeskClientMenuEnum.values()) {
            allMenuCodes.add(clientMenuEnum.getCode());
        }
    }

    private String code;
    private String name;

    DeskClientMenuEnum(String code, String name) {
        this.code = code;
        this.name = name;
    }

    public String getCode() {
        return this.code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
