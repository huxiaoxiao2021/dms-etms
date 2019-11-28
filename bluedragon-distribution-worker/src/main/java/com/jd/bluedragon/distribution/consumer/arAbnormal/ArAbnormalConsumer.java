package com.jd.bluedragon.distribution.consumer.arAbnormal;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.core.message.base.MessageBaseConsumer;
import com.jd.bluedragon.distribution.api.request.ArAbnormalRequest;
import com.jd.bluedragon.distribution.arAbnormal.ArAbnormalService;
import com.jd.bluedragon.distribution.send.domain.SendM;
import com.jd.bluedragon.distribution.send.service.DeliveryService;
import com.jd.bluedragon.dms.utils.WaybillUtil;
import com.jd.bluedragon.utils.BusinessHelper;
import com.jd.bluedragon.utils.DateHelper;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.bluedragon.utils.StringHelper;
import com.jd.jmq.common.message.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * @author tangchunqing
 * @Description: 空铁转陆运 提报异常
 * @date 2018年12月04日 20时:40分
 */
@Service("arAbnormalConsumer")
public class ArAbnormalConsumer extends MessageBaseConsumer {
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private ArAbnormalService arAbnormalService;

    @Autowired
    private DeliveryService deliveryService;

    @Override
    public void consume(Message message) throws Exception {
        if (!JsonHelper.isJsonString(message.getText())) {
            log.warn("ArAbnormalConsumer-消息体非JSON格式，内容为【{}】", message.getText());
            return;
        }

        /**将mq消息体转换成SendDetail对象**/
        ArAbnormalRequest arAbnormalRequest = JsonHelper.fromJsonUseGson(message.getText(), ArAbnormalRequest.class);
        if (arAbnormalRequest == null || StringHelper.isEmpty(arAbnormalRequest.getPackageCode())) {
            log.warn("ArAbnormalConsumer[{}]转换实体失败或没有合法的扫描码",message.getText());
            return;
        }
        /* 触发运输方式变更逻辑 */
        arAbnormalService.dealArAbnormal(arAbnormalRequest);

        /* cancelType为pda升级期间的标识，cancel为1位新版本，需要进行取消发货，否则直接退出方法 */
        if (arAbnormalRequest.getCancelType() == null || arAbnormalRequest.getCancelType() != 1) {
            return;
        }

        try {
            /* 补加取消发货的逻辑 */
            SendM sendMr = new SendM();
            if (BusinessHelper.isSendCode(arAbnormalRequest.getPackageCode())) {
                sendMr.setSendCode(arAbnormalRequest.getPackageCode());
            } else if (BusinessHelper.isBoxcode(arAbnormalRequest.getPackageCode())) {
                sendMr.setBoxCode(arAbnormalRequest.getPackageCode());
            } else if (WaybillUtil.isWaybillCode(arAbnormalRequest.getPackageCode()) || WaybillUtil.isPackageCode(arAbnormalRequest.getPackageCode())) {
                sendMr.setBoxCode(arAbnormalRequest.getPackageCode());
            } else {
                log.warn("航空转陆运触发取消发货，单号{}类型不正确，方法退出。",arAbnormalRequest.getPackageCode());
                return;
            }
            sendMr.setCreateSiteCode(arAbnormalRequest.getSiteCode());
            sendMr.setUpdaterUser(arAbnormalRequest.getUserName());
            sendMr.setUpdateUserCode(arAbnormalRequest.getUserCode());
            Date operateTime = DateHelper.parseDate(arAbnormalRequest.getOperateTime(), Constants.DATE_TIME_FORMAT);
            sendMr.setOperateTime(operateTime);
            sendMr.setUpdateTime(new Date());
            sendMr.setYn(0);
            //senMr不加receiveSiteCode 和 businessType 因为入口在kongtieChange.cs，这两个值无法确认
            deliveryService.dellCancelDeliveryMessage(sendMr, true);
        } catch (Exception e) {
            log.error("运输方式变更触发取消发货处理异常，消息体为：{}" , message.getText(), e);
        }
    }
}
