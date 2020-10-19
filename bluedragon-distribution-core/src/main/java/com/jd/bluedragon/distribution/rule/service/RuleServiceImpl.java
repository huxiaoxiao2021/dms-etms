package com.jd.bluedragon.distribution.rule.service;

import com.jd.bluedragon.distribution.rule.dao.RuleDao;
import com.jd.bluedragon.distribution.rule.domain.Rule;
import com.jd.etms.framework.utils.cache.annotation.Cache;
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

    @Cache(key = "RuleServiceImpl.queryByParamNoPage@args0", memoryEnable = true, memoryExpiredTime = 5 * 60 * 1000,
            redisEnable = true, redisExpiredTime = 10 * 60 * 1000)
    public List<Rule> queryByParamNoPage(Integer createSiteCode) {
        Map<String, Integer> queryParam = new HashMap<>();
        queryParam.put("siteCode", createSiteCode);
        return ruleDao.queryByParamNoPage(queryParam);
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

    @Override
    public Rule get(Integer siteCode, String type) {
        Map<String, Object> param = new HashMap<String, Object>();
        param.put("siteCode", siteCode);
        param.put("type", type);
        return ruleDao.get(param);
    }
}
