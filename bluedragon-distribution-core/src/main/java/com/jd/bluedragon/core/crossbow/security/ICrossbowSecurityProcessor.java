package com.jd.bluedragon.core.crossbow.security;

import com.jd.bluedragon.core.crossbow.CrossbowConfig;
import com.jd.lop.crossbow.dto.LopRequest;

/**
 * <p>
 *     物流网关的加密执行器
 *
 * @author wuzuxiang
 * @since 2019/11/5
 **/
public interface ICrossbowSecurityProcessor {

    /**
     * 对crossbow组件的lopRequest进行加密的处理，加密处理涉及到的内容为crossBowConfig中的配置内容，返回组件请求对象lopRequest
     * @see CrossbowSecurityEnum 请见加密类型枚举，不同的枚举值对应不同的加密处理器 {@code ICrossbowSecurityProcessor} 的实例
     * @param crossbowConfig 用户配置项
     * @return 进行加密处理后的请求
     */
    LopRequest handleSecurityContent(LopRequest request, CrossbowConfig crossbowConfig);

}