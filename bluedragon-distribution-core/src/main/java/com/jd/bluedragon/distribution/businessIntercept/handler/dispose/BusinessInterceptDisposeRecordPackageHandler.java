package com.jd.bluedragon.distribution.businessIntercept.handler.dispose;

import com.jd.bluedragon.distribution.api.Response;
import com.jd.bluedragon.distribution.businessIntercept.dto.SaveDisposeAfterInterceptMsgDto;
import com.jd.bluedragon.distribution.businessIntercept.dto.SaveInterceptMsgDto;
import com.jd.bluedragon.dms.utils.WaybillUtil;
import com.alibaba.fastjson.JSON;
import com.jd.jmq.common.exception.JMQException;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

/**
 * 包裹纬度的拦截记录处理
 *
 * @author fanggang7
 * @time 2020-12-23 09:40:57 周三
 */
@Service("businessInterceptPackageRecordHandler")
public class BusinessInterceptDisposeRecordPackageHandler extends BusinessInterceptDisposeRecordAbstractHandler {

    @Override
    protected Response<Boolean> doHandle(SaveDisposeAfterInterceptMsgDto msgDto){
        Response<Boolean> result = new Response<>();
        result.toSucceed();
        result.setData(true);

        try {
            SaveDisposeAfterInterceptMsgDto saveDisposeAfterInterceptMsgDto = new SaveDisposeAfterInterceptMsgDto();
            BeanUtils.copyProperties(msgDto, saveDisposeAfterInterceptMsgDto);
            saveDisposeAfterInterceptMsgDto.setPackageCode(msgDto.getBarCode());
            saveDisposeAfterInterceptMsgDto.setWaybillCode(WaybillUtil.getWaybillCode(msgDto.getBarCode()));
            log.info("BusinessInterceptDisposeRecordPackageHandler sendDisposeAfterInterceptMsg disposeAfterInterceptSendProducer param: {}", JSON.toJSONString(saveDisposeAfterInterceptMsgDto));
            disposeAfterInterceptSendProducer.send(msgDto.getBarCode(), JSON.toJSONString(saveDisposeAfterInterceptMsgDto));
        } catch (JMQException e) {
            log.error("BusinessInterceptDisposeRecordPackageHandler doHandle disposeAfterInterceptSendProducer send exception: {}", JSON.toJSONString(msgDto));
            result.toError("处理包裹纬度拦截后闭环信息提交异常");
        }
        return result;
    }
}
