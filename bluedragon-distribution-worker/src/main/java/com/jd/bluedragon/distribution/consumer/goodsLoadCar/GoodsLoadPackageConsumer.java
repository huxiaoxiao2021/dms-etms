package com.jd.bluedragon.distribution.consumer.goodsLoadCar;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.core.message.base.MessageBaseConsumer;
import com.jd.bluedragon.distribution.goodsLoadScan.GoodsLoadScanConstants;
import com.jd.bluedragon.distribution.goodsLoadScan.domain.GoodsLoadDto;
import com.jd.bluedragon.distribution.goodsLoadScan.domain.GoodsLoadScanException;
import com.jd.bluedragon.distribution.send.domain.SendM;
import com.jd.bluedragon.distribution.send.service.DeliveryService;
import com.jd.bluedragon.distribution.send.utils.SendBizSourceEnum;
import com.jd.bluedragon.dms.utils.BusinessUtil;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.jmq.common.message.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service("goodsLoadPackageConsume")
public class GoodsLoadPackageConsumer extends MessageBaseConsumer {

    private final Logger log = LoggerFactory.getLogger(GoodsLoadTaskConsumer.class);

    @Autowired
    private DeliveryService deliveryService;

    @Override
    public void consume(Message message) throws Exception {
        log.info("20201029--装车发货消费包裹MQ--begin--，消息体【{}】", JsonHelper.toJson(message));
        if (message == null || "".equals(message.getText()) || null == message.getText()) {
            this.log.warn("PackingConsumableConsumer consume -->消息为空");
            return;
        }

        GoodsLoadDto context = JsonHelper.fromJson(message.getText(), GoodsLoadDto.class);
        if(context == null) {
            log.error("auto下发消息体转换失败，内容为【{}】", message.getText());
            return;
        }

        loadDeliver(context);

    }

    //调用已有发货接口
    private void loadDeliver(GoodsLoadDto req) {

        SendBizSourceEnum bizSource = SendBizSourceEnum.ANDROID_PDA_LOAD_SEND;
        SendM domain = new SendM();
        domain.setReceiveSiteCode(
                req.getReceiveSiteCode() == null || Integer.valueOf(0).equals(req.getReceiveSiteCode()) ?
                        BusinessUtil.getReceiveSiteCodeFromSendCode(req.getSendCode()):req.getReceiveSiteCode());
        domain.setCreateSiteCode(req.getCurrentOperate().getSiteCode());
        domain.setSendCode(req.getSendCode());
        domain.setBoxCode(req.getPackageCode());//包裹号
        domain.setCreateUser(req.getUserName());
        domain.setCreateUserCode(req.getUserCode());
        domain.setSendType(Constants.BUSSINESS_TYPE_POSITIVE);
        domain.setYn(GoodsLoadScanConstants.YN_Y);
        domain.setCreateTime(new Date());
        domain.setOperateTime(req.getUpdateTime());

        if(log.isDebugEnabled()) {
            log.debug("装车完成发货--begin--参数【{}】", JsonHelper.toJson(domain));
        }
        try {
            //调用一车一单发货接口
            deliveryService.packageSend(bizSource, domain);
        }catch (Exception e) {
            log.error("装车发货完成失败----error", e);
            throw  new GoodsLoadScanException("装车发货完成失败");
        }
        if(log.isDebugEnabled()) {
            log.debug("装车完成发货--end--参数【{}】", JsonHelper.toJson(domain));
        }

    }
}
