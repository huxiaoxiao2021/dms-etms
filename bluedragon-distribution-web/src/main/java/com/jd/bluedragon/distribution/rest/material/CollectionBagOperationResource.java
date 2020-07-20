package com.jd.bluedragon.distribution.rest.material;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.distribution.api.JdResponse;
import com.jd.bluedragon.distribution.api.request.material.collectionbag.CollectionBagRequest;
import com.jd.bluedragon.distribution.api.request.material.warmbox.WarmBoxInboundRequest;
import com.jd.bluedragon.distribution.api.request.material.warmbox.WarmBoxOutboundRequest;
import com.jd.bluedragon.distribution.api.response.material.warmbox.WarmBoxInOutResponse;
import com.jd.bluedragon.distribution.base.service.SiteService;
import com.jd.bluedragon.distribution.command.JdResult;
import com.jd.bluedragon.distribution.material.domain.DmsMaterialReceive;
import com.jd.bluedragon.distribution.material.domain.DmsMaterialSend;
import com.jd.bluedragon.distribution.material.enums.MaterialReceiveTypeEnum;
import com.jd.bluedragon.distribution.material.enums.MaterialSendTypeEnum;
import com.jd.bluedragon.distribution.material.enums.MaterialTypeEnum;
import com.jd.bluedragon.distribution.material.util.MaterialServiceFactory;
import com.jd.bluedragon.dms.utils.BusinessUtil;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.ql.basic.dto.BaseStaffSiteOrgDto;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.ArrayList;
import java.util.List;

/**
 * @ClassName CollectionBagOperationResource
 * @Description
 * @Author wyh
 * @Date 2020/6/30 19:13
 **/
@Controller
@Path(Constants.REST_URL)
@Consumes({MediaType.APPLICATION_JSON})
@Produces({MediaType.APPLICATION_JSON})
public class CollectionBagOperationResource {

    private static final Logger LOGGER = LoggerFactory.getLogger(CollectionBagOperationResource.class);

    private static final byte SEND_MODE = MaterialServiceFactory.MaterialSendModeEnum.COLLECTION_BAG_SEND.getCode();

    @Autowired
    private SiteService siteService;

    @Autowired
    private MaterialServiceFactory materialServiceFactory;

    @POST
    @Path("/collectionBag/receive")
    public JdResult<Boolean> receive(CollectionBagRequest request) {

        JdResult<Boolean> response = new JdResult<>();
        response.toSuccess();
        // 参数校验
        response = this.checkRequestParam(request, false);
        if (!response.isSucceed()) {
            return response;
        }

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("集包袋收空袋入参. req:[{}]", JsonHelper.toJson(request));
        }

        try {
            List<DmsMaterialReceive> materialReceives = new ArrayList<>();
            BaseStaffSiteOrgDto baseStaffSiteOrgDto = siteService.getSite(request.getSiteCode());
            for (String collectionBagCode : request.getCollectionBagCodes()) {
                materialReceives.add(this.createMaterialReceiveFromRequest(collectionBagCode, baseStaffSiteOrgDto, request));
            }

            long startTime = System.currentTimeMillis();

            JdResult<Boolean> ret = materialServiceFactory.findMaterialOperationService(SEND_MODE)
                    .saveMaterialReceive(materialReceives, false);

            long endTime = System.currentTimeMillis();
            response.setCode(ret.getCode());
            response.setMessage(ret.getMessage());

            if (LOGGER.isInfoEnabled()) {
                LOGGER.info("集包袋收空袋结果. time:[{}], resp:[{}]", endTime - startTime, JsonHelper.toJson(response));
            }
        }
        catch (Exception ex) {
            LOGGER.error("集包袋收空袋失败. req:[{}]", JsonHelper.toJson(request), ex);
            response.setCode(JdResponse.CODE_INTERNAL_ERROR);
            response.setMessage(JdResponse.MESSAGE_SERVICE_ERROR);
        }

        return response;
    }

    @POST
    @Path("/collectionBag/send")
    public JdResult<Boolean> send(CollectionBagRequest request) {
        JdResult<Boolean> response = new JdResult<>();
        response.toSuccess();
        // 参数校验
        response = this.checkRequestParam(request, true);
        if (!response.isSucceed()) {
            return response;
        }

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("集包袋发空袋参数. req:[{}]", JsonHelper.toJson(request));
        }
        try {
            List<DmsMaterialSend> materialSends = new ArrayList<>();
            BaseStaffSiteOrgDto createSite = siteService.getSite(request.getSiteCode());
            BaseStaffSiteOrgDto receiveSite = siteService.getSite(request.getReceiveSiteCode().intValue());
            for (String collectionBagCode : request.getCollectionBagCodes()) {
                materialSends.add(this.createMaterialSendFromRequest(collectionBagCode, createSite, receiveSite, request));
            }
            long startTime = System.currentTimeMillis();

            JdResult<Boolean> ret = materialServiceFactory.findMaterialOperationService(SEND_MODE)
                    .saveMaterialSend(materialSends, false);

            long endTime = System.currentTimeMillis();
            response.setCode(ret.getCode());
            response.setMessage(ret.getMessage());

            if (LOGGER.isInfoEnabled()) {
                LOGGER.info("集包袋发空袋参数结果. time:[{}], resp:[{}]", endTime - startTime, JsonHelper.toJson(response));
            }
        }
        catch (Exception ex) {
            LOGGER.error("集包袋发空袋参数失败. req:[{}]", JsonHelper.toJson(request), ex);
            response.setCode(JdResponse.CODE_INTERNAL_ERROR);
            response.setMessage(JdResponse.MESSAGE_SERVICE_ERROR);
        }
        return response;
    }

    private JdResult<Boolean> checkRequestParam(CollectionBagRequest request, boolean send) {
        JdResult<Boolean> response = new JdResult<>();
        response.toSuccess();

        if (null == request
                || CollectionUtils.isEmpty(request.getCollectionBagCodes())
                || null == request.getSiteCode()
                || null == request.getUserCode()) {
            response.setCode(JdResponse.CODE_PARAM_ERROR);
            response.setMessage(JdResponse.MESSAGE_PARAM_ERROR);
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

    private DmsMaterialReceive createMaterialReceiveFromRequest(String collectionBagCode,
                                                                BaseStaffSiteOrgDto baseStaffSiteOrgDto,
                                                                CollectionBagRequest request) {
        DmsMaterialReceive receive = new DmsMaterialReceive();
        receive.setCreateSiteCode(request.getSiteCode().longValue());
        receive.setCreateSiteType(baseStaffSiteOrgDto.getSiteType());
        receive.setMaterialCode(collectionBagCode);
        receive.setMaterialType(MaterialTypeEnum.COLLECTION_BAG.getCode());
        receive.setReceiveCode(StringUtils.EMPTY);
        receive.setReceiveType(MaterialReceiveTypeEnum.RECEIVE_BY_SINGLE_MATERIAL.getCode());
        receive.setReceiveNum(1);
        receive.setCreateUserErp(request.getUserErp());
        receive.setCreateUserName(request.getUserName());
        receive.setUpdateUserErp(request.getUserErp());
        receive.setUpdateUserName(request.getUserName());

        return receive;
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
}
