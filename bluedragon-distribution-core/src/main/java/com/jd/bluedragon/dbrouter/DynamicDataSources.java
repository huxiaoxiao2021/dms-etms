package com.jd.bluedragon.dbrouter;

import org.slf4j.LoggerFactory;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

import java.util.HashMap;
import java.util.Map;

public class DynamicDataSources extends AbstractRoutingDataSource {

    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(DynamicDataSources.class);

    private Object defaultDataSource;

    private Object dmsUndivMain;

    private Object dmsUndivSlave;

    private Map<Object, Object> targetDataSources;


    @Override
    public void afterPropertiesSet() {
        if (this.defaultDataSource == null) {
            throw new IllegalArgumentException("Property 'defaultDataSource' is required");
        }
        setDefaultTargetDataSource(defaultDataSource);
        targetDataSources = new HashMap<>();
        targetDataSources.put(DynamicDataSourceType.DEFAULT.name(), defaultDataSource);
        if (dmsUndivMain != null) {
            targetDataSources.put(DynamicDataSourceType.DMS_UNDIV_MAIN.name(), dmsUndivMain);
        }
        if (dmsUndivSlave != null) {
            targetDataSources.put(DynamicDataSourceType.DMS_UNDIV_SLAVE.name(), dmsUndivSlave);
        }
        setTargetDataSources(targetDataSources);
        super.afterPropertiesSet();
    }

    @Override
    protected Object determineCurrentLookupKey() {
        logger.info("数据库路由选举策略...");
        DynamicDataSourceType currentDataSource = DynamicDataSourceHolders.getDataSource();
        DynamicDataSourceType finalDataSource = currentDataSource != null ? currentDataSource : DynamicDataSourceType.DEFAULT;
        logger.info("选择了" + finalDataSource.name() + "数据源");
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

    public Object getDmsUndivMain() {
        return dmsUndivMain;
    }

    public void setDmsUndivMain(Object dmsUndivMain) {
        this.dmsUndivMain = dmsUndivMain;
    }

    public Object getDmsUndivSlave() {
        return dmsUndivSlave;
    }

    public void setDmsUndivSlave(Object dmsUndivSlave) {
        this.dmsUndivSlave = dmsUndivSlave;
    }
}
