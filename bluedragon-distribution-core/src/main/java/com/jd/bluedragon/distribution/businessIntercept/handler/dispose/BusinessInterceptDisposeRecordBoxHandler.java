package com.jd.bluedragon.distribution.businessIntercept.handler.dispose;

import com.jd.bluedragon.distribution.api.Response;
import com.jd.bluedragon.distribution.businessIntercept.dto.SaveDisposeAfterInterceptMsgDto;
import com.jd.bluedragon.distribution.businessIntercept.dto.SaveInterceptMsgDto;
import com.jd.bluedragon.distribution.sorting.domain.Sorting;
import com.jd.bluedragon.distribution.sorting.service.SortingService;
import com.alibaba.fastjson.JSON;
import com.jd.jmq.common.exception.JMQException;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 包裹纬度的拦截记录处理
 *
 * @author fanggang7
 * @time 2020-12-23 09:40:57 周三
 */
@Service("businessInterceptBoxRecordHandler")
public class BusinessInterceptDisposeRecordBoxHandler extends BusinessInterceptDisposeRecordAbstractHandler {

    @Autowired
    private SortingService sortingService;

    @Override
    protected Response<Boolean> doHandle(SaveDisposeAfterInterceptMsgDto msgDto) {
        Response<Boolean> result = new Response<>();
        result.toSucceed();
        result.setData(true);

        try {
            // 查询箱号下所有包裹号 fixme 可能存在箱号中包裹数量过大问题
            Sorting sortinParam = new Sorting();
            sortinParam.setCreateSiteCode(msgDto.getSiteCode());
            sortinParam.setBoxCode(msgDto.getBarCode());
            List<Sorting> sortingList = sortingService.findByBoxCode(sortinParam);
            if (sortingList.size() > 0) {
                for (Sorting sorting : sortingList) {
                    SaveDisposeAfterInterceptMsgDto saveDisposeAfterInterceptMsgDto = new SaveDisposeAfterInterceptMsgDto();
                    BeanUtils.copyProperties(msgDto, saveDisposeAfterInterceptMsgDto);
                    saveDisposeAfterInterceptMsgDto.setPackageCode(sorting.getPackageCode());
                    saveDisposeAfterInterceptMsgDto.setWaybillCode(sorting.getWaybillCode());
                    log.info("BusinessInterceptDisposeRecordBoxHandler doHandle disposeAfterInterceptSendProducer param: {}", JSON.toJSONString(saveDisposeAfterInterceptMsgDto));
                    disposeAfterInterceptSendProducer.send(msgDto.getBarCode(), JSON.toJSONString(saveDisposeAfterInterceptMsgDto));
                }
            }
        } catch (JMQException e) {
            log.error("businessInterceptBoxRecordHandler doHandle businessOperateInterceptSendProducer send exception: {}", JSON.toJSONString(msgDto));
            result.toError("处理箱号纬度拦截信息提交异常");
        }
        return result;
    }
}
