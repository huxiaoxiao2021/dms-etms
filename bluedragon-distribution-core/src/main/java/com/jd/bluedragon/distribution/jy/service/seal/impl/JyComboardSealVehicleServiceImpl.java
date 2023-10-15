package com.jd.bluedragon.distribution.jy.service.seal.impl;

import com.jd.bluedragon.common.dto.seal.request.SealVehicleInfoReq;
import com.jd.bluedragon.common.dto.seal.response.JyAppDataSealVo;
import com.jd.bluedragon.common.dto.seal.response.SealVehicleInfoResp;
import com.jd.bluedragon.configuration.DmsConfigManager;
import com.jd.bluedragon.distribution.jy.service.seal.JyAppDataSealService;
import com.jd.bluedragon.distribution.jy.service.seal.JySealVehicleServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author liwenji
 * @description 组板封车
 * @date 2023-10-13 16:00
 */
@Service("JyComboardSealVehicleService")
public class JyComboardSealVehicleServiceImpl extends JySealVehicleServiceImpl {

    @Autowired
    private JyAppDataSealService jyAppDataSealService;

    @Autowired
    DmsConfigManager dmsConfigManager;


    public void setSavedPageData(SealVehicleInfoReq sealVehicleInfoReq, SealVehicleInfoResp sealVehicleInfoResp) {
        JyAppDataSealVo jyAppDataSealVo = jyAppDataSealService.loadSavedPageData(sealVehicleInfoReq.getSendVehicleDetailBizId());
        selectBoardByTmsAndInitWeightVolume(sealVehicleInfoReq, jyAppDataSealVo);
        sealVehicleInfoResp.setSavedPageData(jyAppDataSealVo);
        sealVehicleInfoResp.setBoardLimit(dmsConfigManager.getPropertyConfig().getJyComboardSealBoardListLimit());
    }
}
