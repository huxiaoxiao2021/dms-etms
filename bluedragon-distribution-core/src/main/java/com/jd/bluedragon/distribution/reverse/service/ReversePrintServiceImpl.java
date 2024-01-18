package com.jd.bluedragon.distribution.reverse.service;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.common.domain.RepeatPrint;
import com.jd.bluedragon.common.domain.Waybill;
import com.jd.bluedragon.common.service.WaybillCommonService;
import com.jd.bluedragon.core.base.BaseMajorManager;
import com.jd.bluedragon.core.base.BaseMinorManager;
import com.jd.bluedragon.core.base.LDOPManager;
import com.jd.bluedragon.core.base.OBCSManager;
import com.jd.bluedragon.core.base.ReceiveManager;
import com.jd.bluedragon.core.base.WaybillQueryManager;
import com.jd.bluedragon.core.base.WaybillTraceManager;
import com.jd.bluedragon.core.hint.constants.HintCodeConstants;
import com.jd.bluedragon.core.hint.service.HintService;
import com.jd.bluedragon.core.jmq.producer.DefaultJMQProducer;
import com.jd.bluedragon.core.jsf.eclp.EclpImportServiceManager;
import com.jd.bluedragon.distribution.abnormalwaybill.domain.AbnormalWayBill;
import com.jd.bluedragon.distribution.abnormalwaybill.service.AbnormalWayBillService;
import com.jd.bluedragon.distribution.api.Response;
import com.jd.bluedragon.distribution.api.request.ReversePrintRequest;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.base.domain.JdCancelWaybillResponse;
import com.jd.bluedragon.distribution.base.service.SiteService;
import com.jd.bluedragon.distribution.base.service.SysConfigService;
import com.jd.bluedragon.distribution.business.entity.BusinessReturnAdress;
import com.jd.bluedragon.distribution.business.entity.BusinessReturnAdressStatusEnum;
import com.jd.bluedragon.distribution.business.service.BusinessReturnAdressService;
import com.jd.bluedragon.distribution.businessIntercept.dto.SaveDisposeAfterInterceptMsgDto;
import com.jd.bluedragon.distribution.businessIntercept.helper.BusinessInterceptConfigHelper;
import com.jd.bluedragon.distribution.businessIntercept.service.IBusinessInterceptReportService;
import com.jd.bluedragon.distribution.command.JdResult;
import com.jd.bluedragon.distribution.jsf.service.JsfSortingResourceService;
import com.jd.bluedragon.distribution.message.OwnReverseTransferDomain;
import com.jd.bluedragon.distribution.operationLog.domain.OperationLog;
import com.jd.bluedragon.distribution.operationLog.service.OperationLogService;
import com.jd.bluedragon.distribution.print.domain.ChangeOrderPrintMq;
import com.jd.bluedragon.distribution.print.domain.WaybillPrintOperateTypeEnum;
import com.jd.bluedragon.distribution.qualityControl.service.QualityControlService;
import com.jd.bluedragon.distribution.record.entity.DmsHasnoPresiteWaybillMq;
import com.jd.bluedragon.distribution.record.enums.DmsHasnoPresiteWaybillMqOperateEnum;
import com.jd.bluedragon.distribution.record.service.WaybillHasnoPresiteRecordService;
import com.jd.bluedragon.distribution.reverse.domain.BackAddressDTOExt;
import com.jd.bluedragon.distribution.reverse.domain.ExchangeWaybillDto;
import com.jd.bluedragon.distribution.reverse.domain.LocalClaimInfoRespDTO;
import com.jd.bluedragon.distribution.reverse.domain.TwiceExchangeRequest;
import com.jd.bluedragon.distribution.reverse.domain.TwiceExchangeResponse;
import com.jd.bluedragon.distribution.task.domain.Task;
import com.jd.bluedragon.distribution.task.service.TaskService;
import com.jd.bluedragon.distribution.ver.domain.Site;
import com.jd.bluedragon.distribution.waybill.domain.CancelWaybill;
import com.jd.bluedragon.distribution.waybill.domain.WaybillCancelInterceptTypeEnum;
import com.jd.bluedragon.distribution.waybill.domain.WaybillStatus;
import com.jd.bluedragon.distribution.waybill.service.WaybillCancelService;
import com.jd.bluedragon.distribution.waybill.service.WaybillService;
import com.jd.bluedragon.dms.utils.BusinessUtil;
import com.jd.bluedragon.dms.utils.DmsConstants;
import com.jd.bluedragon.dms.utils.WaybillSignConstants;
import com.jd.bluedragon.dms.utils.WaybillUtil;
import com.jd.bluedragon.utils.*;
import com.jd.dms.ver.domain.WaybillCancelJsfResponse;
import com.jd.eclp.bbp.notice.domain.dto.BatchImportDTO;
import com.jd.eclp.bbp.notice.enums.ChannelEnum;
import com.jd.eclp.bbp.notice.enums.PostTypeEnum;
import com.jd.eclp.bbp.notice.enums.ResolveTypeEnum;
import com.jd.etms.waybill.api.WaybillAddApi;
import com.jd.etms.waybill.api.WaybillPickupTaskApi;
import com.jd.etms.waybill.domain.BaseEntity;
import com.jd.etms.waybill.domain.PackageState;
import com.jd.etms.waybill.domain.PickupTask;
import com.jd.etms.waybill.domain.WaybillManageDomain;
import com.jd.etms.waybill.dto.BigWaybillDto;
import com.jd.etms.waybill.dto.SwitchWaybillResultDto;
import com.jd.etms.waybill.dto.WChoice;
import com.alibaba.fastjson.JSON;
import com.jd.etms.waybill.dto.WaybillSwitchDto;
import com.jd.jmq.common.exception.JMQException;
import com.jd.ldop.basic.api.BasicTraderIntegrateOutAPI;
import com.jd.ldop.basic.dto.BasicTraderInfoDTO;
import com.jd.ldop.basic.dto.BasicTraderIntegrateDTO;
import com.jd.ldop.basic.dto.ResponseDTO;
import com.jd.ldop.basic.dto.enums.signAreaEnums;
import com.jd.ldop.business.api.dto.request.BackAddressDTO;
import com.jd.ldop.center.api.refund.dto.RefundApplyDTO;
import com.jd.ql.basic.dto.BaseSiteInfoDto;
import com.jd.ql.basic.dto.BaseStaffSiteOrgDto;
import com.jd.ql.dms.common.cache.CacheService;
import com.jd.ql.dms.common.constants.DisposeNodeConstants;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.text.MessageFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import static com.jd.bluedragon.core.hint.constants.HintCodeConstants.WAYBILL_ZERO_WEIGHT_INTERCEPT_HINT_CODE;

/**
 * 逆向换单打印
 * Created by wangtingwei on 14-8-7.
 */
@Service("ReversePrintService")
public class ReversePrintServiceImpl implements ReversePrintService {

    private final Logger log = LoggerFactory.getLogger(ReversePrintServiceImpl.class);

    /**
     * 二次换单站内信通知时间缓存前缀
     * */
    private static final String RETURN_ADDRESS_REDIS_KEY_PREFIX = "RETURN_ADDRESS_REDIS_KEY_PREFIX-";

    private static final String REVERSE_PRINT_MQ_MESSAGE_CATEGORY="BLOCKER_QUEUE_DMS_REVERSE_PRINT";

    private static final Integer PICKUP_FINISHED_STATUS=Integer.valueOf(20); //取件单完成态

    private static final Integer PICKUP_DIFFER_DAYS = 15;   //取件单创建时间和现在相差天数
    //退货站内通知间隔天数
    private static final int NOTIC_DIFFER_DAYS = 1;
    
    @Autowired
    private TaskService taskService;

    @Autowired
    private OperationLogService operationLogService;

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
    AbnormalWayBillService abnormalWayBillService;

    @Autowired
    private WaybillCancelService waybillCancelService;

    @Autowired
    @Qualifier("ownWaybillTransformMQ")
    private DefaultJMQProducer ownWaybillTransformMQ;

    @Autowired(required = false)
    private JsfSortingResourceService jsfSortingResourceService;

    @Autowired
    private WaybillQueryManager waybillQueryManager;

    @Autowired
    private ReverseSpareEclp reverseSpareEclp;

    @Autowired
    private BaseMajorManager baseMajorManager;

    @Autowired
    private QualityControlService qualityControlService;

    @Autowired
    @Qualifier("jimdbCacheService")
    private CacheService cacheService;
    
    @Autowired
	@Qualifier("obcsManager")
	private OBCSManager obcsManager;

    @Autowired
    private WaybillService waybillService;
    
    @Autowired
	@Qualifier("eclpImportServiceManager")
	private EclpImportServiceManager eclpImportServiceManager;

    @Autowired
	@Qualifier("businessReturnAdressService")
	private BusinessReturnAdressService businessReturnAdressService;

    @Autowired
	private LDOPManager lDOPManager;  
    
    @Autowired
    private BaseMinorManager baseMinorManager;

    @Autowired
    private BasicTraderIntegrateOutAPI basicTraderIntegrateOutAPI;

    @Autowired
    private WaybillTraceManager waybillTraceManager;
    
    @Autowired
    private WaybillHasnoPresiteRecordService waybillHasnoPresiteRecordService;

    private static String EXCHANGE_PRINT_BEGIN_KEY = "dms_new_waybill_print_";
    /**
     * 退货地址维护通知key
     */
	@Value("${beans.ReversePrintServiceImpl.noticeKey}")
    private String noticeKey;
    /**
     * 退货地址维护通知标题
     */
	@Value("${beans.ReversePrintServiceImpl.noticeTitle}")
    private String noticeTitle;
    /**
     * 退货地址维护通知内容
     */
	@Value("${beans.ReversePrintServiceImpl.noticeContent}")
    private String noticeContent;
    /**
     * 退货地址维护通知-跳转链接
     */
	@Value("${beans.ReversePrintServiceImpl.noticeHandleLink}")
    private String noticeHandleLink;

    /**
     * 拦截报表服务
     */
    @Autowired
    private IBusinessInterceptReportService businessInterceptReportService;

    @Autowired
    private BusinessInterceptConfigHelper businessInterceptConfigHelper;

    @Autowired
    @Qualifier("changeWaybillPrintProducer")
    private DefaultJMQProducer changeWaybillPrintProducer;

    @Autowired
    private WaybillAddApi waybillAddApi;

    @Autowired
    private SysConfigService sysConfigService;

    @Qualifier("bdBlockerCompleteMQ")
    @Autowired
    private DefaultJMQProducer bdBlockerCompleteMQ;

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
        status.setReturnWaybillCode(domain.getNewCode());
        status.setRemark("换单打印，新运单号"+domain.getNewCode());
        status.setCreateSiteCode(domain.getSiteCode());
        status.setCreateSiteName(domain.getSiteName());
        status.setPackageCode(domain.getOldCode()); //包裹号赋值运单号，防止运单在包裹上进行遍历
        tTask.setBody(JsonHelper.toJson(status));
        /**
         * 原外单添加换单全程跟踪
         * 只有在此单第一次打印的时候才记录 update by liuduo 2018-08-02
         */
        boolean sendOldWaybillOnlyOneFlag = true;
        try{
            //三天校验
            Boolean isSucdess = cacheService.setNx(EXCHANGE_PRINT_BEGIN_KEY+domain.getNewCode(), "1", 3, TimeUnit.DAYS);
            if(!isSucdess){
                //说明非第一次
                sendOldWaybillOnlyOneFlag = false;
            }
        }catch(Exception e){
            this.log.error("判断原单需要换单全程跟踪异常:{}", JsonHelper.toJson(domain), e);
        }
        if(sendOldWaybillOnlyOneFlag){
            //原运单的全程跟踪
            taskService.add(tTask, true);

            // 发送拦截报表  与方刚沟通只需要发送原运单维度的消息即可，所以调整到这里，减少频繁发送的问题
            this.sendDisposeAfterInterceptMsg(domain);

            //发送换单打印消息 三无使用 ，与亚国沟通也放在这里  减少频繁发送的问题
            ChangeOrderPrintMq changeOrderPrintMq = convert2PushPrintRecordDto(domain);
            try {
                log.info("ReversePrintServiceImpl.handlePrint-->发送换单打印消息数据{}",JsonHelper.toJson(changeOrderPrintMq));
                changeWaybillPrintProducer.send(domain.getOldCode(),JsonHelper.toJson(changeOrderPrintMq));
            } catch (JMQException e) {
                log.error("ReversePrintServiceImpl.handlePrint-->发送换单打印消息数据失败{}",JsonHelper.toJson(domain),e);
            }

            //全量接单也是此逻辑 减少频繁发送的问题
            waybillHasnoPresiteRecordService.sendDataChangeMq(toDmsHasnoPresiteWaybillMq(domain));
        }

        tTask.setKeyword1(domain.getNewCode());
        if(StringUtils.isBlank(domain.getNewPackageCode())){
            status.setPackageCode(domain.getNewCode()); //新单添加运单号 防止运单在包裹上进行遍历  只有客户端未升级才会有这种情况
        }else{
            status.setPackageCode(domain.getNewPackageCode()); //新单添加包裹号
        }
        status.setWaybillCode(domain.getNewCode());
        status.setRemark("换单打印，原运单号"+domain.getOldCode());
        status.setReturnWaybillCode(domain.getOldCode());
        tTask.setBody(JsonHelper.toJson(status));
        /**
         * 新外单添加全程跟踪
         */
        taskService.add(tTask);
//        this.log.info(REVERSE_PRINT_MQ_TOPIC+createMqBody(domain.getOldCode())+domain.getOldCode());
        //pushMqService.pubshMq(REVERSE_PRINT_MQ_TOPIC, createMqBody(domain.getOldCode()), domain.getOldCode());
        //  这里将要下掉  modified by zhanglei 20161025
//        bdBlockerCompleteMQ.sendOnFailPersistent(domain.getOldCode(),createMqBody(domain.getOldCode()));
        
        // 推送拦截系统
        pushMq2Block(domain);
        
        /**
         * 逆向换单打印发送换单mq added by zhanglei 20161123
         */

        try {
            reverseChangeWaybillCodeMQ.sendOnFailPersistent(domain.getNewCode(), getReversePrintMqBody(domain));
        }catch (Exception e){
            log.error("发送逆向换单mq失败,新单号为：{}", domain.getNewCode(),e);
        }
        //存在全量接单失败拦截，消息触发退款
        doRefundApply(domain);
        OperationLog operationLog=new OperationLog();
        operationLog.setCreateTime(new Date());
        operationLog.setRemark("【外单逆向换单打印】原单号："+domain.getOldCode()+"新单号："+domain.getNewCode());
        operationLog.setWaybillCode(domain.getOldCode());
        operationLog.setCreateUser(domain.getStaffRealName());
        operationLog.setCreateUserCode(domain.getStaffId());
        operationLog.setCreateSiteCode(domain.getSiteCode());
        operationLog.setCreateSiteName(domain.getSiteName());
        operationLog.setMethodName("ReversePrintServiceImpl#handlePrint");
        operationLogService.add(operationLog);

        //换单打印判断是否发起分拣中心退货任务
        qualityControlService.generateSortingReturnTask(domain.getSiteCode(), domain.getOldCode(), domain.getNewPackageCode(), new Date(domain.getOperateUnixTime()));





        return true;
    }

    private void pushMq2Block(ReversePrintRequest request) {
        // 换单打印-满足以下情况则推送快速退款拦截消息
        // 1、分拣、接货仓场地操作
        // 2、自营生鲜
        // 3、正向单预分拣站点是自营营业部
        // 4、生成的JDT新单号的预分拣站点类型为仓或备件库
        int operateSiteCode = request.getSiteCode();
        Site operateSite = siteService.getOwnSite(operateSiteCode);
        if(!SiteHelper.isSortingCenter(operateSite) && !SiteHelper.isReceiveWms(operateSite)){
            return;
        }
        String oldWaybillCode = request.getOldCode();
        com.jd.etms.waybill.domain.Waybill oldWaybill = waybillService.getWaybillByWayCode(oldWaybillCode);
        if(oldWaybill == null || !BusinessUtil.isSelfFresh(oldWaybill.getSendPay())){
            return;
        }
        Site oldSite = siteService.getOwnSite(oldWaybill.getOldSiteId());
        if(!SiteHelper.isSelfSalesDeptSite(oldSite)){
            return;
        }
        com.jd.etms.waybill.domain.Waybill newWaybill = waybillService.getWaybillByWayCode(request.getNewCode());
        if(newWaybill == null){
            return;
        }
        Site newOldSite = siteService.getOwnSite(newWaybill.getOldSiteId());
        if(!SiteHelper.isSpsHouse(newOldSite) && !SiteHelper.isStoreHouse(newOldSite)){
            return;
        }
        if(log.isInfoEnabled()){
            log.info("老单号:{}操作换单打印推送快速退款拦截消息!", oldWaybillCode);
        }
        bdBlockerCompleteMQ.sendOnFailPersistent(oldWaybillCode,
                BusinessUtil.bdBlockerCompleteMQ(oldWaybillCode, DmsConstants.ORDER_TYPE_REVERSE, DmsConstants.MESSAGE_TYPE_REVERSE_PRINT, DateHelper.formatDateTimeMs(new Date())));
    }

    /**
     * 转换打印记录dto
     * @param request
     * @return
     */
    private ChangeOrderPrintMq convert2PushPrintRecordDto(ReversePrintRequest request){
        ChangeOrderPrintMq dto = new ChangeOrderPrintMq();
        dto.setOperateType(WaybillPrintOperateTypeEnum.SWITCH_BILL_PRINT.getType());
        dto.setWaybillCode(request.getOldCode());
        dto.setSiteCode(request.getSiteCode());
        dto.setUserErp(request.getStaffErpCode());
        dto.setUserCode(request.getStaffId());
        dto.setUserName(request.getStaffRealName());
        dto.setOperateTime(new Date());
        return dto;
    }
    /**
     * 发送换单打印mq
     * @param rePrintRecord
     */
    private DmsHasnoPresiteWaybillMq toDmsHasnoPresiteWaybillMq(ReversePrintRequest request) {
    	DmsHasnoPresiteWaybillMq dmsHasnoPresiteWaybillMq = new DmsHasnoPresiteWaybillMq();
    	dmsHasnoPresiteWaybillMq.setWaybillCode(request.getOldCode());
    	dmsHasnoPresiteWaybillMq.setOperateCode(DmsHasnoPresiteWaybillMqOperateEnum.EXCHANGE.getCode());
    	dmsHasnoPresiteWaybillMq.setOperateUserErp(request.getStaffErpCode());
    	dmsHasnoPresiteWaybillMq.setOperateUserName(request.getStaffRealName());
    	dmsHasnoPresiteWaybillMq.setOperateSiteCode(request.getSiteCode());
    	dmsHasnoPresiteWaybillMq.setOperateTime(new Date());
    	return dmsHasnoPresiteWaybillMq;
    }    
    /**
     * 存在全量接单失败14拦截时，现结订单触发退款
     * @param domain
     */
    private void doRefundApply(ReversePrintRequest domain) {
        //判断是否存在全量接单失败14拦截
    	boolean isFullOrderFail = waybillCancelService.isFullOrderFail(domain.getOldCode());
        if(!isFullOrderFail) {
        	return;
        }
    	//判断是否现结订单
    	boolean isXj = false;
    	BaseEntity<String> waybillSignResult = waybillQueryManager.getWaybillSignByWaybillCode(domain.getOldCode());
    	if(waybillSignResult != null
    			&& StringHelper.isNotEmpty(waybillSignResult.getData())) {
    		isXj = BusinessUtil.isPrepaid(waybillSignResult.getData());
    	}else {
    		log.warn("判断全量接单：获取waybillSgin失败！单号："+domain.getOldCode());
    		return;
    	}
    	if(!isXj) {
    		return;
    	}
    	//调用退款接口
    	RefundApplyDTO refundApplyDTO = new RefundApplyDTO();
    	refundApplyDTO.setDeliveryId(domain.getOldCode());
    	refundApplyDTO.setOperatorPin(domain.getStaffErpCode());
    	refundApplyDTO.setOperatorName(domain.getStaffRealName());
    	refundApplyDTO.setRefundReason("全量接单失败，逆向退回");
    	JdResult<String> result = lDOPManager.refundApply(refundApplyDTO);
        //记录日志
    	OperationLog operationLog=new OperationLog();
        operationLog.setCreateTime(new Date());
        operationLog.setRemark("【外单逆向换单打印】触发退款：原单号:"+domain.getOldCode()+"结果："+JsonHelper.toJson(result));
        operationLog.setWaybillCode(domain.getOldCode());
        operationLog.setCreateUser(domain.getStaffRealName());
        operationLog.setCreateUserCode(domain.getStaffId());
        operationLog.setCreateSiteCode(domain.getSiteCode());
        operationLog.setCreateSiteName(domain.getSiteName());
        operationLog.setMethodName("ReversePrintServiceImpl#handlePrint");
        operationLogService.add(operationLog);
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
                }else{
                	log.warn("获取新单号失败！waybillCommonService.getReverseWaybill。单号{}，返回结果：{}",oldWaybillCode,JsonHelper.toJson(result));
                }

                if(WaybillUtil.isBusiWaybillCode(oldWaybillCode)){
                    return receiveManager.queryDeliveryIdByOldDeliveryId(oldWaybillCode);
                }else{
                    return targetResult;
                }
        }


    }

    /**
     * 发送消息
     * @param reversePrintRequest 换单请求参数
     * @return 发送结果
     * @author fanggang7
     * @time 2020-12-14 14:11:58 周一
     */
    private Response<Boolean> sendDisposeAfterInterceptMsg(ReversePrintRequest reversePrintRequest){
        log.info("ReversePrintServiceImpl sendDisposeAfterInterceptMsg sendDisposeAfterInterceptMsg {}", JSON.toJSONString(reversePrintRequest));
        Response<Boolean> result = new Response<>();
        result.toSucceed();

        try {
            SaveDisposeAfterInterceptMsgDto saveDisposeAfterInterceptMsgDto = new SaveDisposeAfterInterceptMsgDto();
            saveDisposeAfterInterceptMsgDto.setBarCode(reversePrintRequest.getOldCode());
            saveDisposeAfterInterceptMsgDto.setDisposeNode(businessInterceptConfigHelper.getDisposeNodeByConstants(DisposeNodeConstants.EXCHANGE_WAYBILL));
            saveDisposeAfterInterceptMsgDto.setOperateTime(reversePrintRequest.getOperateUnixTime());
            saveDisposeAfterInterceptMsgDto.setOperateUserErp(reversePrintRequest.getStaffErpCode());
            saveDisposeAfterInterceptMsgDto.setOperateUserCode(reversePrintRequest.getStaffId());
            saveDisposeAfterInterceptMsgDto.setOperateUserName(reversePrintRequest.getStaffRealName());
            saveDisposeAfterInterceptMsgDto.setSiteCode(reversePrintRequest.getSiteCode());
            saveDisposeAfterInterceptMsgDto.setSiteName(reversePrintRequest.getSiteName());
            // log.info("ReversePrintServiceImpl sendDisposeAfterInterceptMsg saveDisposeAfterInterceptMsgDto: {}", JsonHelper.toJson(saveDisposeAfterInterceptMsgDto));
            businessInterceptReportService.sendDisposeAfterInterceptMsg(saveDisposeAfterInterceptMsgDto);
        } catch (Exception e) {
            log.error("ReversePrintServiceImpl sendDisposeAfterInterceptMsg exception, reversePrintRequest: [{}]" , JsonHelper.toJson(reversePrintRequest), e);
            result.toError("保存换单操作上报到拦截报表失败，失败提示：" + e.getMessage());
        }
        return result;
    }

    @Override
    public InvokeResult<RepeatPrint> getNewWaybillCode1(String oldWaybillCode, boolean isPickUpFinished) {
        InvokeResult<RepeatPrint> targetResult=new InvokeResult<RepeatPrint>();
        RepeatPrint repeatPrint =new RepeatPrint();
        boolean isOverTime = false;
        if(oldWaybillCode.toUpperCase().startsWith("Q")) {
            BaseEntity<PickupTask> result = waybillCommonService.getPickupTask(oldWaybillCode);
            if(null!=result&&null!=result.getData()&&StringHelper.isNotEmpty(result.getData().getSurfaceCode())) {
                if(isPickUpFinished && !StringHelper.isEmpty(result.getData().getQplCode())){
                    targetResult.customMessage(-1,"请使用"+result.getData().getQplCode()+"操作换单!");
                }else if(isPickUpFinished && !PICKUP_FINISHED_STATUS.equals(result.getData().getStatus())){
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
            if(log.isInfoEnabled()){
                log.info("waybillCommonService.getReverseWaybill req:{},resp:{}",oldWaybillCode,JsonHelper.toJson(result));
            }
            if(result.getCode()==InvokeResult.RESULT_SUCCESS_CODE&&null!=result.getData()){
                repeatPrint.setNewWaybillCode(result.getData().getWaybillCode());
                targetResult.setData(repeatPrint);
                if(targetResult.getCode() == -3){
                    isHasLPMatch(oldWaybillCode,targetResult);
                }
                isHasProductInfoOfPureMatch(targetResult);
                return targetResult;
            }else{
            	log.warn("获取新单号失败！waybillCommonService.getReverseWaybill。单号{}，返回结果：{}",oldWaybillCode,JsonHelper.toJson(result));
            }

            if(WaybillUtil.isBusiWaybillCode(oldWaybillCode)){
                targetResult = receiveManager.queryDeliveryIdByOldDeliveryId1(oldWaybillCode);
                if(log.isInfoEnabled()){
                    log.info("receiveManager.queryDeliveryIdByOldDeliveryId1 req:{},resp:{}",oldWaybillCode,JsonHelper.toJson(targetResult));
                }
                //针对返回码400时特殊处理
                if(400 == targetResult.getCode()){
                    isHasLPMatch(oldWaybillCode,targetResult);
                }
                isHasProductInfoOfPureMatch(targetResult);
            }
            return targetResult;
        }
    }

    /**
     * 检查是否有理赔
     * @param waybillCode
     * @param targetResult
     */
    private void isHasLPMatch(String waybillCode,InvokeResult<RepeatPrint> targetResult){
        CancelWaybill cancelWaybill = this.waybillService.checkClaimDamagedCancelWaybill(waybillCode);
        if(cancelWaybill != null){
            targetResult.getData().setIsLPFlag(true);
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
        if(StringHelper.isEmpty(newWaybillCode)){
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
                //c2c 直接返回
                if(BusinessHelper.isC2c(waybill.getWaybillSign())){
                    return;
                }
                //3.有商品信息直接返回
                if(baseEntity.getData().getGoodsList() != null
                        && baseEntity.getData().getGoodsList().size() > 0){
                    return;
                }else {
                    //纯配退备件库的才提示
                    String spwms_type = PropertiesHelper.newInstance().getValue("spwms_type");
                    BaseStaffSiteOrgDto orgDto = baseMajorManager.getBaseSiteBySiteId(waybill.getOldSiteId());
                    if(orgDto!=null && orgDto.getSiteType().equals(Integer.parseInt(spwms_type))){
                       // errorMessage = "新单" + newWaybillCode + "无商品信息，请在慧眼录入!";
                        errorMessage = "此运单无商品信息，请在【慧眼】(http://exp.ql.jd.com/summary/foreignOrderMain)录入再【换单打印】!";
                    }
                }

            }else{
                errorMessage = "新单" + newWaybillCode + "无运单信息!";
            }

        }catch (Exception e){
            log.error("通过运单号{}查询运单信息异常!",newWaybillCode,e);
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
                    true, true, true, true, false, false, false);
            if(result != null && result.getData() != null && result.getData().getWaybill() != null &&
                    result.getData().getWaybill().getFirstTime() != null)
            {
                Date diffDate = DateHelper.addDate(new Date(),-PICKUP_DIFFER_DAYS);
                return result.getData().getWaybill().getFirstTime().before(diffDate);
            }
            this.log.warn("通过运单号{}查询运单数据为空",waybillCode);
        } catch (Exception e) {
            StringBuilder errorMsg = new StringBuilder(
                    "中心服务调用运单getDataByChoice出错").append("waybillCode=")
                    .append(waybillCode).append("isWaybillC")
                    .append(true).append("isWaybillE").append(true)
                    .append("isWaybillM").append(true)
                    .append("isPackList").append(true);
            log.error(errorMsg.toString(), e);
        }
        errorMessage.append("新单"+waybillCode+"的运单信息为空，请联系it人员处理!");
        return true;
    }

    @Override
    public InvokeResult<Boolean> exchangeOwnWaybill(OwnReverseTransferDomain domain) {
        if(log.isInfoEnabled()){
            log.info("执行自营换单waybillCode={},userId={},userRealName={},siteId={},siteName={}"
                    ,domain.getWaybillCode(),domain.getUserId(),domain.getUserRealName(),domain.getSiteId(),domain.getSiteName());
        }
        InvokeResult<Boolean> result=new InvokeResult<Boolean>();
        domain.setOperateTime(new Date());
        try {
            BaseStaffSiteOrgDto siteDomain = siteService.getSite(domain.getSiteId());
            if(null!=siteDomain){
                domain.setSiteType(siteDomain.getSiteType());
                domain.setOrgName(siteDomain.getOrgName());
                domain.setOrgId(siteDomain.getOrgId());
                domain.setProvinceAgencyCode(siteDomain.getProvinceAgencyCode());
                domain.setProvinceAgencyName(siteDomain.getProvinceAgencyName());
                domain.setAreaHubCode(siteDomain.getAreaCode());
                domain.setAreaHubName(siteDomain.getAreaName());
            }else {
                result.customMessage(2, MessageFormat.format("获取站点【ID={0}】信息为空",domain.getSiteId()));
                log.warn("自营换单获取站点【ID={}】信息为空",domain.getSiteId());
                return result;
            }
        }catch (Exception ex){
            log.error("获取站点",ex);
            result.error("获取站点异常"+ex.getMessage());
            return result;
        }
        try {
            Integer featureType = jsfSortingResourceService.getWaybillCancelByWaybillCode(domain.getWaybillCode());
            domain.setSickWaybillFlag(featureType == null ? Constants.FEATURE_TYPCANCEE_UNSICKL :featureType);//30-病单，31-取消病单，32- 非病单
        }catch (Exception ex){
            log.error("获取订单拦截信息 waybill_cancel 的病单标识异常：",ex);
            result.error("获取订单拦截信息异常");
            return result;
        }
        try{
            ownWaybillTransformMQ.send(domain.getWaybillCode(), JsonHelper.toJson(domain));
            result.success();
            result.setData(Boolean.TRUE);
        }catch (Exception ex){
            log.error("推送运单自营换单MQ异常",ex);
            result.error("推送运单自营换单MQ异常");
        }
        return result;
    }

    @Override
    public InvokeResult<String> exchangeOwnWaybillSync(OwnReverseTransferDomain domain) {
        if (log.isInfoEnabled()) {
            log.info("执行自营换单同步接口waybillCode={},userId={},userRealName={},siteId={},siteName={}"
                    , domain.getWaybillCode(), domain.getUserId(), domain.getUserRealName(), domain.getSiteId(), domain.getSiteName());
        }
        InvokeResult<String> result = new InvokeResult<>();
        domain.setOperateTime(new Date());
        try {
            BaseStaffSiteOrgDto siteDomain = siteService.getSite(domain.getSiteId());
            if (null != siteDomain) {
                domain.setSiteType(siteDomain.getSiteType());
                domain.setOrgName(siteDomain.getOrgName());
                domain.setOrgId(siteDomain.getOrgId());
            } else {
                result.customMessage(2, MessageFormat.format("获取站点【ID={0}】信息为空", domain.getSiteId()));
                log.warn("自营换单同步接口获取站点【ID={}】信息为空:waybillCode={}", domain.getSiteId(), domain.getWaybillCode());
                return result;
            }
        } catch (Exception ex){
            log.error("自营换单同步接口获取站点发生异常:waybillCode={}", domain.getWaybillCode(), ex);
            result.error("获取站点异常" + ex.getMessage());
            return result;
        }
        try {
            Integer featureType = jsfSortingResourceService.getWaybillCancelByWaybillCode(domain.getWaybillCode());
            // 30-病单，31-取消病单，32- 非病单
            domain.setSickWaybillFlag(featureType == null ? Constants.FEATURE_TYPCANCEE_UNSICKL : featureType);
        } catch (Exception ex) {
            log.error("自营换单同步接口获取订单拦截信息waybill_cancel的病单标识异常:waybillCode={}", domain.getWaybillCode(), ex);
            result.error("获取订单拦截信息异常");
            return result;
        }
        try {
            WaybillSwitchDto waybillSwitchDto = new WaybillSwitchDto();
            // 是否拦截订单
            waybillSwitchDto.setInterceptWaybill(Boolean.TRUE);
            // 单据类型，0：默认；30：仓病单拦截
            waybillSwitchDto.setSickWaybillFlag(domain.getSickWaybillFlag());
            waybillSwitchDto.setWaybillCode(domain.getWaybillCode());
            waybillSwitchDto.setOperateTime(domain.getOperateTime());
            waybillSwitchDto.setOrgName(domain.getOrgName());
            waybillSwitchDto.setOrgId(domain.getOrgId());
            waybillSwitchDto.setOperatorSiteId(domain.getSiteId());
            waybillSwitchDto.setOperatorSiteName(domain.getSiteName());
            waybillSwitchDto.setOperatorUserId(domain.getUserId());
            waybillSwitchDto.setOperatorUserName(domain.getUserRealName());
            waybillSwitchDto.setSiteType(domain.getSiteType());
            waybillSwitchDto.setSysCode(Constants.SYS_CODE_DMS);

            BaseEntity<SwitchWaybillResultDto> baseEntity = waybillAddApi.createSwitchWaybill(waybillSwitchDto);
            if (!Constants.NUMBER_ONE.equals(baseEntity.getResultCode())) {
                log.error("自营换单同步接口调用运单换单接口返回失败:waybillCode={},baseEntity={}", domain.getWaybillCode(), JsonHelper.toJson(baseEntity));
                result.parameterError(baseEntity.getMessage());
                return result;
            }
            if (baseEntity.getData() == null) {
                log.error("自营换单同步接口调用运单换单接口返回空:waybillCode={},baseEntity={}", domain.getWaybillCode(), JsonHelper.toJson(baseEntity));
                result.parameterError("调用运单换单接口返回空");
                return result;
            }
            result.success();
            result.setData(baseEntity.getData().getReturnWaybillCode());
        } catch (Exception ex) {
            log.error("自营换单同步接口调用运单换单接口发生异常:waybillCode={}", domain.getWaybillCode(), ex);
            result.error("调用运单换单接口发生异常");
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

        //2.判断运单是否为弃件，如果是弃件禁止换单
        if (waybillTraceManager.isOpCodeWaste(wayBillCode)){
            result.setData(false);
            result.setMessage("弃件禁换单，每月5、20日原运单返到货传站分拣中心，用箱号纸打印“返分拣弃件”贴面单同侧(禁手写/遮挡面单)");
            return result;
        }

        boolean bool = waybillCancelService.checkWaybillCancelInterceptType99(wayBillCode);
        if (bool) {
            result.setData(false);
            result.setMessage(HintService.getHint(HintCodeConstants.WAYBILL_ERROR_RE_PRINT));
            return result;
        }

        //3.获取运单信息判断是否拒收或妥投
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
        //3.1妥投运单，不可以操作逆向换单
        if(wdomain != null && Constants.WAYBILL_DELIVERED_CODE.equals(wdomain.getWaybillState())){
            result.setData(false);
            result.setMessage("该订单已经妥投，不能触发逆向新单");
            return result;
        }
        //3.2拒收运单，可以操作逆向换单
        if(wdomain != null && Constants.WAYBILL_REJECT_CODE.equals(wdomain.getWaybillState())){
            reverseSpareEclp.checkIsPureMatch(waybillDto.getWaybill().getWaybillCode(),waybillDto.getWaybill().getWaybillSign(),result);
            return result;
        }
        //3.3 冷链专送 waybillSign 第5位等于5：异常即报废，不可以操作逆向换单
        if (BusinessUtil.isColdChainExpressScrap(waybillDto.getWaybill().getWaybillSign())){
            result.setData(false);
            result.setMessage(HintService.getHint(HintCodeConstants.COLD_CHAIN_EXPRESS_SCRAP_NO_EXCHANGE, HintCodeConstants.COLD_CHAIN_EXPRESS_SCRAP_NO_EXCHANGE_MSG));
            return result;
        }
        //4.查询运单是否操作异常处理
        AbnormalWayBill abnormalWayBill = abnormalWayBillService.getAbnormalWayBillByWayBillCode(wayBillCode, siteCode);
        //异常操作运单，可以操作逆向换单
        if(abnormalWayBill == null || !wayBillCode.equals(abnormalWayBill.getWaybillCode())){
            result.setData(false);
            result.setMessage("订单未操作拒收或分拣异常处理扫描，请先操作");
        }else{
            reverseSpareEclp.checkIsPureMatch(waybillDto.getWaybill().getWaybillCode(),waybillDto.getWaybill().getWaybillSign(),result);
        }
        // 5. 校验运单暂存拦截，如果存在则不允许逆向换单
        InvokeResult<Boolean> checkClaimDamagedCancelResult = this.checkClaimDamagedCancel(wayBillCode, siteCode);
        if(!checkClaimDamagedCancelResult.getData()){
            result.setData(false);
            result.setMessage(checkClaimDamagedCancelResult.getMessage());
            return result;
        }
        //6.全量接单失败拦截消息14，且操作部门为64分拣中心，判断运单是否到达站点（全程跟踪节点：60,80,110,150）
        BaseStaffSiteOrgDto siteInfo = baseMajorManager.getBaseSiteBySiteId(siteCode);
        boolean isSortingSite = BusinessUtil.isSortingSiteType(siteInfo.getSiteType());
        if (isSortingSite){
        	boolean isFullOrderFail = waybillCancelService.isFullOrderFail(wayBillCode);
        	if(isFullOrderFail) {
        		List<PackageState> siteOperations= waybillTraceManager.getAllOperationsByOpeCodeAndState(wayBillCode, DmsConstants.SITE_OPERAT_STATES);
        		if(CollectionUtils.isNotEmpty(siteOperations)) {
	                result.setData(false);
	                result.setMessage("此运单已到站点，禁止分拣操作逆向换单");
	                return result;
        		}
        	}

        }
        return result;
    }

    /**
     * 逆向换单限制校验(专为德邦提供)
     * 不校验异常处理
     * （纯配外单 且 理赔完成 且 物权归京东的才可以执行逆向换单）
     * @param wayBillCode
     * @return
     */
    @Override
    @JProfiler(jKey = "DMSWEB.ReversePrintServiceImpl.checkWayBillForDpkExchange", mState = {JProEnum.TP, JProEnum.FunctionError})
    public InvokeResult<Boolean> checkWayBillForDpkExchange(String wayBillCode, Integer siteCode){
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

        //2.判断运单是否为弃件，如果是弃件禁止换单
        if (waybillTraceManager.isOpCodeWaste(wayBillCode)){
            result.setData(false);
            result.setMessage("弃件禁换单，每月5、20日原运单返到货传站分拣中心，用箱号纸打印“返分拣弃件”贴面单同侧(禁手写/遮挡面单)");
            return result;
        }

        boolean bool = waybillCancelService.checkWaybillCancelInterceptType99(wayBillCode);
        if (bool) {
            result.setData(false);
            result.setMessage(HintService.getHint(HintCodeConstants.WAYBILL_ERROR_RE_PRINT));
            return result;
        }

        //3.获取运单信息判断是否拒收或妥投
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
        //3.1妥投运单，不可以操作逆向换单
        if(wdomain != null && Constants.WAYBILL_DELIVERED_CODE.equals(wdomain.getWaybillState())){
            result.setData(false);
            result.setMessage("该订单已经妥投，不能触发逆向新单");
            return result;
        }
        //3.2拒收运单，可以操作逆向换单
        if(wdomain != null && Constants.WAYBILL_REJECT_CODE.equals(wdomain.getWaybillState())){
            reverseSpareEclp.checkIsPureMatch(waybillDto.getWaybill().getWaybillCode(),waybillDto.getWaybill().getWaybillSign(),result);
            return result;
        }
        //4.查询运单是否操作异常处理
        AbnormalWayBill abnormalWayBill = abnormalWayBillService.getAbnormalWayBillByWayBillCode(wayBillCode, siteCode);
        if (abnormalWayBill != null && wayBillCode.equals(abnormalWayBill.getWaybillCode())) {
            reverseSpareEclp.checkIsPureMatch(waybillDto.getWaybill().getWaybillCode(), waybillDto.getWaybill().getWaybillSign(), result);
        }
        // 5. 校验运单暂存拦截，如果存在则不允许逆向换单
        InvokeResult<Boolean> checkClaimDamagedCancelResult = this.checkClaimDamagedCancel(wayBillCode, siteCode);
        if(!checkClaimDamagedCancelResult.getData()){
            result.setData(false);
            result.setMessage(checkClaimDamagedCancelResult.getMessage());
            return result;
        }
        //6.全量接单失败拦截消息14，且操作部门为64分拣中心，判断运单是否到达站点（全程跟踪节点：60,80,110,150）
        BaseStaffSiteOrgDto siteInfo = baseMajorManager.getBaseSiteBySiteId(siteCode);
        boolean isSortingSite = BusinessUtil.isSortingSiteType(siteInfo.getSiteType());
        if (isSortingSite){
            boolean isFullOrderFail = waybillCancelService.isFullOrderFail(wayBillCode);
            if(isFullOrderFail) {
                List<PackageState> siteOperations= waybillTraceManager.getAllOperationsByOpeCodeAndState(wayBillCode, DmsConstants.SITE_OPERAT_STATES);
                if(CollectionUtils.isNotEmpty(siteOperations)) {
                    result.setData(false);
                    result.setMessage("此运单已到站点，禁止分拣操作逆向换单");
                    return result;
                }
            }

        }
        return result;
    }


    /**
     * 检查理赔破损拦截
     * @param wayBillCode 运单号
     * @param siteCode 分拣中心或站点
     * @return Boolean true-校验通过，false-校验不通过
     * @author fanggang7
     * @time 2020-09-09 14:29:19 周三
     */
    private InvokeResult<Boolean> checkClaimDamagedCancel(String wayBillCode, Integer siteCode){
        InvokeResult<Boolean> result = new InvokeResult<>();
        result.setData(true);
        // 判断是否有理赔破损拦截
        if(this.hasClaimDamagedCancel(wayBillCode)){
            result.setData(false);

            // 判断站点类型，分拣和站点登录人分别提示
            BaseStaffSiteOrgDto siteInfo = baseMajorManager.getBaseSiteBySiteId(siteCode);
            boolean isSortingSite = BusinessUtil.isSortingSiteType(siteInfo.getSiteType());
            if(isSortingSite){
                result.setMessage("此单为暂存拦截运单请先暂存，分拣工作台收到可操作消息后直接操作换单打印");
            } else {
                result.setMessage("此单为京权破损需拦截派送，请滞留站点等待异常管理平台系统通知");
            }
            return result;
        }
        return result;
    }

    /**
     * 是否有理赔破损拦截
     * @param waybillCode 运单号
     * @return boolean true-有，false-无
     * @author fanggang7
     * @time 2020-09-09 14:29:19 周三
     */
    private boolean hasClaimDamagedCancel(String waybillCode){
        boolean hasClaimDamagedCancel = false;
        List<CancelWaybill> cancelWaybillList = waybillCancelService.getByWaybillCode(waybillCode);
        if(CollectionUtils.isNotEmpty(cancelWaybillList)){
            // 过滤是否有理赔类型的记录
            for (CancelWaybill cancelWaybill : cancelWaybillList) {
                if(Objects.equals(WaybillCancelInterceptTypeEnum.CLAIM_DAMAGED.getCode(), cancelWaybill.getInterceptType())) {
                    hasClaimDamagedCancel = true;
                    break;
                }
            }
        }
        return hasClaimDamagedCancel;
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

	@Override
	public JdResult<TwiceExchangeResponse> getTwiceExchangeInfo(TwiceExchangeRequest twiceExchangeRequest) {
		JdResult<TwiceExchangeResponse> jdResult = new JdResult<TwiceExchangeResponse>();
		jdResult.toSuccess();
		if(twiceExchangeRequest == null 
				|| StringHelper.isEmpty(twiceExchangeRequest.getWaybillCode())){
			jdResult.toFail("请求参数无效！");
			return jdResult;
		}
		TwiceExchangeResponse twiceExchangeResponse = new TwiceExchangeResponse();
		//获取老单号
		BaseEntity<com.jd.etms.waybill.domain.Waybill> oldWaybill = waybillQueryManager.getWaybillByReturnWaybillCode(twiceExchangeRequest.getWaybillCode());
		if(oldWaybill.getResultCode()==1 && oldWaybill.getData()!=null && StringUtils.isNotBlank(oldWaybill.getData().getWaybillCode())){
			String oldWaybillCode = oldWaybill.getData().getWaybillCode();
			Integer busiId = oldWaybill.getData().getBusiId();
			twiceExchangeResponse.setOldWaybillCode(oldWaybillCode);
			//获取理赔状态及物权归属
			LocalClaimInfoRespDTO claimInfoRespDTO =  obcsManager.getClaimListByClueInfo(1,oldWaybillCode);
			if(claimInfoRespDTO == null){
				jdResult.toFail("获取理赔信息失败，请稍后再试");
				return jdResult;
			}
			twiceExchangeResponse.setGoodOwner(claimInfoRespDTO.getGoodOwnerName());
			//划分理赔状态 以及 物权归属
			twiceExchangeResponse.setStatusOfLP(claimInfoRespDTO.getStatusDesc());

			if(LocalClaimInfoRespDTO.LP_STATUS_DOING.equals(twiceExchangeResponse.getStatusOfLP()) && claimInfoRespDTO.getGoodOwner() == 0){
				jdResult.toFail("理赔中运单禁止换单，请稍后再试");
				return jdResult;
			}else if(LocalClaimInfoRespDTO.LP_STATUS_DONE.equals(twiceExchangeResponse.getStatusOfLP()) && claimInfoRespDTO.getGoodOwner() == LocalClaimInfoRespDTO.GOOD_OWNER_JD){
				twiceExchangeResponse.setReturnDestinationTypes("100");
			}else if((LocalClaimInfoRespDTO.LP_STATUS_DONE.equals(twiceExchangeResponse.getStatusOfLP()) 
						&& claimInfoRespDTO.getGoodOwner() == LocalClaimInfoRespDTO.GOOD_OWNER_BUSI)
					|| LocalClaimInfoRespDTO.LP_STATUS_NONE.equals(twiceExchangeResponse.getStatusOfLP())){
				twiceExchangeResponse.setReturnDestinationTypes("011");
				//获取商家退货地址
				JdResult<BackAddressDTOExt> backInfo = getBackAddress(busiId);
				if(backInfo != null 
						&& backInfo.isSucceed() 
						&& backInfo.getData() != null){
					//设置退货信息
					BackAddressDTOExt backInfoData = backInfo.getData();
					twiceExchangeResponse.setAddress(backInfoData.getFullBackAddress());
					twiceExchangeResponse.setHideAddress(BusinessUtil.getHideAddress(backInfoData.getFullBackAddress()));
					twiceExchangeResponse.setContact(backInfoData.getContractName());
					twiceExchangeResponse.setHideContact(BusinessUtil.getHideName(backInfoData.getContractName()));
					String phone = backInfoData.getContractPhone();
					if(StringHelper.isEmpty(phone)){
						phone = backInfoData.getContractMobile();
					}
					twiceExchangeResponse.setPhone(phone);
                    final boolean switchHidePhoneNewVersion = sysConfigService.getConfigByName(Constants.SYS_CONFIG_HIDE_PHONE_6Char);
                    if(switchHidePhoneNewVersion){
                        twiceExchangeResponse.setHidePhone(BusinessUtil.getHidePhone6Char(phone));
                    } else {
                        twiceExchangeResponse.setHidePhone(BusinessUtil.getHidePhone(phone));
                    }

					twiceExchangeResponse.setNeedFillReturnInfo(Boolean.TRUE);
				}
			}
		}
		jdResult.setData(twiceExchangeResponse);
		return jdResult;
	}
	/**
	 * 获取商家退货信息
	 * @param busiId
	 * @return
	 */
	private JdResult<BackAddressDTOExt> getBackAddress(Integer busiId){
		JdResult<BackAddressDTOExt> jdResult = new JdResult<BackAddressDTOExt>();
		jdResult.toSuccess();
        //调用外单接口，根据商家id获取商家编码
        BasicTraderInfoDTO basicTraderInfoDTO = baseMinorManager.getBaseTraderById(busiId);
        String busiCode;
        if(basicTraderInfoDTO != null){
        	busiCode = basicTraderInfoDTO.getTraderCode();
        }else{
        	jdResult.toFail("商家Id无效!");
        	log.warn("getBackInfoAndNotice(),入参商家Id无效!busiId=" +busiId);
        	return jdResult;
        }
		//调用jsf获取退货地址
        JdResult<List<BackAddressDTO>> backAddressResult = lDOPManager.queryBackAddressByType(DmsConstants.RETURN_BACK_ADDRESS_TYPE_6, busiCode);
        if(backAddressResult != null 
        		&& backAddressResult.isSucceed()
        		&& backAddressResult.getData() != null
        		&& backAddressResult.getData().size() > 0){
        	BackAddressDTO backAddress = backAddressResult.getData().get(0);
        	if(backAddress != null){
        		jdResult.setData(lDOPManager.getBackAddressDTOExt(backAddress));
        	}
        }
		return jdResult;
	}

    /**
     * 换单成功后逻辑处理
     * @param request
     * @return
     */
    @Override
    public InvokeResult exchangeSuccessAfter(ExchangeWaybillDto request) {
        InvokeResult result = new InvokeResult();
        try {
            if(request.getTwiceExchangeFlag() != null && request.getTwiceExchangeFlag()){
                saveReturnAddressInfo(request);
            }
        }catch (Exception e){
            log.error("保存二次换单退货地址信息异常!入参【{}】",JsonHelper.toJsonMs(request),e);
            result.error("保存二次换单退货地址信息异常!");
        }
        return result;
    }

    /**
     * 保存二次换单退货地址信息
     * @param request
     */
    @Override
    @JProfiler(jKey = "DMS.BASE.ReversePrintServiceImpl.saveReturnAddressInfo", jAppName = Constants.UMP_APP_NAME_DMSWEB,
            mState = {JProEnum.TP, JProEnum.FunctionError})
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void saveReturnAddressInfo(ExchangeWaybillDto request) {
        String oldWaybillCode = request.getWaybillCode();
        com.jd.etms.waybill.domain.Waybill waybill = waybillQueryManager.getOnlyWaybillByWaybillCode(oldWaybillCode);
        if(waybill == null || waybill.getBusiId() == null){
            log.warn("根据运单号【{}】查询商家id不存在!",request.getWaybillCode());
            return;
        }
        Integer businessId = waybill.getBusiId();
        BasicTraderInfoDTO basicTraderInfoDTO = baseMinorManager.getBaseTraderById(businessId);
        if(basicTraderInfoDTO == null || StringUtils.isEmpty(basicTraderInfoDTO.getTraderCode())
                || StringUtils.isEmpty(basicTraderInfoDTO.getTraderName())){
            log.warn("根据商家id【{}】查询商家信息不存在!",businessId);
            return;
        }
        String businessCode = basicTraderInfoDTO.getTraderCode();
        String businessName = basicTraderInfoDTO.getTraderName();
        Integer dmsSiteCode = request.getCreateSiteCode();

        BusinessReturnAdress businessReturnAdress
                = businessReturnAdressService.queryBySiteAndBusinessId(dmsSiteCode,businessId);
        // 组装商家退货地址
        BusinessReturnAdress commonAddress
                = createCommonAddress(dmsSiteCode,businessId,businessCode,businessName);
        // 是否存在退货地址
        JdResult<List<BackAddressDTO>> jdResult
                = lDOPManager.queryBackAddressByType(DmsConstants.RETURN_BACK_ADDRESS_TYPE_6, businessCode);
        boolean hasBackInfo = jdResult != null && !CollectionUtils.isEmpty(jdResult.getData());
        // 未维护退货地址处理
        update(businessReturnAdress,commonAddress,businessId,hasBackInfo);
        // 站内信通知
        if(!hasBackInfo){
            importSiteNotice(businessCode,businessId);
        }
    }

    /**
     * 发送站内信通知
     *   未维护退货地址 && 一天一个商家只能触发一次
     * @param businessCode
     * @param businessId
     */
    private void importSiteNotice(String businessCode,Integer businessId) {
        String key = RETURN_ADDRESS_REDIS_KEY_PREFIX.concat(String.valueOf(businessId));
        boolean exists = false;
        try {
            exists = cacheService.exists(key);
        }catch (Exception e){
            log.error("获取商家【{}】的站内信通知缓存异常!",businessId,e);
        }
        if(exists){
            // 1天内则不处理
            return;
        }
        Date currentDate = new Date();
        BatchImportDTO noticeInfo = new BatchImportDTO();
        noticeInfo.setChannel(ChannelEnum.POST);
        noticeInfo.setPostType(PostTypeEnum.NOTICE);
        noticeInfo.setResolveType(ResolveTypeEnum.TRADER_NO);
        noticeInfo.setKey(noticeKey);
        noticeInfo.setTitle(noticeTitle);
        noticeInfo.setContent(noticeContent);
        noticeInfo.setHandleLink(noticeHandleLink);
        noticeInfo.setReceivers(businessCode);
        noticeInfo.setPostTime(currentDate);
        JdResult<Boolean> jdResult = eclpImportServiceManager.batchImport(noticeInfo);
        if(jdResult != null && jdResult.isSucceed()){
            try {
                // 记录缓存
                cacheService.setEx(key,String.valueOf(true),Constants.CONSTANT_NUMBER_ONE, TimeUnit.DAYS);
            }catch (Exception e){
               log.error("记录商家【{}】的站内信通知缓存异常!",businessId,e);
            }
        }
    }

    /**
     * 构建商家退货数据
     * @param dmsSiteCode
     * @param businessId
     * @param businessCode
     * @param businessName
     * @return
     */
    private BusinessReturnAdress createCommonAddress(Integer dmsSiteCode, Integer businessId,
                                                     String businessCode, String businessName) {
        String dmsSiteName = null;
        String deptNo = null;
        try {
            BaseSiteInfoDto baseSite = baseMajorManager.getBaseSiteInfoBySiteId(dmsSiteCode);
            dmsSiteName = baseSite == null ? null : baseSite.getSiteName();
            // 签约区域
            ResponseDTO<BasicTraderIntegrateDTO> responseDTO = basicTraderIntegrateOutAPI.queryByTraderCode(businessCode);
            if(responseDTO != null && responseDTO.isSuccess()){
                BasicTraderIntegrateDTO basicTraderIntegrateDTO = responseDTO.getResult();
                if(basicTraderIntegrateDTO != null && basicTraderIntegrateDTO.getSignArea() != null){
                    deptNo = signAreaEnums.getValueName(basicTraderIntegrateDTO.getSignArea());
                }
            }
        }catch (Exception e){
            log.error("根据站点编码【{}】获取站点|根据商家编码【{}】获取签约区域异常!",e,dmsSiteCode,businessCode);
        }
        BusinessReturnAdress commonAddress = new BusinessReturnAdress();
        commonAddress.setDmsSiteCode(dmsSiteCode);
        commonAddress.setDmsSiteName(dmsSiteName);
        commonAddress.setBusinessId(businessId);
        commonAddress.setBusinessCode(businessCode);
        commonAddress.setBusinessName(businessName);
        Date currentDate = new Date();
        commonAddress.setCreateTime(currentDate);
        commonAddress.setLastNoticeTime(currentDate);
        commonAddress.setLastOperateTime(currentDate);
        commonAddress.setReturnAdressStatus(BusinessReturnAdressStatusEnum.NO.getStatusCode());
        commonAddress.setDeptNo(deptNo);
        commonAddress.setReturnQuantity(1);
        return commonAddress;
    }

    /**
     * 商家退货地址处理
     *      1、更新退货次数
     *      2、更新商家退货地址状态
     * @param businessReturnAddress
     * @param commonAddress
     * @param businessId
     * @param hasBackInfo
     */
    private void update(BusinessReturnAdress businessReturnAddress,BusinessReturnAdress commonAddress,
                        Integer businessId, boolean hasBackInfo) {
        int returnAddressStatus = hasBackInfo ?
                BusinessReturnAdressStatusEnum.YES.getStatusCode() : BusinessReturnAdressStatusEnum.NO.getStatusCode();
        if(businessReturnAddress != null){
            // 更新退货次数和签约区域
            businessReturnAddress.setDeptNo(commonAddress.getDeptNo());
            businessReturnAdressService.updateReturnQuantity(businessReturnAddress);
        }else {
            commonAddress.setReturnAdressStatus(returnAddressStatus);
            businessReturnAdressService.add(commonAddress);
        }
        if(hasBackInfo){
            // 更新商家退货地址状态
            businessReturnAdressService.updateStatusByBusinessId(businessId);
        }
    }
}
