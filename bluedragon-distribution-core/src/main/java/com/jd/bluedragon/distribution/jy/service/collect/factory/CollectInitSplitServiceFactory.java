package com.jd.bluedragon.distribution.jy.service.collect.factory;

import com.jd.bluedragon.distribution.jy.exception.JyBizException;
import com.jd.bluedragon.distribution.jy.service.collect.strategy.CollectInitSplitService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * @Author zhengchengfa
 * @Description //集齐初始化拆分处理beanFactory
 * @date
 **/
public class CollectInitSplitServiceFactory {
    private static final Logger log = LoggerFactory.getLogger(CollectInitSplitServiceFactory.class);

    private static Map<Integer, CollectInitSplitService> collectInitSplitNodeMap = new HashMap<>();

    private CollectInitSplitServiceFactory() {
    }

    /**
     * 根据k获取对象
     * @param type
     * @return
     */
    public static CollectInitSplitService getCollectInitSplitService(Integer type) {
        CollectInitSplitService result = collectInitSplitNodeMap.get(type);
        if(result == null) {
            log.error("未查到code={}的集齐初始化拆分类型", type);
            throw new JyBizException("获取集齐初始化拆分类型异常");
        }
        return result;
    }

    /**
     * 初始化集齐初始化服务对象
     * @param type
     * @param collectInitNodeService
     */
    public static void registerCollectInitSplitService(Integer type, CollectInitSplitService collectInitNodeService) {
        collectInitSplitNodeMap.put(type, collectInitNodeService);
    }

}
