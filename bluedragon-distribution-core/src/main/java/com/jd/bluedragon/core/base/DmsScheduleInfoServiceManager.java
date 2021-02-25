package com.jd.bluedragon.core.base;

import com.jd.jp.print.templet.center.sdk.dto.EdnDeliveryReceiptBatchPdfDto;
import com.jd.jp.print.templet.center.sdk.dto.EdnDeliveryReceiptBatchRequest;
import com.jd.ql.dms.common.domain.JdResponse;

public interface DmsScheduleInfoServiceManager {

    public JdResponse<EdnDeliveryReceiptBatchPdfDto> generatePdfUrlByBatchList(EdnDeliveryReceiptBatchRequest param);
}
