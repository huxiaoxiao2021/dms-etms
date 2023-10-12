package com.jd.bluedragon.distribution.capability.send.handler.deal;

import com.jd.bluedragon.distribution.capability.send.domain.SendOfCAContext;
import com.jd.bluedragon.distribution.capability.send.handler.SendDimensionStrategyHandler;
import com.jd.bluedragon.distribution.send.domain.SendM;
import com.jd.bluedragon.distribution.send.domain.SendTaskBody;
import com.jd.bluedragon.distribution.send.domain.SendTaskCategoryEnum;
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
 * @Description: 周转箱
 */
@Service
public class SendTurnoverBoxHandler extends SendDimensionStrategyHandler {


    @Autowired
    private TaskService tTaskService;

    /**
     * 回传周转箱逻辑只处理按箱发货逻辑
     * @param context
     * @return
     */
    @Override
    public boolean doBoxHandler(SendOfCAContext context) {

        SendM domain = context.getRequestTurnToSendM();
        SendTaskBody body = new SendTaskBody();
        body.setHandleCategory(SendTaskCategoryEnum.BOX_SEND.getCode());
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
        //只有箱号添加回传周转箱任务
        // 2回传周转箱号
        tTask.setKeyword1("2");
        tTask.setSubType(null);
        tTask.setFingerprint(Md5Helper.encode(domain.getSendCode() + "_" + tTask.getKeyword1() + domain.getBoxCode() + tTask.getKeyword1()));
        tTaskService.add(tTask, true);

        return true;
    }

}
