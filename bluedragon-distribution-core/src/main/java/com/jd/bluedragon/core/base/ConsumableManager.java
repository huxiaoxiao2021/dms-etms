package com.jd.bluedragon.core.base;

import erp.ql.station.api.dto.WrapBillManageDto;

import java.util.List;

public interface ConsumableManager {

    Boolean checkConsumable(List<String> consumableBarcodes, String Erp);
}
