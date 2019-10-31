package com.jd.bluedragon.external.crossbow.pdd.service;

import com.jd.bluedragon.dms.utils.WaybillUtil;
import com.jd.bluedragon.external.crossbow.pdd.domain.PDDResponse;
import com.jd.bluedragon.external.crossbow.pdd.domain.PDDWaybillDetailDto;
import com.jd.bluedragon.external.crossbow.pdd.domain.PDDWaybillQueryDto;
import com.jd.bluedragon.external.crossbow.pdd.manager.PDDBusinessManager;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.bluedragon.utils.StringHelper;
import com.jd.ql.dms.common.cache.CacheService;
import com.jd.ql.dms.common.domain.JdResponse;
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

    private static final Logger logger = LoggerFactory.getLogger(PDDServiceImpl.class);

    private static final long EXPIRED_TIME = 30;//30天

    private static final String redis_key = "dms.core.PDD.waybill.{0}";

    @Autowired
    @Qualifier("pddWaybillQueryManager")
    private PDDBusinessManager pddWaybillQueryManager;

    @Autowired
    @Qualifier("jimdbCacheService")
    private CacheService jimdbCacheService;

    @Override
    public PDDWaybillDetailDto queryWaybillDetailByWaybillCode(String waybillCode) {
        if (!WaybillUtil.isPDDWaybillCode(waybillCode)) {
            return null;
        }
        PDDWaybillQueryDto pddWaybillQueryDto = new PDDWaybillQueryDto();
        pddWaybillQueryDto.setWaybillCode(waybillCode);

        PDDResponse<PDDWaybillDetailDto> response = pddWaybillQueryManager.doRestInterface(pddWaybillQueryDto);
        logger.debug("获取拼多多的电子面单处理信息，参数为：{}，返回结果为：{}",waybillCode, JsonHelper.toJson(response));
        if (response == null) {
            logger.error("获取拼多多电子面信息失败，信息获取为空,{}",waybillCode);
            return null;
        }
        if (!response.getSuccess()) {
            logger.warn("获取拼多多电子面单信息失败，单号为：{}, 原因：{}",waybillCode,response.getErrorMsg());
            return null;
        }
        return response.getResult();
    }

    @Override
    public PDDResponse<PDDWaybillDetailDto> queryPDDWaybillByWaybillCode(String waybillCode) {
        return queryPDDWaybillByWaybillCode(waybillCode,true);
    }

    /**
     * 刷新缓存,包含pdd返回值
     *
     * @param waybillCode 拼多多电子面单号
     * @return 返回拼多多电子面单处理对象
     */
    @Override
    public PDDResponse<PDDWaybillDetailDto> refreshPDDWaybillByWaybillCode(String waybillCode) {
        return queryPDDWaybillByWaybillCode(waybillCode,false);
    }


    private PDDResponse<PDDWaybillDetailDto> queryPDDWaybillByWaybillCode(String waybillCode,boolean useCache){
        PDDResponse<PDDWaybillDetailDto> response = new PDDResponse<>();
        response.setSuccess(Boolean.TRUE);
        response.setErrorCode(String.valueOf(JdResponse.CODE_SUCCESS));
        response.setErrorMsg(JdResponse.MESSAGE_SUCCESS);
        String redisKey = MessageFormat.format(redis_key,waybillCode);

        /**************************     获取缓存    *******************************/
        if(useCache) {
            try {
                String redisValue = jimdbCacheService.get(redisKey);
                if (StringHelper.isNotEmpty(redisValue)) {
                    /* 如果從redis中可以獲取到value值，則直接反序列化，不走pdd接口 */
                    logger.info("拼多多订单redis命中缓存，waybillCode:{}，返回值：{}", waybillCode, redisValue);
                    response.setResult(JsonHelper.fromJsonUseGson(redisValue, PDDWaybillDetailDto.class));
                    return response;
                }
            } catch (Exception e) {
                logger.error(MessageFormat.format("拼多多获取缓存发生异常，请求参数：{0}", waybillCode), e);
            }
        }

        /**************************     调用接口    *******************************/
        try {
            /* 调用拼多多的接口 */
            PDDWaybillQueryDto condition = new PDDWaybillQueryDto();
            condition.setWaybillCode(waybillCode);
            response = pddWaybillQueryManager.doRestInterface(condition);
            logger.info(MessageFormat.format("PddService.queryPDDWaybillByWaybillCode,req:{0},resp:{1}", waybillCode,JsonHelper.toJson(response)));
        } catch (RuntimeException e) {
            logger.error(MessageFormat.format("拼多多接口调用发生异常，请求参数：{0}", waybillCode), e);
            response.setSuccess(Boolean.FALSE);
            response.setErrorCode(String.valueOf(JdResponse.CODE_ERROR));
            response.setErrorMsg(JdResponse.MESSAGE_ERROR);
            return response;
        }

        /**************************       缓存      *******************************/
        try {
            /* 如果從拼多多的訂單中獲取成功的話，則進行redis緩存 */
            if (response != null && Boolean.TRUE.equals(response.getSuccess()) && response.getResult() != null) {
                jimdbCacheService.setEx(redisKey, JsonHelper.toJson(response.getResult()), EXPIRED_TIME, TimeUnit.DAYS);
            }
        }catch (Exception e){
            logger.error(MessageFormat.format("拼多多缓存发生异常，请求参数：{0}", waybillCode), e);
        }

        return response;
    }
}
