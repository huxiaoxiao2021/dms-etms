package com.jd.bluedragon.configuration.ducc;

import java.util.List;
import java.util.Objects;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.configuration.ucc.UccPropertyConfiguration;
import com.jd.ql.dms.print.utils.JsonHelper;

@Component("duccPropertyConfig")
public class DuccPropertyConfig {
	private static final Logger log = LoggerFactory.getLogger(DuccPropertyConfig.class);
	/**
	 * 使用ducc
	 */
	@Value("${duccPropertyConfig.useDucc:false}")
	private boolean useDucc;
	
	@Value("${duccPropertyConfig.testUcc:test1}")
	private String testUcc;
	/**
	 *BC箱号绑定WJ箱号个数
	 */
	@Value("${duccPropertyConfig.BCContainWJNumberLimit:10}")
	private int BCContainWJNumberLimit;

	/**
	 *WJ箱号包裹数量限制
	 */
	@Value("${duccPropertyConfig.WJPackageNumberLimit:1000}")
	private int WJPackageNumberLimit;

	private List<String> _multiplePackageSpotCheckSitesList = new ArrayList<>();

	@Value("${duccPropertyConfig.addiOwnNumberConf:010K1961638,}")
	private String addiOwnNumberConf;

	/**
	 *人工抽检是否开启AI图片识别
	 */
	@Value("${duccPropertyConfig.aiDistinguishSwitch:true}")
	private boolean aiDistinguishSwitch;

	/**
	 *BC箱号强制绑定循环集包袋拦截黑名单  1代表不拦截  参数是站点代表站点拦截
	 */
	@Value("${duccPropertyConfig.allBCBoxFilterWebSite:-1}")
	private String allBCBoxFilterWebSite;

	/**
	 *纯配外单0重量拦截黑名单： 1 是全部不拦截，如果是 站点，站点  代表站点是拦截黑名单
	 */
	@Value("${duccPropertyConfig.allPureValidateWeightWebSite:-1}")
	private String allPureValidateWeightWebSite;

	/**
	 *转运抽检是否执行新抽检模式
	 */
	@Value("${duccPropertyConfig.androidIsExecuteNewSpotCheck:false}")
	private boolean androidIsExecuteNewSpotCheck;

	@Value("${duccPropertyConfig.approvalSwitch:false}")
	private boolean approvalSwitch;

	/**
	 *JMQ 直接存入JMQ,TBSCHEDULE 直接存入DB或者Redis,FAILOVER 在JMQ、TBSCHEDULE按顺序failover
	 */
	@Value("${duccPropertyConfig.asynBufferDynamicProducerProducerType:TBSCHEDULE}")
	private String asynBufferDynamicProducerProducerType;

	/**
	 *是否存储消费成功的任务信息
	 */
	@Value("${duccPropertyConfig.asynBufferJmqComsumerTaskProcessorPostTaskStoreEnbaled:true}")
	private boolean asynBufferJmqComsumerTaskProcessorPostTaskStoreEnbaled;

	/**
	 *不开启jmq模式的task类型,配置规则：taskType-keyword1；taskType-keyword1
	 */
	@Value("${duccPropertyConfig.asynBufferNotenabledTaskKeyword1:1300-4;1300-6}")
	private String asynBufferNotenabledTaskKeyword1;

	/**
	 *开启的多级异步缓冲组件的任务类型列表，任务类型为整形值，多个类型以分号隔开，如1;2
	 */
	@Value("${duccPropertyConfig.asynbufferEnabledTaskType:3300;0;1;30;40;60;80;1300;4300;6667;1200;1210;1220;1110;1130;1600;1140;1120;1601;1400;1300;1160;6666;1800;7779;1180;1260}")
	private String asynbufferEnabledTaskType;

	/**
	 *龙门架上传称重任务接口切换，开放的场地
	 */
	@Value("${duccPropertyConfig.automaticWeightVolumeExchangeSiteCode:''}")
	private String automaticWeightVolumeExchangeSiteCode;

	/**
	 *龙门架上传称重任务接口切换
	 */
	@Value("${duccPropertyConfig.automaticWeightVolumeExchangeSwitch:false}")
	private boolean automaticWeightVolumeExchangeSwitch;

	/**
	 *反调度校验
	 */
	@Value("${duccPropertyConfig.backDispatchCheck:true}")
	private boolean backDispatchCheck;

	/**
	 *大运单报警
	 */
	@Value("${duccPropertyConfig.bigWaybillWaringSize:5000}")
	private Integer bigWaybillWaringSize;

	/**
	 *组板校验切换开关
	 */
	@Value("${duccPropertyConfig.boardCombinationSwitchVerToWebSites:-1}")
	private String boardCombinationSwitchVerToWebSites;

	/**
	 *PDA建箱包裹数量限制 试用站点
	 */
	@Value("${duccPropertyConfig.boxLimitSites:''}")
	private String boxLimitSites;

	/**
	 *日志查询开关 0：关闭 1：开启
	 */
	@Value("${duccPropertyConfig.businessLogQueryPageSwitch:'1'}")
	private String businessLogQueryPageSwitch;

	/**
	 *取消鸡毛信切换OMS接口开关，true：切换；false：不切换
	 */
	@Value("${duccPropertyConfig.cancelJimaoxinSwitchToOMS:false}")
	private boolean cancelJimaoxinSwitchToOMS;

	/**
	 *cassandra服务的全局开关，若资源不可用则可以关闭写入。
	 */
	@Value("${duccPropertyConfig.cassandraGlobalSwitch:true}")
	private boolean cassandraGlobalSwitch;

	/**
	 *箱号是否发货开关（true:老逻辑，false:新逻辑）
	 */
	@Value("${duccPropertyConfig.checkBoxSendedSwitchOn:true}")
	private boolean checkBoxSendedSwitchOn;

	@Value("${duccPropertyConfig.checkDeviceLocationInRealTimeSwitch:-1}")
	private Integer checkDeviceLocationInRealTimeSwitch;

	/**
	 *是否调度终端 判断签单返还审批
	 */
	@Value("${duccPropertyConfig.checkSignAndReturn:true}")
	private boolean checkSignAndReturn;

	/**
	 *校验站点子类型是否三方:16
	 */
	@Value("${duccPropertyConfig.checkSiteSubType:false}")
	private boolean checkSiteSubType;

	/**
	 *出管国际化页面查询接口开关 true查新接口，false 查旧接口
	 */
	@Value("${duccPropertyConfig.chuguanNewPageQuerySwitch:false}")
	private boolean chuguanNewPageQuerySwitch;

	/**
	 *写出管是否开启供应链中台二期逻辑true开启，false不开启
	 */
	@Value("${duccPropertyConfig.chuguanPurchaseAndSaleSwitch:true}")
	private boolean chuguanPurchaseAndSaleSwitch;

	/**
	 *限制按钮点击间隔时间（秒），配置-1关闭提示
	 */
	@Value("${duccPropertyConfig.clickIntervalSecond:-1}")
	private int clickIntervalSecond;

	/**
	 *客户端下线菜单配置
	 */
	@Value("${duccPropertyConfig.clientOfflineMenuConfig:''}")
	private String clientOfflineMenuConfig;

	/**
	 *打印客户端大查询限制查询间隔时间
	 */
	@Value("${duccPropertyConfig.clientPrintQueryGapTime:10}")
	private int clientPrintQueryGapTime;

	/**
	 *异步缓冲框架，JMQ消费失败不再降级为TB任务  1：关闭DB模式
	 */
	@Value("${duccPropertyConfig.closeAsynBufferSaveTaskToDb:0}")
	private String closeAsynBufferSaveTaskToDb;

	/**
	 *集货区是否删除站点配置
	 */
	@Value("${duccPropertyConfig.collectGoodsDeleteSites:-1}")
	private String collectGoodsDeleteSites;

	/**
	 *集包地配置开启开关（站点以","分开）
	 */
	@Value("${duccPropertyConfig.collectionAddressSiteCodes:910,364605}")
	private String collectionAddressSiteCodes;

	/**
	 *开关控制是否校验包裹号  true 校验  false 不校验
	 */
	@Value("${duccPropertyConfig.controlCheckPackage:true}")
	private boolean controlCheckPackage;

	/**
	 *开关控制是否校验路由异常提醒  true 提醒  false不提醒
	 */
	@Value("${duccPropertyConfig.controlCheckRoute:true}")
	private boolean controlCheckRoute;

	/**
	 *老发货改造兜底task可以执行任务的次数阈值
	 */
	@Value("${duccPropertyConfig.createSendTaskExecuteCount:30}")
	private Integer createSendTaskExecuteCount;

	/**
	 *老发货改造-兜底task超时时间阈值
	 */
	@Value("${duccPropertyConfig.createSendTasktimeOut:30}")
	private Integer createSendTasktimeOut;

	/**
	 *快运大宗订单包裹数定义
	 */
	@Value("${duccPropertyConfig.dazongPackageOperateMax:100}")
	private Integer dazongPackageOperateMax;

	/**
	 *安卓快运发货隐藏站点编号
	 */
	@Value("${duccPropertyConfig.deliverHideSites:910}")
	private String deliverHideSites;

	/**
	 *老发货异步处理开关
	 */
	@Value("${duccPropertyConfig.deliverySendAsyncSite:''}")
	private String deliverySendAsyncSite;

	/**
	 *老发货异步任务延时消费毫秒数
	 */
	@Value("${duccPropertyConfig.deliverySendTaskSleepMills:1000}")
	private int deliverySendTaskSleepMills;

	/**
	 *取消发货校验封车业务开关。0：关闭  非0：开启
	 */
	@Value("${duccPropertyConfig.dellCancelDeliveryCheckSealCar:1}")
	private String dellCancelDeliveryCheckSealCar;

	/**
	 *dws抽检AI识别大包裹限制
	 */
	@Value("${duccPropertyConfig.deviceAIDistinguishPackNum:100}")
	private int deviceAIDistinguishPackNum;

	/**
	 *dws抽检AI识别开关，多个场地以,分隔
	 */
	@Value("${duccPropertyConfig.deviceAIDistinguishSwitch:910}")
	private String deviceAIDistinguishSwitch;

	/**
	 *禁用登陆老接口
	 */
	@Value("${duccPropertyConfig.disablePdaOldLogin:false}")
	private boolean disablePdaOldLogin;

	private List<Integer> dpSiteCodeList;

	/**
	 *DP上海虚拟分拣中心
	 */
	@Value("${duccPropertyConfig.dpSiteCodes:''}")
	private String dpSiteCodes;

	/**
	 *批量一车一单发货，德邦单匹配德邦批次号开关
	 */
	@Value("${duccPropertyConfig.dpWaybillMatchSendCodeSwitch:true}")
	private boolean dpWaybillMatchSendCodeSwitch;

	/**
	 *接受经济网推送箱包关系  false-不接收永远返回成功
	 */
	@Value("${duccPropertyConfig.eNetSyncWaybillCodeAndBoxCode:true}")
	private boolean eNetSyncWaybillCodeAndBoxCode;

	/**
	 *经济网推送智腾达失败重试
	 */
	@Value("${duccPropertyConfig.economicNetPushZTDRetry:true}")
	private boolean economicNetPushZTDRetry;

	/**
	 *众邮重量拦截 true 拦截，false 不拦截
	 */
	@Value("${duccPropertyConfig.economicNetValidateWeightSwitch:true}")
	private boolean economicNetValidateWeightSwitch;

	@Value("${duccPropertyConfig.enableGoodsAreaOfTysScan:true}")
	private boolean enableGoodsAreaOfTysScan;

	/**
	 *单个导出页面限制最大并行导出执行数   -1 作为系统降级不支持导出
	 */
	@Value("${duccPropertyConfig.exportConcurrencyLimitNum:50}")
	private Integer exportConcurrencyLimitNum;

	/**
	 *抽检导出最大限制
	 */
	@Value("${duccPropertyConfig.exportSpotCheckMaxSize:10000}")
	private Integer exportSpotCheckMaxSize;

	/**
	 *面单举报一二级类型配置
	 */
	@Value("${duccPropertyConfig.faceAbnormalReportConfig:''}")
	private String faceAbnormalReportConfig;

	@Value("${duccPropertyConfig.filterSendCodeSwitch:false}")
	private boolean filterSendCodeSwitch;

	/**
	 *易冻损货物暂存时间最小限制时间
	 */
	@Value("${duccPropertyConfig.goodsResidenceTime:3}")
	private int goodsResidenceTime;

	/**
	 *隐藏特殊始发场地目的场地名单
	 */
	@Value("${duccPropertyConfig.hideSpecialStartSitPrintDestinationSiteList:''}")
	private String hideSpecialStartSitPrintDestinationSiteList;

	private List<String> hideSpecialStartSitPrintDestinationSiteStrList = new ArrayList<>();

	/**
	 *隐藏特殊始发场地替换字符
	 */
	@Value("${duccPropertyConfig.hideSpecialStartSitePrintReplaceSymbol:''}")
	private String hideSpecialStartSitePrintReplaceSymbol;

	@Value("${duccPropertyConfig.hideSpecialStartSitePrintReplaceSymbolMaxLength:20}")
	private int hideSpecialStartSitePrintReplaceSymbolMaxLength;

	/**
	 *隐藏特殊始发场地名称开关，0-关，1-开
	 */
	@Value("${duccPropertyConfig.hideSpecialStartSitePrintSwitch:0}")
	private int hideSpecialStartSitePrintSwitch;

	/**
	 *身份证识别功能，站点开关。","为分隔符
	 */
	@Value("${duccPropertyConfig.identityRecogniseSiteSwitch:''}")
	private String identityRecogniseSiteSwitch;

	/**
	 *单次插入数据库的条数
	 */
	@Value("${duccPropertyConfig.insertDbRowsOneTime:100}")
	private int insertDbRowsOneTime;

	@Value("${duccPropertyConfig.inspectNoSendNoLoadWaybillDemotion:false}")
	private boolean inspectNoSendNoLoadWaybillDemotion;

	/**
	 *提供标准包裹验货消息
	 */
	@Value("${duccPropertyConfig.inspectionAggEffectiveSites:-1}")
	private String inspectionAggEffectiveSites;

	/**
	 *验货依赖物料开关 true：不依赖
	 */
	@Value("${duccPropertyConfig.inspectionAssertDemotion:false}")
	private boolean inspectionAssertDemotion;

	/**
	 *大运单拆分包裹验货的分拣中心，配置-1则关闭，配置ALL全部开启
	 */
	@Value("${duccPropertyConfig.inspectionBigWaybillEffectiveSites:-1}")
	private String inspectionBigWaybillEffectiveSites;

    /**
     * 西藏模式业务场景开关，按分拣中心归属的省份配置，不配置业务场景不生效，配置ALL全国生效
     */
	@Value("${duccPropertyConfig.itmsBizEnableSwitch:''}")
	private String itmsBizEnableSwitch;

	/**
	 *判断包裹是否打印的逻辑，包含终端首次打印的数据。默认不包含 0：不包含；1：包含
	 */
	@Value("${duccPropertyConfig.judgePackagePrintedIncludeSiteTerminal:'0'}")
	private String judgePackagePrintedIncludeSiteTerminal;

	/**
	 *拣运基础服务开关
	 */
	@Value("${duccPropertyConfig.jyBasicServerSwitch:false}")
	private boolean jyBasicServerSwitch;

	/**
	 *拣运功能降级开关配置
	 */
	@Value("${duccPropertyConfig.jyDemotionConfig:''}")
	private String jyDemotionConfig;

	/**
	 *
	 */
	@Value("${duccPropertyConfig.jySendTaskCreateTimeBeginDay:30}")
	private Integer jySendTaskCreateTimeBeginDay;

	/**
	 *拣运发货任务装载率上下限配置
	 */
	@Value("${duccPropertyConfig.jySendTaskLoadRateLimit:80,150}")
	private String jySendTaskLoadRateLimit;

	@Value("${duccPropertyConfig.jySendTaskLoadRateLowerLimit:80}")
	private Integer jySendTaskLoadRateLowerLimit;

	@Value("${duccPropertyConfig.jySendTaskLoadRateUpperLimit:150}")
	private Integer jySendTaskLoadRateUpperLimit;

	/**
	 *
	 */
	@Value("${duccPropertyConfig.jySendTaskPlanTimeBeginDay:1}")
	private Integer jySendTaskPlanTimeBeginDay;

	/**
	 *
	 */
	@Value("${duccPropertyConfig.jySendTaskPlanTimeEndDay:2}")
	private Integer jySendTaskPlanTimeEndDay;

	@Value("${duccPropertyConfig.jyTaskPageMax:200}")
	private Integer jyTaskPageMax;

	/**
	 *到车任务按积分排序开关 1：开启
	 */
	@Value("${duccPropertyConfig.jyUnSealTaskOrderByIntegral:0}")
	private Integer jyUnSealTaskOrderByIntegral;

	@Value("${duccPropertyConfig.jyUnSealTaskSwitchToEs:0}")
	private Integer jyUnSealTaskSwitchToEs;

	/**
	 *转运卸车岗查询列表时间过滤
	 */
	@Value("${duccPropertyConfig.jyUnloadCarListQueryDayFilter:3}")
	private Integer jyUnloadCarListQueryDayFilter;

	/**
	 *拣运卸车逐单卸阈值
	 */
	@Value("${duccPropertyConfig.jyUnloadSingleWaybillThreshold:20}")
	private Integer jyUnloadSingleWaybillThreshold;

	/**
	 *是否限制终端人员使用包裹补打 1：限制 0：不限制
	 */
	@Value("${duccPropertyConfig.limitSiteUsePackReprint:'0'}")
	private String limitSiteUsePackReprint;

	/**
	 *装车任务-最大包裹数限制
	 */
	@Value("${duccPropertyConfig.loadScanTaskPackageMaxSize:500}")
	private int loadScanTaskPackageMaxSize;

	/**
	 *装车-版号转包裹号最大包裹数限制
	 */
	@Value("${duccPropertyConfig.loadScanTaskPackageSize:100}")
	private int loadScanTaskPackageSize;

	/**
	 *装车扫描每个任务下的运单数量上线
	 */
	@Value("${duccPropertyConfig.loadScanTaskWaybillSize:200}")
	private int loadScanTaskWaybillSize;

	/**
	 *决定是否使用kafka写入日志
	 */
	@Value("${duccPropertyConfig.logToBusinessLogByKafka:true}")
	private boolean logToBusinessLogByKafka;

	/**
	 *打印客户端菜单功能配置，根据菜单编码和站点类型和站点子类型获取功能权限
	 */
	@Value("${duccPropertyConfig.menuCodeFuncConfig:}")
	private String menuCodeFuncConfig;

	/**
	 *打印客户端菜单功能配置，根据菜单编码和站点类型和站点子类型获取功能权限（新）
	 */
	@Value("${duccPropertyConfig.menuCodeFuncConfigNew:}")
	private String menuCodeFuncConfigNew;

	/**
	 *bool值，表示数据库写复制到备份数据库（mysql）的开关，true表示开
	 */
	@Value("${duccPropertyConfig.migrationDbBackupReplicateEnable:true}")
	private boolean migrationDbBackupReplicateEnable;

	/**
	 *bool值，数据库复制中是否忽略异常，true表示忽略异常，false表示不忽
	 */
	@Value("${duccPropertyConfig.migrationDbBackupReplicateIgnoreExp:true}")
	private boolean migrationDbBackupReplicateIgnoreExp;

	/**
	 *一单多件抽检场地配置
	 */
	@Value("${duccPropertyConfig.multiplePackageSpotCheckSites:''}")
	private String multiplePackageSpotCheckSites;

	/**
	 *一单多件抽检开关，0-关，1-开
	 */
	@Value("${duccPropertyConfig.multiplePackageSpotCheckSwitch:0}")
	private int multiplePackageSpotCheckSwitch;

	private List<String> needInterceptUrlList;

	/**
	 *
	 */
	@Value("${duccPropertyConfig.needInterceptUrls:*}")
	private String needInterceptUrls;

	/**
	 *百川接口-开关 true  false
	 */
	@Value("${duccPropertyConfig.needUseNewReverseApi:true}")
	private boolean needUseNewReverseApi;

	private List<Integer> needValidateMainLineBizSourceCodes;

	/**
	 *传摆发货-干支限制业务列表[25,2]
	 */
	@Value("${duccPropertyConfig.needValidateMainLineBizSourceList:[25]}")
	private String needValidateMainLineBizSourceList;

	/**
	 *日志页面提示
	 */
	@Value("${duccPropertyConfig.newLogPageTips:''}")
	private String newLogPageTips;

	/**
	 *打印客户端无权限菜单编码配置，根据站点类型和站点子类型配置不同菜单编码
	 */
	@Value("${duccPropertyConfig.noAuthMenuConfig:}")
	private String noAuthMenuConfig;

	/**
	 *打印客户端无权限菜单编码配置，根据站点类型和站点子类型配置不同菜单编码（新）
	 */
	@Value("${duccPropertyConfig.noAuthMenuConfigNew:}")
	private String noAuthMenuConfigNew;

	/**
	 *自动签退超过多少小时未签退的数据
	 */
	@Value("${duccPropertyConfig.notSignedOutRecordMoreThanHours:-1}")
	private int notSignedOutRecordMoreThanHours;

	@Value("${duccPropertyConfig.notSignedOutRecordRangeHours:12}")
	private int notSignedOutRecordRangeHours;

	private List<Integer> notValidateTransTypeCodes;

	@Value("${duccPropertyConfig.notValidateTransTypeCodesList:[3,4,10,11]}")
	private String notValidateTransTypeCodesList;

	@Value("${duccPropertyConfig.offlineCurrentLimitingCount:-1}")
	private Integer offlineCurrentLimitingCount;

	/**
	 *true：保存日志 false：不再保存
	 */
	@Value("${duccPropertyConfig.offlineLogGlobalSwitch:true}")
	private boolean offlineLogGlobalSwitch;

	/**
	 *pda下线菜单编码
	 */
	@Value("${duccPropertyConfig.offlinePdaMenuCode:0101015}")
	private String offlinePdaMenuCode;

	/**
	 *下线一键封车
	 */
	@Value("${duccPropertyConfig.offlineQuickSeal:false}")
	private boolean offlineQuickSeal;

	/**
	 *离线任务操作时间限制：默认在系统时间之前96h丢弃，之内则记录上传的操作时间
	 */
	@Value("${duccPropertyConfig.offlineTaskOperateTimeBeforeNowLimitHours:96}")
	private int offlineTaskOperateTimeBeforeNowLimitHours;

	/**
	 *离线任务矫正操作时间得时间范围，超过该值则会被纠正为当前时间（单位：h）
	 */
	@Value("${duccPropertyConfig.offlineTaskOperateTimeCorrectHours:24}")
	private int offlineTaskOperateTimeCorrectHours;

	/**
	 *离线任务上传拦截报表，0 - 全部开启，-1 - 全部关闭，类似1243,3534表示具体场地
	 */
	@Value("${duccPropertyConfig.offlineTaskReportInterceptSites:-1}")
	private String offlineTaskReportInterceptSites;

	/**
	 *老日志页面提示
	 */
	@Value("${duccPropertyConfig.oldLogPageTips:''}")
	private String oldLogPageTips;

	@Value("${duccPropertyConfig.oldSendSplitPageSize:30}")
	private Integer oldSendSplitPageSize;

	/**
	 *导出分页单次查询数据量
	 */
	@Value("${duccPropertyConfig.oneQuerySize:1000}")
	private Integer oneQuerySize;

	/**
	 *0不关闭入口；1关闭基础资料维护入口；2关闭耗材明细的增加和删除按钮；3关闭两者
	 */
	@Value("${duccPropertyConfig.packConsumableSwitch:0}")
	private Integer packConsumableSwitch;

	/**
	 *包裹补打强制拦截状态码
	 */
	@Value("${duccPropertyConfig.packRePrintInterceptStatus:-790}")
	private String packRePrintInterceptStatus;

	/**
	 *转运单个包裹重量阈值（单位kg）
	 */
	@Value("${duccPropertyConfig.packageWeightLimit:'2000'}")
	private String packageWeightLimit;

	/**
	 *并发获取包裹明细 false 关闭
	 */
	@Value("${duccPropertyConfig.paralleGetPackageSwitch:false}")
	private boolean paralleGetPackageSwitch;

	/**
	 *PDA通知自动拉取间隔时间(单位秒)
	 */
	@Value("${duccPropertyConfig.pdaNoticePullIntervalTime:1800}")
	private Integer pdaNoticePullIntervalTime;

	/**
	 *现场预分拣 超区运单拦截开关;true 开启拦截
	 */
	@Value("${duccPropertyConfig.preOutZoneSwitch:true}")
	private boolean preOutZoneSwitch;

	/**
	 *一键封车空批次剔除开关 1：开启剔除 0：关闭
	 */
	@Value("${duccPropertyConfig.preSealVehicleRemoveEmptyBatchCode:1}")
	private String preSealVehicleRemoveEmptyBatchCode;

	/**
	 *现场预分拣校验开关
	 */
	@Value("${duccPropertyConfig.preSortOnSiteSwitchOn:true}")
	private boolean preSortOnSiteSwitchOn;

	/**
	 *按运单大打印回调异步处理的包裹数限制
	 */
	@Value("${duccPropertyConfig.printCompleteCallbackAsyncPackageNum:500}")
	private int printCompleteCallbackAsyncPackageNum;

	/**
	 *打印交接清单新查询开通场地,1)、字符串false代表不开启, 2)、多个场地以,分隔,3)、字符串true代表全国
	 */
	@Value("${duccPropertyConfig.printHandoverListSites:false}")
	private String printHandoverListSites;

	@Value("${duccPropertyConfig.printScrollQueryCountLimit:200}")
	private int printScrollQueryCountLimit;

	/**
	 *查询敏感数据开关
	 */
	@Value("${duccPropertyConfig.querySensitiveFlag:false}")
	private boolean querySensitiveFlag;

	/**
	 *一键封车友情提示
	 */
	@Value("${duccPropertyConfig.quickSealTips:''}")
	private String quickSealTips;

	/**
	 *读转运卸车表开关
	 */
	@Value("${duccPropertyConfig.readUnloadFromTys:false}")
	private boolean readUnloadFromTys;

	/**
	 *#任务redis开关0-关闭 1-开启
	 */
	@Value("${duccPropertyConfig.redisSwitchOn:0}")
	private String redisSwitchOn;

	/**
	 *封车是否剔除空批次  1：开启剔除 0：关闭
	 */
	@Value("${duccPropertyConfig.removeEmptyBatchCode:1}")
	private String removeEmptyBatchCode;

	/**
	 *
	 */
	@Value("${duccPropertyConfig.restApiOuthSwitch:true}")
	private boolean restApiOuthSwitch;

	@Value("${duccPropertyConfig.scannerOperateCalculateIfInterceptSites:''}")
	private String scannerOperateCalculateIfInterceptSites;

	/**
	 *预分拣返调度校验同城 1:开启；0：关闭
	 */
	@Value("${duccPropertyConfig.scheduleSiteCheckSameCity:''}")
	private String scheduleSiteCheckSameCity;

	/**
	 *
	 */
	@Value("${duccPropertyConfig.scrollQuerySize:2000}")
	private int scrollQuerySize;

	/**
	 *
	 */
	@Value("${duccPropertyConfig.sealStatusBatchSizeLimit:30}")
	private int sealStatusBatchSizeLimit;

	/**
	 *作业工作台待解封车强制降级 1：强制 0：不强制
	 */
	@Value("${duccPropertyConfig.sealTaskForceFallback:0}")
	private int sealTaskForceFallback;

	/**
	 *
	 */
	@Value("${duccPropertyConfig.sealTaskHystrixProps: ''}")
	private String sealTaskHystrixProps;

	/**
	 *封车体积校验开关（站点以","分开）
	 */
	@Value("${duccPropertyConfig.sealVolumeCheckSites:-1}")
	private String sealVolumeCheckSites;

	/**
	 *安全次数开关
	 */
	@Value("${duccPropertyConfig.securitySwitch:true}")
	private boolean securitySwitch;

	/**
	 *批次有效性校验的场地
	 */
	@Value("${duccPropertyConfig.sendCodeEffectiveValidation:''}")
	private String sendCodeEffectiveValidation;

	/**
	 *切换新的批次号生成器的开关
	 */
	@Value("${duccPropertyConfig.sendCodeGenSwitchOn:false}")
	private boolean sendCodeGenSwitchOn;

	/**
	 *
	 */
	@Value("${duccPropertyConfig.sensitiveInfoHideSwitch:false}")
	private Boolean sensitiveInfoHideSwitch;

	/**
	 *一车一单发货分拣验证开关
	 */
	@Value("${duccPropertyConfig.singleSendSwitchVerToWebSites:-1}")
	private String singleSendSwitchVerToWebSites;

	/**
	 *启用批次有效性校验的分拣中心. 分拣中心ID逗号分隔
	 */
	@Value("${duccPropertyConfig.siteEnableSendCodeEffectiveValidation:''}")
	private String siteEnableSendCodeEffectiveValidation;

	/**
	 *站点平台打印菜单是否校验功能
	 */
	@Value("${duccPropertyConfig.sitePlateIsCheckFunc:false}")
	private boolean sitePlateIsCheckFunc;

	/**
	 *站点查询最大限制
	 */
	@Value("${duccPropertyConfig.siteQueryLimit:500}")
	private Integer siteQueryLimit;

	/**
	 *分拣查询使用的模式，可配置DMS、MIDDLEEND、FAILOVER三个值
	 */
	@Value("${duccPropertyConfig.sortingQueryMode:FAILOVER}")
	private String sortingQueryMode;

	/**
	 *分拣实操模式，可设置DMS、MIDDLEEND、FAILOVER三个值
	 */
	@Value("${duccPropertyConfig.sortingServiceMode:FAILOVER}")
	private String sortingServiceMode;

	/**
	 *抽检是否控制下发
	 */
	@Value("${duccPropertyConfig.spotCheckIssueControl:true}")
	private boolean spotCheckIssueControl;

	@Value("${duccPropertyConfig.spotCheckNoExcessLimit:1.5}")
	private double spotCheckNoExcessLimit;

	/**
	 *执行抽检改造的站点集合（以英文逗号分隔，true表示全国，false表示全部不开启）
	 */
	@Value("${duccPropertyConfig.spotCheckReformSiteCodes:true}")
	private String spotCheckReformSiteCodes;

	/**
	 *停止写分拣卸车表开关
	 */
	@Value("${duccPropertyConfig.stopWriteUnloadFromDms:false}")
	private boolean stopWriteUnloadFromDms;

	/**
	 *uccPropertyConfiguration.switchVerToWebSites
	 */
	@Value("${duccPropertyConfig.switchVerToWebSites:-1}")
	private String switchVerToWebSites;

	/**
	 *
	 */
	@Value("${duccPropertyConfig.syncJySealStatusSwitch:false}")
	private boolean syncJySealStatusSwitch;

	/**
	 *true：保存日志 false：不保存
	 */
	@Value("${duccPropertyConfig.systemLogGlobalSwitch:true}")
	private boolean systemLogGlobalSwitch;

	/**
	 *转运卸车岗交班次数最大数量
	 */
	@Value("${duccPropertyConfig.tysUnloadTaskHandoverMaxSize:3}")
	private Integer tysUnloadTaskHandoverMaxSize;

	/**
	 *转运卸车完成后补扫时间限制（小时），过后禁止补扫
	 */
	@Value("${duccPropertyConfig.tysUnloadTaskSupplementScanLimitHours:72}")
	private Integer tysUnloadTaskSupplementScanLimitHours;

	/**
	 *转运组板最大包裹数
	 */
	@Value("${duccPropertyConfig.unloadBoardBindingsMaxCount:100}")
	private Integer unloadBoardBindingsMaxCount;

	/**
	 *转运卸车缓存时长（单位小时）
	 */
	@Value("${duccPropertyConfig.unloadCacheDurationHours:24}")
	private Integer unloadCacheDurationHours;

	@Value("${duccPropertyConfig.uploadDeviceLocationInterval:-1}")
	private Integer uploadDeviceLocationInterval;

	/**
	 *装车扫描采用新ES库存查询方式的站点
	 */
	@Value("${duccPropertyConfig.useNewInventorySiteCodes:''}")
	private String useNewInventorySiteCodes;

	/**
	 *虚拟组板自动完结时间，单位：天
	 */
	@Value("${duccPropertyConfig.virtualBoardAutoCloseDays:1}")
	private Integer virtualBoardAutoCloseDays;

	/**
	 *分拣组板功能开通场地
	 */
	@Value("${duccPropertyConfig.virtualBoardCanUseSite:-1}")
	private String virtualBoardCanUseSite;

	private List<String> virtualBoardCanUseSiteList;

	/**
	 *虚拟组板最多流向个数
	 */
	@Value("${duccPropertyConfig.virtualBoardMaxDestinationCount:5}")
	private int virtualBoardMaxDestinationCount;

	/**
	 *虚拟组板最多放置包裹个数
	 */
	@Value("${duccPropertyConfig.virtualBoardMaxItemCount:50}")
	private int virtualBoardMaxItemCount;

	@Value("${duccPropertyConfig.virtualSiteCode:1721378}")
	private Integer virtualSiteCode;

	/**
	 *运单最大包裹数
	 */
	@Value("${duccPropertyConfig.waybillMaxPackNum:40000}")
	private int waybillMaxPackNum;

	/**
	 *分拣拆分任务分页包裹数
	 */
	@Value("${duccPropertyConfig.waybillSplitPageSize:1000}")
	private int waybillSplitPageSize;

	/**
	 *转运单个运单重量阈值（单位kg）
	 */
	@Value("${duccPropertyConfig.waybillWeightLimit:'10000'}")
	private String waybillWeightLimit;

	/**
	 *称重量方拦截开关
	 */
	@Value("${duccPropertyConfig.weightVolumeFilterWholeCountryFlag:false}")
	private boolean weightVolumeFilterWholeCountryFlag;

	/**
	 *称重量方规则标准值（kg、cm单位）
	 */
	@Value("${duccPropertyConfig.weightVolumeRuleStandard:''}")
	private String weightVolumeRuleStandard;

	/**
	 *称重量方的规则一直在变化，为了有一个版本的切换过程，这里加一个开关
	 */
	@Value("${duccPropertyConfig.weightVolumeSwitchVersion:0}")
	private Integer weightVolumeSwitchVersion;

	/**
	 *配置哪些任务失败后不再重复抓取的
	 */
	@Value("${duccPropertyConfig.workerFetchWithoutFailedTable:task_inspection}")
	private String workerFetchWithoutFailedTable;

	/**
	 *写转运卸车表开关
	 */
	@Value("${duccPropertyConfig.writeUnloadFromTys:false}")
	private boolean writeUnloadFromTys;
	
	public boolean isUseDucc() {
		return useDucc;
	}

	public void setUseDucc(boolean useDucc) {
		log.info("DuccPropertyConfig.setUseDucc");
		this.useDucc = useDucc;
	}

	public String getTestUcc() {
		return testUcc;
	}

	public void setTestUcc(String testUcc) {
		log.info("DuccPropertyConfig.setTestUcc");		
		this.testUcc = testUcc;
	}
   //get、set方法	
    public String getScheduleSiteCheckSameCity() {
        return scheduleSiteCheckSameCity;
    }

    public void setScheduleSiteCheckSameCity(String scheduleSiteCheckSameCity) {
        this.scheduleSiteCheckSameCity = scheduleSiteCheckSameCity;
    }

    public int getInsertDbRowsOneTime() {
        return insertDbRowsOneTime;
    }

    public void setInsertDbRowsOneTime(int insertDbRowsOneTime) {
        this.insertDbRowsOneTime = insertDbRowsOneTime;
    }

    public boolean getBackDispatchCheck() {
        return backDispatchCheck;
    }

    public void setBackDispatchCheck(boolean backDispatchCheck) {
        this.backDispatchCheck = backDispatchCheck;
    }

    public String getWeightVolumeRuleStandard() {
        return weightVolumeRuleStandard;
    }

    public void setWeightVolumeRuleStandard(String weightVolumeRuleStandard) {
        this.weightVolumeRuleStandard = weightVolumeRuleStandard;
    }

    public boolean getENetSyncWaybillCodeAndBoxCode() {
        return eNetSyncWaybillCodeAndBoxCode;
    }

    public void setENetSyncWaybillCodeAndBoxCode(boolean eNetSyncWaybillCodeAndBoxCode) {
        this.eNetSyncWaybillCodeAndBoxCode = eNetSyncWaybillCodeAndBoxCode;
    }

    public int getLoadScanTaskWaybillSize() {
        return loadScanTaskWaybillSize;
    }

    public void setLoadScanTaskWaybillSize(int loadScanTaskWaybillSize) {
        this.loadScanTaskWaybillSize = loadScanTaskWaybillSize;
    }

    public int getLoadScanTaskPackageSize() {
        return loadScanTaskPackageSize;
    }

    public void setLoadScanTaskPackageSize(int loadScanTaskPackageSize) {
        this.loadScanTaskPackageSize = loadScanTaskPackageSize;
    }

    public int getLoadScanTaskPackageMaxSize() {
        return loadScanTaskPackageMaxSize;
    }

    public void setLoadScanTaskPackageMaxSize(int loadScanTaskPackageMaxSize) {
        this.loadScanTaskPackageMaxSize = loadScanTaskPackageMaxSize;
    }

    public boolean isLogToBusinessLogByKafka() {
        return logToBusinessLogByKafka;
    }

    public void setLogToBusinessLogByKafka(boolean logToBusinessLogByKafka) {
        this.logToBusinessLogByKafka = logToBusinessLogByKafka;
    }

    public Integer getOfflineCurrentLimitingCount() {
        return offlineCurrentLimitingCount;
    }

    public void setOfflineCurrentLimitingCount(Integer offlineCurrentLimitingCount) {
        this.offlineCurrentLimitingCount = offlineCurrentLimitingCount;
    }

    public String getAsynbufferEnabledTaskType() {
        return asynbufferEnabledTaskType;
    }

    public void setAsynbufferEnabledTaskType(String asynbufferEnabledTaskType) {
        this.asynbufferEnabledTaskType = asynbufferEnabledTaskType;
    }

    public boolean getCassandraGlobalSwitch() {
        return cassandraGlobalSwitch;
    }

    public void setCassandraGlobalSwitch(boolean cassandraGlobalSwitch) {
        this.cassandraGlobalSwitch = cassandraGlobalSwitch;
    }

    public String getAsynBufferDynamicProducerProducerType() {
        return asynBufferDynamicProducerProducerType;
    }

    public void setAsynBufferDynamicProducerProducerType(String asynBufferDynamicProducerProducerType) {
        this.asynBufferDynamicProducerProducerType = asynBufferDynamicProducerProducerType;
    }

    public String getAsynBufferNotenabledTaskKeyword1() {
        return asynBufferNotenabledTaskKeyword1;
    }

    public void setAsynBufferNotenabledTaskKeyword1(String asynBufferNotenabledTaskKeyword1) {
        this.asynBufferNotenabledTaskKeyword1 = asynBufferNotenabledTaskKeyword1;
    }

    public boolean getAsynBufferJmqComsumerTaskProcessorPostTaskStoreEnbaled() {
        return asynBufferJmqComsumerTaskProcessorPostTaskStoreEnbaled;
    }

    public void setAsynBufferJmqComsumerTaskProcessorPostTaskStoreEnbaled(boolean asynBufferJmqComsumerTaskProcessorPostTaskStoreEnbaled) {
        this.asynBufferJmqComsumerTaskProcessorPostTaskStoreEnbaled = asynBufferJmqComsumerTaskProcessorPostTaskStoreEnbaled;
    }

    public boolean getMigrationDbBackupReplicateIgnoreExp() {
        return migrationDbBackupReplicateIgnoreExp;
    }

    public void setMigrationDbBackupReplicateIgnoreExp(boolean migrationDbBackupReplicateIgnoreExp) {
        this.migrationDbBackupReplicateIgnoreExp = migrationDbBackupReplicateIgnoreExp;
    }

    public boolean getMigrationDbBackupReplicateEnable() {
        return migrationDbBackupReplicateEnable;
    }

    public void setMigrationDbBackupReplicateEnable(boolean migrationDbBackupReplicateEnable) {
        this.migrationDbBackupReplicateEnable = migrationDbBackupReplicateEnable;
    }

    public String getWorkerFetchWithoutFailedTable() {
        return workerFetchWithoutFailedTable;
    }

    public void setWorkerFetchWithoutFailedTable(String workerFetchWithoutFailedTable) {
        this.workerFetchWithoutFailedTable = workerFetchWithoutFailedTable;
    }

    public int getWaybillSplitPageSize() {
        return waybillSplitPageSize;
    }

    public void setWaybillSplitPageSize(int waybillSplitPageSize) {
        this.waybillSplitPageSize = waybillSplitPageSize;
    }

    public String getSortingServiceMode() {
        return sortingServiceMode;
    }

    public void setSortingServiceMode(String sortingServiceMode) {
        this.sortingServiceMode = sortingServiceMode;
    }

    public String getSortingQueryMode() {
        return sortingQueryMode;
    }

    public void setSortingQueryMode(String sortingQueryMode) {
        this.sortingQueryMode = sortingQueryMode;
    }

    public boolean isChuguanNewPageQuerySwitch() {
        return chuguanNewPageQuerySwitch;
    }

    public void setChuguanNewPageQuerySwitch(boolean chuguanNewPageQuerySwitch) {
        this.chuguanNewPageQuerySwitch = chuguanNewPageQuerySwitch;
    }

    public boolean isPreOutZoneSwitch() {
        return preOutZoneSwitch;
    }

    public void setPreOutZoneSwitch(boolean preOutZoneSwitch) {
        this.preOutZoneSwitch = preOutZoneSwitch;
    }

    public String getNewLogPageTips() {
        return newLogPageTips;
    }

    public void setNewLogPageTips(String newLogPageTips) {
        this.newLogPageTips = newLogPageTips;
    }

    public String getOldLogPageTips() {
        return oldLogPageTips;
    }

    public void setOldLogPageTips(String oldLogPageTips) {
        this.oldLogPageTips = oldLogPageTips;
    }

    public boolean getEconomicNetPushZTDRetry() {
        return economicNetPushZTDRetry;
    }

    public void setEconomicNetPushZTDRetry(boolean economicNetPushZTDRetry) {
        this.economicNetPushZTDRetry = economicNetPushZTDRetry;
    }

    public int getOfflineTaskOperateTimeCorrectHours() {
        return offlineTaskOperateTimeCorrectHours;
    }

    public void setOfflineTaskOperateTimeCorrectHours(int offlineTaskOperateTimeCorrectHours) {
        this.offlineTaskOperateTimeCorrectHours = offlineTaskOperateTimeCorrectHours;
    }

    public int getOfflineTaskOperateTimeBeforeNowLimitHours() {
        return offlineTaskOperateTimeBeforeNowLimitHours;
    }

    public void setOfflineTaskOperateTimeBeforeNowLimitHours(int offlineTaskOperateTimeBeforeNowLimitHours) {
        this.offlineTaskOperateTimeBeforeNowLimitHours = offlineTaskOperateTimeBeforeNowLimitHours;
    }

    public boolean getAutomaticWeightVolumeExchangeSwitch() {
        return automaticWeightVolumeExchangeSwitch;
    }

    public void setAutomaticWeightVolumeExchangeSwitch(boolean automaticWeightVolumeExchangeSwitch) {
        this.automaticWeightVolumeExchangeSwitch = automaticWeightVolumeExchangeSwitch;
    }

    public String getAutomaticWeightVolumeExchangeSiteCode() {
        return automaticWeightVolumeExchangeSiteCode;
    }

    public void setAutomaticWeightVolumeExchangeSiteCode(String automaticWeightVolumeExchangeSiteCode) {
        this.automaticWeightVolumeExchangeSiteCode = automaticWeightVolumeExchangeSiteCode;
    }

    public String getSealVolumeCheckSites() {
        return sealVolumeCheckSites;
    }

    public void setSealVolumeCheckSites(String sealVolumeCheckSites) {
        this.sealVolumeCheckSites = sealVolumeCheckSites;
    }

    public String getSwitchVerToWebSites() {
        return switchVerToWebSites;
    }

    public void setSwitchVerToWebSites(String switchVerToWebSites) {
        this.switchVerToWebSites = switchVerToWebSites;
    }

    public boolean isDisablePdaOldLogin() {
        return disablePdaOldLogin;
    }

    public void setDisablePdaOldLogin(boolean disablePdaOldLogin) {
        this.disablePdaOldLogin = disablePdaOldLogin;
    }

    public String getCollectionAddressSiteCodes() {
        return collectionAddressSiteCodes;
    }

    public void setCollectionAddressSiteCodes(String collectionAddressSiteCodes) {
        this.collectionAddressSiteCodes = collectionAddressSiteCodes;
    }

    public String getBoxLimitSites() {
        return boxLimitSites;
    }

    public void setBoxLimitSites(String boxLimitSites) {
        this.boxLimitSites = boxLimitSites;
    }

    public int getClientPrintQueryGapTime() {
        return clientPrintQueryGapTime;
    }

    public void setClientPrintQueryGapTime(int clientPrintQueryGapTime) {
        this.clientPrintQueryGapTime = clientPrintQueryGapTime;
    }

    public String getCollectGoodsDeleteSites() {
        return collectGoodsDeleteSites;
    }

    public void setCollectGoodsDeleteSites(String collectGoodsDeleteSites) {
        this.collectGoodsDeleteSites = collectGoodsDeleteSites;
    }

    public String getInspectionBigWaybillEffectiveSites() {
        return inspectionBigWaybillEffectiveSites;
    }

    public void setInspectionBigWaybillEffectiveSites(String inspectionBigWaybillEffectiveSites) {
        this.inspectionBigWaybillEffectiveSites = inspectionBigWaybillEffectiveSites;
    }

    public boolean isOfflineLogGlobalSwitch() {
        return offlineLogGlobalSwitch;
    }

    public void setOfflineLogGlobalSwitch(boolean offlineLogGlobalSwitch) {
        this.offlineLogGlobalSwitch = offlineLogGlobalSwitch;
    }

    public boolean isSystemLogGlobalSwitch() {
        return systemLogGlobalSwitch;
    }

    public void setSystemLogGlobalSwitch(boolean systemLogGlobalSwitch) {
        this.systemLogGlobalSwitch = systemLogGlobalSwitch;
    }

    public boolean getWeightVolumeFilterWholeCountryFlag() {
        return weightVolumeFilterWholeCountryFlag;
    }

    public void setWeightVolumeFilterWholeCountryFlag(boolean weightVolumeFilterWholeCountryFlag) {
        this.weightVolumeFilterWholeCountryFlag = weightVolumeFilterWholeCountryFlag;
    }

    public boolean getOfflineQuickSeal() {
        return offlineQuickSeal;
    }

    public void setOfflineQuickSeal(boolean offlineQuickSeal) {
        this.offlineQuickSeal = offlineQuickSeal;
    }

    public String getSingleSendSwitchVerToWebSites() {
        return singleSendSwitchVerToWebSites;
    }

    public void setSingleSendSwitchVerToWebSites(String singleSendSwitchVerToWebSites) {
        this.singleSendSwitchVerToWebSites = singleSendSwitchVerToWebSites;
    }

    public int getClickIntervalSecond() {
        return clickIntervalSecond;
    }

    public void setClickIntervalSecond(int clickIntervalSecond) {
        this.clickIntervalSecond = clickIntervalSecond;
    }

    public String getBoardCombinationSwitchVerToWebSites() {
        return boardCombinationSwitchVerToWebSites;
    }

    public void setBoardCombinationSwitchVerToWebSites(String boardCombinationSwitchVerToWebSites) {
        this.boardCombinationSwitchVerToWebSites = boardCombinationSwitchVerToWebSites;
    }

    public boolean getEconomicNetValidateWeightSwitch() {
        return economicNetValidateWeightSwitch;
    }

    public void setEconomicNetValidateWeightSwitch(boolean economicNetValidateWeightSwitch) {
        this.economicNetValidateWeightSwitch = economicNetValidateWeightSwitch;
    }

    public Integer getBigWaybillWaringSize() {
        return bigWaybillWaringSize;
    }

    public void setBigWaybillWaringSize(Integer bigWaybillWaringSize) {
        this.bigWaybillWaringSize = bigWaybillWaringSize;
    }

    public boolean isControlCheckPackage() {
        return controlCheckPackage;
    }

    public void setControlCheckPackage(boolean controlCheckPackage) {
        this.controlCheckPackage = controlCheckPackage;
    }

    public boolean isControlCheckRoute() {
        return controlCheckRoute;
    }

    public void setControlCheckRoute(boolean controlCheckRoute) {
        this.controlCheckRoute = controlCheckRoute;
    }

    public String getDellCancelDeliveryCheckSealCar() {
        return dellCancelDeliveryCheckSealCar;
    }

    public void setDellCancelDeliveryCheckSealCar(String dellCancelDeliveryCheckSealCar) {
        this.dellCancelDeliveryCheckSealCar = dellCancelDeliveryCheckSealCar;
    }

	public String getRedisSwitchOn() {
		return redisSwitchOn;
	}

	public void setRedisSwitchOn(String redisSwitchOn) {
		this.redisSwitchOn = redisSwitchOn;
	}

    public String getRemoveEmptyBatchCode() {
        return removeEmptyBatchCode;
    }

    public void setRemoveEmptyBatchCode(String removeEmptyBatchCode) {
        this.removeEmptyBatchCode = removeEmptyBatchCode;
    }

    public String getPreSealVehicleRemoveEmptyBatchCode() {
        return preSealVehicleRemoveEmptyBatchCode;
    }

    public void setPreSealVehicleRemoveEmptyBatchCode(String preSealVehicleRemoveEmptyBatchCode) {
        this.preSealVehicleRemoveEmptyBatchCode = preSealVehicleRemoveEmptyBatchCode;
    }

    public String getInspectionAggEffectiveSites() {
        return inspectionAggEffectiveSites;
    }

    public void setInspectionAggEffectiveSites(String inspectionAggEffectiveSites) {
        this.inspectionAggEffectiveSites = inspectionAggEffectiveSites;
    }

    public String getQuickSealTips() {
        return quickSealTips;
    }

    public void setQuickSealTips(String quickSealTips) {
        this.quickSealTips = quickSealTips;
    }

    public Integer getVirtualSiteCode() {
        return virtualSiteCode;
    }

    public void setVirtualSiteCode(Integer virtualSiteCode) {
        this.virtualSiteCode = virtualSiteCode;
    }

    public boolean isSendCodeGenSwitchOn() {
        return sendCodeGenSwitchOn;
    }

    public void setSendCodeGenSwitchOn(boolean sendCodeGenSwitchOn) {
        this.sendCodeGenSwitchOn = sendCodeGenSwitchOn;
    }

    public String getAllPureValidateWeightWebSite() {
        return allPureValidateWeightWebSite;
    }

    public void setAllPureValidateWeightWebSite(String allPureValidateWeightWebSite) {
        this.allPureValidateWeightWebSite = allPureValidateWeightWebSite;
    }

    public boolean getPreSortOnSiteSwitchOn() {
        return preSortOnSiteSwitchOn;
    }

    public void setPreSortOnSiteSwitchOn(boolean preSortOnSiteSwitchOn) {
        this.preSortOnSiteSwitchOn = preSortOnSiteSwitchOn;
    }

    public String getDeliverHideSites() {
        return deliverHideSites;
    }

    public void setDeliverHideSites(String deliverHideSites) {
        this.deliverHideSites = deliverHideSites;
    }

    public int getBCContainWJNumberLimit() {
        return BCContainWJNumberLimit;
    }

    public void setBCContainWJNumberLimit(int BCContainWJNumberLimit) {
        this.BCContainWJNumberLimit = BCContainWJNumberLimit;
    }

    public int getWJPackageNumberLimit() {
        return WJPackageNumberLimit;
    }

    public void setWJPackageNumberLimit(int WJPackageNumberLimit) {
        this.WJPackageNumberLimit = WJPackageNumberLimit;
    }

    public boolean getCheckBoxSendedSwitchOn() {
        return checkBoxSendedSwitchOn;
    }

    public void setCheckBoxSendedSwitchOn(boolean checkBoxSendedSwitchOn) {
        this.checkBoxSendedSwitchOn = checkBoxSendedSwitchOn;
    }

    public String getAllBCBoxFilterWebSite() {
        return allBCBoxFilterWebSite;
    }

    public void setAllBCBoxFilterWebSite(String allBCBoxFilterWebSite) {
        this.allBCBoxFilterWebSite = allBCBoxFilterWebSite;
    }

    public Integer getSiteQueryLimit() {
        return siteQueryLimit;
    }

    public void setSiteQueryLimit(Integer siteQueryLimit) {
        this.siteQueryLimit = siteQueryLimit;
    }

    public Integer getExportSpotCheckMaxSize() {
        return exportSpotCheckMaxSize;
    }

    public void setExportSpotCheckMaxSize(Integer exportSpotCheckMaxSize) {
        this.exportSpotCheckMaxSize = exportSpotCheckMaxSize;
    }

    public Integer getPdaNoticePullIntervalTime() {
        return pdaNoticePullIntervalTime;
    }

    public void setPdaNoticePullIntervalTime(Integer pdaNoticePullIntervalTime) {
        this.pdaNoticePullIntervalTime = pdaNoticePullIntervalTime;
    }

    public String getOfflineTaskReportInterceptSites() {
        return offlineTaskReportInterceptSites;
    }

    public void setOfflineTaskReportInterceptSites(String offlineTaskReportInterceptSites) {
        this.offlineTaskReportInterceptSites = offlineTaskReportInterceptSites;
    }

    public Boolean getOfflineTaskReportInterceptNeedHandle(Integer siteId) {
        if(StringUtils.isBlank(offlineTaskReportInterceptSites)){
            return false;
        }
        if(Objects.equals("0", offlineTaskReportInterceptSites)){
            return true;
        }
        if(Objects.equals("-1", offlineTaskReportInterceptSites)){
            return false;
        }
        List<String> siteCodes = Arrays.asList(offlineTaskReportInterceptSites.split(Constants.SEPARATOR_COMMA));
        if(siteCodes.contains(siteId + "")){
            return true;
        }
        return false;
    }

    public String getScannerOperateCalculateIfInterceptSites() {
        return scannerOperateCalculateIfInterceptSites;
    }

    public void setScannerOperateCalculateIfInterceptSites(String scannerOperateCalculateIfInterceptSites) {
        this.scannerOperateCalculateIfInterceptSites = scannerOperateCalculateIfInterceptSites;
    }

    public Boolean getScannerOperateCalculateIfInterceptNeedHandle(Integer siteId) {
        if(StringUtils.isBlank(scannerOperateCalculateIfInterceptSites)){
            return false;
        }
        if(Objects.equals("0", scannerOperateCalculateIfInterceptSites)){
            return true;
        }
        if(Objects.equals("-1", scannerOperateCalculateIfInterceptSites)){
            return false;
        }
        List<String> siteCodes = Arrays.asList(scannerOperateCalculateIfInterceptSites.split(Constants.SEPARATOR_COMMA));
        if(siteCodes.contains(siteId + "")){
            return true;
        }
        return false;
    }

    public Integer getExportConcurrencyLimitNum() {
        return exportConcurrencyLimitNum;
    }

    public void setExportConcurrencyLimitNum(Integer exportConcurrencyLimitNum) {
        this.exportConcurrencyLimitNum = exportConcurrencyLimitNum;
    }

    public Integer getOneQuerySize() {
        return oneQuerySize;
    }

    public void setOneQuerySize(Integer oneQuerySize) {
        this.oneQuerySize = oneQuerySize;
    }

    public String getPrintHandoverListSites() {
        return printHandoverListSites;
    }

    public void setPrintHandoverListSites(String printHandoverListSites) {
        this.printHandoverListSites = printHandoverListSites;
    }

    public boolean getCheckSiteSubType() {
        return checkSiteSubType;
    }

    public void setCheckSiteSubType(boolean checkSiteSubType) {
        this.checkSiteSubType = checkSiteSubType;
    }

    public int getScrollQuerySize() {
        return scrollQuerySize;
    }

    public void setScrollQuerySize(int scrollQuerySize) {
        this.scrollQuerySize = scrollQuerySize;
    }

    public boolean getInspectionAssertDemotion() {
        return inspectionAssertDemotion;
    }

    public void setInspectionAssertDemotion(boolean inspectionAssertDemotion) {
        this.inspectionAssertDemotion = inspectionAssertDemotion;
    }

    public int getPrintScrollQueryCountLimit() {
        return printScrollQueryCountLimit;
    }

    public void setPrintScrollQueryCountLimit(int printScrollQueryCountLimit) {
        this.printScrollQueryCountLimit = printScrollQueryCountLimit;
    }

    public Integer getDazongPackageOperateMax() {
        return dazongPackageOperateMax;
    }

    public void setDazongPackageOperateMax(Integer dazongPackageOperateMax) {
        this.dazongPackageOperateMax = dazongPackageOperateMax;
    }

    public boolean getInspectNoSendNoLoadWaybillDemotion() {
        return inspectNoSendNoLoadWaybillDemotion;
    }

    public void setInspectNoSendNoLoadWaybillDemotion(boolean inspectNoSendNoLoadWaybillDemotion) {
        this.inspectNoSendNoLoadWaybillDemotion = inspectNoSendNoLoadWaybillDemotion;
    }

    public String getCloseAsynBufferSaveTaskToDb() {
        return closeAsynBufferSaveTaskToDb;
    }

    public void setCloseAsynBufferSaveTaskToDb(String closeAsynBufferSaveTaskToDb) {
        this.closeAsynBufferSaveTaskToDb = closeAsynBufferSaveTaskToDb;
    }

    public boolean getSpotCheckIssueControl() {
        return spotCheckIssueControl;
    }

    public void setSpotCheckIssueControl(boolean spotCheckIssueControl) {
        this.spotCheckIssueControl = spotCheckIssueControl;
    }

    public int getHideSpecialStartSitePrintSwitch() {
        return hideSpecialStartSitePrintSwitch;
    }

    public boolean getHidePrintSpecialStartSiteNameSwitchOn() {
        return Objects.equals(Constants.YN_YES, hideSpecialStartSitePrintSwitch);
    }

    public void setHideSpecialStartSitePrintSwitch(int hideSpecialStartSitePrintSwitch) {
        this.hideSpecialStartSitePrintSwitch = hideSpecialStartSitePrintSwitch;
    }

    public String getHideSpecialStartSitPrintDestinationSiteList() {
        return hideSpecialStartSitPrintDestinationSiteList;
    }

    public List<String> getHideSpecialStartSitPrintDestinationSiteStrList() {
        if(hideSpecialStartSitPrintDestinationSiteList == null){
            return new ArrayList<>();
        }
        return Arrays.asList(hideSpecialStartSitPrintDestinationSiteList.split(Constants.SEPARATOR_COMMA));
    }

    /**
     * 请勿配置此变量为ucc配置
     */
    public void setHideSpecialStartSitPrintDestinationSiteList(String hideSpecialStartSitPrintDestinationSiteList) {
        this.hideSpecialStartSitPrintDestinationSiteList = hideSpecialStartSitPrintDestinationSiteList;
        this.hideSpecialStartSitPrintDestinationSiteStrList = this.getHideSpecialStartSitPrintDestinationSiteStrList();
    }

    public boolean matchHidePrintSpecialStartSitDestinationSiteList(int siteId) {
        if(StringUtils.isBlank(hideSpecialStartSitPrintDestinationSiteList)){
            return false;
        }
        if(Objects.equals(Constants.STR_ALL, hideSpecialStartSitPrintDestinationSiteList)){
            return true;
        }
        if(hideSpecialStartSitPrintDestinationSiteStrList.contains(String.valueOf(siteId))){
            return true;
        }
        return false;
    }

    public String getHideSpecialStartSitePrintReplaceSymbol() {
        if(hideSpecialStartSitePrintReplaceSymbol == null){
            return "";
        }
        if(hideSpecialStartSitePrintReplaceSymbol.length() > hideSpecialStartSitePrintReplaceSymbolMaxLength){
            return hideSpecialStartSitePrintReplaceSymbol.substring(0, hideSpecialStartSitePrintReplaceSymbolMaxLength);
        }
        return hideSpecialStartSitePrintReplaceSymbol;
    }

    public void setHideSpecialStartSitePrintReplaceSymbol(String hideSpecialStartSitePrintReplaceSymbol) {
        this.hideSpecialStartSitePrintReplaceSymbol = hideSpecialStartSitePrintReplaceSymbol;
    }

    public String getSiteEnableSendCodeEffectiveValidation() {
        return siteEnableSendCodeEffectiveValidation;
    }

    public void setSiteEnableSendCodeEffectiveValidation(String siteEnableSendCodeEffectiveValidation) {
        this.siteEnableSendCodeEffectiveValidation = siteEnableSendCodeEffectiveValidation;
    }

    public boolean isCancelJimaoxinSwitchToOMS() {
        return cancelJimaoxinSwitchToOMS;
    }

    public void setCancelJimaoxinSwitchToOMS(boolean cancelJimaoxinSwitchToOMS) {
        this.cancelJimaoxinSwitchToOMS = cancelJimaoxinSwitchToOMS;
    }

    public boolean getApprovalSwitch() {
        return approvalSwitch;
    }

    public void setApprovalSwitch(boolean approvalSwitch) {
        this.approvalSwitch = approvalSwitch;
    }

    public String getPackRePrintInterceptStatus() {
        return packRePrintInterceptStatus;
    }

    public void setPackRePrintInterceptStatus(String packRePrintInterceptStatus) {
        this.packRePrintInterceptStatus = packRePrintInterceptStatus;
    }

    public String getOfflinePdaMenuCode() {
        return offlinePdaMenuCode;
    }

    public void setOfflinePdaMenuCode(String offlinePdaMenuCode) {
        this.offlinePdaMenuCode = offlinePdaMenuCode;
    }

    public int getWaybillMaxPackNum() {
        return waybillMaxPackNum;
    }

    public void setWaybillMaxPackNum(int waybillMaxPackNum) {
        this.waybillMaxPackNum = waybillMaxPackNum;
    }

    public double getSpotCheckNoExcessLimit() {
        return spotCheckNoExcessLimit;
    }

    public void setSpotCheckNoExcessLimit(double spotCheckNoExcessLimit) {
        this.spotCheckNoExcessLimit = spotCheckNoExcessLimit;
    }

    public boolean isParalleGetPackageSwitch() {
        return paralleGetPackageSwitch;
    }

    public void setParalleGetPackageSwitch(boolean paralleGetPackageSwitch) {
        this.paralleGetPackageSwitch = paralleGetPackageSwitch;
    }

    /**
     * 西藏模式业务场景开关，按分拣中心归属的省份配置，不配置业务场景不生效，配置ALL全国生效
     */
    public String getItmsBizEnableSwitch() {
        return itmsBizEnableSwitch;
    }

    public void setItmsBizEnableSwitch(String itmsBizEnableSwitch) {
        this.itmsBizEnableSwitch = itmsBizEnableSwitch;
    }

    public String getUseNewInventorySiteCodes() {
        return useNewInventorySiteCodes;
    }

    public void setUseNewInventorySiteCodes(String useNewInventorySiteCodes) {
        this.useNewInventorySiteCodes = useNewInventorySiteCodes;
    }

    public int getMultiplePackageSpotCheckSwitch() {
        return multiplePackageSpotCheckSwitch;
    }

    public void setMultiplePackageSpotCheckSwitch(int multiplePackageSpotCheckSwitch) {
        this.multiplePackageSpotCheckSwitch = multiplePackageSpotCheckSwitch;
    }

    public boolean getMultiplePackageSpotCheckSwitchOn() {
        return Objects.equals(Constants.YN_YES, multiplePackageSpotCheckSwitch);
    }

    public String getMultiplePackageSpotCheckSites() {
        return multiplePackageSpotCheckSites;
    }

    public void setMultiplePackageSpotCheckSites(String multiplePackageSpotCheckSites) {
        this.multiplePackageSpotCheckSites = multiplePackageSpotCheckSites;
    }
    public List<String> getMultiplePackageSpotCheckSitesList() {
        if(multiplePackageSpotCheckSites == null){
            return new ArrayList<>();
        }
        return Arrays.asList(multiplePackageSpotCheckSites.split(Constants.SEPARATOR_COMMA));
    }

    /**
     * 请勿配置此变量为ucc配置
     */
    public void setMultiplePackageSpotCheckSitesList(String multiplePackageSpotCheckSites) {
        this.multiplePackageSpotCheckSites = multiplePackageSpotCheckSites;
        this._multiplePackageSpotCheckSitesList = this.getMultiplePackageSpotCheckSitesList();
    }

    public boolean matchMultiplePackageSpotCheckSite(int siteId) {
        if(StringUtils.isBlank(multiplePackageSpotCheckSites)){
            return false;
        }
        if(Objects.equals(Constants.STR_ALL, multiplePackageSpotCheckSites)){
            return true;
        }
        if(_multiplePackageSpotCheckSitesList.contains(String.valueOf(siteId))){
            return true;
        }
        return false;
    }

    public boolean isReadUnloadFromTys() {
        return readUnloadFromTys;
    }

    public void setReadUnloadFromTys(boolean readUnloadFromTys) {
        this.readUnloadFromTys = readUnloadFromTys;
    }

    public boolean isStopWriteUnloadFromDms() {
        return stopWriteUnloadFromDms;
    }

    public void setStopWriteUnloadFromDms(boolean stopWriteUnloadFromDms) {
        this.stopWriteUnloadFromDms = stopWriteUnloadFromDms;
    }

    public boolean isWriteUnloadFromTys() {
        return writeUnloadFromTys;
    }

    public void setWriteUnloadFromTys(boolean writeUnloadFromTys) {
        this.writeUnloadFromTys = writeUnloadFromTys;
    }


    public int getVirtualBoardMaxDestinationCount() {
        return virtualBoardMaxDestinationCount;
    }

    public void setVirtualBoardMaxDestinationCount(int virtualBoardMaxDestinationCount) {
        this.virtualBoardMaxDestinationCount = virtualBoardMaxDestinationCount;
    }

    public int getVirtualBoardMaxItemCount() {
        return virtualBoardMaxItemCount;
    }

    public void setVirtualBoardMaxItemCount(int virtualBoardMaxItemCount) {
        this.virtualBoardMaxItemCount = virtualBoardMaxItemCount;
    }

    public Integer getVirtualBoardAutoCloseDays() {
        return virtualBoardAutoCloseDays;
    }

    public DuccPropertyConfig setVirtualBoardAutoCloseDays(Integer virtualBoardAutoCloseDays) {
        this.virtualBoardAutoCloseDays = virtualBoardAutoCloseDays;
        return this;
    }

    public String getVirtualBoardCanUseSite() {
        return virtualBoardCanUseSite;
    }

    public DuccPropertyConfig setVirtualBoardCanUseSite(String virtualBoardCanUseSite) {
        this.virtualBoardCanUseSite = virtualBoardCanUseSite;
        this.virtualBoardCanUseSiteList = this.getVirtualBoardCanUseSiteList();
        return this;
    }

    public List<String> getVirtualBoardCanUseSiteList() {
        if(virtualBoardCanUseSiteList == null){
            return new ArrayList<>();
        }
        return Arrays.asList(virtualBoardCanUseSite.split(Constants.SEPARATOR_COMMA));
    }

    public boolean matchVirtualSiteCanUseSite(int siteId) {
        if(StringUtils.isBlank(virtualBoardCanUseSite)){
            return false;
        }
        if(Objects.equals(Constants.STR_ALL, virtualBoardCanUseSite)){
            return true;
        }
        if(virtualBoardCanUseSiteList.contains(String.valueOf(siteId))){
            return true;
        }
        return false;
    }

    public boolean getAndroidIsExecuteNewSpotCheck() {
        return androidIsExecuteNewSpotCheck;
    }

    public void setAndroidIsExecuteNewSpotCheck(boolean androidIsExecuteNewSpotCheck) {
        this.androidIsExecuteNewSpotCheck = androidIsExecuteNewSpotCheck;
    }

    public String getNoAuthMenuConfig() {
        return noAuthMenuConfig;
    }

    public void setNoAuthMenuConfig(String noAuthMenuConfig) {
        this.noAuthMenuConfig = noAuthMenuConfig;
    }

    public String getNoAuthMenuConfigNew() {
        return noAuthMenuConfigNew;
    }

    public void setNoAuthMenuConfigNew(String noAuthMenuConfigNew) {
        this.noAuthMenuConfigNew = noAuthMenuConfigNew;
    }

    public String getMenuCodeFuncConfig() {
        return menuCodeFuncConfig;
    }

    public void setMenuCodeFuncConfig(String menuCodeFuncConfig) {
        this.menuCodeFuncConfig = menuCodeFuncConfig;
    }

    public String getMenuCodeFuncConfigNew() {
        return menuCodeFuncConfigNew;
    }

    public void setMenuCodeFuncConfigNew(String menuCodeFuncConfigNew) {
        this.menuCodeFuncConfigNew = menuCodeFuncConfigNew;
    }

    public boolean getSitePlateIsCheckFunc() {
        return sitePlateIsCheckFunc;
    }

    public void setSitePlateIsCheckFunc(boolean sitePlateIsCheckFunc) {
        this.sitePlateIsCheckFunc = sitePlateIsCheckFunc;
    }

    public String getJudgePackagePrintedIncludeSiteTerminal() {
        return judgePackagePrintedIncludeSiteTerminal;
    }

    public void setJudgePackagePrintedIncludeSiteTerminal(String judgePackagePrintedIncludeSiteTerminal) {
        this.judgePackagePrintedIncludeSiteTerminal = judgePackagePrintedIncludeSiteTerminal;
    }

    public int getPrintCompleteCallbackAsyncPackageNum() {
        return printCompleteCallbackAsyncPackageNum;
    }

    public void setPrintCompleteCallbackAsyncPackageNum(int printCompleteCallbackAsyncPackageNum) {
        this.printCompleteCallbackAsyncPackageNum = printCompleteCallbackAsyncPackageNum;
    }

    public String getLimitSiteUsePackReprint() {
        return limitSiteUsePackReprint;
    }

    public void setLimitSiteUsePackReprint(String limitSiteUsePackReprint) {
        this.limitSiteUsePackReprint = limitSiteUsePackReprint;
    }

    public String getSendCodeEffectiveValidation() {
        return sendCodeEffectiveValidation;
    }

    public void setSendCodeEffectiveValidation(String sendCodeEffectiveValidation) {
        this.sendCodeEffectiveValidation = sendCodeEffectiveValidation;
    }

    public int getNotSignedOutRecordMoreThanHours() {
        return notSignedOutRecordMoreThanHours;
    }

    public void setNotSignedOutRecordMoreThanHours(int notSignedOutRecordMoreThanHours) {
        this.notSignedOutRecordMoreThanHours = notSignedOutRecordMoreThanHours;
    }
	public int getNotSignedOutRecordRangeHours() {
		return notSignedOutRecordRangeHours;
	}

	public void setNotSignedOutRecordRangeHours(int notSignedOutRecordRangeHours) {
		this.notSignedOutRecordRangeHours = notSignedOutRecordRangeHours;
	}
    public boolean getAiDistinguishSwitch() {
        return aiDistinguishSwitch;
    }

    public void setAiDistinguishSwitch(boolean aiDistinguishSwitch) {
        this.aiDistinguishSwitch = aiDistinguishSwitch;
    }

    public String getDeviceAIDistinguishSwitch() {
        return deviceAIDistinguishSwitch;
    }

    public void setDeviceAIDistinguishSwitch(String deviceAIDistinguishSwitch) {
        this.deviceAIDistinguishSwitch = deviceAIDistinguishSwitch;
    }

    public int getDeviceAIDistinguishPackNum() {
        return deviceAIDistinguishPackNum;
    }

    public void setDeviceAIDistinguishPackNum(int deviceAIDistinguishPackNum) {
        this.deviceAIDistinguishPackNum = deviceAIDistinguishPackNum;
    }

    public String getSpotCheckReformSiteCodes() {
        return spotCheckReformSiteCodes;
    }

    public void setSpotCheckReformSiteCodes(String spotCheckReformSiteCodes) {
        this.spotCheckReformSiteCodes = spotCheckReformSiteCodes;
    }

    public Integer getPackConsumableSwitch() {
        return packConsumableSwitch;
    }

    public void setPackConsumableSwitch(Integer packConsumableSwitch) {
        this.packConsumableSwitch = packConsumableSwitch;
    }

    public Integer getJyTaskPageMax() {
        return jyTaskPageMax;
    }

    public void setJyTaskPageMax(Integer jyTaskPageMax) {
        this.jyTaskPageMax = jyTaskPageMax;
    }

    public Integer getJyUnloadSingleWaybillThreshold() {
        return jyUnloadSingleWaybillThreshold;
    }

    public void setJyUnloadSingleWaybillThreshold(Integer jyUnloadSingleWaybillThreshold) {
        this.jyUnloadSingleWaybillThreshold = jyUnloadSingleWaybillThreshold;
    }

    public Integer getJyUnSealTaskOrderByIntegral() {
        return jyUnSealTaskOrderByIntegral;
    }

    public void setJyUnSealTaskOrderByIntegral(Integer jyUnSealTaskOrderByIntegral) {
        this.jyUnSealTaskOrderByIntegral = jyUnSealTaskOrderByIntegral;
    }

    public Integer getJyUnSealTaskSwitchToEs() {
        return jyUnSealTaskSwitchToEs;
    }

    public void setJyUnSealTaskSwitchToEs(Integer jyUnSealTaskSwitchToEs) {
        this.jyUnSealTaskSwitchToEs = jyUnSealTaskSwitchToEs;
    }

    public Integer getJySendTaskLoadRateUpperLimit() {
        return jySendTaskLoadRateUpperLimit;
    }

    public Integer getJySendTaskLoadRateLowerLimit() {
        return jySendTaskLoadRateLowerLimit;
    }

    public String getJySendTaskLoadRateLimit() {
        return jySendTaskLoadRateLimit;
    }

    public void setJySendTaskLoadRateLimit(String jySendTaskLoadRateLimit) {
        this.jySendTaskLoadRateLimit = jySendTaskLoadRateLimit;
        String[] loadRateLimit = this.jySendTaskLoadRateLimit.split(Constants.SEPARATOR_COMMA);
        this.jySendTaskLoadRateLowerLimit = Integer.valueOf(loadRateLimit[0]);
        this.jySendTaskLoadRateUpperLimit = Integer.valueOf(loadRateLimit[1]);
    }

    public boolean isNeedUseNewReverseApi() {
		return needUseNewReverseApi;
	}

	public void setNeedUseNewReverseApi(boolean needUseNewReverseApi) {
		this.needUseNewReverseApi = needUseNewReverseApi;
	}

    public String getDpSiteCodes() {
        return dpSiteCodes;
    }

    public void setDpSiteCodes(String dpSiteCodes) {
        if(StringUtils.isBlank(dpSiteCodes)){
            return;
        }
        String[] siteCodesStr = dpSiteCodes.split("[,，]");
        if(ArrayUtils.isEmpty(siteCodesStr)){
            return;
        }
        List<Integer> siteCodeList = new ArrayList<>();
        for(String siteCode : siteCodesStr){
            if(StringUtils.isNumeric(siteCode)){
                siteCodeList.add(Integer.parseInt(siteCode));
            }
        }
        setDpSiteCodeList(siteCodeList);
    }

    public List<Integer> getDpSiteCodeList() {
        return dpSiteCodeList;
    }

    public void setDpSiteCodeList(List<Integer> dpSiteCodeList) {
        this.dpSiteCodeList = dpSiteCodeList;
    }

    public boolean isDpWaybillMatchSendCodeSwitch() {
        return dpWaybillMatchSendCodeSwitch;
    }

    public void setDpWaybillMatchSendCodeSwitch(boolean dpWaybillMatchSendCodeSwitch) {
        this.dpWaybillMatchSendCodeSwitch = dpWaybillMatchSendCodeSwitch;
    }

    public String getFaceAbnormalReportConfig() {
        return faceAbnormalReportConfig;
    }

    public void setFaceAbnormalReportConfig(String faceAbnormalReportConfig) {
        this.faceAbnormalReportConfig = faceAbnormalReportConfig;
    }

    public List<String> getIdentityRecogniseSiteSwitch() {
        return StringUtils.isNotEmpty(identityRecogniseSiteSwitch)?
                Arrays.asList(identityRecogniseSiteSwitch.split(Constants.SEPARATOR_COMMA).clone())
                : Collections.singletonList("0");
    }

    public void setIdentityRecogniseSiteSwitch(String identityRecogniseSiteSwitch) {
        this.identityRecogniseSiteSwitch = identityRecogniseSiteSwitch;
    }

    public Integer getJySendTaskPlanTimeBeginDay() {
        return jySendTaskPlanTimeBeginDay;
    }

    public void setJySendTaskPlanTimeBeginDay(Integer jySendTaskPlanTimeBeginDay) {
        this.jySendTaskPlanTimeBeginDay = jySendTaskPlanTimeBeginDay;
    }

    public Integer getJySendTaskPlanTimeEndDay() {
        return jySendTaskPlanTimeEndDay;
    }

    public void setJySendTaskPlanTimeEndDay(Integer jySendTaskPlanTimeEndDay) {
        this.jySendTaskPlanTimeEndDay = jySendTaskPlanTimeEndDay;
    }

    public String getNeedValidateMainLineBizSourceList() {
        return needValidateMainLineBizSourceList;
    }

    public void setNeedValidateMainLineBizSourceList(String needValidateMainLineBizSourceList) {
        this.needValidateMainLineBizSourceList = needValidateMainLineBizSourceList;
        needValidateMainLineBizSourceCodes = JsonHelper.jsonToList(needValidateMainLineBizSourceList, Integer.class);
    }

    public boolean needValidateMainLine(Integer bizCode) {
    	if(!CollectionUtils.isEmpty(needValidateMainLineBizSourceCodes)) {
    		return needValidateMainLineBizSourceCodes.contains(bizCode);
    	}
    	return false;
    }
    public Boolean getSensitiveInfoHideSwitch() {
        return sensitiveInfoHideSwitch;
    }

    public void setSensitiveInfoHideSwitch(Boolean sensitiveInfoHideSwitch) {
        this.sensitiveInfoHideSwitch = sensitiveInfoHideSwitch;
    }

    public Integer getJySendTaskCreateTimeBeginDay() {
        return jySendTaskCreateTimeBeginDay;
    }

    public void setJySendTaskCreateTimeBeginDay(Integer jySendTaskCreateTimeBeginDay) {
        this.jySendTaskCreateTimeBeginDay = jySendTaskCreateTimeBeginDay;
    }

    public String getNotValidateTransTypeCodesList() {
        return notValidateTransTypeCodesList;
    }

    public void setNotValidateTransTypeCodesList(String notValidateTransTypeCodesList) {
        this.notValidateTransTypeCodesList = notValidateTransTypeCodesList;
        notValidateTransTypeCodes = JsonHelper.jsonToList(notValidateTransTypeCodesList, Integer.class);

    }

    public boolean notValidateTransType(Integer type) {
        if(!CollectionUtils.isEmpty(notValidateTransTypeCodes)) {
            return notValidateTransTypeCodes.contains(type);
        }
        return false;
    }

    public boolean isJyBasicServerSwitch() {
        return jyBasicServerSwitch;
    }

    public void setJyBasicServerSwitch(boolean jyBasicServerSwitch) {
        this.jyBasicServerSwitch = jyBasicServerSwitch;
    }

    public String getClientOfflineMenuConfig() {
        return clientOfflineMenuConfig;
    }

    public void setClientOfflineMenuConfig(String clientOfflineMenuConfig) {
        this.clientOfflineMenuConfig = clientOfflineMenuConfig;
    }

    public boolean isQuerySensitiveFlag() {
        return querySensitiveFlag;
    }

    public void setQuerySensitiveFlag(boolean querySensitiveFlag) {
        this.querySensitiveFlag = querySensitiveFlag;
    }

    public boolean getSecuritySwitch() {
        return securitySwitch;
    }

    public void setSecuritySwitch(boolean securitySwitch) {
        this.securitySwitch = securitySwitch;
    }

    public String getJyDemotionConfig() {
        return jyDemotionConfig;
    }

    public void setJyDemotionConfig(String jyDemotionConfig) {
        this.jyDemotionConfig = jyDemotionConfig;
    }

    public Integer getUnloadCacheDurationHours() {
        return unloadCacheDurationHours;
    }

    public void setUnloadCacheDurationHours(Integer unloadCacheDurationHours) {
        this.unloadCacheDurationHours = unloadCacheDurationHours;
    }

    public Integer getUnloadBoardBindingsMaxCount() {
        return unloadBoardBindingsMaxCount;
    }

    public void setUnloadBoardBindingsMaxCount(Integer unloadBoardBindingsMaxCount) {
        this.unloadBoardBindingsMaxCount = unloadBoardBindingsMaxCount;
    }

    public String getPackageWeightLimit() {
        return packageWeightLimit;
    }

    public void setPackageWeightLimit(String packageWeightLimit) {
        this.packageWeightLimit = packageWeightLimit;
    }

    public String getWaybillWeightLimit() {
        return waybillWeightLimit;
    }

    public void setWaybillWeightLimit(String waybillWeightLimit) {
        this.waybillWeightLimit = waybillWeightLimit;
    }


    public Integer getWeightVolumeSwitchVersion() {
        return weightVolumeSwitchVersion;
    }

    public void setWeightVolumeSwitchVersion(Integer weightVolumeSwitchVersion) {
        this.weightVolumeSwitchVersion = weightVolumeSwitchVersion;
    }

    public Integer getJyUnloadCarListQueryDayFilter() {
        return jyUnloadCarListQueryDayFilter;
    }

    public void setJyUnloadCarListQueryDayFilter(Integer jyUnloadCarListQueryDayFilter) {
        this.jyUnloadCarListQueryDayFilter = jyUnloadCarListQueryDayFilter;
    }

    public int getGoodsResidenceTime() {
        return goodsResidenceTime;
    }

    public void setGoodsResidenceTime(int goodsResidenceTime) {
        this.goodsResidenceTime = goodsResidenceTime;
    }


    public Integer getUploadDeviceLocationInterval() {
        return uploadDeviceLocationInterval;
    }

    public void setUploadDeviceLocationInterval(Integer uploadDeviceLocationInterval) {
        this.uploadDeviceLocationInterval = uploadDeviceLocationInterval;
    }

    public Integer getCheckDeviceLocationInRealTimeSwitch() {
        return checkDeviceLocationInRealTimeSwitch;
    }

    public void setCheckDeviceLocationInRealTimeSwitch(Integer checkDeviceLocationInRealTimeSwitch) {
        this.checkDeviceLocationInRealTimeSwitch = checkDeviceLocationInRealTimeSwitch;
    }

    public boolean getCheckDeviceLocationInRealTimeSwitchIsOn() {
        return Objects.equals(this.getCheckDeviceLocationInRealTimeSwitch(), Constants.YN_YES);
    }

    public Integer getTysUnloadTaskHandoverMaxSize() {
        return tysUnloadTaskHandoverMaxSize;
    }

    public void setTysUnloadTaskHandoverMaxSize(Integer tysUnloadTaskHandoverMaxSize) {
        this.tysUnloadTaskHandoverMaxSize = tysUnloadTaskHandoverMaxSize;
    }

    public Integer getTysUnloadTaskSupplementScanLimitHours() {
        return tysUnloadTaskSupplementScanLimitHours;
    }

    public void setTysUnloadTaskSupplementScanLimitHours(Integer tysUnloadTaskSupplementScanLimitHours) {
        this.tysUnloadTaskSupplementScanLimitHours = tysUnloadTaskSupplementScanLimitHours;
    }

	public boolean iseNetSyncWaybillCodeAndBoxCode() {
		return eNetSyncWaybillCodeAndBoxCode;
	}

	public void seteNetSyncWaybillCodeAndBoxCode(boolean eNetSyncWaybillCodeAndBoxCode) {
		this.eNetSyncWaybillCodeAndBoxCode = eNetSyncWaybillCodeAndBoxCode;
	}

	public List<Integer> getNeedValidateMainLineBizSourceCodes() {
		return needValidateMainLineBizSourceCodes;
	}

	public void setNeedValidateMainLineBizSourceCodes(List<Integer> needValidateMainLineBizSourceCodes) {
		this.needValidateMainLineBizSourceCodes = needValidateMainLineBizSourceCodes;
	}

	public List<Integer> getNotValidateTransTypeCodes() {
		return notValidateTransTypeCodes;
	}

	public void setNotValidateTransTypeCodes(List<Integer> notValidateTransTypeCodes) {
		this.notValidateTransTypeCodes = notValidateTransTypeCodes;
	}

	public List<String> get_multiplePackageSpotCheckSitesList() {
		return _multiplePackageSpotCheckSitesList;
	}

	public void set_multiplePackageSpotCheckSitesList(List<String> _multiplePackageSpotCheckSitesList) {
		this._multiplePackageSpotCheckSitesList = _multiplePackageSpotCheckSitesList;
	}

	public int getHideSpecialStartSitePrintReplaceSymbolMaxLength() {
		return hideSpecialStartSitePrintReplaceSymbolMaxLength;
	}

	public void setJySendTaskLoadRateUpperLimit(Integer jySendTaskLoadRateUpperLimit) {
		this.jySendTaskLoadRateUpperLimit = jySendTaskLoadRateUpperLimit;
	}

	public void setJySendTaskLoadRateLowerLimit(Integer jySendTaskLoadRateLowerLimit) {
		this.jySendTaskLoadRateLowerLimit = jySendTaskLoadRateLowerLimit;
	}

	public void setHideSpecialStartSitPrintDestinationSiteStrList(
			List<String> hideSpecialStartSitPrintDestinationSiteStrList) {
		this.hideSpecialStartSitPrintDestinationSiteStrList = hideSpecialStartSitPrintDestinationSiteStrList;
	}

	public void setVirtualBoardCanUseSiteList(List<String> virtualBoardCanUseSiteList) {
		this.virtualBoardCanUseSiteList = virtualBoardCanUseSiteList;
	}	
}
