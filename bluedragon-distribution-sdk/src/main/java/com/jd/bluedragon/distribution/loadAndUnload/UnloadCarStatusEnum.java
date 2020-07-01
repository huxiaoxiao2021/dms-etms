package com.jd.bluedragon.distribution.loadAndUnload;

/**
 * 卸车任务状态枚举
 *
 * @author: hujiping
 * @date: 2020/6/23 17:44
 */
public enum UnloadCarStatusEnum {

    UNLOAD_CAR_UN_DISTRIBUTE(0, "未分配"),

    UNLOAD_CAR_UN_START(1,"未开始"),

    UNLOAD_CAR_STARTED(2, "已开始"),

    UNLOAD_CAR_END(3, "已完成");

    private int type;

    private String name;

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    UnloadCarStatusEnum(int type, String name) {
        this.type = type;
        this.name = name;
    }

    public static UnloadCarStatusEnum getEnum(int type) {
        for (UnloadCarStatusEnum status : UnloadCarStatusEnum.values()) {
            if (status.getType() == type) {
                return status;
            }
        }
        return null;
    }

}
