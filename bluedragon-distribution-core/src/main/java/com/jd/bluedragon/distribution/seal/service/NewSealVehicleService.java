package com.jd.bluedragon.distribution.seal.service;

import com.jd.etms.vos.dto.CommonDto;
import com.jd.etms.vos.dto.PageDto;
import com.jd.etms.vos.dto.SealCarDto;
import com.jd.etms.vts.dto.VtsTransportResourceDto;

import java.util.List;

public interface NewSealVehicleService {

    public CommonDto<String> seal(List<SealCarDto> sealCars);
    
    public CommonDto<PageDto<SealCarDto>> findSealInfo(SealCarDto request,PageDto<SealCarDto> pageDto);
    
    public CommonDto<String> unseal(List<SealCarDto> sealCars);

    /**
     *VOS查询批次号是否已被封车接口
     * @param batchCode
     * @return
     */
    public CommonDto<Boolean> isBatchCodeHasSealed(String batchCode);

    /**
     * 根据运力编码查询运力编码相关信息
     * @param batchCode
     * @return
     */
    public com.jd.etms.vts.dto.CommonDto<VtsTransportResourceDto> getTransportResourceByTransCode(String batchCode);

    /**
     * 检查批次是否存在
     * @param sendCode
     * @return
     */
    public boolean checkSendIsExsite( String sendCode);

}
