package com.jd.bluedragon.distribution.worker.jy.open;

import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.framework.DBSingleScheduler;
import com.jd.bluedragon.distribution.task.domain.Task;
import com.jd.bluedragon.openplateform.IJYOpenCargoOperate;
import com.jd.bluedragon.openplateform.entity.JYCargoOperateEntity;
import com.jd.bluedragon.utils.JsonHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 发货完成任务处理
 *
 * @author fanggang7
 * @copyright jd.com 京东物流JDL
 * @time 2023-06-08 08:11:01 周四
 */
@Service
public class JYOpenSendVehicleFinishTask extends DBSingleScheduler {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private IJYOpenCargoOperate jyOpenCargoOperate;

    @Override
    protected boolean executeSingleTask(Task task, String ownSign) throws Exception {
        try {
            InvokeResult<Boolean> result = jyOpenCargoOperate.sendVehicleFinish(JsonHelper.fromJsonUseGson(task.getBody(), JYCargoOperateEntity.class));
            return result.codeSuccess();
        } catch (Exception e) {
            log.error("JYOpenSendVehicleFinishTask exception {}", JsonHelper.toJson(task), e);
            throw new Exception(e);
        }
    }
}
