package com.jd.bluedragon.core.crossbow.security;

import com.jd.bluedragon.core.crossbow.CrossbowConfig;
import com.jd.lop.crossbow.dto.LopRequest;
import com.jd.lop.crossbow.util.HmacUtil;
import org.springframework.stereotype.Component;

/**
 * <p>
 *     消息签名处理器
 * <doc>
 *      签名步骤如下：
 *          拼串： app_keyxxmsg_namexxpayloadxxtimestampxxvxx。
 *          拼串使用key-value的形式进行拼接（key.append(value)），网关使用接收到的参数，替换上述串中的xx。其中payload之后的xx为报文摘要（即payload字段的值）。
 *          把插件中配置的appSecret夹在替换后的字符串的两端，如appSecret+app_keyxxmsg_namexxpayloadxxtimestampxxvxx+appSecret。
 *          拼好的串，获取其MD5散列值，并将MD5运算结果转化成大写。
 *          签名完成，网关将最后的值写在url参数的sign字段中。
 * <doc/>
 *
 * @link   http://lft.jd.com/docCenter?docId=2970#%E6%B6%88%E6%81%AF%E7%AD%BE%E5%90%8D%E6%8F%92%E4%BB%B6
 *
 * @author wuzuxiang
 * @since 2019/11/5dms.core.PDDBusinessManager.executor
 **/
@Component("messageSignSecurityProcessor")
public class MessageSignSecurityProcessor implements ICrossbowSecurityProcessor {

    public MessageSignSecurityProcessor() {
        CrossbowSecurityProcessorSelector.register(CrossbowSecurityEnum.message_sign, this);
    }

    @Override
    public LopRequest handleSecurityContent(LopRequest request,CrossbowConfig crossbowConfig) {
        request.addUrlArg("payload",HmacUtil.MD5(request.getBody()));
        return request;
    }
}
