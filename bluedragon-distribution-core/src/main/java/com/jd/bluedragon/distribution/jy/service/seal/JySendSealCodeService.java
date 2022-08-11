package com.jd.bluedragon.distribution.jy.service.seal;

import com.jd.bluedragon.distribution.jy.send.JySendSealCodeEntity;

import java.util.List;

public interface JySendSealCodeService {
    List<String> selectSealCodeByBizId(String bizId);

    int add(JySendSealCodeEntity entity);

    int addBatch(List<JySendSealCodeEntity> list);

    int countByBiz(String sendVehicleBiz);
}
