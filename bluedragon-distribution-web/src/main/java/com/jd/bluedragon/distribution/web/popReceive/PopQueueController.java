package com.jd.bluedragon.distribution.web.popReceive;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.Pager;
import com.jd.bluedragon.core.base.BaseMajorManager;
import com.jd.bluedragon.distribution.api.request.PopQueueQuery;
import com.jd.bluedragon.distribution.base.service.BaseService;
import com.jd.bluedragon.distribution.popPrint.domain.PopQueue;
import com.jd.bluedragon.distribution.popPrint.service.PopQueueService;
import com.jd.bluedragon.utils.DateHelper;
import com.jd.common.web.LoginContext;
import com.jd.ql.basic.domain.BaseOrg;
import com.jd.ql.basic.dto.BaseStaffSiteOrgDto;

@Controller
@RequestMapping("/popQueueManager")
public class PopQueueController {

	@Autowired
	private PopQueueService popQueueService;

	@Autowired
	private BaseService baseService;

	@Autowired
	private BaseMajorManager baseMajorManager;
	
	@SuppressWarnings("rawtypes")
	@RequestMapping(value = "/query")
	public String query(Model model, PopQueueQuery popQueueQuery, Pager pager) {
		select(model, popQueueQuery);
		List<BaseStaffSiteOrgDto> expressList = this.baseService.getPopBaseSiteByOrgId(543);
		if (StringUtils.isEmpty(popQueueQuery.getPrintStartTime())) {
			String printStartTime = DateHelper.formatDate(new Date()) + " 00:00:00";
			String printEndTime = DateHelper.formatDate(new Date()) + " 23:59:59";
			popQueueQuery.setPrintStartTime(printStartTime);
			popQueueQuery.setPrintEndTime(printEndTime);
			model.addAttribute("expressList", expressList);
			model.addAttribute("query", popQueueQuery);
			return "popReceive/pop_queue_manage";
		}
		List<PopQueue> dataList = null;
		int count = 0;
			if (pager == null) {
				pager = new Pager(Pager.DEFAULT_PAGE_NO);
			} else {
				pager = new Pager(pager.getPageNo(), pager.getPageSize());
			}
			popQueueQuery.setStart(pager.getStartIndex());
			popQueueQuery.setEnd(pager.getEndIndex());
            popQueueQuery.setPageSize(pager.getPageSize());
			try {
				count = this.popQueueService.getCount(popQueueQuery);
			} catch (Exception e) {
				e.printStackTrace();
			}
			dataList = this.popQueueService.getPopQueueList(popQueueQuery);

		model.addAttribute("dataList", dataList);
		pager.setTotalSize(count);
		model.addAttribute("pager", pager);
		model.addAttribute("expressList", expressList);
		model.addAttribute("query", popQueueQuery);
		return "popReceive/pop_queue_manage";
	}
	
	private void select(Model model, PopQueueQuery query) {
		LoginContext loginContext = LoginContext.getLoginContext();
		Long userId = loginContext.getUserId();
		List<BaseOrg> orgList = new ArrayList<BaseOrg>();
		Integer defaultSiteCode = null;
		Integer defaultOrgId = null;
		Integer defaultSiteType = null;
		BaseStaffSiteOrgDto baseStaffSiteOrgDto = this.baseMajorManager.getBaseStaffByStaffId(userId.intValue());
		if (baseStaffSiteOrgDto != null) {
			defaultSiteCode = baseStaffSiteOrgDto.getSiteCode();
			defaultOrgId = baseStaffSiteOrgDto.getOrgId();
			defaultSiteType = baseStaffSiteOrgDto.getSiteType();
			if (defaultSiteCode != null && defaultSiteType.equals(64)) {
				model.addAttribute("defaultSiteCode", defaultSiteCode);
				model.addAttribute("defaultSiteType", defaultSiteType);
			}
			if (defaultOrgId != null) {
				model.addAttribute("defaultOrgId", defaultOrgId);
				BaseOrg baseOrg = new BaseOrg();
				baseOrg.setOrgId(defaultOrgId);
				baseOrg.setOrgName(baseStaffSiteOrgDto.getOrgName());
				orgList.add(baseOrg);
			}
		} else {

			orgList = baseService.getAllOrg();
		}
		model.addAttribute("orgList", orgList);
		List<BaseStaffSiteOrgDto> siteList = new ArrayList<BaseStaffSiteOrgDto>();
		if (defaultSiteType != null && defaultSiteType.equals( Constants.DMS_SITE_TYPE)) {
			siteList.add(baseStaffSiteOrgDto);
		} else if (defaultOrgId != null) {
			siteList = this.baseMajorManager.getBaseSiteByOrgIdSubType(defaultOrgId,Constants.DMS_SITE_TYPE);
		} else if (query.getOrgCode() != null) {
			siteList = this.baseMajorManager.getBaseSiteByOrgIdSubType(query.getOrgCode(),Constants.DMS_SITE_TYPE);
		}
		model.addAttribute("siteList", siteList);
	}
}
