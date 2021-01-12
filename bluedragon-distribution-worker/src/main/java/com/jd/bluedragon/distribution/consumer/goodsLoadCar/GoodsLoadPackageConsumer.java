package com.jd.bluedragon.distribution.consumer.goodsLoadCar;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.core.message.base.MessageBaseConsumer;
import com.jd.bluedragon.distribution.appointmentStorage.domain.AppointStorageDto;
import com.jd.bluedragon.distribution.appointmentStorage.service.AppointStorageService;
import com.jd.bluedragon.distribution.goodsLoadScan.GoodsLoadScanConstants;
import com.jd.bluedragon.distribution.goodsLoadScan.domain.GoodsLoadDto;
import com.jd.bluedragon.distribution.goodsLoadScan.domain.GoodsLoadScanException;
import com.jd.bluedragon.distribution.send.domain.SendM;
import com.jd.bluedragon.distribution.send.service.DeliveryService;
import com.jd.bluedragon.distribution.send.utils.SendBizSourceEnum;
import com.jd.bluedragon.distribution.storage.domain.PutawayDTO;
import com.jd.bluedragon.distribution.storage.domain.StorageSourceEnum;
import com.jd.bluedragon.distribution.storage.service.StoragePackageMService;
import com.jd.bluedragon.dms.utils.BusinessUtil;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.common.util.StringUtils;
import com.jd.jmq.common.message.Message;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service("goodsLoadPackageConsume")
public class GoodsLoadPackageConsumer extends MessageBaseConsumer {

    private final Logger log = LoggerFactory.getLogger(GoodsLoadTaskConsumer.class);

    /**
     * 下架时间：装车扫描发货时间推前时间：3s
     * */
    private static final Long PUSH_FORWARD_TIME = 3000L;

    @Autowired
    private DeliveryService deliveryService;

    @Autowired
    private StoragePackageMService storagePackageMService;

    @Autowired
    private AppointStorageService appointStorageService;

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
        //校验暂存包裹，修改暂存包裹状态、发暂存包裹下架消息
        updateTempStoragePackageInfo(context);
        //装车发货，全流程跟踪
        loadDeliver(context);

    }
    //校验暂存包裹，修改暂存包裹状态、发暂存包裹下架消息
    private void updateTempStoragePackageInfo(GoodsLoadDto context) {
        //查询是否为暂存包裹,没有暂存直接操作发货
        List<AppointStorageDto> packageList  = appointStorageService.checkPackageTempStorage(context.getPackageCode());
        if(CollectionUtils.isEmpty(packageList)) {
            return;
        }
        //如果是暂存包裹，修改暂存包裹状态
        int tempRes = appointStorageService.updateTempStoragePackageInfo(packageList.get(0));
        if(tempRes <= 0) {
            log.error("【{}】装车完成时根据包裹号修改暂存包裹状态为已发货状态时出错，包裹信息=【{}】", context.getPackageCode(), JsonHelper.toJson(context));
            throw new RuntimeException("装车发货完成修改暂存包裹【" + context.getPackageCode() + "】状态为已发货时出错");
        }
        //发送包裹的下架全程跟踪（MQ）
        PutawayDTO pd = new PutawayDTO();
        Long time = context.getUpdateTime() == null ? new Date().getTime() : context.getUpdateTime().getTime();
            //下架时间=发货时间-3s
        pd.setOperateTime(time - PUSH_FORWARD_TIME);
        if(context.getCurrentOperate() != null) {
            pd.setCreateSiteCode(context.getCurrentOperate().getSiteCode());
        }
        pd.setBarCode(context.getPackageCode());
        if (StringUtils.isNotBlank(context.getUserName())) {
            pd.setOperatorErp(context.getUserName());
        } else {
            if (context.getUser() != null) {
                pd.setOperatorErp(context.getUser().getUserErp());
            } else {
                pd.setOperatorErp("system");
            }
        }
        pd.setStorageSource(StorageSourceEnum.KY_STORAGE.getCode());
        storagePackageMService.updateWaybillStatusOfKYZC(pd,false);

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
        if (StringUtils.isNotBlank(req.getUserName())) {
            domain.setCreateUser(req.getUserName());
            domain.setCreateUserCode(req.getUserCode());
        } else {
            if (req.getUser() != null) {
                domain.setCreateUser(req.getUser().getUserName());
                domain.setCreateUserCode(req.getUser().getUserCode());
            } else {
                domain.setCreateUser("system");
                domain.setCreateUserCode(0);
            }
        }

        domain.setSendType(Constants.BUSSINESS_TYPE_POSITIVE);
        domain.setYn(GoodsLoadScanConstants.YN_Y);

        if (req.getUpdateTime() != null) {
            domain.setCreateTime(req.getUpdateTime());
            domain.setOperateTime(req.getUpdateTime());
        } else {
            domain.setCreateTime(new Date());
            domain.setOperateTime(new Date());
        }

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
