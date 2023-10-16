package com.jd.bluedragon.distribution.jss.oss;

/**
 * 云存储链接内外网类型枚举
 *
 * @author hujiping
 * @date 2023/10/12 2:24 PM
 */
public enum OssUrlNetTypeEnum {
    
    IN(1, "内网"),

    OUT(2, "外网");

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

    OssUrlNetTypeEnum(int type, String name) {
        this.type = type;
        this.name = name;
    }
}
