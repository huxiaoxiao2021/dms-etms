package com.jd.bluedragon.common.dto.work;

import java.io.Serializable;

import com.jd.bluedragon.common.dto.base.request.BaseReq;

/**
 * @ClassName: JyWorkGridManagerQueryRequest
 * @Description: 任务管理-查询请求
 * @author wuyoude
 * @date 2023年05月30日 11:01:53
 *
 */
public class ScanTaskPositionRequest extends BaseReq implements Serializable {

	private static final long serialVersionUID = 1L;
	/**
	 * 扫描的网格码
	 */
	private String scanPositionCode;
	/**
	 * 任务-网格key
	 */
	private String taskRefGridKey;
	
	

	public String getScanPositionCode() {
		return scanPositionCode;
	}

	public void setScanPositionCode(String scanPositionCode) {
		this.scanPositionCode = scanPositionCode;
	}

	public String getTaskRefGridKey() {
		return taskRefGridKey;
	}

	public void setTaskRefGridKey(String taskRefGridKey) {
		this.taskRefGridKey = taskRefGridKey;
	}
	
}
