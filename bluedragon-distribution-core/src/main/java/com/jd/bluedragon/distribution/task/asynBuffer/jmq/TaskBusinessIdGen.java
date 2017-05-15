package com.jd.bluedragon.distribution.task.asynBuffer.jmq;

import com.jd.bluedragon.distribution.task.domain.Task;
import com.jd.bluedragon.utils.StringHelper;
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
            return task.getType()+DELIMITER+task.getKeyword1();
        }
        return businessId;
    }

    protected String getBussinessId(Task task){
        Integer taskType = task.getType();
        String tableName = task.getTableName();
        String keyword1 = task.getKeyword1();

        //task_send 1300
        //keyword1 = boxCode
        //keyword2 = boxCode
        //keyword3 = boxCode 可以截后面的序列号
        //keyword4 = boxCode 可以截后面的序列号
        //keyword5 = body可以截后面的序列号

        if("task_send".equalsIgnoreCase(tableName) && taskType.equals(Task.TASK_TYPE_SEND_DELIVERY)
                && (keyword1.equals("1")||(keyword1.equals("2")) )){
            return task.getBoxCode();
        }
        if("task_send".equalsIgnoreCase(tableName) && taskType.equals(Task.TASK_TYPE_SEND_DELIVERY)
                && (keyword1.equals("3")||(keyword1.equals("4")) )){
            if(StringHelper.isNotEmpty(task.getBoxCode())) {
                String[] data = task.getBoxCode().split(DELIMITER);
                return data[data.length - 1];
            }
        }
        if("task_send".equalsIgnoreCase(tableName) && taskType.equals(Task.TASK_TYPE_SEND_DELIVERY)
                && keyword1.equals("5")){
            if(StringHelper.isNotEmpty(task.getBody())){
                String [] data = task.getBody().split(DELIMITER);
                return data[data.length-1];
            }
        }
        //task_send:1400
        // boxCode 可以截后面的序列号

        if("task_send".equalsIgnoreCase(tableName) && taskType.equals(Task.TASK_TYPE_DEPARTURE)
                && keyword1.equals("5")){
            if(StringHelper.isNotEmpty(task.getBoxCode())) {
                String[] data = task.getBoxCode().split(DELIMITER);
                return data[data.length - 1];
            }
        }

        //task_sorting 和 task_inspeciton
        if("task_sorting".equalsIgnoreCase(tableName) || "task_inspection".equalsIgnoreCase(tableName)){
            return task.getKeyword2();
        }

        //task_weight
        if (taskType .equals(Task.TASK_TYPE_WEIGHT) ){
            String body = task.getBody();
            if(StringHelper.isNotEmpty(body)) {
                String waybillCode = null;
                try {
                    JSONObject jsonobject = JSONObject.fromObject(body.substring(1,body.length()-1));
                    waybillCode = (String)jsonobject.get("waybillCode");
                } catch (Exception e) {
                    return null;
                }
                return waybillCode;
            }
        }
        return null;
    }
}
