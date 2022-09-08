package com.jd.bluedragon.core.base;

import com.jd.bd.dms.automatic.sdk.common.dto.BaseDmsAutoJsfResponse;
import com.jd.bd.dms.automatic.sdk.modules.dwsCheck.DWSCheckJsfService;
import com.jd.bluedragon.Constants;
import com.jd.ump.profiler.CallerInfo;
import com.jd.ump.profiler.proxy.Profiler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Objects;

/**
 * dws校验包装接口
 *
 * @author hujiping
 * @date 2022/8/24 3:51 PM
 */
@Service("dwsCheckManager")
public class DWSCheckManagerImpl implements DWSCheckManager {

    private static final Logger logger = LoggerFactory.getLogger(DWSCheckManagerImpl.class);

    @Qualifier("dmsCheckJsfService")
    @Autowired
    private DWSCheckJsfService dmsCheckJsfService;

    /**
     * 校验设备称重是否准确
     *
     * @param machineCode
     * @param weightTime
     * @return
     */
    public Boolean checkDWSMachineWeightIsAccurate(String machineCode, Date weightTime) {
        CallerInfo callerInfo = Profiler.registerInfo("dmsWeb.jsf.DWSCheckManager.checkDWSMachineWeightIsAccurate",
                Constants.UMP_APP_NAME_DMSWEB,false,true);
        boolean isAccurate = true;
        try {
            BaseDmsAutoJsfResponse<Integer> response = dmsCheckJsfService.getLatestMachineStatus(machineCode, weightTime);
            if(response != null && Objects.equals(response.getStatusCode(), Constants.NUMBER_ZERO)){
                isAccurate = false;
            }
        }catch (Exception e){
            logger.error("根据设备编码:{},称重时间:{}判断是否设备称重是否合规异常!", machineCode, weightTime, e);
            Profiler.functionError(callerInfo);
        }finally {
            Profiler.registerInfoEnd(callerInfo);
        }
        return isAccurate;
    }
}
