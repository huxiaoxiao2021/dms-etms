package com.jd.bluedragon.distribution.worker.delivery;

import com.jd.bluedragon.distribution.failqueue.domain.DealData_SendDatail;
import com.jd.bluedragon.distribution.framework.DBSingleScheduler;
import com.jd.bluedragon.distribution.send.dao.SendDatailReadDao;
import com.jd.bluedragon.distribution.send.domain.SendDetail;
import com.jd.bluedragon.distribution.task.domain.Task;
import com.jd.bluedragon.utils.BusinessHelper;
import com.jd.bluedragon.utils.DateHelper;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.ump.profiler.CallerInfo;
import com.jd.ump.profiler.proxy.Profiler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by xumei3 on 2017/7/3.
 */
public class DeliveryToFinanceConvertTask extends DBSingleScheduler {
    private static final Logger log = LoggerFactory.getLogger(com.jd.bluedragon.distribution.worker.delivery.DeliveryToFinanceConvertTask.class);

    @Autowired
    private SendDatailReadDao sendDatailReadDao;

    private static final String BUSINESS_TYPE_ONE = "10";
    private static final String BUSINESS_TYPE_THR = "30";

    @Override
    protected boolean executeSingleTask(Task task, String ownSign) throws Exception {
        try {
            String busiType = task.getKeyword1();

            ArrayList<String> sendCodeList = new ArrayList<String>();
            sendCodeList.add(task.getBody());
            List<SendDetail> sendDetailList = new ArrayList<SendDetail>();

            if(busiType.equals(BUSINESS_TYPE_ONE)){
                sendDetailList = sendDatailReadDao.querySendDetailBySendCodes_SELF(sendCodeList);
            }else if(busiType.equals(BUSINESS_TYPE_THR)){
                sendDetailList = sendDatailReadDao.querySendDetailBySendCodes_3PL(sendCodeList);
            }else{
                log.warn("[DeliveryToFinanceConvertTak]匹配businessType失败,读取的busiType = {}", busiType);
            }
            if(sendDetailList == null || sendDetailList.size()<1){
                CallerInfo info = Profiler.registerInfo("delivery_to_finance_convert_task.execute_single_task", false, false);

                Profiler.businessAlarm("delivery_to_finance_convert_task.execute_single_task",
                        "从send_d表查询到的数据为空，批次号为：" + task.getBody());

                //加入监控结束
                Profiler.registerInfoEnd(info);
                return false;
            }
            for (SendDetail sendD : sendDetailList) {
                //转换成DealData_SendDatail对象，写入表
                Long primaryKey = sendD.getSendDId();
                String waybillCode = sendD.getWaybillCode();
                int sortingCenterId = sendD.getCreateSiteCode();
                int targetSiteId = sendD.getReceiveSiteCode();
                String deliveryTime = DateHelper.formatDateTime(sendD.getUpdateTime());
                String sortBatchNo = sendD.getSendCode();

                DealData_SendDatail tmpdata = new DealData_SendDatail(primaryKey, waybillCode,
                        sortingCenterId, targetSiteId, deliveryTime, sortBatchNo);

                //生成Task
                Task task_to_finance = new Task();
                task_to_finance.setCreateSiteCode(tmpdata.getSortingCenterId());
                task_to_finance.setReceiveSiteCode(tmpdata.getTargetSiteId());
                task_to_finance.setBoxCode(sendD.getBoxCode());
                task_to_finance.setKeyword1(tmpdata.getPrimaryKey().toString());  //send_id
                task_to_finance.setKeyword2(tmpdata.getWaybillCode() + "/" + sendD.getSendType()); //运单号/businessType
                task_to_finance.setTableName(Task.TABLE_NAME_DELIVERY_TO_FINANCE);
                task_to_finance.setBody(JsonHelper.toJson(tmpdata));
                task_to_finance.setType(Task.TASK_TYPE_DELIVERY_TO_FINANCE);
                task_to_finance.setOwnSign(BusinessHelper.getOwnSign());

                taskService.add(task_to_finance);
            }
        } catch (Exception e) {
            log.error("DeliveryToFinanceConvert任务转换失败：task id is {} task type is {}",task.getId(),task.getType(),e);
            return Boolean.FALSE;
        }
        return true;
    }

    public List<Task> selectTasks(String arg0, int queueNum, List<String> queryCondition, int fetchNum) throws Exception {
        if (queryCondition.size() == 0) {
            return Collections.emptyList();
        }

        List<Task> tasks = new ArrayList<Task>();
        try {

            if (queryCondition.size() != queueNum) {
                fetchNum = fetchNum * queueNum / queryCondition.size();
            }

            List<Task> Tasks = taskService.findDeliveryToFinanceConvertTasks(this.type, fetchNum,queryCondition);
            for (Task task : Tasks) {
                if (!isMyTask(queueNum, task.getId(), queryCondition)) {
                    continue;
                }
                tasks.add(task);
            }
        } catch (Exception e) {
            this.log.error("出现异常， 异常信息为：{}" , e.getMessage(), e);
        }
        return tasks;
    }
}
