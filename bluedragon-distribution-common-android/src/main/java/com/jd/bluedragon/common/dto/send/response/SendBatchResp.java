package com.jd.bluedragon.common.dto.send.response;

import java.io.Serializable;
import java.util.List;

public class SendBatchResp implements Serializable {
    private static final long serialVersionUID = -200606657361415258L;
    List<SendCodeDto> sendCodeList;

    public List<SendCodeDto> getSendCodeList() {
        return sendCodeList;
    }

    public void setSendCodeList(List<SendCodeDto> sendCodeList) {
        this.sendCodeList = sendCodeList;
    }
}
