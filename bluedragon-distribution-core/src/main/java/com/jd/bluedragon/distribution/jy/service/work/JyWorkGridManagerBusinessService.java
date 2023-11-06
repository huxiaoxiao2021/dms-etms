package com.jd.bluedragon.distribution.jy.service.work;

import com.jd.bluedragon.common.dto.base.response.JdCResponse;
import com.jd.bluedragon.common.dto.work.JyWorkGridManagerTaskEditRequest;
import com.jd.bluedragon.common.dto.work.ScanTaskPositionRequest;
import com.jd.bluedragon.distribution.jy.dto.work.JyBizTaskWorkGridManager;
import com.jd.bluedragon.distribution.jy.dto.work.TaskWorkGridManagerAutoCloseData;
import com.jd.bluedragon.distribution.jy.dto.work.TaskWorkGridManagerSiteScanData;
import com.jd.bluedragon.distribution.task.domain.Task;
import com.jd.ql.basic.dto.BaseSiteInfoDto;
import com.jdl.basic.api.domain.user.JyUserDto;
import com.jdl.basic.api.domain.work.WorkGridManagerTask;
import com.jdl.basic.api.domain.work.WorkGridManagerTaskConfig;
import com.jdl.basic.api.domain.work.WorkGridManagerTaskConfigVo;
import com.jdl.basic.api.domain.workStation.WorkGrid;
import com.jdl.basic.api.domain.workStation.WorkGridModifyMqData;
import com.jdl.basic.api.enums.WorkGridManagerTaskBizType;

import java.util.Date;

/**
 * @ClassName: JyWorkGridManagerBusinessService
 * @Description: 巡检任务表--Service接口
 * @author wuyoude
 * @date 2023年06月02日 10:54:36
 *
 */
public interface JyWorkGridManagerBusinessService {

	JdCResponse<Boolean> submitData(JyWorkGridManagerTaskEditRequest request);

	JdCResponse<Boolean> saveData(JyWorkGridManagerTaskEditRequest request);
	/**
	 * 开启-任务线上化配置扫描任务
	 * @param workGridManagerTaskConfig
	 */
	void startWorkGridManagerScanTask(WorkGridManagerTaskConfig workGridManagerTaskConfig);
	/**
	 * 执行任务
	 * @param task
	 * @return
	 */
	boolean executeWorkGridManagerScanTask(Task task);
	/**
	 * 执行场地维度的任务
	 * @param task
	 * @return
	 */
	boolean executeWorkGridManagerSiteScanTask(Task task);

	void sendTimeLineNotice(WorkGridManagerTaskBizType type, JyUserDto user);

	JyBizTaskWorkGridManager initJyBizTaskWorkGridManager(BaseSiteInfoDto siteInfo, TaskWorkGridManagerSiteScanData taskWorkGridManagerScan,
														  WorkGridManagerTask taskInfo, WorkGridManagerTaskConfigVo configData,
														  WorkGrid grid, Date curDate);

	/**
	 * 执行自动关闭任务
	 * @param task
	 * @return
	 */
	boolean executeWorkGridManagerAutoCloseTask(Task task);

    void addWorkGridManagerAutoCloseTask(TaskWorkGridManagerAutoCloseData taskData);

    /**
	 * 扫描岗位码操作
	 * @param request
	 * @return
	 */
	JdCResponse<Boolean> scanTaskPosition(ScanTaskPositionRequest request);
	/**
	 * 处理网格数据变更的任务数据
	 * @param workGridModifyMqData
	 * @return
	 */
	boolean dealWorkGridModifyTask(WorkGridModifyMqData workGridModifyMqData);
}
