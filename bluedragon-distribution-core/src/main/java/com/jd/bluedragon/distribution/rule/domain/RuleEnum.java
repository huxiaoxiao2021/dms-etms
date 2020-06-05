package com.jd.bluedragon.distribution.rule.domain;

import com.jd.bluedragon.distribution.rules.RulesTypeEnum;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public enum RuleEnum {

    RULE_1000(RulesTypeEnum.RULE_TYPE_1000, RuleContentType.CONTENT_WITH_SITE_CODE, "地铁自提规则"),
    RULE_1005(RulesTypeEnum.RULE_TYPE_1005, RuleContentType.CONTENT_WITH_SITE_CODE, "自提目的地站点类型"),
    RULE_1006(RulesTypeEnum.RULE_TYPE_1006, RuleContentType.CONTENT_WITH_SITE_CODE, "好邻居便利自提点订单-站点编号"),
    RULE_1007(RulesTypeEnum.RULE_TYPE_1007, RuleContentType.CONTENT_WITH_SITE_TYPE, "社区合作自提点订单-站点类型"),
    RULE_1008(RulesTypeEnum.RULE_TYPE_1008, RuleContentType.CONTENT_WITH_SITE_TYPE, "地铁自提柜订单-站点类型"),
    RULE_1009(RulesTypeEnum.RULE_TYPE_1009, RuleContentType.CONTENT_WITH_SITE_TYPE, "中转站订单规则"),
    RULE_1010(RulesTypeEnum.RULE_TYPE_1010, RuleContentType.CONTENT_WITH_SITE_CODE, "奢侈品订单发货站点类型规则"),
    RULE_1011(RulesTypeEnum.RULE_TYPE_1011, RuleContentType.CONTENT_WITH_SITE_CODE, "奢侈品订单发货三方站点规则"),
    RULE_1020(RulesTypeEnum.RULE_TYPE_1020, RuleContentType.CONTENT_WITH_SITE_CODE, "合约订单规则"),
    RULE_1021(RulesTypeEnum.RULE_TYPE_1021, RuleContentType.CONTENT_WITH_SITE_TYPE, "合约订单发货站点类型规则"),
    RULE_1030(RulesTypeEnum.RULE_TYPE_1030, RuleContentType.CONTENT_WITH_SITE_CODE, "以旧换新站点规则"),
    RULE_1040(RulesTypeEnum.RULE_TYPE_1040, RuleContentType.CONTENT_WITH_SITE_CODE, "上门换新站点规则"),
    RULE_1050(RulesTypeEnum.RULE_TYPE_1050, RuleContentType.CONTENT_WITH_SITE_CODE, "货到付款订单发货站点规则"),
    RULE_1060(RulesTypeEnum.RULE_TYPE_1060, RuleContentType.CONTENT_WITH_SITE_TYPE, "正向站点类型规则"),
    RULE_1070(RulesTypeEnum.RULE_TYPE_1070, RuleContentType.CONTENT_WITH_SITE_TYPE, "逆向站点类型规则"),
    RULE_1080(RulesTypeEnum.RULE_TYPE_1080, RuleContentType.CONTENT_WITH_SITE_SUB_TYPE, "逆向库房类型规则"),
    RULE_1090(RulesTypeEnum.RULE_TYPE_1090, RuleContentType.CONTENT_WITH_SITE_SUB_TYPE, "逆向售后类型规则"),
    RULE_1100(RulesTypeEnum.RULE_TYPE_1100, RuleContentType.CONTENT_WITH_SITE_CODE, "FDC仓站点编号"),
    RULE_1110(RulesTypeEnum.RULE_TYPE_1110, RuleContentType.CONTENT_WITH_SITE_CODE, "外单EMS直送"),
    RULE_1120(RulesTypeEnum.RULE_TYPE_1120, RuleContentType.CONTENT_WITH_SWITCH_0_1, "跨分拣提示规则"),
    RULE_1121(RulesTypeEnum.RULE_TYPE_1121, RuleContentType.CONTENT_WITH_SWITCH_0_1, "跨区提示规则"),
    RULE_1122(RulesTypeEnum.RULE_TYPE_1122, RuleContentType.CONTENT_WITH_SWITCH_0_1, "路由校验规则"),
    RULE_1123(RulesTypeEnum.RULE_TYPE_1123, RuleContentType.CONTENT_WITH_SWITCH_0_1, "漏称重PDA拦截规则"),
    RULE_1124(RulesTypeEnum.RULE_TYPE_1124, RuleContentType.CONTENT_WITH_SWITCH_0_1, "分拣机漏称重拦截规则"),
    RULE_1125(RulesTypeEnum.RULE_TYPE_1125, RuleContentType.CONTENT_WITH_SWITCH_0_1, "混装箱开关"),
    RULE_1130(RulesTypeEnum.RULE_TYPE_1130, RuleContentType.CONTENT_WITH_SWITCH_0_1, "运单数据精简规则"),
    RULE_1140(RulesTypeEnum.RULE_TYPE_1140, RuleContentType.CONTENT_WITH_SWITCH_0_1, "waybill库数据精简规则"),
    RULE_1500(RulesTypeEnum.RULE_TYPE_1500, RuleContentType.CONTENT_WITH_SWITCH_0_1, "分拣中心本地数据上传开关"),
    RULE_1600(RulesTypeEnum.RULE_TYPE_1600, RuleContentType.CONTENT_WITH_SWITCH_BOOLEAN, "是否可以清理日志开关"),
    RULE_1700(RulesTypeEnum.RULE_TYPE_1700, RuleContentType.CONTENT_WITH_SWITCH_0_1, "本地scanListsTask的任务开关"),
    RULE_1800(RulesTypeEnum.RULE_TYPE_1800, RuleContentType.CONTENT_WITH_SWITCH_0_1, "是否允许运单称重开关"),
    RULE_2000(RulesTypeEnum.RULE_TYPE_2000, RuleContentType.CONTENT_WITH_STRING, "支持按路区分拣开关"),
    RULE_2001(RulesTypeEnum.RULE_TYPE_2001, RuleContentType.CONTENT_WITH_INTEGER, "支持按特殊类型分拣开关"),
    ;

    private RulesTypeEnum rulesTypeEnum;

    private RuleContentType contentType;

    private String memo;

    RuleEnum(RulesTypeEnum rulesTypeEnum, RuleContentType contentType, String memo) {
        this.rulesTypeEnum = rulesTypeEnum;
        this.contentType = contentType;
        this.memo = memo;
    }

    public static List<Map<String,Object>> getRulesMaps() {
        List<Map<String, Object>> resultList = new ArrayList<>();
        Map<String,Object> map = null;
        for (RuleEnum item : RuleEnum.values()) {
            map = new HashMap<>();
            map.put("ruleType", item.getRulesTypeEnum().getType());
            map.put("ruleTypeName", item.getRulesTypeEnum().getTypeName());
            map.put("contentType", item.getContentType().getContentType());
            map.put("memo",item.getMemo());
            resultList.add(map);
        }
        return resultList;
    }

    public RulesTypeEnum getRulesTypeEnum() {
        return rulesTypeEnum;
    }

    public RuleContentType getContentType() {
        return contentType;
    }

    public String getMemo() {
        return memo;
    }
}
