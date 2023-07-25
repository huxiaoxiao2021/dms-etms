package com.jd.bluedragon.core.jsf.work;

import java.util.List;

import com.jdl.basic.api.domain.work.WorkGridManagerCaseWithItem;

/**
 * 
 * @author wuyoude
 *
 */
public interface WorkGridManagerCaseJsfManager {

    /**
     * 根据taskCode查询
     * @param taskCode
     * @return
     */
	List<WorkGridManagerCaseWithItem> queryCaseWithItemListByTaskCode(String taskCode);

}
