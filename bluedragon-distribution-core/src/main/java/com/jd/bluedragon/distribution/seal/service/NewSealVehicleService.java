package com.jd.bluedragon.distribution.seal.service;

import com.jd.etms.vos.dto.CommonDto;
import com.jd.etms.vos.dto.PageDto;
import com.jd.etms.vos.dto.SealCarDto;

import java.util.List;

public interface NewSealVehicleService {

    public CommonDto<String> seal(List<SealCarDto> sealCars);
    
    public CommonDto<PageDto<SealCarDto>> findSealInfo(SealCarDto request,PageDto<SealCarDto> pageDto);
    
    public CommonDto<String> unseal(List<SealCarDto> sealCars);
}
