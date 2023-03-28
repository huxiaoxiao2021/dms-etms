package com.jd.bluedragon.distribution.external.intensive.service;

import com.jd.bluedragon.distribution.abnormal.domain.RedeliveryMode;
import com.jd.bluedragon.distribution.abnormal.domain.StrandReportRequest;
import com.jd.bluedragon.distribution.api.request.*;
import com.jd.bluedragon.distribution.api.request.material.recyclingbox.RecyclingBoxInOutboundRequest;
import com.jd.bluedragon.distribution.api.response.StrandReportReasonsVO;
import com.jd.bluedragon.distribution.api.response.WarmBoxInOutVO;
import com.jd.bluedragon.distribution.api.response.WaybillTrackResVO;
import com.jd.bluedragon.distribution.api.response.material.recyclingbox.RecyclingBoxInOutResponse;
import com.jd.bluedragon.distribution.base.domain.BaseDataDictVO;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import org.omg.CORBA.INV_FLAG;

import java.util.List;

/**
 * 提供给逆向集约接口
 *
 * @author hujiping
 * @date 2023/3/2 6:17 PM
 */
public interface ReverseIntensiveService {

    /**
     * 根据运单号查询包裹号
     * 
     * @param waybillCode
     * @return
     */
    InvokeResult<List<String>> queryPackageCodes(String waybillCode);

    /**
     * 根据erp查询全程跟踪历史记录
     * 
     * @param erp
     * @return
     */
    InvokeResult<List<String>> queryWaybillTrackHistory(String erp);

    /**
     * 查询运单全程跟踪
     * 
     * @param waybillTrackReqVO
     * @return
     */
    InvokeResult<List<WaybillTrackResVO>> queryWaybillTrack(WaybillTrackReqVO waybillTrackReqVO);

    /**
     * 查询滞留上报原因
     *
     * @return
     */
    InvokeResult<List<StrandReportReasonsVO>> queryStrandReportReasons();
    
    /**
     * 滞留上报接口
     * 
     * @param request
     * @return
     */
    InvokeResult<Boolean> strandReport(StrandReportRequest request);

    /**
     * 根据类型分组获取数据字典信息
     *
     * @param typeGroups
     * @return
     */
    InvokeResult<List<BaseDataDictVO>> getBaseDictByTypeGroups(List<Integer> typeGroups);

    /**
     * 协商再投状态校验
     * 
     * @param request
     * @return
     */
    InvokeResult<RedeliveryMode> redeliveryCheck(RedeliveryCheckRequest request);

    /**
     * 异常处理提交
     * 
     * @param request
     * @return
     */
    InvokeResult<Boolean> exceptionSubmit(QualityControlRequest request);

    /**
     * 获取板号已绑定的保温箱信息
     * @param relationReqVO
     * @return
     */
    InvokeResult<WarmBoxInOutVO> listBoxBoardRelations(WarmBoxBoardRelationReqVO relationReqVO);

    /**
     * 保温箱出库
     * @param outboundReqVO
     * @return
     */
    InvokeResult<Void> warmBoxOutbound(WarmBoxOutboundReqVO outboundReqVO);

    /**
     * 集包袋发空袋
     * @param operationReqVO
     * @return
     */
    InvokeResult<Void> sendCollectionBag(CollectionBagOperationReqVO operationReqVO);

    /**
     * 清流箱出库
     * @param request
     * @return
     */
    InvokeResult<RecyclingBoxInOutResponse> recyclingBoxOutbound(RecyclingBoxInOutboundRequest request);
}
