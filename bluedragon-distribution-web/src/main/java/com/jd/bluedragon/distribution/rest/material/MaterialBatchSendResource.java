package com.jd.bluedragon.distribution.rest.material;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.distribution.api.JdResponse;
import com.jd.bluedragon.distribution.api.request.material.warmbox.MaterialBatchSendRequest;
import com.jd.bluedragon.distribution.base.service.SiteService;
import com.jd.bluedragon.distribution.command.JdResult;
import com.jd.bluedragon.distribution.material.domain.DmsMaterialSend;
import com.jd.bluedragon.distribution.material.enums.MaterialSendTypeEnum;
import com.jd.bluedragon.distribution.material.service.SortingMaterialSendService;
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
 * @ClassName MaterialBatchSendResource
 * @Description 物资发货相关Rest接口
 * @Author wyh
 * @Date 2020/3/16 17:37
 **/
@Controller
@Path(Constants.REST_URL)
@Consumes({MediaType.APPLICATION_JSON})
@Produces({MediaType.APPLICATION_JSON})
public class MaterialBatchSendResource {

    private static final Logger LOGGER = LoggerFactory.getLogger(MaterialBatchSendResource.class);

    @Autowired
    private MaterialServiceFactory materialServiceFactory;

    @Autowired
    private SortingMaterialSendService sortingMaterialSendService;

    @Autowired
    private SiteService siteService;

    @POST
    @Path("/batchSend/send")
    public JdResult<Boolean> materialBatchSend(MaterialBatchSendRequest request) {
        JdResult<Boolean> response = new JdResult<>();
        response.toSuccess();

        if (null != request.getSendBusinessType()
                || null == request.getSiteCode()
                || StringUtils.isBlank(request.getBatchCode())
                || CollectionUtils.isEmpty(request.getSendDetails())) {
            response.toError("缺少必要参数");
            return response;
        }

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("物资按批次号发货参数：[{}]", JsonHelper.toJson(request));
        }

        try {
            List<DmsMaterialSend> materialSends = convertRequest2Send(request);
            response = materialServiceFactory.getMaterialSendService(request.getSendBusinessType()).saveMaterialSend(materialSends);
        }
        catch (Exception ex) {
            LOGGER.error("MaterialBatchSendResource-materialBatchSend Failed. req:[{}]", JsonHelper.toJson(request), ex);
            response.setCode(JdResponse.CODE_INTERNAL_ERROR);
            response.setMessage(JdResponse.MESSAGE_SERVICE_ERROR);
        }
        return response;
    }

    @POST
    @Path("/batchSend/cancel")
    public JdResult<Boolean> cancelMaterialBatchSend(MaterialBatchSendRequest request) {
        JdResult<Boolean> response = new JdResult<>();
        response.toSuccess();

        if (null != request.getSendBusinessType()
                || null == request.getSiteCode()
                || StringUtils.isBlank(request.getBatchCode())) {
            response.toError("缺少必要参数");
            return response;
        }

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("物资按批次号取消发货参数：[{}]", JsonHelper.toJson(request));
        }

        try {
            List<DmsMaterialSend> materialSends = convertRequest2Send(request);
            response = sortingMaterialSendService.cancelMaterialSendByBatchCode(request);
        }
        catch (Exception ex) {
            LOGGER.error("MaterialBatchSendResource-cancelMaterialBatchSend Failed. req:[{}]", JsonHelper.toJson(request), ex);
            response.setCode(JdResponse.CODE_INTERNAL_ERROR);
            response.setMessage(JdResponse.MESSAGE_SERVICE_ERROR);
        }
        return response;
    }

    private List<DmsMaterialSend> convertRequest2Send(MaterialBatchSendRequest request) {
        List<DmsMaterialSend> sends = new ArrayList<>(request.getSendDetails().size());
        BaseStaffSiteOrgDto baseStaffSiteOrgDto = siteService.getSite(request.getSiteCode());
        for (MaterialBatchSendRequest.MaterialSendDetail sendDetail : request.getSendDetails()) {
            DmsMaterialSend send = new DmsMaterialSend();
            send.setSendType(MaterialSendTypeEnum.SEND_BY_BATCH.getCode());
            send.setSendCode(request.getBatchCode());
            send.setSendNum(sendDetail.getSendNum());
            send.setMaterialCode(sendDetail.getMaterialTypeCode());
            // TODO 耗材档案的物资类型
            send.setMaterialType((byte)0);
            send.setCreateSiteCode(request.getSiteCode().longValue());
            send.setCreateSiteType(baseStaffSiteOrgDto.getSiteType());
            send.setReceiveSiteType(0);
            send.setReceiveSiteCode(0L);
            send.setCreateUserErp(request.getUserErp());
            send.setCreateUserName(request.getUserName());
            send.setUpdateUserErp(request.getUserErp());
            send.setUpdateUserName(request.getUserName());

            sends.add(send);
        }

        return sends;
    }



}
