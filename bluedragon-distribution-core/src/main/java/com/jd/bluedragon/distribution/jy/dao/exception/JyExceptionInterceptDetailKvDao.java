package com.jd.bluedragon.distribution.jy.dao.exception;

import com.jd.bluedragon.common.dao.BaseDao;
import com.jd.bluedragon.distribution.jy.exception.model.JyExceptionInterceptDetailKv;
import com.jd.bluedragon.distribution.jy.exception.query.JyExceptionInterceptDetailKvQuery;
import com.jd.coo.sa.mybatis.plugins.id.SequenceGenAdaptor;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * Description: <br>
 * Copyright: Copyright (c) 2020<br>
 * Company: jd.com 京东物流JDL<br>
 * 
 * @author fanggang7
 * @time 2023-11-12 14:09:38 周日
 */
public class JyExceptionInterceptDetailKvDao extends BaseDao<JyExceptionInterceptDetailKv> {

    private static final String DB_TABLE_NAME = "jy_exception_intercept_detail_kv";

    final static String NAMESPACE = JyExceptionInterceptDetailKvDao.class.getName();

    @Autowired
    private SequenceGenAdaptor sequenceGenAdaptor;

    public int insertSelective(JyExceptionInterceptDetailKv jyExceptionInterceptDetailKv) {
        jyExceptionInterceptDetailKv.setId(sequenceGenAdaptor.newId(DB_TABLE_NAME));
        return this.getSqlSession().insert(NAMESPACE + ".insertSelective", jyExceptionInterceptDetailKv);
    }

    public JyExceptionInterceptDetailKv selectOne(JyExceptionInterceptDetailKvQuery jyExceptionInterceptDetailKvQuery) {
        return this.getSqlSession().selectOne(NAMESPACE + ".selectOne", jyExceptionInterceptDetailKvQuery);
    }

    public JyExceptionInterceptDetailKv selectLastOneByKeyword(JyExceptionInterceptDetailKvQuery jyExceptionInterceptDetailKvQuery) {
        return this.getSqlSession().selectOne(NAMESPACE + ".selectLastOneByKeyword", jyExceptionInterceptDetailKvQuery);
    }

    public List<JyExceptionInterceptDetailKv> queryList(JyExceptionInterceptDetailKvQuery jyExceptionInterceptDetailKvQuery) {
        return this.getSqlSession().selectList(NAMESPACE + ".queryList", jyExceptionInterceptDetailKvQuery);
    }
}
