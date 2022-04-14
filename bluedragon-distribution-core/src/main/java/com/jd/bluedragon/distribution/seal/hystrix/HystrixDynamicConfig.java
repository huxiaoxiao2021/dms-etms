package com.jd.bluedragon.distribution.seal.hystrix;

import com.netflix.config.AbstractPollingScheduler;
import com.netflix.config.ConfigurationManager;
import com.netflix.config.DynamicConfiguration;
import com.netflix.config.FixedDelayPollingScheduler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @ClassName HystrixDynamicConfig
 * @Description
 * @Author wyh
 * @Date 2022/3/15 15:15
 **/
@Configuration
public class HystrixDynamicConfig {

    @Autowired
    private HystrixPropertiesAutoInjection propertiesAutoInjection;

    @Bean
    public DynamicConfiguration dynamicConfiguration() {
        boolean ignoreDeletesFromSource = false; // 缺少的属性视为删除  @see https://github.com/Netflix/archaius/wiki/Users-Guide
        AbstractPollingScheduler scheduler = new FixedDelayPollingScheduler(30 * 1000, 60 * 1000, ignoreDeletesFromSource);
        DynamicConfiguration configuration = new DynamicConfiguration(propertiesAutoInjection, scheduler);
        ConfigurationManager.install(configuration); // must install to enable configuration
        return configuration;
    }
}
