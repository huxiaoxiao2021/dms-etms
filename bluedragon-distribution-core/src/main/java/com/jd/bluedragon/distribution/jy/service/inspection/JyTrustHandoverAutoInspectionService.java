package com.jd.bluedragon.distribution.jy.service.inspection;

import com.jd.bluedragon.distribution.jy.dto.unload.trust.PackageArriveAutoInspectionDto;

/**
 * 信任交接-自动验货逻辑
 * @Author zhengchengfa
 * @Date 2024/2/29 21:32
 * @Description
 */
public interface JyTrustHandoverAutoInspectionService {
    /**
     * 围栏到车包裹自动验货逻辑
     * @param packageArriveAutoInspectionDto
     * @return true 成功  false 失败
     */
    void packageArriveAndAutoInspection(PackageArriveAutoInspectionDto packageArriveAutoInspectionDto);
}
