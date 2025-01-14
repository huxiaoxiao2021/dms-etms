package com.jd.bluedragon.configuration.ucc;

import com.google.common.collect.Lists;
import com.jd.bluedragon.Constants;
import com.jd.bluedragon.common.dto.operation.workbench.config.dto.ClientAutoRefreshConfig;
import com.jd.bluedragon.distribution.jy.service.task.autoclose.dto.AutoCloseJyBizTaskConfig;
import com.jd.bluedragon.utils.ObjectHelper;
import com.jd.ql.dms.print.utils.JsonHelper;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.beans.BeanUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by xumei3 on 2017/12/15.
 *  Note :
 *      - Attention: ucc的属性如果是自定义对象，并且自定义对象内属性包含汉字，则需要再get方法中返回新对象，防止ucc属性值被修改
 */
public class UccPropertyConfiguration{

    /** 开启的多级异步缓冲组件的任务类型列表 **/
    private String asynbufferEnabledTaskType;

    /** cassandra服务的全局开关 **/
    private boolean cassandraGlobalSwitch;
    /*一键封车下线*/
    private boolean offlineQuickSeal;

    /** 转运卸车扫描是否启用返回校验不通过的货区编码 **/
    private boolean enableGoodsAreaOfTysScan;

    private boolean offlineLogGlobalSwitch;

    private boolean systemLogGlobalSwitch;

    /** 将日志通过kafka写入businesslog开关 **/
    private boolean logToBusinessLogByKafka;



    /** 使用异步缓冲组件时生产者的类型,
     * 多级缓冲的动态生产者的生产者类型配置项，支持'JMQ‘，’TBSCHEDULE‘和’FAILOVER‘三个可选值。
     * JMQ 直接存入JMQ
     * TBSCHEDULE 直接存入DB或者Redis
     * FAILOVER 在JMQ、TBSCHEDULE按顺序failover
     * **/
    private String asynBufferDynamicProducerProducerType;

    /** 不开启jmq模式的task类型,配置规则：taskType-keyword1；taskType-keyword1 */
    private String asynBufferNotenabledTaskKeyword1;

    /** 异步缓冲组件JMQ方式消费成功后是否落库开关 */
    private boolean asynBufferJmqComsumerTaskProcessorPostTaskStoreEnbaled;

    /** 在去O项目时数据库双写时是否忽略复制异常 */
    private boolean migrationDbBackupReplicateIgnoreExp;

    /** 在去O项目时数据库写完主库后，是否写从库（是否双写） */
    private boolean migrationDbBackupReplicateEnable;

    /** 配置哪些任务失败后不再重复抓取的 */
    private String workerFetchWithoutFailedTable;

    /** 分拣拆分任务 每页执行的包裹数**/
    private int waybillSplitPageSize;

    /**
     * 离线任务限流数量
     */
    private Integer offlineCurrentLimitingCount;


    /** 分拣动作选取的service DMS、MIDDLEEND、FAILOVER**/
    private String sortingServiceMode;


    /**
     * 大运单告警数量
     */
    private Integer bigWaybillWaringSize;

    /** 出管新接口-页面查询方法开关 true 调用新接口，false 调用老接口**/
    private boolean chuguanNewPageQuerySwitch;

    /** 分拣查询的模式配置，支持DMS、MIDDLEEND、FAILOVER三个值 **/
    private String sortingQueryMode;

    /**
     * 现场预分拣 超区运单拦截开关;true 开启拦截
     */
    private boolean preOutZoneSwitch;

    /**
     * 新log查询页面提示文字
     */
    private String newLogPageTips;
    /**
     * 老log查询页面提示
     */
    private String oldLogPageTips;

    /**
     * 离线任务的操作时间在系统时间之后的时间限制范围：24
     */
    private int offlineTaskOperateTimeCorrectHours;

    /**
     * 离线任务的操作时间在系统时间之前的时间限制范围：96h
     */
    private int offlineTaskOperateTimeBeforeNowLimitHours;

    /**
     * 自动化称重的入口切换开关
     */
    private boolean automaticWeightVolumeExchangeSwitch;

    /**
     * 自动化称重的入口切换站点
     */
    private String automaticWeightVolumeExchangeSiteCode;

    /**
     * 封车体积校验
     *  验证场地，例：910,39
     */
    private String sealVolumeCheckSites;

    /*
     * 分拣验证切换到web试用站点
     * */
    private String switchVerToWebSites;



    /**
     * 禁用老版本登陆
     * @return
     */
    private boolean disablePdaOldLogin;

    /**
     * 客户端打印清单查询时间间隔
     * @return
     */
    private int clientPrintQueryGapTime;

    /**
     * 开启集包地场地开关
     *  例：910,39 （-1代表全国）
     */
    private String collectionAddressSiteCodes;
    /*
     * PDA建箱包裹数量限制 试用站点
     * */
    private String boxLimitSites;

    /** 漏称重量方校验 开通全国开关；true打开全国 */
    private boolean weightVolumeFilterWholeCountryFlag;
    /**
     * 集货区可删除站点
     *  例：910,39（-1代表全国）
     */
    private String collectGoodsDeleteSites;

    /**
     * 按钮点击间隔(秒)，配置为-1则关闭控制
     */
    private int clickIntervalSecond;

    /**
     * 验货运单多包裹拆分任务生效的分拣中心
     */
    private String inspectionBigWaybillEffectiveSites;

    private String singleSendSwitchVerToWebSites;

    private String boardCombinationSwitchVerToWebSites;
    /**
     * 众邮称重拦截开关。true 拦截，false 不拦截
     */
    private boolean economicNetValidateWeightSwitch;
    /**
     * 任务redis开关，1-开启
     */
    private String redisSwitchOn;
    /**
     * 控制DTC积压 控制关闭 包裹号存在校验
     * true  校验包裹号
     * false 不校验包裹号
     */
    private boolean  controlCheckPackage;

    /**
     * 组板 开关控制是否校验路由
     * true  校验
     * false 不校验
     */
    private boolean   controlCheckRoute;

    /**
     * 取消发货校验封车业务开关。1：开启 0：关闭
     */
    private String dellCancelDeliveryCheckSealCar;

    /**
     * 封车空批次剔除开关 1：开启剔除 0：关闭
     */
    private String removeEmptyBatchCode;

    /**
     * 验货聚合逻辑生效的分拣中心
     */
    private String inspectionAggEffectiveSites;

    /**
     * 装车扫描每个任务下的运单数量上线
     */
    private int loadScanTaskWaybillSize;

    /**
     * 版号转包裹号最大包裹数限制
     */
    private int loadScanTaskPackageSize;

    /**
     * 最大包裹数限制
     */
    private int loadScanTaskPackageMaxSize;

    /**
     * 经济网箱号称重数据推送智腾达失败是否重试
     */
    private boolean economicNetPushZTDRetry;

    /**
     * 提供给经济网接口存入关系是否生效
     */
    private boolean eNetSyncWaybillCodeAndBoxCode;

    /**
     * 一键封车空批次剔除开关 1：开启剔除 0：关闭
     */
    private String preSealVehicleRemoveEmptyBatchCode;

    /**
     * 一键封车友情提示
     */
    private String quickSealTips;

    /**
     * 虚拟场地编码code
     */
    private Integer virtualSiteCode;

    /**
     * 创建批次号开关，是否使用序列号生成器生成还是原始批次生成工具生成
     * true： 表示使用新的序号生成器生成
     * false：使用原始的工具类生成
     */
    private boolean sendCodeGenSwitchOn;

    /**
     * 纯配外单 0重量拦截 黑名单 (在名单的拦截)
     */
    private String allPureValidateWeightWebSite;

    /**
     * 隐藏站点编号
     */
    private String deliverHideSites;

    /*
     * 现场预分拣是否校验开关
     * */
    private boolean preSortOnSiteSwitchOn;

    /**
     * BC箱号绑定WJ数量限制
     */
    private int BCContainWJNumberLimit;

    /**
     * WJ装箱包裹数限制
     */
    private int WJPackageNumberLimit;

    /**
     * 是否走老逻辑检查箱是否发货开关
     */
    private boolean checkBoxSendedSwitchOn;

    /**
     * 启用运输新接口-开关 1-开启 0-关闭
     */
    private Integer usePdaSorterApi;

    /**
     * BC箱号强制绑定循环集包袋开关(黑名单)
     * 配置的走新的逻辑，不配置的走以前的逻辑
     * 注: 仅为了上线使用
     * @return
     */
    private String allBCBoxFilterWebSite;


    /**
     * 站点查询数量最大限制
     */
    private Integer siteQueryLimit;

    /**
     * 抽检导出最大限制
     */
    private Integer exportSpotCheckMaxSize;

    /**
     * PDA通知自动拉取间隔时间(单位秒)
     */
    private Integer pdaNoticePullIntervalTime;

    /**
     * 是否走新的逆向接口
     */
    private boolean needUseNewReverseApi;

    /**
     * 离线任务上传拦截报表，0 - 全部开启，-1 - 全部关闭，1243,3534表示具体场地
     */
    private String offlineTaskReportInterceptSites;

    /**
     * 龙门架设备实操计算应拦截场地，0 - 全部开启，-1 - 全部关闭，1243,3534表示具体场地
     */
    private String scannerOperateCalculateIfInterceptSites;

    /**
     * 称重良方规则标准
     */
    private String weightVolumeRuleStandard;

    /**
     * 导出并发限制数量
     */
    private Integer exportConcurrencyLimitNum;

    /**
     * 单次查询数据库条数限制
     */
    private Integer oneQuerySize;

    /**
     * 打印交接清单新查询开通场地
     *  1)、字符串false代表不开启
     *  2)、多个场地以,分隔
     *  3)、字符串true代表全国
     */
    private String printHandoverListSites;
    /**
     * 是否开启新发货交接逻辑
     */
    private boolean newPrintHandoverListSwitch;

    /**
     * 校验站点子类型是否三方：16
     */
    private boolean checkSiteSubType;

    /**
     * 解封车查看运单包裹是否集齐
     */
    private String unSealCarHandlePackageFullCollectedSwitch;

    /**
     * 发货交接清单-汇总scrollId查询单批次查询数量
     */
    private int scrollQuerySize;
    /**
     * 发货交接清单-汇总scrollId最大次数限制
     */
    private int printScrollQueryCountLimit;
    /**
     * 审批开关
     */
    private boolean approvalSwitch;

    /**
     * 验货集包袋依赖降级， true时不依赖集包袋服务
     */
    private boolean inspectionAssertDemotion;

    /**
     * 大宗可扫描包裹下限数量
     * @return
     */
    private Integer dazongPackageOperateMax;
    /**
     * 分拣租板大宗可扫描件数最少数量
     */
    private Integer bulkScanPackageMinCount;

    /**
     * 是否校验签单返还
     * true 校验 false 不校验
     */
    private boolean checkSignAndReturn;

    /**
     * 按流向查询已验未发未装运单数据查询jsf接口降级： true时jsf接口做降级提示，不操作查询
     */
    private boolean inspectNoSendNoLoadWaybillDemotion;

    /**
     * 异步缓冲框架，JMQ消费失败不再降级为TB任务
     */
    private String closeAsynBufferSaveTaskToDb;

    /**
     * C网抽检是否下发MQ条件卡控
     */
    private boolean spotCheckIssueControl;

    /**
     * 隐藏特殊始发场地名称开关，0-关，1-开
     */
    private int hideSpecialStartSitePrintSwitch;

    /**
     * 隐藏特殊始发场地目的场地名单，形如 12,333
     */
    private String hideSpecialStartSitPrintDestinationSiteList;

    /**
     * 隐藏特殊始发场地替换字符，形如 **
     */
    private String hideSpecialStartSitePrintReplaceSymbol;

    /**
     * 启用批次有效性校验的分拣中心. 分拣中心ID逗号分隔。
     * 老发货等前端需要完全把批次生成逻辑切换到后台接口才能开启
     * @Deprecated(已废弃)
     */
    @Deprecated
    private String siteEnableSendCodeEffectiveValidation;

    /**
     * 启用批次有效性校验的分拣中心. 分拣中心ID逗号分隔。
     * 老发货等前端需要完全把批次生成逻辑切换到后台接口才能开启
     */
    private String sendCodeEffectiveValidation;

    /**
     * 取消鸡毛信切换OMS接口开关
     */
    private boolean cancelJimaoxinSwitchToOMS;

    /**
     * pda待下线||已下线菜单编码
     *  以,隔开
     */
    private String offlinePdaMenuCode;

    /***
     * 运单最大包裹数
     */
    private int waybillMaxPackNum;

    /**
     * 并发获取包裹明细开关
     */
    private boolean paralleGetPackageSwitch;

    /**
     * 虚拟组板最多流向个数
     */
    private int virtualBoardMaxDestinationCount;

    /**
     * 虚拟组板最多放置包裹个数
     */
    private int virtualBoardMaxItemCount;

    /**
     * 虚拟组板自动关闭天数
     */
    private Integer virtualBoardAutoCloseDays;

    /**
     * 虚拟组板可使用场地
     */
    private String virtualBoardCanUseSite;

    /**
     * 补打后取消拦截
     */
    private boolean printCompeteUpdateCancel;


    /**
     * 是否是所有包裹补打后再取消拦截
     */
    private boolean printCompeteAllPackageUpdateCancel;

    /**
     * 获取异常完结数据范围限制天数
     */
    private int completeExpDayNumLimit;
    /**
     * 找货通知默认消息发送人
     */
    private String findGoodSendMessageDefaultErp;

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

    public boolean getCheckSignAndReturn() {
        return checkSignAndReturn;
    }

    public void setCheckSignAndReturn(boolean checkSignAndReturn) {
        this.checkSignAndReturn = checkSignAndReturn;
    }

    /**
     * 反调度校验滑道信息 开关
     * true :检验 false 不校验
     */
    private boolean backDispatchCheck;

    /**
     * 包裹补打-拦截状态码
     *  以,隔开
     */
    private String packRePrintInterceptStatus;

    /**
     * 单次插入数据库的条数
     */
    private int insertDbRowsOneTime;

    /**
     * 抽检不超标限制
     */
    private double spotCheckNoExcessLimit;

    /**
     * 老发货异步任务开关
     */
    private String deliverySendAsyncSite;

    /**
     * 老发货异步任务延时消费毫秒数
     */
    private int deliverySendTaskSleepMills;

    /**
     * 日志查询功能开关 1：启用 0：禁用
     */
    private String businessLogQueryPageSwitch;

    /**
     * 出管供应链二期改造 开关。true 开启，false 不开启
     */
    private boolean chuguanPurchaseAndSaleSwitch;



    /**
     * 阿迪青龙业主号配置
     */
    private  String addiOwnNumberConf;

    /**
     * 货物滞留时间
     */
    private int goodsResidenceTime;

    /**
     * 写云es开关
     */
    private boolean cloudOssInsertSwitch;


    public String getAddiOwnNumberConf() {
        return addiOwnNumberConf;
    }

    public void setAddiOwnNumberConf(String addiOwnNumberConf) {
        this.addiOwnNumberConf = addiOwnNumberConf;
    }

    public boolean isChuguanPurchaseAndSaleSwitch() {
        return chuguanPurchaseAndSaleSwitch;
    }

    public void setChuguanPurchaseAndSaleSwitch(boolean chuguanPurchaseAndSaleSwitch) {
        this.chuguanPurchaseAndSaleSwitch = chuguanPurchaseAndSaleSwitch;
    }

    public boolean getEnableGoodsAreaOfTysScan() {
        return enableGoodsAreaOfTysScan;
    }

    public void setEnableGoodsAreaOfTysScan(boolean enableGoodsAreaOfTysScan) {
        this.enableGoodsAreaOfTysScan = enableGoodsAreaOfTysScan;
    }

    public String getBusinessLogQueryPageSwitch() {
        return businessLogQueryPageSwitch;
    }

    public void setBusinessLogQueryPageSwitch(String businessLogQueryPageSwitch) {
        this.businessLogQueryPageSwitch = businessLogQueryPageSwitch;
    }

    /**
     * 使用新库存ES的场地
     */
    private String useNewInventorySiteCodes;

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

    /**
     * 一单多件抽检功能开关，0-关，1-开
     */
    private int multiplePackageSpotCheckSwitch;

    /**
     * 一单多件抽检场地配置，配置ALL表示全部开启
     */
    private String multiplePackageSpotCheckSites;

    /**
     * 读转运卸车表开关
     */
    private boolean readUnloadFromTys;

    /**
     * 停止写分拣卸车表开关
     */
    private boolean stopWriteUnloadFromDms;

    /**
     * 写转运卸车表开关
     */
    private boolean writeUnloadFromTys;

    /**
     * 安卓抽检是否执行新抽检模式
     */
    private boolean androidIsExecuteNewSpotCheck;

    /**
     * 打印客户端无权限菜单配置
     */
    private String noAuthMenuConfig;
    /**
     * 打印客户端无权限菜单配置
     */
    private String noAuthMenuConfigNew;

    /**
     * 打印客户端菜单功能配置
     */
    private String menuCodeFuncConfig;
    /**
     * 打印客户端菜单功能配置
     */
    private String menuCodeFuncConfigNew;

    /**
     * 站点平台打印是否校验功能
     */
    private boolean sitePlateIsCheckFunc;

    /**
     * B网面单路由是否开启新模式
     */
    private boolean bTemplateRouterExecuteNew;

    /**
     * 预分拣返调度校验同城
     */
    private String scheduleSiteCheckSameCity;

    /**
     * 判断包裹是否打印的逻辑，包含终端首次打印的数据。默认不包含 0：不包含；1：包含
     */
    private String judgePackagePrintedIncludeSiteTerminal;

    /**
     * 按运单大打印回调异步处理的包裹数限制
     */
    private int printCompleteCallbackAsyncPackageNum;

    /**
     * 是否限制终端人员使用包裹补打 1：限制 0：不限制
     */
    private String limitSiteUsePackReprint;

    /**
     * 抽检改造开通场地
     *  多个场地以,分隔
     *  true表示全国
     *  空表示未开启
     */
    private String spotCheckReformSiteCodes;

    /**
     * 是否对restAPI鉴权的开关
     */
    private boolean restApiOuthSwitch;

    private String needInterceptUrls;

    private List<String> needInterceptUrlList;


    private String operateProgressRegions;

    private List<Integer> operateProgressRegionList;

    /**
     * 作业工作台解封车任务降级配置
     */
    private String sealTaskHystrixProps;

    /**
     * 作业工作台解封车任务强制降级开关
     */
    private int sealTaskForceFallback;

    /**
     * 拣运任务最大翻页数量
     */
    private Integer jyTaskPageMax;

    /**
     * 卸车逐单卸阈值
     */
    private Integer jyUnloadSingleWaybillThreshold;
    private Integer createSendTaskExecuteCount;
    private Integer createSendTasktimeOut;


    /** 老发货拆分任务 每页执行的包裹或箱号数据了**/
    private Integer oldSendSplitPageSize;


    /**
     * 德邦虚拟分拣中心id,字符串以逗号分割
     * @return
     */
    private String dpSiteCodes;
    private List<Integer> dpSiteCodeList = new ArrayList<>();

    /**
     * 批量一车一单 德邦单匹配德邦批次号开关
     */
    private boolean dpWaybillMatchSendCodeSwitch;
    /**
     * 发货岗计划发车时间查询条件前X天
     */
    private Integer jySendTaskPlanTimeBeginDay;
    private Integer jyCzSendTaskPlanTimeBeginDay;
    /**
     * 发货岗计划发车时间查询条件后X天
     */
    private Integer jySendTaskPlanTimeEndDay;
    private Integer jyCzSendTaskPlanTimeEndDay;

    private Integer jySendTaskCreateTimeBeginDay;


    private Integer jyComboardTaskCreateTimeBeginDay;
    /**
     * 切换转运基础服务开关
     */
    private boolean jyBasicServerSwitch;

    /**
     * 拦截批次号开关
     */
    private boolean filterSendCodeSwitch;

    /**
     * 原发货交接清单导出功能是否导出敏感数据开关
     * false：不导出敏感数据
     * true：导出敏感数据
     */
    private boolean querySensitiveFlag;

    /**
     * 安全开关
     */
    private boolean securitySwitch;

    /**
     * 拣运app降级配置
     */
    private String jyDemotionConfig;

    private boolean syncJySealStatusSwitch;

    private boolean syncJyCZSealStatusSwitch;

    private int sealStatusBatchSizeLimit;

    /**
     * 组板封车查询版列表时间
     */
    private String jyComboardSealQueryBoardListTime;

    /**
     * 组板封车全选板列表上线
     */
    private Integer jyComboardSealBoardListSelectLimit;


    private String autoPackageSendInspectionSiteCodes;

    /**
     * 混扫任务流向限制
     */
    private Integer cttGroupSendFLowLimit;

    /**
     * 体积超标是否下发场地
     *  以,隔开，ALL标识开通全国
     */
    private String volumeExcessIssueSites;
    /**
     * 拣运-系统自建滞留任务关闭时间
     */
    private Long jySysStrandTaskCloseTime;

    /**
     * 拣运-人工创建任务关闭时间
     */
    private Long jyArtificialStrandTaskCloseTime;

    /**
     * 拣运-滞留扫描数量上线
     */
    private Integer jyStrandScanNumLimit;


    /**
     * 分拣理货开关
     */
    private boolean checkTeAnSwitch;

    /**
     * 特安场地列表
     */
    private String teAnSiteWhitelist;

    private int batchQueryEndSiteLimit;

    private String qingChangDataOpenSwitch;

    public Boolean getCheckAkboxConfig() {
        return checkAkboxConfig;
    }

    public void setCheckAkboxConfig(Boolean checkAkboxConfig) {
        this.checkAkboxConfig = checkAkboxConfig;
    }

    public Long getPrintCacheTime() {
        return printCacheTime;
    }

    public void setPrintCacheTime(Long printCacheTime) {
        this.printCacheTime = printCacheTime;
    }

    /**
     * 检查是否需要检查周转筐数量和实施数量对比
     */
    private Boolean checkAkboxConfig;

    private Long printCacheTime;

    public String getQingChangDataOpenSwitch(){
        return qingChangDataOpenSwitch;
    }

    public int getBatchQueryEndSiteLimit() {
        return batchQueryEndSiteLimit;
    }

    public void setQingChangDataOpenSwitch(String qingChangDataOpenSwitch) {
        this.qingChangDataOpenSwitch = qingChangDataOpenSwitch;
    }

    public void setBatchQueryEndSiteLimit(int batchQueryEndSiteLimit) {
        this.batchQueryEndSiteLimit = batchQueryEndSiteLimit;
    }

    public String getAutoPackageSendInspectionSiteCodes() {
        return autoPackageSendInspectionSiteCodes;
    }

    public void setAutoPackageSendInspectionSiteCodes(String autoPackageSendInspectionSiteCodes) {
        this.autoPackageSendInspectionSiteCodes = autoPackageSendInspectionSiteCodes;
    }

    private Integer jyComboardSealBoardListLimit;

    private Long reComboardTimeLimit;

    private boolean reComboardSwitch;

    public boolean getReComboardSwitch() {
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


    public boolean getSyncJyCZSealStatusSwitch() {
        return syncJyCZSealStatusSwitch;
    }

    public void setSyncJyCZSealStatusSwitch(boolean syncJyCZSealStatusSwitch) {
        this.syncJyCZSealStatusSwitch = syncJyCZSealStatusSwitch;
    }

    private Integer jyComboardScanUserBeginDay;

    private Integer jyComboardSiteCTTPageSize;

    private boolean syncScheduleTaskSwitch;

    public boolean getSyncScheduleTaskSwitch() {
        return syncScheduleTaskSwitch;
    }

    public void setSyncScheduleTaskSwitch(boolean syncScheduleTaskSwitch) {
        this.syncScheduleTaskSwitch = syncScheduleTaskSwitch;
    }

    private Integer jyComboardTaskSealTimeBeginDay;

    /**
     * 组板岗板列表sql开关
     */
    private Boolean jyComboardListBoardSqlSwitch;

    /**
     * 操作流水-发送开关
     */
    private Boolean sendJyOperateFlowMqSwitch;

    /**
     * 特安发货任务提示优化开关
     */
    private boolean teAnSendTaskTipsOptimizationSwitch;

    public boolean isTeAnSendTaskTipsOptimizationSwitch() {
        return teAnSendTaskTipsOptimizationSwitch;
    }

    public void setTeAnSendTaskTipsOptimizationSwitch(boolean teAnSendTaskTipsOptimizationSwitch) {
        this.teAnSendTaskTipsOptimizationSwitch = teAnSendTaskTipsOptimizationSwitch;
    }

    /**
     * 一秒内特安任务执行限制数
     */
    private int teAnSendTaskNumberLimit;

    public int getTeAnSendTaskNumberLimit() {
        return teAnSendTaskNumberLimit;
    }

    public void setTeAnSendTaskNumberLimit(int teAnSendTaskNumberLimit) {
        this.teAnSendTaskNumberLimit = teAnSendTaskNumberLimit;
    }

    public int getSealStatusBatchSizeLimit() {
        return sealStatusBatchSizeLimit;
    }

    public void setSealStatusBatchSizeLimit(int sealStatusBatchSizeLimit) {
        this.sealStatusBatchSizeLimit = sealStatusBatchSizeLimit;
    }
    public boolean getFilterSendCodeSwitch() {
        return filterSendCodeSwitch;
    }

    public void setFilterSendCodeSwitch(boolean filterSendCodeSwitch) {
        this.filterSendCodeSwitch = filterSendCodeSwitch;
    }

    public boolean getSyncJySealStatusSwitch() {
        return syncJySealStatusSwitch;
    }

    public void setSyncJySealStatusSwitch(boolean syncJySealStatusSwitch) {
        this.syncJySealStatusSwitch = syncJySealStatusSwitch;
    }

    public Integer getOldSendSplitPageSize() {
        return oldSendSplitPageSize;
    }

    public void setOldSendSplitPageSize(Integer oldSendSplitPageSize) {
        this.oldSendSplitPageSize = oldSendSplitPageSize;
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

    public List<String> getNeedInterceptUrlList() {
        return Lists.newArrayList(needInterceptUrlList);
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

    public boolean getRestApiOuthSwitch() {
        return restApiOuthSwitch;
    }

    public void setRestApiOuthSwitch(boolean restApiOuthSwitch) {
        this.restApiOuthSwitch = restApiOuthSwitch;
    }

    /**
     * 自动签退超过多少小时未签退的数据
     */
    private int notSignedOutRecordMoreThanHours;

    /**
     * 自动签退查询数据-扫描小时数
     */
    private int notSignedOutRecordRangeHours = 12;

    /**
     * AI识别图片开关
     */
    private boolean aiDistinguishSwitch;

    /**
     * 设备AI识别图片场地开关
     *  多个场地以,分隔
     *  true表示全国
     *  空表示未开启
     */
    private String deviceAIDistinguishSwitch;

    /**
     * 设备AI识别大包裹限制
     */
    private int deviceAIDistinguishPackNum;

    /**
     * 终端包装耗材重塑项目：
     * 下线分拣维护包装耗材基础信息入口（"分拣"和"其他"类型的除外）
     * 包装耗材确认页面的增加和删除按钮
     * 开关：0不关闭入口；1关闭基础资料维护入口；2关闭耗材明细的增加和删除按钮；3关闭两者
     */
    private Integer packConsumableSwitch;

    /**
     * 到车任务按积分排序开关 1：开启
     */
    private Integer jyUnSealTaskOrderByIntegral;

    /**
     * 到车任务切换ES逻辑开关 1：开启
     */
    private Integer jyUnSealTaskSwitchToEs;

    /**
     * 默认解封车任务时间查询，如果是6则在6小时内，如果是0则不限制到达时间
     */
    private Long jyUnSealTaskLastHourTime;

    /**
     * 面单举报异常配置
     */
    private String faceAbnormalReportConfig;

    /**
     * 拣运发车任务满载率上限。eg:150
     */
    private Integer jySendTaskLoadRateUpperLimit;

    /**
     * 拣运发车任务满载率下限。eg:80
     */
    private Integer jySendTaskLoadRateLowerLimit;

    /**
     * 拣运发车任务满载率配置
     */
    private String jySendTaskLoadRateLimit;

    /**
     * 缓存时长
     */
    private Integer unloadCacheDurationHours;

    /**
     * 板上最多包裹数
     */
    private Integer unloadBoardBindingsMaxCount;

    /**
     * 任务上最多组板数
     */
    private Integer unloadTaskBoardMaxCount;

    /**
     * 包裹重量上限值，单位kg
     */
    private String packageWeightLimit;

    /**
     * 运单重量上限值，单位kg
     */
    private String waybillWeightLimit;

    /**
     * 身份证识别切量开关，全量上线之后，可以删除
     */
    private String identityRecogniseSiteSwitch;

    private List<String> identityRecogniseSiteSwitchList = new ArrayList<>();
    /**
     * 传摆发货-干支限制业务列表
     */
    private String needValidateMainLineBizSourceList;
    private List<Integer> needValidateMainLineBizSourceCodes;

    /**
     * 敏感信息隐藏开关
     */
    private Boolean sensitiveInfoHideSwitch;

    /**
     * 发货运力线路运输方式限制业务列表
     */
    private String notValidateTransTypeCodesList;
    private List<Integer> notValidateTransTypeCodes;

    /**
     * 客户端下线菜单配置,分为普通和特殊
     *  example：
     *  {
     *     "ordinary": {
     *         "0601026,0601027":"此功能已下线!"
     *     },
     *     "special": {
     *         "0601028":"此功能已下线，请用**功能代替!",
     *         "0601029":"此功能已迁移，如需使用请前往如下网址操作|www.baidu.com"
     *     }
     *  }
     */
    private String clientOfflineMenuConfig;

    /**
     * 称重量方的规则一直在变化，为了有一个版本的切换过程，这里加一个开关，
     */
    private Integer weightVolumeSwitchVersion;

    /**
     * 卸车岗列表页过滤最近N天数据
     */
    private Integer jyUnloadCarListQueryDayFilter;

    /**
     * 卸车岗列表页已完成状态任务过滤最近N天数据
     */
    private Integer jyUnloadCarListDoneQueryDayFilter;


    /**
     * 租板-板可组件数上限
     */
    private Integer jyComboardCountLimit;

    public Integer getJyComboardCountLimit() {
        return jyComboardCountLimit;
    }

    public void setJyComboardCountLimit(Integer jyComboardCountLimit) {
        this.jyComboardCountLimit = jyComboardCountLimit;
    }

    /**
     * 定时上传设备位置间隔 秒级时间戳 -1 - 表示不上传
     */
    private Integer uploadDeviceLocationInterval;
    /**
     * 实时判断设备操作位置异常开关 0 - 关，1 - 开
     */
    private Integer checkDeviceLocationInRealTimeSwitch;

    /**
     * 转运卸车任务交班最大次数
     */
    private Integer tysUnloadTaskHandoverMaxSize;

    /**
     * 转运卸车任务完成后补扫描小时数限制，超出该小时后禁止扫描
     */
    private Integer tysUnloadTaskSupplementScanLimitHours;

    /**
     * 运单系统查不到包裹号时的拦截校验开关： true 拦截  false 不拦截
     */
    private Boolean waybillSysNonExistPackageInterceptSwitch;

    /**
     * 安卓登录可跳过不处理的逻辑降级开关
     */
    private Boolean pdaLoginSkipSwitch;

    /**
     * 设备校准任务时长
     *  单位：毫秒
     */
    private Long machineCalibrateTaskDuration;

    /**
     * 设备校准任务查询范围
     *  单位：毫秒
     */
    private Long machineCalibrateTaskQueryRange;

    /**
     * 设备任务强制创建的间隔时间
     *  单位：毫秒
     */
    private Long machineCalibrateTaskForceCreateIntervalTime;

    /**
     * 设备两次合格间隔时间（用于抽检下发校验）
     *  单位：毫秒
     */
    private Long machineCalibrateIntervalTimeOfSpotCheck;

    /**
     * 设备校准后抽检记录设备状态的开关
     * 用于更新非超标抽检记录的设备状态
     */
    private boolean machineCalibrateSpotCheckSwitch;

    /**
     * 设备下发是否依据设备状态标识
     */
    private boolean spotCheckIssueRelyOMachineStatus;

    /**
     * 抽检下发依据设备状态场地维度开关
     *  多个场地以,分隔
     *  ALL表示全国
     *  空表示未开启
     */
    private String spotCheckIssueRelyOnMachineStatusSiteSwitch;

    /**
     * 得物产品类型的商家名单
     *  多个场地以,分隔
     */
    private String dewuCustomerCodes;

    private boolean czQuerySwitch;

    private Boolean boardListQuerySwitch;

    private boolean  supportMutilScan;

    private String dpSpringSiteCode;
    private List<Integer> dpSpringSiteCodeList = new ArrayList<>();

    /**
     * 传站拦截-- 场地黑名单
     */
    private String czSiteForbiddenList;

    /**
     * 传站拦截-- 大区黑名单
     */
    private String czOrgForbiddenList;
    /**
     * 旧版集包功能下线- 场地白名单
     */
    private String collectPackageSitePermitList;

    /**
     * 旧版集包功能下线- 场地黑名单
     */
    private String collectPackageSiteForbiddenList;

    /**
     * 旧版集包功能下线-- 大区黑名单
     */
    private String collectPackageOrgForbiddenList;

    /**
     * 根据箱号类型下线旧版集包功能 白名单-配置的箱号类型 跳过校验
     */
    private String skipOffLineCheckByBoxTypeList;


    public String getCollectPackageSitePermitList(){
        return collectPackageSitePermitList;
    }
    public void setCollectPackageSitePermitList(String collectPackageSitePermitList) {
        this.collectPackageSitePermitList = collectPackageSitePermitList;
    }

    public String getSkipOffLineCheckByBoxTypeList(){
        return skipOffLineCheckByBoxTypeList;
    }

    public void setSkipOffLineCheckByBoxTypeList(String skipOffLineCheckByBoxTypeList) {
        this.skipOffLineCheckByBoxTypeList = skipOffLineCheckByBoxTypeList;
    }

    public String getCollectPackageSiteForbiddenList() {
        return collectPackageSiteForbiddenList;
    }

    public void setCollectPackageSiteForbiddenList(String collectPackageSiteForbiddenList) {
        this.collectPackageSiteForbiddenList = collectPackageSiteForbiddenList;
    }

    public String getCollectPackageOrgForbiddenList(){
        return collectPackageOrgForbiddenList;
    }

    public void setCollectPackageOrgForbiddenList(String collectPackageOrgForbiddenList) {
        this.collectPackageOrgForbiddenList = collectPackageOrgForbiddenList;
    }

    /**
     * 传站拦截-场地类型黑名单
     */
    private String czSiteTypeForbiddenList;

    private boolean batchSendForbiddenSwitch;

    //网格工种限制开关
    private boolean jobTypeLimitSwitch;


    private boolean createBoardBySendFlowSwitch;

    /**
     * 装车评价开关
     */
    private boolean loadCarEvaluateSwitch;

    /**
     * 需要按照岗位码隔离板列表的场地名单
     */
    private String needIsolateBoardByGroupCodeSiteList;

    public String getNeedIsolateBoardByGroupCodeSiteList() {
        return needIsolateBoardByGroupCodeSiteList;
    }

    public void setNeedIsolateBoardByGroupCodeSiteList(String needIsolateBoardByGroupCodeSiteList) {
        this.needIsolateBoardByGroupCodeSiteList = needIsolateBoardByGroupCodeSiteList;
    }

    public boolean getCreateBoardBySendFlowSwitch() {
        return createBoardBySendFlowSwitch;
    }

    public void setCreateBoardBySendFlowSwitch(boolean createBoardBySendFlowSwitch) {
        this.createBoardBySendFlowSwitch = createBoardBySendFlowSwitch;
    }

    private boolean loadProgressByVehicleVolume;

    private boolean productOperateProgressSwitch;

    private int onlineGetTaskSimpleCodeThreshold;

    /**
     * PDA新老互斥开关
     */
    private boolean pdaVersionSwitch;

    /**
     * 交接至德邦校验开关
     */
    private boolean dpTransferSwitch;

    /**
     * 易冻品校验开关
     */
    private boolean easyFreezeSwitch;

    /**
     * 加盟商余额校验开关
     */
    private boolean allianceBusinessSwitch;

    /**
     * 上传超长超重开关
     */
    private boolean uploadOverWeightSwitch;

    /**
     * 异常处理检查场地名单
     */
    private String exceptionSubmitCheckSites;
    /**
     * 异常处理检查运单拦截类型
     */
    private String exceptionSubmitCheckWaybillInterceptTypes;

    /**
     * 忽略转运全程跟踪开关
     */
    private boolean ignoreTysTrackSwitch;

    /**
     * 接货仓发货岗查询发货明细表，默认limit最大值
     */
    private Integer JyWarehouseSendVehicleDetailQueryDefaultLimitSize;
    /**
     * 接货仓发货岗混扫任务下流向数量配置： 当前ucc配置，而且改为PC报表控制
     * ,38:10,,10186:15,,,  配置：38场地支持10个流向， 10186支持15个流向
     */
    private String JyWarehouseSendVehicleMixScanTaskFlowNumConfig;
    /**
     * 拣运扫描没有限制最大数量，取消可能存在巨大值，当取消特大时，需要关注是否为异常
     */
    private Integer JyBuQiWaybillCodeMaxSum;


    /**
     * 封车前装载率限制
     */
    private double beforeSealVehicleLoadRateLimit;

    private boolean beforeSealVehicleLoadRateLimitCheckSwitch;

    /**
     * 批量生成批次号时：最大数量限制
     */
    private Integer batchGenerateSendCodeMaxNum;

    /**
     * 车型优先分数默认值
     */
    private double vehicleIntegralPriorityFraction;

    public Integer getFetchCarDistributionTimeRange() {
        return fetchCarDistributionTimeRange;
    }

    public void setFetchCarDistributionTimeRange(Integer fetchCarDistributionTimeRange) {
        this.fetchCarDistributionTimeRange = fetchCarDistributionTimeRange;
    }

    /**
     * 待派车查询时间范围  单位小时
     */
    private Integer fetchCarDistributionTimeRange;

    /**
     * 外单逆向换单次数
     */
    private Integer reverseExchangeCount;

    /**
     * 封车或取消封车时同步新版app发货任务状态开关: 默认 ： 1, 开启
     */
    private String sealSyncOpenCloseSendTaskSwitch;
    private boolean dmsToVendorSendMQSwitch;

    /**
     * 传站封车板最小数量限制
     */
    private Integer czSealCarBoardCountMinLimit;

    /**
     * 传站封车件数最小数量限制
     */
    private Integer czSealCarPackageCountMinLimit;

    /**
     * 接货仓发货岗待发货任务列表计划发车时间查询条件前X小时
     */
    private Integer jyWarehouseToSendPlanTimeBeginHours;

    /**
     * 接货仓发货岗待发货任务列表计划发车时间查询条件后X小时
     */
    private Integer jyWarehouseToSendPlanTimeEndHours;

    /**
     * 接货仓发货岗发货中任务列表计划发车时间查询条件前X小时
     */
    private Integer jyWarehouseSendingPlanTimeBeginHours;

    /**
     * 接货仓发货岗发货中任务列表计划发车时间查询条件后X小时
     */
    private Integer jyWarehouseSendingPlanTimeEndHours;

    /**
     * 接货仓发货岗待封车任务列表计划发车时间查询条件前X小时
     */
    private Integer jyWarehouseToSealPlanTimeBeginHours;

    /**
     * 接货仓发货岗待封车任务列表计划发车时间查询条件后X小时
     */
    private Integer jyWarehouseToSealPlanTimeEndHours;

    /**
     * 接货仓发货岗已封车任务列表计划发车时间查询条件前X小时
     */
    private Integer jyWarehouseSealedPlanTimeBeginHours;

    /**
     * 接货仓发货岗已封车任务列表计划发车时间查询条件后X小时
     */
    private Integer jyWarehouseSealedPlanTimeEndHours;

    /**
     * 接货仓发货岗任务列表创建时间查询条件前X小时
     */
    private Integer jyWarehouseTaskCreateTimeBeginHours;

    /**
     * 接货仓发货岗自建任务常用车型置顶
     */
    private String jyWarehouseManualTaskKeyVehicleTypes;


    /**
     * 包裹非0重量体积拦截开关
     * <p>
     *     场地维度开关，多个场地以,隔开，ALL表示全国维度
     * </p>
     */
    private String waybillZeroWeightInterceptSites;

    /**
     * 自动化称重限制开关
     */
    private Boolean automaticWeightVolumeUpperCheckSwitch;

    /**
     * PDA静默下载开关
     */
    private Boolean pdaSilentUpdateSwitch;

    public boolean isDmsToVendorSendMQSwitch() {
        return dmsToVendorSendMQSwitch;
    }

    public void setDmsToVendorSendMQSwitch(boolean dmsToVendorSendMQSwitch) {
        this.dmsToVendorSendMQSwitch = dmsToVendorSendMQSwitch;
    }


    public Integer getBatchGenerateSendCodeMaxNum() {
        return batchGenerateSendCodeMaxNum;
    }

    public void setBatchGenerateSendCodeMaxNum(Integer batchGenerateSendCodeMaxNum) {
        this.batchGenerateSendCodeMaxNum = batchGenerateSendCodeMaxNum;
    }

    public int getOnlineGetTaskSimpleCodeThreshold() {
        return onlineGetTaskSimpleCodeThreshold;
    }

    public void setOnlineGetTaskSimpleCodeThreshold(int onlineGetTaskSimpleCodeThreshold) {
        this.onlineGetTaskSimpleCodeThreshold = onlineGetTaskSimpleCodeThreshold;
    }

    public boolean getProductOperateProgressSwitch() {
        return productOperateProgressSwitch;
    }

    public void setProductOperateProgressSwitch(boolean productOperateProgressSwitch) {
        this.productOperateProgressSwitch = productOperateProgressSwitch;
    }

    public boolean getLoadProgressByVehicleVolume() {
        return loadProgressByVehicleVolume;
    }

    public void setLoadProgressByVehicleVolume(boolean loadProgressByVehicleVolume) {
        this.loadProgressByVehicleVolume = loadProgressByVehicleVolume;
    }
    /**
     * 组板扫描页刷新定时间隔
     */
    private Integer jyComboardRefreshTimerInterval;

    public Integer getJyComboardRefreshTimerInterval() {
        return jyComboardRefreshTimerInterval;
    }

    public void setJyComboardRefreshTimerInterval(Integer jyComboardRefreshTimerInterval) {
        this.jyComboardRefreshTimerInterval = jyComboardRefreshTimerInterval;
    }

    private String interceptBlackList;

    public String getInterceptBlackList() {
        return interceptBlackList;
    }

    public void setInterceptBlackList(String interceptBlackList) {
        this.interceptBlackList = interceptBlackList;
    }
    /**
     * 组板路由校验开关
     */
    private boolean boardCombinationRouterSwitch;

    public boolean getBatchSendForbiddenSwitch() {
        return batchSendForbiddenSwitch;
    }

    public void setBatchSendForbiddenSwitch(boolean batchSendForbiddenSwitch) {
        this.batchSendForbiddenSwitch = batchSendForbiddenSwitch;
    }

    /**
     * 终端站点-包裹补打限制开关
     */
    private boolean terminalSitePackagePrintLimitSwitch;

    public boolean isTerminalSitePackagePrintLimitSwitch() {
        return terminalSitePackagePrintLimitSwitch;
    }

    public void setTerminalSitePackagePrintLimitSwitch(boolean terminalSitePackagePrintLimitSwitch) {
        this.terminalSitePackagePrintLimitSwitch = terminalSitePackagePrintLimitSwitch;
    }

    private boolean needValidateBatchCodeHasSealed;

    private String forceSendSiteList;


    private String forceCollectPackageSiteListStr;

    private List<String> forceCollectPackageSiteList;

    public List<String> getForceCollectPackageSiteList(){
        return forceCollectPackageSiteList;
    }

    public void setForceCollectPackageSiteList() {
        if (ObjectHelper.isNotNull(forceCollectPackageSiteListStr)){
            forceCollectPackageSiteList =new ArrayList<>();
            if (forceCollectPackageSiteListStr.contains(",")){
                forceCollectPackageSiteList =Arrays.asList(forceCollectPackageSiteListStr.split(","));
            }else {
                forceCollectPackageSiteList.add(forceCollectPackageSiteListStr);
            }
        }
    }

    public String getForceCollectPackageSiteListStr(){
        return forceCollectPackageSiteListStr;
    }
    public void setForceCollectPackageSiteListStr(String forceCollectPackageSiteListStr) {
        this.forceCollectPackageSiteListStr = forceCollectPackageSiteListStr;
    }

    public String getForceSendSiteList() {
        return forceSendSiteList;
    }

    public void setForceSendSiteList(String forceSendSiteList) {
        this.forceSendSiteList = forceSendSiteList;
    }

    public boolean getNeedValidateBatchCodeHasSealed() {
        return needValidateBatchCodeHasSealed;
    }

    public void setNeedValidateBatchCodeHasSealed(boolean needValidateBatchCodeHasSealed) {
        this.needValidateBatchCodeHasSealed = needValidateBatchCodeHasSealed;
    }

    /**
     * 自动关闭任务配置，转换为对象
     *  Note :
     *       - Attention: ucc的属性如果是自定义对象，并且自定义对象内属性包含汉字，则需要再get方法中返回新对象，防止ucc属性值被修改
     */
    private String autoCloseJyBizTaskConfig;
    private AutoCloseJyBizTaskConfig autoCloseJyBizTaskConfigObj;

    public String getCzSiteTypeForbiddenList() {
        return czSiteTypeForbiddenList;
    }

    public void setCzSiteTypeForbiddenList(String czSiteTypeForbiddenList) {
        this.czSiteTypeForbiddenList = czSiteTypeForbiddenList;
    }

    public String getCzSiteForbiddenList() {
        return czSiteForbiddenList;
    }

    public void setCzSiteForbiddenList(String czSiteForbiddenList) {
        this.czSiteForbiddenList = czSiteForbiddenList;
    }

    public String getCzOrgForbiddenList() {
        return czOrgForbiddenList;
    }

    public void setCzOrgForbiddenList(String czOrgForbiddenList) {
        this.czOrgForbiddenList = czOrgForbiddenList;
    }

    /**
     * 异常报废审批级别限定数量
     *  多个级别以,隔开
     */
    private String exScrapApproveLevelCountLimit;



    /**
     * 三无任务指派数量限制
     */
    private int assignExpTaskQuantityLimit;

    public boolean getSupportMutilScan() {
        return supportMutilScan;
    }

    public void setSupportMutilScan(boolean supportMutilScan) {
        this.supportMutilScan = supportMutilScan;
    }

    /**
     * 转运卸车岗集齐功能降级开关
     */
    private Boolean tysUnloadCarCollectDemoteSwitch;

    /**
     * 拣运集齐场地列表
     */
    private String jyCollectSiteWhitelist;

    /**
     * 发货运输车辆靠台验证刷新间隔
     */
    private Integer jyTransportSendVehicleValidateDockRefreshTime;

    /**
     * 发货运输车辆靠台验证码允许访问上几次验证码个数
     */
    private Integer jyTransportSendVehicleValidateDockAllowRefreshTimes;

    /**
     * 异常破损任务客服未反馈时间（小时）
     */
    private int jyExceptionDamageTaskCustomerNotReturnHours;

    /**
     * 异常任务生成id开关
     */
    private boolean jyExceptionCreateBizIdSwitch;

    private String aggsDataSource;

    private Boolean userSignIgnoreCaseSwitch;

    public Boolean getUserSignIgnoreCaseSwitch() {
        return userSignIgnoreCaseSwitch;
    }

    public void setUserSignIgnoreCaseSwitch(Boolean userSignIgnoreCaseSwitch) {
        this.userSignIgnoreCaseSwitch = userSignIgnoreCaseSwitch;
    }

    public String getAggsDataSource() {
        return aggsDataSource;
    }

    public void setAggsDataSource(String aggsDataSource) {
        this.aggsDataSource = aggsDataSource;
    }
    /**
     * 拣运APP自动刷新时间配置
     *  Note :
     *      - Attention: ucc的属性如果是自定义对象，并且自定义对象内属性包含汉字，则需要再get方法中返回新对象，防止ucc属性值被修改
     */
    private String jyWorkAppAutoRefreshConfig;
    private List<ClientAutoRefreshConfig> jyWorkAppAutoRefreshConfigList = new ArrayList<>();

    /**
     * 组板岗混扫任务查询限制
     */
    private Integer cttGroupDataLimit;

    /**
     * 作业APP发货特殊产品类型提示，到指定剩余分钟数才提示
     */
    private Integer jySendSpecialProductTypeToScanShowRemainMinutes;

    /**
     * 集包岗查询时间限制
     */
    private String jyCollectPackageTaskQueryTimeLimit;

    /**
     * 混包校验使用分拣工作台新版配置
     */
    private boolean mixedConfigUseBasicNew;

    private boolean collectPackageTaskRefreshSwitch;

    private long jyCollectPackageInterceptBitCode;
    /**
     * 空铁提货岗任务查询时间范围
     */
    private Integer jyBizTaskPickingGoodTimeRange;

    /**
     * 提货任务待提件数为0的查询时间范围
     */
    private Integer pickingGoodTaskWaitScanEq0TimeRange;
    /**
     * 自建提货任务自动完成时间
     */
    private Integer pickingGoodTaskManualTimeRange;
    /**
     * 空铁提货岗流向最大数限制
     */
    private Integer sendFlowLimit;
    /**
     * 空铁提货岗计划到达时间查询范围
     */
    private Integer pickingPlanArriveTimeRange;
    /**
     * 空铁提货岗实际到达时间查询范围
     */
    private Integer pickingRealArriveTimeRange;
    /**
     * 空铁提货岗提货完成时间查询范围
     */
    private Integer pickingFinishTimeRange;
    /**
     * 围栏到达包裹自动验货条件不符合时重试消费的最大分钟数
     */
    private Integer packageArriveAutoInspectionRetryMinutes;
    private Integer packageArriveAutoInspectionNullTaskRetryMinutes;
    /**
     * PDA卸车扫描和自动验货互斥开关
     */
    private Boolean pdaUnloadAndAutoInspectionRejectSwitch;
    /**
     * 操作流水新逻辑开关
     */
    private boolean operateFlowNewSwitch;

    /**
     * 自检任务-任务配置编码
     */
    private String selfCheckTaskConfigCode;

    public Integer getPickingPlanArriveTimeRange() {
        return pickingPlanArriveTimeRange;
    }

    public void setPickingPlanArriveTimeRange(Integer pickingPlanArriveTimeRange) {
        this.pickingPlanArriveTimeRange = pickingPlanArriveTimeRange;
    }

    public Integer getPickingRealArriveTimeRange() {
        return pickingRealArriveTimeRange;
    }

    public void setPickingRealArriveTimeRange(Integer pickingRealArriveTimeRange) {
        this.pickingRealArriveTimeRange = pickingRealArriveTimeRange;
    }

    public Integer getPickingFinishTimeRange() {
        return pickingFinishTimeRange;
    }

    public void setPickingFinishTimeRange(Integer pickingFinishTimeRange) {
        this.pickingFinishTimeRange = pickingFinishTimeRange;
    }

    public Integer getSendFlowLimit() {
        return sendFlowLimit;
    }

    public void setSendFlowLimit(Integer sendFlowLimit) {
        this.sendFlowLimit = sendFlowLimit;
    }

    public Integer getPickingGoodTaskWaitScanEq0TimeRange() {
        return pickingGoodTaskWaitScanEq0TimeRange;
    }

    public void setPickingGoodTaskWaitScanEq0TimeRange(Integer pickingGoodTaskWaitScanEq0TimeRange) {
        this.pickingGoodTaskWaitScanEq0TimeRange = pickingGoodTaskWaitScanEq0TimeRange;
    }

    public Integer getPickingGoodTaskManualTimeRange() {
        return pickingGoodTaskManualTimeRange;
    }

    public void setPickingGoodTaskManualTimeRange(Integer pickingGoodTaskManualTimeRange) {
        this.pickingGoodTaskManualTimeRange = pickingGoodTaskManualTimeRange;
    }

    public Integer getJyBizTaskPickingGoodTimeRange() {
        return jyBizTaskPickingGoodTimeRange;
    }

    public void setJyBizTaskPickingGoodTimeRange(Integer jyBizTaskPickingGoodTimeRange) {
        this.jyBizTaskPickingGoodTimeRange = jyBizTaskPickingGoodTimeRange;
    }

    /**
     * 存储仓打包明细 单个箱子的上限
     */
    private Integer storageBoxDetailMaxSizeLimit;

    private boolean thanosSwitch;

    public boolean getThanosSwitch(){
        return thanosSwitch;
    }

    public void setThanosSwitch(boolean thanosSwitch) {
        this.thanosSwitch = thanosSwitch;
    }

    public Integer getStorageBoxDetailMaxSizeLimit(){
        return storageBoxDetailMaxSizeLimit;
    }

    public void setStorageBoxDetailMaxSizeLimit(Integer storageBoxDetailMaxSizeLimit) {
        this.storageBoxDetailMaxSizeLimit = storageBoxDetailMaxSizeLimit;
    }

    public long getJyCollectPackageInterceptBitCode(){
        return jyCollectPackageInterceptBitCode;
    }

    public void setJyCollectPackageInterceptBitCode(long jyCollectPackageInterceptBitCode) {
        this.jyCollectPackageInterceptBitCode = jyCollectPackageInterceptBitCode;
    }

    /**
     * 箱号类型判断终端站点类型
     */
    private String terminalSiteTypeForBoxType;
    private List<Integer> terminalSiteTypeListForBoxType = new ArrayList<>();

    /**
     * 箱号类型新版切换开关
     */
    private boolean boxTypeNewVersionSwitch;


    private Double jyAviationManualCreateSendTaskMaxBookingWeight;

    /**
     * 排班允许进入闸机误差小时数
     */
    private Integer allowEntryHours;

    /**
     * 场地的评价和申诉权限，自动关闭天数
     */
    private Integer evaluateAppealCloseDay;

    /**
     * 申诉记录的超时审核小时数
     */
    private Integer checkOvertimeHour;

    public Integer getCheckOvertimeHour() {
        return checkOvertimeHour;
    }

    public void setCheckOvertimeHour(Integer checkOvertimeHour) {
        this.checkOvertimeHour = checkOvertimeHour;
    }

    public Integer getEvaluateAppealCloseDay() {
        return evaluateAppealCloseDay;
    }

    public void setEvaluateAppealCloseDay(Integer evaluateAppealCloseDay) {
        this.evaluateAppealCloseDay = evaluateAppealCloseDay;
    }

    public Integer getAllowEntryHours() {
        return allowEntryHours;
    }

    public void setAllowEntryHours(Integer allowEntryHours) {
        this.allowEntryHours = allowEntryHours;
    }

    /**
     * 特安作业区编码，逗号分隔
     */
    private String teanWorkAreaCodes;
    private List<String> teanWorkAreaCodesList = new ArrayList<>();

    /**
     * 特安及其他混扫作业区编码，逗号分隔
     */
    private String teanMixScanWorkAreaCodes;
    private List<String> teanMixScanWorkAreaCodesList = new ArrayList<>();

    /**
     * 特安作业区拦截链场地白名单
     */
    private String teanSiteIdWhiteListStr4InterceptFilter;
    private List<Integer> teanSiteIdWhiteList4InterceptFilter = new ArrayList<>();

    /**
     * 特安作业区拦截链启用场地名单
     */
    private String teanSiteIdEnableListStr4InterceptFilter;
    private List<Integer> teanSiteIdEnableList4InterceptFilter = new ArrayList<>();

    /**
     * 异常岗启动场地
     */
    private String interceptExceptionSiteIdEnableListStr;
    private List<Integer> interceptExceptionSiteIdEnableList = new ArrayList<>();

    /**
     * 动态临时路由启用场地
     */
    private String routerDynamicLineReplaceEnableSiteStr;
    private List<Integer> routerDynamicLineReplaceEnableSiteList = new ArrayList<>();

    /**
     * 集包任务统计使用独立接口开关
     */
    private Boolean collectPackageTaskStatisticsUseIndependentInterfaceSwitch;

    /**
     * rest接口限流配置，功能开关
     */
    private Boolean restRateLimiterSwitch;
    /**
     * rest接口限流配置，限流租户
     */
    private String restRateLimiterTenant;
    /**
     * rest接口限流配置内容
     */
    private String restRateLimiterBody;
    /**
     * rest接口限流配置，白名单
     */
    private String restRateLimiterWhiteList;
    /**
     * rest接口限流配置，IP黑名单，永久禁止访问
     */
    private String restRateLimiterBlackIpList;
    /**
     * rest接口限流配置，ERP黑名单，永久禁止访问
     */
    private String restRateLimiterBlackErpList;
    /**
     * rest接口限流配置，功能降级全局提示语
     */
    private String restRateLimiterFallbackGlobalTip;
    /**
     * rest接口限流配置，按URI配置的降级提示语
     */
    private String restRateLimiterFallbackTips;

    /**
     * 整箱中转发货，是否补充收货记录
     */
    private Boolean boxTransferSendInsertReceiveSwitch;

    public boolean getCzQuerySwitch() {
        return czQuerySwitch;
    }

    public void setCzQuerySwitch(boolean czQuerySwitch) {
        this.czQuerySwitch = czQuerySwitch;
    }

    /**
     * 允许操作离线上传的场地编码。以,分隔
     */
    private String offLineAllowedSites;

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

    public Integer getUsePdaSorterApi() {
        return usePdaSorterApi;
    }

    public void setUsePdaSorterApi(Integer usePdaSorterApi) {
        this.usePdaSorterApi = usePdaSorterApi;
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

    public String getUnSealCarHandlePackageFullCollectedSwitch() {
        return unSealCarHandlePackageFullCollectedSwitch;
    }

    public UccPropertyConfiguration setUnSealCarHandlePackageFullCollectedSwitch(String unSealCarHandlePackageFullCollectedSwitch) {
        this.unSealCarHandlePackageFullCollectedSwitch = unSealCarHandlePackageFullCollectedSwitch;
        return this;
    }

    public Boolean getUnSealCarHandlePackageFullCollectedNeedHandle(Long siteId) {
        if(StringUtils.isBlank(unSealCarHandlePackageFullCollectedSwitch)){
            return false;
        }
        if(Objects.equals("all", unSealCarHandlePackageFullCollectedSwitch)){
            return true;
        }
        if(Objects.equals("-1", unSealCarHandlePackageFullCollectedSwitch)){
            return false;
        }
        List<String> siteCodes = Arrays.asList(unSealCarHandlePackageFullCollectedSwitch.split(Constants.SEPARATOR_COMMA));
        if(siteCodes.contains(siteId + "")){
            return true;
        }
        return false;
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

    public boolean getNewPrintHandoverListSwitch() {
        return newPrintHandoverListSwitch;
    }

    public void setNewPrintHandoverListSwitch(boolean newPrintHandoverListSwitch) {
        this.newPrintHandoverListSwitch = newPrintHandoverListSwitch;
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
    private List<String> hideSpecialStartSitPrintDestinationSiteStrList = new ArrayList<>();
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

    private final int hideSpecialStartSitePrintReplaceSymbolMaxLength = 20;
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
    private String itmsBizEnableSwitch;

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
    private List<String> _multiplePackageSpotCheckSitesList = new ArrayList<>();
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

    public UccPropertyConfiguration setVirtualBoardAutoCloseDays(Integer virtualBoardAutoCloseDays) {
        this.virtualBoardAutoCloseDays = virtualBoardAutoCloseDays;
        return this;
    }

    public String getVirtualBoardCanUseSite() {
        return virtualBoardCanUseSite;
    }

    public UccPropertyConfiguration setVirtualBoardCanUseSite(String virtualBoardCanUseSite) {
        this.virtualBoardCanUseSite = virtualBoardCanUseSite;
        this.virtualBoardCanUseSiteList = this.getVirtualBoardCanUseSiteList();
        return this;
    }

    /**
     * 请勿配置此变量为ucc配置
     */
    private List<String> virtualBoardCanUseSiteList = new ArrayList<>();

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

    public boolean getBTemplateRouterExecuteNew() {
        return bTemplateRouterExecuteNew;
    }

    public void setBTemplateRouterExecuteNew(boolean bTemplateRouterExecuteNew) {
        this.bTemplateRouterExecuteNew = bTemplateRouterExecuteNew;
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

    public Long getJyUnSealTaskLastHourTime() {
        return jyUnSealTaskLastHourTime;
    }

    public void setJyUnSealTaskLastHourTime(Long jyUnSealTaskLastHourTime) {
        this.jyUnSealTaskLastHourTime = jyUnSealTaskLastHourTime;
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

    public Integer getUnloadTaskBoardMaxCount() {
        return unloadTaskBoardMaxCount;
    }

    public void setUnloadTaskBoardMaxCount(Integer unloadTaskBoardMaxCount) {
        this.unloadTaskBoardMaxCount = unloadTaskBoardMaxCount;
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

    public Integer getJyUnloadCarListDoneQueryDayFilter() {
        return jyUnloadCarListDoneQueryDayFilter;
    }

    public void setJyUnloadCarListDoneQueryDayFilter(Integer jyUnloadCarListDoneQueryDayFilter) {
        this.jyUnloadCarListDoneQueryDayFilter = jyUnloadCarListDoneQueryDayFilter;
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

    public Boolean getWaybillSysNonExistPackageInterceptSwitch() {
        return waybillSysNonExistPackageInterceptSwitch;
    }

    public void setWaybillSysNonExistPackageInterceptSwitch(Boolean waybillSysNonExistPackageInterceptSwitch) {
        this.waybillSysNonExistPackageInterceptSwitch = waybillSysNonExistPackageInterceptSwitch;
    }

    public Boolean getPdaLoginSkipSwitch() {
        return pdaLoginSkipSwitch;
    }

    public void setPdaLoginSkipSwitch(Boolean pdaLoginSkipSwitch) {
        this.pdaLoginSkipSwitch = pdaLoginSkipSwitch;
    }

    public Long getMachineCalibrateTaskDuration() {
        return machineCalibrateTaskDuration;
    }

    public void setMachineCalibrateTaskDuration(Long machineCalibrateTaskDuration) {
        this.machineCalibrateTaskDuration = machineCalibrateTaskDuration;
    }

    public Long getMachineCalibrateTaskQueryRange() {
        return machineCalibrateTaskQueryRange;
    }

    public void setMachineCalibrateTaskQueryRange(Long machineCalibrateTaskQueryRange) {
        this.machineCalibrateTaskQueryRange = machineCalibrateTaskQueryRange;
    }

    public Long getMachineCalibrateTaskForceCreateIntervalTime() {
        return machineCalibrateTaskForceCreateIntervalTime;
    }

    public void setMachineCalibrateTaskForceCreateIntervalTime(Long machineCalibrateTaskForceCreateIntervalTime) {
        this.machineCalibrateTaskForceCreateIntervalTime = machineCalibrateTaskForceCreateIntervalTime;
    }

    public Long getMachineCalibrateIntervalTimeOfSpotCheck() {
        return machineCalibrateIntervalTimeOfSpotCheck;
    }

    public Integer getBulkScanPackageMinCount() {
        return bulkScanPackageMinCount;
    }

    public void setBulkScanPackageMinCount(Integer bulkScanPackageMinCount) {
        this.bulkScanPackageMinCount = bulkScanPackageMinCount;
    }

    public Integer getJyComboardTaskCreateTimeBeginDay() {
        return jyComboardTaskCreateTimeBeginDay;
    }

    public void setJyComboardTaskCreateTimeBeginDay(Integer jyComboardTaskCreateTimeBeginDay) {
        this.jyComboardTaskCreateTimeBeginDay = jyComboardTaskCreateTimeBeginDay;
    }

    public Integer getJyComboardSiteCTTPageSize() {
        return jyComboardSiteCTTPageSize;
    }
    public void setMachineCalibrateIntervalTimeOfSpotCheck(Long machineCalibrateIntervalTimeOfSpotCheck) {
        this.machineCalibrateIntervalTimeOfSpotCheck = machineCalibrateIntervalTimeOfSpotCheck;
    }

    public boolean getMachineCalibrateSpotCheckSwitch() {
        return machineCalibrateSpotCheckSwitch;
    }

    public void setMachineCalibrateSpotCheckSwitch(boolean machineCalibrateSpotCheckSwitch) {
        this.machineCalibrateSpotCheckSwitch = machineCalibrateSpotCheckSwitch;
    }

    public boolean getSpotCheckIssueRelyOMachineStatus() {
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
    public void setJyComboardSiteCTTPageSize(Integer jyComboardSiteCTTPageSize) {
        this.jyComboardSiteCTTPageSize = jyComboardSiteCTTPageSize;
    }

    public String getDewuCustomerCodes() {
        return dewuCustomerCodes;
    }

    public void setDewuCustomerCodes(String dewuCustomerCodes) {
        this.dewuCustomerCodes = dewuCustomerCodes;
        this.dewuCustomerCodeList = this.getDewuCustomerCodeList();
    }

    /**
     * 请勿配置此变量为ucc配置
     */
    private List<String> dewuCustomerCodeList = new ArrayList<>();

    public List<String> getDewuCustomerCodeList() {
        if(StringUtils.isBlank(dewuCustomerCodes)){
            return new ArrayList<>();
        }
        return Arrays.asList(dewuCustomerCodes.split(Constants.SEPARATOR_COMMA));
    }

    public boolean matchDewuCustomerCode(String customerCode) {
        if(StringUtils.isBlank(dewuCustomerCodes)){
            return false;
        }
        if(dewuCustomerCodeList.contains(customerCode)){
            return true;
        }
        return false;
    }

    public Integer getJyComboardScanUserBeginDay() {
        return jyComboardScanUserBeginDay;
    }

    public void setJyComboardScanUserBeginDay(Integer jyComboardScanUserBeginDay) {
        this.jyComboardScanUserBeginDay = jyComboardScanUserBeginDay;
    }


    public Integer getJyComboardTaskSealTimeBeginDay() {
        return jyComboardTaskSealTimeBeginDay;
    }

    public void setJyComboardTaskSealTimeBeginDay(Integer jyComboardTaskSealTimeBeginDay) {
        this.jyComboardTaskSealTimeBeginDay = jyComboardTaskSealTimeBeginDay;
    }

    public Boolean getJyComboardListBoardSqlSwitch() {
        return jyComboardListBoardSqlSwitch;
    }

    public void setJyComboardListBoardSqlSwitch(Boolean jyComboardListBoardSqlSwitch) {
        this.jyComboardListBoardSqlSwitch = jyComboardListBoardSqlSwitch;
    }

    public String getOffLineAllowedSites() {
        return offLineAllowedSites;
    }

    public void setOffLineAllowedSites(String offLineAllowedSites) {
        this.offLineAllowedSites = offLineAllowedSites;
    }

    public boolean isOffLineAllowedSite(Integer siteCode) {
        return Constants.STR_ALL.equals(offLineAllowedSites) || Arrays.asList(offLineAllowedSites.split(Constants.SEPARATOR_COMMA)).contains(String.valueOf(siteCode));
    }

    public String getJyComboardSealQueryBoardListTime() {
        return jyComboardSealQueryBoardListTime;
    }

    public void setJyComboardSealQueryBoardListTime(String jyComboardSealQueryBoardListTime) {
        this.jyComboardSealQueryBoardListTime = jyComboardSealQueryBoardListTime;
    }

    public Integer getJyComboardSealBoardListSelectLimit() {
        return jyComboardSealBoardListSelectLimit;
    }

    public void setJyComboardSealBoardListSelectLimit(Integer jyComboardSealBoardListSelectLimit) {
        this.jyComboardSealBoardListSelectLimit = jyComboardSealBoardListSelectLimit;
    }

    public Integer getJyComboardSealBoardListLimit() {
        return jyComboardSealBoardListLimit;
    }

    public void setJyComboardSealBoardListLimit(Integer jyComboardSealBoardListLimit) {
        this.jyComboardSealBoardListLimit = jyComboardSealBoardListLimit;
    }

    public Boolean getBoardListQuerySwitch() {
        return boardListQuerySwitch;
    }

    public void setBoardListQuerySwitch(Boolean boardListQuerySwitch) {
        this.boardListQuerySwitch = boardListQuerySwitch;
    }

    public boolean isCloudOssInsertSwitch() {
        return cloudOssInsertSwitch;
    }

    public void setCloudOssInsertSwitch(boolean cloudOssInsertSwitch) {
        this.cloudOssInsertSwitch = cloudOssInsertSwitch;
    }

    public Integer getCttGroupSendFLowLimit() {
        return cttGroupSendFLowLimit;
    }

    public void setCttGroupSendFLowLimit(Integer cttGroupSendFLowLimit) {
        this.cttGroupSendFLowLimit = cttGroupSendFLowLimit;
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
        this.dpSpringSiteCodeList.clear();
        for (String siteCodeStr : dpSpringSiteCodeList) {
            this.dpSpringSiteCodeList.add(Integer.valueOf(siteCodeStr));
        }
    }

    public List<Integer> getDpSpringSiteCodeList() {
        return this.dpSpringSiteCodeList;
    }

    public boolean isDpSpringSiteCode(Integer siteCode) {
        return this.getDpSpringSiteCodeList().contains(siteCode);
    }

    public String getVolumeExcessIssueSites() {
        return volumeExcessIssueSites;
    }

    public void setVolumeExcessIssueSites(String volumeExcessIssueSites) {
        this.volumeExcessIssueSites = volumeExcessIssueSites;
    }

    public String getAutoCloseJyBizTaskConfig() {
        return this.autoCloseJyBizTaskConfig;
    }

    public void setAutoCloseJyBizTaskConfig(String autoCloseJyBizTaskConfig) {
        this.autoCloseJyBizTaskConfig = autoCloseJyBizTaskConfig;
        if(StringUtils.isNotBlank(this.autoCloseJyBizTaskConfig)){
            autoCloseJyBizTaskConfigObj = JsonHelper.fromJson(autoCloseJyBizTaskConfig, AutoCloseJyBizTaskConfig.class);
        }
    }

    public boolean isLoadCarEvaluateSwitch() {
        return loadCarEvaluateSwitch;
    }

    public void setLoadCarEvaluateSwitch(boolean loadCarEvaluateSwitch) {
        this.loadCarEvaluateSwitch = loadCarEvaluateSwitch;
    }

    public Boolean getTysUnloadCarCollectDemoteSwitch() {
        return tysUnloadCarCollectDemoteSwitch;
    }

    public void setTysUnloadCarCollectDemoteSwitch(Boolean tysUnloadCarCollectDemoteSwitch) {
        this.tysUnloadCarCollectDemoteSwitch = tysUnloadCarCollectDemoteSwitch;
    }

    public String getJyCollectSiteWhitelist() {
        return jyCollectSiteWhitelist;
    }

    public void setJyCollectSiteWhitelist(String jyCollectSiteWhitelist) {
        this.jyCollectSiteWhitelist = jyCollectSiteWhitelist;
    }

    public boolean isJobTypeLimitSwitch() {
        return jobTypeLimitSwitch;
    }

    public void setJobTypeLimitSwitch(boolean jobTypeLimitSwitch) {
        this.jobTypeLimitSwitch = jobTypeLimitSwitch;
    }

    public Long getJySysStrandTaskCloseTime() {
        return jySysStrandTaskCloseTime;
    }

    public void setJySysStrandTaskCloseTime(Long jySysStrandTaskCloseTime) {
        this.jySysStrandTaskCloseTime = jySysStrandTaskCloseTime;
    }

    public Long getJyArtificialStrandTaskCloseTime() {
        return jyArtificialStrandTaskCloseTime;
    }

    public void setJyArtificialStrandTaskCloseTime(Long jyArtificialStrandTaskCloseTime) {
        this.jyArtificialStrandTaskCloseTime = jyArtificialStrandTaskCloseTime;
    }

    public Integer getJyStrandScanNumLimit() {
        return jyStrandScanNumLimit;
    }

    public void setJyStrandScanNumLimit(Integer jyStrandScanNumLimit) {
        this.jyStrandScanNumLimit = jyStrandScanNumLimit;
    }

    public String getExScrapApproveLevelCountLimit() {
        return exScrapApproveLevelCountLimit;
    }

    public void setExScrapApproveLevelCountLimit(String exScrapApproveLevelCountLimit) {
        this.exScrapApproveLevelCountLimit = exScrapApproveLevelCountLimit;
    }

    public int getCompleteExpDayNumLimit() {
        return completeExpDayNumLimit;
    }

    public void setCompleteExpDayNumLimit(int completeExpDayNumLimit) {
        this.completeExpDayNumLimit = completeExpDayNumLimit;
    }

    public boolean isCheckTeAnSwitch() {
        return checkTeAnSwitch;
    }

    public void setCheckTeAnSwitch(boolean checkTeAnSwitch) {
        this.checkTeAnSwitch = checkTeAnSwitch;
    }

    public boolean getBoardCombinationRouterSwitch() {
        return boardCombinationRouterSwitch;
    }

    public void setBoardCombinationRouterSwitch(boolean boardCombinationRouterSwitch) {
        this.boardCombinationRouterSwitch = boardCombinationRouterSwitch;
    }

    public boolean isPdaVersionSwitch() {
        return pdaVersionSwitch;
    }

    public void setPdaVersionSwitch(boolean pdaVersionSwitch) {
        this.pdaVersionSwitch = pdaVersionSwitch;
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

    public boolean isAllianceBusinessSwitch() {
        return allianceBusinessSwitch;
    }

    public void setAllianceBusinessSwitch(boolean allianceBusinessSwitch) {
        this.allianceBusinessSwitch = allianceBusinessSwitch;
    }

    public Integer getJyTransportSendVehicleValidateDockRefreshTime() {
        return jyTransportSendVehicleValidateDockRefreshTime;
    }

    public void setJyTransportSendVehicleValidateDockRefreshTime(Integer jyTransportSendVehicleValidateDockRefreshTime) {
        this.jyTransportSendVehicleValidateDockRefreshTime = jyTransportSendVehicleValidateDockRefreshTime;
    }

    public Integer getJyTransportSendVehicleValidateDockAllowRefreshTimes() {
        return jyTransportSendVehicleValidateDockAllowRefreshTimes;
    }

    public void setJyTransportSendVehicleValidateDockAllowRefreshTimes(Integer jyTransportSendVehicleValidateDockAllowRefreshTimes) {
        this.jyTransportSendVehicleValidateDockAllowRefreshTimes = jyTransportSendVehicleValidateDockAllowRefreshTimes;
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
    public ClientAutoRefreshConfig getJyWorkAppAutoRefreshConfigByBusinessType(String businessType) {
        if(CollectionUtils.isNotEmpty(jyWorkAppAutoRefreshConfigList)){
            final Optional<ClientAutoRefreshConfig> first = jyWorkAppAutoRefreshConfigList.stream().filter(item -> Objects.equals(businessType, item.getBusinessType())).findFirst();
            if(first.isPresent()){
                ClientAutoRefreshConfig config = new ClientAutoRefreshConfig();
                BeanUtils.copyProperties(first.get(), config);
                return config;
            }
        }
        return null;
    }

    public String getTeAnSiteWhitelist() {
        return teAnSiteWhitelist;
    }

    public void setTeAnSiteWhitelist(String teAnSiteWhitelist) {
        this.teAnSiteWhitelist = teAnSiteWhitelist;
        this.teAnSiteWhitelistStrList = this.getTeAnSiteWhitelistStrList();
    }

    private List<String> teAnSiteWhitelistStrList = new ArrayList<>();

    public List<String> getTeAnSiteWhitelistStrList() {
        if(teAnSiteWhitelist == null){
            return new ArrayList<>();
        }
        return Arrays.asList(teAnSiteWhitelist.split(Constants.SEPARATOR_COMMA));
    }

    public boolean matchTeAnSiteWhitelist(int siteId) {
        if(StringUtils.isBlank(teAnSiteWhitelist)){
            return false;
        }
        if(Objects.equals(Constants.STR_ALL, teAnSiteWhitelist)){
            return true;
        }
        if(teAnSiteWhitelistStrList.contains(String.valueOf(siteId))){
            return true;
        }
        return false;
    }

    public boolean isUploadOverWeightSwitch() {
        return uploadOverWeightSwitch;
    }

    public void setUploadOverWeightSwitch(boolean uploadOverWeightSwitch) {
        this.uploadOverWeightSwitch = uploadOverWeightSwitch;
    }

    public String getExceptionSubmitCheckSites() {
        return exceptionSubmitCheckSites;
    }

    public void setExceptionSubmitCheckSites(String exceptionSubmitCheckSites) {
        this.exceptionSubmitCheckSites = exceptionSubmitCheckSites;
        this.setExceptionSubmitCheckSiteList(exceptionSubmitCheckSites);
    }

    private List<String> exceptionSubmitCheckSiteList = new ArrayList<>();

    public void setExceptionSubmitCheckSiteList(String exceptionSubmitCheckSites) {
        if(exceptionSubmitCheckSites == null){
            exceptionSubmitCheckSiteList = new ArrayList<>();
            return;
        }
        exceptionSubmitCheckSiteList = Arrays.asList(exceptionSubmitCheckSites.split(Constants.SEPARATOR_COMMA));
    }

    public boolean matchExceptionSubmitCheckSite(int siteId) {
        if(StringUtils.isBlank(exceptionSubmitCheckSites)){
            return false;
        }
        if(Objects.equals(Constants.STR_ALL, exceptionSubmitCheckSites)){
            return true;
        }
        if(exceptionSubmitCheckSiteList.contains(String.valueOf(siteId))){
            return true;
        }
        return false;
    }

    public String getExceptionSubmitCheckWaybillInterceptTypes() {
        return exceptionSubmitCheckWaybillInterceptTypes;
    }

    public void setExceptionSubmitCheckWaybillInterceptTypes(String exceptionSubmitCheckWaybillInterceptTypes) {
        this.exceptionSubmitCheckWaybillInterceptTypes = exceptionSubmitCheckWaybillInterceptTypes;
        this.setExceptionSubmitCheckWaybillInterceptTypeList(exceptionSubmitCheckWaybillInterceptTypes);
    }

    private List<String> exceptionSubmitCheckWaybillInterceptTypeList = new ArrayList<>();

    public void setExceptionSubmitCheckWaybillInterceptTypeList(String exceptionSubmitCheckWaybillInterceptTypes) {
        if(exceptionSubmitCheckWaybillInterceptTypes == null){
            exceptionSubmitCheckWaybillInterceptTypeList = new ArrayList<>();
            return;
        }
        exceptionSubmitCheckWaybillInterceptTypeList = Arrays.asList(exceptionSubmitCheckWaybillInterceptTypes.split(Constants.SEPARATOR_COMMA));
    }

    public boolean matchExceptionSubmitCheckWaybillInterceptType(Integer interceptType) {
        if(StringUtils.isBlank(exceptionSubmitCheckWaybillInterceptTypes)){
            return false;
        }
        if(Objects.equals(Constants.STR_ALL, exceptionSubmitCheckWaybillInterceptTypes)){
            return true;
        }
        if(interceptType == null){
            return false;
        }
        if(exceptionSubmitCheckWaybillInterceptTypeList.contains(String.valueOf(interceptType))){
            return true;
        }
        return false;
    }

    public boolean isIgnoreTysTrackSwitch() {
        return ignoreTysTrackSwitch;
    }

    public void setIgnoreTysTrackSwitch(boolean ignoreTysTrackSwitch) {
        this.ignoreTysTrackSwitch = ignoreTysTrackSwitch;
    }


    public int getAssignExpTaskQuantityLimit() {
        return assignExpTaskQuantityLimit;
    }

    public void setAssignExpTaskQuantityLimit(int assignExpTaskQuantityLimit) {
        this.assignExpTaskQuantityLimit = assignExpTaskQuantityLimit;
    }

    public Integer getJyWarehouseSendVehicleDetailQueryDefaultLimitSize() {
        return JyWarehouseSendVehicleDetailQueryDefaultLimitSize;
    }

    public void setJyWarehouseSendVehicleDetailQueryDefaultLimitSize(Integer jyWarehouseSendVehicleDetailQueryDefaultLimitSize) {
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

    public boolean isBeforeSealVehicleLoadRateLimitCheckSwitch() {
        return beforeSealVehicleLoadRateLimitCheckSwitch;
    }

    public void setBeforeSealVehicleLoadRateLimitCheckSwitch(boolean beforeSealVehicleLoadRateLimitCheckSwitch) {
        this.beforeSealVehicleLoadRateLimitCheckSwitch = beforeSealVehicleLoadRateLimitCheckSwitch;
    }

    public double getVehicleIntegralPriorityFraction() {
        return vehicleIntegralPriorityFraction;
    }

    public void setVehicleIntegralPriorityFraction(double vehicleIntegralPriorityFraction) {
        this.vehicleIntegralPriorityFraction = vehicleIntegralPriorityFraction;
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

    public AutoCloseJyBizTaskConfig getAutoCloseJyBizTaskConfigObj() {
        AutoCloseJyBizTaskConfig config = new AutoCloseJyBizTaskConfig();
        BeanUtils.copyProperties(autoCloseJyBizTaskConfigObj, config);
        return config;
    }

    public void setAutoCloseJyBizTaskConfigObj(AutoCloseJyBizTaskConfig autoCloseJyBizTaskConfigObj) {
        this.autoCloseJyBizTaskConfigObj = autoCloseJyBizTaskConfigObj;
    }

    public List<String> getExceptionSubmitCheckSiteList() {
        return exceptionSubmitCheckSiteList;
    }

    public void setExceptionSubmitCheckSiteList(List<String> exceptionSubmitCheckSiteList) {
        this.exceptionSubmitCheckSiteList = exceptionSubmitCheckSiteList;
    }

    public List<String> getExceptionSubmitCheckWaybillInterceptTypeList() {
        return exceptionSubmitCheckWaybillInterceptTypeList;
    }

    public void setExceptionSubmitCheckWaybillInterceptTypeList(List<String> exceptionSubmitCheckWaybillInterceptTypeList) {
        this.exceptionSubmitCheckWaybillInterceptTypeList = exceptionSubmitCheckWaybillInterceptTypeList;
    }

    public void setDpSpringSiteCodeList(List<Integer> dpSpringSiteCodeList) {
        this.dpSpringSiteCodeList = dpSpringSiteCodeList;
    }

    public void setDewuCustomerCodeList(List<String> dewuCustomerCodeList) {
        this.dewuCustomerCodeList = dewuCustomerCodeList;
    }

    public void setTeAnSiteWhitelistStrList(List<String> teAnSiteWhitelistStrList) {
        this.teAnSiteWhitelistStrList = teAnSiteWhitelistStrList;
    }

    public List<String> getIdentityRecogniseSiteSwitchList() {
        return identityRecogniseSiteSwitchList;
    }

    public void setIdentityRecogniseSiteSwitchList(List<String> identityRecogniseSiteSwitchList) {
        this.identityRecogniseSiteSwitchList = identityRecogniseSiteSwitchList;
    }

    public Integer getCttGroupDataLimit() {
        return cttGroupDataLimit;
    }

    public void setCttGroupDataLimit(Integer cttGroupDataLimit) {
        this.cttGroupDataLimit = cttGroupDataLimit;
    }

    public Integer getJySendSpecialProductTypeToScanShowRemainMinutes() {
        return jySendSpecialProductTypeToScanShowRemainMinutes;
    }

    public void setJySendSpecialProductTypeToScanShowRemainMinutes(Integer jySendSpecialProductTypeToScanShowRemainMinutes) {
        this.jySendSpecialProductTypeToScanShowRemainMinutes = jySendSpecialProductTypeToScanShowRemainMinutes;
    }

    public int getJyExceptionDamageTaskCustomerNotReturnHours() {
        return jyExceptionDamageTaskCustomerNotReturnHours;
    }

    public void setJyExceptionDamageTaskCustomerNotReturnHours(int jyExceptionDamageTaskCustomerNotReturnHours) {
        this.jyExceptionDamageTaskCustomerNotReturnHours = jyExceptionDamageTaskCustomerNotReturnHours;
    }

    public boolean isJyExceptionCreateBizIdSwitch() {
        return jyExceptionCreateBizIdSwitch;
    }

    public void setJyExceptionCreateBizIdSwitch(boolean jyExceptionCreateBizIdSwitch) {
        this.jyExceptionCreateBizIdSwitch = jyExceptionCreateBizIdSwitch;
    }

    /**
     * 违禁品运单缓存时长
     */
    private int contrabandWaybillCacheTime;

    public int getContrabandWaybillCacheTime() {
        return contrabandWaybillCacheTime;
    }

    public void setContrabandWaybillCacheTime(int contrabandWaybillCacheTime) {
        this.contrabandWaybillCacheTime = contrabandWaybillCacheTime;
    }

    public Integer getReverseExchangeCount() {
        return reverseExchangeCount;
    }

    public void setReverseExchangeCount(Integer reverseExchangeCount) {
        this.reverseExchangeCount = reverseExchangeCount;
    }

    public Boolean getSendJyOperateFlowMqSwitch() {
        return sendJyOperateFlowMqSwitch;
    }

    public void setSendJyOperateFlowMqSwitch(Boolean sendJyOperateFlowMqSwitch) {
        this.sendJyOperateFlowMqSwitch = sendJyOperateFlowMqSwitch;
    }

    public boolean isbTemplateRouterExecuteNew() {
        return bTemplateRouterExecuteNew;
    }

    public void setbTemplateRouterExecuteNew(boolean bTemplateRouterExecuteNew) {
        this.bTemplateRouterExecuteNew = bTemplateRouterExecuteNew;
    }

    public String getJyCollectPackageTaskQueryTimeLimit() {
        return jyCollectPackageTaskQueryTimeLimit;
    }

    public void setJyCollectPackageTaskQueryTimeLimit(String jyCollectPackageTaskQueryTimeLimit) {
        this.jyCollectPackageTaskQueryTimeLimit = jyCollectPackageTaskQueryTimeLimit;
    }

    public boolean getMixedConfigUseBasicNew() {
        return mixedConfigUseBasicNew;
    }

    public void setMixedConfigUseBasicNew(boolean mixedConfigUseBasicNew) {
        this.mixedConfigUseBasicNew = mixedConfigUseBasicNew;
    }

    public boolean getCollectPackageTaskRefreshSwitch() {
        return collectPackageTaskRefreshSwitch;
    }

    public void setCollectPackageTaskRefreshSwitch(boolean collectPackageTaskRefreshSwitch) {
        this.collectPackageTaskRefreshSwitch = collectPackageTaskRefreshSwitch;
    }

    /**
     * 给HrUserManager 用的自定义上级，仅uat测试使用
     */
    @Setter
    @Getter
    private String superiorErpKV;

    public String getSealSyncOpenCloseSendTaskSwitch() {
        return sealSyncOpenCloseSendTaskSwitch;
    }

    public void setSealSyncOpenCloseSendTaskSwitch(String sealSyncOpenCloseSendTaskSwitch) {
        this.sealSyncOpenCloseSendTaskSwitch = sealSyncOpenCloseSendTaskSwitch;
    }

    public String getTerminalSiteTypeForBoxType() {
        return terminalSiteTypeForBoxType;
    }

    public void setTerminalSiteTypeForBoxType(String terminalSiteTypeForBoxType) {
        this.terminalSiteTypeForBoxType = terminalSiteTypeForBoxType;
        this.setTerminalSiteTypeListForBoxType();
    }

    public List<Integer> getTerminalSiteTypeListForBoxType() {
        return terminalSiteTypeListForBoxType;
    }

    public void setTerminalSiteTypeListForBoxType() {
        List<String> terminalSiteTypeList = new ArrayList<>();
        if(StringUtils.isNotBlank(terminalSiteTypeForBoxType)){
            final String[] split = terminalSiteTypeForBoxType.split(Constants.SEPARATOR_COMMA);
            terminalSiteTypeList = Arrays.asList(split);
        }
        this.terminalSiteTypeListForBoxType.clear();
        for (String siteCodeStr : terminalSiteTypeList) {
            this.terminalSiteTypeListForBoxType.add(Integer.valueOf(siteCodeStr));
        }
    }

    public boolean getBoxTypeNewVersionSwitch() {
        return boxTypeNewVersionSwitch;
    }

    public UccPropertyConfiguration setBoxTypeNewVersionSwitch(boolean boxTypeNewVersionSwitch) {
        this.boxTypeNewVersionSwitch = boxTypeNewVersionSwitch;
        return this;
    }

    public Integer getCzSealCarBoardCountMinLimit() {
        return czSealCarBoardCountMinLimit;
    }

    public void setCzSealCarBoardCountMinLimit(Integer czSealCarBoardCountMinLimit) {
        this.czSealCarBoardCountMinLimit = czSealCarBoardCountMinLimit;
    }

    public Integer getCzSealCarPackageCountMinLimit() {
        return czSealCarPackageCountMinLimit;
    }

    public void setCzSealCarPackageCountMinLimit(Integer czSealCarPackageCountMinLimit) {
        this.czSealCarPackageCountMinLimit = czSealCarPackageCountMinLimit;
    }

    public Double getJyAviationManualCreateSendTaskMaxBookingWeight() {
        return jyAviationManualCreateSendTaskMaxBookingWeight;
    }

    public void setJyAviationManualCreateSendTaskMaxBookingWeight(Double jyAviationManualCreateSendTaskMaxBookingWeight) {
        this.jyAviationManualCreateSendTaskMaxBookingWeight = jyAviationManualCreateSendTaskMaxBookingWeight;
    }

    public String getFindGoodSendMessageDefaultErp() {
        return findGoodSendMessageDefaultErp;
    }

    public void setFindGoodSendMessageDefaultErp(String findGoodSendMessageDefaultErp) {
        this.findGoodSendMessageDefaultErp = findGoodSendMessageDefaultErp;
    }

    public Integer getJyWarehouseToSendPlanTimeBeginHours() {
        return jyWarehouseToSendPlanTimeBeginHours;
    }

    public void setJyWarehouseToSendPlanTimeBeginHours(Integer jyWarehouseToSendPlanTimeBeginHours) {
        this.jyWarehouseToSendPlanTimeBeginHours = jyWarehouseToSendPlanTimeBeginHours;
    }

    public Integer getJyWarehouseToSendPlanTimeEndHours() {
        return jyWarehouseToSendPlanTimeEndHours;
    }

    public void setJyWarehouseToSendPlanTimeEndHours(Integer jyWarehouseToSendPlanTimeEndHours) {
        this.jyWarehouseToSendPlanTimeEndHours = jyWarehouseToSendPlanTimeEndHours;
    }

    public Integer getJyWarehouseSendingPlanTimeBeginHours() {
        return jyWarehouseSendingPlanTimeBeginHours;
    }

    public void setJyWarehouseSendingPlanTimeBeginHours(Integer jyWarehouseSendingPlanTimeBeginHours) {
        this.jyWarehouseSendingPlanTimeBeginHours = jyWarehouseSendingPlanTimeBeginHours;
    }

    public Integer getJyWarehouseSendingPlanTimeEndHours() {
        return jyWarehouseSendingPlanTimeEndHours;
    }

    public void setJyWarehouseSendingPlanTimeEndHours(Integer jyWarehouseSendingPlanTimeEndHours) {
        this.jyWarehouseSendingPlanTimeEndHours = jyWarehouseSendingPlanTimeEndHours;
    }

    public Integer getJyWarehouseToSealPlanTimeBeginHours() {
        return jyWarehouseToSealPlanTimeBeginHours;
    }

    public void setJyWarehouseToSealPlanTimeBeginHours(Integer jyWarehouseToSealPlanTimeBeginHours) {
        this.jyWarehouseToSealPlanTimeBeginHours = jyWarehouseToSealPlanTimeBeginHours;
    }

    public Integer getJyWarehouseToSealPlanTimeEndHours() {
        return jyWarehouseToSealPlanTimeEndHours;
    }

    public void setJyWarehouseToSealPlanTimeEndHours(Integer jyWarehouseToSealPlanTimeEndHours) {
        this.jyWarehouseToSealPlanTimeEndHours = jyWarehouseToSealPlanTimeEndHours;
    }

    public Integer getJyWarehouseSealedPlanTimeBeginHours() {
        return jyWarehouseSealedPlanTimeBeginHours;
    }

    public void setJyWarehouseSealedPlanTimeBeginHours(Integer jyWarehouseSealedPlanTimeBeginHours) {
        this.jyWarehouseSealedPlanTimeBeginHours = jyWarehouseSealedPlanTimeBeginHours;
    }

    public Integer getJyWarehouseSealedPlanTimeEndHours() {
        return jyWarehouseSealedPlanTimeEndHours;
    }

    public void setJyWarehouseSealedPlanTimeEndHours(Integer jyWarehouseSealedPlanTimeEndHours) {
        this.jyWarehouseSealedPlanTimeEndHours = jyWarehouseSealedPlanTimeEndHours;
    }

    public Integer getJyWarehouseTaskCreateTimeBeginHours() {
        return jyWarehouseTaskCreateTimeBeginHours;
    }

    public void setJyWarehouseTaskCreateTimeBeginHours(Integer jyWarehouseTaskCreateTimeBeginHours) {
        this.jyWarehouseTaskCreateTimeBeginHours = jyWarehouseTaskCreateTimeBeginHours;
    }

    public String getJyWarehouseManualTaskKeyVehicleTypes() {
        return jyWarehouseManualTaskKeyVehicleTypes;
    }

    public void setJyWarehouseManualTaskKeyVehicleTypes(String jyWarehouseManualTaskKeyVehicleTypes) {
        this.jyWarehouseManualTaskKeyVehicleTypes = jyWarehouseManualTaskKeyVehicleTypes;
    }

    public String getWaybillZeroWeightInterceptSites() {
        return waybillZeroWeightInterceptSites;
    }

    public void setWaybillZeroWeightInterceptSites(String waybillZeroWeightInterceptSites) {
        this.waybillZeroWeightInterceptSites = waybillZeroWeightInterceptSites;
    }

    public Boolean getAutomaticWeightVolumeUpperCheckSwitch() {
        return automaticWeightVolumeUpperCheckSwitch;
    }

    public void setAutomaticWeightVolumeUpperCheckSwitch(Boolean automaticWeightVolumeUpperCheckSwitch) {
        this.automaticWeightVolumeUpperCheckSwitch = automaticWeightVolumeUpperCheckSwitch;
    }

    public String getTeanWorkAreaCodes() {
        return teanWorkAreaCodes;
    }

    public void setTeanWorkAreaCodes(String teanWorkAreaCodes) {
        this.teanWorkAreaCodes = teanWorkAreaCodes;
        this.setTeanWorkAreaCodesList();
    }

    public List<String> getTeanWorkAreaCodesList() {
        return teanWorkAreaCodesList;
    }

    public void setTeanWorkAreaCodesList() {
        this.teanWorkAreaCodesList.clear();
        if(StringUtils.isNotBlank(teanWorkAreaCodes)){
            final String[] split = teanWorkAreaCodes.split(Constants.SEPARATOR_COMMA);
            this.teanWorkAreaCodesList = Arrays.asList(split);
        }
    }

    public boolean isTeanWorkAreaCode(String workAreaCode){
        if(Objects.equals(this.teanWorkAreaCodes, Constants.STR_ALL)){
            return true;
        }
        return this.teanWorkAreaCodesList.contains(workAreaCode);
    }

    public String getTeanMixScanWorkAreaCodes() {
        return teanMixScanWorkAreaCodes;
    }

    public void setTeanMixScanWorkAreaCodes(String teanMixScanWorkAreaCodes) {
        this.teanMixScanWorkAreaCodes = teanMixScanWorkAreaCodes;
        this.setTeanMixScanWorkAreaCodesList();
    }

    public List<String> getTeanMixScanWorkAreaCodesList() {
        return teanMixScanWorkAreaCodesList;
    }

    public void setTeanMixScanWorkAreaCodesList() {
        this.teanMixScanWorkAreaCodesList.clear();
        if(StringUtils.isNotBlank(teanMixScanWorkAreaCodes)){
            final String[] split = teanMixScanWorkAreaCodes.split(Constants.SEPARATOR_COMMA);
            this.teanMixScanWorkAreaCodesList = Arrays.asList(split);
        }
    }

    public boolean isTeanMixScanWorkAreaCode(String workAreaCode){
        if(Objects.equals(this.teanMixScanWorkAreaCodes, Constants.STR_ALL)){
            return true;
        }
        return this.teanMixScanWorkAreaCodesList.contains(workAreaCode);
    }

    public String getTeanSiteIdWhiteListStr4InterceptFilter() {
        return teanSiteIdWhiteListStr4InterceptFilter;
    }

    public void setTeanSiteIdWhiteListStr4InterceptFilter(String teanSiteIdWhiteListStr4InterceptFilter) {
        this.teanSiteIdWhiteListStr4InterceptFilter = teanSiteIdWhiteListStr4InterceptFilter;
        this.setTeanSiteIdWhiteList4InterceptFilter();
    }

    public List<Integer> getTeanSiteIdWhiteList4InterceptFilter() {
        return teanSiteIdWhiteList4InterceptFilter;
    }

    public void setTeanSiteIdWhiteList4InterceptFilter() {
        this.teanSiteIdWhiteList4InterceptFilter.clear();
        if(StringUtils.isNotBlank(teanSiteIdWhiteListStr4InterceptFilter)){
            if(Objects.equals(this.teanSiteIdWhiteListStr4InterceptFilter, Constants.STR_ALL)){
                return;
            }
            this.teanSiteIdWhiteList4InterceptFilter = Arrays.stream(teanSiteIdWhiteListStr4InterceptFilter.split(Constants.SEPARATOR_COMMA))
                    .map(Integer::valueOf).collect(Collectors.toList());
        }
    }

    public boolean isTeanSiteIdWhite4InterceptFilter(Integer siteCode){
        if(Objects.equals(this.teanSiteIdWhiteListStr4InterceptFilter, Constants.STR_ALL)){
            return true;
        }
        return this.teanSiteIdWhiteList4InterceptFilter.contains(siteCode);
    }

    public String getTeanSiteIdEnableListStr4InterceptFilter() {
        return teanSiteIdEnableListStr4InterceptFilter;
    }

    public void setTeanSiteIdEnableListStr4InterceptFilter(String teanSiteIdWhiteListStr4InterceptFilter) {
        this.teanSiteIdEnableListStr4InterceptFilter = teanSiteIdWhiteListStr4InterceptFilter;
        this.setTeanSiteIdEnableList4InterceptFilter();
    }

    public List<Integer> getTeanSiteIdEnableList4InterceptFilter() {
        return teanSiteIdEnableList4InterceptFilter;
    }

    public void setTeanSiteIdEnableList4InterceptFilter() {
        this.teanSiteIdEnableList4InterceptFilter.clear();
        if(StringUtils.isNotBlank(teanSiteIdEnableListStr4InterceptFilter)){
            if(Objects.equals(this.teanSiteIdEnableListStr4InterceptFilter, Constants.STR_ALL)){
                return;
            }
            this.teanSiteIdEnableList4InterceptFilter = Arrays.stream(teanSiteIdEnableListStr4InterceptFilter.split(Constants.SEPARATOR_COMMA))
                    .map(Integer::valueOf).collect(Collectors.toList());
        }
    }

    public boolean isTeanSiteIdEnable4InterceptFilter(Integer siteCode){
        if(Objects.equals(this.teanSiteIdEnableListStr4InterceptFilter, Constants.STR_ALL)){
            return true;
        }
        return this.teanSiteIdEnableList4InterceptFilter.contains(siteCode);
    }

    private boolean needCollectLoadingBoxflowCheck;
    public boolean getNeedCollectLoadingBoxflowCheck() {
        return needCollectLoadingBoxflowCheck;
    }
    public void setNeedCollectLoadingBoxflowCheck(boolean needCollectLoadingBoxflowCheck){
        this.needCollectLoadingBoxflowCheck =needCollectLoadingBoxflowCheck;
    }

    public Integer getPackageArriveAutoInspectionRetryMinutes() {
        return packageArriveAutoInspectionRetryMinutes;
    }

    public void setPackageArriveAutoInspectionRetryMinutes(Integer packageArriveAutoInspectionRetryMinutes) {
        this.packageArriveAutoInspectionRetryMinutes = packageArriveAutoInspectionRetryMinutes;
    }

    public Boolean getPdaUnloadAndAutoInspectionRejectSwitch() {
        return pdaUnloadAndAutoInspectionRejectSwitch;
    }

    public void setPdaUnloadAndAutoInspectionRejectSwitch(Boolean pdaUnloadAndAutoInspectionRejectSwitch) {
        this.pdaUnloadAndAutoInspectionRejectSwitch = pdaUnloadAndAutoInspectionRejectSwitch;
    }

    public Integer getPackageArriveAutoInspectionNullTaskRetryMinutes() {
        return packageArriveAutoInspectionNullTaskRetryMinutes;
    }

    public void setPackageArriveAutoInspectionNullTaskRetryMinutes(Integer packageArriveAutoInspectionNullTaskRetryMinutes) {
        this.packageArriveAutoInspectionNullTaskRetryMinutes = packageArriveAutoInspectionNullTaskRetryMinutes;
    }

    /**
     * LL箱号内嵌箱号数量限制
     */
    private int LLContainBoxNumberLimit;

    public int getLLContainBoxNumberLimit() {
        return LLContainBoxNumberLimit;
    }

    public void setLLContainBoxNumberLimit(int LLContainBoxNumberLimit) {
        this.LLContainBoxNumberLimit = LLContainBoxNumberLimit;
    }

    public boolean isOperateFlowNewSwitch() {
        return operateFlowNewSwitch;
    }

    public void setOperateFlowNewSwitch(boolean operateFlowNewSwitch) {
        this.operateFlowNewSwitch = operateFlowNewSwitch;
    }

    /**
     * 寄付运单称重拦截开关
     */
    private boolean waybillJFWeightInterceptSwitch;

    public boolean getWaybillJFWeightInterceptSwitch() {
        return waybillJFWeightInterceptSwitch;
    }

    public void setWaybillJFWeightInterceptSwitch(boolean waybillJFWeightInterceptSwitch) {
        this.waybillJFWeightInterceptSwitch = waybillJFWeightInterceptSwitch;
    }

    public Boolean getPdaSilentUpdateSwitch() {
        return pdaSilentUpdateSwitch;
    }

    public void setPdaSilentUpdateSwitch(Boolean pdaSilentUpdateSwitch) {
        this.pdaSilentUpdateSwitch = pdaSilentUpdateSwitch;
    }

    public String getInterceptExceptionSiteIdEnableListStr() {
        return interceptExceptionSiteIdEnableListStr;
    }

    public void setInterceptExceptionSiteIdEnableListStr(String interceptExceptionSiteIdEnableListStr) {
        this.interceptExceptionSiteIdEnableListStr = interceptExceptionSiteIdEnableListStr;
        this.setInterceptExceptionSiteIdEnableList();
    }

    public List<Integer> getInterceptExceptionSiteIdEnableList() {
        return interceptExceptionSiteIdEnableList;
    }

    public void setInterceptExceptionSiteIdEnableList() {
        this.interceptExceptionSiteIdEnableList.clear();
        if(StringUtils.isNotBlank(interceptExceptionSiteIdEnableListStr)){
            if(Objects.equals(this.interceptExceptionSiteIdEnableListStr, Constants.STR_ALL)){
                return;
            }
            this.interceptExceptionSiteIdEnableList = Arrays.stream(interceptExceptionSiteIdEnableListStr.split(Constants.SEPARATOR_COMMA))
                    .map(Integer::valueOf).collect(Collectors.toList());
        }
    }

    public boolean isInterceptExceptionSiteIdEnable(Integer siteCode){
        if(Objects.equals(this.interceptExceptionSiteIdEnableListStr, Constants.STR_ALL)){
            return true;
        }
        return this.interceptExceptionSiteIdEnableList.contains(siteCode);
    }

    private String imageClarityThreshold;

    private boolean checkBoxIfEmptySwitch;

    public boolean getCheckBoxIfEmptySwitch() {
        return checkBoxIfEmptySwitch;
    }

    public void setCheckBoxIfEmptySwitch(boolean checkBoxIfEmptySwitch) {
        this.checkBoxIfEmptySwitch = checkBoxIfEmptySwitch;
    }

    public String getImageClarityThreshold() {
        return imageClarityThreshold;
    }

    public void setImageClarityThreshold(String imageClarityThreshold) {
        this.imageClarityThreshold = imageClarityThreshold;
    }

    private boolean jyCollectPackCheckBoxEndSiteMatchSwitch;

    public boolean getJyCollectPackCheckBoxEndSiteMatchSwitch() {
        return jyCollectPackCheckBoxEndSiteMatchSwitch;
    }

    public void setJyCollectPackCheckBoxEndSiteMatchSwitch(boolean jyCollectPackCheckBoxEndSiteMatchSwitch) {
        this.jyCollectPackCheckBoxEndSiteMatchSwitch = jyCollectPackCheckBoxEndSiteMatchSwitch;
    }

    public String getRouterDynamicLineReplaceEnableSiteStr() {
        return routerDynamicLineReplaceEnableSiteStr;
    }

    public void setRouterDynamicLineReplaceEnableSiteStr(String routerDynamicLineReplaceEnableSiteStr) {
        this.routerDynamicLineReplaceEnableSiteStr = routerDynamicLineReplaceEnableSiteStr;
        this.setRouterDynamicLineReplaceEnableSiteList();
    }

    public List<Integer> getRouterDynamicLineReplaceEnableSiteList() {
        return routerDynamicLineReplaceEnableSiteList;
    }

    public void setRouterDynamicLineReplaceEnableSiteList() {
        this.routerDynamicLineReplaceEnableSiteList.clear();
        if(StringUtils.isNotBlank(routerDynamicLineReplaceEnableSiteStr)){
            if(Objects.equals(this.routerDynamicLineReplaceEnableSiteStr, Constants.STR_ALL)){
                return;
            }
            this.routerDynamicLineReplaceEnableSiteList = Arrays.stream(routerDynamicLineReplaceEnableSiteStr.split(Constants.SEPARATOR_COMMA))
                    .map(Integer::valueOf).collect(Collectors.toList());
        }
    }
    public boolean isRouterDynamicLineReplaceEnableSite(Integer siteCode){
        if(Objects.equals(this.routerDynamicLineReplaceEnableSiteStr, Constants.STR_ALL)){
            return true;
        }
        return this.routerDynamicLineReplaceEnableSiteList.contains(siteCode);
    }

    public String getSelfCheckTaskConfigCode() {
        return selfCheckTaskConfigCode;
    }

    public void setSelfCheckTaskConfigCode(String selfCheckTaskConfigCode) {
        this.selfCheckTaskConfigCode = selfCheckTaskConfigCode;
    }

    public Boolean getCollectPackageTaskStatisticsUseIndependentInterfaceSwitch() {
        return collectPackageTaskStatisticsUseIndependentInterfaceSwitch;
    }

    public void setCollectPackageTaskStatisticsUseIndependentInterfaceSwitch(Boolean collectPackageTaskStatisticsUseIndependentInterfaceSwitch) {
        this.collectPackageTaskStatisticsUseIndependentInterfaceSwitch = collectPackageTaskStatisticsUseIndependentInterfaceSwitch;
    }

    /** 装笼全局开关 **/
    private boolean cageSwitch;

    public boolean isCageSwitch() {
        return cageSwitch;
    }

    public void setCageSwitch(boolean cageSwitch) {
        this.cageSwitch = cageSwitch;
    }

    public Boolean getRestRateLimiterSwitch() {
        return restRateLimiterSwitch;
    }

    public void setRestRateLimiterSwitch(Boolean restRateLimiterSwitch) {
        this.restRateLimiterSwitch = restRateLimiterSwitch;
    }

    public String getRestRateLimiterTenant() {
        return restRateLimiterTenant;
    }

    public void setRestRateLimiterTenant(String restRateLimiterTenant) {
        this.restRateLimiterTenant = restRateLimiterTenant;
    }

    public String getRestRateLimiterBody() {
        return restRateLimiterBody;
    }

    public void setRestRateLimiterBody(String restRateLimiterBody) {
        this.restRateLimiterBody = restRateLimiterBody;
    }

    public String getRestRateLimiterWhiteList() {
        return restRateLimiterWhiteList;
    }

    public void setRestRateLimiterWhiteList(String restRateLimiterWhiteList) {
        this.restRateLimiterWhiteList = restRateLimiterWhiteList;
    }

    public String getRestRateLimiterBlackIpList() {
        return restRateLimiterBlackIpList;
    }

    public void setRestRateLimiterBlackIpList(String restRateLimiterBlackIpList) {
        this.restRateLimiterBlackIpList = restRateLimiterBlackIpList;
    }

    public String getRestRateLimiterBlackErpList() {
        return restRateLimiterBlackErpList;
    }

    public void setRestRateLimiterBlackErpList(String restRateLimiterBlackErpList) {
        this.restRateLimiterBlackErpList = restRateLimiterBlackErpList;
    }

    public String getRestRateLimiterFallbackGlobalTip() {
        return restRateLimiterFallbackGlobalTip;
    }

    public void setRestRateLimiterFallbackGlobalTip(String restRateLimiterFallbackGlobalTip) {
        this.restRateLimiterFallbackGlobalTip = restRateLimiterFallbackGlobalTip;
    }

    public String getRestRateLimiterFallbackTips() {
        return restRateLimiterFallbackTips;
    }

    public void setRestRateLimiterFallbackTips(String restRateLimiterFallbackTips) {
        this.restRateLimiterFallbackTips = restRateLimiterFallbackTips;
    }

    public Boolean getBoxTransferSendInsertReceiveSwitch() {
        return boxTransferSendInsertReceiveSwitch;
    }

    public void setBoxTransferSendInsertReceiveSwitch(Boolean boxTransferSendInsertReceiveSwitch) {
        this.boxTransferSendInsertReceiveSwitch = boxTransferSendInsertReceiveSwitch;
    }
}