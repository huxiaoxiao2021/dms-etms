package com.jd.bluedragon.distribution.material.service.impl.base;

import com.jd.bluedragon.distribution.material.domain.DmsMaterialReceive;

import java.util.List;

/**
 * @ClassName AbstractMaterialSendServiceImpl
 * @Description
 * @Author wyh
 * @Date 2020/3/22 16:39
 **/
public abstract class AbstractMaterialSendServiceImpl extends AbstractMaterialBaseServiceImpl{

    @Override
    protected Boolean checkReceiveParam(List<DmsMaterialReceive> materialReceives) {
        return true;
    }

    @Override
    protected Boolean receiveBeforeOperation(List<DmsMaterialReceive> materialReceives) {
        return true;
    }

    @Override
    protected Boolean receiveAfterOperation(List<DmsMaterialReceive> materialReceives) {
        return true;
    }
}
