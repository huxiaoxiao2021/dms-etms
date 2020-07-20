package com.jd.bluedragon.distribution.material.service.impl;

import com.jd.bluedragon.core.jmq.producer.DefaultJMQProducer;
import com.jd.bluedragon.distribution.base.service.SiteService;
import com.jd.bluedragon.distribution.material.domain.DmsMaterialReceive;
import com.jd.bluedragon.distribution.material.domain.DmsMaterialSend;
import com.jd.bluedragon.distribution.material.dto.TagInOutMessage;
import com.jd.bluedragon.distribution.material.service.RecyclingBoxInOutOperationService;
import com.jd.bluedragon.distribution.material.service.impl.base.AbstractMaterialBaseServiceImpl;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.jmq.common.message.Message;
import com.jd.ql.basic.dto.BaseStaffSiteOrgDto;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author lijie
 * @date 2020/5/27 15:46
 */
@Service("recyclingBoxInOutOperationService")
public class RecyclingBoxInOutOperationServiceImpl extends AbstractMaterialBaseServiceImpl implements RecyclingBoxInOutOperationService {

    @Autowired
    @Qualifier("tagInOutProducer")
    private DefaultJMQProducer tagInOutProducer;

    @Autowired
    private SiteService siteService;

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
        List<TagInOutMessage> tagInOutMessages = new ArrayList<>();
        for (DmsMaterialReceive materialReceive : materialReceives) {
            tagInOutMessages.add(this.convertMaterialReceive2Message(materialReceive));
        }

        sendInOutMQ(tagInOutMessages);
        return true;
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
        List<TagInOutMessage> tagInOutMessages = new ArrayList<>(materialSends.size());
        for (DmsMaterialSend materialSend : materialSends) {
            tagInOutMessages.add(this.convertMaterialSend2Message(materialSend));
        }

        sendInOutMQ(tagInOutMessages);

        return true;
    }

    private TagInOutMessage convertMaterialReceive2Message(DmsMaterialReceive materialReceive) {
        TagInOutMessage tagInOutMessage = new TagInOutMessage();
        tagInOutMessage.setTagNo(materialReceive.getMaterialCode());
        tagInOutMessage.setDmsId(materialReceive.getCreateSiteCode().intValue());
        tagInOutMessage.setDmsName(getSiteName(materialReceive.getCreateSiteCode()));
        tagInOutMessage.setErpUserCode(materialReceive.getCreateUserErp());
        tagInOutMessage.setErpUserName(materialReceive.getCreateUserName());
        tagInOutMessage.setInOutType(1);
        tagInOutMessage.setOperateTime(new Date());

        return tagInOutMessage;
    }

    private TagInOutMessage convertMaterialSend2Message(DmsMaterialSend materialSend) {
        TagInOutMessage tagInOutMessage = new TagInOutMessage();
        tagInOutMessage.setTagNo(materialSend.getMaterialCode());
        tagInOutMessage.setDmsId(materialSend.getCreateSiteCode().intValue());
        tagInOutMessage.setDmsName(getSiteName(materialSend.getCreateSiteCode()));
        tagInOutMessage.setErpUserCode(materialSend.getCreateUserErp());
        tagInOutMessage.setErpUserName(materialSend.getCreateUserName());
        tagInOutMessage.setInOutType(2);
        tagInOutMessage.setOperateTime(new Date());

        return tagInOutMessage;
    }

    private void sendInOutMQ(List<TagInOutMessage> tagInOutMessages) {
        List<Message> messages = new ArrayList<>();
        for (TagInOutMessage tagInOutMessage : tagInOutMessages) {
            Message message = new Message();
            message.setTopic(tagInOutProducer.getTopic());
            message.setText(JsonHelper.toJson(tagInOutMessage));
            message.setBusinessId(tagInOutMessage.getTagNo());
            messages.add(message);
        }
        tagInOutProducer.batchSendOnFailPersistent(messages);
    }

    private String getSiteName(Long createSiteCode) {
        BaseStaffSiteOrgDto baseStaffSiteOrgDto = siteService.getSite(createSiteCode.intValue());
        return null == baseStaffSiteOrgDto ? StringUtils.EMPTY : baseStaffSiteOrgDto.getSiteName();
    }
}
