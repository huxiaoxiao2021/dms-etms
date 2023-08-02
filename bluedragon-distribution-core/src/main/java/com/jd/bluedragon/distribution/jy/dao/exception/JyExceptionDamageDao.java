package com.jd.bluedragon.distribution.jy.dao.exception;


import com.jd.bluedragon.common.dao.BaseDao;
import com.jd.bluedragon.distribution.jy.exception.JyExceptionDamageEntity;
import com.jd.bluedragon.distribution.jy.exception.JyExceptionScrappedPO;
import com.jd.coo.sa.mybatis.plugins.id.SequenceGenAdaptor;

import java.util.List;

/**
 * 异常-破损 DAO
 */
public class JyExceptionDamageDao extends BaseDao<JyExceptionDamageEntity> {

    final static String NAMESPACE = JyExceptionDamageDao.class.getName();
    private static final String TABLE_NAME = "jy_exception_damage";
    private SequenceGenAdaptor sequenceGenAdaptor;
    public void setSequenceGenAdaptor(SequenceGenAdaptor sequenceGenAdaptor) {
        this.sequenceGenAdaptor = sequenceGenAdaptor;
    }
    private Long generateId(){
        return sequenceGenAdaptor.newId(TABLE_NAME);
    }


    public int insertSelective(JyExceptionDamageEntity entity) {
        entity.setId(this.generateId());
        return this.getSqlSession().insert(NAMESPACE + ".insertSelective", entity);
    }

    public int updateByBizId(JyExceptionDamageEntity entity) {
        return this.getSqlSession().update(NAMESPACE + ".updateByBizId", entity);
    }


    public JyExceptionDamageEntity selectOneByBizId(String bizId) {
        return this.getSqlSession().selectOne(NAMESPACE + ".selectOneByBizId", bizId);
    }

    public List<JyExceptionScrappedPO> getTaskListOfDamage(List<String> bizIds) {
        return this.getSqlSession().selectList(NAMESPACE + ".getTaskListOfscrapped", bizIds);
    }

    public Integer getDamageCountByBarCode(String barCode){
        return this.getSqlSession().selectOne(NAMESPACE + ".getDamageCountByBarCode", barCode);
    }

}
