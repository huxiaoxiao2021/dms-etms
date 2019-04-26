package com.jd.bluedragon.distribution.notice.utils;

/**
 * @author lixin39
 * @Description TODO
 * @ClassName TopDisplayEnum
 * @date 2019/4/17
 */
public enum TopDisplayEnum {

    /**
     * 置顶
     */
    YES(1, "是"),

    /**
     * 不置顶
     */
    NO(0, "否");

    private int code;

    private String name;

    TopDisplayEnum(int code, String name) {
        this.code = code;
        this.name = name;
    }

    public int getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    /**
     * 根据编码获取通知类型信息
     *
     * @param code
     * @return
     */
    public static TopDisplayEnum getEnum(int code) {
        for (TopDisplayEnum type : TopDisplayEnum.values()){
            if (type.getCode() == code){
                return type;
            }
        }
        return null;
    }
}
