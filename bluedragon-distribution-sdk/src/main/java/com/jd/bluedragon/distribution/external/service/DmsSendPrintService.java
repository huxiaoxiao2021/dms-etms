package com.jd.bluedragon.distribution.external.service;

import com.jd.bluedragon.distribution.batch.domain.BatchSend;
import com.jd.bluedragon.distribution.sendprint.domain.BatchSendInfoResponse;

import java.util.List;

/**
 * <p>
 * Created by lixin39 on 2018/5/9.
 */
public interface DmsSendPrintService {

    /**
     * 获取批次下已发货的箱号、原包数量
     *
     * @param batchSends
     * @return
     */
    BatchSendInfoResponse carrySendCarInfo(List<BatchSend> batchSends);

}
