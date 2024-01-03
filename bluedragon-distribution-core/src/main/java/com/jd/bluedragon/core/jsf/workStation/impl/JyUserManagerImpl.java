package com.jd.bluedragon.core.jsf.workStation.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.jdl.basic.api.domain.user.JyUser;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.core.jsf.workStation.JyUserManager;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import com.jdl.basic.api.domain.user.JyUserDto;
import com.jdl.basic.api.domain.user.JyUserQueryDto;
import com.jdl.basic.api.service.user.UserJsfService;
import com.jdl.basic.common.utils.Result;

import lombok.extern.slf4j.Slf4j;

/**
 * 人员岗位查询
 * @author wuyoude
 *
 */
@Slf4j
@Service("jyUserManager")
public class JyUserManagerImpl implements JyUserManager {

    @Autowired
    private UserJsfService userJsfService;
    
    private static final String UMP_KEY_PREFIX = "dmsWeb.jsf.client.userJsfService.";

	@Override
	@JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWEB,jKey = UMP_KEY_PREFIX + "queryUserListBySiteAndPosition",mState={JProEnum.TP,JProEnum.FunctionError})	
	public Result<List<JyUserDto>> queryUserListBySiteAndPosition(Integer siteCode,String organizationCode,String userPositionCode,String userPositionName) {
		Result<List<JyUserDto>> result = Result.success();
		List<JyUserDto> userList = new ArrayList<>();
		JyUserQueryDto condition = new JyUserQueryDto();
		condition.setSiteCode(siteCode);
		condition.setPositionCode(userPositionCode);
		
		condition.setOrganizationCode(organizationCode);
		if(StringUtils.isNotBlank(userPositionName) && userPositionName.contains(",")){
			condition.setPositionNames(Arrays.asList(userPositionName.split(",")));
		}else {
			condition.setPositionName(userPositionName);
		}
		
        try {
            log.info("获取岗位人员列表列表数据 queryUserListBySiteAndPosition： siteCode={},userPositionCode={},userPositionName={}",siteCode,userPositionCode,userPositionName);
    		com.jd.dms.java.utils.sdk.base.Result<List<JyUserDto>> apiResult = userJsfService.queryUserListBySiteAndPosition(condition);
    		if(apiResult != null && apiResult.getData() != null) {
    			userList.addAll(apiResult.getData());
    		}            
        } catch (Exception e) {
            log.error("获取岗位人员列表列表数据异常 {}",  e.getMessage(),e);
        }
		result.setData(userList);
		return result;
	}
	@Override
	@JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWEB,jKey = UMP_KEY_PREFIX + "queryUserInfo",mState={JProEnum.TP,JProEnum.FunctionError})
	public Result<JyUser> queryUserInfo(String erp) {
		Result<JyUser> result = Result.success();
		JyUser condition = new JyUser();
		condition.setUserErp(erp);
		try {
			log.info("根据erp获取岗位人员信息 erp:{}",erp);
			JyUser jyUser = userJsfService.queryUserInfo(condition);
			result.setData(jyUser);
		}catch (Exception e){
			result.toError("根据erp获取岗位人员信息数据异常");
			log.error("根据erp获取岗位人员信息数据异常{}",  e.getMessage(),e);
		}
		return result;
	}

}
