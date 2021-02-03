package com.jd.bluedragon.distribution.box.service;

import com.google.common.collect.Lists;
import com.jd.bluedragon.Constants;
import com.jd.bluedragon.core.base.BaseMajorManager;
import com.jd.bluedragon.distribution.api.JdResponse;
import com.jd.bluedragon.distribution.api.request.BoxRequest;
import com.jd.bluedragon.distribution.api.response.BoxResponse;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.box.domain.BoxDto;
import com.jd.bluedragon.distribution.box.domain.BoxGenReq;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.ql.basic.dto.BaseStaffSiteOrgDto;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;

/**
 * @ClassName DmsBoxUpsertServiceImpl
 * @Description
 * @Author wyh
 * @Date 2021/2/2 19:54
 **/
@Service("dmsBoxUpsertService")
public class DmsBoxUpsertServiceImpl implements DmsBoxUpsertService{

    private static final Logger LOGGER = LoggerFactory.getLogger(DmsBoxUpsertServiceImpl.class);

    private static final int mixBoxType = 0;//不混装
    private static final int transportType = 2;//承运类型：公路

    @Autowired
    private BoxService boxService;

    @Autowired
    private BaseMajorManager baseMajorManager;

    @Override
    @JProfiler(jKey = Constants.UMP_APP_NAME_DMSWEB + ".DmsBoxUpsertService.generateBox", jAppName = Constants.UMP_APP_NAME_DMSWEB ,mState = { JProEnum.Heartbeat, JProEnum.TP })
    public InvokeResult<BoxDto> generateBox(BoxGenReq req) {
        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("开始创建箱号. param:{}", JsonHelper.toJson(req));
        }
        InvokeResult<BoxDto> response = new InvokeResult<>();
        if (null == req
                || null == req.getStartSiteCode()
                || null == req.getDestSiteCode()
                || null == req.getNum() || req.getNum() < 1 || req.getNum() > 100
                || null == req.getBoxType()) {

            response.parameterError("参数校验失败!");
            return response;
        }

        BoxRequest request = convertParam(req);
        boolean useStablePrefixBox = true; // 生成固定BC开头的箱号，用type字段区分箱号类型
        BoxResponse boxResponse = boxService.commonGenBox(request, req.getTenantCode(), true, useStablePrefixBox);
        if (ObjectUtils.notEqual(JdResponse.CODE_OK, boxResponse.getCode())) {
            response.error(boxResponse.getMessage());
            return response;
        }

        response.setData(convertBoxDto(boxResponse, request));
        return response;
    }

    /**
     *
     * @param response
     * @param param
     * @return
     */
    private BoxDto convertBoxDto(BoxResponse response, BoxRequest param) {
        BoxDto dto = new BoxDto();
        if (StringUtils.isNotBlank(response.getRouterText()) && response.getRouterInfo() != null) {
            dto.setRouterText(response.getRouterText());
            dto.setRouter(Lists.newArrayList(Arrays.asList(response.getRouterInfo())));
        }
        dto.setStartSiteName(param.getCreateSiteName());
        dto.setDestSiteName(param.getReceiveSiteName());
        dto.setBoxCodes(Lists.newArrayList(Arrays.asList(response.getBoxCodes().split(Constants.SEPARATOR_COMMA))));
        return dto;
    }

    private BoxRequest convertParam(BoxGenReq request) {
        BoxRequest param = new BoxRequest();
        BaseStaffSiteOrgDto startSite = baseMajorManager.getBaseSiteBySiteId(request.getStartSiteCode());
        BaseStaffSiteOrgDto destSite = baseMajorManager.getBaseSiteBySiteId(request.getDestSiteCode());
        param.setCreateSiteCode(request.getStartSiteCode());
        param.setCreateSiteName(startSite.getSiteName());
        param.setCreateSiteType(startSite.getSiteType().toString());
        param.setReceiveSiteCode(request.getDestSiteCode());
        param.setReceiveSiteName(destSite.getSiteName());
        param.setReceiveSiteType(destSite.getSiteType().toString());
        param.setMixBoxType(mixBoxType);//不混装
        param.setQuantity(request.getNum());
        param.setTransportType(transportType);//默认公路运输
        param.setType(request.getBoxType());
        param.setUserCode(request.getOperatorId());
        param.setUserName(request.getOperatorName());
        return param;
    }
}
