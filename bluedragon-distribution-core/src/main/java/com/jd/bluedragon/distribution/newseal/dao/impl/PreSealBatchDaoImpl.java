package com.jd.bluedragon.distribution.newseal.dao.impl;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.jd.bluedragon.common.dao.BaseDao;
import com.jd.bluedragon.distribution.newseal.dao.PreSealBatchDao;
import com.jd.bluedragon.distribution.newseal.entity.PreSealBatch;

/**
 * @ClassName: PreSealBatchDaoImpl
 * @Description: 预封车批次数据表--Dao接口实现
 * @author wuyoude
 * @date 2020年12月31日 16:45:40
 *
 */
@Repository("preSealBatchDao")
public class PreSealBatchDaoImpl extends BaseDao<PreSealBatch> implements PreSealBatchDao {
	public static final String namespace = PreSealBatchDao.class.getName();
	
	@Override
	public int batchInsert(List<PreSealBatch> preSealBatchs) {
		return this.getSqlSession().insert(namespace + ".batchInsert", preSealBatchs);
	}

	@Override
	public int batchLogicalDeleteByUuid(String preSealUuid) {
		return this.getSqlSession().update(namespace + ".batchLogicalDeleteByUuid", preSealUuid);
	}

	@Override
	public List<String> queryByUuid(String preSealUuid) {
		return this.getSqlSession().selectList(namespace + ".queryByUuid", preSealUuid);
	}

	@Override
	public List<String> querySendCodesByUuids(List<String> preSealUuids) {
		return this.getSqlSession().selectList(namespace + ".querySendCodesByUuids", preSealUuids);
	}


}
