package com.jd.bluedragon.distribution.consumer.goodsLoadCar;

import IceInternal.Ex;
import com.jd.bluedragon.Constants;
import com.jd.bluedragon.common.dto.base.request.CurrentOperate;
import com.jd.bluedragon.core.base.BaseMajorManager;
import com.jd.bluedragon.core.message.base.MessageBaseConsumer;
import com.jd.bluedragon.distribution.goodsLoadScan.GoodsLoadScanConstants;
import com.jd.bluedragon.distribution.goodsLoadScan.domain.GoodsLoadDto;
import com.jd.bluedragon.distribution.goodsLoadScan.domain.GoodsLoadScanException;
import com.jd.bluedragon.distribution.send.domain.SendM;
import com.jd.bluedragon.distribution.send.service.DeliveryService;
import com.jd.bluedragon.distribution.send.utils.SendBizSourceEnum;
import com.jd.bluedragon.distribution.waybill.domain.WaybillStatus;
import com.jd.bluedragon.dms.utils.BusinessUtil;
import com.jd.bluedragon.dms.utils.WaybillUtil;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.common.util.StringUtils;
import com.jd.etms.waybill.api.WaybillSyncApi;
import com.jd.etms.waybill.domain.BaseEntity;
import com.jd.etms.waybill.domain.WaybillParameter;
import com.jd.etms.waybill.dto.OrderShipsDto;
import com.jd.common.util.StringUtils;
import com.jd.jmq.common.message.Message;
import com.jd.ql.basic.dto.BaseStaffSiteOrgDto;
import com.jd.ql.dms.common.cache.CacheService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service("goodsLoadPackageConsume")
public class GoodsLoadPackageConsumer extends MessageBaseConsumer {

    private final Logger log = LoggerFactory.getLogger(GoodsLoadTaskConsumer.class);

    @Autowired
    private DeliveryService deliveryService;

    @Autowired
    private WaybillSyncApi waybillSyncApi;

    @Autowired
    @Qualifier("jimdbCacheService")
    private CacheService jimdbCacheService;

    private Integer waybillOpeType = 8;
    private static final String kyexpressloadsuccess="kyloadcarsuccess";
    public static final String KYSENDMSGSUCCESSPREFIX = "kysendmsgsuccessprefix";

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
        //发送运单妥投消息-仅跨越
        String waybillCode = WaybillUtil.getWaybillCode(req.getPackageCode());
        String kyexpresskey = kyexpressloadsuccess+waybillCode;
        String kyexpectValue = jimdbCacheService.get(kyexpresskey);
        log.info("发送运单妥投消息跨越,waybillcode:{},kyexpresskey={},kyexpectValue={}",waybillCode,kyexpresskey,kyexpectValue);
        if(StringUtils.isNotBlank(kyexpectValue)){//此运单是跨越订单 且成功装车
            if(jimdbCacheService.setNx(KYSENDMSGSUCCESSPREFIX+waybillCode,waybillCode,10,TimeUnit.DAYS)){
                log.info("设置运单妥投消息缓存成功");
                if(!this.sendSuccessInfo(req)){
                    jimdbCacheService.del(KYSENDMSGSUCCESSPREFIX+waybillCode);
                }
            }
        }
    }


    private boolean sendSuccessInfo(GoodsLoadDto req) {
        log.info("发送运单妥投消息sendSuccessInfo:{}",JsonHelper.toJson(req));
        String waybillCode = WaybillUtil.getWaybillCode(req.getPackageCode());
        List<WaybillParameter> waybillParameters = new ArrayList<WaybillParameter>();
        WaybillParameter waybillParameter = new WaybillParameter();
        CurrentOperate currentOperate = req.getCurrentOperate();
        waybillParameter.setWaybillCode(waybillCode);
        waybillParameter.setPsyId(req.getUserCode());
        if (currentOperate != null) {
            waybillParameter.setOrgId(currentOperate.getOrgId());
            waybillParameter.setZdName(currentOperate.getSiteName());
            waybillParameter.setZdId(currentOperate.getSiteCode());
            waybillParameter.setOperateTime(currentOperate.getOperateTime());
        }
        waybillParameter.setOperatorId(req.getUserCode());
        waybillParameter.setOperatorName(req.getUserName());
        waybillParameter.setOperatorType(waybillOpeType);
        OrderShipsDto orderShipDto = new OrderShipsDto();
        waybillParameter.setOrderShipsDto(orderShipDto);
        //妥投
        if (waybillOpeType.equals(WaybillStatus.WAYBILL_OPE_TYPE_DELIVERED)) {
            orderShipDto.setAmount(0);
            orderShipDto.setDistanceType(0);
            if (currentOperate != null) {
                orderShipDto.setLocalTimeState(currentOperate.getOperateTime());
            }
            //在线支付
            orderShipDto.setPayWayId(-2);
            orderShipDto.setType(0);
        }
        waybillParameters.add(waybillParameter);
        log.info("运单同步老接口入参：{}", JsonHelper.toJson(waybillParameter));
        //同步接口
        try{
            BaseEntity<Boolean> result = this.waybillSyncApi.batchUpdateWaybillByWaybillCode(waybillParameters, waybillOpeType);
            log.info("同步接口返回数据:{}",JsonHelper.toJson(result));
            if (result.getResultCode() == 1) {
                log.info("运单发送妥投消息成功,运单号:{}", waybillCode);
                return true;
            } else {
                log.info("运单发送妥投消息失败！运单号:{}", waybillCode);
                return false;
            }
        }catch (Exception e){
            log.error("根据运单号和操作号批量更新运单状态失败,运单号:{},异常信息:",waybillCode,e);
            return false;
        }
    }
}
