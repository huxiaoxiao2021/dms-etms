package com.jd.bluedragon.distribution.jy.dao.send;

import com.jd.bluedragon.common.dao.BaseDao;
import com.jd.bluedragon.distribution.jy.send.JySendEntity;
import com.jd.coo.sa.mybatis.plugins.id.SequenceGenAdaptor;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * 发货明细表
 * 
 * @author liuduo8
 * @email liuduo3@jd.com
 * @date 2022-05-16 17:50:07
 */
public class JySendDao extends BaseDao<JySendEntity> {

    private final static String NAMESPACE = JySendDao.class.getName();

    private static final String DB_TABLE_NAME = "jy_send";

    @Autowired
    private SequenceGenAdaptor sequenceGenAdaptor;

    /**
     * 新增
     *
     * @param
     * @return
     */
    public int insert(JySendEntity entity) {
        entity.setId(sequenceGenAdaptor.newId(DB_TABLE_NAME));
        return this.getSqlSession().insert(NAMESPACE + ".insert", entity);
    }

    public int updateByCondition(JySendEntity entity) {
        return this.getSqlSession().insert(NAMESPACE + ".updateByCondition", entity);
    }

    public JySendEntity findSendRecordExistAbnormal(JySendEntity entity) {
        return this.getSqlSession().selectOne(NAMESPACE + ".findSendRecordExistAbnormal", entity);
    }

    public JySendEntity queryByCodeAndSite(JySendEntity entity) {
        return this.getSqlSession().selectOne(NAMESPACE + ".queryByCodeAndSite", entity);
    }

    public JySendEntity findByBizId(JySendEntity entity) {
        return this.getSqlSession().selectOne(NAMESPACE + ".findByBizId", entity);
    }

    public int updateTransferProperBySendCode(JySendEntity entity) {
        return this.getSqlSession().update(NAMESPACE + ".updateTransferProperBySendCode", entity);
    }
}
