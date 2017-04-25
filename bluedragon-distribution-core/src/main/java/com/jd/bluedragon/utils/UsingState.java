package com.jd.bluedragon.utils;

/**
 * 启用状态
 * <p>
 * Created by lixin39 on 2017/4/10.
 */
public enum UsingState {

    /**
     * 正在使用
     */
    USING(1, "正在使用"),

    /**
     * 未使用
     */
    NOT_USED(0, "未使用");

    private int state;

    private String name;

    public int getState() {
        return state;
    }

    public String getName() {
        return name;
    }

    UsingState(int state, String name) {
        this.state = state;
        this.name = name;
    }

    public static UsingState getEnum(int state) {
        for (UsingState us : UsingState.values()) {
            if (us.getState() == state) {
                return us;
            }
        }
        return null;
    }

}
