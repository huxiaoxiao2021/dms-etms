package com.jd.bluedragon.distribution.reprint.service;

import com.jd.bluedragon.distribution.reprint.domain.ReprintRecord;

public interface ReprintRecordService {

    /**
     * 判断运单是否被补打过
     * @param barCode 运单号或包裹号
     * @return true表示补打过，false表示还没有补打过
     */
    boolean isBarCodeRePrinted(String barCode);

    void insertRePrintRecord(ReprintRecord rePrintRecord);
}
