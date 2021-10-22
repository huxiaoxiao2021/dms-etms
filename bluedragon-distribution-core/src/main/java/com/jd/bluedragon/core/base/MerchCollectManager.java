package com.jd.bluedragon.core.base;

import com.jdl.express.collect.api.CommonDTO;
import com.jdl.express.collect.api.merchcollectwaybill.commands.notaskfinishcollectwaybill.NoTaskFinishCollectWaybillCommand;
import com.jdl.express.collect.api.merchcollectwaybill.commands.notaskfinishcollectwaybill.NoTaskFinishCollectWaybillDTO;

/**
 * 终端揽收
 *
 * @author hujiping
 * @date 2021/10/20 11:03 上午
 */
public interface MerchCollectManager {

    /**
     * 批量无任务揽收
     *
     * @param command
     * @return
     */
    CommonDTO<NoTaskFinishCollectWaybillDTO> noTaskFinishCollectWaybill(NoTaskFinishCollectWaybillCommand command);
}
