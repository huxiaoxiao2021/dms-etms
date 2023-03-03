package com.jd.bluedragon.distribution.qualityControl.service;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.core.base.BaseMajorManager;
import com.jd.bluedragon.core.base.VrsRouteTransferRelationManager;
import com.jd.bluedragon.core.base.WaybillQueryManager;
import com.jd.bluedragon.core.hint.constants.HintCodeConstants;
import com.jd.bluedragon.core.hint.service.HintService;
import com.jd.bluedragon.core.jmq.producer.DefaultJMQProducer;
import com.jd.bluedragon.distribution.abnormalwaybill.domain.AbnormalWayBill;
import com.jd.bluedragon.distribution.abnormalwaybill.service.AbnormalWayBillService;
import com.jd.bluedragon.distribution.api.JdResponse;
import com.jd.bluedragon.distribution.api.request.QualityControlRequest;
import com.jd.bluedragon.distribution.api.request.RedeliveryCheckRequest;
import com.jd.bluedragon.distribution.api.request.ReturnsRequest;
import com.jd.bluedragon.distribution.api.response.QualityControlResponse;
import com.jd.bluedragon.distribution.api.response.base.Result;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.base.domain.JdCancelWaybillResponse;
import com.jd.bluedragon.distribution.base.domain.SysConfigContent;
import com.jd.bluedragon.distribution.base.service.SysConfigService;
import com.jd.bluedragon.distribution.message.OwnReverseTransferDomain;
import com.jd.bluedragon.distribution.qualityControl.QcVersionFlagEnum;
import com.jd.bluedragon.distribution.qualityControl.domain.QualityControl;
import com.jd.bluedragon.distribution.abnormal.domain.RedeliveryMode;
import com.jd.bluedragon.distribution.qualityControl.dto.QcReportJmqDto;
import com.jd.bluedragon.distribution.qualityControl.dto.QcReportOutCallJmqDto;
import com.jd.bluedragon.distribution.reverse.service.ReversePrintService;
import com.jd.bluedragon.distribution.send.dao.SendDatailDao;
import com.jd.bluedragon.distribution.send.domain.SendDetail;
import com.jd.bluedragon.distribution.sorting.service.SortingService;
import com.jd.bluedragon.distribution.task.domain.Task;
import com.jd.bluedragon.distribution.task.domain.TaskResult;
import com.jd.bluedragon.distribution.task.service.TaskService;
import com.jd.bluedragon.distribution.waybill.domain.WaybillStatus;
import com.jd.bluedragon.distribution.waybill.service.WaybillService;
import com.jd.bluedragon.dms.utils.WaybillUtil;
import com.jd.bluedragon.utils.*;
import com.jd.etms.waybill.domain.Waybill;
import com.jd.etms.waybill.util.WaybillCodeRuleValidateUtil;
import com.jd.ldop.business.api.AbnormalOrderApi;
import com.jd.ldop.business.api.dto.request.AbnormalOrderDTO;
import com.jd.ldop.business.api.dto.response.Response;
import com.jd.ldop.business.api.dto.response.ResponseStatus;
import com.jd.ql.basic.domain.BaseDataDict;
import com.jd.ql.basic.dto.BaseStaffSiteOrgDto;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;

/**
 * Created by dudong on 2014/12/1.
 */
@Service
public class QualityControlService {
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private static final int PACKAGE_CODE_TYPE = 1;
    private static final int WAYBILL_CODE_TYPE = 2;
    private static final int BOX_CODE_TYPE = 3;
    private static final int SEND_CODE_TYPE = 4;

    @Autowired
    private SendDatailDao sendDatailDao;

    @Autowired
    @Qualifier("bdExceptionToQcMQ")
    private DefaultJMQProducer bdExceptionToQcMQ;

    @Autowired
    private BaseMajorManager baseMajorManager;

    @Autowired
    private TaskService taskService;

    @Autowired
    private ReversePrintService reversePrintService;

    @Autowired
    AbnormalWayBillService abnormalWayBillService;

    @Autowired
    private VrsRouteTransferRelationManager vrsRouteTransferRelationManager;

    @Autowired
    private AbnormalOrderApi abnormalOrderApi;

    @Autowired
    private SysConfigService sysConfigService;

    @Autowired
    private SortingService sortingService;

    @Autowired
    private WaybillQueryManager waybillQueryManager;

    @Autowired
    private WaybillService waybillService;

    @Resource(name = "checkPrintInterceptReasonIdSetForOld")
    private Set<Integer> checkPrintInterceptReasonIdSetForOld;

    /**
     * 协商再投状态校验
     * 
     * @param request
     * @return
     */
    public InvokeResult<RedeliveryMode> redeliveryCheck(RedeliveryCheckRequest request) {
        InvokeResult<RedeliveryMode> result=new InvokeResult<RedeliveryMode>();

        RedeliveryMode data=new RedeliveryMode();
        data.setIsCompleted(true);

        result.setCode(InvokeResult.RESULT_SUCCESS_CODE);
        result.setMessage(InvokeResult.RESULT_SUCCESS_MESSAGE);
        result.setData(data);

        if(StringUtils.isEmpty(request.getCode()) || null==request.getCodeType() || request.getCodeType()<1){
            log.warn("PDA调用协商再投状态验证接口失败-参数错误。入参:{}",JsonHelper.toJson(request));
            result.setCode(InvokeResult.RESULT_THIRD_ERROR_CODE);
            result.setMessage("请扫描者包裹号、运单号或箱号！");
            return result;
        }

        try{
            List<String> waybillCodeList=new ArrayList<String>();

            //如果是包裹或运单
            if (request.getCodeType()==1 || request.getCodeType()==2){
                String waybillCode= WaybillUtil.getWaybillCode(request.getCode());
                waybillCodeList.add(waybillCode);
            }

            //如果是箱号
            if (request.getCodeType()==3){
                waybillCodeList = sortingService.getWaybillCodeListByBoxCode(request.getCode());
            }

            if(waybillCodeList != null && waybillCodeList.size() > 0){
                for (String waybillCode :waybillCodeList){
                    Waybill waybillData = waybillQueryManager.getWaybillByWayCode(waybillCode);
                    //补打拦截
                    if (waybillData != null
                            && checkPrintInterceptReasonIdSetForOld != null
                            && request.getSupExceptionId() != null
                            && checkPrintInterceptReasonIdSetForOld.contains(request.getSupExceptionId())
                            && waybillService.hasPrintIntercept(waybillCode, waybillData.getWaybillSign())) {
                        //取消拦截  存在时跳过 不进行补打拦截提示
                        JdCancelWaybillResponse jdCancelResponse = waybillService.dealCancelWaybill(waybillCode);
                        if (jdCancelResponse == null || jdCancelResponse.getCode() == null || jdCancelResponse.getCode().equals(JdResponse.CODE_OK)) {
                            data.setIsCompleted(false);
                            data.setWaybillCode(waybillCode);
                            result.setData(data);
                            result.setMessage("此单号["+ waybillCode +"]"+ HintService.getHint(HintCodeConstants.EX_REPORT_CHECK_CHANGE_ADDRESS));
                            break;
                        }
                    }
                    //协商再投拦截
                    if (waybillData != null
                            && waybillData.getBusiId() != null
                            && getRedeliveryState(waybillCode, waybillData.getBusiId()) == 0) {
                        data.setIsCompleted(false);
                        data.setWaybillCode(waybillCode);
                        result.setData(data);
                        result.setMessage("此单号["+ waybillCode +"]为【发起协商再投未处理】状态，需商家审核完成才能提交异常！");
                        break;
                    }
                    else {
                        log.warn("PDA调用协商再投状态验证接口失败-无商家信息。运单号:{},入参:{}",waybillCode,JsonHelper.toJson(request));
                    }
                }
            }
            else {
                log.warn("PDA调用协商再投状态验证接口失败-无运单信息。入参:{}",JsonHelper.toJson(request));
                result.setCode(InvokeResult.RESULT_NULL_WAYBILLCODE_CODE);
                result.setMessage(InvokeResult.RESULT_NULL_WAYBILLCODE_MESSAGE);
            }
        } catch (Exception ex) {
            log.error("PDA调用协商再投状态验证接口失败。异常信息:{}",ex.getMessage(),ex);
            result.setCode(InvokeResult.SERVER_ERROR_CODE);
            result.setMessage(InvokeResult.SERVER_ERROR_MESSAGE);
        }

        return result;
    }

    public InvokeResult<Boolean> exceptionSubmit(QualityControlRequest request) {
        InvokeResult<Boolean> result = new InvokeResult<Boolean>();
        if(StringUtils.isEmpty(request.getQcValue()) || !WaybillCodeRuleValidateUtil.isEffectiveOperateCode(request.getQcValue())){
            log.warn("PDA调用异常配送接口插入质控任务表失败-参数错误[{}]",JsonHelper.toJson(request));
            result.setCode(QualityControlResponse.CODE_SERVICE_ERROR);
            result.setMessage("请扫描运单号或者包裹号！");
            return result;
        }
        try{
            convertThenAddTask(request);
        }catch(Exception ex){
            log.error("PDA调用异常配送接口插入质控任务表失败，原因 " , ex);
            result.setCode(QualityControlResponse.CODE_SERVICE_ERROR);
            result.setMessage(QualityControlResponse.MESSAGE_SERVICE_ERROR);
            return result;
        }
        result.setCode(QualityControlResponse.CODE_OK);
        result.setMessage(QualityControlResponse.MESSAGE_OK);
        return result;
    }

    public TaskResult dealQualityControlTask(Task task) {
        QualityControlRequest request = null;
        List<SendDetail> sendDetails = null;
        SendDetail sendDetail = null;
        String boxCode = null;
        try {
            request = JsonHelper.fromJson(task.getBody(), QualityControlRequest.class);
            switch (Integer.valueOf(request.getQcType()).intValue()) {
                case PACKAGE_CODE_TYPE:
                    toSortingReturn(request);
                    String waybillCode = WaybillUtil.getWaybillCode(request.getQcValue());
                    if(StringHelper.isEmpty(waybillCode)){
                        throw new NullPointerException("通过包裹号 [" + request.getQcValue() + "] 提取运单号为空");
                    }
                    sendDetails = new ArrayList<SendDetail>();
                    sendDetail = new SendDetail();
                    sendDetail.setWaybillCode(waybillCode);
                    sendDetails.add(sendDetail);
                    break;
                case WAYBILL_CODE_TYPE:
                    toSortingReturn(request);
                    sendDetails = new ArrayList<SendDetail>();
                    sendDetail = new SendDetail();
                    sendDetail.setWaybillCode(request.getQcValue());
                    sendDetails.add(sendDetail);
                    break;
                case BOX_CODE_TYPE:
                    boxCode = request.getQcValue();
                    sendDetails = sendDatailDao.queryWaybillsByBoxCode(boxCode);
                    break;
                case SEND_CODE_TYPE:
                    sendDetails = sendDatailDao.queryWaybillsBySendCode(request.getQcValue());
                    break;
            }
        } catch (Exception ex) {
            log.error("调用异常配送接口获取包裹信息失败,发生异常。" , ex);
            return TaskResult.FAILED;
        }

        if (null == sendDetails || sendDetails.size() <= 0) {
            log.warn("调用异常配送接口获取包裹信息失败,数据为空:{}" , JsonHelper.toJson(request));
            return TaskResult.FAILED;
        }

        try {
            //如果是对接新质控系统，走新逻辑
            if (request.getQcVersionFlag() != null && request.getQcVersionFlag() == QcVersionFlagEnum.NEW_QUALITY_CONTROL_SYSTEM.getType()) {
                toNewQualityControlWaybillTrace(sendDetails, request);
            } else {
                toQualityControlAndWaybillTrace(sendDetails, request, boxCode);  // 推质控和全程跟踪
            }
            abnormalWayBillService.insertBatchAbnormalWayBill(convert2AbnormalWayBills(sendDetails, request));
        } catch (Exception ex) {
            log.error("分拣中心异常节点推全程跟踪、质控发生异常。" , ex);
            return TaskResult.REPEAT;
        }


        return TaskResult.SUCCESS;
    }


    /** 发质控和全程跟踪 */
    public void toQualityControlAndWaybillTrace(List<SendDetail> sendDetails, QualityControlRequest request, String boxCode){
        //获取 同步运单状态接口需要的额外参数
        BaseStaffSiteOrgDto operateSite =  baseMajorManager.getBaseSiteBySiteId(request.getDistCenterID());

        //过滤数据，按运单维度处理
        //已经处理过的运单
        Set<String> set = new HashSet<>();

        for(SendDetail sendDetail : sendDetails){
            //过滤数据，按运单维度处理
            if(set.contains(sendDetail.getWaybillCode())){
                continue;
            }
            set.add(sendDetail.getWaybillCode());

            QualityControl qualityControl = convert2QualityControl(sendDetail.getWaybillCode(), request, boxCode);
            log.info("分拣中心异常页面发质控和全程跟踪开始，消息体：{}" , JsonHelper.toJson(qualityControl));
            // 更新运单状态
            updateWaybillStatus(sendDetail.getWaybillCode(), request, operateSite, sendDetail.getBoxCode());
            bdExceptionToQcMQ.sendOnFailPersistent(request.getQcValue(), JsonHelper.toJson(qualityControl));

            //异常处理 节点发MQ 换新单   2016年8月16日18:18:40   by guoyongzhi  逆向整合之：3.2.6	拦截订单，触发新单
            log.info("执行自营换新单  convert2ExchangeNewWaybill exchangeOwnWaybill ");
            OwnReverseTransferDomain domain=convert2ExchangeNewWaybill(sendDetail.getWaybillCode(), request);
            reversePrintService.exchangeOwnWaybill(domain);

        }
    }

    /** 发质控和全程跟踪
     *
     * 对接新质控系统，只变更运单状态和触发换新单
     *
     * */
    public void toNewQualityControlWaybillTrace(List<SendDetail> sendDetails, QualityControlRequest request){
        //获取 同步运单状态接口需要的额外参数
        BaseStaffSiteOrgDto operateSite =  baseMajorManager.getBaseSiteBySiteId(request.getDistCenterID());

        Set<String> set = new HashSet<>();
        for (SendDetail sendDetail : sendDetails) {
            String waybillCode = sendDetail.getWaybillCode();
            //过滤数据，按运单维度处理
            if (set.contains(waybillCode)) {
                continue;
            }
            set.add(sendDetail.getWaybillCode());

            // QualityControl qualityControl = convert2QualityControl(waybillCode, request, null);
            // log.info("分拣中心新异常提交结果同步运单状态开始，消息体：{}", JsonHelper.toJson(qualityControl));
            // 更新运单状态
            updateWaybillStatus(waybillCode, request, operateSite, null);
            //异常处理 节点发MQ 换新单
            log.info("执行自营换新单 convert2ExchangeNewWaybill exchangeOwnWaybill ");
            OwnReverseTransferDomain domain = convert2ExchangeNewWaybill(waybillCode, request);
            reversePrintService.exchangeOwnWaybill(domain);
        }
    }

    /**
     * 异常处理 节点发MQ 换新单
     * @param waybillCode
     * @param request
     * @return
     */
    public OwnReverseTransferDomain convert2ExchangeNewWaybill(String waybillCode, QualityControlRequest request){
        OwnReverseTransferDomain ownReverseTransferDomain=new OwnReverseTransferDomain();
        ownReverseTransferDomain.setWaybillCode(waybillCode);
        ownReverseTransferDomain.setSiteId(request.getDistCenterID());
        ownReverseTransferDomain.setUserId(request.getUserID());
        ownReverseTransferDomain.setUserRealName(request.getUserName());
        ownReverseTransferDomain.setSiteName(request.getDistCenterName());
        return ownReverseTransferDomain;
    }

    /**
     * 更新运单异常状态
     * 此节点节点运单只接收运单维度
     * @param boxCode 箱号
     * @param request
     * @param operateSite
     */
    private void updateWaybillStatus(String waybillCode, QualityControlRequest request,BaseStaffSiteOrgDto operateSite, String boxCode){
        Task tTask = new Task();
        tTask.setBoxCode(boxCode);

        tTask.setCreateSiteCode(request.getDistCenterID());
        tTask.setKeyword2(waybillCode);
        tTask.setReceiveSiteCode(request.getDistCenterID());
        tTask.setType(WaybillStatus.WAYBILL_TRACK_QC);
        tTask.setTableName(Task.TABLE_NAME_WAYBILL);
        tTask.setSequenceName(Task.TABLE_NAME_WAYBILL_SEQ);
        tTask.setOwnSign(BusinessHelper.getOwnSign());
        tTask.setKeyword1(waybillCode);//回传运单状态
        tTask.setFingerprint(Md5Helper.encode(request.getDistCenterID() + "_" + WaybillStatus.WAYBILL_TRACK_QC + "_"
                + waybillCode + "-" + request.getOperateTime() ));


        WaybillStatus tWaybillStatus = new WaybillStatus();
        tWaybillStatus.setOperatorId(request.getUserID());
        tWaybillStatus.setOperator(request.getUserName());
        tWaybillStatus.setOperateTime(request.getOperateTime());
        tWaybillStatus.setCreateSiteCode(request.getDistCenterID());
        tWaybillStatus.setCreateSiteName(request.getDistCenterName());
        tWaybillStatus.setCreateSiteType(operateSite.getSiteType());
        tWaybillStatus.setOrgId(operateSite.getOrgId());
        tWaybillStatus.setOrgName(operateSite.getOrgName());
        tWaybillStatus.setOperateType(WaybillStatus.WAYBILL_TRACK_QC);
        tWaybillStatus.setWaybillCode(waybillCode);

        //组装异常原因
        // FIXME: 2020/2/11 待优化
        String qcName = request.getQcName();
        if(StringUtils.isNotBlank(qcName)){
            if (qcName.indexOf('-') != -1 && qcName.split("-").length == 2) {
                tWaybillStatus.setReasonId(Integer.valueOf(qcName.split("-")[0]));
                tWaybillStatus.setRemark(qcName.split("-")[1]);
            } else {
                tWaybillStatus.setReasonId(request.getQcCode());
                tWaybillStatus.setRemark(qcName);
            }


        }

        tWaybillStatus.setPackageCode(waybillCode); //异常 节点运单只接收运单维度

        tTask.setBody(JsonHelper.toJson(tWaybillStatus));

        taskService.add(tTask);
    }


    public QualityControl convert2QualityControl(String waybillCode, QualityControlRequest request, String boxCode){
        BaseDataDict baseDataDict = baseMajorManager.getBaseDataDictById(request.getQcCode());
        QualityControl qualityControl = new QualityControl();
        qualityControl.setBlameDept(request.getDistCenterID());
        qualityControl.setBlameDeptName(request.getDistCenterName());
        qualityControl.setCreateTime(request.getOperateTime());
        qualityControl.setCreateUserId(request.getUserID());
        qualityControl.setCreateUserErp(request.getUserERP());
        qualityControl.setCreateUserName(request.getUserName());
        qualityControl.setMessageType(baseDataDict.getTypeGroup());
        if(null != boxCode){
            qualityControl.setBoxCode(boxCode);
        }else{
            qualityControl.setBoxCode("null");
        }
        qualityControl.setWaybillCode(waybillCode);
        qualityControl.setTypeCode(baseDataDict.getTypeCode() + "");
        qualityControl.setExtraCode("null");
        qualityControl.setSystemName(QualityControl.SYSTEM_NAME);
        qualityControl.setReturnState("null");
        return qualityControl;
    }


    /** 扫描包裹号或者运单号后生成分拣退货Task任务 */
    public void toSortingReturn(QualityControlRequest request){
        if(request.getIsSortingReturn()){
            try{
                ReturnsRequest sortingReturn = new ReturnsRequest();
                sortingReturn.setSiteCode(request.getDistCenterID());
                sortingReturn.setSiteName(request.getDistCenterName());
                sortingReturn.setUserCode(request.getUserID());
                sortingReturn.setUserName(request.getUserName());
                sortingReturn.setPackageCode(request.getQcValue());
                sortingReturn.setBusinessType(10);  // 只有正向的，三方的使用老的分拣退货页面，还有-1的是分拣验证时插入的数据
                sortingReturn.setOperateTime(DateHelper.formatDateTime(request.getOperateTime()));
                sortingReturn.setShieldsError(request.getQcName());
                Task task = new Task();
                task.setKeyword1(request.getDistCenterID() + "");
                task.setKeyword2(request.getQcValue());
                task.setCreateSiteCode(request.getDistCenterID());
                task.setReceiveSiteCode(request.getDistCenterID());
                task.setOwnSign(BusinessHelper.getOwnSign());
                task.setType(Task.TASK_TYPE_RETURNS);
                task.setTableName(Task.getTableName(task.getType()));
                task.setSequenceName(Task.getSequenceName(task.getTableName()));
                StringBuilder fringerprint = new StringBuilder();
                fringerprint.append(task.getKeyword1()).append("_").append(task.getKeyword2()).append("_").append(task.getCreateSiteCode());
                task.setFingerprint(Md5Helper.encode(fringerprint.toString()));
                task.setBody(Constants.PUNCTUATION_OPEN_BRACKET + JsonHelper.toJson(sortingReturn) + Constants.PUNCTUATION_CLOSE_BRACKET);
                taskService.add(task);
            }catch(Exception e){
                log.error("质控异常生成分拣退货数据异常:{}",JsonHelper.toJson(request));
                log.error("质控异常生成分拣退货数据异常，原因 " , e);
            }
        }
    }

    /**
     * 获取运单协商再投状态
     * @param
     * @return 0：未处理 1：已处理
     */
    @JProfiler(jKey = "DMSWEB.QualityControlService.getRedeliveryState",jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
    public int getRedeliveryState(String waybillCode,Integer businessID) {
        int res = 1;
        Response<AbnormalOrderDTO> dto = abnormalOrderApi.queryByCustomerIdDeliveryIdMainTypeId(waybillCode,businessID,20);
        log.info("获取运单协商再投状态: 入参waybillCode={} businessID={} JSF接口返回:{}"
                ,waybillCode,businessID,JsonHelper.toJson(dto));
        if(null != dto && ResponseStatus.SUCCESS.equals(dto.getStatus()) &&
                null != dto.getResult() && null != dto.getResult().getAbnormalState()){
            res = dto.getResult().getAbnormalState();
        }

        return res;
    }

    /**
     * 构造运单异常操作集合
     * @param sendDetails
     * @param request
     * @return
     */
    private List<AbnormalWayBill> convert2AbnormalWayBills(List<SendDetail> sendDetails, QualityControlRequest request){
        List<AbnormalWayBill> list = new ArrayList<AbnormalWayBill>(sendDetails.size());
        for (SendDetail sendDetail : sendDetails){
            AbnormalWayBill abnormalWayBill = new AbnormalWayBill();
            abnormalWayBill.setWaybillCode(sendDetail.getWaybillCode());
            abnormalWayBill.setPackageCode(sendDetail.getPackageBarcode());
            abnormalWayBill.setCreateUserCode(request.getUserID());
            abnormalWayBill.setCreateUserErp(request.getUserERP());
            abnormalWayBill.setCreateUser(request.getUserName());
            abnormalWayBill.setCreateSiteCode(request.getDistCenterID());
            abnormalWayBill.setCreateSiteName(request.getDistCenterName());
            abnormalWayBill.setQcType(request.getQcType());
            abnormalWayBill.setQcValue(request.getQcValue());
            abnormalWayBill.setQcCode(request.getQcCode());
            abnormalWayBill.setQcName(request.getQcName());
            abnormalWayBill.setSortingReturn(request.getIsSortingReturn());
            abnormalWayBill.setOperateTime(request.getOperateTime());
            if (request.getWaveBusinessId() == null) {
                //查路由系统班次号tangcq 2018年6月29日18:35:32
                abnormalWayBill.setWaveBusinessId(vrsRouteTransferRelationManager.queryWaveInfoByWaybillCodeAndNodeCode(abnormalWayBill.getWaybillCode(), abnormalWayBill.getCreateSiteCode()));
            } else {
                abnormalWayBill.setWaveBusinessId(request.getWaveBusinessId());
            }
            list.add(abnormalWayBill);
        }
        return list;
    }

    public void convertThenAddTask(QualityControlRequest request) throws Exception {

        Task qcTask = new Task();
        qcTask.setKeyword1(request.getQcType() + "");
        qcTask.setKeyword2(request.getQcValue());
        qcTask.setOwnSign(BusinessHelper.getOwnSign());
        qcTask.setStatus(Task.TASK_STATUS_UNHANDLED);
        qcTask.setType(Task.TASK_TYPE_REVERSE_QUALITYCONTROL);
        qcTask.setTableName(Task.getTableName(qcTask.getType()));
        qcTask.setSequenceName(Task.getSequenceName(qcTask.getTableName()));
        qcTask.setBody(JsonHelper.toJson(request));
        qcTask.setCreateTime(new Date());
        qcTask.setCreateSiteCode(Integer.parseInt(String.valueOf(request.getDistCenterID())));
        qcTask.setExecuteCount(0);
        StringBuilder fringerprint = new StringBuilder();
        fringerprint.append(request.getDistCenterID() + "_" + qcTask.getType() + "_" + qcTask.getKeyword1() + "_" + qcTask.getKeyword2());
        qcTask.setFingerprint(Md5Helper.encode(fringerprint.toString()));

        taskService.add(qcTask);
    }

    /*
    * 根据站点和条码判断是否生成分拣退货任务
    * */
    public void generateSortingReturnTask(Integer siteCode, String oldWaybillCode, String newPackageCode, Date operateTime) {
        if (StringHelper.isEmpty(oldWaybillCode) || StringHelper.isEmpty(newPackageCode)) {
            return;
        }
        AbnormalWayBill abnormalWayBill = null;
        String oldPackageCode = null;
        //根据新单组装包裹号
        String index = WaybillUtil.getPackageSuffix(newPackageCode);
        if (StringHelper.isNotEmpty(index)) {
            oldPackageCode = oldWaybillCode + index;
        }

        try {
            if (StringHelper.isNotEmpty(oldPackageCode)) {
                //先用包裹号查询abnormal_waybill表
                abnormalWayBill = this.abnormalWayBillService.getAbnormalWayBillByQcValue(siteCode, oldPackageCode);
            }
            //如果包裹维度没有，查询运单维度
            if (abnormalWayBill == null) {
                abnormalWayBill = this.abnormalWayBillService.getAbnormalWayBillByQcValue(siteCode, oldWaybillCode);
            }

        } catch (Exception e) {
            log.error("获取异常提报记录信息失败，参数:{}, {}, {}", siteCode, oldWaybillCode, newPackageCode, e);
        }

        if (abnormalWayBill != null && this.isGenerateSortingReturnTask(abnormalWayBill.getQcCode())) {
            ReturnsRequest sortingReturn = new ReturnsRequest();
            sortingReturn.setSiteCode(abnormalWayBill.getCreateSiteCode());
            sortingReturn.setSiteName(abnormalWayBill.getCreateSiteName());
            sortingReturn.setUserCode(abnormalWayBill.getCreateUserCode());
            sortingReturn.setUserName(abnormalWayBill.getCreateUser());

            if (StringHelper.isNotEmpty(oldPackageCode) && WaybillUtil.isPackageCode(oldPackageCode)) {
                sortingReturn.setPackageCode(oldPackageCode);
            } else {
                sortingReturn.setPackageCode(oldWaybillCode);
            }
            sortingReturn.setBusinessType(10);
            sortingReturn.setOperateTime(DateHelper.formatDateTime(operateTime));
            sortingReturn.setShieldsError(abnormalWayBill.getQcName());
            Task task = new Task();
            task.setKeyword1(abnormalWayBill.getCreateSiteCode() + "");
            task.setKeyword2(abnormalWayBill.getQcValue());
            task.setCreateSiteCode(abnormalWayBill.getCreateSiteCode());
            task.setReceiveSiteCode(abnormalWayBill.getCreateSiteCode());
            task.setOwnSign(BusinessHelper.getOwnSign());
            task.setType(Task.TASK_TYPE_RETURNS);
            task.setTableName(Task.getTableName(task.getType()));
            task.setSequenceName(Task.getSequenceName(task.getTableName()));
            task.setFingerprint(Md5Helper.encode(task.getKeyword1() + "_" + task.getKeyword2() + "_" + task.getCreateSiteCode()));
            task.setBody(Constants.PUNCTUATION_OPEN_BRACKET + JsonHelper.toJson(sortingReturn) + Constants.PUNCTUATION_CLOSE_BRACKET);

            try{
                taskService.add(task);
            }catch(Exception e){
                log.error("质控异常生成分拣退货数据异常:{}", JsonHelper.toJson(task), e);
            }
        }
    }

    /*
    * 从配置中获取需要发送分拣退货任务的原因列表
    * */
    private boolean isGenerateSortingReturnTask(Integer reasonId){
        SysConfigContent content = sysConfigService.getSysConfigJsonContent(Constants.SYS_CONFIG_ABNORMAL_REASON_ID_GENERATE_SORTING_RETURN_TASK);
        if (content != null && content.getKeyCodes() != null && ! content.getKeyCodes().isEmpty()) {
            return content.getKeyCodes().contains(reasonId.toString());
    }
        return false;
    }

    private String reportSystem = "dms";

    /**
     * 处理异常提报数据
     * @param qcReportJmqDto 消息提
     * @return 处理结果
     * @author fanggang7
     * @time 2022-02-18 15:38:54 周五
     */
    public Result<Boolean> handleQcReportConsume(QcReportJmqDto qcReportJmqDto) {
        log.info("handleQcReportConsume param: {}", JsonHelper.toJson(qcReportJmqDto));
        Result<Boolean> result = Result.success();
        try {
            if (StringUtils.isBlank(qcReportJmqDto.getReportSystem()) || !Objects.equals(qcReportJmqDto.getReportSystem(), reportSystem)){
                return result;
            }
            final BaseStaffSiteOrgDto baseStaff = baseMajorManager.getBaseStaffByErpNoCache(qcReportJmqDto.getCreateUser());
            if(baseStaff == null){
                log.error("未找到此erp:{}信息", qcReportJmqDto.getCreateUser());
                return result.toFail(String.format("未找到此erp:%s信息", qcReportJmqDto.getCreateUser()));
            }

            String barCodes = qcReportJmqDto.getPackageNumber();
            String[] barCodeList = barCodes.split(Constants.SEPARATOR_COMMA);

            for (String barCode : barCodeList) {
                final QualityControlRequest qualityControlRequest = new QualityControlRequest();
                qualityControlRequest.setQcVersionFlag(QcVersionFlagEnum.NEW_QUALITY_CONTROL_SYSTEM.getType());
                qualityControlRequest.setQcValue(barCode);
                qualityControlRequest.setQcType(PACKAGE_CODE_TYPE);
                qualityControlRequest.setUserERP(qcReportJmqDto.getCreateUser());
                qualityControlRequest.setUserName(baseStaff.getStaffName());
                qualityControlRequest.setUserID(baseStaff.getStaffNo());
                qualityControlRequest.setDistCenterID(Integer.parseInt(qcReportJmqDto.getCreateDept()));
                qualityControlRequest.setDistCenterName(qcReportJmqDto.getCreateDeptName());
                qualityControlRequest.setOperateTime(new Date(qcReportJmqDto.getCreateTime()));
                qualityControlRequest.setQcCode(qcReportJmqDto.getAbnormalThirdId().intValue());
                qualityControlRequest.setQcName(qcReportJmqDto.getAbnormalThirdName());
                qualityControlRequest.setIsSortingReturn(false);
                qualityControlRequest.setTrackContent("订单扫描异常【" + qcReportJmqDto.getAbnormalThirdName() + "】");
                Task task = new Task();
                task.setBody(JsonHelper.toJson(qualityControlRequest));
                log.info("dealQualityControlTask param: {}", JsonHelper.toJson(task));
                final TaskResult taskResult = this.dealQualityControlTask(task);
                log.info("dealQualityControlTask param: {} result: {}", JsonHelper.toJson(task), JsonHelper.toJson(taskResult));
                if(!TaskResult.toBoolean(taskResult)){
                    log.error("handleQcReportConsume fail packageCode {} param {} ", barCode, JsonHelper.toJson(qcReportJmqDto));
                    return result.toFail();
                } else {
                    result.setData(true);
                }
            }

        } catch (Exception e) {
            log.error("handleQcReportConsume exception ", e);
            result.toFail("handleQcReportConsume exception " + e.getMessage());
        }
        return result;
    }

    public Result<Void> checkMqParam(QcReportJmqDto qcReportJmqDto) {
        Result<Void> result = Result.success();
        if(StringUtils.isBlank(qcReportJmqDto.getPackageNumber())){
            return result.toFail("参数错误，packageNumber为空");
        }
        if(StringUtils.isBlank(qcReportJmqDto.getCreateUser())){
            return result.toFail("参数错误，createUser为空");
        }
        if(StringUtils.isBlank(qcReportJmqDto.getCreateDept())){
            return result.toFail("参数错误，createDept为空");
        }
        if(qcReportJmqDto.getCreateTime() == null){
            return result.toFail("参数错误，createTime为空");
        }
        if(StringUtils.isBlank(qcReportJmqDto.getCreateDeptName())){
            return result.toFail("参数错误，createDeptName为空");
        }
        if(qcReportJmqDto.getAbnormalThirdId() == null){
            return result.toFail("参数错误，abnormalThirdId为空");
        }
        if(StringUtils.isBlank(qcReportJmqDto.getAbnormalThirdName())){
            return result.toFail("参数错误，abnormalThirdName为空");
        }
        return result;
    }

    /**
     * 处理异常提报数据
     * @param qcReportJmqDto 消息提
     * @return 处理结果
     * @author fanggang7
     * @time 2022-02-18 15:38:54 周五
     */
    public Result<Boolean> handleQcOutCallReportConsume(QcReportOutCallJmqDto qcReportJmqDto) {
        log.info("handleQcReportConsume param: {}", JsonHelper.toJson(qcReportJmqDto));
        Result<Boolean> result = Result.success();
        try {
            if (StringUtils.isBlank(qcReportJmqDto.getReportSystem()) || !Objects.equals(qcReportJmqDto.getReportSystem(), reportSystem)){
                return result;
            }
            final BaseStaffSiteOrgDto baseStaff = baseMajorManager.getBaseStaffByErpNoCache(qcReportJmqDto.getCreateUser());
            if(baseStaff == null){
                log.error("未找到此erp:{}信息", qcReportJmqDto.getCreateUser());
                return result.toFail(String.format("未找到此erp:%s信息", qcReportJmqDto.getCreateUser()));
            }

            String barCodes = qcReportJmqDto.getPackageNumber();
            String[] barCodeList = barCodes.split(Constants.SEPARATOR_COMMA);

            for (String barCode : barCodeList) {
                final QualityControlRequest qualityControlRequest = new QualityControlRequest();
                qualityControlRequest.setQcVersionFlag(QcVersionFlagEnum.NEW_QUALITY_CONTROL_SYSTEM.getType());
                qualityControlRequest.setQcValue(barCode);
                qualityControlRequest.setQcType(PACKAGE_CODE_TYPE);
                qualityControlRequest.setUserERP(qcReportJmqDto.getCreateUser());
                qualityControlRequest.setUserName(baseStaff.getStaffName());
                qualityControlRequest.setUserID(baseStaff.getStaffNo());
                qualityControlRequest.setDistCenterID(Integer.parseInt(qcReportJmqDto.getCreateDept()));
                qualityControlRequest.setDistCenterName(qcReportJmqDto.getCreateDeptName());
                qualityControlRequest.setOperateTime(new Date(qcReportJmqDto.getCreateTime()));
                qualityControlRequest.setQcCode(qcReportJmqDto.getAbnormalThirdId().intValue());
                qualityControlRequest.setQcName(qcReportJmqDto.getAbnormalThirdName());
                qualityControlRequest.setIsSortingReturn(false);
                qualityControlRequest.setTrackContent("订单扫描异常【" + qcReportJmqDto.getAbnormalThirdName() + "】");
                Task task = new Task();
                task.setBody(JsonHelper.toJson(qualityControlRequest));
                log.info("dealQualityControlTask param: {}", JsonHelper.toJson(task));
                final TaskResult taskResult = this.dealQualityControlTask(task);
                log.info("dealQualityControlTask param: {} result: {}", JsonHelper.toJson(task), JsonHelper.toJson(taskResult));
                if(!TaskResult.toBoolean(taskResult)){
                    log.error("handleQcOutCallReportConsume fail packageCode {} param {} ", barCode, JsonHelper.toJson(qcReportJmqDto));
                    return result.toFail();
                } else {
                    result.setData(true);
                }
            }
        } catch (Exception e) {
            log.error("handleQcOutCallReportConsume exception ", e);
            result.toFail("handleQcOutCallReportConsume exception " + e.getMessage());
        }
        return result;
    }

    public Result<Void> checkMqParam(QcReportOutCallJmqDto qcReportJmqDto) {
        Result<Void> result = Result.success();
        if(StringUtils.isBlank(qcReportJmqDto.getPackageNumber())){
            return result.toFail("参数错误，packageNumber为空");
        }
        if(StringUtils.isBlank(qcReportJmqDto.getCreateUser())){
            return result.toFail("参数错误，createUser为空");
        }
        if(StringUtils.isBlank(qcReportJmqDto.getCreateDept())){
            return result.toFail("参数错误，createDept为空");
        }
        if(qcReportJmqDto.getCreateTime() == null){
            return result.toFail("参数错误，createTime为空");
        }
        if(StringUtils.isBlank(qcReportJmqDto.getCreateDeptName())){
            return result.toFail("参数错误，createDeptName为空");
        }
        if(qcReportJmqDto.getAbnormalThirdId() == null){
            return result.toFail("参数错误，abnormalThirdId为空");
        }
        if(StringUtils.isBlank(qcReportJmqDto.getAbnormalThirdName())){
            return result.toFail("参数错误，abnormalThirdName为空");
        }
        return result;
    }

}
