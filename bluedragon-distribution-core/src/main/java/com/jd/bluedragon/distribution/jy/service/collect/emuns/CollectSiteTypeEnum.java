package com.jd.bluedragon.distribution.jy.service.collect.emuns;

/**
 * @Author zhengchengfa
 * @Description //TODO
 * @date
 **/
public enum CollectSiteTypeEnum {

    HANDOVER(101, "中转场地维度交接集齐"),//上游发货-下游验货-中转场地做交接比对
    WAYBILL(102, "末端转运维度运单集齐");//末端场地做整个运单维度库存比对

    private int code;
    private String desc;

    CollectSiteTypeEnum(int code, String desc) {
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
