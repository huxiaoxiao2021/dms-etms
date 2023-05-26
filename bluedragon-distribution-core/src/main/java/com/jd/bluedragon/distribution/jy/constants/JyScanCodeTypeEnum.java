package com.jd.bluedragon.distribution.jy.constants;

import java.util.ArrayList;
import java.util.List;

/**
 * 扫描单号类型枚举
 */
public enum JyScanCodeTypeEnum {
    //
    WAYBILL("101","运单号"),
    PACKAGE("102","包裹号"),
    BOARD("103","板号"),
    BOX("104","箱号"),
    ;
    private String code;
    private String msg;

    JyScanCodeTypeEnum(String code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    /**
     * 接货仓扫描单据类型
     * @return
     */
    public static List<JyScanCodeTypeEnum> jyWarehouseSendScanCodeType() {
        List<JyScanCodeTypeEnum> list = new ArrayList<>();
        list.add(JyScanCodeTypeEnum.PACKAGE);
        list.add(JyScanCodeTypeEnum.WAYBILL);
        list.add(JyScanCodeTypeEnum.BOX);
        return list;
    }

}
