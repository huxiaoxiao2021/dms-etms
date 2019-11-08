package com.jd.bluedragon.external.crossbow.globalTrade.manager;

import com.jd.bluedragon.distribution.api.response.LoadBillReportResponse;
import com.jd.bluedragon.distribution.globaltrade.domain.PreLoadBill;
import com.jd.bluedragon.external.crossbow.AbstractCrossbowManager;

/**
 * <p>
 *
 * @author wuzuxiang
 * @since 2019/11/8
 **/
public class GlobalTradeBusinessManager extends AbstractCrossbowManager<PreLoadBill, LoadBillReportResponse> {

    @Override
    protected PreLoadBill getMyRequestBody(Object condition) {
        return (PreLoadBill) condition;
    }
}
