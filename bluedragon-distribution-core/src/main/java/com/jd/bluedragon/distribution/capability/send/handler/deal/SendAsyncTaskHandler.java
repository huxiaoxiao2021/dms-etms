package com.jd.bluedragon.distribution.capability.send.handler.deal;

import com.jd.bluedragon.distribution.capability.send.domain.SendOfCAContext;
import com.jd.bluedragon.distribution.capability.send.handler.SendDimensionStrategyHandler;
import com.jd.bluedragon.distribution.send.domain.SendM;
import com.jd.bluedragon.distribution.send.domain.SendTaskBody;
import com.jd.bluedragon.distribution.send.domain.SendTaskCategoryEnum;
import com.jd.bluedragon.distribution.send.service.DeliveryService;
import com.jd.bluedragon.distribution.task.domain.Task;
import com.jd.bluedragon.distribution.task.service.TaskService;
import com.jd.bluedragon.utils.BusinessHelper;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.bluedragon.utils.Md5Helper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 天官赐福 ◎ 百无禁忌
 *
 * @Auther: 刘铎（liuduo8）
 * @Date: 2023/8/27
 * @Description: 推送发货相关异步任务
 */
@Service
public class SendAsyncTaskHandler extends SendDimensionStrategyHandler {

    @Autowired
    private TaskService tTaskService;

    @Autowired
    private DeliveryService deliveryService;

    /**
     * 按包裹扫描回传运单逻辑
     * @param context
     * @return
     */
    @Override
    public boolean doPackHandler(SendOfCAContext context) {
        doTask(context,SendTaskCategoryEnum.PACKAGE_SEND.getCode(),Task.TASK_SUB_TYPE_PACKAGE_SEND);
        return true;
    }

    /**
     * 按运单维度异步任务
     * @param context
     * @return
     */
    @Override
    public boolean doWaybillHandler(SendOfCAContext context) {
        deliveryService.pushWaybillSendTask(context.getRequestTurnToSendM(), Task.TASK_TYPE_SEND_DELIVERY);
        return true;
    }
    /**
     * 按箱号扫描回传运单逻辑
     * @param context
     * @return
     */
    @Override
    public boolean doBoxHandler(SendOfCAContext context) {
        doTask(context,SendTaskCategoryEnum.BOX_SEND.getCode(),Task.TASK_SUB_TYPE_BOX_SEND);
        return true;
    }

    /**
     * 按板号异步任务
     * @param context
     * @return
     */
    @Override
    public boolean doBoardHandler(SendOfCAContext context) {
        deliveryService.pushBoardSendTask(context.getRequestTurnToSendM(),Task.TASK_TYPE_BOARD_SEND);
        return true;
    }

    /**
     * 推送回传运单任务，构建原发货任务实体
     * @param context
     * @param handleCategory
     * @param taskSubType
     */
    private void doTask(SendOfCAContext context, Integer handleCategory, Integer taskSubType){
        SendM domain = context.getRequestTurnToSendM();
        SendTaskBody body = new SendTaskBody();
        body.setHandleCategory(handleCategory);
        body.copyFromParent(domain);
        Task tTask = new Task();
        tTask.setBoxCode(domain.getBoxCode());
        tTask.setBody(JsonHelper.toJson(body));
        tTask.setCreateSiteCode(domain.getCreateSiteCode());
        tTask.setKeyword2(String.valueOf(domain.getSendType()));
        tTask.setReceiveSiteCode(domain.getReceiveSiteCode());
        tTask.setType(Task.TASK_TYPE_SEND_DELIVERY);
        tTask.setTableName(Task.getTableName(Task.TASK_TYPE_SEND_DELIVERY));
        tTask.setSequenceName(Task.getSequenceName(Task.TABLE_NAME_SEND));
        String ownSign = BusinessHelper.getOwnSign();
        tTask.setOwnSign(ownSign);
        // 1 回传运单状态
        tTask.setKeyword1("1");
        tTask.setSubType(taskSubType);
        tTask.setFingerprint(Md5Helper.encode(domain.getSendCode() + "_" + tTask.getKeyword1() + domain.getBoxCode() + tTask.getKeyword1()));
        tTaskService.add(tTask, true);
    }


}
