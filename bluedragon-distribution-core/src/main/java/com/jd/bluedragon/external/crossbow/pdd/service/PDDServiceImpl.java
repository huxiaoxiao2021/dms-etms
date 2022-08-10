package com.jd.bluedragon.external.crossbow.pdd.service;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.core.base.WaybillQueryManager;
import com.jd.bluedragon.dms.utils.WaybillUtil;
import com.jd.bluedragon.external.crossbow.pdd.domain.PDDResponse;
import com.jd.bluedragon.external.crossbow.pdd.domain.PDDWaybillDetailDto;
import com.jd.bluedragon.external.crossbow.pdd.domain.PDDWaybillQueryDto;
import com.jd.bluedragon.external.crossbow.pdd.manager.PDDBusinessManager;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.bluedragon.utils.StringHelper;
import com.jd.etms.waybill.domain.Waybill;
import com.jd.etms.waybill.dto.BigWaybillDto;
import com.jd.etms.waybill.dto.WChoice;
import com.jd.ql.dms.common.cache.CacheService;
import com.jd.ql.dms.common.domain.JdResponse;
import com.jd.ump.profiler.CallerInfo;
import com.jd.ump.profiler.proxy.Profiler;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;
import java.util.concurrent.TimeUnit;

/**
 * <p>
 *     拼多多电子面单处理类接口
 *
 * @author wuzuxiang
 * @since 2019/10/15
 **/
@Service
public class PDDServiceImpl implements PDDService {

    private static final Logger log = LoggerFactory.getLogger(PDDServiceImpl.class);

    private static final long EXPIRED_TIME = 30;//30天

    private static final String redis_key = "dms.core.PDD.waybill.{0}";

    @Autowired
    @Qualifier("pddWaybillQueryManager")
    private PDDBusinessManager pddWaybillQueryManager;

    @Autowired
    @Qualifier("jimdbCacheService")
    private CacheService jimdbCacheService;

    @Autowired
    private WaybillQueryManager waybillQueryManager;

    @Override
    public PDDResponse<PDDWaybillDetailDto> queryPDDWaybillInfoByWaybillCodeWithCacheAndSource(String waybillCode, String source, Boolean cacheSwitch, Boolean waybillSwitch) {
        PDDResponse<PDDWaybillDetailDto> response = new PDDResponse<>();
        response.setSuccess(Boolean.FALSE);
        response.setErrorCode(String.valueOf(JdResponse.CODE_FAIL));
        response.setErrorMsg(JdResponse.MESSAGE_ERROR);

        if (!WaybillUtil.isPDDWaybillCode(waybillCode)) {
            return response;
        }

        CallerInfo callerInfo = Profiler.registerInfo("DMS.WEB.PDDService.queryPDDWaybillInfoByWaybillCodeWithCacheAndSource_all_" + source, Constants.UMP_APP_NAME_DMSWEB, false,false);
        // 1. 检查缓存信息，是否包含pdd的缓存信息，如果包含，则从缓存中获取pdd信息
        if (Boolean.TRUE.equals(cacheSwitch) && this.queryPDDWaybillInCache(waybillCode, response)) {
            return response;
        }

        // 2. 检查运单接口，是否包含运单的明文信息，如果包含，则从运单的信息中获取
        if (Boolean.TRUE.equals(waybillSwitch) && this.queryPDDWaybillInWaybill(waybillCode, response)) {
            return response;
        }

        CallerInfo callerInfo1 = Profiler.registerInfo("DMS.WEB.PDDService.queryPDDWaybillInfoByWaybillCodeWithCacheAndSource_to_pdd_all_" + source, Constants.UMP_APP_NAME_DMSWEB, false,false);
        CallerInfo callerInfo2 = null;
        // 3. 调用pdd的外部接口，获取返回值信息
        if (this.queryPDDWaybillInPDD(waybillCode, response)) {
            callerInfo2 = Profiler.registerInfo("DMS.WEB.PDDService.queryPDDWaybillInfoByWaybillCodeWithCacheAndSource_to_pdd_success_" + source, Constants.UMP_APP_NAME_DMSWEB, false,false);
        }
        Profiler.registerInfoEnd(callerInfo);
        Profiler.registerInfoEnd(callerInfo1);
        Profiler.registerInfoEnd(callerInfo2);
        return response;
    }

    private Boolean queryPDDWaybillInCache(String waybillCode,PDDResponse<PDDWaybillDetailDto> response) {

        String redisKey = MessageFormat.format(redis_key,waybillCode);

        try {
            String redisValue = jimdbCacheService.get(redisKey);
            if (StringHelper.isNotEmpty(redisValue)) {
                /* 如果從redis中可以獲取到value值，則直接反序列化，不走pdd接口 */
                log.info("拼多多订单redis命中缓存，waybillCode:{}，返回值：{}", waybillCode, redisValue);
                response.setSuccess(Boolean.TRUE);
                response.setErrorCode(String.valueOf(JdResponse.CODE_SUCCESS));
                response.setErrorMsg(JdResponse.MESSAGE_SUCCESS);
                response.setResult(JsonHelper.fromJsonUseGson(redisValue, PDDWaybillDetailDto.class));
                return Boolean.TRUE;
            }
        } catch (Exception e) {
            log.error("拼多多获取缓存发生异常，请求参数：{}", waybillCode, e);
        }
        return Boolean.FALSE;
    }

    private Boolean queryPDDWaybillInWaybill(String waybillCode, PDDResponse<PDDWaybillDetailDto> response) {
        //初始化运单数据
        WChoice wChoice = new WChoice();
        wChoice.setQueryWaybillC(Boolean.TRUE);
        wChoice.setQueryWaybillE(Boolean.TRUE);
        wChoice.setQueryWaybillM(Boolean.TRUE);
        wChoice.setQueryWaybillExtend(Boolean.TRUE);
        wChoice.setQueryWaybillP(Boolean.TRUE);

        com.jd.etms.waybill.domain.BaseEntity<BigWaybillDto> waybillDtoBaseEntity = waybillQueryManager.getDataByChoice(waybillCode, wChoice);

        //如果运单中收件人电话或收件都是密文，则调用拼多多接口，否则直接返回运单数据
        if(waybillDtoBaseEntity != null && Constants.RESULT_SUCCESS == waybillDtoBaseEntity.getResultCode()
                && waybillDtoBaseEntity.getData()!=null && waybillDtoBaseEntity.getData().getWaybill() != null) {
            Waybill waybill = waybillDtoBaseEntity.getData().getWaybill();

            if ((StringUtils.isNotBlank(waybill.getReceiverMobile())
                    && waybill.getReceiverMobile().indexOf('*') == -1)
                    || (StringUtils.isNotBlank(waybill.getReceiverTel())
                    && waybill.getReceiverTel().indexOf('*') == -1)) {

                /* 对运单接口接口返回内容进行重新组装返回 */
                PDDWaybillDetailDto pddWaybillPrintInfoDto = new PDDWaybillDetailDto();
                pddWaybillPrintInfoDto.setWaybillCode(waybillCode);
                pddWaybillPrintInfoDto.setSenderName(waybill.getConsigner());
                pddWaybillPrintInfoDto.setSenderMobile(waybill.getConsignerMobile());
                pddWaybillPrintInfoDto.setSenderPhone(waybill.getConsignerTel());
                pddWaybillPrintInfoDto.setConsigneeName(waybill.getReceiverName());
                pddWaybillPrintInfoDto.setConsigneeMobile(waybill.getReceiverMobile());
                pddWaybillPrintInfoDto.setConsigneePhone(waybill.getReceiverTel());

                response.setSuccess(Boolean.TRUE);
                response.setErrorCode(String.valueOf(JdResponse.CODE_SUCCESS));
                response.setErrorMsg(JdResponse.MESSAGE_SUCCESS);
                response.setResult(pddWaybillPrintInfoDto);
                return Boolean.TRUE;
            }
        }

        return Boolean.FALSE;
    }

    private Boolean queryPDDWaybillInPDD(String waybillCode, PDDResponse<PDDWaybillDetailDto> response) {
        PDDWaybillQueryDto pddWaybillQueryDto = new PDDWaybillQueryDto();
        pddWaybillQueryDto.setWaybillCode(waybillCode);

        try {
            PDDResponse<PDDWaybillDetailDto> pddResponse = pddWaybillQueryManager.doRestInterface(pddWaybillQueryDto);
            if (pddResponse != null && pddResponse.getResult() != null) {
                /* 如果從拼多多的訂單中獲取成功的話，則進行redis緩存 */
                String redisKey = MessageFormat.format(redis_key,waybillCode);
                jimdbCacheService.setEx(redisKey, JsonHelper.toJson(pddResponse.getResult()), EXPIRED_TIME, TimeUnit.DAYS);

                response.setSuccess(Boolean.TRUE);
                response.setErrorCode(String.valueOf(JdResponse.CODE_SUCCESS));
                response.setErrorMsg(JdResponse.MESSAGE_SUCCESS);
                response.setResult(pddResponse.getResult());
                return Boolean.TRUE;
            }
        } catch (Exception e) {
            log.error("获取拼多多电子面信息失败，信息获取为空,waybillCode={},e:{}", waybillCode, e.getMessage());
        }

        return Boolean.FALSE;
    }
}
