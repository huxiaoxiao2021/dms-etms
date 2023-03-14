package com.jd.bluedragon.distribution.jy.service.collect.strategy;

import com.jd.bluedragon.core.base.BaseMajorManager;
import com.jd.bluedragon.core.base.CargoDetailServiceManager;
import com.jd.bluedragon.core.base.VosManager;
import com.jd.bluedragon.core.jmq.producer.DefaultJMQProducer;
import com.jd.bluedragon.distribution.collection.entity.CollectionScanCodeEntity;
import com.jd.bluedragon.distribution.collection.enums.CollectionAggCodeTypeEnum;
import com.jd.bluedragon.distribution.collection.enums.CollectionScanCodeTypeEnum;
import com.jd.bluedragon.distribution.jy.dto.collect.CollectDto;
import com.jd.bluedragon.distribution.jy.dto.collect.InitCollectDto;
import com.jd.bluedragon.distribution.jy.dto.collect.InitCollectSplitDto;
import com.jd.bluedragon.distribution.jy.exception.JyBizException;
import com.jd.bluedragon.distribution.jy.service.collect.JyCollectCacheService;
import com.jd.bluedragon.distribution.jy.service.collect.JyCollectService;
import com.jd.bluedragon.distribution.jy.service.collect.constant.CollectServiceConstant;
import com.jd.bluedragon.distribution.jy.service.collect.emuns.CollectInitNodeEnum;
import com.jd.bluedragon.distribution.jy.service.collect.factory.CollectInitSplitServiceFactory;
import com.jd.bluedragon.distribution.waybill.service.WaybillService;
import com.jd.bluedragon.dms.utils.BusinessUtil;
import com.jd.bluedragon.dms.utils.WaybillUtil;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.etms.vos.dto.SealCarDto;
import com.jd.jsf.gd.util.JsonUtils;
import com.jd.ql.basic.dto.BaseStaffSiteOrgDto;
import com.jd.tms.data.dto.CargoDetailDto;
import com.jd.tms.data.dto.CommonDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @Author zhengchengfa
 * @Description //集齐初始化： 按封车内批次号分页拆分
 * @date
 **/
public class CollectSealCarBatchInitSplitServiceImpl implements CollectInitSplitService, InitializingBean {
    private Logger log = LoggerFactory.getLogger(CollectSealCarBatchInitSplitServiceImpl.class);

    @Autowired
    private CargoDetailServiceManager cargoDetailServiceManager;
    @Autowired
    private JyCollectService jyCollectService;
    @Autowired
    private VosManager vosManager;
    @Autowired
    private BaseMajorManager baseMajorManager;
    @Autowired
    private WaybillService waybillService;
    @Autowired
    @Qualifier(value = "jyCollectDataPageInitProducer")
    private DefaultJMQProducer jyCollectDataPageInitProducer;
    @Autowired
    private JyCollectCacheService jyCollectCacheService;


    @Override
    public void afterPropertiesSet() throws Exception {
        CollectInitSplitServiceFactory.registerCollectInitSplitService(CollectInitNodeEnum.SEAL_INIT.getCode(), this);
    }


    @Override
    public boolean splitBeforeInit(InitCollectDto initCollectDto) {

        String methodDesc = "CollectSealCarBatchInitSplitServiceImpl.splitBeforeInit:封车节点集齐数据初始化前拆分逻辑：";
        if (jyCollectCacheService.cacheExistSealCarCollectSplitBeforeInit(initCollectDto)) {
            if (log.isInfoEnabled()) {
                log.info("{}防重缓存已存在，不在处理，paramDto={}", methodDesc, JsonHelper.toJson(initCollectDto));
            }
            return true;
        }
        //只处理 到拣运中心的数据, 封车,解封车进围栏 状态数据
        SealCarDto sealCarInfoBySealCarCodeOfTms = vosManager.findSealCarInfoBySealCarCodeOfTms(initCollectDto.getBizId());
        if(sealCarInfoBySealCarCodeOfTms == null){
            log.error("从运输未获取到封车信息,{}", JsonHelper.toJson(initCollectDto));
            return false;
        }
        Integer endSiteId = sealCarInfoBySealCarCodeOfTms.getEndSiteId();
        //检查目的地是否是拣运中心
        BaseStaffSiteOrgDto siteInfo = baseMajorManager.getBaseSiteBySiteId(endSiteId);
        if(siteInfo == null || !BusinessUtil.isSorting(siteInfo.getSiteType())){
            //丢弃数据
            log.info("不需要关心的数据丢弃,目的站点:{},目的站点类型:{}，消息:{}",endSiteId,siteInfo==null?null:siteInfo.getSiteType(),
                    JsonHelper.toJson(initCollectDto));
            return true;
        }

        List<String> batchCodes = sealCarInfoBySealCarCodeOfTms.getBatchCodes();
        if (batchCodes == null) {
            log.warn("封车编码【{}】没有获取到对应的批次信息!,message={}", initCollectDto.getBizId(), JsonHelper.toJson(initCollectDto));
            return true;
        }
        for (String batchCode : batchCodes){
            splitBeforeInitSendMq(batchCode, initCollectDto, sealCarInfoBySealCarCodeOfTms);
        }
        jyCollectCacheService.cacheSaveSealCarCollectSplitBeforeInit(initCollectDto);
        return true;
    }

    @Override
    public boolean initAfterSplit(InitCollectSplitDto initCollectSplitDto) {
        String methodDesc = "CollectSealCarBatchInitSplitServiceImpl.initAfterSplit:封车节点集齐数据拆分后执行初始化：";
        if (jyCollectCacheService.cacheExistSealCarCollectInitAfterSplit(initCollectSplitDto)) {
            if (log.isInfoEnabled()) {
                log.info("{}防重缓存已存在，不在处理，paramDto={}", methodDesc, JsonHelper.toJson(initCollectSplitDto));
            }
            return true;
        }
        List<String> pcList = this.getPageNoPackageCodeListFromTms(initCollectSplitDto);

        //处理包裹数据，并按照运单分批
        Map<String, List<CollectionScanCodeEntity>> waybillCodeMap = pcList.parallelStream().map(s -> {
            CollectionScanCodeEntity collectionScanCodeEntity = new CollectionScanCodeEntity();
            collectionScanCodeEntity.setScanCode(s);
            collectionScanCodeEntity.setScanCodeType(CollectionScanCodeTypeEnum.package_code);
            collectionScanCodeEntity.setCollectedMark(initCollectSplitDto.getBizId());
            collectionScanCodeEntity.setCollectionAggCodeMaps(Collections.singletonMap(CollectionAggCodeTypeEnum.waybill_code, WaybillUtil.getWaybillCode(s)));
            return collectionScanCodeEntity;
        }).collect(Collectors.groupingBy(
            collectionScanCodeEntity ->
                collectionScanCodeEntity.getCollectionAggCodeMaps().getOrDefault(CollectionAggCodeTypeEnum.waybill_code, null))
        );

        waybillCodeMap.forEach((waybillCode, collectionScanCodeEntities) -> {
            Integer nextSiteId = waybillService.getRouterFromMasterDb(waybillCode, initCollectSplitDto.getShouldUnSealSiteCode());
            if(nextSiteId == null) {
                log.warn("CollectSealCarBatchInitSplitServiceImpl.initAfterSplit集齐运单查询下游流向为空，运单号：{}，查询站点：{}"
                    ,waybillCode, initCollectSplitDto.getShouldUnSealSiteCode());
            }
            CollectDto collectDto = new CollectDto();
            collectDto.setCollectNodeSiteCode(initCollectSplitDto.getShouldUnSealSiteCode());
            collectDto.setBizId(initCollectSplitDto.getBizId());
            collectDto.setWaybillCode(initCollectSplitDto.getWaybillCode());
            collectDto.setNextSiteCode(nextSiteId);
            collectDto.setOperatorErp(initCollectSplitDto.getOperatorErp());//erp传集齐场地实操人，封车是上游场地操作节点，非集齐场地（下游解封车场地是集齐场地）无需记录操作人erp

            jyCollectService.initCollect(collectDto, collectionScanCodeEntities);
        });

        jyCollectCacheService.cacheSaveSealCarCollectInitAfterSplit(initCollectSplitDto);
        return true;
    }

    private void splitBeforeInitSendMq(String batchCode, InitCollectDto initCollectDto, SealCarDto sealCarDto) {
        CargoDetailDto cargoDetailDto = new CargoDetailDto();
        cargoDetailDto.setBatchCode(batchCode);
        cargoDetailDto.setYn(1);
        //每次去运输获取数据的量;
        int limitSize = 1000;
        int currentSize  = limitSize ;
        int offset = 0;

       int collectOneBatchSize = CollectServiceConstant.COLLECT_INIT_BATCH_DEAL_SIZE;
        //集齐处理页容量
        int collectBatchPageTotal = collectOneBatchSize > limitSize ? limitSize : collectOneBatchSize;

        while(currentSize >= limitSize){
            CommonDto<List<CargoDetailDto>> cargoDetailReturn = cargoDetailServiceManager.getCargoDetailInfoByBatchCode(cargoDetailDto,offset,limitSize);
            if(cargoDetailReturn == null || cargoDetailReturn.getCode() != com.jd.etms.vos.dto.CommonDto.CODE_SUCCESS ) {
                log.error("获取批次{}下包裹数据异常,条件：offset={},limitSize={}, result={}",batchCode,offset,limitSize, JsonHelper.toJson(cargoDetailReturn));
                throw new JyBizException("运输接口根据批次获取包裹信息异常");
            }
            if(cargoDetailReturn.getData().isEmpty()) {
                break;
            }
            currentSize = cargoDetailReturn.getData().size();
            //发送集齐拆分的最大分页pageNo
            int collectBatchMaxPageNo = currentSize / collectBatchPageTotal + (currentSize % collectBatchPageTotal > 0 ? 1 : 0);
            for(int pageNo = 1; pageNo <= collectBatchMaxPageNo; pageNo++ ) {
                InitCollectSplitDto mqDto = new InitCollectSplitDto();
                mqDto.setBizId(initCollectDto.getBizId());
                mqDto.setOperateTime(initCollectDto.getOperateTime());
                mqDto.setPageSize(collectBatchPageTotal);
                mqDto.setPageNo(pageNo);
                mqDto.setSealBatchCode(batchCode);
                mqDto.setOperateNode(initCollectDto.getOperateNode());
                mqDto.setSealSiteCode(sealCarDto.getSealSiteId());
                mqDto.setShouldUnSealSiteCode(sealCarDto.getEndSiteId());
                String businessId = String.format("%:%s", mqDto.getSealBatchCode(), mqDto.getPageNo());
                String msg = JsonUtils.toJSONString(mqDto);
                if(log.isInfoEnabled()) {
                    log.info("CollectSealCarBatchInItSplitServiceImpl.splitSendMq:封车节点集齐数据初始化按批次号拆分producer, msg={}", msg);
                }
                jyCollectDataPageInitProducer.sendOnFailPersistent(businessId, msg);
            }
            offset =  offset + limitSize;
        }
    }


    private List<String> getPageNoPackageCodeListFromTms(InitCollectSplitDto initCollectSplitDto) {
        CargoDetailDto cargoDetailDto = new CargoDetailDto();
        cargoDetailDto.setBatchCode(initCollectSplitDto.getSealBatchCode());
        cargoDetailDto.setYn(1);
        int offset = ( initCollectSplitDto.getPageNo() -1 ) * initCollectSplitDto.getPageSize() ;

        CommonDto<List<CargoDetailDto>> cargoDetailReturn = cargoDetailServiceManager.getCargoDetailInfoByBatchCode(cargoDetailDto,offset,initCollectSplitDto.getPageSize());
        if(cargoDetailReturn == null || cargoDetailReturn.getCode() != com.jd.etms.vos.dto.CommonDto.CODE_SUCCESS ) {
            log.error("getPageNoPackageListFromTms获取批次{}下包裹数据异常,条件={}, result={}",JsonUtils.toJSONString(initCollectSplitDto), JsonHelper.toJson(cargoDetailReturn));
            throw new JyBizException("运输接口根据批次获取包裹信息异常");
        }
        if(cargoDetailReturn.getData().isEmpty()) {
            log.warn("");
        }
        List<CargoDetailDto> cargoDetailDtoList =  cargoDetailReturn.getData();
        List<String> packageCodeList = new ArrayList<>();
        log.info("从运输系统获取批次【{}】包裹信息,offset={},dto={}", initCollectSplitDto.getSealBatchCode(), offset, JsonUtils.toJSONString(initCollectSplitDto));
        for(CargoDetailDto cargoDetailDtoTemp:cargoDetailDtoList){
            packageCodeList.add(cargoDetailDtoTemp.getPackageCode());
        }
        return packageCodeList;
    }




}
