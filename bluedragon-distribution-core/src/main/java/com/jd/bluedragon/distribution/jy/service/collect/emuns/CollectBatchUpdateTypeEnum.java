package com.jd.bluedragon.distribution.jy.service.collect.emuns;

/**
 * @Author zhengchengfa
 * @Description //集齐初始化节点
 * @date
 **/
public enum CollectBatchUpdateTypeEnum {

    WAYBILL_BATCH(101, "运单维度批处理");

    private int code;
    private String desc;

    CollectBatchUpdateTypeEnum(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}
