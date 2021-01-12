package com.jd.bluedragon.distribution.businessIntercept.handler.intercept;

import com.jd.bluedragon.distribution.api.Response;
import com.jd.bluedragon.distribution.businessIntercept.dto.SaveInterceptMsgDto;
import com.jd.bluedragon.dms.utils.WaybillUtil;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.jmq.common.exception.JMQException;
import org.springframework.stereotype.Service;

/**
 * 包裹纬度的拦截记录处理
 *
 * @author fanggang7
 * @time 2020-12-23 09:40:57 周三
 */
@Service("businessInterceptRecordPackageHandler")
public class BusinessInterceptRecordPackageHandler extends BusinessInterceptRecordAbstractHandler {

    @Override
    protected Response<Boolean> doHandle(SaveInterceptMsgDto msgDto){
        Response<Boolean> result = new Response<>();
        result.toSucceed();
        result.setData(true);

        try {
            msgDto.setPackageCode(msgDto.getBarCode());
            msgDto.setWaybillCode(WaybillUtil.getWaybillCode(msgDto.getBarCode()));
            this.getAndSetWaybillInterceptEffectTime(msgDto);
            log.info("BusinessInterceptRecordPackageHandler sendInterceptMsg businessOperateInterceptSendProducer param: {}", JsonHelper.toJson(msgDto));
            businessOperateInterceptSendProducer.send(msgDto.getBarCode(), JsonHelper.toJson(msgDto));
        } catch (JMQException e) {
            log.error("BusinessInterceptRecordPackageHandler doHandle businessOperateInterceptSendProducer send exception: {}", JsonHelper.toJson(msgDto), e);
            result.toError("包裹维度处理拦截消息提交异常");
        }
        return result;
    }
}
