package com.jd.bluedragon.distribution.jy.service.work;

import java.util.List;

import com.jd.bluedragon.common.dto.work.JyWorkGridManagerCaseData;
import com.jd.bluedragon.distribution.jy.dto.work.JyWorkGridManagerCase;
import com.jd.bluedragon.distribution.jy.dto.work.JyWorkGridManagerCaseQuery;

/**
 * @ClassName: JyWorkGridManagerCaseService
 * @Description: 巡检任务表-检查项--Service接口
 * @author wuyoude
 * @date 2023年06月02日 10:54:36
 *
 */
public interface JyWorkGridManagerCaseService {
	
	/**
	 * 加载任务下的case列表数据
	 * @param taskCaseQuery
	 * @return
	 */
	List<JyWorkGridManagerCaseData> loadCaseListForTaskData(JyWorkGridManagerCaseQuery taskCaseQuery);
	/**
	 * 批量插入数据
	 * @param addCase
	 * @return
	 */
	int batchInsert(List<JyWorkGridManagerCase> addCase);
	/**
	 * 批量更新数据
	 * @param updateCase
	 * @return
	 */
	int batchUpdate(List<JyWorkGridManagerCase> updateCase);


}
