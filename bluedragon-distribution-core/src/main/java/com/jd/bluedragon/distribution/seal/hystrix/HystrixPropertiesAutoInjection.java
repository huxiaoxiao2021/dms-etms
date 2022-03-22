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

    @Autowired
    private UccPropertyConfiguration uccConfig;

    @Override
    public PollResult poll(boolean initial, Object checkPoint) throws Exception {
        Map<String, Object> propMap = null;
        String hystrixProps = uccConfig.getSealTaskHystrixProps();
        if (StringUtils.isNotBlank(hystrixProps)) {
            propMap = JsonHelper.fromJson(hystrixProps, Map.class);
            if (MapUtils.isEmpty(propMap)) {
                log.error("从UCC获取动态解封车降级配置为空.");
            }
        }

        PollResult pollResult = PollResult.createFull(propMap);

        if (log.isInfoEnabled()) {
            log.info("拉取解封车降级配置:{}", JsonHelper.toJson(pollResult));
        }
        return pollResult;
    }
}
