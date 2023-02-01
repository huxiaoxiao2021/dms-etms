package com.jd.bluedragon.distribution.worker.jy.open;

import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.framework.DBSingleScheduler;
import com.jd.bluedragon.distribution.task.domain.Task;
import com.jd.bluedragon.openplateform.IJYOpenCargoOperate;
import com.jd.bluedragon.openplateform.entity.JYCargoOperateEntity;
import com.jd.bluedragon.utils.JsonHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @ProjectName：bluedragon-distribution
 * @Package： com.jd.bluedragon.distribution.worker.jy.open
 * @ClassName: JYOpenSendTask
 * @Description:
 * @Author： wuzuxiang
 * @CreateDate 2022/12/7 16:40
 * @Copyright: Copyright (c)2020 JDL.CN All Right Reserved
 * @Since: JDK 1.8
 * @Version： V1.0
 */
@Service
public class JYOpenSendTask extends DBSingleScheduler {

    @Autowired
    private IJYOpenCargoOperate jyOpenCargoOperate;

    @Override
    protected boolean executeSingleTask(Task task, String ownSign) throws Exception {
        InvokeResult<Boolean> result = jyOpenCargoOperate.send(JsonHelper.fromJsonUseGson(task.getBody(), JYCargoOperateEntity.class));
        return result.codeSuccess();
    }
}
