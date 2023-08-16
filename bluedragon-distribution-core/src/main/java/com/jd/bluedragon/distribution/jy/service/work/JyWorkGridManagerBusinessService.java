package com.jd.bluedragon.distribution.jy.service.work;

import com.jd.bluedragon.common.dto.base.response.JdCResponse;
import com.jd.bluedragon.common.dto.work.JyWorkGridManagerTaskEditRequest;
import com.jd.bluedragon.common.dto.work.ScanTaskPositionRequest;
import com.jd.bluedragon.distribution.task.domain.Task;
import com.jdl.basic.api.domain.work.WorkGridManagerTaskConfig;
import com.jdl.basic.api.domain.workStation.WorkGridModifyMqData;

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
	/**
	 * 执行自动关闭任务
	 * @param task
	 * @return
	 */
	boolean executeWorkGridManagerAutoCloseTask(Task task);
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
