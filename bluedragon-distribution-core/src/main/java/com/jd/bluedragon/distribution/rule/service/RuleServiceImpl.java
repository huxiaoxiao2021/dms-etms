package com.jd.bluedragon.distribution.rule.service;

import com.jd.bluedragon.distribution.rule.dao.RuleMapper;
import com.jd.bluedragon.distribution.rule.domain.Rule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class RuleServiceImpl implements RuleService {

    @Autowired
    private RuleMapper ruleMapper;

    public Rule queryById(long id) {
        return this.ruleMapper.queryById(id);
    }

    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public void add(Rule rule) {
        rule.setTs(System.currentTimeMillis());
        ruleMapper.add(rule);
    }

    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public void update(Rule rule) {
        rule.setTs(System.currentTimeMillis());
        ruleMapper.update(rule);
    }

    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public void del(long id) {
        Rule rule = new Rule();
        rule.setTs(System.currentTimeMillis());
        rule.setRuleId(id);
        ruleMapper.logicDelete(rule);
    }

    public Integer queryAllSize(Map map) {
        return ruleMapper.queryAllSize(map);
    }

    public List<Rule> queryByParamNoPage(Map map) {
        return ruleMapper.queryByParamNoPage(map);
    }

    @Override
    public List<Rule> select(Map map) {
        return ruleMapper.queryByParam(map);
    }

    @Override
    public Integer addRuleByReferSite(Integer siteCode, Integer referSiteCode) {
        Map<String, Object> param = new HashMap<String, Object>();
        param.put("siteCode", siteCode);
        param.put("referSiteCode", referSiteCode);
        param.put("ts", System.currentTimeMillis());
        return ruleMapper.addRuleByReferSite(param);
    }
}
