package com.jd.bluedragon.distribution.jy.dao.send;

import com.jd.bluedragon.common.dao.BaseDao;
import com.jd.bluedragon.distribution.jy.send.JyDriverViolationReportingEntity;

import java.util.List;

/**
 * @author pengchong28
 * @description 司机违规举报DAO
 * @date 2024/4/12
 */
public class JyDriverViolationReportingDao extends BaseDao<JyDriverViolationReportingEntity> {
    private final static String NAMESPACE = JyDriverViolationReportingDao.class.getName();

    public int insertSelective(JyDriverViolationReportingEntity entity) {
        return this.getSqlSession().insert(NAMESPACE + ".insertSelective", entity);
    }

    public JyDriverViolationReportingEntity findByBizId(String bizId) {
        return this.getSqlSession().selectOne(NAMESPACE + ".findByBizId", bizId);
    }

    public List<JyDriverViolationReportingEntity> findByBizIdList(List<String> list) {
        return this.getSqlSession().selectList(NAMESPACE + ".findByBizIdList", list);
    }
}
