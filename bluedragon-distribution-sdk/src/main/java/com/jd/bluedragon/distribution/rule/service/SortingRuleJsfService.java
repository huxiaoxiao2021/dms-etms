package com.jd.bluedragon.distribution.rule.service;

import com.jd.bluedragon.distribution.api.Response;
import com.jd.bluedragon.distribution.rule.dto.SortingRuleDto;

import java.util.List;

/**
 * 天官赐福 ◎ 百无禁忌
 *
 * @Auther: 刘铎（liuduo8）
 * @Date: 2021/2/20 17:53
 * @Description:
 */
public interface SortingRuleJsfService {

    /**
     * 批量获取规则
     * @param siteCode
     * @param ruleTypes
     * @return
     */
    Response<List<SortingRuleDto>> findRules(Integer siteCode, List<String> ruleTypes);

    /**
     * 批量变更规则
     * 全部成功才会返回成功
     * @param sortingRuleDtos
     * @return
     */
    Response<Boolean> changeRules(Integer siteCode,String userErp,List<SortingRuleDto> sortingRuleDtos);
}
