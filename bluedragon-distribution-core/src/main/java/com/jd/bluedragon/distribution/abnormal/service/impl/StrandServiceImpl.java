package com.jd.bluedragon.distribution.abnormal.service.impl;

import com.jd.bluedragon.core.jmq.producer.DefaultJMQProducer;
import com.jd.bluedragon.distribution.abnormal.domain.ReportTypeEnum;
import com.jd.bluedragon.distribution.abnormal.domain.StrandReportRequest;
import com.jd.bluedragon.distribution.abnormal.dto.StrandDetailMessage;
import com.jd.bluedragon.distribution.abnormal.service.StrandService;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.base.service.SiteService;
import com.jd.bluedragon.distribution.packageToMq.domain.Pack;
import com.jd.bluedragon.distribution.send.domain.SendDetail;
import com.jd.bluedragon.distribution.send.domain.dto.SendDetailDto;
import com.jd.bluedragon.distribution.send.service.SendDetailService;
import com.jd.bluedragon.distribution.task.domain.Task;
import com.jd.bluedragon.distribution.task.service.TaskService;
import com.jd.bluedragon.distribution.waybill.domain.WaybillStatus;
import com.jd.bluedragon.distribution.waybill.service.WaybillService;
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
import org.apache.commons.collections4.CollectionUtils;
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
    @Qualifier("strandReportProducer")
    DefaultJMQProducer strandReportProducer;
    @Autowired
    WaybillService waybillService;

    private static final String ABNORMAL_DESCRIPTION_PREFIX = "已滞留：原因：";

    /**
     * 生成滞留明细消息
     * @param request
     */
    @Override
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

            strandReportProducer.sendOnFailPersistent(waybillCode, JsonHelper.toJson(strandDetailMessage));
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
                StrandDetailMessage strandDetailMessage = initStrandDetailMessage(request, request.getBarcode(), waybillCode);
                Message message = new Message(strandReportProducer.getTopic(), JsonHelper.toJson(strandDetailMessage), waybillCode);
                list.add(message);
            }
            strandReportProducer.batchSendOnFailPersistent(list);
            return result;
        }

        /*按箱号或批次号上报*/
        //构建查询sendDetail的查询参数
        SendDetailDto  sendDetail = initSendDetail(reportType, request.getBarcode(), request.getSiteCode());
        List<String> packageCodes = null;
        //查询sendDetail
        if(ReportTypeEnum.BOX_CODE.getCode().equals(reportType)) {
            packageCodes = sendDetailService.queryPackageCodeByboxCode(sendDetail);
        }
        if(ReportTypeEnum.BATCH_NO.getCode().equals(reportType)){
            packageCodes = sendDetailService.queryPackageCodeBySendCode(sendDetail);
        }

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
            StrandDetailMessage strandDetailMessage = initStrandDetailMessage(request, request.getBarcode(), waybillCode);
            Message message = new Message(strandReportProducer.getTopic(), JsonHelper.toJson(strandDetailMessage), waybillCode);
            list.add(message);
        }
        strandReportProducer.batchSendOnFailPersistent(list);
        return result;

    }

//    private
//
//    /**
//     * 根据运单包裹信息构建 滞留详情jmq list
//     * @param packageList
//     * @param request
//     * @return
//     */
//    private List<StrandDetailMessage> initStrandDetailMessages(List<DeliveryPackageD> packageList, String waybillCode,
//                                                               StrandReportRequest request){
//        if(CollectionUtils.isEmpty(packageList)){
//            return Collections.EMPTY_LIST;
//        }
//        List<StrandDetailMessage> list = new ArrayList<>(packageList.size());
//        for(DeliveryPackageD packageD : packageList){
//            list.add(initStrandDetailMessage(request, request.getBarcode(), waybillCode));
//        }
//        return list;
//    }
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
        tTask.setKeyword2(String.valueOf(WaybillStatus.WAYBILL_OPE_TYPE_PUTAWAY));
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
        tWaybillStatus.setOperateType(WaybillStatus.WAYBILL_OPE_TYPE_PUTAWAY);
        tWaybillStatus.setWaybillCode(waybillCode);
        tWaybillStatus.setRemark(ABNORMAL_DESCRIPTION_PREFIX + request.getReasonMessage());
        // 运单自行区分 是包裹号还是运单号来更新状态
        tWaybillStatus.setPackageCode(barcode);
        tTask.setBody(JsonHelper.toJson(tWaybillStatus));
        return taskService.add(tTask);
    }
}
