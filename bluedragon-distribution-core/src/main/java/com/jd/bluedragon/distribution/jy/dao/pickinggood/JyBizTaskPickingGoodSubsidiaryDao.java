package com.jd.bluedragon.distribution.jy.dao.pickinggood;


import com.jd.bluedragon.common.dao.BaseDao;
import com.jd.bluedragon.common.dto.base.request.User;
import com.jd.bluedragon.distribution.jy.pickinggood.JyBizTaskPickingGoodSubsidiaryEntity;

import java.util.Date;
import java.util.List;
import java.util.Objects;

public class JyBizTaskPickingGoodSubsidiaryDao extends BaseDao<JyBizTaskPickingGoodSubsidiaryEntity>{

    private final static String NAMESPACE = JyBizTaskPickingGoodDao.class.getName();

    public int insertSelective(JyBizTaskPickingGoodSubsidiaryEntity entity) {
        return this.getSqlSession().insert(NAMESPACE + ".insertSelective", entity);
    }

    public int deleteByBizId(String bizId, User user) {
        JyBizTaskPickingGoodSubsidiaryEntity entity = new JyBizTaskPickingGoodSubsidiaryEntity(bizId);
        entity.setUpdateTime(new Date());
        if(Objects.isNull(user)) {
            entity.setUpdateUserErp(user.getUserErp());
            entity.setUpdateUserName(user.getUserName());
        }
        return this.getSqlSession().update(NAMESPACE + ".deleteByBizId", bizId);
    }

    public int batchInsert(List<JyBizTaskPickingGoodSubsidiaryEntity> entityList) {
        return this.getSqlSession().insert(NAMESPACE + ".batchInsert", entityList);
    }
}