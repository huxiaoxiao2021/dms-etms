package com.jd.bluedragon.configuration.ucc;

/**
 * Created by xumei3 on 2017/12/15.
 */
public class UccPropertyConfiguration {

    /** 开启的多级异步缓冲组件的任务类型列表 **/
    private String asynbufferEnabledTaskType;

    /** cassandra服务的全局开关 **/
    private boolean cassandraGlobalSwitch;

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


    /** 分拣动作选取的service DMS、MIDDLEEND、FAILOVER**/
    private String sortingServiceMode;

    /** 出管新接口-写入方法开关 true 调用新接口，false 调用老接口**/
    private boolean chuguanNewInterfaceInsertSwitch;

    /** 出管新接口-查询方法开关 true 调用新接口，false 调用老接口**/
    private boolean chuguanNewInterfaceQuerySwitch;


    /** 出管新接口-页面查询方法开关 true 调用新接口，false 调用老接口**/
    private boolean chuguanNewPageQuerySwitch;

    /**
     * 驻场打印 是否开启校验 鸡毛信必输设备号；true 开启,false 不开启
     */
    private boolean stationPrintFeatherLetterCheck;

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
     * b2b分拣补验货开关
     */
    private boolean b2bPushInspectionSwitch;

    /**
     * 冷链卡班短信开关;true 开启
     */
    private boolean coldChainStorageSmsSwitch;

    /**
     * 离线任务的操作时间的更正时间范围
     */
    private int offlineTaskOperateTimeCorrectHours;

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

    public boolean isLogToBusinessLogByKafka() {
        return logToBusinessLogByKafka;
    }

    public void setLogToBusinessLogByKafka(boolean logToBusinessLogByKafka) {
        this.logToBusinessLogByKafka = logToBusinessLogByKafka;
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

    public boolean isStationPrintFeatherLetterCheck() {
        return stationPrintFeatherLetterCheck;
    }

    public void setStationPrintFeatherLetterCheck(boolean stationPrintFeatherLetterCheck) {
        this.stationPrintFeatherLetterCheck = stationPrintFeatherLetterCheck;
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

    public boolean isColdChainStorageSmsSwitch() {
        return coldChainStorageSmsSwitch;
    }

    public void setColdChainStorageSmsSwitch(boolean coldChainStorageSmsSwitch) {
        this.coldChainStorageSmsSwitch = coldChainStorageSmsSwitch;
    }

	/**
	 * @return the b2bPushInspectionSwitch
	 */
	public boolean isB2bPushInspectionSwitch() {
		return b2bPushInspectionSwitch;
	}

	/**
	 * @param b2bPushInspectionSwitch the b2bPushInspectionSwitch to set
	 */
	public void setB2bPushInspectionSwitch(boolean b2bPushInspectionSwitch) {
		this.b2bPushInspectionSwitch = b2bPushInspectionSwitch;
	}

    public int getOfflineTaskOperateTimeCorrectHours() {
        return offlineTaskOperateTimeCorrectHours;
    }

    public void setOfflineTaskOperateTimeCorrectHours(int offlineTaskOperateTimeCorrectHours) {
        this.offlineTaskOperateTimeCorrectHours = offlineTaskOperateTimeCorrectHours;
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
}
