package com.jd.bluedragon.distribution.print.domain;

import java.util.HashSet;
import java.util.Set;

/**
 * 打印平台操作类型
 * Created by shipeilin on 2018/2/5.
 */
public enum WaybillPrintOperateTypeEnum {
    PLATE_PRINT(100101, "平台打印"),
    SITE_PLATE_PRINT(100102, "站点平台打印"),
    PACKAGE_AGAIN_PRINT(100103, "包裹补打"),
    SWITCH_BILL_PRINT(100104, "换单打印"),
    PACKAGE_WEIGH_PRINT(100105, "包裹称重"),
    FIELD_PRINT(100106, "驻场打印"),
    BATCH_SORT_WEIGH_PRINT(100107, "批量分拣称重"),
    FAST_TRANSPORT_PRINT(100108, "快运称重打印"),
    BATCH_PACKAGE_AGAIN_PRINT(100109, "批量包裹补打"),

    SITE_MASTER_PACKAGE_REPRINT(100501, "站长工作台：包裹补打"),
    SITE_MASTER_REVERSE_CHANGE_PRINT(100502, "站长工作台：换单打印"),
    SITE_MASTER_RESCHEDULE_PRINT(100503, "站长工作台：现场预分拣"),

    SMS_REPRINT(100301, "终端：补打"),

    SMS_REVERSE_CHANGE_PRINT(100304, "终端：换单打印"),
    SMS_REVERSE_CHANGE_REPRINT(100305, "终端：换单补打"),
    /**
     * 100306-冷链合伙人打印
     */
    COLD_CHAIN_PRINT(100306, "冷链合伙人打印"),

    SITE_3PL_MASTER_RESCHEDULE_REPRINT(100307, "终端3PL：现场预分拣"),
    SITE_3PL_PACKAGE_AGAIN_REPRINT(100308, "终端3PL：包裹补打"),

    SMS_PDA_REPRINT(100310, "终端：一体机"),
    PDF_DJ_JDB_PACKAGE_REPRINT(100315, "大件：大件京东帮"),
    /**
     * 100316-终端外部渠道B2C打印，调用系统o2o-provider
     */
    SMS_O2O_PRIVIDER_PACKAGE_REPRINT(100316, "终端外部渠道B2C打印");

    private Integer type;//操作类型
    private String name;//操作名称

    WaybillPrintOperateTypeEnum(Integer type, String name) {
        this.type = type;
        this.name = name;
    }
    /**
     * 换单打印类型集合
     */
    private static Set<Integer> SET_EXCHAGE_PRINT_TYPES = new HashSet<Integer>();
    /**
     * 补打类型集合
     */
    private static Set<Integer> SET_REPRINT_TYPES = new HashSet<Integer>();
    static{
        /**
         * 初始化换单打印类型
         */
    	SET_EXCHAGE_PRINT_TYPES.add(SWITCH_BILL_PRINT.getType());
    	SET_EXCHAGE_PRINT_TYPES.add(SITE_MASTER_REVERSE_CHANGE_PRINT.getType());
    	SET_EXCHAGE_PRINT_TYPES.add(SMS_REVERSE_CHANGE_PRINT.getType());
        /**
         * 初始化补打类型
         */
    	SET_REPRINT_TYPES.add(PACKAGE_AGAIN_PRINT.getType());
    	SET_REPRINT_TYPES.add(SITE_MASTER_PACKAGE_REPRINT.getType());
    	SET_REPRINT_TYPES.add(BATCH_PACKAGE_AGAIN_PRINT.getType());
    	SET_REPRINT_TYPES.add(FAST_TRANSPORT_PRINT.getType());
    	SET_REPRINT_TYPES.add(SITE_3PL_PACKAGE_AGAIN_REPRINT.getType());
    	SET_REPRINT_TYPES.add(SMS_REVERSE_CHANGE_REPRINT.getType());
    }
    /**
     * 判断是否换单打印
     * @return
     */
    public static boolean isExchangePrint(Integer type){
    	return type != null && SET_EXCHAGE_PRINT_TYPES.contains(type);
    }
    /**
     * 判断是否补打
     * @return
     */
    public static boolean isRePrint(Integer type){
    	return type != null && SET_REPRINT_TYPES.contains(type);
    }
    public static Integer PLATE_PRINT_TYPE = 100101;      //平台打印操作类型
    public static Integer SITE_PLATE_PRINT_TYPE = 100102;//站点平台打印操作类型
    public static Integer PACKAGE_AGAIN_PRINT_TYPE = 100103;//包裹补打操作类型
    public static Integer SWITCH_BILL_PRINT_TYPE = 100104;//换单打印操作类型
    public static Integer PACKAGE_WEIGH_PRINT_TYPE = 100105;//包裹称重操作类型
    public static Integer FIELD_PRINT_TYPE = 100106;       //驻场打印操作类型
    public static Integer BATCH_SORT_WEIGH_PRINT_TYPE = 100107;//批量分拣称重操作类型
    public static Integer FAST_TRANSPORT_PRINT_TYPE= 100108;//快运称重打印操作类型
    public static Integer BATCH_PACKAGE_AGAIN_PRINT_TYPE = 100109;//批量包裹补打操作类型
    public static Integer SITE_3PL_MASTER_RESCHEDULE_REPRINT_TYPE = 100307;//终端3PL现场预分拣
    public static Integer SITE_3PL_PACKAGE_AGAIN_REPRINT_TYPE = 100308;//终端3PL包裹补打

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }
}
