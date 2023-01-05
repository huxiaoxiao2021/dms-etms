package com.jd.bluedragon.distribution.external.service.impl;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.core.base.VosManager;
import com.jd.bluedragon.distribution.api.request.NewSealVehicleRequest;
import com.jd.bluedragon.distribution.api.response.*;
import com.jd.bluedragon.distribution.api.utils.JsonHelper;
import com.jd.bluedragon.distribution.external.service.DmsNewSealVehicleService;
import com.jd.bluedragon.distribution.rest.seal.NewSealVehicleResource;
import com.jd.bluedragon.distribution.rest.seal.SealBoxResource;
import com.jd.bluedragon.distribution.rest.seal.SealVehicleResource;
import com.jd.bluedragon.distribution.seal.service.NewSealVehicleService;
import com.jd.bluedragon.distribution.sealVehicle.domain.SealCarNotCollectedDto;
import com.jd.bluedragon.distribution.sealVehicle.domain.SealCarNotCollectedPo;
import com.jd.bluedragon.distribution.send.service.DeliveryService;
import com.jd.etms.vos.dto.SealCarDto;
import com.jd.ql.dms.common.domain.JdResponse;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


/**
 * <p>
 * Created by lixin39 on 2018/11/9.
 */
@Service("dmsNewSealVehicleService")
public class DmsNewSealVehicleServiceImpl implements DmsNewSealVehicleService {
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired
    @Qualifier("newSealVehicleResource")
    private NewSealVehicleResource newSealVehicleResource;

    @Autowired
    @Qualifier("sealVehicleResource")
    private SealVehicleResource sealVehicleResource;
    @Autowired
    private NewSealVehicleService newSealVehicleService;
    @Autowired
    @Qualifier("sealBoxResource")
    private SealBoxResource sealBoxResource;
    @Autowired
    private VosManager vosManager;
    @Autowired
    DeliveryService deliveryService;

    @Override
    @JProfiler(jKey = "DMSWEB.DmsNewSealVehicleServiceImpl.getVehicleNumBySimpleCode", mState = {JProEnum.TP, JProEnum.FunctionError}, jAppName = Constants.UMP_APP_NAME_DMSWEB)
    public TransWorkItemResponse getVehicleNumBySimpleCode(String simpleCode) {
        return newSealVehicleResource.getVehicleNumBySimpleCode(simpleCode);
    }

    @Override
    @JProfiler(jKey = "DMSWEB.DmsNewSealVehicleServiceImpl.getVehicleNumberOrItemCodeByParam", mState = {JProEnum.TP, JProEnum.FunctionError}, jAppName = Constants.UMP_APP_NAME_DMSWEB)
    public NewSealVehicleResponse getVehicleNumberOrItemCodeByParam(NewSealVehicleRequest request) {
        return newSealVehicleResource.getVehicleNumberOrItemCodeByParam(request);
    }

    @Override
    @JProfiler(jKey = "DMSWEB.DmsNewSealVehicleServiceImpl.checkTransportCode", mState = {JProEnum.TP, JProEnum.FunctionError}, jAppName = Constants.UMP_APP_NAME_DMSWEB)
    public TransWorkItemResponse checkTransportCode(NewSealVehicleRequest request) {
        return newSealVehicleResource.checkTransportCode(request);
    }

    @Deprecated
    @Override
    @JProfiler(jKey = "DMSWEB.DmsNewSealVehicleServiceImpl.newCheckTranCodeAndBatchCode", mState = {JProEnum.TP, JProEnum.FunctionError}, jAppName = Constants.UMP_APP_NAME_DMSWEB)
    public NewSealVehicleResponse newCheckTranCodeAndBatchCode(String transportCode, String batchCode, Integer sealCarType) {
        return newSealVehicleResource.newCheckTranCodeAndBatchCode(transportCode, batchCode, sealCarType);
    }

    @Override
    @JProfiler(jKey = "DMSWEB.DmsNewSealVehicleServiceImpl.seal", mState = {JProEnum.TP, JProEnum.FunctionError}, jAppName = Constants.UMP_APP_NAME_DMSWEB)
    public NewSealVehicleResponse seal(NewSealVehicleRequest request) {
        return newSealVehicleResource.seal(request);
    }

    @Override
    @JProfiler(jKey = "DMSWEB.DmsNewSealVehicleServiceImpl.findSealInfo", mState = {JProEnum.TP, JProEnum.FunctionError}, jAppName = Constants.UMP_APP_NAME_DMSWEB)
    public NewSealVehicleResponse findSealInfo(NewSealVehicleRequest request) {
        return newSealVehicleResource.findSealInfo(request);
    }

    @Override
    @JProfiler(jKey = "DMSWEB.DmsNewSealVehicleServiceImpl.unsealCheck", mState = {JProEnum.TP, JProEnum.FunctionError}, jAppName = Constants.UMP_APP_NAME_DMSWEB)
    public NewSealVehicleResponse unsealCheck(NewSealVehicleRequest request) {
        return newSealVehicleResource.unsealCheck(request);
    }

    @Override
    @JProfiler(jKey = "DMSWEB.DmsNewSealVehicleServiceImpl.unseal", mState = {JProEnum.TP, JProEnum.FunctionError}, jAppName = Constants.UMP_APP_NAME_DMSWEB)
    public NewSealVehicleResponse unseal(NewSealVehicleRequest request) {
        return newSealVehicleResource.unsealWithCheckUsage(request, false);
    }

    @Override
    @JProfiler(jKey = "DMSWEB.DmsNewSealVehicleServiceImpl.newUnseal", mState = {JProEnum.TP, JProEnum.FunctionError}, jAppName = Constants.UMP_APP_NAME_DMSWEB)
    public NewUnsealVehicleResponse<Boolean> newUnseal(NewSealVehicleRequest request) {
        return newSealVehicleResource.newUnsealWithCheckUsage(request, false);
    }

    @Override
    @JProfiler(jKey = "DMSWEB.DmsNewSealVehicleServiceImpl.getTransportCode", mState = {JProEnum.TP, JProEnum.FunctionError}, jAppName = Constants.UMP_APP_NAME_DMSWEB)
    public RouteTypeResponse getTransportCode(NewSealVehicleRequest request) {
        return newSealVehicleResource.getTransportCode(request);
    }

    @Override
    @JProfiler(jKey = "DMSWEB.DmsNewSealVehicleServiceImpl.findSealVehicleByCode", mState = {JProEnum.TP, JProEnum.FunctionError}, jAppName = Constants.UMP_APP_NAME_DMSWEB)
    public SealVehicleResponse findSealVehicleByCode(String sealCode) {
        return sealVehicleResource.findSealByCode(sealCode);
    }

    @Override
    @JProfiler(jKey = "DMSWEB.DmsNewSealVehicleServiceImpl.findSealBoxByBoxCode", mState = {JProEnum.TP, JProEnum.FunctionError}, jAppName = Constants.UMP_APP_NAME_DMSWEB)
    public SealBoxResponse findSealBoxByBoxCode(String sealBoxCode) {
        return sealBoxResource.findSealByCode(sealBoxCode);
    }

    @Override
    @JProfiler(jKey = "DMSWEB.DmsNewSealVehicleServiceImpl.checkSendCodeIsSealed", mState = {JProEnum.TP, JProEnum.FunctionError}, jAppName = Constants.UMP_APP_NAME_DMSWEB)
    public boolean checkSendCodeIsSealed(String sendCode) {
        if(StringUtils.isBlank(sendCode)){
            return false;
        }
        if(deliveryService.checkSendCodeIsSealed(sendCode)){
            return true;
        }
        return false;
    }

    @Override
    @JProfiler(jKey = "DMSWEB.DmsNewSealVehicleServiceImpl.getSealCarTimeBySendCode", mState = {JProEnum.TP, JProEnum.FunctionError}, jAppName = Constants.UMP_APP_NAME_DMSWEB)
    public Long getSealCarTimeBySendCode(String sendCode) {
        if(StringUtils.isBlank(sendCode)){
            return null;
        }
        Long sealCarTimeBySendCode = newSealVehicleService.getSealCarTimeBySendCode(sendCode);
        if (sealCarTimeBySendCode != null) {
            return sealCarTimeBySendCode;
        }
        try {
            SealCarDto sealCarDto = vosManager.querySealCarByBatchCode(sendCode);
            if (sealCarDto != null) {
                return sealCarDto.getSealCarTime().getTime();
            }
        } catch (Exception e) {
            log.error("批次号取封车信息失败：{}",sendCode, e);
        }
        return null;
    }

    /**
     * 根据车牌号获取派车明细编码或根据派车明细编码获取车牌号
     */
    @Override
    @JProfiler(jKey = "DMSWEB.DmsNewSealVehicleServiceImpl.getVehicleNumberOrItemCode",jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
    public TransWorkItemResponse getVehicleNumberOrItemCode(NewSealVehicleRequest request){
        return newSealVehicleResource.getVehicleNumberOrItemCodeByParam(request);
    }

    /**
     * 按封车号批量查询运单是否有未集齐包裹分页列表
     * @param request 查询参数
     * @return 查询结果
     */
    @Override
    @JProfiler(jKey = "DMSWEB.DmsNewSealVehicleServiceImpl.selectNotCollectedList",jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
    public NewSealVehicleResponse<List<SealCarNotCollectedDto>> selectNotCollectedList(SealCarNotCollectedPo request){
        NewSealVehicleResponse<List<SealCarNotCollectedDto>> response = new NewSealVehicleResponse<>();
        response.setCode(JdResponse.CODE_SUCCESS);
        try {
            com.jd.dms.wb.report.api.sealCar.dto.client.SealCarNotCollectedPo  sealCarNotCollectedPo = new com.jd.dms.wb.report.api.sealCar.dto.client.SealCarNotCollectedPo ();
            BeanUtils.copyProperties(request, sealCarNotCollectedPo);
            NewSealVehicleResponse<List<com.jd.dms.wb.report.api.sealCar.dto.client.SealCarNotCollectedDto>> pageDataRaw = newSealVehicleResource.selectNotCollectedList(sealCarNotCollectedPo);
            if(!Objects.equals(pageDataRaw.getCode(), JdResponse.CODE_SUCCESS)){
                response.setCode(JdResponse.CODE_FAIL);
                response.setMessage(pageDataRaw.getMessage());
                log.error("DmsNewSealVehicleServiceImpl.selectNotCollectedList query fail , result: {}", JsonHelper.toJson(pageDataRaw));
                return response;
            }
            List<SealCarNotCollectedDto> dataList = new ArrayList<>();
            if(CollectionUtils.isNotEmpty(pageDataRaw.getData())){
                List<com.jd.dms.wb.report.api.sealCar.dto.client.SealCarNotCollectedDto> dataRawList = pageDataRaw.getData();
                for (com.jd.dms.wb.report.api.sealCar.dto.client.SealCarNotCollectedDto carNotCollectedDto : dataRawList) {
                    SealCarNotCollectedDto sealCarNotCollectedDtoTemp = new SealCarNotCollectedDto();
                    BeanUtils.copyProperties(carNotCollectedDto, sealCarNotCollectedDtoTemp);
                    dataList.add(sealCarNotCollectedDtoTemp);
                }
            }
            response.setData(dataList);
        } catch (BeansException e) {
            log.error("DmsNewSealVehicleServiceImpl.selectNotCollectedList exception ", e);
            response.setCode(JdResponse.CODE_ERROR);
        }

        return response;
    }
}
