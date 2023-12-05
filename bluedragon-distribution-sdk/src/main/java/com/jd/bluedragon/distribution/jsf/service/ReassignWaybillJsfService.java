package com.jd.bluedragon.distribution.jsf.service;

import com.jd.bluedragon.distribution.api.request.ReassignWaybillApprovalRecordQuery;
import com.jd.bluedragon.distribution.api.response.BaseStaffResponse;
import com.jd.bluedragon.distribution.api.response.ReassignOrder;
import com.jd.bluedragon.distribution.api.response.ReassignWaybillApprovalRecordResponse;
import com.jd.bluedragon.distribution.api.response.StationMatchResponse;
import com.jd.bluedragon.distribution.api.response.base.Result;
import com.jd.bluedragon.distribution.command.JdResult;
import com.jd.bluedragon.distribution.jsf.domain.ReassignWaybillReq;
import com.jd.bluedragon.distribution.jsf.domain.StationMatchRequest;
import com.jd.ql.dms.common.domain.JdResponse;
import com.jd.ql.dms.common.web.mvc.api.PageDto;

import java.util.List;

/**
 * @Author: chenyaguo@jd.com
 * @Date: 2023/10/30 18:13
 * @Description: 返调度 JSF
 */
public interface ReassignWaybillJsfService {

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


    /**
     * 根据站点编码或名称获取站点信息列表
     *
     * @param siteCodeOrName 站点编码或名称
     * @return 响应的站点信息列表
     */

    JdResult<List<BaseStaffResponse>> getSiteByCodeOrName(String siteCodeOrName);

    /**
     * 执行返调度
     */
    JdResult<Boolean> executeReassignWaybill(ReassignWaybillReq req);


    /**
     * 获取返调度审批记录
     * @return
     */
    JdResult<PageDto<ReassignWaybillApprovalRecordResponse>> getReassignWaybillRecordListByPage(ReassignWaybillApprovalRecordQuery query);

    /**
     * 获取重新分配运单审批记录数量
     *
     * @param query 重新分配运单审批记录查询对象
     * @return 重新分配运单审批记录数量
     */

    JdResult<Integer> getReassignWaybillRecordCount(ReassignWaybillApprovalRecordQuery query);

}
