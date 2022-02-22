package com.jd.bluedragon.distribution.spotcheck.enums;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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

    /**
     * 人工来源
     */
    public static final List<String> ARTIFICIAL_SOURCE;
    /**
     * 设备来源
     */
    public static final List<String> EQUIPMENT_SOURCE;

    static{
        C_SPOT_CHECK_SOURCE = new ArrayList<>();
        C_SPOT_CHECK_SOURCE.add(SPOT_CHECK_CLIENT_PLATE.getName());
        C_SPOT_CHECK_SOURCE.add(SPOT_CHECK_DWS.getName());
        C_SPOT_CHECK_SOURCE.add(SPOT_CHECK_ARTIFICIAL.getName());
        B_SPOT_CHECK_SOURCE = new ArrayList<>();
        B_SPOT_CHECK_SOURCE.add(SPOT_CHECK_DMS_WEB.getName());
        B_SPOT_CHECK_SOURCE.add(SPOT_CHECK_ANDROID.getName());

        ARTIFICIAL_SOURCE = new ArrayList<>();
        ARTIFICIAL_SOURCE.add(SPOT_CHECK_ARTIFICIAL.getName());
        ARTIFICIAL_SOURCE.add(SPOT_CHECK_ANDROID.getName());
        ARTIFICIAL_SOURCE.add(SPOT_CHECK_DMS_WEB.getName());
        EQUIPMENT_SOURCE = new ArrayList<>();
        EQUIPMENT_SOURCE.add(SPOT_CHECK_CLIENT_PLATE.getName());
        EQUIPMENT_SOURCE.add(SPOT_CHECK_DWS.getName());
    }

    SpotCheckSourceFromEnum(int code, String name){
        this.code = code;
        this.name = name;
    }

    public static Integer analysisCodeFromName(String name){
        for (SpotCheckSourceFromEnum value : SpotCheckSourceFromEnum.values()) {
            if(Objects.equals(value.getName(), name)){
                return value.getCode();
            }
        }
        return null;
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
