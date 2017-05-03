package com.jd.bluedragon.distribution.asynbuffer.service;

import com.google.gson.reflect.TypeToken;
import com.jd.bluedragon.distribution.api.request.InspectionRequest;
import com.jd.bluedragon.distribution.departure.service.DepartureService;
import com.jd.bluedragon.distribution.framework.AbstractTaskExecute;
import com.jd.bluedragon.distribution.inspection.exception.InspectionException;
import com.jd.bluedragon.distribution.partnerWaybill.service.PartnerWaybillService;
import com.jd.bluedragon.distribution.receive.service.ReceiveService;
import com.jd.bluedragon.distribution.receiveInspectionExc.service.ShieldsErrorService;
import com.jd.bluedragon.distribution.reverse.service.ReverseSendService;
import com.jd.bluedragon.distribution.seal.service.SealBoxService;
import com.jd.bluedragon.distribution.send.service.DeliveryService;
import com.jd.bluedragon.distribution.send.service.ReverseDeliveryService;
import com.jd.bluedragon.distribution.sorting.service.SortingReturnService;
import com.jd.bluedragon.distribution.sorting.service.SortingService;
import com.jd.bluedragon.distribution.task.domain.Task;
import com.jd.bluedragon.utils.JsonHelper;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by xumei3 on 2017/4/17.
 */
public class AsynBufferServiceImpl implements AsynBufferService {
    private final Log logger = LogFactory.getLog(this.getClass());

    private static final String SPLIT_CHAR = "$";
    private static final Type LIST_INSPECTIONREQUEST_TYPE =
            new TypeToken<List<InspectionRequest>>() {
    }.getType();

    //分拣中心收货
    @Autowired
    private ReceiveService receiveService;

    public boolean receiveTaskProcess(Task task)
            throws Exception {

        try {
            receiveService.doReceiveing(receiveService.taskToRecieve(task));
        } catch (Exception e) {
            logger.error(
                    "处理收货任务失败[taskId=" + task.getId() + "]异常信息为："
                            + e.getMessage(), e);
            return false;
        }
        return true;
    }

    //分拣中心验货
    @Qualifier("inspectionTaskExecute")
    @Autowired()
    private AbstractTaskExecute taskExecute;

    public boolean inspectionTaskProcess(Task task)
            throws Exception {
        try {
            logger.info("验货work开始，task_id: " + task.getId());
            if (task == null || StringUtils.isBlank(task.getBody())) {
                return true;
            }
            /**
             * 此处理为消除列表情况，最早任务保存的是数组，此处拆为单条，以防万一
             */
            List<InspectionRequest> middleRequests = JsonHelper.fromJsonUseGson(task.getBody(), LIST_INSPECTIONREQUEST_TYPE);
            if (null == middleRequests || middleRequests.size() == 0) {
                return true;
            }
            Task domain = new Task();

            for (InspectionRequest request : middleRequests) {
                domain.setBody(JsonHelper.toJson(request));
                taskExecute.execute(domain);
            }
        } catch (InspectionException inspectionEx) {
            StringBuilder sb = new StringBuilder("验货执行失败,已知异常");
            sb.append(inspectionEx.getMessage());
            sb.append(SPLIT_CHAR).append(task.getBoxCode());
            sb.append(SPLIT_CHAR).append(task.getKeyword1());
            sb.append(SPLIT_CHAR).append(task.getKeyword2());
            logger.warn(sb.toString());

            return false;
        } catch (Exception e) {
            logger.error("验货worker失败, task id: " + task.getId()
                    + ". 异常信息: " + e.getMessage(), e);
            return false;
        }
        return true;
    }

    //处理运单号关联包裹任务
    @Autowired
    private PartnerWaybillService partnerWaybillService;

    public boolean partnerWaybillTaskProcess(Task task)
            throws Exception {
        try {
            partnerWaybillService.doCreateWayBillCode(partnerWaybillService.doParse(task));
        } catch (Exception e) {
            return false;
        }
        return true;
    }


    //封签[封箱]异常任务
    @Autowired
    private ShieldsErrorService shieldsErrorService;

    public boolean shieldsBoxErrorTaskProcess(Task task)
            throws Exception {
        return shieldsErrorService.doAddShieldsError(shieldsErrorService.doParseShieldsBox(task));
    }


    //封签[封车]异常任务
    public boolean shieldsCarErrorTaskProcess(Task task) throws Exception {
        return shieldsErrorService.doAddShieldsError(shieldsErrorService.doParseShieldsCar(task));
    }


    //运单号关联包裹回传
    public boolean partnerWaybillSynchroTaskProcess(Task task) throws Exception {
        List<Task> taskList = new ArrayList<Task>();
        taskList.add(task);
        try {
            partnerWaybillService.doWayBillCodesProcessed(taskList);
        } catch (Exception e) {
            logger.error("处理运单号关联包裹数据时发生异常", e);
            return false;
        }
        return true;
    }

    //发货新老数据同步任务
    @Autowired
    private ReverseDeliveryService reverseService;

    //中转发货补全任务
    @Autowired
    private DeliveryService deliveryService;

    //回传周转箱项目任务

    //发货回传运单状态任务

    //tasktype=1300
    @Autowired
    private ReverseSendService reverseSendService;


    //支线发车推送运单任务
    @Autowired
    private DepartureService departureService;

    public boolean thirdDepartureTaskProcess(Task task)
            throws Exception {
        return departureService.sendThirdDepartureInfoToTMS(task);
    }


    //封箱解封箱Redis任务
    @Autowired
    private SealBoxService sealService;

    public boolean sealBoxTaskProcess(Task task) throws Exception {
        sealService.doSealBox(task);
        return true;
    }

    //分拣退货Redis任务

    @Autowired
    private SortingReturnService sortingReturnService;

    public boolean sortingReturnTaskProcess(Task task) throws Exception {
        sortingReturnService.doSortingReturn(task);
        return true;
    }

    //分拣
    @Autowired
    private SortingService sortingService;

    public boolean sortingTaskProcess(Task task) throws Exception {
        boolean result = false;
        try {
            this.logger.info("task id is " + task.getId());
            result = this.sortingService.doSorting(task);
        } catch (Exception e) {
            StringBuilder builder = new StringBuilder("task id is");
            builder.append(task.getId());
            builder.append(SPLIT_CHAR).append(task.getBoxCode());
            builder.append(SPLIT_CHAR).append(task.getKeyword1());
            builder.append(SPLIT_CHAR).append(task.getKeyword2());
            this.logger.error(builder.toString());
            this.logger.error("处理分拣任务发生异常，异常信息为：" + e.getMessage(), e);
            return Boolean.FALSE;
        }
        return result;
    }


    //统一处理task_send入口，根据keyword1对应具体的方法
    public boolean taskSendProcess(Task task) throws Exception {
        String keyword1 = task.getKeyword1();
        if (keyword1.equals("1")||keyword1.equals("2")) {
            //发货回传运单状态任务
            return deliveryService.findSendwaybillMessage(task);
        } else if (keyword1.equals("3")) {
            //发货新老数据同步任务
            return reverseService.findsendMToReverse(task);
        } else if (keyword1.equals("4")) {
            return reverseSendService.findSendwaybillMessage(task);

        } else if (keyword1.equals("5")) {
            //中转发货补全任务
            return deliveryService.findTransitSend(task);

        } else {
            //没有找到对应的方法，提供报错信息
            this.logger.error("task id is " + task.getId()+"can not find process method");
        }
        return false;
    }
}
