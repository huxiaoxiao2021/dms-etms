package com.jd.bluedragon.distribution.track;

import com.jd.bluedragon.distribution.api.request.WaybillTrackReqVO;
import com.jd.bluedragon.distribution.api.response.WaybillTrackResVO;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;

import java.util.List;

/**
 * 查询运单全程跟踪接口
 *
 * @author hujiping
 * @date 2023/3/7 3:50 PM
 */
public interface WaybillTrackQueryService {

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
}
