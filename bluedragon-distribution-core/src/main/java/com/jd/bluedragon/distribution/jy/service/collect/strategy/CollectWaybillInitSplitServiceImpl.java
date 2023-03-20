package com.jd.bluedragon.distribution.jy.service.collect.strategy;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.core.base.WaybillPackageManager;
import com.jd.bluedragon.core.base.WaybillQueryManager;
import com.jd.bluedragon.core.jmq.producer.DefaultJMQProducer;
import com.jd.bluedragon.distribution.collection.entity.CollectionScanCodeEntity;
import com.jd.bluedragon.distribution.collection.enums.CollectionAggCodeTypeEnum;
import com.jd.bluedragon.distribution.collection.enums.CollectionScanCodeTypeEnum;
import com.jd.bluedragon.distribution.jy.dto.collect.CollectDto;
import com.jd.bluedragon.distribution.jy.dto.collect.InitCollectDto;
import com.jd.bluedragon.distribution.jy.dto.collect.InitCollectSplitDto;
import com.jd.bluedragon.distribution.jy.enums.ScanCodeTypeEnum;
import com.jd.bluedragon.distribution.jy.exception.JyBizException;
import com.jd.bluedragon.distribution.jy.service.collect.JyCollectCacheService;
import com.jd.bluedragon.distribution.jy.service.collect.JyCollectService;
import com.jd.bluedragon.distribution.jy.service.collect.constant.CollectServiceConstant;
import com.jd.bluedragon.distribution.jy.service.collect.emuns.CollectInitNodeEnum;
import com.jd.bluedragon.distribution.jy.service.collect.factory.CollectInitSplitServiceFactory;
import com.jd.bluedragon.distribution.waybill.service.WaybillService;
import com.jd.bluedragon.dms.utils.WaybillUtil;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.etms.waybill.domain.BaseEntity;
import com.jd.etms.waybill.domain.DeliveryPackageD;
import com.jd.etms.waybill.dto.BigWaybillDto;
import com.jd.etms.waybill.dto.WChoice;
import com.jd.jmq.common.message.Message;
import com.jd.jsf.gd.util.JsonUtils;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import com.jd.ump.profiler.CallerInfo;
import com.jd.ump.profiler.proxy.Profiler;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Author zhengchengfa
 * @Description //集齐初始化： 无任务扫描，按运单号分页拆分
 * @date
 **/
@Service
public class CollectWaybillInitSplitServiceImpl implements CollectInitSplitService, InitializingBean {
    private Logger log = LoggerFactory.getLogger(CollectWaybillInitSplitServiceImpl.class);

//    @Autowired
//    private CargoDetailServiceManager cargoDetailServiceManager;
    @Autowired
    private JyCollectService jyCollectService;
//    @Autowired
//    private VosManager vosManager;
//    @Autowired
//    private BaseMajorManager baseMajorManager;
    @Autowired
    WaybillQueryManager waybillQueryManager;
    @Autowired
    @Qualifier(value = "jyCollectDataPageInitProducer")
    private DefaultJMQProducer jyCollectDataPageInitProducer;
    @Autowired
    private WaybillPackageManager waybillPackageManager;
    @Autowired
    private WaybillService waybillService;
    @Autowired
    private JyCollectCacheService jyCollectCacheService;

    @Override
    public void afterPropertiesSet() throws Exception {
        CollectInitSplitServiceFactory.registerCollectInitSplitService(CollectInitNodeEnum.NULL_TASK_INIT.getCode(), this);
    }

    @Override
    @JProfiler(jKey = "CollectWaybillInitSplitServiceImpl.splitBeforeInit",jAppName= Constants.UMP_APP_NAME_DMSWEB,mState = {JProEnum.TP, JProEnum.FunctionError})
    public boolean splitBeforeInit(InitCollectDto initCollectDto) {
        String methodDesc = "CollectWaybillInitSplitServiceImpl.splitBeforeInit:集齐数据初始化前按运单拆分批次：";
        String waybillCode = WaybillUtil.getWaybillCode(initCollectDto.getTaskNullScanCode());

        if(!jyCollectCacheService.lockSaveTaskNullWaybillCollectSplitBeforeInit(initCollectDto)) {
            if(log.isInfoEnabled()) {
                log.info("{}未获取到锁，说明程序已经处理中，不在处理，paramDto={}", methodDesc, JsonHelper.toJson(initCollectDto));
            }
            return true;
        }
        try {
            if (jyCollectCacheService.cacheExistTaskNullWaybillCollectSplitBeforeInit(initCollectDto)) {
                if (log.isInfoEnabled()) {
                    log.info("{}防重缓存已存在，不在处理，paramDto={}", methodDesc, JsonHelper.toJson(initCollectDto));
                }
                return true;
            }

            BigWaybillDto bigWaybillDto = getWaybillPackage(waybillCode);
            if (bigWaybillDto == null || CollectionUtils.isEmpty(bigWaybillDto.getPackageList())) {
                log.warn("{}运单{}多包裹拆分任务, 查询包裹为空, reqDto:[{}]", methodDesc, waybillCode, JsonHelper.toJson(initCollectDto));
                return false;
            }
            int collectOneBatchSize = CollectServiceConstant.COLLECT_INIT_BATCH_DEAL_SIZE;
            int totalPackageNum = bigWaybillDto.getPackageList().size();
            int collectBatchPageTotal = (totalPackageNum % collectOneBatchSize) == 0 ? (totalPackageNum / collectOneBatchSize) : (totalPackageNum / collectOneBatchSize) + 1;

            List<Message> messageList = new ArrayList<>();
            for (int pageNo = 1; pageNo <= collectBatchPageTotal; pageNo++) {
                InitCollectSplitDto mqDto = new InitCollectSplitDto();
                mqDto.setBizId(initCollectDto.getBizId());
                mqDto.setOperateTime(initCollectDto.getOperateTime());
                mqDto.setPageSize(collectOneBatchSize);
                mqDto.setPageNo(pageNo);
                mqDto.setOperateNode(initCollectDto.getOperateNode());
                mqDto.setOperatorErp(initCollectDto.getOperatorErp());

                mqDto.setTaskNullScanCode(initCollectDto.getTaskNullScanCode());
                mqDto.setWaybillCode(waybillCode);
                mqDto.setTaskNullScanCodeType(initCollectDto.getTaskNullScanCodeType());
                mqDto.setTaskNullScanSiteCode(initCollectDto.getTaskNullScanSiteCode());
                String msgText = JsonUtils.toJSONString(mqDto);
                if(log.isInfoEnabled()) {
                    log.info("{}.splitSendMq, msg={}", methodDesc, msgText);
                }
                //todo 批量发jmq
                messageList.add(new Message(jyCollectDataPageInitProducer.getTopic(),msgText,getBusinessId(mqDto)));

            }
            jyCollectDataPageInitProducer.batchSendOnFailPersistent(messageList);
            jyCollectCacheService.cacheSaveTaskNullWaybillCollectSplitBeforeInit(initCollectDto);
            return true;
        }catch (Exception e) {
            log.error("{},服务异常，request={},errMsg={}", methodDesc, JsonHelper.toJson(initCollectDto), e.getMessage(), e);
            throw new JyBizException("空任务扫描按运单处理集齐初始化前的拆分逻辑异常" + e.getMessage());
        }finally {
            jyCollectCacheService.lockDelTaskNullWaybillCollectSplitBeforeInit(initCollectDto);
        }
    }

    private String getBusinessId(InitCollectSplitDto mqDto) {
        StringBuilder sb = new StringBuilder();
        sb.append(mqDto.getBizId()).append(Constants.SEPARATOR_COLON)
                .append(mqDto.getWaybillCode()).append(Constants.SEPARATOR_COLON)
                .append(mqDto.getTaskNullScanCodeType()).append(Constants.SEPARATOR_COLON)
                .append(mqDto.getPageNo()).append(Constants.SEPARATOR_COLON)
                .append(mqDto.getPageSize());
        return sb.toString();
    }

    @Override
    @JProfiler(jKey = "CollectWaybillInitSplitServiceImpl.initAfterSplit",jAppName= Constants.UMP_APP_NAME_DMSWEB,mState = {JProEnum.TP, JProEnum.FunctionError})
    public boolean initAfterSplit(InitCollectSplitDto request) {
        if(ScanCodeTypeEnum.SCAN_WAYBILL.getCode().equals(request.getTaskNullScanCodeType())) {
            return batchCollectInitAndUpdateStatus(request);
        }else if(ScanCodeTypeEnum.SCAN_PACKAGE.getCode().equals(request.getTaskNullScanCodeType())) {
            return batchCollectInit(request);
        }else {
            log.warn("CollectWaybillInitSplitServiceImpl.initAfterSplit:不做集齐初始化动作,req={}", JsonUtils.toJSONString(request));
            return true;
        }
    }

    /**
     * 按包裹验货仅做集齐初始化
     * @param request
     * @return
     */
    private boolean batchCollectInit(InitCollectSplitDto request) {
        CallerInfo info = Profiler.registerInfo("DMSWEB.CollectWaybillInitSplitServiceImpl.batchCollectInit",Constants.UMP_APP_NAME_DMSWEB, false, true);

        String methodDesc = "CollectSealCarBatchInitSplitServiceImpl.batchCollectInit:集齐数据按运单拆分批次后初始化：";
        if (jyCollectCacheService.cacheExistTaskNullWaybillCollectInitAfterSplit(request)) {
            if (log.isInfoEnabled()) {
                log.info("{}防重缓存已存在，不在处理，paramDto={}", methodDesc, JsonHelper.toJson(request));
            }
            return true;
        }
        String waybillCode = getWaybillCode(request);
        List<String> packageCodeList = getPageNoPackageCodeListFromWaybill(waybillCode, request.getPageNo(), request.getPageSize());
        Integer nextSiteId = waybillService.getRouterFromMasterDb(waybillCode, request.getTaskNullScanSiteCode());
        if(nextSiteId == null) {
            log.warn("{}运单{}查询下游流向为空，reqDto={}", methodDesc, waybillCode, JsonHelper.toJson(request));
        }
        List<CollectionScanCodeEntity> collectionScanCodeEntityList = new ArrayList<>();
        for(String packageCode : packageCodeList){
            CollectionScanCodeEntity collectionScanCodeEntity = new CollectionScanCodeEntity();
            collectionScanCodeEntity.setScanCode(packageCode);
            collectionScanCodeEntity.setScanCodeType(CollectionScanCodeTypeEnum.package_code);
            collectionScanCodeEntity.setCollectedMark(request.getBizId());
            collectionScanCodeEntity.setCollectionAggCodeMaps(Collections.singletonMap(CollectionAggCodeTypeEnum.waybill_code, WaybillUtil.getWaybillCode(packageCode)));
            collectionScanCodeEntityList.add(collectionScanCodeEntity);
        }
        CollectDto collectDto = new CollectDto();
        collectDto.setCollectNodeSiteCode(request.getTaskNullScanSiteCode());
        collectDto.setBizId(request.getBizId());
        collectDto.setWaybillCode(request.getWaybillCode());
        collectDto.setNextSiteCode(nextSiteId);
        collectDto.setOperatorErp(request.getOperatorErp());

        boolean res = jyCollectService.initCollect(collectDto, collectionScanCodeEntityList);
        if(res) {
            jyCollectCacheService.cacheSaveTaskNullWaybillCollectInitAfterSplit(request);
        }
        Profiler.registerInfoEnd(info);
        return res;
    }

    /**
     * 按运单扫描做集齐初始化并修改集齐状态
     * @param request
     * @return
     */
    private boolean batchCollectInitAndUpdateStatus(InitCollectSplitDto request) {
        CallerInfo info = Profiler.registerInfo("DMSWEB.CollectWaybillInitSplitServiceImpl.batchCollectInitAndUpdateStatus",Constants.UMP_APP_NAME_DMSWEB, false, true);

        String methodDesc = "CollectWaybillInitSplitServiceImpl.batchCollectInitAndUpdateStatus:集齐数据按运单拆分批次后初始化并修改集齐状态：";
        if (jyCollectCacheService.cacheExistTaskNullWaybillCollectInitAfterSplit(request)) {
            if (log.isInfoEnabled()) {
                log.info("{}防重缓存已存在，不在处理，paramDto={}", methodDesc, JsonHelper.toJson(request));
            }
            return true;
        }
        String waybillCode = getWaybillCode(request);
        List<String> packageCodeList = getPageNoPackageCodeListFromWaybill(waybillCode, request.getPageNo(), request.getPageSize());
        Integer nextSiteId = waybillService.getRouterFromMasterDb(waybillCode, request.getTaskNullScanSiteCode());
        if(nextSiteId == null) {
            log.warn("{}运单{}查询下游流向为空，reqDto={}", methodDesc, waybillCode, JsonHelper.toJson(request));
        }
        CollectDto collectDto = new CollectDto();
        collectDto.setCollectNodeSiteCode(request.getTaskNullScanSiteCode());
        collectDto.setBizId(request.getBizId());
        collectDto.setWaybillCode(waybillCode);
        collectDto.setNextSiteCode(nextSiteId);
        collectDto.setOperatorErp(request.getOperatorErp());

        boolean res = jyCollectService.initAndCollectedPartCollection(collectDto,
            packageCodeList.parallelStream().map(packageCode -> {
                CollectionScanCodeEntity collectionScanCodeEntity = new CollectionScanCodeEntity();
                collectionScanCodeEntity.setScanCode(packageCode);
                collectionScanCodeEntity.setScanCodeType(CollectionScanCodeTypeEnum.package_code);
                collectionScanCodeEntity.setCollectedMark(request.getBizId());
                collectionScanCodeEntity.setCollectionAggCodeMaps(Collections.singletonMap(CollectionAggCodeTypeEnum.waybill_code, waybillCode));
                return collectionScanCodeEntity;
            }).collect(Collectors.toList()));

        if(res) {
            jyCollectCacheService.cacheSaveTaskNullWaybillCollectInitAfterSplit(request);
        }
        Profiler.registerInfoEnd(info);
        return res;
    }

    private List<String> getPageNoPackageCodeListFromWaybill(String waybillCode, int pageNo, int pageSize) {
        // 分页查询包裹数据
        BigWaybillDto bigWaybillDto = getWaybill(waybillCode, pageNo, pageSize);
        List<String> res = new ArrayList<>();
        List<DeliveryPackageD> packages=bigWaybillDto.getPackageList();
        for (DeliveryPackageD pack : packages) {
            res.add(pack.getPackageBarcode());
        }
        return res;
    }

    private BigWaybillDto getWaybillPackage(String waybillCode) {
        BigWaybillDto result = null;
        BaseEntity<BigWaybillDto> baseEntity = waybillQueryManager.getDataByChoice(waybillCode,true, false, true, true);
        if (baseEntity != null) {
            result = baseEntity.getData();
        }
        if (log.isInfoEnabled()){
            log.info(MessageFormat.format("获取运单信息{0}, 结果为{1}", waybillCode, JsonHelper.toJson(result)));
        }

        return result;
    }

    private String getWaybillCode(InitCollectSplitDto request) {
        if (StringUtils.isNotBlank(request.getWaybillCode())) {
            return request.getWaybillCode();
        }
        else {
            String waybillCode = WaybillUtil.getWaybillCode(request.getTaskNullScanCode());
            request.setWaybillCode(waybillCode);
            return waybillCode;
        }
    }

    /**
     * 分页获取包裹数据
     * @param waybillCode
     * @param pageNo
     * @param pageSize
     * @return
     */
    private BigWaybillDto getWaybill(String waybillCode, int pageNo, int pageSize) {

        WChoice wChoice = new WChoice();
        wChoice.setQueryWaybillC(true);
        wChoice.setQueryWaybillE(false);
        wChoice.setQueryWaybillM(true); // 查询waybillState

        BaseEntity<BigWaybillDto> baseEntity = waybillQueryManager.getDataByChoice(waybillCode, wChoice);
        BigWaybillDto bigWaybillDto = null;
        if (baseEntity != null && baseEntity.getData()!= null) {
            bigWaybillDto = baseEntity.getData();
            BaseEntity<List<DeliveryPackageD>> pageLists =
                    this.waybillPackageManager.getPackListByWaybillCodeOfPage(waybillCode, pageNo, pageSize);
            if (pageLists != null && pageLists.getData() != null ) {
                bigWaybillDto.setPackageList(pageLists.getData());
            }
        }

        return bigWaybillDto;
    }

}
