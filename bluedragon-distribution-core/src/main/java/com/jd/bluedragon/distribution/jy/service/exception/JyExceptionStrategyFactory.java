package com.jd.bluedragon.distribution.jy.service.exception;

import com.jd.bluedragon.distribution.jy.service.exception.impl.JySanwuExceptionServiceImpl;
import com.jd.bluedragon.distribution.jy.service.exception.impl.JyScrappedExceptionServiceImpl;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Author: chenyaguo@jd.com
 * @Date: 2023/3/9 21:35
 * @Description:
 */
@Component
public class JyExceptionStrategyFactory implements InitializingBean, ApplicationContextAware {

    @Autowired
    private JySanwuExceptionServiceImpl jySanwuExceptionService;

    //使用Map集合存储策略信息,彻底消除if...else
    private final Map<Integer, JyExceptionStrategy> strategyMap = new ConcurrentHashMap<>();
    private ApplicationContext appContext;

    @Override
    public void afterPropertiesSet() throws Exception {
        // 将 Spring 容器中所有的 JyExceptionStrategy 注册到 strategyMap
        appContext.getBeansOfType(JyExceptionStrategy.class)
                .values().forEach(hander->strategyMap.put(hander.getExceptionType(),hander));
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        appContext = applicationContext;
    }

    public JyExceptionStrategy getStrategy(Integer type) {
        if(Objects.isNull(type)){
            return jySanwuExceptionService;
        }
        return strategyMap.get(type);
    }



}
