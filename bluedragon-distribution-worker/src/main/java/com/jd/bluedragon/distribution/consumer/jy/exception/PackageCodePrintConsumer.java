package com.jd.bluedragon.distribution.consumer.jy.exception;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.core.message.base.MessageBaseConsumer;
import com.jd.bluedragon.distribution.jy.exception.JyExceptionPrintDto;
import com.jd.bluedragon.distribution.jy.service.exception.JyExceptionService;
import com.jd.bluedragon.distribution.print.domain.RePrintRecordMq;
import com.jd.bluedragon.dms.utils.WaybillUtil;
import com.jd.bluedragon.utils.DateHelper;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.bluedragon.utils.StringHelper;
import com.jd.jmq.client.consumer.MessageListener;
import com.jd.jmq.common.message.Message;
import com.jd.jspliter.jmq.SplitMessage;
import com.jd.jspliter.jmq.consumer.EnvMessageListener;
import com.jd.ps.data.epf.dto.ExpefNotify;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import com.jd.ump.profiler.CallerInfo;
import com.jd.ump.profiler.proxy.Profiler;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * <P>
 *     拦截消息消费处理类
 *     接受来自各的系统的拦截消息
 * </p>
 *
 * <doc>
 *     <topic>waybill_task_platform_msg</topic>
 *     <description>运单下发操作台的控制消息</description>
 *     <link>https://cf.jd.com/pages/viewpage.action?pageId=165087628</link>
 * </doc>
 *
 * <doc>
 *     <topic>waybill_task_platform_complete</topic>
 *     <description>运单下发操作台的控制消息</description>
 *     <link>https://cf.jd.com/pages/viewpage.action?pageId=165088132</link>
 * </doc>
 *
 * <doc>
 *     <topic></topic>
 *     <description>包裹补打消息</description>
 *     <link>*</link>
 * </doc>
 *
 * @author wuzuxiang
 * @since 2019/3/27
 */
@Service("packageCodePrintConsumer")
public class PackageCodePrintConsumer extends MessageBaseConsumer {

    private static final Logger logger = LoggerFactory.getLogger(PackageCodePrintConsumer.class);

    @Autowired
    private JyExceptionService jyExceptionService;

    @Override
    @JProfiler(jKey = "DMS.WORKER.PackageCodePrintConsumer.consume",
            jAppName = Constants.UMP_APP_NAME_DMSWORKER, mState = {JProEnum.TP,JProEnum.FunctionError})
    public void consume(Message message) throws Exception {
        if (StringHelper.isEmpty(message.getText())) {
            logger.warn("PackageCodePrintConsumer consume --> 消息为空");
            return;
        }
        if (!JsonHelper.isJsonString(message.getText())) {
            logger.warn("PackageCodePrintConsumer consume -->消息体非JSON格式，内容为【{}】", message.getText());
            return;
        }
        JyExceptionPrintDto dto = JsonHelper.fromJson(message.getText(), JyExceptionPrintDto.class);
        jyExceptionService.printSuccess(dto);
    }

}

