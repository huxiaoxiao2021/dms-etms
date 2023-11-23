package com.jd.bluedragon.distribution.businessIntercept.service.impl;

import com.jd.bluedragon.core.base.BaseMajorManager;
import com.jd.bluedragon.core.jmq.producer.DefaultJMQProducer;
import com.jd.bluedragon.distribution.api.Response;
import com.jd.bluedragon.distribution.businessIntercept.dto.SaveDisposeAfterInterceptMsgDto;
import com.jd.bluedragon.distribution.businessIntercept.dto.SaveInterceptMsgDto;
import com.jd.bluedragon.distribution.businessIntercept.service.IBusinessInterceptReportService;
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
 * 分拣拦截报表服务实现
 *
 * @author fanggang7
 * @time 2020-12-11 17:36:32 周五
 */
@Data
@Service("businessInterceptReportService")
public class BusinessInterceptReportServiceImpl implements IBusinessInterceptReportService {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Qualifier("businessOperateInterceptActionSendProducer")
    @Autowired
    private DefaultJMQProducer businessOperateInterceptActionSendProducer;

    @Qualifier("disposeActionAfterInterceptSendProducer")
    @Autowired
    private DefaultJMQProducer disposeActionAfterInterceptSendProducer;

    @Autowired
    private BaseMajorManager baseMajorManager;

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
        log.info("BusinessInterceptReportServiceImpl sendInterceptMsg param: {}", JSON.toJSONString(msgDto));
        Response<Boolean> result = new Response<>();
        result.toSucceed();

        try {
            if(StringUtils.isBlank(msgDto.getOperateUserErp()) && msgDto.getOperateUserCode() != null){
                BaseStaffSiteOrgDto baseStaff = baseMajorManager.getBaseStaffByStaffId(msgDto.getOperateUserCode());
                if(baseStaff != null){
                    msgDto.setOperateUserErp(baseStaff.getErp());
                }
            }
            businessOperateInterceptActionSendProducer.send(msgDto.getBarCode(), JSON.toJSONString(msgDto));
            log.info("BusinessInterceptReportServiceImpl sendInterceptMsg full param: {}", JSON.toJSONString(msgDto));
        } catch (Exception e) {
            log.error("BusinessInterceptReportServiceImpl sendInterceptMsg exception {}", e.getMessage(), e);
            result.toError("发送拦截消息异常");
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
        log.info("BusinessInterceptReportServiceImpl sendDisposeAfterInterceptMsg param: {}", JSON.toJSONString(msgDto));
        Response<Boolean> result = new Response<>();
        result.toSucceed();

        try {
            if(StringUtils.isBlank(msgDto.getOperateUserErp()) && msgDto.getOperateUserCode() != null){
                BaseStaffSiteOrgDto baseStaff = baseMajorManager.getBaseStaffByStaffId(msgDto.getOperateUserCode());
                if(baseStaff != null){
                    msgDto.setOperateUserErp(baseStaff.getErp());
                }
            }
            disposeActionAfterInterceptSendProducer.send(msgDto.getBarCode(), JSON.toJSONString(msgDto));
            log.info("BusinessInterceptReportServiceImpl sendDisposeAfterInterceptMsg full param: {}", JSON.toJSONString(msgDto));
        } catch (Exception e) {
            log.error("BusinessInterceptReportServiceImpl sendDisposeAfterInterceptMsg exception {}", e.getMessage(), e);
            result.toError("发送拦截消息异常");
        }
        return result;
    }
}
