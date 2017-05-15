package com.jd.bluedragon.distribution.task.asynBuffer.jmq;

import com.jd.bluedragon.distribution.task.domain.Task;
import com.jd.bluedragon.utils.StringHelper;
import com.jd.ql.framework.asynBuffer.producer.jmq.BusinessIdGen;
import com.jd.ql.framework.idgenerator.util.JsonUtils;


/**
 * task的业务id生成器。
 *
 * @author yangwubing
 */
public class TaskBusinessIdGen implements BusinessIdGen<Task> {
    private static final String DELIMITER = "-";

    /**
     * 生成task对应的业务id，生成规则是：[type]-[keyword1].
     */
    @Override
    public String genId(Task task) {
        String businessId = getBussinessId(task);
        if(businessId==null){
            return task.getType()+DELIMITER+task.getKeyword1();
        }
        return businessId;
    }

    protected String getBussinessId(Task task){
        Integer taskType = task.getType();

        if(taskType.equals(Task.TASK_TYPE_SEND_DELIVERY)){
            if(StringHelper.isNotEmpty(task.getBody()))
                return null;
            String [] data = task.getBody().split(DELIMITER);
            return data[data.length-1];

        } else if(taskType.equals(Task.TASK_TYPE_DEPARTURE)){
            if(StringHelper.isNotEmpty(task.getBoxCode()))
                return null;
            String [] data = task.getBoxCode().split(DELIMITER);
            return data[data.length-1];

        } else if(taskType.equals(Task.TASK_TYPE_RECEIVE)
                || taskType.equals(Task.TASK_TYPE_SHIELDS_CAR_ERROR)
                ||taskType.equals(Task.TASK_TYPE_INSPECTION)
                ||taskType.equals(Task.TASK_TYPE_SHIELDS_BOX_ERROR)
                ||taskType.equals(Task.TASK_TYPE_PARTNER_WAY_BILL)
                ||taskType.equals(Task.TASK_TYPE_PARTNER_WAY_BILL_NOTIFY)
                ||taskType.equals(Task.TASK_TYPE_SORTING)
                ||taskType.equals(Task.TASK_TYPE_SEAL_BOX)
                ||taskType.equals(Task.TASK_TYPE_RETURNS)){
            return task.getKeyword2();
        } else if (taskType .equals(Task.TASK_TYPE_WEIGHT) ){
            String body = task.getBody();
            if(StringHelper.isNotEmpty(body)) {
                String waybillCode = null;
                try {
                    waybillCode = JsonUtils.searchValue(body, "waybillCode");
                } catch (Exception e) {
                    return null;
                }
                return waybillCode;
            }
        }
        return null;
    }
}
