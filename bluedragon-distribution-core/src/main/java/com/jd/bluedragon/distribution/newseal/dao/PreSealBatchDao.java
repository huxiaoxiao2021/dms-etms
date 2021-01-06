package com.jd.bluedragon.distribution.newseal.dao;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.jd.bluedragon.distribution.newseal.entity.PreSealBatch;

/**
 * @ClassName: PreSealBatchDao
 * @Description: 预封车批次数据表--Dao接口
 * @author wuyoude
 * @date 2020年12月31日 16:45:40
 *
 */
@Repository("preSealBatchDao")
public interface PreSealBatchDao {

	int batchInsert(List<PreSealBatch> preSealBatchs);

	int batchLogicalDeleteByUuid(String preSealUuid);

	List<String> queryByUuid(String preSealUuid);
}
