package com.jd.bluedragon.configuration.ucc;

import com.jd.bluedragon.Constants;
import org.apache.commons.lang.StringUtils;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * Created by xumei3 on 2017/12/15.
 */
public class UccPropertyConfiguration {

    /** 开启的多级异步缓冲组件的任务类型列表 **/
    private String asynbufferEnabledTaskType;

    /** cassandra服务的全局开关 **/
    private boolean cassandraGlobalSwitch;

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

    /** 出管新接口-写入方法开关 true 调用新接口，false 调用老接口**/
    private boolean chuguanNewInterfaceInsertSwitch;

    /** 出管新接口-查询方法开关 true 调用新接口，false 调用老接口**/
    private boolean chuguanNewInterfaceQuerySwitch;


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
     * 离线任务上传拦截报表，0 - 全部开启，-1 - 全部关闭，1243,3534表示具体场地
     */
    private String offlineTaskReportInterceptSites;

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
     * 验货集包袋依赖降级， true时不依赖集包袋服务
     */
    private boolean inspectionAssertDemotion;

    /**
     * 取消鸡毛信切换OMS接口开关
     */
    private boolean cancelJimaoxinSwitchToOMS;


    /**
     * 大宗可扫描包裹下限数量
     * @return
     */
    private Integer dazongPackageOperateMax;

    /**
     * 是否校验签单返还
     * true 校验 false 不校验
     */
    private boolean checkSignAndReturn;

    public boolean getCheckSignAndReturn() {
        return checkSignAndReturn;
    }

    public void setCheckSignAndReturn(boolean checkSignAndReturn) {
        this.checkSignAndReturn = checkSignAndReturn;
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

    public boolean isChuguanNewInterfaceInsertSwitch() {
        return chuguanNewInterfaceInsertSwitch;
    }

    public void setChuguanNewInterfaceInsertSwitch(boolean chuguanNewInterfaceInsertSwitch) {
        this.chuguanNewInterfaceInsertSwitch = chuguanNewInterfaceInsertSwitch;
    }

    public boolean isChuguanNewInterfaceQuerySwitch() {
        return chuguanNewInterfaceQuerySwitch;
    }

    public void setChuguanNewInterfaceQuerySwitch(boolean chuguanNewInterfaceQuerySwitch) {
        this.chuguanNewInterfaceQuerySwitch = chuguanNewInterfaceQuerySwitch;
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

    public boolean isCancelJimaoxinSwitchToOMS() {
        return cancelJimaoxinSwitchToOMS;
    }

    public void setCancelJimaoxinSwitchToOMS(boolean cancelJimaoxinSwitchToOMS) {
        this.cancelJimaoxinSwitchToOMS = cancelJimaoxinSwitchToOMS;
    }

    public Integer getDazongPackageOperateMax() {
        return dazongPackageOperateMax;
    }

    public void setDazongPackageOperateMax(Integer dazongPackageOperateMax) {
        this.dazongPackageOperateMax = dazongPackageOperateMax;
    }
}
