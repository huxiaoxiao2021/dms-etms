package com.jd.bluedragon.distribution.businessIntercept.handler.intercept;

import com.jd.bluedragon.core.base.BaseMinorManager;
import com.jd.bluedragon.core.base.WaybillQueryManager;
import com.jd.bluedragon.core.jmq.producer.DefaultJMQProducer;
import com.jd.bluedragon.distribution.api.Response;
import com.jd.bluedragon.distribution.businessIntercept.dto.SaveInterceptMsgDto;
import com.jd.bluedragon.distribution.businessIntercept.helper.BusinessInterceptConfigHelper;
import com.jd.bluedragon.distribution.command.JdResult;
import com.jd.bluedragon.distribution.print.service.ComposeService;
import com.jd.bluedragon.distribution.waybill.domain.CancelWaybill;
import com.jd.bluedragon.distribution.waybill.service.WaybillCancelService;
import com.jd.bluedragon.dms.utils.BusinessUtil;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.etms.cache.util.EnumBusiCode;
import com.jd.etms.waybill.domain.BaseEntity;
import com.jd.etms.waybill.domain.Waybill;
import com.jd.etms.waybill.domain.WaybillManageDomain;
import com.jd.etms.waybill.dto.BigWaybillDto;
import com.jd.etms.waybill.dto.WChoice;
import com.jd.ql.basic.domain.BaseDmsStore;
import com.jd.ql.basic.domain.CrossPackageTagNew;
import com.jd.ql.basic.util.DateUtil;
import com.jd.ql.dms.common.constants.OperateDeviceTypeConstants;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * 拦截记录处理抽象类
 *
 * @author fanggang7
 * @time 2020-12-23 09:55:25 周三
 */
@Service
public abstract class BusinessInterceptRecordAbstractHandler implements IBusinessInterceptRecordHandler {

    protected final Logger log = LoggerFactory.getLogger(this.getClass());

    @Qualifier("businessOperateInterceptSendProducer")
    @Autowired
    protected DefaultJMQProducer businessOperateInterceptSendProducer;

    @Autowired
    private BusinessInterceptConfigHelper businessInterceptConfigHelper;

    @Autowired
    private WaybillCancelService waybillCancelService;

    @Autowired
    private WaybillQueryManager waybillQueryManager;

    @Autowired
    private BaseMinorManager baseMinorManager;

    /**
     * 处理拦截提交数据
     *
     * @param msgDto 提交数据
     * @return 处理结果
     * @author fanggang7
     * @time 2020-12-23 09:36:46 周三
     */
    @Override
    public Response<Boolean> handle(SaveInterceptMsgDto msgDto) {
        Response<Boolean> result = new Response<>();
        result.toSucceed();
        result.setData(true);

        this.processMsgToStandard(msgDto);
        /* 处理拦截消息任务 */
        doHandle(msgDto);

        /* 处理之后 */

        return result;
    }

    /**
     * 消息数据标准化处理，防止大小写问题
     * @param msgDto 消息对象
     * @author fanggang7
     * @time 2021-08-05 10:47:31 周四
     */
    protected void processMsgToStandard(SaveInterceptMsgDto msgDto){
        try {
            msgDto.setBarCode(StringUtils.upperCase(msgDto.getBarCode()));
            msgDto.setWaybillCode(StringUtils.upperCase(msgDto.getWaybillCode()));
            msgDto.setPackageCode(StringUtils.upperCase(msgDto.getPackageCode()));
        } catch (Exception e) {
            log.info("BusinessInterceptRecordAbstractHandler.processMsgToStandard exception {}", e.getMessage(), e);
        }
    }

    /**
     * 抽象处理类，由不同类型处理器处理
     *
     * @param msgDto 提交数据
     * @return 处理结果
     * @author fanggang7
     * @time 2020-12-23 14:24:59 周三
     */
    protected abstract Response<Boolean> doHandle(SaveInterceptMsgDto msgDto);

    /**
     * 获取应拦截生效时间
     *
     * @param msgDto 消息内容
     * @return 处理结果
     * @author fanggang7
     * @time 2021-01-10 18:56:17 周日
     */
    protected Response<Boolean> getAndSetWaybillInterceptEffectTime(SaveInterceptMsgDto msgDto) {
        Response<Boolean> result = new Response<>();
        result.toSucceed();
        result.setData(true);

        List<String> pdaWaybillCancelInterceptCodeList = Arrays.asList(businessInterceptConfigHelper.getPdaInterceptCodeWaybillCancel().split(","));
        List<String> automaticWaybillCancelInterceptCodeList = Arrays.asList(businessInterceptConfigHelper.getAutomaticInterceptCodeWaybillCancel().split(","));
        // 如果是取消类型的code，就查询一下运单取消记录
        if ((Objects.equals(businessInterceptConfigHelper.getOperateDeviceTypeByConstants(OperateDeviceTypeConstants.PDA), msgDto.getDeviceType()) && pdaWaybillCancelInterceptCodeList.contains(String.valueOf(msgDto.getInterceptCode())))
                || (Objects.equals(businessInterceptConfigHelper.getOperateDeviceTypeByConstants(OperateDeviceTypeConstants.AUTOMATIC), msgDto.getDeviceType()) && automaticWaybillCancelInterceptCodeList.contains(String.valueOf(msgDto.getInterceptCode())))
        ) {
            Response<Boolean> cancelTimeResult = this.getAndSetWaybillCancelTime(msgDto);
            result.setData(cancelTimeResult.getData());
            return result;
        }

        return result;
    }

    /**
     * 获取取消拦截生效时间
     *
     * @param msgDto 消息内容
     * @return 处理结果
     * @author fanggang7
     * @time 2021-01-10 18:55:51 周日
     */
    protected Response<Boolean> getAndSetWaybillCancelTime(SaveInterceptMsgDto msgDto) {
        Response<Boolean> result = new Response<>();
        result.toSucceed();
        result.setData(true);

        try {
            List<CancelWaybill> waybillCancelList = waybillCancelService.getByWaybillCode(msgDto.getWaybillCode());
            if (CollectionUtils.isEmpty(waybillCancelList)) {
                log.warn("getWaybillCancelTime empty data waybillCode: {}", msgDto.getWaybillCode());
                return result;
            }
            CancelWaybill cancelWaybill = waybillCancelList.get(0);
            msgDto.setInterceptEffectTime(DateUtil.parse(cancelWaybill.getOperateTime(), DateUtil.FORMAT_DATE_TIME).getTime());
        } catch (Exception e) {
            log.error("getWaybillCancelTime exception");
            result.toError("获取运单取消时间失败");
            result.setData(false);
        }

        return result;
    }

    /**
     * 获取取消拦截生效时间
     *
     * @param msgDto 消息内容
     * @return 处理结果
     * @author fanggang7
     * @time 2021-01-10 18:55:51 周日
     */
    /*protected Response<Boolean> getAndSetWaybillCreateTime(SaveInterceptMsgDto msgDto) {
        Response<Boolean> result = new Response<>();
        result.toSucceed();
        result.setData(true);

        try {
            WChoice wChoice = new WChoice();
            wChoice.setQueryWaybillC(true);
            BaseEntity<BigWaybillDto> bigWaybillDtoBaseEntity = waybillQueryManager.getDataByChoice(msgDto.getWaybillCode(), wChoice);
            if (EnumBusiCode.BUSI_SUCCESS.getCode() == bigWaybillDtoBaseEntity.getResultCode()) {
                BigWaybillDto bigWaybillDto = bigWaybillDtoBaseEntity.getData();
                Waybill waybill = bigWaybillDto.getWaybill();
                if (waybill != null && waybill.getCreateTime() != null) {
                    msgDto.setInterceptEffectTime(waybill.getCreateTime().getTime());
                }
            }

        } catch (Exception e) {
            log.error("getWaybillCancelTime exception");
            result.toError("获取运单取消时间失败");
            result.setData(false);
        }

        return result;
    }*/

    /**
     * 获取取消拦截生效时间
     *
     * @param msgDto 消息内容
     * @return 处理结果
     * @author fanggang7
     * @time 2021-01-10 18:55:51 周日
     */
    protected Response<Boolean> getCrossCode(SaveInterceptMsgDto msgDto) {
        Response<Boolean> result = new Response<>();
        result.toSucceed();
        result.setData(true);

        try {
            WChoice wChoice = new WChoice();
            wChoice.setQueryWaybillC(true);
            wChoice.setQueryWaybillM(true);
            BaseEntity<BigWaybillDto> bigWaybillDtoBaseEntity = waybillQueryManager.getDataByChoice(msgDto.getWaybillCode(), wChoice);
            BigWaybillDto bigWaybillDto = bigWaybillDtoBaseEntity.getData();
            Waybill waybill = bigWaybillDto.getWaybill();
            WaybillManageDomain waybillManageDomain = bigWaybillDto.getWaybillState();
            if(waybillManageDomain == null){
                log.error("BusinessInterceptRecordProcessServiceImpl.getCrossCodeInfo queryCrossPackageTagForPrint bigWaybillDto.getWaybillState is null bigWaybillDto {}", JsonHelper.toJson(bigWaybillDto));
            }
            com.jd.ql.basic.domain.BaseDmsStore baseDmsStore = new BaseDmsStore();
            if(waybillManageDomain != null){
                baseDmsStore.setStoreId(waybillManageDomain.getStoreId());
                baseDmsStore.setCky2(waybillManageDomain.getCky2());
            }
            baseDmsStore.setDmsId(msgDto.getSiteCode());
            Integer targetSiteId = waybill.getOldSiteId();
            Integer originalCrossType = BusinessUtil.getOriginalCrossType(waybill.getWaybillSign(), waybill.getSendPay());
            JdResult<CrossPackageTagNew> jdResult = baseMinorManager.queryCrossPackageTagForPrint(baseDmsStore, targetSiteId, msgDto.getSiteCode(),originalCrossType);
            if (!jdResult.isSucceed()) {
                log.warn("BusinessInterceptRecordProcessServiceImpl.getCrossCodeInfo queryCrossPackageTagForPrint not success {}", jdResult.getMessage());
                return result;
            }
            final CrossPackageTagNew crossPackageTagNew = jdResult.getData();
            if(crossPackageTagNew == null){
                log.warn("BusinessInterceptRecordProcessServiceImpl.getCrossCodeInfo queryCrossPackageTagForPrint data null {}", jdResult.getMessage());
                return result;
            }
        } catch (Exception e) {
            log.error("getWaybillCancelTime exception");
            result.toError("获取运单取消时间失败");
            result.setData(false);
        }

        return result;
    }
}
