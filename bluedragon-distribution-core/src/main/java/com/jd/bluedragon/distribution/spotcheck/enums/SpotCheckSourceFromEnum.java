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
    SPOT_CHECK_CLIENT_PLATE(1,"SPOT_CHECK_CLIENT_PLATE", "平台打印抽检"),
    /**
     * dws抽检
     * */
    SPOT_CHECK_DWS(2,"SPOT_CHECK_DWS", "dws抽检"),
    /**
     * 网页抽检
     * */
    SPOT_CHECK_DMS_WEB(3,"SPOT_CHECK_DMS_WEB", "网页抽检"),
    /**
     * 安卓抽检
     * */
    SPOT_CHECK_ANDROID(4,"SPOT_CHECK_ANDROID", "安卓抽检"),
    /**
     * 人工抽检
     * */
    SPOT_CHECK_ARTIFICIAL(5,"SPOT_CHECK_ARTIFICIAL", "人工抽检"),
    /**
     * 其他
     */
    SPOT_CHECK_OTHER(6,"other", "其他");

    public static final List<String> C_SPOT_CHECK_SOURCE;
    public static final List<String> B_SPOT_CHECK_SOURCE;

    /**
     * 人工来源
     */
    public static final List<String> ARTIFICIAL_SOURCE;
    public static final List<Integer> ARTIFICIAL_SOURCE_NUM;
    /**
     * 设备来源
     */
    public static final List<String> EQUIPMENT_SOURCE;
    public static final List<Integer> EQUIPMENT_SOURCE_NUM;

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
        ARTIFICIAL_SOURCE_NUM = new ArrayList<>();
        ARTIFICIAL_SOURCE_NUM.add(SPOT_CHECK_ARTIFICIAL.getCode());
        ARTIFICIAL_SOURCE_NUM.add(SPOT_CHECK_ANDROID.getCode());
        ARTIFICIAL_SOURCE_NUM.add(SPOT_CHECK_DMS_WEB.getCode());
        EQUIPMENT_SOURCE = new ArrayList<>();
        EQUIPMENT_SOURCE.add(SPOT_CHECK_CLIENT_PLATE.getName());
        EQUIPMENT_SOURCE.add(SPOT_CHECK_DWS.getName());
        EQUIPMENT_SOURCE_NUM = new ArrayList<>();
        EQUIPMENT_SOURCE_NUM.add(SPOT_CHECK_CLIENT_PLATE.getCode());
        EQUIPMENT_SOURCE_NUM.add(SPOT_CHECK_DWS.getCode());
    }

    SpotCheckSourceFromEnum(int code, String name, String description){
        this.code = code;
        this.name = name;
        this.description = description;
    }

    public static String analysisDescFromCode(int code){
        for (SpotCheckSourceFromEnum value : SpotCheckSourceFromEnum.values()) {
            if(Objects.equals(value.getCode(), code)){
                return value.getDescription();
            }
        }
        return null;
    }

    public static Integer analysisCodeFromName(String name){
        for (SpotCheckSourceFromEnum value : SpotCheckSourceFromEnum.values()) {
            if(Objects.equals(value.getName(), name)){
                return value.getCode();
            }
        }
        return null;
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
    private String description;

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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
