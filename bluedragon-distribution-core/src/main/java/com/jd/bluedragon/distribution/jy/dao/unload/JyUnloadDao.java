package com.jd.bluedragon.distribution.jy.dao.unload;

import com.jd.bluedragon.common.dao.BaseDao;
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
public class JyUnloadDao extends BaseDao<JyUnloadEntity> {

    private static final String DB_TABLE_NAME = "jy_unload";

    final static String NAMESPACE = JyUnloadDao.class.getName();

    @Autowired
    private SequenceGenAdaptor sequenceGenAdaptor;

    /**
     * 新增
     *
     * @param
     * @return
     */
    public int insert(JyUnloadEntity entity) {
        entity.setId(sequenceGenAdaptor.newId(DB_TABLE_NAME));
        return this.getSqlSession().insert(NAMESPACE + ".insert", entity);
    }

    public JyUnloadEntity queryByCodeAndSite(JyUnloadEntity query) {
        return this.getSqlSession().selectOne(NAMESPACE + ".queryByCodeAndSite", query);
    }

    public JyUnloadEntity findByBizId(JyUnloadEntity query) {
        return this.getSqlSession().selectOne(NAMESPACE + ".findByBizId", query);
    }

}
