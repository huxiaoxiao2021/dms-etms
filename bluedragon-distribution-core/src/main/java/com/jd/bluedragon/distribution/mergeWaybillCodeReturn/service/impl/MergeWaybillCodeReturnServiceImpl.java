package com.jd.bluedragon.distribution.mergeWaybillCodeReturn.service.impl;

import com.alibaba.fastjson.JSON;
import com.jd.bluedragon.distribution.mergeWaybillCodeReturn.domain.MergeWaybillMessage;
import com.jd.bluedragon.distribution.mergeWaybillCodeReturn.service.MergeWaybillCodeReturnService;
import com.jd.bluedragon.distribution.task.domain.Task;
import com.jd.bluedragon.distribution.task.service.TaskService;
import com.jd.bluedragon.distribution.waybill.domain.WaybillStatus;
import com.jd.bluedragon.utils.BusinessHelper;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.ldop.center.api.reverse.dto.ReturnSignatureMessageDTO;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.lang.reflect.Field;
import java.util.Date;
import java.util.List;


/**
 * @ClassName: MergeWaybillCodeReturnServiceImpl
 * @Description: 签单返回合单
 * @author: hujiping
 * @date: 2018/9/17 20:32
 */
@Service("MergeWaybillCodeReturnService")
public class MergeWaybillCodeReturnServiceImpl implements MergeWaybillCodeReturnService{

    private final Log logger = LogFactory.getLog(this.getClass());

    @Autowired
    private TaskService taskService;

    /**
     * 比较两个是否相同
     * @param data
     * @param secondData
     * @return
     */
    @Override
    public Boolean compare(ReturnSignatureMessageDTO data, ReturnSignatureMessageDTO secondData) {

        try{
            Class<? extends ReturnSignatureMessageDTO> dataClass = data.getClass();
            Class<? extends ReturnSignatureMessageDTO> secondDataClass = secondData.getClass();
            Field[] dataFields = dataClass.getDeclaredFields();
            Field[] secondDataFields = secondDataClass.getDeclaredFields();
            for(int i=0;i<dataFields.length;i++){
                for(int j=0;j<secondDataFields.length;j++){
                    //属性名相同
                    if(dataFields[i].getName().equals(secondDataFields[j].getName())){
                        //属性值不同
                        if(!compareTwo(dataFields[i].get(data),secondDataFields[j].get(secondData))){
                            return false;
                        }
                    }
                }
            }
        }catch (Exception e){
            this.logger.error("比较两对象出错",e);
            return false;
        }
        return true;
    }

    /**
     * 判断是否相同
     * @param data
     * @param secondData
     * @return
     */
    @Override
    public Boolean compareWith(ReturnSignatureMessageDTO data, ReturnSignatureMessageDTO secondData) {
        if (data.getRealName().equals(secondData.getRealName())) {
            if (data.getPhone().equals(secondData.getPhone())) {
                if (data.getAddress().getAddress().equals(secondData.getAddress().getAddress())) {
                    if (data.getTraderCode().equals(secondData.getTraderCode())) {
                        if (data.getTraderName().equals(secondData.getTraderName())) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    /**
     * 发全程跟踪
     * @param message
     */
    @Override
    public void sendTrace(MergeWaybillMessage message) {
        //新单号发全程跟踪
        toTask(message);
        //旧单号发全程跟踪
        List<String> WaybillCodeList = message.getWaybillCodeList();
        for(String waybillCode : WaybillCodeList){
            toTask(message, waybillCode,message.getNewWaybillCode());
        }
    }

    private void toTask(MergeWaybillMessage message) {
        Task tTask = new Task();
        tTask.setKeyword1(message.getNewWaybillCode());
        tTask.setKeyword2(String.valueOf(WaybillStatus.WAYBILL_STATUS_MERGE_WAYBILLCODE_RETURN));
        tTask.setCreateSiteCode(message.getSiteCode());
        tTask.setCreateTime(new Date(message.getOperateTime()));
//        tTask.setReceiveSiteCode(0);
        tTask.setType(Task.TASK_TYPE_WAYBILL_TRACK);
        tTask.setTableName(Task.getTableName(Task.TASK_TYPE_WAYBILL_TRACK));
        tTask.setSequenceName(Task.getSequenceName(Task.TABLE_NAME_POP));
        String ownSign = BusinessHelper.getOwnSign();
        tTask.setOwnSign(ownSign);

        WaybillStatus status=new WaybillStatus();
        status.setOperateType(WaybillStatus.WAYBILL_STATUS_MERGE_WAYBILLCODE_RETURN);
        status.setWaybillCode(message.getNewWaybillCode());
        status.setOperateTime(new Date(message.getOperateTime()));
        status.setOperator(message.getOperatorName());
        status.setOperatorId(message.getOperatorUserId());
        status.setRemark(JSON.toJSONString(message.getWaybillCodeList()));
        status.setCreateSiteCode(message.getSiteCode());
        status.setCreateSiteName(message.getSiteName());
//        status.setPackageCode(domain.getOldCode());
        tTask.setBody(JsonHelper.toJson(status));
        taskService.add(tTask);
    }

    private void toTask(MergeWaybillMessage message, String waybillCode,String newWaybillCode) {
        Task tTask = new Task();
        tTask.setKeyword1(waybillCode);
        tTask.setKeyword2(String.valueOf(WaybillStatus.WAYBILL_STATUS_MERGE_WAYBILLCODE_RETURN));
        tTask.setCreateSiteCode(message.getSiteCode());
        tTask.setCreateTime(new Date(message.getOperateTime()));
//        tTask.setReceiveSiteCode(0);
        tTask.setType(Task.TASK_TYPE_WAYBILL_TRACK);
        tTask.setTableName(Task.getTableName(Task.TASK_TYPE_WAYBILL_TRACK));
        tTask.setSequenceName(Task.getSequenceName(Task.TABLE_NAME_POP));
        String ownSign = BusinessHelper.getOwnSign();
        tTask.setOwnSign(ownSign);

        WaybillStatus status=new WaybillStatus();
        status.setOperateType(WaybillStatus.WAYBILL_STATUS_MERGE_WAYBILLCODE_RETURN);
        status.setWaybillCode(waybillCode);
        status.setSendCode(newWaybillCode);//将新单号存到sendCode字段中
        status.setOperateTime(new Date(message.getOperateTime()));
        status.setOperator(message.getOperatorName());
        status.setOperatorId(message.getOperatorUserId());
        status.setRemark("签单返回合单");
        status.setCreateSiteCode(message.getSiteCode());
        status.setCreateSiteName(message.getSiteName());
//        status.setPackageCode(domain.getOldCode());
        tTask.setBody(JsonHelper.toJson(status));
        taskService.add(tTask);
    }


    /**
     * 对比两个数据是否内容相同
     * @param object1
     * @param object2
     * @return
     */
    public static boolean compareTwo(Object object1, Object object2) {

        if (object1 == null && object2 == null) {
            return true;
        }
        if (object1 == "" && object2 == null) {
            return true;
        }
        if (object1 == null && object2 == "") {
            return true;
         }
        if (object1 == null && object2 != null) {
            return false;
        }
        if (object1.equals(object2)) {
            return true;
        }
        return false;
    }


}
