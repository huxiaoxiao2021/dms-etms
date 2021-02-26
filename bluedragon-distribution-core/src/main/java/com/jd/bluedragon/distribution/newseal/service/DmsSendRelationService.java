package com.jd.bluedragon.distribution.newseal.service;

import java.util.List;

import com.jd.bluedragon.distribution.newseal.entity.DmsSendRelation;
import com.jd.bluedragon.distribution.newseal.entity.DmsSendRelationCondition;
import com.jd.bluedragon.distribution.sealVehicle.domain.PassPreSealQueryRequest;
import com.jd.bluedragon.distribution.sealVehicle.domain.PassPreSealRecord;

/**
 * @ClassName: DmsSendRelationService
 * @Description: 分拣发货关系表--Service接口
 * @author wuyoude
 * @date 2020年12月31日 16:45:40
 *
 */
public interface DmsSendRelationService {
	/**
	 * 保存或更新
	 * @param dmsSendRelation
	 * @return
	 */
	boolean saveOrUpdate(DmsSendRelation dmsSendRelation);
	/**
	 * 保存发货关系表
	 * @param dmsSendRelation
	 * @return
	 */
	boolean saveWithFrequency(DmsSendRelation dmsSendRelation);

	/**
	 * 查询封车看板信息
	 * @param dmsSendRelation
	 * @return
	 */
	List<PassPreSealRecord> queryPassPreSealData(PassPreSealQueryRequest queryCondition);
	/**
	 * 查询封车看板数量
	 * @param dmsSendRelation
	 * @return
	 */
	Integer countPassPreSealData(PassPreSealQueryRequest queryCondition);
}
