package com.jd.bluedragon.configuration.ducc;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import org.springframework.beans.BeanUtils;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.google.common.collect.Lists;
import com.jd.bluedragon.Constants;
import com.jd.bluedragon.common.dto.operation.workbench.config.dto.ClientAutoRefreshConfig;
import com.jd.bluedragon.distribution.jy.service.task.autoclose.dto.AutoCloseJyBizTaskConfig;
import com.jd.laf.config.spring.annotation.LafUcc;
import com.jd.ql.dms.print.utils.JsonHelper;

@Component("duccPropertyConfig")
	@LafUcc
public class DuccPropertyConfig {
	private static final Logger log = LoggerFactory.getLogger(DuccPropertyConfig.class);
	
	public static final String CONFIG_FROM_LOCAL = "local";
	/**
	 * 使用ducc
	 */
	@Value("${duccPropertyConfig.useDucc:false}")
	@LafUcc
	private boolean useDucc;
	/**
	 * 配置来源-默认是local，正常加载ducc，应该是ducc配置的值
	 */
	@Value("${duccPropertyConfig.configFrom:local}")
	@LafUcc
	private String configFrom;
	/**
	 * 配置检查标识，比较ucc和ducc配置，不一致抛出异常
	 */
	@Value("${duccPropertyConfig.checkConfig:false}")
	@LafUcc
	private boolean checkConfig;

	/**
	 *BC箱号绑定WJ箱号个数
	 */
	@Value("${duccPropertyConfig.BCContainWJNumberLimit:10}")
	@LafUcc
	private int BCContainWJNumberLimit;

	/**
	 *WJ箱号包裹数量限制
	 */
	@Value("${duccPropertyConfig.WJPackageNumberLimit:1000}")
	@LafUcc
	private int WJPackageNumberLimit;

	private List<String> _multiplePackageSpotCheckSitesList = new ArrayList<>();

	@Value("${duccPropertyConfig.addiOwnNumberConf:010K1961638,}")
	@LafUcc
	private String addiOwnNumberConf;

	/**
	 *人工抽检是否开启AI图片识别
	 */
	@Value("${duccPropertyConfig.aiDistinguishSwitch:true}")
	@LafUcc
	private boolean aiDistinguishSwitch;

	/**
	 *BC箱号强制绑定循环集包袋拦截黑名单  1代表不拦截  参数是站点代表站点拦截
	 */
	@Value("${duccPropertyConfig.allBCBoxFilterWebSite:-1}")
	@LafUcc
	private String allBCBoxFilterWebSite;

	/**
	 *纯配外单0重量拦截黑名单： 1 是全部不拦截，如果是 站点，站点  代表站点是拦截黑名单
	 */
	@Value("${duccPropertyConfig.allPureValidateWeightWebSite:-1}")
	@LafUcc
	private String allPureValidateWeightWebSite;

	/**
	 *转运抽检是否执行新抽检模式
	 */
	@Value("${duccPropertyConfig.androidIsExecuteNewSpotCheck:false}")
	@LafUcc
	private boolean androidIsExecuteNewSpotCheck;

	@Value("${duccPropertyConfig.approvalSwitch:false}")
	@LafUcc
	private boolean approvalSwitch;

	/**
	 *JMQ 直接存入JMQ,TBSCHEDULE 直接存入DB或者Redis,FAILOVER 在JMQ、TBSCHEDULE按顺序failover
	 */
	@Value("${duccPropertyConfig.asynBufferDynamicProducerProducerType:TBSCHEDULE}")
	@LafUcc
	private String asynBufferDynamicProducerProducerType;

	/**
	 *是否存储消费成功的任务信息
	 */
	@Value("${duccPropertyConfig.asynBufferJmqComsumerTaskProcessorPostTaskStoreEnbaled:true}")
	@LafUcc
	private boolean asynBufferJmqComsumerTaskProcessorPostTaskStoreEnbaled;

	/**
	 *不开启jmq模式的task类型,配置规则：taskType-keyword1；taskType-keyword1
	 */
	@Value("${duccPropertyConfig.asynBufferNotenabledTaskKeyword1:1300-4;1300-6}")
	@LafUcc
	private String asynBufferNotenabledTaskKeyword1;

	/**
	 *开启的多级异步缓冲组件的任务类型列表，任务类型为整形值，多个类型以分号隔开，如1;2
	 */
	@Value("${duccPropertyConfig.asynbufferEnabledTaskType:3300;0;1;30;40;60;80;1300;4300;6667;1200;1210;1220;1110;1130;1600;1140;1120;1601;1400;1300;1160;6666;1800;7779;1180;1260}")
	@LafUcc
	private String asynbufferEnabledTaskType;

	/**
	 *龙门架上传称重任务接口切换，开放的场地
	 */
	@Value("${duccPropertyConfig.automaticWeightVolumeExchangeSiteCode:''}")
	@LafUcc
	private String automaticWeightVolumeExchangeSiteCode;

	/**
	 *龙门架上传称重任务接口切换
	 */
	@Value("${duccPropertyConfig.automaticWeightVolumeExchangeSwitch:false}")
	@LafUcc
	private boolean automaticWeightVolumeExchangeSwitch;

	/**
	 *反调度校验
	 */
	@Value("${duccPropertyConfig.backDispatchCheck:true}")
	@LafUcc
	private boolean backDispatchCheck;

	/**
	 *大运单报警
	 */
	@Value("${duccPropertyConfig.bigWaybillWaringSize:5000}")
	@LafUcc
	private Integer bigWaybillWaringSize;

	/**
	 *组板校验切换开关
	 */
	@Value("${duccPropertyConfig.boardCombinationSwitchVerToWebSites:-1}")
	@LafUcc
	private String boardCombinationSwitchVerToWebSites;

	/**
	 *PDA建箱包裹数量限制 试用站点
	 */
	@Value("${duccPropertyConfig.boxLimitSites:''}")
	@LafUcc
	private String boxLimitSites;

	/**
	 *日志查询开关 0：关闭 1：开启
	 */
	@Value("${duccPropertyConfig.businessLogQueryPageSwitch:'1'}")
	@LafUcc
	private String businessLogQueryPageSwitch;

	/**
	 *取消鸡毛信切换OMS接口开关，true：切换；false：不切换
	 */
	@Value("${duccPropertyConfig.cancelJimaoxinSwitchToOMS:false}")
	@LafUcc
	private boolean cancelJimaoxinSwitchToOMS;

	/**
	 *cassandra服务的全局开关，若资源不可用则可以关闭写入。
	 */
	@Value("${duccPropertyConfig.cassandraGlobalSwitch:true}")
	@LafUcc
	private boolean cassandraGlobalSwitch;

	/**
	 *箱号是否发货开关（true:老逻辑，false:新逻辑）
	 */
	@Value("${duccPropertyConfig.checkBoxSendedSwitchOn:true}")
	@LafUcc
	private boolean checkBoxSendedSwitchOn;

	@Value("${duccPropertyConfig.checkDeviceLocationInRealTimeSwitch:-1}")
	@LafUcc
	private Integer checkDeviceLocationInRealTimeSwitch;

	/**
	 *是否调度终端 判断签单返还审批
	 */
	@Value("${duccPropertyConfig.checkSignAndReturn:true}")
	@LafUcc
	private boolean checkSignAndReturn;

	/**
	 *校验站点子类型是否三方:16
	 */
	@Value("${duccPropertyConfig.checkSiteSubType:false}")
	@LafUcc
	private boolean checkSiteSubType;

	/**
	 *出管国际化页面查询接口开关 true查新接口，false 查旧接口
	 */
	@Value("${duccPropertyConfig.chuguanNewPageQuerySwitch:false}")
	@LafUcc
	private boolean chuguanNewPageQuerySwitch;

	/**
	 *写出管是否开启供应链中台二期逻辑true开启，false不开启
	 */
	@Value("${duccPropertyConfig.chuguanPurchaseAndSaleSwitch:true}")
	@LafUcc
	private boolean chuguanPurchaseAndSaleSwitch;

	/**
	 *限制按钮点击间隔时间（秒），配置-1关闭提示
	 */
	@Value("${duccPropertyConfig.clickIntervalSecond:-1}")
	@LafUcc
	private int clickIntervalSecond;

	/**
	 *客户端下线菜单配置
	 */
	@Value("${duccPropertyConfig.clientOfflineMenuConfig:''}")
	@LafUcc
	private String clientOfflineMenuConfig;

	/**
	 *打印客户端大查询限制查询间隔时间
	 */
	@Value("${duccPropertyConfig.clientPrintQueryGapTime:10}")
	@LafUcc
	private int clientPrintQueryGapTime;

	/**
	 *异步缓冲框架，JMQ消费失败不再降级为TB任务  1：关闭DB模式
	 */
	@Value("${duccPropertyConfig.closeAsynBufferSaveTaskToDb:0}")
	@LafUcc
	private String closeAsynBufferSaveTaskToDb;

	/**
	 *集货区是否删除站点配置
	 */
	@Value("${duccPropertyConfig.collectGoodsDeleteSites:-1}")
	@LafUcc
	private String collectGoodsDeleteSites;

	/**
	 *集包地配置开启开关（站点以","分开）
	 */
	@Value("${duccPropertyConfig.collectionAddressSiteCodes:910,364605}")
	@LafUcc
	private String collectionAddressSiteCodes;

	/**
	 *开关控制是否校验包裹号  true 校验  false 不校验
	 */
	@Value("${duccPropertyConfig.controlCheckPackage:true}")
	@LafUcc
	private boolean controlCheckPackage;

	/**
	 *开关控制是否校验路由异常提醒  true 提醒  false不提醒
	 */
	@Value("${duccPropertyConfig.controlCheckRoute:true}")
	@LafUcc
	private boolean controlCheckRoute;

	/**
	 *老发货改造兜底task可以执行任务的次数阈值
	 */
	@Value("${duccPropertyConfig.createSendTaskExecuteCount:30}")
	@LafUcc
	private Integer createSendTaskExecuteCount;

	/**
	 *老发货改造-兜底task超时时间阈值
	 */
	@Value("${duccPropertyConfig.createSendTasktimeOut:30}")
	@LafUcc
	private Integer createSendTasktimeOut;

	/**
	 *快运大宗订单包裹数定义
	 */
	@Value("${duccPropertyConfig.dazongPackageOperateMax:100}")
	@LafUcc
	private Integer dazongPackageOperateMax;

	/**
	 *安卓快运发货隐藏站点编号
	 */
	@Value("${duccPropertyConfig.deliverHideSites:910}")
	@LafUcc
	private String deliverHideSites;

	/**
	 *老发货异步处理开关
	 */
	@Value("${duccPropertyConfig.deliverySendAsyncSite:''}")
	@LafUcc
	private String deliverySendAsyncSite;

	/**
	 *老发货异步任务延时消费毫秒数
	 */
	@Value("${duccPropertyConfig.deliverySendTaskSleepMills:1000}")
	@LafUcc
	private int deliverySendTaskSleepMills;

	/**
	 *取消发货校验封车业务开关。0：关闭  非0：开启
	 */
	@Value("${duccPropertyConfig.dellCancelDeliveryCheckSealCar:1}")
	@LafUcc
	private String dellCancelDeliveryCheckSealCar;

	/**
	 *dws抽检AI识别大包裹限制
	 */
	@Value("${duccPropertyConfig.deviceAIDistinguishPackNum:100}")
	@LafUcc
	private int deviceAIDistinguishPackNum;

	/**
	 *dws抽检AI识别开关，多个场地以,分隔
	 */
	@Value("${duccPropertyConfig.deviceAIDistinguishSwitch:910}")
	@LafUcc
	private String deviceAIDistinguishSwitch;

	/**
	 *禁用登陆老接口
	 */
	@Value("${duccPropertyConfig.disablePdaOldLogin:false}")
	@LafUcc
	private boolean disablePdaOldLogin;

	private List<Integer> dpSiteCodeList = new ArrayList<>();

	/**
	 *DP上海虚拟分拣中心
	 */
	@Value("${duccPropertyConfig.dpSiteCodes:''}")
	@LafUcc
	private String dpSiteCodes;

	/**
	 *批量一车一单发货，德邦单匹配德邦批次号开关
	 */
	@Value("${duccPropertyConfig.dpWaybillMatchSendCodeSwitch:true}")
	@LafUcc
	private boolean dpWaybillMatchSendCodeSwitch;

	/**
	 *接受经济网推送箱包关系  false-不接收永远返回成功
	 */
	@Value("${duccPropertyConfig.eNetSyncWaybillCodeAndBoxCode:true}")
	@LafUcc
	private boolean eNetSyncWaybillCodeAndBoxCode;

	/**
	 *经济网推送智腾达失败重试
	 */
	@Value("${duccPropertyConfig.economicNetPushZTDRetry:true}")
	@LafUcc
	private boolean economicNetPushZTDRetry;

	/**
	 *众邮重量拦截 true 拦截，false 不拦截
	 */
	@Value("${duccPropertyConfig.economicNetValidateWeightSwitch:true}")
	@LafUcc
	private boolean economicNetValidateWeightSwitch;

	@Value("${duccPropertyConfig.enableGoodsAreaOfTysScan:true}")
	@LafUcc
	private boolean enableGoodsAreaOfTysScan;

	/**
	 *单个导出页面限制最大并行导出执行数   -1 作为系统降级不支持导出
	 */
	@Value("${duccPropertyConfig.exportConcurrencyLimitNum:50}")
	@LafUcc
	private Integer exportConcurrencyLimitNum;

	/**
	 *抽检导出最大限制
	 */
	@Value("${duccPropertyConfig.exportSpotCheckMaxSize:10000}")
	@LafUcc
	private Integer exportSpotCheckMaxSize;

	/**
	 *面单举报一二级类型配置
	 */
	@Value("${duccPropertyConfig.faceAbnormalReportConfig:''}")
	@LafUcc
	private String faceAbnormalReportConfig;

	@Value("${duccPropertyConfig.filterSendCodeSwitch:false}")
	@LafUcc
	private boolean filterSendCodeSwitch;

	/**
	 *易冻损货物暂存时间最小限制时间
	 */
	@Value("${duccPropertyConfig.goodsResidenceTime:3}")
	@LafUcc
	private int goodsResidenceTime;

	/**
	 *隐藏特殊始发场地目的场地名单
	 */
	@Value("${duccPropertyConfig.hideSpecialStartSitPrintDestinationSiteList:''}")
	@LafUcc
	private String hideSpecialStartSitPrintDestinationSiteList;

	private List<String> hideSpecialStartSitPrintDestinationSiteStrList = new ArrayList<>();

	/**
	 *隐藏特殊始发场地替换字符
	 */
	@Value("${duccPropertyConfig.hideSpecialStartSitePrintReplaceSymbol:''}")
	@LafUcc
	private String hideSpecialStartSitePrintReplaceSymbol;

	@Value("${duccPropertyConfig.hideSpecialStartSitePrintReplaceSymbolMaxLength:20}")
	@LafUcc
	private int hideSpecialStartSitePrintReplaceSymbolMaxLength;

	/**
	 *隐藏特殊始发场地名称开关，0-关，1-开
	 */
	@Value("${duccPropertyConfig.hideSpecialStartSitePrintSwitch:0}")
	@LafUcc
	private int hideSpecialStartSitePrintSwitch;

	/**
	 *身份证识别功能，站点开关。","为分隔符
	 */
	@Value("${duccPropertyConfig.identityRecogniseSiteSwitch:}")
	@LafUcc
	private String identityRecogniseSiteSwitch;
    
    private List<String> identityRecogniseSiteSwitchList = new ArrayList<>();

	/**
	 *单次插入数据库的条数
	 */
	@Value("${duccPropertyConfig.insertDbRowsOneTime:100}")
	@LafUcc
	private int insertDbRowsOneTime;

	@Value("${duccPropertyConfig.inspectNoSendNoLoadWaybillDemotion:false}")
	@LafUcc
	private boolean inspectNoSendNoLoadWaybillDemotion;

	/**
	 *提供标准包裹验货消息
	 */
	@Value("${duccPropertyConfig.inspectionAggEffectiveSites:-1}")
	@LafUcc
	private String inspectionAggEffectiveSites;

	/**
	 *验货依赖物料开关 true：不依赖
	 */
	@Value("${duccPropertyConfig.inspectionAssertDemotion:false}")
	@LafUcc
	private boolean inspectionAssertDemotion;

	/**
	 *大运单拆分包裹验货的分拣中心，配置-1则关闭，配置ALL全部开启
	 */
	@Value("${duccPropertyConfig.inspectionBigWaybillEffectiveSites:-1}")
	@LafUcc
	private String inspectionBigWaybillEffectiveSites;

    /**
     * 西藏模式业务场景开关，按分拣中心归属的省份配置，不配置业务场景不生效，配置ALL全国生效
     */
	@Value("${duccPropertyConfig.itmsBizEnableSwitch:''}")
	@LafUcc
	private String itmsBizEnableSwitch;

	/**
	 *判断包裹是否打印的逻辑，包含终端首次打印的数据。默认不包含 0：不包含；1：包含
	 */
	@Value("${duccPropertyConfig.judgePackagePrintedIncludeSiteTerminal:'0'}")
	@LafUcc
	private String judgePackagePrintedIncludeSiteTerminal;

	/**
	 *拣运基础服务开关
	 */
	@Value("${duccPropertyConfig.jyBasicServerSwitch:false}")
	@LafUcc
	private boolean jyBasicServerSwitch;

	/**
	 *拣运功能降级开关配置
	 */
	@Value("${duccPropertyConfig.jyDemotionConfig:''}")
	@LafUcc
	private String jyDemotionConfig;

	/**
	 *
	 */
	@Value("${duccPropertyConfig.jySendTaskCreateTimeBeginDay:30}")
	@LafUcc
	private Integer jySendTaskCreateTimeBeginDay;

	/**
	 *拣运发货任务装载率上下限配置
	 */
	@Value("${duccPropertyConfig.jySendTaskLoadRateLimit:80,150}")
	@LafUcc
	private String jySendTaskLoadRateLimit;

	@Value("${duccPropertyConfig.jySendTaskLoadRateLowerLimit:80}")
	@LafUcc
	private Integer jySendTaskLoadRateLowerLimit;

	@Value("${duccPropertyConfig.jySendTaskLoadRateUpperLimit:150}")
	@LafUcc
	private Integer jySendTaskLoadRateUpperLimit;

	/**
	 *
	 */
	@Value("${duccPropertyConfig.jySendTaskPlanTimeBeginDay:1}")
	@LafUcc
	private Integer jySendTaskPlanTimeBeginDay;

	/**
	 *
	 */
	@Value("${duccPropertyConfig.jySendTaskPlanTimeEndDay:2}")
	@LafUcc
	private Integer jySendTaskPlanTimeEndDay;

	@Value("${duccPropertyConfig.jyTaskPageMax:200}")
	@LafUcc
	private Integer jyTaskPageMax;

	/**
	 *到车任务按积分排序开关 1：开启
	 */
	@Value("${duccPropertyConfig.jyUnSealTaskOrderByIntegral:0}")
	@LafUcc
	private Integer jyUnSealTaskOrderByIntegral;

	@Value("${duccPropertyConfig.jyUnSealTaskSwitchToEs:0}")
	@LafUcc
	private Integer jyUnSealTaskSwitchToEs;

	/**
	 *转运卸车岗查询列表时间过滤
	 */
	@Value("${duccPropertyConfig.jyUnloadCarListQueryDayFilter:3}")
	@LafUcc
	private Integer jyUnloadCarListQueryDayFilter;

	/**
	 *拣运卸车逐单卸阈值
	 */
	@Value("${duccPropertyConfig.jyUnloadSingleWaybillThreshold:20}")
	@LafUcc
	private Integer jyUnloadSingleWaybillThreshold;

	/**
	 *是否限制终端人员使用包裹补打 1：限制 0：不限制
	 */
	@Value("${duccPropertyConfig.limitSiteUsePackReprint:'0'}")
	@LafUcc
	private String limitSiteUsePackReprint;

	/**
	 *装车任务-最大包裹数限制
	 */
	@Value("${duccPropertyConfig.loadScanTaskPackageMaxSize:500}")
	@LafUcc
	private int loadScanTaskPackageMaxSize;

	/**
	 *装车-版号转包裹号最大包裹数限制
	 */
	@Value("${duccPropertyConfig.loadScanTaskPackageSize:100}")
	@LafUcc
	private int loadScanTaskPackageSize;

	/**
	 *装车扫描每个任务下的运单数量上线
	 */
	@Value("${duccPropertyConfig.loadScanTaskWaybillSize:200}")
	@LafUcc
	private int loadScanTaskWaybillSize;

	/**
	 *决定是否使用kafka写入日志
	 */
	@Value("${duccPropertyConfig.logToBusinessLogByKafka:true}")
	@LafUcc
	private boolean logToBusinessLogByKafka;

	/**
	 *打印客户端菜单功能配置，根据菜单编码和站点类型和站点子类型获取功能权限
	 */
	@Value("${duccPropertyConfig.menuCodeFuncConfig:}")
	@LafUcc
	private String menuCodeFuncConfig;

	/**
	 *打印客户端菜单功能配置，根据菜单编码和站点类型和站点子类型获取功能权限（新）
	 */
	@Value("${duccPropertyConfig.menuCodeFuncConfigNew:}")
	@LafUcc
	private String menuCodeFuncConfigNew;

	/**
	 *bool值，表示数据库写复制到备份数据库（mysql）的开关，true表示开
	 */
	@Value("${duccPropertyConfig.migrationDbBackupReplicateEnable:true}")
	@LafUcc
	private boolean migrationDbBackupReplicateEnable;

	/**
	 *bool值，数据库复制中是否忽略异常，true表示忽略异常，false表示不忽
	 */
	@Value("${duccPropertyConfig.migrationDbBackupReplicateIgnoreExp:true}")
	@LafUcc
	private boolean migrationDbBackupReplicateIgnoreExp;

	/**
	 *一单多件抽检场地配置
	 */
	@Value("${duccPropertyConfig.multiplePackageSpotCheckSites:''}")
	@LafUcc
	private String multiplePackageSpotCheckSites;

	/**
	 *一单多件抽检开关，0-关，1-开
	 */
	@Value("${duccPropertyConfig.multiplePackageSpotCheckSwitch:0}")
	@LafUcc
	private int multiplePackageSpotCheckSwitch;

	private List<String> needInterceptUrlList = new ArrayList<>();

	/**
	 *
	 */
	@Value("${duccPropertyConfig.needInterceptUrls:*}")
	@LafUcc
	private String needInterceptUrls;

	/**
	 *百川接口-开关 true  false
	 */
	@Value("${duccPropertyConfig.needUseNewReverseApi:true}")
	@LafUcc
	private boolean needUseNewReverseApi;

	private List<Integer> needValidateMainLineBizSourceCodes = new ArrayList<>();

	/**
	 *传摆发货-干支限制业务列表[25,2]
	 */
	@Value("${duccPropertyConfig.needValidateMainLineBizSourceList:[25]}")
	@LafUcc
	private String needValidateMainLineBizSourceList;

	/**
	 *日志页面提示
	 */
	@Value("${duccPropertyConfig.newLogPageTips:''}")
	@LafUcc
	private String newLogPageTips;

	/**
	 *打印客户端无权限菜单编码配置，根据站点类型和站点子类型配置不同菜单编码
	 */
	@Value("${duccPropertyConfig.noAuthMenuConfig:}")
	@LafUcc
	private String noAuthMenuConfig;

	/**
	 *打印客户端无权限菜单编码配置，根据站点类型和站点子类型配置不同菜单编码（新）
	 */
	@Value("${duccPropertyConfig.noAuthMenuConfigNew:}")
	@LafUcc
	private String noAuthMenuConfigNew;

	/**
	 *自动签退超过多少小时未签退的数据
	 */
	@Value("${duccPropertyConfig.notSignedOutRecordMoreThanHours:-1}")
	@LafUcc
	private int notSignedOutRecordMoreThanHours;

	@Value("${duccPropertyConfig.notSignedOutRecordRangeHours:12}")
	@LafUcc
	private int notSignedOutRecordRangeHours;

	private List<Integer> notValidateTransTypeCodes = new ArrayList<>();

	@Value("${duccPropertyConfig.notValidateTransTypeCodesList:[3,4,10,11]}")
	@LafUcc
	private String notValidateTransTypeCodesList;

	@Value("${duccPropertyConfig.offlineCurrentLimitingCount:-1}")
	@LafUcc
	private Integer offlineCurrentLimitingCount;

	/**
	 *true：保存日志 false：不再保存
	 */
	@Value("${duccPropertyConfig.offlineLogGlobalSwitch:true}")
	@LafUcc
	private boolean offlineLogGlobalSwitch;

	/**
	 *pda下线菜单编码
	 */
	@Value("${duccPropertyConfig.offlinePdaMenuCode:0101015}")
	@LafUcc
	private String offlinePdaMenuCode;

	/**
	 *下线一键封车
	 */
	@Value("${duccPropertyConfig.offlineQuickSeal:false}")
	@LafUcc
	private boolean offlineQuickSeal;

	/**
	 *离线任务操作时间限制：默认在系统时间之前96h丢弃，之内则记录上传的操作时间
	 */
	@Value("${duccPropertyConfig.offlineTaskOperateTimeBeforeNowLimitHours:96}")
	@LafUcc
	private int offlineTaskOperateTimeBeforeNowLimitHours;

	/**
	 *离线任务矫正操作时间得时间范围，超过该值则会被纠正为当前时间（单位：h）
	 */
	@Value("${duccPropertyConfig.offlineTaskOperateTimeCorrectHours:24}")
	@LafUcc
	private int offlineTaskOperateTimeCorrectHours;

	/**
	 *离线任务上传拦截报表，0 - 全部开启，-1 - 全部关闭，类似1243,3534表示具体场地
	 */
	@Value("${duccPropertyConfig.offlineTaskReportInterceptSites:-1}")
	@LafUcc
	private String offlineTaskReportInterceptSites;

	/**
	 *老日志页面提示
	 */
	@Value("${duccPropertyConfig.oldLogPageTips:''}")
	@LafUcc
	private String oldLogPageTips;

	@Value("${duccPropertyConfig.oldSendSplitPageSize:30}")
	@LafUcc
	private Integer oldSendSplitPageSize;

	/**
	 *导出分页单次查询数据量
	 */
	@Value("${duccPropertyConfig.oneQuerySize:1000}")
	@LafUcc
	private Integer oneQuerySize;

	/**
	 *0不关闭入口；1关闭基础资料维护入口；2关闭耗材明细的增加和删除按钮；3关闭两者
	 */
	@Value("${duccPropertyConfig.packConsumableSwitch:0}")
	@LafUcc
	private Integer packConsumableSwitch;

	/**
	 *包裹补打强制拦截状态码
	 */
	@Value("${duccPropertyConfig.packRePrintInterceptStatus:-790}")
	@LafUcc
	private String packRePrintInterceptStatus;

	/**
	 *转运单个包裹重量阈值（单位kg）
	 */
	@Value("${duccPropertyConfig.packageWeightLimit:'2000'}")
	@LafUcc
	private String packageWeightLimit;

	/**
	 *并发获取包裹明细 false 关闭
	 */
	@Value("${duccPropertyConfig.paralleGetPackageSwitch:false}")
	@LafUcc
	private boolean paralleGetPackageSwitch;

	/**
	 *PDA通知自动拉取间隔时间(单位秒)
	 */
	@Value("${duccPropertyConfig.pdaNoticePullIntervalTime:1800}")
	@LafUcc
	private Integer pdaNoticePullIntervalTime;

	/**
	 *现场预分拣 超区运单拦截开关;true 开启拦截
	 */
	@Value("${duccPropertyConfig.preOutZoneSwitch:true}")
	@LafUcc
	private boolean preOutZoneSwitch;

	/**
	 *一键封车空批次剔除开关 1：开启剔除 0：关闭
	 */
	@Value("${duccPropertyConfig.preSealVehicleRemoveEmptyBatchCode:1}")
	@LafUcc
	private String preSealVehicleRemoveEmptyBatchCode;

	/**
	 *现场预分拣校验开关
	 */
	@Value("${duccPropertyConfig.preSortOnSiteSwitchOn:true}")
	@LafUcc
	private boolean preSortOnSiteSwitchOn;

	/**
	 *按运单大打印回调异步处理的包裹数限制
	 */
	@Value("${duccPropertyConfig.printCompleteCallbackAsyncPackageNum:500}")
	@LafUcc
	private int printCompleteCallbackAsyncPackageNum;

	/**
	 *打印交接清单新查询开通场地,1)、字符串false代表不开启, 2)、多个场地以,分隔,3)、字符串true代表全国
	 */
	@Value("${duccPropertyConfig.printHandoverListSites:false}")
	@LafUcc
	private String printHandoverListSites;

	@Value("${duccPropertyConfig.printScrollQueryCountLimit:200}")
	@LafUcc
	private int printScrollQueryCountLimit;

	/**
	 *查询敏感数据开关
	 */
	@Value("${duccPropertyConfig.querySensitiveFlag:false}")
	@LafUcc
	private boolean querySensitiveFlag;

	/**
	 *一键封车友情提示
	 */
	@Value("${duccPropertyConfig.quickSealTips:''}")
	@LafUcc
	private String quickSealTips;

	/**
	 *读转运卸车表开关
	 */
	@Value("${duccPropertyConfig.readUnloadFromTys:false}")
	@LafUcc
	private boolean readUnloadFromTys;

	/**
	 *#任务redis开关0-关闭 1-开启
	 */
	@Value("${duccPropertyConfig.redisSwitchOn:0}")
	@LafUcc
	private String redisSwitchOn;

	/**
	 *封车是否剔除空批次  1：开启剔除 0：关闭
	 */
	@Value("${duccPropertyConfig.removeEmptyBatchCode:1}")
	@LafUcc
	private String removeEmptyBatchCode;

	/**
	 *
	 */
	@Value("${duccPropertyConfig.restApiOuthSwitch:true}")
	@LafUcc
	private boolean restApiOuthSwitch;

	@Value("${duccPropertyConfig.scannerOperateCalculateIfInterceptSites:''}")
	@LafUcc
	private String scannerOperateCalculateIfInterceptSites;

	/**
	 *预分拣返调度校验同城 1:开启；0：关闭
	 */
	@Value("${duccPropertyConfig.scheduleSiteCheckSameCity:''}")
	@LafUcc
	private String scheduleSiteCheckSameCity;

	/**
	 *
	 */
	@Value("${duccPropertyConfig.scrollQuerySize:2000}")
	@LafUcc
	private int scrollQuerySize;

	/**
	 *
	 */
	@Value("${duccPropertyConfig.sealStatusBatchSizeLimit:30}")
	@LafUcc
	private int sealStatusBatchSizeLimit;

	/**
	 *作业工作台待解封车强制降级 1：强制 0：不强制
	 */
	@Value("${duccPropertyConfig.sealTaskForceFallback:0}")
	@LafUcc
	private int sealTaskForceFallback;

	/**
	 *
	 */
	@Value("${duccPropertyConfig.sealTaskHystrixProps: ''}")
	@LafUcc
	private String sealTaskHystrixProps;

	/**
	 *封车体积校验开关（站点以","分开）
	 */
	@Value("${duccPropertyConfig.sealVolumeCheckSites:-1}")
	@LafUcc
	private String sealVolumeCheckSites;

	/**
	 *安全次数开关
	 */
	@Value("${duccPropertyConfig.securitySwitch:true}")
	@LafUcc
	private boolean securitySwitch;

	/**
	 *批次有效性校验的场地
	 */
	@Value("${duccPropertyConfig.sendCodeEffectiveValidation:''}")
	@LafUcc
	private String sendCodeEffectiveValidation;

	/**
	 *切换新的批次号生成器的开关
	 */
	@Value("${duccPropertyConfig.sendCodeGenSwitchOn:false}")
	@LafUcc
	private boolean sendCodeGenSwitchOn;

	/**
	 *
	 */
	@Value("${duccPropertyConfig.sensitiveInfoHideSwitch:false}")
	@LafUcc
	private Boolean sensitiveInfoHideSwitch;

	/**
	 *一车一单发货分拣验证开关
	 */
	@Value("${duccPropertyConfig.singleSendSwitchVerToWebSites:-1}")
	@LafUcc
	private String singleSendSwitchVerToWebSites;

	/**
	 *启用批次有效性校验的分拣中心. 分拣中心ID逗号分隔
	 */
	@Value("${duccPropertyConfig.siteEnableSendCodeEffectiveValidation:''}")
	@LafUcc
	private String siteEnableSendCodeEffectiveValidation;

	/**
	 *站点平台打印菜单是否校验功能
	 */
	@Value("${duccPropertyConfig.sitePlateIsCheckFunc:false}")
	@LafUcc
	private boolean sitePlateIsCheckFunc;

	/**
	 *站点查询最大限制
	 */
	@Value("${duccPropertyConfig.siteQueryLimit:500}")
	@LafUcc
	private Integer siteQueryLimit;

	/**
	 *分拣查询使用的模式，可配置DMS、MIDDLEEND、FAILOVER三个值
	 */
	@Value("${duccPropertyConfig.sortingQueryMode:FAILOVER}")
	@LafUcc
	private String sortingQueryMode;

	/**
	 *分拣实操模式，可设置DMS、MIDDLEEND、FAILOVER三个值
	 */
	@Value("${duccPropertyConfig.sortingServiceMode:FAILOVER}")
	@LafUcc
	private String sortingServiceMode;

	/**
	 *抽检是否控制下发
	 */
	@Value("${duccPropertyConfig.spotCheckIssueControl:true}")
	@LafUcc
	private boolean spotCheckIssueControl;

	@Value("${duccPropertyConfig.spotCheckNoExcessLimit:1.5}")
	@LafUcc
	private double spotCheckNoExcessLimit;

	/**
	 *执行抽检改造的站点集合（以英文逗号分隔，true表示全国，false表示全部不开启）
	 */
	@Value("${duccPropertyConfig.spotCheckReformSiteCodes:true}")
	@LafUcc
	private String spotCheckReformSiteCodes;

	/**
	 *停止写分拣卸车表开关
	 */
	@Value("${duccPropertyConfig.stopWriteUnloadFromDms:false}")
	@LafUcc
	private boolean stopWriteUnloadFromDms;

	/**
	 *uccPropertyConfiguration.switchVerToWebSites
	 */
	@Value("${duccPropertyConfig.switchVerToWebSites:-1}")
	@LafUcc
	private String switchVerToWebSites;

	/**
	 *
	 */
	@Value("${duccPropertyConfig.syncJySealStatusSwitch:false}")
	@LafUcc
	private boolean syncJySealStatusSwitch;

	/**
	 *true：保存日志 false：不保存
	 */
	@Value("${duccPropertyConfig.systemLogGlobalSwitch:true}")
	@LafUcc
	private boolean systemLogGlobalSwitch;

	/**
	 *转运卸车岗交班次数最大数量
	 */
	@Value("${duccPropertyConfig.tysUnloadTaskHandoverMaxSize:3}")
	@LafUcc
	private Integer tysUnloadTaskHandoverMaxSize;

	/**
	 *转运卸车完成后补扫时间限制（小时），过后禁止补扫
	 */
	@Value("${duccPropertyConfig.tysUnloadTaskSupplementScanLimitHours:72}")
	@LafUcc
	private Integer tysUnloadTaskSupplementScanLimitHours;

	/**
	 *转运组板最大包裹数
	 */
	@Value("${duccPropertyConfig.unloadBoardBindingsMaxCount:100}")
	@LafUcc
	private Integer unloadBoardBindingsMaxCount;

	/**
	 *转运卸车缓存时长（单位小时）
	 */
	@Value("${duccPropertyConfig.unloadCacheDurationHours:24}")
	@LafUcc
	private Integer unloadCacheDurationHours;

	@Value("${duccPropertyConfig.uploadDeviceLocationInterval:-1}")
	@LafUcc
	private Integer uploadDeviceLocationInterval;

	/**
	 *装车扫描采用新ES库存查询方式的站点
	 */
	@Value("${duccPropertyConfig.useNewInventorySiteCodes:''}")
	@LafUcc
	private String useNewInventorySiteCodes;

	/**
	 *虚拟组板自动完结时间，单位：天
	 */
	@Value("${duccPropertyConfig.virtualBoardAutoCloseDays:1}")
	@LafUcc
	private Integer virtualBoardAutoCloseDays;

	/**
	 *分拣组板功能开通场地
	 */
	@Value("${duccPropertyConfig.virtualBoardCanUseSite:-1}")
	@LafUcc
	private String virtualBoardCanUseSite;

	private List<String> virtualBoardCanUseSiteList = new ArrayList<>();

	/**
	 *虚拟组板最多流向个数
	 */
	@Value("${duccPropertyConfig.virtualBoardMaxDestinationCount:5}")
	@LafUcc
	private int virtualBoardMaxDestinationCount;

	/**
	 *虚拟组板最多放置包裹个数
	 */
	@Value("${duccPropertyConfig.virtualBoardMaxItemCount:50}")
	@LafUcc
	private int virtualBoardMaxItemCount;

	@Value("${duccPropertyConfig.virtualSiteCode:1721378}")
	@LafUcc
	private Integer virtualSiteCode;

	/**
	 *运单最大包裹数
	 */
	@Value("${duccPropertyConfig.waybillMaxPackNum:40000}")
	@LafUcc
	private int waybillMaxPackNum;

	/**
	 *分拣拆分任务分页包裹数
	 */
	@Value("${duccPropertyConfig.waybillSplitPageSize:1000}")
	@LafUcc
	private int waybillSplitPageSize;

	/**
	 *转运单个运单重量阈值（单位kg）
	 */
	@Value("${duccPropertyConfig.waybillWeightLimit:'10000'}")
	@LafUcc
	private String waybillWeightLimit;

	/**
	 *称重量方拦截开关
	 */
	@Value("${duccPropertyConfig.weightVolumeFilterWholeCountryFlag:false}")
	@LafUcc
	private boolean weightVolumeFilterWholeCountryFlag;

	/**
	 *称重量方规则标准值（kg、cm单位）
	 */
	@Value("${duccPropertyConfig.weightVolumeRuleStandard:''}")
	@LafUcc
	private String weightVolumeRuleStandard;

	/**
	 *称重量方的规则一直在变化，为了有一个版本的切换过程，这里加一个开关
	 */
	@Value("${duccPropertyConfig.weightVolumeSwitchVersion:0}")
	@LafUcc
	private Integer weightVolumeSwitchVersion;

	/**
	 *配置哪些任务失败后不再重复抓取的
	 */
	@Value("${duccPropertyConfig.workerFetchWithoutFailedTable:task_inspection}")
	@LafUcc
	private String workerFetchWithoutFailedTable;

	/**
	 *写转运卸车表开关
	 */
	@Value("${duccPropertyConfig.writeUnloadFromTys:false}")
	@LafUcc
	private boolean writeUnloadFromTys;
	/**
	 *
	 */
	@Value("${duccPropertyConfig.aggsDataSource:jyCore}")
	@LafUcc
	private String aggsDataSource;

	@Value("${duccPropertyConfig.allianceBusinessSwitch:true}")
	@LafUcc
	private boolean allianceBusinessSwitch;

	/**
	 *三无任务指派数量限制
	 */
	@Value("${duccPropertyConfig.assignExpTaskQuantityLimit:50}")
	@LafUcc
	private int assignExpTaskQuantityLimit;

	@Value("${duccPropertyConfig.autoCloseJyBizTaskConfig:{}}")
	@LafUcc
	private String autoCloseJyBizTaskConfig;

	private AutoCloseJyBizTaskConfig autoCloseJyBizTaskConfigObj;

	/**
	 *
	 */
	@Value("${duccPropertyConfig.autoPackageSendInspectionSiteCodes:3}")
	@LafUcc
	private String autoPackageSendInspectionSiteCodes;

	@Value("${duccPropertyConfig.batchGenerateSendCodeMaxNum:100}")
	@LafUcc
	private Integer batchGenerateSendCodeMaxNum;

	@Value("${duccPropertyConfig.batchQueryEndSiteLimit:30}")
	@LafUcc
	private int batchQueryEndSiteLimit;

	/**
	 *
	 */
	@Value("${duccPropertyConfig.batchSendForbiddenSwitch:false}")
	@LafUcc
	private boolean batchSendForbiddenSwitch;

	/**
	 *组板路由校验-是否开启路由校验开关
	 */
	@Value("${duccPropertyConfig.boardCombinationRouterSwitch:false}")
	@LafUcc
	private boolean boardCombinationRouterSwitch;

	@Value("${duccPropertyConfig.boardListQuerySwitch:true}")
	@LafUcc
	private Boolean boardListQuerySwitch;

	/**
	 *大宗包裹最小数量
	 */
	@Value("${duccPropertyConfig.bulkScanPackageMinCount:100}")
	@LafUcc
	private Integer bulkScanPackageMinCount;

	@Value("${duccPropertyConfig.checkTeAnSwitch:true}")
	@LafUcc
	private boolean checkTeAnSwitch;

	@Value("${duccPropertyConfig.cloudOssInsertSwitch:false}")
	@LafUcc
	private boolean cloudOssInsertSwitch;

	/**
	 *完成状态的异常任务获取天数限制
	 */
	@Value("${duccPropertyConfig.completeExpDayNumLimit:15}")
	@LafUcc
	private int completeExpDayNumLimit;

	@Value("${duccPropertyConfig.createBoardBySendFlowSwitch:true}")
	@LafUcc
	private boolean createBoardBySendFlowSwitch;

	@Value("${duccPropertyConfig.cttGroupSendFLowLimit:10}")
	@LafUcc
	private Integer cttGroupSendFLowLimit;

	/**
	 *
	 */
	@Value("${duccPropertyConfig.czOrgForbiddenList:''}")
	@LafUcc
	private String czOrgForbiddenList;

	@Value("${duccPropertyConfig.czQuerySwitch:true}")
	@LafUcc
	private boolean czQuerySwitch;

	@Value("${duccPropertyConfig.czSiteForbiddenList:''}")
	@LafUcc
	private String czSiteForbiddenList;

	/**
	 *拦截传站-网点类型黑名单
	 */
	@Value("${duccPropertyConfig.czSiteTypeForbiddenList:''}")
	@LafUcc
	private String czSiteTypeForbiddenList;

	private List<String> dewuCustomerCodeList = new ArrayList<>();

	@Value("${duccPropertyConfig.dewuCustomerCodes:-1}")
	@LafUcc
	private String dewuCustomerCodes;

	/**
	 *德邦春节模式场地
	 */
	@Value("${duccPropertyConfig.dpSpringSiteCode:}")
	@LafUcc
	private String dpSpringSiteCode;

	private List<Integer> dpSpringSiteCodeList = new ArrayList<>();

	/**
	 *交接至德邦校验开关
	 */
	@Value("${duccPropertyConfig.dpTransferSwitch:true}")
	@LafUcc
	private boolean dpTransferSwitch;

	/**
	 *易冻品校验开关
	 */
	@Value("${duccPropertyConfig.easyFreezeSwitch:true}")
	@LafUcc
	private boolean easyFreezeSwitch;

	@Value("${duccPropertyConfig.exScrapApproveLevelCountLimit:50,100}")
	@LafUcc
	private String exScrapApproveLevelCountLimit;

	private List<String> exceptionSubmitCheckSiteList = new ArrayList<>();

	/**
	 *异常处理检查场地名单
	 */
	@Value("${duccPropertyConfig.exceptionSubmitCheckSites:}")
	@LafUcc
	private String exceptionSubmitCheckSites;

	private List<String> exceptionSubmitCheckWaybillInterceptTypeList = new ArrayList<>();

	/**
	 *异常处理检查运单拦截类型
	 */
	@Value("${duccPropertyConfig.exceptionSubmitCheckWaybillInterceptTypes:}")
	@LafUcc
	private String exceptionSubmitCheckWaybillInterceptTypes;

	@Value("${duccPropertyConfig.forceSendSiteList:''}")
	@LafUcc
	private String forceSendSiteList;

	@Value("${duccPropertyConfig.ignoreTysTrackSwitch:false}")
	@LafUcc
	private boolean ignoreTysTrackSwitch;

	/**
	 *网格工种签到限制开关
	 */
	@Value("${duccPropertyConfig.jobTypeLimitSwitch:false}")
	@LafUcc
	private boolean jobTypeLimitSwitch;

	@Value("${duccPropertyConfig.jyArtificialStrandTaskCloseTime:28800000}")
	@LafUcc
	private Long jyArtificialStrandTaskCloseTime;

	@Value("${duccPropertyConfig.jyCollectSiteWhitelist:}")
	@LafUcc
	private String jyCollectSiteWhitelist;

	@Value("${duccPropertyConfig.jyComboardCountLimit:100}")
	@LafUcc
	private Integer jyComboardCountLimit;

	/**
	 *板列表查询sql执行开关
	 */
	@Value("${duccPropertyConfig.jyComboardListBoardSqlSwitch:true}")
	@LafUcc
	private Boolean jyComboardListBoardSqlSwitch;

	@Value("${duccPropertyConfig.jyComboardRefreshTimerInterval:30}")
	@LafUcc
	private Integer jyComboardRefreshTimerInterval;

	@Value("${duccPropertyConfig.jyComboardScanUserBeginDay:1}")
	@LafUcc
	private Integer jyComboardScanUserBeginDay;

	@Value("${duccPropertyConfig.jyComboardSealBoardListLimit:3}")
	@LafUcc
	private Integer jyComboardSealBoardListLimit;

	/**
	 *封车板数选择上限
	 */
	@Value("${duccPropertyConfig.jyComboardSealBoardListSelectLimit:3}")
	@LafUcc
	private Integer jyComboardSealBoardListSelectLimit;

	/**
	 *组板封车岗查询待封车板时间限制
	 */
	@Value("${duccPropertyConfig.jyComboardSealQueryBoardListTime:'2'}")
	@LafUcc
	private String jyComboardSealQueryBoardListTime;

	/**
	 *组板岗场地笼车查询分页参数
	 */
	@Value("${duccPropertyConfig.jyComboardSiteCTTPageSize:5000}")
	@LafUcc
	private Integer jyComboardSiteCTTPageSize;

	@Value("${duccPropertyConfig.jyComboardTaskCreateTimeBeginDay:7}")
	@LafUcc
	private Integer jyComboardTaskCreateTimeBeginDay;

	@Value("${duccPropertyConfig.jyComboardTaskSealTimeBeginDay:2}")
	@LafUcc
	private Integer jyComboardTaskSealTimeBeginDay;

	/**
	 *
	 */
	@Value("${duccPropertyConfig.jyCzSendTaskPlanTimeBeginDay:0}")
	@LafUcc
	private Integer jyCzSendTaskPlanTimeBeginDay;

	@Value("${duccPropertyConfig.jyCzSendTaskPlanTimeEndDay:1}")
	@LafUcc
	private Integer jyCzSendTaskPlanTimeEndDay;

	@Value("${duccPropertyConfig.jyStrandScanNumLimit:200}")
	@LafUcc
	private Integer jyStrandScanNumLimit;

	@Value("${duccPropertyConfig.jySysStrandTaskCloseTime:14400000}")
	@LafUcc
	private Long jySysStrandTaskCloseTime;

	/**
	 *发货运输车辆靠台验证码允许访问上几次验证码个数
	 */
	@Value("${duccPropertyConfig.jyTransportSendVehicleValidateDockAllowRefreshTimes:2}")
	@LafUcc
	private Integer jyTransportSendVehicleValidateDockAllowRefreshTimes;

	/**
	 *发货运输车辆靠台验证刷新间隔
	 */
	@Value("${duccPropertyConfig.jyTransportSendVehicleValidateDockRefreshTime:10}")
	@LafUcc
	private Integer jyTransportSendVehicleValidateDockRefreshTime;

	@Value("${duccPropertyConfig.jyUnSealTaskLastHourTime:6}")
	@LafUcc
	private Long jyUnSealTaskLastHourTime;

	@Value("${duccPropertyConfig.jyUnloadCarListDoneQueryDayFilter:5}")
	@LafUcc
	private Integer jyUnloadCarListDoneQueryDayFilter;

	@Value("${duccPropertyConfig.jyWorkAppAutoRefreshConfig:''}")
	@LafUcc
	private String jyWorkAppAutoRefreshConfig;

	private List<ClientAutoRefreshConfig> jyWorkAppAutoRefreshConfigList = new ArrayList<>();

	@Value("${duccPropertyConfig.loadCarEvaluateSwitch:true}")
	@LafUcc
	private boolean loadCarEvaluateSwitch;

	/**
	 *
	 */
	@Value("${duccPropertyConfig.loadProgressByVehicleVolume:false}")
	@LafUcc
	private boolean loadProgressByVehicleVolume;

	/**
	 *设备两次合格间隔时间（用于抽检下发校验，默认8h，毫秒）
	 */
	@Value("${duccPropertyConfig.machineCalibrateIntervalTimeOfSpotCheck:28800000}")
	@LafUcc
	private Long machineCalibrateIntervalTimeOfSpotCheck;

	/**
	 *dws设备校完成后更新非超标抽检记录设备状态开关
	 */
	@Value("${duccPropertyConfig.machineCalibrateSpotCheckSwitch:true}")
	@LafUcc
	private boolean machineCalibrateSpotCheckSwitch;

	/**
	 *设备校准任务时长，默认8h（毫秒）
	 */
	@Value("${duccPropertyConfig.machineCalibrateTaskDuration:10800000}")
	@LafUcc
	private Long machineCalibrateTaskDuration;

	/**
	 *设备任务强制创建的间隔时间，默认2h（毫秒）
	 */
	@Value("${duccPropertyConfig.machineCalibrateTaskForceCreateIntervalTime:720000}")
	@LafUcc
	private Long machineCalibrateTaskForceCreateIntervalTime;

	@Value("${duccPropertyConfig.machineCalibrateTaskQueryRange:172800000}")
	@LafUcc
	private Long machineCalibrateTaskQueryRange;

	@Value("${duccPropertyConfig.needIsolateBoardByGroupCodeSiteList:''}")
	@LafUcc
	private String needIsolateBoardByGroupCodeSiteList;

	@Value("${duccPropertyConfig.needValidateBatchCodeHasSealed:false}")
	@LafUcc
	private boolean needValidateBatchCodeHasSealed;

	@Value("${duccPropertyConfig.offLineAllowedSites:''}")
	@LafUcc
	private String offLineAllowedSites;

	@Value("${duccPropertyConfig.onlineGetTaskSimpleCodeThreshold:50}")
	@LafUcc
	private int onlineGetTaskSimpleCodeThreshold;

	private List<Integer> operateProgressRegionList = new ArrayList<>();

	/**
	 *
	 */
	@Value("${duccPropertyConfig.operateProgressRegions:10}")
	@LafUcc
	private String operateProgressRegions;

	@Value("${duccPropertyConfig.pdaLoginSkipSwitch:false}")
	@LafUcc
	private Boolean pdaLoginSkipSwitch;

	@Value("${duccPropertyConfig.pdaVersionSwitch:true}")
	@LafUcc
	private boolean pdaVersionSwitch;

	/**
	 *是否是所有包裹补打后再取消拦截
	 */
	@Value("${duccPropertyConfig.printCompeteAllPackageUpdateCancel:true}")
	@LafUcc
	private boolean printCompeteAllPackageUpdateCancel;

	/**
	 *是否处理重复运单取消拦截
	 */
	@Value("${duccPropertyConfig.printCompeteUpdateCancel:false}")
	@LafUcc
	private boolean printCompeteUpdateCancel;

	/**
	 *生成装车进度数据消息的开关
	 */
	@Value("${duccPropertyConfig.productOperateProgressSwitch:false}")
	@LafUcc
	private boolean productOperateProgressSwitch;

	@Value("${duccPropertyConfig.reComboardSwitch:true}")
	@LafUcc
	private boolean reComboardSwitch;

	@Value("${duccPropertyConfig.reComboardTimeLimit:3}")
	@LafUcc
	private Long reComboardTimeLimit;

	@Value("${duccPropertyConfig.spotCheckIssueRelyOMachineStatus:true}")
	@LafUcc
	private boolean spotCheckIssueRelyOMachineStatus;

	@Value("${duccPropertyConfig.spotCheckIssueRelyOnMachineStatusSiteSwitch:ALL}")
	@LafUcc
	private String spotCheckIssueRelyOnMachineStatusSiteSwitch;

	/**
	 *
	 */
	@Value("${duccPropertyConfig.supportMutilScan:false}")
	@LafUcc
	private boolean supportMutilScan;

	@Value("${duccPropertyConfig.syncJyCZSealStatusSwitch:false}")
	@LafUcc
	private boolean syncJyCZSealStatusSwitch;

	@Value("${duccPropertyConfig.syncScheduleTaskSwitch:true}")
	@LafUcc
	private boolean syncScheduleTaskSwitch;

	@Value("${duccPropertyConfig.teAnSiteWhitelist:}")
	@LafUcc
	private String teAnSiteWhitelist;

	private List<String> teAnSiteWhitelistStrList = new ArrayList<>();

	/**
	 *集齐服务开关（true: 降级）
	 */
	@Value("${duccPropertyConfig.tysUnloadCarCollectDemoteSwitch:false}")
	@LafUcc
	private Boolean tysUnloadCarCollectDemoteSwitch;

	@Value("${duccPropertyConfig.unloadTaskBoardMaxCount:100}")
	@LafUcc
	private Integer unloadTaskBoardMaxCount;

	@Value("${duccPropertyConfig.uploadOverWeightSwitch:true}")
	@LafUcc
	private boolean uploadOverWeightSwitch;

	/**
	 *车型优先分数默认值
	 */
	@Value("${duccPropertyConfig.vehicleIntegralPriorityFraction:200.0}")
	@LafUcc
	private double vehicleIntegralPriorityFraction;

	@Value("${duccPropertyConfig.volumeExcessIssueSites:910}")
	@LafUcc
	private String volumeExcessIssueSites;

	/**
	 *转运验货拦截包裹号是否在运单系统存在（true:拦截， false不拦截）
	 */
	@Value("${duccPropertyConfig.waybillSysNonExistPackageInterceptSwitch:true}")
	@LafUcc
	private Boolean waybillSysNonExistPackageInterceptSwitch;
	
    /**
     * 操作流水-发送开关
     */
	@Value("${duccPropertyConfig.sendJyOperateFlowMqSwitch:true}")
	@LafUcc	
    private Boolean sendJyOperateFlowMqSwitch;
	
	@Value("${duccPropertyConfig.needValidateMainLineBizSources}")
	@LafUcc	
    private String needValidateMainLineBizSources;
	
    /**
     * 是否使用设备抽检图片AI识别结果
     */
	@Value("${duccPropertyConfig.useEquipmentSpotCheckAIDistinguishResult:false}")
	@LafUcc	
    private boolean useEquipmentSpotCheckAIDistinguishResult;
	
	@Value("${duccPropertyConfig.oldSendForbiddenSwitch:false}")
	@LafUcc
    private boolean oldSendForbiddenSwitch;
    /**
     * 接货仓发货岗查询发货明细表，默认limit最大值
     */
	@Value("${duccPropertyConfig.JyWarehouseSendVehicleDetailQueryDefaultLimitSize:30}")
	@LafUcc	
    private Integer JyWarehouseSendVehicleDetailQueryDefaultLimitSize;
    /**
     * 接货仓发货岗混扫任务下流向数量配置： 当前ucc配置，而且改为PC报表控制
     * ,38:10,,10186:15,,,  配置：38场地支持10个流向， 10186支持15个流向
     */
	@Value("${duccPropertyConfig.JyWarehouseSendVehicleMixScanTaskFlowNumConfig:,38:10,,,10186:15,,,910:20,,,223094:13}")
	@LafUcc		
    private String JyWarehouseSendVehicleMixScanTaskFlowNumConfig;
    /**
     * 拣运扫描没有限制最大数量，取消可能存在巨大值，当取消特大时，需要关注是否为异常
     */
	@Value("${duccPropertyConfig.JyBuQiWaybillCodeMaxSum:50000}")
	@LafUcc	
    private Integer JyBuQiWaybillCodeMaxSum;
    /**
     * 封车前装载率限制
     */
	@Value("${duccPropertyConfig.beforeSealVehicleLoadRateLimit:0.0}")
	@LafUcc	
    private double beforeSealVehicleLoadRateLimit;
	@Value("${duccPropertyConfig.beforeSendVehicleLoadRateLimit:0.0}")
	@LafUcc
    private double beforeSendVehicleLoadRateLimit;
	@Value("${duccPropertyConfig.beforeSealVehicleLoadRateLimitCheckSwitch:false}")
	@LafUcc
    private boolean beforeSealVehicleLoadRateLimitCheckSwitch;
    /**
     * 航空发货封车岗列表查询
     */
	@Value("${duccPropertyConfig.aviationSendSealListNextSiteQueryLimit:}")
	@LafUcc
    private Integer aviationSendSealListNextSiteQueryLimit;
    /**
     * 待派车查询时间范围  单位小时
     */
	@Value("${duccPropertyConfig.fetchCarDistributionTimeRange:48}")
	@LafUcc
    private Integer fetchCarDistributionTimeRange;
    /**
     * 作业APP发货特殊产品类型提示，到指定剩余分钟数才提示
     */
	@Value("${duccPropertyConfig.jySendSpecialProductTypeToScanShowRemainMinutes:30}")
	@LafUcc	
    private Integer jySendSpecialProductTypeToScanShowRemainMinutes;
    /**
     * 抽检数据是否下发给计费
     */
	@Value("${duccPropertyConfig.isIssueToFinance:false}")
	@LafUcc	
    private boolean isIssueToFinance;
    /**
     * 检查是否需要检查周转筐数量和实施数量对比
     */
	@Value("${duccPropertyConfig.checkAkboxConfig:true}")
	@LafUcc	
    private Boolean checkAkboxConfig;
    /**
     * 违禁品运单缓存时长
     */
	@Value("${duccPropertyConfig.contrabandWaybillCacheTime:0}")
	@LafUcc	
    private int contrabandWaybillCacheTime;
	
	@Value("${duccPropertyConfig.printCacheTime:50000}")
	@LafUcc
    private Long printCacheTime;
    /**
     * 组板岗混扫任务查询限制
     */
	@Value("${duccPropertyConfig.cttGroupDataLimit:5}")
	@LafUcc	
    private Integer cttGroupDataLimit;
    /**
     * 是否执行BC融合
     */
    /**
     * 组板岗混扫任务查询限制
     */
	@Value("${duccPropertyConfig.executeBCFuse:false}")
	@LafUcc		
    private boolean executeBCFuse;	
	
	@Value("${duccPropertyConfig.interceptBlackList:*}")
	@LafUcc	
    private String interceptBlackList;
    /**
     * 异常任务生成id开关
     */
	@Value("${duccPropertyConfig.jyExceptionCreateBizIdSwitch:false}")
	@LafUcc	
    private boolean jyExceptionCreateBizIdSwitch;
    /**
     * 异常破损任务客服未反馈时间（小时）
     */
	@Value("${duccPropertyConfig.jyExceptionDamageTaskCustomerNotReturnHours:0}")
	@LafUcc		
    private int jyExceptionDamageTaskCustomerNotReturnHours;
	@Value("${duccPropertyConfig.qingChangDataOpenSwitch:test}")
	@LafUcc	
    private String qingChangDataOpenSwitch;
    
    /**
     * 外单逆向换单次数
     */
	@Value("${duccPropertyConfig.reverseExchangeCount:3}")
	@LafUcc	
    private Integer reverseExchangeCount;
    /**
     * 终端站点-包裹补打限制开关
     */
	@Value("${duccPropertyConfig.terminalSitePackagePrintLimitSwitch:false}")
	@LafUcc	
    private boolean terminalSitePackagePrintLimitSwitch;
    
	public boolean isUseDucc() {
		return useDucc;
	}

	public void setUseDucc(boolean useDucc) {
		log.info("DuccPropertyConfig.setUseDucc");
		this.useDucc = useDucc;
	}

   public String getConfigFrom() {
		return configFrom;
	}

	public void setConfigFrom(String configFrom) {
		this.configFrom = configFrom;
	}

	public boolean isCheckConfig() {
		return checkConfig;
	}

	public void setCheckConfig(boolean checkConfig) {
		this.checkConfig = checkConfig;
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
    	this.dpSiteCodes=dpSiteCodes;
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

    public String getIdentityRecogniseSiteSwitch() {
        return identityRecogniseSiteSwitch;
    }

    public void setIdentityRecogniseSiteSwitch(String identityRecogniseSiteSwitch) {
        this.identityRecogniseSiteSwitch = identityRecogniseSiteSwitch;
        this.identityRecogniseSiteSwitchList = StringUtils.isNotEmpty(identityRecogniseSiteSwitch)?
                Arrays.asList(identityRecogniseSiteSwitch.split(Constants.SEPARATOR_COMMA).clone())
                : Collections.singletonList("0");
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

	public String getAddiOwnNumberConf() {
		return addiOwnNumberConf;
	}

	public void setAddiOwnNumberConf(String addiOwnNumberConf) {
		this.addiOwnNumberConf = addiOwnNumberConf;
	}

	public String getBusinessLogQueryPageSwitch() {
		return businessLogQueryPageSwitch;
	}

	public void setBusinessLogQueryPageSwitch(String businessLogQueryPageSwitch) {
		this.businessLogQueryPageSwitch = businessLogQueryPageSwitch;
	}

	public boolean isCheckSignAndReturn() {
		return checkSignAndReturn;
	}

	public void setCheckSignAndReturn(boolean checkSignAndReturn) {
		this.checkSignAndReturn = checkSignAndReturn;
	}

	public boolean isChuguanPurchaseAndSaleSwitch() {
		return chuguanPurchaseAndSaleSwitch;
	}

	public void setChuguanPurchaseAndSaleSwitch(boolean chuguanPurchaseAndSaleSwitch) {
		this.chuguanPurchaseAndSaleSwitch = chuguanPurchaseAndSaleSwitch;
	}

	public Integer getCreateSendTaskExecuteCount() {
		return createSendTaskExecuteCount;
	}

	public void setCreateSendTaskExecuteCount(Integer createSendTaskExecuteCount) {
		this.createSendTaskExecuteCount = createSendTaskExecuteCount;
	}

	public Integer getCreateSendTasktimeOut() {
		return createSendTasktimeOut;
	}

	public void setCreateSendTasktimeOut(Integer createSendTasktimeOut) {
		this.createSendTasktimeOut = createSendTasktimeOut;
	}

	public String getDeliverySendAsyncSite() {
		return deliverySendAsyncSite;
	}

	public void setDeliverySendAsyncSite(String deliverySendAsyncSite) {
		this.deliverySendAsyncSite = deliverySendAsyncSite;
	}

	public int getDeliverySendTaskSleepMills() {
		return deliverySendTaskSleepMills;
	}

	public void setDeliverySendTaskSleepMills(int deliverySendTaskSleepMills) {
		this.deliverySendTaskSleepMills = deliverySendTaskSleepMills;
	}

	public boolean isEnableGoodsAreaOfTysScan() {
		return enableGoodsAreaOfTysScan;
	}

	public void setEnableGoodsAreaOfTysScan(boolean enableGoodsAreaOfTysScan) {
		this.enableGoodsAreaOfTysScan = enableGoodsAreaOfTysScan;
	}

	public boolean isFilterSendCodeSwitch() {
		return filterSendCodeSwitch;
	}

	public void setFilterSendCodeSwitch(boolean filterSendCodeSwitch) {
		this.filterSendCodeSwitch = filterSendCodeSwitch;
	}

	public List<String> getNeedInterceptUrlList() {
		return needInterceptUrlList;
	}

	public void setNeedInterceptUrlList(List<String> needInterceptUrlList) {
		this.needInterceptUrlList = needInterceptUrlList;
	}

	public String getNeedInterceptUrls() {
		return needInterceptUrls;
	}

	public void setNeedInterceptUrls(String needInterceptUrls) {
		this.needInterceptUrls = needInterceptUrls;
        if (needInterceptUrls!=null && !"".equals(needInterceptUrls)){
            List<String> urlList=new ArrayList<>();
            if (needInterceptUrls.contains(",")){
                urlList = Arrays.asList(needInterceptUrls.split(","));
            }
            else {
                urlList.add(needInterceptUrls);
            }
            this.needInterceptUrlList =urlList;
        }		
	}

	public Integer getOldSendSplitPageSize() {
		return oldSendSplitPageSize;
	}

	public void setOldSendSplitPageSize(Integer oldSendSplitPageSize) {
		this.oldSendSplitPageSize = oldSendSplitPageSize;
	}

	public boolean isRestApiOuthSwitch() {
		return restApiOuthSwitch;
	}

	public void setRestApiOuthSwitch(boolean restApiOuthSwitch) {
		this.restApiOuthSwitch = restApiOuthSwitch;
	}

	public int getSealStatusBatchSizeLimit() {
		return sealStatusBatchSizeLimit;
	}

	public void setSealStatusBatchSizeLimit(int sealStatusBatchSizeLimit) {
		this.sealStatusBatchSizeLimit = sealStatusBatchSizeLimit;
	}

	public int getSealTaskForceFallback() {
		return sealTaskForceFallback;
	}

	public void setSealTaskForceFallback(int sealTaskForceFallback) {
		this.sealTaskForceFallback = sealTaskForceFallback;
	}

	public String getSealTaskHystrixProps() {
		return sealTaskHystrixProps;
	}

	public void setSealTaskHystrixProps(String sealTaskHystrixProps) {
		this.sealTaskHystrixProps = sealTaskHystrixProps;
	}

	public boolean isSyncJySealStatusSwitch() {
		return syncJySealStatusSwitch;
	}

	public void setSyncJySealStatusSwitch(boolean syncJySealStatusSwitch) {
		this.syncJySealStatusSwitch = syncJySealStatusSwitch;
	}

	public String getAggsDataSource() {
		return aggsDataSource;
	}

	public void setAggsDataSource(String aggsDataSource) {
		this.aggsDataSource = aggsDataSource;
	}

	public boolean isAllianceBusinessSwitch() {
		return allianceBusinessSwitch;
	}

	public void setAllianceBusinessSwitch(boolean allianceBusinessSwitch) {
		this.allianceBusinessSwitch = allianceBusinessSwitch;
	}

	public int getAssignExpTaskQuantityLimit() {
		return assignExpTaskQuantityLimit;
	}

	public void setAssignExpTaskQuantityLimit(int assignExpTaskQuantityLimit) {
		this.assignExpTaskQuantityLimit = assignExpTaskQuantityLimit;
	}

	public String getAutoCloseJyBizTaskConfig() {
		return autoCloseJyBizTaskConfig;
	}

    public void setAutoCloseJyBizTaskConfig(String autoCloseJyBizTaskConfig) {
        this.autoCloseJyBizTaskConfig = autoCloseJyBizTaskConfig;
        if(StringUtils.isNotBlank(this.autoCloseJyBizTaskConfig)){
            autoCloseJyBizTaskConfigObj = JsonHelper.fromJson(autoCloseJyBizTaskConfig, AutoCloseJyBizTaskConfig.class);
        }
    }

	public AutoCloseJyBizTaskConfig getAutoCloseJyBizTaskConfigObj() {
        AutoCloseJyBizTaskConfig config = new AutoCloseJyBizTaskConfig();
        BeanUtils.copyProperties(autoCloseJyBizTaskConfigObj, config);
        return config;
	}

	public void setAutoCloseJyBizTaskConfigObj(AutoCloseJyBizTaskConfig autoCloseJyBizTaskConfigObj) {
		this.autoCloseJyBizTaskConfigObj = autoCloseJyBizTaskConfigObj;
	}

	public String getAutoPackageSendInspectionSiteCodes() {
		return autoPackageSendInspectionSiteCodes;
	}

	public void setAutoPackageSendInspectionSiteCodes(String autoPackageSendInspectionSiteCodes) {
		this.autoPackageSendInspectionSiteCodes = autoPackageSendInspectionSiteCodes;
	}

	public Integer getBatchGenerateSendCodeMaxNum() {
		return batchGenerateSendCodeMaxNum;
	}

	public void setBatchGenerateSendCodeMaxNum(Integer batchGenerateSendCodeMaxNum) {
		this.batchGenerateSendCodeMaxNum = batchGenerateSendCodeMaxNum;
	}

	public int getBatchQueryEndSiteLimit() {
		return batchQueryEndSiteLimit;
	}

	public void setBatchQueryEndSiteLimit(int batchQueryEndSiteLimit) {
		this.batchQueryEndSiteLimit = batchQueryEndSiteLimit;
	}

	public boolean isBatchSendForbiddenSwitch() {
		return batchSendForbiddenSwitch;
	}

	public void setBatchSendForbiddenSwitch(boolean batchSendForbiddenSwitch) {
		this.batchSendForbiddenSwitch = batchSendForbiddenSwitch;
	}

	public boolean isBoardCombinationRouterSwitch() {
		return boardCombinationRouterSwitch;
	}

	public void setBoardCombinationRouterSwitch(boolean boardCombinationRouterSwitch) {
		this.boardCombinationRouterSwitch = boardCombinationRouterSwitch;
	}

	public Boolean getBoardListQuerySwitch() {
		return boardListQuerySwitch;
	}

	public void setBoardListQuerySwitch(Boolean boardListQuerySwitch) {
		this.boardListQuerySwitch = boardListQuerySwitch;
	}

	public Integer getBulkScanPackageMinCount() {
		return bulkScanPackageMinCount;
	}

	public void setBulkScanPackageMinCount(Integer bulkScanPackageMinCount) {
		this.bulkScanPackageMinCount = bulkScanPackageMinCount;
	}

	public boolean isCheckTeAnSwitch() {
		return checkTeAnSwitch;
	}

	public void setCheckTeAnSwitch(boolean checkTeAnSwitch) {
		this.checkTeAnSwitch = checkTeAnSwitch;
	}

	public boolean isCloudOssInsertSwitch() {
		return cloudOssInsertSwitch;
	}

	public void setCloudOssInsertSwitch(boolean cloudOssInsertSwitch) {
		this.cloudOssInsertSwitch = cloudOssInsertSwitch;
	}

	public int getCompleteExpDayNumLimit() {
		return completeExpDayNumLimit;
	}

	public void setCompleteExpDayNumLimit(int completeExpDayNumLimit) {
		this.completeExpDayNumLimit = completeExpDayNumLimit;
	}

	public boolean isCreateBoardBySendFlowSwitch() {
		return createBoardBySendFlowSwitch;
	}

	public void setCreateBoardBySendFlowSwitch(boolean createBoardBySendFlowSwitch) {
		this.createBoardBySendFlowSwitch = createBoardBySendFlowSwitch;
	}

	public Integer getCttGroupSendFLowLimit() {
		return cttGroupSendFLowLimit;
	}

	public void setCttGroupSendFLowLimit(Integer cttGroupSendFLowLimit) {
		this.cttGroupSendFLowLimit = cttGroupSendFLowLimit;
	}

	public String getCzOrgForbiddenList() {
		return czOrgForbiddenList;
	}

	public void setCzOrgForbiddenList(String czOrgForbiddenList) {
		this.czOrgForbiddenList = czOrgForbiddenList;
	}

	public boolean isCzQuerySwitch() {
		return czQuerySwitch;
	}

	public void setCzQuerySwitch(boolean czQuerySwitch) {
		this.czQuerySwitch = czQuerySwitch;
	}

	public String getCzSiteForbiddenList() {
		return czSiteForbiddenList;
	}

	public void setCzSiteForbiddenList(String czSiteForbiddenList) {
		this.czSiteForbiddenList = czSiteForbiddenList;
	}

	public String getCzSiteTypeForbiddenList() {
		return czSiteTypeForbiddenList;
	}

	public void setCzSiteTypeForbiddenList(String czSiteTypeForbiddenList) {
		this.czSiteTypeForbiddenList = czSiteTypeForbiddenList;
	}

	public List<String> getDewuCustomerCodeList() {
		return dewuCustomerCodeList;
	}

	public void setDewuCustomerCodeList(List<String> dewuCustomerCodeList) {
		this.dewuCustomerCodeList = dewuCustomerCodeList;
	}

	public String getDewuCustomerCodes() {
		return dewuCustomerCodes;
	}

	public void setDewuCustomerCodes(String dewuCustomerCodes) {
		this.dewuCustomerCodes = dewuCustomerCodes;
        if(StringUtils.isNotBlank(dewuCustomerCodes)){
            this.dewuCustomerCodeList = Arrays.asList(dewuCustomerCodes.split(Constants.SEPARATOR_COMMA));
        }
	}

	public String getDpSpringSiteCode() {
		return dpSpringSiteCode;
	}

	public void setDpSpringSiteCode(String dpSpringSiteCode) {
		this.dpSpringSiteCode = dpSpringSiteCode;
        List<String> dpSpringSiteCodeList = new ArrayList<>();
        if(StringUtils.isNotBlank(dpSpringSiteCode)){
            final String[] split = dpSpringSiteCode.split(Constants.SEPARATOR_COMMA);
            dpSpringSiteCodeList = Arrays.asList(split);
        }
        for (String siteCodeStr : dpSpringSiteCodeList) {
            this.dpSpringSiteCodeList.add(Integer.valueOf(siteCodeStr));
        }		
		
	}

	public List<Integer> getDpSpringSiteCodeList() {
		return dpSpringSiteCodeList;
	}

	public void setDpSpringSiteCodeList(List<Integer> dpSpringSiteCodeList) {
		this.dpSpringSiteCodeList = dpSpringSiteCodeList;
	}

	public boolean isDpTransferSwitch() {
		return dpTransferSwitch;
	}

	public void setDpTransferSwitch(boolean dpTransferSwitch) {
		this.dpTransferSwitch = dpTransferSwitch;
	}

	public boolean isEasyFreezeSwitch() {
		return easyFreezeSwitch;
	}

	public void setEasyFreezeSwitch(boolean easyFreezeSwitch) {
		this.easyFreezeSwitch = easyFreezeSwitch;
	}

	public String getExScrapApproveLevelCountLimit() {
		return exScrapApproveLevelCountLimit;
	}

	public void setExScrapApproveLevelCountLimit(String exScrapApproveLevelCountLimit) {
		this.exScrapApproveLevelCountLimit = exScrapApproveLevelCountLimit;
	}

	public List<String> getExceptionSubmitCheckSiteList() {
		return exceptionSubmitCheckSiteList;
	}

	public void setExceptionSubmitCheckSiteList(List<String> exceptionSubmitCheckSiteList) {
		this.exceptionSubmitCheckSiteList = exceptionSubmitCheckSiteList;
	}

	public String getExceptionSubmitCheckSites() {
		return exceptionSubmitCheckSites;
	}

	public void setExceptionSubmitCheckSites(String exceptionSubmitCheckSites) {
		this.exceptionSubmitCheckSites = exceptionSubmitCheckSites;
        if(exceptionSubmitCheckSites == null){
            exceptionSubmitCheckSiteList = new ArrayList<>();
            return;
        }
        exceptionSubmitCheckSiteList = Arrays.asList(exceptionSubmitCheckSites.split(Constants.SEPARATOR_COMMA));		
	}

	public List<String> getExceptionSubmitCheckWaybillInterceptTypeList() {
		return exceptionSubmitCheckWaybillInterceptTypeList;
	}

	public void setExceptionSubmitCheckWaybillInterceptTypeList(List<String> exceptionSubmitCheckWaybillInterceptTypeList) {
		this.exceptionSubmitCheckWaybillInterceptTypeList = exceptionSubmitCheckWaybillInterceptTypeList;
	}

	public String getExceptionSubmitCheckWaybillInterceptTypes() {
		return exceptionSubmitCheckWaybillInterceptTypes;
	}

	public void setExceptionSubmitCheckWaybillInterceptTypes(String exceptionSubmitCheckWaybillInterceptTypes) {
		this.exceptionSubmitCheckWaybillInterceptTypes = exceptionSubmitCheckWaybillInterceptTypes;
        if(exceptionSubmitCheckWaybillInterceptTypes == null){
            exceptionSubmitCheckWaybillInterceptTypeList = new ArrayList<>();
            return;
        }
        exceptionSubmitCheckWaybillInterceptTypeList = Arrays.asList(exceptionSubmitCheckWaybillInterceptTypes.split(Constants.SEPARATOR_COMMA));		
	}

	public String getForceSendSiteList() {
		return forceSendSiteList;
	}

	public void setForceSendSiteList(String forceSendSiteList) {
		this.forceSendSiteList = forceSendSiteList;
	}

	public boolean isIgnoreTysTrackSwitch() {
		return ignoreTysTrackSwitch;
	}

	public void setIgnoreTysTrackSwitch(boolean ignoreTysTrackSwitch) {
		this.ignoreTysTrackSwitch = ignoreTysTrackSwitch;
	}

	public boolean isJobTypeLimitSwitch() {
		return jobTypeLimitSwitch;
	}

	public void setJobTypeLimitSwitch(boolean jobTypeLimitSwitch) {
		this.jobTypeLimitSwitch = jobTypeLimitSwitch;
	}

	public Long getJyArtificialStrandTaskCloseTime() {
		return jyArtificialStrandTaskCloseTime;
	}

	public void setJyArtificialStrandTaskCloseTime(Long jyArtificialStrandTaskCloseTime) {
		this.jyArtificialStrandTaskCloseTime = jyArtificialStrandTaskCloseTime;
	}

	public String getJyCollectSiteWhitelist() {
		return jyCollectSiteWhitelist;
	}

	public void setJyCollectSiteWhitelist(String jyCollectSiteWhitelist) {
		this.jyCollectSiteWhitelist = jyCollectSiteWhitelist;
	}

	public Integer getJyComboardCountLimit() {
		return jyComboardCountLimit;
	}

	public void setJyComboardCountLimit(Integer jyComboardCountLimit) {
		this.jyComboardCountLimit = jyComboardCountLimit;
	}

	public Boolean getJyComboardListBoardSqlSwitch() {
		return jyComboardListBoardSqlSwitch;
	}

	public void setJyComboardListBoardSqlSwitch(Boolean jyComboardListBoardSqlSwitch) {
		this.jyComboardListBoardSqlSwitch = jyComboardListBoardSqlSwitch;
	}

	public Integer getJyComboardRefreshTimerInterval() {
		return jyComboardRefreshTimerInterval;
	}

	public void setJyComboardRefreshTimerInterval(Integer jyComboardRefreshTimerInterval) {
		this.jyComboardRefreshTimerInterval = jyComboardRefreshTimerInterval;
	}

	public Integer getJyComboardScanUserBeginDay() {
		return jyComboardScanUserBeginDay;
	}

	public void setJyComboardScanUserBeginDay(Integer jyComboardScanUserBeginDay) {
		this.jyComboardScanUserBeginDay = jyComboardScanUserBeginDay;
	}

	public Integer getJyComboardSealBoardListLimit() {
		return jyComboardSealBoardListLimit;
	}

	public void setJyComboardSealBoardListLimit(Integer jyComboardSealBoardListLimit) {
		this.jyComboardSealBoardListLimit = jyComboardSealBoardListLimit;
	}

	public Integer getJyComboardSealBoardListSelectLimit() {
		return jyComboardSealBoardListSelectLimit;
	}

	public void setJyComboardSealBoardListSelectLimit(Integer jyComboardSealBoardListSelectLimit) {
		this.jyComboardSealBoardListSelectLimit = jyComboardSealBoardListSelectLimit;
	}

	public String getJyComboardSealQueryBoardListTime() {
		return jyComboardSealQueryBoardListTime;
	}

	public void setJyComboardSealQueryBoardListTime(String jyComboardSealQueryBoardListTime) {
		this.jyComboardSealQueryBoardListTime = jyComboardSealQueryBoardListTime;
	}

	public Integer getJyComboardSiteCTTPageSize() {
		return jyComboardSiteCTTPageSize;
	}

	public void setJyComboardSiteCTTPageSize(Integer jyComboardSiteCTTPageSize) {
		this.jyComboardSiteCTTPageSize = jyComboardSiteCTTPageSize;
	}

	public Integer getJyComboardTaskCreateTimeBeginDay() {
		return jyComboardTaskCreateTimeBeginDay;
	}

	public void setJyComboardTaskCreateTimeBeginDay(Integer jyComboardTaskCreateTimeBeginDay) {
		this.jyComboardTaskCreateTimeBeginDay = jyComboardTaskCreateTimeBeginDay;
	}

	public Integer getJyComboardTaskSealTimeBeginDay() {
		return jyComboardTaskSealTimeBeginDay;
	}

	public void setJyComboardTaskSealTimeBeginDay(Integer jyComboardTaskSealTimeBeginDay) {
		this.jyComboardTaskSealTimeBeginDay = jyComboardTaskSealTimeBeginDay;
	}

	public Integer getJyCzSendTaskPlanTimeBeginDay() {
		return jyCzSendTaskPlanTimeBeginDay;
	}

	public void setJyCzSendTaskPlanTimeBeginDay(Integer jyCzSendTaskPlanTimeBeginDay) {
		this.jyCzSendTaskPlanTimeBeginDay = jyCzSendTaskPlanTimeBeginDay;
	}

	public Integer getJyCzSendTaskPlanTimeEndDay() {
		return jyCzSendTaskPlanTimeEndDay;
	}

	public void setJyCzSendTaskPlanTimeEndDay(Integer jyCzSendTaskPlanTimeEndDay) {
		this.jyCzSendTaskPlanTimeEndDay = jyCzSendTaskPlanTimeEndDay;
	}

	public Integer getJyStrandScanNumLimit() {
		return jyStrandScanNumLimit;
	}

	public void setJyStrandScanNumLimit(Integer jyStrandScanNumLimit) {
		this.jyStrandScanNumLimit = jyStrandScanNumLimit;
	}

	public Long getJySysStrandTaskCloseTime() {
		return jySysStrandTaskCloseTime;
	}

	public void setJySysStrandTaskCloseTime(Long jySysStrandTaskCloseTime) {
		this.jySysStrandTaskCloseTime = jySysStrandTaskCloseTime;
	}

	public Integer getJyTransportSendVehicleValidateDockAllowRefreshTimes() {
		return jyTransportSendVehicleValidateDockAllowRefreshTimes;
	}

	public void setJyTransportSendVehicleValidateDockAllowRefreshTimes(
			Integer jyTransportSendVehicleValidateDockAllowRefreshTimes) {
		this.jyTransportSendVehicleValidateDockAllowRefreshTimes = jyTransportSendVehicleValidateDockAllowRefreshTimes;
	}

	public Integer getJyTransportSendVehicleValidateDockRefreshTime() {
		return jyTransportSendVehicleValidateDockRefreshTime;
	}

	public void setJyTransportSendVehicleValidateDockRefreshTime(Integer jyTransportSendVehicleValidateDockRefreshTime) {
		this.jyTransportSendVehicleValidateDockRefreshTime = jyTransportSendVehicleValidateDockRefreshTime;
	}

	public Long getJyUnSealTaskLastHourTime() {
		return jyUnSealTaskLastHourTime;
	}

	public void setJyUnSealTaskLastHourTime(Long jyUnSealTaskLastHourTime) {
		this.jyUnSealTaskLastHourTime = jyUnSealTaskLastHourTime;
	}

	public Integer getJyUnloadCarListDoneQueryDayFilter() {
		return jyUnloadCarListDoneQueryDayFilter;
	}

	public void setJyUnloadCarListDoneQueryDayFilter(Integer jyUnloadCarListDoneQueryDayFilter) {
		this.jyUnloadCarListDoneQueryDayFilter = jyUnloadCarListDoneQueryDayFilter;
	}

	public String getJyWorkAppAutoRefreshConfig() {
		return jyWorkAppAutoRefreshConfig;
	}

	public void setJyWorkAppAutoRefreshConfig(String jyWorkAppAutoRefreshConfig) {
		this.jyWorkAppAutoRefreshConfig = jyWorkAppAutoRefreshConfig;
        if(StringUtils.isNotEmpty(jyWorkAppAutoRefreshConfig)){
            final List<ClientAutoRefreshConfig> clientAutoRefreshConfigList = JsonHelper.jsonToList(jyWorkAppAutoRefreshConfig, ClientAutoRefreshConfig.class);
            if (CollectionUtils.isNotEmpty(clientAutoRefreshConfigList)) {
                jyWorkAppAutoRefreshConfigList = clientAutoRefreshConfigList;
            }
        }		
	}

	public List<ClientAutoRefreshConfig> getJyWorkAppAutoRefreshConfigList() {
        return Lists.newArrayList(jyWorkAppAutoRefreshConfigList);
	}

	public void setJyWorkAppAutoRefreshConfigList(List<ClientAutoRefreshConfig> jyWorkAppAutoRefreshConfigList) {
		this.jyWorkAppAutoRefreshConfigList = jyWorkAppAutoRefreshConfigList;
	}

	public boolean isLoadCarEvaluateSwitch() {
		return loadCarEvaluateSwitch;
	}

	public void setLoadCarEvaluateSwitch(boolean loadCarEvaluateSwitch) {
		this.loadCarEvaluateSwitch = loadCarEvaluateSwitch;
	}

	public boolean isLoadProgressByVehicleVolume() {
		return loadProgressByVehicleVolume;
	}

	public void setLoadProgressByVehicleVolume(boolean loadProgressByVehicleVolume) {
		this.loadProgressByVehicleVolume = loadProgressByVehicleVolume;
	}

	public Long getMachineCalibrateIntervalTimeOfSpotCheck() {
		return machineCalibrateIntervalTimeOfSpotCheck;
	}

	public void setMachineCalibrateIntervalTimeOfSpotCheck(Long machineCalibrateIntervalTimeOfSpotCheck) {
		this.machineCalibrateIntervalTimeOfSpotCheck = machineCalibrateIntervalTimeOfSpotCheck;
	}

	public boolean isMachineCalibrateSpotCheckSwitch() {
		return machineCalibrateSpotCheckSwitch;
	}

	public void setMachineCalibrateSpotCheckSwitch(boolean machineCalibrateSpotCheckSwitch) {
		this.machineCalibrateSpotCheckSwitch = machineCalibrateSpotCheckSwitch;
	}

	public Long getMachineCalibrateTaskDuration() {
		return machineCalibrateTaskDuration;
	}

	public void setMachineCalibrateTaskDuration(Long machineCalibrateTaskDuration) {
		this.machineCalibrateTaskDuration = machineCalibrateTaskDuration;
	}

	public Long getMachineCalibrateTaskForceCreateIntervalTime() {
		return machineCalibrateTaskForceCreateIntervalTime;
	}

	public void setMachineCalibrateTaskForceCreateIntervalTime(Long machineCalibrateTaskForceCreateIntervalTime) {
		this.machineCalibrateTaskForceCreateIntervalTime = machineCalibrateTaskForceCreateIntervalTime;
	}

	public Long getMachineCalibrateTaskQueryRange() {
		return machineCalibrateTaskQueryRange;
	}

	public void setMachineCalibrateTaskQueryRange(Long machineCalibrateTaskQueryRange) {
		this.machineCalibrateTaskQueryRange = machineCalibrateTaskQueryRange;
	}

	public String getNeedIsolateBoardByGroupCodeSiteList() {
		return needIsolateBoardByGroupCodeSiteList;
	}

	public void setNeedIsolateBoardByGroupCodeSiteList(String needIsolateBoardByGroupCodeSiteList) {
		this.needIsolateBoardByGroupCodeSiteList = needIsolateBoardByGroupCodeSiteList;
	}

	public boolean isNeedValidateBatchCodeHasSealed() {
		return needValidateBatchCodeHasSealed;
	}

	public void setNeedValidateBatchCodeHasSealed(boolean needValidateBatchCodeHasSealed) {
		this.needValidateBatchCodeHasSealed = needValidateBatchCodeHasSealed;
	}

	public String getOffLineAllowedSites() {
		return offLineAllowedSites;
	}

	public void setOffLineAllowedSites(String offLineAllowedSites) {
		this.offLineAllowedSites = offLineAllowedSites;
	}

	public int getOnlineGetTaskSimpleCodeThreshold() {
		return onlineGetTaskSimpleCodeThreshold;
	}

	public void setOnlineGetTaskSimpleCodeThreshold(int onlineGetTaskSimpleCodeThreshold) {
		this.onlineGetTaskSimpleCodeThreshold = onlineGetTaskSimpleCodeThreshold;
	}

	public List<Integer> getOperateProgressRegionList() {
		return operateProgressRegionList;
	}

	public void setOperateProgressRegionList(List<Integer> operateProgressRegionList) {
		this.operateProgressRegionList = operateProgressRegionList;
	}

	public String getOperateProgressRegions() {
		return operateProgressRegions;
	}

	public void setOperateProgressRegions(String operateProgressRegions) {
		this.operateProgressRegions = operateProgressRegions;
	       if (operateProgressRegions!=null && !"".equals(operateProgressRegions)){
	            List<Integer> operateProgressRegionList = new ArrayList<>();
	            if (operateProgressRegions.contains(",")){
	                String[] regionArr =operateProgressRegions.split(",");
	                for (String region:regionArr){
	                    operateProgressRegionList.add(Integer.valueOf(region));
	                }
	            }
	            else {
	                operateProgressRegionList.add(Integer.valueOf(operateProgressRegions));
	            }
	            if (CollectionUtils.isNotEmpty(operateProgressRegionList)){
	                this.operateProgressRegionList =operateProgressRegionList;
	                Collections.sort(this.operateProgressRegionList);
	            }
	        }		
	}

	public Boolean getPdaLoginSkipSwitch() {
		return pdaLoginSkipSwitch;
	}

	public void setPdaLoginSkipSwitch(Boolean pdaLoginSkipSwitch) {
		this.pdaLoginSkipSwitch = pdaLoginSkipSwitch;
	}

	public boolean isPdaVersionSwitch() {
		return pdaVersionSwitch;
	}

	public void setPdaVersionSwitch(boolean pdaVersionSwitch) {
		this.pdaVersionSwitch = pdaVersionSwitch;
	}

	public boolean isPrintCompeteAllPackageUpdateCancel() {
		return printCompeteAllPackageUpdateCancel;
	}

	public void setPrintCompeteAllPackageUpdateCancel(boolean printCompeteAllPackageUpdateCancel) {
		this.printCompeteAllPackageUpdateCancel = printCompeteAllPackageUpdateCancel;
	}

	public boolean isPrintCompeteUpdateCancel() {
		return printCompeteUpdateCancel;
	}

	public void setPrintCompeteUpdateCancel(boolean printCompeteUpdateCancel) {
		this.printCompeteUpdateCancel = printCompeteUpdateCancel;
	}

	public boolean isProductOperateProgressSwitch() {
		return productOperateProgressSwitch;
	}

	public void setProductOperateProgressSwitch(boolean productOperateProgressSwitch) {
		this.productOperateProgressSwitch = productOperateProgressSwitch;
	}

	public boolean isReComboardSwitch() {
		return reComboardSwitch;
	}

	public void setReComboardSwitch(boolean reComboardSwitch) {
		this.reComboardSwitch = reComboardSwitch;
	}

	public Long getReComboardTimeLimit() {
		return reComboardTimeLimit;
	}

	public void setReComboardTimeLimit(Long reComboardTimeLimit) {
		this.reComboardTimeLimit = reComboardTimeLimit;
	}

	public boolean isSpotCheckIssueRelyOMachineStatus() {
		return spotCheckIssueRelyOMachineStatus;
	}

	public void setSpotCheckIssueRelyOMachineStatus(boolean spotCheckIssueRelyOMachineStatus) {
		this.spotCheckIssueRelyOMachineStatus = spotCheckIssueRelyOMachineStatus;
	}

	public String getSpotCheckIssueRelyOnMachineStatusSiteSwitch() {
		return spotCheckIssueRelyOnMachineStatusSiteSwitch;
	}

	public void setSpotCheckIssueRelyOnMachineStatusSiteSwitch(String spotCheckIssueRelyOnMachineStatusSiteSwitch) {
		this.spotCheckIssueRelyOnMachineStatusSiteSwitch = spotCheckIssueRelyOnMachineStatusSiteSwitch;
	}

	public boolean isSupportMutilScan() {
		return supportMutilScan;
	}

	public void setSupportMutilScan(boolean supportMutilScan) {
		this.supportMutilScan = supportMutilScan;
	}

	public boolean isSyncJyCZSealStatusSwitch() {
		return syncJyCZSealStatusSwitch;
	}

	public void setSyncJyCZSealStatusSwitch(boolean syncJyCZSealStatusSwitch) {
		this.syncJyCZSealStatusSwitch = syncJyCZSealStatusSwitch;
	}

	public boolean isSyncScheduleTaskSwitch() {
		return syncScheduleTaskSwitch;
	}

	public void setSyncScheduleTaskSwitch(boolean syncScheduleTaskSwitch) {
		this.syncScheduleTaskSwitch = syncScheduleTaskSwitch;
	}

	public String getTeAnSiteWhitelist() {
		return teAnSiteWhitelist;
	}

	public void setTeAnSiteWhitelist(String teAnSiteWhitelist) {
		this.teAnSiteWhitelist = teAnSiteWhitelist;
        if(StringUtils.isBlank(teAnSiteWhitelist)){
        	teAnSiteWhitelistStrList = new ArrayList<>();
        }else {
        	teAnSiteWhitelistStrList =  Arrays.asList(teAnSiteWhitelist.split(Constants.SEPARATOR_COMMA));
        }
	}

	public List<String> getTeAnSiteWhitelistStrList() {
		return teAnSiteWhitelistStrList;
	}

	public void setTeAnSiteWhitelistStrList(List<String> teAnSiteWhitelistStrList) {
		this.teAnSiteWhitelistStrList = teAnSiteWhitelistStrList;
	}

	public Boolean getTysUnloadCarCollectDemoteSwitch() {
		return tysUnloadCarCollectDemoteSwitch;
	}

	public void setTysUnloadCarCollectDemoteSwitch(Boolean tysUnloadCarCollectDemoteSwitch) {
		this.tysUnloadCarCollectDemoteSwitch = tysUnloadCarCollectDemoteSwitch;
	}

	public Integer getUnloadTaskBoardMaxCount() {
		return unloadTaskBoardMaxCount;
	}

	public void setUnloadTaskBoardMaxCount(Integer unloadTaskBoardMaxCount) {
		this.unloadTaskBoardMaxCount = unloadTaskBoardMaxCount;
	}

	public boolean isUploadOverWeightSwitch() {
		return uploadOverWeightSwitch;
	}

	public void setUploadOverWeightSwitch(boolean uploadOverWeightSwitch) {
		this.uploadOverWeightSwitch = uploadOverWeightSwitch;
	}

	public double getVehicleIntegralPriorityFraction() {
		return vehicleIntegralPriorityFraction;
	}

	public void setVehicleIntegralPriorityFraction(double vehicleIntegralPriorityFraction) {
		this.vehicleIntegralPriorityFraction = vehicleIntegralPriorityFraction;
	}

	public String getVolumeExcessIssueSites() {
		return volumeExcessIssueSites;
	}

	public void setVolumeExcessIssueSites(String volumeExcessIssueSites) {
		this.volumeExcessIssueSites = volumeExcessIssueSites;
	}

	public Boolean getWaybillSysNonExistPackageInterceptSwitch() {
		return waybillSysNonExistPackageInterceptSwitch;
	}

	public void setWaybillSysNonExistPackageInterceptSwitch(Boolean waybillSysNonExistPackageInterceptSwitch) {
		this.waybillSysNonExistPackageInterceptSwitch = waybillSysNonExistPackageInterceptSwitch;
	}

	public void setHideSpecialStartSitePrintReplaceSymbolMaxLength(int hideSpecialStartSitePrintReplaceSymbolMaxLength) {
		this.hideSpecialStartSitePrintReplaceSymbolMaxLength = hideSpecialStartSitePrintReplaceSymbolMaxLength;
	}

	public List<String> getIdentityRecogniseSiteSwitchList() {
		return identityRecogniseSiteSwitchList;
	}

	public void setIdentityRecogniseSiteSwitchList(List<String> identityRecogniseSiteSwitchList) {
		this.identityRecogniseSiteSwitchList = identityRecogniseSiteSwitchList;
	}

	public Boolean getSendJyOperateFlowMqSwitch() {
		return sendJyOperateFlowMqSwitch;
	}

	public void setSendJyOperateFlowMqSwitch(Boolean sendJyOperateFlowMqSwitch) {
		this.sendJyOperateFlowMqSwitch = sendJyOperateFlowMqSwitch;
	}

	public String getNeedValidateMainLineBizSources() {
		return needValidateMainLineBizSources;
	}

	public void setNeedValidateMainLineBizSources(String needValidateMainLineBizSources) {
		this.needValidateMainLineBizSources = needValidateMainLineBizSources;
	}

	public boolean isUseEquipmentSpotCheckAIDistinguishResult() {
		return useEquipmentSpotCheckAIDistinguishResult;
	}

	public void setUseEquipmentSpotCheckAIDistinguishResult(boolean useEquipmentSpotCheckAIDistinguishResult) {
		this.useEquipmentSpotCheckAIDistinguishResult = useEquipmentSpotCheckAIDistinguishResult;
	}

	public boolean isOldSendForbiddenSwitch() {
		return oldSendForbiddenSwitch;
	}

	public void setOldSendForbiddenSwitch(boolean oldSendForbiddenSwitch) {
		this.oldSendForbiddenSwitch = oldSendForbiddenSwitch;
	}

	public Integer getJyWarehouseSendVehicleDetailQueryDefaultLimitSize() {
		return JyWarehouseSendVehicleDetailQueryDefaultLimitSize;
	}

	public void setJyWarehouseSendVehicleDetailQueryDefaultLimitSize(
			Integer jyWarehouseSendVehicleDetailQueryDefaultLimitSize) {
		JyWarehouseSendVehicleDetailQueryDefaultLimitSize = jyWarehouseSendVehicleDetailQueryDefaultLimitSize;
	}

	public String getJyWarehouseSendVehicleMixScanTaskFlowNumConfig() {
		return JyWarehouseSendVehicleMixScanTaskFlowNumConfig;
	}

	public void setJyWarehouseSendVehicleMixScanTaskFlowNumConfig(String jyWarehouseSendVehicleMixScanTaskFlowNumConfig) {
		JyWarehouseSendVehicleMixScanTaskFlowNumConfig = jyWarehouseSendVehicleMixScanTaskFlowNumConfig;
	}

	public Integer getJyBuQiWaybillCodeMaxSum() {
		return JyBuQiWaybillCodeMaxSum;
	}

	public void setJyBuQiWaybillCodeMaxSum(Integer jyBuQiWaybillCodeMaxSum) {
		JyBuQiWaybillCodeMaxSum = jyBuQiWaybillCodeMaxSum;
	}

	public double getBeforeSealVehicleLoadRateLimit() {
		return beforeSealVehicleLoadRateLimit;
	}

	public void setBeforeSealVehicleLoadRateLimit(double beforeSealVehicleLoadRateLimit) {
		this.beforeSealVehicleLoadRateLimit = beforeSealVehicleLoadRateLimit;
	}

	public double getBeforeSendVehicleLoadRateLimit() {
		return beforeSendVehicleLoadRateLimit;
	}

	public void setBeforeSendVehicleLoadRateLimit(double beforeSendVehicleLoadRateLimit) {
		this.beforeSendVehicleLoadRateLimit = beforeSendVehicleLoadRateLimit;
	}

	public boolean isBeforeSealVehicleLoadRateLimitCheckSwitch() {
		return beforeSealVehicleLoadRateLimitCheckSwitch;
	}

	public void setBeforeSealVehicleLoadRateLimitCheckSwitch(boolean beforeSealVehicleLoadRateLimitCheckSwitch) {
		this.beforeSealVehicleLoadRateLimitCheckSwitch = beforeSealVehicleLoadRateLimitCheckSwitch;
	}

	public Integer getAviationSendSealListNextSiteQueryLimit() {
		return aviationSendSealListNextSiteQueryLimit;
	}

	public void setAviationSendSealListNextSiteQueryLimit(Integer aviationSendSealListNextSiteQueryLimit) {
		this.aviationSendSealListNextSiteQueryLimit = aviationSendSealListNextSiteQueryLimit;
	}

	public Integer getFetchCarDistributionTimeRange() {
		return fetchCarDistributionTimeRange;
	}

	public void setFetchCarDistributionTimeRange(Integer fetchCarDistributionTimeRange) {
		this.fetchCarDistributionTimeRange = fetchCarDistributionTimeRange;
	}

	public Integer getJySendSpecialProductTypeToScanShowRemainMinutes() {
		return jySendSpecialProductTypeToScanShowRemainMinutes;
	}

	public void setJySendSpecialProductTypeToScanShowRemainMinutes(
			Integer jySendSpecialProductTypeToScanShowRemainMinutes) {
		this.jySendSpecialProductTypeToScanShowRemainMinutes = jySendSpecialProductTypeToScanShowRemainMinutes;
	}

	public boolean isIssueToFinance() {
		return isIssueToFinance;
	}

	public void setIssueToFinance(boolean isIssueToFinance) {
		this.isIssueToFinance = isIssueToFinance;
	}

	public Boolean getCheckAkboxConfig() {
		return checkAkboxConfig;
	}

	public void setCheckAkboxConfig(Boolean checkAkboxConfig) {
		this.checkAkboxConfig = checkAkboxConfig;
	}

	public int getContrabandWaybillCacheTime() {
		return contrabandWaybillCacheTime;
	}

	public void setContrabandWaybillCacheTime(int contrabandWaybillCacheTime) {
		this.contrabandWaybillCacheTime = contrabandWaybillCacheTime;
	}

	public Long getPrintCacheTime() {
		return printCacheTime;
	}

	public void setPrintCacheTime(Long printCacheTime) {
		this.printCacheTime = printCacheTime;
	}

	public Integer getCttGroupDataLimit() {
		return cttGroupDataLimit;
	}

	public void setCttGroupDataLimit(Integer cttGroupDataLimit) {
		this.cttGroupDataLimit = cttGroupDataLimit;
	}

	public boolean isExecuteBCFuse() {
		return executeBCFuse;
	}

	public void setExecuteBCFuse(boolean executeBCFuse) {
		this.executeBCFuse = executeBCFuse;
	}

	public String getInterceptBlackList() {
		return interceptBlackList;
	}

	public void setInterceptBlackList(String interceptBlackList) {
		this.interceptBlackList = interceptBlackList;
	}

	public boolean isJyExceptionCreateBizIdSwitch() {
		return jyExceptionCreateBizIdSwitch;
	}

	public void setJyExceptionCreateBizIdSwitch(boolean jyExceptionCreateBizIdSwitch) {
		this.jyExceptionCreateBizIdSwitch = jyExceptionCreateBizIdSwitch;
	}

	public int getJyExceptionDamageTaskCustomerNotReturnHours() {
		return jyExceptionDamageTaskCustomerNotReturnHours;
	}

	public void setJyExceptionDamageTaskCustomerNotReturnHours(int jyExceptionDamageTaskCustomerNotReturnHours) {
		this.jyExceptionDamageTaskCustomerNotReturnHours = jyExceptionDamageTaskCustomerNotReturnHours;
	}

	public String getQingChangDataOpenSwitch() {
		return qingChangDataOpenSwitch;
	}

	public void setQingChangDataOpenSwitch(String qingChangDataOpenSwitch) {
		this.qingChangDataOpenSwitch = qingChangDataOpenSwitch;
	}

	public Integer getReverseExchangeCount() {
		return reverseExchangeCount;
	}

	public void setReverseExchangeCount(Integer reverseExchangeCount) {
		this.reverseExchangeCount = reverseExchangeCount;
	}

	public boolean isTerminalSitePackagePrintLimitSwitch() {
		return terminalSitePackagePrintLimitSwitch;
	}

	public void setTerminalSitePackagePrintLimitSwitch(boolean terminalSitePackagePrintLimitSwitch) {
		this.terminalSitePackagePrintLimitSwitch = terminalSitePackagePrintLimitSwitch;
	}

	public static Logger getLog() {
		return log;
	}

	public static String getConfigFromLocal() {
		return CONFIG_FROM_LOCAL;
	}	
}