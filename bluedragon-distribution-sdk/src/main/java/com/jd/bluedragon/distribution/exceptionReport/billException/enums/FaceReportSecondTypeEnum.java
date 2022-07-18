package com.jd.bluedragon.distribution.exceptionReport.billException.enums;

import java.util.HashMap;
import java.util.Map;

/**
 * 面单异常举报二级举报类型
 *
 * @author hujiping
 * @date 2022/6/9 4:46 PM
 */
public enum FaceReportSecondTypeEnum {

    // 操作问题
    NOT_BIG_SIDE(101, "面单未贴箱体最大面", FaceReportFirstTypeEnum.OP_PROBLEM.getCode()),
    BILL_QF(102, "面单骑缝", FaceReportFirstTypeEnum.OP_PROBLEM.getCode()),
    DUPLICATE_BILL(103, "双面单", FaceReportFirstTypeEnum.OP_PROBLEM.getCode()),
    BILL_FOLDS(104, "面单褶皱不平", FaceReportFirstTypeEnum.OP_PROBLEM.getCode()),
    MORE_PACK_SHARE_BILL(105, "多包裹共用一个面单", FaceReportFirstTypeEnum.OP_PROBLEM.getCode()),
    NO_PASTE_WINTER_BILL(106, "未粘贴冬季专用面单", FaceReportFirstTypeEnum.OP_PROBLEM.getCode()),
    NOT_SMALL_SIDE(107, "快运面单未贴箱体最小面", FaceReportFirstTypeEnum.OP_PROBLEM.getCode()),
    TRANSFORM_NO_EXCHANGE(108, "转网未换单", FaceReportFirstTypeEnum.OP_PROBLEM.getCode()),
    // 设备问题
    WIRE_DRAW_BROKE_FRAME(201, "打印拉丝断桢", FaceReportFirstTypeEnum.EQUIPMENT_PROBLEM.getCode()),
    BILL_NO_INK(202, "面单部分无墨/墨印重", FaceReportFirstTypeEnum.EQUIPMENT_PROBLEM.getCode()),
    // 模板问题
    SLIDE_TOO_SMALL_FONT(301, "滑道号字体过小不易识别", FaceReportFirstTypeEnum.TEMPLATE_PROBLEM.getCode()),
    BILL_STR_CHANGE(302, "面单信息字符串位", FaceReportFirstTypeEnum.TEMPLATE_PROBLEM.getCode()),
    TOP_NO_PACK(303, "最上条码无包裹号(-1-1-)", FaceReportFirstTypeEnum.TEMPLATE_PROBLEM.getCode()),
    NO_JD_BILL(304, "非京东标准面单模板", FaceReportFirstTypeEnum.TEMPLATE_PROBLEM.getCode()),
    LEAK_CROSS_CODE(305, "滑道信息缺失", FaceReportFirstTypeEnum.TEMPLATE_PROBLEM.getCode()),
    // 系统问题
    LEAK_INFO_ERROR(401, "滑道信息错误", FaceReportFirstTypeEnum.SYSTEM_PROBLEM.getCode()),
    CREATE_SITE_INFO_ERROR(402, "始发分拣信息错误", FaceReportFirstTypeEnum.SYSTEM_PROBLEM.getCode()),
    ;

    public static final Map<Integer, String> ENUM_MAP;

    static {
        //将所有枚举装载到map中
        ENUM_MAP = new HashMap<Integer, String>();
        for (FaceReportSecondTypeEnum enumItem : FaceReportSecondTypeEnum.values()) {
            ENUM_MAP.put(enumItem.getCode(), enumItem.getName());
        }
    }

    public static String getEnumNameByCode(Integer code) {
        return ENUM_MAP.get(code);
    }

    FaceReportSecondTypeEnum(Integer code, String name, Integer parentCode){
        this.code = code;
        this.name = name;
        this.parentCode = parentCode;
    }

    private Integer code;
    private String name;
    private Integer parentCode;

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

    public Integer getParentCode() {
        return parentCode;
    }

    public void setParentCode(Integer parentCode) {
        this.parentCode = parentCode;
    }
}
