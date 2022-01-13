package com.jd.bluedragon.distribution.exceptionReport.billException.enums;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 异常面单举报，举报类型分类枚举
 *
 * @author fanggang7
 * @time 2022-01-04 11:05:15 周二
 */
public enum ExpressReportTypeEnum {

    /**
     * 操作问题
     */
    NOT_BIG_SIDE(1, "面单未贴箱体最大面", ExpressReportTypeCategoryEnum.OP_PROBLEM.getCode()),

    /**
     * 设备问题
     */
    DUPLICATE_BILL(2, "双面单", ExpressReportTypeCategoryEnum.OP_PROBLEM.getCode()),

    /**
     * 模板问题
     */
    BILL_QF(3, "面单骑缝", ExpressReportTypeCategoryEnum.OP_PROBLEM.getCode()),

    /**
     * 系统问题
     */
    BILL_FOLDS(4, "面单褶皱", ExpressReportTypeCategoryEnum.OP_PROBLEM.getCode()),
    BILL_INJUSTICE(5, "面单粘贴不抚平", ExpressReportTypeCategoryEnum.OP_PROBLEM.getCode()),
    WIRE_DRAW_BROKE_FRAME(6, "打印拉丝断桢", ExpressReportTypeCategoryEnum.EQUIPMENT_PROBLEM.getCode()),
    TOO_SMALL_FONT(7, "面单字体小不容易识别", ExpressReportTypeCategoryEnum.TEMPLATE_PROBLEM.getCode()),
    LEAK_DESTINATION_SITE(8, "缺少分拣目的地站点信息", ExpressReportTypeCategoryEnum.SYSTEM_PROBLEM.getCode()),
    LEA_SORTING_INFO(9, "分拣信息缺失", ExpressReportTypeCategoryEnum.SYSTEM_PROBLEM.getCode()),
    SPECIAL_FACE_SHEET_NOT_PASTED(10, "未粘贴专用面单", ExpressReportTypeCategoryEnum.OP_PROBLEM.getCode()),
    BARCODE_ADDRESSES_ARE_SEPARATED(11, "条码地址被分隔", ExpressReportTypeCategoryEnum.OP_PROBLEM.getCode()),
    MULTIPLE_PACKAGE(12, "一单多件", ExpressReportTypeCategoryEnum.OP_PROBLEM.getCode()),
    COVER_THE_SPOKESPERSON_AVATAR(13, "遮住代言人头像", ExpressReportTypeCategoryEnum.OP_PROBLEM.getCode()),
    LEAK_CROSS_CODE(14, "滑道信息缺失", ExpressReportTypeCategoryEnum.SYSTEM_PROBLEM.getCode()),
    TOP_BARCODE_NOT_PACKAGE(15, "最上条码非包裹号(无-1-1-)", ExpressReportTypeCategoryEnum.TEMPLATE_PROBLEM.getCode()),
    INFORMATION_FIELD_STRING(16, "信息字段串位(分拣中心/滑道代码/站点)", ExpressReportTypeCategoryEnum.TEMPLATE_PROBLEM.getCode()),
    CROSS_CODE_WRONG(17, "滑道代码信息错误", ExpressReportTypeCategoryEnum.SYSTEM_PROBLEM.getCode()),
    NON_INTEGRATED(18, "非一体化面单", ExpressReportTypeCategoryEnum.TEMPLATE_PROBLEM.getCode()),
    CROSS_CODE_FONT_SMALL(19, "滑道字体太小", ExpressReportTypeCategoryEnum.TEMPLATE_PROBLEM.getCode()),
    OTHER(20, "其它", ExpressReportTypeCategoryEnum.OP_PROBLEM.getCode()),
    ;

    public static Map<Integer, String> ENUM_MAP;
    public static Map<Integer, Integer> CODE_2_REPORT_CATEGORY_MAP;

    public static List<Integer> ENUM_LIST;

    private Integer code;

    private String name;

    private Integer typeCategory;

    static {
        //将所有枚举装载到map中
        ENUM_MAP = new HashMap<Integer, String>();
        ENUM_LIST = new ArrayList<Integer>();
        CODE_2_REPORT_CATEGORY_MAP = new HashMap<Integer, Integer>();
        for (ExpressReportTypeEnum enumItem : ExpressReportTypeEnum.values()) {
            ENUM_MAP.put(enumItem.getCode(), enumItem.getName());
            ENUM_LIST.add(enumItem.getCode());
            CODE_2_REPORT_CATEGORY_MAP.put(enumItem.getCode(), enumItem.getTypeCategory());
        }
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

    ExpressReportTypeEnum(Integer code, String name, Integer typeCategory) {
        this.code = code;
        this.name = name;
        this.typeCategory = typeCategory;
    }

    public Integer getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public Integer getTypeCategory() {
        return typeCategory;
    }
}
