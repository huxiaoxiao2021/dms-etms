package com.jd.bluedragon.distribution.jy.service.work;

import java.util.List;

import com.jd.bluedragon.distribution.jy.dto.work.JyWorkGridManagerCaseItem;

/**
 * @ClassName: JyWorkGridManagerCaseItemService
 * @Description: 巡检任务表-检查项明细--Service接口
 * @author wuyoude
 * @date 2023年06月02日 10:54:36
 *
 */
public interface JyWorkGridManagerCaseItemService {
	/**
	 * 查询任务下caseItem列表
	 * @param bizId
	 * @return
	 */
	List<JyWorkGridManagerCaseItem> queryItemListByBizId(String bizId);
	/**
	 * 批量插入item数据
	 * @param addCaseItem
	 * @return
	 */
	int batchInsert(List<JyWorkGridManagerCaseItem> addCaseItem);


}
