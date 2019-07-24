package com.jd.bluedragon.distribution.external.service.impl;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.distribution.api.JdResponse;
import com.jd.bluedragon.distribution.api.domain.BatchSendSummary;
import com.jd.bluedragon.distribution.api.request.GantryDeviceConfigJsfRequest;
import com.jd.bluedragon.distribution.api.request.UploadDataJsfRequest;
import com.jd.bluedragon.distribution.api.response.BatchSendSummaryResponse;
import com.jd.bluedragon.distribution.auto.domain.UploadData;
import com.jd.bluedragon.distribution.auto.service.ScannerFrameConsume;
import com.jd.bluedragon.distribution.external.service.DmsScannerFrameService;
import com.jd.bluedragon.distribution.gantry.domain.GantryDeviceConfig;
import com.jd.bluedragon.distribution.gantry.service.GantryDeviceService;
import com.jd.bluedragon.distribution.send.domain.SendDetail;
import com.jd.bluedragon.distribution.waybill.domain.WaybillPackageDTO;
import com.jd.bluedragon.distribution.waybill.service.WaybillService;
import com.jd.bluedragon.utils.BeanHelper;
import com.jd.bluedragon.utils.SerialRuleUtil;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import com.jd.ump.profiler.CallerInfo;
import com.jd.ump.profiler.proxy.Profiler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by wuzuxiang on 2018/11/7.
 */
@Component
public class DmsScannerFrameServiceImpl implements DmsScannerFrameService{

    private static final Logger LOGGER = LoggerFactory.getLogger(DmsScannerFrameServiceImpl.class);

    /**
     * 此处只能使用@Resource注解，使用@Autowired会报错
     * Key type [class java.lang.Integer] of map [java.util.Map]
     * must be assignable to [java.lang.String]
     */
    @Resource(name = "scannerFrameConsumeMap")
    private Map<Integer, ScannerFrameConsume> scannerFrameConsumeMap;

    @Autowired
    private GantryDeviceService gantryDeviceService;

    @Autowired
    private WaybillService waybillService;


    @Override
    @JProfiler(jKey = "DMSWEB.DmsScannerFrameServiceImpl.dealScannerFrameConsume", mState = {JProEnum.TP, JProEnum.FunctionError}, jAppName = Constants.UMP_APP_NAME_DMSWEB)
    public boolean dealScannerFrameConsume(UploadDataJsfRequest uploadData, GantryDeviceConfigJsfRequest config) {
        boolean result = Boolean.FALSE;

        CallerInfo info = Profiler.registerInfo("DMS.WEB.DmsScannerFrameService.dealScannerFrameConsume", false, true);
        try {
            UploadData uploadData1 = convert2UploadData(uploadData);
            GantryDeviceConfig config1 = convert2GantryDeviceConfig(config);

            Iterator<Map.Entry<Integer, ScannerFrameConsume>> item = scannerFrameConsumeMap.entrySet().iterator();
            while (item.hasNext()) {
                Map.Entry<Integer, ScannerFrameConsume> consume = item.next();
                if (consume.getKey().intValue() == (config.getBusinessType().intValue() & consume.getKey().intValue())) {
                    LOGGER.info("龙门架分发消息registerNo={},operateTime={},consume={},barcode={}",
                            uploadData1.getRegisterNo(), uploadData1.getScannerTime(), consume.getKey(), uploadData1.getBarCode());
                    result = consume.getValue().onMessage(uploadData1, config1);
                }
            }
            Profiler.registerInfoEnd(info);
        } catch (Exception e) {
            Profiler.functionError(info);
            LOGGER.error("DmsScannerFrameServiceImpl.dealScannerFrameConsume-->龙门架核心处理JSF接口异常",e);
        }
        return result;
    }

    @Override
    @JProfiler(jKey = "DMSWEB.DmsScannerFrameServiceImpl.countSendCode", mState = {JProEnum.TP, JProEnum.FunctionError}, jAppName = Constants.UMP_APP_NAME_DMSWEB)
    public BatchSendSummaryResponse countSendCode(String[] sendCodes, boolean packageNumFlag, boolean volumeFlag) {
        if (sendCodes.length == 0) {
            return new BatchSendSummaryResponse(JdResponse.CODE_OK_NULL,JdResponse.MESSAGE_OK_NULL);
        }

        CallerInfo info = Profiler.registerInfo("DMS.WEB.DmsScannerFrameService.countSendCode", false, true);
        BatchSendSummaryResponse response = new BatchSendSummaryResponse(JdResponse.CODE_OK,JdResponse.MESSAGE_OK);
        BatchSendSummary summary;
        List<BatchSendSummary> summaries = new ArrayList<BatchSendSummary>();
        for (String sendCode : sendCodes) {
            summary = new BatchSendSummary();
            summary.setCreateSiteCode(SerialRuleUtil.getCreateSiteCodeFromSendCode(sendCode));
            summary.setReceiveSiteCode(SerialRuleUtil.getReceiveSiteCodeFromSendCode(sendCode));
            summary.setSendCode(sendCode);
            summary.setPackageSum(0);
            summary.setVolumeSum(0d);

            if (packageNumFlag && !volumeFlag) {
            /* 只计算包裹数量 && 不计算包裹体积 */
                Integer packageCount = gantryDeviceService.querySendDCountBySendCode(sendCode);
                summary.setPackageSum(packageCount);
            } else if (volumeFlag) {
                /* 计算包裹体积时，都会顺带计算包裹数量 */
                List<SendDetail> sendDetailList = gantryDeviceService.queryBoxCodeBySendCode(sendCode);
                Double volumeSum = 0.00;//取分拣体积
                if (null == sendDetailList ||  sendDetailList.size() == 0) {
                    continue;
                }

                HashSet<String> sendDByBoxCode = new HashSet<String>();
                for (SendDetail sendD : sendDetailList) {
                    //根据sendD的boxCode去重
                    if (sendDByBoxCode.contains(sendD.getBoxCode())) {
                        continue;
                    }
                    sendDByBoxCode.add(sendD.getBoxCode());
                    WaybillPackageDTO waybillPackageDTO = waybillService.getWaybillPackage(sendD.getBoxCode());
                    if (waybillPackageDTO == null) {
                        continue;
                    }
                    volumeSum += waybillPackageDTO.getVolume() == 0 ? waybillPackageDTO.getOriginalVolume() : waybillPackageDTO.getVolume();
                }
                summary.setPackageSum(sendDetailList.size());//获取包裹的数量
                summary.setVolumeSum(new BigDecimal(volumeSum).setScale(2, RoundingMode.UP).doubleValue());//四舍五入;保留两位有效数字
            }

            summaries.add(summary);
        }
        response.setBatchSendSummaries(summaries);

        Profiler.registerInfoEnd(info);
        return response;
    }

    private UploadData convert2UploadData(UploadDataJsfRequest request) {
        UploadData uploadData = new UploadData();
        BeanHelper.copyProperties(uploadData,request);
        return uploadData;
    }

    private GantryDeviceConfig convert2GantryDeviceConfig(GantryDeviceConfigJsfRequest request) {
        GantryDeviceConfig gantryDeviceConfig = new GantryDeviceConfig();
        BeanHelper.copyProperties(gantryDeviceConfig,request);
        return gantryDeviceConfig;
    }
}
