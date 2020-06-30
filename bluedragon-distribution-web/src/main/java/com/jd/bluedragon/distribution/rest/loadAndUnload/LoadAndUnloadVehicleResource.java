package com.jd.bluedragon.distribution.rest.loadAndUnload;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.loadAndUnload.UnloadCarDetailScanResult;
import com.jd.bluedragon.distribution.loadAndUnload.UnloadCarScanRequest;
import com.jd.bluedragon.distribution.loadAndUnload.UnloadCarScanResult;
import com.jd.bluedragon.distribution.loadAndUnload.service.UnloadCarService;
import com.jd.bluedragon.dms.utils.WaybillUtil;
import com.jd.dms.logger.annotation.BusinessLog;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.List;

/**
 * 装卸车REST接口
 *
 * @author: hujiping
 * @date: 2020/6/23 10:19
 */
@Component
@Path(Constants.REST_URL)
@Consumes({ MediaType.APPLICATION_JSON })
@Produces({ MediaType.APPLICATION_JSON })
public class LoadAndUnloadVehicleResource {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private UnloadCarService unloadCarService;

    /**
     * 获取卸车任务
     * @param sealCarCode 封车编码
     * @return
     */
    @GET
    @Path("/unload/getUnloadCar/{sealCarCode}")
    public InvokeResult<UnloadCarScanResult> getUnloadCar(@PathParam("sealCarCode") String sealCarCode) {
        InvokeResult<UnloadCarScanResult> result = new InvokeResult<UnloadCarScanResult>();
        if(StringUtils.isEmpty(sealCarCode)){
            result.parameterError("封车编码不存在!");
            return result;
        }
        return unloadCarService.getUnloadCarBySealCarCode(sealCarCode);
    }

    /**
     * 卸车扫描
     * @param unloadCarScanRequest
     * @return
     */
    @POST
    @Path("/unload/barCodeScan")
    @BusinessLog(sourceSys = Constants.BUSINESS_LOG_SOURCE_SYS_DMSWEB,bizType = 1016,operateType = 101601)
    public InvokeResult<UnloadCarScanResult> barCodeScan(UnloadCarScanRequest unloadCarScanRequest) {
        InvokeResult<UnloadCarScanResult> result = new InvokeResult<UnloadCarScanResult>();
        if(unloadCarScanRequest == null ||
                (!WaybillUtil.isWaybillCode(unloadCarScanRequest.getBarCode())
                        && !WaybillUtil.isPackageCode(unloadCarScanRequest.getBarCode()))){
            result.parameterError(InvokeResult.PARAM_ERROR);
            return result;
        }
        return unloadCarService.barCodeScan(unloadCarScanRequest);
    }

    /**
     * 获取卸车扫描任务明细
     * @param sealCarCode 封车编码
     * @return
     */
    @GET
    @Path("/unload/getUnloadCarDetail/{sealCarCode}")
    public InvokeResult<List<UnloadCarDetailScanResult>> getUnloadCarDetail(@PathParam("sealCarCode") String sealCarCode) {
        InvokeResult<List<UnloadCarDetailScanResult>> result = new InvokeResult<List<UnloadCarDetailScanResult>>();
        if(StringUtils.isEmpty(sealCarCode)){
            result.parameterError("封车编码不存在!");
            return result;
        }
        return unloadCarService.searchUnloadDetail(sealCarCode);
    }

}
