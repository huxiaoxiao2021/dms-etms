package com.jd.bluedragon.distribution.consumer.jy.exception;

import com.google.common.collect.Lists;
import com.jd.bluedragon.Constants;
import com.jd.bluedragon.common.dto.operation.workbench.enums.JyBizTaskExceptionProcessStatusEnum;
import com.jd.bluedragon.common.dto.operation.workbench.enums.JyExpStatusEnum;
import com.jd.bluedragon.core.message.base.MessageBaseConsumer;
import com.jd.bluedragon.distribution.jy.exception.JyBizTaskExceptionEntity;
import com.jd.bluedragon.distribution.jy.exception.JyExScrapNoticeMQ;
import com.jd.bluedragon.distribution.jy.service.exception.JyExceptionService;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.bluedragon.utils.NoticeUtils;
import com.jd.common.util.StringUtils;
import com.jd.jmq.common.message.Message;
import com.jd.ump.profiler.CallerInfo;
import com.jd.ump.profiler.proxy.Profiler;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

/**
 * 异常报废咚咚通知
 *
 * @author hujiping
 * @date 2023/3/13 1:54 PM
 */
@Service("dmsExScrapNoticeConsumer")
public class DmsExScrapNoticeConsumer extends MessageBaseConsumer {

    private static final Logger logger = LoggerFactory.getLogger(DmsExScrapNoticeConsumer.class);
    
    @Autowired
    private JyExceptionService jyExceptionService;
    
    @Override
    public void consume(Message message) throws Exception {
        CallerInfo info = Profiler.registerInfo("DmsExScrapNoticeConsumer.consume", Constants.UMP_APP_NAME_DMSWORKER, false, true);
        try {
            if (!JsonHelper.isJsonString(message.getText())) {
                logger.warn("异常报废咚咚通知消息体非JSON格式，内容为【{}】", message.getText());
                return;
            }
            JyExScrapNoticeMQ jyExScrapNoticeMQ = JsonHelper.fromJson(message.getText(), JyExScrapNoticeMQ.class);
            if(jyExScrapNoticeMQ == null || StringUtils.isEmpty(jyExScrapNoticeMQ.getHandlerErp()) 
                    || jyExScrapNoticeMQ.getQueryStartTime() == null || jyExScrapNoticeMQ.getQueryEndTime() == null) {
                logger.warn("异常报废咚咚通知消息体异常，内容为【{}】", message.getText());
                return;
            }
            String handlerErp = jyExScrapNoticeMQ.getHandlerErp();
            List<JyBizTaskExceptionEntity> scrapDetailList = jyExceptionService.queryScrapDetailByCondition(handlerErp, jyExScrapNoticeMQ.getQueryStartTime(), 
                    jyExScrapNoticeMQ.getQueryEndTime());
            if(CollectionUtils.isEmpty(scrapDetailList)){
                logger.warn("未查询到:{}的异常报废数据!", jyExScrapNoticeMQ.getHandlerErp());
                return;
            }
            int totalCount = scrapDetailList.size();
            int approveCount = 0; // 审批数量
            int kfCount = 0; // 客服介入梳理
            int completeCount = 0; // 已完成数量
            for (JyBizTaskExceptionEntity temp : scrapDetailList) {
                if(Objects.equals(temp.getProcessingStatus(), JyBizTaskExceptionProcessStatusEnum.APPROVING.getCode())){
                    approveCount ++;
                }
                if(Objects.equals(temp.getProcessingStatus(), JyBizTaskExceptionProcessStatusEnum.WAITER_INTERVENTION.getCode())){
                    kfCount ++;
                }
                if(Objects.equals(temp.getStatus(), JyExpStatusEnum.COMPLETE.getCode())){
                    completeCount ++;
                }
            }
            // 推送咚咚
            String title = "已领取报废任务详情通知";
            String template = "您目前有%s单报废任务,其中%s单审批中,%s单客服介入中,%s单已完结需要对实物进行处理,请及时处理!";
            String content = String.format(template, totalCount, approveCount, kfCount, completeCount);
            List<String> erpList = Lists.newArrayList(handlerErp);
            NoticeUtils.noticeToTimelineWithNoUrl(title, content, erpList);
        } catch (Exception e) {
            Profiler.functionError(info);
            logger.error("异常报废推送咚咚处理异常, 消息体:{}", message.getText(), e);
        } finally {
            Profiler.registerInfoEnd(info);
        }
    }
}
