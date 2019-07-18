package com.jd.bluedragon.distribution.batchForward.service.impl;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.distribution.api.request.BatchForwardRequest;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.batchForward.service.BatchForwardService;
import com.jd.bluedragon.distribution.seal.service.NewSealVehicleService;
import com.jd.bluedragon.distribution.send.domain.SendM;
import com.jd.bluedragon.distribution.send.domain.SendResult;
import com.jd.bluedragon.distribution.send.service.DeliveryService;
import com.jd.bluedragon.distribution.send.utils.SendBizSourceEnum;
import com.jd.bluedragon.distribution.task.domain.Task;
import com.jd.bluedragon.distribution.task.service.TaskService;
import com.jd.bluedragon.utils.BusinessHelper;
import com.jd.bluedragon.utils.DateHelper;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.bluedragon.utils.SerialRuleUtil;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;


/**
 * Created by hujiping on 2018/8/1.
 */
@Service("batchForwardService")
public class BatchForwardServiceImpl implements BatchForwardService {

    private final Logger logger = Logger.getLogger(BatchForwardServiceImpl.class);

    @Autowired
    private TaskService taskService;

    @Autowired
    private DeliveryService deliveryService;

    @Autowired
    private NewSealVehicleService newSealVehicleService;

    //自营
    private static final Integer BUSINESSTYPE = 10;
    //批次转发发货类型
    private static final String SENDTYPE = "8";

    @Override
    public InvokeResult batchSend(BatchForwardRequest request) {

        InvokeResult result = new InvokeResult();
        //批次是否封车校验
        if (newSealVehicleService.checkSendCodeIsSealed(request.getNewSendCode())) {
            result.customMessage(SendResult.CODE_SENDED, "新批次号已操作封车，请换批次！");
            return result;
        }
        //插入批次转发的任务
        insertBatchForwardTask(request);
        result.customMessage(SendResult.CODE_OK, SendResult.MESSAGE_OK);
        return result;
    }

    // TODO: 2019/3/27 此处若发货批次量较大，在操作整批转发时，存在潜在风险
    @Override
    public boolean dealBatchForwardTask(Task task) {
        this.logger.info("批次转发开始：" + JsonHelper.toJson(task));
        BatchForwardRequest request = JsonHelper.fromJson(task.getBody(), BatchForwardRequest.class);
        //获得旧批次号下的所有sendM
        String oldSendCode = request.getOldSendCode();
        Integer oldCreateSiteCode = SerialRuleUtil.getCreateSiteCodeFromSendCode(oldSendCode);
        Integer oldReceiveSiteCode = SerialRuleUtil.getReceiveSiteCodeFromSendCode(oldSendCode);
        List<SendM> oldSendMList = deliveryService.getSendMBySendCodeAndSiteCode(oldSendCode, oldCreateSiteCode, oldReceiveSiteCode);
        //新批次
        String newSendCode = request.getNewSendCode();
        Integer newCreateSiteCode = SerialRuleUtil.getCreateSiteCodeFromSendCode(newSendCode);
        Integer newReceiveSiteCode = SerialRuleUtil.getReceiveSiteCodeFromSendCode(newSendCode);
        SendM domain = new SendM();
        domain.setCreateSiteCode(newCreateSiteCode);
        domain.setReceiveSiteCode(newReceiveSiteCode);
        domain.setSendCode(newSendCode);
        domain.setCreateUser(request.getUserName());
        domain.setCreateUserCode(request.getUserCode());
        domain.setSendType(request.getBusinessType());
        domain.setYn(1);
        domain.setCreateTime(new Date(System.currentTimeMillis() + Constants.DELIVERY_DELAY_TIME));
        domain.setOperateTime(new Date(System.currentTimeMillis() + Constants.DELIVERY_DELAY_TIME));
        if(oldSendMList != null &&oldSendMList.size() > 0){
            for(SendM oldSendM : oldSendMList){
                domain.setBoxCode(oldSendM.getBoxCode());
                deliveryService.packageSend(SendBizSourceEnum.BATCH_FORWARD_SEND, domain);
            }
            return true;
        }
        return false;
    }


    /**
     * 查询批次号下是否有箱或包裹
     * @param sendCode
     * @return
     */
    @Override
    public Boolean isHaveBox(String sendCode) {
        Integer createSiteCode = SerialRuleUtil.getCreateSiteCodeFromSendCode(sendCode);
        Integer receiveSiteCode = SerialRuleUtil.getReceiveSiteCodeFromSendCode(sendCode);
        List<SendM> oldSendMList = deliveryService.getSendMBySendCodeAndSiteCode(sendCode, createSiteCode, receiveSiteCode);
        if(oldSendMList == null || oldSendMList.size() == 0){
            return false;
        }
        return true;
    }

    private void insertBatchForwardTask(BatchForwardRequest request) {

        Task task = new Task();

        Integer createSiteCode = SerialRuleUtil.getCreateSiteCodeFromSendCode(request.getNewSendCode());
        Integer receiveSiteCode = SerialRuleUtil.getReceiveSiteCodeFromSendCode(request.getNewSendCode());

        task.setCreateSiteCode(createSiteCode);
        task.setReceiveSiteCode(receiveSiteCode);
        task.setBusinessType(BUSINESSTYPE);
        task.setType(Task.TASK_TYPE_SEND_DELIVERY);
        task.setTableName(Task.getTableName(Task.TASK_TYPE_SEND_DELIVERY));
        task.setSequenceName(Task.getSequenceName(Task.TABLE_NAME_SEND));
        task.setKeyword1(SENDTYPE);
        task.setKeyword2(String.valueOf(BUSINESSTYPE));
        task.setFingerprint(request.getNewSendCode());
        task.setOperateTime(DateHelper.parseDate(request.getOperateTime()));
        task.setOwnSign(BusinessHelper.getOwnSign());

        task.setBody(JsonHelper.toJson(request));
        taskService.add(task, true);
        logger.info("批次转发插入task_send" + JsonHelper.toJson(task));
    }

}
