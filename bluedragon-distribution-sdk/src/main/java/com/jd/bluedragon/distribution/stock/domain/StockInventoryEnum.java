package com.jd.bluedragon.distribution.stock.domain;


import java.util.HashMap;
import java.util.Map;

/**
 * 库存盘点枚举
 *
 * @author hujiping
 * @date 2021/6/7 11:18 上午
 */
public enum StockInventoryEnum {

    INVENTORY_UN(0,"未盘点"),
    INVENTORY_ALREADY(1,"已盘点"),
    INVENTORY_OTHER(2,"被他人盘点");

    private int code;
    private String name;

    private static Map<Integer, StockInventoryEnum> codeMap;

    static {
        //将所有枚举装载到map中
        codeMap = new HashMap<Integer, StockInventoryEnum>();

        for (StockInventoryEnum _enum : StockInventoryEnum.values()) {
            codeMap.put(_enum.getCode(), _enum);
        }
    }

    /**
     * 通过编码获取枚举
     *
     * @param code 编码
     * @return 规则类型
     */
    public static StockInventoryEnum getEnumByKey(int code) {
        return codeMap.get(code);
    }

    StockInventoryEnum(int code, String name) {
        this.code = code;
        this.name = name;
    }

    public int getCode() {
        return code;
    }

    public String getName() {
        return name;
    }
}
