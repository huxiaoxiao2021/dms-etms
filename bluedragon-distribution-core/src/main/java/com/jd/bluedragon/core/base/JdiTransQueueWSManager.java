package com.jd.bluedragon.core.base;

import com.jd.tms.jdi.dto.CommonDto;
import com.jd.tms.jdi.param.TransWorkItemQueueCallParam;

public interface JdiTransQueueWSManager {

    /**
     * 派车任务明细叫号
     * @param param
     * @return
     */
    CommonDto<String> callByWorkItem(TransWorkItemQueueCallParam param);

}
