package com.jd.bluedragon.distribution.jy.service.collect.emuns;

/**
 * @Author zhengchengfa
 * @Description //集齐初始化节点
 * @date
 **/
public enum CollectInitNodeEnum {


    SEAL_INIT(101, "封车初始化"),
    CANCEL_SEAL_INIT(102, "本车集齐"),
    NULL_TASK_INIT(103, "在库集齐");

    private int code;
    private String desc;

    CollectInitNodeEnum(int code, String desc) {
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
