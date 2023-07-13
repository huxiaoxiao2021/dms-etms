package com.jd.bluedragon.core.jsf.work;

import com.jdl.basic.api.domain.work.WorkGridManagerTaskConfigVo;
import com.jdl.basic.common.utils.Result;

/**
 * 
 * @author wuyoude
 *
 */
public interface WorkGridManagerTaskConfigJsfManager {

    /**
     * 根据taskConfigCode查询
     * @param taskConfigCode
     * @return
     */
	Result<WorkGridManagerTaskConfigVo> queryByTaskConfigCode(String taskConfigCode);

}
