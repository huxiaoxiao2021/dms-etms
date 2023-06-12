package com.jd.bluedragon.distribution.jy.service.work;

import com.jd.bluedragon.common.dto.base.response.JdCResponse;
import com.jd.bluedragon.common.dto.work.JyWorkGridManagerTaskEditRequest;

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

}
