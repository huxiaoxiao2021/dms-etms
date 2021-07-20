package com.jd.bluedragon.distribution.consumer.goodsLoadCar;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.common.dto.base.request.CurrentOperate;
import com.jd.bluedragon.core.message.base.MessageBaseConsumer;
import com.jd.bluedragon.distribution.goodsLoadScan.GoodsLoadScanConstants;
import com.jd.bluedragon.distribution.goodsLoadScan.domain.GoodsLoadDto;
import com.jd.bluedragon.distribution.goodsLoadScan.domain.GoodsLoadScanException;
import com.jd.bluedragon.distribution.send.domain.SendM;
import com.jd.bluedragon.distribution.send.service.DeliveryService;
import com.jd.bluedragon.distribution.send.utils.SendBizSourceEnum;
import com.jd.bluedragon.distribution.task.domain.Task;
import com.jd.bluedragon.distribution.task.service.TaskService;
import com.jd.bluedragon.distribution.waybill.domain.WaybillStatus;
import com.jd.bluedragon.dms.utils.BusinessUtil;
import com.jd.bluedragon.dms.utils.WaybillUtil;
import com.jd.bluedragon.utils.BusinessHelper;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.bluedragon.utils.Md5Helper;
import com.jd.common.util.StringUtils;
import com.jd.etms.waybill.api.WaybillSyncApi;
import com.jd.etms.waybill.domain.WaybillParameter;
import com.jd.etms.waybill.dto.OrderShipsDto;
import com.jd.jmq.common.message.Message;
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

    @Autowired
    private TaskService taskService;

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
        String kyexpresskey = Constants.KYEXPRESSLOADSUCCESS+waybillCode;
        String kyexpectValue = jimdbCacheService.get(kyexpresskey);
        log.info("发送运单妥投消息跨越,waybillcode:{},kyexpresskey={},kyexpectValue={}",waybillCode,kyexpresskey,kyexpectValue);
        //此运单是跨越订单 且成功装车
        if(StringUtils.isNotBlank(kyexpectValue)){
            if(jimdbCacheService.setNx(Constants.KYSENDMSGSUCCESSPREFIX+waybillCode,waybillCode,10,TimeUnit.DAYS)){
                log.info("设置运单妥投消息缓存成功");
                if(!this.sendSuccessInfo(req)){
                    jimdbCacheService.del(Constants.KYSENDMSGSUCCESSPREFIX+waybillCode);
                }
            }
        }
    }


    private boolean sendSuccessInfo(GoodsLoadDto req) {
        if(log.isInfoEnabled()){
            log.info("发送运单妥投消息sendSuccessInfo:{}",JsonHelper.toJson(req));
        }
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
            //不确定传值是什么
            //orderShipDto.setType(0);
        }
        waybillParameters.add(waybillParameter);
        log.info("运单同步老接口入参：{}", JsonHelper.toJson(waybillParameter));
        //发送妥投成功
        sendWaybillSuccessMsg(req,currentOperate,waybillParameters);
        return true;
    }

    public void sendWaybillSuccessMsg(GoodsLoadDto req,CurrentOperate operate,List<WaybillParameter> body){
        try {
            String waybillCode = WaybillUtil.getWaybillCode(req.getPackageCode());
            String jsonStr = JsonHelper.toJson(body);
            Task task = new Task();
            task.setBody(jsonStr);
            task.setFingerprint(Md5Helper.encode(req.getUserCode()+"_"+operate.getSiteCode()
                    + "_" + waybillCode + "_" + req.getPackageCode() +"_"
                    + System.currentTimeMillis())); //
            task.setCreateSiteCode(operate.getSiteCode());
            task.setCreateTime(req.getUpdateTime());
            task.setKeyword1(waybillCode);
            task.setKeyword2(req.getPackageCode());
            task.setType(Task.TASK_TYPE_WAYBILL_FINISHED);
            task.setTableName(Task.TABLE_NAME_WAYBILL);
            task.setSequenceName(Task.getSequenceName(task.getTableName()));
            task.setOwnSign(BusinessHelper.getOwnSign());
            this.taskService.add(task);
        }catch (Exception ex){
            log.error("跨越妥投加入任务失败:",ex);
            throw new RuntimeException("跨越妥投加入任务失败,抛出异常,触发重试");
        }
    }
}
