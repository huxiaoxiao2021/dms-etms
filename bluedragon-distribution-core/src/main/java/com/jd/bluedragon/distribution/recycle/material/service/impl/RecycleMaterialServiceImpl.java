package com.jd.bluedragon.distribution.recycle.material.service.impl;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.core.base.BaseMajorManager;
import com.jd.bluedragon.distribution.api.request.material.collectionbag.CollectionBagRequest;
import com.jd.bluedragon.distribution.api.request.material.recyclingbox.RecyclingBoxInOutboundRequest;
import com.jd.bluedragon.distribution.api.request.material.warmbox.WarmBoxInOutBaseRequest;
import com.jd.bluedragon.distribution.api.request.material.warmbox.WarmBoxInboundRequest;
import com.jd.bluedragon.distribution.api.request.material.warmbox.WarmBoxOutboundRequest;
import com.jd.bluedragon.distribution.api.response.material.recyclingbox.RecyclingBoxInOutResponse;
import com.jd.bluedragon.distribution.api.response.material.warmbox.WarmBoxInOutResponse;
import com.jd.bluedragon.distribution.base.service.SiteService;
import com.jd.bluedragon.distribution.box.service.BoxService;
import com.jd.bluedragon.distribution.command.JdResult;
import com.jd.bluedragon.distribution.material.domain.DmsMaterialReceive;
import com.jd.bluedragon.distribution.material.domain.DmsMaterialSend;
import com.jd.bluedragon.distribution.material.enums.MaterialReceiveTypeEnum;
import com.jd.bluedragon.distribution.material.enums.MaterialSendTypeEnum;
import com.jd.bluedragon.distribution.material.enums.MaterialTypeEnum;
import com.jd.bluedragon.distribution.material.util.MaterialServiceFactory;
import com.jd.bluedragon.distribution.recycle.material.domain.RecycleBasketEntity;
import com.jd.bluedragon.distribution.recycle.material.domain.RecycleBasketPrintInfo;
import com.jd.bluedragon.distribution.recycle.material.enums.MaterialStatusEnum;
import com.jd.bluedragon.distribution.recycle.material.enums.PrintTypeEnum;
import com.jd.bluedragon.distribution.recycle.material.enums.TransStatusEnum;
import com.jd.bluedragon.distribution.recycle.material.service.RecycleMaterialService;
import com.jd.bluedragon.dms.utils.BusinessUtil;
import com.jd.bluedragon.sdk.common.dto.ApiResult;
import com.jd.bluedragon.sdk.modules.recyclematerial.RecycleMaterialJsfService;
import com.jd.bluedragon.sdk.modules.recyclematerial.dto.RecycleMaterial;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.ql.basic.dto.BaseStaffSiteOrgDto;
import com.jd.ql.dms.common.domain.JdResponse;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.jd.bluedragon.distribution.recycle.material.enums.MaterialTypeEnum.BASKET;

@Service("recycleMaterialService")
public class RecycleMaterialServiceImpl implements RecycleMaterialService {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    @Autowired
    BoxService boxService;
    @Autowired
    BaseMajorManager baseMajorManager;
    @Autowired
    RecycleMaterialJsfService recycleMaterialJsfService;

    @Autowired
    private SiteService siteService;

    @Autowired
    private MaterialServiceFactory materialServiceFactory;

    private static final int DEFAULT_RECEIVE_NUM = 1;

    private static final byte COLLECTION_BAG_SEND = MaterialServiceFactory.MaterialSendModeEnum.COLLECTION_BAG_SEND.getCode();
    
    private static final int DEFAULT_SEND_NUM = 1;

    private static final byte WARM_BOX_SEND = MaterialServiceFactory.MaterialSendModeEnum.WARM_BOX_SEND.getCode();

    private static final byte MATERIAL_TAG_SEND = MaterialServiceFactory.MaterialSendModeEnum.MATERIAL_TAG_SEND.getCode();

    @Override
    @JProfiler(jKey = "dms.web.RecycleMaterialServiceImpl.getPrintInfo", jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
    public JdResponse<RecycleBasketPrintInfo> getPrintInfo(RecycleBasketEntity recycleBasketEntity) {
        // 首打印
        if(PrintTypeEnum.PRINT.getCode() == recycleBasketEntity.getPrintType()){
            return generateRecycleBasketPrintInfo(recycleBasketEntity);
        }
        // 补打
        return getReprintInfo(recycleBasketEntity);
    }

    @Override
    public JdResult<Boolean> CollectionBagOperationSend(CollectionBagRequest request) {
        JdResult<Boolean> response = new JdResult<>();
        response.toSuccess();
        // 参数校验
        response = this.CollectionCheckRequestParam(request, true);
        if (!response.isSucceed()) {
            return response;
        }

        if (logger.isDebugEnabled()) {
            logger.debug("集包袋发空袋参数. req:[{}]", JsonHelper.toJson(request));
        }
        try {
            List<DmsMaterialSend> materialSends = new ArrayList<>();
            BaseStaffSiteOrgDto createSite = siteService.getSite(request.getSiteCode());
            BaseStaffSiteOrgDto receiveSite = siteService.getSite(request.getReceiveSiteCode().intValue());
            for (String collectionBagCode : request.getCollectionBagCodes()) {
                materialSends.add(this.createMaterialSendFromRequest(collectionBagCode, createSite, receiveSite, request));
            }
            long startTime = System.currentTimeMillis();

            JdResult<Boolean> ret = materialServiceFactory.findMaterialOperationService(COLLECTION_BAG_SEND)
                    .saveMaterialSend(materialSends, false);

            long endTime = System.currentTimeMillis();
            response.setCode(ret.getCode());
            response.setMessage(ret.getMessage());

            if (logger.isInfoEnabled()) {
                logger.info("集包袋发空袋参数结果. time:[{}], resp:[{}]", endTime - startTime, JsonHelper.toJson(response));
            }
        }
        catch (Exception ex) {
            logger.error("集包袋发空袋参数失败. req:[{}]", JsonHelper.toJson(request), ex);
            response.setCode(com.jd.bluedragon.distribution.api.JdResponse.CODE_INTERNAL_ERROR);
            response.setMessage(com.jd.bluedragon.distribution.api.JdResponse.MESSAGE_SERVICE_ERROR);
        }
        return response;    
    }

    private DmsMaterialSend createMaterialSendFromRequest(String collectionBagCode,
                                                          BaseStaffSiteOrgDto createSite,
                                                          BaseStaffSiteOrgDto receiveSite,
                                                          CollectionBagRequest request) {
        DmsMaterialSend send = new DmsMaterialSend();
        send.setMaterialCode(collectionBagCode);
        send.setMaterialType(MaterialTypeEnum.COLLECTION_BAG.getCode());
        send.setCreateSiteCode(request.getSiteCode().longValue());
        send.setCreateSiteType(createSite.getSiteType());
        send.setSendCode(StringUtils.EMPTY);
        send.setSendType(MaterialSendTypeEnum.SEND_BY_SINGLE_MATERIAL.getCode());
        send.setSendNum(1);
        send.setCreateUserErp(request.getUserErp());
        send.setCreateUserName(request.getUserName());
        send.setUpdateUserErp(request.getUserErp());
        send.setUpdateUserName(request.getUserName());
        send.setReceiveSiteCode(request.getReceiveSiteCode());
        send.setReceiveSiteType(receiveSite.getSiteType());
        return send;
    }
    
    private JdResult<Boolean> CollectionCheckRequestParam(CollectionBagRequest request, boolean send) {
        JdResult<Boolean> response = new JdResult<>();
        response.toSuccess();

        if (null == request
                || org.apache.commons.collections.CollectionUtils.isEmpty(request.getCollectionBagCodes())
                || null == request.getSiteCode()
                || null == request.getUserCode()) {
            response.setCode(com.jd.bluedragon.distribution.api.JdResponse.CODE_PARAM_ERROR);
            response.setMessage(com.jd.bluedragon.distribution.api.JdResponse.MESSAGE_PARAM_ERROR);
            return response;
        }

        for (String collectionBagCode : request.getCollectionBagCodes()) {
            if (!BusinessUtil.isCollectionBag(collectionBagCode)) {
                response.toError("非法的集包袋号:" + collectionBagCode);
                return response;
            }
        }

        if (send) {
            if (null == request.getReceiveSiteCode() || request.getReceiveSiteCode() <= 0 || StringUtils.isBlank(request.getReceiveSiteName())) {
                response.toError("目的网点不能为空!");
                return response;
            }
        }

        return response;
    }
    
    @Override
    public JdResult<WarmBoxInOutResponse> warmBoxOutbound(WarmBoxOutboundRequest request) {
        JdResult<WarmBoxInOutResponse> response = new JdResult<>();
        response.toSuccess();
        // 参数校验
        response = this.warmBoxCheckRequestParam(request);
        if (!response.isSucceed()) {
            return response;
        }

        if (logger.isDebugEnabled()) {
            logger.debug("保温箱出库参数. req:[{}]", JsonHelper.toJson(request));
        }
        try {
            String sendCode = null != request.getBoardCode() ? request.getBoardCode() : StringUtils.EMPTY;
            List<DmsMaterialSend> materialSends = new ArrayList<>();
            BaseStaffSiteOrgDto baseStaffSiteOrgDto = siteService.getSite(request.getSiteCode());
            for (String warmBoxCode : request.getWarmBoxCodes()) {
                materialSends.add(this.createMaterialSendFromRequest(warmBoxCode, sendCode, baseStaffSiteOrgDto, request));
            }
            long startTime = System.currentTimeMillis();

            boolean saveFlow = true;
            JdResult<Boolean> ret = materialServiceFactory.findMaterialOperationService(WARM_BOX_SEND)
                    .saveMaterialSend(materialSends, saveFlow);

            long endTime = System.currentTimeMillis();
            response.setCode(ret.getCode());
            response.setMessage(ret.getMessage());

            if (logger.isInfoEnabled()) {
                logger.info("保温箱出库结果. time:[{}], resp:[{}]", endTime - startTime, JsonHelper.toJson(response));
            }
        }
        catch (Exception ex) {
            logger.error("保温箱出库失败. req:[{}]", JsonHelper.toJson(request), ex);
            response.setCode(com.jd.bluedragon.distribution.api.JdResponse.CODE_INTERNAL_ERROR);
            response.setMessage(com.jd.bluedragon.distribution.api.JdResponse.MESSAGE_SERVICE_ERROR);
        }
        return response;
    }

    private DmsMaterialSend createMaterialSendFromRequest(String warmBoxCode,
                                                          String sendCode,
                                                          BaseStaffSiteOrgDto baseStaffSiteOrgDto,
                                                          WarmBoxOutboundRequest request) {
        DmsMaterialSend send = new DmsMaterialSend();
        send.setMaterialCode(warmBoxCode);
        send.setMaterialType(MaterialTypeEnum.WARM_BOX.getCode());
        send.setCreateSiteCode(request.getSiteCode().longValue());
        send.setCreateSiteType(baseStaffSiteOrgDto.getSiteType());
        send.setSendCode(sendCode);
        if (StringUtils.isEmpty(sendCode)) {
            send.setSendType(MaterialSendTypeEnum.SEND_BY_SINGLE_MATERIAL.getCode());
        }
        else {
            send.setSendType(MaterialSendTypeEnum.SEND_BY_CONTAINER.getCode());
        }
        send.setSendNum(DEFAULT_SEND_NUM);
        send.setCreateUserErp(request.getUserErp());
        send.setCreateUserName(request.getUserName());
        send.setUpdateUserErp(request.getUserErp());
        send.setUpdateUserName(request.getUserName());

        send.setReceiveSiteCode(0L);
        send.setReceiveSiteType(0);

        return send;
    }
    
    private JdResult<WarmBoxInOutResponse> warmBoxCheckRequestParam(WarmBoxInOutBaseRequest request) {
        JdResult<WarmBoxInOutResponse> response = new JdResult<>();
        response.toSuccess();

        if (null == request
                || org.apache.commons.collections.CollectionUtils.isEmpty(request.getWarmBoxCodes())
                || null == request.getSiteCode()
                || null == request.getUserCode()) {
            response.setCode(com.jd.bluedragon.distribution.api.JdResponse.CODE_PARAM_ERROR);
            response.setMessage(com.jd.bluedragon.distribution.api.JdResponse.MESSAGE_PARAM_ERROR);
            return response;
        }
        if (!StringUtils.isEmpty(request.getBoardCode()) && !BusinessUtil.isBoardCode(request.getBoardCode())) {
            response.toError("板号输入错误！");
            return response;
        }
        for (String warmBoxCode : request.getWarmBoxCodes()) {
            if (!BusinessUtil.isWarmBoxCode(warmBoxCode)) {
                response.toError("非法的保温箱号:" + warmBoxCode);
                return response;
            }
        }

        return response;
    }


    public JdResult<RecyclingBoxInOutResponse> recyclingBoxCheckParam(RecyclingBoxInOutboundRequest request) {

        JdResult<RecyclingBoxInOutResponse> response = new JdResult<>();
        response.toSuccess();

        if (null == request || org.apache.commons.collections.CollectionUtils.isEmpty(request.getTagNos())
                || null == request.getSiteCode() || null == request.getUserCode()) {
            response.setCode(com.jd.bluedragon.distribution.api.JdResponse.CODE_PARAM_ERROR);
            response.setMessage(com.jd.bluedragon.distribution.api.JdResponse.MESSAGE_PARAM_ERROR);
        }

        if (logger.isDebugEnabled()) {
            logger.debug("青流箱入库参数. req:[{}]", JsonHelper.toJson(request));
        }

        return response;
    }
    
    @Override
    public JdResult<RecyclingBoxInOutResponse> recyclingBoxOutbound(RecyclingBoxInOutboundRequest request) {
        JdResult<RecyclingBoxInOutResponse> response = new JdResult<>();
        response.toSuccess();

        //青流箱出库参数校验
        response = this.recyclingBoxCheckParam(request);
        if (!response.isSucceed()) {
            return response;
        }

        try {
            List<DmsMaterialSend> materialSends = new ArrayList<>();
            BaseStaffSiteOrgDto baseStaffSiteOrgDto = siteService.getSite(request.getSiteCode());
            for (String tagNo : request.getTagNos()) {
                materialSends.add(this.createMaterialRecyclingBoxSendFromRequest(tagNo, baseStaffSiteOrgDto, request));
            }
            JdResult<Boolean> result = materialServiceFactory.findMaterialOperationService(MATERIAL_TAG_SEND)
                    .saveMaterialSend(materialSends, false);
            response.setCode(result.getCode());
            response.setMessage(result.getMessage());
        } catch (Exception e) {
            logger.error("青流箱出库失败. req:[{}]", JsonHelper.toJson(request), e);
            response.setCode(com.jd.bluedragon.distribution.api.JdResponse.CODE_INTERNAL_ERROR);
            response.setMessage(com.jd.bluedragon.distribution.api.JdResponse.MESSAGE_SERVICE_ERROR);
        }

        if (logger.isDebugEnabled()) {
            logger.debug("青流箱出库参数. req:[{}]", JsonHelper.toJson(request));
        }

        return  response;
    }

    private DmsMaterialSend createMaterialRecyclingBoxSendFromRequest(String tagNo,
                                                          BaseStaffSiteOrgDto baseStaffSiteOrgDto,
                                                          RecyclingBoxInOutboundRequest request) {
        DmsMaterialSend materialSend = new DmsMaterialSend();
        materialSend.setMaterialCode(tagNo);
        materialSend.setMaterialType(MaterialTypeEnum.TAG_NO.getCode());
        materialSend.setCreateSiteCode(request.getSiteCode().longValue());
        if (baseStaffSiteOrgDto != null) {
            materialSend.setCreateSiteType(baseStaffSiteOrgDto.getSiteType());
        }
        materialSend.setSendCode(StringUtils.EMPTY);
        materialSend.setSendType(MaterialSendTypeEnum.SEND_BY_SINGLE_MATERIAL.getCode());
        materialSend.setSendNum(DEFAULT_RECEIVE_NUM);
        materialSend.setCreateUserErp(request.getUserErp());
        materialSend.setCreateUserName(request.getUserName());
        materialSend.setUpdateUserErp(request.getUserErp());
        materialSend.setUpdateUserName(request.getUserName());

        materialSend.setReceiveSiteType(0);
        materialSend.setReceiveSiteCode(0L);

        return materialSend;
    }
    
    private JdResponse<RecycleBasketPrintInfo> generateRecycleBasketPrintInfo(RecycleBasketEntity recycleBasketEntity){
        JdResponse<RecycleBasketPrintInfo> response = new JdResponse<>();
        List<String> codes = boxService.generateRecycleBasketCode(recycleBasketEntity.getQuantity());
        if(CollectionUtils.isEmpty(codes)){
            logger.error("周转筐打印生成编码失败");
            response.toError("周转筐打印生成编码失败，请稍后重试，或联系分拣小秘!");
            return response;
        }
        BaseStaffSiteOrgDto baseStaffSiteOrgDto = baseMajorManager.getBaseSiteBySiteId(recycleBasketEntity.getCreateSiteCode());
        if(baseStaffSiteOrgDto == null){
            logger.error("周转筐打印,根据站点id:{}获取站点信息为空", recycleBasketEntity.getCreateSiteCode());
            response.toError("获取站点信息失败，请稍后重试，或联系分拣小秘!");
            return response;
        }
        //保存到循环物资表
        ApiResult<Integer> addResult = addRecycleMaterials(codes, recycleBasketEntity, baseStaffSiteOrgDto);
        if(!addResult.isSucceed()){
            logger.error("周转筐打印,保存周转筐信息失败：{}", addResult.getMessage());
            response.toFail(addResult.getMessage());
            return response;
        }
        //返回打印信息
        RecycleBasketPrintInfo printInfo = new RecycleBasketPrintInfo();
        printInfo.setRecycleBasketCodes(codes);
        printInfo.setOrgName(baseStaffSiteOrgDto.getOrgName());
        printInfo.setCreateSiteName(baseStaffSiteOrgDto.getSiteName());
        printInfo.setOrgAndSiteName(baseStaffSiteOrgDto.getOrgName() + "-" + baseStaffSiteOrgDto.getSiteName());
        response.setData(printInfo);
        return response;
    }

    private ApiResult<Integer> addRecycleMaterials(List<String> codes, RecycleBasketEntity recycleBasketEntity,
                                                   BaseStaffSiteOrgDto baseStaffSiteOrgDto){
        List<RecycleMaterial> list = new ArrayList<>(codes.size());
        for(String code: codes){
            RecycleMaterial recycleMaterial = new RecycleMaterial();
            recycleMaterial.setMaterialStatus(MaterialStatusEnum.NORMAL.getCode());
            recycleMaterial.setMaterialCode(code);
            recycleMaterial.setMaterialType(BASKET.getCode());
            recycleMaterial.setImpSiteCode(baseStaffSiteOrgDto.getSiteCode());
            recycleMaterial.setImpSiteName(baseStaffSiteOrgDto.getSiteName());
            recycleMaterial.setImpOperatorErp(recycleBasketEntity.getUserErp());
            recycleMaterial.setTransStatus(TransStatusEnum.AT_THE_SITE.getCode());
            recycleMaterial.setOrgId(baseStaffSiteOrgDto.getOrgId());
            recycleMaterial.setOrgName(baseStaffSiteOrgDto.getOrgName());
            recycleMaterial.setCurrentSiteCode(baseStaffSiteOrgDto.getSiteCode());
            recycleMaterial.setCurrentSiteName(baseStaffSiteOrgDto.getSiteName());
            recycleMaterial.setOperationTime(new Date());
            recycleMaterial.setOperatorErp(recycleBasketEntity.getUserErp());
            recycleMaterial.setCreateUser(recycleBasketEntity.getUserErp());
            list.add(recycleMaterial);
        }
        return recycleMaterialJsfService.batchInsertRecycleMaterial(list);
    }

    private  JdResponse<RecycleBasketPrintInfo> getReprintInfo(RecycleBasketEntity recycleBasketEntity){
        JdResponse<RecycleBasketPrintInfo> response = new JdResponse<>();

        ApiResult<RecycleMaterial> recycleMaterialApiResult = recycleMaterialJsfService
                .findByMaterialCode(recycleBasketEntity.getRecycleBasketCode());
        if(!recycleMaterialApiResult.isSucceed()){
            logger.error("周转筐补打根据编码:{}查周转筐信息失败：{}", recycleBasketEntity.getRecycleBasketCode(),
                    recycleMaterialApiResult.getMessage());
            response.toFail("根据编码查周转筐信息失败，请稍后重试！");
            return response;
        }
        RecycleMaterial recycleMaterial = recycleMaterialApiResult.getData();
        if(recycleMaterial == null){
            logger.error("周转筐补打根据编码:{}未查到周转筐信息", recycleBasketEntity.getRecycleBasketCode());
            response.toFail("根据编码未查到周转筐信息，请检查编码是否正确！");
            return response;
        }

        if(!recycleBasketEntity.getCreateSiteCode().equals(recycleMaterial.getCurrentSiteCode())){
            logger.error("周转筐补打根据编码:{}查到周转筐信息的", recycleBasketEntity.getRecycleBasketCode());
            response.toFail("该周转筐目前所属[{}],和你绑定的分拣中心不一致，您不能操作补打！");
            return response;
        }
        RecycleBasketPrintInfo printInfo = new RecycleBasketPrintInfo();
        //查询最新的名称
        BaseStaffSiteOrgDto baseStaffSiteOrgDto = baseMajorManager.getBaseSiteBySiteId(recycleMaterial.getCurrentSiteCode());
        String siteName = recycleMaterial.getCurrentSiteName();
        if(baseStaffSiteOrgDto != null){
            siteName = baseStaffSiteOrgDto.getSiteName();
        }
        printInfo.setCreateSiteName(siteName);
        printInfo.setOrgName(recycleMaterial.getOrgName());
        printInfo.setOrgAndSiteName(recycleMaterial.getOrgName() + "-" + siteName);
        List<String> codes = new ArrayList<>();
        codes.add(recycleMaterial.getMaterialCode());
        printInfo.setRecycleBasketCodes(codes);
        response.setData(printInfo);
        return  response;
    }
}
