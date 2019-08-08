package com.jd.bluedragon.distribution.exception.service;

import com.jd.bluedragon.distribution.api.request.SendCodeExceptionRequest;
import com.jd.ql.dms.report.domain.BaseEntity;
import com.jd.ql.dms.report.domain.GoodsPrintDto;
import com.jd.ql.dms.report.domain.Pager;
import com.jd.ql.dms.report.domain.SendCodeSummaryResponse;

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
    BaseEntity<SendCodeSummaryResponse> summaryPackageBySendCodes(SendCodeExceptionRequest request);

    /**
     * 根据批次号和类型，查询批次明细
     * @param request
     * @return
     */
    BaseEntity<Pager<GoodsPrintDto>> querySendCodeDetailByCondition(SendCodeExceptionRequest request);

    /**
     * 根据条件进行数据的导出
     * @param request 查询条件
     */
    List<Object[]> exportSendCodeDetail(SendCodeExceptionRequest request) ;
}
