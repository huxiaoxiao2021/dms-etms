package com.jd.bluedragon.distribution.reverse.service;

import com.jd.bluedragon.distribution.abnormalwaybill.domain.AbnormalWayBill;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.etms.waybill.dto.BigWaybillDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 逆向换单打印(德邦特殊逻辑)
 */
@Service("reversePrintServiceDpk")
public class ReversePrintServiceDpkImpl extends ReversePrintServiceImpl {

    @Autowired
    private ReverseSpareEclp reverseSpareEclp;

    public void checkAbnormalOperation(String wayBillCode, Integer siteCode, BigWaybillDto waybillDto, InvokeResult<Boolean> result) {
        AbnormalWayBill abnormalWayBill = abnormalWayBillService.getAbnormalWayBillByWayBillCode(wayBillCode, siteCode);
        if (abnormalWayBill != null && wayBillCode.equals(abnormalWayBill.getWaybillCode())) {
            reverseSpareEclp.checkIsPureMatch(waybillDto.getWaybill().getWaybillCode(), waybillDto.getWaybill().getWaybillSign(), result);
        }
    }

}