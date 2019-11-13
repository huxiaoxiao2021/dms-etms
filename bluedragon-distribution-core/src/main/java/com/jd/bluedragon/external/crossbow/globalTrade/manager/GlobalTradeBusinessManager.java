package com.jd.bluedragon.external.crossbow.globalTrade.manager;

import com.jd.bluedragon.distribution.api.response.LoadBillReportResponse;
import com.jd.bluedragon.distribution.globaltrade.domain.PreLoadBill;
import com.jd.bluedragon.external.crossbow.AbstractCrossbowManager;

/**
 * <p>
 *     全球购业务，调用卓志的预装载接口实现类
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
