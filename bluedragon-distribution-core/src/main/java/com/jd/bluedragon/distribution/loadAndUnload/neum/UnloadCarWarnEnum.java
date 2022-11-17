package com.jd.bluedragon.distribution.loadAndUnload.neum;

/**
 * @Author zhengchengfa
 * @Description //卸车扫描返回提醒(非拦截，可与拦截并存返回)话术枚举  level 标识返回话术优先级，desc标识返回话术
 * level 从1开始，数值越大，优先级越小
 * @date 20210610
 **/
public enum UnloadCarWarnEnum {

    NO_WEIGHT_FORBID_SEND_MESSAGE("0", "无重量，请补称重量方"),//实际返回话术：包裹号 + desc
    PDA_STAGING_CONFIRM_MESSAGE("1","此单为预约暂存运单，请单独交接暂存管理人员操作暂存上架！"),
    PACKAGE_OVER_WEIGHT_MESSAGE("2","包裹重量%sKG，请注意重量是否异常并针对异常包裹复重抽检"),
    PRIVATE_NETWORK_PACKAGE("3","此单为专网保障运单，请单独交接专网操作人员！"),
    PACK_NOTIN_SEAL_INTERCEPT_MESSAGE("4","此包裹不在卸车任务内,多货扫描！"),
    //    SEAL_NOT_SCANPACK_INTERCEPT_MESSAGE("5","此任务下无待卸包裹，请核实！");
    FLOW_DISACCORD("5", "该包裹流向与当前板号流向不一致！");

    private String level;

    private String desc;

    UnloadCarWarnEnum(String level, String desc) {
        this.level = level;
        this.desc = desc;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}
