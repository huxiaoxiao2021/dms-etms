package com.jd.bluedragon.common.utils.routingDao;

import com.jd.bluedragon.common.router.Partition;
import com.jd.bluedragon.utils.PropertiesHelper;
import org.apache.ibatis.cache.CacheKey;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.plugin.*;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.NamedThreadLocal;

import java.util.Map;
import java.util.Properties;

/**
 * Created by zhaoyukai on 2017/9/18.
 */

@Intercepts({
        @Signature(
                type = Executor.class,
                method = "query",
                args = {MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class}),
        @Signature(
                type = Executor.class,
                method = "query",
                args = {MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class, CacheKey.class, BoundSql.class}),
        /*如写库需分库 则再开启
        @Signature(
                type = Executor.class,
                method = "update",
                args = {MappedStatement.class, Object.class}
                )*/
})
public class RoutingPlugin implements Interceptor {
    private static Logger LOGGER = LoggerFactory.getLogger(RoutingPlugin.class);

    static NamedThreadLocal<String> threadLocal = new NamedThreadLocal<String>("RoutingPluginValue");

    public Object intercept(Invocation invocation) throws Throwable {
        //根据运单缓存库数据源切换标识 - readMultiDB 切换读主库或分库 ,readMultiDB：true-读分库，false-读主库
        boolean readMultiDB = Boolean.parseBoolean(PropertiesHelper.newInstance().getValue("readMultiDB"));
        LOGGER.debug("RoutingPlugin start..");
        Object[] args = invocation.getArgs();
        Object parameter= args[1];
        String dbKey = null;
        //仅当配置Ver 访问总部库 & 读总部分库 & 传参为Map的非空实例时，才执行分库策略
        if (readMultiDB && parameter != null && parameter instanceof Map) {
            Map paramsMap = (Map) parameter;
            String db = (String) paramsMap.get("db");
            if (db != null) {
                dbKey = Partition.WAYBILL_DB_MAIN + "_" + db + "_db" ;
            }
        }
        if (dbKey == null) {
            dbKey = Partition.WAYBILL_DB_MAIN;
        }
        threadLocal.set(dbKey);
        LOGGER.debug("routing set dbKey {}", dbKey);
        return invocation.proceed();
    }

    public Object plugin(Object target) {
        return Plugin.wrap(target, this);
    }

    public void setProperties(Properties var1){

    }
}
