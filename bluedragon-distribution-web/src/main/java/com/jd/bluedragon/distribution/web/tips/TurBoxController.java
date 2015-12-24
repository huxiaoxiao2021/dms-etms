package com.jd.bluedragon.distribution.web.tips;

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
import com.jd.bluedragon.distribution.base.service.BaseService;
import com.jd.bluedragon.distribution.receive.domain.TurnoverBox;
import com.jd.bluedragon.distribution.receive.service.TurnoverBoxService;
import com.jd.bluedragon.utils.DateHelper;
import com.jd.common.web.LoginContext;
import com.jd.ql.basic.domain.BaseOrg;
import com.jd.ql.basic.dto.BaseStaffSiteOrgDto;

@Controller
@RequestMapping("/turBoxManager")
public class TurBoxController {

	@Autowired
	private TurnoverBoxService turnoverBoxService;

	@Autowired
	private BaseService baseService;

	@Autowired
	private BaseMajorManager baseMajorManager;
	
	@SuppressWarnings("rawtypes")
	@RequestMapping(value = "/query")
	public String query(Model model, TurnoverBox turnoverBox, Pager pager) {
		select(model, turnoverBox);
		if (StringUtils.isEmpty(turnoverBox.getPrintStartTime())) {
			String printStartTime = DateHelper.formatDate(new Date()) + " 00:00:00";
			String printEndTime = DateHelper.formatDate(new Date()) + " 23:59:59";
			turnoverBox.setPrintStartTime(printStartTime);
			turnoverBox.setPrintEndTime(printEndTime);
			model.addAttribute("query", turnoverBox);
			return "tips/turbox";
		}
		List<TurnoverBox> dataList = null;
		int count = 0;
			if (pager == null) {
				pager = new Pager(Pager.DEFAULT_PAGE_NO);
			} else {
				pager = new Pager(pager.getPageNo(), pager.getPageSize());
			}
			turnoverBox.setStart(pager.getStartIndex());
			turnoverBox.setEnd(pager.getEndIndex());
			try {
				count = this.turnoverBoxService.getCount(turnoverBox);
				dataList = this.turnoverBoxService.getTurnoverBoxList(turnoverBox);
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			
		model.addAttribute("dataList", dataList);
		pager.setTotalSize(count);
		model.addAttribute("pager", pager);
		model.addAttribute("query", turnoverBox);
		return "tips/turbox";
	}
	
	private void select(Model model, TurnoverBox turnoverBox) {
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
		} else if (turnoverBox.getOrgCode() != null) {
			siteList = this.baseMajorManager.getBaseSiteByOrgIdSubType(turnoverBox.getOrgCode(),Constants.DMS_SITE_TYPE);
		}
		model.addAttribute("siteList", siteList);
	}
}
