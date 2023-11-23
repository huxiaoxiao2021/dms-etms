package com.jd.bluedragon.distribution.jy.service.work;

import java.util.List;
import java.util.Map;

import com.jd.bluedragon.common.dto.base.response.JdCResponse;
import com.jd.bluedragon.common.dto.work.JyWorkGridManagerCountData;
import com.jd.bluedragon.common.dto.work.JyWorkGridManagerData;
import com.jd.bluedragon.common.dto.work.JyWorkGridManagerQueryRequest;
import com.jd.bluedragon.common.dto.work.JyWorkGridManagerTransferData;
import com.jd.bluedragon.distribution.jy.dto.work.JyBizTaskWorkGridManager;
import com.jd.bluedragon.distribution.jy.dto.work.JyBizTaskWorkGridManagerBatchUpdate;
import com.jd.bluedragon.distribution.jy.dto.work.JyBizTaskWorkGridManagerQuery;

/**
 * @ClassName: JyBizTaskWorkGridManagerService
 * @Description: 巡检任务表--Service接口
 * @author wuyoude
 * @date 2023年06月02日 10:54:36
 *
 */
public interface JyBizTaskWorkGridManagerService {

	/**
	 * 新增一条任务数据
	 * @param jyTask
	 * @return
	 */
	int addTask(JyBizTaskWorkGridManager jyTask);
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
	/**
	 * 任务分配-查询数量
	 * @param query
	 * @return
	 */
	Integer queryDataCountForDistribution(JyBizTaskWorkGridManagerQuery query);
	/**
	 * 任务分配-查询待分配数据
	 * @param query
	 * @return
	 */
	List<JyBizTaskWorkGridManager> queryDataListForDistribution(JyBizTaskWorkGridManagerQuery query);
	/**
	 * 分配任务
	 * @param distributionData
	 * @return
	 */
	int distributionTask(JyBizTaskWorkGridManagerBatchUpdate distributionData);
	/**
	 * 自动关闭任务
	 * @param closeData
	 * @return
	 */
	int autoCloseTask(JyBizTaskWorkGridManagerBatchUpdate closeData);
	/**
	 * 批次完结-关闭无效的任务
	 * @param closeData
	 * @return
	 */
	int closeTaskForEndBatch(JyBizTaskWorkGridManagerBatchUpdate closeData);
	/**
	 * 新增一条任务数据
	 * @param jyTask
	 * @return
	 */
	int batchAddTask(List<JyBizTaskWorkGridManager> taskList);

	int batchInsertDistributionTask(List<JyBizTaskWorkGridManager> taskList);

	/**
	 * 自动取消任务-由网格删除触发
	 * @param data
	 * @return
	 */
	int autoCancelTaskForGridDelete(JyBizTaskWorkGridManagerBatchUpdate data);

	void generateManageInspectionTask(String erp, String positionCode, String userName, Integer userSiteCode);

	JdCResponse<Boolean> transferCandidate(JyWorkGridManagerTransferData request);

    String selectLastHandlerErp(String taskCode, Integer siteCode);

	int updateTask4Uat(Map<String,Object> data);
}
