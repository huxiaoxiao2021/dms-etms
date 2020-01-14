package com.jd.bluedragon.distribution.rest.orders;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.distribution.api.JdResponse;
import com.jd.bluedragon.distribution.api.request.ReassignWaybillRequest;
import com.jd.bluedragon.distribution.api.response.BaseResponse;
import com.jd.bluedragon.distribution.command.JdResult;
import com.jd.bluedragon.distribution.reassignWaybill.domain.ReassignWaybill;
import com.jd.bluedragon.distribution.reassignWaybill.service.ReassignWaybillService;
import com.jd.bluedragon.dms.utils.WaybillUtil;
import com.jd.bluedragon.utils.StringHelper;
import com.jd.ump.profiler.CallerInfo;
import com.jd.ump.profiler.proxy.Profiler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

@Controller
@Path(Constants.REST_URL)
@Consumes({ MediaType.APPLICATION_JSON })
@Produces({ MediaType.APPLICATION_JSON })
public class ReassignWaybillResource {
	private final Logger log = LoggerFactory.getLogger(this.getClass());
	@Autowired
	ReassignWaybillService reassignWaybillService;

	@POST
	@Path("/tagPrint/returnPack")
	public JdResponse add(ReassignWaybillRequest request) {
		JdResult<Boolean> jdResult = reassignWaybillService.backScheduleAfter(request);
		if (jdResult.isSucceed()) {
			return new JdResponse(JdResponse.CODE_OK, JdResponse.MESSAGE_OK);
		} else {
			return new JdResponse(jdResult.getMessageCode(), jdResult.getMessage());
		}

	}

    /**
     *  通过包裹号查询包裹最后的反调度站点
     *  @param packageCode 包裹号
     *  @return
     * */
    @GET
    @Path("/packLastScheduleSite/{packageCode}")
    public BaseResponse queryLastScheduleSite(@PathParam("packageCode") String packageCode){
        this.log.info("the packagecode is : {}", packageCode);
        BaseResponse baseResponse = new BaseResponse();
        if(StringHelper.isEmpty(packageCode)){
            this.log.warn("获取包裹最后一次反调度站点失败，参数包裹号为空。");
            baseResponse.setCode(JdResponse.CODE_PARAM_ERROR);
            baseResponse.setMessage(JdResponse.MESSAGE_PARAM_ERROR);
            return baseResponse;
        }

        CallerInfo info = Profiler.registerInfo("DMSWEB.ReassignWaybillResource.queryLastScheduleSite", Constants.UMP_APP_NAME_DMSWEB,false, true);
        ReassignWaybill reassignWaybill = null;
        try{
        	if(WaybillUtil.isPackageCode(packageCode))//判断是否是包裹号
        		reassignWaybill = reassignWaybillService.queryByPackageCode(packageCode);
        	else                                         //否则默认按运单号处理
        		reassignWaybill = reassignWaybillService.queryByWaybillCode(packageCode);
        }catch(Exception e){
            Profiler.functionError(info);
            this.log.error("获取包裹 [{}] 最后一次反调度站点异常，原因：",packageCode, e);
            baseResponse.setCode(JdResponse.CODE_SERVICE_ERROR);
            baseResponse.setMessage(JdResponse.MESSAGE_SERVICE_ERROR);
            return baseResponse;
        }finally {
            Profiler.registerInfoEnd(info);
        }

        if(null == reassignWaybill){
            this.log.warn("获取包裹 [{}] 最后一次反调度站点失败，反调度站点为空",packageCode);
            baseResponse.setCode(JdResponse.CODE_PACKAGE_ERROR);
            baseResponse.setMessage(JdResponse.MESSAGE_PACKAGE_ERROR);
            return baseResponse;
        }

        baseResponse.setCode(JdResponse.CODE_OK);
        baseResponse.setMessage(JdResponse.MESSAGE_OK);
        baseResponse.setSiteCode(reassignWaybill.getChangeSiteCode());
        baseResponse.setSiteName(reassignWaybill.getChangeSiteName());
        return baseResponse;
    }
}
