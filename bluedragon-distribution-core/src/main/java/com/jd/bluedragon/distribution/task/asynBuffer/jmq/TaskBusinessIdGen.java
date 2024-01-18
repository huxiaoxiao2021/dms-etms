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
    private static final Integer BUSINESS_ID_LENGTH = 16;

    /**
     * 生成task对应的业务id
     */
    @Override
    public String genId(Task task) {
        String businessId = "";
        try {
            businessId = getBussinessId(task);
        }catch (Exception e){
            businessId = task.getType()+DELIMITER+task.getKeyword1();
        }
        if(!StringHelper.isNotEmpty(businessId)) {
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
            String boxCode= task.getBoxCode().trim();
            if(boxCode.length()>BUSINESS_ID_LENGTH){
                boxCode = boxCode.substring(boxCode.length()-BUSINESS_ID_LENGTH);
            }
            return boxCode;
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
                return data[data.length-1];//批次号最后的串 334044-908-20180309142625076
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

        if("task_send".equalsIgnoreCase(tableName) && taskType.equals(Task.TASK_TYPE_SEND_DELIVERY)
                && (keyword1.equals("10")|| keyword1.equals("11"))){
            if(StringHelper.isNotEmpty(task.getBoxCode())) {

                return keyword1+task.getBoxCode();
            }
        }

        //task_sorting 和 task_inspeciton
        if("task_sorting".equalsIgnoreCase(tableName) || "task_inspection".equalsIgnoreCase(tableName)){
            String keyWord2 = task.getKeyword2().trim();
            if(keyWord2.length()>BUSINESS_ID_LENGTH){
                keyWord2 = keyWord2.substring(keyWord2.length()-BUSINESS_ID_LENGTH);
            }
            return keyWord2;
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

        if (Task.TASK_TYPE_DELIVERY_ASYNC.equals(taskType)) {
            return task.getKeyword2();
        }
        if (Task.TASK_TYPE_DELIVERY_ASYNC_V2.equals(taskType)) {
            return task.getKeyword2();
        }

        return null;
    }
}
