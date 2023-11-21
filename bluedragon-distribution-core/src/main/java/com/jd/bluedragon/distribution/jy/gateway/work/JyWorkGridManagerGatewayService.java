package com.jd.bluedragon.distribution.jy.gateway.work;

import com.jd.bluedragon.common.dto.base.response.JdCResponse;
import com.jd.bluedragon.common.dto.work.*;

import java.util.List;

/**
 * 任务管理--Service接口
 *
 * @author wuyoude
 * @date 2023年05月30日 14:30:43
 *
 */
public interface JyWorkGridManagerGatewayService {

	/**
	 * 按条件分页查询
	 * @param query
	 * @return
	 */
	JdCResponse<JyWorkGridManagerPageData> queryDataList(JyWorkGridManagerQueryRequest query);
	/**
	 * 根据bizId查询单条数据
	 * @param bizId
	 * @return
	 */
	JdCResponse<JyWorkGridManagerData> queryDataByBizId(String bizId);
	/**
	 * 保存数据-保存已编辑的数据，任务状态改成处理中
	 * @param data
	 * @return
	 */
	JdCResponse<Boolean> saveData(JyWorkGridManagerTaskEditRequest request);
	/**
	 * 提交数据-保存已编辑的数据，任务状态改成处理完成
	 * @param data
	 * @return
	 */
	JdCResponse<Boolean> submitData(JyWorkGridManagerTaskEditRequest request);
	/**
	 * 扫描任务岗位码，验证是否和任务对应的岗位码一致
	 * @param request
	 * @return
	 */
	JdCResponse<Boolean> scanTaskPosition(ScanTaskPositionRequest request);

	/**
	 * 任务转派
	 * @param request
	 * @return
	 */
	JdCResponse<Boolean> transferCandidate(JyWorkGridManagerTransferData request);

	/**
	 * 查询可转派责任人列表
	 * @param query
	 * @return
	 */
	JdCResponse<List<String>> queryCandidateList(JyWorkGridManagerQueryRequest query);

	/**
	 * uat 用代码不会合并到正式
	 * 根据id修改任务处理人获改成无效
	 * @param jyWorkGridManagerData
	 * @return
	 */
	JdCResponse<Boolean> updateTask4Uat(JyWorkGridManagerData jyWorkGridManagerData);
	
}
