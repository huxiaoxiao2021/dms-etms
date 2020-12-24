package com.jd.bluedragon.distribution.businessIntercept.handler.intercept;

import com.jd.bluedragon.core.jmq.producer.DefaultJMQProducer;
import com.jd.bluedragon.distribution.api.Response;
import com.jd.bluedragon.distribution.businessIntercept.dto.SaveInterceptMsgDto;
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
public abstract class BusinessInterceptRecordAbstractHandler implements IBusinessInterceptRecordHandler {

    protected final Logger log = LoggerFactory.getLogger(this.getClass());

    @Qualifier("businessOperateInterceptSendProducer")
    @Autowired
    protected DefaultJMQProducer businessOperateInterceptSendProducer;

    /**
     * 处理拦截提交数据
     *
     * @param msgDto 提交数据
     * @return 处理结果
     * @author fanggang7
     * @time 2020-12-23 09:36:46 周三
     */
    @Override
    public Response<Boolean> handle(SaveInterceptMsgDto msgDto) {
        Response<Boolean> result = new Response<>();
        result.toSucceed();
        result.setData(true);

        /* 处理拦截消息任务 */
        doHandle(msgDto);

        /* 处理之后 */

        return result;
    }

    /**
     * 抽象处理类，由不同类型处理器处理
     * @param msgDto 提交数据
     * @return 处理结果
     * @author fanggang7
     * @time 2020-12-23 14:24:59 周三
     */
    protected abstract Response<Boolean> doHandle(SaveInterceptMsgDto msgDto);
}
