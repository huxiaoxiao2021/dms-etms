package com.jd.bluedragon.distribution.businessIntercept.handler.intercept;

import com.jd.bluedragon.distribution.api.Response;
import com.jd.bluedragon.distribution.businessIntercept.dto.SaveInterceptMsgDto;
import com.jd.bluedragon.dms.utils.WaybillUtil;
import com.jd.fastjson.JSON;
import com.jd.jmq.common.exception.JMQException;
import org.springframework.beans.BeanUtils;
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
            SaveInterceptMsgDto saveInterceptMsgDto = new SaveInterceptMsgDto();
            BeanUtils.copyProperties(msgDto, saveInterceptMsgDto);
            saveInterceptMsgDto.setPackageCode(msgDto.getPackageCode());
            saveInterceptMsgDto.setWaybillCode(WaybillUtil.getWaybillCode(msgDto.getPackageCode()));
            log.info("BusinessInterceptRecordPackageHandler sendInterceptMsg businessOperateInterceptSendProducer param: {}", JSON.toJSONString(saveInterceptMsgDto));
            businessOperateInterceptSendProducer.send(msgDto.getBarCode(), JSON.toJSONString(saveInterceptMsgDto));
        } catch (JMQException e) {
            log.error("BusinessInterceptRecordPackageHandler doHandle businessOperateInterceptSendProducer send exception: {}", JSON.toJSONString(msgDto));
            result.toError("包裹维度处理拦截消息提交异常");
        }
        return result;
    }
}
