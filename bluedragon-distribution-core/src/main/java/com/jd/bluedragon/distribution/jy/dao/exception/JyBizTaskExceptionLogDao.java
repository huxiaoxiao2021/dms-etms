package com.jd.bluedragon.distribution.jy.dao.exception;

import com.jd.bluedragon.common.dao.BaseDao;
import com.jd.bluedragon.distribution.jy.exception.JyBizTaskExceptionLogEntity;

public class JyBizTaskExceptionLogDao extends BaseDao<JyBizTaskExceptionLogEntity> {


    final static String NAMESPACE = JyBizTaskExceptionLogDao.class.getName();

    public int insertSelective(JyBizTaskExceptionLogEntity entity) {
        return this.getSqlSession().insert(NAMESPACE + ".insertSelective", entity);
    }

}
