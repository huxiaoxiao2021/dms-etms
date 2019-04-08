package com.jd.bluedragon.distribution.print.waybill.handler;

import com.jd.bluedragon.KeyConstants;
import com.jd.bluedragon.core.redis.service.RedisManager;
import com.jd.bluedragon.distribution.api.JdResponse;
import com.jd.bluedragon.distribution.handler.InterceptHandler;
import com.jd.bluedragon.distribution.handler.InterceptResult;
import com.jd.bluedragon.utils.StringHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * <P>
 *     校验包裹补打是否在1小时内重复打印
 * </p>
 *
 * @author wuzuxiang
 * @since 2019/4/5
 */
@Service("printCheckInterceptHandler")
public class PrintCheckInterceptHandler implements InterceptHandler<WaybillPrintContext,String> {

    private static final Logger LOGGER = LoggerFactory.getLogger(PrintCheckInterceptHandler.class);

    @Autowired
    private RedisManager redisManager;

    @Override
    public InterceptResult<String> handle(WaybillPrintContext context) {
        InterceptResult<String> result = context.getResult();
        String barCode = context.getRequest().getBarCode();
        if (StringHelper.isEmpty(barCode)) {
            return result;
        }

        /* 读取redis的缓存记录 */
        String barCodeCached = redisManager.getCache(
                KeyConstants.genConstantsKey(KeyConstants.REDIS_PREFIX_KEY_PACK_REPRINT, barCode));
        if(StringHelper.isNotEmpty(barCodeCached)){
            LOGGER.warn("handler.PrintCheckInterceptHandler-->{}该单号一小时之内重复打印",barCode);
            result.toWeakSuccess(JdResponse.CODE_RE_PRINT_IN_ONE_HOUR,JdResponse.MESSAGE_RE_PRINT_IN_ONE_HOUR);
        }

        return context.getResult();
    }
}
