package com.jd.bluedragon.distribution.rules;

/**
 * 规则类型枚举
 */
public enum RulesTypeEnum {

    RULE_TYPE_1000(1000,"地铁自提规则"),
    RULE_TYPE_1005(1005,"自提目的地站点类型"),
    RULE_TYPE_1006(1006,"好邻居便利自提点订单-站点编号"),
    RULE_TYPE_1007(1007,"社区合作自提点订单-站点类型"),
    RULE_TYPE_1008(1008,"地铁自提柜订单-站点类型"),
    RULE_TYPE_1009(1009,"中转站订单规则"),
    RULE_TYPE_1010(1010,"奢侈品订单发货站点类型规则"),
    RULE_TYPE_1011(1011,"奢侈品订单发货三方站点规则"),
    RULE_TYPE_1020(1020,"合约订单规则"),
    RULE_TYPE_1021(1021,"合约订单发货站点类型规则"),
    RULE_TYPE_1030(1030,"以旧换新站点规则"),
    RULE_TYPE_1040(1040,"上门换新站点规则"),
    RULE_TYPE_1050(1050,"货到付款订单发货站点规则"),
    RULE_TYPE_1060(1060,"正向站点类型规则"),
    RULE_TYPE_1070(1070,"逆向站点类型规则"),
    RULE_TYPE_1080(1080,"逆向库房类型规则"),
    RULE_TYPE_1090(1090,"逆向售后类型规则"),
    RULE_TYPE_1100(1100,"FDC仓站点编号"),
    RULE_TYPE_1110(1110,"外单EMS直送"),
    RULE_TYPE_1120(1120,"跨分拣提示规则"),
    RULE_TYPE_1121(1121,"跨区提示规则"),
    RULE_TYPE_1122(1122,"路由校验规则"),
    RULE_TYPE_1123(1123,"漏称重PDA拦截规则"),
    RULE_TYPE_1124(1124,"分拣机漏称重拦截规则"),
    RULE_TYPE_1125(1125,"混装箱开关"),
    RULE_TYPE_1130(1130,"运单数据精简规则"),
    RULE_TYPE_1140(1140,"waybill库数据精简规则"),
    RULE_TYPE_1500(1500,"分拣中心本地数据上传开关"),
    RULE_TYPE_1600(1600,"是否可以清理日志开关"),
    RULE_TYPE_1700(1700,"本地scanListsTask的任务开关"),
    RULE_TYPE_1800(1800,"是否允许运单称重开关"),
    RULE_TYPE_2000(2000,"支持按路区分拣开关"),
    RULE_TYPE_2001(2001,"支持按特殊类型分拣开关")
    ;

    private Integer Type;

    private String typeName;

    RulesTypeEnum(Integer type, String typeName) {
        Type = type;
        this.typeName = typeName;
    }

    public Integer getType() {
        return Type;
    }

    public String getTypeName() {
        return typeName;
    }
}
