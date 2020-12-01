package com.jd.bluedragon.distribution.worker.inspection;

import com.alibaba.fastjson.JSON;
import com.jd.bluedragon.Constants;
import com.jd.bluedragon.core.jmq.producer.DefaultJMQProducer;
import com.jd.bluedragon.distribution.cyclebox.domain.BoxMaterialRelationMQ;
import com.jd.bluedragon.distribution.framework.TaskHook;
import com.jd.bluedragon.distribution.receive.domain.CenConfirm;
import com.jd.jmq.common.exception.JMQException;
import com.jd.jmq.common.message.Message;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @ClassName CycleMaterialHook
 * @Description
 * @Author wyh
 * @Date 2020/11/18 15:09
 **/
public class CycleMaterialHook extends AbstractTaskHook {

    private static final Logger log = LoggerFactory.getLogger(CycleMaterialHook.class);

    @Autowired
    @Qualifier("dmsInspectionPackageProducer")
    private DefaultJMQProducer inspectionMaterialSendMQ;

    @Override
    @JProfiler(jKey = "dmsworker.CycleMaterialHook.hook", jAppName= Constants.UMP_APP_NAME_DMSWORKER, mState={JProEnum.TP, JProEnum.FunctionError})
    public int hook(InspectionTaskExecuteContext context) {

        this.pushCycleMaterialMQ(context);

        return 0;
    }

    /**
     * 循环集包袋验货对外MQ
     * */
    private void pushCycleMaterialMQ (InspectionTaskExecuteContext context) {
        List<CenConfirm> cenConfirmList = context.getCenConfirmList();
        if (CollectionUtils.isEmpty(cenConfirmList)) {
            return;
        }
        List<Message> messageList = new ArrayList<>();
        for (CenConfirm cenConfirm : context.getCenConfirmList()) {
            //循环集包袋在验货环节发送解绑MQ
            BoxMaterialRelationMQ loopPackageMq = new BoxMaterialRelationMQ();
            loopPackageMq.setOperatorCode(cenConfirm.getInspectionUserCode()==null?0:cenConfirm.getInspectionUserCode());
            loopPackageMq.setOperatorName(cenConfirm.getInspectionUser());
            loopPackageMq.setSiteCode(String.valueOf(cenConfirm.getCreateSiteCode()));
            loopPackageMq.setWaybillCode(Collections.singletonList(cenConfirm.getWaybillCode()));
            loopPackageMq.setPackageCode(Collections.singletonList(cenConfirm.getPackageBarcode()));
            loopPackageMq.setOperatorTime(cenConfirm.getInspectionTime());
            Message message = new Message();
            message.setBusinessId(cenConfirm.getPackageBarcode());
            message.setText(JSON.toJSONString(loopPackageMq));
            message.setTopic(inspectionMaterialSendMQ.getTopic());
            messageList.add(message);
        }

        inspectionMaterialSendMQ.batchSendOnFailPersistent(messageList);
    }

    @Override
    public boolean escape(InspectionTaskExecuteContext context) {

        return siteEnableInspectionAgg(context);
    }
}
