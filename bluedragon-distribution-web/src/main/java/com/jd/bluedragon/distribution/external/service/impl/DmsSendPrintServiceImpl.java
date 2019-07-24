package com.jd.bluedragon.distribution.external.service.impl;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.distribution.batch.domain.BatchSend;
import com.jd.bluedragon.distribution.external.service.DmsSendPrintService;
import com.jd.bluedragon.distribution.rest.sendprint.SendPrintResource;
import com.jd.bluedragon.distribution.sendprint.domain.BatchSendInfoResponse;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * Created by lixin39 on 2018/11/9.
 */
@Service("dmsSendPrintService")
public class DmsSendPrintServiceImpl implements DmsSendPrintService {

    @Autowired
    @Qualifier("sendPrintResource")
    private SendPrintResource sendPrintResource;

    @Override
    @JProfiler(jKey = "DMSWEB.DmsSendPrintServiceImpl.carrySendCarInfo", mState = {JProEnum.TP, JProEnum.FunctionError}, jAppName = Constants.UMP_APP_NAME_DMSWEB)
    public BatchSendInfoResponse carrySendCarInfo(List<BatchSend> batchSends) {
        return sendPrintResource.carrySendCarInfo(batchSends);
    }
}
