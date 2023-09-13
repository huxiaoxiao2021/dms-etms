package com.jd.bluedragon.distribution.cycleBox.service;

import com.jd.bluedragon.distribution.cycleBox.domain.BoxMaterialRelationDto;
import com.jd.bluedragon.distribution.jsf.domain.InvokeResult;

public interface CycleBoxJsfService {

    /**
     * 根据箱号获取箱号绑定的集包袋
     * @param materialCode
     * @return
     */
    BoxMaterialRelationDto getBoxMaterialRelationByMaterialCode(String materialCode);


    InvokeResult<Boolean> unBindBoxMaterialRelation(String boxCode, String materialCode, Integer createSiteCode);

    /**
     * 根据箱号查询集包袋绑定信息
     * @param boxCode
     * @return
     */
    InvokeResult<BoxMaterialRelationDto> getBoxMaterialRelation(String boxCode);
}
