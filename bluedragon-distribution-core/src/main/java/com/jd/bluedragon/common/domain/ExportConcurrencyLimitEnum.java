package com.jd.bluedragon.common.domain;

/**
 * @Author: liming522
 * @Description:
 * @Date: create in 2021/4/14 17:44
 */
public enum ExportConcurrencyLimitEnum {
    ABNORMAL_UNKNOWN_WAYBILL_REPORT("ABNORMAL_UNKNOWN_WAYBILL_REPORT","三无外地托寄物核实"),
    DMS_BAR_CODE_REPORT("DMS_BAR_CODE_REPORT","69码查询商品名称报表"),
    POP_RECEIVE_ABNORMAL_REPORT("POP_RECEIVE_ABNORMAL_REPORT","平台差异处理报表"),
    POP_ABNORMAL_REPORT("POP_RECEIVE_ABNORMAL_REPORT","POP差异订单数据"),
    BUSINESS_RETURN_ADDRESS_REPORT("BUSINESS_RETURN_ADDRESS_REPORT","换单地址维护统计报表"),
    CROSS_BOX_REPORT("CROSS_BOX_REPORT","跨箱号中转报表"),
    GOODS_PRINT_REPORT("GOODS_PRINT_REPORT","托寄物品名打印报表"),
    REVERSE_PART_DETAIL_REPORT("REVERSE_PART_DETAIL_REPORT","半退明细查询报表"),
    MERCHANT_WEIGHT_AND_VOLUME_WHITE_REPORT("MERCHANT_WEIGHT_AND_VOLUME_WHITE_REPORT","商家称重量方白名单报表"),
    INVENTORY_TASK_REPORT("INVENTORY_TASK_REPORT","转运清场任务查询报表"),
    INVENTORY_EXCEPTION_REPORT("INVENTORY_EXCEPTION_REPORT","转运清场异常查询报表"),
    RMA_HAND_OVER_REPORT("RMA_HAND_OVER_REPORT","RMA交接清单打印"),
    WAYBILL_CONSUMABLE_RECORD_REPORT("WAYBILL_CONSUMABLE_RECORD_REPORT","B网包装耗材报表"),
    STORAGE_PACKAGE_M_REPORT("STORAGE_PACKAGE_M_REPORT","暂存管理报表"),
    COLLECT_GOODS_DETAIL_REPORT("COLLECT_GOODS_DETAIL_REPORT","集货报表"),
    DMS_SCHEDULE_INFO_REPORT("DMS_SCHEDULE_INFO_REPORT","拣货功能"),
    REVIEW_WEIGHT_SPOT_CHECK_REPORT("ReviewWeightSpotCheck","重复抽检任务导入"),
    WEIGHT_AND_VOLUME_CHECK_REPORT("WEIGHT_AND_VOLUME_CHECK_REPORT","重量体积抽检统计报表"),
    SIGN_RETURN_REPORT("SIGN_RETURN_REPORT","签单返回合单打印交接单"),
    ENTERPRISE_DISTRIBUTION_QUALITY_INSPECTION("ENTERPRISE_DISTRIBUTION_QUALITY_INSPECTION", "企配仓质检报表");

    private String code;

    private String name;

    ExportConcurrencyLimitEnum (String code, String name) {
        this.code = code;
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}

