package com.jd.bluedragon.distribution.rma.service;

import com.jd.bluedragon.Pager;
import com.jd.bluedragon.distribution.rma.request.RmaHandoverQueryParam;
import com.jd.bluedragon.distribution.rma.domain.RmaHandoverWaybill;
import com.jd.bluedragon.distribution.send.domain.SendDetail;
import com.jd.ql.dms.common.domain.PrintStatusEnum;
import org.springframework.stereotype.Service;

import java.util.List;


/**
 * <p>
 * Created by lixin39 on 2018/9/20.
 */
@Service("rmaHandOverWaybillService")
public class RmaHandOverWaybillServiceImpl implements RmaHandOverWaybillService {


    @Override
    public void add(RmaHandoverWaybill rmaHandoverWaybill) {

    }

    @Override
    public void update(RmaHandoverWaybill rmaHandoverWaybill) {

    }

    @Override
    public Pager<List<RmaHandoverWaybill>> getListWithoutDetail(RmaHandoverQueryParam param, Pager pager) {
        return null;
    }

    @Override
    public RmaHandoverWaybill get(Long id, boolean needDetail) {
        return null;
    }

    @Override
    public Pager<List<RmaHandoverWaybill>> getList(List<Long> ids, boolean needDetail) {
        return null;
    }

    @Override
    public RmaHandoverWaybill getByParams(String waybillCode, Integer createSiteCode, boolean needDetail) {
        return null;
    }

    @Override
    public String getReceiverAddressByWaybillCode(String waybillCode) {
        return null;
    }

    @Override
    public void updatePrintStatus(Long id, PrintStatusEnum status) {

    }

    @Override
    public void addConsumer(SendDetail sendDetail) {

    }
}
