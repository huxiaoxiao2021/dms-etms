package com.jd.bluedragon.distribution.station.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.alibaba.fastjson.JSONObject;
import com.jd.bd.dms.automatic.sdk.common.utils.DateHelper;
import com.jd.security.aces.mybatis.util.AcesFactory;
import com.jd.bluedragon.distribution.api.response.base.Result;
import com.jd.bluedragon.distribution.station.dao.UserSignRecordFlowDao;
import com.jd.bluedragon.distribution.station.domain.UserSignRecord;
import com.jd.bluedragon.distribution.station.domain.UserSignRecordFlow;
import com.jd.bluedragon.distribution.station.enums.SignFlowStatusEnum;
import com.jd.bluedragon.distribution.station.query.UserSignRecordFlowQuery;
import com.jd.bluedragon.distribution.station.query.UserSignRecordQuery;
import com.jd.bluedragon.distribution.station.service.UserSignRecordHistoryService;
import com.jd.bluedragon.distribution.station.service.UserSignRecordService;
import com.jd.bluedragon.utils.easydata.EasyDataClientUtil;
import com.jd.bluedragon.utils.easydata.UserSignRecordEasyDataConfig;
import com.jd.fds.lib.dto.server.FdsPage;
import com.jd.fds.lib.dto.server.FdsServerResult;

import lombok.extern.slf4j.Slf4j;

/**
 * 人员签到流程业务--Service接口实现
 * 
 * @author wuyoude
 * @date 2023年03月10日 14:30:43
 *
 */
@Slf4j
@Service("userSignRecordHistoryService")
public class UserSignRecordHistoryServiceImpl implements UserSignRecordHistoryService {

	@Autowired
	@Qualifier("userSignRecordFlowDao")
	private UserSignRecordFlowDao userSignRecordFlowDao;
	
	@Autowired
	@Qualifier("userSignRecordService")
	private UserSignRecordService userSignRecordService;
	
	@Value("${beans.userSignRecordHistoryService.useEasyData:false}")
	private boolean useEasyData = true;
	
    @Autowired
    EasyDataClientUtil easyDataClientUtil;

    @Autowired
    UserSignRecordEasyDataConfig userSignRecordEasyDataConfig;
	
	@Override
	public Integer querySignCount(UserSignRecordFlowQuery query) {
		if(this.useEasyData) {
			return querySignCountWithEasyData(query);
		}
		UserSignRecordQuery historyQuery = new UserSignRecordQuery();
		BeanUtils.copyProperties(query, historyQuery);
		return userSignRecordService.queryCountForFlow(historyQuery);
	}
	
	private Integer querySignCountWithEasyData(UserSignRecordFlowQuery query) {
        Map<String, Object> queryParam = buildQuerySignDataParams(query);
        //调用easydata
        FdsServerResult edresult = easyDataClientUtil.queryNoPage(userSignRecordEasyDataConfig.getQueryCountForFlow(), queryParam,
        		userSignRecordEasyDataConfig.getApiGroupName(),userSignRecordEasyDataConfig.getAppToken(),
                 userSignRecordEasyDataConfig.getTenant());
		return converVoInteger(edresult);
	}

	private Integer converVoInteger(FdsServerResult edresult) {
		if(edresult != null 
				&& !CollectionUtils.isEmpty(edresult.getResult())) {
			Map<String, Object> data = edresult.getResult().get(0);
			if(data.containsKey("num")) {
				return Integer.valueOf(data.get("num").toString());
			}
		}
		return 0;
	}

	private Map<String, Object> buildQuerySignDataParams(UserSignRecordFlowQuery query) {
        Map<String, Object> result = new HashMap<>();
        if (null != query.getOrgCode()){
            result.put("orgCode",query.getOrgCode().toString());
        }
        if (null != query.getSiteCode()){
            result.put("siteCode",query.getSiteCode().toString());
        }
        if (null != query.getUserCode()){
        	result.put("userCodeIndex",AcesFactory.getIns().calculateStringIndex(query.getUserCode()));
        	result.put("userCode",query.getUserCode());
        }
        if (null != query.getSignDateStr()){
            result.put("signDateStr",query.getSignDateStr());
        }         
		return result;
	}

	@Override
	public List<UserSignRecordFlow> querySignList(UserSignRecordFlowQuery query) {
		if(this.useEasyData) {
			return querySignListWithEasyData(query);
		}
		List<UserSignRecordFlow> flowList = new ArrayList<>();
		UserSignRecordQuery historyQuery = new UserSignRecordQuery();
		BeanUtils.copyProperties(query, historyQuery);
		List<UserSignRecord> historyList = userSignRecordService.queryDataListForFlow(historyQuery);
		if(!CollectionUtils.isEmpty(historyList)) {
			for(UserSignRecord signData : historyList) {
				flowList.add(toUserSignRecordFlow(signData));
			}
		}
		return flowList;
	}
	private List<UserSignRecordFlow> querySignListWithEasyData(UserSignRecordFlowQuery query) {
	      Map<String, Object> queryParam = buildQuerySignDataParams(query);
	        //调用easydata
	      FdsPage edresult = easyDataClientUtil.query(userSignRecordEasyDataConfig.getQueryDataListForFlow(), queryParam,
	        		userSignRecordEasyDataConfig.getApiGroupName(),userSignRecordEasyDataConfig.getAppToken(),
	        		query.getPageNumber(),query.getPageSize(),
	                 userSignRecordEasyDataConfig.getTenant());
	        List<UserSignRecordFlow> dataList = new ArrayList<>();
	        if (null != edresult && !CollectionUtils.isEmpty(edresult.getContent())){
	            for (Object tmp : edresult.getContent()){
	            	 Map<String,Object> tmpMap = (Map<String,Object>)tmp;
	            	dataList.add(converVoUserSignRecordFlow(tmpMap));
	            }
	        }
			return dataList;
	}	
	@Override
	public UserSignRecordFlow queryById(Long recordId) {
		if(this.useEasyData) {
			return queryByIdForFlowWithEasyData(recordId);
		}
		UserSignRecord queryResult  = userSignRecordService.queryByIdForFlow(recordId);
		if(queryResult != null) {
			return toUserSignRecordFlow(queryResult);
		}
		return null;
	}
	private UserSignRecordFlow queryByIdForFlowWithEasyData(Long recordId) {
        Map<String, Object> queryParam = new HashMap<>();
        queryParam.put("recordId", recordId.toString());
        //调用easydata
        FdsServerResult edresult = easyDataClientUtil.queryNoPage(userSignRecordEasyDataConfig.getQueryByIdForFlow(), queryParam,
        		userSignRecordEasyDataConfig.getApiGroupName(),userSignRecordEasyDataConfig.getAppToken(),
                 userSignRecordEasyDataConfig.getTenant());
		return converVoUserSignRecordFlow(edresult);
	}

	private UserSignRecordFlow converVoUserSignRecordFlow(FdsServerResult edresult) {
		if(edresult != null 
				&& !CollectionUtils.isEmpty(edresult.getResult())) {
			return converVoUserSignRecordFlow(edresult.getResult().get(0));
		}
		return null;
	}

	private UserSignRecordFlow converVoUserSignRecordFlow(Map<String, Object> tmpMap) {
        String signInTime = null;
        if (null != tmpMap.get("signInTime")){
            signInTime = String.valueOf(tmpMap.get("signInTime"));
        }
        String signOutTime = null;
        if (null != tmpMap.get("signOutTime")){
            signOutTime = String.valueOf(tmpMap.get("signOutTime"));
        }
        String signDate = null;
        if (null != tmpMap.get("signDate")){
            signDate = String.valueOf(tmpMap.get("signDate"));
        }

        UserSignRecordFlow result = JSONObject.parseObject(JSONObject.toJSONString(tmpMap), UserSignRecordFlow.class);

        if (StringUtils.isNotEmpty(signInTime)){
            result.setSignInTime(new Date(Long.valueOf(signInTime)));
        }
        if (StringUtils.isNotEmpty(signOutTime)){
            result.setSignOutTime(new Date(Long.valueOf(signOutTime)));
        }
        if (StringUtils.isNotEmpty(signDate)){
            result.setSignDate(new Date(Long.valueOf(signDate)));
        }
		return result;
	}

	private UserSignRecordFlow toUserSignRecordFlow(UserSignRecord signData) {
		if(signData == null) {
			return null;
		}
		UserSignRecordFlow flowData = new UserSignRecordFlow();
		BeanUtils.copyProperties(signData, flowData);
		flowData.setId(null);
		flowData.setRefRecordId(signData.getId());
		flowData.setFlowStatus(SignFlowStatusEnum.DEFALUT.getCode());
		return flowData;
	}
	@Override
	public boolean checkSignTimeForFlow(UserSignRecordFlowQuery checkQuery) {
		if(this.useEasyData) {
			return queryCountForCheckSignTimeWithEasyData(checkQuery) == 0;
		}	
		return userSignRecordService.queryCountForCheckSignTime(checkQuery)==0;
	}
	private Integer queryCountForCheckSignTimeWithEasyData(UserSignRecordFlowQuery query) {
        Map<String, Object> queryParam = new HashMap<>();
        
        if (null != query.getRefRecordId()){
        	queryParam.put("recordId", query.getRefRecordId().toString());
        }
        if (null != query.getUserCode()){
        	queryParam.put("userCodeIndex",AcesFactory.getIns().calculateStringIndex(query.getUserCode()));
        	queryParam.put("userCode",query.getUserCode());
        }
        if (null != query.getSignInTime()){
        	queryParam.put("signInTime",DateHelper.formatDateTime(query.getSignInTime()));
        } 
        if (null != query.getSignOutTime()){
        	queryParam.put("signOutTime",DateHelper.formatDateTime(query.getSignOutTime()));
        }         
        //调用easydata
        FdsServerResult edresult = easyDataClientUtil.queryNoPage(userSignRecordEasyDataConfig.getQueryCountForCheckSignTime(), queryParam,
        		userSignRecordEasyDataConfig.getApiGroupName(),userSignRecordEasyDataConfig.getAppToken(),
                 userSignRecordEasyDataConfig.getTenant());
		return converVoInteger(edresult);
	}
}