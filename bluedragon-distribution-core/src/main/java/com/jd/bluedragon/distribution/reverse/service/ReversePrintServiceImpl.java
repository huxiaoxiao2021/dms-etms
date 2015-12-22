package com.jd.bluedragon.distribution.reverse.service;

import com.jd.bluedragon.core.base.ReceiveManager;
import com.jd.bluedragon.distribution.api.request.ReversePrintRequest;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.base.service.SiteService;
import com.jd.bluedragon.distribution.external.jos.service.JosService;
import com.jd.bluedragon.distribution.operationLog.domain.OperationLog;
import com.jd.bluedragon.distribution.operationLog.service.OperationLogService;
import com.jd.bluedragon.distribution.packageToMq.service.IPushPackageToMqService;
import com.jd.bluedragon.distribution.sorting.service.SortingReturnServiceImple;
import com.jd.bluedragon.distribution.task.domain.Task;
import com.jd.bluedragon.distribution.task.service.TaskService;
import com.jd.bluedragon.distribution.waybill.domain.WaybillStatus;
import com.jd.bluedragon.utils.BusinessHelper;
import com.jd.bluedragon.utils.DateHelper;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.bluedragon.utils.SerialRuleUtil;
import com.jd.etms.basic.dto.BaseStaffSiteOrgDto;
import com.jd.etms.erp.service.domain.BaseEntity;
import com.jd.etms.erp.ws.ErpQuerySafWS;
import com.jd.etms.waybill.api.WaybillSyncApi;
import com.jd.etms.waybill.domain.WaybillParameter;
import com.jd.etms.waybill.handler.WaybillSyncParameter;
import com.jd.etms.waybill.handler.WaybillSyncParameterExtend;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import sun.plugin2.message.Message;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 逆向换单打印
 * Created by wangtingwei on 14-8-7.
 */
@Service("ReversePrintService")
public class ReversePrintServiceImpl implements ReversePrintService {


    private final Log logger= LogFactory.getLog(ReversePrintServiceImpl.class);

    private static final String REVERSE_PRINT_MQ_TOPIC="bd_blocker_complete";

    private static final String REVERSE_PRINT_MQ_MESSAGE_CATEGORY="BLOCKER_QUEUE_DMS_REVERSE_PRINT";

    private static final Integer EXCHANGE_OWN_WAYBILL_OP_TYPE=Integer.valueOf(4200);
    @Autowired
    private TaskService taskService;

    @Autowired
    private OperationLogService operationLogService;

    @Autowired
    private IPushPackageToMqService pushMqService;

    @Autowired
    private ReceiveManager receiveManager;

    @Autowired
    private WaybillSyncApi waybillSyncApi;

    @Autowired
    private SiteService siteService;

    @Autowired
    private ErpQuerySafWS baseErpQuerySafWSInfoSafService;
    /**
     * 处理逆向打印数据
     * 【1：发送全程跟踪 2：写分拣中心操作日志】
     * @param domain 打印提交数据
     * @return
     */
    @Override
    public boolean handlePrint(ReversePrintRequest domain) {
        if(BusinessHelper.isPackageCode(domain.getOldCode())){
            domain.setOldCode(BusinessHelper.getWaybillCodeByPackageBarcode(domain.getOldCode()));
        }

        if(BusinessHelper.isPackageCode(domain.getNewCode())){
            domain.setNewCode(BusinessHelper.getWaybillCodeByPackageBarcode(domain.getNewCode()));
        }
        Task tTask = new Task();
        tTask.setKeyword1(domain.getOldCode());
        tTask.setCreateSiteCode(domain.getSiteCode());
        tTask.setCreateTime(new Date(domain.getOperateUnixTime()));
        tTask.setKeyword2(String.valueOf(1700));
        tTask.setReceiveSiteCode(0);
        tTask.setType(Task.TASK_TYPE_WAYBILL_TRACK);
        tTask.setTableName(Task.getTableName(Task.TASK_TYPE_WAYBILL_TRACK));
        tTask.setSequenceName(Task.getSequenceName(Task.TABLE_NAME_POP));
        String ownSign = BusinessHelper.getOwnSign();
        tTask.setOwnSign(ownSign);

        WaybillStatus status=new WaybillStatus();
        status.setOperateType(WaybillStatus.WAYBILL_TRACK_REVERSE_PRINT);
        status.setWaybillCode(domain.getOldCode());
        status.setOperateTime(new Date(domain.getOperateUnixTime()));
        status.setOperator(domain.getStaffRealName());
        status.setOperatorId(domain.getStaffId());
        status.setRemark("换单打印，新运单号"+domain.getNewCode());
        status.setCreateSiteCode(domain.getSiteCode());
        status.setCreateSiteName(domain.getSiteName());
        tTask.setBody(JsonHelper.toJson(status));
        /**
         * 原外单添加换单全程跟踪
         */
        taskService.add(tTask, true);
        tTask.setKeyword1(domain.getNewCode());
        status.setWaybillCode(domain.getNewCode());
        status.setRemark("换单打印，原运单号"+domain.getOldCode());
        tTask.setBody(JsonHelper.toJson(status));
        /**
         * 新外单添加全程跟踪
         */
        taskService.add(tTask);
        this.logger.info(REVERSE_PRINT_MQ_TOPIC+createMqBody(domain.getOldCode())+domain.getOldCode());
        pushMqService.pubshMq(REVERSE_PRINT_MQ_TOPIC, createMqBody(domain.getOldCode()), domain.getOldCode());
        OperationLog operationLog=new OperationLog();
        operationLog.setCreateTime(new Date());
        operationLog.setRemark("【外单逆向换单打印】原单号："+domain.getOldCode()+"新单号："+domain.getNewCode());
        operationLog.setWaybillCode(domain.getOldCode());
        operationLog.setCreateUser(domain.getStaffRealName());
        operationLog.setCreateUserCode(domain.getStaffId());
        operationLog.setCreateSiteCode(domain.getSiteCode());
        operationLog.setCreateSiteName(domain.getSiteName());
        operationLogService.add(operationLog);
        return true;
    }

    /**
     * 根据原单号获取对应的新单号
     * 1.自营拒收：新运单规则：T+原运单号。调取运单来源：从运单处获取，调取运单新接口。
     * 2.外单拒收：新运单规则：生成新的V单。调取运单来源：1）从外单获得新外单单号。2）通过新外单单号从运单处调取新外单的信息。
     * 3.售后取件单：新运单规则：生成W单或VY单。调取运单来源：从运单处获取，调取运单新接口。
     * 4.配送异常类订单：新运单规则：T+原运单号,调取运单来源：从运单处获得，调取运单新接口。
     * 5.返单换单：1）新运单规则：F+原运单号  或  F+8位数字,调取运单来源：从运单处获得，调取运单新接口。2）分拣中心集中换单，暂时不做。
     * @param oldWaybillCode 原单号
     * @return
     */
    @Override
    public InvokeResult<String> getNewWaybillCode(String oldWaybillCode) {
        if(SerialRuleUtil.isMatchReceivePackageNo(oldWaybillCode)){
            return receiveManager.queryDeliveryIdByOldDeliveryId(oldWaybillCode);
        }else{
            InvokeResult<String> targetResult=new InvokeResult<String>();
            try {
                BaseEntity<String> result = this.baseErpQuerySafWSInfoSafService.getChangeWaybillCode(oldWaybillCode);
                if(null!=result){
                    targetResult.setCode(result.getResultCode());
                    targetResult.setMessage(result.getMessage());
                    targetResult.setData(result.getData());
                }else{
                    targetResult.customMessage(InvokeResult.RESULT_NULL_CODE,InvokeResult.RESULT_NULL_MESSAGE);
                }
            }catch (Exception ex){
                targetResult.error(ex);
            }
            return targetResult;
        }

    }

    @Override
    public InvokeResult<Boolean> exchangeOwnWaybill(String oldWaybillCode, Integer userId, String userRealName, Integer siteId, String siteName) {
        if(logger.isInfoEnabled()){
            logger.info(MessageFormat.format("执行自营换单waybillCode={0},userId={1},userRealName={2},siteId={3},siteName={4}",oldWaybillCode,userId,userRealName,siteId,siteName));
        }
        InvokeResult<Boolean> result=new InvokeResult<Boolean>();
        List<WaybillSyncParameter> parameters=new ArrayList<WaybillSyncParameter>(1);
        WaybillSyncParameter para=new WaybillSyncParameter();
        para.setOperatorCode(oldWaybillCode);
        para.setOperatorId(userId);
        para.setOperatorName(userRealName);
        para.setOperateTime(new Date());
        para.setZdId(siteId);
        para.setZdName(siteName);
        WaybillSyncParameterExtend extend = new WaybillSyncParameterExtend();
        extend.setOperateType(EXCHANGE_OWN_WAYBILL_OP_TYPE);
        extend.setTaskId(System.currentTimeMillis());
        para.setWaybillSyncParameterExtend(extend);
        try {
            BaseStaffSiteOrgDto siteDomain = siteService.getSite(siteId);
            if(null!=siteDomain){
                para.setZdType(siteDomain.getSiteType());
                para.setOrgId(siteDomain.getOrgId());
                para.setOrgName(siteDomain.getOrgName());
            }else {
                result.customMessage(2, MessageFormat.format("获取站点【ID={0}】信息为空",siteId));
                logger.error(MessageFormat.format("自营换单获取站点【ID={0}】信息为空",siteId));
                return result;
            }
        }catch (Exception ex){
            logger.error("获取站点",ex);
            result.error("获取站点异常"+ex.getMessage());
            return result;
        }
        try{
            com.jd.etms.waybill.domain.BaseEntity<List<String>> waybillResult = this.waybillSyncApi.batchUpdateWaybillByOperatorCode(parameters, EXCHANGE_OWN_WAYBILL_OP_TYPE);
            if(Integer.valueOf(1).equals(waybillResult.getResultCode())){
                result.success();
                result.setData(true);
            }else {
                result.customMessage(waybillResult.getResultCode(),"推送运单换单信息失败");
                logger.error(MessageFormat.format("推送运单自营换单信息失败{0}", oldWaybillCode));
            }
        }catch (Exception ex){
            logger.error("推送运单自营换单信息异常",ex);
            result.error("推送运单自营换单信息异常");
        }
        return result;
    }


    private String createMqBody(String orderId) {
        StringBuffer sb = new StringBuffer();
        sb.append("<?xml version=\"1.0\" encoding=\"utf-16\"?>");
        sb.append("<OrderTaskInfo xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\">");
        sb.append("<OrderId>");
        sb.append(orderId);
        sb.append("</OrderId>");
        sb.append("<OrderType>");
        sb.append(0);
        sb.append("</OrderType>");
        sb.append("<MessageType>");
        sb.append(REVERSE_PRINT_MQ_MESSAGE_CATEGORY);
        sb.append("</MessageType>");
        sb.append("<OperatTime>");
        sb.append(DateHelper.formatDateTime(new Date()));
        sb.append("</OperatTime>");
        sb.append("</OrderTaskInfo>");
        return sb.toString();
    }
}
