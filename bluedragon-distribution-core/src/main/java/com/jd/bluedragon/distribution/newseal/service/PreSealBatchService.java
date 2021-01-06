package com.jd.bluedragon.distribution.newseal.service;

import java.util.List;

import com.jd.bluedragon.distribution.newseal.entity.PreSealBatch;

/**
 * @ClassName: PreSealBatchService
 * @Description: 预封车批次数据表--Service接口
 * @author wuyoude
 * @date 2020年12月31日 16:45:40
 *
 */
public interface PreSealBatchService {

	/**
	 * 插入一条数据
	 * @param tmsVehicleRoute
	 * @return
	 */
	boolean batchInsert(List<PreSealBatch> preSealBatchs);
	/**
	 * 逻辑删除一条数据
	 * @param tmsVehicleRoute
	 * @return
	 */
	boolean batchLogicalDeleteByUuid(String preSealUuid);
	/**
	 * 根据uuid查询批次列表
	 * @param preSealUuid
	 * @return
	 */
	List<String> queryByUuid(String preSealUuid);
}
