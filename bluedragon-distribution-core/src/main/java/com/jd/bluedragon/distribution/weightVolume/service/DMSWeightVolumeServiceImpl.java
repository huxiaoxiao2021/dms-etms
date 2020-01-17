package com.jd.bluedragon.distribution.weightVolume.service;

import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.task.domain.Task;
import com.jd.bluedragon.distribution.task.service.TaskService;
import com.jd.bluedragon.distribution.weightVolume.check.WeightVolumeChecker;
import com.jd.bluedragon.distribution.weightVolume.handler.WeightVolumeHandlerStrategy;
import com.jd.bluedragon.distribution.weightVolume.domain.WeightVolumeEntity;
import com.jd.bluedragon.utils.BusinessHelper;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.bluedragon.utils.StringHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * <p>
 *     分拣称重量方处理逻辑
 *
 * @author wuzuxiang
 * @since 2020/1/9
 **/
@Service("dmsWeightVolumeService")
public class DMSWeightVolumeServiceImpl implements DMSWeightVolumeService {

    private static final Logger logger = LoggerFactory.getLogger(DMSWeightVolumeServiceImpl.class);

    @Autowired
    private TaskService taskService;

    @Autowired
    private WeightVolumeHandlerStrategy weightVolumeHandlerStrategy;

    @Override
    public InvokeResult<Boolean> dealWeightAndVolume(WeightVolumeEntity entity, boolean isSync) {
        InvokeResult<Boolean> result = new InvokeResult<>();
        result.success();
        result.setData(Boolean.TRUE);

        InvokeResult<Boolean> checkResult = WeightVolumeChecker.check(entity);
        if(checkResult.getCode() != InvokeResult.RESULT_SUCCESS_CODE){
            logger.warn("称重数据上传，校验未通过，参数：{}，返回值：{}",JsonHelper.toJson(entity), JsonHelper.toJson(checkResult));
            return checkResult;
        }
        if (isSync) {
            //同步处理
            return weightVolumeHandlerStrategy.doHandler(entity);
        } else {
            //异步处理
            Task weightVolumeTask = new Task();
            weightVolumeTask.setKeyword1(entity.getBarCode());
            weightVolumeTask.setKeyword2(entity.getBusinessType().name());
            weightVolumeTask.setCreateSiteCode(entity.getOperateSiteCode());
            weightVolumeTask.setCreateTime(entity.getOperateTime());
            weightVolumeTask.setType(Task.TASK_TYPE_WEIGHT_VOLUME);
            weightVolumeTask.setTableName(Task.getTableName(Task.TASK_TYPE_WEIGHT_VOLUME));
            weightVolumeTask.setSequenceName(Task.getSequenceName(Task.TABLE_NAME_WEIGHT_VOLUME));
            String ownSign = BusinessHelper.getOwnSign();
            weightVolumeTask.setOwnSign(ownSign);

            weightVolumeTask.setBody(JsonHelper.toJson(entity));
            taskService.add(weightVolumeTask);
            return result;
        }
    }

    @Override
    public InvokeResult<Boolean> dealWeightAndVolume(WeightVolumeEntity entity) {
        /* 同步处理 */
        return this.dealWeightAndVolume(entity,true);
    }
}
