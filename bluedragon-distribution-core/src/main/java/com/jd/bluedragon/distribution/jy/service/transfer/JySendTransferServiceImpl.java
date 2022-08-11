package com.jd.bluedragon.distribution.jy.service.transfer;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.common.dto.send.request.TransferSendTaskReq;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("jySendTransferService")
public class JySendTransferServiceImpl implements JySendTransferService{



    @Override
    @JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWEB, jKey = "DMSWEB.JySendTransferServiceImpl.transferSendBatch", mState = {JProEnum.TP, JProEnum.FunctionError})
    public InvokeResult transferSendBatch(TransferSendTaskReq transferSendTaskReq) {
        InvokeResult result =new InvokeResult();
        if (transferSendTaskReq.getSameWayFlag()){

        }
        else {

        }
        return result;
    }
}
