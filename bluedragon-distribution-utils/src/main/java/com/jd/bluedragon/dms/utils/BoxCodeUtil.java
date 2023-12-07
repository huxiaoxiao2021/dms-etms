package com.jd.bluedragon.dms.utils;

import org.apache.commons.lang.StringUtils;

/**
 * @author jinjingcheng
 * @Description
 * @date 2019/4/22.
 */
public class BoxCodeUtil {
    private static final String[] REPLACE_CHARS = { "分拣中心", "分拨中心", "中转场", "中转站" };

    public static String getTemplateName(int count){
        switch (count){
            case 0:
                return "dms-boxlabelzero";
            case 1:
                return "dms-boxlabelone";
            case 2:
                return "dms-boxlabeltwo";
            case 3:
                return "dms-boxlabelthree";
            default:
                return "dms-boxlabelthreeover";
        }
    }
    public static Integer getTemplateVersion(String  templateName){
//        switch (templateName){
//            case  "dms-boxlabelzero":
//                return 1;
//            case "dms-boxlabelone":
//                return 1;
//            case "dms-boxlabeltwo":
//                return 1;
//            case "dms-boxlabelthree":
//                return 1;
//            case "dms-boxlabelthreeover":
//                return 1;
//            default:
//                return 1;
//        }
        return 1;
    }
    public static String replaceSortingName(String sortingName) {
        if(StringUtils.isBlank(sortingName)){
            return sortingName;
        }
        for (String item : REPLACE_CHARS) {
            sortingName = sortingName.replace(item, "");
        }
        return sortingName;
    }

    public static String getBoxTypeName(String type) {
        if(StringUtils.isBlank(type)){
            return "未知类型";
        }
        if (type.equals("BC")) {
            return "正普";
        } else if (type.equals("TC")) {
            return "退普";
        } else if (type.equals("GC")) {
            return "取普";
        } else if (type.equals("BS")) {
            return "正奢";
        } else if (type.equals("TS")) {
            return "退奢";
        } else if (type.equals("GS")) {
            return "取奢";
        } else if (type.equals("FC")) {
            return "返普";
        } else if (type.equals("FS")) {
            return "返奢";
        } else if (type.equals("ZC")) {
            return "上接";
        } else if (type.equals("FCQ")) {
            return "返单";
        } else if (type.equals("ZC1")) {
            return "商后";
        } else if (type.equals("BX")) {
            return "BX";
        } else if (type.equals("TW")) {
            return "逆配";
        } else if (type.equals("LP")) {
            return "理赔";
        }
        return "未知类型";
    }
    public static String getCategoryText(Integer transportType) {
        if(null == transportType){
            return "公";
        }
        switch (transportType){
            case 1:
                return "航";
            case 2:
                return "公";
            case 3:
                return "铁";
            default:
            return "公";
        }
    }
    public static String getMixBoxTypeText(Integer mixBoxType) {
        if(mixBoxType == null){
            return "混";
        }
        if (mixBoxType.equals(0)){
            return "";
        }
        return "混";
    }


}
