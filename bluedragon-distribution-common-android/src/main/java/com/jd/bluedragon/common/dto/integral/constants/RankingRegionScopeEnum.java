package com.jd.bluedragon.common.dto.integral.constants;

/**
 * @ClassName RankingRegionScopeEnum
 * @Description 积分排行榜查询范围
 * @Author wyh
 * @Date 2023/3/6 14:28
 **/
public enum RankingRegionScopeEnum {

    SCOPE_SITE(1, "场地排名"),

    SCOPE_ORG(2, "区域排名"),

    SCOPE_WHOLE_COUNTRY(3, "全国排名");

    private Integer type;
    private String name;

    RankingRegionScopeEnum(Integer type, String name) {
        this.type = type;
        this.name = name;
    }

    public Integer getType() {
        return type;
    }

    public String getName() {
        return name;
    }
}
