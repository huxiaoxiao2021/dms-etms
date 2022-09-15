package com.jd.bluedragon.distribution.send.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.jd.bluedragon.Constants;
import com.jd.bluedragon.KeyConstants;
import com.jd.bluedragon.UmpConstants;
import com.jd.bluedragon.common.domain.Pack;
import com.jd.bluedragon.common.domain.WaybillCache;
import com.jd.bluedragon.common.service.WaybillCommonService;
import com.jd.bluedragon.common.utils.CacheKeyConstants;
import com.jd.bluedragon.common.utils.ProfilerHelper;
import com.jd.bluedragon.configuration.ucc.UccPropertyConfiguration;
import com.jd.bluedragon.core.base.*;
import com.jd.bluedragon.core.hint.constants.HintArgsConstants;
import com.jd.bluedragon.core.hint.constants.HintCodeConstants;
import com.jd.bluedragon.core.hint.service.HintService;
import com.jd.bluedragon.core.jmq.producer.DefaultJMQProducer;
import com.jd.bluedragon.core.jsf.dms.GroupBoardManager;
import com.jd.bluedragon.core.redis.service.RedisManager;
import com.jd.bluedragon.distribution.abnormal.domain.DmsOperateHintTrack;
import com.jd.bluedragon.distribution.abnormal.service.DmsOperateHintService;
import com.jd.bluedragon.distribution.api.JdResponse;
import com.jd.bluedragon.distribution.api.request.*;
import com.jd.bluedragon.distribution.api.request.box.BoxReq;
import com.jd.bluedragon.distribution.api.response.BoardResponse;
import com.jd.bluedragon.distribution.api.response.CheckBeforeSendResponse;
import com.jd.bluedragon.distribution.api.response.DeliveryResponse;
import com.jd.bluedragon.distribution.api.response.SortingResponse;
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
import com.jd.bluedragon.distribution.box.constants.BoxTypeEnum;
import com.jd.bluedragon.distribution.box.domain.Box;
import com.jd.bluedragon.distribution.box.domain.BoxRelation;
import com.jd.bluedragon.distribution.box.service.BoxRelationService;
import com.jd.bluedragon.distribution.box.service.BoxService;
import com.jd.bluedragon.distribution.busineCode.sendCode.service.SendCodeService;
import com.jd.bluedragon.distribution.businessIntercept.constants.Constant;
import com.jd.bluedragon.distribution.businessIntercept.dto.SaveInterceptMsgDto;
import com.jd.bluedragon.distribution.businessIntercept.helper.BusinessInterceptConfigHelper;
import com.jd.bluedragon.distribution.businessIntercept.service.IBusinessInterceptReportService;
import com.jd.bluedragon.distribution.coldchain.domain.ColdChainSend;
import com.jd.bluedragon.distribution.coldchain.service.ColdChainSendService;
import com.jd.bluedragon.distribution.command.JdResult;
import com.jd.bluedragon.distribution.consumable.service.WaybillConsumableRecordService;
import com.jd.bluedragon.distribution.cyclebox.domain.BoxMaterialRelationEnum;
import com.jd.bluedragon.distribution.cyclebox.domain.BoxMaterialRelationMQ;
import com.jd.bluedragon.distribution.delivery.IDeliveryOperationService;
import com.jd.bluedragon.distribution.departure.service.DepartureService;
import com.jd.bluedragon.distribution.funcSwitchConfig.FuncSwitchConfigEnum;
import com.jd.bluedragon.distribution.funcSwitchConfig.domain.FuncSwitchConfigAllPureDto;
import com.jd.bluedragon.distribution.funcSwitchConfig.service.FuncSwitchConfigService;
import com.jd.bluedragon.distribution.economic.domain.EconomicNetException;
import com.jd.bluedragon.distribution.economic.service.IEconomicNetService;
import com.jd.bluedragon.distribution.external.constants.BoxStatusEnum;
import com.jd.bluedragon.distribution.external.constants.OpBoxNodeEnum;
import com.jd.bluedragon.distribution.goodsLoadScan.dao.GoodsLoadScanRecordDao;
import com.jd.bluedragon.distribution.goodsLoadScan.domain.GoodsLoadScanRecord;
import com.jd.bluedragon.distribution.handler.InterceptResult;
import com.jd.bluedragon.distribution.inspection.InspectionBizSourceEnum;
import com.jd.bluedragon.distribution.inspection.service.InspectionExceptionService;
import com.jd.bluedragon.distribution.inspection.service.WaybillPackageBarcodeService;
import com.jd.bluedragon.distribution.jsf.domain.InvokeResult;
import com.jd.bluedragon.distribution.jsf.domain.SortingCheck;
import com.jd.bluedragon.distribution.jsf.domain.SortingJsfResponse;
import com.jd.bluedragon.distribution.jsf.service.JsfSortingResourceService;
import com.jd.bluedragon.distribution.loadAndUnload.dao.LoadCarDao;
import com.jd.bluedragon.distribution.log.BusinessLogProfilerBuilder;
import com.jd.bluedragon.distribution.material.service.CycleMaterialNoticeService;
import com.jd.bluedragon.distribution.operationLog.domain.OperationLog;
import com.jd.bluedragon.distribution.operationLog.service.OperationLogService;
import com.jd.bluedragon.distribution.packageWeighting.dao.PackageWeightingDao;
import com.jd.bluedragon.distribution.packageWeighting.domain.BusinessTypeEnum;
import com.jd.bluedragon.distribution.packageWeighting.domain.PackageWeighting;
import com.jd.bluedragon.distribution.reverse.dao.ReverseSpareDao;
import com.jd.bluedragon.distribution.reverse.domain.ReverseSpare;
import com.jd.bluedragon.distribution.reverse.part.service.ReversePartDetailService;
import com.jd.bluedragon.distribution.seal.service.NewSealVehicleService;
import com.jd.bluedragon.distribution.send.dao.SendDatailDao;
import com.jd.bluedragon.distribution.send.dao.SendDatailReadDao;
import com.jd.bluedragon.distribution.send.dao.SendMDao;
import com.jd.bluedragon.distribution.send.domain.*;
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
import com.jd.bluedragon.distribution.third.domain.ThirdBoxDetail;
import com.jd.bluedragon.distribution.third.service.ThirdBoxDetailService;
import com.jd.bluedragon.distribution.transBillSchedule.service.TransBillScheduleService;
import com.jd.bluedragon.distribution.urban.service.TransbillMService;
import com.jd.bluedragon.distribution.ver.domain.Site;
import com.jd.bluedragon.distribution.ver.exception.SortingCheckException;
import com.jd.bluedragon.distribution.ver.service.SortingCheckService;
import com.jd.bluedragon.distribution.waybill.domain.WaybillStatus;
import com.jd.bluedragon.distribution.waybill.service.WaybillCacheService;
import com.jd.bluedragon.distribution.waybill.service.WaybillService;
import com.jd.bluedragon.distribution.weight.service.DmsWeightFlowService;
import com.jd.bluedragon.distribution.weightVolume.domain.ZeroWeightVolumeCheckEntity;
import com.jd.bluedragon.distribution.weightVolume.service.DMSWeightVolumeService;
import com.jd.bluedragon.dms.utils.BusinessUtil;
import com.jd.bluedragon.dms.utils.WaybillUtil;
import com.jd.bluedragon.external.crossbow.itms.domain.ItmsCancelSendCheckSendCodeDto;
import com.jd.bluedragon.external.crossbow.itms.domain.ItmsResponse;
import com.jd.bluedragon.external.crossbow.itms.service.TibetBizService;
import com.jd.bluedragon.utils.*;
import com.jd.bluedragon.utils.log.BusinessLogConstans;
import com.jd.dms.logger.external.BusinessLogProfiler;
import com.jd.dms.logger.external.LogEngine;
import com.jd.etms.erp.service.dto.SendInfoDto;
import com.jd.etms.vos.dto.CommonDto;
import com.jd.etms.vos.dto.SealCarDto;
import com.jd.etms.waybill.api.WaybillPickupTaskApi;
import com.jd.etms.waybill.domain.BaseEntity;
import com.jd.etms.waybill.domain.DeliveryPackageD;
import com.jd.etms.waybill.domain.PickupTask;
import com.jd.etms.waybill.domain.Waybill;
import com.jd.etms.waybill.dto.BigWaybillDto;
import com.jd.etms.waybill.dto.WChoice;
import com.jd.jim.cli.Cluster;
import com.jd.jmq.common.exception.JMQException;
import com.jd.jmq.common.message.Message;
import com.jd.ql.basic.dto.BaseStaffSiteOrgDto;
import com.jd.ql.basic.util.DateUtil;
import com.jd.ql.dms.common.constants.OperateDeviceTypeConstants;
import com.jd.ql.dms.common.constants.OperateNodeConstants;
import com.jd.tp.common.utils.Objects;
import com.jd.transboard.api.dto.OperatorInfo;
import com.jd.transboard.api.dto.Response;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import com.jd.ump.profiler.CallerInfo;
import com.jd.ump.profiler.proxy.Profiler;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

import java.math.BigDecimal;
import java.text.MessageFormat;
import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;

import static com.jd.bluedragon.Constants.KY_DELIVERY;

@Service("deliveryService")
public class DeliveryServiceImpl implements DeliveryService,DeliveryJsfService {

    private final Logger log = LoggerFactory.getLogger(DeliveryServiceImpl.class);

    private final int MAX_SHOW_NUM = 3;

    public static int WAYBILL_SPLIT_NUM = 100;

    private final String PERFORMANCE_DMSSITECODE_SWITCH = "performance.dmsSiteCode.switch";

    /**
     * 封车状态 已封车
     */
    private final Integer SEAL_CAR_STATUS_SEAL=10;

    /**
     * 封车状态 已解封车
     */
    private final Integer SEAL_CAR_STATUS_UNSEAL=20;

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
    private TerminalManager terminalManager;

    @Autowired
    private ThirdBoxDetailService thirdBoxDetailService;

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
    private IEconomicNetService economicNetService;

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
    private GroupBoardManager groupBoardManager;

    @Autowired
    private ReversePartDetailService reversePartDetailService;

    @Autowired
    private WaybillPackageBarcodeService waybillPackageBarcodeService;

    @Autowired
    @Qualifier("dmsColdChainSendWaybill")
    private DefaultJMQProducer dmsColdChainSendWaybill;

    @Autowired
    private ColdChainSendService coldChainSendService;

    @Autowired
    @Qualifier("redisClientCache")
    private Cluster redisClientCache;

    @Autowired
    private LogEngine logEngine;

    @Autowired
    private SendCodeService sendCodeService;

    @Autowired
    private CycleMaterialNoticeService cycleMaterialNoticeService;

    @Autowired
    private SortingCheckService sortingCheckService;

    @Autowired
    private UccPropertyConfiguration uccPropertyConfiguration;

    @Autowired
    private WaybillCacheService waybillCacheService;

    @Autowired
    private VosManager vosManager;

    /**
     * 按运单发货 redis 前缀
     */
    private static final String SEND_BY_WAYBILL_REDIS_PREFIX = "SEND_BY_WAYBILL_REDIS_PREFIX_";
    private static final String SEND_BY_WAYBILL_PACK_REDIS_PREFIX = "SEND_BY_WAYBILL_PACK_REDIS_PREFIX";

    @Autowired
    private GoodsLoadScanRecordDao goodsLoadScanRecordDao;

    @Autowired
    private LoadCarDao loadCarDao;

    @Autowired
    private WaybillService waybillService;

    @Autowired
    private BoxRelationService boxRelationService;

    @Autowired
    private PackageWeightingDao packageWeightingDao;

    @Autowired
    private FuncSwitchConfigService funcSwitchConfigService;

    @Autowired
    private WaybillTraceManager waybillTraceManager;

    @Autowired
    private SendDetailService sendDetailService;

    @Autowired
    private DMSWeightVolumeService dmsWeightVolumeService;

    @Autowired
    private IBusinessInterceptReportService businessInterceptReportService;

    @Autowired
    private BusinessInterceptConfigHelper businessInterceptConfigHelper;

    @Autowired
    private IDeliveryOperationService deliveryOperationService;

    @Autowired
    private TibetBizService tibetBizService;

    /**
     * 自动过期时间 30分钟
     */
    private final static long REDIS_CACHE_EXPIRE_TIME = 30 * 60;

    private final static String REDIS_CACHE_KEY = "PACKAGE-SEND-LOCK-";

    private static final int OPERATE_TYPE_REVERSE_SEND = 50;

    private static final int OPERATE_TYPE_FORWARD_SORTING = 1;

    private static final int OPERATE_TYPE_FORWARD_SEND = 2;

    private static final int OPERATE_TYPE_REVERSE_SORTING = 40;

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

    private final int BIG_SEND_NUM = 1000;
    /**
     * 发货任务类型-发货处理关系
     */
    private static final Map<Integer,SendTaskCategoryEnum> TASK_MAPPING_SUBTYPE_CATEGORY = new HashMap<Integer,SendTaskCategoryEnum>();
    static{
    	TASK_MAPPING_SUBTYPE_CATEGORY.put(Task.TASK_SUB_TYPE_BATCH_SEND, SendTaskCategoryEnum.BATCH_SEND);
    	TASK_MAPPING_SUBTYPE_CATEGORY.put(Task.TASK_SUB_TYPE_BOX_SEND, SendTaskCategoryEnum.BOX_SEND);
    	TASK_MAPPING_SUBTYPE_CATEGORY.put(Task.TASK_SUB_TYPE_BOX_TRANSIT_SEND, SendTaskCategoryEnum.BOX_TRANSIT_SEND);
    	TASK_MAPPING_SUBTYPE_CATEGORY.put(Task.TASK_SUB_TYPE_PACKAGE_SEND, SendTaskCategoryEnum.PACKAGE_SEND);
    }

    @Override
    public JdResult<CheckBeforeSendResponse> checkBeforeSend(DeliveryRequest deliveryRequest) {
        JdResult<CheckBeforeSendResponse> result = new JdResult<CheckBeforeSendResponse>();
        CheckBeforeSendResponse checkResponse = new CheckBeforeSendResponse();
        result.setData(checkResponse);
        checkResponse.setTipMessages(new ArrayList<String>());
        result.toSuccess();
        try {

            // 验证批次号是否合法
            com.jd.bluedragon.distribution.base.domain.InvokeResult<Boolean> sendChkResult = sendCodeService.validateSendCodeEffective(deliveryRequest.getSendCode());
            if (!sendChkResult.codeSuccess()) {
                result.toFail(sendChkResult.getMessage());
                return result;
            }

            // 围板箱校验
            if(BusinessUtil.isBoxcode(deliveryRequest.getBoxCode())){
                Box box = boxService.findBoxByCode(deliveryRequest.getBoxCode());
                if (Objects.nonNull(box) && BoxTypeEnum.TYPE_MS.getCode().equals(box.getType())) {
                    if (!Objects.isNull(deliveryRequest.getSiteCode()) && deliveryRequest.getSiteCode().equals(box.getReceiveSiteCode())) {
                        result.toFail(SortingResponse.CODE_29462,HintService.getHint(HintCodeConstants.CODE_COLD_CHAIN_SEND_BOX_ERROR));
                        return result;
                    }
                }
            }

            //不是快运发货，调用箱号验证
            if(!KY_DELIVERY.equals(deliveryRequest.getOpType())){
                DeliveryResponse boxCheckResponse = this.doCheckDeliveryInfo(deliveryRequest.getBoxCode(),
                        deliveryRequest.getSiteCode(),
                        deliveryRequest.getReceiveSiteCode(),
                        deliveryRequest.getBusinessType(),
                        deliveryRequest.getOpType());
                if(boxCheckResponse == null){
                    result.toFail("箱号验证失败！");
                    return result;
                }
                if(!JdResponse.CODE_OK.equals(boxCheckResponse.getCode())){
                    if(boxCheckResponse.getCode() != null
                            && boxCheckResponse.getCode()>=30000
                            && boxCheckResponse.getCode()<=40000){
                        result.toWarn(boxCheckResponse.getCode(), boxCheckResponse.getMessage());
                        checkResponse.getTipMessages().add(boxCheckResponse.getMessage());
                    }else{
                        result.toFail(boxCheckResponse.getCode(), boxCheckResponse.getMessage());
                        return result;
                    }
                }
                checkResponse.setWaybillType(boxCheckResponse.getWaybillType());
            }
            //初始化批次已发货的数量
            if(BusinessHelper.isSendCode(deliveryRequest.getSendCode())){
                deliveryRequest.setHasSendPackageNum(sendDetailService.querySendDCountBySendCode(deliveryRequest.getSendCode()));
            }

            //调用分拣无重量拦截链---2020.12.17 目前主要针对满足纯配外单的0 重量
            ZeroWeightVolumeCheckEntity entity = new ZeroWeightVolumeCheckEntity();
            entity.setPackageCode(deliveryRequest.getBoxCode());
            if(WaybillUtil.isPackageCode(deliveryRequest.getBoxCode())){
                entity.setWaybillCode(WaybillUtil.getWaybillCode(deliveryRequest.getBoxCode()));
            }else{
                entity.setWaybillCode(deliveryRequest.getBoxCode());
            }
            if(dmsWeightVolumeService.zeroWeightVolumeIntercept(entity)){
                result.setCode(JdResult.CODE_FAIL);
                result.setMessageCode(SortingResponse.CODE_29419);
                result.setMessage(HintService.getHint(HintCodeConstants.WAYBILL_WITHOUT_WEIGHT));
                // 发送拦截消息
                this.sendBusinessInterceptMsg(deliveryRequest, result);
                return result;
            }

            //调用ver校验链
            JdResult<CheckBeforeSendResponse> verCheckResult = jsfSortingResourceService.checkBeforeSend(deliveryRequest);
            if(!verCheckResult.isSucceed()){
                // 发送拦截消息
                this.sendBusinessInterceptMsg(deliveryRequest, verCheckResult);
                return verCheckResult;
            }else{
                //前面校验
                if(!result.isWarn()){
                    result.setCode(verCheckResult.getCode());
                    result.setMessageCode(verCheckResult.getMessageCode());
                    result.setMessage(verCheckResult.getMessage());
                }
                if(verCheckResult.isWarn() && verCheckResult.getData().getTipMessages() != null){
                    checkResponse.getTipMessages().addAll(verCheckResult.getData().getTipMessages());
                    // 发送拦截消息
                    this.sendBusinessInterceptMsg(deliveryRequest, verCheckResult);
                }
                checkResponse.setPackageNum(verCheckResult.getData().getPackageNum());
            }
            if(log.isDebugEnabled()){
                log.debug("调用verjsf进行老发货校验拦截,返回值:{}" , JSON.toJSONString(result));
            }
            return result;
        }catch (Exception e){

            log.error("调用ver接口进行老发货验证异常:{}" ,JSON.toJSONString(deliveryRequest),e);
            result.toError("调用ver接口进行老发货验证异常.");
        }
        return result;
    }

    /**
     * 发送拦截消息
     * @param deliveryRequest 请求参数
     * @param result 校验结果
     * @return 处理结果
     * @author fanggang7
     * @time 2020-12-22 18:18:15 周二
     */
    private boolean sendBusinessInterceptMsg(DeliveryRequest deliveryRequest, JdResult<CheckBeforeSendResponse> result){
        log.info("DeliveryResource sendBusinessInterceptMsg param {}, {}", JSON.toJSONString(deliveryRequest), JSON.toJSONString(result));
        try {
            SaveInterceptMsgDto saveInterceptMsgDto = new SaveInterceptMsgDto();
            saveInterceptMsgDto.setInterceptCode(result.getMessageCode());
            saveInterceptMsgDto.setInterceptMessage(result.getMessage());
            saveInterceptMsgDto.setBarCode(deliveryRequest.getBoxCode());
            saveInterceptMsgDto.setSiteCode(deliveryRequest.getSiteCode());
            saveInterceptMsgDto.setDeviceType(businessInterceptConfigHelper.getInterceptOperateDeviceTypePda());
            saveInterceptMsgDto.setDeviceCode(Constant.DEVICE_CODE_PDA);
            long operateTimeMillis = DateUtil.parse(deliveryRequest.getOperateTime(), DateUtil.FORMAT_DATE_TIME).getTime();
            saveInterceptMsgDto.setOperateTime(operateTimeMillis);
            saveInterceptMsgDto.setOperateNode(businessInterceptConfigHelper.getInterceptOperateNodeSend());
            saveInterceptMsgDto.setSiteName(deliveryRequest.getSiteName());
            saveInterceptMsgDto.setOperateUserCode(deliveryRequest.getUserCode());
            saveInterceptMsgDto.setOperateUserName(deliveryRequest.getUserName());

            String saveInterceptMqMsg = JSON.toJSONString(saveInterceptMsgDto);
            try {
                businessInterceptReportService.sendInterceptMsg(saveInterceptMsgDto);
            } catch (Exception e) {
                log.error("DeliveryResource.sendBusinessInterceptMsg call sendInterceptMsg exception [{}]" , saveInterceptMqMsg, e);
                return false;
            }
        } catch (Exception e) {
            log.error("DeliveryResource.sendBusinessInterceptMsg call sendInterceptMsg exception [{}]" , e.getMessage(), e);
        }
        return true;
    }

    /**
     * 老发货、快运发货扫描箱号校验
     * @param boxCode
     * @param siteCode
     * @param receiveSiteCode
     * @param businessType
     * @return
     */
    @Override
    public DeliveryResponse doCheckDeliveryInfo(String boxCode,
                                                 Integer siteCode,
                                                 Integer receiveSiteCode,
                                                 Integer businessType,
                                                 Integer opType) {
        CallerInfo info = Profiler.registerInfo("DMSWEB.DeliveryResource.checkDeliveryInfo", Constants.UMP_APP_NAME_DMSWEB,false, true);
        SendM tSendM = new SendM();
        tSendM.setBoxCode(boxCode);
        tSendM.setCreateSiteCode(siteCode);
        tSendM.setReceiveSiteCode(receiveSiteCode);
        tSendM.setSendType(businessType);

        try {
            boolean isTransferSend = isTransferSend(tSendM);
            DeliveryResponse tDeliveryResponse = findSendMByBoxCode(tSendM, isTransferSend, opType);
            this.log.debug("结束验证箱号信息");
            if (tDeliveryResponse != null) {
                //设置运单类型
                String waybillCodeToJudgeType = null;
                if(WaybillUtil.isPackageCode(boxCode)){
                    waybillCodeToJudgeType = WaybillUtil.getWaybillCode(boxCode);
                }else if(BusinessUtil.isBoxcode(boxCode)){
                    //从箱子中取出一单
                    List<String> waybillCodeList = getWaybillCodesByBoxCodeAndFetchNum(boxCode,1);
                    if(waybillCodeList != null && waybillCodeList.size() > 0){
                        waybillCodeToJudgeType = waybillCodeList.get(0);
                    }
                }
                //获取运单类型
                Integer waybillType = waybillService.getWaybillTypeByWaybillSign(waybillCodeToJudgeType);
                tDeliveryResponse.setWaybillType(waybillType);

                if(JdResponse.CODE_OK.equals(tDeliveryResponse.getCode())){

                    //added by hanjiaxing3 2018.10.12 delivered is not allowed to reverse
                    if (WaybillUtil.isPackageCode(boxCode)) {
                        CallerInfo reverseCheckInfo = Profiler.registerInfo("DMSWEB.DeliveryResource.checkDeliveryInfo.reverseCheckInfo", Constants.UMP_APP_NAME_DMSWEB,false, true);
                        try {
                            BaseStaffSiteOrgDto baseStaffSiteOrgDto = this.baseMajorManager.getBaseSiteBySiteId(receiveSiteCode);
                            if (baseStaffSiteOrgDto != null) {
                                Integer siteType = baseStaffSiteOrgDto.getSiteType();
                                //售后
                                String asm_type = PropertiesHelper.newInstance().getValue("asm_type");
                                //仓储
                                String wms_type = PropertiesHelper.newInstance().getValue("wms_type");
                                //备件库退货
                                String spwms_type = PropertiesHelper.newInstance().getValue("spwms_type");
                                if (siteType == Integer.parseInt(asm_type) || siteType == Integer.parseInt(wms_type) || siteType == Integer.parseInt(spwms_type)) {
                                    String waybillCode = WaybillUtil.getWaybillCode(boxCode);
                                    com.jd.bluedragon.distribution.base.domain.InvokeResult<Boolean> result = waybillService.isReverseOperationAllowed(waybillCode, siteCode);
                                    if(result != null && com.jd.bluedragon.distribution.base.domain.InvokeResult.RESULT_SUCCESS_CODE != result.getCode()) {
                                        return new DeliveryResponse(result.getCode(), result.getMessage());
                                    }
                                }else{
                                    //校验滑道号
                                    if(!checkPackageCrossCodeSucc(boxCode)){
                                        this.log.warn("滑道号不正确[{}]",boxCode);
                                        return new DeliveryResponse(DeliveryResponse.CODE_CROSS_CODE_ERROR,
                                                HintService.getHint(HintCodeConstants.PACKAGE_CODE_ILLEGAL));
                                    }
                                }
                            } else{
                                this.log.warn("发货校验获取站点信息为空：{}" , receiveSiteCode);
                            }
                        } catch (Exception e) {
                            Profiler.functionError(reverseCheckInfo);
                            this.log.error("发货校验获取站点信息失败，站点编号:{}" , receiveSiteCode, e);
                        }finally {
                            Profiler.registerInfoEnd(reverseCheckInfo);
                        }
                    }

                    return tDeliveryResponse;
                    //adder end
                }else{
                    return tDeliveryResponse;
                }
            } else {
                return new DeliveryResponse(JdResponse.CODE_NOT_FOUND, JdResponse.MESSAGE_SERVICE_ERROR);
            }
        } catch (Exception e) {
            Profiler.functionError(info);
            log.error("发货校验异常:{}", com.jd.bluedragon.distribution.api.utils.JsonHelper.toJson(tSendM), e);
            return new DeliveryResponse(JdResponse.CODE_NOT_FOUND, JdResponse.MESSAGE_SERVICE_ERROR);
        }finally {
            Profiler.registerInfoEnd(info);
        }
    }

    @Override
    public DeliveryResponse sendDeliveryInfoForKY(List<DeliveryRequest> request,SendBizSourceEnum sourceEnum){
        List<SendM> waybillCodeSendMList = this.assembleSendMForWaybillCode(request);
        List<SendM> otherSendMList = this.assembleSendMWithoutWaybillCode(request);
        if (waybillCodeSendMList.size() == 0) {
            return dellDeliveryMessage(sourceEnum, otherSendMList);
        }
        /** 快运发货 */
        DeliveryResponse response = dellDeliveryMessageWithLock(sourceEnum, waybillCodeSendMList);
        if (JdResponse.CODE_OK.equals(response.getCode()) && otherSendMList!=null && otherSendMList.size()>0) {
            return dellDeliveryMessage(sourceEnum, otherSendMList);
        }
        return response;
    }

    @Override
    public DeliveryResponse coldChainSendDelivery(List<ColdChainDeliveryRequest> request,SendBizSourceEnum sourceEnum,boolean checkSealCar) {
        DeliveryResponse response;

        // 同一运输计划编号加锁，解决并发问题
        String lockKey = null;
        try {

            if (CollectionUtils.isNotEmpty(request)) {
                if (StringUtils.isNotBlank(request.get(0).getTransPlanCode())) {
                    String keyTemplate = KeyConstants.COLD_CHAIN_SEND_TRANS_PLAN_CODE_HANDLING;
                    lockKey = String.format(keyTemplate, request.get(0).getTransPlanCode());
                    boolean isExistHandling = redisClientCache.set(lockKey, 1 + "", KeyConstants.COLD_CHAIN_SEND_TRANS_PLAN_CODE_HANDLING__EXPIRED, TimeUnit.SECONDS, false);
                    if (!isExistHandling) {
                        throw new RuntimeException("该运输计划号发货任务正在处理中，请稍后再试！");
                    }
                }
            }

            response = this.coldChainSendCheckAndFixSendCode(request,checkSealCar);
            if (response != null) {
                return response;
            }

            // 组装运单号维度sendM对象
            List<SendM> waybillCodeSendMList = this.assembleSendMForWaybillCode(request);
            // 组装非运单号（箱号，包裹号）维度sendM对象
            List<SendM> otherSendMList = this.assembleSendMWithoutWaybillCode(request);

            /** 冷链发货 */
            if (waybillCodeSendMList.size() == 0) {
                response = dellDeliveryMessage(sourceEnum, otherSendMList);
            } else {
                response = dellDeliveryMessageWithLock(sourceEnum, waybillCodeSendMList);
                if (JdResponse.CODE_OK.equals(response.getCode()) &&
                        otherSendMList!=null && otherSendMList.size()>0) {
                    response = dellDeliveryMessage(sourceEnum, otherSendMList);
                }
            }

            if (JdResponse.CODE_OK.equals(response.getCode())) {
                coldChainSendService.batchAdd(waybillCodeSendMList, request.get(0).getTransPlanCode());
                coldChainSendService.batchAdd(otherSendMList, request.get(0).getTransPlanCode());
            }
        }
        finally {
            if (StringUtils.isNotBlank(lockKey)) {
                redisClientCache.del(lockKey);
            }
        }

        return response;
    }

    private DeliveryResponse coldChainSendCheckAndFixSendCode(List<ColdChainDeliveryRequest> request,boolean checkSealCar) {
        if (request != null && !request.isEmpty()) {
            ColdChainDeliveryRequest request0 = request.get(0);
            if (StringUtils.isEmpty(request0.getTransPlanCode()) || request0.getBoxCode() == null || request0.getSiteCode() == null || request0.getReceiveSiteCode() == null || request0.getBusinessType() == null) {
                return new DeliveryResponse(JdResponse.CODE_PARAM_ERROR, JdResponse.MESSAGE_PARAM_ERROR);
            }
            String sendCode = coldChainSendService.getOrGenerateSendCode(request0.getTransPlanCode(), request0.getSiteCode(), request0.getReceiveSiteCode());
            // 批次号封车校验，已封车不能发货
            StringBuffer customMsg = new StringBuffer().append("该运输计划编码对应批次已经封车，请更换其他运输计划编码");
            if (checkSealCar && newSealVehicleService.newCheckSendCodeSealed(sendCode, customMsg)) {
                return new DeliveryResponse(DeliveryResponse.CODE_SEND_CODE_ERROR, customMsg.toString());
            }
            request0.setSendCode(sendCode);

            for (int i = 1; i < request.size(); i++) {
                DeliveryRequest deliveryRequest = request.get(i);
                if (StringUtils.isEmpty(request0.getTransPlanCode()) || deliveryRequest.getBoxCode() == null || deliveryRequest.getSiteCode() == null || deliveryRequest.getReceiveSiteCode() == null || deliveryRequest.getBusinessType() == null) {
                    return new DeliveryResponse(JdResponse.CODE_PARAM_ERROR, JdResponse.MESSAGE_PARAM_ERROR);
                }
                deliveryRequest.setSendCode(sendCode);
            }
            return null;
        }
        return new DeliveryResponse(JdResponse.CODE_PARAM_ERROR, JdResponse.MESSAGE_PARAM_ERROR);
    }

    protected <T extends DeliveryRequest> List<SendM> assembleSendMForWaybillCode(List<T> request) {
        List<SendM> sendMList = new ArrayList<>();
        if (request != null && !request.isEmpty()) {
            for (DeliveryRequest deliveryRequest : request) {
                if (WaybillUtil.isWaybillCode(deliveryRequest.getBoxCode())) {
                    //B冷链快运发货支持扫运单号发货
                    DeliveryResponse response = isValidWaybillCode(deliveryRequest);
                    if (!JdResponse.CODE_OK.equals(response.getCode())) {
                        log.warn("DeliveryResource--toSendDetailList出现运单号，但非冷链快运发货，siteCode:{},单号:{}",
                                deliveryRequest.getSiteCode() , deliveryRequest.getBoxCode());
                        continue;
                    }
                    sendMList.add(deliveryRequest2SendM(deliveryRequest));
                }
            }
        }
        return sendMList;
    }

    protected <T extends DeliveryRequest> List<SendM> assembleSendMWithoutWaybillCode(List<T> request) {
        List<SendM> sendMList = new ArrayList<>();
        if (request != null && !request.isEmpty()) {
            for (DeliveryRequest deliveryRequest : request) {
                if (!WaybillUtil.isWaybillCode(deliveryRequest.getBoxCode())) {
                    sendMList.add(deliveryRequest2SendM(deliveryRequest));
                }
            }
        }
        return sendMList;
    }

    @Override
    public DeliveryResponse checkThreeDelivery(DeliveryRequest request, Integer flag) {
        try {
            if (request == null || StringUtils.isBlank(request.getBoxCode()) ||
                    request.getSiteCode() == null || request.getReceiveSiteCode() == null) {
                return new DeliveryResponse(JdResponse.CODE_PARAM_ERROR, JdResponse.MESSAGE_PARAM_ERROR);
            }

            //如果扫描的是运单号，判断是否是B冷链操作的快运发货
            if(isWaybillCode(request.getBoxCode())){
                DeliveryResponse response = isValidWaybillCode(request);
                if(!JdResponse.CODE_OK.equals(response.getCode())){
                    return response;
                }
            }
            //校验滑道号
            if(WaybillUtil.isPackageCode(request.getBoxCode()) && !checkPackageCrossCodeSucc(request.getBoxCode())){
                log.warn("滑道号不正确[{}]",request.getBoxCode());
                return new DeliveryResponse(DeliveryResponse.CODE_CROSS_CODE_ERROR,
                        HintService.getHint(HintCodeConstants.PACKAGE_CODE_ILLEGAL));
            }

            Integer opType = request.getOpType();
            DeliveryResponse response = new DeliveryResponse(JdResponse.CODE_OK,JdResponse.MESSAGE_OK);
            if(KY_DELIVERY.equals(opType)){//只有快运发货才做路由校验
                // 因为B冷链转运中心需要支持扫描运单号发货，
                // 如果扫的是运单号，则生成第一个包裹号，用于校验
                if (isWaybillCode(request.getBoxCode())) {
                    List<String> waybillCodeList = waybillPackageBarcodeService.getPackageCodeListByWaybillCode(request.getBoxCode());
                    if(waybillCodeList == null || waybillCodeList.size() < 1){
                        log.warn("快运发货扫运单号，根据运单号[{}]生成包裹号失败.没有运单/包裹信息",request.getBoxCode());
                        response.setCode(JdResponse.CODE_CAN_NOT_GENERATE_PACKAGECODE);
                        Map<String, String> argsMap = new HashMap<>();
                        argsMap.put(HintArgsConstants.ARG_FIRST, request.getBoxCode());
                        response.setMessage(HintService.getHint(HintCodeConstants.SEND_VALIDATE_WAYBILL_HAS_PACKAGE, argsMap));
                        return response;
                    }
                    request.setBoxCode(waybillCodeList.get(0));
                }
                response =  checkRouterForKY(deliveryRequest2SendM(request), flag);
            }
            return response;
        } catch (Exception ex) {
            log.error("快运发货路由验证出错：{}", com.jd.bluedragon.distribution.api.utils.JsonHelper.toJson(request),ex);
            return new DeliveryResponse(JdResponse.CODE_SERVICE_ERROR, JdResponse.MESSAGE_SERVICE_ERROR);
        }
    }

    /**
     * 严格判断是否是运单号
     * 不是包裹号&&不是箱号&&是运单号
     * @param waybillCode
     * @return
     */
    private boolean isWaybillCode(String waybillCode){
        if(StringUtils.isBlank(waybillCode)){
            return false;
        }
        return !WaybillUtil.isPackageCode(waybillCode) && !BusinessHelper.isBoxcode(waybillCode)
                && WaybillUtil.isWaybillCode(waybillCode);
    }

    /**
     * B冷链转运中心--快运发货支持扫描运单号发货
     * 如果扫描的是运单号，判断是否符是B冷链转运中心 && 入口是快运发货
     * @param request
     * @return
     */
    private DeliveryResponse isValidWaybillCode(DeliveryRequest request){
        Integer opType = request.getOpType();
        DeliveryResponse response = new DeliveryResponse(JdResponse.CODE_OK, JdResponse.MESSAGE_OK);

        //判断是否是正确的箱号/包裹号--仅B冷链转运中心6460快运发货支持扫运单
        //登录人机构不是冷链分拣中心
        BaseStaffSiteOrgDto siteOrgDto = siteService.getSite(request.getSiteCode());
        if (siteOrgDto == null) {
            response.setCode(JdResponse.CODE_NO_SITE);
            response.setMessage(MessageFormat.format(JdResponse.MESSAGE_NO_SITE, request.getSiteCode()));
            return response;
        }
        if (!(Constants.B2B_CODE_SITE_TYPE.equals(siteOrgDto.getSubType())&& KY_DELIVERY.equals(opType)) ) {
            response.setCode(JdResponse.CODE_INVALID_PACKAGECODE_BOXCODE);
            response.setMessage(JdResponse.MESSAGE_INVALID_PACKAGECODE_BOXCODE);
            return response;
        }
        return response;
    }

    /**
     * DeliveryRequest对象转sendM
     * @param deliveryRequest
     * @return
     */
    protected SendM deliveryRequest2SendM(DeliveryRequest deliveryRequest){
        SendM sendM = new SendM();
        sendM.setBoxCode(deliveryRequest.getBoxCode());
        sendM.setCreateSiteCode(deliveryRequest.getSiteCode());
        sendM.setReceiveSiteCode(deliveryRequest.getReceiveSiteCode());
        sendM.setCreateUserCode(deliveryRequest.getUserCode());
        sendM.setSendType(deliveryRequest.getBusinessType());
        sendM.setCreateUser(deliveryRequest.getUserName());
        sendM.setSendCode(deliveryRequest.getSendCode());
        sendM.setCreateTime(new Date());
        sendM.setOperateTime(new Date());
        sendM.setYn(1);
        sendM.setTurnoverBoxCode(deliveryRequest.getTurnoverBoxCode());
        sendM.setTransporttype(deliveryRequest.getTransporttype());
        return sendM;
    }



    /**
     * 只校验包裹的 校验滑道号
     * @return true 滑道号正确，或者非包裹号，false 不正确
     */
    private boolean checkPackageCrossCodeSucc(String packageCode){
        if(!uccPropertyConfiguration.isControlCheckPackage()){
            return true;
        }
        return jsfSortingResourceService.checkPackageCrossCode(WaybillUtil.getWaybillCode(packageCode),packageCode);
    }
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
    @JProfiler(jKey = "DMSWEB.DeliveryServiceImpl.packageSendCanCancel", mState = {JProEnum.TP, JProEnum.FunctionError})
    public SendResult packageSend(SendBizSourceEnum bizSource, SendM domain, boolean isForceSend, boolean isCancelLastSend) {
        log.info("[一车一单发货]packageSend-箱号/包裹号:{},批次号：{},操作站点：{},是否强制操作：{}"
                ,domain.getBoxCode(),domain.getSendCode(),domain.getCreateSiteCode(),isForceSend);
        // 若第一次校验不通过，需要点击选择确认框后，二次调用时跳过校验
        if (!isForceSend) {
            // 发货验证
            SendResult sendResult = this.beforeSendVerification(domain, true, isCancelLastSend);
            if (!SendResult.CODE_OK.equals(sendResult.getKey())) {
                return sendResult;
            }
        }
        //众邮0重量拦截
        //不能强制发货，因此放在外面
        DeliveryResponse weightAndVolumeCheck = zeroWeightAndVolumeCheck(domain);
        if (DeliveryResponse.CODE_CANCELDELIVERYCHECK_ZERO_WEIGHT_VOLUME.equals(weightAndVolumeCheck.getCode())){
            SendResult sendResult = new SendResult();
            sendResult.init(SendResult.CODE_SENDED,weightAndVolumeCheck.getMessage());
            // 待用，整箱未称时提交拦截报表
            // this.sendBusinessInterceptMsg(domain, weightAndVolumeCheck);
            return  sendResult;
        }
        if (isCancelLastSend) {
            this.doCancelLastSend(domain);
        }
        SendResult sendResult = this.doPackageSend(bizSource, domain);

        // BC箱号发货校验通过后，处理WJ箱号的发货逻辑
        if (ObjectUtils.equals(SendResult.CODE_OK, sendResult.getKey()) || ObjectUtils.equals(SendResult.CODE_WARN, sendResult.getKey())) {

            this.dealFileBoxSingleCarSend(bizSource, domain);
        }

        return sendResult;

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
        log.info("[一车一单发货]packageSend-箱号/包裹号:{},批次号：{},操作站点：{},是否强制操作：{}"
                ,domain.getBoxCode(),domain.getSendCode(),domain.getCreateSiteCode(),isForceSend);
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

        // BC箱号发货校验通过后，处理WJ箱号的发货逻辑
        if (SendResult.CODE_OK.equals(sendResult.getKey()) || SendResult.CODE_WARN.equals(sendResult.getKey())) {

            this.dealFileBoxSingleCarSend(bizSource, domain);
        }

        return sendResult;
    }

    @Override
    @JProfiler(jKey = "DMSWEB.DeliveryServiceImpl.packageSendByWaybill", mState = {JProEnum.TP, JProEnum.FunctionError})
    public SendResult packageSendByWaybill(SendM domain, Boolean isForceSend, Boolean isCancelLastSend) {
        if (log.isInfoEnabled()) {
            log.info("按运单发货接口处理开始 boxCode={},isForceSend={},isCancelLastSend={}", domain.getBoxCode(), isForceSend, isCancelLastSend);
        }
        SendResult result = new SendResult();
        result.init(SendResult.CODE_OK, SendResult.MESSAGE_OK);
        // 校验参数
        checkSendByWaybillParam(domain, result);

        if (!SendResult.CODE_OK.equals(result.getKey())) {
            return result;
        }
        if (!Boolean.TRUE.equals(isForceSend)) {
            // 发货验证
            result = this.beforeSendVerification(domain, true, isCancelLastSend);
            if (log.isInfoEnabled()) {
                log.info("按运单发货接口处理,拦截器链路校验结果: result={}", JsonHelper.toJson(result));
            }
            if (!SendResult.CODE_OK.equals(result.getKey())) {
                return result;
            }
        }

        if (!packageSendByRealWaybill(domain, isCancelLastSend, result)) {
            return result;
        }
        return result;
    }

    @Override
    @JProfiler(jKey = "DMSWEB.DeliveryServiceImpl.packageSendByRealWaybill", mState = {JProEnum.TP, JProEnum.FunctionError})
    public boolean packageSendByRealWaybill(SendM domain, Boolean isCancelLastSend, SendResult result) {
        String waybillCode = WaybillUtil.getWaybillCode(domain.getBoxCode());
        Integer createSiteCode = domain.getCreateSiteCode();
        Waybill waybill = waybillQueryManager.getOnlyWaybillByWaybillCode(waybillCode);
        if (waybill == null) {
            log.error("按运单发货任务处理,查询运单不存在:waybillCode={}", waybillCode);
            result.init(SendResult.CODE_SENDED, "查询运单不存在:" + waybillCode);
            return false;
        }
        // 校验是否已有包裹操作过发货 v2新需求：如果有包裹号单独先发货，则跳过已发货包裹号，剩余的包裹号执行发货逻辑
//        if (redisClientCache.exists(getSendByWaybillPackLockKey(waybillCode, createSiteCode))) {
//            result.init(SendResult.CODE_SENDED, DeliveryResponse.MESSAGE_DELIVERY_BY_WAYBILL_HAS_SEND_PACK);
//            return result;
//        }
        // 锁定运单发货
        if (!lockWaybillSend(waybillCode, createSiteCode, waybill.getGoodNumber())) {
            result.init(SendResult.CODE_SENDED, DeliveryResponse.MESSAGE_DELIVERY_ALL_PROCESSING);
            return false;
        }
        if (Boolean.TRUE.equals(isCancelLastSend)) {
            this.doCancelLastSend(domain);
        }
        try {
            // 写入按运单发货任务
            pushWaybillSendTask(domain, Task.TASK_TYPE_SEND_DELIVERY);
        } catch (Throwable e) {
            unlockWaybillSend(waybillCode, createSiteCode);
            log.error("写入按运单发货任务出错:waybill={}", waybillCode, e);
            result.init(SendResult.CODE_SERVICE_ERROR, "写入按运单发货任务出错:" + waybillCode);
            return false;
        }

        return true;
    }

    /**
     * 按运单发货任务
     * @param domain 发货数据
     */
    private void pushWaybillSendTask(SendM domain,Integer taskType) {
        if (WaybillUtil.isPackageCode(domain.getBoxCode())) {
            domain.setBoxCode(WaybillUtil.getWaybillCode(domain.getBoxCode()));
        }
        Task tTask = new Task();
        tTask.setBoxCode(domain.getBoxCode());
        tTask.setBody(JsonHelper.toJson(domain));
        tTask.setCreateSiteCode(domain.getCreateSiteCode());
        tTask.setReceiveSiteCode(domain.getReceiveSiteCode());

        tTask.setKeyword1("10");
        tTask.setKeyword2(domain.getBoxCode());

        tTask.setType(taskType);
        tTask.setTableName(Task.getTableName(taskType));
        String ownSign = BusinessHelper.getOwnSign();
        tTask.setOwnSign(ownSign);

        // TB 调度防止重复
        tTask.setFingerprint(Md5Helper.encode(domain.getSendCode() + "_" + tTask.getKeyword1() + "_" + domain.getBoxCode()));
        if (log.isInfoEnabled()) {
            log.info("按运单发货写入异步任务：task={}" ,JsonHelper.toJsonMs(tTask));
        }
        tTaskService.add(tTask, true);
    }

    /**
     *
     * 锁定运单发货
     */
    private boolean lockWaybillSend(String waybillCode, Integer createSiteCode,int totalPackNum) {
        String redisKey = getSendByWaybillLockKey(waybillCode, createSiteCode);

        // 避免消费数据重复逻辑 插入redis 如果插入失败 说明有其他线程正在消费相同数据信息
        Boolean set = redisClientCache.set(redisKey, "" + totalPackNum, REDIS_CACHE_EXPIRE_TIME, TimeUnit.SECONDS, false);
        if (log.isInfoEnabled()) {
            log.info("按运单发货接口处理,锁定运单:key={}结果:{}", redisKey, set);
        }
        return set;
    }

    /**
     * 解锁运单发货
     *
     */
    private void unlockWaybillSend(String waybillCode, Integer createSiteCode) {
        String redisKey = getSendByWaybillLockKey(waybillCode, createSiteCode);
        redisClientCache.del(redisKey);
        log.info("按运单发货移除运单锁:key={}",redisKey);
    }

    // 按运单发货，锁定整个运单，防止重复处理
    private String getSendByWaybillLockKey(String waybill, Integer operateSiteCode) {
        if (WaybillUtil.isPackageCode(waybill)) {
            waybill = WaybillUtil.getWaybillCode(waybill);
        }
        return SEND_BY_WAYBILL_REDIS_PREFIX + waybill + "_" + operateSiteCode;
    }

    // 按运单发货 锁定包裹
    private String getSendByWaybillPackLockKey(String waybill, Integer operateSiteCode) {
        if (WaybillUtil.isPackageCode(waybill)) {
            waybill = WaybillUtil.getWaybillCode(waybill);
        }
        return SEND_BY_WAYBILL_PACK_REDIS_PREFIX + waybill + "_" + operateSiteCode;
    }

    private void checkSendByWaybillParam(SendM domain, SendResult result) {
        if (StringUtils.isEmpty(domain.getSendCode())) {
            result.init(SendResult.CODE_SENDED, "批次号不能为空!");
            return;
        }
        if (StringUtils.isEmpty(domain.getBoxCode())) {
            result.init(SendResult.CODE_SENDED, "包裹号不能为空!");
            return;
        }
        if(!WaybillUtil.isPackageCode(domain.getBoxCode())) {
            result.init(SendResult.CODE_SENDED, "请扫描正确的包裹号!");
            return;
        }
    }

    /**
     * 获取BC箱号关联的WJ箱号的发货对象
     * @param BCSendM
     * @return
     */
    private List<SendM> getWJSendMDomains(SendM BCSendM) {

        List<SendM> WJSendMList = new ArrayList<>();

        if (BusinessUtil.isBoxcode(BCSendM.getBoxCode())) {
            CallerInfo callerInfo = Profiler.registerInfo("DMSWEB.DeliveryServiceImpl.getWJSendMDomains", false, true);

            List<BoxRelation> boxRelations = null;
            com.jd.bluedragon.distribution.base.domain.InvokeResult<List<BoxRelation>> sr = boxRelationService.getRelationsByBoxCode(BCSendM.getBoxCode());
            if (sr.codeSuccess() && CollectionUtils.isNotEmpty(sr.getData())) {
                boxRelations =  sr.getData();
            }
            if (CollectionUtils.isNotEmpty(boxRelations)) {
                for (BoxRelation relation : boxRelations) {
                    SendM sendM = new SendM();

                    BeanUtils.copyProperties(BCSendM, sendM);

                    sendM.setBoxCode(relation.getRelationBoxCode());
                    WJSendMList.add(sendM);
                }
            }
            Profiler.registerInfoEnd(callerInfo);
        }

        return WJSendMList;
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
            this.dellCancelDeliveryMessageWithServerTime(getCancelSendM(lastSendM, domain, new Date()), true);
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
            // 设置批次号为空，B冷链发货会调用该接口，传入无效批次号，故在此清空
            domain.setSendCode(null);
            return this.dellCancelDeliveryMessageWithServerTime(domain, true);
        } else {
            return new ThreeDeliveryResponse(DeliveryResponse.CODE_Delivery_NO_MESAGE,
                    HintService.getHint(HintCodeConstants.PACKAGE_SENDM_MISSING), null);
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
            this.log.info("发送发货提示语MQ[{}],业务ID[{}]" ,operateHintTrackMQ.getTopic(),dmsOperateHintTrack.getWaybillCode());
            this.operateHintTrackMQ.sendOnFailPersistent(dmsOperateHintTrack.getWaybillCode(), mqText);
        } catch (Exception e) {
            log.error("发货提示语发mq异常,SendM:{}" ,JsonHelper.toJson(domain), e);
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
            result.init(SendResult.CODE_SENDED, HintService.getHint(HintCodeConstants.BATCH_ORIGIN_AND_OPERATOR_ORIGIN_DIFFERENCE));
            return result;
        }

        // 校验发货批次号状态
        StringBuffer customMsg = new StringBuffer().append(HintService.getHint(HintCodeConstants.SEND_CODE_SEALED_TIPS_SECOND));
        if (newSealVehicleService.newCheckSendCodeSealed(domain.getSendCode(), customMsg)) {
            result.init(SendResult.CODE_SENDED, customMsg.toString());
            return result;
        }

        // 运单和包裹互斥 按运单发货正在处理中( 按运单/包裹 SETNX 互相校验)
        if (isSendByWaybillProcessing(domain)) {
            result.init(SendResult.CODE_SENDED, HintService.getHint(HintCodeConstants.SEND_BY_WAYBILL_PROCESSING));
            return result;
        }

        // 判断是否使用多次发货取消上次发货
        if (isUseMultiSendVerify) {

            // 启用西藏业务模式，西藏模式去掉自动取消上次发货逻辑
            boolean tibetMode = tibetBizService.tibetModeSwitch(domain.getCreateSiteCode(), domain.getReceiveSiteCode());

            if (!tibetMode && !isSkipMultiSendVerify) {
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

        CallerInfo freshInfo = Profiler.registerInfo("DMSWEB.DeliveryServiceImpl.packageSend.freshInfoCheck",Constants.UMP_APP_NAME_DMSWEB, false, true);
        /*
         * 生鲜批次号只能装生鲜订单校验 针对包裹发货或者是按运单发货
         * 生鲜运单得标位：
         *      WaybillSign 55位=1：“生鲜专送”；
         *      WaybillSign 55位<>1且WaybillSign 31位=A：“生鲜特惠”；
         *      WaybillSign 55位<>1且WaybillSign 31位=9，且waybillSign54位=2：“生鲜特快”
         */
        if ((WaybillUtil.isPackageCode(domain.getBoxCode()) || WaybillUtil.isWaybillCode(domain.getBoxCode()))
                && sendCodeService.isFreshSendCode(domain.getSendCode())) {
            com.jd.bluedragon.common.domain.Waybill waybill = waybillCommonService.findByWaybillCode(WaybillUtil.getWaybillCode(domain.getBoxCode()));
            if (null == waybill  || !BusinessUtil.isFreshWaybill(waybill.getWaybillSign())) {
                result.init(SendResult.CODE_SENDED, HintService.getHint(HintCodeConstants.SPECIAL_FRESH_BATCH));
                return result;
            }
        }
        Profiler.registerInfoEnd(freshInfo);

        // 根据发货的条码类型进行校验
        this.sendVerificationByBarCodeType(domain, result);

        //验证通过，补成第一个包裹号，如果后面发现这单是一单多件，再进行提示
        if (!BusinessUtil.isBoxcode(domain.getBoxCode()) && !WaybillUtil.isPackageCode(domain.getBoxCode()) &&
                WaybillUtil.isWaybillCode(domain.getBoxCode())) {
            log.info("一车一单发货扫描运单[{}]，校验通过，生成包裹号:{}" ,
                    domain.getBoxCode(), BusinessHelper.getFirstPackageCodeByWaybillCode(domain.getBoxCode()));
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
    @Override
    public boolean multiSendVerification(SendM domain, SendResult result) {
        // 根据箱号/包裹号 + 始发站点 + 目的站点获取发货记录
        SendM lastSendM = this.getRecentSendMByParam(domain.getBoxCode(), domain.getCreateSiteCode(), null, domain.getOperateTime());
        if (lastSendM != null) {
            String lastSendCode = lastSendM.getSendCode();
            if (StringUtils.isNotEmpty(lastSendCode)) {
                if (!this.sendSealTimeIsOverOneHour(lastSendCode, domain.getOperateTime())) {
                    if (domain.getReceiveSiteCode().equals(lastSendM.getReceiveSiteCode())) {
                        if (domain.getSendCode().equals(lastSendCode)) {
                            result.init(SendResult.CODE_SENDED, HintService.getHint(HintCodeConstants.BOX_SENT_BY_THIS_BATCH));
                        } else {
                            Map<String, String> argsMap = new HashMap<>();
                            argsMap.put(HintArgsConstants.ARG_FIRST, lastSendCode);
                            result.init(SendResult.CODE_SENDED, HintService.getHint(HintCodeConstants.BOX_SENT_BY_CONCRETE_BATCH, argsMap));
                        }
                    } else {
                        result.setKey(SendResult.CODE_CONFIRM);
                        result.setValue(HintService.getHint(HintCodeConstants.CANCEL_LAST_SEND_TIPS_SECOND));
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
        SortingJsfResponse response = null;
        try {
            if (sortingCheckService.isNeedCheck(uccPropertyConfiguration.getSingleSendSwitchVerToWebSites(), sortingCheck.getCreateSiteCode())) {
                response = sortingCheckService.singleSendCheckAndReportIntercept(sortingCheck);
            } else {
                response = jsfSortingResourceService.check(sortingCheck);
            }

        } catch (Exception ex) {
            log.error("调用总部VER验证JSF服务失败,sortingCheck:{}",JsonHelper.toJson(sortingCheck), ex);
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
                log.error("一车一单获取预分拣站点异常，单号：{}",sortingCheck.getBoxCode(), e);
            } finally {
                Profiler.registerInfoEnd(infoSendFindByWaybillCode);
            }

            if (response.getCode() >= SendResult.RESPONSE_CODE_MAPPING_CONFIRM) {
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
    @Override
    public SortingCheck getSortingCheck(SendM domain) {
        //大件分拣拦截验证
        SortingCheck sortingCheck = new SortingCheck();
        sortingCheck.setOperateNode(OperateNodeConstants.SEND);
        sortingCheck.setReceiveSiteCode(domain.getReceiveSiteCode());
        sortingCheck.setCreateSiteCode(domain.getCreateSiteCode());
        sortingCheck.setBoxCode(domain.getBoxCode());
        sortingCheck.setPackageCode(domain.getBoxCode());
        sortingCheck.setBusinessType(domain.getSendType());
        sortingCheck.setOperateUserCode(domain.getCreateUserCode());
        sortingCheck.setOperateUserName(domain.getCreateUser());
        sortingCheck.setOperateTime(DateHelper.formatDateTime(new Date()));
        sortingCheck.setBizSourceType(domain.getBizSource());
        //// FIXME: 2018/3/26 待校验后做修改
        if (domain.getCreateSiteCode() != null && siteService.getCRouterAllowedList().contains(domain.getCreateSiteCode())) {
            //判断批次号目的地的站点类型，是64的走新逻辑，非64的走老逻辑
            BaseStaffSiteOrgDto siteInfo = baseService.queryDmsBaseSiteByCode(domain.getReceiveSiteCode() + "");
            if (siteInfo == null || siteInfo.getSiteType() != 64) {
                sortingCheck.setOperateType(1);
            } else {
                sortingCheck.setOperateType(Constants.OPERATE_TYPE_NEW_PACKAGE_SEND);
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
            log.info("redis取PDA提示语结果：{}",msg);
        }catch (Throwable e){
            log.error("redis取PDA提示语失败：{}", JsonHelper.toJson(sendM), e);
        }
        return msg;
    }

    /**
     * 根据箱号查询箱号的运单号
     * @param boxCode
     * @return
     */
    @Override
    public List<String>  getWaybillCodesByBoxCodeAndFetchNum(String boxCode,Integer fetchNum){
        Box box = this.boxService.findBoxByCode(boxCode);
        if(box != null) {
            return sendDatailReadDao.getWaybillCodesByBoxCodeAndFetchNum(boxCode, box.getCreateSiteCode(),fetchNum);
        }else{
            log.warn("一车一单发货箱号为空：{}",boxCode);
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
                return new SendResult(SendResult.CODE_SENDED, HintService.getHint(HintCodeConstants.BATCH_ORIGIN_AND_OPERATOR_ORIGIN_DIFFERENCE));
            }
            //2.判断批次号是否已经封车
            StringBuffer customMsg = new StringBuffer().append(HintService.getHint(HintCodeConstants.SEND_CODE_SEALED_TIPS_SECOND));
            if (newSealVehicleService.newCheckSendCodeSealed(domain.getSendCode(), customMsg)) {
                return new SendResult(SendResult.CODE_SENDED, customMsg.toString());
            }
            //3.校验是否操作过按板发货,按板号和createSiteCode查询send_m表看是是否有记录
            if(sendMDao.checkSendByBoard(domain)){
                return new SendResult(SendResult.CODE_SENDED,HintService.getHint(HintCodeConstants.BOARD_SENT_ALREADY));
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
        changeBoardStatusSend(boardCode, domain);

        return new SendResult(SendResult.CODE_OK, SendResult.MESSAGE_OK);
    }

    /**
     * 写组板发货任务完成，调用TC修改板状态为发货
     */
    private void changeBoardStatusSend(String boardCode, SendM domain){
        try{
            Response<Boolean> closeBoardResponse = boardCombinationService.changeBoardStatusSend(boardCode);
            if(!JdResponse.CODE_OK.equals(closeBoardResponse.getCode()) || !closeBoardResponse.getData()){
                log.warn("组板发货调用TC置板号状态为发货,板号：{}，结果：{}" ,boardCode, JsonHelper.toJson(closeBoardResponse));
            }
        } catch (Exception e) {
            log.error("组板发货调用TC改板状态异常：{}" , JsonHelper.toJson(domain),e);
        }
    }

    /**
     * 写组板发货任务完成，调用TC执行关板
     */
    private void closeBoard(String boardCode, SendM domain){
        try{
            Response<Boolean> closeBoardResponse = boardCombinationService.closeBoard(boardCode);
            if(!JdResponse.CODE_OK.equals(closeBoardResponse.getCode()) || !closeBoardResponse.getData()){//关板失败
                log.warn("组板发货调用TC关板失败,板号：{}，关板结果：{}" ,boardCode, JsonHelper.toJson(closeBoardResponse));
            }
        } catch (Exception e) {
            log.error("组板发货调用TC关板异常：{}" , JsonHelper.toJson(domain),e);
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
                return new SendResult(SendResult.CODE_SENDED,HintService.getHint(HintCodeConstants.FAIL_TO_GET_BOARD_DEST));
            }
            if(SerialRuleUtil.getReceiveSiteCodeFromSendCode(domain.getSendCode())==null){
                return new SendResult(SendResult.CODE_SENDED,HintService.getHint(HintCodeConstants.FAIL_TO_GET_BATCH_DEST));
            }
            if(!SerialRuleUtil.getReceiveSiteCodeFromSendCode(domain.getSendCode()).equals(boardResponse.getReceiveSiteCode())){
                return new SendResult(SendResult.CODE_CONFIRM,HintService.getHint(HintCodeConstants.BOARD_AND_BATCH_DEST_DIFFERENCE));
            }
        }catch (Exception e){
            log.error("组板发货板号校验失败:{}" , JsonHelper.toJson(domain),e);
            return new SendResult(SendResult.CODE_SENDED,HintService.getHint(HintCodeConstants.BOARD_SEND_FAIL));
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
        // 锁定当前包裹，防止按运单发货时 重复发货
        lockWaybillByPack(domain.getCreateSiteCode(), domain.getBoxCode());
        // 插入SEND_M
        this.sendMManager.insertSendM(domain);
        //发送发货业务通知MQ
        deliverGoodsNoticeMQ(domain);
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
     * 按运单发货发货数据落库，写相关的异步任务
     *
     *  1.获取运单下的所有包裹号
     *  2.补充按运单分拣
     *  3.按包裹分页 调用一车一单发货逻辑，,移除补分拣逻辑
     *  4.所有分页数据处理完释放按运单锁
     *
     * @param domain 发货对象
     * @return 1：发货成功  2：发货失败
     */
    @JProfiler(jKey = "DMSWEB.DeliveryServiceImpl.doPackageSendByWaybill", mState = {JProEnum.TP, JProEnum.FunctionError})
    @Override
    public void doPackageSendByWaybill(SendM domain) {
        // 插入SEND_M
        this.sendMManager.insertSendM(domain);
        //发送发货业务通知MQ
        deliverGoodsNoticeMQ(domain);
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

    /**
     * 构建sendm对象
     *
     * @param lastSendM
     * @param domain
     * @param operateTime 分别来源于：pda（操作时间设置的是服务器时间）、自动化设备（操作时间设置的是设备扫描时间）
     * @return
     */
    private SendM getCancelSendM(SendM lastSendM, SendM domain, Date operateTime) {
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
        sendM.setOperateTime(operateTime);
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
        log.info("一车一单插入task_sorting单号:{}" , domain.getBoxCode());
    }

    @Override
    public int pushStatusTask(SendM domain) {
    	/**
    	 * 是否按箱发货
    	 */
    	boolean isSendByBox = BusinessHelper.isBoxcode(domain.getBoxCode());
        SendTaskBody body = new SendTaskBody();
        if(isSendByBox){
        	body.setHandleCategory(SendTaskCategoryEnum.BOX_SEND.getCode());
        }else{
        	body.setHandleCategory(SendTaskCategoryEnum.PACKAGE_SEND.getCode());
        }
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
        if(isSendByBox){
        	tTask.setSubType(Task.TASK_SUB_TYPE_BOX_SEND);
        }else{
        	tTask.setSubType(Task.TASK_SUB_TYPE_PACKAGE_SEND);
        }
        tTask.setFingerprint(Md5Helper.encode(domain.getSendCode() + "_" + tTask.getKeyword1() + domain.getBoxCode() + tTask.getKeyword1()));
        tTaskService.add(tTask, true);
        //只有箱号添加回传周转箱任务
        if (isSendByBox) {
            // 2回传周转箱号
            tTask.setKeyword1("2");
            tTask.setSubType(null);
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
        log.info("组板发货任务推送成功：板号={}，箱号={}" ,domain.getBoardCode(), domain.getBoxCode());
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

    @Override
    public Integer add(SendDetail sendDetail) {
        if (sendDetail.getPackageBarcode() == null) {
            sendDetail.setPackageNum(0);
        } else {
            Integer packageNum = BusinessUtil.getPackNumByPackCode(sendDetail.getPackageBarcode());
            if (packageNum == null) {
                this.log.warn("无法获得包裹数量[{}]",sendDetail.getPackageBarcode());
                sendDetail.setPackageNum(0);
            } else {
                sendDetail.setPackageNum(packageNum);
            }
        }
        return this.sendDatailDao.add(SendDatailDao.namespace, sendDetail);
    }

    @Override
    public Integer update(SendDetail sendDetail) {
        return this.sendDatailDao.update(SendDatailDao.namespace, sendDetail);
    }

    @JProfiler(jKey = "Bluedragon_dms_center.dms.method.deliveryService.updateCancel", mState = {JProEnum.TP,
            JProEnum.FunctionError})
    public Integer updateCancel(SendDetail sendDetail) {
        return this.sendDatailDao.updateCancel(sendDetail);
    }

    public void saveOrUpdateBatch(List<SendDetail> sdList) {
        CallerInfo umpMonitor = ProfilerHelper.registerInfo("Bluedragon_dms_center.dms.method.deliveryService.saveOrUpdateBatch");
        List<SendDetail>[] sendArray = splitList(sdList);
        List<String> result = new ArrayList<String>();

        List<SendDetail> updateList = new ArrayList<SendDetail>();
        //批量查询是否存在send_d
        for (List<SendDetail> list : sendArray) {
            List<String> boxCodeList = CollectionHelper.joinToList(list,"getBoxCode");
            Integer createSiteCode = list.get(0).getCreateSiteCode();
            Integer receiveSiteCode = list.get(0).getReceiveSiteCode();
            SendDetail request = new SendDetail();
            request.setBoxCodeList(boxCodeList);
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
            List<String> boxCodelist = CollectionHelper.joinToList(list,"getBoxCode");
            Integer createSiteCode = list.get(0).getCreateSiteCode();
            Integer receiveSiteCode = list.get(0).getReceiveSiteCode();
            SendDetail request = new SendDetail();
            request.setBoxCodeList(boxCodelist);
            request.setCreateSiteCode(createSiteCode);
            request.setReceiveSiteCode(receiveSiteCode);
            sendDatailDao.updateCancelBatch(request);
        }
        Profiler.registerInfoEnd(umpMonitor);
    }

    @Override
    public void saveOrUpdate(SendDetail sendDetail) {
        if (Constants.NO_MATCH_DATA == this.update(sendDetail).intValue()) {
            this.add(sendDetail);
        }
    }

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

    @Override
    public Boolean canCancelFuzzy(SendDetail sendDetail) {
        return this.sendDatailDao.canCancelFuzzy(sendDetail);
    }

    /**
     * 发货主表数据写入
     *
     * @param sendMList 发货相关数据
     */
    public void insertSendM(Integer source, List<SendM> sendMList, List<String> list) {

        List<SendM> actualList = Lists.newArrayListWithCapacity(sendMList.size());

        for (SendM dSendM : sendMList) {
            if (!list.contains(dSendM.getBoxCode())) {
                dSendM.setBizSource(source);
                actualList.add(dSendM);
            }
        }

        if (CollectionUtils.isEmpty(actualList)) {
            return;
        }

        sendMManager.batchSaveSendM(actualList);

        this.batchSendMaterialMq(actualList);

    }

    /**
     * 发送发货物资消息
     * @param sendMList
     */
    private void batchSendMaterialMq(List<SendM> sendMList) {
        List<BoxMaterialRelationMQ> list = Lists.newArrayListWithCapacity(sendMList.size());
        for (SendM sdm : sendMList) {
            list.add(makeBoxMaterialRelationFromSendM(sdm));
        }

        cycleMaterialNoticeService.batchSendDeliveryMessage(list);
    }

    /**
     * 发送发货业务通知MQ 自消费
     * @param sdm
     */
     private void deliverGoodsNoticeMQ(SendM sdm) {
         BoxMaterialRelationMQ mq = makeBoxMaterialRelationFromSendM(sdm);

         cycleMaterialNoticeService.deliverySendGoodsMessage(mq);
     }

    /**
     *
     * @param sdm
     * @return
     */
     private BoxMaterialRelationMQ makeBoxMaterialRelationFromSendM(SendM sdm) {
         BoxMaterialRelationMQ mq = new BoxMaterialRelationMQ();
         mq.setBoxCode(sdm.getBoxCode());
         mq.setBusinessType(BoxMaterialRelationEnum.SEND.getType());
         mq.setOperatorName(sdm.getCreateUser());
         mq.setOperatorCode(sdm.getCreateUserCode());
         mq.setSiteCode(sdm.getCreateSiteCode().toString());

         mq.setReceiveSiteCode(sdm.getReceiveSiteCode().longValue());
         BaseStaffSiteOrgDto siteOrgDto = baseMajorManager.getBaseSiteBySiteId(sdm.getReceiveSiteCode());
         mq.setReceiveSiteName(null != siteOrgDto ? siteOrgDto.getSiteName() : StringUtils.EMPTY);

         return mq;
     }

    /***
     * 发货写入任务表
     */
    @Override
    public void addTaskSend(SendM sendM) {
        SendTaskBody body = new SendTaskBody();
        body.setHandleCategory(SendTaskCategoryEnum.BATCH_SEND.getCode());
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
        tTask.setSubType(Task.TASK_SUB_TYPE_BATCH_SEND);
        tTask.setFingerprint(sendM.getSendCode() + "_" + tTask.getKeyword1());
        tTaskService.add(tTask, true);

        tTask.setSubType(null);
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
    private void addOperationLog(SendDetail sendDetail,String siteName,String methodName) {
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
        operationLog.setCreateSiteName(siteName);
        operationLog.setMethodName(methodName);
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
            return this.dellCreateSendM(source, sendMList, false);
        } catch (Exception e) {
            this.log.error("生成发货数据处理异常，sendMList：{}",JsonHelper.toJson(sendMList), e);
            return new DeliveryResponse(DeliveryResponse.CODE_Delivery_ERROR,
                    DeliveryResponse.MESSAGE_Delivery_ERROR);
        }
    }

    /**
     * 生成发货数据处理（带有操作锁）
     *
     * @param source
     * @param sendMList
     * @return
     */
    @Override
    public DeliveryResponse dellDeliveryMessageWithLock(SendBizSourceEnum source, List<SendM> sendMList) {
        if (sendMList == null || sendMList.size() == 0) {
            return new DeliveryResponse(JdResponse.CODE_SENDDATA_GENERATED_EMPTY, JdResponse.MESSAGE_SENDDATA_GENERATED_EMPTY);
        }

        List<String> barCodeList = this.getCodeList(sendMList);
        List<String> processingList = null;
        try {
            // 获取是否存在正在执行中的发货任务 若不存在则加锁
            processingList = this.getProcessingListAndLock(barCodeList, sendMList.get(0).getCreateSiteCode());
            // 不存在正在执行中的
            if (processingList.size() == 0) {
                // 处理发货
                return this.dellCreateSendM(source, sendMList, true);
            }

            if (processingList.size() == sendMList.size()) {
                return new DeliveryResponse(DeliveryResponse.CODE_DELIVERY_ALL_PROCESSING, HintService.getHint(HintCodeConstants.WAYBILL_SEND_IS_PROCESSING));
            }

            // 移除正在处理中的
            this.removeProcessing(sendMList, processingList);
            // 处理发货
            DeliveryResponse response = this.dellCreateSendM(source, sendMList, true);
            if (JdResponse.CODE_OK.equals(response.getCode())) {
                Map<String, String> argsMap = new HashMap<>();
                argsMap.put(HintArgsConstants.ARG_FIRST, String.valueOf(processingList.size()));
                return new DeliveryResponse(DeliveryResponse.CODE_DELIVERY_EXIST_PROCESSING, HintService.getHint(HintCodeConstants.CURRENT_SEND_EXIST_PROCESSING, argsMap));
            }
            return response;
        } catch (Exception e) {
            this.log.error("老发货数据处理异常,sendMList:{}",JsonHelper.toJson(sendMList), e);
            return new DeliveryResponse(DeliveryResponse.CODE_Delivery_ERROR, DeliveryResponse.MESSAGE_Delivery_ERROR);
        } finally {
            // 移除正在执行中的任务
            if (processingList != null && processingList.size() > 0) {
                barCodeList.removeAll(processingList);
            }
            // 解锁已经处理完成的数据
            for (String waybillCode : barCodeList) {
                this.delCacheLock(waybillCode, sendMList.get(0).getCreateSiteCode());
            }
        }
    }

    /**
     * 移除正在处理中的
     *
     * @param sendMList
     * @param processingList
     */
    private void removeProcessing(List<SendM> sendMList, List<String> processingList) {
        Iterator<SendM> iterable = sendMList.iterator();
        while (iterable.hasNext()) {
            SendM sendM = iterable.next();
            if (processingList.contains(sendM.getBoxCode())) {
                iterable.remove();
            }
        }
    }

    /**
     * 处理运单号维度的SendM转换为包裹号维度
     *
     * @param sendMList
     * @return
     */
    private List<SendM> waybillCodeSendMHandler(List<SendM> sendMList) {
        Iterator<SendM> iterator = sendMList.iterator();
        List<SendM> packageCodeSendMList = new ArrayList<>();

        while (iterator.hasNext()) {
            SendM sendM = iterator.next();
            if (WaybillUtil.isWaybillCode(sendM.getBoxCode())) {
                // 移除按照运单发货
                iterator.remove();
                //生成包裹号
                List<String> packageCodes = waybillPackageBarcodeService.getPackageCodeListByWaybillCode(sendM.getBoxCode());
                for (String packageCode : packageCodes) {
                    SendM packageCodeSendM = new SendM();
                    packageCodeSendM.setBoxCode(packageCode);
                    packageCodeSendM.setCreateSiteCode(sendM.getCreateSiteCode());
                    packageCodeSendM.setReceiveSiteCode(sendM.getReceiveSiteCode());
                    packageCodeSendM.setCreateUserCode(sendM.getCreateUserCode());
                    packageCodeSendM.setSendType(sendM.getSendType());
                    packageCodeSendM.setCreateUser(sendM.getCreateUser());
                    packageCodeSendM.setSendCode(sendM.getSendCode());
                    packageCodeSendM.setCreateTime(new Date());
                    packageCodeSendM.setOperateTime(new Date());
                    packageCodeSendM.setYn(1);
                    packageCodeSendM.setTurnoverBoxCode(sendM.getTurnoverBoxCode());
                    packageCodeSendM.setTransporttype(sendM.getTransporttype());
                    packageCodeSendMList.add(packageCodeSendM);
                }
            }
        }
        sendMList.addAll(packageCodeSendMList);
        return sendMList;
    }

    private List<String> getCodeList(List<SendM> sendMList) {
        List<String> codeList = new ArrayList<>(sendMList.size());
        for (SendM sendM : sendMList) {
            codeList.add(sendM.getBoxCode());
        }
        return codeList;
    }

    private String getRedisCacheKey(String barCode, Integer operateSiteCode) {
        return REDIS_CACHE_KEY + operateSiteCode + Constants.SEPARATOR_HYPHEN + barCode;
    }

    /**
     * Redis缓存锁，解决同一运单在未处理完时，再次操作的问题
     *
     * @return
     */
    private List<String> getProcessingListAndLock(List<String> barCodes, Integer operateSiteCode) {
        List<String> processingList = new ArrayList<>();
        try {
            for (String barCode : barCodes) {
                String redisKey = this.getRedisCacheKey(barCode, operateSiteCode);
                // 避免消费数据重复逻辑 插入redis 如果插入失败 说明有其他线程正在消费相同数据信息
                if (!redisClientCache.set(redisKey, "1", REDIS_CACHE_EXPIRE_TIME, TimeUnit.SECONDS, false)) {
                    processingList.add(barCode);
                }
            }
        } catch (Exception e) {
            log.error("[老发货运单处理]设置Redis并发锁时发生异常，barCodes:{}" , barCodes, e);
        }
        return processingList;
    }

    /**
     * 处理完成，删除Redis锁
     *
     * @param barCode
     * @param operateSiteCode
     */
    private void delCacheLock(String barCode, Integer operateSiteCode) {
        String redisKey = this.getRedisCacheKey(barCode, operateSiteCode);
        try {
            redisClientCache.del(redisKey);
        } catch (Exception e) {
            log.error("[老发货运单处理]删除Redis并发锁时发生异常，redisKey:{}" , redisKey, e);
        }
    }

    /**
     * 发货主表数据处理
     *
     * @param source
     * @param sendMList 包裹/箱号/运单发货对象
     * @param sendByWaybill
     * @return
     */
    private DeliveryResponse dellCreateSendM(SendBizSourceEnum source, List<SendM> sendMList, boolean sendByWaybill) {
        CallerInfo info1 = Profiler.registerInfo("Bluedragon_dms_center.dms.method.delivery.send", false, true);

        if(sendMList == null || sendMList.size() < 1){
            return new DeliveryResponse(JdResponse.CODE_SENDDATA_GENERATED_EMPTY,
                    JdResponse.MESSAGE_SENDDATA_GENERATED_EMPTY);
        }

        Collections.sort(sendMList);
        // 批次号封车校验，已封车不能发货
        if (!SendBizSourceEnum.OFFLINE_OLD_SEND.equals(source) && !SendBizSourceEnum.COLD_LOAD_CAR_KY_SEND.equals(source)
                && !SendBizSourceEnum.COLD_LOAD_CAR_SEND.equals(source)) {

            StringBuffer customMsg = new StringBuffer().append(HintService.getHint(HintCodeConstants.SEND_CODE_SEALED_TIPS_SECOND));
            if (newSealVehicleService.newCheckSendCodeSealed(sendMList.get(0).getSendCode(), customMsg)) {
                return new DeliveryResponse(DeliveryResponse.CODE_SEND_CODE_ERROR, customMsg.toString());
            }
        }
        Profiler.registerInfoEnd(info1);

        // 发货异步任务
        if (deliveryOperationService.deliverySendAsyncSwitch(sendMList.get(0).getCreateSiteCode())) {
            DeliveryResponse response = deliveryOperationService.asyncHandleDelivery(sendMList, source);
            if (!DeliveryResponse.CODE_OK.equals(response.getCode())) {
                return response;
            }
        }
        else {

            // 按运单发货转为包裹维度的发货对象
            if (sendByWaybill) {
                this.waybillCodeSendMHandler(sendMList);
            }

            this.deliveryCoreLogic(source.getCode(), sendMList);

            // 写入任务
            addTaskSend(sendMList.get(0));
        }

        return new DeliveryResponse(JdResponse.CODE_OK, JdResponse.MESSAGE_OK);
    }

    /**
     * 发货核心逻辑
     * @param source
     * @param sendMList
     */
    @JProfiler(jKey = "Bluedragon_dms_center.dms.method.deliveryService.deliveryCoreLogic",
            jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
    @Override
    public void deliveryCoreLogic(Integer source, List<SendM> sendMList) {

        /*查询已发货的箱号*/
        List<String> deliveredList = batchQuerySendMList(sendMList);

        CallerInfo info3 = Profiler.registerInfo("Bluedragon_dms_center.dms.method.delivery.cancelStatusReceipt", false, true);
        // 取消发货在发货状态位回执
        this.cancelStatusReceipt(sendMList, deliveredList);
        Profiler.registerInfoEnd(info3);

        CallerInfo info2 = Profiler.registerInfo("Bluedragon_dms_center.dms.method.delivery.send2",Constants.UMP_APP_NAME_DMSWEB, false, true);

        // 写入发货表数据
        this.insertSendM(source, sendMList, deliveredList);

        // 插入中转任务
        this.batchTransitSend(sendMList);

        Profiler.registerInfoEnd(info2);
    }

    @Override
    public DeliveryResponse dealFileBoxBatchSending(SendBizSourceEnum source, List<SendM> sendMList, List<SendM> fileSendMList) {

        CallerInfo callerInfo = Profiler.registerInfo("DMSWEB.DeliveryServiceImpl.dealFileBoxSingleCarSend", Constants.UMP_APP_NAME_DMSWEB, false, true);

        // 获得可以发货的文件箱号
        List<SendM> needSendBox = this.findCouldSendingFileBox(fileSendMList);

        if (CollectionUtils.isEmpty(needSendBox)) {
            return new DeliveryResponse(JdResponse.CODE_OK, JdResponse.MESSAGE_OK);
        }

        long startTime = System.currentTimeMillis();
        // 老发货逻辑
        DeliveryResponse result = this.dellCreateSendM(source, needSendBox, false);
        long endTime = System.currentTimeMillis();

        // 添加Log
        this.addFileSendingBizLog(sendMList, needSendBox, JsonHelper.toJson(result), startTime, endTime);

        Profiler.registerInfoEnd(callerInfo);

        return new DeliveryResponse(JdResponse.CODE_OK, JdResponse.MESSAGE_OK);
    }

    /**
     * 批量处理BC箱号绑定的WJ箱号的发货逻辑
     *
     * <h3>WJ箱号发货逻辑</h3>
     * @param bizSource
     * @param BCSendM
     * @return
     */
    @Override
    public SendResult dealFileBoxSingleCarSend(SendBizSourceEnum bizSource, SendM BCSendM) {
        SendResult result = new SendResult(SendResult.CODE_OK, SendResult.MESSAGE_OK);

        List<SendM> WJSendMList = this.getWJSendMDomains(BCSendM);
        if (CollectionUtils.isEmpty(WJSendMList)) {
            return result;
        }

        CallerInfo callerInfo = Profiler.registerInfo("DMSWEB.DeliveryServiceImpl.dealFileBoxSingleCarSend", Constants.UMP_APP_NAME_DMSWEB, false, true);

        // 获得能够发货的WJ箱号
        List<SendM> needSendBox = this.findCouldSendingFileBox(WJSendMList);

        if (CollectionUtils.isEmpty(needSendBox)) {
            return result;
        }

        // 批量处理新发货逻辑
        long startTime = System.currentTimeMillis();
        for (SendM domain : needSendBox) {
            result = this.doPackageSend(bizSource, domain);
        }
        long endTime = System.currentTimeMillis();

        // 添加Business Log
        this.addFileSendingBizLog(Collections.singletonList(BCSendM), needSendBox, JsonHelper.toJson(result), startTime, endTime);

        Profiler.registerInfoEnd(callerInfo);

        return result;
    }

    /**
     * 文件发货添加Business Log
     * @param BCSendMList
     * @param fileSendMList
     * @param result
     * @param startTime
     * @param endTime
     */
    private void addFileSendingBizLog(List<SendM> BCSendMList, List<SendM> fileSendMList, String result, Long startTime, Long endTime) {
        JSONObject operateRequest = new JSONObject();
        operateRequest.put("bcSendM", JsonHelper.toJson(BCSendMList));
        List<String> fileBoxCodes = Lists.newArrayListWithExpectedSize(fileSendMList.size());
        for (SendM sendBox : fileSendMList) {
            fileBoxCodes.add(sendBox.getBoxCode());
        }
        operateRequest.put("wjBoxCodes", fileBoxCodes);

        BusinessLogProfiler businessLogProfiler=new BusinessLogProfilerBuilder()
                .operateTypeEnum(BusinessLogConstans.OperateTypeEnum.SEND_FILE_BOX)
                .operateRequest(operateRequest)
                .operateResponse(result)
                .processTime(endTime, startTime)
                .methodName("DeliveryServiceImpl#dealFileBoxSending")
                .build();

        logEngine.addLog(businessLogProfiler);
    }

    /**
     * 可以发货的文件箱号
     * <ul>
     *     <li>如果BC绑定的WJ已经发过货，且重复同一个批次号发货，则系统自动跳过，不重复发货，系统不显示提示。</li>
     *     <li>如果BC绑定的WJ已经发过货，上次与本次发货批次号不同，且时间在【封车+1小时】内（老逻辑），
     * 则系统自动取消上次发货，以本次发货为准，系统不显示提示。</li>
     *     <li>如果BC绑定的WJ已经发过货，上次与本次发货批次号不同，且时间在【封车+1小时】以外（老逻辑），
     * 则系统直接按新批次发货，不取消上次发货，系统不显示提示。</li>
     * </ul>
     * @param sendMList WJ箱号发货
     * @return 可发货的WJ箱号
     */
    private List<SendM> findCouldSendingFileBox(List<SendM> sendMList) {
        List<SendM> needSendBox = Lists.newArrayListWithExpectedSize(sendMList.size());

        for (SendM domain : sendMList) {

            SendM lastSendM = this.getRecentSendMByParam(domain.getBoxCode(), domain.getCreateSiteCode(), null, domain.getOperateTime());
            if (lastSendM != null) {
                String lastSendCode = lastSendM.getSendCode();
                // 继续用相同批次发货，不重复发货
                if (ObjectUtils.equals(domain.getSendCode(), lastSendM.getSendCode())) {
                    log.warn("箱子{}已经在批次[" + lastSendCode + "]中发货，不重复发货");
                }
                else {
                    // 用新批次再次发货
                    // 发货时间距离封车时间不超过1个小时
                    if (!this.sendSealTimeIsOverOneHour(lastSendCode, domain.getOperateTime())) {
                        // 取消上次发货
                        this.doCancelLastSend(domain);
                        needSendBox.add(domain);
                    }
                    else {
                        needSendBox.add(domain);
                    }
                }
            }
            else {
                needSendBox.add(domain);
            }
        }

        return needSendBox;
    }

    /**
     * 批量判断箱号是否已经发货，提出公用，减少查询次数
     * *
     */
    private List<String> batchQuerySendMList(List<SendM> sendMList) {
        List<SendM>[] sendArray = splitSendMList(sendMList);
        List<String> result = new ArrayList<>();
        for (List<SendM> list : sendArray) {
            List<String> boxCodelist = CollectionHelper.joinToList(list,"getBoxCode");
            Integer createSiteCode = list.get(0).getCreateSiteCode();
            Integer receiveSiteCode = list.get(0).getReceiveSiteCode();
            SendM request = new SendM();
            request.setBoxCodeList(boxCodelist);
            request.setCreateSiteCode(createSiteCode);
            request.setReceiveSiteCode(receiveSiteCode);
            result.addAll(sendMDao.batchQuerySendMList(request));
        }

        return result;
    }

    /***
     * 补全包裹信息
     */
    private void fillPickup(SendDetail tSendDatail, SendM tsendM) {
        CallerInfo umpMonitor = ProfilerHelper.registerInfo("Bluedragon_dms_center.dms.method.deliveryService.fillPickup");
        tSendDatail.setCreateUser(tsendM.getCreateUser());
        tSendDatail.setCreateUserCode(tsendM.getCreateUserCode());
        tSendDatail.setSendType(tsendM.getSendType());
        tSendDatail.setStatus(0);
        tSendDatail.setIsCancel(Constants.OPERATE_TYPE_CANCEL_L);
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
                    this.log.error("调用取件单号信息ws接口异常，单号：{}",tsendM.getBoxCode());
                }
                if (pickup != null && pickup.getData() != null) {
                    tSendDatail.setPickupCode(pickup.getData().getPickupCode());
                    tSendDatail.setPackageBarcode(tsendM.getBoxCode());
                    tSendDatail.setWaybillCode(pickup.getData().getOldWaybillCode());
                }
            }
        }
        Profiler.registerInfoEnd(umpMonitor);
    }

    /**
     * 次发货数据状态更新,添加批量操作
     *
     * @param sendMList 待发货列表
     * @param deliveredList      已发货的箱号列表
     */
    private void cancelStatusReceipt(List<SendM> sendMList, List<String> deliveredList) {
        //操作过取消发货的箱子查询  result结果集
        List<SendM>[] sendArray = splitSendMList(sendMList);
        List<String> result = new ArrayList<String>();
        for (List<SendM> slist : sendArray) {
            List<String> boxCodeList = CollectionHelper.joinToList(slist,"getBoxCode");
            Integer createSiteCode = slist.get(0).getCreateSiteCode();
            Integer receiveSiteCode = slist.get(0).getReceiveSiteCode();
            SendM request = new SendM();
            request.setBoxCodeList(boxCodeList);
            request.setCreateSiteCode(createSiteCode);
            request.setReceiveSiteCode(receiveSiteCode);
            result.addAll(sendMDao.batchQueryCancelSendMList(request));
        }
        List<SendDetail> sdList = new ArrayList<>();

        for (SendM tSendM : sendMList) {
            SendDetail tSendDetail = new SendDetail();
            tSendDetail.setBoxCode(tSendM.getBoxCode());
            tSendDetail.setCreateSiteCode(tSendM.getCreateSiteCode());
            tSendDetail.setReceiveSiteCode(tSendM.getReceiveSiteCode());

            // 未操作过发货的或者发货已取消的
            if (!deliveredList.contains(tSendM.getBoxCode())) {
                // 箱号并且取消发货的
                if (BusinessHelper.isBoxcode(tSendM.getBoxCode()) && result.contains(tSendM.getBoxCode())) {
                    tSendDetail.setStatus(2);
                    CallerInfo umpMonitor = ProfilerHelper.registerInfo("Bluedragon_dms_center.dms.method.deliveryService.cancelStatusReceipt.updateCancel");
                    // 重置包裹信息发货状态
                    this.updateCancel(tSendDetail);
                    Profiler.registerInfoEnd(umpMonitor);
                }
                // 包裹号或者面单号
                if (WaybillUtil.isPackageCode(tSendM.getBoxCode())
                        || WaybillUtil.isSurfaceCode(tSendM.getBoxCode())) {
                    // 补全包裹信息
                    this.fillPickup(tSendDetail, tSendM);
                    tSendDetail.setOperateTime(tSendM.getCreateTime());
                    sdList.add(tSendDetail);
                }
            }
        }

        //批量处理
        if (sdList != null && !sdList.isEmpty()) {
            this.saveOrUpdateBatch(sdList);
        }

    }

    @Override
    public DeliveryResponse findSendMByBoxCode(SendM tSendM, boolean isTransferSend, Integer opType) {
        DeliveryResponse response = deliveryCheckHasSend(tSendM);

        if (JdResponse.CODE_OK.equals(response.getCode())) {
            response = checkBoxSendProcessing(tSendM);
        }

        if (JdResponse.CODE_OK.equals(response.getCode())) {
            response = waybillWasteCheck(tSendM);
        }

        // 文件包裹必须装箱才能发货
        if (JdResponse.CODE_OK.equals(response.getCode())) {
            // 不校验快运发货
            if (!ObjectUtils.equals(Constants.KY_DELIVERY, opType)) {

                response = this.filePackSendByBox(tSendM);
            }
        }

        if (JdResponse.CODE_OK.equals(response.getCode())) {
            response = zeroWeightAndVolumeCheck(tSendM);
        }
        if (JdResponse.CODE_OK.equals(response.getCode())) {
            response = deliveryCheckTransit(tSendM, isTransferSend);
        }
        if (JdResponse.CODE_OK.equals(response.getCode())) {
            response = threeDeliveryCheck(tSendM);
        }

        return response;
    }

    /**
     * 符合文件标识的包裹必须按箱发货
     * @param tSendM
     * @return
     */
    private DeliveryResponse filePackSendByBox(SendM tSendM) {
        DeliveryResponse response = new DeliveryResponse(JdResponse.CODE_OK, JdResponse.MESSAGE_OK);

        // 未配置不拦截
        if (! funcSwitchConfigService.getFuncStatusByAllDimension(FuncSwitchConfigEnum.FUNCTION_FILE_INTERCEPTION.getCode(),
                tSendM.getCreateSiteCode(), null)) {
            return response;
        }

        String waybillCode = null;
        if (WaybillUtil.isPackageCode(tSendM.getBoxCode())) {
            waybillCode = WaybillUtil.getWaybillCode(tSendM.getBoxCode());
        }
        else if (WaybillUtil.isWaybillCode(tSendM.getBoxCode())) {
            waybillCode = tSendM.getBoxCode();
        }

        // 未按箱发货
        if (StringUtils.isNotBlank(waybillCode)) {

            WaybillCache waybillCache = waybillCacheService.getFromCache(waybillCode);

            if (null != waybillCache) {

                Site site = siteService.get(tSendM.getCreateSiteCode());

                // 判断运单是否是文件
                if (waybillService.allowFilePackFilter(site.getSubType(), waybillCache.getWaybillSign())) {

                    if (log.isWarnEnabled()) {
                        log.warn("文件包裹禁止按包裹发货. packageBarCode:{}", tSendM.getBoxCode());
                    }

                    response.setCode(DeliveryResponse.CODE_20020);
                    response.setMessage(HintService.getHint(HintCodeConstants.FILE_SEND_WITHOUT_BOX));

                    return response;
                }
            }
        }

        return response;
    }

    /**
     * 弃件检测
     * @param tSendM
     * @return
     */
    private DeliveryResponse waybillWasteCheck(SendM tSendM){
        DeliveryResponse response = new DeliveryResponse(JdResponse.CODE_OK, JdResponse.MESSAGE_OK);
        if (null == tSendM || StringUtils.isEmpty(tSendM.getBoxCode())){
            return response;
        }

        if(WaybillUtil.isPackageCode(tSendM.getBoxCode()) || WaybillUtil.isWaybillCode(tSendM.getBoxCode())){
            String waybill=tSendM.getBoxCode();
            if(WaybillUtil.isPackageCode(tSendM.getBoxCode())){
                waybill=WaybillUtil.getWaybillCode(tSendM.getBoxCode());
            }
            if (waybillTraceManager.isWaybillWaste(waybill)){
                response.setCode(DeliveryResponse.CODE_WAYBILL_IS_WASTE);
                response.setMessage(HintService.getHint(HintCodeConstants.WASTE_WAYBILL_TEMP_STORE));
            }
        }

        return response;
    }

    /**
     * 0重量和体积校验
     * 目前指针对一下单子拦截：众邮
     * @param tSendM
     * @return
     */
    private DeliveryResponse zeroWeightAndVolumeCheck(SendM tSendM) {
        DeliveryResponse response = new DeliveryResponse(JdResponse.CODE_OK, JdResponse.MESSAGE_OK);
        //限定范围
        //实发网点类型为经济网 10000 为众邮箱号
        if (null == tSendM || StringUtils.isEmpty(tSendM.getBoxCode())){
            return response;
        }
        Box box = boxService.findBoxByCode(tSendM.getBoxCode());
        if(null == box){
            return response;
        }
        BaseStaffSiteOrgDto yrDto = this.baseMajorManager.getBaseSiteBySiteId(box.getCreateSiteCode());
        if (!SiteHelper.isEconomicNet(yrDto)){
            return response;
        }
        //查询箱重量和体积
        List<PackageWeighting> packageWeightings = packageWeightingDao.findWeightVolume(tSendM.getBoxCode(),tSendM.getBoxCode(),Arrays.asList(BusinessTypeEnum.DMS.getCode()));

        //判断
        if (CollectionUtils.isEmpty(packageWeightings) ||
                packageWeightings.get(0).getWeight().compareTo(0.0) <= 0 ||
                packageWeightings.get(0).getVolume().compareTo(0.0) <= 0){
            response.setCode(DeliveryResponse.CODE_CANCELDELIVERYCHECK_ZERO_WEIGHT_VOLUME);
            response.setMessage(HintService.getHint(HintCodeConstants.ZY_BOX_MISSING_WEIGHT_OR_VOLUME));
            // 待用，整箱未称时提交拦截报表
            this.sendBusinessInterceptMsg(tSendM, response);
        }
        return response;
    }

    /**
     * 发送拦截消息
     * @param deliveryRequest 请求参数
     * @param result 校验结果
     * @return 处理结果
     * @author fanggang7
     * @time 2020-12-22 18:18:15 周二
     */
    private boolean sendBusinessInterceptMsg(SendM deliveryRequest, DeliveryResponse result){
        log.info("DeliveryServiceImpl sendBusinessInterceptMsg param {}, {}", JSON.toJSONString(deliveryRequest), JSON.toJSONString(result));
        try {
            SaveInterceptMsgDto saveInterceptMsgDto = new SaveInterceptMsgDto();
            saveInterceptMsgDto.setInterceptCode(result.getCode());
            saveInterceptMsgDto.setInterceptMessage(result.getMessage());
            saveInterceptMsgDto.setBarCode(deliveryRequest.getBoxCode());
            saveInterceptMsgDto.setSiteCode(deliveryRequest.getCreateSiteCode());
            saveInterceptMsgDto.setDeviceType(businessInterceptConfigHelper.getOperateDeviceTypeByConstants(OperateDeviceTypeConstants.PDA));
            saveInterceptMsgDto.setDeviceCode(Constant.DEVICE_CODE_PDA);
            long operateTimeMillis = System.currentTimeMillis();
            if(deliveryRequest.getOperateTime() != null){
                operateTimeMillis = deliveryRequest.getOperateTime().getTime();
            }
            saveInterceptMsgDto.setOperateTime(operateTimeMillis);
            saveInterceptMsgDto.setOperateNode(businessInterceptConfigHelper.getOperateNodeByConstants(OperateNodeConstants.SEND));
            saveInterceptMsgDto.setOperateUserCode(deliveryRequest.getCreateUserCode());
            saveInterceptMsgDto.setOperateUserName(deliveryRequest.getCreateUser());

            try {
                businessInterceptReportService.sendInterceptMsg(saveInterceptMsgDto);
            } catch (Exception e) {
                String saveInterceptMqMsg = JSON.toJSONString(saveInterceptMsgDto);
                log.error("DeliveryServiceImpl.sendBusinessInterceptMsg call sendInterceptMsg exception [{}]" , saveInterceptMqMsg, e);
                return false;
            }
        } catch (Exception e) {
            log.error("DeliveryServiceImpl.sendBusinessInterceptMsg call sendInterceptMsg exception [{}]" , e.getMessage(), e);
        }
        return true;
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
                    tsendDatail.setIsCancel(Constants.OPERATE_TYPE_CANCEL_Y);
                    List<SendDetail> sendDatailist = this.sendDatailDao.querySendDatailsBySelective(tsendDatail);
                    if (sendDatailist != null && !sendDatailist.isEmpty()) {
                        response.setMessage(StringHelper.getStringValue(sendDatailist.size()));
                    }
                    if (inspectionExceptionService.queryExceptions(tSendM.getCreateSiteCode(),tSendM.getReceiveSiteCode(),tSendM.getBoxCode()) > 0) {
                        // 第三方发货验证是否存在异常
                        response.setCode(DeliveryResponse.CODE_Delivery_ALL_CHECK);
                        response.setMessage(HintService.getHint(HintCodeConstants.THIRD_BOX_INSPECTION_EXCEPTION));
                    }
                }
            } else {
                response.setCode(DeliveryResponse.CODE_Delivery_NO_MESAGE);
                response.setMessage(HintService.getHint(HintCodeConstants.BOX_SENDM_MISSING));
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
                    response.setMessage(HintService.getHint(HintCodeConstants.BOX_DESTINATION_AND_RECEVIE_SITE_DIFFERENCE));
                }
            } else {
                response.setCode(DeliveryResponse.CODE_Delivery_NO_MESAGE);
                response.setMessage(HintService.getHint(HintCodeConstants.BOX_SENDM_MISSING));
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
                    response.setMessage(HintService.getHint(HintCodeConstants.BOX_DESTINATION_AND_RECEVIE_SITE_DIFFERENCE));
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
                response.setMessage(HintService.getHint(HintCodeConstants.BOX_SENT_ALREADY));
            }
        } else if (WaybillUtil.isPackageCode(tSendM.getBoxCode())) {
            // 再投标识
            tSendMList = this.sendMDao.findSendMByBoxCode(tSendM);
            if (tSendMList != null && !tSendMList.isEmpty()) {
                response.setCode(DeliveryResponse.CODE_Delivery_IS_SEND);
                response.setMessage(HintService.getHint(HintCodeConstants.BOX_SENT_ALREADY));
            }
        }

        return response;
    }

    /**
     * 生成取消发货数据处理
     *  hit：
     *      1、操作时间取实操时间
     *      2、取消发货和发货的时间都用的是操作时间
     * @param tSendM
     * @param needSendMQ
     * @return
     */
    @Override
    public ThreeDeliveryResponse dellCancelDeliveryMessageWithOperateTime(SendM tSendM, boolean needSendMQ) {
        return dellCancelDeliveryMessage(tSendM, needSendMQ);
    }

    /**
     * 生成取消发货数据处理
     *  hit：
     *      1、操作时间取服务器时间
     *      2、取消发货和发货的时间都用的是服务器时间
     * @param tSendM
     * @param needSendMQ
     * @return
     */
    @Override
    @JProfiler(jKey = "DMSWEB.DeliveryService.dellCancelDeliveryMessageWithServerTime")
    public ThreeDeliveryResponse dellCancelDeliveryMessageWithServerTime(SendM tSendM, boolean needSendMQ) {
        tSendM.setOperateTime(new Date());
        return dellCancelDeliveryMessage(tSendM, needSendMQ);
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
            CallerInfo callerInfo = null;
            SendDetail tSendDetail = new SendDetail();
            tSendDetail.setBoxCode(tSendM.getBoxCode());
            tSendDetail.setCreateSiteCode(tSendM.getCreateSiteCode());
            // 按照运单取消处理
            if (WaybillUtil.isWaybillCode(tSendM.getBoxCode()) || WaybillUtil.isSurfaceCode(tSendM.getBoxCode()) || WaybillUtil.isPackageCode(tSendM.getBoxCode())) {
                callerInfo = Profiler.registerInfo("DMSWEB.DeliveryService.dellCancel.waybillCode",Constants.SYSTEM_CODE_WEB,false,true);
                // 判断 按运单发货在处理中，则稍后再试
                if (isSendByWaybillProcessing(tSendM) || sendByWaybillProcessing(tSendM)) {
                    return new ThreeDeliveryResponse(
                            DeliveryResponse.CODE_DELIVERY_BY_WAYBILL_PROCESSING,
                            HintService.getHint(HintCodeConstants.DELIVERY_BY_WAYBILL_PROCESSING), null);
                }
                SendDetail mSendDetail = new SendDetail();
                if (WaybillUtil.isWaybillCode(tSendM.getBoxCode())) {
                    mSendDetail.setWaybillCode(tSendM.getBoxCode());
                } else {
                    mSendDetail.setPackageBarcode(tSendM.getBoxCode());
                }
                mSendDetail.setCreateSiteCode(tSendM.getCreateSiteCode());
                mSendDetail.setReceiveSiteCode(tSendM.getReceiveSiteCode());
                mSendDetail.setIsCancel(Constants.OPERATE_TYPE_CANCEL_L);
                List<SendDetail> tlist = this.sendDatailDao.querySendDatailsBySelective(mSendDetail);
                tSendM.setCancelPackageCount(tlist.size());
                if (tlist != null && !tlist.isEmpty()) {
                    ThreeDeliveryResponse responsePack = cancelUpdateDataByPack(tSendM, tlist);
                    if (responsePack.getCode().equals(200)) {
                        delDeliveryFromRedis(tSendM);      //取消发货成功，删除redis缓存的发货数据
                        sendMessage(tlist, tSendM, needSendMQ);
                        //同步取消半退明细
                        reversePartDetailService.cancelPartSend(tSendM);
                        // 更新包裹装车记录表的扫描状态为取消扫描状态
                        updateScanActionByPackageCodes(tlist, tSendM);
                    }
                    Profiler.registerInfoEnd(callerInfo);
					return responsePack;
				} else {
                    Profiler.registerInfoEnd(callerInfo);
					return new ThreeDeliveryResponse(
							DeliveryResponse.CODE_Delivery_NO_MESAGE,
							HintService.getHint(HintCodeConstants.PACKAGE_SENDM_MISSING), null);
				}
			} else if (BusinessHelper.isBoxcode(tSendM.getBoxCode())) {
                callerInfo = Profiler.registerInfo("DMSWEB.DeliveryService.dellCancel.boxCode",Constants.SYSTEM_CODE_WEB,false,true);
				List<SendM> sendMList = this.sendMDao.findSendMByBoxCode2(tSendM);
                ThreeDeliveryResponse threeDeliveryResponse = cancelUpdateDataByBox(tSendM, tSendDetail, sendMList);
                if (threeDeliveryResponse.getCode().equals(200)) {
                    SendM dSendM = this.getLastSendDate(sendMList);
                    SendDetail queryDetail = new SendDetail();
                    queryDetail.setBoxCode(dSendM.getBoxCode());
                    queryDetail.setCreateSiteCode(dSendM.getCreateSiteCode());
                    queryDetail.setReceiveSiteCode(dSendM.getReceiveSiteCode());
                    List<SendDetail> sendDatails = sendDatailDao.querySendDatailsBySelective(queryDetail);
                    tSendM.setCancelPackageCount(sendDatails.size());
                    delDeliveryFromRedis(tSendM);     //取消发货成功，删除redis缓存的发货数据
                    //更新箱号状态
                    openBox(tSendM);
                    sendMessage(sendDatails, tSendM, needSendMQ);
                }
                Profiler.registerInfoEnd(callerInfo);
                return threeDeliveryResponse;
            } else if (BusinessUtil.isBoardCode(tSendM.getBoxCode())){
                callerInfo = Profiler.registerInfo("DMSWEB.DeliveryService.dellCancel.boardCode",Constants.SYSTEM_CODE_WEB,false,true);
                tSendM.setBoardCode(tSendM.getBoxCode());
                //1.组板发货批次，板号校验（强校验）
                if(!checkSendM(tSendM)){
                    log.info("按板取消发货checkSendM===批次号始发ID与操作人所属单位ID不一致");
                    return new ThreeDeliveryResponse(
                            DeliveryResponse.CODE_SEND_SITE_NOTMATCH__ERROR,
                            DeliveryResponse.MESSAGE_SEND_SITE_NOTMATCH_ERROR,null);
                }
                //2.校板是否已经发车
                if(checkBoardIsDepartured(tSendM)){
                    log.info("按板取消发货checkBoardIsDepartured=== 板号已操作发车，不能取消发货");
                    return new ThreeDeliveryResponse(
                            DeliveryResponse.CODE_BOARD_SENDED_ERROR,
                            HintService.getHint(HintCodeConstants.ABORT_CANCEL_AFTER_BOARD_SENT),null);
                }

                //3.校验是否有板号对应的发货数据
                List<String> sendMList = sendMDao.selectBoxCodeByBoardCodeAndSendCode(tSendM);
                if(sendMList == null || sendMList.size()<1){
                    log.info("按板取消发货==========没有找到板号的发货明细");
                    //提示没有找到板号的发货明细
                    return new ThreeDeliveryResponse(
                            DeliveryResponse.CODE_NO_BOARDSEND_DETAIL_ERROR,
                            HintService.getHint(HintCodeConstants.BOARD_SENDM_MISSING), null);
                }
                //4.校验是否有同一板号的发货任务没有跑完
                String redisKey = REDIS_PREFIX_BOARD_DELIVERY + tSendM.getBoxCode();
                if(StringHelper.isNotEmpty(redisManager.get(redisKey))){
                    log.info("按板取消发货==========存在有同一板号的发货任务没有跑完");
                    return new ThreeDeliveryResponse(
                            DeliveryResponse.CODE_BOARD_SEND_NOT_FINISH_ERROR,
                            HintService.getHint(HintCodeConstants.BOARD_DELIVERY_PROCESSING),null);
                }
                //生产一个按板号取消发货的任务
                pushBoardSendTask(tSendM,Task.TASK_TYPE_BOARD_SEND_CANCEL);
                log.info("按板取消发货==========pushBoardSendTask");
                //将板由“关闭”状态变为“组板中”的状态
                List<String> boardList = new ArrayList<>();
                boardList.add(tSendM.getBoardCode());
                changeBoardStatus(tSendM,boardList);
                log.info("按板取消发货==========将板由“关闭”状态变为“组板中”的状态");
                // 更新包裹装车记录表的扫描状态为取消扫描状态
                updateScanActionByBoardCode(tSendM);
                log.info("按板取消发货==========更新包裹装车记录表的扫描状态为取消扫描状态");
                Profiler.registerInfoEnd(callerInfo);
                return new ThreeDeliveryResponse(JdResponse.CODE_OK, JdResponse.MESSAGE_OK, null);
            } else if (BusinessHelper.isSendCode(tSendM.getSendCode()) && tSendM.getCreateSiteCode() != null) {
                callerInfo = Profiler.registerInfo("DMS.WEB.deliveryService.cancelBySendCode",Constants.SYSTEM_CODE_WEB,false,true);
                /* 请求参数中只有sendCode参数和createSiteCode参数有效 */
                SendDetail sendDetailRequest = new SendDetail();
                sendDetailRequest.setCreateSiteCode(tSendM.getCreateSiteCode());
                sendDetailRequest.setSendCode(tSendM.getSendCode());
                sendDetailRequest.setIsCancel(Constants.OPERATE_TYPE_CANCEL_L);
                List<SendM> sendMList = this.sendMDao.selectBySiteAndSendCode(tSendM.getCreateSiteCode(),tSendM.getSendCode());
                if (sendMList == null || sendMList.isEmpty()) {
                    return new ThreeDeliveryResponse(DeliveryResponse.CODE_Delivery_NO_MESAGE,
                            HintService.getHint(HintCodeConstants.QUERY_DELIVERY_BY_BATCH_MISSING), null);
                }
                Set<String> boardSet = new TreeSet<>();
                /* 循环处理明细数据，分包裹和箱号两种，按批次号取消的场景大循环需要注意 */
                for (SendM sendMItem : sendMList) {
                    sendMItem.setOperateTime(tSendM.getOperateTime());
                    sendMItem.setUpdateTime(tSendM.getUpdateTime());
                    sendMItem.setUpdaterUser(tSendM.getUpdaterUser());
                    sendMItem.setUpdateUserCode(tSendM.getUpdateUserCode());

                    //将板号添加到板号集合中
                    if(StringUtils.isNotBlank(sendMItem.getBoardCode())){
                        boardSet.add(sendMItem.getBoardCode());
                    }

                    /* 根据sendM组装sendD请求条件 */
                    SendDetail mSendDetail = new SendDetail();
                    if (WaybillUtil.isWaybillCode(sendMItem.getBoxCode())) {
                        mSendDetail.setWaybillCode(sendMItem.getBoxCode());
                    } else if (WaybillUtil.isPackageCode(sendMItem.getBoxCode())){
                        mSendDetail.setPackageBarcode(sendMItem.getBoxCode());
                    } else {
                        mSendDetail.setBoxCode(sendMItem.getBoxCode());
                    }
                    mSendDetail.setCreateSiteCode(sendMItem.getCreateSiteCode());
                    mSendDetail.setReceiveSiteCode(sendMItem.getReceiveSiteCode());
                    mSendDetail.setIsCancel(Constants.OPERATE_TYPE_CANCEL_L);
                    List<SendDetail> tlist = this.sendDatailDao.querySendDatailsBySelective(mSendDetail);//查询sendD明细
                    tSendM.setCancelPackageCount(tlist.size());
                    if (WaybillUtil.isWaybillCode(sendMItem.getBoxCode()) || WaybillUtil.isPackageCode(sendMItem.getBoxCode())) {
                        /* 按包裹号和运单号的逻辑走 */
                        ThreeDeliveryResponse responsePack = cancelUpdateDataByPack(sendMItem, tlist);
                        if (responsePack.getCode().equals(200)) {
                            reversePartDetailService.cancelPartSend(sendMItem);//同步取消半退明细
                        } else {
                            continue;
                        }
                    } else if (BusinessHelper.isBoxcode(sendMItem.getBoxCode())) {
                        /* 按箱号的逻辑走 */
                        List<SendM> sendMs = new ArrayList<>();
                        sendMs.add(sendMItem);
                        ThreeDeliveryResponse threeDeliveryResponse = cancelUpdateDataByBox(sendMItem, mSendDetail, sendMs);
                        if (threeDeliveryResponse.getCode().equals(200)) {
                            /* 更新箱号缓存状态 */
                            openBox(sendMItem);
                        } else {
                            continue;
                        }
                    } else {
                        log.info("该发货明细不属于按运单按包裹按箱号发货范畴：{}" , JsonHelper.toJson(sendMItem));
                        continue;
                    }
                    if (!ObjectHelper.isNotNull(sendMItem.getUpdaterUser())){
                        sendMItem.setUpdaterUser(tSendM.getUpdaterUser());
                    }
                    if (!ObjectHelper.isNotNull(sendMItem.getUpdateUserCode())){
                        sendMItem.setUpdateUserCode(tSendM.getUpdateUserCode());
                    }
                    sendMessage(tlist, sendMItem, needSendMQ);
                    delDeliveryFromRedis(sendMItem);//取消发货成功，删除redis缓存的发货数据 根据boxCode和createSiteCode
                }
                // 更新包裹装车记录表的扫描状态为取消扫描状态
                updateScanActionByBatchCode(tSendM);
                //将板号的集合转换成String类型的列表
                if(CollectionUtils.isNotEmpty(boardSet)){
                    List<String> boardList = new CollectionHelper<String>().toList(boardSet);
                    changeBoardStatus(tSendM,boardList);
                }
                Profiler.registerInfoEnd(callerInfo);
                return new ThreeDeliveryResponse(JdResponse.CODE_OK, JdResponse.MESSAGE_OK, null);
            }
            // 改变箱子状态为分拣
        } catch (Exception e) {
            log.error("取消发货时异常", e);
            return new ThreeDeliveryResponse(
                    DeliveryResponse.CODE_Delivery_ERROR,
                    DeliveryResponse.MESSAGE_Delivery_ERROR, null);
        }
        return new ThreeDeliveryResponse(
                DeliveryResponse.CODE_Delivery_NO_MESAGE,
                DeliveryResponse.MESSAGE_Delivery_NO_REQUEST, null);
    }

    /**
     * 由于取消发货，打开箱
     * @param tSendM
     */
    private void openBox(SendM tSendM){
        //参数构建
        BoxReq boxReq = new BoxReq();
        boxReq.setBoxCode(tSendM.getBoxCode());
        boxReq.setBoxStatus(BoxStatusEnum.OPEN.getStatus());
        boxReq.setOpNodeCode(OpBoxNodeEnum.CANCELSEND.getNodeCode());
        boxReq.setOpNodeName(OpBoxNodeEnum.CANCELSEND.getNodeName());
        boxReq.setOpSiteCode(tSendM.getCreateSiteCode());
        boxReq.setOpSiteName("");
        boxReq.setOpErp(tSendM.getCreateUser());
        boxReq.setOpTime(tSendM.getOperateTime());
        boxReq.setOpDescription(String.format("%s操作取消发货，打开此箱号%s的箱子", tSendM.getCreateUser(),tSendM.getBoxCode()));
        //修改箱状态
        boxService.updateBoxStatus(boxReq);
    }

    /**
     * 取消发货校验封车业务
     * 1、已解封车不允许取消发货
     * 2、封车超过一小时不允许取消发货
     * 3、取消发货后批次内货物不允许为空
     * @param tSendM
     * @return
     */
    @JProfiler(jKey = "DMSWEB.DeliveryService.dellCancelDeliveryCheckSealCar", mState = JProEnum.TP, jAppName = Constants.UMP_APP_NAME_DMSWEB)
    @Override
    public DeliveryResponse dellCancelDeliveryCheckSealCar(SendM tSendM) {
        try {

            String isCheck=uccPropertyConfiguration.getDellCancelDeliveryCheckSealCar();
            if(Constants.STRING_FLG_FALSE.equals(isCheck)){
                return new DeliveryResponse(JdResponse.CODE_OK, JdResponse.MESSAGE_OK);
            }

            boolean isPackageCode=WaybillUtil.isPackageCode(tSendM.getBoxCode());
            boolean isWaybillCode=WaybillUtil.isWaybillCode(tSendM.getBoxCode());
            boolean isSurfaceCode=WaybillUtil.isSurfaceCode(tSendM.getBoxCode());
            boolean isBoxcode=BusinessHelper.isBoxcode(tSendM.getBoxCode());
            boolean isBoardCode=BusinessUtil.isBoardCode(tSendM.getBoxCode());

            // 按照运单取消处理
            if (isPackageCode || isWaybillCode || isSurfaceCode) {
                SendDetail sendDetail = new SendDetail();
                if (isWaybillCode) {
                    sendDetail.setWaybillCode(tSendM.getBoxCode());
                } else {
                    sendDetail.setPackageBarcode(tSendM.getBoxCode());
                }
                sendDetail.setCreateSiteCode(tSendM.getCreateSiteCode());
                sendDetail.setReceiveSiteCode(tSendM.getReceiveSiteCode());
                sendDetail.setIsCancel(Constants.OPERATE_TYPE_CANCEL_L);
                //查询Send_d表中的批次号
                String sendCode = this.sendDatailDao.querySendCodeBySelective(sendDetail);
                if (StringHelper.isEmpty(sendCode)) {
                    if(isWaybillCode){
                        return new DeliveryResponse(DeliveryResponse.CODE_Delivery_NO_MESAGE,
                                HintService.getHint(HintCodeConstants.WAYBILL_SENDM_MISSING));
                    }else {
                        return new DeliveryResponse(DeliveryResponse.CODE_Delivery_NO_MESAGE,
                                HintService.getHint(HintCodeConstants.PACKAGE_SENDM_MISSING));
                    }
                }

                sendDetail.setSendCode(sendCode);
                return checkAllow(sendDetail,isPackageCode || isSurfaceCode,isWaybillCode,isBoxcode);
            } else if (isBoxcode) {
                //查询Send_m表中的批次号
                String sendCode = this.sendMDao.querySendCodeBySelective(tSendM);
                if (StringHelper.isEmpty(sendCode)) {
                    return new DeliveryResponse(DeliveryResponse.CODE_Delivery_NO_MESAGE,
                            HintService.getHint(HintCodeConstants.BOX_SENDM_MISSING));
                }

                SendDetail sendDetail = new SendDetail();
                sendDetail.setBoxCode(tSendM.getBoxCode());
                sendDetail.setSendCode(sendCode);
                sendDetail.setCreateSiteCode(tSendM.getCreateSiteCode());
                sendDetail.setReceiveSiteCode(tSendM.getReceiveSiteCode());
                sendDetail.setIsCancel(Constants.OPERATE_TYPE_CANCEL_L);
                return checkAllow(sendDetail,isPackageCode || isSurfaceCode,isWaybillCode,isBoxcode);
            } else if (isBoardCode){
                tSendM.setBoardCode(tSendM.getBoxCode());
                DeliveryResponse checkResponse = sealCarCheck(tSendM.getSendCode());
                //如果是未封车，则直接返回不拦截
                if (checkResponse.getCode().equals(DeliveryResponse.CODE_CANCELDELIVERYCHECK_NOSEAL)) {
                    return new DeliveryResponse(JdResponse.CODE_OK, JdResponse.MESSAGE_OK);
                }
                //如果封车状态校验不通过，则返回拦截
                if (!checkResponse.getCode().equals(JdResponse.CODE_OK)) {
                    return new DeliveryResponse(checkResponse.getCode(),checkResponse.getMessage());
                }

                List<String> sendMList = sendMDao.selectBoxCodeByBoardCodeAndSendCode(tSendM);
                if(sendMList == null || sendMList.isEmpty()){
                    //提示没有找到板号的发货明细
                    return new DeliveryResponse(DeliveryResponse.CODE_Delivery_NO_MESAGE,
                            HintService.getHint(HintCodeConstants.BOARD_SENDM_MISSING));
                }

                SendDetail sendDetail = new SendDetail();
                sendDetail.setSendCode(tSendM.getSendCode());
                sendDetail.setCreateSiteCode(tSendM.getCreateSiteCode());
                sendDetail.setReceiveSiteCode(tSendM.getReceiveSiteCode());
                sendDetail.setIsCancel(Constants.OPERATE_TYPE_CANCEL_L);

                List<String> sendDList=sendDatailDao.queryBoxCodeSingleBySendCode(sendDetail);
                if(sendDList == null || sendDList.isEmpty()){
                    //提示没有找到板号的发货明细
                    return new DeliveryResponse(DeliveryResponse.CODE_Delivery_NO_MESAGE,
                            HintService.getHint(HintCodeConstants.BOARD_SENDM_MISSING));
                }

                sendDList.removeAll(sendMList);
                if(sendDList == null || sendDList.isEmpty()){
                    return new DeliveryResponse(DeliveryResponse.CODE_CANCELDELIVERYCHECK_ONLY, HintService.getHint(HintCodeConstants.UNIQUE_BOARD_IN_THE_BATCH));
                }

                return new DeliveryResponse(JdResponse.CODE_OK, JdResponse.MESSAGE_OK);
            }
        } catch (Exception e) {
            log.error("取消发货校验封车异常", e);
            return new DeliveryResponse(DeliveryResponse.CODE_CANCELDELIVERYCHECK_ERROR,
                    HintService.getHint(HintCodeConstants.ABORT_DELIVERY_CHECK_SEAL_ERROR));
        }

        return new DeliveryResponse(DeliveryResponse.CODE_CANCELDELIVERYCHECK_ERROR,
                HintService.getHint(HintCodeConstants.ABORT_DELIVERY_CHECK_SEAL_ERROR));
    }

    /**
     * 校验是否允许取消发货
     * @param querySendDatail
     * @param isPackageCode
     * @param isWaybillCode
     * @param isBoxcode
     * @return
     */
    public DeliveryResponse checkAllow(SendDetail querySendDatail,boolean isPackageCode,boolean isWaybillCode,boolean isBoxcode){
        DeliveryResponse checkResponse = sealCarCheck(querySendDatail.getSendCode());
        //如果是未封车，则直接返回不拦截
        if (checkResponse.getCode().equals(DeliveryResponse.CODE_CANCELDELIVERYCHECK_NOSEAL)) {
            return new DeliveryResponse(JdResponse.CODE_OK, JdResponse.MESSAGE_OK);
        }
        //如果封车状态校验不通过，则返回拦截
        if (!checkResponse.getCode().equals(JdResponse.CODE_OK)) {
            return new DeliveryResponse(checkResponse.getCode(),checkResponse.getMessage());
        }

        //如果取消后批次内无货物
        if(sendDatailDao.queryCountExclusion(querySendDatail)<=0){
            if(isPackageCode){
                return new DeliveryResponse(DeliveryResponse.CODE_CANCELDELIVERYCHECK_ONLY,
                        HintService.getHint(HintCodeConstants.UNIQUE_PACKAGE_IN_THE_BATCH));
            }

            if(isWaybillCode){
                return new DeliveryResponse(DeliveryResponse.CODE_CANCELDELIVERYCHECK_ONLY,
                        HintService.getHint(HintCodeConstants.UNIQUE_WAYBILL_IN_THE_BATCH));
            }

            if(isBoxcode){
                return new DeliveryResponse(DeliveryResponse.CODE_CANCELDELIVERYCHECK_ONLY,
                        HintService.getHint(HintCodeConstants.UNIQUE_BOX_IN_THE_BATCH));
            }
        }

        return new DeliveryResponse(JdResponse.CODE_OK, JdResponse.MESSAGE_OK);
    }


    /**
     * 根据批次号获取封车状态并判断是否可以取消发货
     * @param batchCode
     * @return
     */
    private DeliveryResponse sealCarCheck(String batchCode){
        try{
            Integer createSite = SerialRuleUtil.getCreateSiteCodeFromSendCode(batchCode);
            Integer receiveSite = SerialRuleUtil.getReceiveSiteCodeFromSendCode(batchCode);
            boolean tibetMode = tibetBizService.tibetModeSwitch(createSite, receiveSite);
            if (tibetMode) {
                ItmsCancelSendCheckSendCodeDto request = new ItmsCancelSendCheckSendCodeDto();
                request.setReceiptCode(batchCode);
                request.setOpeSiteId(String.valueOf(createSite));
                ItmsResponse response = tibetBizService.cancelSendCheckSendCode(request);

                if (log.isInfoEnabled()) {
                    log.info("取消发货调用ITMS服务, 参数:{}, 结果:{}", JsonHelper.toJson(request), JsonHelper.toJson(response));
                }

                if (!response.success()) {
                    return DeliveryResponse.itmsFail(response.getMessage());
                }
            }
            else {
                SealCarDto sealCarInfo= vosManager.querySealCarByBatchCode(batchCode);
                if (sealCarInfo != null) {
                    //如果是已解封车状态返回拦截
                    if(SEAL_CAR_STATUS_UNSEAL.equals(sealCarInfo.getStatus())){
                        return new DeliveryResponse(DeliveryResponse.CODE_CANCELDELIVERYCHECK_UNSEAL,
                                HintService.getHint(HintCodeConstants.CANCEL_DELIVERY_CHECK_UNSEAL));
                    }

                    //已封车状态
                    if(SEAL_CAR_STATUS_SEAL.equals(sealCarInfo.getStatus())){
                        long date = System.currentTimeMillis();
                        //封车时间大于一小时
                        if(date - (sealCarInfo.getSealCarTime()==null ? 0 :sealCarInfo.getSealCarTime().getTime()) > DateHelper.ONE_HOUR_MILLI){
                            return new DeliveryResponse(DeliveryResponse.CODE_CANCELDELIVERYCHECK_SEAL,
                                    HintService.getHint(HintCodeConstants.ABORT_CANCEL_EXCEED_ONE_HOUR));
                        }

                        return new DeliveryResponse(JdResponse.CODE_OK,JdResponse.MESSAGE_OK);
                    }
                }
            }
        }catch (Exception ex){
            log.error("根据批次号获取封车状态时异常", ex);
            return new DeliveryResponse(DeliveryResponse.CODE_CANCELDELIVERYCHECK_ERROR,
                    HintService.getHint(HintCodeConstants.GET_BATCH_SEAL_STATUS_ERROR));
        }

        return new DeliveryResponse(DeliveryResponse.CODE_CANCELDELIVERYCHECK_NOSEAL, DeliveryResponse.MESSAGE_CANCELDELIVERYCHECK_NOSEAL);
    }

    //将板号由“关闭”状态变更未“组板中”状态
    public void changeBoardStatus(SendM tSendM,List<String> boardList){
        OperatorInfo operatorInfo = new OperatorInfo();
        operatorInfo.setOperatorErp(Integer.toString(tSendM.getUpdateUserCode()));
        operatorInfo.setOperatorName(tSendM.getUpdaterUser());
        operatorInfo.setSiteCode(tSendM.getCreateSiteCode());
        BaseStaffSiteOrgDto baseStaffSiteOrgDto = baseMajorManager.getBaseSiteBySiteId(tSendM.getCreateSiteCode());
        if(baseStaffSiteOrgDto != null){
            operatorInfo.setSiteName(baseMajorManager.getBaseSiteBySiteId(tSendM.getCreateSiteCode()).getSiteName());
        }
        //取消板号的关闭状态
        groupBoardManager.resuseBoards(boardList,operatorInfo);
    }

   private void delDeliveryFromRedis(SendM sendM) {
        Long result = redisManager.del(
                CacheKeyConstants.REDIS_KEY_IS_DELIVERY
                        + sendM.getCreateSiteCode()
                        + sendM.getBoxCode());
        if (result <= 0) {
            log.warn("remove sendms of key [{}-{}-{}] from redis fail",
                    CacheKeyConstants.REDIS_KEY_IS_DELIVERY,sendM.getCreateSiteCode(),sendM.getBoxCode());
        } else {
            log.warn("remove sendms of key [{}-{}-{}] from redis success",
                    CacheKeyConstants.REDIS_KEY_IS_DELIVERY,sendM.getCreateSiteCode(),sendM.getBoxCode());
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
            log.error("取消发货 发全程跟踪sendMessage： " + ex);
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
            if (SendBizSourceEnum.getEnum(bizSource) == SendBizSourceEnum.COLD_CHAIN_SEND
                    || SendBizSourceEnum.getEnum(bizSource) == SendBizSourceEnum.COLD_LOAD_CAR_KY_SEND
                    || SendBizSourceEnum.getEnum(bizSource) == SendBizSourceEnum.COLD_LOAD_CAR_SEND
            ) {
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
            log.error("[PDA操作取消发货]冷链取消发货 - 推送TMS运输MQ消息时发生异常：{}",JsonHelper.toJson(coldChainSendDetails), e);
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
            log.error("[PDA操作取消发货]发送MQ消息时发生异常:{}",JsonHelper.toJson(sendDetail), e);
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
        log.info("取消发货 发全程跟踪work6666-3800：{} " ,sendDetail.getWaybillCode());
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
            dSendM.setOperateTime(tSendM.getOperateTime());
            // 是否发车 2021年12月15日18:09:28 下线

            tSendDatail.setReceiveSiteCode(dSendM.getReceiveSiteCode());
            // 是否发货状态更新
            if (sendDatailDao.querySendDatailBySendStatus(tSendDatail) != null) {
                return new ThreeDeliveryResponse(
                        DeliveryResponse.CODE_Delivery_NO_MESAGE,
                        HintService.getHint(HintCodeConstants.DELIVERY_PROCESSING), null);
            }
            return this.cancelDeliveryStatusByBox(dSendM, tSendDatail);
        } else {
            return new ThreeDeliveryResponse(
                    DeliveryResponse.CODE_Delivery_NO_MESAGE,
                    HintService.getHint(HintCodeConstants.BOX_SENDM_MISSING), null);
        }
    }

    //处理运单
    private ThreeDeliveryResponse cancelUpdateDataByPack(SendM tSendM,
                                                         List<SendDetail> tList) {
        Collections.sort(tList);
		for (SendDetail dSendDetail : tList) {
			tSendM.setBoxCode(dSendDetail.getBoxCode());
			List<SendM> sendMList = this.sendMDao.findSendMByBoxCode(tSendM);
			// 发车验证 2021年12月15日18:08:57 下线
			if (sendMList == null || sendMList.isEmpty()) {
				return new ThreeDeliveryResponse(
						DeliveryResponse.CODE_Delivery_NO_MESAGE,
						HintService.getHint(HintCodeConstants.BOX_SENDM_MISSING), null);
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
			//如果按包裹取消发货，需取消分拣，更新取消分拣的操作时间晚取消分拣一秒
            sorting.setOperateTime(new Date(tSendM.getUpdateTime().getTime() + 1000));
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
        mSendDetail.setIsCancel(Constants.OPERATE_TYPE_CANCEL_Y);
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
    @Override
    public boolean cancelSendM(SendM tSendM) {
        return this.sendMDao.cancelSendM(tSendM);
    }

    /**
     * 取消发货明细表数据处理
     *
     * @param tSendDetail 发货相关数据
     */
    @Override
    public boolean cancelSendDatailByPackage(SendDetail tSendDetail) {
        if (tSendDetail != null) {
            try {
                this.sendDatailDao.cancelSendDatail(tSendDetail);
            } catch (Exception e) {
                this.log.error("取消发货cancelSendDatailByPackage,参数:{}",JsonHelper.toJson(tSendDetail), e);
            }
            return true;
        } else {
            return false;
        }
    }

    /**
     * 取消发货明细表数据处理
     *
     * @param sendM 发货相关数据
     */
    @Override
    public boolean cancelSendDatailByBox(SendM sendM) {
        SendDetail tSendDatail = new SendDetail();
        tSendDatail.setBoxCode(sendM.getBoxCode());
        tSendDatail.setCreateSiteCode(sendM.getCreateSiteCode());
        tSendDatail.setReceiveSiteCode(sendM.getReceiveSiteCode());

        try {
            sendDatailDao.cancelSendDatail(tSendDatail);
        } catch (Exception e) {
            this.log.error("取消发货cancelSendDatailByBox,参数{}",JsonHelper.toJson(tSendDatail), e);
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
                            this.log.warn("发货数据调用基础资料接口参数信息不全：包裹号为{}" , tSendDetail.getPackageBarcode());
                        } else {
                            WaybillStatus tWaybillStatus = this.buildWaybillStatus(tSendDetail, createSiteDto, receiveSiteDto);
                            if (tSendDetail.getYn().equals(1) && tSendDetail.getIsCancel().equals(0)) {
                                // 添加操作日志
                                addOperationLog(tSendDetail,createSiteDto.getSiteName(),"DeliveryServiceImpl#updateWaybillStatus");
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
                                this.log.info("发送MQ[{}],业务ID[{}]",sendMessage.getTopic(),sendMessage.getBusinessId());
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
            this.log.error("发货全程跟踪调用站点信息异常:{}",siteCode, e);
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
    @Override
    public boolean updatewaybillCodeMessage(Task task) {
        if(log.isInfoEnabled()){
            log.info("发货状态开始处理:{}" , JsonHelper.toJson(task));
        }
        Map<String, Long> timeMap = new LinkedHashMap<>();
        long startTime = System.currentTimeMillis();
        timeMap.put("1", startTime);
        if (task == null || task.getBoxCode() == null || task.getCreateSiteCode() == null) {
            return true;
        }
        SendTaskCategoryEnum sendTaskCategory = getTaskCategory(task);
        List<SendM> tSendM = null;
        CallerInfo info = null;
        int sendMNum = 0;
        int sendDNum = 0;
        String sendCode = "";
        try {
        	//body里是任务
            timeMap.put("2", System.currentTimeMillis());
	        if (JsonHelper.isJsonString(task.getBody())) {
	            SendTaskBody body = JsonHelper.fromJson(task.getBody(), SendTaskBody.class);
	            sendCode = body.getSendCode();
	            if(body.getHandleCategory() != null){
	            	sendTaskCategory = SendTaskCategoryEnum.getEnum(body.getHandleCategory());
	            }
	            info = startMonitor(sendTaskCategory);
	            // 按照批次号
                timeMap.put("2.1", System.currentTimeMillis());
	            if (SendTaskCategoryEnum.BATCH_SEND.getCode().equals(body.getHandleCategory())){
	                tSendM = this.sendMDao.selectBySiteAndSendCodeBYtime(body.getCreateSiteCode(), body.getSendCode());
	            } else { // 按照箱号
	                tSendM = new ArrayList<SendM>(1);
	                tSendM.add(body);
	            }
                timeMap.put("2.2", System.currentTimeMillis());
	        } else {
	        	sendCode = task.getBoxCode();
	        	info = startMonitor(SendTaskCategoryEnum.BATCH_SEND);
	            tSendM = this.sendMDao.selectBySiteAndSendCodeBYtime(task.getCreateSiteCode(), task.getBoxCode());
	        }
            timeMap.put("3", System.currentTimeMillis());
	        if(tSendM != null){
	        	sendMNum = tSendM.size();
	        }
	        log.info("发货任务处理{}:sendM{}条",sendCode,sendMNum);
	        if (log.isDebugEnabled()) {
	            log.debug("SEND_M明细:{}" ,JsonHelper.toJson(tSendM));
	        }
	        SendDetail tSendDetail = new SendDetail();
	        List<SendDetail> sendDetailListTemp;
	        List<SendDetail> sendDetailList = new ArrayList<SendDetail>();
	        if(CollectionUtils.isEmpty(tSendM)){
	            return true;
            }
	        for (SendM newSendM : tSendM) {
	            tSendDetail.setBoxCode(newSendM.getBoxCode());
	            tSendDetail.setCreateSiteCode(newSendM.getCreateSiteCode());
	            tSendDetail.setReceiveSiteCode(newSendM.getReceiveSiteCode());
	            tSendDetail.setIsCancel(Constants.OPERATE_TYPE_CANCEL_L);
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
            timeMap.put("4", System.currentTimeMillis());
	        if(sendDetailList != null){
	        	sendDNum = sendDetailList.size();
	        }
	        log.info("发货任务处理{}:sendD{}条",sendCode,sendDNum);
	        if(sendDNum > BIG_SEND_NUM){
	        	log.warn("发货任务处理-sendD超过{}条,{}:sendD{}条",BIG_SEND_NUM,sendCode,sendDNum);
	        }
	        if (log.isDebugEnabled()) {
	            log.debug("SEND_D明细:{}" , JsonHelper.toJson(sendDetailList));
	        }
	        updateWaybillStatus(sendDetailList);
            timeMap.put("5", System.currentTimeMillis());
	        //如果是按运单发货，解除按运单发货的redis锁
	        unlockWaybillByPack(tSendM);
            timeMap.put("6", System.currentTimeMillis());
        }catch(Exception e){
        	Profiler.functionError(info);
        	log.error("发货任务处理异常！", e);
        	throw e;
        }finally{
            long runTime = System.currentTimeMillis() - startTime;
            if(sendDNum > BIG_SEND_NUM || runTime > 3000){
                log.warn(String.format("longRunTimeOrLargeBatch_DeliveryServiceImpl.updatewaybillCodeMessage sendCode: %s, sendMNum: %s, sendDNum: %s, runTime: %s, timeMap: %s ", sendCode, sendMNum, sendDNum, runTime, JsonHelper.toJson(timeMap)));
            }
        	Profiler.registerInfoEnd(info);
        }
        return true;
    }

    private void unlockWaybillByPack(List<SendM> tSendM) {
        if (log.isInfoEnabled()) {
            log.info("按运单发货解锁运单中的包裹:{}", JsonHelper.toJsonMs(tSendM));
        }
        if (CollectionUtils.isEmpty(tSendM)) {
            return;
        }
        try {
            for (SendM sendM : tSendM) {
                if (SendBizSourceEnum.WAYBILL_SEND.getCode().equals(sendM.getBizSource())) {
                    String waybillCode = WaybillUtil.getWaybillCode(sendM.getBoxCode());
                    String packLockKey = getSendByWaybillPackLockKey(waybillCode, sendM.getCreateSiteCode());
                    String waybillLockKey = getSendByWaybillLockKey(waybillCode, sendM.getCreateSiteCode());

                    String s = redisClientCache.get(waybillLockKey);
                    if (StringUtils.isEmpty(s)) {
                        log.warn("按运单发货解锁运单中的包裹,key={}运单总包裹数获取为空!", waybillLockKey);
                        return;
                    }
                    int packCount = redisClientCache.bitCount(packLockKey).intValue();
                    int totalPack = Integer.parseInt(s);
                    log.info("按运单发货解锁运单中的包裹,key={}运单总包裹数:{},已处理包裹数:{}", tSendM, s, packCount);
                    if (packCount == totalPack) {
                        redisClientCache.del(waybillLockKey);
                        redisClientCache.del(packLockKey);
                        log.info("按运单发货所有包裹处理完成,移除运单锁:packLockKey={}", packLockKey);
                    }
                }

                // 解锁包裹/箱号发货
                String redisKey = String.format(CacheKeyConstants.PACKAGE_SEND_LOCK_KEY, sendM.getBoxCode(), sendM.getCreateSiteCode());
                redisClientCache.del(redisKey);

            }
        } catch (Exception e) {
            log.error("按运单发货所有包裹处理完成,移除运单锁异常:" + tSendM, e);
        }
    }

    /**
     * 按运单发货是否在处理中
     */
    public boolean isSendByWaybillProcessing(SendM sendM) {
        try {
            if (sendM == null) {
                return false;
            }
            return Boolean.TRUE.equals(redisClientCache.exists(getSendByWaybillLockKey(sendM.getBoxCode(), sendM.getCreateSiteCode())));
        } catch (Exception e) {
            log.error("判断运单是否发货出错:" + sendM, e);
        }
        return false;
    }
    /**
     * 根据任务获取对应的SendTaskCategoryEnum
     * @param task
     * @return
     */
    private SendTaskCategoryEnum getTaskCategory(Task task){
    	if(task.getSubType()!= null){
    		return TASK_MAPPING_SUBTYPE_CATEGORY.get(task.getSubType());
    	}
    	return null;
    }
    /**
     * 开启任务监控
     * @param sendTaskCategory
     * @return
     */
    private CallerInfo startMonitor(SendTaskCategoryEnum sendTaskCategory){
    	if(sendTaskCategory != null ){
    		return ProfilerHelper.registerInfo("dmsWorker.task.sendHandler."+sendTaskCategory.getKey(), Constants.UMP_APP_NAME_DMSWORKER);
    	}
    	return ProfilerHelper.registerInfo("dmsWorker.task.sendHandler."+SendTaskCategoryEnum.PACKAGE_SEND.getKey(), Constants.UMP_APP_NAME_DMSWORKER);
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
            newSendDetail.setCreateTime(sendDetail.getCreateTime());
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
            if (SendTaskCategoryEnum.BATCH_SEND.getCode().equals(body.getHandleCategory())){
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
            this.log.info("发货数据--------BoxInfoList参数不全");
        } else {
            shouHuoes.add(tShouHuoInfo);
            String requestXmls = XmlHelper
                    .objectToXml(tShouHuoInfo, new ShouHuoConverter());

            if (StringHelper.isNotEmpty(requestXmls)) {
                Result result = this.dmsToTmsWebService.shouHuoService(requestXmls);
                this.log.info(result.getResultMessage());
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
                    this.log.info("DMC数据同步-------OrderInfoList参数不全");
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
            log.info("调用运单接口返回信息:{}" ,results.getMessage());
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
            log.error("取件单基础信息调用异常:{}",waybillCodes,e);
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

    @Override
    public List<SendDetail> findOrder(SendDetail sendDetail) {
        return sendDatailDao.findOrder(sendDetail);
    }

    /**
     * 快运发货校验运单包裹不齐
     * @param sendMList
     * @return
     */
    @Override
    public ThreeDeliveryResponse checkThreePackageForKY(List<SendM> sendMList){
        List<SendThreeDetail> tDeliveryResponse = null;
        Integer businessType = sendMList.size() > 0 ? sendMList.get(0).getSendType() : 10;
        List<SendDetail> allList = new ArrayList<SendDetail>();
        getAllList(sendMList, allList);
        //1.判断发货数据是否包含派车单并进行派车单运单不齐校验
//        DeliveryResponse scheduleWaybillResponse = new DeliveryResponse();
//        scheduleWaybillResponse.setCode(DeliveryResponse.CODE_OK);
//        if(!businessType.equals(20)){    //非逆向才进行派车单运单齐全校验
//            log.debug("发货数据判断运单是否不全");
//            checkScheduleWaybill(allList, scheduleWaybillResponse);    //发货请求是否包含派车单
//        }
        //2.发货数据判断包裹是否不全
        this.log.debug("发货数据判断包裹是否不全");
        if (businessType.equals(20)) {
            tDeliveryResponse =  reverseComputer.compute(allList, true);    //逆向不处理派车单发货的情况
        } else {
            tDeliveryResponse =  forwardComputer.compute(allList, true);
        }
        //派车单发货不齐不返回明细数据
        String msg = tDeliveryResponse != null && !tDeliveryResponse.isEmpty() ? HintService.getHint(HintCodeConstants.WAYBILL_SEND_NOT_COMPLETE) : "";
//        if(!DeliveryResponse.CODE_OK.equals(scheduleWaybillResponse.getCode())){
//            msg = StringUtils.isNotBlank(msg) ? "运单/" + msg : scheduleWaybillResponse.getMessage();
//        }
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
    @Override
  public ThreeDeliveryResponse differentialQuery(List<SendM> sendMList, Integer queryType) {
    // 未扫描包裹
    List<SendThreeDetail> notScaned = null;
    List<SendThreeDetail> res = new ArrayList<SendThreeDetail>();
    Integer businessType = sendMList.size() > 0 ? sendMList.get(0).getSendType() : 10;
    List<SendDetail> hasScaned = new ArrayList<SendDetail>();
    this.log.debug("快运发货差异查询");
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
        long startTime=new Date().getTime();

        DeliveryResponse response = new DeliveryResponse();
        response.setCode(JdResponse.CODE_OK);
        response.setMessage(JdResponse.MESSAGE_OK);

        String boxCode = queryPara.getBoxCode();
        Integer createSiteCode = queryPara.getCreateSiteCode();
        Integer receiveSiteCode = queryPara.getReceiveSiteCode();

        //批次号目的地类型为64的进行路由校验，否则走原来的逻辑
        BaseStaffSiteOrgDto siteInfo = baseService.queryDmsBaseSiteByCode(receiveSiteCode+"");
        if(siteInfo == null){
            log.warn("checkRouterForCBox获取到的站点信息为空.站点：{}" , receiveSiteCode);
            return response;
        }
        if(siteInfo.getSiteType() != 64){
            log.info("checkRouterForCBox 批次号目的地[{}]的站点类型为：{},不进行路由校验",receiveSiteCode, siteInfo.getSiteType());
            return response;
        }

        // 获取箱中的运单号
        List<String> waybillCodes = getWaybillCodesByBoxCodeAndFetchNum(boxCode,3);

        //获取运单对应的路由
        String routerStr = null;
        String waybillCodeForVerify = null;
        if (waybillCodes != null && !waybillCodes.isEmpty()) {
            for(String  waybillCode : waybillCodes){
                //根据waybillCode查库获取路由信息
                routerStr = waybillCacheService.getRouterByWaybillCode(waybillCode);

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

        log.warn("C网路由校验按箱发货,箱号为:{} 取到的运单号为：{}，运单正确路由为:{}" ,boxCode,waybillCodeForVerify,routerStr);

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
                    log.info(logInfo);
                    long endTime = new Date().getTime();

                    JSONObject request=new JSONObject();
                    request.put("boxCode",boxCode);

                    JSONObject operateResponse=new JSONObject();
                    operateResponse.put("info", logInfo);

                    BusinessLogProfiler businessLogProfiler=new BusinessLogProfilerBuilder()
                            .operateTypeEnum(BusinessLogConstans.OperateTypeEnum.SEND_ONECAR_SEND)
                            .operateRequest(request)
                            .operateResponse(response)
                            .methodName("DeliveryServiceImpl#checkRouterForCBox")
                            .build();

                    logEngine.addLog(businessLogProfiler);

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
            long endTime = new Date().getTime();


            JSONObject request=new JSONObject();
            request.put("boxCode",boxCode);

            JSONObject operateResponse=new JSONObject();
            operateResponse.put("info", logInfo);

            BusinessLogProfiler businessLogProfiler=new BusinessLogProfilerBuilder()
                    .operateTypeEnum(BusinessLogConstans.OperateTypeEnum.SEND_ONECAR_SEND)
                    .operateRequest(request)
                    .operateResponse(response)
                    .methodName("DeliveryServiceImpl#checkRouterForCBox")
                    .processTime(endTime,startTime)
                    .build();
            businessLogProfiler.setTimeStamp(endTime);
            businessLogProfiler.setUrl("");

            logEngine.addLog(businessLogProfiler);

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

//        addCassandraLog(boxCode,boxCode,logInfo);


        long endTime = new Date().getTime();

        JSONObject request=new JSONObject();
        request.put("boxCode",boxCode);

        JSONObject operateResponse=new JSONObject();
        operateResponse.put("info", logInfo);

        BusinessLogProfiler businessLogProfiler=new BusinessLogProfilerBuilder()
                .operateTypeEnum(BusinessLogConstans.OperateTypeEnum.SEND_ONECAR_SEND)
                .operateRequest(request)
                .operateResponse(response)
                .methodName("DeliveryServiceImpl#checkRouterForCBox")
                .processTime(endTime,startTime)
                .build();
        businessLogProfiler.setTimeStamp(endTime);
        businessLogProfiler.setUrl("");

        logEngine.addLog(businessLogProfiler);
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
    @Override
    public DeliveryResponse checkRouterForKY(SendM sendM, Integer flag){
        DeliveryResponse response = new DeliveryResponse(JdResponse.CODE_OK,JdResponse.MESSAGE_OK);

        //获取提示语
        List<String> tipMessageList = new ArrayList<String>();
        response.setTipMessages(tipMessageList);
        if(isWaybillNeedAddQuarantine(sendM)){
            tipMessageList.add(DeliveryResponse.TIP_MESSAGE_NEED_ADD_QUARANTINE);
        }

        // 校验发货任务是否在处理中
        response = checkBoxSendProcessing(sendM);
        if (!DeliveryResponse.CODE_OK.equals(response.getCode())) {
            return response;
        }

        //1.批次号封车校验，已封车不能发货
        if (StringUtils.isNotEmpty(sendM.getSendCode()) && BusinessUtil.isSendCode(sendM.getSendCode())) {
            StringBuffer customMsg = new StringBuffer().append(HintService.getHint(HintCodeConstants.SEND_CODE_SEALED_TIPS_SECOND));
            if (newSealVehicleService.newCheckSendCodeSealed(sendM.getSendCode(), customMsg)) {
                response.setCode(DeliveryResponse.CODE_SEND_CODE_ERROR);
                response.setMessage(customMsg.toString());
                return response;
            }
        }else{
            log.warn("checkRouterForKY未校验封车状态，入参{}",JsonHelper.toJson(sendM));
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

        //3.包装耗材服务确认拦截
        if (this.checkWaybillConsumable(sendM)) {
            response.setCode(DeliveryResponse.CODE_29120);
            response.setMessage(HintService.getHint(HintCodeConstants.PACKING_CONSUMABLE_CONFIRM_TIPS_SECOND));
            return response;
        }

        //4.快运称重及运费拦截
        log.debug("快运发货运单重量及运费拦截开始");
        List<String> waybillCodes = getWaybillCodesBySendM(sendM);
        InterceptResult<String> interceptResult = this.interceptWaybillForB2b(waybillCodes);
        if(!interceptResult.isSucceed()){
        	log.warn("快运发货运单重量及运费拦截：{}",interceptResult.getMessage());
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
            log.warn("快运发货无法获取目的站点：{}",receiveSiteCode);
            return response;
        }else if(!Integer.valueOf(Constants.DMS_SITE_TYPE).equals(receiveSite.getSiteType())){//发货至分拣中心才校验
            log.warn("快运发货目的站点非分拣中心，不校验B2B路由：{}",receiveSiteCode);
            if(!checkDmsToVendor(sendM)){
                response.setCode(DeliveryResponse.CODE_SCHEDULE_INCOMPLETE);
                response.setMessage(HintService.getHint(HintCodeConstants.WAYBILL_C_TO_B));
                return response;
            }
            return response;
        }

        //6.判断路由
        Integer destinationSiteCode = getDestinationSiteCode(sendM);
        log.debug("根据包裹号或箱号获取目的分拣中心：{}",destinationSiteCode);
        if(destinationSiteCode == null){
            response.setCode(DeliveryResponse.CODE_SCHEDULE_INCOMPLETE);
            response.setMessage(HintService.getHint(HintCodeConstants.MISSING_ROUTER));
            return response;
        }
        try {
            if(log.isDebugEnabled()){
                log.debug("B网路由查询条件：",JsonHelper.toJson(sendM));
            }
            List<B2BRouter> routers = b2bRouterService.getB2BRouters(originalSiteCode, destinationSiteCode);
            if(log.isDebugEnabled()){
                log.debug("B网路由查询结果：",JsonHelper.toJson(routers));
            }
            if(routers == null || routers.isEmpty()){
                response.setCode(DeliveryResponse.CODE_SCHEDULE_INCOMPLETE);
                response.setMessage(HintService.getHint(HintCodeConstants.MISSING_ROUTER));
            }else{
                List<B2BRouterNode> nodes = b2bRouterService.getNextCodes(originalSiteCode, destinationSiteCode, receiveSiteCode);
                if(log.isDebugEnabled()){
                    log.debug("B网路由下一节点查询结果：{}",JsonHelper.toJson(nodes));
                }
                if(nodes == null || nodes.isEmpty()){
                    response.setCode(DeliveryResponse.CODE_SCHEDULE_INCOMPLETE);
                    response.setMessage(HintService.getHint(HintCodeConstants.NEXT_ROUTER_AND_DESTINATION_DIFFERENCE));
                }
            }
        }catch (Exception e){
            log.error("B网路由查询异常：{}",JsonHelper.toJson(sendM), e);
            response.setCode(JdResponse.CODE_SERVICE_ERROR);
            response.setMessage(JdResponse.MESSAGE_SERVICE_ERROR);
        }
        if(!JdResponse.CODE_OK.equals(response.getCode())){
            log.warn("B网路由拦截：{}->{}->{},{}",originalSiteCode,receiveSiteCode,destinationSiteCode,response.getMessage());
        }

        return response;
    }

    /**
     * 校验包裹/箱号/运单正在处理发货中
     * @param tSendM
     * @return
     */
    private DeliveryResponse checkBoxSendProcessing(SendM tSendM) {
        String barCode = tSendM.getBoxCode();
        if (BusinessUtil.isBoxcode(barCode)) {
            if (sendByBoxOrPackProcessing(tSendM)) {
                return new DeliveryResponse(DeliveryResponse.BARCODE_DELIVERY_IS_PROCESSING, "该箱号发货正在处理中，请等待处理完成!");
            }
        }
        else if (WaybillUtil.isPackageCode(barCode)) {
            if (sendByWaybillProcessing(tSendM)) {
                return new DeliveryResponse(DeliveryResponse.BARCODE_DELIVERY_IS_PROCESSING,
                        HintService.getHint(HintCodeConstants.WAYBILL_SEND_IS_PROCESSING));
            }
            if (sendByBoxOrPackProcessing(tSendM)) {
                return new DeliveryResponse(DeliveryResponse.BARCODE_DELIVERY_IS_PROCESSING, "该包裹发货正在处理中，请等待处理完成!");
            }
        }
        else if (WaybillUtil.isWaybillCode(barCode)) {
            if (sendByWaybillProcessing(tSendM)) {
                return new DeliveryResponse(DeliveryResponse.BARCODE_DELIVERY_IS_PROCESSING,
                        HintService.getHint(HintCodeConstants.WAYBILL_SEND_IS_PROCESSING));
            }
        }

        return DeliveryResponse.oK();
    }

    /**
     * 按运单发货正在处理中
     * @param tSendM
     * @return
     */
    private boolean sendByWaybillProcessing(SendM tSendM) {
        String waybillCode = WaybillUtil.getWaybillCode(tSendM.getBoxCode());
        String batchUniqKey = waybillCode + Constants.UNDER_LINE + tSendM.getCreateSiteCode();
        String redisKey = String.format(CacheKeyConstants.WAYBILL_SEND_BATCH_KEY, batchUniqKey);
        return StringUtils.isNotBlank(redisClientCache.get(redisKey));
    }

    /**
     * 按箱号/包裹号发货正在处理中
     * @param tSendM
     * @return
     */
    private boolean sendByBoxOrPackProcessing(SendM tSendM) {
        String redisKey = String.format(CacheKeyConstants.PACKAGE_SEND_LOCK_KEY, tSendM.getBoxCode(), tSendM.getCreateSiteCode());
        return StringUtils.isNotBlank(redisClientCache.get(redisKey));
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
                        deliveryResponse.setMessage(HintService.getHint(HintCodeConstants.BOX_SENT_ALREADY));
                    } else {
                        //如果不相同，提示一个确认框
                        deliveryResponse.setCode(DeliveryResponse.CODE_CONFIRM_CANCEL_LAST_SEND);
                        deliveryResponse.setMessage(HintService.getHint(HintCodeConstants.CANCEL_LAST_SEND));
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
                log.warn("快运发货箱号不存在，无法获取最终目的地：{}",JsonHelper.toJson(sendM));
                return destinationSiteCode;
            }
            BaseStaffSiteOrgDto boxReceiveSiteCode = baseMajorManager.getBaseSiteBySiteId(box.getReceiveSiteCode());
            if(boxReceiveSiteCode != null){
                destinationSiteCode = Integer.valueOf(Constants.DMS_SITE_TYPE).equals(boxReceiveSiteCode.getSiteType()) ? box.getReceiveSiteCode():boxReceiveSiteCode.getDmsId();
            }else{
                log.warn("快运发货箱号目的地不存在，无法获取最终目的地：{}",JsonHelper.toJson(sendM));
            }
        } else if (WaybillUtil.isPackageCode(sendM.getBoxCode())) {
            Integer preSiteCode = null;
            String waybillCode = WaybillUtil.getWaybillCode(sendM.getBoxCode());
            if(StringUtils.isBlank(waybillCode)){
                log.warn("快运发货包裹号非法，无法获取最终目的地：{}",JsonHelper.toJson(sendM));
            }else{
                preSiteCode = getPreSiteCodeByWayBillCode(waybillCode);
            }
            if(preSiteCode != null){
                BaseStaffSiteOrgDto preSiteCodeDto = baseMajorManager.getBaseSiteBySiteId(preSiteCode);
                if(preSiteCodeDto != null){
                    destinationSiteCode = preSiteCodeDto.getDmsId();
                }else{
                    log.warn("快运发货包裹预分拣站点不存在，无法获取最终目的地：{}",JsonHelper.toJson(sendM));
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
        if(! Constants.BASE_SITE_MOTORCADE.equals(receiveSite.getSiteType())){
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
                log.warn("调用运单接口获取运单数据为空，waybillCode：{}" , waybillCode);
            }else{
                preSiteCode = baseEntity.getData().getWaybill().getOldSiteId();
            }
        }
        if(preSiteCode == null){
            log.warn("调用运单接口获取运单预分拣站点为空，waybillCode：{}" , waybillCode);
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
            log.error("快运发货查询寄付运费拦截开关失败，不在拦截寄付运费，对应运单：{}",waybillCodes, e);
        }

    	List<String> noHasWeightWaybills = new ArrayList<String>();
    	List<String> noHasFreightWaybills = new ArrayList<String>();
    	List<String> sendNoHasFreightWaybills = new ArrayList<String>();
        List<String> noHasVolumeWaybills = new ArrayList<>();
        for(String waybillCode:waybillCodes){
        	BaseEntity<BigWaybillDto> baseEntity = waybillQueryManager.getDataByChoice(waybillCode, true, true, true, false);
        	if(baseEntity != null
					 && baseEntity.getData() != null
					 && baseEntity.getData().getWaybill() != null){
        		Waybill waybill = baseEntity.getData().getWaybill();

                //edited by hanjiaxing3 2018.07.26
                //40位非0（C网以外）并且66位为0（必须称重），需要称重量方拦截
                if (! BusinessUtil.isSignChar(waybill.getWaybillSign(), 40, '0') && BusinessUtil.isSignChar(waybill.getWaybillSign(), 66, '0')
                        && !WaybillUtil.isReturnCode(waybillCode)) {
                    //WaybillSign40=2时（只有外单快运纯配业务），需校验重量
                    //edited by hanjiaxing3 2019.04.10 临时欠款运单也需要称重拦截
                    if(BusinessUtil.isSignChar(waybill.getWaybillSign(), 40, '2') || BusinessUtil.isTemporaryArrearsWaybill(waybill.getWaybillSign())){
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
                    //added by hanjiaxing3 2019.04.10 临时欠款运单校验量方
                    if (BusinessUtil.isTemporaryArrearsWaybill(waybill.getWaybillSign())){
                        //先校验运单的复核体积，以及称重流水（含体积）
                        if (! (NumberHelper.gt0(waybill.getSpareColumn2()) || dmsWeightFlowService.checkTotalWeight(waybillCode))) {
                            noHasVolumeWaybills.add(waybillCode);
                        }
                    }
                    //end
        		}
        		//b2b校验是否包含-到付运费 2021年08月30日13:46:57 移除

        		//b2b校验是否包含-寄付运费
        		if(sendFreightInterception && !BusinessHelper.hasSendFreightForB2b(baseEntity.getData())){
                    sendNoHasFreightWaybills.add(waybillCode);
        		}
        	}else{
        		noHasWeightWaybills.add(waybillCode);
                noHasVolumeWaybills.add(waybillCode);
        	}
        	//超过3单则中断校验逻辑
    		if(noHasWeightWaybills.size() >= MAX_SHOW_NUM
                    ||noHasFreightWaybills.size() >= MAX_SHOW_NUM
                    || sendNoHasFreightWaybills.size() >= MAX_SHOW_NUM || noHasVolumeWaybills.size() >= MAX_SHOW_NUM){
    			break;
    		}
        }
        if(!noHasWeightWaybills.isEmpty()){
        	interceptResult.toFail();
        	Map<String, String> argsMap = new HashMap<>();
        	argsMap.put(HintArgsConstants.ARG_FIRST, noHasWeightWaybills.toString());
        	interceptResult.setMessage(HintService.getHint(HintCodeConstants.WAYBILL_MISSING_TOTAL_WEIGHT, argsMap));
        	return interceptResult;
        }
        //added by hanjiaxing3 2019.04.10 临时欠款运单校验量方
        if(! noHasVolumeWaybills.isEmpty()) {
            interceptResult.toFail();
            Map<String, String> argsMap = new HashMap<>();
            argsMap.put(HintArgsConstants.ARG_FIRST, noHasVolumeWaybills.toString());
            interceptResult.setMessage(HintService.getHint(HintCodeConstants.WAYBILL_MISSING_TOTAL_VOLUME, argsMap));
            return interceptResult;
        }
        //end
        if(!noHasFreightWaybills.isEmpty()){
            // 2021年12月15日18:02:50 下线
        	/*interceptResult.toFail();
            Map<String, String> argsMap = new HashMap<>();
            argsMap.put(HintArgsConstants.ARG_FIRST, noHasFreightWaybills.toString());
        	interceptResult.setMessage(HintService.getHint(HintCodeConstants.WAYBILL_MISSING_RECEIVE_FREIGHT, argsMap));
        	return interceptResult;*/
        }
        if(!sendNoHasFreightWaybills.isEmpty()){
            // 2021年12月15日18:03:09 下线
            /*interceptResult.toFail();
            Map<String, String> argsMap = new HashMap<>();
            argsMap.put(HintArgsConstants.ARG_FIRST, sendNoHasFreightWaybills.toString());
            interceptResult.setMessage(HintService.getHint(HintCodeConstants.WAYBILL_MISSING_SEND_FREIGHT, argsMap));
            return interceptResult;*/
        }
        return interceptResult;
    }
	/**
     * 老发货校验服务，校验包裹不齐
     * @param sendMList
     * @return
     */
    @SuppressWarnings("rawtypes")
    @Override
    public ThreeDeliveryResponse checkThreePackage(List<SendM> sendMList) {
        List<SendThreeDetail> tDeliveryResponse = null;
        Integer businessType = sendMList.size() > 0 ? sendMList.get(0).getSendType() : 10;
        List<SendDetail> allList = new ArrayList<SendDetail>();
        this.log.debug("发货数据判断包裹是否不全");
        getAllList(sendMList, allList);
        if (businessType.equals(20)) {
            tDeliveryResponse = reverseComputer.compute(allList, false);
        } else {
            tDeliveryResponse = forwardComputer.compute(allList, false);
        }
        if (tDeliveryResponse != null && !tDeliveryResponse.isEmpty()) {
            return new ThreeDeliveryResponse(DeliveryResponse.CODE_Delivery_THREE_SORTING,
                    HintService.getHint(HintCodeConstants.WAYBILL_SEND_DIFFERENCE), tDeliveryResponse);
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
    @Override
    public void getAllList(List<SendM> sendMList, List<SendDetail> allList) {
        for (SendM tSendM : sendMList) {
            SendDetail tSendDatail = new SendDetail();
            tSendDatail.setBoxCode(tSendM.getBoxCode());
            tSendDatail.setCreateSiteCode(tSendM.getCreateSiteCode());
            tSendDatail.setReceiveSiteCode(tSendM.getReceiveSiteCode());
            tSendDatail.setIsCancel(Constants.OPERATE_TYPE_CANCEL_Y);

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
				tSendDatail.setIsCancel(Constants.OPERATE_TYPE_CANCEL_Y);
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
    public List<SendDetail> findWaybillStatus(List<Long> queryCondition) {
        log.debug("findWaybillStatus查询");
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
            log.error("调用运单queryPackageListForParcodes接口时候失败:{}", JsonHelper.toJson(sendDetail),e);
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
    @Override
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
    @Override
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
    public boolean transitSend(SendM domain) {
        if (isTransferSend(domain)) {
            pushTransferSendTask(domain);
            return true;
        } else {
            return false;
        }
    }

    /**
     * 批量插入中转任务
     *
     * @param sendMList
     * @return
     */
    @Override
    public boolean batchTransitSend(List<SendM> sendMList) {
        if (!CollectionUtils.isEmpty(sendMList)) {
            List<Task> tasks = Lists.newArrayListWithCapacity(sendMList.size());
            for (SendM sendM : sendMList) {
                if (isTransferSend(sendM)) {
                    tasks.add(genTransferSendTask(sendM));
                }
            }

            if (!CollectionUtils.isEmpty(tasks)) {
                tTaskService.addBatch(tasks);
            }
        }

        return true;
    }

    /**
     * 判断当前发货是否为中转发货
     * 1:装箱、正向、逆向发货
     * 2:发货起始站与箱子起始站不同
     * 3:发货目的是分拣中心，发货目的站与箱子目的不同
     * 4.如发货目的是站点并且站点类型在所属站类型范围内，取箱号目的所属站和批次目的地相同
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
        Box box = this.boxService.findBoxByCode(domain.getBoxCode());
        if (null == box || null == box.getCreateSiteCode() || null == box.getReceiveSiteCode()) {
            return false;
        }

        //发货起始站与箱子起始站不同，属于中转
        if (! domain.getCreateSiteCode().equals(box.getCreateSiteCode())) {
            return true;
        }

        BaseStaffSiteOrgDto yrDto = this.baseMajorManager.getBaseSiteBySiteId(domain.getReceiveSiteCode());
        if (yrDto == null) {
            return false;
        }
        //发货目的站是分拣中心
        if (BusinessUtil.isDistrubutionCenter(yrDto.getSiteType())) {
            return ! domain.getReceiveSiteCode().equals(box.getReceiveSiteCode());
        }

        BaseStaffSiteOrgDto boxReceiveSite = this.baseMajorManager.getBaseSiteBySiteId(box.getReceiveSiteCode());
        //判断箱号目的的所属站是否与批次目的地一致，如果一致属于跨站发货（通俗理解，小站箱号发大站的批次）
        if (boxReceiveSite != null && BusinessUtil.isMayBelongSiteExist(boxReceiveSite.getSiteType(), boxReceiveSite.getSubType())) {
            return domain.getReceiveSiteCode().equals(baseMajorManager.getPartnerSiteBySiteId(box.getReceiveSiteCode()));
        }
        return false;
    }


    @Override
    public void pushTransferSendTask(SendM domain) {
        Task tTask = genTransferSendTask(domain);
        tTaskService.add(tTask, true);
    }

    /**
     * 构建中转发货任务
     * @param domain
     * @return
     */
    private Task genTransferSendTask(SendM domain) {
        Task tTask = new Task();
        tTask.setBoxCode(domain.getBoxCode());
        tTask.setBody(domain.getSendCode());
        tTask.setCreateSiteCode(domain.getCreateSiteCode());
        tTask.setKeyword2(String.valueOf(domain.getSendType()));
        tTask.setReceiveSiteCode(domain.getReceiveSiteCode());
        tTask.setType(Task.TASK_TYPE_SEND_DELIVERY);
        tTask.setSubType(Task.TASK_SUB_TYPE_BOX_TRANSIT_SEND);
        tTask.setTableName(Task.getTableName(Task.TASK_TYPE_SEND_DELIVERY));
        tTask.setSequenceName(Task.getSequenceName(Task.TABLE_NAME_SEND));
        String ownSign = BusinessHelper.getOwnSign();
        tTask.setOwnSign(ownSign);
        tTask.setKeyword1("5");//5 中转发货补全数据
        tTask.setFingerprint(Md5Helper.encode(domain.getBoxCode() + "_" + domain.getCreateSiteCode() + "_" + domain.getReceiveSiteCode() + "-" + tTask.getKeyword1()));

        log.info("插入中转发车任务，箱号：{}，批次号：{}" ,domain.getBoxCode(), domain.getSendCode());
        return tTask;
    }

    @Override
    @JProfiler(jKey = "dmsWorker.task.sendHandler.boxTransitSend", mState = {JProEnum.TP, JProEnum.FunctionError})
    public boolean findTransitSend(Task task) throws Exception {
        if (task == null || task.getBoxCode() == null
                || task.getCreateSiteCode() == null
                || task.getKeyword2() == null
                || task.getReceiveSiteCode() == null) {
            log.warn("dofindTransitSend:中转任务参数校验失败:{}" ,JsonHelper.toJson(task));
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
                    log.warn("dofindTransitSend-find sendm from db success,value {}" , JsonHelper.toJson(sendMs.get(0)));
                    sendM = sendMs.get(0);
                } else {
                    log.warn("dofindTransitSend-find sendm from db fail,param :{}" , JsonHelper.toJson(queryParam));
                }
            } catch (Throwable e) {
                log.error("dofindTransitSend-发货全程跟踪查询sendM异常,boxCode={}", boxCode,e);
            }
            for (SendDetail tSendDetail : list) {
                //2、处理单个send明细，加入监控
                CallerInfo step2PerMonitor = ProfilerHelper.registerInfo(step2PerMonitorKey, Constants.UMP_APP_NAME_DMSWORKER);
                tSendDetail.setSendDId(null);//把主键置空，避免后面新增时报主键冲突 组织数据将原数据状态清空
                tSendDetail.setCreateSiteCode(bCreateSiteCode);
                tSendDetail.setReceiveSiteCode(bReceiveSiteCode);
                tSendDetail.setSendType(type);
                tSendDetail.setStatus(0);
                tSendDetail.setIsCancel(Constants.OPERATE_TYPE_CANCEL_L);
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
        } else {
            step2TotalMonitor = ProfilerHelper.registerInfo(
                    ProfilerHelper.genKeyByQuantity(step2TotalMonitorKey, 0),
                    Constants.UMP_APP_NAME_DMSWORKER);
            log.warn("dofindTransitSend:根据箱号查询发货明细为空:boxCode={}" , boxCode);
        }
        Profiler.registerInfoEnd(step2TotalMonitor);
        return true;
    }

    private void saveOrUpdateCancel(SendDetail sendDetail) {
        if(log.isDebugEnabled()){
            log.debug("WORKER处理中转发货-插入SEND—D表:{}" , JsonHelper.toJson(sendDetail));
        }
        if (Constants.NO_MATCH_DATA == this.update(sendDetail).intValue()) {
            this.add(sendDetail);
        }
    }

    @Override
    @JProfiler(jKey = UmpConstants.UMP_KEY_BASE + "DeliveryServiceImpl.getCancelSendByBox",
            jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.Heartbeat, JProEnum.FunctionError})
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
        tsendDatail.setIsCancel(Constants.OPERATE_TYPE_CANCEL_Y);
        // 查询未操作取消的跨中转发货明细数据，用于补中转发货sendD数据
        List<SendDetail> sendDetailList = this.sendDatailDao.querySendDatailsBySelective(tsendDatail);
        // 判断sendD数据是否存在，若不存在则视为站点发货至分拣，调用TMS获取箱子对应的装箱明细信息
        if (sendDetailList == null || sendDetailList.isEmpty()) {
            if(sendDetailList == null){
                sendDetailList = new ArrayList<SendDetail>();
            }
            List<SendInfoDto> sendDetailsFromZD = terminalManager.getSendDetailsFromZD(boxCode);
            if (CollectionUtils.isNotEmpty(sendDetailsFromZD)) {
                //根据箱号判断终端箱子的正逆向
                Integer businessType = BusinessUtil.isReverseBoxCode(boxCode) ? Constants.BUSSINESS_TYPE_REVERSE : Constants.BUSSINESS_TYPE_POSITIVE;
                for (SendInfoDto dto : sendDetailsFromZD) {
                    SendDetail dsendDatail = new SendDetail();
                    dsendDatail.setBoxCode(dto.getBoxCode());

                    dsendDatail.setWaybillCode(dto.getWaybillCode());
                    dsendDatail.setPackageBarcode(dto.getPackageBarcode());

                    if (WaybillUtil.isSurfaceCode(dto.getPackageBarcode())) {
                        BaseEntity<PickupTask> pickup = null;
                        try {
                            pickup = this.waybillPickupTaskApi.getDataBySfCode(WaybillUtil.getWaybillCode(dto.getPackageBarcode()));
                        } catch (Exception e) {
                            this.log.error("调用取件单号信息ws接口异常：{}",dto.getPackageBarcode(),e);
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
                    dsendDatail.setIsCancel(Constants.OPERATE_TYPE_CANCEL_L);
                    sendDetailList.add(dsendDatail);
                }
            }else{
                sendDetailList = getCancelSendByBoxFromThird(box);
                if(!CollectionUtils.isEmpty(sendDetailList)){
                    return sendDetailList;
                }
            }
        }
        return sendDetailList;
    }

    /**
     * 获取经济网装箱明细
     * @param box
     * @return
     */
    public List<SendDetail> getCancelSendByBoxFromThird(Box box) {
        if(box == null) {
            return null;
        }
        BaseStaffSiteOrgDto startSite = baseMajorManager.getBaseSiteBySiteId(box.getCreateSiteCode());
        if(!Constants.THIRD_ENET_SITE_TYPE.equals(startSite.getSiteType())){
            return null;
        }
        //防止并发问题。处理中转发货时需要等待经济网想包关系完全存入后方可进行
        if(!economicNetService.isReady(box)){
            //当前箱子没有准备好时需要再次重试一次，仍未准备就绪则抛出异常
            try{
                Thread.sleep(1000);
            }catch (Exception e){}
            if(!economicNetService.isReady(box)){
                throw new EconomicNetException("处理中转发货时需要等待经济网箱包关系未完全初始化，请稍后重试！"+box.getCode());
            }
        }
        List<ThirdBoxDetail> thirdBoxDetails = thirdBoxDetailService.queryByBoxCode(Constants.TENANT_CODE_ECONOMIC, startSite.getSiteCode(), box.getCode());
        if(CollectionUtils.isEmpty(thirdBoxDetails)){
            return null;
        }
        List<SendDetail> sendDetailList = new ArrayList<>(thirdBoxDetails.size());
        for(ThirdBoxDetail detail : thirdBoxDetails){
            SendDetail sendDetail = new SendDetail();
            sendDetail.setBoxCode(detail.getBoxCode());
            sendDetail.setWaybillCode(detail.getWaybillCode());
            sendDetail.setPackageBarcode(detail.getPackageCode());

            sendDetail.setCreateUser(detail.getOperatorName());
            sendDetail.setCreateUserCode(-1);
            sendDetail.setOperateTime(detail.getOperatorTime());
            sendDetail.setSendType(Constants.BUSSINESS_TYPE_POSITIVE);
            if (sendDetail.getPackageBarcode() != null) {
                sendDetail.setPackageNum(BusinessUtil.getPackNumByPackCode(sendDetail.getPackageBarcode()));
            } else {
                sendDetail.setPackageNum(1);
            }
            sendDetail.setIsCancel(Constants.OPERATE_TYPE_CANCEL_L);
            sendDetailList.add(sendDetail);
        }
        return sendDetailList;
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
            log.error("dealWithSendBatch处理异常:{}",JsonHelper.toJson(sendM), e);
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
                                this.arSendDetailProducer.sendOnFailPersistent(sendMessage.getBusinessId(),sendMessage.getText());
                            }
                        }else{
                            log.warn("新发货明细MQ任务根据批次号获取发货明细为空,批次号：{}",sendCode);
                        }
                    }else{
                        log.warn("新发货明细MQ任务根据批次号为空,task_id:{}",task.getId());
                    }
                }
            }else{
                log.warn("新发货明细MQ任务body为空,task_id:{}",task.getId());
            }
            return true;
        }catch (Exception e){
            log.error("新发货明细MQ任务处理失败:{}",JsonHelper.toJson(task),e);
            Profiler.functionError(info);
            return false;
        }finally{
            Profiler.registerInfoEnd(info);
        }
    }

    @Override
    public InvokeResult<Boolean> checkIsSend(String barcode, Integer createSiteCode) {
        InvokeResult<Boolean> result = new InvokeResult<Boolean>();
        result.success();

        if(org.apache.commons.lang3.StringUtils.isBlank(barcode) || createSiteCode == null){
            result.parameterError("入参barcode或createSiteCode为空");
            return result;
        }
        SendDetail param = new SendDetail();

        if(WaybillUtil.isPackageCode(barcode)){
            param.setPackageBarcode(barcode);
        }else if(BusinessUtil.isBoxcode(barcode)){
            param.setBoxCode(barcode);
        }else {
            result.parameterError("条码无效非包裹号箱号");
            return result;
        }
        param.setCreateSiteCode(createSiteCode);
        //未取消
        param.setIsCancel(0);
        //已发货
        param.setStatus(1);
        SendDetail sendDetail = sendDetailService.queryOneSendDatailBySendM(param);
        if(sendDetail == null ){
            result.setData(Boolean.FALSE);
        }else {
            result.setData(Boolean.TRUE);
        }
        return result;

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
      if(list.isEmpty()){
          return null;
      }
      Integer createSiteCode = list.get(0).getCreateSiteCode();
      Integer receiveSiteCode = list.get(0).getReceiveSiteCode();

      List<SendThreeDetail> diffrenceList = new ArrayList<SendThreeDetail>();
      for (SendDetail item : list) { // 遍历该箱的所有包裹
        // 包含派车单且发现包裹不齐，直接退出循环（派车单校验不要明细）
        if (isScheduleRequest && hasDiff > 0) {
          break;
        }
        SendThreeDetail diff = new SendThreeDetail();
        diff.setBoxCode(item.getBoxCode());
        diff.setPackageBarcode(item.getPackageBarcode());
        diff.setMark(AbstructDiffrenceComputer.HAS_SCANED);
        diff.setIsWaybillFull(1);
        if (!item.getWaybillCode().equals(lastWaybillCode)) { // 初次验单 或 每验完一单货，下一单开始验时 进入分支
          // 1.上一单已集齐 则返回0， 并重新初始化 pacageSumShoudBe、scanCount
          // 2.上一单未集齐 则返回未扫描的包裹（即缺失包裹数）循环结束后会根据此判断是否集齐包裹
          hasDiff += invoke(pacageSumShoudBe, scanCount, diffrenceList,receiveSiteCode);
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
          invoke(pacageSumShoudBe, scanCount, diffrenceList,receiveSiteCode); // 遍历完成后，对该箱最后一单的未集齐包裹做处理，如最后一单已齐返回 0
      if (hasDiff > 0) { // hasDiff>0 则未集齐 需移除所有集齐的包裹 只保留未集齐的包裹 并封装list返回pda显示
        List<SendThreeDetail> targetList = removeFullPackages(diffrenceList);

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
      if (null == list || list.isEmpty()) {
          return list;
      }
      List<SendThreeDetail> targetList = new ArrayList<SendThreeDetail>(list.size());
      List<String> packageCodes = new ArrayList<>();
      for (SendThreeDetail item : list) {
        if (item.getMark().equals(AbstructDiffrenceComputer.HAS_SCANED)) {
          targetList.add(item);
        } else {
            packageCodes.add(item.getPackageBarcode());//记录需要查询的包裹号的列表
        }
      }
        targetList.addAll(
                getUnSendPackages(createSiteCode, receiveSiteCode, packageCodes));
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
       * 获取未发货包裹【若已分拣，则箱号显示已分拣箱号，否中央电视台显示空箱号表示未分拣】
       *
       * @param createSiteCode 发货站点
       * @param receiveSiteCode 接收站点
       * @param packageCodes 包裹号列表
       * @return
       */
      private final List<SendThreeDetail> getUnSendPackages(
              Integer createSiteCode, Integer receiveSiteCode, List<String> packageCodes) {

          List<SendThreeDetail> list = new ArrayList<SendThreeDetail>(packageCodes.size());

          //按照运单分组
          Map<String, List<String>> waybillMap = new HashMap<>();
          for (String packageItem : packageCodes) {
              String waybillCodeItem = WaybillUtil.getWaybillCode(packageItem);
              if (!waybillMap.containsKey(waybillCodeItem)) {
                  List<String> packItems = new ArrayList<>();
                  packItems.add(packageItem);
                  waybillMap.put(waybillCodeItem, packItems);
              } else {
                  waybillMap.get(waybillCodeItem).add(packageItem);
              }
          }

          for (Map.Entry<String,List<String>> entry : waybillMap.entrySet()) {
              String waybillCode = entry.getKey();
              List<String> packages = entry.getValue();

              SendDetail sendDatail = new SendDetail();
              sendDatail.setWaybillCode(waybillCode);
              sendDatail.setCreateSiteCode(createSiteCode);
              sendDatail.setReceiveSiteCode(receiveSiteCode);
              sendDatail.setIsCancel(0);
              List<SendDetail> resultList = sendDatailDao.querySendDatailsBySelective(sendDatail);

              Map<String, SendDetail> sendMap = new HashMap<>();
              if (CollectionUtils.isNotEmpty(resultList)) {
                  for (SendDetail sendDetailItem : resultList) {
                      if (!sendMap.containsKey(sendDetailItem.getPackageBarcode())) {
                          sendMap.put(sendDetailItem.getPackageBarcode(), sendDetailItem);
                      }
                  }
              }

              for (String packageCodeItem : packages) {
                  SendThreeDetail mSend = new SendThreeDetail();
                  mSend.setPackageBarcode(packageCodeItem);
                  mSend.setBoxCode(sendMap.containsKey(packageCodeItem)? sendMap.get(packageCodeItem).getBoxCode() : "");
                  mSend.setMark(AbstructDiffrenceComputer.NO_SCANEd);
                  list.add(mSend);
              }
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

    public abstract int invoke(int counter, int scanCount, List<SendThreeDetail> diffrenceList,Integer receiveSiteCode);

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
        private final Logger log = LoggerFactory.getLogger(ForwardSendDiffrence.class);
        @Autowired
        private WaybillCommonService waybillCommonService;
        @Override
        public int invoke(int counter, int scanCount, List<SendThreeDetail> diffrenceList,Integer receiveSiteCode) {
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
                    log.debug("生成全部包裹号：{}" , diffrenceList.get(diffrenceList.size() - 1).getPackageBarcode());
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

        private static final Logger log = LoggerFactory.getLogger(ReverseSendDiffrence.class);
        @Autowired
        private WaybillCommonService waybillCommonService;

        @Autowired
        private SiteService siteService;

        @Override
        public int invoke(int counter, int scanCount, List<SendThreeDetail> diffrenceList,Integer receiveSiteCode) {
            int hasDiff = 0;
            if (counter != scanCount) {/* 有差异*/

                com.jd.bluedragon.common.domain.Waybill waybill = waybillCommonService.findWaybillAndPack(SerialRuleUtil.getWaybillCode(diffrenceList.get(diffrenceList.size() - 1).getPackageBarcode()));
                List<String> geneList = null;
                if (null != waybill && null != waybill.getPackList() && waybill.getPackList().size() > 0) {
                    if(BusinessUtil.isSick(waybill.getWaybillSign())){//病单则直接返回0 不验证包裹是否集齐
                        return 0;
                    }
                    if(BusinessUtil.isPartReverse(waybill.getWaybillSign())){//半退 不验证包裹是否集齐
                        return 0;
                    }
                    if(receiveSiteCode!=null){
                        BaseStaffSiteOrgDto site = siteService.getSite(receiveSiteCode);
                        if(site!=null){
                            Integer spwms_type = Integer.valueOf(PropertiesHelper.newInstance().getValue("spwms_type"));
                            if(BusinessUtil.isPurematch(waybill.getWaybillSign()) && spwms_type.equals(site.getSiteType())
                                    && !BusinessHelper.isC2c(waybill.getWaybillSign())){
                                //纯配非C2C的可半退至备件库 不验证集齐
                                return 0;
                            }
                            Integer wmsType = Integer.valueOf(PropertiesHelper.newInstance().getValue("wms_type"));
                            if (BusinessUtil.preSellAndUnpaidBalance(getOldWaybillSendPay(waybill))
                                    && wmsType.equals(site.getSiteType())){//预售到仓且未付尾款的运单(到仓)，不做集齐校验
                                return 0;
                            }
                        }
                    }

                    geneList = new ArrayList<String>(waybill.getPackList().size());
                    for (Pack p : waybill.getPackList()) {
                        geneList.add(p.getPackCode());
                    }
                } else {
                    log.debug("运单中没有包裹");
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
                    log.debug("未扫描{}" , noScanDetail.getPackageBarcode());
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

        /**
         * 获取sendPay
         * 如果有换单，返回老的sendPay
         * @param waybill
         * @return
         */
        private  String getOldWaybillSendPay(com.jd.bluedragon.common.domain.Waybill waybill) {
            String result = waybill.getSendPay();
            if(BusinessUtil.isWaybillMarkForward(waybill.getWaybillSign())){
                return result;
            }
            com.jd.bluedragon.distribution.base.domain.InvokeResult<com.jd.bluedragon.common.domain.Waybill> waybillInvokeResult = waybillCommonService.getReverseWaybill(waybill.getWaybillCode());
            if (null == waybillInvokeResult || null == waybillInvokeResult.getData()){
                return  result;
            }
            result = waybillInvokeResult.getData().getSendPay();
            return result;
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
            if (log.isInfoEnabled()) {
                log.info("execute device auto send,parameter is :{}" , JsonHelper.toJson(domain));
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
            log.error("一车一单自动发货异常，sendM：{}" , JsonHelper.toJson(domain), e);
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
                // 目前只有分拣机、龙门架发货调用，操作时间已经加了5s，故取消需要减去5s
                this.dellCancelDeliveryMessageWithOperateTime(getCancelSendM(lastSendM, domain, new Date(domain.getOperateTime().getTime() - Constants.DELIVERY_DELAY_TIME)), true);
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
        tsendDatail.setIsCancel(Constants.OPERATE_TYPE_CANCEL_L);
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
        inspection.setBizSource(InspectionBizSourceEnum.AUTOMATIC_SORTING_MACHINE_INSPECTION.getCode());
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
        if(log.isDebugEnabled()){
            log.debug("分拣机自动发货-验货任务插入条数:{}条,请求参数:{}",result,JsonHelper.toJson(task));
        }
    }

	@Override
	@JProfiler(jKey = "DMSWEB.DeliveryServiceImpl.doBoardDelivery", mState = {JProEnum.TP, JProEnum.FunctionError})
	public boolean doBoardDelivery(Task task) {
        if(log.isDebugEnabled()){
            log.debug("组板发货逐单发货开始：{}" ,JsonHelper.toJson(task));
        }
        SendM domain = JsonHelper.fromJson(task.getBody(), SendM.class);
        String boardCode = domain.getBoardCode();
        Response<List<String>> tcResponse = boardCombinationService.getBoxesByBoardCode(boardCode);
        if(log.isInfoEnabled()){
            log.info("组板发货逐单发货查询板号明细：{}" , JsonHelper.toJson(tcResponse));
        }
        if(tcResponse.getData() != null && !tcResponse.getData().isEmpty()){
            // 根据任务类型获取发货业务来源
            SendBizSourceEnum source = this.getBoardDeliveryBizSource(task.getType());
            for(String boxCode : tcResponse.getData()){
                domain.setSendMId(null);
                domain.setBoxCode(boxCode);
                String lastSendedCode = getSendedCode(domain);
                //未发过货的 或者 上次发货和本次发货的批次不一致的 才执行发货
                if (StringUtils.isBlank(lastSendedCode) || !lastSendedCode.equals(domain.getSendCode())) {
                    packageSend(source, domain);
                }
            }
            boardCombinationService.clearBoardCache(boardCode);//发货完成，删除组板时加的缓存
            if(log.isInfoEnabled()){
                log.info("组板发货逐单发货执行完成：{}" , JsonHelper.toJson(domain));
            }
        }else{
            log.warn("组板发货,逐单发货查询板标明细出错,组板发货任务：{}" , JsonHelper.toJson(domain));
            log.warn("组板发货,逐单发货查询板标明细出错，查询明细结果：{}" , JsonHelper.toJson(tcResponse));
        }
        //发货任务处理完毕，删除缓存
        redisManager.del(REDIS_PREFIX_BOARD_DELIVERY +domain.getBoardCode());
        return true;
	}

    /**
     * 获取运单下的所有包裹号
     * 补充按运单分拣
     * 按包裹分页 调用一车一单发货逻辑(需移除补分拣逻辑)
     * 所有分页数据处理完释放按运单锁
     */
    @Override
    public boolean doWaybillSendDelivery(Task task) {
        if(log.isInfoEnabled()){
            log.info("按运单发货任务处理开始：{}" ,JsonHelper.toJson(task));
        }
        SendM domain = JsonHelper.fromJson(task.getBody(),SendM.class);
        if (domain == null) {
            return false;
        }
        // 包裹号转运单号
        if (WaybillUtil.isPackageCode(domain.getBoxCode())) {
            domain.setBoxCode( WaybillUtil.getWaybillCode(domain.getBoxCode()));
        }

        String waybillCode = domain.getBoxCode();
        int pageSize = uccPropertyConfiguration.getWaybillSplitPageSize() == 0 ? WAYBILL_SPLIT_NUM : uccPropertyConfiguration.getWaybillSplitPageSize();
        Waybill waybill = waybillQueryManager.getOnlyWaybillByWaybillCode(waybillCode);
        if (waybill.getGoodNumber() == null) {
            log.error("获取运单中的包裹数量异常!{}", JsonHelper.toJsonMs(task));
            return false;
        }
        lockWaybillSend(waybillCode, domain.getCreateSiteCode(), waybill.getGoodNumber());
        // 按运单补分拣任务
        pushSorting(domain);
        log.info("按运单发货任务处理,补分拣任务完成:waybillCode={}", waybillCode);
        // 按包裹分页 拆分任务调用一车一单发货逻辑
        int splitSize = (waybill.getGoodNumber() / pageSize) + 1;
        for (int i = 1; i <= splitSize; i++) {
            pushWaybillSendSplitTask(domain, Task.TASK_TYPE_SEND_DELIVERY, i, pageSize);
        }
        log.info("按运单发货任务处理,完成 waybillCode={}", waybillCode);

        return true;
    }
    @Override
    public boolean doSendByWaybillSplitTask(Task task) {
        if(log.isInfoEnabled()){
            log.info("按运单发货拆分任务处理,开始：{}" ,JsonHelper.toJson(task));
        }
        SendM domain = JsonHelper.fromJson(task.getBody(),SendM.class);
        if (domain == null) {
            return false;
        }
        String waybillCode = domain.getBoxCode();
        String[] split = task.getKeyword2().split(",");
        BaseEntity<List<DeliveryPackageD>> waybillCodeOfPage = waybillPackageManager.getPackListByWaybillCodeOfPage(waybillCode, Integer.parseInt(split[0]), Integer.parseInt(split[1]));
        if (waybillCodeOfPage == null || CollectionUtils.isEmpty(waybillCodeOfPage.getData())) {
            log.warn("按运单发货拆分任务处理,获取包裹数量为空:waybillCode={}", waybillCode);
            return false;
        }

        // 循环调用按包裹发货逻辑
        for (DeliveryPackageD pack : waybillCodeOfPage.getData()) {
            SendM packSendM = new SendM();
            BeanUtils.copyProperties(domain, packSendM);
            packSendM.setBoxCode(pack.getPackageBarcode());
            // 按包裹锁定
            if(lockWaybillByPack(domain.getCreateSiteCode(), pack.getPackageBarcode())){
                // 一车一单发货逻辑,其中 补分拣逻辑已移除(while循环外已按运单补分拣逻辑)
                if(log.isInfoEnabled()){
                    log.info("按运单发货拆分任务处理,锁定包裹完成,开始调用一车一单逻辑：packSendM={}" ,JsonHelper.toJson(packSendM));
                }
                doPackageSendByWaybill(packSendM);
            }
        }
        if (log.isInfoEnabled()) {
            log.info("按运单发货逐单发货处理完成 waybillCode={},pageNo={},pageSize={}", waybillCode, Integer.parseInt(split[0]), Integer.parseInt(split[1]));
        }

        return true;
    }


    /**
     * 按运单发货任务 , 拆分
     * @param domain 发货数据
     */
    private void pushWaybillSendSplitTask(SendM domain,Integer taskType,Integer pageNo,Integer pageSize) {
        Task tTask = new Task();
        tTask.setBoxCode(domain.getBoxCode());
        tTask.setBody(JsonHelper.toJson(domain));
        tTask.setCreateSiteCode(domain.getCreateSiteCode());
        tTask.setReceiveSiteCode(domain.getReceiveSiteCode());

        tTask.setKeyword1("11");
        tTask.setKeyword2(pageNo + "," + pageSize);
        tTask.setType(taskType);
        tTask.setTableName(Task.getTableName(taskType));
        String ownSign = BusinessHelper.getOwnSign();
        tTask.setOwnSign(ownSign);

        // TB 调度防止重复
        tTask.setFingerprint(Md5Helper.encode(domain.getSendCode() + "_" + tTask.getKeyword1() + "_" + domain.getBoxCode() + "_" + tTask.getKeyword2()));
        if (log.isInfoEnabled()) {
            log.info("按运单发货任务处理,开始写入拆分任务：task={}", JsonHelper.toJsonMs(tTask));
        }

        tTaskService.add(tTask, true);
    }

    /**
     * 按运单发货，将对应的包裹数据缓存，以便判断所有包裹始发均执行完成
     * 以当前是第几个包裹 设置 redis值 中 对应的 位置的 bit 位
     */
    private boolean lockWaybillByPack(Integer createSiteCode, String packageCode) {
        Boolean locked = false;
        try {
            int num = WaybillUtil.getCurrentPackageNum(packageCode);
            String lockWaybillByPackKey = getSendByWaybillPackLockKey(packageCode, createSiteCode);

            // 返回值为false 表示 设置之前 当前offset的值为 false 即 之前没设置过
            locked = !redisClientCache.setBit(lockWaybillByPackKey, num - 1, true);
            redisClientCache.expire(lockWaybillByPackKey, REDIS_CACHE_EXPIRE_TIME, TimeUnit.SECONDS);
        } catch (Exception e) {
            log.error("锁定包裹异常:createSiteCode:{},packageCode:{}", createSiteCode, packageCode);
        }
        log.info("lockWaybillByPack锁定包裹-locked={}:createSiteCode={},packageCode={}", locked, createSiteCode, packageCode);
        return locked;
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
    @Override
    public boolean doBoardDeliveryCancel(Task task){
        if(log.isDebugEnabled()){
            log.debug("按板取消发货开始：{}" ,JsonHelper.toJson(task));
        }
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
                    dellCancelDeliveryMessageWithServerTime(domain, true);
                }catch(Exception e){
                    log.error("按板取消发货，取消包裹/箱号{}失败,失败原因{}",boxCode,e.getMessage(),e);
                    //如果任务处理过程中有异常，任务重跑
                    isSuccess = false;
                    continue;
                }
            }
        }else{
            log.warn("按板取消发货,查询sendm中的发货明细为空：{}" , JsonHelper.toJson(domain));
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
        long startTime=new Date().getTime();
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
            if(log.isInfoEnabled()) {
                log.info(logInfo);
            }

            long endTime = new Date().getTime();

            JSONObject operateRequest=new JSONObject();
            operateRequest.put("boxCode",domain.getBoxCode());

            JSONObject operateResponse=new JSONObject();
            operateResponse.put("info", logInfo);


            BusinessLogProfiler businessLogProfiler=new BusinessLogProfilerBuilder()
                    .operateTypeEnum(BusinessLogConstans.OperateTypeEnum.BOARD_DEBOARDCOMBINATION)
                    .operateRequest(operateRequest)
                    .operateResponse(operateResponse)
                    .processTime(endTime,startTime)
                    .methodName("DeliveryServiceImpl#boardCombinationCancel")
                    .build();
            logEngine.addLog(businessLogProfiler);


            //记录cassandra日志
            addCassandraLog(domain.getBoxCode(),domain.getBoxCode(),logInfo);

        }catch(Exception e){
            Profiler.functionError(info);
            //取消组板异常
            log.error("一车一单发货取消组板异常.包裹号/箱号:{},操作站点:{}",domain.getBoxCode(),domain.getCreateSiteCode(), e);
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
        com.jd.bluedragon.common.domain.Waybill waybill = waybillCommonService.findByWaybillCode(waybillCode);
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
                            response = new DeliveryResponse(DeliveryResponse.CODE_Delivery_SAVE,
                                    HintService.getHint(HintCodeConstants.JP_TEMP_STORE));
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

        log.debug("B网包装耗材确认拦截开始...");
        Waybill waybill = null;
        try {
            //判断快运发货是够是原包发货，原包发货boxCode为包裹号
            if (WaybillUtil.isPackageCode(sendM.getBoxCode()) && ! WaybillUtil.isSurfaceCode(sendM.getBoxCode())) {
                String waybillCode = WaybillUtil.getWaybillCode(sendM.getBoxCode());
                if (StringHelper.isNotEmpty(waybillCode)) {
                    /* 终端包装耗材重塑项目：不进行标位判断，见任务进行拦截 */
                    return waybillConsumableRecordService.needConfirmed(waybillCode);
                } else {
                    //运单号转换失败
                    log.warn("{}转换运单号失败！",sendM.getBoxCode());
                }
            }
        } catch (Exception e) {
            log.error("查询运单是否已经确认耗材失败，运单号：{}" , sendM.getBoxCode(), e);
        }
        return false;
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
        log.debug("查询是否需要录入检疫证票号...");
        String waybillCode = WaybillUtil.getWaybillCode(sendM.getBoxCode());
        Integer siteCode = sendM.getCreateSiteCode();

        if (StringUtils.isBlank(waybillCode) || siteCode == null) {
            return false;
        }

        return coldChainQuarantineManager.isWaybillNeedAddQuarantine(waybillCode,siteCode);
    }

    /**
     * 校验批次创建时间
     * @param sendCode
     * @return
     */
    @Override
    public boolean checkSendCodeIsOld(String sendCode) {
        // 获取批次创建时间
        try{
            String[] sendCodeSplit = sendCode.split(Constants.SEPARATOR_HYPHEN);
            Date createTime = DateHelper.parseDate(sendCodeSplit[2], DateHelper.DATE_FORMAT_YYYYMMDDHHmmssSSS);
            if(DateHelper.daysBetween(createTime,new Date()) > 30){
                return true;
            }
        }catch (Exception e){
            log.error("校验批次【{}】创建时间异常!", sendCode, e);
        }
        return false;
    }

    /**
     * 校验批次是否封车
     *  查redis后查运输接口兜底
     * @param sendCode
     * @return
     */
    @Override
    public boolean checkSendCodeIsSealed(String sendCode) {
        // 查redis后查运输接口兜底
        return newSealVehicleService.checkSendCodeIsSealed(sendCode);
    }

    /**
     * 根据包裹号列表批量更新取消发货的包裹为取消扫描状态
     * @param list 发货包裹列表
     */
    public void updateScanActionByPackageCodes(List<SendDetail> list, SendM sendM) {
        CallerInfo info = Profiler.registerInfo("com.jd.bluedragon.distribution.send.service.DeliveryServiceImpl.updateScanActionByPackageCodes", false, true);
        try {
            if (CollectionUtils.isNotEmpty(list)) {
                GoodsLoadScanRecord record = new GoodsLoadScanRecord();
                List<String> packageCodes = new ArrayList<>();
                for (SendDetail detail : list) {
                    packageCodes.add(detail.getPackageBarcode());
                }
                record.setCreateSiteCode(Long.valueOf(list.get(0).getCreateSiteCode()));
                record.setPackageCodeList(packageCodes);
                record.setUpdateTime(new Date());
                record.setUpdateUserName(sendM.getUpdaterUser());
                record.setUpdateUserCode(sendM.getUpdateUserCode());
                goodsLoadScanRecordDao.updateScanActionByPackageCodes(record);
            }
        } catch (Exception e) {
            Profiler.functionError(info);
            log.error("取消发货--根据包裹号列表批量更新取消发货的包裹为取消扫描状态发生错误e=", e);
        } finally {
            Profiler.registerInfoEnd(info);
        }
    }

    /**
     * 根据板号列表批量更新取消发货的包裹为取消扫描状态
     * @param sendM 发货数据
     */
    public void updateScanActionByBoardCode(SendM sendM) {
        CallerInfo info = Profiler.registerInfo("com.jd.bluedragon.distribution.send.service.DeliveryServiceImpl.updateScanActionByBoardCode", false, true);
        try {
            if (sendM != null) {
                GoodsLoadScanRecord record = new GoodsLoadScanRecord();
                record.setBoardCode(sendM.getBoardCode());
                record.setCreateSiteCode(Long.valueOf(sendM.getCreateSiteCode()));
                record.setUpdateTime(new Date());
                record.setUpdateUserName(sendM.getUpdaterUser());
                record.setUpdateUserCode(sendM.getUpdateUserCode());
                goodsLoadScanRecordDao.updateScanActionByBoardCode(record);
            }
        } catch (Exception e) {
            Profiler.functionError(info);
            log.error("取消发货--根据板号批量更新取消发货的包裹为取消扫描状态发生错误e=", e);
        } finally {
            Profiler.registerInfoEnd(info);
        }
    }

    /**
     * 根据批次号批量更新取消发货的包裹为取消扫描状态
     * @param sendM 发货数据
     */
    public void updateScanActionByBatchCode(SendM sendM) {
        CallerInfo info = Profiler.registerInfo("com.jd.bluedragon.distribution.send.service.DeliveryServiceImpl.updateScanActionByBatchCode", false, true);
        try {
            if (sendM != null) {
                // 根据批次号查询装车任务ID集合
                List<Long> taskIds = loadCarDao.findLoadCarByBatchCode(sendM.getSendCode());
                if (CollectionUtils.isNotEmpty(taskIds)) {
                    // 根据任务ID集合批量修改包裹扫描记录
                    GoodsLoadScanRecord record = new GoodsLoadScanRecord();
                    record.setTaskIdList(taskIds);
                    record.setCreateSiteCode(Long.valueOf(sendM.getCreateSiteCode()));
                    record.setUpdateTime(new Date());
                    record.setUpdateUserName(sendM.getUpdaterUser());
                    record.setUpdateUserCode(sendM.getUpdateUserCode());
                    goodsLoadScanRecordDao.updateScanActionByTaskIds(record);
                }
            }
        } catch (Exception e) {
            Profiler.functionError(info);
            log.error("取消发货--根据批次号批量更新取消发货的包裹为取消扫描状态发生错误e=",e);
        } finally {
            Profiler.registerInfoEnd(info);
        }
    }
}
