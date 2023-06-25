package com.jd.bluedragon.core.jsf.workStation.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.jd.bluedragon.core.jsf.workStation.JyUserManager;
import com.jdl.basic.api.domain.user.JyUser;
import com.jdl.basic.api.domain.user.JyUserQueryCondition;
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

	@Override
	public Result<List<String>> queryUserListBySiteAndPosition(Integer siteCode, String userPosition) {
		Result<List<String>> result = Result.success();
		List<String> userList = new ArrayList<>();
		JyUserQueryCondition condition = new JyUserQueryCondition();
		condition.setSiteCode(siteCode);
		condition.setPositionCode(userPosition);
        try {
            log.info("获取岗位人员列表列表数据 queryUserListBySiteAndPosition： siteCode={},userPosition={}",siteCode,userPosition);
    		com.jd.dms.java.utils.sdk.base.Result<List<JyUser>> apiResult = userJsfService.queryUserListBySiteAndPosition(condition);
    		if(apiResult != null && apiResult.getData() != null) {
    			for(JyUser user: apiResult.getData()) {
    				userList.add(user.getUserErp());
    			}
    		}            
        } catch (Exception e) {
            log.error("获取岗位人员列表列表数据异常 {}",  e.getMessage(),e);
        }
		result.setData(userList);
		return result;
	}
}
