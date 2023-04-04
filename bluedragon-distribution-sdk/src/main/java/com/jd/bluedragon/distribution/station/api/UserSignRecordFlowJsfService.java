package com.jd.bluedragon.distribution.station.api;

import com.jd.bluedragon.distribution.api.response.base.Result;
import com.jd.bluedragon.distribution.station.domain.UserSignFlowRequest;
import com.jd.bluedragon.distribution.station.domain.UserSignRecordFlow;
import com.jd.bluedragon.distribution.station.query.UserSignRecordFlowQuery;
import com.jd.ql.dms.common.web.mvc.api.PageDto;

/**
 * 人员签到流程表--JsfService接口
 * 
 * @author wuyoude
 * @date 2023年03月10日 14:30:43
 *
 */
public interface UserSignRecordFlowJsfService {
	/**
	 * 添加流程接口
	 * @param addRequest
	 * @return
	 */
	Result<Boolean> addSignFlow(UserSignFlowRequest addRequest);
	/**
	 * 分页查询逻辑
	 * @param query
	 * @return
	 */
	Result<PageDto<UserSignRecordFlow>> queryFlowPageList(UserSignRecordFlowQuery query);
}
