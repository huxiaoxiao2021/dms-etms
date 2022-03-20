package com.jd.bluedragon.dbrouter;

import org.slf4j.LoggerFactory;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

import java.util.HashMap;
import java.util.Map;

public class DynamicDataSources extends AbstractRoutingDataSource {

    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(DynamicDataSources.class);

    private Object defaultDataSource; //默认业务数据源，api项目为各个专享的业务数据源，custom项目默认配置公有云数据源

    private Object failShift; //迁移失败数据源

    private Object factorsMiss; //四要素缺失数据源

    private Object configCenterDataSource; //配置中心数据源

    private Object closedbate; //closedbate测试数据源

    private Object cll0814; //常聊聊专享

    private Object heling0603; //荷领专享

    private Object xiangliaobank; //配置中心数据源

    private Object xianliao1229; //配置中心数据源

    private Object zhiweilai; //配置中心数据源

    private Object cjbank; //配置中心数据源

    private Object aiyueba20180309; //配置中心数据源

    private Object pgyu6atqp5dbu; //配置中心数据源

    private Map<Object, Object> targetDataSources;


    @Override
    public void afterPropertiesSet() {
        if (this.defaultDataSource == null) {
            throw new IllegalArgumentException("Property 'defaultDataSource' is required");
        }
        setDefaultTargetDataSource(defaultDataSource);
        targetDataSources = new HashMap<>();
        targetDataSources.put(DynamicDataSourceType.DEFAULT.name(), defaultDataSource);
        if (configCenterDataSource!=null){
            targetDataSources.put(DynamicDataSourceType.CONFIGCENTER.name(),configCenterDataSource);
        }
        if (failShift!=null){
            targetDataSources.put(DynamicDataSourceType.FAILSHIFT.name(), failShift);
        }
        if (factorsMiss!=null){
            targetDataSources.put(DynamicDataSourceType.FACTORMISS.name(), factorsMiss);
        }
        if (cll0814!=null){
            targetDataSources.put(DynamicDataSourceType.CLL0814.name(), cll0814);
        }
        if (heling0603!=null){
            targetDataSources.put(DynamicDataSourceType.HELING0603.name(), heling0603);
        }
        if (xiangliaobank!=null){
            targetDataSources.put(DynamicDataSourceType.XIANGLIAOBANK.name(), xiangliaobank);
        }
        if (xianliao1229!=null){
            targetDataSources.put(DynamicDataSourceType.XIANLIAO1229.name(), xianliao1229);
        }
        if (zhiweilai!=null){
            targetDataSources.put(DynamicDataSourceType.ZHIWEILAI.name(), zhiweilai);
        }
        if (cjbank!=null){
            targetDataSources.put(DynamicDataSourceType.CJBANK.name(), cjbank);
        }
        if (closedbate!=null){
            targetDataSources.put(DynamicDataSourceType.CLOSEDBATE.name(), closedbate);
        }
        if (aiyueba20180309!=null){
            targetDataSources.put(DynamicDataSourceType.AIYUEBA20180309.name(), aiyueba20180309);
        }
        if (pgyu6atqp5dbu!=null){
            targetDataSources.put(DynamicDataSourceType.PGYU6ATQP5DBU.name(), pgyu6atqp5dbu);
        }
        setTargetDataSources(targetDataSources);
        super.afterPropertiesSet();
    }

    @Override
    protected Object determineCurrentLookupKey() {
        logger.info("数据库路由选举策略...");

        String finalDataSource = DynamicDataSourceType.DEFAULT.name();
        DynamicDataSourceType dynamicDataSourceType = DynamicDataSourceHolders.getDataSource();
        logger.info("current datasource:"+dynamicDataSourceType);

        if(dynamicDataSourceType == null || dynamicDataSourceType == DynamicDataSourceType.DEFAULT) {
            finalDataSource = DynamicDataSourceType.DEFAULT.name();
        }
        else {
            finalDataSource = dynamicDataSourceType.name() ;
        }
        logger.info("选择了"+finalDataSource+"数据源");
        return finalDataSource ;
    }

    public  Map<Object, Object> getTargetDataSources(){
        return targetDataSources;
    }

    public Object getDefaultDataSource() {
        return defaultDataSource;
    }

    public void setDefaultDataSource(Object defaultDataSource) {
        this.defaultDataSource = defaultDataSource;
    }

    public Object getConfigCenterDataSource() {
        return configCenterDataSource;
    }

    public void setConfigCenterDataSource(Object configCenterDataSource) {
        this.configCenterDataSource = configCenterDataSource;
    }

    public Object getFailShift() {
        return failShift;
    }

    public void setFailShift(Object failShift) {
        this.failShift = failShift;
    }

    public Object getFactorsMiss() {
        return factorsMiss;
    }

    public void setFactorsMiss(Object factorsMiss) {
        this.factorsMiss = factorsMiss;
    }

    public Object getClosedbate() {
        return closedbate;
    }

    public void setClosedbate(Object closedbate) {
        this.closedbate = closedbate;
    }

    public Object getCll0814() {
        return cll0814;
    }

    public void setCll0814(Object cll0814) {
        this.cll0814 = cll0814;
    }

    public Object getHeling0603() {
        return heling0603;
    }

    public void setHeling0603(Object heling0603) {
        this.heling0603 = heling0603;
    }

    public Object getXiangliaobank() {
        return xiangliaobank;
    }

    public void setXiangliaobank(Object xiangliaobank) {
        this.xiangliaobank = xiangliaobank;
    }

    public Object getXianliao1229() {
        return xianliao1229;
    }

    public void setXianliao1229(Object xianliao1229) {
        this.xianliao1229 = xianliao1229;
    }

    public Object getZhiweilai() {
        return zhiweilai;
    }

    public void setZhiweilai(Object zhiweilai) {
        this.zhiweilai = zhiweilai;
    }

    public Object getCjbank() {
        return cjbank;
    }

    public void setCjbank(Object cjbank) {
        this.cjbank = cjbank;
    }

    public Object getAiyueba20180309() {
        return aiyueba20180309;
    }

    public void setAiyueba20180309(Object aiyueba20180309) {
        this.aiyueba20180309 = aiyueba20180309;
    }

    public Object getPgyu6atqp5dbu() {
        return pgyu6atqp5dbu;
    }

    public void setPgyu6atqp5dbu(Object pgyu6atqp5dbu) {
        this.pgyu6atqp5dbu = pgyu6atqp5dbu;
    }
}
