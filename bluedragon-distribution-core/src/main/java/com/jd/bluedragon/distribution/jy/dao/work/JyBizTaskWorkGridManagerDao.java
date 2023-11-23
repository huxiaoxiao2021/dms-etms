package com.jd.bluedragon.distribution.jy.dao.work;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.jd.bluedragon.common.dao.BaseDao;
import com.jd.bluedragon.common.dto.work.JyWorkGridManagerQueryRequest;
import com.jd.bluedragon.distribution.jy.dto.work.JyBizTaskWorkGridManager;
import com.jd.bluedragon.distribution.jy.dto.work.JyBizTaskWorkGridManagerBatchUpdate;
import com.jd.bluedragon.distribution.jy.dto.work.JyBizTaskWorkGridManagerCount;
import com.jd.bluedragon.distribution.jy.dto.work.JyBizTaskWorkGridManagerQuery;
import com.jd.bluedragon.distribution.work.domain.WorkGridManagerReportQuery;

/**
 * @ClassName: JyBizTaskWorkGridManagerDao
 * @Description: 巡检任务表--Dao接口
 * @author wuyoude
 * @date 2023年06月02日 10:54:36
 *
 */
public class JyBizTaskWorkGridManagerDao extends BaseDao<JyBizTaskWorkGridManager> {

    final static String NAMESPACE = JyBizTaskWorkGridManagerDao.class.getName();

	public int addTask(JyBizTaskWorkGridManager jyTask) {
		return this.getSqlSession().insert(NAMESPACE + ".insert", jyTask);
	}
	public JyBizTaskWorkGridManager queryByBizId(String bizId) {
		return this.getSqlSession().selectOne(NAMESPACE + ".queryByBizId", bizId);
	}

	public List<JyBizTaskWorkGridManagerCount> queryDataCountListForPda(JyWorkGridManagerQueryRequest query) {
		return this.getSqlSession().selectList(NAMESPACE + ".queryDataCountListForPda", query);
	}

	public List<JyBizTaskWorkGridManager> queryDataListForPda(JyWorkGridManagerQueryRequest query) {
		return this.getSqlSession().selectList(NAMESPACE + ".queryDataListForPda", query);
	}

	public Integer queryTransferCountListForPda(JyWorkGridManagerQueryRequest query) {
		return this.getSqlSession().selectOne(NAMESPACE + ".queryTransferCountListForPda", query);
	}

	public int finishTask(JyBizTaskWorkGridManager updateTaskData) {
		return this.getSqlSession().update(NAMESPACE + ".finishTask", updateTaskData);
	}

	public List<JyBizTaskWorkGridManager> queryListForReport(WorkGridManagerReportQuery query) {
		return this.getSqlSession().selectList(NAMESPACE + ".queryListForReport", query);
	}

	public Integer queryCountForReport(WorkGridManagerReportQuery query) {
		return this.getSqlSession().selectOne(NAMESPACE + ".queryCountForReport", query);
	}
	public Integer queryDataCountForDistribution(JyBizTaskWorkGridManagerQuery query) {
		return this.getSqlSession().selectOne(NAMESPACE + ".queryDataCountForDistribution", query);
	}
	public List<JyBizTaskWorkGridManager> queryDataListForDistribution(JyBizTaskWorkGridManagerQuery query) {
		return this.getSqlSession().selectList(NAMESPACE + ".queryDataListForDistribution", query);
	}
	public int distributionTask(JyBizTaskWorkGridManagerBatchUpdate distributionData) {
		return this.getSqlSession().update(NAMESPACE + ".distributionTask", distributionData);
	}
	public int autoCloseTask(JyBizTaskWorkGridManagerBatchUpdate closeData) {
		return this.getSqlSession().update(NAMESPACE + ".autoCloseTask", closeData);
	}
	public int closeTaskForEndBatch(JyBizTaskWorkGridManagerBatchUpdate closeData) {
		return this.getSqlSession().update(NAMESPACE + ".closeTaskForEndBatch", closeData);
	}
	public int batchAddTask(List<JyBizTaskWorkGridManager> taskList) {
		return this.getSqlSession().update(NAMESPACE + ".batchInsert", taskList);
	}
	public int batchInsertDistributionTask(List<JyBizTaskWorkGridManager> taskList) {
		return this.getSqlSession().update(NAMESPACE + ".batchInsertDistributionTask", taskList);
	}
	public int autoCancelTaskForGridDelete(JyBizTaskWorkGridManagerBatchUpdate cancelData) {
		return this.getSqlSession().update(NAMESPACE + ".autoCancelTaskForGridDelete", cancelData);
	}
	public Integer selectHandlerTodayTaskCountByTaskBizType(Integer siteCode, Date startTime, String handlerErp,
															List<String> taskCodeList, String taskRefGridKey){
		Map<String, Object> param = new HashMap<>();
		param.put("siteCode", siteCode);
		param.put("startTime", startTime);
		param.put("handlerErp", handlerErp);
		param.put("taskCodeList", taskCodeList);
		param.put("taskRefGridKey", taskRefGridKey);
		return this.getSqlSession().selectOne(NAMESPACE + ".selectHandlerTodayTaskCountByTaskBizType", param);
	}

	public int transfer(JyBizTaskWorkGridManager manager) {
		return this.getSqlSession().update(NAMESPACE + ".transfer", manager);
	}
	
	public String selectLastHandlerErp(String taskCode){
		Map<String, Object> param = new HashMap<>();
		param.put("taskCode", taskCode);
		return this.getSqlSession().selectOne(NAMESPACE + ".selectLastHandlerErp", param);
	}
}
