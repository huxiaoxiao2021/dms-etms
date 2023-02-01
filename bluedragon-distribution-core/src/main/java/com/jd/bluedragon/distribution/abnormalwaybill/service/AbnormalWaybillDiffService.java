package com.jd.bluedragon.distribution.abnormalwaybill.service;

import com.jd.bluedragon.distribution.abnormalwaybill.domain.AbnormalWaybillDiff;

import java.util.List;

public interface AbnormalWaybillDiffService {
    void importExcel(List<AbnormalWaybillDiff> dataList);

    List<AbnormalWaybillDiff> query(AbnormalWaybillDiff abnormalWaybillDiff);

    List<AbnormalWaybillDiff> queryCache(AbnormalWaybillDiff abnormalWaybillDiff);

    void save(String waybillCodeC,String waybillCodeE,String type);

    void delByWaybillCodeE(String waybillCodeE);
    void delByWaybillCodeC(String waybillCodeC);

    void updateByWaybillCodeE(String waybillCodeE,String type);
    void updateByWaybillCodeC(String waybillCodeC,String type);
}
