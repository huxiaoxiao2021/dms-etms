package com.jd.bluedragon.distribution.base.controller;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.core.base.BaseMajorManager;
import com.jd.bluedragon.distribution.api.domain.LoginUser;
import com.jd.bluedragon.distribution.web.ErpUserClient;
import com.jd.common.web.LoginContext;
import com.jd.ql.basic.dto.BaseStaffSiteOrgDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.ui.Model;

import java.util.Objects;

/**
 * 
 * @ClassName: DmsBaseController
 * @Description: web层基础类
 * @author: wuyoude
 * @date: 2018年4月26日 上午11:33:51
 *
 */
public class DmsBaseController {
	
	private static final Logger log = LoggerFactory.getLogger(DmsBaseController.class);
	
    @Autowired
    private BaseMajorManager baseMajorManager;
    /**
     * 是否线上系统，没有配置则默认为true
     */
	@Value("${app.config.isOnline:true}")
    private boolean isOnline;
    /**
     * 获取当前登录账户信息
     * @return
     */
    public LoginUser getLoginUser() {
		LoginContext loginContext = LoginContext.getLoginContext();
		if (loginContext == null) {
			log.warn("未获取到当前登录信息!");
			//线上系统，获取不到当前登录用户则返回null
			if(isOnline){
				return null;
			}else{
				//非线上系统，获取不到当前登录用户则返回bjxings
				loginContext = new LoginContext();
				loginContext.setPin("bjxings");
				loginContext.setNick("邢松");
			}
		}
		LoginUser loginUser = new LoginUser();
		loginUser.setUserId(isOnline ? ErpUserClient.getLoginUserId(loginContext) : 10053);
		loginUser.setUserErp(loginContext.getPin());
		loginUser.setUserName(loginContext.getNick());
		BaseStaffSiteOrgDto userOrgInfo = baseMajorManager.getBaseStaffByErpNoCache(loginUser.getUserErp());
		if(userOrgInfo != null){
			loginUser.setStaffNo(userOrgInfo.getStaffNo());
			loginUser.setOrgId(userOrgInfo.getOrgId());
			loginUser.setOrgName(userOrgInfo.getOrgName());
			loginUser.setSiteType(userOrgInfo.getSiteType());
			loginUser.setSiteCode(userOrgInfo.getSiteCode());
			loginUser.setSiteName(userOrgInfo.getSiteName());
			loginUser.setDmsSiteCode(userOrgInfo.getDmsSiteCode());
			loginUser.setProvinceAgencyCode(userOrgInfo.getProvinceAgencyCode());
			loginUser.setProvinceAgencyName(userOrgInfo.getProvinceAgencyName());
			loginUser.setAreaHubCode(userOrgInfo.getAreaCode());
			loginUser.setAreaHubName(userOrgInfo.getAreaName());
		}
		return loginUser;    
	}

	/**
	 * 设置model基础信息
	 * 
	 * @param model
	 */
	public void setBaseModelInfo(Model model) {
		ErpUserClient.ErpUser erpUser = ErpUserClient.getCurrUser();
		String userCode;
		Long createSiteCode = (long) -1;
		String createSiteName = Constants.EMPTY_FILL;
		Integer orgId = -1;
		String orgName = Constants.EMPTY_FILL;
		String provinceAgencyCode = Constants.EMPTY_FILL;
		String provinceAgencyName = Constants.EMPTY_FILL;
		String areaHubCode = Constants.EMPTY_FILL;
		String areaHubName = Constants.EMPTY_FILL;

		if(erpUser!=null){
			userCode = erpUser.getUserCode();
			BaseStaffSiteOrgDto bssod = baseMajorManager.getBaseStaffByErpNoCache(userCode);
			if (bssod != null) {
				createSiteCode = new Long(bssod.getSiteCode());
				createSiteName = bssod.getSiteName();
				orgId = bssod.getOrgId();
				orgName = bssod.getOrgName();
				provinceAgencyCode = bssod.getProvinceAgencyCode();
				provinceAgencyName = bssod.getProvinceAgencyName();
				areaHubCode = bssod.getAreaCode();
				areaHubName = bssod.getAreaName();
			}
		}

		model.addAttribute("orgId",orgId)
				.addAttribute("orgName",orgName)
				.addAttribute("createSiteCode",createSiteCode)
				.addAttribute("createSiteName",createSiteName)
				.addAttribute("provinceAgencyCode",provinceAgencyCode)
				.addAttribute("provinceAgencyName",provinceAgencyName)
				.addAttribute("areaHubCode",areaHubCode)
				.addAttribute("areaHubName",areaHubName);
	}
}
