package com.jd.bluedragon.distribution.send.service;

import com.google.common.base.Strings;
import com.jd.bluedragon.Constants;
import com.jd.bluedragon.common.domain.Pack;
import com.jd.bluedragon.common.service.WaybillCommonService;
import com.jd.bluedragon.common.utils.CacheKeyConstants;
import com.jd.bluedragon.common.utils.ProfilerHelper;
import com.jd.bluedragon.core.base.BaseMajorManager;
import com.jd.bluedragon.core.base.ColdChainQuarantineManager;
import com.jd.bluedragon.core.base.DmsInterturnManager;
import com.jd.bluedragon.core.base.WaybillPackageManager;
import com.jd.bluedragon.core.base.WaybillQueryManager;
import com.jd.bluedragon.core.jmq.producer.DefaultJMQProducer;
import com.jd.bluedragon.core.redis.service.RedisManager;
import com.jd.bluedragon.distribution.abnormal.domain.DmsOperateHintTrack;
import com.jd.bluedragon.distribution.abnormal.service.DmsOperateHintService;
import com.jd.bluedragon.distribution.api.JdResponse;
import com.jd.bluedragon.distribution.api.request.BoardCombinationRequest;
import com.jd.bluedragon.distribution.api.request.InspectionRequest;
import com.jd.bluedragon.distribution.api.request.SortingRequest;
import com.jd.bluedragon.distribution.api.request.TaskRequest;
import com.jd.bluedragon.distribution.api.response.BoardResponse;
import com.jd.bluedragon.distribution.api.response.DeliveryResponse;
import com.jd.bluedragon.distribution.auto.domain.UploadData;
import com.jd.bluedragon.distribution.b2bRouter.domain.B2BRouter;
import com.jd.bluedragon.distribution.b2bRouter.domain.B2BRouterNode;
import com.jd.bluedragon.distribution.b2bRouter.service.B2BRouterService;
import com.jd.bluedragon.distribution.base.domain.SysConfig;
import com.jd.bluedragon.distribution.base.domain.SysConfigContent;
import com.jd.bluedragon.distribution.base.service.BaseService;
import com.jd.bluedragon.distribution.base.service.SiteService;
import com.jd.bluedragon.distribution.base.service.SysConfigService;
import com.jd.bluedragon.distribution.batch.dao.BatchSendDao;
import com.jd.bluedragon.distribution.batch.domain.BatchSend;
import com.jd.bluedragon.distribution.board.service.BoardCombinationService;
import com.jd.bluedragon.distribution.box.domain.Box;
import com.jd.bluedragon.distribution.box.domain.BoxStatusEnum;
import com.jd.bluedragon.distribution.box.service.BoxService;
import com.jd.bluedragon.distribution.coldchain.domain.ColdChainSend;
import com.jd.bluedragon.distribution.coldchain.service.ColdChainSendService;
import com.jd.bluedragon.distribution.consumable.service.WaybillConsumableRecordService;
import com.jd.bluedragon.distribution.departure.service.DepartureService;
import com.jd.bluedragon.distribution.handler.InterceptResult;
import com.jd.bluedragon.distribution.inspection.service.InspectionExceptionService;
import com.jd.bluedragon.distribution.jsf.domain.InvokeResult;
import com.jd.bluedragon.distribution.jsf.domain.SortingCheck;
import com.jd.bluedragon.distribution.jsf.domain.SortingJsfResponse;
import com.jd.bluedragon.distribution.jsf.service.JsfSortingResourceService;
import com.jd.bluedragon.distribution.operationLog.domain.OperationLog;
import com.jd.bluedragon.distribution.operationLog.service.OperationLogService;
import com.jd.bluedragon.distribution.reverse.dao.ReverseSpareDao;
import com.jd.bluedragon.distribution.reverse.domain.ReverseSpare;
import com.jd.bluedragon.distribution.reverse.part.service.ReversePartDetailService;
import com.jd.bluedragon.distribution.seal.service.NewSealVehicleService;
import com.jd.bluedragon.distribution.send.dao.SendDatailDao;
import com.jd.bluedragon.distribution.send.dao.SendDatailReadDao;
import com.jd.bluedragon.distribution.send.dao.SendMDao;
import com.jd.bluedragon.distribution.send.domain.ArSendDetailMQBody;
import com.jd.bluedragon.distribution.send.domain.BoxInfo;
import com.jd.bluedragon.distribution.send.domain.ColdChainSendMessage;
import com.jd.bluedragon.distribution.send.domain.ConfirmMsgBox;
import com.jd.bluedragon.distribution.send.domain.DeliveryCancelSendMQBody;
import com.jd.bluedragon.distribution.send.domain.OrderInfo;
import com.jd.bluedragon.distribution.send.domain.PackInfo;
import com.jd.bluedragon.distribution.send.domain.SendDetail;
import com.jd.bluedragon.distribution.send.domain.SendM;
import com.jd.bluedragon.distribution.send.domain.SendResult;
import com.jd.bluedragon.distribution.send.domain.SendTaskBody;
import com.jd.bluedragon.distribution.send.domain.SendThreeDetail;
import com.jd.bluedragon.distribution.send.domain.ShouHuoConverter;
import com.jd.bluedragon.distribution.send.domain.ShouHuoInfo;
import com.jd.bluedragon.distribution.send.domain.ThreeDeliveryResponse;
import com.jd.bluedragon.distribution.send.domain.TurnoverBoxInfo;
import com.jd.bluedragon.distribution.send.manager.SendMManager;
import com.jd.bluedragon.distribution.send.utils.SendBizSourceEnum;
import com.jd.bluedragon.distribution.send.ws.client.dmc.DmsToTmsWebService;
import com.jd.bluedragon.distribution.send.ws.client.dmc.Result;
import com.jd.bluedragon.distribution.sorting.domain.Sorting;
import com.jd.bluedragon.distribution.sorting.service.SortingService;
import com.jd.bluedragon.distribution.storage.service.StoragePackageMService;
import com.jd.bluedragon.distribution.systemLog.domain.Goddess;
import com.jd.bluedragon.distribution.systemLog.service.GoddessService;
import com.jd.bluedragon.distribution.task.domain.Task;
import com.jd.bluedragon.distribution.task.service.TaskService;
import com.jd.bluedragon.distribution.transBillSchedule.service.TransBillScheduleService;
import com.jd.bluedragon.distribution.urban.service.TransbillMService;
import com.jd.bluedragon.distribution.waybill.domain.WaybillStatus;
import com.jd.bluedragon.distribution.waybill.service.WaybillService;
import com.jd.bluedragon.distribution.weight.service.DmsWeightFlowService;
import com.jd.bluedragon.dms.utils.BusinessUtil;
import com.jd.bluedragon.dms.utils.WaybillUtil;
import com.jd.bluedragon.utils.BusinessHelper;
import com.jd.bluedragon.utils.CollectionHelper;
import com.jd.bluedragon.utils.DateHelper;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.bluedragon.utils.Md5Helper;
import com.jd.bluedragon.utils.NumberHelper;
import com.jd.bluedragon.utils.PropertiesHelper;
import com.jd.bluedragon.utils.SerialRuleUtil;
import com.jd.bluedragon.utils.StringHelper;
import com.jd.bluedragon.utils.XmlHelper;
import com.jd.etms.erp.service.dto.SendInfoDto;
import com.jd.etms.erp.ws.SupportServiceInterface;
import com.jd.etms.waybill.api.WaybillPackageApi;
import com.jd.etms.waybill.api.WaybillPickupTaskApi;
import com.jd.etms.waybill.common.Page;
import com.jd.etms.waybill.domain.BaseEntity;
import com.jd.etms.waybill.domain.DeliveryPackageD;
import com.jd.etms.waybill.domain.PickupTask;
import com.jd.etms.waybill.domain.Waybill;
import com.jd.etms.waybill.dto.BigWaybillDto;
import com.jd.etms.waybill.dto.DeliveryPackageDto;
import com.jd.etms.waybill.dto.WChoice;
import com.jd.fastjson.JSON;
import com.jd.jmq.common.exception.JMQException;
import com.jd.jmq.common.message.Message;
import com.jd.ql.basic.dto.BaseStaffSiteOrgDto;
import com.jd.transboard.api.dto.Response;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import com.jd.ump.profiler.CallerInfo;
import com.jd.ump.profiler.proxy.Profiler;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Set;

@Service("deliveryService")
public class DeliveryServiceImpl implements DeliveryService {

    private final Logger logger = Logger.getLogger(DeliveryServiceImpl.class);

    private final int MAX_SHOW_NUM = 3;

    private final String PERFORMANCE_DMSSITECODE_SWITCH = "performance.dmsSiteCode.switch";

    @Autowired
    B2BRouterService b2bRouterService;

    @Autowired
    DepartureService departureService;

    @Autowired
    private SendMDao sendMDao;

    @Autowired
    private SendMManager sendMManager;

    @Autowired
    private SendDatailDao sendDatailDao;

    @Autowired
    private SendDatailReadDao sendDatailReadDao;

    @Autowired
    private ReverseSpareDao reverseSpareDao;

    @Autowired
    private BoxService boxService;

    @Autowired
    private SortingService tSortingService;

    @Autowired
    private WaybillPickupTaskApi waybillPickupTaskApi;

    @Autowired
    WaybillPackageManager waybillPackageManager;

    @Autowired
    private WaybillQueryManager waybillQueryManager;

    @Autowired
    private DmsToTmsWebService dmsToTmsWebService;

    @Autowired
    private OperationLogService operationLogService;

    @Autowired
    private InspectionExceptionService inspectionExceptionService;

    @Autowired
    private TaskService tTaskService;

    @Autowired
    ReverseDeliveryService reverseDeliveryService;

    @Autowired
    private SupportServiceInterface supportProxy;

    @Autowired
    private DmsOperateHintService dmsOperateHintService;
    @Autowired
    private GoddessService goddessService;

    @Autowired
    @Qualifier("batchSendDao")
    private BatchSendDao batchSendDao;

    @Autowired
    @Qualifier("forwardComputer")
    private PackageDiffrence forwardComputer;

    @Autowired
    @Qualifier("reverseComputer")
    private PackageDiffrence reverseComputer;

    @Autowired
    private TaskService taskService;

    @Autowired
    private BaseMajorManager baseMajorManager;

    @Autowired
    private SiteService siteService;

    @Autowired
    private RedisManager redisManager;

    @Autowired
    private WaybillCommonService waybillCommonService;

    @Autowired
    private JsfSortingResourceService jsfSortingResourceService;

    @Autowired
    private BaseService baseService;

    @Autowired
    @Qualifier("turnoverBoxMQ")
    private DefaultJMQProducer turnoverBoxMQ;

    @Autowired
    @Qualifier("deliveryCancelSendMQ")
    private DefaultJMQProducer deliveryCancelSendMQ;

    @Autowired
    @Qualifier("dmsWorkSendDetailMQ")
    private DefaultJMQProducer dmsWorkSendDetailMQ;

    @Autowired
    @Qualifier("operateHintTrackMQ")
    private DefaultJMQProducer operateHintTrackMQ;

    @Autowired
    @Qualifier("arSendDetailProducer")
    private DefaultJMQProducer arSendDetailProducer;

    @Autowired
    private TransBillScheduleService transBillScheduleService;

    @Autowired
    private NewSealVehicleService newSealVehicleService;

    @Resource(name = "transbillMService")
    private TransbillMService transbillMService;

    @Autowired
    @Qualifier("dmsWeightFlowService")
    private DmsWeightFlowService dmsWeightFlowService;

    @Autowired
    BoardCombinationService boardCombinationService;

    @Autowired
    SysConfigService sysConfigService;

    @Autowired
    private StoragePackageMService storagePackageMService;

    @Autowired
    private WaybillConsumableRecordService waybillConsumableRecordService;

    @Autowired
    private ColdChainQuarantineManager coldChainQuarantineManager;

    @Autowired
    private DmsInterturnManager dmsInterturnManager;

    @Autowired
    private ReversePartDetailService reversePartDetailService;

    @Autowired
    private WaybillService waybillService;

    @Autowired
    private WaybillPackageApi waybillPackageApi;

    @Autowired
    @Qualifier("dmsColdChainSendWaybill")
    private DefaultJMQProducer dmsColdChainSendWaybill;

    @Autowired
    private ColdChainSendService coldChainSendService;

    private static final int OPERATE_TYPE_REVERSE_SEND = 50;

    private static final int OPERATE_TYPE_FORWARD_SORTING = 1;

    private static final int OPERATE_TYPE_FORWARD_SEND = 2;

    private static final int OPERATE_TYPE_REVERSE_SORTING = 40;

    private static final int OPERATE_TYPE_CANCEL_L = 0;

    private static final int OPERATE_TYPE_CANCEL_Y = 1;

    private static final int OPERATE_TYPE_NEW_PACKAGE_SEND=60;

    private final Integer BATCH_NUM = 999;

    private final Integer BATCH_NUM_M = 99;

    /**
     * 组板发货任务的Redis缓存key的前缀
     */
    private final String REDIS_PREFIX_BOARD_DELIVERY= "BoardDelivery-";

    /**
     * 组板发货任务的Redis过期时间
     */
    private final int EXPIRE_REDIS_BOARD_TASK = 60;

    /**
     * 运单路由字段使用的分隔符
     */
    private static final  String WAYBILL_ROUTER_SPLITER = "\\|";

    /**
     * B网营业厅寄付现结运费发货拦截开关KEY（1开启，0关闭）
     */
    private static final  String FREIGHT_INTERCEPTION = "FREIGHT_INTERCEPTION";

    /**
     * 原包发货[前提条件]1：箱号、原包没有发货; 2：原包调用分拣拦截验证通过; 3：批次没有发车
     * （1）若原包发货，则补写分拣任务；若箱号发货则更新SEND_D状态及批次号
     * （2）写SEND_M表
     * （3）推送运单状态及回传周转箱
     * （4）对中转发货写入补全SEND_D任务
     * UPDATED BY wangtingwei@jd.com
     *
     * @param domain 发货对象
     * @return 1：发货成功  2：发货失败  4：需要用户确认
     */
    @Override
    @JProfiler(jKey = "DMSWEB.DeliveryServiceImpl.packageSend", mState = {JProEnum.TP, JProEnum.FunctionError})
    public SendResult packageSend(SendBizSourceEnum bizSource, SendM domain, boolean isForceSend, boolean isCancelLastSend) {
        logger.info("[一车一单发货]packageSend-箱号/包裹号:" + domain.getBoxCode() + ",批次号：" + domain.getSendCode() + ",操作站点：" + domain.getCreateSiteCode() + ",是否强制操作：" + isForceSend);
        // 若第一次校验不通过，需要点击选择确认框后，二次调用时跳过校验
        if (!isForceSend) {
            // 发货验证
            SendResult sendResult = this.beforeSendVerification(domain, true, isCancelLastSend);
            if (!SendResult.CODE_OK.equals(sendResult.getKey())) {
                return sendResult;
            }
        }
        if (isCancelLastSend) {
            this.doCancelLastSend(domain);
        }
        return this.doPackageSend(bizSource, domain);
    }

    /**
     * 一车一单发货逻辑，不包括多次发货取消上次发货校验和处理逻辑
     *
     * @param domain
     * @param isForceSend
     * @return
     */
    @Override
    public SendResult packageSend(SendBizSourceEnum bizSource, SendM domain, boolean isForceSend){
        logger.info("[一车一单发货]packageSend-箱号/包裹号:" + domain.getBoxCode() + ",批次号：" + domain.getSendCode() + ",操作站点：" + domain.getCreateSiteCode() + ",是否强制操作：" + isForceSend);
        // 若第一次校验不通过，需要点击选择确认框后，二次调用时跳过校验
        SendResult sendResult;
        if (!isForceSend) {
            // 发货验证
            sendResult = this.beforeSendVerification(domain, false, true);
            if (!SendResult.CODE_OK.equals(sendResult.getKey())) {
                return sendResult;
            }
        }
        sendResult = this.doPackageSend(bizSource, domain);
        return sendResult;
    }

    /**
     * 执行取消上次发货逻辑
     *
     * @param domain
     */
    private void doCancelLastSend(SendM domain) {
        CallerInfo callerInfo = Profiler.registerInfo("DMSWEB.DeliveryServiceImpl.packageSend.doCancelLastSend", false, true);
        SendM lastSendM = this.getRecentSendMByParam(domain.getBoxCode(), domain.getCreateSiteCode(), null, domain.getOperateTime());
        if (lastSendM != null) {
            this.dellCancelDeliveryMessage(getCancelSendM(lastSendM, domain), true);
        }
        Profiler.registerInfoEnd(callerInfo);
    }

    /**
     * 取消最近一次发货记录
     *
     * @param domain
     */
    @Override
    @JProfiler(jKey = "DMSWEB.DeliveryServiceImpl.packageSend.cancelLastSend", mState = {JProEnum.TP, JProEnum.FunctionError}, jAppName = Constants.UMP_APP_NAME_DMSWEB)
    public ThreeDeliveryResponse cancelLastSend(SendM domain) {
        SendM lastSendM = this.getRecentSendMByParam(domain.getBoxCode(), domain.getCreateSiteCode(), null, domain.getOperateTime());
        if (lastSendM != null) {
            //如果不是包裹号，需指定取消发货的目的地
            if (!BusinessHelper.isBoxcode(lastSendM.getBoxCode())) {
                domain.setReceiveSiteCode(lastSendM.getReceiveSiteCode());
            }
            //替换掉发货类型
            domain.setSendType(lastSendM.getSendType());
            //更新时间为操作时间
            domain.setUpdateTime(domain.getOperateTime());
            return this.dellCancelDeliveryMessage(domain, true);
        } else {
            return new ThreeDeliveryResponse(DeliveryResponse.CODE_Delivery_NO_MESAGE, DeliveryResponse.MESSAGE_Delivery_NO_PACKAGE, null);
        }
    }

    /**
     * 执行一车一单发货所有业务逻辑
     *
     * @param domain
     * @return
     */
    private SendResult doPackageSend(SendBizSourceEnum bizSource, SendM domain) {
        SendResult sendResult = new SendResult(SendResult.CODE_OK, SendResult.MESSAGE_OK);
        // 自动取消组板
        this.autoBoardCombinationCancel(domain);

        CallerInfo temp_info3 = Profiler.registerInfo("DMSWEB.DeliveryServiceImpl.packageSend.temp_info3", false, true);
        if (bizSource == null){
            bizSource = SendBizSourceEnum.NEW_PACKAGE_SEND;
        }
        // 一车一单发货逻辑
        this.packageSend(bizSource, domain);
        Profiler.registerInfoEnd(temp_info3);

        //获取发货提示语
        CallerInfo temp_info4 = Profiler.registerInfo("DMSWEB.DeliveryServiceImpl.packageSend.temp_info4", false, true);
        String hints = this.getPdaHints(domain);
        Profiler.registerInfoEnd(temp_info4);

        if (StringUtils.isNotBlank(hints)) {
            //发货环节产生加急提示，发加急提示追踪的mq消息
            this.sendDmsOperateHintTrackMQ(domain);
            sendResult.setKey(SendResult.CODE_WARN);
            sendResult.setValue(hints);
        }
        return sendResult;
    }

    /**
     * 自动取消组板
     *
     * @param domain
     */
    private void autoBoardCombinationCancel(SendM domain){
        //判断是否进行过组板，如果已经组板则从板中取消，并发送取消组板的全称跟踪
        SysConfigContent content = sysConfigService.getSysConfigJsonContent(Constants.SYS_CONFIG_BOARD_COM_CANCEL_ATUO_OPEN_DMS_CODES);
        if (content != null) {
            logger.info("从sysConfig表中获取key=" + Constants.SYS_CONFIG_BOARD_COM_CANCEL_ATUO_OPEN_DMS_CODES +
                    "的配置为:" + content + "操作单位:" + domain.getCreateSiteCode());
            if (content.getMasterSwitch() || content.getSiteCodes().contains(domain.getCreateSiteCode())) {
                this.boardCombinationCancel(domain);
            }
        }
    }

    /**
     * 发货环节产生加急提示，发加急提示追踪的mq消息
     *
     * @param domain
     */
    private void sendDmsOperateHintTrackMQ(SendM domain) {
        try {
            DmsOperateHintTrack dmsOperateHintTrack = new DmsOperateHintTrack();
            dmsOperateHintTrack.setWaybillCode(WaybillUtil.getWaybillCode(domain.getBoxCode()));
            dmsOperateHintTrack.setHintDmsCode(domain.getCreateSiteCode());
            dmsOperateHintTrack.setHintOperateNode(DmsOperateHintTrack.OPERATE_NODE_SEND);
            dmsOperateHintTrack.setOperateUserCode(domain.getCreateUserCode());
            dmsOperateHintTrack.setHintTime(new Date());
            String mqText = JSON.toJSONString(dmsOperateHintTrack);
            this.logger.info("发送MQ[" + operateHintTrackMQ.getTopic() + "],业务ID[" + dmsOperateHintTrack.getWaybillCode() + "],消息内容: " + mqText);
            this.operateHintTrackMQ.sendOnFailPersistent(dmsOperateHintTrack.getWaybillCode(), mqText);
        } catch (Exception e) {
            logger.error("发货提示语发mq异常,异常原因:" + e);
        }
    }

    /**
     * 发货校验
     *
     * @param domain
     * @param isUseMultiSendVerify  是否开启多次发货取消上次发货校验，否 - 走旧校验逻辑
     * @param isSkipMultiSendVerify 是否跳过校验多次发货取消上次发货
     * @return
     */
    private SendResult beforeSendVerification(SendM domain, boolean isUseMultiSendVerify, boolean isSkipMultiSendVerify) {
        SendResult result = new SendResult(SendResult.CODE_OK, SendResult.MESSAGE_OK);

        CallerInfo temp_info1 = Profiler.registerInfo("DMSWEB.DeliveryServiceImpl.packageSend.temp_info1", false, true);
        // 机构和操作人所属机构是否一致校验
        if (!checkSendM(domain)) {
            result.init(SendResult.CODE_SENDED, "当前批次始发ID与操作人所属单位ID不一致!");
            return result;
        }

        if (newSealVehicleService.checkSendCodeIsSealed(domain.getSendCode())) {
            result.init(SendResult.CODE_SENDED, "批次号已操作封车，请换批次！");
            return result;
        }

        // 判断是否使用多次发货取消上次发货
        if (isUseMultiSendVerify) {
            if (!isSkipMultiSendVerify) {
                // 多次发货取消上次发货校验
                if (!multiSendVerification(domain, result)) {
                    return result;
                }
            }
        } else {
            // 原有的发货校验
            String oldSendCode = getSendedCode(domain);
            if (StringUtils.isNotBlank(oldSendCode)) {
                result.init(SendResult.CODE_SENDED, "箱子已经在批次" + oldSendCode + "中发货");
                return result;
            }
        }
        Profiler.registerInfoEnd(temp_info1);

        // 根据发货的条码类型进行校验
        this.sendVerificationByBarCodeType(domain, result);

        //验证通过，补成第一个包裹号，如果后面发现这单是一单多件，再进行提示
        if (!BusinessUtil.isBoxcode(domain.getBoxCode()) && !WaybillUtil.isPackageCode(domain.getBoxCode()) &&
                WaybillUtil.isWaybillCode(domain.getBoxCode())) {
            logger.info("一车一单发货扫描运单[" + domain.getBoxCode() + "]，校验通过，生成包裹号:" +
                    BusinessHelper.getFirstPackageCodeByWaybillCode(domain.getBoxCode()));
            domain.setBoxCode(BusinessHelper.getFirstPackageCodeByWaybillCode(domain.getBoxCode()));
        }
        return result;
    }

    /**
     * 多次发货是否需要取消上次发货校验
     * 条件：若封车时间在当前时间一小时内或未封车,则提示是否取消上次发货
     * 说明：方法返回值true为继续走校验逻辑，false为校验不通过返回提示信息
     *
     * @param domain
     * @param result
     * @return
     */
    private boolean multiSendVerification(SendM domain, SendResult result) {
        // 根据箱号/包裹号 + 始发站点 + 目的站点获取发货记录
        SendM lastSendM = this.getRecentSendMByParam(domain.getBoxCode(), domain.getCreateSiteCode(), null, domain.getOperateTime());
        if (lastSendM != null) {
            String lastSendCode = lastSendM.getSendCode();
            if (StringUtils.isNotEmpty(lastSendCode)) {
                if (!this.sendSealTimeIsOverOneHour(lastSendCode, domain.getOperateTime())) {
                    if (domain.getReceiveSiteCode().equals(lastSendM.getReceiveSiteCode())) {
                        if (domain.getSendCode().equals(lastSendCode)) {
                            result.init(SendResult.CODE_SENDED, "箱子已经在该批次中发货，请勿重复操作");
                        } else {
                            result.init(SendResult.CODE_SENDED, "箱子已经在批次[" + lastSendCode + "]中发货");
                        }
                    } else {
                        result.setKey(SendResult.CODE_CONFIRM);
                        result.setValue("该包裹已发货，是否取消上次发货并重新发货？");
                        result.setReceiveSiteCode(domain.getReceiveSiteCode());
                        result.setInterceptCode(ConfirmMsgBox.CODE_CONFIRM_CANCEL_LAST_SEND);
                    }
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * 根据条码类型进行校验
     * 说明：方法返回值true为继续走校验逻辑，false为校验不通过返回提示信息
     *
     * @param domain
     * @param result
     * @return
     */
    private boolean sendVerificationByBarCodeType(SendM domain, SendResult result) {
        CallerInfo temp_info2 = Profiler.registerInfo("DMSWEB.DeliveryServiceImpl.packageSend.temp_info2", false, true);
        if (!BusinessUtil.isBoxcode(domain.getBoxCode())) {
            // 按包裹发货分拣校验
            return this.sortingVerificationByPackage(this.getSortingCheck(domain), result);
        } else if (siteService.getCRouterAllowedList().contains(domain.getCreateSiteCode())) {
            // 按箱发货，从箱中取出一单校验
            DeliveryResponse response = checkRouterForCBox(domain);
            if (DeliveryResponse.CODE_CROUTER_ERROR.equals(response.getCode())) {
                result.init(SendResult.CODE_CONFIRM, response.getMessage(), response.getCode(), null);
                return false;
            }
        }
        Profiler.registerInfoEnd(temp_info2);
        return true;
    }

    /**
     * 按包裹发货执行分拣校验
     * 说明：方法返回值true为继续走校验逻辑，false为校验不通过返回提示信息
     *
     * @param sortingCheck
     * @return
     */
    private boolean sortingVerificationByPackage(SortingCheck sortingCheck, SendResult result) {
        CallerInfo info1 = Profiler.registerInfo("DMSWEB.DeliveryServiceImpl.packageSend.callsortingcheck", false, true);
        SortingJsfResponse response;
        try {
            response = jsfSortingResourceService.check(sortingCheck);
        } catch (Exception ex) {
            logger.error("调用总部VER验证JSF服务失败", ex);
            result.init(DeliveryResponse.CODE_VER_CHECK_EXCEPTION, DeliveryResponse.MESSAGE_VER_CHECK_EXCEPTION, 100, 0);
            return false;
        } finally {
            Profiler.registerInfoEnd(info1);
        }

        if (!response.getCode().equals(200)) {
            //如果校验不OK
            CallerInfo infoSendFindByWaybillCode = Profiler.registerInfo("DMSWEB.DeliveryServiceImpl.packageSend.findByWaybillCode", false, true);
            //获得运单的预分拣站点
            Integer preSortingSiteCode = null;
            try {
                com.jd.bluedragon.common.domain.Waybill waybill = waybillCommonService.findWaybillAndPack(WaybillUtil.getWaybillCode(sortingCheck.getBoxCode()), true, false, false, false);
                if (null != waybill) {
                    preSortingSiteCode = waybill.getSiteCode();
                }
            } catch (Throwable e) {
                logger.error("一车一单获取预分拣站点异常", e);
            } finally {
                Profiler.registerInfoEnd(infoSendFindByWaybillCode);
            }

            if (response.getCode() >= 39000) {
                result.init(SendResult.CODE_CONFIRM, response.getMessage(), response.getCode(), preSortingSiteCode);
            } else {
                result.init(SendResult.CODE_SENDED, response.getMessage(), response.getCode(), preSortingSiteCode);
            }
            return false;
        }
        return true;
    }

    /**
     * 获取分拣校验对象
     *
     * @param domain
     * @return
     */
    private SortingCheck getSortingCheck(SendM domain) {
        //大件分拣拦截验证
        SortingCheck sortingCheck = new SortingCheck();
        sortingCheck.setReceiveSiteCode(domain.getReceiveSiteCode());
        sortingCheck.setCreateSiteCode(domain.getCreateSiteCode());
        sortingCheck.setBoxCode(domain.getBoxCode());
        sortingCheck.setPackageCode(domain.getBoxCode());
        sortingCheck.setBusinessType(domain.getSendType());
        sortingCheck.setOperateUserCode(domain.getCreateUserCode());
        sortingCheck.setOperateUserName(domain.getCreateUser());
        sortingCheck.setOperateTime(DateHelper.formatDateTime(new Date()));
        //// FIXME: 2018/3/26 待校验后做修改
        if (domain.getCreateSiteCode() != null && siteService.getCRouterAllowedList().contains(domain.getCreateSiteCode())) {
            //判断批次号目的地的站点类型，是64的走新逻辑，非64的走老逻辑
            BaseStaffSiteOrgDto siteInfo = baseService.queryDmsBaseSiteByCode(domain.getReceiveSiteCode() + "");
            if (siteInfo == null || siteInfo.getSiteType() != 64) {
                sortingCheck.setOperateType(1);
            } else {
                sortingCheck.setOperateType(OPERATE_TYPE_NEW_PACKAGE_SEND);
            }
        } else {
            sortingCheck.setOperateType(1);
        }
        return sortingCheck;
    }

    /**
     * 原包发货获取PDA提示语
     * @param sendM
     * @return
     */
    private String getPdaHints(SendM sendM) {
        String msg = "";
        try {
            if(WaybillUtil.isPackageCode(sendM.getBoxCode())){
                //原包
                msg = dmsOperateHintService.getDeliveryHintMessageByWaybillCode(WaybillUtil.getWaybillCode(sendM.getBoxCode()));
            }
            logger.info("redis取PDA提示语结果："+msg);
        }catch (Throwable e){
            logger.error("redis取PDA提示语失败："+ JsonHelper.toJson(sendM), e);
        }
        return msg;
    }

    /**
     * 根据箱号查询箱号的运单号
     * @param boxCode
     * @return
     */
    public List<String>  getWaybillCodesByBoxCodeAndFetchNum(String boxCode,Integer fetchNum){
        Box box = this.boxService.findBoxByCode(boxCode);
        if(box != null) {
            return sendDatailReadDao.getWaybillCodesByBoxCodeAndFetchNum(boxCode, box.getCreateSiteCode(),fetchNum);
        }else{
            logger.warn("一车一单发货箱号为空："+boxCode);
        }
        return null;
    }

    /**
     * （1）若原包发货，则补写分拣任务；若箱号发货则更新SEND_D状态及批次号
     * （2）写SEND_M表
     * （3）推送运单状态及回传周转箱
     * （4）对中转发货写入补全SEND_D任务
     * @param domain 发货对象
     * @return 1：发货成功
     */
    @Override
    @JProfiler(jKey = "DMSWEB.DeliveryServiceImpl.offlinePackageSend", mState = {JProEnum.TP, JProEnum.FunctionError})
    public SendResult offlinePackageSend(SendBizSourceEnum sourceEnum, SendM domain) {
        if (StringUtils.isBlank(getSendedCode(domain))) {
            //未发过货的才执行发货
            this.packageSend(sourceEnum, domain);
        }
        return new SendResult(1, "发货成功");
    }

    /**
     * 组板发货，写组板发货任务
     *
     * @param domain
     * @return
     */
    @Override
    @JProfiler(jKey = "DMSWEB.DeliveryServiceImpl.boardSend", mState = {JProEnum.TP, JProEnum.FunctionError})
    public SendResult boardSend(SendM domain,boolean isForceSend) {
        String boardCode = domain.getBoardCode();
        if(!isForceSend){
            //1.组板发货批次，板号校验（强校验）
            if(!checkSendM(domain)){
                return new SendResult(SendResult.CODE_SENDED, "当前批次始发ID与操作人所属单位ID不一致!");
            }
            //2.判断批次号是否已经封车
            if(newSealVehicleService.checkSendCodeIsSealed(domain.getSendCode())){
                return new SendResult(SendResult.CODE_SENDED, "批次号已操作封车，请换批次！");
            }
            //3.校验是否操作过按板发货,按板号和createSiteCode查询send_m表看是是否有记录
            if(sendMDao.checkSendByBoard(domain)){
                return new SendResult(SendResult.CODE_SENDED,"已经操作过按板发货.");
            }
            //4.校验板号和批次号的目的地是否一致，并校验板号的合法性
            SendResult checkResponse = checkBoard(boardCode, domain);
            if(!SendResult.CODE_OK.equals(checkResponse.getKey())){
                return checkResponse;
            }
        }

        //5.写发货任务
        pushBoardSendTask(domain,Task.TASK_TYPE_BOARD_SEND);

        //6.写组板发货任务完成，调用TC执行关板
        closeBoard(boardCode, domain);

        return new SendResult(SendResult.CODE_OK, SendResult.MESSAGE_OK);
    }

    /**
     * 写组板发货任务完成，调用TC执行关板
     */
    private void closeBoard(String boardCode, SendM domain){
        try{
            Response<Boolean> closeBoardResponse = boardCombinationService.closeBoard(boardCode);
            logger.info("组板发货关板板号：" + boardCode + "，关板结果：" + JsonHelper.toJson(closeBoardResponse));
            if(!JdResponse.CODE_OK.equals(closeBoardResponse.getCode()) || !closeBoardResponse.getData()){//关板失败
                logger.error("组板发货调用TC关板失败：" + JsonHelper.toJson(closeBoardResponse));
            }
        } catch (Exception e) {
            logger.error("组板发货调用TC关板异常：" + JsonHelper.toJson(domain),e);
        }
    }

    /**
     * 校验板号和批次号的目的地是否一致，并校验板号的合法性
     * @param boardCode
     * @param domain
     * @return
     */
    private SendResult checkBoard(String boardCode, SendM domain){
        try{
            BoardResponse boardResponse=boardCombinationService.getBoardByBoardCode(boardCode);
            if(boardResponse.getStatusInfo() != null && boardResponse.getStatusInfo().size() >0){
                return new SendResult(SendResult.CODE_SENDED, boardResponse.buildStatusMessages());
            }
            if(boardResponse.getReceiveSiteCode()==null){
                return new SendResult(SendResult.CODE_SENDED,"获取板号目的地失败");
            }
            if(SerialRuleUtil.getReceiveSiteCodeFromSendCode(domain.getSendCode())==null){
                return new SendResult(SendResult.CODE_SENDED,"获取批次号目的地失败");
            }
            if(!SerialRuleUtil.getReceiveSiteCodeFromSendCode(domain.getSendCode()).equals(boardResponse.getReceiveSiteCode())){
                return new SendResult(SendResult.CODE_CONFIRM,"板号目的地与批次号目的地不一致，是否强制操作发货？");
            }
        }catch (Exception e){
            logger.error("组板发货板号校验失败:" + JsonHelper.toJson(domain),e);
            return new SendResult(SendResult.CODE_SENDED,"组板发货板号校验失败");
        }
        return new SendResult(SendResult.CODE_OK,"校验通过!");
    }

    /**
     * 一车一单发货数据落库，写相关的异步任务
     *
     * （1）原包发货，补写分拣任务
     * （2）写SEND_M表
     * （3）推送运单状态及回传周转箱
     * （4）对中转发货写入补全SEND_D任务
     *
     * @param domain 发货对象
     * @return 1：发货成功  2：发货失败
     */
    @JProfiler(jKey = "DMSWEB.DeliveryServiceImpl.packageSend", mState = {JProEnum.TP, JProEnum.FunctionError})
    @Override
    public void packageSend(SendBizSourceEnum sourceEnum, SendM domain) {
        if (sourceEnum != null) {
            // 设置发货来源
            domain.setBizSource(sourceEnum.getCode());
        }
        // 插入SEND_M
        this.sendMManager.insertSendM(domain);
        // 判断是按箱发货还是包裹发货
        if (!BusinessUtil.isBoxcode(domain.getBoxCode())) {
            // 按包裹 补分拣任务 大件写TASK_SORTING
            pushSorting(domain);
        } else {
            // 按箱
            SendDetail sendDetail = new SendDetail();
            sendDetail.setBoxCode(domain.getBoxCode());
            sendDetail.setCreateSiteCode(domain.getCreateSiteCode());
            sendDetail.setReceiveSiteCode(domain.getReceiveSiteCode());
            //更新SEND_D状态
            this.updateCancel(sendDetail);
        }
        // 判断是否是中转发货
        this.transitSend(domain);
        this.pushStatusTask(domain);
    }

    /**
     * 箱子已发货则返回已发货批次号
     * @param domain
     */
    private String getSendedCode(SendM domain) {
        SendM sendM = this.getRecentSendMByParam(domain.getBoxCode(), domain.getCreateSiteCode(), domain.getReceiveSiteCode(), null);
        if (sendM != null) {
            return sendM.getSendCode();
        }
        return null;
    }

    /**
     * 获取最近一次的发货信息
     *
     * @param boxCode
     * @param createSiteCode
     * @param receiveSiteCode
     * @return
     */
    private SendM getRecentSendMByParam(String boxCode, Integer createSiteCode, Integer receiveSiteCode, Date operateTime) {
        //查询箱子发货记录
        /* 不直接使用domain的原因，SELECT语句有[test="createUserId!=null"]等其它 */
        SendM queryPara = new SendM();
        queryPara.setBoxCode(boxCode);
        queryPara.setCreateSiteCode(createSiteCode);
        if (receiveSiteCode != null) {
            queryPara.setReceiveSiteCode(receiveSiteCode);
        }
        if (operateTime != null){
            queryPara.setUpdateTime(operateTime);
        }
        List<SendM> sendMList = this.sendMDao.selectBySendSiteCode(queryPara);
        if (null != sendMList && sendMList.size() > 0) {
            return sendMList.get(0);
        }
        return null;
    }

    /**
     * 判断发货时间是否在封车一小时后
     *
     * @param sendCode
     * @param operateTime
     * @return
     */
    private boolean sendSealTimeIsOverOneHour(String sendCode, Date operateTime) {
        // 获取封车时间
        Long sealCarTime = newSealVehicleService.getSealCarTimeBySendCode(sendCode);
        if (operateTime != null) {
            long operateTimeMilli = operateTime.getTime();
            // 已封车 发货操作时间在上次发货后封车时间1小时内，则取消上次发货
            if (sealCarTime != null && operateTimeMilli - sealCarTime > DateHelper.ONE_HOUR_MILLI) {
                return true;
            }
        }
        return false;
    }

    private SendM getCancelSendM(SendM lastSendM, SendM domain) {
        SendM sendM = new SendM();
        sendM.setBoxCode(lastSendM.getBoxCode());
        sendM.setCreateSiteCode(lastSendM.getCreateSiteCode());
        if (!BusinessHelper.isBoxcode(lastSendM.getBoxCode())) {
            sendM.setReceiveSiteCode(lastSendM.getReceiveSiteCode());
        }
        sendM.setSendType(lastSendM.getSendType());
        sendM.setUpdaterUser(domain.getCreateUser());
        sendM.setUpdateUserCode(domain.getCreateUserCode());
        sendM.setUpdateTime(DateHelper.add(domain.getOperateTime(), Calendar.SECOND, -10));
        sendM.setYn(0);
        return sendM;
    }

    /**
     * 推分拣任务
     * @param domain
     */
    @Override
    public void pushSorting(SendM domain) {
        BaseStaffSiteOrgDto create = siteService.getSite(domain.getCreateSiteCode());
        String createSiteName = null != create ? create.getSiteName() : null;
        BaseStaffSiteOrgDto receive = siteService.getSite(domain.getReceiveSiteCode());
        String receiveSiteName = null != receive ? receive.getSiteName() : null;
        Task task = new Task();
        task.setBoxCode(domain.getBoxCode());
        task.setCreateSiteCode(domain.getCreateSiteCode());
        task.setReceiveSiteCode(domain.getReceiveSiteCode());
        task.setBusinessType(10);
        task.setType(Task.TASK_TYPE_SORTING);
        task.setTableName(Task.getTableName(Task.TASK_TYPE_SORTING));
        task.setSequenceName(Task.getSequenceName(task.getTableName()));
        task.setKeyword1(domain.getCreateSiteCode().toString());
        task.setKeyword2(domain.getBoxCode());
        task.setOperateTime(new Date(domain.getOperateTime().getTime()-Constants.DELIVERY_DELAY_TIME));
        taskService.initFingerPrint(task);
        task.setOwnSign(BusinessHelper.getOwnSign());
        SortingRequest sortDomain = new SortingRequest();
        sortDomain.setOperateTime(DateHelper.formatDateTimeMs(new Date(domain.getOperateTime().getTime()-Constants.DELIVERY_DELAY_TIME)));
        sortDomain.setBoxCode(domain.getBoxCode());
        sortDomain.setUserCode(domain.getCreateUserCode());
        sortDomain.setUserName(domain.getCreateUser());
        sortDomain.setPackageCode(domain.getBoxCode());
        sortDomain.setSiteName(createSiteName);
        sortDomain.setIsCancel(0);
        sortDomain.setSiteCode(domain.getCreateSiteCode());
        sortDomain.setBsendCode("");
        sortDomain.setIsLoss(0);
        sortDomain.setFeatureType(0);
        sortDomain.setUserName(domain.getCreateUser());
        sortDomain.setBusinessType(10);
        sortDomain.setWaybillCode(SerialRuleUtil.getWaybillCode(domain.getBoxCode()));
        sortDomain.setReceiveSiteCode(domain.getReceiveSiteCode());
        sortDomain.setReceiveSiteName(receiveSiteName);
        task.setBody(JsonHelper.toJson(new SortingRequest[]{sortDomain}));
        taskService.add(task, true);
        logger.info("一车一单插入task_sorting" + JsonHelper.toJson(task));
    }

    @Override
    public int pushStatusTask(SendM domain) {
        SendTaskBody body = new SendTaskBody();
        body.setHandleCategory(2);
        body.copyFromParent(domain);
        Task tTask = new Task();
        tTask.setBoxCode(domain.getBoxCode());
        tTask.setBody(JsonHelper.toJson(body));
        tTask.setCreateSiteCode(domain.getCreateSiteCode());
        tTask.setKeyword2(String.valueOf(domain.getSendType()));
        tTask.setReceiveSiteCode(domain.getReceiveSiteCode());
        tTask.setType(Task.TASK_TYPE_SEND_DELIVERY);
        tTask.setTableName(Task.getTableName(Task.TASK_TYPE_SEND_DELIVERY));
        tTask.setSequenceName(Task.getSequenceName(Task.TABLE_NAME_SEND));
        String ownSign = BusinessHelper.getOwnSign();
        tTask.setOwnSign(ownSign);
        // 1 回传运单状态
        tTask.setKeyword1("1");
        tTask.setFingerprint(Md5Helper.encode(domain.getSendCode() + "_" + tTask.getKeyword1() + domain.getBoxCode() + tTask.getKeyword1()));
        tTaskService.add(tTask, true);
        //只有箱号添加回传周转箱任务
        if (BusinessHelper.isBoxcode(domain.getBoxCode())) {
            // 2回传周转箱号
            tTask.setKeyword1("2");
            tTask.setFingerprint(Md5Helper.encode(domain.getSendCode() + "_" + tTask.getKeyword1() + domain.getBoxCode() + tTask.getKeyword1()));
            tTaskService.add(tTask, true);
        }
        return 0;
    }

    /**
     * 推组板发货任务
     * @param domain
     * @return
     */
    private boolean pushBoardSendTask(SendM domain,Integer taskType) {
        Task tTask = new Task();
        tTask.setBoxCode(domain.getBoardCode());
        tTask.setBody(JsonHelper.toJson(domain));
        tTask.setCreateSiteCode(domain.getCreateSiteCode());
        tTask.setKeyword2(String.valueOf(domain.getSendType()));
        tTask.setReceiveSiteCode(domain.getReceiveSiteCode());
        tTask.setType(taskType);
        tTask.setTableName(Task.getTableName(taskType));
        String ownSign = BusinessHelper.getOwnSign();
        tTask.setOwnSign(ownSign);
        tTask.setKeyword1(domain.getBoardCode());
        tTask.setFingerprint(Md5Helper.encode(domain.getSendCode() + "_" + tTask.getKeyword1() + domain.getBoardCode() + tTask.getKeyword2()));
        logger.info("组板发货任务推送成功：" + JsonHelper.toJson(tTask));
        tTaskService.add(tTask, true);
        //写redis记录任务状态
        if(Task.TASK_TYPE_BOARD_SEND.equals(taskType)) {
            String redisKey = REDIS_PREFIX_BOARD_DELIVERY + domain.getBoardCode();
            redisManager.setex(redisKey, EXPIRE_REDIS_BOARD_TASK, domain.getBoardCode());
        }
        return true;
    }

    /**
     * 查询箱号发货记录
     *
     * @param boxCode 箱号
     * @return 发货记录列表
     */
    @JProfiler(jKey = "DMSWORKER.deliveryServiceImpl.getSendMListByBoxCode", mState = {JProEnum.TP,
            JProEnum.FunctionError})
    @Override
    public List<SendM> getSendMListByBoxCode(String boxCode) {
        SendM domain = new SendM();
        domain.setBoxCode(boxCode);
        return this.sendMDao.findSendMByBoxCode(domain);
    }

    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    @Override
    public Integer add(SendDetail sendDetail) {
        if (sendDetail.getPackageBarcode() == null) {
            sendDetail.setPackageNum(0);
        } else {
            Integer packageNum = BusinessUtil.getPackNumByPackCode(sendDetail.getPackageBarcode());
            if (packageNum == null) {
                this.logger.error("无法获得包裹数量[" + sendDetail.getPackageBarcode()
                        + "]");
                sendDetail.setPackageNum(0);
            } else {
                sendDetail.setPackageNum(packageNum);
            }
        }
        return this.sendDatailDao.add(SendDatailDao.namespace, sendDetail);
    }

    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    @Override
    public Integer update(SendDetail sendDetail) {
        return this.sendDatailDao.update(SendDatailDao.namespace, sendDetail);
    }

    @JProfiler(jKey = "Bluedragon_dms_center.dms.method.deliveryService.updateCancel", mState = {JProEnum.TP,
            JProEnum.FunctionError})
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public Integer updateCancel(SendDetail sendDetail) {
        return this.sendDatailDao.updateCancel(sendDetail);
    }

    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public void saveOrUpdateBatch(List<SendDetail> sdList) {
        List<SendDetail>[] sendArray = splitList(sdList);
        List<String> result = new ArrayList<String>();

        List<SendDetail> updateList = new ArrayList<SendDetail>();
        //批量查询是否存在send_d
        for (List<SendDetail> list : sendArray) {
            String boxCode = StringHelper.join(list, "getBoxCode", Constants.SEPARATOR_COMMA, Constants.SEPARATOR_APOSTROPHE);
            Integer createSiteCode = list.get(0).getCreateSiteCode();
            Integer receiveSiteCode = list.get(0).getReceiveSiteCode();
            SendDetail request = new SendDetail();
            request.setBoxCode(boxCode);
            request.setCreateSiteCode(createSiteCode);
            request.setReceiveSiteCode(receiveSiteCode);
            result.addAll(sendDatailDao.batchQuerySendDList(request));
        }

        //对不存在send_d的包裹写入
        for (SendDetail senddetail : sdList) {
            if (!result.contains(senddetail.getBoxCode())) {
                this.add(senddetail);
            } else {
                updateList.add(senddetail);
            }
        }
        //对于存在send_d的执行批量更新
        sendArray = splitList(updateList);
        for (List<SendDetail> list : sendArray) {
            String boxCode = StringHelper.join(list, "getBoxCode", Constants.SEPARATOR_COMMA, Constants.SEPARATOR_APOSTROPHE);
            Integer createSiteCode = list.get(0).getCreateSiteCode();
            Integer receiveSiteCode = list.get(0).getReceiveSiteCode();
            SendDetail request = new SendDetail();
            request.setBoxCode(boxCode);
            request.setCreateSiteCode(createSiteCode);
            request.setReceiveSiteCode(receiveSiteCode);
            sendDatailDao.updateCancelBatch(request);
        }
    }

    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    @Override
    public void saveOrUpdate(SendDetail sendDetail) {
        if (Constants.NO_MATCH_DATA == this.update(sendDetail).intValue()) {
            this.add(sendDetail);
        }
    }

    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    @Override
    public Boolean canCancel(SendDetail sendDetail) {
        return this.sendDatailDao.canCancel(sendDetail);
    }
    /**
     * 取消发货操作 不判断send_type added by zhanglei
     * @param sendDetail
     * @return
     */
    @Override
    public Boolean canCancel2(SendDetail sendDetail) {
        return this.sendDatailDao.canCancel2(sendDetail);
    }

    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    @Override
    public Boolean canCancelFuzzy(SendDetail sendDetail) {
        return this.sendDatailDao.canCancelFuzzy(sendDetail);
    }

    /**
     * 发货主表数据写入
     *
     * @param sendMList 发货相关数据
     */
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void insertSendM(SendBizSourceEnum source, List<SendM> sendMList, List<String> list) {
        Integer sourceCode = null;
        if (source != null){
            sourceCode = source.getCode();
        }
        for (SendM dSendM : sendMList) {
            if (!list.contains(dSendM.getBoxCode())) {
                dSendM.setBizSource(sourceCode);
                this.sendMManager.insertSendM(dSendM);
            }
        }
    }

    /***
     * 发货写入任务表
     */
    private void addTaskSend(SendM sendM) {
        SendTaskBody body = new SendTaskBody();
        body.setHandleCategory(1);
        body.copyFromParent(sendM);
        Task tTask = new Task();
        tTask.setBoxCode(sendM.getSendCode());
        //tTask.setBody(sendM.getSendCode());
        tTask.setBody(JsonHelper.toJson(body));
        tTask.setCreateSiteCode(sendM.getCreateSiteCode());
        tTask.setKeyword2(String.valueOf(sendM.getSendType()));
        tTask.setReceiveSiteCode(sendM.getReceiveSiteCode());
        tTask.setType(Task.TASK_TYPE_SEND_DELIVERY);
        tTask.setTableName(Task.getTableName(Task.TASK_TYPE_SEND_DELIVERY));
        tTask.setSequenceName(Task.getSequenceName(Task.TABLE_NAME_SEND));
        String ownSign = BusinessHelper.getOwnSign();
        tTask.setOwnSign(ownSign);
        tTask.setKeyword1("1");// 1 回传运单状态
        tTask.setFingerprint(sendM.getSendCode() + "_" + tTask.getKeyword1());
        tTaskService.add(tTask, true);
        tTask.setKeyword1("2");// 2回传周转箱号
        tTask.setFingerprint(sendM.getSendCode() + "_" + tTask.getKeyword1());
        tTaskService.add(tTask, true);

        tTask.setBody(sendM.getSendCode());

        tTask.setKeyword1("3");// 3回传dmc
        tTask.setFingerprint(sendM.getSendCode() + "_" + tTask.getKeyword1());
        tTaskService.add(tTask, true);
        // 发货类型
        Integer sendType = sendM.getSendType();
        // 逆向发货
        if (sendType != null && Constants.BUSSINESS_TYPE_REVERSE == sendType.intValue()) {
            //&& sendM.getSendCode().startsWith(Box.BOX_TYPE_WEARHOUSE)  取消逆向发车的时候推送仓储任务，修改到发货环节推送 20150724
            tTask.setKeyword1("4");//4逆向任务
            tTask.setFingerprint(sendM.getSendCode() + "_" + tTask.getKeyword1());
            tTaskService.add(tTask);
        }

		/* 第三方发货推送财务
		*  写到task_delviery_to_finance_batch表*/
        if (sendType != null && Constants.BUSSINESS_TYPE_THIRD_PARTY == sendType.intValue()){
            String sendCode = sendM.getSendCode();
            Task task = new Task();
            task.setKeyword1(Constants.BUSSINESS_TYPE_THIRD_PARTY+"");
            task.setBody(sendCode);
            task.setTableName(Task.TABLE_NAME_DELIVERY_TO_FINANCE_BATCH);
            task.setType(Task.TASK_TYPE_DELIVERY_TO_FINANCE_BATCH);
            task.setOwnSign(BusinessHelper.getOwnSign());

            taskService.add(task);
        }

        /*添加自动化分拣发货回传波次表*/
        BatchSend batchSend = new BatchSend();
        batchSend.setSendCode(sendM.getSendCode());
        batchSendDao.batchUpdateStatus(batchSend);
    }

    /**
     * 插入pda操作日志表
     *
     * @param sendDetail
     */
    private void addOperationLog(SendDetail sendDetail) {
        OperationLog operationLog = new OperationLog();
        operationLog.setBoxCode(sendDetail.getBoxCode());
        operationLog.setCreateSiteCode(sendDetail.getCreateSiteCode());
        operationLog.setCreateTime(sendDetail.getOperateTime());
        operationLog.setCreateUser(sendDetail.getCreateUser());
        operationLog.setCreateUserCode(sendDetail.getCreateUserCode());
        if (sendDetail.getIsCancel() == 2) {
            operationLog.setLogType(OperationLog.LOG_TYPE_SEND_CANCELDELIVERY);
        } else {
            operationLog.setLogType(OperationLog.LOG_TYPE_SEND_DELIVERY);
        }
        operationLog.setOperateTime(sendDetail.getOperateTime());
        operationLog.setPackageCode(sendDetail.getPackageBarcode());
        operationLog.setPickupCode(sendDetail.getPickupCode());
        operationLog.setReceiveSiteCode(sendDetail.getReceiveSiteCode());
        operationLog.setUpdateTime(sendDetail.getUpdateTime());
        operationLog.setWaybillCode(sendDetail.getWaybillCode());
        operationLogService.add(operationLog);
    }

    /**
     * 生成发货数据处理
     *
     * @param sendMList 发货相关数据
     */
    @Override
    public DeliveryResponse dellDeliveryMessage(SendBizSourceEnum source, List<SendM> sendMList) {
        try {
            return this.dellCreateSendM(source, sendMList);
        } catch (Exception e) {
            this.logger.error("生成发货数据处理", e);
            return new DeliveryResponse(DeliveryResponse.CODE_Delivery_ERROR,
                    DeliveryResponse.MESSAGE_Delivery_ERROR);
        }
    }

    /**
     * 发货主表数据处理
     *
     * @param sendMList 发货相关数据
     */
    private DeliveryResponse dellCreateSendM(SendBizSourceEnum source, List<SendM> sendMList) {
        CallerInfo info1 = Profiler.registerInfo("Bluedragon_dms_center.dms.method.delivery.send", false, true);

        if(sendMList == null || sendMList.size() < 1){
            return new DeliveryResponse(JdResponse.CODE_SENDDATA_GENERATED_EMPTY,
                    JdResponse.MESSAGE_SENDDATA_GENERATED_EMPTY);
        }

        Collections.sort(sendMList);
        //批次号封车校验，已封车不能发货
        if (newSealVehicleService.checkSendCodeIsSealed(sendMList.get(0).getSendCode())) {
            return new DeliveryResponse(DeliveryResponse.CODE_SEND_CODE_ERROR, DeliveryResponse.MESSAGE_SEND_CODE_ERROR);
        }

        /*查询已发货的箱号*/
        List<String> list = batchQuerySendMList(sendMList);

        Profiler.registerInfoEnd(info1);
        // 取消发货在发货状态位回执
        this.cancelStatusReceipt(sendMList, list);

        CallerInfo info2 = Profiler.registerInfo("Bluedragon_dms_center.dms.method.delivery.send2",Constants.UMP_APP_NAME_DMSWEB, false, true);
        // 写入发货表数据
        this.insertSendM(source, sendMList, list);

//        //判断是否操作过组板，如果操作过，则从板上取消
//        this.boardCombinationCancelBatch(sendMList, list);

        for (SendM domain : sendMList) {
            this.transitSend(domain);//插入中转任务
        }
        // 写入任务
        addTaskSend(sendMList.get(0));
        Profiler.registerInfoEnd(info2);

        return new DeliveryResponse(JdResponse.CODE_OK, JdResponse.MESSAGE_OK);
    }


    /**
     * 批量判断箱号是否已经发货，提出公用，减少查询次数
     * *
     */
    private List<String> batchQuerySendMList(List<SendM> sendMList) {
        List<SendM>[] sendArray = splitSendMList(sendMList);
        List<String> result = new ArrayList<String>();
        for (List<SendM> list : sendArray) {
            String boxCode = StringHelper.join(list, "getBoxCode", Constants.SEPARATOR_COMMA, Constants.SEPARATOR_APOSTROPHE);
            Integer createSiteCode = list.get(0).getCreateSiteCode();
            Integer receiveSiteCode = list.get(0).getReceiveSiteCode();
            SendM request = new SendM();
            request.setBoxCode(boxCode);
            request.setCreateSiteCode(createSiteCode);
            request.setReceiveSiteCode(receiveSiteCode);
            result.addAll(sendMDao.batchQuerySendMList(request));
        }

        return result;
    }

    /**
     * 次发货数据状态更新,添加批量操作
     *
     * @param sendMList 待发货列表
     * @param list      已发货的箱号列表
     */
    private void cancelStatusReceipt(List<SendM> sendMList, List<String> list) {
        //操作过取消发货的箱子查询  result结果集
        List<SendM>[] sendArray = splitSendMList(sendMList);
        List<String> result = new ArrayList<String>();
        for (List<SendM> slist : sendArray) {
            String boxCode = StringHelper.join(slist, "getBoxCode", Constants.SEPARATOR_COMMA, Constants.SEPARATOR_APOSTROPHE);
            Integer createSiteCode = slist.get(0).getCreateSiteCode();
            Integer receiveSiteCode = slist.get(0).getReceiveSiteCode();
            SendM request = new SendM();
            request.setBoxCode(boxCode);
            request.setCreateSiteCode(createSiteCode);
            request.setReceiveSiteCode(receiveSiteCode);
            result.addAll(sendMDao.batchQueryCancelSendMList(request));
        }
        List<SendDetail> sdList = new ArrayList<SendDetail>();

        for (SendM tsendM : sendMList) {
            SendDetail tSendDatail = new SendDetail();
            tSendDatail.setBoxCode(tsendM.getBoxCode());
            tSendDatail.setCreateSiteCode(tsendM.getCreateSiteCode());
            tSendDatail.setReceiveSiteCode(tsendM.getReceiveSiteCode());
            // 未操作过发货的或者发货已取消的
            if (!list.contains(tsendM.getBoxCode())) {
                // 箱号并且取消发货的
                if (BusinessHelper.isBoxcode(tsendM.getBoxCode()) && result.contains(tsendM.getBoxCode())) {
                    tSendDatail.setStatus(2);
                    // 重置包裹信息发货状态
                    this.updateCancel(tSendDatail);
                }
                // 包裹号或者面单号
                if (WaybillUtil.isPackageCode(tsendM.getBoxCode())
                        || WaybillUtil.isSurfaceCode(tsendM.getBoxCode())) {
                    // 补全包裹信息
                    this.fillPickup(tSendDatail, tsendM);
                    tSendDatail.setOperateTime(tsendM.getCreateTime());
                    sdList.add(tSendDatail);
                }
            }
        }
        //批量处理
        if (sdList != null && !sdList.isEmpty()) {
            this.saveOrUpdateBatch(sdList);
        }

    }

    /***
     * 补全包裹信息
     */
    private void fillPickup(SendDetail tSendDatail, SendM tsendM) {
        tSendDatail.setCreateUser(tsendM.getCreateUser());
        tSendDatail.setCreateUserCode(tsendM.getCreateUserCode());
        tSendDatail.setSendType(tsendM.getSendType());
        tSendDatail.setStatus(0);
        tSendDatail.setIsCancel(OPERATE_TYPE_CANCEL_L);
        tSendDatail.setOperateTime(new Date());
        tSendDatail.setUpdateTime(new Date());
        if (WaybillUtil.isPackageCode(tsendM.getBoxCode())) {
            // 如果大件包裹没有分拣添加明细表信息
            tSendDatail.setPackageBarcode(tsendM.getBoxCode());
            tSendDatail.setWaybillCode(WaybillUtil.getWaybillCode(tsendM
                    .getBoxCode()));
        } else if (WaybillUtil.isSurfaceCode(tsendM.getBoxCode())) {
            if (WaybillUtil.isPickupCodeWW(tsendM.getBoxCode())) {
                tSendDatail.setWaybillCode(tsendM.getBoxCode());
            } else {
                BaseEntity<PickupTask> pickup = null;
                try {
                    pickup = this.waybillPickupTaskApi.getDataBySfCode(tsendM.getBoxCode());
                } catch (Exception e) {
                    this.logger.error("调用取件单号信息ws接口异常");
                }
                if (pickup != null && pickup.getData() != null) {
                    tSendDatail.setPickupCode(pickup.getData().getPickupCode());
                    tSendDatail.setPackageBarcode(tsendM.getBoxCode());
                    tSendDatail.setWaybillCode(pickup.getData().getOldWaybillCode());
                }
            }
        }
    }

    @Override
    public DeliveryResponse findSendMByBoxCode(SendM tSendM, boolean isTransferSend) {
        DeliveryResponse response = deliveryCheckHasSend(tSendM);
        if (JdResponse.CODE_OK.equals(response.getCode())) {
            response = deliveryCheckTransit(tSendM, isTransferSend);
        }
        if (JdResponse.CODE_OK.equals(response.getCode())) {
            response = threeDeliveryCheck(tSendM);
        }
        return response;
    }

    /**
     * 三方发货时校验箱子是否完验
     * @param tSendM
     * @return
     */
    private DeliveryResponse threeDeliveryCheck(SendM tSendM){
        DeliveryResponse response = new DeliveryResponse(JdResponse.CODE_OK, JdResponse.MESSAGE_OK);
        if (BusinessHelper.isBoxcode(tSendM.getBoxCode())) {
            Box box = this.boxService.findBoxByCode(tSendM.getBoxCode());
            if (box != null) {
                if (tSendM.getSendType().equals(Constants.BUSSINESS_TYPE_THIRD_PARTY)) {
                    // 查找该箱子的包裹数量
                    SendDetail tsendDatail = new SendDetail();
                    tsendDatail.setBoxCode(tSendM.getBoxCode());
                    tsendDatail.setCreateSiteCode(tSendM.getCreateSiteCode());
                    tsendDatail.setReceiveSiteCode(tSendM.getReceiveSiteCode());
                    tsendDatail.setIsCancel(OPERATE_TYPE_CANCEL_Y);
                    List<SendDetail> sendDatailist = this.sendDatailDao.querySendDatailsBySelective(tsendDatail);
                    if (sendDatailist != null && !sendDatailist.isEmpty()) {
                        response.setMessage(StringHelper.getStringValue(sendDatailist.size()));
                    }
                    if (inspectionExceptionService.queryExceptions(tSendM.getCreateSiteCode(),tSendM.getReceiveSiteCode(),tSendM.getBoxCode()) > 0) {
                        // 第三方发货验证是否存在异常
                        response.setCode(DeliveryResponse.CODE_Delivery_ALL_CHECK);
                        response.setMessage(DeliveryResponse.MESSAGE_Delivery_ALL_CHECK);
                    }
                }
            } else {
                response.setCode(DeliveryResponse.CODE_Delivery_NO_MESAGE);
                response.setMessage(DeliveryResponse.MESSAGE_Delivery_NO_MESAGE);
            }
        }
        return response;
    }

    /**
     * 箱号与目的地不一致校验
     *
     * @param tSendM
     * @return
     */
    private DeliveryResponse deliveryCheckTransit(SendM tSendM, boolean isTransferSend) {
        DeliveryResponse response = new DeliveryResponse(JdResponse.CODE_OK, JdResponse.MESSAGE_OK);
        if (BusinessHelper.isBoxcode(tSendM.getBoxCode())) {
            Box box = this.boxService.findBoxByCode(tSendM.getBoxCode());
            if (box != null) {
                if (!box.getReceiveSiteCode().equals(tSendM.getReceiveSiteCode()) && !isTransferSend) {
                    response.setCode(DeliveryResponse.CODE_Delivery_TRANSIT);
                    response.setMessage(DeliveryResponse.MESSAGE_Delivery_TRANSIT);
                }
            } else {
                response.setCode(DeliveryResponse.CODE_Delivery_NO_MESAGE);
                response.setMessage(DeliveryResponse.MESSAGE_Delivery_NO_MESAGE);
            }
        } else if (WaybillUtil.isPackageCode(tSendM.getBoxCode())) {
            response.setMessage(StringHelper.getStringValue(1));
            Sorting sorting = new Sorting();
            sorting.setBoxCode(tSendM.getBoxCode());
            sorting.setCreateSiteCode(tSendM.getCreateSiteCode());
            sorting.setType(tSendM.getSendType());
            List<Sorting> tSorting = this.tSortingService.findByBoxCode(sorting);
            if (tSorting != null && !tSorting.isEmpty()) {
                Sorting nSorting = getLastSortingDate(tSorting);
                if (!nSorting.getReceiveSiteCode().equals(tSendM.getReceiveSiteCode()) && !isTransferSend) {
                    response.setCode(DeliveryResponse.CODE_Delivery_TRANSIT);
                    response.setMessage(DeliveryResponse.MESSAGE_Delivery_TRANSIT);
                }
            }
        }
        return response;
    }
    /**
     * 老发货-快运发货校验箱子是否已经发货
     * @param tSendM
     * @return
     */
    private DeliveryResponse deliveryCheckHasSend(SendM tSendM){
        DeliveryResponse response = new DeliveryResponse(JdResponse.CODE_OK, JdResponse.MESSAGE_OK);
        List<SendM> tSendMList = new ArrayList<SendM>();
        // 验证是否发货箱子和包裹分开处理
        if (BusinessHelper.isBoxcode(tSendM.getBoxCode())) {
            if (boxService.checkBoxIsSent(tSendM.getBoxCode(), tSendM.getCreateSiteCode())) {
                response.setCode(DeliveryResponse.CODE_Delivery_IS_SEND);
                response.setMessage(DeliveryResponse.MESSAGE_Delivery_IS_SEND);
            }
        } else if (WaybillUtil.isPackageCode(tSendM.getBoxCode())) {
            // 再投标识
            tSendMList = this.sendMDao.findSendMByBoxCode(tSendM);
            if (tSendMList != null && !tSendMList.isEmpty()) {
                response.setCode(DeliveryResponse.CODE_Delivery_IS_SEND);
                response.setMessage(DeliveryResponse.MESSAGE_Delivery_IS_SEND);
            }
        }

        return response;
    }

    /**
     * 生成取消发货数据处理
     * updated by wangtingwei@jd.com
     * edit:将取消发货分为三类
     * 一类为按板号
     * 一类为按箱号
     * 一类为按包裹（包括按运单、包裹、取件单）
     * @param tSendM 发货相关数据
     */
    @JProfiler(jKey = "DMSWEB.DeliveryService.dellCancel", mState = {JProEnum.TP})
    @Override
    public ThreeDeliveryResponse dellCancelDeliveryMessage(SendM tSendM, boolean needSendMQ) {
        try {
            SendDetail tSendDetail = new SendDetail();
            tSendDetail.setBoxCode(tSendM.getBoxCode());
            tSendDetail.setCreateSiteCode(tSendM.getCreateSiteCode());
            // 按照运单取消处理
            if (WaybillUtil.isWaybillCode(tSendM.getBoxCode()) || WaybillUtil.isSurfaceCode(tSendM.getBoxCode()) || WaybillUtil.isPackageCode(tSendM.getBoxCode())) {
                SendDetail mSendDetail = new SendDetail();
                if (WaybillUtil.isWaybillCode(tSendM.getBoxCode())) {
                    mSendDetail.setWaybillCode(tSendM.getBoxCode());
                } else {
                    mSendDetail.setPackageBarcode(tSendM.getBoxCode());
                }
                mSendDetail.setCreateSiteCode(tSendM.getCreateSiteCode());
                mSendDetail.setReceiveSiteCode(tSendM.getReceiveSiteCode());
                mSendDetail.setIsCancel(OPERATE_TYPE_CANCEL_L);
                List<SendDetail> tlist = this.sendDatailDao.querySendDatailsBySelective(mSendDetail);
                if (tlist != null && !tlist.isEmpty()) {
                    ThreeDeliveryResponse responsePack = cancelUpdateDataByPack(tSendM, tlist);
                    if (responsePack.getCode().equals(200)) {
                        delDeliveryFromRedis(tSendM);      //取消发货成功，删除redis缓存的发货数据
                        sendMessage(tlist, tSendM, needSendMQ);
                        //同步取消半退明细
                        reversePartDetailService.cancelPartSend(tSendM);
                    }
					return responsePack;
				} else {
					return new ThreeDeliveryResponse(
							DeliveryResponse.CODE_Delivery_NO_MESAGE,
							DeliveryResponse.MESSAGE_Delivery_NO_PACKAGE, null);
				}
			} else if (BusinessHelper.isBoxcode(tSendM.getBoxCode())) {
				List<SendM> sendMList = this.sendMDao.findSendMByBoxCode2(tSendM);
                ThreeDeliveryResponse threeDeliveryResponse = cancelUpdateDataByBox(tSendM, tSendDetail, sendMList);
                if (threeDeliveryResponse.getCode().equals(200)) {
                    SendM dSendM = this.getLastSendDate(sendMList);
                    SendDetail queryDetail = new SendDetail();
                    queryDetail.setBoxCode(dSendM.getBoxCode());
                    queryDetail.setCreateSiteCode(dSendM.getCreateSiteCode());
                    queryDetail.setReceiveSiteCode(dSendM.getReceiveSiteCode());
                    List<SendDetail> sendDatails = sendDatailDao.querySendDatailsBySelective(queryDetail);
                    delDeliveryFromRedis(tSendM);     //取消发货成功，删除redis缓存的发货数据
                    //更新箱号状态缓存 added by hanjiaxing3 2018.10.20
                    boxService.updateBoxStatusRedis(tSendM.getBoxCode(), tSendM.getCreateSiteCode(), BoxStatusEnum.CANCELED_STATUS.getCode());
                    sendMessage(sendDatails, tSendM, needSendMQ);
                }
                return threeDeliveryResponse;
            } else if (BusinessUtil.isBoardCode(tSendM.getBoxCode())){
                tSendM.setBoardCode(tSendM.getBoxCode());
                //1.组板发货批次，板号校验（强校验）
                if(!checkSendM(tSendM)){
                    return new ThreeDeliveryResponse(
                            DeliveryResponse.CODE_SEND_SITE_NOTMATCH__ERROR,
                            DeliveryResponse.MESSAGE_SEND_SITE_NOTMATCH_ERROR,null);
                }
                //2.校板是否已经发车
                if(checkBoardIsDepartured(tSendM)){
                    return new ThreeDeliveryResponse(
                            DeliveryResponse.CODE_BOARD_SENDED_ERROR,
                            DeliveryResponse.MESSAGE_BOARD_SENDED_ERROR,null);
                }

                //3.校验是否有板号和批次号对应的发货数据
                List<String> sendMList = sendMDao.selectBoxCodeByBoardCodeAndSendCode(tSendM);
                if(sendMList == null || sendMList.size()<1){
                    //提示没有找到板号的发货明细
                    return new ThreeDeliveryResponse(
                            DeliveryResponse.CODE_NO_BOARDSEND_DETAIL_ERROR,
                            DeliveryResponse.MESSAGE_NO_BOARDSEND_DETAIL_ERROR, null);
                }
                //4.校验是否有同一板号的发货任务没有跑完
                String redisKey = REDIS_PREFIX_BOARD_DELIVERY + tSendM.getBoxCode();
                if(StringHelper.isNotEmpty(redisManager.get(redisKey))){
                    return new ThreeDeliveryResponse(
                            DeliveryResponse.CODE_BOARD_SEND_NOT_FINISH_ERROR,
                            DeliveryResponse.MESSAGE_BOARD_SEND_NOT_FINISH_ERROR,null);
                }
                //生产一个按板号取消发货的任务
                pushBoardSendTask(tSendM,Task.TASK_TYPE_BOARD_SEND_CANCEL);
                return new ThreeDeliveryResponse(JdResponse.CODE_OK, JdResponse.MESSAGE_OK, null);
            }
            // 改变箱子状态为分拣
        } catch (Exception e) {
            return new ThreeDeliveryResponse(
                    DeliveryResponse.CODE_Delivery_ERROR,
                    DeliveryResponse.MESSAGE_Delivery_ERROR, null);
        }
        return new ThreeDeliveryResponse(
                DeliveryResponse.CODE_Delivery_NO_MESAGE,
                DeliveryResponse.MESSAGE_Delivery_NO_REQUEST, null);
    }


    private void delDeliveryFromRedis(SendM sendM) {
        Long result = redisManager.del(
                CacheKeyConstants.REDIS_KEY_IS_DELIVERY
                        + sendM.getCreateSiteCode()
                        + sendM.getBoxCode());
        if (result <= 0) {
            logger.warn("remove sendms of key ["
                    + CacheKeyConstants.REDIS_KEY_IS_DELIVERY
                    + sendM.getCreateSiteCode() + sendM.getBoxCode()
                    + "] from redis fail");
        } else {
            logger.warn("remove sendms of key ["
                    + CacheKeyConstants.REDIS_KEY_IS_DELIVERY
                    + sendM.getCreateSiteCode() + sendM.getBoxCode()
                    + "] from redis success");
        }
    }

    /****
     * 发送全程跟踪和取消发货MQ消息
     *
     * @param sendDetails
     * @param tSendM
     */
    private void sendMessage(List<SendDetail> sendDetails, SendM tSendM, boolean needSendMQ) {
        try {
            if (sendDetails == null || sendDetails.isEmpty()) {
                return;
            }
            Set<String> coldChainWaybillSet = new HashSet<>();
            List<SendDetail> coldChainSendDetails = new ArrayList<>();
            //按照包裹
            for (SendDetail model : sendDetails) {
                if (StringHelper.isNotEmpty(model.getSendCode())) {
                    // 发送全程跟踪任务
                    send(model, tSendM);
                    if (needSendMQ) {
                        // 发送取消发货MQ
                        sendMQ(model, tSendM);
                        if (this.isColdChainSend(model, tSendM, coldChainWaybillSet)) {
                            coldChainSendDetails.add(model);
                        }
                    }
                }
            }
            this.sendColdChainSendMQ(coldChainSendDetails);
        } catch (Exception ex) {
            logger.error("取消发货 发全程跟踪sendMessage： " + ex);
        }
    }

    /**
     * 取消发货判断运单是否为冷链卡班发货
     *
     * @param sendD
     * @param tSendM
     * @param coldChainWaybillSet
     * @return
     */
    private boolean isColdChainSend(SendDetail sendD, SendM tSendM, Set<String> coldChainWaybillSet) {
        Integer bizSource = sendD.getBizSource() == null ? tSendM.getBizSource() : sendD.getBizSource();
        if (bizSource != null) {
            if (SendBizSourceEnum.getEnum(bizSource) == SendBizSourceEnum.COLD_CHAIN_SEND) {
                if (coldChainWaybillSet.add(sendD.getWaybillCode())) {
                    return true;
                }
            }
        } else {
            if (!coldChainWaybillSet.contains(sendD.getWaybillCode())) {
                List<ColdChainSend> coldChainSends = coldChainSendService.getByWaybillCode(sendD.getWaybillCode());
                if (coldChainSends != null && coldChainSends.size() > 0) {
                    if (coldChainWaybillSet.add(sendD.getWaybillCode())) {
                        if (StringUtils.isEmpty(sendD.getSendCode())) {
                            sendD.setSendCode(coldChainSends.get(0).getSendCode());
                        }
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * 冷链取消发货MQ消息
     *
     * @param coldChainSendDetails
     */
    private void sendColdChainSendMQ(List<SendDetail> coldChainSendDetails) {
        try {
            if (coldChainSendDetails.size() > 0) {
                List<Message> messageList = new ArrayList<>();
                for (SendDetail sendDetail : coldChainSendDetails) {
                    BaseStaffSiteOrgDto createSiteDto = baseMajorManager.getBaseSiteBySiteId(sendDetail.getCreateSiteCode());
                    BaseStaffSiteOrgDto receiveSiteDto = baseMajorManager.getBaseSiteBySiteId(sendDetail.getReceiveSiteCode());
                    if (createSiteDto != null && receiveSiteDto != null) {
                        ColdChainSend coldChainSend = coldChainSendService.getBySendCode(sendDetail.getWaybillCode(), sendDetail.getSendCode());
                        if (coldChainSend != null && com.jd.common.util.StringUtils.isNotEmpty(coldChainSend.getTransPlanCode())) {
                            ColdChainSendMessage messageBody = new ColdChainSendMessage();
                            messageBody.setWaybillCode(sendDetail.getWaybillCode());
                            messageBody.setSendCode(sendDetail.getSendCode());
                            // 取消发货
                            messageBody.setSendType(2);
                            messageBody.setTransPlanCode(coldChainSend.getTransPlanCode());
                            messageBody.setCreateSiteCode(createSiteDto.getDmsSiteCode());
                            messageBody.setReceiveSiteCode(receiveSiteDto.getDmsSiteCode());
                            messageBody.setOperateTime(sendDetail.getOperateTime().getTime());
                            messageBody.setOperateUserName(sendDetail.getCreateUser());
                            if (sendDetail.getCreateUserCode() != null) {
                                BaseStaffSiteOrgDto dto = baseMajorManager.getBaseStaffByStaffId(sendDetail.getCreateUserCode());
                                if (dto != null) {
                                    messageBody.setOperateUserErp(dto.getErp());
                                }
                            }
                            messageList.add(new Message(dmsColdChainSendWaybill.getTopic(), JSON.toJSONString(messageBody), sendDetail.getWaybillCode()));
                        }
                    }
                }
                dmsColdChainSendWaybill.batchSend(messageList);
            }
        } catch (JMQException e) {
            logger.error("[PDA操作取消发货]冷链取消发货 - 推送TMS运输MQ消息时发生异常", e);
        }
    }

    /**
     * PDA操作取消发货MQ消息发送
     */
    private void sendMQ(SendDetail sendDetail, SendM sendM) {
        try {
            DeliveryCancelSendMQBody body = new DeliveryCancelSendMQBody();
            body.setPackageBarcode(sendDetail.getPackageBarcode());
            body.setWaybillCode(sendDetail.getWaybillCode());
            body.setSendCode(sendDetail.getSendCode());
            body.setOperateTime(sendM.getUpdateTime());
            Integer userCode = sendM.getUpdateUserCode();
            if (userCode != null) {
                BaseStaffSiteOrgDto dto = baseMajorManager.getBaseStaffByStaffId(userCode);
                if (dto != null) {
                    body.setOperatorErp(dto.getErp());
                }
            }
            deliveryCancelSendMQ.send(sendDetail.getPackageBarcode(), JsonHelper.toJson(body));
        } catch (Exception e) {
            logger.error("[PDA操作取消发货]发送MQ消息时发生异常", e);
        }
    }

    /******
     * 按照包裹号取消发货 发送全程跟踪
     *
     * @param sendDetail
     * @param tSendM
     */
    private void send(SendDetail sendDetail, SendM tSendM) {
        Task tTask = new Task();
        tTask.setKeyword1(tSendM.getThirdWaybillCode());
        tTask.setCreateSiteCode(tSendM.getReceiveSiteCode());
        tTask.setCreateTime(new Date());
        tTask.setKeyword2(String.valueOf(3800));
        tTask.setReceiveSiteCode(0);
        tTask.setType(Task.TASK_TYPE_WAYBILL_TRACK);
        tTask.setTableName(Task.getTableName(Task.TASK_TYPE_WAYBILL_TRACK));
        tTask.setSequenceName(Task.getSequenceName(Task.TABLE_NAME_POP));
        String ownSign = BusinessHelper.getOwnSign();
        tTask.setOwnSign(ownSign);

        WaybillStatus status = new WaybillStatus();
        status.setOperateType(3800);
        status.setWaybillCode(sendDetail.getWaybillCode());
        status.setPackageCode(sendDetail.getPackageBarcode());
        status.setOperateTime(tSendM.getUpdateTime());
        status.setOperator(tSendM.getUpdaterUser());
        status.setOperatorId(tSendM.getUpdateUserCode());
        status.setRemark("取消发货，批次号为：" +sendDetail.getSendCode());
        status.setCreateSiteCode(tSendM.getCreateSiteCode());

        BaseStaffSiteOrgDto dto = baseMajorManager.getBaseSiteBySiteId(tSendM.getCreateSiteCode());

        status.setCreateSiteName(dto.getSiteName());
        tTask.setBody(JsonHelper.toJson(status));
        logger.info("取消发货 发全程跟踪work6666-3800： " + JsonHelper.toJson(status));
        taskService.add(tTask);
    }


    //处理箱子
    private ThreeDeliveryResponse cancelUpdateDataByBox(SendM tSendM,
                                                        SendDetail tSendDatail, List<SendM> sendMList) {
        Collections.sort(sendMList);
        if (sendMList != null && !sendMList.isEmpty()) {
            SendM dSendM = this.getLastSendDate(sendMList);
            dSendM.setUpdaterUser(tSendM.getUpdaterUser());
            dSendM.setUpdateUserCode(tSendM.getUpdateUserCode());
            dSendM.setUpdateTime(new Date());
            String caruser = dSendM.getSendUser();
            // 是否发车
            if (caruser != null && !"".equals(caruser)) {
                return new ThreeDeliveryResponse(
                        DeliveryResponse.CODE_Delivery_NO_DEPART,
                        DeliveryResponse.MESSAGE_Delivery_NO_DEPART, null);
            }

            tSendDatail.setReceiveSiteCode(dSendM.getReceiveSiteCode());
            // 是否发货状态更新
            if (sendDatailDao.querySendDatailBySendStatus(tSendDatail) != null) {
                return new ThreeDeliveryResponse(
                        DeliveryResponse.CODE_Delivery_NO_MESAGE,
                        DeliveryResponse.MESSAGE_Delivery_IS_MESAGE, null);
            }
            return this.cancelDeliveryStatusByBox(dSendM, tSendDatail);
        } else {
            return new ThreeDeliveryResponse(
                    DeliveryResponse.CODE_Delivery_NO_MESAGE,
                    DeliveryResponse.MESSAGE_Delivery_NO_MESAGE, null);
        }
    }

    //处理运单
    private ThreeDeliveryResponse cancelUpdateDataByPack(SendM tSendM,
                                                         List<SendDetail> tList) {
        Collections.sort(tList);
		for (SendDetail dSendDetail : tList) {
			tSendM.setBoxCode(dSendDetail.getBoxCode());
			List<SendM> sendMList = this.sendMDao.findSendMByBoxCode(tSendM);
			// 发车验证
			if (sendMList != null && !sendMList.isEmpty()) {
				SendM dSendM = this.getLastSendDate(sendMList);
				dSendM.setUpdaterUser(tSendM.getUpdaterUser());
				dSendM.setUpdateUserCode(tSendM.getUpdateUserCode());
				dSendM.setUpdateTime(new Date());
				String caruser = dSendM.getSendUser();
				if (caruser != null && !"".equals(caruser)) {
					return new ThreeDeliveryResponse(
							DeliveryResponse.CODE_Delivery_NO_DEPART,
							DeliveryResponse.MESSAGE_Delivery_NO_DEPART, null);
				}
				break;
			} else {
				return new ThreeDeliveryResponse(
						DeliveryResponse.CODE_Delivery_NO_MESAGE,
						DeliveryResponse.MESSAGE_Delivery_NO_MESAGE, null);
			}
		}
		for (SendDetail dSendDetail : tList) {
			cancelDeliveryStatusByPack(tSendM, dSendDetail);
			Sorting sorting = new Sorting.Builder(
					dSendDetail.getCreateSiteCode())
					.boxCode(dSendDetail.getBoxCode())
					.waybillCode(dSendDetail.getWaybillCode())
					.packageCode(dSendDetail.getPackageBarcode())
					.type(dSendDetail.getSendType())
					.receiveSiteCode(dSendDetail.getReceiveSiteCode())
					.updateUser(dSendDetail.getCreateUser())
					.updateUserCode(dSendDetail.getCreateUserCode())
					.updateTime(new Date()).build();
			tSortingService.canCancel2(sorting);
		}
		return new ThreeDeliveryResponse(JdResponse.CODE_OK,
				JdResponse.MESSAGE_OK, null);
	}

    //箱子更新取消发货状态
    public ThreeDeliveryResponse cancelDeliveryStatusByBox(SendM tSendM, SendDetail tSendDatail) {
        SendDetail mSendDetail = new SendDetail();
        mSendDetail.setBoxCode(tSendM.getBoxCode());
        mSendDetail.setCreateSiteCode(tSendM.getCreateSiteCode());
        mSendDetail.setReceiveSiteCode(tSendM.getReceiveSiteCode());
        mSendDetail.setIsCancel(OPERATE_TYPE_CANCEL_Y);
        List<SendDetail> tlist = this.sendDatailDao.querySendDatailsBySelective(mSendDetail);
        Collections.sort(tlist);
        //更新m表和d表
        reverseDeliveryService.updateIsCancelByBox(tSendM);
        //写入运单回传状态
        reverseDeliveryService.updateIsCancelToWaybillByBox(tSendM, tlist);
        return new ThreeDeliveryResponse(JdResponse.CODE_OK, JdResponse.MESSAGE_OK, null);
    }

    public ThreeDeliveryResponse cancelDeliveryStatusByPack(SendM tSendM,
                                                            SendDetail tSendDatail) {
        // 更新m表和d表
        reverseDeliveryService.updateIsCancelByPackageCode(tSendM, tSendDatail);
        // 写入运单回传状态
        reverseDeliveryService.updateIsCancelToWaybillByPackageCode(tSendM,
                tSendDatail);
        return new ThreeDeliveryResponse(JdResponse.CODE_OK,
                JdResponse.MESSAGE_OK, null);
    }

    /**
     * 取消发货明主表数据处理
     *
     * @param tSendM 发货相关数据
     */
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public boolean cancelSendM(SendM tSendM) {
        return this.sendMDao.cancelSendM(tSendM);
    }

    /**
     * 取消发货明细表数据处理
     *
     * @param tSendDetail 发货相关数据
     */
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public boolean cancelSendDatailByPackage(SendDetail tSendDetail) {
        if (tSendDetail != null) {
            try {
                this.sendDatailDao.cancelSendDatail(tSendDetail);
            } catch (Exception e) {
                this.logger.error("取消发货cancelSendDatailByPackage,参数"
                        + JsonHelper.toJson(tSendDetail), e);
            }
            return true;
        } else {
            return false;
        }
    }

    /**
     * 取消发货明细表数据处理
     *
     * @param tlist 发货相关数据
     */
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public boolean cancelSendDatailByBox(SendM sendM) {
        SendDetail tSendDatail = new SendDetail();
        tSendDatail.setBoxCode(sendM.getBoxCode());
        tSendDatail.setCreateSiteCode(sendM.getCreateSiteCode());
        tSendDatail.setReceiveSiteCode(sendM.getReceiveSiteCode());

        try {
            sendDatailDao.cancelSendDatail(tSendDatail);
        } catch (Exception e) {
            this.logger.error("取消发货cancelSendDatailByBox,参数"
                    + JsonHelper.toJson(tSendDatail), e);
        }
        return true;
    }

    /**
     * 2012-10-12
     * 拆分数据数据 每组999
     *
     * @param transresult
     * @return
     */
    @SuppressWarnings("unchecked")
    private List<SendDetail>[] splitList(List<SendDetail> transresult) {
        List<List<SendDetail>> splitList = new ArrayList<List<SendDetail>>();
        for (int i = 0; i < transresult.size(); i += BATCH_NUM) {
            int size = i + BATCH_NUM > transresult.size() ? transresult.size() : i + BATCH_NUM;
            List<SendDetail> tmp = (List<SendDetail>) transresult.subList(i, size);
            splitList.add(tmp);
        }
        return splitList.toArray(new List[0]);
    }

    /**
     * 2012-10-12
     * 拆分数据数据 每组99
     *
     * @param transresult
     * @return
     */
    @SuppressWarnings("unchecked")
    private List<SendM>[] splitSendMList(List<SendM> transresult) {
        List<List<SendM>> splitList = new ArrayList<List<SendM>>();
        for (int i = 0; i < transresult.size(); i += BATCH_NUM_M) {
            int size = i + BATCH_NUM_M > transresult.size() ? transresult.size() : i + BATCH_NUM_M;
            List<SendM> tmp = (List<SendM>) transresult.subList(i, size);
            splitList.add(tmp);
        }
        return splitList.toArray(new List[0]);
    }

    @JProfiler(jKey = "DMSWEB.DeliveryService.updateWaybillStatus", mState = {JProEnum.TP})
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    @Override
    public boolean updateWaybillStatus(List<SendDetail> sendDetails) {
        if (sendDetails != null && !sendDetails.isEmpty()) {
            List<SendDetail> sendDetailList = new ArrayList<SendDetail>();
            List<SendDetail> cancelSendList = new ArrayList<SendDetail>();
            List<WaybillStatus> waybillStatusList = new ArrayList<WaybillStatus>(sendDetails.size());
            List<Integer> sendTypeList = new ArrayList<Integer>(sendDetails.size());
            List<Message> sendDetailMQList = new ArrayList<Message>(sendDetails.size());

            // 增加获取订单类型判断是否是LBP订单e
            for (SendDetail tSendDetail : sendDetails) {
                tSendDetail.setStatus(1);
                if (tSendDetail.getIsCancel().equals(1)) {
                    cancelSendList.add(tSendDetail);
                } else {
                    BaseStaffSiteOrgDto createSiteDto = this.getBaseStaffSiteDto(tSendDetail.getCreateSiteCode());
                    BaseStaffSiteOrgDto receiveSiteDto = this.getBaseStaffSiteDto(tSendDetail.getReceiveSiteCode());
                    if (receiveSiteDto != null && receiveSiteDto.getSiteType() != null && createSiteDto != null && createSiteDto.getSiteType() != null) {
                        if (!checkParameter(tSendDetail)) {
                            this.logger.info("发货数据调用基础资料接口参数信息不全：包裹号为" + tSendDetail.getPackageBarcode());
                        } else {
                            WaybillStatus tWaybillStatus = this.buildWaybillStatus(tSendDetail, createSiteDto, receiveSiteDto);
                            if (tSendDetail.getYn().equals(1) && tSendDetail.getIsCancel().equals(0)) {
                                // 添加操作日志
                                addOperationLog(tSendDetail);
                                // 判断是正向发货还是逆向发货
                                if (tSendDetail.getSendType() != null && Constants.BUSSINESS_TYPE_REVERSE == tSendDetail.getSendType().intValue()) {
                                    tWaybillStatus.setOperateType(OPERATE_TYPE_REVERSE_SEND);
                                } else {
                                    tWaybillStatus.setOperateType(OPERATE_TYPE_FORWARD_SEND);
                                }
                                waybillStatusList.add(tWaybillStatus);
                                sendTypeList.add(tSendDetail.getSendType());

                                //发送发货明细mq
                                Message sendMessage = parseSendDetailToMessage(tSendDetail, dmsWorkSendDetailMQ.getTopic(), Constants.SEND_DETAIL_SOUCRE_NORMAL);
                                sendDetailMQList.add(sendMessage);
                                this.logger.info("发送MQ[" + sendMessage.getTopic() + "],业务ID[" + sendMessage.getBusinessId() + "],消息主题: " + sendMessage.getText());
                            } else if (tSendDetail.getYn().equals(0) && tSendDetail.getIsCancel().equals(2)) {
                                tSendDetail.setSendCode(null);
                                // 判断是正向分拣还是逆向分拣
                                if (tSendDetail.getSendType() != null && Constants.BUSSINESS_TYPE_REVERSE == tSendDetail.getSendType().intValue()) {
                                    tWaybillStatus.setOperateType(OPERATE_TYPE_REVERSE_SORTING);
                                } else {
                                    tWaybillStatus.setOperateType(OPERATE_TYPE_FORWARD_SORTING);
                                }
                                waybillStatusList.add(tWaybillStatus);
                                sendTypeList.add(tSendDetail.getSendType());
                            }
                        }
                    }
                    sendDetailList.add(tSendDetail);
                }
            }

            // 批量发送发货明细MQ消息
            this.dmsWorkSendDetailMQ.batchSendOnFailPersistent(sendDetailMQList);

            // 批量添加回传全程跟踪状态任务
            this.addWaybillStatusTask(waybillStatusList, sendTypeList);

            if (!sendDetailList.isEmpty()) {
                this.updateWaybillStatusByPackage(sendDetailList);
            }

            if (!cancelSendList.isEmpty()) {
                this.updateSendStatusByPackage(cancelSendList);
            }
        }
        return true;
    }

    private WaybillStatus buildWaybillStatus(SendDetail tSendDetail, BaseStaffSiteOrgDto cbDto, BaseStaffSiteOrgDto rbDto) {
        WaybillStatus tWaybillStatus = new WaybillStatus();
        tWaybillStatus.setReceiveSiteCode(tSendDetail.getReceiveSiteCode());
        tWaybillStatus.setReceiveSiteName(rbDto.getSiteName());
        tWaybillStatus.setReceiveSiteType(rbDto.getSiteType());
        tWaybillStatus.setOperatorId(tSendDetail.getCreateUserCode());
        tWaybillStatus.setOperator(tSendDetail.getCreateUser());
        tWaybillStatus.setOperateTime(tSendDetail.getOperateTime());
        tWaybillStatus.setOrgId(rbDto.getOrgId());
        tWaybillStatus.setOrgName(rbDto.getOrgName());
        tWaybillStatus.setPackageCode(tSendDetail.getPackageBarcode());
        tWaybillStatus.setCreateSiteCode(tSendDetail.getCreateSiteCode());
        tWaybillStatus.setCreateSiteName(cbDto.getSiteName());
        tWaybillStatus.setCreateSiteType(cbDto.getSiteType());
        tWaybillStatus.setOperateType(OPERATE_TYPE_REVERSE_SEND);
        tWaybillStatus.setWaybillCode(tSendDetail.getWaybillCode());
        tWaybillStatus.setSendCode(tSendDetail.getSendCode());
        tWaybillStatus.setBoxCode(tSendDetail.getBoxCode());
        return tWaybillStatus;
    }

    private boolean checkParameter(SendDetail tSendDetail) {
        if (tSendDetail.getCreateUserCode() == null) {
            return Boolean.FALSE;
        } else if (StringHelper.isEmpty(tSendDetail.getCreateUser())) {
            return Boolean.FALSE;
        } else if (tSendDetail.getOperateTime() == null) {
            return Boolean.FALSE;
        } else {
            return Boolean.TRUE;
        }
    }

    private BaseStaffSiteOrgDto getBaseStaffSiteDto(Integer siteCode){
        BaseStaffSiteOrgDto baseSiteDto = null;
        try {
            baseSiteDto = this.baseMajorManager.getBaseSiteBySiteId(siteCode);
        } catch (Exception e) {
            this.logger.error("发货全程跟踪调用站点信息异常", e);
        }

        if (baseSiteDto == null) {
            baseSiteDto = baseMajorManager.queryDmsBaseSiteByCodeDmsver(String.valueOf(siteCode));
        }
        return baseSiteDto;
    }

    /**
     * 校验板号是否已经发车
     * @param domain
     * @return
     */
    private boolean checkBoardIsDepartured(SendM domain){
        SendM sendM = sendMDao.findSendMByBoardCode(domain);
        if(sendM != null && StringHelper.isNotEmpty(sendM.getSendUser())){
            return true;
        }
        return false;
    }
    /**
     * 校验sendm中的批次号
     * @param sendm
     * @return
     */
    private boolean checkSendM(SendM sendm) {
        String sendCode = sendm.getSendCode();
        Integer createSiteCode = SerialRuleUtil.getCreateSiteCodeFromSendCode(sendCode);
        if(createSiteCode == null){
            return false;
        }
        if(createSiteCode.equals(sendm.getCreateSiteCode())){
            return true;
        }
        return false;
    }

    private boolean addWaybillStatusTask(List<WaybillStatus> waybillStatusList, List<Integer> sendTypeList) {
        if (waybillStatusList.size() > 0) {
            List<Task> tasks = new ArrayList<Task>(waybillStatusList.size());
            int index = 0;
            for (WaybillStatus tWaybillStatus : waybillStatusList) {
                if (tWaybillStatus == null) {
                    continue;
                }
                Task tTask = new Task();
                tTask.setBoxCode(tWaybillStatus.getBoxCode());
                tTask.setBody(JsonHelper.toJson(tWaybillStatus));
                tTask.setCreateSiteCode(tWaybillStatus.getCreateSiteCode());
                tTask.setKeyword2(tWaybillStatus.getPackageCode());
                tTask.setReceiveSiteCode(tWaybillStatus.getReceiveSiteCode());
                tTask.setType(Task.TASK_TYPE_SEND_DELIVERY);
                tTask.setTableName(Task.TABLE_NAME_WAYBILL);
                tTask.setSequenceName(Task.TABLE_NAME_WAYBILL_SEQ);
                tTask.setOwnSign(BusinessHelper.getOwnSign());
                //回传运单状态
                tTask.setKeyword1(tWaybillStatus.getWaybillCode());
                tTask.setFingerprint(Md5Helper.encode(tWaybillStatus.getCreateSiteCode() + "_"
                        + tWaybillStatus.getReceiveSiteCode() + "_" + sendTypeList.get(index) + "-"
                        + tWaybillStatus.getOperateType() + "_" + tWaybillStatus.getPackageCode() + "_" + tWaybillStatus.getOperateTime()));
                tasks.add(tTask);
                index++;
            }
            tTaskService.addBatch(tasks);
        }
        return true;
    }

    private void updateWaybillStatusByPackage(List<SendDetail> newSendDList) {
        Collections.sort(newSendDList);
        for (SendDetail tSendDetail : newSendDList) {
            sendDatailDao.updatewaybillCodeStatus(tSendDetail);
            if (WaybillUtil.isReverseSpareCode(tSendDetail.getPackageBarcode())) {
                ReverseSpare tReverseSpare = new ReverseSpare();
                tReverseSpare.setSpareCode(tSendDetail.getPackageBarcode());
                tReverseSpare.setSendCode(tSendDetail.getSendCode());
                reverseSpareDao.update(ReverseSpareDao.namespace, tReverseSpare);
            }
        }
    }

    private void updateSendStatusByPackage(List<SendDetail> newSendDList) {
        Collections.sort(newSendDList);
        for (SendDetail tSendDetail : newSendDList) {
            sendDatailDao.updateSendStatusByPackage(tSendDetail);
        }
    }

    /**
     * 回传发货状态至运单【两种方式，一种按批次回传，另一种按箱号回传】
     *
     * @param task
     * @return
     */
    @JProfiler(jKey = "DMSWORKER.DeliveryService.updatewaybillCodeMessage", mState = {JProEnum.TP})
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    @Override
    public boolean updatewaybillCodeMessage(Task task) {
        logger.info("发货状态开始处理" + JsonHelper.toJson(task) + "是否JSON字符串" + JsonHelper.isJsonString(task.getBody()));
        if (task == null || task.getBoxCode() == null || task.getCreateSiteCode() == null) {
            return true;
        }
        List<SendM> tSendM = null;
        if (JsonHelper.isJsonString(task.getBody())) {
            SendTaskBody body = JsonHelper.fromJson(task.getBody(), SendTaskBody.class);
            logger.info("发货状态BODY" + JsonHelper.toJson(body));
            // 按照批次号
            if (Integer.valueOf(1).equals(body.getHandleCategory())) {
                tSendM = this.sendMDao.selectBySiteAndSendCodeBYtime(body.getCreateSiteCode(), body.getSendCode());
            } else { // 按照箱号
                tSendM = new ArrayList<SendM>(1);
                tSendM.add(body);
                logger.info("BODY明细" + JsonHelper.toJson(body));
            }
        } else {
            tSendM = this.sendMDao.selectBySiteAndSendCodeBYtime(task.getCreateSiteCode(), task.getBoxCode());
        }
        if (logger.isInfoEnabled()) {
            logger.info("SEND_M明细" + JsonHelper.toJson(tSendM));
        }
        SendDetail tSendDetail = new SendDetail();
        List<SendDetail> sendDetailListTemp;
        List<SendDetail> sendDetailList = new ArrayList<SendDetail>();
        for (SendM newSendM : tSendM) {
            tSendDetail.setBoxCode(newSendM.getBoxCode());
            tSendDetail.setCreateSiteCode(newSendM.getCreateSiteCode());
            tSendDetail.setReceiveSiteCode(newSendM.getReceiveSiteCode());
            tSendDetail.setIsCancel(OPERATE_TYPE_CANCEL_L);
            sendDetailListTemp = this.sendDatailDao.querySendDatailsBySelective(tSendDetail);

            for (SendDetail dSendDetail : sendDetailListTemp) {
                //只处理未发货的数据, 如果已发货则跳过
                if (dSendDetail.getStatus().equals(Constants.CONTAINER_RELATION_SEND_STATUS_YES)) {
                    continue;
                }
                dSendDetail.setSendCode(newSendM.getSendCode());
                dSendDetail.setOperateTime(newSendM.getOperateTime());
                dSendDetail.setCreateUser(newSendM.getCreateUser());
                dSendDetail.setCreateUserCode(newSendM.getCreateUserCode());
                dSendDetail.setBoardCode(newSendM.getBoardCode());
                dSendDetail.setBizSource(newSendM.getBizSource());
                sendDetailList.add(dSendDetail);
            }
        }
        if (logger.isInfoEnabled()) {
            logger.info("SEND_D明细" + JsonHelper.toJson(sendDetailList));
        }
        updateWaybillStatus(sendDetailList);
        return true;
    }

    private Message parseSendDetailToMessage(SendDetail sendDetail, String topic, String source) {
        Message message = new Message();
        SendDetail newSendDetail = new SendDetail();
        if (sendDetail != null) {
            // MQ包含的信息:包裹号,发货站点,发货时间,组板发货时包含板号
            newSendDetail.setPackageBarcode(sendDetail.getPackageBarcode());
            newSendDetail.setCreateSiteCode(sendDetail.getCreateSiteCode());
            newSendDetail.setReceiveSiteCode(sendDetail.getReceiveSiteCode());
            newSendDetail.setOperateTime(sendDetail.getOperateTime());
            newSendDetail.setSendCode(sendDetail.getSendCode());
            newSendDetail.setCreateUserCode(sendDetail.getCreateUserCode());
            newSendDetail.setCreateUser(sendDetail.getCreateUser());
            newSendDetail.setSource(source);
            newSendDetail.setBoxCode(sendDetail.getBoxCode());
            newSendDetail.setBoardCode(sendDetail.getBoardCode());
            newSendDetail.setBizSource(sendDetail.getBizSource());
            message.setTopic(topic);
            message.setText(JSON.toJSONString(newSendDetail));
            message.setBusinessId(sendDetail.getPackageBarcode());
        }
        return message;
    }

    private Message parseSendDetailToMessageOfAR(SendDetail sendDatail,String topic,String arSendRegisterId) {
        Message message = new Message();
        ArSendDetailMQBody arSendDetailMQBody = new ArSendDetailMQBody();
        if (sendDatail != null) {
            // MQ包含的信息:包裹号,发货站点,发货时间
            arSendDetailMQBody.setPackageBarcode(sendDatail.getPackageBarcode());
            arSendDetailMQBody.setCreateSiteCode(sendDatail.getCreateSiteCode());
            arSendDetailMQBody.setReceiveSiteCode(sendDatail.getReceiveSiteCode());
            arSendDetailMQBody.setOperateTime(sendDatail.getOperateTime());
            arSendDetailMQBody.setSendCode(sendDatail.getSendCode());
            arSendDetailMQBody.setCreateUserCode(sendDatail.getCreateUserCode());
            arSendDetailMQBody.setCreateUser(sendDatail.getCreateUser());
            arSendDetailMQBody.setBoxCode(sendDatail.getBoxCode());
            arSendDetailMQBody.setArSendRegisterId(arSendRegisterId);
            message.setTopic(topic);
            message.setText(JSON.toJSONString(arSendDetailMQBody));
            message.setBusinessId(sendDatail.getPackageBarcode());
        }
        return message;
    }
    @Override
    public boolean findSendwaybillMessage(Task task) throws Exception {
        if (task == null || task.getBoxCode() == null || task.getCreateSiteCode() == null || task.getKeyword2() == null)
            return true;

        List<SendM> tSendM = null;
        if (JsonHelper.isJsonString(task.getBody())) {
            SendTaskBody body = JsonHelper.fromJson(task.getBody(), SendTaskBody.class);
            if (body.getHandleCategory().equals(1)) {
                tSendM = this.sendMDao.selectBySiteAndSendCodeBYtime(
                        body.getCreateSiteCode(), body.getSendCode());
            } else {
                tSendM = new ArrayList<SendM>(1);
                tSendM.add(body);
            }
        } else {
            tSendM = this.sendMDao.selectBySiteAndSendCodeBYtime(
                    task.getCreateSiteCode(), task.getBoxCode());
        }
        if (tSendM != null && !tSendM.isEmpty()) {
            for (SendM newSendM : tSendM) {
                if (BusinessHelper.isBoxcode(newSendM.getBoxCode()) && newSendM.getTurnoverBoxCode() != null) {
                    TurnoverBoxInfo tTurnoverBoxInfo = new TurnoverBoxInfo();
                    tTurnoverBoxInfo.setBoxCode(newSendM.getBoxCode());
                    tTurnoverBoxInfo.setDestSiteId(newSendM.getReceiveSiteCode());
                    tTurnoverBoxInfo.setMessageType("SORTING_DELIVERY_QUEUE");
                    tTurnoverBoxInfo.setOperateTime(DateHelper.formatDateTime(newSendM.getCreateTime()));
                    tTurnoverBoxInfo.setOperatorId(newSendM.getCreateUserCode());
                    tTurnoverBoxInfo.setOperatorName(newSendM.getCreateUser());
                    tTurnoverBoxInfo.setOperatorSortingId(newSendM.getCreateSiteCode());
                    tTurnoverBoxInfo.setSendCode(newSendM.getSendCode());
                    tTurnoverBoxInfo.setTurnoverBoxCode(newSendM.getTurnoverBoxCode());
                    String body = JsonHelper.toJson(tTurnoverBoxInfo);
                    //messageClient.sendMessage("turnover_box", body, newSendM.getSendCode());
                    turnoverBoxMQ.send(newSendM.getSendCode(), body);
                }
            }
        }
        return true;
    }

    /**
     * 比较时间大小
     *
     * @param sortinhList
     */
    public Sorting getLastSortingDate(List<Sorting> sortinhList) {
        Sorting tSorting = null;
        if (sortinhList != null && !sortinhList.isEmpty()) {
            for (Sorting dSorting : sortinhList) {
                if (tSorting == null) {
                    tSorting = dSorting;
                } else if (tSorting.getCreateTime().getTime() < dSorting.getCreateTime().getTime()) {
                    tSorting = dSorting;
                }
            }
        }
        return tSorting;
    }

    /**
     * 比较时间大小
     *
     * @param sendMList
     */
    public SendM getLastSendDate(List<SendM> sendMList) {
        SendM tSendM = null;
        if (sendMList != null && !sendMList.isEmpty()) {
            for (SendM dSendM : sendMList) {
                if (tSendM == null) {
                    tSendM = dSendM;
                } else if (tSendM.getCreateTime().getTime() < dSendM.getCreateTime().getTime()) {
                    tSendM = dSendM;
                }
            }
        }
        return tSendM;
    }

    @Override
    public boolean checkSend(SendDetail tSendDatail) {
        List<SendDetail> sendDetails = this.sendDatailDao.querySendDatailsBySelective(tSendDatail);//FIXME:无create_site_code有跨节点风险
        if (sendDetails != null && !sendDetails.isEmpty()) {
            Set<SendDetail> sendDatailset = new CollectionHelper<SendDetail>().toSet(sendDetails);
            sendDetails = new CollectionHelper<SendDetail>().toList(sendDatailset);
            for (SendDetail dSendDatail : sendDetails) {
                SendM tSendM = new SendM();
                tSendM.setBoxCode(dSendDatail.getBoxCode());
                tSendM.setReceiveSiteCode(dSendDatail.getReceiveSiteCode());
                tSendM.setCreateSiteCode(dSendDatail.getCreateSiteCode());
            }
        }
        return true;
    }

    @Override
    public SendDetail getSendSiteID(String packbarCode, Integer sitecode) {
        if (packbarCode == null || packbarCode.isEmpty() || sitecode == null) {
            return null;
        }
        SendDetail sendDetail = new SendDetail();
        sendDetail.setPackageBarcode(packbarCode);
        sendDetail.setCreateSiteCode(sitecode);
        sendDetail.setReceiveSiteCode(sitecode);
        return getLastSendDetailDate(sendDatailDao.getSendSiteID(sendDetail));
    }

    /**
     * 比较时间大小
     *
     * @param SendDatailList
     */
    public SendDetail getLastSendDetailDate(List<SendDetail> SendDatailList) {
        SendDetail tSendDatail = null;
        if (SendDatailList != null && !SendDatailList.isEmpty()) {
            for (SendDetail dSendDatail : SendDatailList) {
                if (tSendDatail == null) {
                    tSendDatail = dSendDatail;
                } else if (tSendDatail.getOperateTime().getTime() < dSendDatail.getOperateTime().getTime()) {
                    tSendDatail = dSendDatail;
                }
            }
        }
        return tSendDatail;
    }

    @Override
    public boolean sendMTooldtms(List<SendM> tSendMList) {
        SendM tSendM = tSendMList.get(0);
        Set<ShouHuoInfo> shouHuoes = new HashSet<ShouHuoInfo>();
        ShouHuoInfo tShouHuoInfo = new ShouHuoInfo();
        tShouHuoInfo.setBatchId(tSendM.getSendCode());
        tShouHuoInfo.setUuId(tSendM.getSendCode());
        tShouHuoInfo.setCreateTime(tSendM.getCreateTime());
        if (tSendM.getCarCode() != null) {
            tShouHuoInfo.setCarNo(tSendM.getCarCode());
        } else {
            tShouHuoInfo.setCarNo("");
        }
        tShouHuoInfo.setCallCode("360BUY.BJ.A");
        this.allBoxInfo(tSendMList, tShouHuoInfo);
        if (tShouHuoInfo.getBoxInfoList() == null
                || tShouHuoInfo.getBoxInfoList().isEmpty()) {
            this.logger.info("发货数据--------BoxInfoList参数不全");
        } else {
            shouHuoes.add(tShouHuoInfo);
            String requestXmls = XmlHelper
                    .objectToXml(tShouHuoInfo, new ShouHuoConverter());

            if (StringHelper.isNotEmpty(requestXmls)) {
                Result result = this.dmsToTmsWebService.shouHuoService(requestXmls);
                this.logger.info(result.getResultCode());
                this.logger.info(result.getResultMessage());
                if (result.getResultCode() == 1) {
                    return true;
                }
            }
        }
        return false;
    }

    private void allBoxInfo(List<SendM> tSendMList, ShouHuoInfo tShouHuoInfo) {
        List<BoxInfo> tBoxInfoList = new ArrayList<BoxInfo>();
        if (tSendMList != null && !tSendMList.isEmpty()) {
            for (SendM dSendM : tSendMList) {
                BoxInfo tBoxInfo = new BoxInfo();
                tBoxInfo.setBatchId(dSendM.getSendCode());
                tBoxInfo.setBoxId(dSendM.getBoxCode());
                BaseStaffSiteOrgDto bDto = this.baseMajorManager.getBaseSiteBySiteId(dSendM
                        .getReceiveSiteCode());
                if (bDto != null) {
                    tBoxInfo.setSendType(String.valueOf(bDto.getSiteType()));
                    tBoxInfo.setSendName(bDto.getSiteName());
                } else {
                    tBoxInfo.setSendType("");
                    tBoxInfo.setSendName("");
                }
                if (dSendM.getSendUserCode() != null) {
                    tBoxInfo.setCarrierId(String.valueOf(dSendM.getSendUserCode()));
                } else {
                    tBoxInfo.setCarrierId("");
                }
                if (dSendM.getSendUser() != null) {
                    tBoxInfo.setCarrierName(dSendM.getSendUser());
                } else {
                    tBoxInfo.setCarrierName("");
                }

                tBoxInfo.setOperatorId(String.valueOf(dSendM.getCreateUserCode()));
                tBoxInfo.setOperatorName(dSendM.getCreateUser());
                tBoxInfo.setSendId(String.valueOf(dSendM.getReceiveSiteCode()));
                this.allOrderInfo(tBoxInfo);
                if (tBoxInfo.getOrderInfoList() == null || tBoxInfo.getOrderInfoList().isEmpty()) {
                    this.logger.info("DMC数据同步-------OrderInfoList参数不全");
                    continue;
                }
                tBoxInfoList.add(tBoxInfo);
            }
        }

        tShouHuoInfo.setBoxInfoList(tBoxInfoList);
    }

    private void allOrderInfo(BoxInfo tBoxInfo) {
        SendDetail tSendDatail = new SendDetail();
        tSendDatail.setBoxCode(tBoxInfo.getBoxId());
        List<OrderInfo> tOrderInfoList = new ArrayList<OrderInfo>();
        List<SendDetail> tSendDatailList = this.sendDatailDao
                .querySendDatailsByBoxCode(tSendDatail);
        if (tSendDatailList != null && !tSendDatailList.isEmpty()) {
            Map<String, SendDetail> sendDatailMap = new HashMap<String, SendDetail>();
            Set<String> waybillset = new HashSet<String>();
            for (SendDetail dSendDatail : tSendDatailList) {
                waybillset.add(dSendDatail.getWaybillCode());
                sendDatailMap.put(dSendDatail.getWaybillCode(), dSendDatail);
            }
            List<String> waybillList = new CollectionHelper<String>().toList(waybillset);
            WChoice queryWChoice = new WChoice();
            queryWChoice.setQueryPackList(true);
            queryWChoice.setQueryWaybillC(true);
            List<BigWaybillDto> tWaybillList = getWaillCodeListMessge(queryWChoice, waybillList);
            if (tWaybillList != null && !tWaybillList.isEmpty()) {
                for (BigWaybillDto tWaybill : tWaybillList) {
                    if (tWaybill != null && tWaybill.getWaybill() != null) {
                        SendDetail dSendDatail = sendDatailMap.get(tWaybill.getWaybill().getWaybillCode());
                        OrderInfo tOrderInfo = new OrderInfo();

                        Waybill waybill = tWaybill.getWaybill();
                        List<DeliveryPackageD> deliveryPackage = tWaybill.getPackageList();
                        tOrderInfo.setOrderId(dSendDatail.getWaybillCode());
                        tOrderInfo.setOrderType((waybill == null || waybill.getWaybillType() == null) ? null : String.valueOf(waybill.getWaybillType()));
                        tOrderInfo.setOrderSource(String.valueOf(dSendDatail.getCreateSiteCode()));
                        tOrderInfo.setPackNum((waybill == null || waybill.getGoodNumber() == null) ? 1 : waybill.getGoodNumber());
                        tOrderInfo.setBoxId(dSendDatail.getBoxCode());
                        tOrderInfo.setOrderAdd((waybill == null || waybill.getReceiverAddress() == null) ? null : waybill.getReceiverAddress());
                        tOrderInfo.setRemark((waybill == null || waybill.getGoodWeight() == null) ? null : String.valueOf(waybill.getGoodWeight()));
                        Integer siteId = 0;
                        if (waybill != null && waybill.getOldSiteId() != null) {
                            siteId = waybill.getOldSiteId();
                        }
                        tOrderInfo.setZdId(String.valueOf(siteId));
                        String createDate = DateHelper.formatDate(tSendDatail.getOperateTime(),
                                Constants.DATE_TIME_FORMAT);
                        tOrderInfo.setCreateTime(createDate);
                        tOrderInfo.setBatchId(tBoxInfo.getBatchId());
                        tOrderInfo.setDispatchType((waybill == null || waybill.getWaybillType() == null) ? "0" : String.valueOf(waybill.getDistanceType()));
                        this.allPackInfo(tSendDatailList, tOrderInfo, deliveryPackage);
                        if (tOrderInfo.getPackInfoList() == null
                                || tOrderInfo.getPackInfoList().isEmpty()) {
                            continue;
                        }
                        tOrderInfoList.add(tOrderInfo);
                    }
                }
            }
            tBoxInfo.setOrderInfoList(tOrderInfoList);
        }
    }

    private void allPackInfo(List<SendDetail> tSendDatailList, OrderInfo tOrderInfo, List<DeliveryPackageD> deliveryPackage) {
        List<PackInfo> list = new ArrayList<PackInfo>();
        for (SendDetail dSendDatail : tSendDatailList) {
            if (tOrderInfo.getOrderId().equals(dSendDatail.getWaybillCode())) {
                PackInfo tPackInfo = new PackInfo();
                tPackInfo.setPackNo(dSendDatail.getPackageBarcode());
                tPackInfo.setPackWeight(BigDecimal.valueOf(0.0));
                if (deliveryPackage != null && !deliveryPackage.isEmpty()
                        && BusinessHelper.checkIntNumRange(deliveryPackage.size())) {
                    for (DeliveryPackageD delivery : deliveryPackage) {
                        if (delivery.getPackageBarcode().equals(dSendDatail.getPackageBarcode()) && delivery.getAgainWeight() != null) {
                            tPackInfo.setPackWeight(BigDecimal.valueOf(delivery.getAgainWeight()));
                        }
                    }
                }
                tPackInfo.setExpNo(dSendDatail.getWaybillCode());
                list.add(tPackInfo);
            }
        }
        tOrderInfo.setPackInfoList(list);
    }

    private void getWaybillResult(List<BigWaybillDto> datalist, WChoice queryWChoice, List<String> waybills) {
        BaseEntity<List<BigWaybillDto>> results = waybillQueryManager.getDatasByChoice(waybills, queryWChoice);
        if (results != null && results.getResultCode() > 0) {
            logger.info("调用运单接口返回信息" + results.getResultCode() + "-----" + results.getMessage());
            List<BigWaybillDto> datas = results.getData();
            if (datas != null && !datas.isEmpty()) {
                for (BigWaybillDto dto : datas) {
                    datalist.add(dto);
                }
            }
        }
    }

    @Override
    public List<BigWaybillDto> getWaillCodeListMessge(WChoice queryWChoice, List<String> waybillCodes) {
        List<BigWaybillDto> datalist = new ArrayList<BigWaybillDto>();
        try {
            if (waybillCodes != null && !waybillCodes.isEmpty()) {
                int n = waybillCodes.size() / 50;
                int m = waybillCodes.size() % 50;
                if (n > 0) {
                    List<String> waybills = new ArrayList<String>();
                    int num = 0;
                    for (String code : waybillCodes) {
                        num++;
                        waybills.add(code);
                        if (num / 50 > 0 && num % 50 == 0 && n > 0) {
                            getWaybillResult(datalist, queryWChoice, waybills);
                            waybills = new ArrayList<String>();
                            n--;
                        } else if (n == 0 && m > 0 && waybillCodes.size() == num) {
                            getWaybillResult(datalist, queryWChoice, waybills);
                        }
                    }
                } else {
                    getWaybillResult(datalist, queryWChoice, waybillCodes);
                }
            }
        } catch (Exception e) {
            logger.error("取件单基础信息调用异常-------");
        }
        return datalist;
    }

    /**
     * 根据包裹号、箱号、创建站点（分拣中心）、接收站点来判断
     * send_type=30表示三方
     * is_cancel=0表示未取消分拣或者发货
     *
     * @param sendDetail
     * @return
     */
    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    @Override
    public boolean checkSendByPackage(SendDetail sendDetail) {
        //step 1.判断箱子是否发货send_m
        SendM sendM = new SendM();
        sendM.setCreateSiteCode(sendDetail.getCreateSiteCode());
        sendM.setReceiveSiteCode(sendDetail.getReceiveSiteCode());
        sendM.setBoxCode(sendDetail.getBoxCode());
        sendM.setSendType(sendDetail.getSendType());
        if (sendMDao.checkSendByBox(sendM)) {//主表，记录箱子的发货信息
            //step 2.判断包裹是否发货
            return sendDatailDao.checkSendByPackage(sendDetail);
        }
        return false;
    }

    public List<SendDetail> findOrder(SendDetail sendDetail) {
        return sendDatailDao.findOrder(sendDetail);
    }

    /**
     * 快运发货校验运单包裹不齐
     * @param sendMList
     * @return
     */
    public ThreeDeliveryResponse checkThreePackageForKY(List<SendM> sendMList){
        List<SendThreeDetail> tDeliveryResponse = null;
        Integer businessType = sendMList.size() > 0 ? sendMList.get(0).getSendType() : 10;
        List<SendDetail> allList = new ArrayList<SendDetail>();
        getAllList(sendMList, allList);
        //1.判断发货数据是否包含派车单并进行派车单运单不齐校验
        DeliveryResponse scheduleWaybillResponse = new DeliveryResponse();
        scheduleWaybillResponse.setCode(DeliveryResponse.CODE_OK);
        if(!businessType.equals(20)){    //非逆向才进行派车单运单齐全校验
            logger.info("发货数据判断运单是否不全");
            checkScheduleWaybill(allList, scheduleWaybillResponse);    //发货请求是否包含派车单
        }
        //2.发货数据判断包裹是否不全
        this.logger.info("发货数据判断包裹是否不全");
        if (businessType.equals(20)) {
            tDeliveryResponse =  reverseComputer.compute(allList, true);    //逆向不处理派车单发货的情况
        } else {
            tDeliveryResponse =  forwardComputer.compute(allList, true);
        }
        //派车单发货不齐不返回明细数据
        String msg = tDeliveryResponse != null && !tDeliveryResponse.isEmpty() ? DeliveryResponse.MESSAGE_SCHEDULE_PACKAGE_INCOMPLETE : "";
        if(!DeliveryResponse.CODE_OK.equals(scheduleWaybillResponse.getCode())){
            msg = StringUtils.isNotBlank(msg) ? "运单/" + msg : scheduleWaybillResponse.getMessage();
        }
        if(StringUtils.isNotBlank(msg)){
            return new ThreeDeliveryResponse(DeliveryResponse.CODE_SCHEDULE_INCOMPLETE, msg, null);
        }else{
            return new ThreeDeliveryResponse(JdResponse.CODE_OK, JdResponse.MESSAGE_OK, null);
        }
    }

    /**
   * 快运发货差异查询
   *
   * @param sendMList
   * @param queryType
   * @return
   */
  public ThreeDeliveryResponse differentialQuery(List<SendM> sendMList, Integer queryType) {
    // 未扫描包裹
    List<SendThreeDetail> notScaned = null;
    List<SendThreeDetail> res = new ArrayList<SendThreeDetail>();
    Integer businessType = sendMList.size() > 0 ? sendMList.get(0).getSendType() : 10;
    List<SendDetail> hasScaned = new ArrayList<SendDetail>();
    this.logger.info("快运发货差异查询");
    getAllList(sendMList, hasScaned);
    //查询未扫描或者查询所有
    if (AbstructDiffrenceComputer.QUERY_NOSCANED.equals(queryType) || AbstructDiffrenceComputer.QUERY_ALL.equals(queryType)) {
      if (businessType.equals(20)) {
        notScaned = reverseComputer.compute(hasScaned, false);
      } else {
        notScaned = forwardComputer.compute(hasScaned, false);
      }
      if (null != notScaned) {
          //循环删除已扫描数据只保留未扫数据
          for (int index = 0; index < notScaned.size(); ++index) {
          if (AbstructDiffrenceComputer.HAS_SCANED.equals(notScaned.get(index).getMark())) {
            notScaned.remove(index--);
          }
        }

        res.addAll(notScaned);
      }
    }
      //查询已扫描或者查询所有
    if (AbstructDiffrenceComputer.QUERY_HASSCANED.equals(queryType) || AbstructDiffrenceComputer.QUERY_ALL.equals(queryType)) {
      for (SendDetail item : hasScaned) { // 遍历该箱的所有包裹
        SendThreeDetail diff = new SendThreeDetail();
        diff.setBoxCode(item.getBoxCode());
        diff.setPackageBarcode(item.getPackageBarcode());
        diff.setMark(AbstructDiffrenceComputer.HAS_SCANED);
        diff.setIsWaybillFull(1);
        res.add(diff);
      }
    }

    return new ThreeDeliveryResponse(JdResponse.CODE_OK, JdResponse.MESSAGE_OK, res);
    }

    /**
     * 一车一单按箱发货路由检验
     * 从sorting表中获取该箱号中的记录
     * 取出一单，读取其路由，如果为空，则再取一单
     * @param queryPara
     * @return
     */
    public DeliveryResponse checkRouterForCBox(SendM queryPara){
        DeliveryResponse response = new DeliveryResponse();
        response.setCode(JdResponse.CODE_OK);
        response.setMessage(JdResponse.MESSAGE_OK);

        String boxCode = queryPara.getBoxCode();
        Integer createSiteCode = queryPara.getCreateSiteCode();
        Integer receiveSiteCode = queryPara.getReceiveSiteCode();

        //批次号目的地类型为64的进行路由校验，否则走原来的逻辑
        BaseStaffSiteOrgDto siteInfo = baseService.queryDmsBaseSiteByCode(receiveSiteCode+"");
        if(siteInfo == null){
            logger.warn("checkRouterForCBox获取到的站点信息为空.站点：" + receiveSiteCode);
            return response;
        }
        if(siteInfo.getSiteType() != 64){
            logger.info("checkRouterForCBox 批次号目的地["+receiveSiteCode + "]的站点类型为：" + siteInfo.getSiteType()+"不进行路由校验");
            return response;
        }

        // 获取箱中的运单号
        List<String> waybillCodes = getWaybillCodesByBoxCodeAndFetchNum(boxCode,3);

        //获取运单对应的路由
        String routerStr = null;
        String waybillCodeForVerify = null;
        if (waybillCodes != null && !waybillCodes.isEmpty()) {
            for(String  waybillCode : waybillCodes){
                //获取路由信息
                routerStr = jsfSortingResourceService.getRouterByWaybillCode(waybillCode);

                //如果路由为空，则取下一单
                if(StringHelper.isNotEmpty(routerStr)){
                    waybillCodeForVerify = waybillCode;
                    break;
                }
            }
        }

        if(routerStr == null || StringHelper.isEmpty(routerStr)){
            return response;
        }

        logger.warn("C网路由校验按箱发货,箱号为:"+ boxCode +"取到的运单号为：" + waybillCodeForVerify + "，运单正确路由为:" + routerStr);

        String  logInfo = "";

        //路由校验逻辑
        boolean getCurNodeFlag = false;  //路由中是否包含当前分拣中心标识

        String [] routerNodes = routerStr.split(WAYBILL_ROUTER_SPLITER);

        //当前分拣中心可以到达的下一网点集合
        List<Integer> routerShow = new ArrayList<Integer>();

        for(int i=0 ;i< routerNodes.length-1; i++){
            int curNode = Integer.parseInt(routerNodes[i]);
            int nexNode = Integer.parseInt(routerNodes[i+1]);
            if(curNode == createSiteCode){
                getCurNodeFlag = true;
                routerShow.add(nexNode);
                if(nexNode == receiveSiteCode){
                    //校验成功增加cassandra日志
                    logInfo = "C网路由校验按箱发货校验通过.箱号:"+ boxCode  + ",取到的运单号："+
                            waybillCodes + ",进行校验的运单号：" + waybillCodeForVerify +
                            ",运单正确路由:" + routerStr +  ",操作站点：" + createSiteCode +
                            ",批次号的目的地：" + receiveSiteCode;
                    logger.info(logInfo);
                    addCassandraLog(boxCode,boxCode,logInfo);
                    return response;
                }
            }
        }

        //运单的路由上不包含当前操作的分拣中心，则无法确定下一站，直接返回
        if(!getCurNodeFlag){
            logInfo="C网路由校验按箱发货，路由中不包含当前分拣中心.箱号:"+ boxCode  + ",取到的运单号："+
                    waybillCodes + ",进行校验的运单号：" + waybillCodeForVerify +
                    ",运单正确路由:" + routerStr +  ",操作站点：" + createSiteCode +
                    ",批次号的目的地：" + receiveSiteCode;
            addCassandraLog(boxCode,boxCode,logInfo);
            return response;
        }

        //将下一站由编码转换成名称，并进行截取，供pda提示
        String routerShortNames="";
        for(Integer dmsCode : routerShow){
            if(StringHelper.isEmpty(baseService.getDmsShortNameByCode(dmsCode))){
                continue;
            }
            routerShortNames +=  baseService.getDmsShortNameByCode(dmsCode) + Constants.SEPARATOR_COMMA;
        }

        if(StringHelper.isNotEmpty(routerShortNames)){
            routerShortNames = routerShortNames.substring(0,routerShortNames.length()-1);
        }

        response.setCode(DeliveryResponse.CODE_CROUTER_ERROR);
        response.setMessage(DeliveryResponse.MESSAGE_CROUTER_ERROR +
                "取到运单：" + waybillCodeForVerify + "，路由下一站:" + routerShortNames);

        logInfo = "C网路由校验按箱发货,箱号为:"+ boxCode  + ",取到的运单号为："+
                waybillCodes + ",进行校验的运单号为：" + waybillCodeForVerify +
                ",运单正确路由为:" + routerStr +  ",操作站点为：" + createSiteCode +
                ",批次号的目的地为：" + receiveSiteCode;

        addCassandraLog(boxCode,boxCode,logInfo);

        return response;
    }

    /**
     * 记录cassandra日志
     * @param head
     * @param key
     * @param body
     */
    private void addCassandraLog(String head,String key, String body){
        //记录cassandra日志
        Goddess goddess = new Goddess();
        goddess.setHead(head);
        goddess.setKey(key);
        goddess.setDateTime(new Date());

        goddess.setBody(body);

        goddessService.save(goddess);
    }
    /**
     * 快运发货校验路由信息
     * @param sendM
     * @flag 新老版本标识，0是老版本调用，1是新版本调用接口
     * @return
     */
    public DeliveryResponse checkRouterForKY(SendM sendM, Integer flag){
        DeliveryResponse response = new DeliveryResponse(JdResponse.CODE_OK,JdResponse.MESSAGE_OK);

        //获取提示语
        List<String> tipMessageList = new ArrayList<String>();
        response.setTipMessages(tipMessageList);
        if(isWaybillNeedAddQuarantine(sendM)){
            tipMessageList.add(DeliveryResponse.TIP_MESSAGE_NEED_ADD_QUARANTINE);
        }

        //1.批次号封车校验，已封车不能发货
        if (StringUtils.isNotEmpty(sendM.getSendCode()) && newSealVehicleService.checkSendCodeIsSealed(sendM.getSendCode())) {
            response.setCode(DeliveryResponse.CODE_SEND_CODE_ERROR);
            response.setMessage(DeliveryResponse.MESSAGE_SEND_CODE_ERROR);
            return response;
        }

        //2.校验箱号或者包裹是否已发货
        SysConfigContent sysConfigContent = sysConfigService.getSysConfigJsonContent("b.check.send.cancel.last.send.switch");
        //判断条件：
        // （1）接口标识为调用的新接口
        // （2）开关存在并开启状态，或当前始发分拣中心存在于列表里
        // 同时满足1和2走新逻辑，否则都走老逻辑
        if (Constants.DELIVERY_ROUTER_VERIFICATION_NEW.equals(flag) && sysConfigContent != null && (sysConfigContent.getMasterSwitch() || sysConfigContent.getSiteCodes().contains(sendM.getCreateSiteCode()))) {
            response = checkCancelLastSend(sendM);
        }
        else {
            response = deliveryCheckHasSend(sendM);
        }

        response.setTipMessages(tipMessageList);

        if(!JdResponse.CODE_OK.equals(response.getCode())){
            return response;
        }

        //3.B网包装耗材服务确认拦截
        if (! this.checkWaybillConsumable(sendM)) {
            response.setCode(DeliveryResponse.CODE_29120);
            response.setMessage(DeliveryResponse.MESSAGE_29120);
            return response;
        }

        //4.快运称重及运费拦截
        logger.info("快运发货运单重量及运费拦截开始");
        List<String> waybillCodes = getWaybillCodesBySendM(sendM);
        InterceptResult<String> interceptResult = this.interceptWaybillForB2b(waybillCodes);
        if(!interceptResult.isSucceed()){
        	logger.warn("快运发货运单重量及运费拦截："+interceptResult.getMessage());
            response.setCode(DeliveryResponse.CODE_INTERCEPT_FOR_B2B);
            response.setMessage(interceptResult.getMessage());
            return response;
        }

        //5.快运发货非城配运单发往车队，判断是否可以C转B
        Integer receiveSiteCode = sendM.getReceiveSiteCode();
        Integer originalSiteCode = sendM.getCreateSiteCode();
        BaseStaffSiteOrgDto receiveSite = baseMajorManager.getBaseSiteBySiteId(receiveSiteCode);
        if(receiveSite == null){
            response.setCode(JdResponse.CODE_PARAM_ERROR);
            response.setMessage("无法获取目的站点："+receiveSiteCode);
            logger.warn("快运发货无法获取目的站点："+receiveSiteCode);
            return response;
        }else if(!Integer.valueOf(Constants.DMS_SITE_TYPE).equals(receiveSite.getSiteType())){//发货至分拣中心才校验
            logger.warn("快运发货目的站点非分拣中心，不校验B2B路由："+receiveSiteCode);
            if(!checkDmsToVendor(sendM)){
                response.setCode(DeliveryResponse.CODE_SCHEDULE_INCOMPLETE);
                response.setMessage(DeliveryResponse.MESSAGE_DMS_TO_VENDOR_ERROR);
                return response;
            }
            return response;
        }

        //6.判断路由
        Integer destinationSiteCode = getDestinationSiteCode(sendM);
        logger.info("根据包裹号或箱号获取目的分拣中心："+destinationSiteCode);
        if(destinationSiteCode == null){
            response.setCode(DeliveryResponse.CODE_SCHEDULE_INCOMPLETE);
            response.setMessage(DeliveryResponse.MESSAGE_ROUTER_MISS_ERROR);
            return response;
        }
        try {
            logger.info("B网路由查询条件："+JsonHelper.toJson(sendM));
            List<B2BRouter> routers = b2bRouterService.getB2BRouters(originalSiteCode, destinationSiteCode);
            logger.info("B网路由查询结果："+JsonHelper.toJson(routers));
            if(routers == null || routers.isEmpty()){
                response.setCode(DeliveryResponse.CODE_SCHEDULE_INCOMPLETE);
                response.setMessage(DeliveryResponse.MESSAGE_ROUTER_MISS_ERROR);
            }else{
                List<B2BRouterNode> nodes = b2bRouterService.getNextCodes(originalSiteCode, destinationSiteCode, receiveSiteCode);
                logger.info("B网路由下一节点查询结果："+JsonHelper.toJson(nodes));
                if(nodes == null || nodes.isEmpty()){
                    response.setCode(DeliveryResponse.CODE_SCHEDULE_INCOMPLETE);
                    response.setMessage(DeliveryResponse.MESSAGE_ROUTER_ERROR);
                }
            }
        }catch (Exception e){
            logger.error("B网路由查询异常："+JsonHelper.toJson(sendM), e);
            response.setCode(JdResponse.CODE_SERVICE_ERROR);
            response.setMessage(JdResponse.MESSAGE_SERVICE_ERROR);
        }
        if(!JdResponse.CODE_OK.equals(response.getCode())){
            logger.warn("B网路由拦截："+originalSiteCode+"->"+receiveSiteCode+"->"+destinationSiteCode+","+response.getMessage());
        }

        return response;
    }

    /**
     * 校验多次发货是否取消上次发货
     * @param sendM 发货实体
     */
    public DeliveryResponse checkCancelLastSend(SendM sendM) {

        DeliveryResponse deliveryResponse = new DeliveryResponse(JdResponse.CODE_OK, JdResponse.MESSAGE_OK);

        String boxCode = sendM.getBoxCode();
        Integer createSiteCode = sendM.getCreateSiteCode();
        Integer receiveSiteCode = sendM.getReceiveSiteCode();
        Date operateTime = sendM.getOperateTime();

        // 根据箱号/包裹号、始发站点、目的站点获取最后一次的发货记录
        SendM lastSendM = this.getRecentSendMByParam(boxCode, createSiteCode, null, operateTime);
        if (lastSendM != null) {
            String lastSendCode = lastSendM.getSendCode();
            if (StringUtils.isNotEmpty(lastSendCode)) {
                //封车一小时判断
                if (! this.sendSealTimeIsOverOneHour(lastSendCode, operateTime)) {
                    if (receiveSiteCode.equals(lastSendM.getReceiveSiteCode())) {
                        //当前发货目的地 与 最后一次发货目的地相同，提示已发货
                        deliveryResponse.setCode(DeliveryResponse.CODE_Delivery_IS_SEND);
                        deliveryResponse.setMessage(DeliveryResponse.MESSAGE_Delivery_IS_SEND);
                    } else {
                        //如果不相同，提示一个确认框
                        deliveryResponse.setCode(DeliveryResponse.CODE_CONFIRM_CANCEL_LAST_SEND);
                        deliveryResponse.setMessage(DeliveryResponse.MESSAGE_CONFIRM_CANCEL_LAST_SEND);
                    }
                }
            }
        }

        return deliveryResponse;
    }

    /**
     *  获取包裹或者箱子的末级分拣中心
     * @param sendM
     * @return
     */
    private Integer getDestinationSiteCode(SendM sendM){
        Integer destinationSiteCode = null;
        if (BusinessHelper.isBoxcode(sendM.getBoxCode())) {
            Box box = boxService.findBoxByCode(sendM.getBoxCode());
            if(box == null){
                logger.warn("快运发货箱号不存在，无法获取最终目的地："+JsonHelper.toJson(sendM));
                return destinationSiteCode;
            }
            BaseStaffSiteOrgDto boxReceiveSiteCode = baseMajorManager.getBaseSiteBySiteId(box.getReceiveSiteCode());
            if(boxReceiveSiteCode != null){
                destinationSiteCode = Integer.valueOf(Constants.DMS_SITE_TYPE).equals(boxReceiveSiteCode.getSiteType()) ? box.getReceiveSiteCode():boxReceiveSiteCode.getDmsId();
            }else{
                logger.warn("快运发货箱号目的地不存在，无法获取最终目的地："+JsonHelper.toJson(sendM));
            }
        } else if (WaybillUtil.isPackageCode(sendM.getBoxCode())) {
            Integer preSiteCode = null;
            String waybillCode = WaybillUtil.getWaybillCode(sendM.getBoxCode());
            if(StringUtils.isBlank(waybillCode)){
                logger.warn("快运发货包裹号非法，无法获取最终目的地："+JsonHelper.toJson(sendM));
            }else{
                preSiteCode = getPreSiteCodeByWayBillCode(waybillCode);
            }
            if(preSiteCode != null){
                BaseStaffSiteOrgDto preSiteCodeDto = baseMajorManager.getBaseSiteBySiteId(preSiteCode);
                if(preSiteCodeDto != null){
                    destinationSiteCode = preSiteCodeDto.getDmsId();
                }else{
                    logger.warn("快运发货包裹预分拣站点不存在，无法获取最终目的地："+JsonHelper.toJson(sendM));
                }
            }
        }
        return destinationSiteCode;
    }

    /**
     * 快运发货非城配运单发往车队，判断是否可以C转B
     * @param sendM
     * @return
     */
    private boolean checkDmsToVendor(SendM sendM){
        BaseStaffSiteOrgDto receiveSite = baseMajorManager.getBaseSiteBySiteId(sendM.getReceiveSiteCode());

        //发货目的地不是车队，返回true，不再校验
        if(Constants.BASE_SITE_MOTORCADE != receiveSite.getSiteType()){
            return true;
        }
        String waybillCode = null;
        if (!BusinessHelper.isBoxcode(sendM.getBoxCode())) {
            if(WaybillUtil.isPackageCode(sendM.getBoxCode())){
                waybillCode = WaybillUtil.getWaybillCode(sendM.getBoxCode());
            }else if(WaybillUtil.isWaybillCode(sendM.getBoxCode())){
                waybillCode = sendM.getBoxCode();
            }
            if(StringUtils.isNotEmpty(waybillCode) && WaybillUtil.isWaybillCode(waybillCode)){
                com.jd.bluedragon.common.domain.Waybill waybill =  waybillCommonService.findByWaybillCode(waybillCode);
                //运单为非城配类型，且 发货目的地为【车队】类型的
                if(waybill != null && !BusinessHelper.isDmsToVendor(waybill.getWaybillSign(), waybill.getSendPay())){
                    InvokeResult<Boolean> result = dmsInterturnManager.dispatchToExpress(sendM.getCreateSiteCode(), waybill.getBusiId(), waybill.getWaybillSign());
                    if(JdResponse.CODE_OK == result.getCode() && result.getData() != null && result.getData().booleanValue() == false){
                        return false;
                    }
                }
            }
        }

        return true;
    }

    /**
     * 获取运单的预分拣站点
     * @param waybillCode
     * @return
     */
    private Integer getPreSiteCodeByWayBillCode(String waybillCode){
        Integer preSiteCode = null;
        BaseEntity<BigWaybillDto> baseEntity = waybillQueryManager.getDataByChoice(waybillCode, true, false, false, false);
        if(baseEntity != null && Constants.RESULT_SUCCESS == baseEntity.getResultCode()) {
            //运单数据为空，直接返回运单数据为空异常
            if (baseEntity.getData() == null || baseEntity.getData().getWaybill() == null) {
                logger.warn("调用运单接口获取运单数据为空，waybillCode：" + waybillCode);
            }else{
                preSiteCode = baseEntity.getData().getWaybill().getOldSiteId();
            }
        }
        if(preSiteCode == null){
            logger.warn("调用运单接口获取运单预分拣站点为空，waybillCode：" + waybillCode);
        }
        return preSiteCode;
    }

    /**
     * b2b运单拦截相关的处理逻辑
     * @param waybillCodes
     * @return
     */
    private InterceptResult<String> interceptWaybillForB2b(List<String> waybillCodes){
    	InterceptResult<String> interceptResult = new InterceptResult<String>();
    	interceptResult.toSuccess();
    	//B网营业厅寄付现结运费发货拦截开关（1开启，0关闭）
        boolean sendFreightInterception = false;
        try{
            SysConfig config = sysConfigService.findConfigContentByConfigName(FREIGHT_INTERCEPTION);
            if(config != null && Constants.STRING_FLG_TRUE.equals(config.getConfigContent())){
                sendFreightInterception = true;
            }
        }catch (Exception e){
            logger.error("快运发货查询寄付运费拦截开关失败，不在拦截寄付运费，对应运单："+waybillCodes, e);
        }

    	List<String> noHasWeightWaybills = new ArrayList<String>();
    	List<String> noHasFreightWaybills = new ArrayList<String>();
    	List<String> sendNoHasFreightWaybills = new ArrayList<String>();
        for(String waybillCode:waybillCodes){
        	BaseEntity<BigWaybillDto> baseEntity = waybillQueryManager.getDataByChoice(waybillCode, true, true, true, false);
        	if(baseEntity != null
					 && baseEntity.getData() != null
					 && baseEntity.getData().getWaybill() != null){
        		Waybill waybill = baseEntity.getData().getWaybill();

                //edited by hanjiaxing3 2018.07.26
                //40位非0（C网以外）并且66位为0（必须称重），需要称重量方拦截
                if (! BusinessUtil.isSignChar(waybill.getWaybillSign(), 40, '0') && BusinessUtil.isSignChar(waybill.getWaybillSign(), 66, '0')) {
                    //WaybillSign40=2时（只有外单快运纯配业务），需校验重量
                    if(BusinessUtil.isSignChar(waybill.getWaybillSign(), 40, '2')){
                        boolean hasTotalWeight = false;
                        //先校验运单的againWeight然后校验称重流水
                        if(NumberHelper.gt0(waybill.getAgainWeight())){
                            hasTotalWeight = true;
                        }else{
                            hasTotalWeight = dmsWeightFlowService.checkTotalWeight(waybillCode);
                        }
                        if(!hasTotalWeight){
                            noHasWeightWaybills.add(waybillCode);
                        }
                    }
                //end
        		}
        		//b2b校验是否包含-到付运费
        		if(!BusinessHelper.hasFreightForB2b(baseEntity.getData())){
        			noHasFreightWaybills.add(waybillCode);
        		}
        		//b2b校验是否包含-寄付运费
        		if(sendFreightInterception && !BusinessHelper.hasSendFreightForB2b(baseEntity.getData())){
                    sendNoHasFreightWaybills.add(waybillCode);
        		}
        	}else{
        		noHasWeightWaybills.add(waybillCode);
        	}
        	//超过3单则中断校验逻辑
    		if(noHasWeightWaybills.size() >= MAX_SHOW_NUM
                    ||noHasFreightWaybills.size() >= MAX_SHOW_NUM
                    || sendNoHasFreightWaybills.size() >= MAX_SHOW_NUM){
    			break;
    		}
        }
        if(!noHasWeightWaybills.isEmpty()){
        	interceptResult.toFail();
        	interceptResult.setMessage("运单无总重量："+noHasWeightWaybills);
        	return interceptResult;
        }
        if(!noHasFreightWaybills.isEmpty()){
        	interceptResult.toFail();
        	interceptResult.setMessage("运单无到付运费金额："+noHasFreightWaybills);
        	return interceptResult;
        }
        if(!sendNoHasFreightWaybills.isEmpty()){
            interceptResult.toFail();
            interceptResult.setMessage("运单无寄付运费金额："+sendNoHasFreightWaybills);
            return interceptResult;
        }
        return interceptResult;
    }
	/**
     * 老发货校验服务，校验包裹不齐
     * @param sendMList
     * @return
     */
    @SuppressWarnings("rawtypes")
    public ThreeDeliveryResponse checkThreePackage(List<SendM> sendMList) {
        List<SendThreeDetail> tDeliveryResponse = null;
        Integer businessType = sendMList.size() > 0 ? sendMList.get(0).getSendType() : 10;
        List<SendDetail> allList = new ArrayList<SendDetail>();
        this.logger.info("发货数据判断包裹是否不全");
        getAllList(sendMList, allList);
        if (businessType.equals(20)) {
            tDeliveryResponse = reverseComputer.compute(allList, false);
            //退仓时 增加 支持半退逻辑
            if(!sysConfigService.getConfigByName("reverse.part.not.check.switch")){
                ThreeDeliveryResponse response = checkReversePartSend(tDeliveryResponse,allList);
                if(response.getCode().equals(DeliveryResponse.CODE_Delivery_PART_SEND_ERROR) || response.getCode().equals(DeliveryResponse.CODE_Delivery_PART_SEND) ){
                    return response;
                }
            }

        } else {
            tDeliveryResponse = forwardComputer.compute(allList, false);
        }
        if (tDeliveryResponse != null && !tDeliveryResponse.isEmpty()) {
            return new ThreeDeliveryResponse(DeliveryResponse.CODE_Delivery_THREE_SORTING,
                    DeliveryResponse.MESSAGE_Delivery_THREE_SORTING, tDeliveryResponse);
        } else {
            return new ThreeDeliveryResponse(JdResponse.CODE_OK, JdResponse.MESSAGE_OK, null);
        }
    }

    /**
     * 获取发货明细中派车单号与运单号的对应关系
     * @param allList
     * @param scheduleWaybillResponse
     * @return
     */
    private boolean checkScheduleWaybill(List<SendDetail> allList, DeliveryResponse scheduleWaybillResponse){
        scheduleWaybillResponse.setCode(DeliveryResponse.CODE_OK);
        //获取派车单和箱号的对应关系
        Map<String/*scheduleCode*/, Set<String>/*Set<waybillCode>*/> scheduleWaybillCodeMap = getScheduleCodeWithBoxCode(allList);
        boolean isScheduleRequst = scheduleWaybillCodeMap.size() > 0 ;
        if(isScheduleRequst){//有派车单的箱子
            //依次校验各个派车单下已分拣运单是否齐全
            for (Map.Entry<String, Set<String>> entry : scheduleWaybillCodeMap.entrySet()) {
                String scheduleCode = entry.getKey();
                Set<String> waybillSet = entry.getValue();
                List<String> scheduleWaybillCodelist = transbillMService.getEffectWaybillCodesByScheduleBillCode(scheduleCode);
                if(scheduleWaybillCodelist != null && scheduleWaybillCodelist.size() != waybillSet.size()){//此派车单运单不齐
                    scheduleWaybillResponse.setCode(DeliveryResponse.CODE_SCHEDULE_INCOMPLETE);
                    scheduleWaybillResponse.setMessage(DeliveryResponse.MESSAGE_SCHEDULE_WAYBILL_INCOMPLETE);
                    break;
                }
            }
        }

        return isScheduleRequst;   //返回此发货请求是否含派车单号
    }

    /**
     * 由于一个派车单可以装多个箱子，这里获取派车单下所有发货的运单
     * @param allList
     * @return
     */
    private Map<String, Set<String>> getScheduleCodeWithBoxCode(List<SendDetail> allList){
        //其实可以不排序（生成的时候是按箱依次添加的）
        Collections.sort(allList, new Comparator<SendDetail>() {
            @Override
            public int compare(SendDetail lhs, SendDetail rhs) {
                return lhs.getBoxCode().compareToIgnoreCase(rhs.getBoxCode());
            }
        });
        Map<String/*scheduleCode*/, Set<String>/*Set<waybillCode>*/> scheduleCodeWaybillCodeMap = new HashMap<String, Set<String>>();
        String lastBoxCode = "";
        String scheduleBCode = null;
        for (SendDetail sendDetail : allList) {
            if (BusinessHelper.isBoxcode(sendDetail.getBoxCode())) {
                String boxCode = StringUtils.trim(sendDetail.getBoxCode());
                String wayBillCode = sendDetail.getWaybillCode();
                if(!lastBoxCode.equals(boxCode)){//下一箱
                    lastBoxCode = boxCode;
                    scheduleBCode = getScheduleCode(boxCode, wayBillCode);
                }
                if(StringUtils.isNotBlank(scheduleBCode)){
                    putValueToMap(scheduleCodeWaybillCodeMap, scheduleBCode, wayBillCode);
                }
            }else if (WaybillUtil.isPackageCode(sendDetail.getBoxCode())) {
                String wayBillCode = WaybillUtil.getWaybillCode(sendDetail.getBoxCode());
                String scheduleWCode = getScheduleCode(null, wayBillCode);
                //原包发货的包裹是派车单的包裹
                if(StringUtils.isNotBlank(scheduleWCode)){
                    putValueToMap(scheduleCodeWaybillCodeMap, scheduleWCode, wayBillCode);
                }
            }
        }
        return scheduleCodeWaybillCodeMap;
    }

    /**
     * 获取派车单号
     * @param boxCode
     * @param wayBillCode
     * @return
     */
    private String getScheduleCode(String boxCode, String wayBillCode){
        String scheduleCode = null;
        if(StringUtils.isNotBlank(boxCode)){    //1.缓存读
            scheduleCode = transBillScheduleService.getKey(boxCode);
        }
        if((StringUtils.isBlank(scheduleCode) || Constants.SCHEDULE_CODE_DEFAULT.equals(scheduleCode)) && StringUtils.isNotBlank(wayBillCode)){
            scheduleCode = transBillScheduleService.queryScheduleCode(wayBillCode);
        }
        if(StringUtils.isBlank(scheduleCode) || Constants.SCHEDULE_CODE_DEFAULT.equals(scheduleCode)){
            scheduleCode = null;
        }
        return scheduleCode;
    }

    /**
     * Map的value为集合时的put方法
     * @param map
     * @param key
     * @param aValue
     */
    private static void putValueToMap(Map<String, Set<String>> map, String key, String aValue){
        if (map.containsKey(key)) {
            map.get(key).add(aValue);
        } else {
            Set<String> temp = new HashSet<String>();
            temp.add(aValue);
            map.put(key, temp);
        }
    }

    /**
     * 获取所有发货明细
     * @param sendMList
     * @param allList
     */
    public void getAllList(List<SendM> sendMList, List<SendDetail> allList) {
        for (SendM tSendM : sendMList) {
            SendDetail tSendDatail = new SendDetail();
            tSendDatail.setBoxCode(tSendM.getBoxCode());
            tSendDatail.setCreateSiteCode(tSendM.getCreateSiteCode());
            tSendDatail.setReceiveSiteCode(tSendM.getReceiveSiteCode());
            tSendDatail.setIsCancel(OPERATE_TYPE_CANCEL_Y);

            if (BusinessHelper.isBoxcode(tSendM.getBoxCode())) {
                List<SendDetail> oneList = sendDatailDao.querySendDatailsBySelective(tSendDatail);
                if (oneList != null && !oneList.isEmpty()) {
                    for (SendDetail dSendDatail : oneList) {
                        if (!WaybillUtil.isSurfaceCode(dSendDatail.getPackageBarcode())) {
                            allList.add(dSendDatail);
                        }
                    }
                }
            } else if (WaybillUtil.isPackageCode(tSendM.getBoxCode())) {
                tSendDatail.setPackageBarcode(tSendM.getBoxCode());
                tSendDatail.setWaybillCode(WaybillUtil.getWaybillCode(tSendM.getBoxCode()));
                if (!WaybillUtil.isSurfaceCode(tSendM.getBoxCode())) {
                    allList.add(tSendDatail);
                }
            }
        }
    }
    /**
     * 根据sendM查询运单号
     * @param sendM
     * @return
     */
    public List<String> getWaybillCodesBySendM(SendM sendM) {
    	List<String> waybillCodes = new ArrayList<String>();
		if (BusinessHelper.isBoxcode(sendM.getBoxCode())) {
			Box box = this.boxService.findBoxByCode(sendM.getBoxCode());
			if (box != null) {
				SendDetail tSendDatail = new SendDetail();
				tSendDatail.setBoxCode(sendM.getBoxCode());
				tSendDatail.setCreateSiteCode(box.getCreateSiteCode());
				tSendDatail.setReceiveSiteCode(box.getReceiveSiteCode());
				tSendDatail.setIsCancel(OPERATE_TYPE_CANCEL_Y);
				List<SendDetail> SendDList = sendDatailDao
						.querySendDatailsBySelective(tSendDatail);
				if (SendDList != null && !SendDList.isEmpty()) {
					for (SendDetail dSendDatail : SendDList) {
						if (!WaybillUtil.isSurfaceCode(dSendDatail
								.getPackageBarcode())) {
							if(!waybillCodes.contains(dSendDatail.getWaybillCode())){
								waybillCodes.add(dSendDatail.getWaybillCode());
							}
						}
					}
				}
			}
		} else if (WaybillUtil.isPackageCode(sendM.getBoxCode())) {
            if (!WaybillUtil.isSurfaceCode(sendM.getBoxCode())) {
                waybillCodes.add(WaybillUtil.getWaybillCode(sendM.getBoxCode()));
            }
		}
        return waybillCodes;
    }

    private int getNumCount(List<SendDetail> allList, String waybillcode,
                            int num, List<String> packlist, Map<String, String> sendMap,
                            Set<String> pbList) {
        for (SendDetail dSendDatail : allList) {
            if (dSendDatail.getWaybillCode().equals(waybillcode)
                    && !pbList.contains(dSendDatail.getPackageBarcode())) {
                pbList.add(dSendDatail.getPackageBarcode());
                num++;
                packlist.add(dSendDatail.getPackageBarcode());
                sendMap.put(waybillcode, dSendDatail.getBoxCode());
            }
        }
        return num;
    }

    private void getNumMap(List<SendDetail> allList, Map<String, Integer> numMap) {
        for (SendDetail dSendDatail : allList) {
            numMap.put(dSendDatail.getWaybillCode(), BusinessUtil.getPackNumByPackCode(dSendDatail.getPackageBarcode()));
        }
    }

    /*******************
     * 根据包裹和站点查询箱号
     ******************************************/
    private List<SendDetail> queryBoxCodeBypackageCode(String packageCode,
                                                       Integer createSite, Integer receiveSite) {
        SendDetail sendDatail = new SendDetail();
        sendDatail.setPackageBarcode(packageCode);
        sendDatail.setCreateSiteCode(createSite);
        sendDatail.setReceiveSiteCode(receiveSite);
        sendDatail.setIsCancel(0);
        List<SendDetail> resultList = sendDatailDao.querySendDatailsBySelective(sendDatail);
        return resultList;
    }

    /*******************
     * 发货已扫描数据
     ******************************************/
    @Deprecated
    private Set<String> getDeliveryPackageCode(List<String> packlist, List<String> code) {
        Set<String> codeList = new HashSet<String>();
        for (String packageBarcode : packlist) {
            for (String codepackage : code) {
                if (!packageBarcode.equals(codepackage)) {
                    codeList.add(packageBarcode);
                }
            }
        }
        return codeList;
    }

    /*******************
     * 未扫描数据
     ******************************************/
    public List<String> getPackageCode(List<String> packlist) {
        List<String> codeList = new ArrayList<String>();
        for (String packageBarcode : packlist) {
            if (WaybillUtil.isPackageCode(packageBarcode)){
                codeList.addAll(WaybillUtil.generateAllPackageCodes(packageBarcode));
            }
            break;
        }
        for (String packageBarcode : packlist) {
            codeList.remove(packageBarcode);
        }
        return codeList;
    }

    @Override
    public List<SendDetail> findDeliveryPackageBySite(SendDetail sendDetail) {
        return sendDatailDao.findDeliveryPackageBySite(sendDetail);
    }

    @Override
    public List<SendDetail> findDeliveryPackageByCode(SendDetail sendDetail) {
        return sendDatailDao.findDeliveryPackageByCode(sendDetail);
    }

    @Override
    public List<SendDetail> findWaybillStatus(List<String> queryCondition) {
        logger.info("findWaybillStatus查询");
        return sendDatailReadDao.findUpdatewaybillCodeMessage(queryCondition);
    }

    @Override
    public List<SendDetail> queryBySendCodeAndSiteCode(String sendCode, Integer createSiteCode, Integer receiveSiteCode, Integer senddStatus) {
        return sendDatailReadDao.queryBySendCodeAndSiteCode(sendCode, createSiteCode, receiveSiteCode, senddStatus);
    }

    @Override
    public List<SendM> queryCountByBox(SendM sendM) {
        return sendMDao.selectBySendSiteCode(sendM);
    }

    /**
     * 补全包裹重量
     */
    @Override
    public SendDetail measureRetrieve(SendDetail sendDetail) {

        //一单多件调用接口获取包裹欣慰为空处理
        BaseEntity<List<DeliveryPackageD>> waybillWSRs = new BaseEntity<List<DeliveryPackageD>>();
        List<DeliveryPackageD> datas = null;
        try {
            //logger.info("调用运单queryPackageListForParcodes调用参数"+sendDetail.getPackageBarcode());
            waybillWSRs = waybillPackageManager.queryPackageListForParcodes(
                    Arrays.asList(new String[]{sendDetail.getPackageBarcode()}));
            if (waybillWSRs != null) {
                datas = waybillWSRs.getData();
            }

            if (null != datas && !datas.isEmpty() && null != datas.get(0) && null != datas.get(0).getGoodWeight()) {
                sendDetail.setWeight(datas.get(0).getGoodWeight());
            }
        } catch (Exception e) {
            //如果重量写入失败不影响分拣的结果
            logger.error("调用运单queryPackageListForParcodes接口时候失败", e);
        }

        return sendDetail;
    }

    /**
     * 一单多件包裹不全验证
     *
     * @param boxCode
     * @param createSiteCode
     * @param receiveSiteCode
     * @return
     */
    public List<SendThreeDetail> checkSortingDiff(String boxCode, Integer createSiteCode, Integer receiveSiteCode) {

        List<SendDetail> allList = new ArrayList<SendDetail>();
        List<SendThreeDetail> lostList = new ArrayList<SendThreeDetail>();
        Map<String, Integer> numMap = new HashMap<String, Integer>();

        if ((boxCode != null) && (!boxCode.trim().equals(""))) {
            SendDetail queryDetail = new SendDetail();
            queryDetail.setBoxCode(boxCode);
            queryDetail.setCreateSiteCode(createSiteCode);
            List<SendDetail> oneList = sendDatailDao.querySortingDiff(queryDetail);
            if (oneList != null && !oneList.isEmpty()) {
                for (SendDetail dSendDatail : oneList) {
                    allList.add(dSendDatail);
                }
            }
        } else {
            SendDetail queryDetail = new SendDetail();
            queryDetail.setCreateSiteCode(createSiteCode);
            queryDetail.setReceiveSiteCode(receiveSiteCode);
            List<SendDetail> oneList = sendDatailDao.querySortingDiff(queryDetail);
            if (oneList != null && !oneList.isEmpty()) {
                for (SendDetail dSendDatail : oneList) {
                    allList.add(dSendDatail);
                }
            }
        }

        if (allList != null && !allList.isEmpty()) {
            for (SendDetail dSendDatail : allList) {
                numMap.put(dSendDatail.getWaybillCode(), BusinessUtil.getPackNumByPackCode(dSendDatail.getPackageBarcode()));
            }

            for (Iterator<Entry<String, Integer>> it = numMap.entrySet().iterator(); it.hasNext(); ) {
                Map.Entry<String, Integer> entry = (Map.Entry<String, Integer>) it.next();
                String waybillcode = (String) entry.getKey();
                Integer numCount = (Integer) entry.getValue();

                int num = 0;
                List<String> packlist = new ArrayList<String>();
                List<SendThreeDetail> duplicatedPacklist = new ArrayList<SendThreeDetail>();
                Map<String, String> sendMap = new HashMap<String, String>();
                Set<String> packset = new HashSet<String>();
                for (SendDetail dSendDatail : allList) {
                    if (dSendDatail.getWaybillCode().equals(waybillcode)) {
                        if (!packset.contains(dSendDatail.getPackageBarcode())) {
                            packset.add(dSendDatail.getPackageBarcode());
                            num++;
                            packlist.add(dSendDatail.getPackageBarcode());
                            sendMap.put(waybillcode, dSendDatail.getBoxCode());
                        }
                        SendThreeDetail sendThreeDetail = new SendThreeDetail();
                        sendThreeDetail.setPackageBarcode(dSendDatail.getPackageBarcode());
                        sendThreeDetail.setBoxCode(dSendDatail.getBoxCode());
                        sendThreeDetail.setMark("已扫描");
                        duplicatedPacklist.add(sendThreeDetail);
                    }
                }

                if (numCount != num) {
                    if (duplicatedPacklist != null) {
                        lostList.addAll(duplicatedPacklist);
                    }
                    //未在本批次的
                    List<String> codeList = getPackageCode(packlist);
                    if (codeList != null && !codeList.isEmpty()) {
                        for (String packageBarcode : codeList) {
                            SendThreeDetail sendThreeDetail = new SendThreeDetail();
                            sendThreeDetail.setPackageBarcode(packageBarcode);
                            sendThreeDetail.setBoxCode("");
                            sendThreeDetail.setMark("未扫描");
                            lostList.add(sendThreeDetail);
                        }
                    }
                }
            }
        }
        return lostList;
    }

    /**
     * 发货明细表包裹数量补全
     * 默认补全两天的数据
     *
     * @param boxCode
     * @param createSiteCode
     * @param receiveSiteCode
     * @return
     */
    public Integer appendPackageNum(String boxCode, Integer createSiteCode,
                                    Integer receiveSiteCode) {

        SendDetail queryDetail = new SendDetail();
        queryDetail.setBoxCode(boxCode);
        queryDetail.setCreateSiteCode(createSiteCode);
        queryDetail.setReceiveSiteCode(receiveSiteCode);
        List<SendDetail> allList = sendDatailDao.queryWithoutPackageNum(queryDetail);

        Collections.sort(allList);
        Integer count = 0;
        if (allList != null && !allList.isEmpty()) {
            for (SendDetail record : allList) {
                count++;
                Integer packageNum = record.getPackageNum();
                Integer validPackageNum = null;
                if (packageNum == null) {
                    validPackageNum = BusinessUtil.getPackNumByPackCode(record.getPackageBarcode());
                }
                if (validPackageNum != null) {
                    record.setPackageNum(validPackageNum);
                    sendDatailDao.updatePackageNum(record);
                }
            }
        }
        return count;
    }

    @Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public boolean transitSend(SendM domain) {
        if (isTransferSend(domain)) {
            pushTransferSendTask(domain);
            return true;
        } else {
            return false;
        }
    }

    /**
     * 判断当前发货是否为中转发货
     * 1:装箱、正向、逆向发货
     * 2.1:发货起始站与箱子起始站不同
     * 2.2:发货起始站与箱子起始站相同，但目的站不同，且发货目的站必须是分拣中心
     *
     * @param domain 发货对象
     * @return
     */
    @Override
    public boolean isTransferSend(SendM domain) {
        if (!BusinessHelper.isBoxcode(domain.getBoxCode())) {
            return false;
        }
        if (!domain.getSendType().equals(Constants.BUSSINESS_TYPE_POSITIVE) && !domain.getSendType().equals(Constants.BUSSINESS_TYPE_REVERSE)) {
            return false;
        }
        if (domain.getReceiveSiteCode() == null || domain.getCreateSiteCode() == null) {
            return false;
        }
        String sendReceiveSiteType = "";
        BaseStaffSiteOrgDto yrDto = this.baseMajorManager.getBaseSiteBySiteId(domain.getReceiveSiteCode());
        if (yrDto != null) {
            sendReceiveSiteType = String.valueOf(yrDto.getSiteType());
        }
        Box box = this.boxService.findBoxByCode(domain.getBoxCode());
        if (null == box || null == box.getCreateSiteCode() || null == box.getReceiveSiteCode()) {
            return false;
        }
        return (!domain.getCreateSiteCode().equals(box.getCreateSiteCode()))
                || (domain.getCreateSiteCode().equals(box.getCreateSiteCode())
                && !domain.getReceiveSiteCode().equals(box.getReceiveSiteCode())
                && sendReceiveSiteType.equals("64")
        );
    }

    @Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void pushTransferSendTask(SendM domain) {
        Task tTask = new Task();
        tTask.setBoxCode(domain.getBoxCode());
        tTask.setBody(domain.getSendCode());
        tTask.setCreateSiteCode(domain.getCreateSiteCode());
        tTask.setKeyword2(String.valueOf(domain.getSendType()));
        tTask.setReceiveSiteCode(domain.getReceiveSiteCode());
        tTask.setType(Task.TASK_TYPE_SEND_DELIVERY);
        tTask.setTableName(Task.getTableName(Task.TASK_TYPE_SEND_DELIVERY));
        tTask.setSequenceName(Task.getSequenceName(Task.TABLE_NAME_SEND));
        String ownSign = BusinessHelper.getOwnSign();
        tTask.setOwnSign(ownSign);
        tTask.setKeyword1("5");//5 中转发货补全数据
        tTask.setFingerprint(Md5Helper.encode(domain.getBoxCode() + "_" + domain.getCreateSiteCode() + "_" + domain.getReceiveSiteCode() + "-" + tTask.getKeyword1()));
        tTaskService.add(tTask, true);
        logger.info("插入中转发车任务" + JsonHelper.toJson(tTask));
    }

    @Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    @JProfiler(jKey = "DMSWORKER.DeliveryService.findTransitSend", mState = {JProEnum.TP})
    public boolean findTransitSend(Task task) throws Exception {
        if (task == null || task.getBoxCode() == null
                || task.getCreateSiteCode() == null
                || task.getKeyword2() == null
                || task.getReceiveSiteCode() == null) {
            logger.warn("dofindTransitSend:中转任务参数校验失败！" + JsonHelper.toJson(task));
            return true;
        }
        Integer bCreateSiteCode = task.getCreateSiteCode();
        Integer bReceiveSiteCode = task.getReceiveSiteCode();
        String boxCode = task.getBoxCode();
        Integer type = Integer.valueOf(task.getKeyword2());//业务的正逆向
        String step1MonitorKey = "DMSWORKER.DeliveryService.findTransitSend1.getSendDetailsByBox";
        String step2TotalMonitorKey = "DMSWORKER.DeliveryService.findTransitSend2.dealSendDetails";
        String step2PerMonitorKey = "DMSWORKER.DeliveryService.findTransitSend2.dealSendDetail";
        CallerInfo step2TotalMonitor = null;
        long beginTime = System.currentTimeMillis();
        //1、根据箱号查询send明细，加入监控
        CallerInfo step1Monitor = ProfilerHelper.registerInfo(step1MonitorKey, Constants.UMP_APP_NAME_DMSWORKER);
        List<SendDetail> list = getCancelSendByBox(boxCode);
        Profiler.registerInfoEnd(step1Monitor);
        //2、循环处理send明细，根据获取的包裹数量加入监控
        if (list != null && !list.isEmpty()) {
            step2TotalMonitor = ProfilerHelper.registerInfo(
                    ProfilerHelper.genKeyByQuantity(step2TotalMonitorKey, list.size()),
                    Constants.UMP_APP_NAME_DMSWORKER);
            SendM sendM = null;
            try {
                SendM queryParam = new SendM();
                queryParam.setCreateSiteCode(bCreateSiteCode);
                queryParam.setBoxCode(boxCode);
                queryParam.setReceiveSiteCode(bReceiveSiteCode);
                List<SendM> sendMs = sendMDao.findSendMByBoxCode(queryParam);
                if (null != sendMs && !sendMs.isEmpty()) {
                    logger.warn("dofindTransitSend-find sendm from db success,value " + JsonHelper.toJson(sendMs.get(0)));
                    sendM = sendMs.get(0);
                } else {
                    logger.warn("dofindTransitSend-find sendm from db fail,param :" + JsonHelper.toJson(queryParam));
                }
            } catch (Throwable e) {
                logger.error("dofindTransitSend-发货全程跟踪异常", e);
            }
            for (SendDetail tSendDetail : list) {
                //2、处理单个send明细，加入监控
                CallerInfo step2PerMonitor = ProfilerHelper.registerInfo(step2PerMonitorKey, Constants.UMP_APP_NAME_DMSWORKER);
                tSendDetail.setSendDId(null);//把主键置空，避免后面新增时报主键冲突 组织数据将原数据状态清空
                tSendDetail.setCreateSiteCode(bCreateSiteCode);
                tSendDetail.setReceiveSiteCode(bReceiveSiteCode);
                tSendDetail.setSendType(type);
                tSendDetail.setStatus(0);
                tSendDetail.setIsCancel(OPERATE_TYPE_CANCEL_L);
                tSendDetail.setExcuteTime(new Date());
                tSendDetail.setExcuteCount(0);
                tSendDetail.setOperateTime(task.getCreateTime());

                if ((!tSendDetail.getBoxCode().equals(task.getBody())) && (!StringHelper.isEmpty(task.getBody())) && task.getBody().contains("-")) {
                    tSendDetail.setSendCode(task.getBody());
                    tSendDetail.setYn(1);
                }

                // 中转发货-插入send_d表
                this.saveOrUpdateCancel(tSendDetail);

                if ((!tSendDetail.getBoxCode().equals(task.getBody())) && (!StringHelper.isEmpty(task.getBody())) && task.getBody().contains("-")) {
                    if (null != sendM) {
                        tSendDetail.setOperateTime(sendM.getOperateTime());
                        tSendDetail.setCreateUser(sendM.getCreateUser());
                        tSendDetail.setCreateUserCode(sendM.getCreateUserCode());
                        tSendDetail.setBoardCode(sendM.getBoardCode());
                        // 设置发货业务来源
                        tSendDetail.setBizSource(sendM.getBizSource());
                    }
                    List<SendDetail> sendDetails = new ArrayList<SendDetail>(1);
                    sendDetails.add(tSendDetail);
                    // 回传发货全程跟踪
                    this.updateWaybillStatus(sendDetails);
                }
                Profiler.registerInfoEnd(step2PerMonitor);
            }
            long costTime = System.currentTimeMillis() - beginTime;
            //消耗时间大于500ms
            if (costTime >= 2000) {
                logger.warn("dofindTransitSend-cost:boxCode:" + boxCode + ",size:" + list.size() + ",cost:" + costTime + "ms");
            }
        } else {
            step2TotalMonitor = ProfilerHelper.registerInfo(
                    ProfilerHelper.genKeyByQuantity(step2TotalMonitorKey, 0),
                    Constants.UMP_APP_NAME_DMSWORKER);
            logger.warn("dofindTransitSend:根据箱号查询发货明细为空！" + boxCode + "");
        }
        Profiler.registerInfoEnd(step2TotalMonitor);
        return true;
    }

    private void saveOrUpdateCancel(SendDetail sendDetail) {
        logger.info("WORKER处理中转发货-插入SEND—D表" + JsonHelper.toJson(sendDetail));
        if (Constants.NO_MATCH_DATA == this.update(sendDetail).intValue()) {
            this.add(sendDetail);
        }
    }

    public List<SendDetail> getCancelSendByBox(String boxCode) {
        Box box = null;
        box = this.boxService.findBoxByCode(boxCode);
        if (box == null) {
            return null;
        }
        Integer yCreateSiteCode = box.getCreateSiteCode();
        Integer yReceiveSiteCode = box.getReceiveSiteCode();
        SendDetail tsendDatail = new SendDetail();
        tsendDatail.setBoxCode(boxCode);
        tsendDatail.setCreateSiteCode(yCreateSiteCode);
        tsendDatail.setReceiveSiteCode(yReceiveSiteCode);
        tsendDatail.setIsCancel(OPERATE_TYPE_CANCEL_Y);
        // 查询未操作取消的跨中转发货明细数据，用于补中转发货sendD数据
        List<SendDetail> sendDatailist = this.sendDatailDao.querySendDatailsBySelective(tsendDatail);
        // 判断sendD数据是否存在，若不存在则视为站点发货至分拣，调用TMS获取箱子对应的装箱明细信息
        if (sendDatailist == null || sendDatailist.isEmpty()) {
            if(sendDatailist == null){
                sendDatailist = new ArrayList<SendDetail>();
            }
            SendInfoDto sendInfoDto = new SendInfoDto();
            sendInfoDto.setBoxCode(boxCode);
            com.jd.etms.erp.service.domain.BaseEntity<List<SendInfoDto>> baseEntity = supportProxy.getSendDetails(sendInfoDto);
            if (baseEntity != null && baseEntity.getResultCode() > 0) {
                List<SendInfoDto> datas = baseEntity.getData();
                if (datas != null && !datas.isEmpty()) {
                    //根据箱号判断终端箱子的正逆向
                    Integer businessType = BusinessUtil.isReverseBoxCode(boxCode) ? Constants.BUSSINESS_TYPE_REVERSE : Constants.BUSSINESS_TYPE_POSITIVE;
                    for (SendInfoDto dto : datas) {
                        SendDetail dsendDatail = new SendDetail();
                        dsendDatail.setBoxCode(dto.getBoxCode());

                        dsendDatail.setWaybillCode(dto.getWaybillCode());
                        dsendDatail.setPackageBarcode(dto.getPackageBarcode());

                        if (WaybillUtil.isSurfaceCode(dto.getPackageBarcode())) {
                            BaseEntity<PickupTask> pickup = null;
                            try {
                                pickup = this.waybillPickupTaskApi.getDataBySfCode(WaybillUtil.getWaybillCode(dto.getPackageBarcode()));
                            } catch (Exception e) {
                                this.logger.error("调用取件单号信息ws接口异常");
                            }
                            if (pickup != null && pickup.getData() != null) {
                                dsendDatail.setPickupCode(pickup.getData().getPickupCode());
                                dsendDatail.setWaybillCode(pickup.getData().getSurfaceCode());
                            }
                            if(WaybillUtil.isWaybillCode(dto.getPackageBarcode())){//FIXME:这里只是针对取件单的临时更改,应当从运单获得取件单包裹明细进行组装2018-07-17 黄亮 已做stash save
                                dsendDatail.setPackageBarcode(dto.getPackageBarcode()+"-1-1-");
                            }
                        }
                        dsendDatail.setCreateUser(dto.getOperatorName());
                        dsendDatail.setCreateUserCode(dto.getOperatorId());
                        dsendDatail.setOperateTime(dto.getHandoverDate());
                        dsendDatail.setSendType(businessType);
                        if (dsendDatail.getPackageBarcode() != null) {
                            dsendDatail.setPackageNum(BusinessUtil.getPackNumByPackCode(dsendDatail.getPackageBarcode()));
                        } else {
                            dsendDatail.setPackageNum(1);
                        }
                        dsendDatail.setIsCancel(OPERATE_TYPE_CANCEL_L);
                        sendDatailist.add(dsendDatail);
                    }
                } else {
                    logger.info("调用tms取站点箱子明细接口返回信息为空"
                            + baseEntity.getResultCode());
                }
            }
        }
        return sendDatailist;
    }

    @Override
    public List<SendDetail> queryBySendCodeAndSendType(String sendCode, Integer sendType) {
        SendDetail queryDetail = new SendDetail();
        queryDetail.setSendCode(sendCode);
        queryDetail.setSendType(sendType);
        return this.sendDatailDao.queryBySendCodeAndSendType(queryDetail);
    }

    @Override
    public Integer cancelDelivery(SendDetail sendDetail) {
        return this.sendDatailDao.cancelDelivery(sendDetail);
    }

    @Override
    public DeliveryResponse dealWithSendBatch(SendM sendM) {
        try {
            Sorting sorting = new Sorting();
            String sendCode = sendM.getSendCode();
            sorting.setBsendCode(sendCode);
            sorting.setCreateSiteCode(sendM.getCreateSiteCode());
            List<Sorting> list = tSortingService.findByBsendCode(sorting);
            if (list == null || list.isEmpty()) {
                return new DeliveryResponse(DeliveryResponse.CODE_Delivery_NO_MESAGE,
                        DeliveryResponse.MESSAGE_Delivery_NO_BATCH);
            }
            String rsiteCode = null;
            for (Sorting tSorting : list) {
                List<SendM> sendMList = new ArrayList<SendM>();
                SendM nsendM;
                nsendM = (SendM) sendM.clone();
                nsendM.setBoxCode(tSorting.getBoxCode());
                nsendM.setSendCode(getBatchCode(sendCode, tSorting));
                nsendM.setReceiveSiteCode(tSorting.getReceiveSiteCode());
                sendMList.add(nsendM);
                DeliveryResponse response = dellDeliveryMessage(SendBizSourceEnum.BATCH_OLD_PACKAGE_SEND, sendMList);
                if (!response.getCode().equals(DeliveryResponse.CODE_OK)) {
                    rsiteCode = rsiteCode + "," + String.valueOf(tSorting.getReceiveSiteCode());
                }
            }
            if (rsiteCode != null) {
                return new DeliveryResponse(DeliveryResponse.CODE_Delivery_ERROR,
                        rsiteCode + "站点箱号" + DeliveryResponse.MESSAGE_Delivery_ERROR);
            }
        } catch (Exception e) {
            logger.error("dealWithSendBatch处理异常", e);
            return new DeliveryResponse(DeliveryResponse.CODE_Delivery_ERROR,
                    DeliveryResponse.MESSAGE_Delivery_ERROR);

        }

        return new DeliveryResponse(DeliveryResponse.CODE_OK,
                DeliveryResponse.MESSAGE_OK);
    }

    private String getBatchCode(String sendCode, Sorting tSorting) {
        int maxlength = 32;
        String code = tSorting.getCreateSiteCode() +
                Constants.SEPARATOR_HYPHEN +
                tSorting.getReceiveSiteCode() +
                Constants.SEPARATOR_HYPHEN +
                sendCode;
        //+getTimeCode();
        if (code.length() > maxlength) {
            code = code.substring(0, maxlength);
        }
        return code;
    }

    @Override
    public DeliveryResponse autoBatchSend(List<SendM> sendMList) {
        String rsiteCode = null;
        for (SendM sendM : sendMList) {
            List<SendM> sendMs = new ArrayList<SendM>();
            SendM nsendM;
            try {
                String[] boxs = sendM.getBoxCode().split(",");
                for (String boxCode : boxs) {
                    nsendM = (SendM) sendM.clone();
                    nsendM.setBoxCode(boxCode);
                    sendMs.add(nsendM);
                }

                DeliveryResponse response = dellDeliveryMessage(SendBizSourceEnum.BATCH_OLD_PACKAGE_SEND, sendMs);
                if (!response.getCode().equals(DeliveryResponse.CODE_OK)) {
                    rsiteCode = rsiteCode + "," + String.valueOf(sendM.getReceiveSiteCode());
                }
            } catch (Exception e) {
                e.printStackTrace();
                return new DeliveryResponse(
                        DeliveryResponse.CODE_Delivery_ERROR,
                        DeliveryResponse.MESSAGE_Delivery_ERROR);
            }

        }
        if (rsiteCode != null) {
            rsiteCode = rsiteCode.replaceAll("null,", "");
            return new DeliveryResponse(DeliveryResponse.CODE_Delivery_ERROR,
                    rsiteCode + "站点箱号"
                            + DeliveryResponse.MESSAGE_Delivery_ERROR);
        }
        return new DeliveryResponse(DeliveryResponse.CODE_OK,
                DeliveryResponse.MESSAGE_OK);
    }

    @Override
    public boolean sendDetailMQ(Task task) {
        //body中是批次号,号分割
        CallerInfo info = null;
        try{
            info = Profiler.registerInfo( "DMSWORKER.DeliveryServiceImpl.sendDetailMQ",false, true);
            String body = task.getBody();
            String arSendRegisterId = task.getKeyword2();
            if(StringUtils.isNotBlank(body)){
                String[] sendCodes = body.split(Constants.SEPARATOR_COMMA);
                for(String sendCode : sendCodes){
                    if(StringUtils.isNotBlank(sendCode)){
                        List<SendDetail> sendDetailList = sendDatailDao.querySendDetailBySendCode(sendCode);
                        if(null != sendDetailList && sendDetailList.size() > 0){
                            for(SendDetail sendDetail : sendDetailList){
                                //获取包裹明细
                                Message sendMessage = parseSendDetailToMessageOfAR(sendDetail,arSendDetailProducer.getTopic(),arSendRegisterId);
                                //this.logger.info("发送MQ["+sendMessage.getTopic()+"],业务ID["+sendMessage.getBusinessId()+"],消息主题: " + sendMessage.getText());
                                this.arSendDetailProducer.sendOnFailPersistent(sendMessage.getBusinessId(),sendMessage.getText());

                            }
                        }else{
                            logger.error("新发货明细MQ任务根据批次号获取发货明细为空,批次号："+sendCode);
                        }
                    }else{
                        logger.error("新发货明细MQ任务根据批次号为空,task_id:"+task.getId());
                    }
                }
            }else{
                logger.error("新发货明细MQ任务body为空,task_id:"+task.getId());
            }
            return true;
        }catch (Exception e){
            logger.error("新发货明细MQ任务处理失败:"+e.getMessage());
            Profiler.functionError(info);
            return false;
        }finally{
            Profiler.registerInfoEnd(info);
        }
    }

    /**
     * 包裹不全差异对比计算
     */
    public static interface PackageDiffrence {
        /**
         * 计算差异结果
         *
         * @param list 发货明细列表
         * @param isScheduleRequest 是否包含派车单
         * @return 差异明细结果列表
         */
        List<SendThreeDetail> compute(List<SendDetail> list, boolean isScheduleRequest);
    }

  /** 利用排序法计算差异 1：按运单号升序排序 */
  public static abstract class AbstructDiffrenceComputer implements PackageDiffrence {

    @Autowired private SendDatailDao sendDatailDao;

    @Autowired private WaybillCommonService waybillCommonService;

    @Autowired private SiteService siteService;

      @Autowired
      private WaybillQueryManager waybillQueryManager;


    @Override
    public List<SendThreeDetail> compute(List<SendDetail> list, boolean isScheduleRequest) {
      Collections.sort(
          list,
          new Comparator<SendDetail>() {
            @Override
            public int compare(SendDetail lhs, SendDetail rhs) {
              return lhs.getPackageBarcode().compareToIgnoreCase(rhs.getPackageBarcode());
            }
          });
      return computeUsePackage(list, isScheduleRequest);
    }

    /**
     * 只保留不全订单的包裹，因为PDA操作界面太小，只应该看不全订单
     *
     * @param list
     * @return
     */
    private final List<SendThreeDetail> computeUsePackage(
        List<SendDetail> list, boolean isScheduleRequest) {
      String lastWaybillCode = null;
      int scanCount = 0;
      int pacageSumShoudBe = 0;
      int hasDiff = 0;
      List<SendThreeDetail> diffrenceList = new ArrayList<SendThreeDetail>();
      for (SendDetail item : list) { // 遍历该箱的所有包裹
        // 包含派车单且发现包裹不齐，直接退出循环（派车单校验不要明细）
        if (isScheduleRequest && hasDiff > 0) {
          break;
        }
          //纯配外单支持缺量退备件库因此剔除
          String waybillCode = item.getWaybillCode();
          com.jd.bluedragon.common.domain.Waybill reverseWaybill = waybillCommonService.findByWaybillCode(waybillCode);
          if(reverseWaybill != null && StringUtils.isNotBlank(reverseWaybill.getWaybillSign())){
              BaseStaffSiteOrgDto site = siteService.getSite(item.getReceiveSiteCode());
              Integer spwms_type = Integer.valueOf(PropertiesHelper.newInstance().getValue("spwms_type"));
              if(BusinessUtil.isPurematch(reverseWaybill.getWaybillSign()) && spwms_type.equals(site.getSiteType())){
                  break;
              }
          }
        SendThreeDetail diff = new SendThreeDetail();
        diff.setBoxCode(item.getBoxCode());
        diff.setPackageBarcode(item.getPackageBarcode());
        diff.setMark(AbstructDiffrenceComputer.HAS_SCANED);
        diff.setIsWaybillFull(1);
        if (!item.getWaybillCode().equals(lastWaybillCode)) { // 初次验单 或 每验完一单货，下一单开始验时 进入分支
          // 1.上一单已集齐 则返回0， 并重新初始化 pacageSumShoudBe、scanCount
          // 2.上一单未集齐 则返回未扫描的包裹（即缺失包裹数）循环结束后会根据此判断是否集齐包裹
          hasDiff += invoke(pacageSumShoudBe, scanCount, diffrenceList);
          lastWaybillCode = item.getWaybillCode(); // 获取当前要验证的运单号
          pacageSumShoudBe =
              WaybillUtil.getPackNumByPackCode(item.getPackageBarcode()); // 根据运单中一个包裹的包裹号 获取包裹数量
          if (pacageSumShoudBe == 0) { // 特殊包裹号，包裹总数位是0时，从运单获取包裹总数

              BaseEntity<BigWaybillDto> entity = waybillQueryManager.getDataByChoice(lastWaybillCode, true, true, true, false);
              if(entity!= null && entity.getData() != null && entity.getData().getWaybill() != null){
                  pacageSumShoudBe = entity.getData().getWaybill().getGoodNumber() == null?0:entity.getData().getWaybill().getGoodNumber();
              }
          }

          scanCount = 0;
        }
        ++scanCount; // 扫描计数器：1.如包裹全齐 则等于包裹总数量 2.如中间出现不齐的单，则等于不齐的单中已扫描的包裹
        diffrenceList.add(
            diff); // 每次循环均加入结果diffrenceList，如中间某单的包裹不齐，会在invoke中将不齐的单子加入diffrenceList 以便最终结算时
        // 获取该单的包裹（代码中该单无论缺几个，都返回该单的所有包裹）
      }
      hasDiff +=
          invoke(pacageSumShoudBe, scanCount, diffrenceList); // 遍历完成后，对该箱最后一单的未集齐包裹做处理，如最后一单已齐返回 0
      if (hasDiff > 0) { // hasDiff>0 则未集齐 需移除所有集齐的包裹 只保留未集齐的包裹 并封装list返回pda显示
        List<SendThreeDetail> targetList = removeFullPackages(diffrenceList);
        Integer createSiteCode = list.get(0).getCreateSiteCode();
        Integer receiveSiteCode = list.get(0).getReceiveSiteCode();
        return setSortingBoxCode(createSiteCode, receiveSiteCode, targetList);
      } else {
        return null;
      }
    }

    /**
     * 设置未发货包裹的箱号[便于现场判断哪个已分拣的箱子未发货]！！！！ 1：已分拣包裹的箱号显示分拣箱号 2：未分拣包裹的箱号显示空
     *
     * @param createSiteCode 发货站点
     * @param receiveSiteCode 收货站点
     * @param list 差异列表 差异列表
     * @return
     */
    private final List<SendThreeDetail> setSortingBoxCode(
        Integer createSiteCode, Integer receiveSiteCode, List<SendThreeDetail> list) {
      if (null == list || list.isEmpty()) return list;
      List<SendThreeDetail> targetList = new ArrayList<SendThreeDetail>(list.size());
      for (SendThreeDetail item : list) {
        if (item.getMark().equals(AbstructDiffrenceComputer.HAS_SCANED)) {
          targetList.add(item);
        } else {
          targetList.addAll(
              getUnSendPackages(createSiteCode, receiveSiteCode, item.getPackageBarcode()));
        }
      }
      return targetList;
    }

    /**
     * 获取未发货包裹【若已分拣，则箱号显示已分拣箱号，否中央电视台显示空箱号表示未分拣】
     *
     * @param createSiteCode 发货站点
     * @param receiveSiteCode 接收站点
     * @param packageCode 包裹号
     * @return
     */
    private final List<SendThreeDetail> getUnSendPackages(
        Integer createSiteCode, Integer receiveSiteCode, String packageCode) {
      SendDetail sendDatail = new SendDetail();
      sendDatail.setPackageBarcode(packageCode);
      sendDatail.setCreateSiteCode(createSiteCode);
      sendDatail.setReceiveSiteCode(receiveSiteCode);
      sendDatail.setIsCancel(0);
      List<SendDetail> resultList = sendDatailDao.querySendDatailsBySelective(sendDatail);
      List<SendThreeDetail> list = new ArrayList<SendThreeDetail>(1);
      if (resultList != null && !resultList.isEmpty()) {
        for (SendDetail rendDatail : resultList) {
          SendThreeDetail mSend = new SendThreeDetail();
          mSend.setPackageBarcode(packageCode);
          mSend.setBoxCode(rendDatail.getBoxCode());
          mSend.setMark(AbstructDiffrenceComputer.NO_SCANEd);
          list.add(mSend);
        }
      } else {
        SendThreeDetail mSend = new SendThreeDetail();
        mSend.setPackageBarcode(packageCode);
        mSend.setBoxCode("");
        mSend.setMark(AbstructDiffrenceComputer.NO_SCANEd);
        list.add(mSend);
      }
      return list;
    }

    /**
     * 从列表中去除已完全扫描[一单多件齐全]的包裹（按运单为单）
     *
     * @param list
     * @return
     */
    private final List<SendThreeDetail> removeFullPackages(List<SendThreeDetail> list) {
      if (null != list) {
        for (int index = 0; index < list.size(); ++index) {
          /*去除全的包裹*/
          if (list.get(index).getIsWaybillFull() > 0) {
            list.remove(index--);
          }
        }
        return list;
      } else {
        return null;
      }
    }

    public abstract int invoke(int counter, int scanCount, List<SendThreeDetail> diffrenceList);

    public static final String HAS_SCANED = "已扫描";

    public static final String NO_SCANEd = "未扫描";
    /** 查询未扫描 */
    public static final Integer QUERY_NOSCANED = 1;
    /** 查询已扫描 */
    public static final Integer QUERY_HASSCANED = 2;
    /** 查询所有 */
    public static final Integer QUERY_ALL = 3;
    }

    /**
     * 正向发货差异对比
     * 1：包括自营发货及三方站点发货
     * 2：利用包裹号进行对比【差异结果不准确-当条码包裹号与运单中的不一致时】
     */
    public static class ForwardSendDiffrence extends AbstructDiffrenceComputer {
        private final Logger logger = Logger.getLogger(ForwardSendDiffrence.class);
        @Autowired
        private WaybillCommonService waybillCommonService;

        @Override
        public int invoke(int counter, int scanCount, List<SendThreeDetail> diffrenceList) {
            int hasDiff = 0;
            if (counter != scanCount) {/* 有差异*/
                com.jd.bluedragon.common.domain.Waybill waybill = waybillCommonService.findWaybillAndPack(SerialRuleUtil.getWaybillCode(diffrenceList.get(diffrenceList.size() - 1).getPackageBarcode()));
                List<String> geneList = null;
                if (null != waybill && null != waybill.getPackList() && waybill.getPackList().size() > 0) {
                    geneList = new ArrayList<String>(waybill.getPackList().size());
                    for (Pack p : waybill.getPackList()) {
                        geneList.add(p.getPackCode());
                    }
                }else{
                    logger.info("生成全部包裹号：" + diffrenceList.get(diffrenceList.size() - 1).getPackageBarcode());
                    geneList = WaybillUtil.generateAllPackageCodes(diffrenceList.get(diffrenceList.size() - 1).getPackageBarcode());
                }

                for (int index = scanCount; index > 0; --index) {
                    geneList.remove(diffrenceList.get(diffrenceList.size() - index).getPackageBarcode());
                }
                List<SendThreeDetail> noScanList = new ArrayList<SendThreeDetail>(geneList.size());
                for (String packageCode : geneList) {
                    SendThreeDetail noScanDetail = new SendThreeDetail();
                    noScanDetail.setBoxCode(diffrenceList.get(diffrenceList.size() - 1).getBoxCode());
                    noScanDetail.setPackageBarcode(packageCode);
                    noScanDetail.setMark(AbstructDiffrenceComputer.NO_SCANEd);
                    noScanDetail.setIsWaybillFull(0);
                    noScanList.add(noScanDetail);
                    ++hasDiff;
                }
                if (noScanList.size() > 0) {
                    for (int index = scanCount; index > 0; --index) {
                        diffrenceList.get(diffrenceList.size() - index).setIsWaybillFull(0);
                    }
                }
                diffrenceList.addAll(noScanList);
            }
            return hasDiff;
        }

    }

    /**
     * 逆向发货差异对比
     * 1：针对退库房订单进行处理
     * 2：利用发货包裹明细与运单中包裹进行比对
     */
    public static class ReverseSendDiffrence extends AbstructDiffrenceComputer {

        private static final Log logger = LogFactory.getLog(ReverseSendDiffrence.class);
        @Autowired
        private WaybillCommonService waybillCommonService;

        @Override
        public int invoke(int counter, int scanCount, List<SendThreeDetail> diffrenceList) {
            int hasDiff = 0;
            if (counter != scanCount) {/* 有差异*/

                com.jd.bluedragon.common.domain.Waybill waybill = waybillCommonService.findWaybillAndPack(SerialRuleUtil.getWaybillCode(diffrenceList.get(diffrenceList.size() - 1).getPackageBarcode()));
                List<String> geneList = null;
                if (null != waybill && null != waybill.getPackList() && waybill.getPackList().size() > 0) {
                    if(BusinessUtil.isSick(waybill.getWaybillSign())){//病单则直接返回0 不验证包裹是否集齐
                        return 0;
                    }
                    logger.info("运单中包裹数量为" + waybill.getPackList().size());
                    geneList = new ArrayList<String>(waybill.getPackList().size());
                    for (Pack p : waybill.getPackList()) {
                        geneList.add(p.getPackCode());
                        logger.info("运单中包裹为" + p.getPackCode());
                    }
                } else {
                    logger.info("运单中没有包裹");
                    geneList = WaybillUtil.generateAllPackageCodes(diffrenceList.get(diffrenceList.size() - 1).getPackageBarcode());
                }
                for (int index = scanCount; index > 0; --index) {
                    geneList.remove(diffrenceList.get(diffrenceList.size() - index).getPackageBarcode());
                }
                List<SendThreeDetail> noScanList = new ArrayList<SendThreeDetail>(geneList.size());
                for (String packageCode : geneList) {
                    SendThreeDetail noScanDetail = new SendThreeDetail();
                    noScanDetail.setBoxCode(diffrenceList.get(diffrenceList.size() - 1).getBoxCode());
                    noScanDetail.setPackageBarcode(packageCode);
                    noScanDetail.setMark(AbstructDiffrenceComputer.NO_SCANEd);
                    noScanDetail.setIsWaybillFull(0);
                    logger.info("未扫描" + noScanDetail.getPackageBarcode());
                    noScanList.add(noScanDetail);
                    ++hasDiff;
                }
                if (noScanList.size() > 0) {
                    for (int index = scanCount; index > 0; --index) {
                        diffrenceList.get(diffrenceList.size() - index).setIsWaybillFull(0);
                    }
                }
                diffrenceList.addAll(noScanList);
            }
            return hasDiff;
        }
    }

    /**
     * 原包发货[前提条件]1：箱号、原包没有发货;
     * （1）若原包发货，则补写分拣任务；若箱号发货则更新SEND_D状态及批次号
     * （2）写SEND_M表
     * （3）推送运单状态及回传周转箱
     * （4）对中转发货写入补全SEND_D任务
     *
     * @param domain 发货对象
     * @return 1：发货成功  2：发货失败  4：需要用户确认
     */
    @Override
    public SendResult autoPackageSend(SendM domain, UploadData uploadData) {
        CallerInfo info = Profiler.registerInfo("DMSWEB.DeliveryServiceImpl.AtuopackageSend", Constants.UMP_APP_NAME_DMSWEB,false, true);
        try {
            if (logger.isInfoEnabled()) {
                logger.info("execute device auto send,parameter is :" + JsonHelper.toJson(domain));
            }

            /*
                不在区分分拣机自动发货和龙门架自动发货逻辑，
                新的抽象方式为: 按原包发货  和  按箱号进行发货
                原来逻辑:
                    分拣机 = this.sortMachineAutoPackageSend(domain, uploadData)
                    龙门架 = this.scannerFrameAutoPackageSend(domain)
                @TIME 2019-01-22 18:30:06
             */
            return this.scannerFrameAutoPackageSend(domain, uploadData);

        } catch (Exception e) {
            Profiler.functionError(info);
            logger.error("一车一单自动发货异常，sendM：" + JsonHelper.toJson(domain), e);
            return new SendResult(SendResult.CODE_SERVICE_ERROR, SendResult.MESSAGE_SERVICE_ERROR);
        }finally {
            Profiler.registerInfoEnd(info);
        }
    }

    /**
     * 龙门架和分拣机共用自动发货逻辑
     *
     * @param domain
     * @return
     */
    private SendResult scannerFrameAutoPackageSend(SendM domain, UploadData uploadData) {
        // 根据箱号/包裹号 + 始发站点 + 目的站点获取发货记录
        SendM lastSendM = this.getRecentSendMByParam(domain.getBoxCode(), domain.getCreateSiteCode(), null, domain.getOperateTime());
        if (null != lastSendM) {
            SendResult result = this.checkIsEffectiveDelivery(domain, lastSendM);
            if (result != null) {
                return result;
            }
            // 多次发货 若上次发货未封车或封车时间在一小时内则取消上次发货
            this.autoMultiSendCancelLast(domain, lastSendM);
        }

        /* 如果是分拣机原包发货的话，需要补上验货任务 */
        if (uploadData.getSource() != null && uploadData.getSource() == 2) {
            if (WaybillUtil.isPackageCode(domain.getBoxCode())) {
                pushInspection(domain,null);
            }
            // 分拣机发货
            this.packageSend(SendBizSourceEnum.SORT_MACHINE_SEND, domain);
        } else {
            // 龙门架发货
            this.packageSend(SendBizSourceEnum.SCANNER_FRAME_SEND, domain);
        }

        return new SendResult(SendResult.CODE_OK, SendResult.MESSAGE_OK);
    }

    /**
     * 检测是否为有效发货
     * 检测条件：本次发货与上次发货同一批次且操作时间相差一分钟以上
     *
     * @param sendM
     * @param lastSendM
     * @return
     */
    private SendResult checkIsEffectiveDelivery(SendM sendM, SendM lastSendM) {
        if (StringUtils.isNotBlank(lastSendM.getSendCode())) {
            long operateTime = sendM.getOperateTime().getTime();
            long lastOperateTime = lastSendM.getOperateTime().getTime();
            // 同一批次 一分钟内发两次货 则自动丢弃本次发货
            if (lastSendM.getSendCode().equals(sendM.getSendCode()) && operateTime - lastOperateTime < DateHelper.ONE_MINUTES_MILLI) {
                return new SendResult(SendResult.CODE_SENDED, SendResult.MESSAGE_SENDED);
            }
        }
        return null;
    }

    /**
     * 多次发货若封车时间在当前时间一小时内或未封车,则取消上次发货
     *
     * @param domain
     */
    private void autoMultiSendCancelLast(SendM domain, SendM lastSendM) {
        String lastSendCode = lastSendM.getSendCode();
        if (StringUtils.isNotBlank(lastSendCode)) {
            if (!this.sendSealTimeIsOverOneHour(lastSendCode, domain.getOperateTime())) {
                // 未封车 直接取消上次发货
                this.dellCancelDeliveryMessage(getCancelSendM(lastSendM, domain), true);
            }
        }
    }

	@Override
	public List<SendDetail> getSendDetailsByBoxCode(String boxCode) {
        Box box = null;
        box = this.boxService.findBoxByCode(boxCode);
        if (box == null) {
            return null;
        }
        Integer yCreateSiteCode = box.getCreateSiteCode();
        Integer yReceiveSiteCode = box.getReceiveSiteCode();
        SendDetail tsendDatail = new SendDetail();
        tsendDatail.setBoxCode(boxCode);
        tsendDatail.setCreateSiteCode(yCreateSiteCode);
        tsendDatail.setReceiveSiteCode(yReceiveSiteCode);
        tsendDatail.setIsCancel(OPERATE_TYPE_CANCEL_L);
        List<SendDetail> sendDatailist = this.sendDatailDao
                .querySendDatailsBySelective(tsendDatail);
        return sendDatailist;
	}

    /**
     * 推验货任务
     * @param domain
     */
    private void pushInspection(SendM domain,String packageCode) {
        BaseStaffSiteOrgDto create = siteService.getSite(domain.getCreateSiteCode());
        String createSiteName = null != create ? create.getSiteName() : null;

        InspectionRequest inspection=new InspectionRequest();
        inspection.setUserCode(domain.getCreateUserCode());
        inspection.setUserName(domain.getCreateUser());
        inspection.setSiteCode(domain.getCreateSiteCode());
        inspection.setSiteName(createSiteName);
        inspection.setOperateTime(DateHelper.formatDateTime(new Date(domain.getOperateTime().getTime()-60000)));
        inspection.setBusinessType(Constants.BUSSINESS_TYPE_POSITIVE);
        if(packageCode != null){
            inspection.setPackageBarOrWaybillCode(packageCode);
        }else{
            inspection.setPackageBarOrWaybillCode(domain.getBoxCode());
        }

        TaskRequest request=new TaskRequest();
        request.setBusinessType(Constants.BUSSINESS_TYPE_POSITIVE);
        request.setKeyword1(String.valueOf(domain.getCreateUserCode()));
        if(packageCode != null){
            request.setKeyword2(packageCode);
        }else{
            request.setKeyword2(domain.getBoxCode());
        }
        request.setType(Task.TASK_TYPE_INSPECTION);
        request.setOperateTime(DateHelper.formatDateTime(new Date(domain.getOperateTime().getTime()-60000)));
        request.setSiteCode(domain.getCreateSiteCode());
        request.setSiteName(createSiteName);
        request.setUserCode(domain.getCreateUserCode());
        request.setUserName(domain.getCreateUser());
        //request.setBody();
        String eachJson = Constants.PUNCTUATION_OPEN_BRACKET
                + JsonHelper.toJson(inspection)
                + Constants.PUNCTUATION_CLOSE_BRACKET;
        Task task=this.taskService.toTask(request, eachJson);

        int result= this.taskService.add(task, true);
        if(logger.isDebugEnabled()){
            logger.debug("分拣机自动发货-验货任务插入条数:"+result+"条,请求参数:"+JsonHelper.toJson(task));
        }
    }

	@Override
	@JProfiler(jKey = "DMSWEB.DeliveryServiceImpl.doBoardDelivery", mState = {JProEnum.TP, JProEnum.FunctionError})
	public boolean doBoardDelivery(Task task) {
        logger.info("组板发货逐单发货开始：" + JsonHelper.toJson(task));
        SendM domain = JsonHelper.fromJson(task.getBody(), SendM.class);
        String boardCode = domain.getBoardCode();
        Response<List<String>> tcResponse = boardCombinationService.getBoxesByBoardCode(boardCode);
        logger.info("组板发货逐单发货查询板号明细：" + JsonHelper.toJson(tcResponse));
        if(tcResponse.getData() != null && !tcResponse.getData().isEmpty()){
            // 根据任务类型获取发货业务来源
            SendBizSourceEnum source = this.getBoardDeliveryBizSource(task.getType());
            for(String boxCode : tcResponse.getData()){
                domain.setSendMId(null);
                domain.setBoxCode(boxCode);
                if (StringUtils.isBlank(getSendedCode(domain))) {//未发过货的才执行发货
                    packageSend(source, domain);
                }
            }
            boardCombinationService.clearBoardCache(boardCode);//发货完成，删除组板时加的缓存
            logger.info("组板发货逐单发货执行完成：" + JsonHelper.toJson(domain));
        }else{
            logger.warn("组板发货,逐单发货查询板标明细出错,组板发货任务：" + JsonHelper.toJson(domain));
            logger.warn("组板发货,逐单发货查询板标明细出错，查询明细结果：" + JsonHelper.toJson(tcResponse));
        }
        //发货任务处理完毕，删除缓存
        redisManager.del(REDIS_PREFIX_BOARD_DELIVERY +domain.getBoardCode());
        return true;
	}

    /**
     * 根据任务类型获取发货业务来源
     *
     * @param taskType
     * @return
     */
    private SendBizSourceEnum getBoardDeliveryBizSource(Integer taskType) {
        if (Task.TASK_TYPE_ACARABILL_SEND_DELIVERY.equals(taskType)) {
            return SendBizSourceEnum.OFFLINE_BOARD_SEND;
        } else if (Task.TASK_TYPE_BOARD_SEND.equals(taskType)) {
            return SendBizSourceEnum.BOARD_SEND;
        } else {
            return SendBizSourceEnum.BOARD_SEND;
        }
    }

    /**
     * 按板取消发货任务
     * @param task 任务实体
     * @return
     */
    @JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWEB,jKey = "DMSWEB.DeliveryServiceImpl.doBoardDeliveryCancel", mState = {JProEnum.TP, JProEnum.FunctionError})
    public boolean doBoardDeliveryCancel(Task task){
        logger.info("按板取消发货开始：" + JsonHelper.toJson(task));
        SendM domain = JsonHelper.fromJson(task.getBody(), SendM.class);
        List<String> boxCodeList = sendMDao.selectBoxCodeByBoardCodeAndSendCode(domain);

        return doBoardDeliveryCancel(domain, boxCodeList);
    }

    /**
     * 按板取消发货任务：逐个箱子取消
     * @param domain
     * @param boxCodeList
     * @return
     */
    private boolean doBoardDeliveryCancel(SendM domain, List<String> boxCodeList){
        boolean isSuccess = true;
        if(boxCodeList!=null && !boxCodeList.isEmpty()){
            for(String boxCode : boxCodeList){
                try {
                    domain.setSendMId(null);
                    domain.setBoxCode(boxCode);
                    if (BusinessHelper.isBoxcode(boxCode)) {
                        domain.setReceiveSiteCode(null);
                    } else{
                        domain.setReceiveSiteCode(SerialRuleUtil.getReceiveSiteCodeFromSendCode(domain.getSendCode()));
                    }
                    dellCancelDeliveryMessage(domain, true);
                }catch(Exception e){
                    logger.warn(String.format("按板取消发货，取消包裹/箱号{0}失败,失败原因{1}",boxCode,e.getMessage()));
                    //如果任务处理过程中有异常，任务重跑
                    isSuccess = false;
                    continue;
                }
            }
        }else{
            logger.error("按板取消发货,查询sendm中的发货明细为空：" + JsonHelper.toJson(domain));
        }
        return isSuccess;
    }

    /**
     * 取消组板
     * 一车一单发货，扫描的是包裹号/箱号，则需要判断是否进行过组板，如果操作过，则需要从板中取消
     * 此处为了减少性能损耗，直接掉用取消组板的接口，组过板的直接取消
     * @param domain
     */
    private void boardCombinationCancel(SendM domain){
        BoardCombinationRequest request = new BoardCombinationRequest();
        request.setBoxOrPackageCode(domain.getBoxCode());
        request.setSiteCode(domain.getCreateSiteCode());
        request.setReceiveSiteCode(domain.getReceiveSiteCode());
        request.setUserCode(domain.getCreateUserCode());
        request.setUserName(domain.getCreateUser());
        BoardResponse response = null;
        CallerInfo info = Profiler.registerInfo("DMSWEB.DeliveryServiceImpl.boardCombinationCancel", Constants.UMP_APP_NAME_DMSWEB,false, true);
        try {
            response = boardCombinationService.boardCombinationCancel(request);
            String logInfo = "一车一单发货取消组板.包裹号/箱号:" + domain.getBoxCode() +
                    ",操作站点:" + domain.getCreateSiteCode() + ",板号:" + response.getBoardCode() +
                    ",取消组板结果：" + response.buildStatusMessages();
            if(logger.isInfoEnabled()) {
                logger.info(logInfo);
            }

            //记录cassandra日志
            addCassandraLog(domain.getBoxCode(),domain.getBoxCode(),logInfo);

        }catch(Exception e){
            Profiler.functionError(info);
            //取消组板异常
            logger.error("一车一单发货取消组板异常.包裹号/箱号:" + domain.getBoxCode() + ",操作站点:" + domain.getCreateSiteCode() +
                    "异常原因:" +e);
        }finally {
            Profiler.registerInfoEnd(info);
        }
    }

    /**
     *  查询发货记录
     * @param sendCode 批次号
     * @param createSiteCode 始发分拣中心
     * @param receiveSiteCode 目的分拣中心
     * @return
     */
    @Override
    public List<SendM> getSendMBySendCodeAndSiteCode(String sendCode, Integer createSiteCode, Integer receiveSiteCode){
        SendM queryParam = new SendM();
        queryParam.setSendCode(sendCode);
        queryParam.setCreateSiteCode(createSiteCode);
        queryParam.setReceiveSiteCode(receiveSiteCode);
        /**查询箱子发货记录*/
        List<SendM> sendMList = this.sendMDao.selectBySendSiteCode(queryParam);
        return sendMList;
    }

    @Override
    public DeliveryResponse dealJpWaybill(Integer dmsSiteCode, String waybillCode) {

        DeliveryResponse response = new DeliveryResponse(JdResponse.CODE_OK,JdResponse.MESSAGE_OK);
        Integer preSiteCode = null;
        Integer destinationDmsId = null;
        com.jd.bluedragon.common.domain.Waybill waybill = waybillCommonService.findWaybillAndPack(waybillCode);
        if(waybill != null && waybill.getWaybillSign() != null){
            //是否是金鹏订单
            if(BusinessUtil.isPerformanceOrder(waybill.getWaybillSign())){
                //预分拣站点
                preSiteCode = waybill.getSiteCode();
                BaseStaffSiteOrgDto bDto = siteService.getSite(preSiteCode);
                if(bDto != null && bDto.getDmsId() != null){
                    //末级分拣中心
                    destinationDmsId = bDto.getDmsId();
                }
                String dmsIds = PropertiesHelper.newInstance().getValue(PERFORMANCE_DMSSITECODE_SWITCH);
                String[] dmsCodes = dmsIds.split(",");
                List<String> dmsList = Arrays.asList(dmsCodes);
                if(dmsList != null && dmsList.size() > 0 && dmsList.contains(String.valueOf(destinationDmsId)) ||
                        Strings.isNullOrEmpty(PropertiesHelper.newInstance().getValue(PERFORMANCE_DMSSITECODE_SWITCH))){
                    //登陆人操作机构是否是末级分拣中心
                    if(dmsSiteCode.equals(destinationDmsId)){
                        //运单是否发货
                        Boolean isCanSend = storagePackageMService.checkWaybillCanSend(waybillCode,waybill.getWaybillSign());
                        if(!isCanSend){
                            response = new DeliveryResponse(DeliveryResponse.CODE_Delivery_SAVE,DeliveryResponse.MESSAGE_Delivery_SAVE);
                        }
                    }
                }
            }
        }
        return response;
    }

    /**
     * 校验B网运单是否确认了耗材包装服务 added by hanjiaxing3 2018.10.16
     * @param sendM 发货数据
     * @return true:确认了包装，不拦截 false:拦截
     */
    private Boolean checkWaybillConsumable(SendM sendM){

        logger.info("B网包装耗材确认拦截开始...");
        Waybill waybill = null;
        try {
            //判断快运发货是够是原包发货，原包发货boxCode为包裹号
            if (WaybillUtil.isPackageCode(sendM.getBoxCode()) && ! WaybillUtil.isSurfaceCode(sendM.getBoxCode())) {
                String waybillCode = WaybillUtil.getWaybillCode(sendM.getBoxCode());
                if (StringHelper.isNotEmpty(waybillCode)) {
                    WChoice wChoice = new WChoice();
                    wChoice.setQueryWaybillS(true);
                    wChoice.setQueryWaybillC(true);
                    //获取运单信息
                    BaseEntity<BigWaybillDto> baseEntity = this.waybillQueryManager.getDataByChoice(waybillCode, wChoice);
                    if (baseEntity != null && baseEntity.getData() != null && baseEntity.getData().getWaybill() != null) {
                        this.logger.info("运单号【 " + waybillCode + "】调用运单数据成功！");

                        waybill = baseEntity.getData().getWaybill();
                        String waybillSign = waybill.getWaybillSign();
                        //判断waybillSign是够支持包装耗材服务，支持才判断是否确认
                        if (BusinessHelper.isNeedConsumable(waybillSign)) {
                            //返回确认结果
                            return waybillConsumableRecordService.isConfirmed(waybillCode);
                        }
                    } else {
                        //无运单数据
                        logger.warn(waybillCode + "对应的运单信息为空！");
                    }
                } else {
                    //运单号转换失败
                    logger.warn(sendM.getBoxCode() + "转换运单号失败！");
                }
            }
        } catch (Exception e) {
            logger.error("查询运单是否已经确认耗材失败，运单号：" + sendM.getBoxCode(), e);
        }
        return true;
    }


    /**
     * 检查 批次中是否同时存在已集齐和未集齐（支持半退的）的包裹
     *
     * 只有退仓是才会去检测
     * @param tDeliveryResponse
     * @return
     */
    private ThreeDeliveryResponse checkReversePartSend(List<SendThreeDetail> tDeliveryResponse , List<SendDetail> allList){

        ThreeDeliveryResponse response = new ThreeDeliveryResponse();
        response.setCode(DeliveryResponse.CODE_OK);
        response.setMessage(DeliveryResponse.MESSAGE_OK);
        //tDeliveryResponse 此列表中为未集齐
        //allList 此次发货全部明细

        //提取未集齐的运单号 和 全部发货的运单号
        Set<String> partWaybills = new HashSet<String>();
        Map<String,List<String>> waybills = new HashMap<String, List<String>>();


        Set<String> needRemoveWaybill = new HashSet<String>();
        Set<String> needRemoveBox = new HashSet<String>();

        if(tDeliveryResponse!=null && !tDeliveryResponse.isEmpty()){
            //存在未集齐发货记录

            //只有退仓才会有提示
            if(allList!=null && !allList.isEmpty()){
                Integer reverseSiteCode = allList.get(0).getReceiveSiteCode();
                BaseStaffSiteOrgDto site = baseMajorManager.getBaseSiteBySiteId(reverseSiteCode);
                if(site==null || site.getSiteType()==null || !site.getSiteType().toString().equals(PropertiesHelper.newInstance().getValue("wms_type"))){
                    return response;
                }
            }else{
                return response;
            }

            for(SendThreeDetail std :tDeliveryResponse){
                //此sendD是拼装的 箱子里其他拼装出来的未扫描数据中没有箱号字段
                if(StringUtils.isBlank(std.getBoxCode()) || BusinessUtil.isBoxcode(std.getBoxCode())){
                    continue;
                }
                partWaybills.add(WaybillUtil.getWaybillCode(std.getPackageBarcode()));
            }

            for(SendDetail sd :allList){
                //跳过装箱数据
                if(StringUtils.isNotBlank(sd.getBoxCode()) && BusinessUtil.isBoxcode(sd.getBoxCode())){
                    needRemoveBox.add(sd.getBoxCode());
                    continue;
                }
                String waybillCode = WaybillUtil.getWaybillCode(sd.getPackageBarcode());
                if(waybills.containsKey(waybillCode)){
                    waybills.get(waybillCode).add(sd.getPackageBarcode());
                }else{
                    List<String> ps = new ArrayList<String>();
                    ps.add(sd.getPackageBarcode());
                    waybills.put(waybillCode,ps);
                }
            }

            if(partWaybills.size()==0){
                //无未集齐数据
                return response;
            }

            if(waybills.size()>partWaybills.size()){
                //1 发货明细中是否存在其他已集齐的运单
                //找出存在半退标的运单 提示剔除掉 剩下未集齐暂时忽略，通过下次提交发货时再去校验
                for(String waybillCode : partWaybills){
                    BaseEntity<BigWaybillDto> baseEntity = waybillQueryManager.getDataByChoice(waybillCode,true,false,false,false);
                    if(baseEntity.getResultCode() == 1 && baseEntity.getData()!=null && baseEntity.getData().getWaybill()!=null){
                        if(BusinessUtil.isPartReverse(baseEntity.getData().getWaybill().getWaybillSign())){
                            //未集齐包裹中存在 支持半退运单 提示剔除掉
                            needRemoveWaybill.add(waybillCode);
                        }
                    }
                }



            }else if(waybills.size() == partWaybills.size()){
                //全部都没有半退标志
                boolean notExistPart = false;
                //2 全部为半退 找出不支持半退的运单 提示剔除
                for(String waybillCode : partWaybills){
                    BaseEntity<BigWaybillDto> baseEntity = waybillQueryManager.getDataByChoice(waybillCode,true,false,false,false);
                    if(baseEntity.getResultCode() == 1 && baseEntity.getData()!=null && baseEntity.getData().getWaybill()!=null){
                        if(!BusinessUtil.isPartReverse(baseEntity.getData().getWaybill().getWaybillSign())){
                            //未集齐包裹中存在 不支持半退运单 提示剔除掉
                            needRemoveWaybill.add(waybillCode);
                        }else{
                            //存在一个打标的则更更新标志
                            notExistPart = !notExistPart?true:notExistPart;
                        }
                    }
                }
                //如果都没有半退则按原集齐提示
                if(!notExistPart){
                    return response;
                }


                //如果全部未可半退的需要给出提示 该批次号对应运单均为半退至仓，确认发货？
                if(needRemoveWaybill.isEmpty() && needRemoveBox.isEmpty()){
                    response.setCode(DeliveryResponse.CODE_Delivery_PART_SEND);
                    response.setMessage(DeliveryResponse.MESSAGE_Delivery_PART_SEND);
                }else{
                    //如果存在箱子数据 还要追加剔除箱子
                    if(!needRemoveBox.isEmpty()){
                        needRemoveWaybill.addAll(needRemoveBox);
                    }
                }


            }

            if(!needRemoveWaybill.isEmpty()){
                response.setCode(DeliveryResponse.CODE_Delivery_PART_SEND_ERROR);
                response.setMessage(DeliveryResponse.MESSAGE_Delivery_PART_SEND_ERROR);
                List<SendThreeDetail> needRemoveDestails = new ArrayList<SendThreeDetail>();
                response.setData(needRemoveDestails);
                for(String waybillCode : needRemoveWaybill){
                    if(waybills.get(waybillCode)==null){
                        //此时为箱号
                        SendThreeDetail needRemoveDestail = new SendThreeDetail();
                        needRemoveDestail.setPackageBarcode(waybillCode);
                        needRemoveDestails.add(needRemoveDestail);
                    }else{
                        for (String packageCode : waybills.get(waybillCode)){
                            SendThreeDetail needRemoveDestail = new SendThreeDetail();
                            needRemoveDestail.setPackageBarcode(packageCode);
                            needRemoveDestails.add(needRemoveDestail);
                        }
                    }

                }
            }


        }


        return response;
    }


    /**
     * 快运发货判断是否需要提示录入检疫证票号
     * @param sendM 发货数据
     * @return
     */
    private Boolean isWaybillNeedAddQuarantine(SendM sendM) {
        logger.info("查询是否需要录入检疫证票号...");
        String waybillCode = WaybillUtil.getWaybillCode(sendM.getBoxCode());
        Integer siteCode = sendM.getCreateSiteCode();

        if (StringUtils.isBlank(waybillCode) || siteCode == null) {
            return false;
        }

        return coldChainQuarantineManager.isWaybillNeedAddQuarantine(waybillCode,siteCode);
    }
}
