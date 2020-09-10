package com.jd.bluedragon.distribution.material.service.impl;

import com.alibaba.fastjson.JSON;
import com.jd.bluedragon.core.jmq.producer.DefaultJMQProducer;
import com.jd.bluedragon.distribution.command.JdResult;
import com.jd.bluedragon.distribution.cyclebox.domain.BoxMaterialRelationMQ;
import com.jd.bluedragon.distribution.material.service.CycleMaterialNoticeService;
import com.jd.bluedragon.distribution.receive.domain.CenConfirm;
import com.jd.bluedragon.utils.BusinessHelper;
import com.jd.bluedragon.utils.JsonHelper;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

/**
 * @ClassName CycleMaterialNoticeServiceImpl
 * @Description
 * @Author wyh
 * @Date 2020/8/24 10:48
 **/
@Service
public class CycleMaterialNoticeServiceImpl implements CycleMaterialNoticeService {

    @Autowired
    @Qualifier("deliverGoodsNoticeSendMQ")
    private DefaultJMQProducer deliverGoodsNoticeSendMQ;

    @Autowired
    @Qualifier("dmsSortingPackageProducer")
    private DefaultJMQProducer sortingPackageSendMQ;

    @Override
    public JdResult<Boolean> deliverySendGoodsMessage(BoxMaterialRelationMQ relationMQ) {
        JdResult<Boolean> result = new JdResult<>();
        result.toSuccess();
        String boxCode = relationMQ.getBoxCode();
        if (BusinessHelper.isBoxcode(boxCode)) {
            deliverGoodsNoticeSendMQ.sendOnFailPersistent(boxCode, JsonHelper.toJson(relationMQ));
        }
        return result;
    }

    @Override
    public JdResult<Boolean> sendSortingMaterialMessage(BoxMaterialRelationMQ relationMQ) {
        JdResult<Boolean> result = new JdResult<>();
        result.toSuccess();

        sortingPackageSendMQ.sendOnFailPersistent(this.getBusinessId(relationMQ), JsonHelper.toJson(relationMQ));
        return result;
    }

    private String getBusinessId(BoxMaterialRelationMQ relationMQ) {
        String businessId = StringUtils.EMPTY;
        if (CollectionUtils.isNotEmpty(relationMQ.getPackageCode())) {
            businessId = relationMQ.getPackageCode().get(0);
        }
        else if (CollectionUtils.isNotEmpty(relationMQ.getWaybillCode())) {
            businessId = relationMQ.getWaybillCode().get(0);
        }
        return businessId;
    }
}
