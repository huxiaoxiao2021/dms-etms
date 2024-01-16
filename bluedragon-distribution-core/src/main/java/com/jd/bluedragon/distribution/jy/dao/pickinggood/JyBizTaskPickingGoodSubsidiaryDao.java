package com.jd.bluedragon.distribution.jy.dao.pickinggood;


import com.jd.bluedragon.common.dao.BaseDao;
import com.jd.bluedragon.common.dto.base.request.User;
import com.jd.bluedragon.distribution.jy.pickinggood.JyBizTaskPickingGoodSubsidiaryEntity;

import java.util.Date;
import java.util.List;
import java.util.Objects;

public class JyBizTaskPickingGoodSubsidiaryDao extends BaseDao<JyBizTaskPickingGoodSubsidiaryEntity>{

    private final static String NAMESPACE = JyBizTaskPickingGoodSubsidiaryDao.class.getName();

    public int insertSelective(JyBizTaskPickingGoodSubsidiaryEntity entity) {
        return this.getSqlSession().insert(NAMESPACE + ".insertSelective", entity);
    }

    public int deleteByBusinessNumber(JyBizTaskPickingGoodSubsidiaryEntity entity) {
        return this.getSqlSession().update(NAMESPACE + ".deleteByBusinessNumber", entity);
    }

    public int batchInsert(List<JyBizTaskPickingGoodSubsidiaryEntity> entityList) {
        return this.getSqlSession().insert(NAMESPACE + ".batchInsert", entityList);
    }

    public List<String> listBizIdByLastSiteId(JyBizTaskPickingGoodSubsidiaryEntity entity) {
        return this.getSqlSession().selectList(NAMESPACE + ".listBizIdByLastSiteId", entity);
    }

    public List<JyBizTaskPickingGoodSubsidiaryEntity> listBatchInfoByBizId(List<String> bizIdList) {
        return this.getSqlSession().selectList(NAMESPACE + ".listBatchInfoByBizId", bizIdList);
    }
}