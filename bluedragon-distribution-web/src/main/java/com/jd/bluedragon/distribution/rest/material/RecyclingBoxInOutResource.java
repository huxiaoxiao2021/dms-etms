package com.jd.bluedragon.distribution.rest.material;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.distribution.api.JdResponse;
import com.jd.bluedragon.distribution.api.request.material.recyclingbox.RecyclingBoxInOutboundRequest;
import com.jd.bluedragon.distribution.api.response.material.recyclingbox.RecyclingBoxInOutResponse;
import com.jd.bluedragon.distribution.base.service.SiteService;
import com.jd.bluedragon.distribution.command.JdResult;
import com.jd.bluedragon.distribution.material.domain.DmsMaterialReceive;
import com.jd.bluedragon.distribution.material.domain.DmsMaterialSend;
import com.jd.bluedragon.distribution.material.enums.MaterialReceiveTypeEnum;
import com.jd.bluedragon.distribution.material.enums.MaterialSendTypeEnum;
import com.jd.bluedragon.distribution.material.enums.MaterialTypeEnum;
import com.jd.bluedragon.distribution.material.util.MaterialServiceFactory;
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
 * @Description 青流箱相关Rest接口
 * @author lijie
 * @date 2020/5/26 16:40
 */
@Controller
@Path(Constants.REST_URL)
@Consumes({MediaType.APPLICATION_JSON})
@Produces({MediaType.APPLICATION_JSON})
public class RecyclingBoxInOutResource {

    @Autowired
    private SiteService siteService;

    private static final Logger LOGGER = LoggerFactory.getLogger(RecyclingBoxInOutResource.class);

    @Autowired
    private MaterialServiceFactory materialServiceFactory;

    private static final int DEFAULT_RECEIVE_NUM = 1;
    private static final byte SEND_MODE = MaterialServiceFactory.MaterialSendModeEnum.MATERIAL_TAG_SEND.getCode();

    @POST
    @Path("/recyclingBox/inbound")
    public JdResult<RecyclingBoxInOutResponse> recyclingBoxInbound(RecyclingBoxInOutboundRequest request) {

        JdResult<RecyclingBoxInOutResponse> response = new JdResult<>();
        response.toSuccess();

        //青流箱入库参数校验
        response = this.checkParam(request);
        if (!response.isSucceed()) {
            return response;
        }

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("青流箱入库参数. req:[{}]", JsonHelper.toJson(request));
        }

        try {
            List<DmsMaterialReceive> materialReceives = new ArrayList<>();
            BaseStaffSiteOrgDto baseStaffSiteOrgDto = siteService.getSite(request.getSiteCode());
            for (String tagNo : request.getTagNos()) {
                materialReceives.add(this.createMaterialReceiveFromRequest(tagNo, baseStaffSiteOrgDto, request));
            }
            JdResult<Boolean> result = materialServiceFactory.findMaterialOperationService(SEND_MODE)
                    .saveMaterialReceive(materialReceives, false);
            response.setCode(result.getCode());
            response.setMessage(result.getMessage());
        } catch (Exception e) {
            LOGGER.error("青流箱入库失败. req:[{}]", JsonHelper.toJson(request), e);
            response.setCode(JdResponse.CODE_INTERNAL_ERROR);
            response.setMessage(JdResponse.MESSAGE_SERVICE_ERROR);
        }

        return response;
    }

    @POST
    @Path("/recyclingBox/outbound")
    public JdResult<RecyclingBoxInOutResponse> recyclingBoxOutbound(RecyclingBoxInOutboundRequest request) {
        JdResult<RecyclingBoxInOutResponse> response = new JdResult<>();
        response.toSuccess();

        //青流箱出库参数校验
        response = this.checkParam(request);
        if (!response.isSucceed()) {
            return response;
        }

        try {
            List<DmsMaterialSend> materialSends = new ArrayList<>();
            BaseStaffSiteOrgDto baseStaffSiteOrgDto = siteService.getSite(request.getSiteCode());
            for (String tagNo : request.getTagNos()) {
                materialSends.add(this.createMaterialSendFromRequest(tagNo, baseStaffSiteOrgDto, request));
            }
            JdResult<Boolean> result = materialServiceFactory.findMaterialOperationService(SEND_MODE)
                    .saveMaterialSend(materialSends, false);
            response.setCode(result.getCode());
            response.setMessage(result.getMessage());
        } catch (Exception e) {
            LOGGER.error("青流箱出库失败. req:[{}]", JsonHelper.toJson(request), e);
            response.setCode(JdResponse.CODE_INTERNAL_ERROR);
            response.setMessage(JdResponse.MESSAGE_SERVICE_ERROR);
        }

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("青流箱出库参数. req:[{}]", JsonHelper.toJson(request));
        }

        return  response;
    }

    public JdResult<RecyclingBoxInOutResponse> checkParam(RecyclingBoxInOutboundRequest request) {

        JdResult<RecyclingBoxInOutResponse> response = new JdResult<>();
        response.toSuccess();

        if (null == request || CollectionUtils.isEmpty(request.getTagNos())
                || null == request.getSiteCode() || null == request.getUserCode()) {
            response.setCode(JdResponse.CODE_PARAM_ERROR);
            response.setMessage(JdResponse.MESSAGE_PARAM_ERROR);
        }

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("青流箱入库参数. req:[{}]", JsonHelper.toJson(request));
        }

        return response;
    }



    private DmsMaterialReceive createMaterialReceiveFromRequest(String tagNo,
                                                                BaseStaffSiteOrgDto baseStaffSiteOrgDto,
                                                                RecyclingBoxInOutboundRequest request) {
        DmsMaterialReceive materialReceive = new DmsMaterialReceive();
        materialReceive.setMaterialCode(tagNo);
        materialReceive.setMaterialType(MaterialTypeEnum.TAG_NO.getCode());
        materialReceive.setCreateSiteCode(request.getSiteCode().longValue());
        materialReceive.setCreateSiteType(baseStaffSiteOrgDto.getSiteType());
        materialReceive.setReceiveCode(StringUtils.EMPTY);
        materialReceive.setReceiveType(MaterialReceiveTypeEnum.RECEIVE_BY_SINGLE_MATERIAL.getCode());
        materialReceive.setReceiveNum(DEFAULT_RECEIVE_NUM);
        materialReceive.setCreateUserErp(request.getUserErp());
        materialReceive.setCreateUserName(request.getUserName());
        materialReceive.setUpdateUserErp(request.getUserErp());
        materialReceive.setUpdateUserName(request.getUserName());

        return materialReceive;
    }

    private DmsMaterialSend createMaterialSendFromRequest(String tagNo,
                                                          BaseStaffSiteOrgDto baseStaffSiteOrgDto,
                                                          RecyclingBoxInOutboundRequest request) {
        DmsMaterialSend materialSend = new DmsMaterialSend();
        materialSend.setMaterialCode(tagNo);
        materialSend.setMaterialType(MaterialTypeEnum.TAG_NO.getCode());
        materialSend.setCreateSiteCode(request.getSiteCode().longValue());
        materialSend.setCreateSiteType(baseStaffSiteOrgDto.getSiteType());
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
}
