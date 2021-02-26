package com.jd.bluedragon.distribution.newseal.dao.impl;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.jd.bluedragon.common.dao.BaseDao;
import com.jd.bluedragon.distribution.newseal.dao.DmsSendRelationDao;
import com.jd.bluedragon.distribution.newseal.entity.DmsSendRelation;
import com.jd.bluedragon.distribution.sealVehicle.domain.PassPreSealQueryRequest;
import com.jd.bluedragon.distribution.sealVehicle.domain.PassPreSealRecord;

/**
 * @ClassName: DmsSendRelationDaoImpl
 * @Description: 分拣发货关系表--Dao接口实现
 * @author wuyoude
 * @date 2020年12月31日 16:45:40
 *
 */
@Repository("dmsSendRelationDao")
public class DmsSendRelationDaoImpl extends BaseDao<DmsSendRelation> implements DmsSendRelationDao {
	public static final String namespace = DmsSendRelationDao.class.getName();
	
	@Override
	public int insert(DmsSendRelation dmsSendRelation) {
		return this.getSqlSession().insert(namespace + ".insert", dmsSendRelation);
	}

	@Override
	public int update(DmsSendRelation dmsSendRelation) {
		return this.getSqlSession().insert(namespace + ".update", dmsSendRelation);
	}

	@Override
	public DmsSendRelation queryByBusinessKey(DmsSendRelation dmsSendRelation) {
		return this.getSqlSession().selectOne(namespace + ".queryByBusinessKey",dmsSendRelation);
	}

	@Override
	public List<PassPreSealRecord> queryPassPreSealData(PassPreSealQueryRequest queryCondition) {
		return this.getSqlSession().selectList(namespace + ".queryPassPreSealData",queryCondition);
	}

	@Override
	public Integer countPassPreSealData(PassPreSealQueryRequest queryCondition) {
		return this.getSqlSession().selectOne(namespace + ".countPassPreSealData",queryCondition);
	}
}
