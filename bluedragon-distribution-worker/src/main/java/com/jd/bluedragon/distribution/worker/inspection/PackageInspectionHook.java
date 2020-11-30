package com.jd.bluedragon.distribution.worker.inspection;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.core.jmq.producer.DefaultJMQProducer;
import com.jd.bluedragon.distribution.framework.TaskHook;
import com.jd.bluedragon.distribution.inspection.domain.Inspection;
import com.jd.bluedragon.distribution.inspection.domain.InspectionPackageMQ;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.jmq.common.message.Message;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.util.ArrayList;
import java.util.List;

/**
 * @ClassName PackageInspectionHook
 * @Description
 * @Author wyh
 * @Date 2020/11/18 15:09
 **/
public class PackageInspectionHook extends AbstractTaskHook {

    @Autowired
    @Qualifier("dmsInspectionMQProducer")
    private DefaultJMQProducer dmsInspectionMQ;

    @Override
    @JProfiler(jKey = "dmsworker.PackageInspectionHook.hook", jAppName= Constants.UMP_APP_NAME_DMSWORKER, mState={JProEnum.TP, JProEnum.FunctionError})
    public int hook(InspectionTaskExecuteContext context) {

        this.pushPackageInspectionMQ(context);

        return 1;
    }

    /**
     * 发送包裹验货消息
     * @param context
     */
    private void pushPackageInspectionMQ(InspectionTaskExecuteContext context) {

        List<Inspection> inspectionList = context.getInspectionList();
        if (CollectionUtils.isEmpty(inspectionList)) {
            return;
        }
        List<Message> messageList = new ArrayList<>();
        for (Inspection inspection : inspectionList) {

            InspectionPackageMQ mq = new InspectionPackageMQ();
            mq.setPackageCode(inspection.getPackageBarcode());
            mq.setWaybillCode(inspection.getWaybillCode());
            mq.setBoxCode(inspection.getBoxCode());
            mq.setOperateUserId(inspection.getCreateUserCode());
            mq.setOperateUser(inspection.getCreateUser());
            mq.setOperateSiteCode(inspection.getCreateSiteCode());
            mq.setReceiveSiteCode(inspection.getReceiveSiteCode());
            mq.setInspectionTime(inspection.getOperateTime());
            mq.setInspectionType(inspection.getInspectionType());
            mq.setOperateType(inspection.getOperateType());
            mq.setExceptionType(inspection.getExceptionType());
            mq.setRecordCreateTime(inspection.getCreateTime());

            Message message = new Message();
            message.setBusinessId(mq.getPackageCode());
            message.setText(JsonHelper.toJson(mq));
            message.setTopic(dmsInspectionMQ.getTopic());
            messageList.add(message);
        }

        dmsInspectionMQ.batchSendOnFailPersistent(messageList);
    }
}
