package com.jd.bluedragon.distribution.rule.dao;

import com.jd.bluedragon.distribution.rule.domain.Rule;

import java.util.List;
import java.util.Map;

public interface RuleMapper {

    Rule get(Map map);
    
    Rule queryById(long id);
    
    List<Rule> queryByParam(Map map);

    List<Rule>  queryByParamNoPage(Map map);

    Integer queryAllSize(Map map);
    
    void add(Rule rule);
    
    void logicDelete(Rule rule);

    void update(Rule rule);

    Integer addRuleByReferSite(Map param);
}
