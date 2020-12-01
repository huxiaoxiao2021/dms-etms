package com.jd.bluedragon.distribution.material.service.impl;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.core.jmq.producer.DefaultJMQProducer;
import com.jd.bluedragon.distribution.api.request.material.batch.MaterialBatchSendRequest;
import com.jd.bluedragon.distribution.api.response.material.batch.MaterialTypeResponse;
import com.jd.bluedragon.distribution.base.service.SiteService;
import com.jd.bluedragon.distribution.command.JdResult;
import com.jd.bluedragon.distribution.consumable.domain.PackingConsumableInfo;
import com.jd.bluedragon.distribution.consumable.domain.PackingTypeEnum;
import com.jd.bluedragon.distribution.consumable.service.PackingConsumableInfoService;
import com.jd.bluedragon.distribution.material.dao.MaterialSendDao;
import com.jd.bluedragon.distribution.material.dao.MaterialSendFlowDao;
import com.jd.bluedragon.distribution.material.domain.DmsMaterialSend;
import com.jd.bluedragon.distribution.material.dto.MaterialCancelSendDto;
import com.jd.bluedragon.distribution.material.dto.MaterialSendDto;
import com.jd.bluedragon.distribution.material.enums.MaterialSendTypeEnum;
import com.jd.bluedragon.distribution.material.service.SortingMaterialSendService;
import com.jd.bluedragon.distribution.material.service.impl.base.AbstractMaterialSendServiceImpl;
import com.jd.bluedragon.dms.utils.BusinessUtil;
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
public class SortingMaterialSendServiceImpl extends AbstractMaterialSendServiceImpl implements SortingMaterialSendService {

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
    public Boolean checkSendParam(List<DmsMaterialSend> materialSends) {
        if (CollectionUtils.isEmpty(materialSends))
            return false;
        String sendCode = materialSends.get(0).getSendCode();
        if (StringUtils.isBlank(sendCode))
            return false;
        if (!BusinessUtil.isSendCode(sendCode))
            return false;
        return true;
    }

    @Override
    public Boolean sendBeforeOperation(List<DmsMaterialSend> materialSends) {
        return true;
    }

    @Override
    public Boolean sendAfterOperation(List<DmsMaterialSend> materialSends) {

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
    @JProfiler(jKey = "DMS.WEB.SortingMaterialSendServiceImpl.cancelMaterialSendBySendCode", jAppName= Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
    public JdResult<Boolean> cancelMaterialSendBySendCode(MaterialBatchSendRequest request) {
        JdResult<Boolean> result = new JdResult<>();
        result.toSuccess();

        if (null == request || StringUtils.isBlank(request.getSendCode()) || null == request.getSiteCode()){
            result.toError("缺少参数！");
            return result;
        }

        try {

            cancelSendRecord(request);

            cancelSendFlow(request);

            sendCancelMaterialSendMQ(request);

        }
        catch (Exception ex) {
            result.toError("服务器异常!");
            LOGGER.error("Failed to cancel material send by batch code. body:[{}].", JsonHelper.toJson(request), ex);
            throw new RuntimeException("取消发货失败");
        }

        return result;
    }

    private void cancelSendRecord(MaterialBatchSendRequest request) {
        materialSendDao.logicalDeleteBatchSendBySendCode(request.getSendCode(), request.getSiteCode().longValue(), request.getUserErp(), request.getUserName());
    }

    private void cancelSendFlow(MaterialBatchSendRequest request) {
        materialSendFlowDao.logicalDeleteSendFlowBySendCode(request.getSendCode(), request.getSiteCode().longValue(), request.getUserErp(), request.getUserName());
    }

    private void sendCancelMaterialSendMQ(MaterialBatchSendRequest request) {
        MaterialCancelSendDto dto = new MaterialCancelSendDto();
        dto.setBatchCode(request.getSendCode());
        dto.setCreateSiteCode(request.getSiteCode().longValue());
        dto.setCreateSiteName(getSiteName(request.getSiteCode().longValue()));
        dto.setErpUserCode(request.getUserErp());
        dto.setErpUserName(request.getUserName());
        dto.setOperateTime(System.currentTimeMillis());

        cancelMaterialSendMQProducer.sendOnFailPersistent(dto.getBatchCode(), JsonHelper.toJson(dto));
    }

    @Override
    @JProfiler(jKey = "DMS.WEB.SortingMaterialSendServiceImpl.listSortingMaterialType", jAppName= Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
    public JdResult<List<MaterialTypeResponse>> listSortingMaterialType(MaterialBatchSendRequest request) {
        JdResult<List<MaterialTypeResponse>> result = new JdResult<>();
        result.toSuccess();

        if (null == request) {
            result.toError("缺少参数");
            return result;
        }

        List<PackingConsumableInfo> packingInfos = packingConsumableInfoService.listByTypeCode(PackingTypeEnum.TY010.getTypeCode());
        List<MaterialTypeResponse> materialTypeList = new ArrayList<>();
        if (!CollectionUtils.isEmpty(packingInfos)) {
            for (PackingConsumableInfo packingInfo : packingInfos) {
                MaterialTypeResponse response = new MaterialTypeResponse();
                response.setMaterialCode(packingInfo.getCode());
                response.setMaterialName(packingInfo.getName());
                materialTypeList.add(response);
            }
        }
        result.setData(materialTypeList);

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("sorting material types:[{}]", JsonHelper.toJson(materialTypeList));
        }

        return result;
    }
}
