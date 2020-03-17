package com.jd.bluedragon.distribution.material.service.impl;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.core.jmq.producer.DefaultJMQProducer;
import com.jd.bluedragon.distribution.api.request.material.warmbox.MaterialBatchSendRequest;
import com.jd.bluedragon.distribution.base.service.SiteService;
import com.jd.bluedragon.distribution.command.JdResult;
import com.jd.bluedragon.distribution.consumable.domain.PackingConsumableInfo;
import com.jd.bluedragon.distribution.consumable.service.PackingConsumableInfoService;
import com.jd.bluedragon.distribution.material.dao.MaterialSendDao;
import com.jd.bluedragon.distribution.material.dao.MaterialSendFlowDao;
import com.jd.bluedragon.distribution.material.domain.DmsMaterialReceive;
import com.jd.bluedragon.distribution.material.domain.DmsMaterialSend;
import com.jd.bluedragon.distribution.material.domain.DmsMaterialSendFlow;
import com.jd.bluedragon.distribution.material.dto.MaterialCancelSendDto;
import com.jd.bluedragon.distribution.material.dto.MaterialSendDto;
import com.jd.bluedragon.distribution.material.enums.MaterialSendTypeEnum;
import com.jd.bluedragon.distribution.material.service.SortingMaterialSendService;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.jmq.common.exception.JMQException;
import com.jd.jmq.common.message.Message;
import com.jd.ql.basic.dto.BaseStaffSiteOrgDto;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;

/**
 * @ClassName SortingMaterialSendServiceImpl
 * @Description
 * @Author wyh
 * @Date 2020/3/16 13:52
 **/
@Service("materialBatchSendService")
public class SortingMaterialSendServiceImpl extends AbstractMaterialBaseServiceImpl implements SortingMaterialSendService {

    private static final Logger LOGGER = LoggerFactory.getLogger(SortingMaterialSendServiceImpl.class);

    @Qualifier("materialSendMQProducer")
    @Autowired
    private DefaultJMQProducer materialSendMQProducer;

    @Qualifier("cancelMaterialSendMQProducer")
    @Autowired
    private DefaultJMQProducer cancelMaterialSendMQProducer;

    @Autowired
    private MaterialSendDao materialSendDao;

    @Autowired
    private MaterialSendFlowDao materialSendFlowDao;

    @Autowired
    private PackingConsumableInfoService packingConsumableInfoService;

    @Autowired
    private SiteService siteService;

    @Override
    protected Boolean checkReceiveParam(List<DmsMaterialReceive> materialReceives) {
        return true;
    }

    @Override
    protected Boolean checkSendParam(List<DmsMaterialSend> materialSends) {
        return CollectionUtils.isEmpty(materialSends);
    }

    @Override
    protected Boolean sendAfterOperation(List<DmsMaterialSend> materialSends) {

        List<MaterialSendDto> mqBodyList = generateMQBody(materialSends);

        sendMaterialMQ(mqBodyList);

        return true;
    }

    private void sendMaterialMQ(List<MaterialSendDto> mqBodyList) {
        List<Message> messages = new ArrayList<>(mqBodyList.size());
        for (MaterialSendDto materialSendDto : mqBodyList) {
            Message message = new Message();
            message.setTopic(materialSendMQProducer.getTopic());
            message.setText(JsonHelper.toJson(materialSendDto));
            message.setBusinessId(materialSendDto.getBatchCode());
            messages.add(message);
        }
        materialSendMQProducer.batchSendOnFailPersistent(messages);
    }

    private List<MaterialSendDto> generateMQBody(List<DmsMaterialSend> materialSends) {

        Map<String, PackingConsumableInfo> packingMap = getPackingInfoMap(materialSends);

        String siteName = getSiteName(materialSends.get(0).getCreateSiteCode());

        List<MaterialSendDto> mqDtos = new ArrayList<>(materialSends.size());
        for (DmsMaterialSend materialSend : materialSends) {
            if (MaterialSendTypeEnum.SEND_BY_BATCH.getCode() == materialSend.getSendType()) {
                MaterialSendDto sendDto = new MaterialSendDto();
                sendDto.setBatchCode(materialSend.getSendCode());
                sendDto.setCreateSiteCode(materialSend.getCreateSiteCode());
                sendDto.setErpUserCode(materialSend.getUpdateUserErp());
                sendDto.setErpUserName(materialSend.getUpdateUserName());
                sendDto.setCreateSiteName(siteName);
                sendDto.setMaterialCode(materialSend.getMaterialCode());

                sendDto.setOperateTime(System.currentTimeMillis());
                sendDto.setSendNum(materialSend.getSendNum());

                BigDecimal totalVolume = BigDecimal.ZERO;
                BigDecimal totalWeight = BigDecimal.ZERO;
                String materialName = StringUtils.EMPTY;
                PackingConsumableInfo packingInfo = packingMap.get(materialSend.getMaterialCode());
                if (null != packingInfo) {
                    totalVolume = packingInfo.getVolume().multiply(new BigDecimal(materialSend.getSendNum()));
                    totalWeight = packingInfo.getWeight().multiply(new BigDecimal(materialSend.getSendNum()));
                    materialName = packingInfo.getName();
                }
                sendDto.setMaterialName(materialName);
                sendDto.setTotalVolume(totalVolume);
                sendDto.setTotalWeight(totalWeight);

                mqDtos.add(sendDto);
            }
        }

        return mqDtos;
    }

    private String getSiteName(Long createSiteCode) {
        BaseStaffSiteOrgDto baseStaffSiteOrgDto = siteService.getSite(createSiteCode.intValue());
        return null == baseStaffSiteOrgDto ? StringUtils.EMPTY : baseStaffSiteOrgDto.getSiteName();
    }

    private Map<String, PackingConsumableInfo> getPackingInfoMap(List<DmsMaterialSend> materialSends) {
        Set<String> materialCodes = new HashSet<>();
        for (DmsMaterialSend send : materialSends) {
            materialCodes.add(send.getMaterialCode());
        }
        Map<String, PackingConsumableInfo> packingMap = new HashMap<>();
        List<PackingConsumableInfo> packingInfos = packingConsumableInfoService.listPackingConsumableInfoByCodes(new ArrayList<>(materialCodes));
        if (!CollectionUtils.isEmpty(packingInfos)) {
            for (PackingConsumableInfo packingInfo : packingInfos) {
                packingMap.put(packingInfo.getCode(), packingInfo);
            }
        }

        return packingMap;
    }

    @Override
    @Transactional
    @JProfiler(jKey = "DMS.WEB.SortingMaterialSendServiceImpl.cancelMaterialSendByBatchCode", jAppName= Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
    public JdResult<Boolean> cancelMaterialSendByBatchCode(MaterialBatchSendRequest request) {
        JdResult<Boolean> result = new JdResult<>();
        result.toSuccess();

        if (null == request || StringUtils.isBlank(request.getBatchCode()) || null == request.getSiteCode()){
            result.toError("缺少参数！");
            return result;
        }

        try {

            createCancelSendFlow(request);

            materialSendDao.logicalDeleteBatchSendBySendCode(request.getBatchCode(), request.getSiteCode().longValue());

            sendCancelMaterialSendMQ(request);

        }
        catch (Exception ex) {
            result.toError("服务器异常!");
            LOGGER.error("Failed to cancel material send by batch code. body:[{}].", JsonHelper.toJson(request), ex);
            throw new RuntimeException("取消发货失败");
        }

        return result;
    }

    private void createCancelSendFlow(MaterialBatchSendRequest request) {
        List<DmsMaterialSend> materialSends = materialSendDao.listBySendCode(request.getBatchCode(), request.getSiteCode().longValue());

        if (CollectionUtils.isEmpty(materialSends)) {
            LOGGER.warn("发货记录已取消. sendCode:[{}], createSite:[{}]", request.getBatchCode(), request.getSiteName());
            return;
        }

        List<DmsMaterialSendFlow> flows = new ArrayList<>(materialSends.size());
        for (DmsMaterialSend materialSend : materialSends) {
            DmsMaterialSendFlow flow = materialSend.convert2SendFlow();
            flow.setCreateUserErp(request.getUserErp());
            flow.setCreateUserName(request.getUserName());
            flow.setUpdateUserErp(request.getUserErp());
            flow.setUpdateUserName(request.getUserName());
            flows.add(flow);
        }
        if (flows.size() <= INSERT_NUMBER_UPPER_LIMIT) {
            materialSendFlowDao.batchInsert(flows);
        }
        else {
            for (int fromIndex = 0; fromIndex < flows.size(); fromIndex = fromIndex + INSERT_NUMBER_UPPER_LIMIT) {
                int toIndex = fromIndex + INSERT_NUMBER_UPPER_LIMIT;
                if (toIndex > flows.size()) {
                    toIndex = flows.size();
                }
                List<DmsMaterialSendFlow> subFlows = flows.subList(fromIndex, toIndex);
                materialSendFlowDao.batchInsert(subFlows);
            }
        }
    }

    private void sendCancelMaterialSendMQ(MaterialBatchSendRequest request) throws JMQException {
        MaterialCancelSendDto dto = new MaterialCancelSendDto();
        dto.setBatchCode(request.getBatchCode());
        dto.setCreateSiteCode(dto.getCreateSiteCode());
        dto.setCreateSiteName(getSiteName(dto.getCreateSiteCode()));
        dto.setErpUserCode(request.getUserErp());
        dto.setErpUserName(request.getUserName());
        dto.setOperateTime(System.currentTimeMillis());

        cancelMaterialSendMQProducer.send(dto.getBatchCode(), JsonHelper.toJson(dto));
    }
}
