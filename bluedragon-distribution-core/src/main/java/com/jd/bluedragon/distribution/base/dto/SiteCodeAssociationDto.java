package com.jd.bluedragon.distribution.base.dto;

import lombok.Data;

import java.util.List;

/**
 * 场地关联关系类
 *
 * @author fanggang7
 * @copyright jd.com 京东物流JDL
 * @time 2023-07-13 21:05:02 周四
 */
@Data
public class SiteCodeAssociationDto {

    /**
     * 关联场地ID
     */
    private List<Integer> sa;

    /**
     * 生效开始时间
     */
    private String start;

    /**
     * 生效结束时间
     */
    private String end;
}
