package com.jd.bluedragon.distribution.transport.domain;

/**
 * Created by xumei3 on 2017/12/29.
 */
public enum ARCommonDictionaryType {
    //始发城市
    START_CITY(10),

    //目的城市
    END_CITY(20),

    //车型
    BUS_TYPE(30);


    // 枚举对象的type,表示通用字典中属性的类型
    private int type;

    // 枚举对象构造函数
    private ARCommonDictionaryType(int  type) {
        this.type = type;
    }

    public int getType() {
        return this.type;
    }
}
