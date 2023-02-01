package com.jd.bluedragon.distribution.abnormalwaybill.service;

import com.jd.bluedragon.distribution.abnormalwaybill.domain.AbnormalWaybillDiff;

import java.util.List;

public interface AbnormalWaybillDiffService {
    void importExcel(List<AbnormalWaybillDiff> dataList);
}
