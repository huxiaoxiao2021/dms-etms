package com.jd.bluedragon.distribution.qualityControl.service;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.core.base.BaseMajorManager;
import com.jd.bluedragon.core.base.VrsRouteTransferRelationManager;
import com.jd.bluedragon.core.jmq.producer.DefaultJMQProducer;
import com.jd.bluedragon.distribution.abnormalwaybill.domain.AbnormalWayBill;
import com.jd.bluedragon.distribution.abnormalwaybill.service.AbnormalWayBillService;
import com.jd.bluedragon.distribution.api.request.QualityControlRequest;
import com.jd.bluedragon.distribution.api.request.ReturnsRequest;
import com.jd.bluedragon.distribution.message.OwnReverseTransferDomain;
import com.jd.bluedragon.distribution.qualityControl.domain.QualityControl;
import com.jd.bluedragon.distribution.reverse.service.ReversePrintService;
import com.jd.bluedragon.distribution.send.dao.SendDatailDao;
import com.jd.bluedragon.distribution.send.domain.SendDetail;
import com.jd.bluedragon.distribution.task.domain.Task;
import com.jd.bluedragon.distribution.task.domain.TaskResult;
import com.jd.bluedragon.distribution.task.service.TaskService;
import com.jd.bluedragon.distribution.waybill.domain.WaybillStatus;
import com.jd.bluedragon.dms.utils.WaybillUtil;
import com.jd.bluedragon.utils.*;
import com.jd.etms.waybill.api.WaybillSyncApi;
import com.jd.etms.waybill.api.WaybillTraceApi;
import com.jd.etms.waybill.dto.BdTraceDto;
import com.jd.ldop.business.api.dto.request.AbnormalOrderDTO;
import com.jd.ldop.business.api.dto.response.ResponseStatus;
import com.jd.ql.basic.domain.BaseDataDict;
import com.jd.ql.basic.dto.BaseStaffSiteOrgDto;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import com.jd.ldop.business.api.AbnormalOrderApi;
import com.jd.ldop.business.api.dto.response.Response;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dudong on 2014/12/1.
 */
@Service
public class QualityControlService {
    private final Log logger = LogFactory.getLog(this.getClass());

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
	private WaybillTraceApi waybillTraceApi;

    @Autowired
    private WaybillSyncApi waybillSyncApi;

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
            logger.error("调用异常配送接口获取包裹信息失败,发生异常。" + ex);
            return TaskResult.FAILED;
        }

        if (null == sendDetails || sendDetails.size() <= 0) {
            logger.error("调用异常配送接口获取包裹信息失败,数据为空。" + JsonHelper.toJson(request));
            return TaskResult.FAILED;
        }

        try {
            toQualityControlAndWaybillTrace(sendDetails, request, boxCode);  // 推质控和全程跟踪
            abnormalWayBillService.insertBatchAbnormalWayBill(convert2AbnormalWayBills(sendDetails, request));
        } catch (Exception ex) {
            logger.error("分拣中心异常节点推全程跟踪、质控发生异常。" + ex);
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
        List<String> doWaybillCodes = new ArrayList<String>();

        for(SendDetail sendDetail : sendDetails){

            //过滤数据，按运单维度处理
            if(doWaybillCodes.contains(sendDetail.getWaybillCode())){
                continue;
            }
            doWaybillCodes.add(sendDetail.getWaybillCode());


            //BdTraceDto bdTraceDto = convert2WaybillTrace(sendDetail, request);
            QualityControl qualityControl = convert2QualityControl(sendDetail, request, boxCode);
            logger.info("分拣中心异常页面发质控和全程跟踪开始，消息体：" + JsonHelper.toJson(qualityControl));
            //waybillTraceApi.sendBdTrace(bdTraceDto);
            // 更新运单状态
            updateWaybillStatus(sendDetail,request,operateSite);
            //messageClient.sendMessage(MessageDestinationConstant.QualityControlMQ.getName(), JsonHelper.toJson(qualityControl),request.getQcValue());   // 推质控
            bdExceptionToQcMQ.sendOnFailPersistent(request.getQcValue(), JsonHelper.toJson(qualityControl));

            //异常处理 节点发MQ 换新单   2016年8月16日18:18:40   by guoyongzhi  逆向整合之：3.2.6	拦截订单，触发新单
            logger.info("执行自营换新单  convert2ExchangeNewWaybill exchangeOwnWaybill ");
            OwnReverseTransferDomain domain=convert2ExchangeNewWaybill(sendDetail, request);
            reversePrintService.exchangeOwnWaybill(domain);

        }
    }

    /**
     * 异常处理 节点发MQ 换新单
     * @param sendDetail
     * @param request
     * @return
     */
    public OwnReverseTransferDomain convert2ExchangeNewWaybill(SendDetail sendDetail,QualityControlRequest request){
        OwnReverseTransferDomain ownReverseTransferDomain=new OwnReverseTransferDomain();
        ownReverseTransferDomain.setWaybillCode(sendDetail.getWaybillCode());
        ownReverseTransferDomain.setSiteId(request.getDistCenterID());
        ownReverseTransferDomain.setUserId(request.getUserID());
        ownReverseTransferDomain.setUserRealName(request.getUserName());
        ownReverseTransferDomain.setSiteName(request.getDistCenterName());
        return ownReverseTransferDomain;
    }

    public BdTraceDto convert2WaybillTrace(SendDetail sendDetail, QualityControlRequest request){
        BdTraceDto bdTraceDto = new BdTraceDto();
        bdTraceDto.setOperateType(WaybillStatus.WAYBILL_TRACK_QC);
        bdTraceDto.setOperatorSiteId(request.getDistCenterID());
        bdTraceDto.setOperatorSiteName(request.getDistCenterName());
        bdTraceDto.setOperatorTime(request.getOperateTime());
        bdTraceDto.setOperatorUserId(request.getUserID());
        bdTraceDto.setOperatorUserName(request.getUserName());
        bdTraceDto.setPackageBarCode(sendDetail.getPackageBarcode());
        bdTraceDto.setWaybillCode(sendDetail.getWaybillCode());
//        bdTraceDto.setOperatorDesp("包裹记录【" + request.getQcName() + "】异常");
        bdTraceDto.setOperatorDesp(request.getTrackContent());
        return bdTraceDto;
    }

    /**
     * 更新运单异常状态
     * 此节点节点运单只接收运单维度
     * @param sendDetail
     * @param request
     * @param operateSite
     */
    private void updateWaybillStatus(SendDetail sendDetail,QualityControlRequest request,BaseStaffSiteOrgDto operateSite){


        Task tTask = new Task();
        tTask.setBoxCode(sendDetail.getBoxCode());

        tTask.setCreateSiteCode(request.getDistCenterID());
        tTask.setKeyword2(sendDetail.getWaybillCode());
        tTask.setReceiveSiteCode(request.getDistCenterID());
        tTask.setType(WaybillStatus.WAYBILL_TRACK_QC);
        tTask.setTableName(Task.TABLE_NAME_WAYBILL);
        tTask.setSequenceName(Task.TABLE_NAME_WAYBILL_SEQ);
        tTask.setOwnSign(BusinessHelper.getOwnSign());
        tTask.setKeyword1(sendDetail.getWaybillCode());//回传运单状态
        tTask.setFingerprint(Md5Helper.encode(request.getDistCenterID() + "_" + WaybillStatus.WAYBILL_TRACK_QC + "_"
                + sendDetail.getWaybillCode() + "-" + request.getOperateTime() ));


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
        tWaybillStatus.setWaybillCode(sendDetail.getWaybillCode());

        //组装异常原因
        String qcName = request.getQcName();
        if(StringUtils.isNotBlank(qcName) && qcName.indexOf('-') != -1 && qcName.split("-").length == 2){

            tWaybillStatus.setReasonId(Integer.valueOf(qcName.split("-")[0]));
            tWaybillStatus.setRemark(qcName.split("-")[1]);

        }

        tWaybillStatus.setPackageCode(sendDetail.getWaybillCode()); //异常 节点运单只接收运单维度

        tTask.setBody(JsonHelper.toJson(tWaybillStatus));

        taskService.add(tTask);
    }


    public QualityControl convert2QualityControl(SendDetail sendDetail, QualityControlRequest request, String boxCode){
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
        qualityControl.setWaybillCode(sendDetail.getWaybillCode());
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
                logger.error("质控异常生成分拣退货数据异常:"+JsonHelper.toJson(request));
                logger.error("质控异常生成分拣退货数据异常，原因 " + e);
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
        if(null!=dto && dto.getStatus()== ResponseStatus.SUCCESS && null!=dto.getResult()){
           if (null != dto.getResult().getAbnormalState()) {
               res = dto.getResult().getAbnormalState();
           }
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

}
