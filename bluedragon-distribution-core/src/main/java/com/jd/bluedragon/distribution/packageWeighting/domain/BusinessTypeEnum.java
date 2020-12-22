package com.jd.bluedragon.distribution.packageWeighting.domain;

import java.util.ArrayList;
import java.util.List;

/**
 * 运单包裹称重操作类型
 * Created by jinjingcheng on 2018/4/22.
 */
public enum BusinessTypeEnum {
    //1-分拣  2-接货仓配送员接货  3-接货中心驻场操作  4-仓库操作  5-车队操作 6总重量（快运称重）7 计费操作 8：站点无任务揽收运单维度操作；9：京牛抽检；10：配送交接包裹维度操作；
    // *  11：揽收交接运单维度操作；12：配送交接运单维度操作；13：揽收交接包裹维度操作；
    DMS(1, "分拣"),
    PICKER(2, "接货仓配送员接货"),
    PICK_RESIDENT(3, "接货中心驻场操作"),
    PICK(4, "仓库操作"),
    CAR_TEAM(5, "车队操作"),
    TOTAL(6, "总重量"),
    BILLING(7,"计费操作"),
    SITE_OPERATING(8,"站点无任务揽收运单维度操作"),
    JN_INSPECT(9,"京牛抽检"),
    DELIVERY_PACK_OPERATE(10,"配送交接包裹维度操作"),
    COLLECT_WAYBILL_OPERATE(11,"揽收交接运单维度操作"),
    DELIVERY_WAYBILL_OPERATE(12,"配送交接运单维度操作"),
    COLLECT_PACK_OPERATE(13,"揽收交接包裹维度操作");

    private int code;
    private String name;

    //运单包裹称重类型最全的类型
    public static List<Integer> getAllCode(){
        BusinessTypeEnum[] list = BusinessTypeEnum.values();
        List<Integer> codes = new ArrayList<Integer>(list.length);
        for(BusinessTypeEnum businessType : list){
            codes.add(businessType.code);
        }
        return codes;
    }

    BusinessTypeEnum(int code, String name) {
        this.code = code;
        this.name = name;
    }

    public int getCode() {
        return code;
    }

    public String getName() {
        return name;
    }
}
