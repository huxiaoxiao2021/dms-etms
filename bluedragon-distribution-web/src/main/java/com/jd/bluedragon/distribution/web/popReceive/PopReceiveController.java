package com.jd.bluedragon.distribution.web.popReceive;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.Pager;
import com.jd.bluedragon.core.base.BaseMajorManager;
import com.jd.bluedragon.distribution.base.service.BaseService;
import com.jd.bluedragon.distribution.popReveice.domain.PopReceive;
import com.jd.bluedragon.distribution.popReveice.domain.PopReceiveDto;
import com.jd.bluedragon.distribution.popReveice.service.PopReceiveService;
import com.jd.bluedragon.utils.DateHelper;
import com.jd.common.web.LoginContext;
import com.jd.ql.basic.domain.BaseOrg;
import com.jd.ql.basic.dto.BaseStaffSiteOrgDto;
import com.jd.uim.annotation.Authorization;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.RequestMapping;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author zhaohc
 * @E-mail zhaohengchong@360buy.com
 * @createTime 2012-8-21 下午06:39:49
 * 
 *             POP收货处理
 */
@Controller
@RequestMapping("/popReceive")
public class PopReceiveController {
	private final Logger log = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private BaseService baseService;
	
	@Autowired
	private BaseMajorManager baseMajorManager;

	@Autowired
	private PopReceiveService popReceiveService;

	@InitBinder
	public void initBinder(WebDataBinder binder) {
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		dateFormat.setLenient(true);
		binder.registerCustomEditor(Date.class, new CustomDateEditor(
				dateFormat, true));
	}
	/**
	 * POP实收列表
	 * @return
	 */
    @Authorization(Constants.DMS_WEB_PTORDER_RECEIVE_R)
	@RequestMapping("/getPopRecieveList")
	public String getPopRecieveList(PopReceiveDto popReceiveDTO,Model model,Pager pager){
		select(model,popReceiveDTO);
		if(StringUtils.isEmpty(popReceiveDTO.getOperateStartTime())&&StringUtils.isEmpty(popReceiveDTO.getWaybillCode())&&StringUtils.isEmpty(popReceiveDTO.getThirdWaybillCode())){
			String operateStartTime=DateHelper.formatDate(new Date())+ " 00:00:00";
			String operateEndTime=DateHelper.formatDate(new Date())+ " 23:59:59";
			popReceiveDTO.setOperateStartTime(operateStartTime);
			popReceiveDTO.setOperateEndTime(operateEndTime);
			model.addAttribute("query", popReceiveDTO);
			return "popReceive/getPopRecieveList";
		}
		if (pager == null) {
			pager = new Pager(Pager.DEFAULT_PAGE_NO);
		} else {
			pager= new Pager(pager.getPageNo(), pager.getPageSize());
		}
		Map<String,Object> queryMap = new HashMap<String,Object>();
		
		if(StringUtils.isNotEmpty(popReceiveDTO.getWaybillCode())){
			queryMap.put("waybillCode", popReceiveDTO.getWaybillCode());
		}
		if(StringUtils.isNotEmpty(popReceiveDTO.getThirdWaybillCode())){
			queryMap.put("thirdWaybillCode", popReceiveDTO.getThirdWaybillCode());
		}
		if(popReceiveDTO.getCreateSiteCode()!=null){
			queryMap.put("createSiteCode", popReceiveDTO.getCreateSiteCode());
		}
		if(StringUtils.isNotEmpty(popReceiveDTO.getOperateStartTime())){
			queryMap.put("operateStartTime", popReceiveDTO.getOperateStartTime());
		}
		if(StringUtils.isNotEmpty(popReceiveDTO.getOperateEndTime())){
			queryMap.put("operateEndTime", popReceiveDTO.getOperateEndTime());
		}
		int count =this.popReceiveService.count(queryMap);
		pager.setTotalSize(count);
		queryMap.put("start", pager.getStartIndex());
		queryMap.put("end", pager.getEndIndex());
        queryMap.put("pageSize",pager.getPageSize());
		List<PopReceive> dataList =this.popReceiveService.findPopReceiveList(queryMap);
		log.info("count:{}", count);
		model.addAttribute("dataList", dataList);
		model.addAttribute("pager", pager);
		model.addAttribute("query", popReceiveDTO);
		return "popReceive/getPopRecieveList";
	}
	
	private void select(Model model, PopReceiveDto query) {
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
			siteList = this.baseMajorManager.getBaseSiteByOrgIdSiteType(defaultOrgId,Constants.DMS_SITE_TYPE);
		} else if (query.getOrgCode() != null) {
			siteList = this.baseMajorManager.getBaseSiteByOrgIdSiteType(query.getOrgCode(),Constants.DMS_SITE_TYPE);
		}
		model.addAttribute("siteList", siteList);
	}
}
