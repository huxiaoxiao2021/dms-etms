package com.jd.bluedragon.distribution.ver.filter.filters;

import com.jd.bluedragon.distribution.api.response.SortingResponse;
import com.jd.bluedragon.distribution.api.response.TransBillScheduleResponse;
import com.jd.bluedragon.distribution.transBillSchedule.domain.TransBillScheduleRequest;
import com.jd.bluedragon.distribution.transBillSchedule.service.TransBillScheduleService;
import com.jd.bluedragon.distribution.ver.domain.FilterContext;
import com.jd.bluedragon.distribution.ver.exception.SortingCheckException;
import com.jd.bluedragon.distribution.ver.filter.Filter;
import com.jd.bluedragon.distribution.ver.filter.FilterChain;
import com.jd.bluedragon.dms.utils.BusinessUtil;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created by wuzuxiang on 2017/4/13.
 */
public class CityDistributionFilter implements Filter {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());


    private static final String default_schedule = "-1";


    @Autowired
    private TransBillScheduleService transBillScheduleService;

    @Override
    public void doFilter(FilterContext request, FilterChain chain) throws Exception {

        logger.info("do CityDistributionFilter process...");
        if (BusinessUtil.isUrban(request.getWaybillCache().getWaybillSign(), request.getWaybillCache().getSendPay())) {
            TransBillScheduleResponse response = new TransBillScheduleResponse();
            if (BusinessUtil.isBoxcode(request.getBoxCode())) {
                TransBillScheduleRequest transBillScheduleRequest = new TransBillScheduleRequest();
                transBillScheduleRequest.setBoxCode(request.getBoxCode());
                transBillScheduleRequest.setWaybillCode(request.getWaybillCode());
                response = this.checkScheduleBill(transBillScheduleRequest);

                if (response != null
                        && request.getBoxCode().equals(response.getBoxCode())
                        && request.getWaybillCode().equals(response.getWaybillCode())
                        && response.getSameScheduleBill()) {
                    this.logger.info("城配拦截通过" + response.toString());
                    if (StringUtils.isNotBlank(response.getRoadCode())) {
                        request.getWaybillCache().setRoadCode(response.getRoadCode());
                    }
                } else {
                    if (response != null) {
                        this.logger.info("城配拦截未通过" + response.toString());
                    }
                    throw new SortingCheckException(SortingResponse.CODE_29212, SortingResponse.MESSAGE_29212);
                }

            }
            String firstPackageSchedule = default_schedule;

            this.logger.info("箱号的派车单号为：" + firstPackageSchedule + " ，此单的派车单号为：" + response.getScheduleCode());
            if (! firstPackageSchedule.equals(response.getScheduleCode())) {
                throw new SortingCheckException(SortingResponse.CODE_29212, SortingResponse.MESSAGE_29212);
            }
        }
        chain.doFilter(request, chain);
    }

    private TransBillScheduleResponse checkScheduleBill(TransBillScheduleRequest request) {
        TransBillScheduleResponse response = new TransBillScheduleResponse();

        if (request != null && StringUtils.isNotBlank(request.getBoxCode()) && StringUtils.isNotBlank(request.getWaybillCode())) {
            try {
                response.setBoxCode(request.getBoxCode());
                response.setWaybillCode(request.getWaybillCode());
                response.setScheduleCode(transBillScheduleService.queryScheduleCode(request.getWaybillCode()));
                response.setSameScheduleBill(transBillScheduleService.checkSameScheduleBill(request));
                response.setRoadCode(transBillScheduleService.queryTruckSpotByWaybillCode(request.getWaybillCode()));
            } catch (Exception e) {
                this.logger.error("派车单信息校验失败：{}", request.toString(), e);
            }
        }
        return response;
    }
}