package com.jd.bluedragon.distribution.material.service.impl;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.jd.bluedragon.core.base.BaseMajorManager;
import com.jd.bluedragon.core.jmq.producer.DefaultJMQProducer;
import com.jd.bluedragon.distribution.cyclebox.domain.BoxMaterialRelationEnum;
import com.jd.bluedragon.distribution.cyclebox.domain.BoxMaterialRelationMQ;
import com.jd.bluedragon.distribution.material.domain.DmsMaterialReceive;
import com.jd.bluedragon.distribution.material.domain.DmsMaterialSend;
import com.jd.bluedragon.distribution.material.service.CollectionBagOperationService;
import com.jd.bluedragon.distribution.material.service.impl.base.AbstractMaterialBaseServiceImpl;
import com.jd.jmq.common.message.Message;
import com.jd.ql.basic.dto.BaseStaffSiteOrgDto;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * @ClassName CollectionBagOperationServiceImpl
 * @Description
 * @Author wyh
 * @Date 2020/6/30 16:18
 **/
@Service("collectionBagOperationService")
public class CollectionBagOperationServiceImpl extends AbstractMaterialBaseServiceImpl implements CollectionBagOperationService {

    @Autowired
    @Qualifier("cycleMaterialSendMQ")
    private DefaultJMQProducer cycleMaterialSendMQ;

    @Autowired
    private BaseMajorManager baseMajorManager;

    @Override
    protected Boolean checkReceiveParam(List<DmsMaterialReceive> materialReceives) {
        return CollectionUtils.isNotEmpty(materialReceives);
    }

    @Override
    protected Boolean receiveBeforeOperation(List<DmsMaterialReceive> materialReceives) {
        return true;
    }

    @Override
    protected Boolean receiveAfterOperation(List<DmsMaterialReceive> materialReceives) {
        return pushReceiveCycleMaterialBagMQ(materialReceives);
    }

    @Override
    protected Boolean checkSendParam(List<DmsMaterialSend> materialSends) {
        return CollectionUtils.isNotEmpty(materialSends);
    }

    @Override
    protected Boolean sendBeforeOperation(List<DmsMaterialSend> materialSends) {
        return true;
    }

    @Override
    protected Boolean sendAfterOperation(List<DmsMaterialSend> materialSends) {
        return pushSendCycleMaterialBagMQ(materialSends);
    }

    private Boolean pushSendCycleMaterialBagMQ(List<DmsMaterialSend> materialSends) {

        Date operateTime = new Date();
        Long receiveSiteCode = materialSends.get(0).getReceiveSiteCode();
        BaseStaffSiteOrgDto site = baseMajorManager.getBaseSiteBySiteId(receiveSiteCode.intValue());
        String siteName = null == site ? StringUtils.EMPTY : site.getSiteName();

        List<Message> messages = Lists.newArrayListWithExpectedSize(materialSends.size());
        for (DmsMaterialSend send : materialSends) {
            BoxMaterialRelationMQ mqBody = getMqBody(BoxMaterialRelationEnum.SEND_BAG, send.getMaterialCode(), send.getCreateUserName(), send.getCreateSiteCode(), operateTime);
            mqBody.setReceiveSiteCode(receiveSiteCode);
            mqBody.setReceiveSiteName(siteName);
            messages.add(makeMQMessage(mqBody));
        }

        cycleMaterialSendMQ.batchSendOnFailPersistent(messages);

        return Boolean.TRUE;
    }

    private Boolean pushReceiveCycleMaterialBagMQ(List<DmsMaterialReceive> receives) {
        List<Message> messages = Lists.newArrayListWithExpectedSize(receives.size());
        Date operateTime = new Date();
        for (DmsMaterialReceive receive : receives) {
            BoxMaterialRelationMQ mqBody = getMqBody(BoxMaterialRelationEnum.RECEIVE_BAG, receive.getMaterialCode(), receive.getCreateUserName(), receive.getCreateSiteCode(), operateTime);
            messages.add(makeMQMessage(mqBody));
        }

        cycleMaterialSendMQ.batchSendOnFailPersistent(messages);

        return Boolean.TRUE;
    }

    private BoxMaterialRelationMQ getMqBody(BoxMaterialRelationEnum enumType, String materialCode, String userName, Long siteCode, Date operateTime) {
        BoxMaterialRelationMQ mqBody = new BoxMaterialRelationMQ();
        mqBody.setMaterialCode(materialCode);
        mqBody.setBusinessType(enumType.getType());
        mqBody.setOperatorCode(0);
        mqBody.setOperatorName(userName);
        mqBody.setSiteCode(String.valueOf(siteCode));
        mqBody.setOperatorTime(operateTime);

        return mqBody;
    }

    private Message makeMQMessage(BoxMaterialRelationMQ mqBody) {

        Message message = new Message();
        message.setBusinessId(mqBody.getMaterialCode());
        message.setText(JSON.toJSONString(mqBody));
        message.setTopic(cycleMaterialSendMQ.getTopic());

        return message;
    }

}
