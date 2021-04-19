package com.jd.bluedragon.distribution.cycleBox.service;

import com.jd.bluedragon.distribution.cycleBox.domain.BoxMaterialRelationDto;

public interface CycleBoxJsfService {

    /**
     * 根据箱号获取箱号绑定的集包袋
     * @param materialCode
     * @return
     */
  BoxMaterialRelationDto getBoxMaterialRelationByMaterialCode(String materialCode);

}
