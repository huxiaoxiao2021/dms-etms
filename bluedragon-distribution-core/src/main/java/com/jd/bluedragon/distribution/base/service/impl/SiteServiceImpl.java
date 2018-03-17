package com.jd.bluedragon.distribution.base.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.Pager;
import com.jd.bluedragon.core.base.BaseMajorManager;
import com.jd.bluedragon.core.base.BaseMinorManager;
import com.jd.bluedragon.distribution.api.JdResponse;
import com.jd.bluedragon.distribution.api.request.CapacityCodeRequest;
import com.jd.bluedragon.distribution.api.response.RouteTypeResponse;
import com.jd.bluedragon.distribution.base.domain.SiteWareHouseMerchant;
import com.jd.bluedragon.distribution.base.domain.SysConfig;
import com.jd.bluedragon.distribution.base.service.SiteService;
import com.jd.bluedragon.distribution.base.service.SysConfigService;
import com.jd.bluedragon.distribution.departure.domain.CapacityCodeResponse;
import com.jd.bluedragon.distribution.departure.domain.CapacityDomain;
import com.jd.bluedragon.utils.NumberHelper;
import com.jd.bluedragon.utils.StringHelper;
import com.jd.etms.framework.utils.cache.annotation.Cache;
import com.jd.etms.vts.dto.CommonDto;
import com.jd.etms.vts.dto.VtsTransportResourceDto;
import com.jd.etms.vts.proxy.VtsQueryWSProxy;
import com.jd.etms.vts.ws.VtsQueryWS;
import com.jd.ldop.basic.dto.BasicTraderInfoDTO;
import com.jd.ql.basic.dto.BaseStaffSiteOrgDto;


@Service("siteService")
public class SiteServiceImpl implements SiteService {
	private static final int SITE_PAGE_SIZE=1000;

	@Autowired
	private BaseMajorManager baseMajorManager;
	
	@Autowired
	private BaseMinorManager baseMinorManager;
	
	@Autowired
	private VtsQueryWS vtsQueryWS;
	
	@Autowired
	private VtsQueryWSProxy vtsQueryWSProxy;
	
	@Autowired
	private SysConfigService sysConfigService;

    private final Log logger = LogFactory.getLog(this.getClass());

	/**
	 * 批次号正则
	 */
	private static final Pattern RULE_SEND_CODE_REGEX = Pattern.compile("^[Y|y]?(\\d+)-(\\d+)-([0-9]{14,})$");

	public BaseStaffSiteOrgDto getSite(Integer siteCode) {
		return this.baseMajorManager.getBaseSiteBySiteId(siteCode);
	}

	public BasicTraderInfoDTO getTrader(Integer siteCode) {
		return this.baseMinorManager.getBaseTraderById(siteCode);
	}

	//车辆管理系统获取运力编码
	@Override
	public RouteTypeResponse getCapacityCodeInfo(String capacityCode) {
		CommonDto<VtsTransportResourceDto> vtsDto = vtsQueryWS.getTransportResourceByTransCode(capacityCode);
		RouteTypeResponse base = new RouteTypeResponse();
		if(vtsDto == null){    //JSF接口返回空
			base.setCode(JdResponse.CODE_SERVICE_ERROR);
			base.setMessage("查询运力信息结果为空:" + capacityCode);
			return base;
		}
		if(Constants.RESULT_SUCCESS == vtsDto.getCode() ){ //JSF接口调用成功
            VtsTransportResourceDto vtrd = vtsDto.getData();
            if(vtrd!=null){
                base.setSiteCode(vtrd.getEndNodeId());
                base.setSendUserType(vtrd.getTransType());
                base.setDriverId(vtrd.getCarrierId());
                base.setRouteType(vtrd.getRouteType()); // 增加运输类型返回值
                base.setDriver(vtrd.getCarrierName());
                base.setTransWay(vtrd.getTransMode());
                base.setCarrierType(vtrd.getTransType());
                base.setCode(JdResponse.CODE_OK);
                base.setMessage(JdResponse.MESSAGE_OK);
            }else{
                base.setCode(JdResponse.CODE_SERVICE_ERROR);
                base.setMessage("查询运力信息结果为空:" + capacityCode);
            }
		}else if( Constants.RESULT_WARN == vtsDto.getCode()){    //查询运力信息接口返回警告，给出前台提示
            base.setCode(JdResponse.CODE_SERVICE_ERROR);
            base.setMessage(vtsDto.getMessage());
		}else { //服务出错或者出异常，打日志
            base.setCode(JdResponse.CODE_SERVICE_ERROR);
            base.setMessage("查询运力信息出错！");
            logger.error("查询运力信息出错,出错原因:" + vtsDto.getMessage());
            logger.error("查询运力信息出错,运力编码:" + capacityCode);
		}
		return base;
	}
	
	@Override
	public CapacityCodeResponse queryCapacityCodeInfo(
			CapacityCodeRequest request) {
		CapacityCodeResponse response = new CapacityCodeResponse();
		VtsTransportResourceDto dtorequest = new VtsTransportResourceDto();
		
		response.setCode(JdResponse.CODE_OK);
		response.setMessage(JdResponse.MESSAGE_OK);
		
		try {
			// 始发区域
			if (request.getSorgid() != null)
				dtorequest.setStartOrgId(request.getSorgid());
			// 始发站
			if (request.getScode() != null)
				dtorequest.setStartNodeId(request.getScode());
			// 目的区域
			if (request.getRorgid() != null)
				dtorequest.setEndOrgId(request.getRorgid());
			// 目的站
			if (request.getRcode() != null)
				dtorequest.setEndNodeId(request.getRcode());
			// 线路类型
			if (request.getRouteType() != null)
				dtorequest.setRouteType(request.getRouteType());
			// 运力类型
			if (request.getTranType() != null)
				dtorequest.setTransType(request.getTranType());
			// 运输方式
			if (request.getTranMode() != null)
				dtorequest.setTransMode(request.getTranMode());
			// 运力编码
			if (request.getTranCode() != null)
				dtorequest.setTransCode(request.getTranCode());
			// 承运人信息
			if (request.getCarrierId() != null){
				//承运商ID
				if (NumberHelper.isNumber(request.getCarrierId()) || request.getCarrierId().equals(Constants.JDZY))
					dtorequest.setCarrierId(Integer.parseInt(request.getCarrierId()));
				//承运商名称
				else dtorequest.setCarrierName(request.getCarrierId());
			}
		
			List<VtsTransportResourceDto> result = vtsQueryWSProxy.getVtsTransportResourceDtoAll(dtorequest);

			if (result != null && !result.isEmpty()) {
				List<CapacityDomain> domainList = new ArrayList<CapacityDomain>();
				for (VtsTransportResourceDto dto : result) {
					// 返回客户端所有信息
					CapacityDomain domain = new CapacityDomain();

					// 到车时间
					domain.setArriveTime(String.valueOf(dto.getArriveCarTime()));
					// 承运商
					domain.setCarrierName(String.valueOf(dto.getCarrierName()));
					// 目的站
					domain.setRcode(String.valueOf(dto.getEndNodeId()));
                    domain.setRname(dto.getEndNodeName());
					// 目的区域
					domain.setRorgid(String.valueOf(dto.getEndOrgId()));
                    domain.setRorgName(dto.getEndOrgName());
					// 线路类型
					domain.setRouteType(String.valueOf(dto.getRouteType()));
					// 始发站
					domain.setScode(String.valueOf(dto.getStartNodeId()));
                    domain.setSname(dto.getStartNodeName());
					// 发车时间
					domain.setSendTime(String.valueOf(dto.getSendCarTime()));
					// 始发区域
					domain.setSorgid(String.valueOf(dto.getStartOrgId()));
                    domain.setSorgName(dto.getStartOrgName());
					// 运力编码
					domain.setTranCode(String.valueOf(dto.getTransCode()));
					// 运输方式
					domain.setTranMode(String.valueOf(dto.getTransMode()));
					// 运力类型
					domain.setTranType(String.valueOf(dto.getTransType()));
					// 在途时长
					domain.setTravelTime(String.valueOf(dto.getTravelTime()));
					// 承运商名称
					domain.setCarrierName(dto.getCarrierName());
					// 承运商ID
					domain.setCarrierId(String.valueOf(dto.getCarrierId()));
					//航空班次
					domain.setAirShiftName(dto.getAirShiftName());

					domainList.add(domain);
				}
				if (domainList != null && !domainList.isEmpty()) {
					response.setData(domainList);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			response.setCode(JdResponse.CODE_SERVICE_ERROR);
			response.setMessage(JdResponse.MESSAGE_SERVICE_ERROR);
		}

		return response;
	}

    @Cache(key = "SiteServiceImpl.getSitesByPage@args0-@args1",memoryEnable = false,redisExpiredTime =1000*60*5,redisEnable = true)
    @Override
    public Pager<List<SiteWareHouseMerchant>> getSitesByPage(int category, int pageNo) {
        switch (category){
            case 1:
                return baseMajorManager.getBaseSiteByPage(pageNo);
            case 2:
                return baseMajorManager.getBaseStoreInfoByPage(pageNo);
            case 3:
                return baseMajorManager.getTraderListByPage(pageNo);
            default:
                return baseMajorManager.getBaseSiteByPage(pageNo);
        }

    }

	/**
	 * 根据批次号的正则匹配始发分拣中心id和目的分拣中心id
	 * @param sendCode 批次号
	 * @return
     */
	@Override
	public Integer[] getSiteCodeBySendCode(String sendCode){
		Integer[] sites = new Integer[]{-1, -1};
		if (StringHelper.isNotEmpty(sendCode)) {
			Matcher matcher = RULE_SEND_CODE_REGEX.matcher(sendCode.trim());
			if (matcher.matches()) {
				sites[0] = Integer.valueOf(matcher.group(1));
				sites[1] = Integer.valueOf(matcher.group(2));
			}
		}
		return sites;
	}
	/**
	 * 获取属于北京的分拣中心列表
	 */
	@Cache(key = "SiteServiceImpl.getBjDmsSiteCodes",memoryEnable = true, memoryExpiredTime = 10 * 60 * 1000,redisEnable = false)
	@Override
	public Set<Integer> getBjDmsSiteCodes() {
		Set<Integer> bjDmsSiteCodes = new TreeSet<Integer>();
		List<SysConfig> bjDmsSiteConfigs = sysConfigService.getListByConfigName(Constants.SYS_CONFIG_NAME_BJ_DMS_SITE_CODES);
		if(bjDmsSiteConfigs != null && !bjDmsSiteConfigs.isEmpty()){
			String contents = bjDmsSiteConfigs.get(0).getConfigContent();
			Set<String> sites = StringHelper.splitToSet(contents, Constants.SEPARATOR_COMMA);
			for(String site:sites){
				bjDmsSiteCodes.add(Integer.valueOf(site));
			}
		}
		return bjDmsSiteCodes;
	}
}
