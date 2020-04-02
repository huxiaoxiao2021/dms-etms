package com.jd.bluedragon.distribution.abnormal.service.impl;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.core.jmq.producer.DefaultJMQProducer;
import com.jd.bluedragon.distribution.abnormal.domain.ReportTypeEnum;
import com.jd.bluedragon.distribution.abnormal.domain.StrandReportRequest;
import com.jd.bluedragon.distribution.abnormal.dto.StrandDetailMessage;
import com.jd.bluedragon.distribution.abnormal.service.StrandService;
import com.jd.bluedragon.distribution.api.response.DeliveryResponse;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.base.service.SiteService;
import com.jd.bluedragon.distribution.packageToMq.domain.Pack;
import com.jd.bluedragon.distribution.send.domain.SendDetail;
import com.jd.bluedragon.distribution.send.domain.SendM;
import com.jd.bluedragon.distribution.send.domain.ThreeDeliveryResponse;
import com.jd.bluedragon.distribution.send.domain.dto.SendDetailDto;
import com.jd.bluedragon.distribution.send.service.DeliveryService;
import com.jd.bluedragon.distribution.send.service.SendDetailService;
import com.jd.bluedragon.distribution.task.domain.Task;
import com.jd.bluedragon.distribution.task.service.TaskService;
import com.jd.bluedragon.distribution.waybill.domain.WaybillStatus;
import com.jd.bluedragon.distribution.waybill.service.WaybillService;
import com.jd.bluedragon.dms.utils.BusinessUtil;
import com.jd.bluedragon.dms.utils.WaybillUtil;
import com.jd.bluedragon.utils.BusinessHelper;
import com.jd.bluedragon.utils.DateHelper;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.bluedragon.utils.Md5Helper;
import com.jd.etms.waybill.domain.DeliveryPackageD;
import com.jd.etms.waybill.dto.BigWaybillDto;
import com.jd.jddl.executor.function.scalar.filter.In;
import com.jd.jmq.common.exception.JMQException;
import com.jd.jmq.common.message.Message;
import com.jd.ql.basic.dto.BaseStaffSiteOrgDto;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

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
    @Qualifier("strandReportProducer")
    DefaultJMQProducer strandReportProducer;
    @Autowired
    WaybillService waybillService;
    @Autowired
    private DeliveryService deliveryService;

    private static final String ABNORMAL_DESCRIPTION_PREFIX = "已滞留：原因：";

    /**
     * 生成滞留明细消息
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
            result.error("你所在的分拣中心id未查到站点信息,请联系配送系统运营！");
            return result;
        }

        /*按包裹上报*/
        if(ReportTypeEnum.PACKAGE_CODE.getCode().equals(reportType)){
            String waybillCode = WaybillUtil.getWaybillCode(request.getBarcode());
            //发全程跟踪
            int addCount = addPackageCodeWaybilTraceTask(request.getBarcode(), waybillCode, request, siteOrgDto);
            //发滞留明细jmq
            StrandDetailMessage strandDetailMessage = initStrandDetailMessage(request, request.getBarcode(), waybillCode);

            strandReportDetailProducer.sendOnFailPersistent(waybillCode, JsonHelper.toJson(strandDetailMessage));
            return result;
        }

        /*按运单上报*/
        if(ReportTypeEnum.WAYBILL_CODE.getCode().equals(reportType)){
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
            List<Message> list = new ArrayList<>(bigWaybillDto.getPackageList().size());
            for(DeliveryPackageD packageD : bigWaybillDto.getPackageList()){
                String packageCode = packageD.getPackageBarcode();
                //全程跟踪
                addPackageCodeWaybilTraceTask(packageCode, waybillCode, request, siteOrgDto);
                //构建
                StrandDetailMessage strandDetailMessage = initStrandDetailMessage(request, packageCode, waybillCode);
                Message message = new Message(strandReportDetailProducer.getTopic(), JsonHelper.toJson(strandDetailMessage), waybillCode);
                list.add(message);
            }
            strandReportDetailProducer.batchSendOnFailPersistent(list);
            return result;
        }

        /*按箱号或批次号上报*/
        List<String> packageCodes = getPackageCodesByBoxCodeOrSendCode(reportType, request);

        if(CollectionUtils.isEmpty(packageCodes)){
            String reportTypeName = ReportTypeEnum.getReportTypeName(reportType);
            log.warn("根据{}:{}上报滞留，内无包裹号", reportTypeName,request.getBarcode());
            result.error(MessageFormat.format("上报失败，该{0}内无包裹信息！", reportTypeName));
            return result;
        }
        //发全程跟踪和上报明细消息
        List<Message> list = new ArrayList<>(packageCodes.size());
        for(String packageCode : packageCodes){
            String waybillCode = WaybillUtil.getWaybillCode(packageCode);
            //全程跟踪
            addPackageCodeWaybilTraceTask(packageCode, waybillCode, request, siteOrgDto);
            //构建
            StrandDetailMessage strandDetailMessage = initStrandDetailMessage(request, packageCode, waybillCode);
            Message message = new Message(strandReportDetailProducer.getTopic(), JsonHelper.toJson(strandDetailMessage), waybillCode);
            list.add(message);
        }
        strandReportDetailProducer.batchSendOnFailPersistent(list);
        return result;

    }

    private List<String> getPackageCodesByBoxCodeOrSendCode(Integer reportType, StrandReportRequest request){
        //构建查询sendDetail的查询参数
        SendDetailDto  sendDetail = initSendDetail(reportType, request.getBarcode(), request.getSiteCode());
        if(ReportTypeEnum.BOX_CODE.getCode().equals(reportType)) {
            return  sendDetailService.queryPackageCodeByboxCode(sendDetail);
        }
        return  sendDetailService.queryPackageCodeBySendCode(sendDetail);
    }

    /**
     * 根据sendCode 或 boxCode构建查询send_d 查询参数
     * @param reportType
     * @param barcode
     * @param createSiteCode
     * @return
     */
    private SendDetailDto initSendDetail( Integer reportType, String barcode, int createSiteCode){
        SendDetailDto sendDetail = new SendDetailDto();
        sendDetail.setIsCancel(0);
        sendDetail.setCreateSiteCode(createSiteCode);
        if(ReportTypeEnum.BOX_CODE.getCode().equals(reportType)){
            sendDetail.setBoxCode(barcode);
        }
        if(ReportTypeEnum.BATCH_NO.getCode().equals(reportType)){
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
    private StrandDetailMessage initStrandDetailMessage(StrandReportRequest request, String packageCode, String waybillCode){
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
        tTask.setKeyword2(barcode);
        tTask.setReceiveSiteCode(siteOrgDto.getSiteCode());
        tTask.setType(WaybillStatus.WAYBILL_OPE_TYPE_PUTAWAY);
        tTask.setTableName(Task.TABLE_NAME_WAYBILL);
        tTask.setSequenceName(Task.TABLE_NAME_WAYBILL_SEQ);
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
        tWaybillStatus.setOperateType(WaybillStatus.WAYBILL_OPE_TYPE_PUTAWAY);
        tWaybillStatus.setWaybillCode(waybillCode);
        // 运单自行区分 是包裹号还是运单号来更新状态
        tWaybillStatus.setPackageCode(barcode);
        tTask.setBody(JsonHelper.toJson(tWaybillStatus));
        return taskService.add(tTask);
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
        /*按箱号或批次号上报*/
        List<String> packageCodes = getPackageCodesByBoxCodeOrSendCode(reportType, request);
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
        ThreeDeliveryResponse response = deliveryService.dellCancelDeliveryMessage(sendM, true);
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
    public void sendStrandReportJmq(StrandReportRequest request) throws JMQException {
        strandReportProducer.send(request.getBarcode(), JsonHelper.toJson(request));
    }
}
