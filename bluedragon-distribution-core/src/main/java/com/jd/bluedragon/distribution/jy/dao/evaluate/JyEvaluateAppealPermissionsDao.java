package com.jd.bluedragon.distribution.jy.dao.evaluate;

import com.jd.bluedragon.common.dao.BaseDao;
import com.jd.bluedragon.distribution.jy.evaluate.JyEvaluateAppealPermissionsEntity;

import java.util.List;

/***
 * 
 * @author pengchong28
 * @email pengchong28@jd.com
 * @date 2024-03-01 15:59:15
 */
public class JyEvaluateAppealPermissionsDao extends BaseDao<JyEvaluateAppealPermissionsEntity> {

    private final static String NAMESPACE = JyEvaluateAppealPermissionsDao.class.getName();

    public JyEvaluateAppealPermissionsEntity queryByCondition(Integer siteCode) {
        return this.getSqlSession().selectOne(NAMESPACE + ".queryByCondition", siteCode);
    }

    public int updateAppealStatusById(JyEvaluateAppealPermissionsEntity entity) {
        return this.getSqlSession().update(NAMESPACE + ".updateAppealStatusById", entity);
    }

    public int insert(JyEvaluateAppealPermissionsEntity entity) {
        return this.getSqlSession().insert(NAMESPACE + ".insert", entity);
    }
}
