package com.jd.bluedragon.distribution.wss.dto;

import java.util.List;
import java.util.Map;

public class SealCarResultDto {

    private final List<SealCarDto> sealCarDtos;
    private final Map<String, String> disableSendCode;

    public SealCarResultDto(List<SealCarDto> sealCarDtos, Map<String, String> disableSendCode) {
        this.sealCarDtos = sealCarDtos;
        this.disableSendCode = disableSendCode;
    }

    public Map<String, String> getDisableSendCode() {
        return disableSendCode;
    }

    public List<SealCarDto> getSealCarDtos() {
        return sealCarDtos;
    }
}
