package com.jd.bluedragon.distribution.print.waybill.handler;

import com.jd.bluedragon.common.utils.CacheKeyConstants;
import com.jd.bluedragon.distribution.api.response.WaybillPrintResponse;
import com.jd.bluedragon.distribution.handler.InterceptHandler;
import com.jd.bluedragon.distribution.handler.InterceptResult;
import com.jd.bluedragon.distribution.merchantWeightAndVolume.service.MerchantWeightAndVolumeWhiteListService;
import com.jd.bluedragon.utils.StringHelper;
import com.jd.etms.waybill.domain.Waybill;
import com.jd.etms.waybill.dto.BigWaybillDto;
import com.jd.ql.dms.common.cache.CacheService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;

/**
 * 是否需要打印标签处理器
 *
 * @author: hujiping
 * @date: 2019/11/18 16:52
 */
@Service
public class IsNeedPrintHandler implements InterceptHandler<WaybillPrintContext,String> {

    private static final Logger logger = LoggerFactory.getLogger(IsNeedPrintHandler.class);

    @Autowired
    @Qualifier("jimdbCacheService")
    private CacheService jimdbCacheService;

    @Autowired
    private MerchantWeightAndVolumeWhiteListService merchantWeightAndVolumeWhiteListService;

    @Override
    public InterceptResult<String> handle(WaybillPrintContext context) {
        InterceptResult<String> interceptResult = context.getResult();
        try {
            WaybillPrintResponse commonWaybill = context.getResponse();
            Integer dmsCode = context.getRequest().getDmsSiteCode();
            BigWaybillDto bigWaybillDto = context.getBigWaybillDto();
            Waybill waybill = bigWaybillDto.getWaybill();
            //自动识别包裹标签打印标识
            Boolean discernFlag = context.getRequest().getDiscernFlag();
            if(discernFlag){
                String redisKey = MessageFormat.format(CacheKeyConstants.CACHE_KEY_PRINT_BUSI_SITE,waybill.getBusiId(),dmsCode);
                String redisValue = jimdbCacheService.get(redisKey);
                if (StringHelper.isNotEmpty(redisValue)) {
                    commonWaybill.setNeedPrintFlag(!Boolean.valueOf(redisValue));
                }else {
                    commonWaybill.setNeedPrintFlag(!merchantWeightAndVolumeWhiteListService.isExist(waybill.getBusiId(),dmsCode));
                }
            }
        }catch (Exception e){
            logger.error("查询商家、站点对应白名单异常!");
        }
        return interceptResult;
    }
}
