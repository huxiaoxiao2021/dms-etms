package com.jd.bluedragon.distribution.base.service;

import com.jd.etms.waybill.domain.Waybill;
import com.jd.ldop.basic.dto.BasicTraderNeccesaryInfoDTO;

public interface BasicInfoPackService {

   Waybill packBasicInfo(String waybillCode);

    BasicTraderNeccesaryInfoDTO  getBaseTraderNeccesaryInfo(String traderCode);
}
