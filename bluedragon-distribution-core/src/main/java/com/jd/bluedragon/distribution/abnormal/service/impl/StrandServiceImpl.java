package com.jd.bluedragon.distribution.abnormal.service.impl;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.common.dto.base.response.JdCResponse;
import com.jd.bluedragon.common.utils.ProfilerHelper;
import com.jd.bluedragon.core.hint.constants.HintCodeConstants;
import com.jd.bluedragon.core.hint.service.HintService;
import com.jd.bluedragon.core.jmq.producer.DefaultJMQProducer;
import com.jd.bluedragon.core.jsf.dms.GroupBoardManager;
import com.jd.bluedragon.distribution.abnormal.domain.ReportTypeEnum;
import com.jd.bluedragon.distribution.abnormal.domain.StrandReportRequest;
import com.jd.bluedragon.distribution.abnormal.dto.StrandDetailMessage;
import com.jd.bluedragon.distribution.abnormal.service.StrandService;
import com.jd.bluedragon.distribution.api.response.ArAbnormalResponse;
import com.jd.bluedragon.distribution.api.response.DeliveryResponse;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.base.service.SiteService;
import com.jd.bluedragon.distribution.router.RouterService;
import com.jd.bluedragon.distribution.router.domain.dto.RouteNextDto;
import com.jd.bluedragon.distribution.send.domain.SendDetail;
import com.jd.bluedragon.distribution.send.domain.SendM;
import com.jd.bluedragon.distribution.send.domain.ThreeDeliveryResponse;
import com.jd.bluedragon.distribution.send.domain.dto.SendDetailDto;
import com.jd.bluedragon.distribution.send.service.DeliveryService;
import com.jd.bluedragon.distribution.send.service.SendDetailService;
import com.jd.bluedragon.distribution.send.service.SendMService;
import com.jd.bluedragon.distribution.sorting.domain.Sorting;
import com.jd.bluedragon.distribution.sorting.service.SortingService;
import com.jd.bluedragon.distribution.task.domain.Task;
import com.jd.bluedragon.distribution.task.service.TaskService;
import com.jd.bluedragon.distribution.waybill.domain.WaybillStatus;
import com.jd.bluedragon.distribution.waybill.service.WaybillService;
import com.jd.bluedragon.dms.job.JobHandler;
import com.jd.bluedragon.dms.job.JobResult;
import com.jd.bluedragon.dms.utils.BusinessUtil;
import com.jd.bluedragon.dms.utils.WaybillUtil;
import com.jd.bluedragon.utils.BusinessHelper;
import com.jd.bluedragon.utils.DateHelper;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.bluedragon.utils.Md5Helper;
import com.jd.bluedragon.utils.NumberHelper;
import com.jd.etms.waybill.domain.DeliveryPackageD;
import com.jd.etms.waybill.dto.BigWaybillDto;
import com.jd.jddl.executor.function.scalar.filter.In;
import com.jd.jmq.common.exception.JMQException;
import com.jd.jmq.common.message.Message;
import com.jd.ql.basic.dto.BaseStaffSiteOrgDto;
import com.jd.transboard.api.dto.Response;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import com.jd.ump.profiler.CallerInfo;
import com.jd.ump.profiler.proxy.Profiler;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

/**
 * 包裹滞留上报
 * @author jinjingcheng
 * @date 2020/3/12
 */
@Service("strandService")
public class StrandServiceImpl implements StrandService {
    private final Logger log = LoggerFactory.getLogger(StrandServiceImpl.class);
    @Autowired
    private SendDetailService sendDetailService;
    @Autowired
    private TaskService taskService;
    @Autowired
    private SiteService siteService;
    @Autowired
    @Qualifier("strandReportDetailProducer")
    DefaultJMQProducer strandReportDetailProducer;
    @Autowired
    SendMService sendMService;
    /**
     * 工作台mq
     */
    @Autowired
    @Qualifier("strandReportDetailWbProducer")
    DefaultJMQProducer strandReportDetailWbProducer;    
    @Autowired
    @Qualifier("strandReportProducer")
    DefaultJMQProducer strandReportProducer;
    @Autowired
    WaybillService waybillService;
    @Autowired
    private DeliveryService deliveryService;
    @Autowired
    private RouterService routerService;
    @Autowired
    GroupBoardManager groupBoardManager;
    @Autowired
    SortingService sortingService;

    private static final String ABNORMAL_DESCRIPTION_PREFIX = "已滞留：原因：";
    @Autowired
    @Qualifier("checkStrandReportJobHandler")
    private JobHandler<List<String>,List<String>> checkStrandReportJobHandler;
    
    @Value("${beans.StrandServiceImpl.checkStrandReportTimeout:30000}")
    private long checkStrandReportTimeout;

    /**
     * 上报滞留明细消息
     * @param request
     */
    @Override
    @JProfiler(jKey = "DMS.WEB.StrandServiceImpl.reportStrandDetail", jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
    public InvokeResult reportStrandDetail(StrandReportRequest request) throws JMQException {
        InvokeResult result = new InvokeResult();
        Integer reportType = request.getReportType();
        //操作人所在的分拣中心信息
        BaseStaffSiteOrgDto siteOrgDto = siteService.getSite(request.getSiteCode());
        if(siteOrgDto == null){
            log.warn("滞留上报，分拣中心id:{}无信息", request.getSiteCode());
            result.error("你所在的分拣中心id未查到站点信息,请联系org.wlxt2！");
            return result;
        }
        boolean syncFlag = Constants.YN_YES.equals(request.getSyncFlag());
        /*按包裹上报*/
        if(ReportTypeEnum.PACKAGE_CODE.getCode().equals(reportType)){
            return reportByPackage(result,request,syncFlag,siteOrgDto);
        }
        /*按运单上报*/
        if(ReportTypeEnum.WAYBILL_CODE.getCode().equals(reportType)){
            return reportByWayBill(result,request,syncFlag,siteOrgDto);
        }
        //按板号进行上报
        if(ReportTypeEnum.BOARD_NO.getCode().equals(reportType)){
            return reportByBoard(result,request,syncFlag,siteOrgDto);
        }
        /*按箱号或批次号上报*/
        return reportByBoxOrSendCode(result,request,syncFlag,siteOrgDto);
    }

    private InvokeResult reportByBoard(InvokeResult result, StrandReportRequest request, boolean syncFlag, BaseStaffSiteOrgDto siteOrgDto) {
        log.info("===========reportByBoard==============");
        String boardCode = request.getBarcode();
        Response<List<String>> response= groupBoardManager.getBoxesByBoardCode(boardCode);
        if(!(JdCResponse.CODE_SUCCESS.equals(response.getCode())
                && null!=response.getData()
                && response.getData().size()>0)){
            log.warn("根据板号：{}未查到包裹/箱号信息", boardCode);
            result.error(MessageFormat.format("上报失败，该板{0}内无包裹/箱号信息！", boardCode));
            return result;
        }
        log.info("===========reportByBoard==============根据板号找到组板箱号和包裹号");
        List<String> packOrBoxCodes =response.getData();
        List<String> packageCodes =getPackageCodesFromPackOrBoxCodes(packOrBoxCodes,request.getSiteCode());
        log.info("===========reportByBoard==============找到板子下的小包裹{}",packageCodes.size()>0?packageCodes.toString():"null");
        batchSendReportJmqByPackageCodes(packageCodes,request,syncFlag,siteOrgDto);
        return result;
    }

    private void batchSendReportJmqByPackageCodes(List<String> packageCodes, StrandReportRequest request, boolean syncFlag, BaseStaffSiteOrgDto siteOrgDto) {
        List<Message> list = new ArrayList<>(packageCodes.size());
        List<Message> listWb = new ArrayList<>(packageCodes.size());
        Map<String,StrandDetailMessage> waybillInfoMap = new HashMap<String,StrandDetailMessage>();
        for(String packageCode : packageCodes){
            String waybillCode = WaybillUtil.getWaybillCode(packageCode);
            StrandDetailMessage waybillStrandDetailMessage = null;
            if(waybillInfoMap.containsKey(waybillCode)) {
                waybillStrandDetailMessage = waybillInfoMap.get(waybillCode);
            }else {
                waybillStrandDetailMessage = this.loadWaybillInfo(request, waybillCode, null);
                waybillInfoMap.put(waybillCode, waybillStrandDetailMessage);
            }
            if(syncFlag) {
                //全程跟踪
                addPackageCodeWaybilTraceTask(packageCode, waybillCode, request, siteOrgDto);
            }
            //构建
            StrandDetailMessage strandDetailMessage = initStrandDetailMessage(request, packageCode, waybillCode,waybillStrandDetailMessage);
           log.info("按包裹滞留上报消息体 {}",JsonHelper.toJson(strandDetailMessage));
            Message message = new Message(strandReportDetailWbProducer.getTopic(), JsonHelper.toJson(strandDetailMessage), waybillCode);
            listWb.add(message);
            if(syncFlag) {
                list.add(new Message(strandReportDetailProducer.getTopic(),message.getText(),waybillCode));
            }
        }
        if(syncFlag) {
            strandReportDetailProducer.batchSendOnFailPersistent(list);
            log.info("=====report滞留上报按板或者按包裹（最终都是按包裹）给运单发全程跟踪消息成功============");

        }
        strandReportDetailWbProducer.batchSendOnFailPersistent(listWb);
        log.info("=====report滞留上报按板或者按包裹（最终都是按包裹）给分拣报表发消息成功============");
    }

    private InvokeResult reportByBoxOrSendCode(InvokeResult result, StrandReportRequest request, boolean syncFlag, BaseStaffSiteOrgDto siteOrgDto) {
        List<String> packageCodes = getPackageCodesByBoxCodeOrSendCode(request.getBarcode(),request.getSiteCode());
        if(CollectionUtils.isEmpty(packageCodes)){
            String reportTypeName = ReportTypeEnum.getReportTypeName(request.getReportType());
            log.warn("根据{}:{}上报滞留，内无包裹号", reportTypeName,request.getBarcode());
            result.error(MessageFormat.format("上报失败，该{0}内无包裹信息！", reportTypeName));
            return result;
        }
        batchSendReportJmqByPackageCodes(packageCodes,request,syncFlag,siteOrgDto);
        return result;
    }

    private InvokeResult reportByWayBill(InvokeResult result, StrandReportRequest request, boolean syncFlag, BaseStaffSiteOrgDto siteOrgDto) {
        String waybillCode = request.getBarcode();
        BigWaybillDto bigWaybillDto =  waybillService.getWaybill(waybillCode);
        if(bigWaybillDto == null){
            log.warn("根据运单号：{}未查到运单信息", waybillCode);
            result.error("上报失败，运单信息为空,请联系运单小秘！");
            return result;
        }
        if(CollectionUtils.isEmpty(bigWaybillDto.getPackageList())){
            log.warn("根据运单号：{}未查到包裹信息", waybillCode);
            result.error("上报失败，改运单包裹信息为空,请联系运单小秘！");
            return result;
        }
        StrandDetailMessage waybillStrandDetailMessage = this.loadWaybillInfo(request, waybillCode, bigWaybillDto);
        List<Message> list = new ArrayList<>(bigWaybillDto.getPackageList().size());
        List<Message> listWb = new ArrayList<>(bigWaybillDto.getPackageList().size());
        for(DeliveryPackageD packageD : bigWaybillDto.getPackageList()){
            String packageCode = packageD.getPackageBarcode();
            //构建
            StrandDetailMessage strandDetailMessage = initStrandDetailMessage(request, packageCode, waybillCode, waybillStrandDetailMessage);
            Message message = new Message(strandReportDetailWbProducer.getTopic(), JsonHelper.toJson(strandDetailMessage), waybillCode);
            listWb.add(message);
            if(syncFlag) {
                list.add(new Message(strandReportDetailProducer.getTopic(),message.getText(),waybillCode));
            }
        }
        if(syncFlag) {
            //全程跟踪
            addPackageCodeWaybilTraceTask(waybillCode, waybillCode, request, siteOrgDto);
            strandReportDetailProducer.batchSendOnFailPersistent(list);
            log.info("=====reportByWayBill给运单发全程跟踪消息成功============");
        }
        strandReportDetailWbProducer.batchSendOnFailPersistent(listWb);
        log.info("=====reportByWayBill给分拣报表发消息成功============");
        return result;
    }

    private InvokeResult reportByPackage(InvokeResult result, StrandReportRequest request, boolean syncFlag, BaseStaffSiteOrgDto siteOrgDto) {
        String waybillCode = WaybillUtil.getWaybillCode(request.getBarcode());
        StrandDetailMessage waybillStrandDetailMessage = loadWaybillInfo(request, waybillCode, null);
        //生成滞留明细jmq
        StrandDetailMessage strandDetailMessage = initStrandDetailMessage(request, request.getBarcode(), waybillCode , waybillStrandDetailMessage);
        if(syncFlag) {
            //发全程跟踪
            int addCount = addPackageCodeWaybilTraceTask(request.getBarcode(), waybillCode, request, siteOrgDto);
            strandReportDetailProducer.sendOnFailPersistent(waybillCode, JsonHelper.toJson(strandDetailMessage));
            log.info("=====reportByPackage给运单发全程跟踪消息成功============");
        }
        strandReportDetailWbProducer.sendOnFailPersistent(waybillCode, JsonHelper.toJson(strandDetailMessage));
        log.info("=====reportByPackage给分拣报表发消息成功============");
        return result;
    }


    private List<String> getPackageCodesFromPackOrBoxCodes(List<String> packOrBoxCodes,Integer siteCode) {
        List<String> packageCodes =new ArrayList<>();
        for (String code:packOrBoxCodes){
            if (BusinessUtil.isBoxcode(code)){
                log.info("=====getPackageCodesFromPackOrBoxCodes=======根据箱号获取集包包裹 {}",code);
                List<String> pCodes =getPackageCodesByBoxCodeOrSendCode(code,siteCode);
                if (pCodes!=null && pCodes.size()>0){
                    log.info("======getPackageCodesFromPackOrBoxCodes======根据sendD找到包裹信息{}",pCodes.toString());
                    packageCodes.addAll(pCodes);
                }
            }
            else {
                packageCodes.add(code);
            }
        }
        return packageCodes;
    }

    /**
     * 滞留明细初始化前加载运单其他信息
     * @param request
     * @param waybillCode
     * @param bigWaybillDto
     * @return
     */
    private StrandDetailMessage loadWaybillInfo(StrandReportRequest request,String waybillCode, BigWaybillDto bigWaybillDto) {
    	StrandDetailMessage strandDetailMessage = new StrandDetailMessage();
    	BigWaybillDto waybillDto = bigWaybillDto;
    	if(waybillDto == null) {
    		waybillDto =  waybillService.getWaybill(waybillCode);
    	}
    	if(waybillDto != null
    			&& waybillDto.getWaybill() != null) {
    		strandDetailMessage.setWaybillAgainWeight(waybillDto.getWaybill().getAgainWeight());
    		if(NumberHelper.isBigDecimal(waybillDto.getWaybill().getSpareColumn2())) {
    			strandDetailMessage.setWaybillAgainVolume(new Double(waybillDto.getWaybill().getSpareColumn2()));
    		}
    	}else {
    		log.warn("滞留明细初始化：{}加载称重量方信息失败，运单信息不存在！",waybillCode);
    	}
    	RouteNextDto routeNextDto = routerService.matchRouterNextNode(request.getSiteCode(), waybillCode);
    	//加载路由信息，设置下一站站点
    	if(routeNextDto != null) {
    		strandDetailMessage.setRouterNextSiteCode(routeNextDto.getFirstNextSiteId());
    	}else {
    		log.warn("滞留明细初始化：{}加载路由下一站失败，路由信息不存在！",waybillCode);
    	}
    	return strandDetailMessage;
    }
    private List<String> getPackageCodesByBoxCodeOrSendCode(String boxOrSendCode,Integer siteCode){
        //构建查询sendDetail的查询参数
        SendDetailDto  sendDetail = initSendDetail(boxOrSendCode, siteCode);
        if(BusinessUtil.isBoxcode(boxOrSendCode)) {
            return  sendDetailService.queryPackageCodeByboxCode(sendDetail);
        }
        return  sendDetailService.queryPackageCodeBySendCode(sendDetail);
    }

    /**
     * 根据sendCode 或 boxCode构建查询send_d 查询参数
     * @param barcode
     * @param createSiteCode
     * @return
     */
    private SendDetailDto initSendDetail(String barcode, int createSiteCode){
        SendDetailDto sendDetail = new SendDetailDto();
        sendDetail.setIsCancel(0);
        sendDetail.setCreateSiteCode(createSiteCode);
        if(BusinessUtil.isBoxcode(barcode)){
            sendDetail.setBoxCode(barcode);
        }
        if(BusinessUtil.isSendCode(barcode)){
         sendDetail.setSendCode(barcode);
        }
        return sendDetail;
    }


    /**
     * 构建 StrandDetailMessage
     * @param request
     * @param packageCode
     * @param waybillCode
     * @return
     */
    private StrandDetailMessage initStrandDetailMessage(StrandReportRequest request, String packageCode, String waybillCode, StrandDetailMessage waybillStrandDetailMessage){
        StrandDetailMessage strandDetailMessage = new StrandDetailMessage();
        /*异常描述*/
        strandDetailMessage.setAbnormalDescription(ABNORMAL_DESCRIPTION_PREFIX + request.getReasonMessage());
        /*上报场地id*/
        strandDetailMessage.setCreateSiteCode(request.getSiteCode());
        /*操作时间*/
        strandDetailMessage.setOperateTime(request.getOperateTime());
        /*操作人id*/
        strandDetailMessage.setOperatorCode(request.getUserCode());
        /*包裹号*/
        strandDetailMessage.setPackageCode(packageCode);
        /*滞留原因code*/
        strandDetailMessage.setReasonCode(request.getReasonCode());
        /*滞留原因描述*/
        strandDetailMessage.setReasonMessage(request.getReasonMessage());
        /*上报扫描条码*/
        strandDetailMessage.setReportBarcode(request.getBarcode());
        /*上报容器类型*/
        strandDetailMessage.setReportType(request.getReportType());
        /* 运单号*/
        strandDetailMessage.setWaybillCode(waybillCode);
        //同步标识
        strandDetailMessage.setSyncFlag(request.getSyncFlag());
        if(waybillStrandDetailMessage != null) {
        	strandDetailMessage.setWaybillAgainWeight(waybillStrandDetailMessage.getWaybillAgainWeight());
        	strandDetailMessage.setWaybillAgainVolume(waybillStrandDetailMessage.getWaybillAgainVolume());
        	strandDetailMessage.setRouterNextSiteCode(waybillStrandDetailMessage.getRouterNextSiteCode());
        }
        return strandDetailMessage;
    }

    /**
     * 生成暂存的全程跟踪任务
     * @param barcode
     * @param waybillCode
     * @param request
     * @param siteOrgDto
     */
    private Integer addPackageCodeWaybilTraceTask(String barcode, String waybillCode, StrandReportRequest request,
                                               BaseStaffSiteOrgDto siteOrgDto){
        Date operateTime = DateHelper.parseDateTime(request.getOperateTime());
        Task tTask = new Task();
        tTask.setBoxCode(barcode);
        tTask.setCreateSiteCode(siteOrgDto.getSiteCode());
        tTask.setKeyword2(String.valueOf(WaybillStatus.WAYBILL_STRAND_REPORT));
        tTask.setReceiveSiteCode(siteOrgDto.getSiteCode());
        tTask.setType(Task.TASK_TYPE_WAYBILL_TRACK);
        tTask.setTableName(Task.getTableName(Task.TASK_TYPE_WAYBILL_TRACK));
        tTask.setSequenceName(Task.getSequenceName(Task.TABLE_NAME_POP));
        tTask.setOwnSign(BusinessHelper.getOwnSign());
        //回传运单状态
        tTask.setKeyword1(waybillCode);
        tTask.setFingerprint(Md5Helper.encode(siteOrgDto.getSiteCode() + "_"
                + barcode + "-" + request.getReportType() + "-" + operateTime.getTime() ));
        WaybillStatus tWaybillStatus = new WaybillStatus();
        tWaybillStatus.setOperatorId(request.getUserCode());
        tWaybillStatus.setOperator(request.getUserName());
        tWaybillStatus.setOperateTime(operateTime);
        tWaybillStatus.setOrgId(siteOrgDto.getOrgId());
        tWaybillStatus.setOrgName(siteOrgDto.getOrgName());
        tWaybillStatus.setCreateSiteCode(siteOrgDto.getSiteCode());
        tWaybillStatus.setCreateSiteName(siteOrgDto.getSiteName());
        tWaybillStatus.setCreateSiteType(siteOrgDto.getSiteType());
        tWaybillStatus.setOperateType(WaybillStatus.WAYBILL_STRAND_REPORT);
        tWaybillStatus.setWaybillCode(waybillCode);
        String content = getReportTypeAndBarCode(request);
        tWaybillStatus.setRemark("滞留上报，原因：" + request.getReasonMessage() + content);
        // 运单自行区分 是包裹号还是运单号来更新状态
        tWaybillStatus.setPackageCode(barcode);
        tTask.setBody(JsonHelper.toJson(tWaybillStatus));
        return taskService.add(tTask);
    }

    /**
     * 获取滞留上报方式
     * @return
     */
    private String getReportTypeAndBarCode(StrandReportRequest request){
        String s="";
        if(ReportTypeEnum.PACKAGE_CODE.getCode().equals(request.getReportType())){
            s = ", 方式: 按包裹" ;
        }else if(ReportTypeEnum.WAYBILL_CODE.getCode().equals(request.getReportType())){
            s = ", 方式: 按运单" ;
        }else if(ReportTypeEnum.BOX_CODE.getCode().equals(request.getReportType())){
            s = ", 方式: 按箱号," + request.getBarcode();
        }else if(ReportTypeEnum.BATCH_NO.getCode().equals(request.getReportType())){
            s = ", 方式: 按批次号," + request.getBarcode();
        }else if(ReportTypeEnum.BOARD_NO.getCode().equals(request.getReportType())){
            s = ", 方式: 按板号," + request.getBarcode();
        }
        return s;
    }

    /**
     * 是否有发货
     * @param request
     * @return
     */
    @Override
    public boolean hasSenddetail(StrandReportRequest request){
        Integer reportType = request.getReportType();
        String barcode = request.getBarcode();
        /*按包裹或运单上报*/
        if(ReportTypeEnum.PACKAGE_CODE.getCode().equals(reportType) ||
                ReportTypeEnum.WAYBILL_CODE.getCode().equals(reportType)){
            String waybillCode = WaybillUtil.getWaybillCode(barcode);
            SendDetail sendDetail = sendDetailService.findOneByWaybillCode(request.getSiteCode(), waybillCode);
            if(sendDetail != null){
                return true;
            }
            return false;
        }
        /*按板号进行上报*/
        else if (ReportTypeEnum.BOARD_NO.getCode().equals(reportType)){
            SendM sendM =sendMService.selectSendByBoardCode(request.getSiteCode(),request.getBarcode(),1);
            if(sendM != null){
                return true;
            }
            return false;
        }
        /*按箱号或批次号上报*/
        List<String> packageCodes = getPackageCodesByBoxCodeOrSendCode(request.getBarcode(),request.getSiteCode());
        if(!CollectionUtils.isEmpty(packageCodes)){
            return true;
        }
        return false;
    }

    private SendM initSendM(StrandReportRequest request){
        SendM sendM = new SendM();
        sendM.setCreateSiteCode(request.getSiteCode());
        sendM.setUpdaterUser(request.getUserName());
        sendM.setSendType(request.getBusinessType());
        sendM.setUpdateUserCode(request.getUserCode());
        String barcode = request.getBarcode();
        Integer reportType = request.getReportType();
        if(ReportTypeEnum.BATCH_NO.getCode().equals(reportType)){
            sendM.setSendCode(barcode);
        }else{
            sendM.setBoxCode(barcode);
        }
        Date operateTime = DateHelper.parseDate(request.getOperateTime(), Constants.DATE_TIME_FORMAT);
        //取消发货的时间减1秒
        operateTime = DateUtils.addSeconds(operateTime, -1);
        sendM.setOperateTime(operateTime);
        //从批次号中获取目的站点
        if (ReportTypeEnum.BATCH_NO.getCode().equals(reportType)) {
            sendM.setReceiveSiteCode(BusinessUtil.getReceiveSiteCodeFromSendCode(barcode));
        }
        sendM.setUpdateTime(new Date());
        sendM.setYn(0);
        return sendM;
    }

    /**
     * 滞留上报和取消发货
     */
    @Override
    @JProfiler(jKey = "DMS.WEB.StrandServiceImpl.strandReportAndCancelDelivery", jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
    public void strandReportAndCancelDelivery(StrandReportRequest request) throws JMQException {
        //发暂存的全程跟踪 和 滞留明细jmq
        reportStrandDetail(request);
        SendM sendM = initSendM(request);
        //按批次或板号提交,不取消发货
        if((ReportTypeEnum.BATCH_NO.getCode().equals(request.getReportType())) ||
                (ReportTypeEnum.BOARD_NO.getCode().equals(request.getReportType()))){
            return;
        }
        if (ReportTypeEnum.BOARD_NO.getCode().equals(request.getReportType())){
            SendM sendMDto =sendMService.selectSendByBoardCode(request.getSiteCode(),request.getBarcode(),1);
            if (sendMDto==null){
                log.info("按板滞留上报==========没有找到按板的sendM(发货)记录");
                return;
            }
            sendM.setSendCode(sendMDto.getSendCode());
        }
        ThreeDeliveryResponse response = deliveryService.dellCancelDeliveryMessageWithServerTime(sendM, true);
        //取消发货时异常
        if(DeliveryResponse.CODE_Delivery_ERROR.equals(response.getCode())){
            log.error("包裹滞留上报取消发货时异常条码:{},createSiteCode:{}", request.getBarcode(), request.getSiteCode());
        }
    }

    /**
     * 发送滞留上报消息
     * @param request
     */
    @Override
    public InvokeResult<Boolean> sendStrandReportJmq(StrandReportRequest request) throws JMQException {
    	InvokeResult<Boolean> result = new InvokeResult<Boolean>();
    	//增加校验,特快送不允许滞留上报
        Integer reportType = request.getReportType();
        String reportTypeName = ReportTypeEnum.getReportTypeName(reportType);
        String barcode = request.getBarcode();
        /*按包裹或运单上报*/
        if(ReportTypeEnum.PACKAGE_CODE.getCode().equals(reportType) ||
                ReportTypeEnum.WAYBILL_CODE.getCode().equals(reportType)){
            String waybillCode = WaybillUtil.getWaybillCode(barcode);
            checkCanStrandReport(waybillCode,result);
            if(!result.codeSuccess()) {
            	result.setMessage(result.getMessage()+"\n"+barcode);
            	return result;
            }
        }
        /*按板号进行上报*/
        else if (ReportTypeEnum.BOARD_NO.getCode().equals(reportType)){
            Response<List<String>> response= groupBoardManager.getBoxesByBoardCode(barcode);
            if(!(JdCResponse.CODE_SUCCESS.equals(response.getCode())
                    && null!=response.getData()
                    && response.getData().size()>0)){
                log.warn("根据板号：{}未查到包裹/箱号信息", barcode);
                result.error(MessageFormat.format("上报失败，该板{0}内无包裹/箱号信息！", barcode));
                return result;
            }
            List<String> packOrBoxCodes =response.getData();
            List<String> packageCodes = new ArrayList<>();
            for (String code:packOrBoxCodes){
            	//按箱号校验
                if (BusinessUtil.isBoxcode(code)){
                    List<String> packageCodesInBox =getPackageCodesByBoxCodeOrSendCode(code,request.getSiteCode());
                    if (CollectionUtils.isEmpty(packageCodesInBox)){
                    	continue;
                    }
                    packageCodes.addAll(packageCodesInBox);
                } else {
                	packageCodes.add(code);
                }
            }
            checkCanStrandReport(request,packageCodes,result);
        }else{
            /*按箱号或批次号上报*/
            List<String> packageCodes = getPackageCodesByBoxCodeOrSendCode(request.getBarcode(),request.getSiteCode());
            if(CollectionUtils.isEmpty(packageCodes)){
                log.warn("根据箱号或批次号：{}未查到包裹信息", barcode);
                result.error(MessageFormat.format("上报失败，该{0}{1}内无包裹信息！",reportTypeName, barcode));
                return result;
            }
            checkCanStrandReport(request,packageCodes,result);
        }
        if(!result.codeSuccess()) {
        	log.warn("滞留上报失败！请求：{}，失败原因:{}",JsonHelper.toJson(request),result.getMessage());
        	return result;
        }
        //无拦截，发送上报mq
        strandReportProducer.send(request.getBarcode(), JsonHelper.toJson(request));
        return result;
    }
    /**
     * 按包裹列表校验
     * @param request
     * @param packageCodes
     * @param result
     * @return
     */
    InvokeResult<Boolean> checkCanStrandReport(StrandReportRequest request,List<String> packageCodes,InvokeResult<Boolean> result) {
    	if (CollectionUtils.isEmpty(packageCodes)){
    		return result;
        }
    	List<String> waybillCodes = new ArrayList<>();
    	List<String> checkPackageCodes = new ArrayList<>();
        //每个运单拿出一个包裹进行校验
    	for(String packageCode:packageCodes) {
            String waybillCode = WaybillUtil.getWaybillCode(packageCode);
            if(waybillCodes.contains(waybillCode)) {
            	continue;
            }
            waybillCodes.add(waybillCode);
            checkPackageCodes.add(packageCode);
        }
        List<String> checkFailPackageCodes;
        CallerInfo call = ProfilerHelper.registerInfo("dmsWeb.job.checkStrandReportJobHandler.handle");
        log.info("滞留上报多包裹校验【{}】包裹数{}",request.getBarcode(),checkPackageCodes.size());
        try {
        	JobResult<List<String>> jobResult= this.checkStrandReportJobHandler.handle(checkPackageCodes, checkStrandReportTimeout);
        	if(jobResult != null && jobResult.isSuc()) {
        		checkFailPackageCodes = jobResult.getData();
        	}else {
                result.error("滞留上报操作异常，请稍后重试！");
            	return result;
        	}
		} catch (Exception e) {
			Profiler.functionError(call);
			log.error("滞留上报操作执行异常：请求参数：{}", JsonHelper.toJson(request),e);
            result.error("滞留上报操作异常，请稍后重试！");
        	return result;
		}finally {
			Profiler.registerInfoEnd(call);
		}
        if(!CollectionUtils.isEmpty(checkFailPackageCodes)) {
        	result.error("特快送包裹禁止上报\n"+checkFailPackageCodes.get(0));
        }
    	return result;
    }
    InvokeResult<Boolean> checkCanStrandReport(String waybillCode,InvokeResult<Boolean> result) {
        BigWaybillDto bigWaybillDto =  waybillService.getWaybill(waybillCode);
        if(bigWaybillDto == null 
        		|| bigWaybillDto.getWaybill() == null){
            log.warn("根据运单号：{}未查到运单信息", waybillCode);
            result.error("运单信息为空,请联系运单小秘！");
            return result;
        }
        if(BusinessUtil.isVasWaybill(bigWaybillDto.getWaybill().getWaybillSign()) && waybillService.isLuxurySecurityVosWaybill(waybillCode)) {
            log.warn("根据运单号：特快送包裹禁止上报{}", waybillCode);
            result.error("特快送包裹禁止上报");
            return result;
        }
    	return result;
    }
}
