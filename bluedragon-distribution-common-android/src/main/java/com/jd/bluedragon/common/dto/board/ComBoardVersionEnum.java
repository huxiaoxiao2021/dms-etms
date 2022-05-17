package com.jd.bluedragon.common.dto.board;

/**
 * com_board表新老需求不兼容，根据版本号做数据隔离
 */
public enum ComBoardVersionEnum {
    VERSION_INIT(0, "初始版本，分拣组板功能模型走场地+erp维度，一人一套模型"),
    VERSION_1(1, "分拣组板模型走场地+流向维度，同一流向人共用一套模型");

    private int version;
    private String desc;

    ComBoardVersionEnum(int version, String desc) {
        this.version = version;
        this.desc = desc;

    }


    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}
