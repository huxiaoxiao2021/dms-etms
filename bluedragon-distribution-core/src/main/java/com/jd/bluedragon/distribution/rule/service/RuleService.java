package com.jd.bluedragon.distribution.rule.service;

import com.jd.bluedragon.distribution.rule.domain.Rule;

import java.util.List;
import java.util.Map;

public interface RuleService {

	Rule get(String type) throws Exception;
	
    Rule get(Integer siteCode, String type) throws Exception;

    List<Rule> select(Map map);

    void add(Rule rule);

    void del(long id);

    Integer queryAllSize(Map map);

    Rule queryById(long id);

    void update(Rule rule);

    List<Rule>  queryByParamNoPage(Map map);

    Integer addRuleByReferSite(Integer siteCode, Integer referSiteCode);

    Rule findRule(Integer siteCode, String type);
}
