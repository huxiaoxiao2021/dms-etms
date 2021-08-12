package com.jd.bluedragon.distribution.businessIntercept.handler.dispose;

import com.jd.bluedragon.core.jmq.producer.DefaultJMQProducer;
import com.jd.bluedragon.distribution.api.Response;
import com.jd.bluedragon.distribution.businessIntercept.dto.SaveDisposeAfterInterceptMsgDto;
import com.jd.bluedragon.distribution.businessIntercept.dto.SaveInterceptMsgDto;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

/**
 * 拦截记录处理抽象类
 *
 * @author fanggang7
 * @time 2020-12-23 09:55:25 周三
 */
@Service
public abstract class BusinessInterceptDisposeRecordAbstractHandler implements IBusinessInterceptDisposeRecordHandler {

    protected final Logger log = LoggerFactory.getLogger(this.getClass());

    @Qualifier("disposeAfterInterceptSendProducer")
    @Autowired
    protected DefaultJMQProducer disposeAfterInterceptSendProducer;

    /**
     * 处理拦截后处理闭环提交数据
     *
     * @param msgDto 提交数据
     * @return 处理结果
     * @author fanggang7
     * @time 2020-12-23 09:36:46 周三
     */
    @Override
    public Response<Boolean> handle(SaveDisposeAfterInterceptMsgDto msgDto) {
        Response<Boolean> result = new Response<>();
        result.toSucceed();
        result.setData(true);

        this.processMsgToStandard(msgDto);

        /* 处理拦截消息任务 */
        doHandle(msgDto);

        /* 处理之后 */

        return result;
    }

    /**
     * 消息数据标准化处理，防止大小写问题
     * @param msgDto 消息对象
     * @author fanggang7
     * @time 2021-08-05 10:47:31 周四
     */
    protected void processMsgToStandard(SaveDisposeAfterInterceptMsgDto msgDto){
        try {
            msgDto.setBarCode(StringUtils.upperCase(msgDto.getBarCode()));
            msgDto.setWaybillCode(StringUtils.upperCase(msgDto.getWaybillCode()));
            msgDto.setPackageCode(StringUtils.upperCase(msgDto.getPackageCode()));
        } catch (Exception e) {
            log.info("BusinessInterceptDisposeRecordAbstractHandler.processMsgToStandard exception {}", e.getMessage(), e);
        }
    }

    protected abstract Response<Boolean> doHandle(SaveDisposeAfterInterceptMsgDto msgDto);
}
