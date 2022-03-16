package com.jd.bluedragon.distribution.consumable.domain;

/**
 * @ProjectName：bluedragon-distribution
 * @Package： com.jd.bluedragon.distribution.consumable.domain
 * @ClassName: ConsumableCodeEnums
 * @Description: 维护所有的包装耗材编码，木质的包装耗材需要在方法中维护 isWoodenConsumable
 * 终端包装耗材融合项目， 所有的耗材类型都放到终端维护，针对木质类型的包装耗材编码的判断也有了以下新的标准，编码在终端平台上维护，各个端同步写死
 *
 * 计费编码 包装名称
 * 09092048 木架
 * 09092049 木托盘
 * 09092050 木箱
 *
 * 能力平台木质包装编码：
 * A-VMJ-0001  打木架
 * A-VMX-0001  打木箱
 * A-VMT-0001   打木托
 *
 * 产品编码：
 * 快递包装的产品编码：ed-a-0011
 * 快运包装的产品编码：fr-a-0005
 * 包装售卖的产品编码：ed-a-0046
 *
 * @Author： wuzuxiang
 * @CreateDate 2022/1/19 14:45
 * @Copyright: Copyright (c)2020 JDL.CN All Right Reserved
 * @Since: JDK 1.8
 * @Version： V1.0
 */
public enum ConsumableCodeEnums {

    MJ("09092048","木架"),
    MTP("09092049","木托盘"),
    MX("09092050","木箱");


    private final String code;

    private final String name;

    ConsumableCodeEnums(String code, String name) {
        this.code = code;
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    /**
     * 判断该耗材编码是不是木质类型的包装耗材
     * @param code 包装耗材编码
     * @return
     */
    public static boolean isWoodenConsumable(String code) {
        return MJ.getCode().equals(code) || MTP.getCode().equals(code) || MX.getCode().equals(code);
    }
}
