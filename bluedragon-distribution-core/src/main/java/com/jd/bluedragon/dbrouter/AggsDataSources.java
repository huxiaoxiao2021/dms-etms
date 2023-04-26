package com.jd.bluedragon.dbrouter;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

import java.util.HashMap;
import java.util.Map;

public class AggsDataSources extends AbstractRoutingDataSource {

  private static final org.slf4j.Logger logger = LoggerFactory.getLogger(AggsDataSources.class);

  private Object defaultDataSource;

  private Object jyCore;

  private Object aggsMain;

  private Object aggsSlave;

  private Map<Object, Object> targetDataSources;

  @Value("${jy.aggs.dataSource}")
  private String shouldChooseDateSource;


  @Override
  public void afterPropertiesSet() {
    if (this.defaultDataSource == null) {
      throw new IllegalArgumentException("Property 'defaultDataSource' is required");
    }
    setDefaultTargetDataSource(defaultDataSource);
    targetDataSources = new HashMap<>();
    targetDataSources.put(DynamicDataSourceType.DEFAULT.name(), defaultDataSource);
    if (jyCore != null) {
      targetDataSources.put(DynamicDataSourceType.JY_CORE.name(), jyCore);
    }
    if (aggsMain != null) {
      targetDataSources.put(DynamicDataSourceType.AGGS_MAIN.name(), aggsMain);
    }
    if (aggsSlave != null) {
      targetDataSources.put(DynamicDataSourceType.AGGS_SLAVE.name(), aggsSlave);
    }
    setTargetDataSources(targetDataSources);
    super.afterPropertiesSet();
  }

  @Override
  protected Object determineCurrentLookupKey() {
    logger.info("AggsDataSources数据库路由选举策略...");
    DynamicDataSourceType currentDataSource = DynamicDataSourceHolders.getDataSource();
    logger.info(".DynamicDataSourceHolders..{}", currentDataSource.name());
    DynamicDataSourceType finalDataSource =
        currentDataSource != null ? currentDataSource : DynamicDataSourceType.DEFAULT;
    logger.info("最终选择了" + finalDataSource.name() + "数据源");
    if (finalDataSource.getName().equals(shouldChooseDateSource)){
      logger.info("选对了");
    }
    else {
      logger.info("选错了");
    }

    return finalDataSource.name();
  }

  public Map<Object, Object> getTargetDataSources() {
    return targetDataSources;
  }

  public Object getDefaultDataSource() {
    return defaultDataSource;
  }

  public void setDefaultDataSource(Object defaultDataSource) {
    this.defaultDataSource = defaultDataSource;
  }

  public Object getJyCore() {
    return jyCore;
  }

  public void setJyCore(Object jyCore) {
    this.jyCore = jyCore;
  }

  public Object getAggsMain() {
    return aggsMain;
  }

  public void setAggsMain(Object aggsMain) {
    this.aggsMain = aggsMain;
  }

  public Object getAggsSlave() {
    return aggsSlave;
  }

  public void setAggsSlave(Object aggsSlave) {
    this.aggsSlave = aggsSlave;
  }
}
