package com.jd.bluedragon.distribution.jy.manager;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.UmpConstants;
import com.jd.bluedragon.common.utils.ProfilerHelper;
import com.jd.bluedragon.distribution.jy.dto.send.SendPackageDto;
import com.jd.bluedragon.distribution.jy.dto.send.SendWaybillDto;
import com.jd.bluedragon.distribution.jy.enums.ExcepScanLabelEnum;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.bluedragon.utils.ObjectHelper;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import com.jd.ump.profiler.CallerInfo;
import com.jd.ump.profiler.proxy.Profiler;
import com.jdl.jy.realtime.api.send.ISendVehicleJsfService;
import com.jdl.jy.realtime.base.Pager;
import com.jdl.jy.realtime.base.ServiceResult;
import com.jdl.jy.realtime.model.es.job.SendPackageEsDto;
import com.jdl.jy.realtime.model.es.send.JySendTaskWaybillAgg;
import com.jdl.jy.realtime.model.query.send.SendVehicleTaskQuery;
import com.jdl.jy.realtime.model.vo.send.SendBarCodeDetailVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * @ClassName JySendVehicleJsfManagerImpl
 * @Description
 * @Author wyh
 * @Date 2022/6/14 14:14
 **/
@Component
public class JySendVehicleJsfManagerImpl implements IJySendVehicleJsfManager {

    private static final Logger log = LoggerFactory.getLogger(JySendVehicleJsfManagerImpl.class);

    @Autowired
    @Qualifier("jySendVehicleJsfService")
    private ISendVehicleJsfService sendVehicleJsfService;

    @Override
    public Pager<SendBarCodeDetailVo> queryByCondition(Pager<SendVehicleTaskQuery> queryPager) {
        CallerInfo ump = ProfilerHelper.registerInfo("dms.web.IJySendVehicleManager.queryByCondition");
        try {
            ServiceResult<Pager<SendBarCodeDetailVo>> serviceResult = sendVehicleJsfService.queryByCondition(queryPager);
            if (serviceResult.retSuccess()) {
                return serviceResult.getData();
            }
            else {
                log.warn("分页查询发车包裹失败. {}. {}", JsonHelper.toJson(queryPager), JsonHelper.toJson(serviceResult));
            }
        }
        catch (Exception ex) {
            Profiler.functionError(ump);
            log.error("查询发车包裹明细异常. {}", JsonHelper.toJson(queryPager), ex);
        }
        Profiler.registerInfoEnd(ump);

        return null;
    }

    @Override
    @JProfiler(jKey = UmpConstants.UMP_KEY_BASE + "JySendVehicleJsfManagerImpl.querySendTaskWaybill",
            jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.Heartbeat, JProEnum.FunctionError})
    public Pager<SendWaybillDto> querySendTaskWaybill(Pager<SendVehicleTaskQuery> queryPager) {
        try {
            ServiceResult<Pager<JySendTaskWaybillAgg>> serviceResult =sendVehicleJsfService.querySendTaskWaybill(queryPager);
            if (serviceResult.retSuccess()) {
                Pager<JySendTaskWaybillAgg> pagerData = serviceResult.getData();
                if (ObjectHelper.isNotNull(pagerData) && ObjectHelper.isNotNull(pagerData.getData()) && pagerData.getData().size()>0){
                    List<JySendTaskWaybillAgg> list =pagerData.getData();
                    List<SendWaybillDto> sendWaybillDtoList = transformData(list);

                    Pager<SendWaybillDto> pager =new Pager();
                    pager.setData(sendWaybillDtoList);
                    pager.setPageNo(pagerData.getPageNo());
                    pager.setPageSize(pager.getPageSize());
                    pager.setTotal(pager.getTotal());
                    return pager;
                }
            }
        } catch (Exception e) {
            log.error("查询发货运单数据异常. {}", JsonHelper.toJson(queryPager), e);
        }
        return null;
    }

    private List<SendWaybillDto> transformData(List<JySendTaskWaybillAgg> jySendTaskWaybillAggList) {
        List<SendWaybillDto> waybillDtoList = new ArrayList<>();
        for (JySendTaskWaybillAgg waybillAgg : jySendTaskWaybillAggList) {
            SendWaybillDto sendWaybillDto = new SendWaybillDto();
            sendWaybillDto.setWaybillCode(waybillAgg.getWaybillCode());
            sendWaybillDto.setAllPackCount(waybillAgg.getTotalCount() == null ? 0L : Long.valueOf(waybillAgg.getTotalCount()));
            sendWaybillDto.setScanPackCount(waybillAgg.getPackageCount() == null ? 0L : Long.valueOf(waybillAgg.getPackageCount()));
            waybillDtoList.add(sendWaybillDto);
        }
        return waybillDtoList;
    }

    @Override
    @JProfiler(jKey = UmpConstants.UMP_KEY_BASE + "JySendVehicleJsfManagerImpl.querySendPackageDetail",
            jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.Heartbeat, JProEnum.FunctionError})
    public Pager<SendPackageDto> querySendPackageDetail(Pager<SendVehicleTaskQuery> queryPager) {
        try {
            ServiceResult<Pager<SendPackageEsDto>> serviceResult =sendVehicleJsfService.querySendPackageDetail(queryPager);
            if (serviceResult.retSuccess()) {
                Pager<SendPackageEsDto> pagerData = serviceResult.getData();
                if (ObjectHelper.isNotNull(pagerData) && ObjectHelper.isNotNull(pagerData.getData()) && pagerData.getData().size()>0){
                    List<SendPackageDto> sendPackageDtoList = new ArrayList();
                    SendVehicleTaskQuery sendVehicleTaskQuery =queryPager.getSearchVo();
                    for (SendPackageEsDto sendPackageEsDto:pagerData.getData()){
                        SendPackageDto sendPackageDto =new SendPackageDto();
                        sendPackageDto.setPackageCode(sendPackageEsDto.getPackageCode());
                        sendPackageDto.setExcepScanLabelEnum(resolveExcepScanLabelEnum(sendPackageEsDto,sendVehicleTaskQuery.getQueryBarCodeFlag()));
                        sendPackageDtoList.add(sendPackageDto);
                    }

                    Pager<SendPackageDto> pager =new Pager();
                    pager.setData(sendPackageDtoList);
                    pager.setPageNo(pagerData.getPageNo());
                    pager.setPageSize(pager.getPageSize());
                    pager.setTotal(pager.getTotal());
                    return pager;
                }
            }
        } catch (Exception e) {
            log.error("查询发货包裹数据异常. {}", JsonHelper.toJson(queryPager), e);
        }
        return null;
    }

    private ExcepScanLabelEnum resolveExcepScanLabelEnum(SendPackageEsDto sendPackageEsDto, Integer queryBarCodeFlag) {
        if (ObjectHelper.isNotNull(queryBarCodeFlag)){
            switch (queryBarCodeFlag) {
                case 3:
                    if (sendPackageEsDto.getInterceptFlag().equals(Constants.STAGING_CHECK_SUCCESS_CODE)) {
                        return ExcepScanLabelEnum.INTERCEPTED;
                    }
                    if (sendPackageEsDto.getForceSendFlag().equals(Constants.STAGING_CHECK_SUCCESS_CODE)) {
                        return ExcepScanLabelEnum.FORCE_SEND;
                    }
                    break;
                case 5:
                    if (!ObjectHelper.isNotNull(sendPackageEsDto.getShouldScanFlag())) {
                        return ExcepScanLabelEnum.INCOMPELETE_NOT_ARRIVE;

                    }else if (sendPackageEsDto.getShouldScanFlag() ==1 && !ObjectHelper.isNotNull(sendPackageEsDto.getScannedFlag())){
                        return ExcepScanLabelEnum.INCOMPELETE_HAVE_INSPECTION_BUT_NOT_SEND;
                    }
                    break;
            }
        }
        return null;
    }

}
