package com.jd.bluedragon.distribution.jy.service.unload;

import com.jd.bluedragon.distribution.jy.unload.JyBizTaskUnloadVehicleStageEntity;

import java.util.List;

public interface JyBizTaskUnloadVehicleStageService {
    String generateStageBizId(String unloadVehicleBizId);
    JyBizTaskUnloadVehicleStageEntity queryCurrentStage(JyBizTaskUnloadVehicleStageEntity condition);

    int insertBatch(List<JyBizTaskUnloadVehicleStageEntity> entityList);

    int insertSelective(JyBizTaskUnloadVehicleStageEntity entity);

    int updateByPrimaryKeySelective(JyBizTaskUnloadVehicleStageEntity condition);
}
