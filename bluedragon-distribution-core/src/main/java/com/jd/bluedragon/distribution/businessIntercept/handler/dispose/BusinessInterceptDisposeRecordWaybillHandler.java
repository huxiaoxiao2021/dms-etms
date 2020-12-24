package com.jd.bluedragon.distribution.businessIntercept.handler.dispose;

import com.jd.bluedragon.common.domain.Pack;
import com.jd.bluedragon.common.domain.Waybill;
import com.jd.bluedragon.common.service.WaybillCommonService;
import com.jd.bluedragon.distribution.api.Response;
import com.jd.bluedragon.distribution.businessIntercept.dto.SaveDisposeAfterInterceptMsgDto;
import com.jd.bluedragon.distribution.businessIntercept.dto.SaveInterceptMsgDto;
import com.jd.bluedragon.dms.utils.WaybillUtil;
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
@Service("businessInterceptWaybillRecordHandler")
public class BusinessInterceptDisposeRecordWaybillHandler extends BusinessInterceptDisposeRecordAbstractHandler {

    @Autowired
    private WaybillCommonService waybillCommonService;

    @Override
    protected Response<Boolean> doHandle(SaveDisposeAfterInterceptMsgDto msgDto) {
        Response<Boolean> result = new Response<>();
        result.toSucceed();
        result.setData(true);

        try {
            String waybillCode = WaybillUtil.getWaybillCode(msgDto.getBarCode());
            Waybill waybillAndPack = waybillCommonService.findWaybillAndPack(waybillCode, true, false, true, true);
            if(waybillAndPack != null && CollectionUtils.isNotEmpty(waybillAndPack.getPackList())){
                List<Pack> packList = waybillAndPack.getPackList();
                for (Pack pack : packList) {
                    SaveDisposeAfterInterceptMsgDto saveDisposeAfterInterceptMsgDto = new SaveDisposeAfterInterceptMsgDto();
                    BeanUtils.copyProperties(msgDto, saveDisposeAfterInterceptMsgDto);
                    saveDisposeAfterInterceptMsgDto.setPackageCode(pack.getPackageCode());
                    saveDisposeAfterInterceptMsgDto.setWaybillCode(waybillCode);
                    log.info("BusinessInterceptDisposeRecordWaybillHandler doHandle disposeAfterInterceptSendProducer param: {}", JSON.toJSONString(saveDisposeAfterInterceptMsgDto));
                    disposeAfterInterceptSendProducer.send(msgDto.getBarCode(), JSON.toJSONString(saveDisposeAfterInterceptMsgDto));
                }
            }
        } catch (JMQException e) {
            log.error("BusinessInterceptDisposeRecordWaybillHandler doHandle disposeAfterInterceptSendProducer send exception: {}", JSON.toJSONString(msgDto));
            result.toError("处理包裹纬度拦截后闭环信息提交异常");
        }
        return result;
    }
}
