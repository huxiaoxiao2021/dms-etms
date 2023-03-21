package com.jd.bluedragon.distribution.jy.dao.exception;


import com.jd.bluedragon.common.dao.BaseDao;
import com.jd.bluedragon.distribution.jy.exception.JyBizTaskExceptionEntity;
import com.jd.bluedragon.distribution.jy.exception.JyExceptionEntity;
import com.jd.bluedragon.distribution.jy.exception.JyExceptionScrappedPO;

import java.util.List;

public class JyExceptionScrappedDao  extends BaseDao<JyExceptionScrappedPO> {

    private static final String DB_TABLE_NAME = "jy_exception_scrapped";

    final static String NAMESPACE = JyExceptionScrappedDao.class.getName();


    public JyExceptionScrappedPO selectOneByBizId(String bizId) {
        return this.getSqlSession().selectOne(NAMESPACE + ".selectOneByBizId", bizId);
    }
    public int insertSelective(JyExceptionScrappedPO entity) {
        return this.getSqlSession().insert(NAMESPACE + ".insertSelective", entity);
    }

    public int updateByBizId(JyExceptionScrappedPO entity) {
        return this.getSqlSession().update(NAMESPACE + ".updateByBizId", entity);
    }

    public List<JyExceptionScrappedPO> getTaskListOfscrapped(List<String> bizIds) {
        return this.getSqlSession().selectList(NAMESPACE + ".getTaskListOfscrapped", bizIds);
    }


}