package com.jd.bluedragon.distribution.spotcheck.service.impl;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.spotcheck.domain.SpotCheckDto;
import com.jd.bluedragon.distribution.spotcheck.exceptions.SpotCheckBusinessException;
import com.jd.bluedragon.distribution.spotcheck.exceptions.SpotCheckSysException;
import com.jd.bluedragon.distribution.spotcheck.handler.SpotCheckHandlerStrategy;
import com.jd.bluedragon.distribution.spotcheck.service.SpotCheckCurrencyService;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.ump.profiler.CallerInfo;
import com.jd.ump.profiler.proxy.Profiler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 抽检通用接口实现
 *
 * @author hujiping
 * @date 2021/8/10 9:36 上午
 */
@Service("spotCheckCurrencyService")
public class SpotCheckCurrencyServiceImpl implements SpotCheckCurrencyService {

    private static Logger logger = LoggerFactory.getLogger(SpotCheckCurrencyServiceImpl.class);

    @Autowired
    private SpotCheckHandlerStrategy spotCheckHandlerStrategy;

    @Override
    public InvokeResult<Integer> checkIsExcess(SpotCheckDto spotCheckDto) {
        InvokeResult<Integer> result = new InvokeResult<Integer>();
        CallerInfo info = Profiler.registerInfo("dms.SpotCheckCurrencyService.checkIsExcess", Constants.UMP_APP_NAME_DMSWEB,false, true);
        try {
            result = spotCheckHandlerStrategy.checkIsExcess(spotCheckDto);
        }catch (SpotCheckBusinessException e){
            result.customMessage(InvokeResult.RESULT_INTERCEPT_CODE, e.getMessage());
        }catch (Exception e){
            logger.error("服务异常,入参:{}", JsonHelper.toJson(spotCheckDto), e);
            result.error();
            Profiler.functionError(info);
        }finally {
            Profiler.registerInfoEnd(info);
        }
        return result;
    }

    /**
     * 抽检处理
     *
     * @param spotCheckDto
     * @return
     */
    @Override
    public InvokeResult<Boolean> spotCheckDeal(SpotCheckDto spotCheckDto) {
        InvokeResult<Boolean> result = new InvokeResult<Boolean>();
        CallerInfo info = Profiler.registerInfo("dms.SpotCheckCurrencyService.spotCheckDeal", Constants.UMP_APP_NAME_DMSWEB,false, true);
        try {
            result = spotCheckHandlerStrategy.dealSpotCheck(spotCheckDto);
        }catch (SpotCheckBusinessException e){
            result.customMessage(InvokeResult.RESULT_INTERCEPT_CODE, e.getMessage());
        }catch (SpotCheckSysException e){
            logger.error("抽检系统异常，进行重试!");
            throw e;
        }catch (Exception e){
            logger.error("服务异常,入参:{}", JsonHelper.toJson(spotCheckDto), e);
            result.error();
            Profiler.functionError(info);
        }finally {
            Profiler.registerInfoEnd(info);
        }
        return result;
    }
}
