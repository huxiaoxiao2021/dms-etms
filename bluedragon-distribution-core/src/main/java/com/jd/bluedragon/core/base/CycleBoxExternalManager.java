package com.jd.bluedragon.core.base;

import com.jd.ham.cb.domain.external.CycleBoxDTO;
import com.jd.ham.cb.domain.external.CycleBoxQueryDTO;

import java.util.List;

public interface CycleBoxExternalManager {
    /**
     * 调用运单接口获取青流箱明细
     * @param dto
     * @return
     * @throws Exception
     */
    List<CycleBoxDTO> getCycleBoxInfo(CycleBoxQueryDTO dto) throws Exception;

    /**
     * 根据运单号获取青流箱明细
     * @param waybillCode
     * @return
     */
    List<String> getCbUniqueNoByWaybillCode(String waybillCode);
}
