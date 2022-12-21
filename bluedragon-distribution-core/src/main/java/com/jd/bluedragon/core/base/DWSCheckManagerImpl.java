package com.jd.bluedragon.core.base;

import com.jd.bd.dms.automatic.sdk.common.dto.BaseDmsAutoJsfResponse;
import com.jd.bd.dms.automatic.sdk.common.utils.DateHelper;
import com.jd.bd.dms.automatic.sdk.modules.dwsCheck.DWSCheckJsfService;
import com.jd.bd.dms.automatic.sdk.modules.dwsCheck.dto.DWSCheckRequest;
import com.jd.bd.dms.automatic.sdk.modules.dwsCheck.dto.DwsCheckRecord;
import com.jd.bd.dms.automatic.sdk.modules.dwsCheck.dto.DwsCheckResponse;
import com.jd.bluedragon.Constants;
import com.jd.bluedragon.common.dto.operation.workbench.calibrate.DwsWeightVolumeCalibrateDetail;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.ump.profiler.CallerInfo;
import com.jd.ump.profiler.proxy.Profiler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
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
    //dws设备状态正常
    private static final int DWS_STATUS_NORMAL = 0;

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
            // data = 0 正常 1-尺寸异常、2-重量异常、3-尺寸与重量异常
            BaseDmsAutoJsfResponse<Integer> response = dmsCheckJsfService.getLatestMachineStatus(machineCode, weightTime);
            logger.info("根据设备编码:{},称重时间:{}判断是否设备称重状态返回:{}", machineCode, DateHelper.formatDateTime(weightTime),
                    JsonHelper.toJson(response));
            if(response != null && Objects.equals(response.getStatusCode(), BaseDmsAutoJsfResponse.SUCCESS_CODE)
                    && !Objects.equals(response.getData(), DWS_STATUS_NORMAL)){
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

    public DwsCheckResponse getLastDwsCheckByTime(DWSCheckRequest checkRequest){
        CallerInfo callerInfo = Profiler.registerInfo("dmsWeb.jsf.DWSCheckManager.getLastDwsCheckByTime",
                Constants.UMP_APP_NAME_DMSWEB,false,true);
        try {
            BaseDmsAutoJsfResponse<DwsCheckResponse> response = dmsCheckJsfService.getLastDwsCheckByTime(checkRequest);

            if (response != null && response.getData() != null) {
                return response.getData();
            }
        }catch (Exception e){
            logger.error("查询校验细节异常，入参{}", checkRequest, e);
            Profiler.functionError(callerInfo);
        }finally {
            Profiler.registerInfoEnd(callerInfo);
        }
        return null;
    }
}
