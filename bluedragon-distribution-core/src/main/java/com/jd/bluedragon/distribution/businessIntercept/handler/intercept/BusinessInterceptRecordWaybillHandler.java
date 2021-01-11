package com.jd.bluedragon.distribution.businessIntercept.handler.intercept;

import com.jd.bluedragon.common.domain.Pack;
import com.jd.bluedragon.common.domain.Waybill;
import com.jd.bluedragon.common.service.WaybillCommonService;
import com.jd.bluedragon.distribution.api.Response;
import com.jd.bluedragon.distribution.businessIntercept.dto.SaveInterceptMsgDto;
import com.jd.fastjson.JSON;
import com.jd.jmq.common.exception.JMQException;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 运单纬度的拦截记录处理
 *
 * @author fanggang7
 * @time 2020-12-23 09:40:57 周三
 */
@Service("businessInterceptRecordWaybillHandler")
public class BusinessInterceptRecordWaybillHandler extends BusinessInterceptRecordAbstractHandler {

    @Autowired
    private WaybillCommonService waybillCommonService;

    @Override
    protected Response<Boolean> doHandle(SaveInterceptMsgDto msgDto) {
        Response<Boolean> result = new Response<>();
        result.toSucceed();
        result.setData(true);

        try {
            Waybill waybillAndPack = waybillCommonService.findWaybillAndPack(msgDto.getBarCode(), true, false, true, true);
            if(waybillAndPack != null && CollectionUtils.isNotEmpty(waybillAndPack.getPackList())){
                List<Pack> packList = waybillAndPack.getPackList();
                msgDto.setWaybillCode(msgDto.getBarCode());
                this.getAndSetWaybillInterceptEffectTime(msgDto);
                for (Pack pack : packList) {
                    SaveInterceptMsgDto saveInterceptMsgDto = new SaveInterceptMsgDto();
                    BeanUtils.copyProperties(msgDto, saveInterceptMsgDto);
                    saveInterceptMsgDto.setPackageCode(pack.getPackageCode());
                    log.info("BusinessInterceptRecordWaybillHandler sendInterceptMsg businessOperateInterceptSendProducer param: {}", JSON.toJSONString(saveInterceptMsgDto));
                    businessOperateInterceptSendProducer.send(msgDto.getBarCode(), JSON.toJSONString(saveInterceptMsgDto));
                }
            }
        } catch (JMQException e) {
            log.error("BusinessInterceptRecordWaybillHandler doHandle businessOperateInterceptSendProducer send exception: {}", JSON.toJSONString(msgDto), e);
            result.toError("运单维度处理拦截消息提交异常");
        }
        return result;
    }
}
