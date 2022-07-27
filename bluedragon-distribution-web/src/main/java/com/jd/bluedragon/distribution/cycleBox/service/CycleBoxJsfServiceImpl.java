package com.jd.bluedragon.distribution.cycleBox.service;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.distribution.cycleBox.domain.BoxMaterialRelationDto;
import com.jd.bluedragon.distribution.cyclebox.domain.BoxMaterialRelation;
import com.jd.bluedragon.distribution.cyclebox.service.BoxMaterialRelationService;
import com.jd.bluedragon.distribution.jsf.domain.InvokeResult;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("cycleBoxJsfService")
public class CycleBoxJsfServiceImpl implements CycleBoxJsfService{

    @Autowired
    BoxMaterialRelationService boxMaterialRelationService;

    @Override
    @JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWEB, jKey = "DMS.WEB.CycleBoxJsfServiceImpl.getBoxMaterialRelationByMaterialCode", mState = JProEnum.TP)
    public BoxMaterialRelationDto getBoxMaterialRelationByMaterialCode(String materialCode) {
        BoxMaterialRelation boxMaterialRelation = boxMaterialRelationService.getDataByMaterialCode(materialCode);
        if (boxMaterialRelation == null){
            return null;
        }
        BoxMaterialRelationDto result = new BoxMaterialRelationDto();
        BeanUtils.copyProperties(boxMaterialRelation,result);
        result.setOperatorERP(boxMaterialRelation.getOperatorErp());
        return result;
    }

    @Override
    @JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWEB, jKey = "DMS.WEB.CycleBoxJsfServiceImpl.unBindBoxMaterialRelation", mState = JProEnum.TP)
    public InvokeResult<Boolean> unBindBoxMaterialRelation(String boxCode, String materialCode, Integer createSiteCode) {
        return null;
    }
}
