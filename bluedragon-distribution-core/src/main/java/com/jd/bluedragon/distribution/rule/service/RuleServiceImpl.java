package com.jd.bluedragon.distribution.rule.service;

import com.jd.bluedragon.distribution.rule.dao.RuleDao;
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
    private RuleDao ruleDao;

    public Rule queryById(long id) {
        return this.ruleDao.queryById(id);
    }

    public void add(Rule rule) {
        rule.setTs(System.currentTimeMillis());
        ruleDao.add(rule);
    }

    public void update(Rule rule) {
        rule.setTs(System.currentTimeMillis());
        ruleDao.update(rule);
    }

    public void del(long id) {
        Rule rule = new Rule();
        rule.setTs(System.currentTimeMillis());
        rule.setRuleId(id);
        ruleDao.logicDelete(rule);
    }

    public Integer queryAllSize(Map map) {
        return ruleDao.queryAllSize(map);
    }

    public List<Rule> queryByParamNoPage(Map map) {
        return ruleDao.queryByParamNoPage(map);
    }

    @Override
    public List<Rule> select(Map map) {
        return ruleDao.queryByParam(map);
    }

    @Override
    public Integer addRuleByReferSite(Integer siteCode, Integer referSiteCode) {
        Map<String, Object> param = new HashMap<String, Object>();
        param.put("siteCode", siteCode);
        param.put("referSiteCode", referSiteCode);
        param.put("ts", System.currentTimeMillis());
        return ruleDao.addRuleByReferSite(param);
    }
}
