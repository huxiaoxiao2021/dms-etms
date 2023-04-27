package com.jd.bluedragon.common.dto.integral.constants;

/**
 * @ClassName RankingTimeRangeScopeEnum
 * @Description 积分排行榜查询时间范围
 * @Author wyh
 * @Date 2023/3/6 14:31
 **/
public enum RankingTimeRangeScopeEnum {

    RANGE_TODAY(1, "今日"),

    RANGE_MONTH(2, "本月"),

    RANGE_YEAR(3, "本年");

    private Integer type;
    private String name;

    RankingTimeRangeScopeEnum(Integer type, String name) {
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
