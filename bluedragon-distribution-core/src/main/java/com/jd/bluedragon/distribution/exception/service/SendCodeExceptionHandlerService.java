package com.jd.bluedragon.distribution.exception.service;

import com.jd.bluedragon.distribution.api.domain.SendCodeSummary;
import com.jd.bluedragon.distribution.api.request.SendCodeExceptionRequest;
import com.jd.bluedragon.distribution.api.response.SendCodeExceptionResponse;

import java.util.List;

/**
 * <P>
 * </p>
 *
 * @author wuzuxiang
 * @since 2019/4/22
 */
public interface SendCodeExceptionHandlerService {

    /**
     * 根据单号查询上游发往本分拣的批次信息
     * @param siteCode
     * @param barCode
     * @return
     */
    List<String> querySendCodesByBarCode(Integer siteCode, String barCode);

    /**
     * 根据批次号的信息查询目的地的操作未操作的统计信息
     * @param request
     * @return
     */
    SendCodeExceptionResponse summaryPackageBySendCodes(SendCodeExceptionRequest request);

    /**
     * 根据批次号和类型，查询批次明细
     * @param request
     * @return
     */
    SendCodeSummary querySendCodeDetailByCondition(SendCodeExceptionRequest request);
}
