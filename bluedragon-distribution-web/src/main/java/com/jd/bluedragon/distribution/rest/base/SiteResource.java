package com.jd.bluedragon.distribution.rest.base;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.jd.bluedragon.Pager;
import com.jd.bluedragon.core.base.BaseMajorManager;
import com.jd.bluedragon.distribution.api.response.RouteTypeResponse;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.base.domain.SiteWareHouseMerchant;
import com.jd.ldop.basic.dto.BasicTraderInfoDTO;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.resteasy.annotations.GZIP;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.distribution.api.JdResponse;
import com.jd.bluedragon.distribution.api.request.CapacityCodeRequest;
import com.jd.bluedragon.distribution.base.service.SiteService;
import com.jd.bluedragon.distribution.departure.domain.CapacityCodeResponse;
import com.jd.ql.basic.dto.BaseStaffSiteOrgDto;
import com.jd.ql.basic.dto.BaseTradeInfoDto;

import java.text.MessageFormat;
import java.util.List;

@Component
@Path(Constants.REST_URL)
@Consumes({ MediaType.APPLICATION_JSON })
@Produces({ MediaType.APPLICATION_JSON })
public class SiteResource {

	@Autowired
	private SiteService siteService;

    @Autowired
    private BaseMajorManager baseMajorManager;

	private final Log logger = LogFactory.getLog(this.getClass());

	@GET
	@GZIP
	@Path("/site/{siteCode}")
	public BaseStaffSiteOrgDto getSite(@PathParam("siteCode") Integer siteCode) {
		return this.siteService.getSite(siteCode);
	}
	
	@GET
	@GZIP
	@Path("/trader/{siteCode}")
	public BasicTraderInfoDTO getTrader(@PathParam("siteCode") Integer siteCode) {
		return this.siteService.getTrader(siteCode);
	}
	
	/** 
	 * 通过运力编码获取基础资料信息
	 * 3/19
	 * cyk
	 * 
	 * */
	@GET
	@Path("/bases/capacityCode/{capacityCode}")
	public RouteTypeResponse getCapacityCodeInfo(@PathParam("capacityCode") String capacityCode) {
		this.logger.info("capacityCode is " + capacityCode);
        RouteTypeResponse response = new RouteTypeResponse();
		try {
			response = siteService.getCapacityCodeInfo(capacityCode);
			response.setCode(JdResponse.CODE_OK);
			response.setMessage(JdResponse.MESSAGE_OK);
		} catch (Exception e) {
			response.setCode(JdResponse.CODE_SERVICE_ERROR);
			response.setMessage(JdResponse.MESSAGE_SERVICE_ERROR);
			e.printStackTrace();
		}
		
		return response;
	}
	/** 
	 * 通过查询条件获取运力编码信息
	 * 14/5/7
	 * cyk
	 * 
	 * */
	@POST
	@Path("/bases/querycapacitylist")
	public CapacityCodeResponse queryCapacityCodeInfo(CapacityCodeRequest request) {
		
		CapacityCodeResponse response = new CapacityCodeResponse();
		//校验必要参数是否齐全
		if(request == null){
			//参数不全返回
			response.setCode(JdResponse.CODE_PARAM_ERROR);
			response.setMessage(JdResponse.MESSAGE_PARAM_ERROR);
		}
		//接口实现
			response = siteService.queryCapacityCodeInfo(request);
		
		return response;
	}

    /**
     * 获取VER站点测试
     * @param siteCode
     * @return
     */
    @GET
    @Path("/bases/siteString/{siteCode}")
    public BaseStaffSiteOrgDto getSiteString(@PathParam("siteCode") String siteCode) {
        return this.baseMajorManager.queryDmsBaseSiteByCodeDmsver(siteCode);
    }

    /**
     * 分页获取所有站点
     * @param pageNo 页号
     * @param category  1：站点---2：库房---3：商家
     * @return
     */
    @GET
    @Path("/site/siteWareHourceMerchantByPage/{category}/{pageNo}")
    @JProfiler(jKey = "DMS.siteResource.getSiteByPageNo", mState = {JProEnum.TP, JProEnum.FunctionError})
    public InvokeResult<Pager<List<SiteWareHouseMerchant>>> getSiteByPageNo(@PathParam("category") int category,@PathParam("pageNo") int pageNo){
        InvokeResult<Pager<List<SiteWareHouseMerchant>>> result=new InvokeResult<Pager<List<SiteWareHouseMerchant>>>();
        try{
            result.setData(this.siteService.getSitesByPage(category,pageNo));
        }catch (Throwable throwable){
            logger.error(MessageFormat.format("分页获取站点数据失败{0}-{1}",category,pageNo),throwable);
            result.error("获取站点出现异常，请联系孔春飞");
        }
        return result;
    }


}
