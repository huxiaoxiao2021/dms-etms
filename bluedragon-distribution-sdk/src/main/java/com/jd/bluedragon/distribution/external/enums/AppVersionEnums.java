package com.jd.bluedragon.distribution.external.enums;

public enum AppVersionEnums {

    PDA_OLD("1", "初版APP"),
    PDA_GUIDED("2", "新版引导式APP");

    private final String version;

    private final String desc;

    AppVersionEnums(String version, String desc) {
        this.version = version;
        this.desc = desc;
    }

    public String getVersion() {
        return version;
    }

    public String getDesc() {
        return desc;
    }

    public static boolean existValidation(String version) {
        for(AppVersionEnums enums : AppVersionEnums.values()) {
            if(enums.getVersion().equals(version)) {
                return true;
            }
        }
        return false;
    }

    public static String getDescByCode(String version) {
        for(AppVersionEnums enums : AppVersionEnums.values()) {
            if(enums.getVersion().equals(version)) {
                return enums.getDesc();
            }
        }
        return null;
    }
}
