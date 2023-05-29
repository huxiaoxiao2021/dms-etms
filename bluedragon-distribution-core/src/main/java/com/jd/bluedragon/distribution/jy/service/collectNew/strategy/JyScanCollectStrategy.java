package com.jd.bluedragon.distribution.jy.service.collectNew.strategy;

import com.jd.bluedragon.core.base.WaybillQueryManager;
import com.jd.bluedragon.core.jmq.producer.DefaultJMQProducer;
import com.jd.bluedragon.distribution.busineCode.jqCode.JQCodeService;
import com.jd.bluedragon.distribution.collection.service.JyScanCollectService;
import com.jd.bluedragon.distribution.jy.constants.JyCollectionMqBizSourceEnum;
import com.jd.bluedragon.distribution.jy.constants.JyPostEnum;
import com.jd.bluedragon.distribution.jy.constants.JyScanCodeTypeEnum;
import com.jd.bluedragon.distribution.jy.dto.collectNew.JyScanCollectMqDto;
import com.jd.bluedragon.distribution.jy.exception.JyBizException;
import com.jd.bluedragon.dms.utils.WaybillUtil;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.etms.waybill.domain.BaseEntity;
import com.jd.etms.waybill.dto.BigWaybillDto;
import com.jd.jmq.common.message.Message;
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


    /**
     * 集齐扫描处理
     * @param collectDto
     * @return
     */
    public boolean scanCollectDeal(JyScanCollectMqDto collectDto) {
        String methodDesc = "JyScanCollectService.scanCollectDeal:拣运扫描处理集齐数据：";
        if (JyScanCodeTypeEnum.WAYBILL.getCode().equals(collectDto.getBarCodeType())) {
            return this.scanWaybillCollectDeal(collectDto);
        } else if (JyScanCodeTypeEnum.PACKAGE.getCode().equals(collectDto.getBarCodeType())) {
            return this.scanPackageCollectDeal(collectDto);
        } else if (JyScanCodeTypeEnum.BOX.getCode().equals(collectDto.getBarCodeType())) {
            return this.scanBoxCollectDeal(collectDto);
        } else if (JyScanCodeTypeEnum.BOARD.getCode().equals(collectDto.getBarCodeType())) {
            return this.scanBoardCollectDeal(collectDto);
        }else {
            log.warn("{}目前仅支持处理按包裹、运单、箱号、板号维度处理集齐，当前类型暂不支持处理，直接丢弃，param={}", methodDesc, JsonHelper.toJson(collectDto));
            return true;
        }
    }

    /**
     * 扫描包裹处理集齐
     * @param collectDto
     * @return
     */
    public boolean scanPackageCollectDeal(JyScanCollectMqDto collectDto) {
        String waybillCode = WaybillUtil.getWaybillCode(collectDto.getBarCode());
        if(StringUtils.isBlank(collectDto.getCollectionCode())) {
            //实操扫描没有collectionCode, 消费拆分再拆异步时会前置存入collectionCode
            collectDto.setCollectionCode(this.getCollectionCode(collectDto));
        }
        collectDto.setPackageCode(collectDto.getBarCode());
        collectDto.setWaybillCode(waybillCode);
        //修改集齐运单明细表
        jyScanCollectService.insertCollectionRecordDetailInBizId(collectDto);
        //修改集齐运单表
        jyScanCollectService.upInsertCollectionRecordInBizId(collectDto);
        return true;
    }


    /**
     * 扫描运单处理集齐
     * @param collectDto
     * @return
     */
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
            mqDto.setBarCode(deliveryPackageD.getPackageBarcode());
            mqDto.setBizSource(JyCollectionMqBizSourceEnum.PRODUCE_NODE_MQ_WAYBILL_SPLIT.getCode());
            mqDto.setWaybillCode(waybillCode);
            mqDto.setCollectionCode(collectionCode);

            //运单号+操作任务+岗位类型+触发节点
            String businessId = String.format("%s:%s:%s:%s", mqDto.getBarCode(),
                    mqDto.getJyPostType(),
                    mqDto.getMainTaskBizId(),
                    mqDto.getBizSource());
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
    public boolean scanBoxCollectDeal(JyScanCollectMqDto collectDto) {
        return true;
    }

    /**
     * 扫描板号处理集齐
     * @param collectDto
     * @return
     */
    public boolean scanBoardCollectDeal(JyScanCollectMqDto collectDto) {
        return true;
    }

    /**
     * 获取场地任务collectionCode
     * @param collectDto
     * @return
     */
    public String getCollectionCode(JyScanCollectMqDto collectDto) {
        if(StringUtils.isBlank(collectDto.getJyPostType())
                || Objects.isNull(collectDto.getOperateSiteId())
                || StringUtils.isBlank(collectDto.getMainTaskBizId())) {
            log.error("JyScanCollectStrategy.getCollectionCode,获取collectionCode参数缺失,param={}", JsonHelper.toJson(collectDto));
            throw new JyBizException("获取collectionCode参数缺失");
        }
        String collectionCode = jqCodeService.getOrGenJyScanTaskCollectionCode(
                JyPostEnum.getJyPostEnumByCode(collectDto.getJyPostType()),
                collectDto.getOperateSiteId(),
                collectDto.getMainTaskBizId(),
                null);
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
