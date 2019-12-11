package com.jd.bluedragon.core.crossbow.security;

import com.jd.bluedragon.core.crossbow.CrossbowConfig;
import com.jd.lop.crossbow.dto.LopRequest;
import org.springframework.stereotype.Component;

/**
 * <p>
 *     默认的空安全插件处理器，无处理
 *
 * @author wuzuxiang
 * @since 2019/11/5
 **/
@Component("defaultSecurityProcessor")
public class DefaultSecurityProcessor implements ICrossbowSecurityProcessor {

    public DefaultSecurityProcessor() {
        CrossbowSecurityProcessorSelector.register(CrossbowSecurityEnum.default_,this);
    }

    @Override
    public LopRequest handleSecurityContent(LopRequest request,CrossbowConfig crossbowConfig) {
        return request;
    }
}
