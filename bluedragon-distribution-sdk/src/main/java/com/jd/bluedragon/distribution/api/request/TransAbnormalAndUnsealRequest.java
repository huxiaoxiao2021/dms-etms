package com.jd.bluedragon.distribution.api.request;

import com.jd.bluedragon.distribution.wss.dto.SealCarDto;

import java.io.Serializable;

/**
 * @ProjectName：bluedragon-distribution
 * @Package： com.jd.bluedragon.distribution.api.request
 * @ClassName: TransAbnormalAndUnseal
 * @Description:
 * @Author： wuzuxiang
 * @CreateDate 2022/3/2 18:04
 * @Copyright: Copyright (c)2020 JDL.CN All Right Reserved
 * @Since: JDK 1.8
 * @Version： V1.0
 */
public class TransAbnormalAndUnsealRequest implements Serializable {

    private TransAbnormalDto transAbnormalDto;

    private SealCarDto sealCarDto;

    public TransAbnormalDto getTransAbnormalDto() {
        return transAbnormalDto;
    }

    public void setTransAbnormalDto(TransAbnormalDto transAbnormalDto) {
        this.transAbnormalDto = transAbnormalDto;
    }

    public SealCarDto getSealCarDto() {
        return sealCarDto;
    }

    public void setSealCarDto(SealCarDto sealCarDto) {
        this.sealCarDto = sealCarDto;
    }
}
