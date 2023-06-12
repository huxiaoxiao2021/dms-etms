package com.jd.bluedragon.distribution.jy.service.work;

import java.util.List;

import com.jd.bluedragon.common.dto.work.JyWorkGridManagerCountData;
import com.jd.bluedragon.common.dto.work.JyWorkGridManagerData;
import com.jd.bluedragon.common.dto.work.JyWorkGridManagerQueryRequest;
import com.jd.bluedragon.distribution.jy.dto.work.JyBizTaskWorkGridManager;

/**
 * @ClassName: JyBizTaskWorkGridManagerService
 * @Description: 巡检任务表--Service接口
 * @author wuyoude
 * @date 2023年06月02日 10:54:36
 *
 */
public interface JyBizTaskWorkGridManagerService {
	
	/**
	 * 根据bizId查询单条数据
	 * @param bizId
	 * @return
	 */
	JyWorkGridManagerData queryTaskDataByBizId(String bizId);
	
	/**
	 * 查询状态统计列表
	 * @param query
	 * @return
	 */
	List<JyWorkGridManagerCountData> queryDataCountListForPda(JyWorkGridManagerQueryRequest query);
	/**
	 * 查询数据列表
	 * @param query
	 * @return
	 */
	List<JyWorkGridManagerData> queryDataListForPda(JyWorkGridManagerQueryRequest query);
	/**
	 * 完成任务
	 * @param updateTaskData
	 * @return
	 */
	int finishTask(JyBizTaskWorkGridManager updateTaskData);


}
