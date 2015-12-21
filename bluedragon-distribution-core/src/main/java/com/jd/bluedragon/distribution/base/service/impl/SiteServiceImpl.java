package com.jd.bluedragon.distribution.base.service.impl;

import com.jd.bluedragon.core.base.BaseMajorManager;
import com.jd.bluedragon.core.base.BaseMinorManager;
import com.jd.bluedragon.distribution.api.JdResponse;
import com.jd.bluedragon.distribution.api.request.CapacityCodeRequest;
import com.jd.bluedragon.distribution.api.response.RouteTypeResponse;
import com.jd.bluedragon.distribution.base.service.SiteService;
import com.jd.bluedragon.distribution.departure.domain.CapacityCodeResponse;
import com.jd.bluedragon.distribution.departure.domain.CapacityDomain;
import com.jd.bluedragon.utils.NumberHelper;
import com.jd.ql.basic.domain.BaseResult;
import com.jd.ql.basic.domain.MainBranchSchedule;
import com.jd.ql.basic.dto.BaseStaffSiteOrgDto;
import com.jd.ql.basic.dto.BaseTradeInfoDto;


@Service("siteService")
public class SiteServiceImpl implements SiteService {
	
	@Autowired
	private BaseMajorManager baseMajorManager;
	
	@Autowired
	private BaseMinorManager baseMinorManager;

	public BaseStaffSiteOrgDto getSite(Integer siteCode) {
		return this.baseMajorManager.getBaseSiteBySiteId(siteCode);
	}

	public BaseTradeInfoDto getTrader(Integer siteCode) {
		return this.baseMinorManager.getBaseTraderById(siteCode);
	}

	@Override
	public RouteTypeResponse getCapacityCodeInfo(String capacityCode) {
		MainBranchSchedule schedule = baseMinorManager.getMainBranchScheduleByTranCode(capacityCode);
        RouteTypeResponse base = new RouteTypeResponse();
		if(schedule!=null){
			base.setSiteCode(schedule.getDesSiteId());
			base.setSendUserType(schedule.getTranType());
			base.setDriverId(schedule.getCarrierId());
            base.setRouteType(schedule.getRouteType()); // 增加运输类型返回值
			BaseStaffSiteOrgDto dto = baseMajorManager.getBaseSiteBySiteId(schedule.getCarrierId());
			if(dto!=null)
				base.setDriver(dto.getSiteName());
		}
		return base;
	}
	
	@Override
	public MainBranchSchedule getByCapacityCodeInfo(String capacityCode) {
		MainBranchSchedule schedule = baseMinorManager.getMainBranchScheduleByTranCode(capacityCode);
		return schedule;
	}

	@Override
	public CapacityCodeResponse queryCapacityCodeInfo(
			CapacityCodeRequest request) {
		CapacityCodeResponse response = new CapacityCodeResponse();
		MainBranchSchedule mbs = new MainBranchSchedule();
		
		//默认返回ok
		response.setCode(JdResponse.CODE_OK);
		response.setMessage(JdResponse.MESSAGE_OK);
		
		try {
			// 始发区域
			if (request.getSorgid() != null)
				mbs.setOriOrgId(request.getSorgid());
			// 始发站
			if (request.getScode() != null)
				mbs.setOriSiteId(request.getScode());
			// 目的区域
			if (request.getRorgid() != null)
				mbs.setDesOrgId(request.getRorgid());
			// 目的站
			if (request.getRcode() != null)
				mbs.setDesSiteId(request.getRcode());
			// 线路类型
			if (request.getRouteType() != null)
				mbs.setRouteType(request.getRouteType());
			// 运力类型
			if (request.getTranType() != null)
				mbs.setTranType(request.getTranType());
			// 运输方式
			if (request.getTranMode() != null)
				mbs.setTranMode(request.getTranMode());
			// 运力编码
			if (request.getTranCode() != null)
				mbs.setTranCode(request.getTranCode());
			// 承运人信息
			if (request.getCarrierId() != null){
				//承运商ID
				if (NumberHelper.isNumber(request.getCarrierId()))
					mbs.setCarrierId(Integer.parseInt(request.getCarrierId()));
				//承运商名称
				else mbs.setCarrierName(request.getCarrierId());
			}
		
			// getMainBranchScheduleList
			//基础资料接口实现人李文江
			BaseResult<List<MainBranchSchedule>> result = baseMinorManager
					.getMainBranchScheduleList(mbs);

			if (result != null) {
				List<MainBranchSchedule> list = result.getData();
				if (list != null && !list.isEmpty()) {
					List<CapacityDomain> domainList = new ArrayList<CapacityDomain>();
					for (MainBranchSchedule tmbs : list) {
						//返回客户端所有信息
						CapacityDomain domain = new CapacityDomain();
						
						//到车时间
						domain.setArriveTime(String.valueOf(tmbs.getArriveTime()));
						//承运商
						domain.setCarrierName(String.valueOf(tmbs.getCarrierName()));
						//目的站
						domain.setRcode(String.valueOf(tmbs.getDesSiteId()));
						//目的区域
						domain.setRorgid(String.valueOf(tmbs.getDesOrgId()));
						//线路类型
						domain.setRouteType(String.valueOf(tmbs.getRouteType()));
						//始发站
						domain.setScode(String.valueOf(tmbs.getOriSiteId()));
						//发车时间
						domain.setSendTime(String.valueOf(tmbs.getSendTime()));
						//始发区域
						domain.setSorgid(String.valueOf(tmbs.getOriOrgId()));
						//运力编码
						domain.setTranCode(String.valueOf(tmbs.getTranCode()));
						//运输方式
						domain.setTranMode(String.valueOf(tmbs.getTranMode()));
						//运力类型
						domain.setTranType(String.valueOf(tmbs.getTranType()));
						//在途时长
						domain.setTravelTime(String.valueOf(tmbs.getTravelTime()));
						//承运商名称
						domain.setCarrierName(tmbs.getCarrierName());
						//承运商ID
						domain.setCarrierId(String.valueOf(tmbs.getCarrierId()));
						
						domainList.add(domain);
					}
					if (domainList != null && !domainList.isEmpty()) {
						response.setData(domainList);
					}
				}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			response.setCode(JdResponse.CODE_SERVICE_ERROR);
			response.setMessage(JdResponse.MESSAGE_SERVICE_ERROR);
		}

		return response;
	}
	
}
