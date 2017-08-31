package com.jd.bluedragon.distribution.seal.service;

import com.jd.etms.vos.dto.CommonDto;
import com.jd.etms.vos.dto.PageDto;
import com.jd.etms.vos.dto.SealCarDto;
import com.jd.ql.basic.domain.MainBranchSchedule;

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
    public MainBranchSchedule getMainBranchScheduleByTranCode(String batchCode);

}
