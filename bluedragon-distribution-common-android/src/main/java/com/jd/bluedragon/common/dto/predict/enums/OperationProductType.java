package com.jd.bluedragon.common.dto.predict.enums;


import java.util.ArrayList;
import java.util.TreeSet;

public enum OperationProductType {
    LUXURY("LUXURY", "奢侈品（特保）"),//

    //用EASY_FROZEN|364605拼起来的
    EASYFROZEN("EASYFROZEN", "易冻品"), //
    FRESH("FRESH", "生鲜"),//
    TIKTOK("TIKTOK", "抖音"),//
    KA("KA", "KA"),//
    MEDICINE("MEDICINE", "医药"), //
    FAST("FAST", "特快送"), //
    NONE("NONE", "-");//


    public String type;
    public String name;

    OperationProductType(String type, String name) {
        this.type = type;
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    public static String nameByType(String type) {
        for (OperationProductType operationProductType : OperationProductType.values()) {
            if (operationProductType.getType().equals(type)) {
                return operationProductType.getName();
            }
        }
        return NONE.getName();
    }

    public static OperationProductType getByType(String type, String siteCode) {
        for (OperationProductType operationProductType : OperationProductType.values()) {
            if (operationProductType.getType().equals(type)
                    //易冻品的场地编码，特殊处理
                    || ((operationProductType.getType().equals(EASYFROZEN.type)) && (type.equals(EASYFROZEN.type + "|" + siteCode)))) {
                return operationProductType;
            }
        }
        return NONE;
    }

    /**
     * 根据类型，站点id，优先级，判断应该用哪个
     * 站点id用来判断易冻品
     *
     * @param
     * @param siteCode
     * @param operationProductTypePriority
     * @return
     */
    public static String productTypeByPriority(TreeSet<String> productTypes, Integer siteCode, ArrayList<OperationProductType> operationProductTypePriority) {

        for (OperationProductType operationProductType : operationProductTypePriority) {
            String type1 = operationProductType.getType();
            //易冻品的场地编码，特殊处理
            if (type1.equals(EASYFROZEN.type)) {
                type1 = type1 + "|" + siteCode;
            }
            if (productTypes.contains(type1)) {
                return operationProductType.getType();
            }

        }
        return NONE.getType();
    }


}
