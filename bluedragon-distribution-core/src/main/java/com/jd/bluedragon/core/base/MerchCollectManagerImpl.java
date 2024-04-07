package com.jd.bluedragon.core.base;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.ump.profiler.CallerInfo;
import com.jd.ump.profiler.proxy.Profiler;
import com.jdl.express.collect.api.CommonDTO;
import com.jdl.express.collect.api.merchcollectwaybill.MerchCollectWaybillApi;
import com.jdl.express.collect.api.merchcollectwaybill.commands.notaskcollectwaybillterminate.NoTaskCollectWaybillTerminateCommand;
import com.jdl.express.collect.api.merchcollectwaybill.commands.notaskcollectwaybillterminate.NoTaskCollectWaybillTerminateDTO;
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

    // 揽收来源：14表示分拣系统
    private static final Integer COLLECT_SYSTEM_SOURCE = 14;

    @Autowired
    private MerchCollectWaybillApi merchCollectWaybillApi;

    @Override
    public CommonDTO<NoTaskFinishCollectWaybillDTO> noTaskFinishCollectWaybill(NoTaskFinishCollectWaybillCommand command) {

        CallerInfo callerInfo = Profiler.registerInfo("dmsWeb.jsf.MerchCollectManager.noTaskFinishCollectWaybill",
                Constants.UMP_APP_NAME_DMSWEB,false,true);
        try {
            if(command.getSystemSource() == null){
                command.setSystemSource(COLLECT_SYSTEM_SOURCE);    
            }
            if(logger.isInfoEnabled()){
                logger.info("调用终端揽收接口入参:{}", JsonHelper.toJson(command));
            }
            CommonDTO<NoTaskFinishCollectWaybillDTO> collectResult = merchCollectWaybillApi.noTaskFinishCollectWaybill(command);
            if(logger.isInfoEnabled()){
                logger.info("调用终端揽收接口出参:{}", JsonHelper.toJson(collectResult));
            }
            return collectResult;
        }catch (Exception e){
            logger.error("根据条件:{}操作无任务揽收异常!", JsonHelper.toJson(command), e);
            Profiler.functionError(callerInfo);
        }finally {
            Profiler.registerInfoEnd(callerInfo);
        }
        return null;
    }

    @Override
    public CommonDTO<NoTaskCollectWaybillTerminateDTO> terminateNoTaskCollectWaybill(NoTaskCollectWaybillTerminateCommand command) {
        CallerInfo callerInfo = Profiler.registerInfo("dmsWeb.jsf.MerchCollectManager.terminateNoTaskCollectWaybill",
                Constants.UMP_APP_NAME_DMSWEB,false,true);
        try {
            command.setSystemSource(COLLECT_SYSTEM_SOURCE);
            if(logger.isInfoEnabled()){
                logger.info("单号:{}调用终端终止揽收接口入参:{}", command.getWaybillCode(), JsonHelper.toJson(command));
            }
            CommonDTO<NoTaskCollectWaybillTerminateDTO> terminateResult = merchCollectWaybillApi.terminateNoTaskCollectWaybill(command);
            if(logger.isInfoEnabled()){
                logger.info("单号:{}调用终端终止揽收接口结果:{}", command.getWaybillCode(), JsonHelper.toJson(terminateResult));
            }
            return terminateResult;
        }catch (Exception e){
            logger.error("根据条件:{}操作终止揽收异常!", JsonHelper.toJson(command), e);
            Profiler.functionError(callerInfo);
        }finally {
            Profiler.registerInfoEnd(callerInfo);
        }
        return null;
    }
}
