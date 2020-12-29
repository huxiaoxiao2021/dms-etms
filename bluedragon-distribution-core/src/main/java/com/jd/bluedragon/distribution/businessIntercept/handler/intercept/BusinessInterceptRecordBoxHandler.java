package com.jd.bluedragon.distribution.businessIntercept.handler.intercept;

import com.jd.bluedragon.distribution.api.Response;
import com.jd.bluedragon.distribution.businessIntercept.dto.SaveInterceptMsgDto;
import com.jd.bluedragon.distribution.sorting.domain.Sorting;
import com.jd.bluedragon.distribution.sorting.service.SortingService;
import com.jd.fastjson.JSON;
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
@Service("businessInterceptRecordBoxHandler")
public class BusinessInterceptRecordBoxHandler extends BusinessInterceptRecordAbstractHandler {

    @Autowired
    private SortingService sortingService;

    @Override
    protected Response<Boolean> doHandle(SaveInterceptMsgDto msgDto) {
        Response<Boolean> result = new Response<>();
        result.toSucceed();
        result.setData(true);

        try {
            // 查询箱号下所有包裹号 fixme 可能存在箱号中包裹数量过大问题
            Sorting sortingParam = new Sorting();
            sortingParam.setCreateSiteCode(msgDto.getSiteCode());
            sortingParam.setBoxCode(msgDto.getBarCode());
            List<Sorting> sortingList = sortingService.findByBoxCode(sortingParam);
            if (sortingList.size() > 0) {
                for (Sorting sorting : sortingList) {
                    SaveInterceptMsgDto saveInterceptMsgDto = new SaveInterceptMsgDto();
                    BeanUtils.copyProperties(msgDto, saveInterceptMsgDto);
                    saveInterceptMsgDto.setPackageCode(sorting.getPackageCode());
                    saveInterceptMsgDto.setWaybillCode(sorting.getWaybillCode());
                    log.info("BusinessInterceptRecordBoxHandler sendInterceptMsg businessOperateInterceptSendProducer param: {}", JSON.toJSONString(saveInterceptMsgDto));
                    businessOperateInterceptSendProducer.send(msgDto.getBarCode(), JSON.toJSONString(saveInterceptMsgDto));
                }
            }
        } catch (JMQException e) {
            log.error("BusinessInterceptRecordBoxHandler doHandle businessOperateInterceptSendProducer send exception: {}", JSON.toJSONString(msgDto));
            result.toError("处理箱号纬度拦截信息提交异常");
        }
        return result;
    }
}
