package com.jd.bluedragon.distribution.jy.dao.exception;


import com.jd.bluedragon.common.dao.BaseDao;
import com.jd.bluedragon.distribution.jy.exception.JyDamageConsumableEntity;
import com.jd.bluedragon.distribution.jy.exception.JyExceptionDamageEntity;
import com.jd.coo.sa.mybatis.plugins.id.SequenceGenAdaptor;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 异常-破损耗材 DAO
 */
public class JyDamageConsumableDao extends BaseDao<JyDamageConsumableEntity> {

    final static String NAMESPACE = JyDamageConsumableDao.class.getName();
    private static final String TABLE_NAME = "jy_damage_consumable_detail";
    private SequenceGenAdaptor sequenceGenAdaptor;
    public void setSequenceGenAdaptor(SequenceGenAdaptor sequenceGenAdaptor) {
        this.sequenceGenAdaptor = sequenceGenAdaptor;
    }
    private Long generateId(){
        return sequenceGenAdaptor.newId(TABLE_NAME);
    }


    public int insertBatch(List<JyDamageConsumableEntity> entities) {
        return this.getSqlSession().insert(NAMESPACE + ".insertBatch", entities);
    }

    public List<JyDamageConsumableEntity> selectByBizId(String bizId) {
        return this.getSqlSession().selectList(NAMESPACE + ".selectByBizId", bizId);
    }

    public int deleteByBizId(String bizId) {
        return this.getSqlSession().update(NAMESPACE + ".deleteByBizId", bizId);
    }

}
