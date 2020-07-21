package com.jd.bluedragon.distribution.rest.material;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.distribution.api.JdResponse;
import com.jd.bluedragon.distribution.api.request.material.warmbox.WarmBoxBoardRelationRequest;
import com.jd.bluedragon.distribution.api.request.material.warmbox.WarmBoxInOutBaseRequest;
import com.jd.bluedragon.distribution.api.request.material.warmbox.WarmBoxInboundRequest;
import com.jd.bluedragon.distribution.api.request.material.warmbox.WarmBoxOutboundRequest;
import com.jd.bluedragon.distribution.api.response.material.warmbox.WarmBoxInOutResponse;
import com.jd.bluedragon.distribution.base.service.SiteService;
import com.jd.bluedragon.distribution.command.JdResult;
import com.jd.bluedragon.distribution.material.domain.DmsMaterialReceive;
import com.jd.bluedragon.distribution.material.domain.DmsMaterialRelation;
import com.jd.bluedragon.distribution.material.domain.DmsMaterialSend;
import com.jd.bluedragon.distribution.material.enums.MaterialReceiveTypeEnum;
import com.jd.bluedragon.distribution.material.enums.MaterialSendTypeEnum;
import com.jd.bluedragon.distribution.material.enums.MaterialTypeEnum;
import com.jd.bluedragon.distribution.material.service.WarmBoxInOutOperationService;
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
 * @ClassName WarmBoxInOutResource
 * @Description 保温箱相关Rest接口
 * @Author wyh
 * @Date 2020/2/26 15:17
 **/
@Controller
@Path(Constants.REST_URL)
@Consumes({MediaType.APPLICATION_JSON})
@Produces({MediaType.APPLICATION_JSON})
public class WarmBoxInOutResource {

    private static final Logger LOGGER = LoggerFactory.getLogger(WarmBoxInOutResource.class);

    private static final int DEFAULT_RECEIVE_NUM = 1;
    private static final int DEFAULT_SEND_NUM = 1;
    private static final byte SEND_MODE = MaterialServiceFactory.MaterialSendModeEnum.WARM_BOX_SEND.getCode();

    @Autowired
    private MaterialServiceFactory materialServiceFactory;

    @Autowired
    private WarmBoxInOutOperationService warmBoxInOutOperationService;

    @Autowired
    private SiteService siteService;

    @POST
    @Path("/warmBox/relations")
    public JdResult<WarmBoxInOutResponse> listBoxBoardRelation(WarmBoxBoardRelationRequest request) {

        JdResult<WarmBoxInOutResponse> response = new JdResult<>();
        response.toSuccess();

        if (null == request || StringUtils.isBlank(request.getBoardCode())) {
            response.setCode(JdResponse.CODE_PARAM_ERROR);
            response.setMessage(JdResponse.MESSAGE_PARAM_ERROR);
            return response;
        }

        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("查询保温箱和板的绑定关系. req:[{}]", JsonHelper.toJson(request));
        }

        try {
            JdResult<List<DmsMaterialRelation>> result = warmBoxInOutOperationService.listMaterialRelations(request.getBoardCode());
            response.setCode(result.getCode());
            response.setMessage(result.getMessage());

            if (response.isSucceed() && !CollectionUtils.isEmpty(result.getData())) {
                WarmBoxInOutResponse bodyResp = new WarmBoxInOutResponse();
                bodyResp.setBoardCode(request.getBoardCode());
                List<String> boxCodes = new ArrayList<>();
                for (DmsMaterialRelation relation : result.getData()) {
                    if (relation.getMaterialType() == MaterialTypeEnum.WARM_BOX.getCode()) {
                        boxCodes.add(relation.getMaterialCode());
                    }
                }
                bodyResp.setBoxes(boxCodes);
                response.setData(bodyResp);
            }

            if (LOGGER.isInfoEnabled()) {
                LOGGER.info("查询保温箱和板的绑定关系. resp:[{}]", JsonHelper.toJson(response));
            }
        }
        catch (Exception ex) {
            LOGGER.error("获取保温箱和板的绑定关系失败. req:[{}]", JsonHelper.toJson(request), ex);
            response.setCode(JdResponse.CODE_INTERNAL_ERROR);
            response.setMessage(JdResponse.MESSAGE_SERVICE_ERROR);
        }

        return response;
    }

    @POST
    @Path("/warmBox/inbound")
    public JdResult<WarmBoxInOutResponse> warmBoxInbound(WarmBoxInboundRequest request) {

        JdResult<WarmBoxInOutResponse> response = new JdResult<>();
        response.toSuccess();
        // 参数校验
        response = this.checkRequestParam(request);
        if (!response.isSucceed()) {
            return response;
        }

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("保温箱入库参数. req:[{}]", JsonHelper.toJson(request));
        }

        try {
            String receiveCode = null != request.getBoardCode() ? request.getBoardCode() : StringUtils.EMPTY;
            List<DmsMaterialReceive> materialReceives = new ArrayList<>();
            BaseStaffSiteOrgDto baseStaffSiteOrgDto = siteService.getSite(request.getSiteCode());
            for (String warmBoxCode : request.getWarmBoxCodes()) {
                materialReceives.add(this.createMaterialReceiveFromRequest(warmBoxCode, receiveCode, baseStaffSiteOrgDto, request));
            }

            long startTime = System.currentTimeMillis();

            JdResult<Boolean> ret = materialServiceFactory.findMaterialOperationService(SEND_MODE)
                            .saveMaterialReceive(materialReceives, false);

            long endTime = System.currentTimeMillis();
            response.setCode(ret.getCode());
            response.setMessage(ret.getMessage());

            if (LOGGER.isInfoEnabled()) {
                LOGGER.info("保温箱入库结果. time:[{}], resp:[{}]", endTime - startTime, JsonHelper.toJson(response));
            }
        }
        catch (Exception ex) {
            LOGGER.error("保温箱入库失败. req:[{}]", JsonHelper.toJson(request), ex);
            response.setCode(JdResponse.CODE_INTERNAL_ERROR);
            response.setMessage(JdResponse.MESSAGE_SERVICE_ERROR);
        }

        return response;
    }

    @POST
    @Path("/warmBox/outbound")
    public JdResult<WarmBoxInOutResponse> warmBoxOutbound(WarmBoxOutboundRequest request) {
        JdResult<WarmBoxInOutResponse> response = new JdResult<>();
        response.toSuccess();
        // 参数校验
        response = this.checkRequestParam(request);
        if (!response.isSucceed()) {
            return response;
        }

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("保温箱出库参数. req:[{}]", JsonHelper.toJson(request));
        }
        try {
            String sendCode = null != request.getBoardCode() ? request.getBoardCode() : StringUtils.EMPTY;
            List<DmsMaterialSend> materialSends = new ArrayList<>();
            BaseStaffSiteOrgDto baseStaffSiteOrgDto = siteService.getSite(request.getSiteCode());
            for (String warmBoxCode : request.getWarmBoxCodes()) {
                materialSends.add(this.createMaterialSendFromRequest(warmBoxCode, sendCode, baseStaffSiteOrgDto, request));
            }
            long startTime = System.currentTimeMillis();

            JdResult<Boolean> ret = materialServiceFactory.findMaterialOperationService(SEND_MODE)
                    .saveMaterialSend(materialSends, false);

            long endTime = System.currentTimeMillis();
            response.setCode(ret.getCode());
            response.setMessage(ret.getMessage());

            if (LOGGER.isInfoEnabled()) {
                LOGGER.info("保温箱出库结果. time:[{}], resp:[{}]", endTime - startTime, JsonHelper.toJson(response));
            }
        }
        catch (Exception ex) {
            LOGGER.error("保温箱出库失败. req:[{}]", JsonHelper.toJson(request), ex);
            response.setCode(JdResponse.CODE_INTERNAL_ERROR);
            response.setMessage(JdResponse.MESSAGE_SERVICE_ERROR);
        }
        return response;
    }

    private JdResult<WarmBoxInOutResponse> checkRequestParam(WarmBoxInOutBaseRequest request) {
        JdResult<WarmBoxInOutResponse> response = new JdResult<>();
        response.toSuccess();

        if (null == request
                || CollectionUtils.isEmpty(request.getWarmBoxCodes())
                || null == request.getSiteCode()
                || null == request.getUserCode()) {
            response.setCode(JdResponse.CODE_PARAM_ERROR);
            response.setMessage(JdResponse.MESSAGE_PARAM_ERROR);
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

    private DmsMaterialReceive createMaterialReceiveFromRequest(String warmBoxCode,
                                                                String receiveCode,
                                                                BaseStaffSiteOrgDto baseStaffSiteOrgDto,
                                                                WarmBoxInboundRequest request) {
        DmsMaterialReceive receive = new DmsMaterialReceive();
        receive.setCreateSiteCode(request.getSiteCode().longValue());
        receive.setCreateSiteType(baseStaffSiteOrgDto.getSiteType());
        receive.setMaterialCode(warmBoxCode);
        receive.setMaterialType(MaterialTypeEnum.WARM_BOX.getCode());
        receive.setReceiveCode(receiveCode);
        if (StringUtils.isEmpty(receiveCode)) {
            receive.setReceiveType(MaterialReceiveTypeEnum.RECEIVE_BY_SINGLE_MATERIAL.getCode());
        }
        else {
            receive.setReceiveType(MaterialReceiveTypeEnum.RECEIVE_BY_CONTAINER.getCode());
        }
        receive.setReceiveNum(DEFAULT_RECEIVE_NUM);
        receive.setCreateUserErp(request.getUserErp());
        receive.setCreateUserName(request.getUserName());
        receive.setUpdateUserErp(request.getUserErp());
        receive.setUpdateUserName(request.getUserName());

        return receive;
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
}
