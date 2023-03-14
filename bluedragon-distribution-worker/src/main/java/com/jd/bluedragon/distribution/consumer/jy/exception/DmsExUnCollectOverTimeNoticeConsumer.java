package com.jd.bluedragon.distribution.consumer.jy.exception;

import com.google.common.collect.Lists;
import com.jd.bluedragon.Constants;
import com.jd.bluedragon.core.base.BaseMajorManager;
import com.jd.bluedragon.core.base.HrUserManager;
import com.jd.bluedragon.core.message.base.MessageBaseConsumer;
import com.jd.bluedragon.distribution.jy.exception.JyExceptionAgg;
import com.jd.bluedragon.utils.DateHelper;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.bluedragon.utils.NoticeUtils;
import com.jd.common.util.StringUtils;
import com.jd.jmq.common.message.Message;
import com.jd.ql.basic.dto.BaseStaffSiteOrgDto;
import com.jd.ump.profiler.CallerInfo;
import com.jd.ump.profiler.proxy.Profiler;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * 异常-未领取超时任务咚咚通知
 *
 * @author hujiping
 * @date 2023/3/13 4:31 PM
 */
@Service("dmsExUnCollectOverTimeNoticeConsumer")
public class DmsExUnCollectOverTimeNoticeConsumer extends MessageBaseConsumer {

    private static final Logger logger = LoggerFactory.getLogger(DmsExUnCollectOverTimeNoticeConsumer.class);

    @Autowired
    private HrUserManager hrUserManager;

    @Autowired
    private BaseMajorManager baseMajorManager;

    @Override
    public void consume(Message message) throws Exception {
        CallerInfo info = Profiler.registerInfo("DmsExUnCollectOverTimeNoticeConsumer.consume", Constants.UMP_APP_NAME_DMSWORKER, false, true);
        try {
            if (!JsonHelper.isJsonString(message.getText())) {
                logger.warn("未领取超时异常通知消息体非JSON格式，内容为【{}】", message.getText());
                return;
            }
            List<JyExceptionAgg> jyExceptionAggs = JsonHelper.jsonToList(message.getText(), JyExceptionAgg.class);
            if(CollectionUtils.isEmpty(jyExceptionAggs)) {
                logger.warn("未领取超时任务消息体为空，内容为【{}】", message.getText());
                return;
            }
            // 场地负责人
            String createUserErp = jyExceptionAggs.get(0).getCreateUserErp();
            String superiorErp = hrUserManager.getSuperiorErp(createUserErp);
            if(StringUtils.isEmpty(superiorErp)){
                logger.warn("创建人:{}的上级领导不存在!", createUserErp);
                return;
            }
            // 场地名称
            Integer siteCode = jyExceptionAggs.get(0).getSiteCode();
            BaseStaffSiteOrgDto baseSite = baseMajorManager.getBaseSiteBySiteId(siteCode);
            if(baseSite == null){
                logger.warn("场地id:{}不存在!", siteCode);
                return;
            }
            String siteName = baseSite.getSiteName();
            // 推送咚咚
            String title = "超时未领取报废任务通知";
            String templatePrefix = "场地:%s,截止%s,有%s单三无/作废任务超24H未领取,";
            String templateGrid = "%s网格%s单,";
            String templateSuffix = "请关注!";
            
            int totalCount = 0; // 总数量
            StringBuilder gridContent = new StringBuilder(Constants.EMPTY_FILL);
            for (JyExceptionAgg temp : jyExceptionAggs) {
                totalCount += temp.getQuantity();
                gridContent.append(String.format(templateGrid, temp.getGridCode(), temp.getQuantity()));
            }
            // 咚咚提示内容
            String content = String.format(templatePrefix, siteName, DateHelper.formatDateTime(new Date()), totalCount) 
                    + gridContent + templateSuffix;
            NoticeUtils.noticeToTimelineWithNoUrl(title, content, Lists.newArrayList(superiorErp));
        } catch (Exception e) {
            Profiler.functionError(info);
            logger.error("超时未领取任务推送场地负责人异常, 消息体:{}", message.getText(), e);
        } finally {
            Profiler.registerInfoEnd(info);
        }
    }
}
