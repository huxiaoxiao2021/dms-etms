package com.jd.bluedragon.core.jsf.workStation.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.jd.bluedragon.core.base.BaseMajorManager;
import com.jd.bluedragon.distribution.base.domain.SysConfig;
import com.jd.bluedragon.distribution.base.service.SysConfigService;
import com.jd.etms.framework.utils.JsonUtils;
import com.jd.etms.framework.utils.cache.annotation.Cache;
import com.jd.ql.basic.dto.BaseSiteInfoDto;
import com.jdl.basic.api.domain.user.*;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.core.jsf.workStation.JyUserManager;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
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
	@Autowired
	private BaseMajorManager baseMajorManager;

	@Autowired
	SysConfigService sysConfigService;
    
    private static final String UMP_KEY_PREFIX = "dmsWeb.jsf.client.userJsfService.";

	//一线机构负责人岗位名称
	private static final String FIRST_LINE_LEADER_POSITION_NAME_KEY = "first.line.leader.position.name";

	//一线机构负责人岗位名称
	private static final String FIRST_LINE_LEADER_POSITION_NAME = "机构负责人岗";

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

	@Override
	@JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWEB,jKey = UMP_KEY_PREFIX + "batchQueryJyThirdpartyUser",mState={JProEnum.TP,JProEnum.FunctionError})
	public Result<List<JyThirdpartyUser>> batchQueryJyThirdpartyUser(List<JyTpUserScheduleQueryDto> dtos) {
		Result<List<JyThirdpartyUser>> result = Result.success();
		if(CollectionUtils.isEmpty(dtos)){
			return Result.fail("批量查询三方储备人员信息查询参数为空");
		}
		try {
			com.jd.dms.java.utils.sdk.base.Result<List<JyThirdpartyUser>> jyThirdpartyUsers = userJsfService.batchQueryJyThirdpartyUser(dtos);
			if(jyThirdpartyUsers == null){
				log.info("批量查询三方储备人员信息返回值为null,dtos:{}", JsonUtils.beanToJson(dtos));
				return result;
			}
			if(CollectionUtils.isEmpty(jyThirdpartyUsers.getData())){
				log.info("批量查询三方储备人员信息-未查到三方储备信息,dtos:{}", JsonUtils.beanToJson(dtos));
			}
			return result.setData(jyThirdpartyUsers.getData());
		}catch (Exception e){
			result.toError("批量查询三方储备人员信息异常");
			log.error("批量查询三方储备人员信息异常{}",  e.getMessage(),e);
		}
		return result;
	}
	
	
	@Override
	@JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWEB,jKey = UMP_KEY_PREFIX + "querySiteLeader",mState={JProEnum.TP,JProEnum.FunctionError})
	@Cache(key = "JyUserManagerImpl.querySiteLeader@args0", memoryEnable = true, memoryExpiredTime = 10 * 60 * 1000,
			redisEnable = true, redisExpiredTime = 20 * 60 * 1000)
	public JyUserDto querySiteLeader(Integer siteCode){
		if(siteCode == null){
			log.warn("查询场地负责人，参数为空！");
			return null;
		}
		BaseSiteInfoDto siteInfo = baseMajorManager.getBaseSiteInfoBySiteId(siteCode);
		if(siteInfo == null) {
			log.warn("查询场地负责人,场地【{}】在青龙基础资料不存在！",siteCode);
			return null;
		}
		String positionName = getFirstLineLeaderPositionName();
		Result<List<JyUserDto>> result = queryUserListBySiteAndPosition(siteCode, siteInfo.getOrganizationCode(),
				"",positionName);
		if(result == null || CollectionUtils.isEmpty(result.getData())){
			log.info("查询场地负责人,未查到，siteCode:{}", siteCode);
			return null;
		}
		return result.getData().get(0);
	}
	private String getFirstLineLeaderPositionName(){
		String name = FIRST_LINE_LEADER_POSITION_NAME;
		SysConfig nameConfig = sysConfigService.findConfigContentByConfigName(FIRST_LINE_LEADER_POSITION_NAME_KEY);
		if(nameConfig == null || StringUtils.isBlank(nameConfig.getConfigContent())){
			return name;
		}
		return nameConfig.getConfigContent();
	}

	@Override
	@JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWEB,jKey = "JyUserManagerImpl.getUserByErpOrIdNum", mState={JProEnum.TP,JProEnum.FunctionError})
	public JyUserDto getUserByErpOrIdNum(JyUserQueryDto queryDto) {
		com.jd.dms.java.utils.sdk.base.Result<JyUserDto> result = userJsfService.getUserByErpOrIdNum(queryDto);
		if (result == null) {
			log.error("WorkGridScheduleManagerImpl.getUserByUserCode 返回结果为空！");
			return null;
		}
		if (result.isFail()) {
			log.error("WorkGridScheduleManagerImpl.getUserByUserCode 调用失败  message{}！", result.getMessage());
			return null;
		}
		if (result.getData() == null) {
			log.error("WorkGridScheduleManagerImpl.getUserByUserCode 返回data为null");
			return null;
		}
		return result.getData();
	}

}
