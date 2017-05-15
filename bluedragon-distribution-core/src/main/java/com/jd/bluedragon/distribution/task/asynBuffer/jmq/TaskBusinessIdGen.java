package com.jd.bluedragon.distribution.task.asynBuffer.jmq;

import com.jd.bluedragon.distribution.task.domain.Task;
import com.jd.ql.framework.asynBuffer.producer.jmq.BusinessIdGen;
import net.sf.json.JSONObject;


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
            return task.getType()+"-"+task.getKeyword1();
        }
        return businessId;
    }

    protected String getBussinessId(Task task){
        Integer taskType = task.getType();

        if(taskType.equals(Task.TASK_TYPE_SEND_DELIVERY)){
            if(task.getBody()== null)
                return null;
            String [] data = task.getBody().split("_");
            if(data.length<1)
                return null;
            return data[data.length-1];
        } else if(taskType.equals(Task.TASK_TYPE_DEPARTURE)){
            if(task.getBoxCode() == null){
                return null;
            }
            String [] data = task.getBoxCode().split("_");
            if(data.length<1)
                return null;
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
            String waybillCode = null;
            if(body.startsWith("[")&&body.endsWith("]"))
                body = body.substring(1,body.length()-1);
            try {
                JSONObject jsonObject = JSONObject.fromObject(body);
                waybillCode = (String) jsonObject.get("waybillCode");
            }catch(Exception e){
                //获取waybillCode失败。
            }
            return waybillCode;
        }
        return null;
    }
}
