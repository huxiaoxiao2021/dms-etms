package com.jd.bluedragon.distribution.reverse.service;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.common.domain.RepeatPrint;
import com.jd.bluedragon.common.domain.Waybill;
import com.jd.bluedragon.common.service.WaybillCommonService;
import com.jd.bluedragon.core.base.ReceiveManager;
import com.jd.bluedragon.core.base.WaybillQueryManager;
import com.jd.bluedragon.core.jmq.producer.DefaultJMQProducer;
import com.jd.bluedragon.distribution.abnormalwaybill.domain.AbnormalWayBill;
import com.jd.bluedragon.distribution.abnormalwaybill.service.AbnormalWayBillService;
import com.jd.bluedragon.distribution.api.request.ReversePrintRequest;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.base.service.SiteService;
import com.jd.bluedragon.distribution.jsf.service.JsfSortingResourceService;
import com.jd.bluedragon.distribution.message.OwnReverseTransferDomain;
import com.jd.bluedragon.distribution.operationLog.domain.OperationLog;
import com.jd.bluedragon.distribution.operationLog.service.OperationLogService;
import com.jd.bluedragon.distribution.packageToMq.service.IPushPackageToMqService;
import com.jd.bluedragon.distribution.popPrint.domain.PopPrint;
import com.jd.bluedragon.distribution.popPrint.service.PopPrintService;
import com.jd.bluedragon.distribution.task.domain.Task;
import com.jd.bluedragon.distribution.task.service.TaskService;
import com.jd.bluedragon.distribution.waybill.domain.WaybillStatus;
import com.jd.bluedragon.distribution.waybill.service.WaybillService;
import com.jd.bluedragon.dms.utils.BusinessUtil;
import com.jd.bluedragon.dms.utils.WaybillUtil;
import com.jd.bluedragon.utils.BusinessHelper;
import com.jd.bluedragon.utils.DateHelper;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.bluedragon.utils.SerialRuleUtil;
import com.jd.bluedragon.utils.StringHelper;
import com.jd.etms.waybill.api.WaybillPickupTaskApi;
import com.jd.etms.waybill.domain.BaseEntity;
import com.jd.etms.waybill.domain.PickupTask;
import com.jd.etms.waybill.domain.WaybillManageDomain;
import com.jd.etms.waybill.dto.BigWaybillDto;
import com.jd.etms.waybill.dto.WChoice;
import com.jd.ql.basic.dto.BaseStaffSiteOrgDto;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;
import java.util.Date;
import java.util.List;

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

    private static final Integer PICKUP_FINISHED_STATUS=Integer.valueOf(20); //取件单完成态

    private static final Integer PICKUP_DIFFER_DAYS = 15;   //取件单创建时间和现在相差天数

    @Autowired
    private TaskService taskService;

    @Autowired
    private OperationLogService operationLogService;

    @Autowired
    private IPushPackageToMqService pushMqService;

    @Autowired
    private ReceiveManager receiveManager;

    @Autowired
    private DefaultJMQProducer reverseChangeWaybillCodeMQ;

    @Autowired
    private WaybillPickupTaskApi waybillPickupTaskApi;

    @Autowired
    private SiteService siteService;

    @Autowired
    private WaybillCommonService waybillCommonService;

    @Autowired
    private WaybillService waybillService;

    @Autowired
    AbnormalWayBillService abnormalWayBillService;

    @Autowired
    @Qualifier("ownWaybillTransformMQ")
    private DefaultJMQProducer ownWaybillTransformMQ;

    @Autowired(required = false)
    private JsfSortingResourceService jsfSortingResourceService;

    @Autowired
    private WaybillQueryManager waybillQueryManager;

    @Autowired
    private PopPrintService popPrintService;
    @Autowired
    private ReverseSpareEclp reverseSpareEclp;

    /**
     * 处理逆向打印数据
     * 【1：发送全程跟踪 2：写分拣中心操作日志】
     * @param domain 打印提交数据
     * @return
     */
    @Override
    public boolean handlePrint(ReversePrintRequest domain) {

        domain.setOperateUnixTime(DateHelper.adjustTimestampToJava(domain.getOperateUnixTime()));

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
        status.setPackageCode(domain.getOldCode()); //包裹号赋值运单号，防止运单在包裹上进行遍历
        tTask.setBody(JsonHelper.toJson(status));
        /**
         * 原外单添加换单全程跟踪
         * 只有在此单第一次打印的时候才记录 update by liuduo 2018-08-02
         */
        List<PopPrint> popPrintList = this.popPrintService.findAllByWaybillCode(domain.getNewCode());
        if(null==popPrintList||popPrintList.size()==0){
            taskService.add(tTask, true);
        }


        tTask.setKeyword1(domain.getNewCode());
        if(StringUtils.isBlank(domain.getNewPackageCode())){
            status.setPackageCode(domain.getNewCode()); //新单添加运单号 防止运单在包裹上进行遍历  只有客户端未升级才会有这种情况
        }else{
            status.setPackageCode(domain.getNewPackageCode()); //新单添加包裹号
        }
        status.setWaybillCode(domain.getNewCode());
        status.setRemark("换单打印，原运单号"+domain.getOldCode());
        tTask.setBody(JsonHelper.toJson(status));
        /**
         * 新外单添加全程跟踪
         */
        taskService.add(tTask);
//        this.logger.info(REVERSE_PRINT_MQ_TOPIC+createMqBody(domain.getOldCode())+domain.getOldCode());
        //pushMqService.pubshMq(REVERSE_PRINT_MQ_TOPIC, createMqBody(domain.getOldCode()), domain.getOldCode());
        //  这里将要下掉  modified by zhanglei 20161025
//        bdBlockerCompleteMQ.sendOnFailPersistent(domain.getOldCode(),createMqBody(domain.getOldCode()));
        /**
         * 逆向换单打印发送换单mq added by zhanglei 20161123
         */

        try {
            reverseChangeWaybillCodeMQ.sendOnFailPersistent(domain.getNewCode(), getReversePrintMqBody(domain));
        }catch (Exception e){
            logger.error("发送逆向换单mq失败,新单号为："+domain.getNewCode(),e);
        }
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
     * added by zhanglei
     * 获取换单打印mq消息体 消息体中增加dmsDisCode字段  当在分拣中心换单打印时 值为siteCode 当在站点换单打印时 值为站点对应的分拣中心code
     * 增加字段原因是为了兼容性 以后如果有别的业务消费这个mq 不会覆盖掉原始换单地点
     * @param domain
     * @return
     */
    private String getReversePrintMqBody(ReversePrintRequest domain){
        Integer siteCode = domain.getSiteCode();
        BaseStaffSiteOrgDto siteDomain = siteService.getSite(siteCode);
        if(siteDomain == null){
            return JsonHelper.toJsonUseGson(domain);
        }
        int siteType = siteDomain.getSiteType();
        if(siteType == 64){
            domain.setDmsDisCode(domain.getSiteCode());
            return JsonHelper.toJsonUseGson(domain);
        }else{
            domain.setDmsDisCode(siteDomain.getDmsId());
            return JsonHelper.toJsonUseGson(domain);
        }
    }

    /**
     * 根据原单号获取对应的新单号
     * 1.自营拒收：新运单规则：T+原运单号。调取运单来源：从运单处获取，调取运单新接口。
     * 2.外单拒收：新运单规则：生成新的V单。调取运单来源：1）从外单获得新外单单号。2）通过新外单单号从运单处调取新外单的信息。
     * 3.售后取件单：新运单规则：生成W单或VY单。调取运单来源：从运单处获取，调取运单新接口。
     * 4.配送异常类订单：新运单规则：T+原运单号,调取运单来源：从运单处获得，调取运单新接口。
     * 5.返单换单：1）新运单规则：F+原运单号  或  F+8位数字,调取运单来源：从运单处获得，调取运单新接口。2）分拣中心集中换单，暂时不做。
     * @param oldWaybillCode 原单号
     * @param isPickUpFinished 是否限制取件完成
     * @return
     */
    @Override
    @JProfiler(jKey = "DMSWEB.ReversePrint.getNewWaybillCode", mState = {JProEnum.TP,JProEnum.FunctionError})
    public InvokeResult<String> getNewWaybillCode(String oldWaybillCode, boolean isPickUpFinished) {
        if(oldWaybillCode.toUpperCase().startsWith("Q")) {
            InvokeResult<String> targetResult=new InvokeResult<String>();

            BaseEntity<PickupTask> result= waybillPickupTaskApi.getPickTaskByPickCode(oldWaybillCode);
            if(null!=result&&null!=result.getData()&&StringHelper.isNotEmpty(result.getData().getSurfaceCode())) {
                if(isPickUpFinished && !PICKUP_FINISHED_STATUS.equals(result.getData().getStatus())){
                    targetResult.customMessage(-1,"未操作取件完成无法打印面单");
                }else{
                    targetResult.setData(result.getData().getSurfaceCode());
                    targetResult.setMessage(result.getData().getServiceCode());
                }
            }else{
                targetResult.customMessage(-1,"没有获取到新的取件单");
            }
            return targetResult;
        }
        else{
                InvokeResult<String> targetResult=new InvokeResult<String>();
                InvokeResult<Waybill> result = this.waybillCommonService.getReverseWaybill(oldWaybillCode);
                targetResult.setCode(result.getCode());
                targetResult.setMessage(result.getMessage());
                if(result.getCode()==InvokeResult.RESULT_SUCCESS_CODE&&null!=result.getData()){
                    targetResult.setData(result.getData().getWaybillCode());
                    return targetResult;
                }

                if(WaybillUtil.isBusiWaybillCode(oldWaybillCode)){
                    return receiveManager.queryDeliveryIdByOldDeliveryId(oldWaybillCode);
                }else{
                    return targetResult;
                }
        }


    }

    @Override
    public InvokeResult<RepeatPrint> getNewWaybillCode1(String oldWaybillCode, boolean isPickUpFinished) {
        InvokeResult<RepeatPrint> targetResult=new InvokeResult<RepeatPrint>();
        RepeatPrint repeatPrint =new RepeatPrint();
        boolean isOverTime = false;
        if(oldWaybillCode.toUpperCase().startsWith("Q")) {
            BaseEntity<PickupTask> result = waybillCommonService.getPickupTask(oldWaybillCode);
            if(null!=result&&null!=result.getData()&&StringHelper.isNotEmpty(result.getData().getSurfaceCode())) {
                if(isPickUpFinished && !PICKUP_FINISHED_STATUS.equals(result.getData().getStatus())){
                    targetResult.customMessage(-1,"未操作取件完成无法打印面单");
                }else{
                    StringBuilder errorMessage = new StringBuilder();
                    targetResult.setMessage(result.getData().getServiceCode());
                    repeatPrint.setNewWaybillCode(result.getData().getSurfaceCode());
                    repeatPrint.setOldWaybillCode(oldWaybillCode);
                    repeatPrint.setOverTime(isExceed(result.getData().getSurfaceCode(),errorMessage));
                    if(!"".equals(errorMessage.toString())){
                        targetResult.customMessage(-1,errorMessage.toString());
                    }
                    targetResult.setData(repeatPrint);
                }
            }else{
                targetResult.customMessage(-1,"没有获取到新的取件单");
            }
            return targetResult;
        }
        else{
            InvokeResult<Waybill> result = this.waybillCommonService.getReverseWaybill(oldWaybillCode);
            targetResult.setCode(result.getCode());
            targetResult.setMessage(result.getMessage());
            repeatPrint.setOverTime(isOverTime);
            if(result.getCode()==InvokeResult.RESULT_SUCCESS_CODE&&null!=result.getData()){
                repeatPrint.setNewWaybillCode(result.getData().getWaybillCode());
                targetResult.setData(repeatPrint);
                isHasProductInfoOfPureMatch(targetResult);
                return targetResult;
            }

            if(WaybillUtil.isBusiWaybillCode(oldWaybillCode)){
                targetResult = receiveManager.queryDeliveryIdByOldDeliveryId1(oldWaybillCode);
                isHasProductInfoOfPureMatch(targetResult);
            }
            return targetResult;
        }
    }

    /**
     * 纯配外单判断是否有商品信息
     * @param targetResult
     * @return
     */
    private void isHasProductInfoOfPureMatch(InvokeResult<RepeatPrint> targetResult) {
        String errorMessage = null;
        String newWaybillCode = targetResult.getData()==null?null:targetResult.getData().getNewWaybillCode();
        //1.新单号不存在
        if(StringHelper.isEmpty(targetResult.getData().getNewWaybillCode())){
            return;
        }
        try{
            WChoice wChoice = new WChoice();
            wChoice.setQueryWaybillC(true);
            wChoice.setQueryWaybillExtend(true);
            wChoice.setQueryGoodList(true);
            BaseEntity<BigWaybillDto> baseEntity = waybillQueryManager.getDataByChoiceNoCache(newWaybillCode,wChoice);

            if(baseEntity != null && baseEntity.getData() != null && baseEntity.getData().getWaybill() != null){

                com.jd.etms.waybill.domain.Waybill waybill = baseEntity.getData().getWaybill();
                //2.非纯配外单直接返回
                if(!StringHelper.isEmpty(waybill.getWaybillSign())
                        && !BusinessUtil.isPurematch(waybill.getWaybillSign())){
                    return;
                }
                //3.有商品信息直接返回
                if(baseEntity.getData().getGoodsList() != null
                        && baseEntity.getData().getGoodsList().size() > 0){
                    return;
                }else {
                    errorMessage = "新单没有商品信息，请登陆慧眼录入!";
                }

            }else{
                errorMessage = "新单" + newWaybillCode + "无运单信息!";
            }

        }catch (Exception e){
            logger.error("通过运单号"+newWaybillCode+"查询运单信息异常!",e);
            errorMessage = InvokeResult.SERVER_ERROR_MESSAGE;
        }

        if(!StringHelper.isEmpty(errorMessage)){
            targetResult.setCode(InvokeResult.RESULT_THIRD_ERROR_CODE);
            targetResult.setMessage(errorMessage);
        }

    }

    /**
     *  判断取件单换单后的新单的创建时间是否超过15天
     *
     * */
    private Boolean isExceed(String waybillCode,StringBuilder errorMessage) {

        BaseEntity<BigWaybillDto> result = null;
        try {
            result = waybillQueryManager.getDataByChoice(waybillCode,
                    true, true, true, true, true, false, false);
            if(result != null && result.getData() != null && result.getData().getWaybill() != null &&
                    result.getData().getWaybill().getFirstTime() != null)
            {
                Date diffDate = DateHelper.addDate(new Date(),-PICKUP_DIFFER_DAYS);
                return result.getData().getWaybill().getFirstTime().before(diffDate);
            }
            this.logger.warn("通过运单号" + waybillCode + "查询运单数据为空");
        } catch (Exception e) {
            StringBuilder errorMsg = new StringBuilder(
                    "中心服务调用运单getDataByChoice出错").append("waybillCode=")
                    .append(waybillCode).append("isWaybillC")
                    .append(true).append("isWaybillE").append(true)
                    .append("isWaybillM").append(true)
                    .append("isPackList").append(true);
            logger.error(errorMsg, e);
        }
        errorMessage.append("新单"+waybillCode+"的运单信息为空，请联系it人员处理!");
        return true;
    }

    @Override
    public InvokeResult<Boolean> exchangeOwnWaybill(OwnReverseTransferDomain domain) {
        if(logger.isInfoEnabled()){
            logger.info(MessageFormat.format("执行自营换单waybillCode={0},userId={1},userRealName={2},siteId={3},siteName={4}",domain.getWaybillCode(),domain.getUserId(),domain.getUserRealName(),domain.getSiteId(),domain.getSiteName()));
        }
        InvokeResult<Boolean> result=new InvokeResult<Boolean>();
        domain.setOperateTime(new Date());
        try {
            BaseStaffSiteOrgDto siteDomain = siteService.getSite(domain.getSiteId());
            if(null!=siteDomain){
                domain.setSiteType(siteDomain.getSiteType());
                domain.setOrgName(siteDomain.getOrgName());
                domain.setOrgId(siteDomain.getOrgId());
            }else {
                result.customMessage(2, MessageFormat.format("获取站点【ID={0}】信息为空",domain.getSiteId()));
                logger.error(MessageFormat.format("自营换单获取站点【ID={0}】信息为空",domain.getSiteId()));
                return result;
            }
        }catch (Exception ex){
            logger.error("获取站点",ex);
            result.error("获取站点异常"+ex.getMessage());
            return result;
        }
        try {
            Integer featureType = jsfSortingResourceService.getWaybillCancelByWaybillCode(domain.getWaybillCode());
            domain.setSickWaybillFlag(featureType == null ? Constants.FEATURE_TYPCANCEE_UNSICKL :featureType);//30-病单，31-取消病单，32- 非病单
        }catch (Exception ex){
            logger.error("获取订单拦截信息 waybill_cancel 的病单标识异常：",ex);
            result.error("获取订单拦截信息异常");
            return result;
        }
        try{
            ownWaybillTransformMQ.send(domain.getWaybillCode(), JsonHelper.toJson(domain));
            result.success();
            result.setData(Boolean.TRUE);
        }catch (Exception ex){
            logger.error("推送运单自营换单MQ异常",ex);
            result.error("推送运单自营换单MQ异常");
        }
        return result;
    }


    /**
     * 逆向换单限制校验
     * 拒收和异常处理的运单才可以执行逆向换单（该限制仅限手工逆向换单操作）
     * （纯配外单 且 理赔完成 且 物权归京东的才可以执行逆向换单）
     * @param wayBillCode
     * @return
     */
    @Override
    @JProfiler(jKey = "DMSWEB.ReversePrintServiceImpl.checkWayBillForExchange", mState = {JProEnum.TP, JProEnum.FunctionError})
    public InvokeResult<Boolean> checkWayBillForExchange(String wayBillCode, Integer siteCode){
        if(WaybillUtil.isPackageCode(wayBillCode)){
            wayBillCode = SerialRuleUtil.getWaybillCode(wayBillCode);
        }
        InvokeResult<Boolean> result = new InvokeResult();
        result.setData(true);
        //1.运单号为空
        if(StringUtils.isBlank(wayBillCode) || siteCode == null){
            result.setData(false);
            result.setMessage("运单号或站点信息为空");
            return result;
        }

        //2.获取运单信息判断是否拒收或妥投
        WChoice wChoice = new WChoice();
        wChoice.setQueryWaybillC(true);
        wChoice.setQueryWaybillM(true);
        BaseEntity<BigWaybillDto> baseEntity = waybillQueryManager.getDataByChoice(wayBillCode, wChoice);
        BigWaybillDto waybillDto = null;
        if(baseEntity != null && baseEntity.getData() != null){
            waybillDto = baseEntity.getData();
        }
        if(waybillDto == null || waybillDto.getWaybill() == null){
            result.setData(false);
            result.setMessage("运单接口调用返回结果为空");
            return result;
        }
        WaybillManageDomain wdomain = waybillDto.getWaybillState();
        //2.1妥投运单，不可以操作逆向换单
        if(wdomain != null && Constants.WAYBILL_DELIVERED_CODE.equals(wdomain.getWaybillState())){
            result.setData(false);
            result.setMessage("该订单已经妥投，不能触发逆向新单");
            return result;
        }
        //2.2拒收运单，可以操作逆向换单
        if(wdomain != null && Constants.WAYBILL_REJECT_CODE.equals(wdomain.getWaybillState())){
            reverseSpareEclp.checkIsPureMatch(waybillDto.getWaybill().getWaybillCode(),waybillDto.getWaybill().getWaybillSign(),result);
            return result;
        }
        //3.查询运单是否操作异常处理
        AbnormalWayBill abnormalWayBill = abnormalWayBillService.getAbnormalWayBillByWayBillCode(wayBillCode, siteCode);
        //异常操作运单，可以操作逆向换单
        if(abnormalWayBill == null || !wayBillCode.equals(abnormalWayBill.getWaybillCode())){
            result.setData(false);
            result.setMessage("订单未操作拒收或分拣异常处理扫描，请先操作");
        }else{
            reverseSpareEclp.checkIsPureMatch(waybillDto.getWaybill().getWaybillCode(),waybillDto.getWaybill().getWaybillSign(),result);
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
