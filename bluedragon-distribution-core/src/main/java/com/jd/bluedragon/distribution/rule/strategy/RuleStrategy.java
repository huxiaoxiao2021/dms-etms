package com.jd.bluedragon.distribution.rule.strategy;


import com.jd.bluedragon.distribution.rule.domain.Rule;

/**
 * Created by zhanglei51 on 2017/12/13.
 */
public interface RuleStrategy {

    Rule get(Integer siteCode, String type) throws Exception;

    Rule findRule(Integer siteCode, String type);
}
