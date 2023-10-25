package com.jd.bluedragon.distribution.jy.dao.collectpackage;


import com.jd.bluedragon.common.dao.BaseDao;
import com.jd.bluedragon.distribution.jy.collectpackage.JyCollectPackageEntity;
import com.jd.coo.sa.mybatis.plugins.id.SequenceGenAdaptor;
import org.springframework.beans.factory.annotation.Autowired;

public class JyCollectPackageDao extends BaseDao<JyCollectPackageEntity> {
    private final static String NAMESPACE = JyCollectPackageDao.class.getName();
    private static final String DB_TABLE_NAME = "jy_collect_package";
    @Autowired
    private SequenceGenAdaptor sequenceGenAdaptor;

    int deleteByPrimaryKey(Long id) {
        return this.getSqlSession().delete(NAMESPACE + ".deleteByPrimaryKey", id);
    }

    int insert(JyCollectPackageEntity record) {
        record.setId(sequenceGenAdaptor.newId(DB_TABLE_NAME));
        return this.getSqlSession().insert(NAMESPACE + ".insert", record);
    }

    public int insertSelective(JyCollectPackageEntity record) {
        record.setId(sequenceGenAdaptor.newId(DB_TABLE_NAME));
        return this.getSqlSession().insert(NAMESPACE + ".insertSelective", record);
    }

    public JyCollectPackageEntity selectByPrimaryKey(Long id) {
        return this.getSqlSession().selectOne(NAMESPACE + ".selectByPrimaryKey", id);
    }

    public int updateByPrimaryKeySelective(JyCollectPackageEntity record) {
        return this.getSqlSession().update(NAMESPACE + ".updateByPrimaryKeySelective", record);
    }

    int updateByPrimaryKey(JyCollectPackageEntity record) {
        return this.getSqlSession().update(NAMESPACE + ".updateByPrimaryKey", record);
    }

    public JyCollectPackageEntity queryJyCollectPackageRecord(JyCollectPackageEntity query) {
        return this.getSqlSession().selectOne(NAMESPACE + ".queryJyCollectPackageRecord", query);
    }
}