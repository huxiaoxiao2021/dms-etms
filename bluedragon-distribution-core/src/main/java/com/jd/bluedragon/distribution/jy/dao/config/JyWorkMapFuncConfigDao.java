package com.jd.bluedragon.distribution.jy.dao.config;

import com.jd.bluedragon.distribution.jy.config.JyWorkMapFuncConfigEntity;
import com.jd.bluedragon.common.dao.BaseDao;
import com.jd.bluedragon.distribution.jy.config.JyWorkMapFuncQuery;

import java.util.List;

/**
 * 拣运APP功能和工序映射表
 * 
 * @author liuduo8
 * @email liuduo3@jd.com
 * @date 2022-04-02 17:50:10
 */
public class JyWorkMapFuncConfigDao extends BaseDao<JyWorkMapFuncConfigEntity> {

    final static String NAMESPACE = JyWorkMapFuncConfigDao.class.getName();

    public int insert(JyWorkMapFuncConfigEntity entity) {
        return this.getSqlSession().insert(NAMESPACE + ".insert", entity);
    }

    public int updateById(JyWorkMapFuncConfigEntity entity) {
        return this.getSqlSession().update(NAMESPACE + ".updateById", entity);
    }

    public int deleteById(JyWorkMapFuncConfigEntity entity) {
        return this.getSqlSession().update(NAMESPACE + ".deleteById", entity);
    }

    public JyWorkMapFuncConfigEntity queryById(Long id) {
        return this.getSqlSession().selectOne(NAMESPACE + ".queryById", id);
    }

    public JyWorkMapFuncConfigEntity queryByBusinessKey(String businessKey) {
        return this.getSqlSession().selectOne(NAMESPACE + ".queryByBusinessKey", businessKey);
    }

    public List<JyWorkMapFuncConfigEntity> queryList(JyWorkMapFuncQuery query) {
        return this.getSqlSession().selectList(NAMESPACE+".queryList",query);
    }

    public int queryCount(JyWorkMapFuncQuery query) {
        return this.getSqlSession().selectOne(NAMESPACE+".queryCount",query);
    }

    public List<JyWorkMapFuncConfigEntity> queryByCondition(JyWorkMapFuncConfigEntity entity) {
        return this.getSqlSession().selectList(NAMESPACE+".queryByCondition", entity);
    }
}
