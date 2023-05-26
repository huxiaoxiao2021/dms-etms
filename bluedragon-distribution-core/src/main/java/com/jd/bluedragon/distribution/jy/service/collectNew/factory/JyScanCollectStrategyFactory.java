package com.jd.bluedragon.distribution.jy.service.collectNew.factory;

import com.jd.bluedragon.distribution.jy.constants.JyPostEnum;
import com.jd.bluedragon.distribution.jy.exception.JyBizException;
import com.jd.bluedragon.distribution.jy.service.collectNew.strategy.JyScanCollectStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * @Author zhengchengfa
 * @Description //
 * @date
 **/
public class JyScanCollectStrategyFactory {
    private static final Logger log = LoggerFactory.getLogger(JyScanCollectStrategyFactory.class);

    //K-岗位类型  V-该岗位处理集齐service
    private static Map<String, JyScanCollectStrategy> jyScanCollectStrategyMap = new HashMap<>();

    private JyScanCollectStrategyFactory() {
    }

    /**
     * 根据k获取对象
     * @param jyPostType
     * @return
     */
    public static JyScanCollectStrategy getJyScanCollectService(String jyPostType) {
        JyScanCollectStrategy result = jyScanCollectStrategyMap.get(jyPostType);
        if(result == null) {
            log.error("获取拣运扫描处理集齐服务异常：未查到岗位【{}|{}】的集齐处理逻辑", jyPostType, JyPostEnum.getDescByCode(jyPostType));
            throw new JyBizException("获取拣运扫描处理集齐服务异常");
        }
        return result;
    }

    /**
     * 初始化集齐初始化服务对象
     */
    public static void registerJyScanCollectService(String jyPostType, JyScanCollectStrategy jyScanCollectStrategy) {
        jyScanCollectStrategyMap.put(jyPostType, jyScanCollectStrategy);
    }

}
