package com.jd.bluedragon.distribution.jy.service.work.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.jsoup.helper.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.jd.bluedragon.common.dto.work.JyWorkGridManagerCountData;
import com.jd.bluedragon.common.dto.work.JyWorkGridManagerData;
import com.jd.bluedragon.common.dto.work.JyWorkGridManagerQueryRequest;
import com.jd.bluedragon.core.jsf.work.WorkGridManagerTaskJsfManager;
import com.jd.bluedragon.distribution.jy.dao.work.JyBizTaskWorkGridManagerDao;
import com.jd.bluedragon.distribution.jy.dto.work.JyBizTaskWorkGridManager;
import com.jd.bluedragon.distribution.jy.dto.work.JyBizTaskWorkGridManagerBatchUpdate;
import com.jd.bluedragon.distribution.jy.dto.work.JyBizTaskWorkGridManagerCount;
import com.jd.bluedragon.distribution.jy.dto.work.JyBizTaskWorkGridManagerQuery;
import com.jd.bluedragon.distribution.jy.service.work.JyBizTaskWorkGridManagerService;
import com.jd.bluedragon.dms.utils.DmsConstants;
import com.jd.jsf.gd.util.StringUtils;
import com.jdl.basic.api.domain.work.WorkGridManagerTask;
import com.jdl.basic.common.utils.Result;

/**
 * @ClassName: JyBizTaskWorkGridManagerServiceImpl
 * @Description: 巡检任务表--Service接口实现
 * @author wuyoude
 * @date 2023年06月02日 10:54:36
 *
 */
@Service("jyBizTaskWorkGridManagerService")
public class JyBizTaskWorkGridManagerServiceImpl implements JyBizTaskWorkGridManagerService {

	private static final Logger logger = LoggerFactory.getLogger(JyBizTaskWorkGridManagerServiceImpl.class);

	@Autowired
	@Qualifier("jyBizTaskWorkGridManagerDao")
	private JyBizTaskWorkGridManagerDao jyBizTaskWorkGridManagerDao;
	
	@Autowired
	@Qualifier("workGridManagerTaskJsfManager")
	private WorkGridManagerTaskJsfManager workGridManagerTaskJsfManager;

	@Override
	public JyWorkGridManagerData queryTaskDataByBizId(String bizId) {
		JyBizTaskWorkGridManager jyTaskData = jyBizTaskWorkGridManagerDao.queryByBizId(bizId);
		if(jyTaskData == null) {
			return null;
		}
		JyWorkGridManagerData taskData  = toJyWorkGridManagerData(jyTaskData);
		Result<WorkGridManagerTask> taskInfoResult = workGridManagerTaskJsfManager.queryByTaskCode(jyTaskData.getTaskCode());
		if(taskInfoResult != null 
				&& taskInfoResult.getData() != null) {
			//字段生成任务时，冗余到任务数据中
		}
		return taskData;
	}
	private JyWorkGridManagerData toJyWorkGridManagerData(JyBizTaskWorkGridManager jyTaskData) {
		JyWorkGridManagerData taskData  = new JyWorkGridManagerData();
		BeanUtils.copyProperties(jyTaskData, taskData);
		return taskData;
	}
	@Override
	public List<JyWorkGridManagerCountData> queryDataCountListForPda(JyWorkGridManagerQueryRequest query) {
		List<JyWorkGridManagerCountData> dataList = new ArrayList<>();
		if(!checkAndInitQuery(query)) {
			return dataList;
		}
		List<JyBizTaskWorkGridManagerCount> jyDataList  = jyBizTaskWorkGridManagerDao.queryDataCountListForPda(query);
		if(CollectionUtils.isEmpty(jyDataList)) {
			return dataList;
		}
		for(JyBizTaskWorkGridManagerCount jyData: jyDataList) {
			dataList.add(toJyWorkGridManagerCountData(jyData));
		}
		return dataList;
	}
	private JyWorkGridManagerCountData toJyWorkGridManagerCountData(JyBizTaskWorkGridManagerCount jyData) {
		JyWorkGridManagerCountData taskCountData  = new JyWorkGridManagerCountData();
		BeanUtils.copyProperties(jyData, taskCountData);
		return taskCountData;
	}
	@Override
	public List<JyWorkGridManagerData> queryDataListForPda(JyWorkGridManagerQueryRequest query) {
		List<JyWorkGridManagerData> dataList = new ArrayList<>();
		if(!checkAndInitQuery(query)) {
			return dataList;
		}
		List<JyBizTaskWorkGridManager> jyDataList  = jyBizTaskWorkGridManagerDao.queryDataListForPda(query);
		if(CollectionUtils.isEmpty(jyDataList)) {
			return dataList;
		}
		for(JyBizTaskWorkGridManager jyTaskData: jyDataList) {
			dataList.add(toJyWorkGridManagerData(jyTaskData));
		}
		return dataList;
	}
	private boolean checkAndInitQuery(JyWorkGridManagerQueryRequest query) {
    	if(query == null
    			|| StringUtils.isBlank(query.getOperateUserCode())) {
    		return false;
    	}
		if(query.getPageSize() == null || query.getPageSize() <= 0) {
			query.setPageSize(DmsConstants.PAGE_SIZE_DEFAULT);
		};
		query.setOffset(0);
		query.setLimit(query.getPageSize());
		if(query.getPageNumber() != null && query.getPageNumber() > 0) {
			query.setOffset((query.getPageNumber() - 1) * query.getPageSize());
		};
		query.setProcessBeginTime(new Date());
		return true;
	}
	@Override
	public int finishTask(JyBizTaskWorkGridManager updateTaskData) {
		return jyBizTaskWorkGridManagerDao.finishTask(updateTaskData);
	}
	@Override
	public int addTask(JyBizTaskWorkGridManager jyTask) {
		return jyBizTaskWorkGridManagerDao.addTask(jyTask);
	}
	@Override
	public Integer queryDataCountForDistribution(JyBizTaskWorkGridManagerQuery query) {
		return jyBizTaskWorkGridManagerDao.queryDataCountForDistribution(query);
	}
	@Override
	public List<JyBizTaskWorkGridManager> queryDataListForDistribution(JyBizTaskWorkGridManagerQuery query) {
		return jyBizTaskWorkGridManagerDao.queryDataListForDistribution(query);
	}
	@Override
	public int distributionTask(JyBizTaskWorkGridManagerBatchUpdate distributionData) {
		if(distributionData == null
				|| distributionData.getData() == null
				|| CollectionUtils.isEmpty(distributionData.getBizIdList())) {
			return 0;
		}
		return jyBizTaskWorkGridManagerDao.distributionTask(distributionData);
	}
	@Override
	public int autoCloseTask(JyBizTaskWorkGridManagerBatchUpdate closeData) {
		if(closeData == null
				|| closeData.getData() == null
				|| CollectionUtils.isEmpty(closeData.getBizIdList())) {
			return 0;
		}		
		return jyBizTaskWorkGridManagerDao.autoCloseTask(closeData);
	}
	@Override
	public int closeTaskForEndBatch(JyBizTaskWorkGridManagerBatchUpdate closeData) {
		if(closeData == null
				|| closeData.getData() == null) {
			return 0;
		}		
		return jyBizTaskWorkGridManagerDao.closeTaskForEndBatch(closeData);
	}
	@Override
	public int batchAddTask(List<JyBizTaskWorkGridManager> taskList) {
		return jyBizTaskWorkGridManagerDao.batchAddTask(taskList);
	}
	@Override
	public int autoCancelTaskForGridDelete(JyBizTaskWorkGridManagerBatchUpdate cancelData) {
		if(cancelData == null
				|| cancelData.getData() == null
				|| StringUtil.isBlank(cancelData.getData().getTaskRefGridKey())) {
			return 0;
		}
		return jyBizTaskWorkGridManagerDao.autoCancelTaskForGridDelete(cancelData);
	}
}
