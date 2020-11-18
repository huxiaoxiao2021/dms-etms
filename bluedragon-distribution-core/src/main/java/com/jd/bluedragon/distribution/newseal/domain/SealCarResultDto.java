package com.jd.bluedragon.distribution.newseal.domain;

import com.jd.etms.vos.dto.SealCarDto;
import java.util.List;
import java.util.Map;

public class SealCarResultDto {

    private final List<SealCarDto> sealCarDtos;
    private final List<SealCarDto> removeCarDtos;
    private final Map<String, String> disableSendCode;

    public SealCarResultDto(List<SealCarDto> sealCarDtos,List<SealCarDto> removeCarDtos, Map<String, String> disableSendCode) {
        this.sealCarDtos = sealCarDtos;
        this.disableSendCode = disableSendCode;
        this.removeCarDtos=removeCarDtos;
    }

    public Map<String, String> getDisableSendCode() {
        return disableSendCode;
    }

    public List<SealCarDto> getSealCarDtos() {
        return sealCarDtos;
    }

    public List<SealCarDto> getRemoveCarDtos() {
        return removeCarDtos;
    }
}
