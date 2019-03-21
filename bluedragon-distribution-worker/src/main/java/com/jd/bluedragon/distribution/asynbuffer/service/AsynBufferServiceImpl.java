package com.jd.bluedragon.distribution.asynbuffer.service;

import com.google.gson.reflect.TypeToken;
import com.jd.bluedragon.Constants;
import com.jd.bluedragon.common.utils.ProfilerHelper;
import com.jd.bluedragon.distribution.api.request.InspectionRequest;
import com.jd.bluedragon.distribution.auto.domain.UploadData;
import com.jd.bluedragon.distribution.auto.service.ScannerFrameDispatchService;
import com.jd.bluedragon.distribution.departure.service.DepartureService;
import com.jd.bluedragon.distribution.framework.AbstractTaskExecute;
import com.jd.bluedragon.distribution.inspection.exception.InspectionException;
import com.jd.bluedragon.distribution.inspection.exception.WayBillCodeIllegalException;
import com.jd.bluedragon.distribution.inspection.service.InspectionService;
import com.jd.bluedragon.distribution.partnerWaybill.service.PartnerWaybillService;
import com.jd.bluedragon.distribution.receive.service.impl.ReceiveTaskExecutor;
import com.jd.bluedragon.distribution.receiveInspectionExc.service.ShieldsErrorService;
import com.jd.bluedragon.distribution.reverse.service.ReverseSendService;
import com.jd.bluedragon.distribution.seal.service.SealBoxService;
import com.jd.bluedragon.distribution.send.service.DeliveryService;
import com.jd.bluedragon.distribution.send.service.ReverseDeliveryService;
import com.jd.bluedragon.distribution.send.utils.SendBizSourceEnum;
import com.jd.bluedragon.distribution.sorting.service.SortingReturnService;
import com.jd.bluedragon.distribution.sorting.service.SortingService;
import com.jd.bluedragon.distribution.task.domain.DmsTaskExecutor;
import com.jd.bluedragon.distribution.task.domain.Task;
import com.jd.bluedragon.distribution.weight.service.WeightService;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.ump.profiler.CallerInfo;
import com.jd.ump.profiler.proxy.Profiler;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.lang.reflect.Type;
import java.util.ArrayList;
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
//    @Autowired
//    private ReceiveService receiveService;
    @Autowired
    private ReceiveTaskExecutor receiveTaskExecutor;
    

    public boolean receiveTaskProcess(Task task)
            throws Exception {
        try {
        	return receiveTaskExecutor.execute(task, task.getOwnSign());
        } catch (Exception e) {
            logger.error(
                    "处理收货任务失败[taskId=" + task.getId() + "]异常信息为："
                            + e.getMessage(), e);
            return false;
        }
    }

    //分拣中心验货
    @Qualifier("inspectionTaskExecute")
    @Autowired()
    private AbstractTaskExecute taskExecute;
    public boolean inspectionTaskProcess(Task task)
            throws Exception {
		CallerInfo callerInfo = ProfilerHelper.registerInfo("DmsWorker.Task.InspectionTask.execute",
				Constants.UMP_APP_NAME_DMSWORKER);
        try {
            logger.info("验货work开始，task_id: " + task.getId());
            if (task == null || StringUtils.isBlank(task.getBody())) {
                return true;
            }

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
        }catch (WayBillCodeIllegalException wayBillCodeIllegalEx){
            StringBuilder sb=new StringBuilder("验货执行失败,已知异常");
            sb.append(wayBillCodeIllegalEx.getMessage());
            sb.append(SPLIT_CHAR).append(task.getBoxCode());
            sb.append(SPLIT_CHAR).append(task.getKeyword1());
            sb.append(SPLIT_CHAR).append(task.getKeyword2());
            logger.warn(sb.toString());
            return false;
        }catch (Exception e) {
        	Profiler.functionError(callerInfo);
            logger.error("验货worker失败, task id: " + task.getId() + ". 异常信息: " + e.getMessage(), e);
            return false;
        }finally{
        	Profiler.registerInfoEnd(callerInfo);
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
        return sortingService.processTaskData(task);
    }

    //称重信息回传运单中心
    @Autowired
    private WeightService weightService;
    public boolean weightTaskProcess(Task task) throws Exception{
        boolean result = Boolean.FALSE;
        try {
            this.logger.info("task id is " + task.getId());
            result = this.weightService.doWeightTrack(task);
        } catch (Exception e) {
            this.logger.error("task id is" + task.getId());
            this.logger.error("处理称重回传任务发生异常，异常信息为：" + e.getMessage(), e);
            return Boolean.FALSE;
        }
        return result;
    }

    //统一处理task_send入口，根据keyword1对应具体的方法
    public boolean taskSendProcess(Task task) throws Exception {
        String keyword1 = task.getKeyword1().trim();
        if (keyword1.equals("1")){
            //发货回传运单状态任务
            return deliveryService.updatewaybillCodeMessage(task);
        } else if(keyword1.equals("2")){
            //回传周转箱号任务
            return  deliveryService.findSendwaybillMessage(task);
        } else if (keyword1.equals("3")) {
            //发货新老数据同步任务
            return reverseService.findsendMToReverse(task);
        } else if (keyword1.equals("4")) {
            return reverseSendService.findSendwaybillMessage(task);
        } else if (keyword1.equals("5")) {
            //中转发货补全任务
            return deliveryService.findTransitSend(task);
        } else if (keyword1.equals("6")) {
            //发送发货明细MQ任务
            return deliveryService.sendDetailMQ(task);
        } else if (keyword1.equals("7")) {
            //组板任务处理
            return deliveryService.doBoardDelivery(SendBizSourceEnum.BOARD_SEND, task);
        } else {
            //没有找到对应的方法，提供报错信息
            this.logger.error("task id is " + task.getId()+"can not find process method");
        }
        return false;
    }

    @Autowired
    private DmsTaskExecutor<Task> offlineCoreTaskExecutor;

    /**
     * 离线任务
     *
     * @param task
     * @return
     * @throws Exception
     */
    public boolean offlineTaskProcess(Task task) throws Exception {
        try {
           return offlineCoreTaskExecutor.execute(task, task.getOwnSign());
        } catch (Exception e) {
            logger.error("处理离线任务[offline]失败[taskId=" + task.getId() + "]异常信息为：" + e.getMessage(), e);
            return false;
        }
    }



    @Autowired
    private ScannerFrameDispatchService scannerFrameDispatchService;

    /**
     * 龙门架自动发货任务处理
     * @param task
     * @return
     */
    public boolean scannerFrameDispatchProcess(Task task) throws Exception{
        return scannerFrameDispatchService.dispatch(com.jd.bluedragon.distribution.api.utils.JsonHelper.fromJson(task.getBody(), UploadData.class));
    }

    @Autowired
    private InspectionService inspectionService;
    //平台打印补验货数据
    public boolean popPrintInspection(Task task) throws Exception{
        try {
            this.logger.info("task id&type is " + task.getId()+"&"+task.getType());
            this.inspectionService.popPrintInspection(task,task.getOwnSign());
        } catch (Exception e) {
            this.logger.error("task id is" + task.getId());
            this.logger.error("平台打印补验货数据，异常信息为：" + e.getMessage(), e);
            return Boolean.FALSE;
        }
        return Boolean.TRUE;
    }
}
