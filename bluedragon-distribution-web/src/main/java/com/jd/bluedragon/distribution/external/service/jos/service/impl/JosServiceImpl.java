package com.jd.bluedragon.distribution.external.service.jos.service.impl;

import com.jd.bluedragon.distribution.api.JdResponse;
import com.jd.bluedragon.distribution.api.request.LoadBillReportRequest;
import com.jd.bluedragon.distribution.api.response.LoadBillReportResponse;
import com.jd.bluedragon.distribution.external.jos.service.JosService;
import com.jd.bluedragon.distribution.globaltrade.domain.LoadBillReport;
import com.jd.bluedragon.distribution.globaltrade.service.LoadBillService;
import com.jd.bluedragon.utils.StringHelper;
import com.jd.jsf.gd.util.StringUtils;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by yangbo7 on 2015/9/1.
 */
@Service("josService")
public class JosServiceImpl implements JosService {

    private final Logger logger = Logger.getLogger(JosServiceImpl.class);

    @Autowired
    private LoadBillService loadBillService;

    @Override
    @JProfiler(jKey = "DMSWEB.JosServiceImpl.updateLoadBillStatus", mState = {JProEnum.TP})
    public LoadBillReportResponse updateLoadBillStatus(LoadBillReportRequest request) {
        if(request != null) {
            logger.info("全球购更新配载单状态获取到的appKey:" + request.getAppKey());
        }
        LoadBillReportResponse response = new LoadBillReportResponse(1, JdResponse.MESSAGE_OK);
        try {
            if (request == null || StringUtils.isBlank(request.getReportId())
                    || StringUtils.isBlank(request.getLoadId())
                    || StringUtils.isBlank(request.getOrderId())
                    || StringUtils.isBlank(request.getWarehouseId())
                    || request.getStatus() == null
                    || request.getStatus() < 1) {
                return new LoadBillReportResponse(2, "参数错误");
            }
            loadBillService.updateLoadBillStatusByReport(resolveLoadBillReport(request));
        } catch (Exception e) {
            response = new LoadBillReportResponse(2, "操作异常");
            logger.error("GlobalTradeController 发生异常,异常信息 : ", e);
        }
        return response;
    }



    private LoadBillReport resolveLoadBillReport(LoadBillReportRequest request) {
        LoadBillReport report = new LoadBillReport();
        report.setReportId(request.getReportId());
        report.setLoadId(request.getLoadId());
        report.setProcessTime(request.getProcessTime());
        report.setStatus(request.getStatus());
        report.setWarehouseId(request.getWarehouseId());
        report.setNotes(request.getNotes());
        report.setOrderId(request.getOrderId());
        report.setWaybillCode(request.getOrderId());
        report.setCustBillNo(request.getCustBillNo());
        report.setCiqCheckFlag(request.getCiqCheckFlag());
        return report;
    }

}
