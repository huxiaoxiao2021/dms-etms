package com.jd.bluedragon.distribution.reassignWaybill.service;

import com.jd.bluedragon.distribution.api.request.ReassignWaybillRequest;
import com.jd.bluedragon.distribution.api.response.OrderResponse;
import com.jd.bluedragon.distribution.api.response.ReassignOrder;
import com.jd.bluedragon.distribution.api.response.ReassignWaybillApprovalRecordResponse;
import com.jd.bluedragon.distribution.api.response.StationMatchResponse;
import com.jd.bluedragon.distribution.api.response.base.Result;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.command.JdResult;
import com.jd.bluedragon.distribution.jsf.domain.ReassignWaybillReq;
import com.jd.bluedragon.distribution.jsf.domain.StationMatchRequest;
import com.jd.bluedragon.distribution.reassignWaybill.domain.ReassignWaybill;
import com.jd.bluedragon.distribution.reassignWaybill.domain.ReassignWaybillApprovalRecordMQ;
import com.jd.lsb.flow.domain.HistoryApprove;
import com.jd.ql.basic.dto.BaseStaffSiteOrgDto;
import com.jd.ql.dms.common.web.mvc.api.PageDto;

import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import java.util.List;

public interface ReassignWaybillService {
     ReassignWaybill queryByPackageCode(String packageCode);
	ReassignWaybill queryByWaybillCode(String waybillCode);
	/**
	 * 现场预分拣回调处理
	 */
	JdResult<Boolean> backScheduleAfter(ReassignWaybillRequest reassignWaybillRequest);

	Boolean addToDebon(ReassignWaybill packTagPrint);


    /**
     * 获取重新分配订单响应
     *
     * @param packageCode 订单包裹代码
     *                   入参参数描述
     * @param decryptFields 解密字段列表
     *                     入参参数描述
     * @return OrderResponse 响应的重新分配订单响应
     *                       响应的参数描述
     */
	JdResult<ReassignOrder> getReassignOrderInfo(String packageCode, List<String> decryptFields);

	/**
	 * 地址匹配
	 * @param request
	 * @return
	 */
	JdResult<StationMatchResponse> stationMatchByAddress(StationMatchRequest request);


	InvokeResult<List<BaseStaffSiteOrgDto>> getSiteByCodeOrName(String siteCodeOrName);

	/**
	 * 执行返调度
	 */
	JdResult<Boolean> executeReassignWaybill(ReassignWaybillReq req);

	void dealReassignWaybillApprove(ReassignWaybillApprovalRecordMQ mq);

	/**
	 * 获取返调度审批记录
	 * @return
	 */
	JdResult<PageDto<ReassignWaybillApprovalRecordResponse>> getReassignWaybillRecordListByPage();

	void dealApproveResult(HistoryApprove historyApprove);


}
