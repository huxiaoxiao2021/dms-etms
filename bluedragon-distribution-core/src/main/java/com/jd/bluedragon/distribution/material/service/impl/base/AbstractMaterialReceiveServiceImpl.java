package com.jd.bluedragon.distribution.material.service.impl.base;

import com.jd.bluedragon.distribution.material.domain.DmsMaterialSend;

import java.util.List;

/**
 * @ClassName AbstractMaterialReceiveServiceImpl
 * @Description
 * @Author wyh
 * @Date 2020/3/22 16:42
 **/
public abstract class AbstractMaterialReceiveServiceImpl extends AbstractMaterialBaseServiceImpl {

    @Override
    protected Boolean checkSendParam(List<DmsMaterialSend> materialSends) {
        return true;
    }

    @Override
    protected Boolean sendBeforeOperation(List<DmsMaterialSend> materialSends) {
        return true;
    }

    @Override
    protected Boolean sendAfterOperation(List<DmsMaterialSend> materialSends) {
        return true;
    }
}
