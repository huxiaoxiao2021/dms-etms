package com.jd.bluedragon.distribution.businessIntercept.service.impl;

import com.jd.bluedragon.common.service.WaybillCommonService;
import com.jd.bluedragon.core.base.BaseMajorManager;
import com.jd.bluedragon.core.base.WaybillQueryManager;
import com.jd.bluedragon.core.jmq.producer.DefaultJMQProducer;
import com.jd.bluedragon.distribution.api.Response;
import com.jd.bluedragon.distribution.businessIntercept.dto.SaveDisposeAfterInterceptMsgDto;
import com.jd.bluedragon.distribution.businessIntercept.dto.SaveInterceptMsgDto;
import com.jd.bluedragon.distribution.businessIntercept.handler.dispose.BusinessInterceptDisposeRecordHandlerStrategy;
import com.jd.bluedragon.distribution.businessIntercept.handler.intercept.BusinessInterceptRecordAbstractHandler;
import com.jd.bluedragon.distribution.businessIntercept.handler.intercept.BusinessInterceptRecordHandlerStrategy;
import com.jd.bluedragon.distribution.businessIntercept.service.IBusinessInterceptDetailReportService;
import com.jd.bluedragon.distribution.sorting.service.SortingService;
import com.alibaba.fastjson.JSON;
import com.jd.ql.basic.dto.BaseStaffSiteOrgDto;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

/**
 * 分拣拦截报表明细提交服务实现
 *
 * @author fanggang7
 * @time 2020-12-11 17:36:32 周五
 */
@Data
@Service("businessInterceptDetailReportService")
public class BusinessInterceptDetailReportServiceImpl implements IBusinessInterceptDetailReportService {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Qualifier("businessOperateInterceptSendProducer")
    @Autowired
    private DefaultJMQProducer businessOperateInterceptSendProducer;

    @Qualifier("disposeAfterInterceptSendProducer")
    @Autowired
    private DefaultJMQProducer disposeAfterInterceptSendProducer;

    @Autowired
    private BusinessInterceptRecordHandlerStrategy businessInterceptRecordHandlerStrategy;

    @Autowired
    private BusinessInterceptDisposeRecordHandlerStrategy businessInterceptDisposeRecordHandlerStrategy;

    @Autowired
    private SortingService sortingService;

    @Autowired
    private BaseMajorManager baseMajorManager;

    @Autowired
    private WaybillQueryManager waybillQueryManager;

    @Autowired
    private WaybillCommonService waybillCommonService;

    /**
     * 发送拦截消息
     *
     * @param msgDto 消息数据
     * @return 发送结果
     * @author fanggang7
     * @time 2020-12-11 17:38:50 周五
     */
    @Override
    public Response<Boolean> sendInterceptMsg(SaveInterceptMsgDto msgDto) {
        log.info("BusinessInterceptDetailReportServiceImpl sendInterceptMsg param: {}", JSON.toJSONString(msgDto));
        Response<Boolean> result = new Response<>();
        result.toSucceed();
        result.setData(true);

        try {
            if(StringUtils.isBlank(msgDto.getOperateUserErp()) && msgDto.getOperateUserCode() != null){
                BaseStaffSiteOrgDto baseStaff = baseMajorManager.getBaseStaffByStaffId(msgDto.getOperateUserCode());
                if(baseStaff != null){
                    msgDto.setOperateUserErp(baseStaff.getErp());
                }
            }

            Response<Boolean> handleResult = businessInterceptRecordHandlerStrategy.handle(msgDto);
            if(!handleResult.isSucceed()){
                result.setData(false);
                result.setMessage(handleResult.getMessage());
            }
        } catch (Exception e) {
            log.error("BusinessInterceptDetailReportServiceImpl sendInterceptMsg exception {}", e.getMessage(), e);
            result.toError("发送拦截消息异常 " + e.getMessage());
        }
        return result;
    }

    /**
     * 发送拦截后处理消息
     *
     * @param msgDto 消息数据
     * @return 发送结果
     * @author fanggang7
     * @time 2020-12-11 17:39:15 周五
     */
    @Override
    public Response<Boolean> sendDisposeAfterInterceptMsg(SaveDisposeAfterInterceptMsgDto msgDto) {
        log.info("BusinessInterceptDetailReportServiceImpl sendDisposeAfterInterceptMsg param: {}", JSON.toJSONString(msgDto));
        Response<Boolean> result = new Response<>();
        result.toSucceed();
        result.setData(true);

        try {
            if(StringUtils.isBlank(msgDto.getOperateUserErp()) && msgDto.getOperateUserCode() != null){
                BaseStaffSiteOrgDto baseStaff = baseMajorManager.getBaseStaffByStaffId(msgDto.getOperateUserCode());
                if(baseStaff != null){
                    msgDto.setOperateUserErp(baseStaff.getErp());
                }
            }
            Response<Boolean> handleResult = businessInterceptDisposeRecordHandlerStrategy.handle(msgDto);
            if(!handleResult.isSucceed()){
                result.setData(false);
                result.setMessage(handleResult.getMessage());
            }
        } catch (Exception e) {
            log.error("BusinessInterceptDetailReportServiceImpl sendDisposeAfterInterceptMsg exception {}", e.getMessage(), e);
            result.toError("发送拦截后闭环处理消息异常 " + e.getMessage());
        }
        return result;
    }
}
