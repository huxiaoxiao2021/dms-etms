package com.jd.bluedragon.common.dto.blockcar.enumeration;

/**
 * SealCarTypeEnum
 * 封车类型
 *
 * @author jiaowenqiang
 * @date 2019/7/2
 */
public enum SealCarTypeEnum {

    SEAL_BY_TRANSPORT_CAPABILITY(10, "按运力"),

    SEAL_BY_TASK(20, "按派车任务明细");

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

    SealCarTypeEnum(int type, String name) {
        this.type = type;
        this.name = name;
    }

    public static SealCarTypeEnum getEnum(int type) {
        for (SealCarTypeEnum status : SealCarTypeEnum.values()) {
            if (status.getType() == type) {
                return status;
            }
        }
        return null;
    }
}
