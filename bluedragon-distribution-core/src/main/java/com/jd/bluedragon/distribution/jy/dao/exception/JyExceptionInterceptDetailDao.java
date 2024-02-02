package com.jd.bluedragon.distribution.jy.dao.exception;

import com.jd.bluedragon.common.dao.BaseDao;
import com.jd.bluedragon.distribution.jy.exception.model.JyExceptionInterceptDetail;
import com.jd.bluedragon.distribution.jy.exception.query.JyExceptionInterceptDetailQuery;
import com.jd.coo.sa.mybatis.plugins.id.SequenceGenAdaptor;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * 异常任务-拦截明细表
 * Copyright: Copyright (c) 2020<br>
 * Company: jd.com 京东物流JDL<br>
 * 
 * @author fanggang7
 * @time 2023-11-12 14:09:38 周日
 */
public class JyExceptionInterceptDetailDao extends BaseDao<JyExceptionInterceptDetail> {

    private static final String DB_TABLE_NAME = "jy_exception_intercept_detail";

    final static String NAMESPACE = JyExceptionInterceptDetailDao.class.getName();

    @Autowired
    private SequenceGenAdaptor sequenceGenAdaptor;

    public int insertSelective(JyExceptionInterceptDetail jyExceptionInterceptDetail) {
        jyExceptionInterceptDetail.setId(sequenceGenAdaptor.newId(DB_TABLE_NAME));
        return this.getSqlSession().insert(NAMESPACE + ".insertSelective", jyExceptionInterceptDetail);
    }

    public JyExceptionInterceptDetail selectOne(JyExceptionInterceptDetailQuery jyExceptionInterceptDetailQuery) {
        return this.getSqlSession().selectOne(NAMESPACE + ".selectOne", jyExceptionInterceptDetailQuery);
    }

    public List<JyExceptionInterceptDetail> queryList(JyExceptionInterceptDetailQuery jyExceptionInterceptDetailQuery) {
        return this.getSqlSession().selectList(NAMESPACE + ".queryList", jyExceptionInterceptDetailQuery);
    }

    public JyExceptionInterceptDetail updateByBizId(JyExceptionInterceptDetail jyExceptionInterceptDetail) {
        return this.getSqlSession().selectOne(NAMESPACE + ".updateByBizId", jyExceptionInterceptDetail);
    }
}
