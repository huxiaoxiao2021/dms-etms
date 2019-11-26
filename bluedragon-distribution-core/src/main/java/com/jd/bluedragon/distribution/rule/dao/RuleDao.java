package com.jd.bluedragon.distribution.rule.dao;

import com.jd.bluedragon.common.dao.BaseDao;
import com.jd.bluedragon.distribution.rule.domain.Rule;

import java.util.List;
import java.util.Map;

public class RuleDao extends BaseDao<Rule> {

    private static final String NAMESPACE = RuleDao.class.getName();

    public Rule get(Map map) {
        return super.getSqlSession().selectOne(RuleDao.NAMESPACE + ".get", map);
    }

    public Rule queryById(long id) {
        return super.getSqlSession().selectOne(RuleDao.NAMESPACE + ".queryById", id);
    }
    
    public List<Rule> queryByParam(Map map) {
        return super.getSqlSession().selectList(RuleDao.NAMESPACE + ".queryByParam", map);
    }

    public List<Rule>  queryByParamNoPage(Map map) {
        return super.getSqlSession().selectList(RuleDao.NAMESPACE + ".queryByParam", map);
    }

    public Integer queryAllSize(Map map) {
        return super.getSqlSession().selectOne(RuleDao.NAMESPACE + ".queryAllSize", map);
    }

    public int add(Rule rule) {
        return super.getSqlSession().insert(RuleDao.NAMESPACE + ".add", rule);
    }

    public int logicDelete(Rule rule) {
        return super.getSqlSession().update(RuleDao.NAMESPACE + ".logicDelete", rule);
    }

    public int update(Rule rule) {
        return super.getSqlSession().update(RuleDao.NAMESPACE + ".update", rule);
    }

    public Integer addRuleByReferSite(Map param) {
        return super.getSqlSession().insert(RuleDao.NAMESPACE + ".addRuleByReferSite", param);
    }
}
