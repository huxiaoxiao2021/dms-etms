package com.jd.bluedragon.core.base;

import com.jd.tms.data.dto.CargoDetailDto;
import com.jd.tms.data.dto.CommonDto;

import java.util.List;

public interface CargoDetailServiceManager {

    /**
     * 运输接口：根据批次号获取批次下包裹以及运单维度信息。
     * @param CargoDetailDto var1
     * @param int offset
     * @param int rowlimit
     * @return CommonDto<List<CargoDetailDto>>
     */
    public CommonDto<List<CargoDetailDto>> getCargoDetailInfoByBatchCode(CargoDetailDto cargoDetailDto, int offset, int rowlimit);

    public List<String> querySealCarPackageList(Integer createSiteCode, String batchCode);
}
