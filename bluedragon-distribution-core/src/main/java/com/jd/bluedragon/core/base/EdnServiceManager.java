package com.jd.bluedragon.core.base;

import java.util.List;

import com.jd.bluedragon.distribution.command.JdResult;
import com.jd.bluedragon.distribution.schedule.vo.DmsEdnBatchVo;

/**
 * 金鹏相关服务管理类
 * @author wuyoude
 *
 */
public interface EdnServiceManager {
	/**
	 * 
	 * @param ednBatchNumList
	 * @return
	 */
	JdResult<List<DmsEdnBatchVo>> batchGetDeliveryReceipt(List<String> ednBatchNumList);
}
