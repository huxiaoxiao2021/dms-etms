package com.jd.bluedragon.distribution.api.request;

import com.jd.bluedragon.distribution.wss.dto.SealCarDto;

import java.io.Serializable;

/**
 * @ProjectName：bluedragon-distribution
 * @Package： com.jd.bluedragon.distribution.api.request
 * @ClassName: TransAbnormalAndDeSealRequest
 * @Description:
 * @Author： wuzuxiang
 * @CreateDate 2022/3/2 18:02
 * @Copyright: Copyright (c)2020 JDL.CN All Right Reserved
 * @Since: JDK 1.8
 * @Version： V1.0
 */
public class TransAbnormalAndDeSealRequest implements Serializable {

    private TransAbnormalDto transAbnormalDto;

    private DeSealCodeRequest deSealCodeRequest;

    public TransAbnormalDto getTransAbnormalDto() {
        return transAbnormalDto;
    }

    public void setTransAbnormalDto(TransAbnormalDto transAbnormalDto) {
        this.transAbnormalDto = transAbnormalDto;
    }

    public DeSealCodeRequest getDeSealCodeRequest() {
        return deSealCodeRequest;
    }

    public void setDeSealCodeRequest(DeSealCodeRequest deSealCodeRequest) {
        this.deSealCodeRequest = deSealCodeRequest;
    }
}
