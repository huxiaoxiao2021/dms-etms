package com.jd.bluedragon.distribution.seal.hystrix;

import com.jd.bluedragon.configuration.ucc.UccPropertyConfiguration;
import com.jd.bluedragon.distribution.api.utils.JsonHelper;
import com.netflix.config.PollResult;
import com.netflix.config.PolledConfigurationSource;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * @ClassName HystrixPropertiesAutoInjection
 * @Description
 * @Author wyh
 * @Date 2022/3/14 19:55
 **/
@Component
public class HystrixPropertiesAutoInjection implements PolledConfigurationSource {

    private static final Logger log = LoggerFactory.getLogger(HystrixPropertiesAutoInjection.class);

    private static final String DEFAULT_PROPS_KEY = "defaultProperties";
    private static final String COMMAND_PROPS_KEY = "commandProperties";
    private static final String THREAD_POOL_PROPS_KEY = "threadPoolProperties";


    private static final Map<String, Object> DEFAULT_PROPS = new HashMap<>();

    static {
        DEFAULT_PROPS.put("hystrix.command.default.circuitBreaker.enabled", false); //关闭断路由器
        DEFAULT_PROPS.put("hystrix.command.default.execution.timeout.enabled", false); //关闭超时降级开关
        DEFAULT_PROPS.put("hystrix.command.default.fallback.enabled", false); //关闭回退开关
    }

    @Autowired
    private UccPropertyConfiguration uccConfig;

    @Override
    public PollResult poll(boolean initial, Object checkPoint) throws Exception {
        Map<String, Object> allPropMap = new HashMap<>();

        String hystrixProps = uccConfig.getSealTaskHystrixProps();
        if (StringUtils.isNotBlank(hystrixProps)) {
            Map<String, Object> uccConfigMap = JsonHelper.fromJson(hystrixProps, Map.class);
            Map<String, Object> defaultPropMap = (Map<String, Object>) uccConfigMap.get(DEFAULT_PROPS_KEY);
            if (MapUtils.isNotEmpty(defaultPropMap)) {
                allPropMap.putAll(defaultPropMap);
            }
            Map<String, Object> cmdPropMap = (Map<String, Object>) uccConfigMap.get(COMMAND_PROPS_KEY);
            if (MapUtils.isNotEmpty(cmdPropMap)) {
                allPropMap.putAll(cmdPropMap);
            }
            Map<String, Object> threadPropMap = (Map<String, Object>) uccConfigMap.get(THREAD_POOL_PROPS_KEY);
            if (MapUtils.isNotEmpty(threadPropMap)) {
                allPropMap.putAll(threadPropMap);
            }
        }
        else {
            log.error("从UCC获取动态解封车降级配置为空.");
            allPropMap = DEFAULT_PROPS;
        }

        PollResult pollResult = PollResult.createFull(allPropMap);

        if (log.isInfoEnabled()) {
            log.info("拉取解封车降级配置:{}", JsonHelper.toJson(pollResult));
        }

        return pollResult;
    }
}
