package com.jd.bluedragon.distribution.jy.dao.exception;

import com.jd.bluedragon.common.dao.BaseDao;
import com.jd.bluedragon.distribution.jy.exception.JyExceptionContrabandDto;
import com.jd.bluedragon.distribution.jy.exception.JyExceptionContrabandEntity;
import com.jd.coo.sa.mybatis.plugins.id.SequenceGenAdaptor;

import java.util.List;

public class JyExceptionContrabandDao extends BaseDao<JyExceptionContrabandEntity> {
    final static String NAMESPACE = JyExceptionContrabandDao.class.getName();
    private static final String TABLE_NAME = "jy_exception_contraband";
    private SequenceGenAdaptor sequenceGenAdaptor;

    public void setSequenceGenAdaptor(SequenceGenAdaptor sequenceGenAdaptor) {
        this.sequenceGenAdaptor = sequenceGenAdaptor;
    }

    private Long generateId() {
        return sequenceGenAdaptor.newId(TABLE_NAME);
    }

    public int insertSelective(JyExceptionContrabandEntity entity) {
        entity.setId(this.generateId());
        return this.getSqlSession().insert(NAMESPACE + ".insertSelective", entity);
    }

    public JyExceptionContrabandEntity selectOneByParams(JyExceptionContrabandDto entity) {
        return this.getSqlSession().selectOne(NAMESPACE + ".selectOneByParams", entity);
    }
}