package com.jd.bluedragon.distribution.material.service.impl;

import com.jd.bluedragon.distribution.material.domain.DmsMaterialReceive;
import com.jd.bluedragon.distribution.material.domain.DmsMaterialSend;
import com.jd.bluedragon.distribution.material.service.WarmBoxInOutOperationService;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @ClassName WarmBoxInOutOperationServiceImpl
 * @Description
 * @Author wyh
 * @Date 2020/3/13 18:20
 **/
@Service("warmBoxInOutOperationService")
public class WarmBoxInOutOperationServiceImpl extends AbstractMaterialBaseServiceImpl implements WarmBoxInOutOperationService {

    @Override
    protected Boolean checkReceiveParam(List<DmsMaterialReceive> materialReceives) {
        return CollectionUtils.isEmpty(materialReceives);
    }

    @Override
    protected Boolean receiveBeforeOperation(List<DmsMaterialReceive> materialReceives) {
        return null;
    }

    @Override
    protected Boolean receiveAfterOperation(List<DmsMaterialReceive> materialReceives) {
        return null;
    }

    @Override
    protected Boolean checkSendParam(List<DmsMaterialSend> materialSends) {
        return CollectionUtils.isEmpty(materialSends);
    }

    @Override
    protected Boolean sendBeforeOperation(List<DmsMaterialSend> materialSends) {
        return null;
    }

    @Override
    protected Boolean sendAfterOperation(List<DmsMaterialSend> materialSends) {
        return null;
    }
}
