package com.jd.bluedragon.distribution.rest.cyclebox;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.distribution.api.request.WaybillCodeListRequest;
import com.jd.bluedragon.distribution.api.request.DeliveryRequest;
import com.jd.bluedragon.distribution.api.utils.JsonHelper;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.cyclebox.CycleBoxService;
import com.jd.bluedragon.distribution.cyclebox.domain.CycleBox;
import com.jd.dms.logger.annotation.BusinessLog;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;

@Controller
@Path(Constants.REST_URL)
@Consumes({MediaType.APPLICATION_JSON})
@Produces({MediaType.APPLICATION_JSON})
public class CycleBoxResource {
    private final Logger logger = Logger.getLogger(CycleBoxResource.class);

    @Autowired
    CycleBoxService cycleBoxService;

    /**
     * 快运发货获取青流箱数量
     *
     * @param request
     * @return
     */
    @POST
    @Path("/cycleBox/getCycleBoxNum")
    @BusinessLog(sourceSys = 1,bizType = 1015,operateType = 101501)
    public InvokeResult<CycleBox> getCycleBoxNum(List<DeliveryRequest> request) {
        this.logger.info("快运发货获取青流箱数量" + JsonHelper.toJson(request));
        InvokeResult<CycleBox> result = new InvokeResult<CycleBox>();
        try {
            result.setData(cycleBoxService.getCycleBoxNum(request));
        } catch (Exception e) {
            result.error(InvokeResult.SERVER_ERROR_MESSAGE);
        }

        return result;
    }

    @POST
    @Path("/cycleBox/pushClearBoxStatus")
    @BusinessLog(sourceSys = 1,bizType = 1015,operateType = 101502)
    public InvokeResult pushClearBoxStatus(WaybillCodeListRequest request) {
        InvokeResult result = new InvokeResult();
        try {
            //参数校验
            if (request == null || request.getWaybillCodeList() == null || request.getSiteCode() == null) {
                result.setCode(InvokeResult.RESULT_THIRD_ERROR_CODE);
                result.setMessage(InvokeResult.PARAM_ERROR);
                return result;
            }

            //生成任务
            cycleBoxService.addCycleBoxStatusTask(request);
        } catch (Exception e) {
            result.error(InvokeResult.SERVER_ERROR_MESSAGE);
        }
        return result;
    }

    /**
     * 通过流水号获取青流箱明细
     *
     * @param batchCode
     * @return
     */
    @GET
    @Path("/cycleBox/getCycleBoxByBatchCode/{batchCode}")
    @BusinessLog(sourceSys = 1,bizType = 1015,operateType = 101503)
    public InvokeResult<CycleBox> getCycleBoxByBatchCode(@PathParam("batchCode") String batchCode) {
        this.logger.info("通过流水号获取青流箱明细，流水号:" + batchCode);
        InvokeResult<CycleBox> result = new InvokeResult<CycleBox>();

        if (StringUtils.isBlank(batchCode)) {
            result.setCode(InvokeResult.RESULT_THIRD_ERROR_CODE);
            result.setMessage(InvokeResult.PARAM_ERROR);
            return result;
        }
        try {
            result.setData(cycleBoxService.getCycleBoxByBatchCode(batchCode));
        } catch (Exception e) {
            result.error(InvokeResult.SERVER_ERROR_MESSAGE);
        }
        return result;
    }
}
