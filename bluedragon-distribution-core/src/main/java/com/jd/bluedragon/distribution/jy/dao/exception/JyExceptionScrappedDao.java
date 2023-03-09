package com.jd.bluedragon.distribution.jy.dao.exception;


import com.jd.bluedragon.common.dao.BaseDao;
import com.jd.bluedragon.distribution.jy.exception.JyBizTaskExceptionEntity;
import com.jd.bluedragon.distribution.jy.exception.JyExceptionScrappedPO;

public class JyExceptionScrappedDao  extends BaseDao<JyExceptionScrappedPO> {

    private static final String DB_TABLE_NAME = "jy_exception_scrapped";

    final static String NAMESPACE = JyExceptionScrappedDao.class.getName();


    public int insertSelective(JyExceptionScrappedPO entity) {
        return this.getSqlSession().insert(NAMESPACE + ".insertSelective", entity);
    }

    public int updateByBizId(JyBizTaskExceptionEntity entity) {
        return this.getSqlSession().update(NAMESPACE + ".updateByBizId", entity);
    }


}