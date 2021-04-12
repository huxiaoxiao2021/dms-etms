package com.jd.bluedragon.distribution.economic.service;

import IceInternal.Ex;
import com.alibaba.fastjson.JSONObject;
import com.jd.bluedragon.Constants;
import com.jd.bluedragon.core.base.BoxOperateApiManager;
import com.jd.bluedragon.core.base.WaybillQueryManager;
import com.jd.bluedragon.core.base.WeightVolumeFlowJSFManager;
import com.jd.bluedragon.core.jmq.producer.DefaultJMQProducer;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.base.service.SiteService;
import com.jd.bluedragon.distribution.box.domain.Box;
import com.jd.bluedragon.distribution.box.service.BoxService;
import com.jd.bluedragon.distribution.economic.domain.EconomicNetException;
import com.jd.bluedragon.distribution.log.BusinessLogProfilerBuilder;
import com.jd.bluedragon.distribution.third.domain.ThirdBoxDetail;
import com.jd.bluedragon.distribution.third.service.ThirdBoxDetailService;
import com.jd.bluedragon.distribution.weightVolume.domain.WeightVolumeEntity;
import com.jd.bluedragon.distribution.weightVolume.handler.WeightVolumeHandlerStrategy;
import com.jd.bluedragon.distribution.weightvolume.FromSourceEnum;
import com.jd.bluedragon.distribution.weightvolume.WeightVolumeBusinessTypeEnum;
import com.jd.bluedragon.dms.utils.BusinessUtil;
import com.jd.bluedragon.dms.utils.WaybillUtil;
import com.jd.bluedragon.external.crossbow.economicNet.domain.EconomicNetBoxWeightVolumeMq;
import com.jd.bluedragon.utils.*;
import com.jd.bluedragon.utils.log.BusinessLogConstans;
import com.jd.dms.logger.external.BusinessLogProfiler;
import com.jd.dms.logger.external.LogEngine;
import com.jd.etms.waybill.domain.Waybill;
import com.jd.jmq.common.message.Message;
import com.jd.ql.basic.dto.BaseStaffSiteOrgDto;
import com.jd.ql.dms.common.cache.CacheService;
import com.jd.ql.dms.report.weightVolumeFlow.domain.WeightVolumeFlowEntity;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import com.zhongyouex.order.api.dto.BoxDetailInfoDto;
import com.zhongyouex.order.api.dto.BoxDetailResultDto;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.collections4.SetUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.TimeUnit;

import static com.jd.bluedragon.Constants.TENANT_CODE_ECONOMIC;

/**
 * 天官赐福 ◎ 百无禁忌
 *
 * @Auther: 刘铎（liuduo8）
 * @Date: 2021/1/20 11:08
 * @Description: 经济网业务逻辑
 */
@Service("economicNetService")
public class EconomicNetServiceImpl implements IEconomicNetService{

    private String LOCK_BEGIN = "enet:s:";

    private String LOAD_SAVE_BEGIN = "enet:box:w:";

    private String EQUALIZATION_FOMART = "enet:e:%s:%s:%s";


    @Autowired
    private LogEngine logEngine;

    private Integer DEFAULT_BATCH_ADD = 50;

    private Integer DEFAULT_BOX_SPLIT = 1;

    private Integer DEFAULT_BATCH_QUERY_ES = 300;

    private Logger logger = LoggerFactory.getLogger(EconomicNetServiceImpl.class);

    @Autowired
    private BoxOperateApiManager boxOperateApiManager;

    @Autowired
    @Qualifier("jimdbCacheService")
    private CacheService cacheService;

    @Autowired
    private ThirdBoxDetailService thirdBoxDetailService;

    @Autowired
    private WeightVolumeHandlerStrategy weightVolumeHandlerStrategy;

    @Autowired
    private WeightVolumeFlowJSFManager weightVolumeFlowJSFManager;

    /*运单查询*/
    @Autowired
    private WaybillQueryManager waybillQueryManager;

    @Autowired
    private BoxService boxService;

    @Autowired
    private SiteService siteService;

    @Autowired
    @Qualifier("economicNetSplitWeightVolumeFlowProducer")
    private DefaultJMQProducer economicNetSplitWeightVolumeFlowProducer;
    /**
     * 此箱号是否完成初始三方装箱包关系
     *
     * @param box
     * @return
     */
    @Override
    public boolean isReady(Box box) {
        // 当前箱子没有被锁定 并且 已初始化完成所有箱包关系数据，现阶段怎么认为所有箱号关系 则才认为已经准备就绪
        if(!isLock(box)){
            return thirdBoxDetailService.isExist(TENANT_CODE_ECONOMIC,box.getCreateSiteCode(),box.getCode());
        }
        return false;
    }

    /**
     * 加载存储箱包关系
     *
     * @param box
     * @return
     */
    @Override
    @Transactional(value = "main_undiv", propagation = Propagation.REQUIRED, rollbackFor = Throwable.class)
    @JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWORKER,jKey = "DMS.BASE.EconomicNetServiceImpl.loadAndSaveBoxPackageData", mState = {JProEnum.TP, JProEnum.FunctionError})
    public boolean loadAndSaveBoxPackageData(Box box) {
        if(box == null){
            logger.error("加载存储箱包关系锁失败,箱号为空{}",JsonHelper.toJson(box));
            return false;
        }
        if(!lock(box)){
            throw new EconomicNetException("加载存储箱包关系锁失败，请稍后重试");
        }
        try{
            //检查是否已加载过
            if(isLoadSaveFlag(box.getCode())){
                if(logger.isWarnEnabled()){
                    logger.warn("经济网已加载过数据，已第一次加载为准{}",JsonHelper.toJson(box));
                }
                return true;
            }
            String boxCode = box.getCode();
            //获取装箱明细
            List<BoxDetailInfoDto> resultDto = boxOperateApiManager.findBoxDetailInfoList(boxCode);

            if(CollectionUtils.isEmpty(resultDto)){
                logger.error("未加载到装箱数据，箱号 {}，返回消息 {}",boxCode, JsonHelper.toJson(resultDto));
                return false;
            }

            if(logger.isInfoEnabled()){
                logger.info("boxOperateApiManager.findBoxDetailInfoList {} find size",boxCode,resultDto.size());
            }
            List<ThirdBoxDetail> thirdBoxDetails = new ArrayList<>();
            for(BoxDetailInfoDto boxDetailInfoDto : resultDto){
                thirdBoxDetails.add(convertThirdBoxDetail(boxDetailInfoDto,box));
            }
            List<List<ThirdBoxDetail>> partition = ListUtils.partition(thirdBoxDetails, DEFAULT_BATCH_ADD);
            for(List<ThirdBoxDetail> batchData : partition ){
                if(!thirdBoxDetailService.batchAdd(batchData)){
                    throw new EconomicNetException("批量保存箱包关系失败");
                }
            }
            //保存所有装箱明细后需要移除单独称重数据
            List<ThirdBoxDetail> existFlowDetails = findExistFlowPackages(thirdBoxDetails);
            if(existFlowDetails!=null && !existFlowDetails.isEmpty()){
                //存在 则逻辑移除，保留数据方便排查问题使用
                for(ThirdBoxDetail thirdBoxDetail : existFlowDetails){
                    thirdBoxDetailService.cancelNoCareSite(thirdBoxDetail);
                }
            }
            //保存加载记录
            addLoadSaveFlag(box.getCode());
            //保存操作日志
            loadAndSaveBoxPackageDataSuccessLog(box,resultDto.size());
            return true;
        }catch (Exception e){
            logger.error("loadAndSaveBoxPackageData error! {} {}",JsonHelper.toJson(box),e.getMessage(),e);
            //移除加载记录
            delLoadSaveFlag(box.getCode());
            loadAndSaveBoxPackageDataFailLog(box,e.getMessage());
            throw new EconomicNetException("批量保存箱包关系失败"+e.getMessage());
        }finally {
            unLock(box);
        }

    }

    /**
     * 均分称重量方数据
     *  排除包裹或运动的单独已称重数据再进行均分
     * @param box
     * @return
     */
    @Override
    @JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWORKER,jKey = "DMS.BASE.EconomicNetServiceImpl.equalizationWeightAndVolume", mState = {JProEnum.TP, JProEnum.FunctionError})
    public boolean equalizationWeightAndVolume(Box box, WeightVolumeEntity vo) {
        if(logger.isInfoEnabled()){
            logger.info("经济网箱号重量均摊重算 {}",JsonHelper.toJson(vo));
        }
        //最终未称重的明细数据
        List<ThirdBoxDetail> noExistFlowDetails = new ArrayList<>();
        //获取明细 分页异步处理
        List<ThirdBoxDetail> thirdBoxDetails = thirdBoxDetailService.queryByBoxCode(TENANT_CODE_ECONOMIC,box.getCreateSiteCode(),box.getCode());
        if(thirdBoxDetails == null || thirdBoxDetails.isEmpty()){
            logger.error("均分称重量方数据失败，无明细数据 {}",JsonHelper.toJson(box));
            return false;
        }
        noExistFlowDetails.addAll(thirdBoxDetails);
        //获取已称重数据 暂时认为数据库的里数据就是已经排除过称重的关系数据
        /* List<ThirdBoxDetail> existFlowDetails = findExistFlowPackages(thirdBoxDetails);
        noExistFlowDetails.removeAll(existFlowDetails); */
        //均分称重量方数据
        Double itemWeight = vo.getWeight() == null ? Constants.DOUBLE_ZERO :  vo.getWeight() / noExistFlowDetails.size();
        Double itemLength = vo.getLength();
        Double itemWidth = vo.getWidth();
        Double itemHeight = vo.getHeight() == null ? Constants.DOUBLE_ZERO : vo.getHeight() / noExistFlowDetails.size();
        //重量体积保留小数点1位 四舍五入
        BigDecimal bw = BigDecimal.valueOf(itemWeight);
        itemWeight = bw.setScale(3, BigDecimal.ROUND_HALF_UP).doubleValue();
        BigDecimal bh = BigDecimal.valueOf(itemHeight);
        itemHeight = bh.setScale(3, BigDecimal.ROUND_HALF_UP).doubleValue();

        List<Message> messages = new ArrayList<>();
        // 分批处理
        List<List<ThirdBoxDetail>> pageFlows = ListUtils.partition(noExistFlowDetails,DEFAULT_BOX_SPLIT);
        for(List<ThirdBoxDetail> pageFlow : pageFlows){
            Message message = new Message();
            List<WeightVolumeEntity> pageWeightEntities = new ArrayList<>();
            message.setBusinessId(box.getCode());
            for(ThirdBoxDetail entity: pageFlow){
                WeightVolumeEntity itemEntity = new WeightVolumeEntity();
                itemEntity.setBarCode(entity.getPackageCode());
                itemEntity.setWaybillCode(entity.getWaybillCode());
                itemEntity.setBoxCode(entity.getBoxCode());
                itemEntity.setWeight(itemWeight);
                itemEntity.setLength(itemLength);
                itemEntity.setWidth(itemWidth);
                itemEntity.setHeight(itemHeight);
                itemEntity.setOperateSiteCode(vo.getOperateSiteCode());
                itemEntity.setOperateSiteName(vo.getOperateSiteName());
                itemEntity.setOperatorId(vo.getOperatorId());
                itemEntity.setOperatorCode(vo.getOperatorCode());
                itemEntity.setOperatorName(vo.getOperatorName());
                itemEntity.setOperateTime(vo.getOperateTime());
                itemEntity.setBusinessType(WeightVolumeBusinessTypeEnum.BY_PACKAGE);
                itemEntity.setSourceCode(FromSourceEnum.ENET_BOX_SPLIT_PACKAGE);
                pageWeightEntities.add(itemEntity);
            }
            message.setTopic(economicNetSplitWeightVolumeFlowProducer.getTopic());
            message.setText(JsonHelper.toJson(pageWeightEntities));
            messages.add(message);
        }
        //批量推送消息
        economicNetSplitWeightVolumeFlowProducer.batchSendOnFailPersistent(messages);
        //记录日志
        equalizationWeightAndVolumeSuccessLog(box,vo,itemLength,itemWidth,itemHeight,itemWeight);
        return true;
    }

    /**
     * 称重量方数据监听
     *
     * @param weightVolumeEntity
     * @return
     */
    @Override
    @JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWORKER,jKey = "DMS.BASE.EconomicNetServiceImpl.packageOrWaybillWeightVolumeListener", mState = {JProEnum.TP, JProEnum.FunctionError})
    @Transactional(value = "main_undiv", propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public boolean packageOrWaybillWeightVolumeListener(WeightVolumeEntity weightVolumeEntity) {

        // 监听 按运单称重 或 按包裹称重数据
        List<FromSourceEnum> excludeSourceCode = Arrays.asList(
                FromSourceEnum.CLPS_WEIGHT_BY_BOX,FromSourceEnum.DMS_INNER_SPLIT,FromSourceEnum.ENET_BOX_SPLIT_PACKAGE);
        List<WeightVolumeBusinessTypeEnum> excludeBusinessType = Arrays.asList(
                WeightVolumeBusinessTypeEnum.BY_HANDOVER,WeightVolumeBusinessTypeEnum.BY_BOX);
        //排除部分数据
        if(excludeSourceCode.contains(weightVolumeEntity.getSourceCode())
                || excludeBusinessType.contains(weightVolumeEntity.getBusinessType())){
            return true;
        }
        try{
            //判断是否是经济网运单
            String waybillCode = weightVolumeEntity.getWaybillCode();
            String packageCode = weightVolumeEntity.getPackageCode();
            if(StringUtils.isBlank(waybillCode) && StringUtils.isNotBlank(packageCode)){
                waybillCode = WaybillUtil.getWaybillCode(packageCode);
            }
            if(StringUtils.isBlank(waybillCode)){
                logger.error("weightVolumeListener 未获取到运单号，{}",JsonHelper.toJson(weightVolumeEntity));
                return false;
            }
            //次包裹或运单已在本场地已触发过
            if(isEqualization(weightVolumeEntity.getWaybillCode(),weightVolumeEntity.getPackageCode(),String.valueOf(weightVolumeEntity.getOperateSiteCode()))){
                if(logger.isInfoEnabled()){
                    logger.info("经济网箱号重量均摊重算 已被执行过无需继续执行 {}",JsonHelper.toJson(weightVolumeEntity));
                }
                return true;
            }
            Waybill waybill = waybillQueryManager.getOnlyWaybillByWaybillCode(waybillCode);
            if(waybill == null || StringUtils.isBlank(waybill.getWaybillSign())){
                logger.error("weightVolumeListener 未获取到运单数据，{}",JsonHelper.toJson(weightVolumeEntity));
                return true;
            }
            if(!BusinessUtil.isBusinessNet(waybill.getWaybillSign())){
                // 非经济网运单排除
                return true;
            }
            //取出经济网箱包关系数据。如果存在则触发重新均摊
            String boxCode = null;
            List<ThirdBoxDetail> thirdBoxDetails = thirdBoxDetailService.queryByWaybillOrPackage(TENANT_CODE_ECONOMIC,waybillCode,packageCode);
            if(thirdBoxDetails == null || thirdBoxDetails.isEmpty()){
                // 不存在装箱数据
                return true;
            }else{
                //存在 且只有一个箱子 如果有多个就取最新的一条
                boxCode = thirdBoxDetails.get(0)!=null?thirdBoxDetails.get(0).getBoxCode():null;
            }
            if(StringUtils.isBlank(boxCode)){
                logger.error("weightVolumeListener 未获取到经济网装箱对应的箱号数据，{}",JsonHelper.toJson(weightVolumeEntity));
                return true;
            }
            //存在装箱数据，需要判断是否同场地内存在装箱称重，如果存在则触发重新分配
            WeightVolumeFlowEntity boxFlow = weightVolumeFlowJSFManager.findExistFlowOfBox(boxCode,weightVolumeEntity.getOperateSiteCode());
            if(boxFlow == null){
                //无箱号称重数据
                return true;
            }
            //存在同场地箱号称重数据
            /* 获取箱号的信息 */
            Box box = boxService.findBoxByCode(boxCode);
            if(box == null){
                logger.error("weightVolumeListener box is null! {}",boxCode);
                return true;
            }
            //解绑箱号和包裹关系
            ThirdBoxDetail cancelParam = new ThirdBoxDetail();
            cancelParam.setStartSiteId(box.getCreateSiteCode());
            cancelParam.setTenantCode(TENANT_CODE_ECONOMIC);
            cancelParam.setBoxCode(boxCode);
            cancelParam.setUpdateUserId(String.valueOf(weightVolumeEntity.getOperatorId()));
            cancelParam.setUpdateUserName(weightVolumeEntity.getOperatorName());
            cancelParam.setOperatorTime(weightVolumeEntity.getOperateTime());
            if(WaybillUtil.isPackageCode(weightVolumeEntity.getBarCode())){
                cancelParam.setPackageCode(weightVolumeEntity.getBarCode());
            }else{
                cancelParam.setWaybillCode(weightVolumeEntity.getBarCode());
            }
            if(thirdBoxDetailService.cancelNoCareSite(cancelParam)){
                //触发重算
                WeightVolumeEntity equalization = new WeightVolumeEntity();
                equalization.setLength(boxFlow.getLength());
                equalization.setWidth(boxFlow.getWidth());
                equalization.setHeight(boxFlow.getHeight());
                equalization.setWeight(boxFlow.getWeight());
                equalization.setVolume(boxFlow.getVolume());
                equalization.setOperatorId(weightVolumeEntity.getOperatorId());
                equalization.setOperatorCode(weightVolumeEntity.getOperatorCode());
                equalization.setOperatorName(weightVolumeEntity.getOperatorName());
                equalization.setOperateSiteCode(weightVolumeEntity.getOperateSiteCode());
                equalization.setOperateSiteName(weightVolumeEntity.getOperateSiteName());
                equalization.setOperateTime(weightVolumeEntity.getOperateTime());
                return equalizationWeightAndVolume(box,equalization);
            }else{
                throw new EconomicNetException("取消箱包关系失败");
            }

        }catch (Exception e){
            logger.error("equalizationWeightAndVolume error {}",JsonHelper.toJson(weightVolumeEntity),e);
            delEqualization(weightVolumeEntity.getWaybillCode(),weightVolumeEntity.getPackageCode(),String.valueOf(weightVolumeEntity.getOperateSiteCode()));
            throw new EconomicNetException(e.getMessage());
        }
    }

    /**
     * 称重量方数据监听 (箱号)
     *
     * @param weightVolumeEntity
     * @return
     */
    @Override
    @JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWORKER,jKey = "DMS.BASE.EconomicNetServiceImpl.boxWeightVolumeListener", mState = {JProEnum.TP, JProEnum.FunctionError})
    @Transactional(value = "main_undiv", propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public boolean boxWeightVolumeListener(WeightVolumeEntity weightVolumeEntity) {

        // 监听 按箱号称重数据

        List<WeightVolumeBusinessTypeEnum> includeBusinessType = Arrays.asList(WeightVolumeBusinessTypeEnum.BY_BOX);
        //排除部分数据
        if(!includeBusinessType.contains(weightVolumeEntity.getBusinessType())){
            return true;
        }

        /* 获取箱号的信息 */
        Box box = boxService.findBoxByCode(weightVolumeEntity.getBarCode());
        if(box == null){
            logger.error("economicNetBoxWeightConsumer box is null! {}",JsonHelper.toJson(weightVolumeEntity));
            return false;
        }
        BaseStaffSiteOrgDto siteEntity = siteService.getSite(box.getCreateSiteCode());
        if (siteEntity == null || siteEntity.getSiteType() != BaseContants.ECONOMIC_NET_SITE) {
            logger.info("economicNetBoxWeightConsumer siteEntity not satisfy! {}",JsonHelper.toJson(weightVolumeEntity));
            return true;
        }
        //获取箱包关系批量存储
        if(loadAndSaveBoxPackageData(box)){
            //按包裹散列称重数据
            return equalizationWeightAndVolume(box,weightVolumeEntity);
        }

        return false;
    }

    /**
     * 处理箱号分页称重数据
     *
     * @param entities
     * @return
     */
    @Override
    @JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWORKER,jKey = "DMS.BASE.EconomicNetServiceImpl.dealBoxSplitWeightOfPage", mState = {JProEnum.TP, JProEnum.FunctionError})
    public boolean dealBoxSplitWeightOfPage(List<WeightVolumeEntity> entities) {

        if(!CollectionUtils.isEmpty(entities)){
            for(WeightVolumeEntity entity : entities){
                InvokeResult<Boolean> result = weightVolumeHandlerStrategy.doHandler(entity);
                if(result == null || !result.getData()){
                    //单个称重失败 暂定触发重新推送
                    throw new EconomicNetException("经济网单个推送包裹称重数据异常"+entity.getBarCode());
                }
            }
        }

        return true;
    }

    /**
     * 转换
     * @param boxDetailInfoDto
     * @param box
     * @return
     */
    private ThirdBoxDetail convertThirdBoxDetail(BoxDetailInfoDto boxDetailInfoDto,Box box){
        ThirdBoxDetail detail = new ThirdBoxDetail();
        detail.setTenantCode(Constants.TENANT_CODE_ECONOMIC);
        detail.setStartSiteId(box.getCreateSiteCode());
        detail.setStartSiteCode(boxDetailInfoDto.getStartSiteCode());
        detail.setEndSiteId(box.getReceiveSiteCode());
        detail.setEndSiteCode(boxDetailInfoDto.getEndSiteCode());
        detail.setOperatorId(boxDetailInfoDto.getOperatorId());
        detail.setOperatorName(boxDetailInfoDto.getOperatorName());
        detail.setOperatorUnitName(boxDetailInfoDto.getOperatorUnitName());
        detail.setOperatorTime(boxDetailInfoDto.getOperatorTime());
        detail.setBoxCode(boxDetailInfoDto.getBoxCode());
        detail.setWaybillCode(boxDetailInfoDto.getWaybillCode());
        detail.setPackageCode(boxDetailInfoDto.getPackageCode());
        return detail;
    }

    /**
     * 获取已称重包裹
     * @param thirdBoxDetails
     * @return
     */
    private List<ThirdBoxDetail> findExistFlowPackages(List<ThirdBoxDetail> thirdBoxDetails){
        List<ThirdBoxDetail> result = new ArrayList<>();

        Set<String> waybillCodes = new HashSet<>();
        Set<String> packageCodes = new HashSet<>();
        //运单对应包裹数，不从运单获取  以传入的关系为准
        Map<String,List<ThirdBoxDetail>> waybillPackMap = new HashMap<>();
        //Map<String,ThirdBoxDetail> packMap = new HashMap<>();

        //组装所需要数据结构
        if(thirdBoxDetails!=null){
            for(ThirdBoxDetail thirdBoxDetail : thirdBoxDetails){
                if(StringUtils.isNotBlank(thirdBoxDetail.getWaybillCode())){
                    waybillCodes.add(thirdBoxDetail.getWaybillCode());
                    if(!waybillPackMap.containsKey(thirdBoxDetail.getWaybillCode())){
                        waybillPackMap.put(thirdBoxDetail.getWaybillCode(),new ArrayList<ThirdBoxDetail>());
                    }
                    waybillPackMap.get(thirdBoxDetail.getWaybillCode()).add(thirdBoxDetail);
                }
                if(StringUtils.isNotBlank(thirdBoxDetail.getPackageCode())){
                    packageCodes.add(thirdBoxDetail.getPackageCode());
                    //packMap.put(thirdBoxDetail.getPackageCode(),thirdBoxDetail);
                }
            }

        }
        List<List<String>> wsplits = ListUtils.partition(new ArrayList<String>(waybillCodes),DEFAULT_BATCH_QUERY_ES);
        //已称重集合
        Set<String> waybillCodesOfWeight = new HashSet<>();

        //获取已称重运单
        for(List<String> wsplit : wsplits){
           Set<String> r = weightVolumeFlowJSFManager.findExistFlow(wsplit,null);
           if(r!=null && !r.isEmpty()){
               waybillCodesOfWeight.addAll(r);
           }
        }

        //组装返回对象
        for(String waybill : waybillCodesOfWeight){
            result.addAll(waybillPackMap.get(waybill));
        }
        return result;
    }

    private boolean lock(Box box){
        try{
            String lockKey = LOCK_BEGIN+box.getCode();
            if(!cacheService.setNx(lockKey,StringUtils.EMPTY,5, TimeUnit.MINUTES)){
                Thread.sleep(1000);
                return cacheService.setNx(lockKey,StringUtils.EMPTY,5, TimeUnit.MINUTES);
            }
        }catch (Exception e){
            logger.error("lock异常:{}",JsonHelper.toJson(box),e);
        }
        return true;
    }

    /**
     * 是否已加载过
     * @param boxCode
     * @return
     */
    private boolean isLoadSaveFlag(String boxCode){
        if(StringUtils.isBlank(boxCode)){
            return true;
        }
        String lockKey = LOAD_SAVE_BEGIN+boxCode;
        try{
            return cacheService.exists(lockKey);
        }catch (Exception e){
            logger.error("检查此箱号是否已加载过箱包关系异常{},{}",boxCode,e.getMessage(),e);
        }
        return true;
    }

    /**
     * 是否已重新均摊过
     * @param waybillCode
     * @param packageCode
     * @return
     */
    private boolean isEqualization(String waybillCode,String packageCode,String siteCode){
        if(StringUtils.isBlank(waybillCode) && StringUtils.isBlank(packageCode)){
            return true;
        }
        String key = String.format(EQUALIZATION_FOMART,waybillCode,packageCode,siteCode);
        try{
            return !cacheService.setNx(key,StringUtils.EMPTY,15, TimeUnit.DAYS);
        }catch (Exception e){
            logger.error("检查是否已重新均摊过异常{},{},{},{}",waybillCode,packageCode,siteCode,e.getMessage(),e);
        }
        return true;
    }

    /**
     * 删除
     * @param waybillCode
     * @param packageCode
     * @param siteCode
     * @return
     */
    private boolean delEqualization(String waybillCode,String packageCode,String siteCode){
        if(StringUtils.isBlank(waybillCode) && StringUtils.isBlank(packageCode)){
            return true;
        }
        String key = String.format(EQUALIZATION_FOMART,waybillCode,packageCode,siteCode);
        try{
            return cacheService.del(key);
        }catch (Exception e){
            logger.error("delEqualization error {},{},{},{}",waybillCode,packageCode,siteCode,e.getMessage(),e);
        }
        return true;
    }


    /**
     * 是否已加载过
     * @param boxCode
     * @return
     */
    private boolean addLoadSaveFlag(String boxCode){
        if(StringUtils.isBlank(boxCode)){
            return false;
        }
        String lockKey = LOAD_SAVE_BEGIN+boxCode;
        try{
            return cacheService.setNx(lockKey,StringUtils.EMPTY,15, TimeUnit.DAYS);
        }catch (Exception e){
            logger.error("经济网加载存储箱包关系获取是否已加载过异常{}",boxCode,e);
            try{
                Thread.sleep(500);
                return cacheService.setNx(lockKey,StringUtils.EMPTY,15, TimeUnit.DAYS);
            }catch (Exception e2){
                logger.error("二次经济网加载存储箱包关系获取是否已加载过异常{}",boxCode,e2);
            }
        }
        return false;
    }

    private void delLoadSaveFlag(String boxCode){
        try{
            String lockKey = LOAD_SAVE_BEGIN+boxCode;
            cacheService.del(lockKey);
        }catch (Exception e){
            logger.error("delLoadSaveFlag 异常:{}",boxCode,e);
        }
    }

    private boolean unLock(Box box){
        try{
            String lockKey = LOCK_BEGIN+box.getCode();
            cacheService.del(lockKey);
        }catch (Exception e){
            logger.error("unLock异常:{}",JsonHelper.toJson(box),e);
        }
        return true;
    }

    /**
     * 此箱号是否在锁定状态
     *
     * @param box
     * @return
     */
    private boolean isLock(Box box) {
        String lockKey = LOCK_BEGIN+box.getCode();
        try{
            return cacheService.exists(lockKey);
        }catch (Exception e){
            logger.error("检查此箱号是否在锁定状态异常{},{}",JsonHelper.toJson(box),e.getMessage(),e);
        }
        return true;
    }


    private void writeLog(String boxCode,String waybillCode,String packageCode,
                          Integer siteCode,String operatorCode,Date operateTime,String remark
                        ,BusinessLogConstans.OperateTypeEnum operateTypeEnum) {
        try {
            long startTime = System.currentTimeMillis();
            JSONObject request = new JSONObject();
            request.put("operatorCode",operatorCode);
            request.put("siteCode", siteCode);
            request.put("operateTime", operateTime);
            request.put("boxCode", boxCode);
            request.put("waybillCode", waybillCode);
            request.put("packageCode", packageCode);

            long endTime = System.currentTimeMillis();

            BusinessLogProfiler logProfiler = new BusinessLogProfilerBuilder()
                    .operateTypeEnum(operateTypeEnum)
                    .processTime(endTime, startTime)
                    .operateRequest(request)
                    .reMark(remark)
                    .methodName("EconomicNetServiceImpl")
                    .build();

            logEngine.addLog(logProfiler);
        }catch (Exception e){
            logger.error("EconomicNetServiceImpl businessLog异常!{}",remark,e);
        }
    }

    private void loadAndSaveBoxPackageDataSuccessLog(Box box,int size){
        try{
            writeLog(box.getCode(),"","",0,"",new Date(),
                    "加载箱包关系成功，获取到"+size+"个包裹",BusinessLogConstans.OperateTypeEnum.ECONOMIC_NET_BOX_LOAD);
        }catch (Exception e){
            logger.error("loadAndSaveBoxPackageDataSuccessLog error",e);
        }

    }

    private void loadAndSaveBoxPackageDataFailLog(Box box,String msg){
        try{
            writeLog(box.getCode(),"","",0,"",new Date(),
                    "加载箱包关系失败"+msg,BusinessLogConstans.OperateTypeEnum.ECONOMIC_NET_BOX_LOAD);
        }catch (Exception e){
            logger.error("loadAndSaveBoxPackageDataFailLog error",e);
        }

    }

    private void equalizationWeightAndVolumeSuccessLog(Box box, WeightVolumeEntity vo,Double l ,Double w , Double h , Double wi ){
        try{
            String msg = vo.getBarCode()+"触发重新分摊称重数据,分摊后长："+l+"宽："+w+"高："+h+"重量："+wi+ " 称重流水消息："+JsonHelper.toJson(vo);
            writeLog(box.getCode(),vo.getWaybillCode(),vo.getPackageCode(),vo.getOperateSiteCode(),vo.getOperatorCode(),vo.getOperateTime(),
                    msg,BusinessLogConstans.OperateTypeEnum.ECONOMIC_NET_BOX_EQUALIZATION);
        }catch (Exception e){
            logger.error("equalizationWeightAndVolumeSuccessLog error",e);
        }
    }


}
