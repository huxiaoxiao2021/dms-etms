package com.jd.bluedragon.distribution.external.gateway.box;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.core.base.BaseMajorManager;
import com.jd.bluedragon.core.jmq.producer.DefaultJMQProducer;
import com.jd.bluedragon.distribution.api.JdResponse;
import com.jd.bluedragon.distribution.api.request.BoxRequest;
import com.jd.bluedragon.distribution.api.response.BoxResponse;
import com.jd.bluedragon.distribution.rest.box.BoxResource;
import com.jd.bluedragon.external.gateway.base.GateWayBaseResponse;
import com.jd.bluedragon.external.gateway.box.BoxGateWayExternalService;
import com.jd.bluedragon.external.gateway.dto.request.BoxGenerateRequest;
import com.jd.bluedragon.external.gateway.dto.request.ThirdBoxCodeMessageVO;
import com.jd.bluedragon.external.gateway.dto.response.BoxDto;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.ql.basic.dto.BaseStaffSiteOrgDto;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * @author : xumigen
 * @date : 2020/1/2
 */
public class BoxGateWayExternalServiceImpl implements BoxGateWayExternalService {

    private static final String boxType = "ECONOMIC_NET_ORDINARY";//经济网箱子类型：普通
    private static final String type = "BC";//经济网箱子类型：普通对应京东类型
    private static final int mixBoxType = 0;//不混装
    private static final int transportType = 2;//承运类型：公路

    /**
     * 箱号resource
     */
    @Autowired
    private BoxResource boxResource;

    @Autowired
    private BaseMajorManager baseMajorManager;
    @Autowired
    @Qualifier(value = "thirdBoxCodeProducer")
    private DefaultJMQProducer thirdBoxCodeProducer;

    private final Logger logger = LoggerFactory.getLogger(BoxGateWayExternalServiceImpl.class);

    @Override
    @JProfiler(jKey = "DMSWEB.BoxGateWayExternalServiceImpl.generateBoxCodes", jAppName = Constants.UMP_APP_NAME_DMSWEB ,mState = {JProEnum.TP})
    public GateWayBaseResponse<BoxDto> generateBoxCodes(BoxGenerateRequest request,String pin) {
        logger.info("生成箱号request[{}]", JsonHelper.toJson(request));
        GateWayBaseResponse gateWayBaseResponse = new GateWayBaseResponse();
        //参数校验
        if(!Constants.TENANT_CODE_ECONOMIC.equals(request.getTenantCode()) || !boxType.equals(request.getBoxType())
                || request.getNum() < 0 || request.getNum() > 100 ){
            gateWayBaseResponse.toError(GateWayBaseResponse.MESSAGE_ERROR);
            return gateWayBaseResponse;
        }

        //箱号生成
        BoxRequest param = convertParam(request, pin);
        BoxResponse response = boxResource.printClientBoxes(param);
        if(!JdResponse.CODE_OK.equals(response.getCode())){
            gateWayBaseResponse.toFail(response.getMessage());
            return gateWayBaseResponse;
        }
        //结果转换
        BoxDto dto = convertBoxDto(response, param);
        gateWayBaseResponse.toSucceed(GateWayBaseResponse.MESSAGE_SUCCESS);
        gateWayBaseResponse.setData(dto);
        //推送箱号给众邮
        pushBoxCode(dto, response);
        return gateWayBaseResponse;
    }

    private void pushBoxCode(BoxDto dto, BoxResponse response) {
        if (dto == null || dto.getBoxCodes() == null || response == null) {
            return;
        }
        for (String boxCode : dto.getBoxCodes()) {
            try {
                ThirdBoxCodeMessageVO message = new ThirdBoxCodeMessageVO();
                message.setBoxCode(boxCode);
                message.setCreateSiteCode(String.valueOf(response.getCreateSiteCode()));
                message.setReceiveSiteCode(String.valueOf(response.getReceiveSiteCode()));
                thirdBoxCodeProducer.sendOnFailPersistent(message.getBoxCode(), JsonHelper.toJson(message));
            } catch (Exception e) {
                logger.error("推送箱号给众邮出错:e={}", e);
            }
        }

    }
    /**
     * dto转换
     * @param response
     * @param param
     * @return
     */
    private BoxDto convertBoxDto(BoxResponse response,BoxRequest param){
        BoxDto dto = new BoxDto();
        if(StringUtils.isNotBlank(response.getRouterText()) && response.getRouterInfo() != null){
            dto.setRouterText(response.getRouterText());
            dto.setRouter(new ArrayList<String>(Arrays.asList(response.getRouterInfo())));
        }
        dto.setEndSiteName(param.getReceiveSiteName());
        dto.setStartSiteName(param.getCreateSiteName());
        dto.setBoxCodes(new ArrayList<String>(Arrays.asList(response.getBoxCodes().split(Constants.SEPARATOR_COMMA))));
        return dto;
    }
    /**
     * 箱号获取
     * @param request 请求参数
     * @param pin 京东PIN码 物流网关会透传过来
     * @return
     */
    private BoxRequest convertParam(BoxGenerateRequest request,String pin){
        BoxRequest param = new BoxRequest();
        BaseStaffSiteOrgDto startSite =baseMajorManager.getBaseSiteByDmsCode(request.getStartSiteCode());
        BaseStaffSiteOrgDto endSite =baseMajorManager.getBaseSiteByDmsCode(request.getEndSiteCode());
        param.setCreateSiteCode(startSite.getSiteCode());
        param.setCreateSiteName(startSite.getSiteName());
        param.setCreateSiteType(startSite.getSiteType().toString());
        param.setReceiveSiteCode(endSite.getSiteCode());
        param.setReceiveSiteName(endSite.getSiteName());
        param.setReceiveSiteType(endSite.getSiteType().toString());
        param.setMixBoxType(mixBoxType);//不混装
        param.setQuantity(request.getNum());
        param.setTransportType(transportType);//默认公路运输
        param.setType(type);//正向普通
        param.setUserCode(request.getOperatorId());
        param.setUserName(request.getOperatorName());
        return param;
    }
}
