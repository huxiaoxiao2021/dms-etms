package com.jd.bluedragon.distribution.labelPrint.service.impl;

import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.label.domain.farmar.FarmarEntity;
import com.jd.bluedragon.distribution.label.api.LabelPrintJsfService;
import com.jd.bluedragon.distribution.labelPrint.service.LabelPrintService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 标签对外jsf实现
 *
 * @author hujiping
 * @date 2022/8/22 5:24 PM
 */
@Service("labelPrintJsfService")
public class LabelPrintJsfServiceImpl implements LabelPrintJsfService {

    @Autowired
    private LabelPrintService labelPrintService;

    @Override
    public InvokeResult<FarmarEntity> getFarmarInfoByCode(String farmarCode) {
        InvokeResult<FarmarEntity> result = new InvokeResult<FarmarEntity>();
        if(StringUtils.isEmpty(farmarCode)){
            result.parameterError();
            return result;
        }
        return labelPrintService.getFarmarInfo(farmarCode);
    }
}
