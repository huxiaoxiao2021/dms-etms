package com.jd.bluedragon.common.utils.routingDao;

import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

/**
 * Created by zhaoyukai on 2017/9/18.
 */
public class RoutingDataSource extends AbstractRoutingDataSource {
    @Override
    protected Object determineCurrentLookupKey() {
        return RoutingPlugin.threadLocal.get();
    }
}
