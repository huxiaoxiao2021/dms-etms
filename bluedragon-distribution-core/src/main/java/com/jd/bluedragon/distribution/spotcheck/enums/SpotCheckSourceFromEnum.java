package com.jd.bluedragon.distribution.spotcheck.enums;

import java.util.ArrayList;
import java.util.List;

/**
 * 抽检来源
 *
 * @author hujiping
 * @date 2021/8/10 10:31 上午
 */
public enum SpotCheckSourceFromEnum {

    /**
     * 平台打印抽检
     * */
    SPOT_CHECK_CLIENT_PLATE(1,"SPOT_CHECK_CLIENT_PLATE"),
    /**
     * dws抽检
     * */
    SPOT_CHECK_DWS(2,"SPOT_CHECK_DWS"),
    /**
     * 网页抽检
     * */
    SPOT_CHECK_DMS_WEB(3,"SPOT_CHECK_DMS_WEB"),
    /**
     * 安卓抽检
     * */
    SPOT_CHECK_ANDROID(4,"SPOT_CHECK_ANDROID"),
    /**
     * 人工抽检
     * */
    SPOT_CHECK_ARTIFICIAL(5,"SPOT_CHECK_ARTIFICIAL"),
    /**
     * 其他
     */
    SPOT_CHECK_OTHER(6,"other");

    public static final List<String> C_SPOT_CHECK_SOURCE;
    public static final List<String> B_SPOT_CHECK_SOURCE;

    static{
        C_SPOT_CHECK_SOURCE = new ArrayList<>();
        C_SPOT_CHECK_SOURCE.add(SPOT_CHECK_CLIENT_PLATE.name);
        C_SPOT_CHECK_SOURCE.add(SPOT_CHECK_DWS.name);
        C_SPOT_CHECK_SOURCE.add(SPOT_CHECK_ARTIFICIAL.name);
        B_SPOT_CHECK_SOURCE = new ArrayList<>();
        B_SPOT_CHECK_SOURCE.add(SPOT_CHECK_DMS_WEB.name);
        B_SPOT_CHECK_SOURCE.add(SPOT_CHECK_ANDROID.name);
    }

    SpotCheckSourceFromEnum(int code, String name){
        this.code = code;
        this.name = name;
    }

    private int code;
    private String name;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
