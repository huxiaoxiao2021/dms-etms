package com.jd.bluedragon.distribution.jy.service.inspection;

import com.jd.bluedragon.distribution.jy.dto.unload.trust.PackageArriveAutoInspectionDto;
import com.jd.bluedragon.distribution.jy.dto.unload.trust.RecycleMaterialAutoInspectionBoxDto;
import com.jd.bluedragon.distribution.jy.dto.unload.trust.RecycleMaterialAutoInspectionDto;
import com.jd.bluedragon.distribution.jy.dto.unload.trust.RecycleMaterialAutoInspectionPackageDto;

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
    void packageArriveCarAutoInspection(PackageArriveAutoInspectionDto packageArriveAutoInspectionDto);

    /**
     * 循环物资进场触发自动验货
     * @param dto
     */
    void recycleMaterialEnterSiteAutoInspection(RecycleMaterialAutoInspectionDto dto);

    /**
     * 循环物资内关联箱自动验货拆分包裹维度
     * @param mqBody
     */
    void recycleMaterialBoxAutoInspection(RecycleMaterialAutoInspectionBoxDto mqBody);

    /**
     * 循环物资内关联包裹维度自动验货
     * @param mqBody
     */
    void recycleMaterialPackageAutoInspection(RecycleMaterialAutoInspectionPackageDto mqBody);

}
