package com.jd.bluedragon.distribution.jy.service.collectNew.strategy;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.core.base.WaybillQueryManager;
import com.jd.bluedragon.core.jmq.producer.DefaultJMQProducer;
import com.jd.bluedragon.distribution.api.request.SortingPageRequest;
import com.jd.bluedragon.distribution.busineCode.jqCode.JQCodeService;
import com.jd.bluedragon.distribution.collection.service.JyScanCollectCacheService;
import com.jd.bluedragon.distribution.collectNew.service.JyScanCollectService;
import com.jd.bluedragon.distribution.jy.service.collectNew.enums.JyCollectionMqBizSourceEnum;
import com.jd.bluedragon.distribution.jy.constants.JyPostEnum;
import com.jd.bluedragon.distribution.jy.constants.JyScanCodeTypeEnum;
import com.jd.bluedragon.distribution.jy.dto.collectNew.JyScanCollectMqDto;
import com.jd.bluedragon.distribution.jy.exception.JyBizException;
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

    @Autowired
    private JyScanCollectService jyScanCollectService;
    @Autowired
    private JQCodeService jqCodeService;
    @Autowired
    WaybillQueryManager waybillQueryManager;
    @Qualifier("jyScanCollectProducer")
    private DefaultJMQProducer jyScanCollectProducer;
    @Autowired
    private JyScanCollectCacheService jyScanCollectCacheService;
    @Autowired
    private DynamicSortingQueryDao dynamicSortingQueryDao;


    /**
     * 集齐扫描处理
     * @param collectDto
     * @return
     */
    @JProfiler(jKey = "DMSWORKER.jy.JyScanCollectStrategy.scanCollectDeal",jAppName = Constants.UMP_APP_NAME_DMSWORKER, mState = {JProEnum.TP,JProEnum.FunctionError})
    public boolean scanCollectDeal(JyScanCollectMqDto collectDto) {
        String methodDesc = "JyScanCollectService.scanCollectDeal:拣运扫描处理集齐数据：";
        if(!this.filterInvalid(collectDto)) {
            return true;
        }
        if (JyScanCodeTypeEnum.WAYBILL.getCode().equals(collectDto.getCodeType())) {
            return this.scanWaybillCollectDeal(collectDto);
        } else if (JyScanCodeTypeEnum.PACKAGE.getCode().equals(collectDto.getCodeType())) {
            return this.scanPackageCollectDeal(collectDto);
        } else if (JyScanCodeTypeEnum.BOX.getCode().equals(collectDto.getCodeType())) {
            return this.scanBoxCollectDeal(collectDto);
        } else if (JyScanCodeTypeEnum.BOARD.getCode().equals(collectDto.getCodeType())) {
            return this.scanBoardCollectDeal(collectDto);
        }else {
            log.warn("{}目前仅支持处理按包裹、运单、箱号、板号维度处理集齐，当前类型暂不支持处理，直接丢弃，param={}", methodDesc, JsonHelper.toJson(collectDto));
            return true;
        }
    }

    /**
     * 过滤无效数据
     * @param collectDto
     * @return true: 有效数据  false: 无效数据
     */
    public boolean filterInvalid(JyScanCollectMqDto collectDto) {
        if(Objects.isNull(collectDto)
                || org.apache.commons.lang3.StringUtils.isBlank(collectDto.getBizSource())    //逻辑分支必选
                || org.apache.commons.lang3.StringUtils.isBlank(collectDto.getCodeType())    //逻辑分支必选
                || org.apache.commons.lang3.StringUtils.isBlank(collectDto.getJyPostType())  //collectionCode必选
                || Objects.isNull(collectDto.getOperateTime())      //业务必选
                || Objects.isNull(collectDto.getOperateSiteId())    //分库拆分键
                || org.apache.commons.lang3.StringUtils.isBlank(collectDto.getBarCode())     //业务必选
        ) {
            log.warn("JyScanCollectStrategy.filterInvalid:集齐消息消费必要参数缺失，不做处理，msg={}", JsonHelper.toJson(collectDto));
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
        if (jyScanCollectCacheService.existCacheScanPackageCollectDeal(collectDto.getCollectionCode(), packageCode)) {
            log.warn("{}防重cache拦截，当前包裹已处理过，collectDto={}", methodDesc, JsonHelper.toJson(collectDto));
            return true;
        }
        String waybillCode = WaybillUtil.getWaybillCode(packageCode);

        if(StringUtils.isBlank(collectDto.getCollectionCode())) {
            //实操扫描没有collectionCode, 消费拆分再拆异步时会前置存入collectionCode
            collectDto.setCollectionCode(this.getCollectionCode(collectDto));
        }
        collectDto.setPackageCode(packageCode);
        collectDto.setWaybillCode(waybillCode);
        //修改集齐运单明细表
        jyScanCollectService.insertCollectionRecordDetailInBizId(collectDto);
        //修改集齐运单表
        jyScanCollectService.upInsertCollectionRecordInBizId(collectDto);

        jyScanCollectCacheService.saveCacheScanPackageCollectDeal(collectDto.getCollectionCode(), packageCode);
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
            mqDto.setCodeType(JyScanCodeTypeEnum.PACKAGE.getCode());//实操扫描非包裹维度，拆分后类型改为包裹维度

            //运单号+操作任务+岗位类型+触发节点
            String businessId = this.getBusinessId(collectDto);
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
        for(int pageNo = 1; pageNo <= 11; pageNo++) {
            if(pageNo > 10) {
                log.error("{}集齐处理查询箱内包裹已超过1W,超过理论上的最大值(实际ucc配置几百)，视为异常，研发处理，参数={}", methodDesc, JsonHelper.toJson(collectDto));
                throw new JyBizException("拣运扫描箱号处理集齐数据异常" + collectDto.getBarCode());
            }
            SortingPageRequest boxQuery = new SortingPageRequest();
            boxQuery.setBoxCode(collectDto.getBarCode());
            boxQuery.setCreateSiteCode(collectDto.getOperateSiteId());
            boxQuery.setLimit(pageSize);
            boxQuery.setOffset(pageSize * (pageNo - 1));
            List<Sorting> sortList = dynamicSortingQueryDao.getPagePackageNoByBoxCode(boxQuery);
            sortingList.addAll(sortList);
            if(CollectionUtils.isEmpty(sortingList) || sortingList.size() < pageSize) {
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
            mqDto.setCodeType(JyScanCodeTypeEnum.PACKAGE.getCode());//实操扫描非包裹维度，拆分后类型改为包裹维度

            //运单号+操作任务+岗位类型+触发节点
            String businessId = this.getBusinessId(collectDto);
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


    public String getBusinessId(JyScanCollectMqDto mqDto) {
         return String.format("%s:%s:%s:%s", mqDto.getPackageCode(),
                mqDto.getJyPostType(),
                mqDto.getMainTaskBizId(),
                mqDto.getBizSource());
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
                JyPostEnum.getJyPostEnumByCode(collectDto.getJyPostType()), collectDto.getSendCode(), null);
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

}
