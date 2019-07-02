package com.jd.bluedragon.distribution.exception.service;

import com.jd.bluedragon.distribution.api.request.SendCodeExceptionRequest;
import com.jd.bluedragon.utils.BusinessHelper;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.bluedragon.utils.StringHelper;
import com.jd.ql.dms.report.ReportExternalService;
import com.jd.ql.dms.report.domain.BaseEntity;
import com.jd.ql.dms.report.domain.GoodsPrintDto;
import com.jd.ql.dms.report.domain.Pager;
import com.jd.ql.dms.report.domain.SendCodeSummaryResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * <P>
 * </p>
 *
 * @author wuzuxiang
 * @since 2019/4/22
 */
@Service("sendCodeExceptionHandlerService")
public class SendCodeExceptionHandlerServiceImpl implements SendCodeExceptionHandlerService {

    private static final Logger LOGGER = LoggerFactory.getLogger(SendCodeExceptionHandlerServiceImpl.class);

    @Autowired
    private ReportExternalService reportExternalService;

    @Override
    public List<String> querySendCodesByBarCode(Integer siteCode, String barCode) {
        if (null == siteCode || siteCode <= 0 || StringHelper.isEmpty(barCode)) {
            return Collections.emptyList();
        }
        List<String> result = new ArrayList<>();
        if (BusinessHelper.isSendCode(barCode) || BusinessHelper.isTerminalBatchCode(barCode)) {
            result.add(barCode);
        } else {
            /* 1.从分拣的报表中获取批次信息 */
            BaseEntity<List<String>> listBaseEntity = reportExternalService.findUpSendCodeByBarCode(barCode,siteCode);
            if (listBaseEntity != null && BaseEntity.CODE_SUCCESS.equals(listBaseEntity.getCode())) {
                result.addAll(listBaseEntity.getData());
            }
        }
        return result;
    }

    @Override
    public BaseEntity<SendCodeSummaryResponse> summaryPackageBySendCodes(SendCodeExceptionRequest request) {
        if (null == request || request.getSendCodes() == null || request.getSendCodes().isEmpty() || request.getSiteCode() == null) {
            return null;
        }
        /* 1.如果是分拣的批次则从分拣的报表中获取批次的汇总信息 */
        BaseEntity<SendCodeSummaryResponse> summaryResponseBaseEntity = reportExternalService
                .summaryPackageBySendCodes(request.getSendCodes(),request.getSiteCode());
        LOGGER.debug("获取汇总的批次信息为：{},参数为：{}",JsonHelper.toJson(summaryResponseBaseEntity), JsonHelper.toJson(request));
        /* 2.如果是站点的批次则从分拣的报表中获取批次的汇总信息 */

        return summaryResponseBaseEntity;
    }

    @Override
    public BaseEntity<Pager<GoodsPrintDto>> querySendCodeDetailByCondition(SendCodeExceptionRequest request) {
        if (null == request || request.getSendCodes() == null || request.getSiteCode() == null || request.getType() == null) {
            LOGGER.error("查询异常批次参数不全：{}", JsonHelper.toJson(request));
            return null;
        }
        Pager pager = new Pager();
        pager.setPageNo(request.getPageNo()==null? 1 : request.getPageNo());
        pager.setPageSize(request.getPageSize() == null || request.getPageSize() > 500? 10 : request.getPageSize());
        BaseEntity<Pager<GoodsPrintDto>> pagerBaseEntity = reportExternalService.findSendCodeDetailByCondition
                (request.getSendCodes(),request.getSiteCode(),request.getType(),pager);
        LOGGER.debug("获取明细数据为：{},参数为：{}",JsonHelper.toJson(pagerBaseEntity), JsonHelper.toJson(request));
        return pagerBaseEntity;
    }

    @Override
    public List<Object[]> exportSendCodeDetail(SendCodeExceptionRequest request) {
        if (null == request || request.getSendCodes() == null || request.getSiteCode() == null || request.getType() == null) {
            LOGGER.warn("导出批次号明细参数不全；{}",JsonHelper.toJson(request));
            return null;
        }
        BaseEntity<List<GoodsPrintDto>> res = reportExternalService
                .exportSendCodeExceptionDetail(request.getSendCodes(), request.getSiteCode(), request.getType());

        if (null == res ||res.getCode() != BaseEntity.CODE_SUCCESS || res.getData() == null) {
            LOGGER.warn("导出批次号明细失败，参数为：{}，获取结果为：{}", JsonHelper.toJson(request), JsonHelper.toJson(res));
            return null;
        }
        List<Object[]> resultData = new ArrayList<>(res.getData().size());
        for (GoodsPrintDto item : res.getData()) {
            String packageCode = StringHelper.isEmpty(item.getPackageCode())? "" : item.getPackageCode();
            String waybillCode = StringHelper.isEmpty(item.getWaybillCode())? "" : item.getWaybillCode();
            String boxCode = StringHelper.isEmpty(item.getBoxCode())? "" : item.getBoxCode();
            String sendCode = StringHelper.isEmpty(item.getSendCode())? "" : item.getSendCode();

            String[] rowItem = new String[]{packageCode, waybillCode, boxCode, sendCode};
            resultData.add(rowItem);
        }
        return resultData;
    }
}
