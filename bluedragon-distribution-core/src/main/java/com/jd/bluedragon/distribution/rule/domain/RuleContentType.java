package com.jd.bluedragon.distribution.rule.domain;

/**
 * 分拣规则表的content填充的内容枚举
 */
public enum RuleContentType {

    /* 填充内容为siteCode，以,(英文逗号)隔开 */
    CONTENT_WITH_SITE_CODE(1),

    /* 填充内容为siteType，以,(英文逗号)隔开 */
    CONTENT_WITH_SITE_TYPE(2),

    /* 填充内容为siteSubType，以,(英文逗号)隔开 */
    CONTENT_WITH_SITE_SUB_TYPE(3),

    /* 填充内容为0|1 */
    CONTENT_WITH_SWITCH_0_1(4),

    /* 填充内容为true|false */
    CONTENT_WITH_SWITCH_BOOLEAN(5),

    /* 填充内容为指定的String类型字符串 */
    CONTENT_WITH_STRING(6),

    /* 填充内容为指定的Integer类型字符串 */
    CONTENT_WITH_INTEGER(7),
    ;

    private int contentType;

    RuleContentType(int contentType) {
        this.contentType = contentType;
    }

    public int getContentType() {
        return contentType;
    }
}
