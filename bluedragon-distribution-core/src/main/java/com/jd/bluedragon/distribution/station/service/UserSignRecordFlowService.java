package com.jd.bluedragon.distribution.station.service;

import java.util.List;

import com.jd.bluedragon.common.dto.base.response.JdCResponse;
import com.jd.bluedragon.common.dto.station.UserSignQueryRequest;
import com.jd.bluedragon.common.dto.station.UserSignRecordData;
import com.jd.bluedragon.common.dto.station.UserSignRequest;
import com.jd.bluedragon.distribution.api.response.base.Result;
import com.jd.bluedragon.distribution.station.domain.UserSignNoticeVo;
import com.jd.bluedragon.distribution.station.domain.UserSignRecord;
import com.jd.bluedragon.distribution.station.domain.UserSignRecordFlow;
import com.jd.bluedragon.distribution.station.domain.UserSignRecordReportSumVo;
import com.jd.bluedragon.distribution.station.domain.UserSignRecordReportVo;
import com.jd.bluedragon.distribution.station.query.UserSignRecordFlowQuery;
import com.jd.bluedragon.distribution.station.query.UserSignRecordQuery;
import com.jd.ql.dms.common.web.mvc.api.PageDto;

/**
 * 人员签到表--Service接口
 * 
 * @author wuyoude
 * @date 2021年12月30日 14:30:43
 *
 */
public interface UserSignRecordFlowService {

	/**
	 * 新增一条记录
	 * @param userSignRecordFlow
	 */
	boolean addData(UserSignRecordFlow userSignRecordFlow);
	/**
	 * 查询数量
	 * @param query
	 * @return
	 */
	Integer queryFlowCount(UserSignRecordFlowQuery query);
	/**
	 * 查询列表
	 * @param query
	 * @return
	 */
	List<UserSignRecordFlow> queryFlowList(UserSignRecordFlowQuery query);
	/**
	 * 处理-审批通过流程
	 * @param processInstanceNo
	 * @param state
	 * @param flowUser
	 * @param comment
	 */
	void dealFlowPassResult(String processInstanceNo, Integer state, String flowUser, String comment);
	/**
	 * 处理-审批失败、驳回、取消等流程
	 * @param processInstanceNo
	 * @param state
	 * @param flowUser
	 * @param comment
	 */
	void dealFlowUnPassResult(String processInstanceNo, Integer state, String flowUser, String comment);

}
