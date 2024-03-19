package com.jd.bluedragon.distribution.jy.service.picking.strategy;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.core.jmq.producer.DefaultJMQProducer;
import com.jd.bluedragon.distribution.jy.constants.PickingGoodTaskDetailInitServiceEnum;
import com.jd.bluedragon.distribution.jy.dto.pickinggood.CalculateWaitPickingItemNumDto;
import com.jd.bluedragon.distribution.jy.dto.pickinggood.PickingGoodTaskDetailInitDto;
import com.jd.bluedragon.distribution.jy.service.picking.factory.PickingGoodDetailInitServiceFactory;
import com.jd.bluedragon.distribution.jy.service.picking.template.AviationPickingGoodTaskInit;
import com.jd.bluedragon.distribution.router.RouterService;
import com.jd.bluedragon.distribution.send.dao.SendDatailReadDao;
import com.jd.bluedragon.distribution.send.domain.SendDetail;
import com.jd.bluedragon.dms.utils.BusinessUtil;
import com.jd.bluedragon.dms.utils.WaybillUtil;
import com.jd.bluedragon.utils.BusinessHelper;
import com.jd.bluedragon.utils.NumberHelper;
import com.jd.jmq.common.message.Message;
import com.jd.jsf.gd.util.JsonUtils;
import com.jd.ql.basic.dto.BaseStaffSiteOrgDto;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import com.jd.ump.profiler.CallerInfo;
import com.jd.ump.profiler.proxy.Profiler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 待提明细初始化-取分拣发货数据
 * @Author zhengchengfa
 * @Date 2023/12/7 21:42
 * @Description
 */
@Service("pickingGoodDetailInitByDmsSendInfoServiceImpl")
public class PickingGoodDetailInitByDmsSendInfoServiceImpl implements PickingGoodDetailInitService, InitializingBean {

    private static final Logger log = LoggerFactory.getLogger(AviationPickingGoodTaskInit.class);
    @Autowired
    private SendDatailReadDao sendDatailReadDao;

    @Autowired
    @Qualifier(value = "jyPickingGoodDetailInitSplitProducer")
    private DefaultJMQProducer jyPickingGoodDetailInitSplitProducer;
    @Autowired
    @Qualifier(value = "jyPickingGoodSaveWaitScanItemNumProducer")
    private DefaultJMQProducer jyPickingGoodSaveWaitScanItemNumProducer;
    @Autowired
    private RouterService routerService;

    @Override
    public void afterPropertiesSet() throws Exception {
        PickingGoodDetailInitServiceFactory.registerPickingGoodDetailInitService(PickingGoodTaskDetailInitServiceEnum.DMS_SEND_DMS_PICKING.getTargetCode(), this);
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
    @JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWEB,jKey = "DMS.PickingGoodDetailInitByDmsSendInfoServiceImpl.pickingGoodDetailInitSplit",mState = {JProEnum.TP, JProEnum.FunctionError})
    public boolean pickingGoodDetailInitSplit(PickingGoodTaskDetailInitDto initDto) {
//        todo zcf  改成分页查询
        List<SendDetail> sendDetailList = sendDatailReadDao.querySendDSimpleInfoBySendCode(initDto.getBatchCode(), initDto.getStartSiteId().intValue());
        //todo zcf
        this.sendInitDetailPackageMq(initDto, sendDetailList);
        //计算待提agg中待提件数
        this.calculateWaitPickingItemNum(initDto, sendDetailList);
        //计算待发agg中待发件数 todo 当批次内大量运单时，遍历查询路由下一跳有性能问题， 现在发货统计在下游包裹维度做计数处理
//        this.calculateWaitSendItemNum(initDto, sendDetailList);

        return true;
    }


    private void sendInitDetailPackageMq(PickingGoodTaskDetailInitDto initDto, List<SendDetail> sendDetailList) {
        List<Message> messageList = new ArrayList<>();
        sendDetailList.forEach(sendDetail -> {
            PickingGoodTaskDetailInitDto detailSplitInitDto = new PickingGoodTaskDetailInitDto();
            BeanUtils.copyProperties(initDto, detailSplitInitDto);
            detailSplitInitDto.setPackageCode(sendDetail.getPackageBarcode());
            detailSplitInitDto.setSysTime(System.currentTimeMillis());
            if(BusinessHelper.isBoxcode(sendDetail.getBoxCode())) {
                detailSplitInitDto.setBoxCode(sendDetail.getBoxCode());
                detailSplitInitDto.setScanIsBoxType(true);
            }else {
                detailSplitInitDto.setScanIsBoxType(false);
            }

            String msgText = JsonUtils.toJSONString(detailSplitInitDto);
            logInfo("dmsToDms待提明细初始化拆分最小包裹维度，businessId={},msg={}", sendDetail.getPackageBarcode(), msgText);
            messageList.add(new Message(jyPickingGoodDetailInitSplitProducer.getTopic(), msgText, sendDetail.getPackageBarcode()));

        });
        jyPickingGoodDetailInitSplitProducer.batchSendOnFailPersistent(messageList);
    }

    private void calculateWaitPickingItemNum(PickingGoodTaskDetailInitDto initDto, List<SendDetail> sendDetailList) {
        Map<String, List<SendDetail>> map = sendDetailList.stream().collect(Collectors.groupingBy(SendDetail::getBoxCode));

        List<Message> messageList = new ArrayList<>();
        CalculateWaitPickingItemNumDto bizIdItemNumDto = new CalculateWaitPickingItemNumDto();
        bizIdItemNumDto.setBizId(initDto.getBizId());
        bizIdItemNumDto.setCode(initDto.getBatchCode());
        bizIdItemNumDto.setCodeType(CalculateWaitPickingItemNumDto.CODE_TYPE_BATCH_CODE);
        bizIdItemNumDto.setPickingSiteId(initDto.getPickingSiteId());
        bizIdItemNumDto.setWaitPickingItemNum(map.size());
        bizIdItemNumDto.setCalculateNextSiteAggFlag(false);
        bizIdItemNumDto.setOperateTime(initDto.getOperateTime());
        bizIdItemNumDto.setSysTime(System.currentTimeMillis());
        String msgText1 = JsonUtils.toJSONString(bizIdItemNumDto);
        logInfo("dmsToDms计算bizId维度待提总件数发送消息，businessId={},msg={}", initDto.getBizId(), msgText1);
        messageList.add(new Message(jyPickingGoodSaveWaitScanItemNumProducer.getTopic(), msgText1, initDto.getBizId()));
        jyPickingGoodSaveWaitScanItemNumProducer.batchSendOnFailPersistent(messageList);
    }


    private void calculateWaitSendItemNum(PickingGoodTaskDetailInitDto initDto, List<SendDetail> sendDetailList) {
        CallerInfo callerInfo = Profiler.registerInfo("DMS.PickingGoodDetailInitByDmsSendInfoServiceImpl.calculateWaitSendItemNum", Constants.UMP_APP_NAME_DMSWEB,false,true);

        Map<String, List<SendDetail>> map = sendDetailList.stream().collect(Collectors.groupingBy(SendDetail::getBoxCode));

        //K-barCode V-流向场地
        Map<String, Integer> waybillRouteMap = new HashMap<>();
        //K-流向场地 V-该流向件数
        Map<Integer, Integer> routeNextCountMap = new HashMap<>();

        map.forEach((barCode, sendList) -> {
            if(BusinessUtil.isBoxcode(barCode)) {
                Integer nextSiteId = null;
                Set<String> waybillSet = new HashSet<>();
                for(SendDetail sd : sendList) {
                    String waybillCode = sd.getWaybillCode();
                    if(waybillSet.contains(waybillCode)) {
                        continue;
                    }
                    nextSiteId = waybillRouteMap.get(waybillCode);
                    if(!Objects.isNull(nextSiteId)) {
                        break;
                    }

                    BaseStaffSiteOrgDto dto = routerService.getRouteNextSiteByWaybillCode(initDto.getPickingSiteId().intValue(), waybillCode);
                    if (!Objects.isNull(dto) && !Objects.isNull(dto.getSiteCode())) {
                        nextSiteId = dto.getSiteCode();
                        waybillRouteMap.put(waybillCode, nextSiteId);
                        break;
                    }
                    waybillSet.add(waybillCode);
                    if(waybillSet.size() >= 3) {
                        break;
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
                    logInfo("dmsToDms提货待发件数计算，【bizId={}|batchCode={}】根据箱号{}未查到路由下一跳，查询流向依赖单号【{}】，不做流向维度agg统计", initDto.getBizId(), initDto.getBatchCode(), barCode, JsonUtils.toJSONString(waybillSet));
                }
            }else if(WaybillUtil.isPackageCode(barCode)){
                String waybillCode = WaybillUtil.getWaybillCode(barCode);
                Integer nextSiteId = waybillRouteMap.get(waybillCode);
                if(Objects.isNull(nextSiteId)) {
                    BaseStaffSiteOrgDto dto = routerService.getRouteNextSiteByWaybillCode(initDto.getPickingSiteId().intValue(), waybillCode);
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
                    logInfo("dmsToDms提货待发件数计算，【bizId={}|batchCode={}】根据包裹号{}未查到路由下一跳，不做流向维度agg统计", initDto.getBizId(), initDto.getBatchCode(), barCode);
                }
            }else {
                logWarn("dmsToDms提货待发件数计算，barCode={}非箱号、非包裹号， 暂不处理， initDto={}", barCode, JsonUtils.toJSONString(initDto));
            }
        });

        List<Message> messageList = new ArrayList<>();

        routeNextCountMap.forEach((nextSiteId, waitPickingItemNum) -> {
            CalculateWaitPickingItemNumDto nextItemNumDto = new CalculateWaitPickingItemNumDto();
            nextItemNumDto.setBizId(initDto.getBizId());
            nextItemNumDto.setCode(initDto.getBatchCode());
            nextItemNumDto.setCodeType(CalculateWaitPickingItemNumDto.CODE_TYPE_BATCH_CODE);
            nextItemNumDto.setPickingSiteId(initDto.getPickingSiteId());
            nextItemNumDto.setNextSiteId(nextSiteId.longValue());
            nextItemNumDto.setWaitPickingItemNum(waitPickingItemNum);
            nextItemNumDto.setCalculateNextSiteAggFlag(true);
            nextItemNumDto.setOperateTime(initDto.getOperateTime());
            nextItemNumDto.setSysTime(System.currentTimeMillis());
            String msgText = JsonUtils.toJSONString(nextItemNumDto);
            String businessId = String.format("%s|%s", initDto.getBizId(), nextSiteId);
            logInfo("dmsToDms计算bizId流向维度待发总件数发送消息，businessId={},msg={}", businessId, msgText);
            messageList.add(new Message(jyPickingGoodSaveWaitScanItemNumProducer.getTopic(), msgText, businessId));
        });
        jyPickingGoodSaveWaitScanItemNumProducer.batchSendOnFailPersistent(messageList);
        Profiler.registerInfoEnd(callerInfo);
    }


}
