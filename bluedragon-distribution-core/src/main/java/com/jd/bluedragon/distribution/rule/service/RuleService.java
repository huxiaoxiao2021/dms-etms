package com.jd.bluedragon.distribution.rule.service;

import com.jd.bluedragon.distribution.rule.domain.Rule;

import java.util.List;
import java.util.Map;

public interface RuleService {

    List<Rule> select(Map map);

    boolean add(Rule rule);

    void del(long id);

    Integer queryAllSize(Map map);

    Rule queryById(long id);

    boolean update(Rule rule);

    List<Rule>  queryByParamNoPage(Integer createSiteCode);

    Integer addRuleByReferSite(Integer siteCode, Integer referSiteCode);

    Rule get(Integer siteCode,String type) throws Exception;

    List<Rule> getByTypes(Integer siteCode,List<String> type);

}
