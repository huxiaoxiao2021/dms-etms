package com.jd.bluedragon.distribution.jy.service.collectNew.strategy;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.core.base.WaybillQueryManager;
import com.jd.bluedragon.core.jmq.producer.DefaultJMQProducer;
import com.jd.bluedragon.distribution.api.request.SortingPageRequest;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.busineCode.jqCode.JQCodeService;
import com.jd.bluedragon.distribution.busineCode.jqCode.JQCodeServiceImpl;
import com.jd.bluedragon.distribution.businessCode.dao.BusinessCodeDao;
import com.jd.bluedragon.distribution.collectNew.entity.JyCollectRecordCondition;
import com.jd.bluedragon.distribution.collectNew.entity.JyCollectRecordDetailCondition;
import com.jd.bluedragon.distribution.collectNew.entity.JyCollectRecordDetailPo;
import com.jd.bluedragon.distribution.collectNew.entity.JyCollectRecordPo;
import com.jd.bluedragon.distribution.collectNew.service.JyScanCollectCacheService;
import com.jd.bluedragon.distribution.collectNew.service.JyScanCollectService;
import com.jd.bluedragon.distribution.jy.constants.JyCollectScanCodeTypeEnum;
import com.jd.bluedragon.distribution.jy.dto.collectNew.JyCancelScanCollectMqDto;
import com.jd.bluedragon.distribution.jy.dto.collectNew.JyScanCollectMqDto;
import com.jd.bluedragon.distribution.jy.enums.JyFuncCodeEnum;
import com.jd.bluedragon.distribution.jy.exception.JyBizException;
import com.jd.bluedragon.distribution.jy.service.collectNew.enums.JyCollectionMqBizSourceEnum;
import com.jd.bluedragon.distribution.middleend.sorting.dao.DynamicSortingQueryDao;
import com.jd.bluedragon.distribution.sorting.domain.Sorting;
import com.jd.bluedragon.dms.utils.WaybillUtil;
import com.jd.bluedragon.utils.BusinessHelper;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.etms.waybill.domain.BaseEntity;
import com.jd.etms.waybill.dto.BigWaybillDto;
import com.jd.jmq.common.message.Message;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * @Author zhengchengfa
 * @Date 2023/5/25 21:02
 * @Description
 */
@Service
public class JyScanCollectStrategy {
    private Logger log = LoggerFactory.getLogger(JyWarehouseScanCollectHandler.class);

    public static final Integer CODE_FAIL_REPEAT = 20001;

    @Autowired
    private JyScanCollectService jyScanCollectService;
    @Autowired
    private JQCodeService jqCodeService;
    @Autowired
    private WaybillQueryManager waybillQueryManager;
    @Autowired
    @Qualifier("jyScanCollectProducer")
    private DefaultJMQProducer jyScanCollectProducer;
    @Autowired
    private JyScanCollectCacheService jyScanCollectCacheService;
    @Autowired
    private DynamicSortingQueryDao dynamicSortingQueryDao;

    @Autowired
    private BusinessCodeDao businessCodeDao;

    /**
     * 集齐扫描处理
     * @param collectDto
     * @return
     */
    @JProfiler(jKey = "DMSWORKER.jy.JyScanCollectStrategy.scanCollectDeal",jAppName = Constants.UMP_APP_NAME_DMSWORKER, mState = {JProEnum.TP,JProEnum.FunctionError})
    public boolean scanCollectDeal(JyScanCollectMqDto collectDto) {
        String methodDesc = "JyScanCollectService.scanCollectDeal:拣运扫描处理集齐数据：";
        if(!this.scanFilterInvalid(collectDto)) {
            return true;
        }
//        //必要字段前置加工
//        this.mustFieldFill(collectDto);

        if (JyCollectScanCodeTypeEnum.WAYBILL.getCode().equals(collectDto.getCodeType())) {
            return this.scanWaybillCollectDeal(collectDto);
        } else if (JyCollectScanCodeTypeEnum.PACKAGE.getCode().equals(collectDto.getCodeType())) {
            return this.scanPackageCollectDeal(collectDto);
        } else if (JyCollectScanCodeTypeEnum.BOX.getCode().equals(collectDto.getCodeType())) {
            return this.scanBoxCollectDeal(collectDto);
        } else if (JyCollectScanCodeTypeEnum.BOARD.getCode().equals(collectDto.getCodeType())) {
            return this.scanBoardCollectDeal(collectDto);
        }else {
            log.warn("{}目前仅支持处理按包裹、运单、箱号、板号维度处理集齐，当前类型暂不支持处理，直接丢弃，param={}", methodDesc, JsonHelper.toJson(collectDto));
            return true;
        }
    }

    /**
     * 必填字段填充
     * @param collectDto
     */
    public void mustFieldFill(JyScanCollectMqDto collectDto) {
    }

    /**
     * 过滤无效数据
     * @param collectDto
     * @return true: 有效数据  false: 无效数据
     */
    public boolean scanFilterInvalid(JyScanCollectMqDto collectDto) {
        if(Objects.isNull(collectDto)
                || org.apache.commons.lang3.StringUtils.isBlank(collectDto.getBizSource())    //逻辑分支必选
                || org.apache.commons.lang3.StringUtils.isBlank(collectDto.getCodeType())    //逻辑分支必选
                || org.apache.commons.lang3.StringUtils.isBlank(collectDto.getJyPostType())  //collectionCode必选
                || Objects.isNull(collectDto.getOperateTime())      //业务必选
                || Objects.isNull(collectDto.getOperateSiteId())    //分库拆分键
                || org.apache.commons.lang3.StringUtils.isBlank(collectDto.getBarCode())     //业务必选
        ) {
            log.warn("JyScanCollectStrategy.scanFilterInvalid:集齐消息消费必要参数缺失，不做处理，msg={}", JsonHelper.toJson(collectDto));
            return false;
        }
        return true;
    }

    /**
     * 扫描包裹处理集齐
     * @param collectDto
     * @return
     */
    @JProfiler(jKey = "DMSWORKER.jy.JyScanCollectStrategy.scanPackageCollectDeal",jAppName = Constants.UMP_APP_NAME_DMSWORKER, mState = {JProEnum.TP,JProEnum.FunctionError})
    public boolean scanPackageCollectDeal(JyScanCollectMqDto collectDto) {
        String methodDesc = "JyScanCollectStrategy.scanPackageCollectDeal:建议扫描处理包裹维度集齐逻辑：";
        //
        String packageCode = (JyCollectionMqBizSourceEnum.PRODUCE_NODE_PDA_SCAN.getCode().equals(collectDto.getBizSource()))
                ? collectDto.getBarCode() : collectDto.getPackageCode();
        if(StringUtils.isBlank(packageCode)) {
            log.error("{}拣运扫描包裹维度包裹号为空,mqDto={}", methodDesc, JsonHelper.toJson(collectDto));
            throw new JyBizException("拣运扫描包裹维度包裹号为空");
        }

        if(StringUtils.isBlank(collectDto.getCollectionCode())) {
            //实操扫描没有collectionCode, 消费拆分再拆异步时会前置存入collectionCode
            collectDto.setCollectionCode(this.getCollectionCode(collectDto));
        }

        if (jyScanCollectCacheService.existCacheScanPackageCollectDeal(collectDto.getCollectionCode(), packageCode, collectDto.getOperateTime())) {
            log.warn("{}防重cache拦截，当前包裹已处理过，collectDto={}", methodDesc, JsonHelper.toJson(collectDto));
            return true;
        }
        String waybillCode = WaybillUtil.getWaybillCode(packageCode);


        collectDto.setPackageCode(packageCode);
        collectDto.setWaybillCode(waybillCode);
        //修改集齐运单明细表
        jyScanCollectService.insertCollectionRecordDetail(collectDto);
        //修改集齐运单表
        JyCollectRecordPo collectRecordPo = new JyCollectRecordPo();
        collectRecordPo.setCollectionCode(collectDto.getCollectionCode());
        collectRecordPo.setAggCode(collectDto.getWaybillCode());
        collectRecordPo.setSiteId(collectDto.getOperateSiteId().longValue());
        collectRecordPo.setCustomType(collectDto.getToBWaybill());
        collectRecordPo.setShouldCollectNum(collectDto.getWaybillGoodNumber());
        jyScanCollectService.upInsertCollectionRecord(collectRecordPo, true);

        jyScanCollectCacheService.saveCacheScanPackageCollectDeal(collectDto.getCollectionCode(), packageCode, collectDto.getOperateTime());
        return true;
    }


    /**
     * 扫描运单处理集齐
     * @param collectDto
     * @return
     */
    @JProfiler(jKey = "DMSWORKER.jy.JyScanCollectStrategy.scanWaybillCollectDeal",jAppName = Constants.UMP_APP_NAME_DMSWORKER, mState = {JProEnum.TP,JProEnum.FunctionError})
    public boolean scanWaybillCollectDeal(JyScanCollectMqDto collectDto) {
        String methodDesc = "JyScanCollectStrategy.scanWaybillCollectDeal拣运按单扫描集齐处理:";
        String waybillCode = WaybillUtil.getWaybillCode(collectDto.getBarCode());
        //获取运单包裹信息
        BigWaybillDto bigWaybillDto = this.getWaybillPackage(waybillCode);
        if (bigWaybillDto == null || CollectionUtils.isEmpty(bigWaybillDto.getPackageList())) {
            log.warn("JyScanCollectStrategy.scanWaybillCollectDeal运单多包裹拆分任务, 查询包裹为空, body:[{}]", JsonHelper.toJson(collectDto));
            return false;
        }
        int packageNum =  bigWaybillDto.getPackageList().size();
        //获取场地任务collectionCode
        String collectionCode = this.getCollectionCode(collectDto);
        //消息体加工
        List<Message> messageList = new ArrayList<>();
        bigWaybillDto.getPackageList().forEach(deliveryPackageD -> {
            JyScanCollectMqDto mqDto = new JyScanCollectMqDto();
            BeanUtils.copyProperties(collectDto, mqDto);
            mqDto.setCollectionCode(collectionCode);
            mqDto.setPackageCode(deliveryPackageD.getPackageBarcode());
            mqDto.setWaybillCode(waybillCode);
            mqDto.setBizSource(JyCollectionMqBizSourceEnum.PRODUCE_NODE_MQ_WAYBILL_SPLIT.getCode());
            mqDto.setCodeType(JyCollectScanCodeTypeEnum.PACKAGE.getCode());//实操扫描非包裹维度，拆分后类型改为包裹维度

            //运单号+操作任务+岗位类型+触发节点
            String businessId = this.getScanBusinessId(collectDto);
            String msg = JsonHelper.toJson(mqDto);
            if(log.isInfoEnabled()) {
                log.info("{}运单{}包裹数为{}，拆分包裹businessId={}, msg={}",
                        methodDesc, waybillCode, packageNum, businessId, msg);
            }
            messageList.add(new Message(jyScanCollectProducer.getTopic(), msg, businessId));

        });
        jyScanCollectProducer.batchSendOnFailPersistent(messageList);
        return true;
    }


    /**
     * 扫描箱号处理集齐
     * @param collectDto
     * @return
     */
    @JProfiler(jKey = "DMSWORKER.jy.JyScanCollectStrategy.scanBoxCollectDeal",jAppName = Constants.UMP_APP_NAME_DMSWORKER, mState = {JProEnum.TP,JProEnum.FunctionError})
    public boolean scanBoxCollectDeal(JyScanCollectMqDto collectDto) {
        String methodDesc = "JyScanCollectStrategy.scanBoxCollectDeal:拣运扫描箱号集齐处理:";
        if(!BusinessHelper.isBoxcode(collectDto.getBarCode())) {
            log.error("{}单据非箱号,collectDto={}", methodDesc, JsonHelper.toJson(collectDto));
            throw new JyBizException("拣运扫描箱号集齐处理单据非箱号" + collectDto.getBarCode());
        }

       int pageSize = 1000;
        List<Sorting> sortingList = new ArrayList<>();
        for(int pageNo = 1; pageNo <= 100; pageNo++) {
            if(pageNo > 50) {
                log.error("{}集齐处理查询箱内包裹已超过5W,超过理论上的最大值(实际ucc配置几百)，超出不做处理，参数={}", methodDesc, JsonHelper.toJson(collectDto));
                break;
            }
            SortingPageRequest boxQuery = new SortingPageRequest();
            boxQuery.setBoxCode(collectDto.getBarCode());
            boxQuery.setCreateSiteCode(collectDto.getOperateSiteId());
            boxQuery.setLimit(pageSize);
            boxQuery.setOffset(pageSize * (pageNo - 1));
            List<Sorting> sortList = dynamicSortingQueryDao.getPagePackageNoByBoxCode(boxQuery);
            sortingList.addAll(sortList);
            if(CollectionUtils.isEmpty(sortingList) || sortList.size() < pageSize) {
                break;
            }
        }
        //获取场地任务collectionCode
        String collectionCode = this.getCollectionCode(collectDto);
        //消息体加工
        List<Message> messageList = new ArrayList<>();
        sortingList.forEach(sorting -> {
            JyScanCollectMqDto mqDto = new JyScanCollectMqDto();
            BeanUtils.copyProperties(collectDto, mqDto);
            mqDto.setCollectionCode(collectionCode);
            mqDto.setPackageCode(sorting.getPackageCode());
            mqDto.setWaybillCode(WaybillUtil.getWaybillCode(mqDto.getPackageCode()));
            mqDto.setBizSource(JyCollectionMqBizSourceEnum.PRODUCE_NODE_MQ_BOX_SPLIT.getCode());
            mqDto.setCodeType(JyCollectScanCodeTypeEnum.PACKAGE.getCode());//实操扫描非包裹维度，拆分后类型改为包裹维度

            //运单号+操作任务+岗位类型+触发节点
            String businessId = this.getScanBusinessId(collectDto);
            String msg = JsonHelper.toJson(mqDto);
            if(log.isInfoEnabled()) {
                log.info("{}箱号{}包裹数为{}，拆分包裹businessId={}, msg={}",
                        methodDesc, collectDto.getBarCode(), sortingList.size(), businessId, msg);
            }
            messageList.add(new Message(jyScanCollectProducer.getTopic(), msg, businessId));

        });
        jyScanCollectProducer.batchSendOnFailPersistent(messageList);
        return true;
    }


    public String getScanBusinessId(JyScanCollectMqDto mqDto) {
        //PDA扫描时可能按单按箱，消费时最终会转成包裹维度，转换之后存储packageCode
        String code =  StringUtils.isBlank(mqDto.getPackageCode()) ? mqDto.getBarCode() : mqDto.getPackageCode();
         return String.format("%s:%s:%s:%s", code,
                mqDto.getJyPostType(),
                mqDto.getSendCode(),
                mqDto.getBizSource());
    }

    public String getCancelScanBusinessId(JyCancelScanCollectMqDto mqDto) {
        return String.format("%s:%s:%s", mqDto.getBarCode(), mqDto.getJyPostType(), mqDto.getOperateSiteId());
    }

    /**
     * 扫描板号处理集齐
     * @param collectDto
     * @return
     */
    @JProfiler(jKey = "DMSWORKER.jy.JyScanCollectStrategy.scanBoardCollectDeal",jAppName = Constants.UMP_APP_NAME_DMSWORKER, mState = {JProEnum.TP,JProEnum.FunctionError})
    public boolean scanBoardCollectDeal(JyScanCollectMqDto collectDto) {
        return true;
    }

    /**
     * 获取场地任务collectionCode
     * @param collectDto
     * @return
     */
    @JProfiler(jKey = "DMSWORKER.jy.JyScanCollectStrategy.getCollectionCode",jAppName = Constants.UMP_APP_NAME_DMSWORKER, mState = {JProEnum.TP,JProEnum.FunctionError})
    public String getCollectionCode(JyScanCollectMqDto collectDto) {
        if(StringUtils.isBlank(collectDto.getJyPostType())
                || StringUtils.isBlank(collectDto.getSendCode())) {
            log.error("JyScanCollectStrategy.getCollectionCode,获取collectionCode参数缺失,param={}", JsonHelper.toJson(collectDto));
            throw new JyBizException("获取collectionCode参数缺失");
        }
        String collectionCode = jqCodeService.getOrGenJyScanTaskSendCodeCollectionCode(
                JyFuncCodeEnum.getJyPostEnumByCode(collectDto.getJyPostType()), collectDto.getSendCode(), null);
        if(StringUtils.isBlank(collectionCode)) {
            log.error("JyScanCollectStrategy.getCollectionCode获取为空，param={}", JsonHelper.toJson(collectDto));
            throw new JyBizException("获取collectionCode为空");
        }
        return collectionCode;
    }


    /**
     * '获取运单下包裹信息
     * @param waybillCode
     * @return
     */
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

    /**
     * 取消扫描处理集齐逻辑
     * @param cancelScanCollectMqDto
     * @return
     */
    public boolean cancelScanCollectDeal(JyCancelScanCollectMqDto cancelScanCollectMqDto) {
        String methodDesc = "JyScanCollectStrategy.cancelScanCollectDea:取消扫描处理集齐数据：";
        if(!this.cancelScanFilterInvalid(cancelScanCollectMqDto)) {
            return true;
        }
        if (JyCollectScanCodeTypeEnum.WAYBILL.getCode().equals(cancelScanCollectMqDto.getBarCodeType())) {
            return this.cancelScanWaybillCollectDeal(cancelScanCollectMqDto);
        } else if (JyCollectScanCodeTypeEnum.PACKAGE.getCode().equals(cancelScanCollectMqDto.getBarCodeType())) {
            return this.cancelScanPackageCollectDeal(cancelScanCollectMqDto);
        } else {
            log.warn("{}目前仅支持按包裹、运单取消发货处理集齐数据，当前类型暂不支持处理，直接丢弃，param={}", methodDesc, JsonHelper.toJson(cancelScanCollectMqDto));
            return true;
        }
    }

    /**
     * 过滤无效数据
     * @param cancelScanCollectMqDto
     * @return true: 有效数据  false: 无效数据
     */
    public boolean cancelScanFilterInvalid(JyCancelScanCollectMqDto cancelScanCollectMqDto) {
        if(Objects.isNull(cancelScanCollectMqDto)
                || StringUtils.isBlank(cancelScanCollectMqDto.getJyPostType())  //collectionCode必选
                || Objects.isNull(cancelScanCollectMqDto.getOperateTime())      //业务必选
                || Objects.isNull(cancelScanCollectMqDto.getOperateSiteId())    //分库拆分键
                || StringUtils.isBlank(cancelScanCollectMqDto.getBarCode())      //业务必选
                || StringUtils.isBlank(cancelScanCollectMqDto.getJyPostType())   //业务必选
        ) {
            log.warn("JyScanCollectStrategy.cancelScanFilterInvalid:集齐消息消费必要参数缺失，不做处理，msg={}", JsonHelper.toJson(cancelScanCollectMqDto));
            return false;
        }
        return true;
    }

    /**
     * 按包裹处理取消扫描
     * @param cancelScanCollectMqDto
     * @return
     */
    private boolean cancelScanPackageCollectDeal(JyCancelScanCollectMqDto cancelScanCollectMqDto) {
        String methodDesc = "JyScanCollectStrategy.cancelScanPackageCollectDeal包裹取消发货扫描处理集齐数据：";

        InvokeResult<List<String>> invokeResult = this.delCollectDetailAndReturnDelCollectionCodes(cancelScanCollectMqDto);
        List<String> collectionCodeList = new ArrayList<>();
        if(invokeResult.codeSuccess()) {
            //删除成功获取删除的collectionCode
            collectionCodeList = invokeResult.getData();
        }else if(JyScanCollectStrategy.CODE_FAIL_REPEAT.equals(invokeResult.getCode())) {
            //上面逻辑重复拦截时，下面逻辑正常执行，可能重试,
            //正常逻辑是删除明细，对删除的collectionCode
             collectionCodeList = this.findPageCollectByCondition(cancelScanCollectMqDto);
             if(log.isInfoEnabled()) {
                 log.info("{}按包裹取消重复时，对当前岗位所有collectionCode进行处理不齐，req={}.collectionCodeList={}",
                         methodDesc, JsonHelper.toJson(cancelScanCollectMqDto), JsonHelper.toJson(collectionCodeList));
             }

        }
        if(CollectionUtils.isEmpty(collectionCodeList)) {
            return true;
        }
        //修改运单统计数据
        collectionCodeList.forEach( collectionCode -> {
            JyCollectRecordPo collectRecordPo = new JyCollectRecordPo();
            collectRecordPo.setCollectionCode(collectionCode);
            collectRecordPo.setAggCode(WaybillUtil.getWaybillCode(cancelScanCollectMqDto.getBarCode()));
            collectRecordPo.setSiteId(cancelScanCollectMqDto.getOperateSiteId().longValue());
            jyScanCollectService.upInsertCollectionRecord(collectRecordPo, false);
        });

        return true;
    }


    /**
     * 取消扫描删除逻辑
     * 注： 扫描是先确定collectionCode,对指定collectionCode添加
     *     删除时，不指定collectionCode, 按场地查询，但是要对查询结果中的collectionCode校验是否当前岗位，避免同场地不同岗位数据影响，如发货岗取消了卸车岗数据
     * @param cancelScanCollectMqDto
     * @return
     */
    private InvokeResult<List<String>> delCollectDetailAndReturnDelCollectionCodes(JyCancelScanCollectMqDto cancelScanCollectMqDto) {
        String methodDesc = "JyScanCollectStrategy.delCollectDetailAndReturnDelCollectionCodes删除集齐明细返回删除数据的：";
        InvokeResult<List<String>> res = new InvokeResult<>();
        res.success();
        //防重复（区分真的查不到和重复操作同一批数据删除之后再查查不到， 删除后面统计修改获取不到锁会重试，需要保证删除后可以再次出发后面逻辑）
        if(jyScanCollectCacheService.existCacheCancelScanPackageInSiteAndScanCode(cancelScanCollectMqDto.getOperateSiteId(), cancelScanCollectMqDto.getBarCode(), cancelScanCollectMqDto.getOperateTime())) {
            if(log.isInfoEnabled()) {
                log.info("{}重复了，参数={}", methodDesc, JsonHelper.toJson(cancelScanCollectMqDto));
            }
            res.setCode(JyScanCollectStrategy.CODE_FAIL_REPEAT);
            return res;
        }
        //查询场地所有符合条件的collectionCode扫描信息
        List<JyCollectRecordDetailPo> dPoRes = this.findPageCollectDetailByCondition(cancelScanCollectMqDto);
        List<String> collectionCodeList = new ArrayList<>();
//        collectionCodeList = dPoRes.stream().map(JyCollectRecordDetailPo::getCollectionCode).collect(Collectors.toList());
        //仅过滤当前岗位数据
        if(CollectionUtils.isNotEmpty(dPoRes)) {
            dPoRes.forEach(collectDetailPo -> {
                List<String> ccListTemp = businessCodeDao.findAttributeValueByCodeAndPossibleKey(collectDetailPo.getCollectionCode(), JQCodeServiceImpl.ATTRIBUTE_JY_POST);
            if(CollectionUtils.isNotEmpty(ccListTemp) && cancelScanCollectMqDto.getJyPostType().equals(ccListTemp.get(0))) {
                collectionCodeList.add(collectDetailPo.getCollectionCode());
            }
            });
        }
        if(CollectionUtils.isEmpty(collectionCodeList)) {
            if(log.isWarnEnabled()) {
                log.warn("{}未查到相关collectionCodes不做处理，方法req={}, 查包裹返回={}", methodDesc, JsonHelper.toJson(cancelScanCollectMqDto), JsonHelper.toJson(dPoRes));
            }
            res.setMessage("不存在相关包裹");
            return res;
        }

        res.setData(collectionCodeList);
        if(log.isInfoEnabled()) {
            log.info("{}该场地扫描该包裹collectionCodes为{}，参数={}", methodDesc, JsonHelper.toJson(collectionCodeList), JsonHelper.toJson(cancelScanCollectMqDto));
        }
        //根据已知collectionCodeList 进行包裹删除
        this.deleteCollectionRecordDetail(cancelScanCollectMqDto, collectionCodeList);
        jyScanCollectCacheService.saveCacheCancelScanPackageInSiteAndScanCode(cancelScanCollectMqDto.getOperateSiteId(), cancelScanCollectMqDto.getBarCode(), cancelScanCollectMqDto.getOperateTime());
        return res;
    }

    private List<JyCollectRecordDetailPo> findPageCollectDetailByCondition(JyCancelScanCollectMqDto cancelScanCollectMqDto) {
        JyCollectRecordDetailCondition dPo = new JyCollectRecordDetailCondition();
        dPo.setScanCode(cancelScanCollectMqDto.getBarCode());
        dPo.setAggCode(WaybillUtil.getWaybillCode(cancelScanCollectMqDto.getBarCode()));
        dPo.setSiteId(cancelScanCollectMqDto.getOperateSiteId().longValue());
        dPo.setEndOperateTime(new Date(cancelScanCollectMqDto.getOperateTime()));
        return jyScanCollectService.findPageCollectDetailByCondition(dPo);
    }

    private void deleteCollectionRecordDetail(JyCancelScanCollectMqDto cancelScanCollectMqDto, List<String> collectionCodeList) {
        JyCollectRecordDetailCondition dPo = new JyCollectRecordDetailCondition();
        dPo.setScanCode(cancelScanCollectMqDto.getBarCode());
        dPo.setAggCode(WaybillUtil.getWaybillCode(cancelScanCollectMqDto.getBarCode()));
        dPo.setSiteId(cancelScanCollectMqDto.getOperateSiteId().longValue());
        dPo.setEndOperateTime(new Date(cancelScanCollectMqDto.getOperateTime()));
        if(CollectionUtils.isNotEmpty(collectionCodeList)) {
            dPo.setCollectionCodeList(collectionCodeList);
        }
        jyScanCollectService.deleteByScanCode(dPo);
    }


    /**
     * 根据aggCode 查询集齐主表中的collectionCode
     * 注： 查询结果过滤是否当前岗位，避免同场地不同岗位数据互相影响
     * @param cancelScanCollectMqDto
     * @return
     */
    private List<String> findPageCollectByCondition(JyCancelScanCollectMqDto cancelScanCollectMqDto) {
        JyCollectRecordCondition dPo = new JyCollectRecordCondition();
        dPo.setAggCode(WaybillUtil.getWaybillCode(cancelScanCollectMqDto.getBarCode()));
        dPo.setSiteId(cancelScanCollectMqDto.getOperateSiteId().longValue());
        List<JyCollectRecordPo> collectRecordPoList = jyScanCollectService.findPageCollectByCondition(dPo);
        List<String> res = new ArrayList<>();
        //当前岗位校验
        if(CollectionUtils.isNotEmpty(collectRecordPoList)) {
            collectRecordPoList.forEach(collectPo -> {
                List<String> ccListTemp = businessCodeDao.findAttributeValueByCodeAndPossibleKey(collectPo.getCollectionCode(), JQCodeServiceImpl.ATTRIBUTE_JY_POST);
                if(CollectionUtils.isNotEmpty(ccListTemp) && cancelScanCollectMqDto.getJyPostType().equals(ccListTemp.get(0))) {
                    res.add(collectPo.getCollectionCode());
                }
            });
        }
        if(log.isInfoEnabled()) {
            log.info("参数={}，根据场地运单查询当前岗位扫描该单的collectionCodes为{},全场扫描collect信息为{}", JsonHelper.toJson(cancelScanCollectMqDto), JsonHelper.toJson(res), JsonHelper.toJson(collectRecordPoList));
        }
        return res;
    }

    /**
     * 按运单处理取消扫描(*幂等)
     * @param cancelScanCollectMqDto
     * @return
     */
    private boolean cancelScanWaybillCollectDeal(JyCancelScanCollectMqDto cancelScanCollectMqDto) {
        String methodDesc = "JyScanCollectStrategy.cancelScanWaybillCollectDeal:按单取消扫描处理集齐数据";
        //查询集齐主表中当前岗位所有所有扫描该单的collectionCode
        List<String> collectionCodeList = this.findPageCollectByCondition(cancelScanCollectMqDto);
        if(CollectionUtils.isEmpty(collectionCodeList)) {
            if(log.isInfoEnabled()) {
                log.info("{}未查到当前当前岗位下操作该运单的collectionCode，不做处理，参数={}", methodDesc, JsonHelper.toJson(cancelScanCollectMqDto));
            }
            return true;
        }
        if(log.isInfoEnabled()) {
            log.info("{}查到操作该单的collectionCodes为{},参数={}", methodDesc, JsonHelper.toJson(collectionCodeList), JsonHelper.toJson(cancelScanCollectMqDto));
        }
        collectionCodeList.forEach(collectionCode -> {
            JyCollectRecordDetailCondition dPo = new JyCollectRecordDetailCondition();
            dPo.setSiteId(cancelScanCollectMqDto.getOperateSiteId().longValue());
            dPo.setCollectionCode(collectionCode);
            dPo.setAggCode(WaybillUtil.getWaybillCode(cancelScanCollectMqDto.getBarCode()));
            dPo.setEndOperateTime(new Date(cancelScanCollectMqDto.getOperateTime()));
            jyScanCollectService.deleteByAggCode(dPo);
        });

        //修改运单统计数据
        collectionCodeList.forEach( collectionCode -> {
            JyCollectRecordPo collectRecordPo = new JyCollectRecordPo();
            collectRecordPo.setCollectionCode(collectionCode);
            collectRecordPo.setAggCode(cancelScanCollectMqDto.getBarCode());
            collectRecordPo.setSiteId(cancelScanCollectMqDto.getOperateSiteId().longValue());
            jyScanCollectService.upInsertCollectionRecord(collectRecordPo, false);
        });
        return true;
    }
}
