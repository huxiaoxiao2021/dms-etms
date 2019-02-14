package com.jd.bluedragon.distribution.failqueue.service;

import com.jd.bluedragon.distribution.api.JdResponse;
import com.jd.bluedragon.distribution.client.Departure3PLDataClient;
import com.jd.bluedragon.distribution.departure.domain.Departure;
import com.jd.bluedragon.distribution.failqueue.dao.TaskFailQueueDao;
import com.jd.bluedragon.distribution.failqueue.domain.DealData_Departure;
import com.jd.bluedragon.distribution.failqueue.domain.DealData_Departure_3PL;
import com.jd.bluedragon.distribution.failqueue.domain.DealData_SendDatail;
import com.jd.bluedragon.distribution.failqueue.domain.TaskFailQueue;
import com.jd.bluedragon.distribution.send.dao.SendDatailReadDao;
import com.jd.bluedragon.distribution.send.domain.SendDetail;
import com.jd.bluedragon.distribution.send.domain.SendM;
import com.jd.bluedragon.utils.DateHelper;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.etms.finance.wss.WaybillDataServiceWS;
import com.jd.etms.finance.wss.pojo.ResponseMessage;
import com.jd.etms.finance.wss.pojo.SortingCar;
import com.jd.etms.finance.wss.pojo.SortingOrder;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class FailQueueServiceImpl implements IFailQueueService {

    private final static String PART = "/";

    private final Log logger = LogFactory.getLog(this.getClass());

    @Autowired
    WaybillDataServiceWS waybillDataServiceWS;

    @Autowired
    TaskFailQueueDao taskFailQueueDao;

    @Autowired
    SendDatailReadDao sendDatailReadDao;

    // public List<SendDetail> querySendDatail(List<String> param){
    // return taskFailQueueDao.querySendDatail(param);
    // }

    public List<TaskFailQueue> query(Map<String, Object> param) {
        return taskFailQueueDao.query(param);
    }

    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public void departureNewData(Departure departure, long shieldsCarId,
                                 boolean pushDeparture) {
        List<SendM> sendMs = departure.getSendMs();
        StringBuffer sortBatchNosb = new StringBuffer();
        /* 通过批次号查询发货数据，饭后拼装后，放入task_failqueue */
        ArrayList<String> sendCodeAl = new ArrayList<String>();
        for (SendM sendm : sendMs) {
            String sendCode = sendm.getSendCode();
            sortBatchNosb.append(sendCode + ",");
            sendCodeAl.add(sendCode);
        }
        sendCodeNewData(sendCodeAl, IFailQueueService.DMS_SEND_SELF);
        logger.info("发车对应的发货数据，准备推送财务，SendCode：" + sortBatchNosb.toString());
        if (!pushDeparture) {
            /**
             * 如果不需要推发车就返回
             */
            return;
        }

        DealData_Departure dealData = new DealData_Departure();

        String sortBatchNo = sortBatchNosb.toString();
        sortBatchNo = sortBatchNosb != null && sortBatchNo.length() > 0 ? sortBatchNo
                .substring(0, sortBatchNo.length() - 1) : "";

        dealData.setSortingCenterId(sendMs.get(0).getCreateSiteCode());// 分拣中心
        dealData.setTargetSiteId(sendMs.get(0).getReceiveSiteCode());// 目标站点
        dealData.setSortCarId(shieldsCarId);// 发车批次
        dealData.setCarrierId(departure.getSendUserCode());// 承运商编号
        dealData.setSortCarTime(DateHelper.formatDateTime(new Date()));// 发货批次好
        dealData.setSortBatchNo(sortBatchNo);
        dealData.setWeight(departure.getWeight());// 重量
        dealData.setVolume(departure.getVolume());// 体积

        TaskFailQueue tmpdata = DataTranTool.transTaskFailQueue_Departure(
                dealData, IFailQueueService.DEPARTURE_TYPE);
        if (taskFailQueueDao.update(TaskFailQueueDao.namespace, tmpdata) == 0) {
            taskFailQueueDao.add(TaskFailQueueDao.namespace, tmpdata);
        }

    }

    @Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
    public void failData(List<TaskFailQueue> list) {
        List<TaskFailQueue> sendDatailAl = new ArrayList<TaskFailQueue>();
        List<TaskFailQueue> sendDatailAl_batch = new ArrayList<TaskFailQueue>();
        List<TaskFailQueue> departureAl = new ArrayList<TaskFailQueue>();
        List<TaskFailQueue> departure3Pl = new ArrayList<TaskFailQueue>();

        for (TaskFailQueue task : list) {
            if (task.getBusiType().equals(IFailQueueService.SEND_TYPE_SELF)
                    || task.getBusiType().equals(
                    IFailQueueService.SEND_TYPE_3PL)) {
                sendDatailAl.add(task);
            } else if (task.getBusiType().equals(
                    IFailQueueService.DEPARTURE_TYPE)) {
                departureAl.add(task);
            } else if (task.getBusiType().equals(
                    IFailQueueService.SEND_TYPE_BATCH)) {
                sendDatailAl_batch.add(task);
            } else if (task.getBusiType().equals(
                    IFailQueueService.DEPARTURE_TYPE_3PL)) {
                departure3Pl.add(task);
            } else {
                logger.error("TaskFailQueue错误的类型数据 ID:" + task.getFailqueueId()
                        + " buistype:" + task.getBusiType());
            }
        }

        sendDatailFailData(sendDatailAl);
        sendDatailBatchData(sendDatailAl_batch);
        departureFailData(departureAl);
        departure3PLData(departure3Pl);// 支线发车并且三方承运商
    }

    /**
     * @see com.jd.bluedragon.distribution.failqueue.service.IFailQueueService#departure3PLData(java.util.List)
     */
    @Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
    public void departure3PLData(List<TaskFailQueue> list) {
        if (list.size() == 0) {
            /**数据为空不处理*/
            return;
        }
        try {
            logger.info("支线三方发车数据推送财务");

            Set<Long> failList = new HashSet<Long>();
            //发送数据到3PL
            for (TaskFailQueue tfq : list) {
                DealData_Departure_3PL tmpdealData = JsonHelper.fromJson(tfq.getBody(), DealData_Departure_3PL.class);

                int returnCode = Departure3PLDataClient.departure3PLData(tmpdealData);
                //将失败的加入失败列表
                if (JdResponse.CODE_OK != returnCode)
                    failList.add(tfq.getBusiId());
            }

			/*处理状态*/
            List<TaskFailQueue> dealResult_Success = new ArrayList<TaskFailQueue>();
            List<TaskFailQueue> dealResult_Fail = new ArrayList<TaskFailQueue>();

            for (TaskFailQueue faildata : list) {
                if (failList.contains(faildata.getBusiId())) {
                    dealResult_Fail.add(faildata);
                } else {
                    dealResult_Success.add(faildata);
                }
            }

            logger.info("处理成功数据 数据量:" + dealResult_Success.size() + " 类型:" + IFailQueueService.DEPARTURE_TYPE);
            udpateSuccessOrFail_TaskFailQueue(dealResult_Success, true);
            logger.info("处理失败数据 数据量:" + dealResult_Fail.size() + " 类型:" + IFailQueueService.DEPARTURE_TYPE);
            udpateSuccessOrFail_TaskFailQueue(dealResult_Fail, false);

        } catch (Exception e) {
            logger.error("发送支线发车三方承运商发车数据到3PL状态异常", e);
            udpateSuccessOrFail_TaskFailQueue(list, false);
            return;
        }

        logger.info("处理完毕");
    }

    private void departureFailData(List<TaskFailQueue> list) {
        if (list.size() == 0) {
            /** 数据为空不处理 */
            return;
        }
        try {
            logger.info("失败队列发货数据推送财务");
            logger.info("TaskFailQueue 预转换  DealData");
            List<DealData_Departure> dealal = DataTranTool
                    .transTaskFailQueueToDealData_Departure(list);
            logger.info("DealData 预转换 sortingCarAl ");
            List<com.jd.etms.finance.wss.pojo.SortingCar> sortingCarAl = DataTranTool
                    .transDealDataDepartureToFinanceData(dealal);

            List<String> failList = null;
            try {
                failList = pushDepartureWss(sortingCarAl);
            } catch (Exception e) {
                logger.error("调用waybillDataServiceWS.sendSortingOrder", e);
                udpateSuccessOrFail_TaskFailQueue(list, false);
                return;
            }

			/* 处理状态 */
            List<TaskFailQueue> dealResult_Success = new ArrayList<TaskFailQueue>();
            List<TaskFailQueue> dealResult_Fail = new ArrayList<TaskFailQueue>();

            for (TaskFailQueue faildata : list) {
                if (failList.contains(String.valueOf(faildata.getBusiId()))) {
                    dealResult_Fail.add(faildata);
                } else {
                    dealResult_Success.add(faildata);
                }
            }

            logger.info("处理成功数据 数据量:" + dealResult_Success.size() + " 类型:"
                    + IFailQueueService.DEPARTURE_TYPE);
            udpateSuccessOrFail_TaskFailQueue(dealResult_Success, true);
            logger.info("处理失败数据 数据量:" + dealResult_Fail.size() + " 类型:"
                    + IFailQueueService.DEPARTURE_TYPE);
            udpateSuccessOrFail_TaskFailQueue(dealResult_Fail, false);
        } catch (Exception e) {
            logger.error("处理失败队列数据状态异常", e);
            udpateSuccessOrFail_TaskFailQueue(list, false);
            return;
        }
        logger.info("处理完毕");
    }

    private void sendDatailFailData(List<TaskFailQueue> list) {
        if (list.size() == 0) {
            /** 数据为空不处理 */
            return;
        }
        try {
            logger.info("失败队列发货数据推送财务");
            logger.info("TaskFailQueue 预转换  DealData");
            List<DealData_SendDatail> dealal = DataTranTool
                    .transTaskFailQueueToDealData_SendDatail(list);
            logger.info("DealData 预转换 SortingOrder");
            ArrayList<com.jd.etms.finance.wss.pojo.SortingOrder> sendDataAl = DataTranTool
                    .transDealDataSendDatailToFinanceData(dealal);

            List<String> failList = null;
            try {
                failList = pushSendDatailhWss(sendDataAl);
            } catch (Exception e) {
                udpateSuccessOrFail_TaskFailQueue(list, false);
                logger.error("调用waybillDataServiceWS.sendSortingOrder", e);
                return;
            }

			/* 处理状态 */
            List<TaskFailQueue> dealResult_Success = new ArrayList<TaskFailQueue>();
            List<TaskFailQueue> dealResult_Fail = new ArrayList<TaskFailQueue>();
            HashMap<Long, TaskFailQueue> failmap = new HashMap<Long, TaskFailQueue>();
            logger.info("准备区分成功数据和失败数据");

            for (TaskFailQueue taskFailQueue : list) {
                failmap.put(taskFailQueue.getBusiId(), taskFailQueue);
            }

            for (DealData_SendDatail dealData : dealal) {
                /* 如果运单号包含在失败列表中，则添加到失败处理集合中 */
                if (failList.contains(dealData.getWaybillCode())) {
                    dealResult_Fail.add(failmap.get(dealData.getPrimaryKey()));
                } else {
                    dealResult_Success
                            .add(failmap.get(dealData.getPrimaryKey()));
                }
            }
            logger.info("处理成功数据 数据量:" + dealResult_Success.size());
            udpateSuccessOrFail_TaskFailQueue(dealResult_Success, true);
            logger.info("处理失败数据 数据量:" + dealResult_Fail.size());
            udpateSuccessOrFail_TaskFailQueue(dealResult_Fail, false);
        } catch (Exception e) {
            logger.error("处理失败队列数据状态异常", e);
            udpateSuccessOrFail_TaskFailQueue(list, false);
            return;
        }
        logger.info("处理完毕");
    }

    private List<String> pushDepartureWss(List<SortingCar> sortingCarAl)
            throws Exception {
        /* 推送WSS */
        logger.info("调用waybillDataServiceWS.sendSortingCar推送数据");
        ResponseMessage responseMessage = waybillDataServiceWS
                .sendSortingCar(sortingCarAl);
        /* 处理返回数据 */
        logger.info("获得WSS执行结果处理状态,WSS结果 result="
                + responseMessage.getIsSuccess());
        return responseMessage.getWbcList();
    }

    private List<String> pushSendDatailhWss(ArrayList<SortingOrder> sendDataAl)
            throws Exception {
        /* 推送WSS */
        logger.info("调用waybillDataServiceWS.sendSortingOrder推送数据");
        ResponseMessage responseMessage = waybillDataServiceWS
                .sendSortingOrder(sendDataAl);
        /* 处理返回数据 */
        logger.info("获得WSS执行结果处理状态,WSS结果 result="
                + responseMessage.getIsSuccess());
        return responseMessage.getWbcList();
    }

    /**
     * TaskFailQueue处理完成 更新状态（成功或者失败，调用不同的方法）
     *
     * @param failQueueList 业务类型 三方发货，自营发货，发车
     * @param isSuccess     是否执行成功
     */
    private void udpateSuccessOrFail_TaskFailQueue(
            List<TaskFailQueue> failQueueList, boolean isSuccess) {
        /**
         * 发货的数据统一处理
         */
        if (failQueueList.size() == 0) {
            /* 数据为空，不进行处理 */
            return;
        }

        if (isSuccess) {
            for (TaskFailQueue failData : failQueueList) {
                taskFailQueueDao.updateSuccess(failData.getFailqueueId());
            }
        } else {
            for (TaskFailQueue failData : failQueueList) {
                taskFailQueueDao.updateFail(failData.getFailqueueId());
            }
        }
    }

    @Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
    public void sendDatailBatchData(List<TaskFailQueue> sendDatailAl_batch) {
        /* 组织批次号 */
        try {
            if (sendDatailAl_batch.size() == 0) {
                return;
            }
            ArrayList<String> sendCodeAl_self = new ArrayList<String>();
            ArrayList<String> sendCodeAl_3pl = new ArrayList<String>();
            for (TaskFailQueue sendm : sendDatailAl_batch) {
                String[] params = sendm.getBody().split(PART);
                if (params[1]
                        .equals(IFailQueueService.DMS_SEND_SELF.toString())) {
                    sendCodeAl_self.add(params[0]);
                } else {
                    sendCodeAl_3pl.add(params[0]);
                }
            }
			/* 根据批次号查询发货信息，并记录发货信息主键 */
            ArrayList<Long> sendidAL = new ArrayList<Long>();
            ArrayList<SendDetail> senddAL = new ArrayList<SendDetail>();
            if (sendCodeAl_self.size() > 0) {
                List<SendDetail> senddAL_self = sendDatailReadDao.querySendDetailBySendCodes_SELF(sendCodeAl_self);
                senddAL.addAll(senddAL_self);
            }

            if (sendCodeAl_3pl.size() > 0) {
                List<SendDetail> senddAL_3pl = sendDatailReadDao.querySendDetailBySendCodes_3PL(sendCodeAl_3pl);
                senddAL.addAll(senddAL_3pl);
            }

            for (SendDetail sendd : senddAL) {
                sendidAL.add(sendd.getSendDId());
            }
			/* 查询taskFailqueue看那些数据需要更新，那些需要新增 */
            List<Long> allExist = queryTaskFailQueueBySendID(sendidAL);
            for (SendDetail datail : senddAL) {
                TaskFailQueue tmp = DataTranTool
                        .transSendDataToDealDate(datail);
                if (tmp.getBusiType().equals(IFailQueueService.ERROR_TYPE)) {
                    logger.error("错误的SendDatail sendType:"
                            + datail.getSendType() + " [" + datail.getSendDId()
                            + "]");
                } else {
                    if (!allExist.contains(datail.getSendDId())) {
                        taskFailQueueDao.add(TaskFailQueueDao.namespace, tmp);
                    }
                }
            }
            udpateSuccessOrFail_TaskFailQueue(sendDatailAl_batch, true);
        } catch (Exception e) {
            logger.error("处理批次号队列数据状态异常", e);
            udpateSuccessOrFail_TaskFailQueue(sendDatailAl_batch, false);
        }
    }

    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public void sendCodeNewData(String sendCode, Integer type) {
        ArrayList<String> al = new ArrayList<String>();
        al.add(sendCode);

        sendCodeNewData(al, type);
    }

    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public void sendCodeNewData(List<String> sendCodeAl, Integer type) {
        for (String sendCode : sendCodeAl) {
            TaskFailQueue tmp = DataTranTool
                    .transTaskFailQueue_SendCode(sendCode + PART + type);
            taskFailQueueDao.add(TaskFailQueueDao.namespace, tmp);
        }
    }

    /**
     * 查询所有存在的send_id
     *
     * @param sendidAL
     * @return
     */
    private List<Long> queryTaskFailQueueBySendID(List<Long> sendidAL) {
        ArrayList<Long> allExist = new ArrayList<Long>();
        List<Long>[] sendds = splitList(sendidAL);
        for (List<Long> param : sendds) {
            List<Long> result = taskFailQueueDao.queryBatchByBusiId(param);
            allExist.addAll(result);
        }

        return allExist;
    }

    private List<Long>[] splitList(List<Long> transresult) {
        int BATCH_NUM = 900;
        List<List<Long>> splitList = new ArrayList<List<Long>>();
        for (int i = 0; i < transresult.size(); i += BATCH_NUM) {
            int size = i + BATCH_NUM > transresult.size() ? transresult.size()
                    : i + BATCH_NUM;
            List<Long> tmp = (List<Long>) transresult.subList(i, size);
            splitList.add(tmp);
        }

        return splitList.toArray(new List[0]);
    }

    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public void lock(List<TaskFailQueue> tasks) {
        for (TaskFailQueue task : tasks) {
            taskFailQueueDao.updateLock(task.getFailqueueId());
        }
    }
}
