package com.jd.bluedragon.distribution.worker.web.controller;

import com.jd.bluedragon.core.base.BaseMajorManager;
import com.jd.bluedragon.distribution.api.domain.LoginUser;
import com.jd.common.web.LoginContext;
import com.jd.ql.basic.dto.BaseStaffSiteOrgDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

/**
 * 
 * @ClassName: DmsBaseController
 * @Description: web层基础类
 * @author: wuyoude
 * @date: 2018年4月26日 上午11:33:51
 *
 */
public class DmsBaseWorkerController {
	
	private static final Logger log = LoggerFactory.getLogger(DmsBaseWorkerController.class);
	
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
				loginContext.setUserId(10053);
				loginContext.setPin("bjxings");
				loginContext.setNick("邢松");
			}
		}
		LoginUser loginUser = new LoginUser();
		loginUser.setUserId((int)loginContext.getUserId());
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
		}
		return loginUser;    
	}
}