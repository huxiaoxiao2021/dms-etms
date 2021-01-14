package com.jd.bluedragon.distribution.newseal.dao;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.jd.bluedragon.distribution.newseal.entity.DmsSendRelation;
import com.jd.bluedragon.distribution.newseal.entity.DmsSendRelationCondition;

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
	/**
	 * 根据条件查询列表
	 * @param dmsSendRelation
	 * @return
	 */
	List<DmsSendRelation> queryByCondition(DmsSendRelationCondition dmsSendRelation);
}
