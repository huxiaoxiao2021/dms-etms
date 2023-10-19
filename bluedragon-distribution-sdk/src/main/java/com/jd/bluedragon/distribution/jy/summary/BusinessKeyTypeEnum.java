package com.jd.bluedragon.distribution.jy.summary;

public enum BusinessKeyTypeEnum {
    /*todo 自行统计统计维度*/
    JY_SEND_TASK("send_biz_id", "拣运发货主任务维度汇总"),
    JY_SEND_TASK_DETAIL("send_detail_biz_id", "拣运发货子任务维度汇总"),
    BATCH_CODE("batch_code", "批次号维度汇总"),
    ;

    private String code;

    private String desc;

    BusinessKeyTypeEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}
