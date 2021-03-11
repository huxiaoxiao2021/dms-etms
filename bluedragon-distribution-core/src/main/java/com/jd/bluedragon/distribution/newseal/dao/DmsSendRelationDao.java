package com.jd.bluedragon.distribution.newseal.dao;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.jd.bluedragon.distribution.newseal.entity.DmsSendRelation;
import com.jd.bluedragon.distribution.newseal.entity.DmsSendRelationCondition;
import com.jd.bluedragon.distribution.sealVehicle.domain.PassPreSealQueryRequest;
import com.jd.bluedragon.distribution.sealVehicle.domain.PassPreSealRecord;

/**
 * @ClassName: DmsSendRelationDao
 * @Description: 分拣发货关系表--Dao接口
 * @author wuyoude
 * @date 2020年12月31日 16:45:40
 *
 */
@Repository("dmsSendRelationDao")
public interface DmsSendRelationDao {
	int insert(DmsSendRelation dmsSendRelation);

	int update(DmsSendRelation dmsSendRelation);
	
	DmsSendRelation queryByBusinessKey(DmsSendRelation dmsSendRelation);

	List<PassPreSealRecord> queryPassPreSealData(PassPreSealQueryRequest queryCondition);

	Integer countPassPreSealData(PassPreSealQueryRequest queryCondition);
}
