package com.jd.bluedragon.distribution.jy.service.picking.strategy;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.core.base.CargoDetailServiceManager;
import com.jd.bluedragon.core.jmq.producer.DefaultJMQProducer;
import com.jd.bluedragon.distribution.jy.constants.PickingGoodTaskDetailInitServiceEnum;
import com.jd.bluedragon.distribution.jy.dto.pickinggood.CalculateWaitPickingItemNumDto;
import com.jd.bluedragon.distribution.jy.dto.pickinggood.PickingGoodTaskDetailInitDto;
import com.jd.bluedragon.distribution.jy.exception.JyBizException;
import com.jd.bluedragon.distribution.jy.service.common.CommonService;
import com.jd.bluedragon.distribution.jy.service.picking.JyPickingTaskAggsService;
import com.jd.bluedragon.distribution.jy.service.picking.factory.PickingGoodDetailInitServiceFactory;
import com.jd.bluedragon.distribution.jy.service.picking.template.AviationPickingGoodTaskInit;
import com.jd.bluedragon.dms.utils.WaybillUtil;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.bluedragon.utils.NumberHelper;
import com.jd.jmq.common.message.Message;
import com.jd.jsf.gd.util.JsonUtils;
import com.jd.ql.basic.dto.BaseStaffSiteOrgDto;
import com.jd.tms.data.dto.CargoDetailDto;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import com.jd.ump.profiler.CallerInfo;
import com.jd.ump.profiler.proxy.Profiler;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * 待提明细初始化-取运输封车数据
 * @Author zhengchengfa
 * @Date 2023/12/7 21:40
 * @Description
 */
@Service("pickingGoodDetailInitByTmsSealInfoServiceImpl")
public class PickingGoodDetailInitByTmsSealInfoServiceImpl implements PickingGoodDetailInitService , InitializingBean {

    private static final Logger log = LoggerFactory.getLogger(AviationPickingGoodTaskInit.class);
    @Autowired
    private CargoDetailServiceManager cargoDetailServiceManager;
    @Autowired
    @Qualifier(value = "jyPickingGoodDetailInitSplitProducer")
    private DefaultJMQProducer jyPickingGoodDetailInitSplitProducer;
    @Autowired
    @Qualifier(value = "jyPickingGoodSaveWaitScanItemNumProducer")
    private DefaultJMQProducer jyPickingGoodSaveWaitScanItemNumProducer;
    @Autowired
    private JyPickingTaskAggsService jyPickingTaskAggsService;
    @Autowired
    private CommonService commonService;


    @Override
    public void afterPropertiesSet() throws Exception {
        PickingGoodDetailInitServiceFactory.registerPickingGoodDetailInitService(PickingGoodTaskDetailInitServiceEnum.OTHER_SEND_DMS_PICKING.getTargetCode(), this);
    }


    private void logInfo(String message, Object... objects) {
        if (log.isInfoEnabled()) {
            log.info(message, objects);
        }
    }

    private void logWarn(String message, Object... objects) {
        if (log.isWarnEnabled()) {
            log.warn(message, objects);
        }
    }

    @Override
    @JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWEB,jKey = "DMS.PickingGoodDetailInitByTmsSealInfoServiceImpl.pickingGoodDetailInitSplit",mState = {JProEnum.TP, JProEnum.FunctionError})
    public boolean pickingGoodDetailInitSplit(PickingGoodTaskDetailInitDto initDto) {
        List<String> packageCodeList = this.getAllPackageCodeByBatchCode(initDto.getBatchCode());
        if(CollectionUtils.isEmpty(packageCodeList)) {
            logWarn("提货任务明细初始化：根据批次号查询运输封车明细为空，paramDto={}", JsonHelper.toJson(initDto));
            return true;
        }
        //拆分包裹消息
        this.sendInitDetailPackageMq(initDto, packageCodeList);
        //计算待提件数
        this.calculateWaitPickingItemNum(initDto, packageCodeList);

        return true;
    }


    private List<String> getAllPackageCodeByBatchCode(String batchCode) {
        CallerInfo callerInfo = Profiler.registerInfo("DMS.PickingGoodDetailInitByTmsSealInfoServiceImpl.getAllPackageCodeByBatchCode", Constants.UMP_APP_NAME_DMSWEB,false,true);
        Long startTime = System.currentTimeMillis();
        CargoDetailDto cargoDetailDto = new CargoDetailDto();
        cargoDetailDto.setBatchCode(batchCode);
        cargoDetailDto.setYn(1);
        int limitSize = 1000;
        int currentSize  = limitSize ;
        int offset = 0;
        List<String> packageCodeList = new ArrayList<>();
        while(currentSize >= limitSize){
            com.jd.tms.data.dto.CommonDto<List<CargoDetailDto>> cargoDetailReturn = cargoDetailServiceManager.getCargoDetailInfoByBatchCode(cargoDetailDto,offset,limitSize);
            if(cargoDetailReturn == null || cargoDetailReturn.getCode() != com.jd.etms.vos.dto.CommonDto.CODE_SUCCESS ) {
                log.error("获取批次{}下包裹数据异常,条件：offset={},limitSize={}, result={}",batchCode, offset, limitSize, JsonHelper.toJson(cargoDetailReturn));
                throw new JyBizException("运输接口根据批次获取包裹信息异常");
            }
            if(cargoDetailReturn.getData().isEmpty()) {
                break;
            }
            cargoDetailReturn.getData().forEach(tmsDetailDto -> {
                packageCodeList.add(tmsDetailDto.getPackageCode());
            });
            currentSize = cargoDetailReturn.getData().size();
            offset =  offset + limitSize;
        }
        logInfo("提货任务明细初始化：根据批次号{}查询运输封车明细数量为{}, 耗时={}ms", batchCode, packageCodeList.size(), System.currentTimeMillis() - startTime);
        Profiler.registerInfoEnd(callerInfo);

        return packageCodeList;
    }


    private void sendInitDetailPackageMq(PickingGoodTaskDetailInitDto initDto, List<String> packageCodeList) {
        List<Message> messageList = new ArrayList<>();
        packageCodeList.forEach(packageCode -> {
            PickingGoodTaskDetailInitDto detailSplitInitDto = new PickingGoodTaskDetailInitDto();
            BeanUtils.copyProperties(initDto, detailSplitInitDto);
            detailSplitInitDto.setPackageCode(packageCode);
            detailSplitInitDto.setSysTime(System.currentTimeMillis());
            detailSplitInitDto.setScanIsBoxType(false);

            String msgText = JsonUtils.toJSONString(detailSplitInitDto);
            logInfo("otherToDms待提明细初始化拆分最小包裹维度，businessId={},msg={}", packageCode, msgText);
            messageList.add(new Message(jyPickingGoodDetailInitSplitProducer.getTopic(), msgText, packageCode));
        });
        jyPickingGoodDetailInitSplitProducer.batchSendOnFailPersistent(messageList);
    }


    //计算待提件数
    private void calculateWaitPickingItemNum(PickingGoodTaskDetailInitDto initDto, List<String> packageCodeList) {
        CallerInfo callerInfo = Profiler.registerInfo("DMS.PickingGoodDetailInitByTmsSealInfoServiceImpl.calculateWaitPickingItemNum", Constants.UMP_APP_NAME_DMSWEB,false,true);
        //K-运单号 V-流向场地
        Map<String, Integer> waybillRouteMap = new HashMap<>();
        //K-流向场地 V-该流向件数
        Map<Integer, Integer> routeNextCountMap = new HashMap<>();

        packageCodeList.forEach(packageCode -> {
            String waybillCode = WaybillUtil.getWaybillCode(packageCode);
            Integer nextSiteId = waybillRouteMap.get(waybillCode);
            if(Objects.isNull(nextSiteId)) {
                BaseStaffSiteOrgDto dto = commonService.getRouteNextSiteByWaybillCode(initDto.getPickingSiteId().intValue(), waybillCode);
                if (!Objects.isNull(dto) && !Objects.isNull(dto.getSiteCode())) {
                    nextSiteId = dto.getSiteCode();
                    waybillRouteMap.put(waybillCode, nextSiteId);
                }
            }
            if(!Objects.isNull(nextSiteId)) {
                Integer count = routeNextCountMap.get(nextSiteId);
                if(!NumberHelper.gt0(count)) {
                    routeNextCountMap.put(nextSiteId, 1);
                }else {
                    routeNextCountMap.put(nextSiteId, count + 1);
                }
            }else {
                logInfo("otherToDms提货待提件数计算，【bizId={}|batchCode={}】根据包裹号{}未查到路由下一跳，不做流向维度agg统计", initDto.getBizId(), initDto.getBatchCode(), packageCode);
            }

        });

        List<Message> messageList = new ArrayList<>();
        CalculateWaitPickingItemNumDto bizIdItemNumDto = new CalculateWaitPickingItemNumDto();
        bizIdItemNumDto.setBizId(initDto.getBizId());
        bizIdItemNumDto.setBatchCode(initDto.getBatchCode());
        bizIdItemNumDto.setPickingSiteId(initDto.getPickingSiteId());
        bizIdItemNumDto.setWaitPickingItemNum(packageCodeList.size());
        bizIdItemNumDto.setCalculateNextSiteAggFlag(false);
        bizIdItemNumDto.setOperateTime(initDto.getOperateTime());
        bizIdItemNumDto.setSysTime(System.currentTimeMillis());
        String msgText1 = JsonUtils.toJSONString(bizIdItemNumDto);
        logInfo("otherToDms计算bizId维度待提总件数发送消息，businessId={},msg={}", initDto.getBizId(), msgText1);
        messageList.add(new Message(jyPickingGoodSaveWaitScanItemNumProducer.getTopic(), msgText1, initDto.getBizId()));

        routeNextCountMap.forEach((nextSiteId, waitPickingItemNum) -> {
            CalculateWaitPickingItemNumDto nextItemNumDto = new CalculateWaitPickingItemNumDto();
            nextItemNumDto.setBizId(initDto.getBizId());
            nextItemNumDto.setBatchCode(initDto.getBatchCode());
            nextItemNumDto.setPickingSiteId(initDto.getPickingSiteId());
            nextItemNumDto.setNextSiteId(nextSiteId.longValue());
            nextItemNumDto.setWaitPickingItemNum(waitPickingItemNum);
            nextItemNumDto.setCalculateNextSiteAggFlag(true);
            nextItemNumDto.setOperateTime(initDto.getOperateTime());
            nextItemNumDto.setSysTime(System.currentTimeMillis());
            String msgText = JsonUtils.toJSONString(nextItemNumDto);
            String businessId = String.format("%s|%s", initDto.getBizId(), nextSiteId);
            logInfo("otherToDms计算bizId流向维度待提总件数发送消息，businessId={},msg={}", businessId, msgText);
            messageList.add(new Message(jyPickingGoodSaveWaitScanItemNumProducer.getTopic(), msgText, businessId));
        });
        jyPickingGoodSaveWaitScanItemNumProducer.batchSendOnFailPersistent(messageList);
        Profiler.registerInfoEnd(callerInfo);
    }


}
