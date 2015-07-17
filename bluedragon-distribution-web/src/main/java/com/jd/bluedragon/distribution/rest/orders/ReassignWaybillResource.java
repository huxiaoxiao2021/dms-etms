package com.jd.bluedragon.distribution.rest.orders;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

import com.jd.bluedragon.distribution.api.response.BaseResponse;
import com.jd.bluedragon.utils.BusinessHelper;
import com.jd.bluedragon.utils.StringHelper;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.distribution.api.JdResponse;
import com.jd.bluedragon.distribution.api.request.ReassignWaybillRequest;
import com.jd.bluedragon.distribution.reassignWaybill.domain.ReassignWaybill;
import com.jd.bluedragon.distribution.reassignWaybill.service.ReassignWaybillService;

@Controller
@Path(Constants.REST_URL)
@Consumes({ MediaType.APPLICATION_JSON })
@Produces({ MediaType.APPLICATION_JSON })
public class ReassignWaybillResource {
	private final Log logger = LogFactory.getLog(this.getClass());
	@Autowired
	ReassignWaybillService reassignWaybillService;

	@POST
	@Path("/tagPrint/returnPack")
	public JdResponse add(ReassignWaybillRequest request) {
		if (request == null || StringUtils.isBlank(request.getPackageBarcode())) {
			this.logger.error("ReturnPackTagPrintResource add --> 传入参数非法");
			return new JdResponse(JdResponse.CODE_PARAM_ERROR,
					JdResponse.MESSAGE_PARAM_ERROR);
		}
		this.logger
				.info("ReturnPackTagPrintResource add --> packageBarcode is ["
						+ request.getPackageBarcode() + "]");
		ReassignWaybill packTagPrint = ReassignWaybill.toReassignWaybill(request);
		if (reassignWaybillService.add(packTagPrint)) {
			return new JdResponse(JdResponse.CODE_OK, JdResponse.MESSAGE_OK);
		} else {
			return new JdResponse(308, "处理失败");
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
        this.logger.info("the packagecode is : " + packageCode);
        BaseResponse baseResponse = new BaseResponse();
        if(StringHelper.isEmpty(packageCode)){
            this.logger.error("获取包裹最后一次反调度站点失败，参数包裹号为空。");
            baseResponse.setCode(JdResponse.CODE_PARAM_ERROR);
            baseResponse.setMessage(JdResponse.MESSAGE_PARAM_ERROR);
            return baseResponse;
        }

        ReassignWaybill reassignWaybill = null;
        try{
        	if(BusinessHelper.isPackageCode(packageCode))//判断是否是包裹号
        		reassignWaybill = reassignWaybillService.queryByPackageCode(packageCode);
        	else                                         //否则默认按运单号处理
        		reassignWaybill = reassignWaybillService.queryByWaybillCode(packageCode);
        }catch(Exception e){
            this.logger.error("获取包裹 [" + packageCode +"] 最后一次反调度站点异常，原因：" + e);
            baseResponse.setCode(JdResponse.CODE_SERVICE_ERROR);
            baseResponse.setMessage(JdResponse.MESSAGE_SERVICE_ERROR);
            return baseResponse;
        }

        if(null == reassignWaybill){
            this.logger.error("获取包裹 [" + packageCode +"] 最后一次反调度站点失败，反调度站点为空");
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
