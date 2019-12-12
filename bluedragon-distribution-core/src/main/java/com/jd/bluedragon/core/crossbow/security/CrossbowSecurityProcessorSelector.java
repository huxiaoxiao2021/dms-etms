package com.jd.bluedragon.core.crossbow.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.validation.constraints.NotNull;
import java.util.concurrent.ConcurrentHashMap;

/**
 * <p>
 *     ICrossbowSecurityProcessor安全插件的注册器
 *
 * @author wuzuxiang
 * @since 2019/11/5
 **/
public class CrossbowSecurityProcessorSelector {

    private static final Logger logger = LoggerFactory.getLogger(CrossbowSecurityProcessorSelector.class);

    /**
     * 安全插件处理器持有者
     */
    private static final ConcurrentHashMap<CrossbowSecurityEnum, ICrossbowSecurityProcessor> securityProcessor = new ConcurrentHashMap<>();

    /**
     * 安全插件处理器初始化注册方法
     * @param securityEnum
     * @param processor
     */
    public static void register(@NotNull CrossbowSecurityEnum securityEnum, @NotNull ICrossbowSecurityProcessor processor){
        if (securityProcessor.containsKey(securityEnum)) {
            logger.error("crossbow组件安全插件处理注册失败， 【{}】重复注册", securityEnum);
            throw new RuntimeException("crossbow组件安全插件【" + securityEnum + "】重复注册，请检查ICrossbowSecurityProcessor的所有子类构造器");
        }
        securityProcessor.put(securityEnum, processor);
    }

    /**
     * 根据枚举获取具体的安全插件的处理类
     * @param securityEnum 枚举类型
     * @return 返回具体的处理器
     */
    public static ICrossbowSecurityProcessor getProcessor(CrossbowSecurityEnum securityEnum){
        if (securityEnum == null || !securityProcessor.containsKey(securityEnum)) {
            return securityProcessor.get(CrossbowSecurityEnum.default_);
        }
        return securityProcessor.get(securityEnum);
    }
}
