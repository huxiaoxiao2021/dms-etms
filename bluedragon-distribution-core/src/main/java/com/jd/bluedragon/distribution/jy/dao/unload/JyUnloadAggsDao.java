package com.jd.bluedragon.distribution.jy.dao.unload;

import com.jd.bluedragon.common.dao.BaseDao;
import com.jd.bluedragon.distribution.jy.unload.JyUnloadAggsEntity;
import com.jd.coo.sa.mybatis.plugins.id.SequenceGenAdaptor;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * 卸车进度汇总表
 * 
 * @author liuduo8
 * @email liuduo3@jd.com
 * @date 2022-04-01 16:23:35
 */
public class JyUnloadAggsDao extends BaseDao<JyUnloadAggsEntity> {

    private static final String DB_TABLE_NAME = "jy_unload_aggs";

    final static String NAMESPACE = JyUnloadAggsDao.class.getName();

    @Autowired
    private SequenceGenAdaptor sequenceGenAdaptor;

    /**
     * 新增
     *
     * @param
     * @return
     */
    public int insert(JyUnloadAggsEntity entity) {
        entity.setId(sequenceGenAdaptor.newId(DB_TABLE_NAME));
        return this.getSqlSession().insert(NAMESPACE + ".insert", entity);
    }

    public List<JyUnloadAggsEntity> queryByBizId(JyUnloadAggsEntity entity) {
        return this.getSqlSession().selectList(NAMESPACE + ".queryByBizId", entity);
    }

    /**
     * 新增或修改
     *
     * @param
     * @return
     */
    public int insertOrUpdate(JyUnloadAggsEntity entity) {
        return this.getSqlSession().insert(NAMESPACE + ".insertOrUpdate", entity);
    }
}
