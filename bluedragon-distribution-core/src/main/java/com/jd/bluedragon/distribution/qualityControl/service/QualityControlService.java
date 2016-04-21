package com.jd.bluedragon.distribution.qualityControl.service;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.core.base.BaseMajorManager;
import com.jd.bluedragon.core.jmq.producer.DefaultJMQProducer;
import com.jd.bluedragon.core.message.MessageDestinationConstant;
import com.jd.bluedragon.distribution.api.request.QualityControlRequest;
import com.jd.bluedragon.distribution.api.request.ReturnsRequest;
import com.jd.bluedragon.distribution.qualityControl.domain.QualityControl;
import com.jd.bluedragon.distribution.send.dao.SendDatailDao;
import com.jd.bluedragon.distribution.send.domain.SendDetail;
import com.jd.bluedragon.distribution.task.domain.Task;
import com.jd.bluedragon.distribution.task.domain.TaskResult;
import com.jd.bluedragon.distribution.task.service.TaskService;
import com.jd.bluedragon.distribution.waybill.domain.WaybillStatus;
import com.jd.bluedragon.utils.*;
import com.jd.etms.waybill.api.WaybillTraceApi;
import com.jd.etms.waybill.dto.BdTraceDto;
import com.jd.ql.basic.domain.BaseDataDict;
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
    private BaseMajorManager baseMajorManager;

    @Autowired
    private TaskService taskService;

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
                    String waybillCode = BusinessHelper.getWaybillCode(request.getQcValue());
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
        } catch (Exception ex) {
            logger.error("分拣中心异常节点推全程跟踪、质控发生异常。" + ex);
            return TaskResult.REPEAT;
        }


        return TaskResult.SUCCESS;
    }


    /** 发质控和全程跟踪 */
    public void toQualityControlAndWaybillTrace(List<SendDetail> sendDetails, QualityControlRequest request, String boxCode){
        for(SendDetail sendDetail : sendDetails){
            BdTraceDto bdTraceDto = convert2WaybillTrace(sendDetail, request);
            QualityControl qualityControl = convert2QualityControl(sendDetail, request, boxCode);
            logger.error("分拣中心异常页面发质控和全程跟踪开始。运单号" + JsonHelper.toJson(qualityControl));
            waybillTraceApi.sendBdTrace(bdTraceDto);   // 推全程跟踪
            //messageClient.sendMessage(MessageDestinationConstant.QualityControlMQ.getName(), JsonHelper.toJson(qualityControl),request.getQcValue());   // 推质控
            bdExceptionToQcMQ.sendOnFailPersistent(request.getQcValue(), JsonHelper.toJson(qualityControl));

        }
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


    public QualityControl convert2QualityControl(SendDetail sendDetail, QualityControlRequest request, String boxCode){
        BaseDataDict baseDataDict = baseMajorManager.getBaseDataDictById(request.getQcCode());
        QualityControl qualityControl = new QualityControl();
        qualityControl.setBlameDept(request.getDistCenterID());
        qualityControl.setBlameDeptName(request.getDistCenterName());
        qualityControl.setCreateTime(request.getOperateTime());
        qualityControl.setCreateUserId(request.getUserID());
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
                logger.warn("质控异常生成分拣退货数据异常，原因 " + e);
            }
        }
    }

}
