package com.jd.bluedragon.distribution.rest.base;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.Pager;
import com.jd.bluedragon.common.utils.ProfilerHelper;
import com.jd.bluedragon.core.base.BaseMajorManager;
import com.jd.bluedragon.distribution.api.JdResponse;
import com.jd.bluedragon.distribution.api.request.CapacityCodeRequest;
import com.jd.bluedragon.distribution.api.response.RouteTypeResponse;
import com.jd.bluedragon.distribution.base.domain.CreateAndReceiveSiteInfo;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.base.domain.SiteWareHouseMerchant;
import com.jd.bluedragon.distribution.base.service.SiteService;
import com.jd.bluedragon.distribution.departure.domain.CapacityCodeResponse;
import com.jd.bluedragon.utils.NumberHelper;
import com.jd.bluedragon.utils.StringHelper;
import com.jd.ql.basic.dto.BaseStaffSiteOrgDto;
import com.jd.ump.profiler.CallerInfo;
import com.jd.ump.profiler.proxy.Profiler;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.resteasy.annotations.GZIP;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.text.MessageFormat;
import java.util.ArrayList;
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
		} catch (Exception e) {
			response.setCode(JdResponse.CODE_SERVICE_ERROR);
			response.setMessage(JdResponse.MESSAGE_SERVICE_ERROR);
			logger.error("通过运力编码获取基础资料信息异常：", e);
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
			return response;
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
    public InvokeResult<Pager<List<SiteWareHouseMerchant>>> getSiteByPageNo(@PathParam("category") int category,@PathParam("pageNo") int pageNo){
		CallerInfo info = Profiler.registerInfo("DMS.siteResource.getSiteByPageNo", Constants.UMP_APP_NAME_DMSWEB,false, true);
    	InvokeResult<Pager<List<SiteWareHouseMerchant>>> result=new InvokeResult<Pager<List<SiteWareHouseMerchant>>>();
        try{
            result.setData(this.siteService.getSitesByPage(category,pageNo));
        }catch (Throwable throwable){
			Profiler.functionError(info);
            logger.error(MessageFormat.format("分页获取站点数据失败{0}-{1}",category,pageNo),throwable);
            result.error("获取站点出现异常，请联系IT运维！");
        }finally {
			Profiler.registerInfoEnd(info);
		}
        return result;
    }
    /**
     * 新接口-分页获取所有站点
     * @param pageNo 当前页码
     * @param category  1：站点---2：库房---3：商家
     * @return
     */
    @GET
    @Path("/site/getSitesWithPageNo/{category}/{pageNo}")
    public InvokeResult<Pager<List<SiteWareHouseMerchant>>> getSitesWithPageNo(@PathParam("category") int category,@PathParam("pageNo") int pageNo){
        InvokeResult<Pager<List<SiteWareHouseMerchant>>> result=new InvokeResult<Pager<List<SiteWareHouseMerchant>>>();
        //按站点类型添加监控
        CallerInfo callerInfo = ProfilerHelper.registerInfo("DMS.siteResource.getSitesWithPageNo" + category);
        try{
            result.setData(this.siteService.getSitesByPage(category,pageNo));
        }catch (Throwable throwable){
            logger.error(MessageFormat.format("分页获取站点数据失败{0}-{1}",category,pageNo),throwable);
            result.error("获取站点出现异常，请联系IT运维！");
            Profiler.functionError(callerInfo);
        }finally{
        	Profiler.registerInfoEnd(callerInfo);
        }
        return result;
    }


	@GET
	@GZIP
	@Path("/site/getSitesBySendCode/{sendCode}")
	public InvokeResult<CreateAndReceiveSiteInfo> getSitesInfoBySendCode(@PathParam("sendCode") String sendCode){
		InvokeResult<CreateAndReceiveSiteInfo> result = new InvokeResult<CreateAndReceiveSiteInfo>();

		//验证sendCode
		if(StringHelper.isEmpty(sendCode)){
			logger.error("根据批次号获取始发和目的分拣信息失败，参数批次号为空");
			result.error("根据批次号获取始发和目的分拣信息失败，参数批次号为空");
			return result;
		}
		try{
			//解析批次号，获取始发分拣中心id和目的分拣中心id，0是始发，1是目的
			Integer[] siteCodes = this.siteService.getSiteCodeBySendCode(sendCode);
			if (siteCodes[0] == -1 || siteCodes[1] == -1) {
				logger.error("根据批次号获取始发和目的分拣信息失败，批次号:" + sendCode + "始发分拣code:" + siteCodes[0] + ",目的分拣Code:" + siteCodes[1]);
				result.error("根据批次号获取始发和目的分拣信息失败，批次号：" + "始发分拣code:" + siteCodes[0] + ",目的分拣Code:" + siteCodes[1]);
				return result;
			}

			//根据站点id获取站点信息，并将始发站点信息和目的站点信息映射到CreateAndReceiveSiteInfo对象中
			CreateAndReceiveSiteInfo createAndReceiveSite = new CreateAndReceiveSiteInfo();
			BaseStaffSiteOrgDto createSite = siteService.getSite(siteCodes[0]);
			BaseStaffSiteOrgDto receiveSite = siteService.getSite(siteCodes[1]);

			//始发站点信息的映射
			if(createSite != null){
				createAndReceiveSite.setCreateSiteCode(createSite.getSiteCode());
				createAndReceiveSite.setCreateSiteName(createSite.getSiteName());
				createAndReceiveSite.setCreateSiteType(createSite.getSiteType());
				createAndReceiveSite.setCreateSiteSubType(createSite.getSubType());
			}

			//目的站点信息的映射
			if(receiveSite != null){
				createAndReceiveSite.setReceiveSiteCode(receiveSite.getSiteCode());
				createAndReceiveSite.setReceiveSiteName(receiveSite.getSiteName());
				createAndReceiveSite.setReceiveSiteType(receiveSite.getSiteType());
				createAndReceiveSite.setReceiveSiteSubType(receiveSite.getSubType());
			}

			result.setMessage("success");
			result.setData(createAndReceiveSite);
		}catch (Exception e){
			logger.error("根据批次号获取始发和目的分拣信息失败，批次号：" + sendCode);
			result.error("根据批次号获取始发和目的分拣信息出现异常，请联系孔春飞");
		}

		return result;
	}

	/**
	 * 获取站点信息
	 * 根据Code精确匹配和那么模糊匹配
	 * 模糊做多返回20条
	 * @return list
     */
	@GET
	@Path("/bases/getSiteByCodeOrName/{siteCodeOrName}")
	public InvokeResult<List<BaseStaffSiteOrgDto>> getSiteByCodeOrName(@PathParam("siteCodeOrName") String siteCodeOrName) {
		InvokeResult<List<BaseStaffSiteOrgDto>> result = new InvokeResult<>();
		if (StringHelper.isEmpty(siteCodeOrName)) {
			return result;
		}
		List<BaseStaffSiteOrgDto> resList = new ArrayList<>();
		if (NumberHelper.isNumber(siteCodeOrName)) {
			/* 站点code精确匹配 */
			resList.add(baseMajorManager.queryDmsBaseSiteByCodeDmsver(siteCodeOrName));
		} else {
			/* 站点名称模糊匹配 */
			resList.addAll(siteService.fuzzyGetSiteBySiteName(siteCodeOrName));
		}
		result.setData(resList);
		return result;
	}

}
