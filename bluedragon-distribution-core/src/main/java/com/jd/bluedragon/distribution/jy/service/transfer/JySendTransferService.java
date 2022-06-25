package com.jd.bluedragon.distribution.jy.service.transfer;

import com.jd.bluedragon.common.dto.send.request.TransferSendTaskReq;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;

public interface JySendTransferService {
    InvokeResult transferSendBatch(TransferSendTaskReq transferSendTaskReq);
}
