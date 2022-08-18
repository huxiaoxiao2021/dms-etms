package com.jd.bluedragon.distribution.jy.dao.exception;

import com.jd.bluedragon.common.dao.BaseDao;
import com.jd.bluedragon.distribution.jy.exception.JyExceptionEntity;
import com.jd.bluedragon.distribution.jy.unload.JyUnloadEntity;
import com.jd.coo.sa.mybatis.plugins.id.SequenceGenAdaptor;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * 卸车任务明细表
 * 
 * @author liuduo8
 * @email liuduo3@jd.com
 * @date 2022-04-01 16:23:35
 */
public class JyExceptionDao extends BaseDao<JyExceptionEntity> {

    private static final String DB_TABLE_NAME = "jy_exception";

    final static String NAMESPACE = JyExceptionDao.class.getName();

    @Autowired
    private SequenceGenAdaptor sequenceGenAdaptor;

    /**
     * 新增
     *
     * @param
     * @return
     */
    public int insert(JyExceptionEntity entity) {
        entity.setId(sequenceGenAdaptor.newId(DB_TABLE_NAME));
        return this.getSqlSession().insert(NAMESPACE + ".insert", entity);
    }
    public int insertSelective(JyExceptionEntity entity) {
        entity.setId(sequenceGenAdaptor.newId(DB_TABLE_NAME));
        return this.getSqlSession().insert(NAMESPACE + ".insertSelective", entity);
    }

    public JyExceptionEntity queryByBarCodeAndSite(JyExceptionEntity query) {
        return this.getSqlSession().selectOne(NAMESPACE + ".queryByBarCodeAndSite", query);
    }

    public Integer update(JyExceptionEntity entity) {
        return super.update(NAMESPACE, entity);
    }
}
