package com.jd.bluedragon.core.base;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.ump.profiler.CallerInfo;
import com.jd.ump.profiler.proxy.Profiler;
import com.jdl.express.collect.api.CommonDTO;
import com.jdl.express.collect.api.merchcollectwaybill.MerchCollectWaybillApi;
import com.jdl.express.collect.api.merchcollectwaybill.commands.notaskfinishcollectwaybill.NoTaskFinishCollectWaybillCommand;
import com.jdl.express.collect.api.merchcollectwaybill.commands.notaskfinishcollectwaybill.NoTaskFinishCollectWaybillDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 终端揽收
 *
 * @author hujiping
 * @date 2021/10/20 11:08 上午
 */
@Service("merchCollectManager")
public class MerchCollectManagerImpl implements MerchCollectManager {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private MerchCollectWaybillApi merchCollectWaybillApi;

    @Override
    public CommonDTO<NoTaskFinishCollectWaybillDTO> noTaskFinishCollectWaybill(NoTaskFinishCollectWaybillCommand command) {

        CallerInfo callerInfo = Profiler.registerInfo("dmsWeb.jsf.MerchCollectManager.noTaskFinishCollectWaybill",
                Constants.UMP_APP_NAME_DMSWEB,false,true);
        try {
            return merchCollectWaybillApi.noTaskFinishCollectWaybill(command);
        }catch (Exception e){
            logger.error("根据条件:{}操作无任务揽收异常!", JsonHelper.toJson(command), e);
            Profiler.functionError(callerInfo);
        }finally {
            Profiler.registerInfoEnd(callerInfo);
        }
        return null;
    }
}
