package com.jd.bluedragon.distribution.jy.service.picking.bridge;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.core.jmq.producer.DefaultJMQProducer;
import com.jd.bluedragon.distribution.jy.constants.PickingGoodTaskDetailInitServiceEnum;
import com.jd.bluedragon.distribution.jy.dto.collect.InitCollectSplitDto;
import com.jd.bluedragon.distribution.jy.dto.pickinggood.PickingGoodTaskDetailInitDto;
import com.jd.bluedragon.distribution.jy.service.picking.factory.PickingGoodDetailInitServiceFactory;
import com.jd.bluedragon.distribution.send.dao.SendDatailReadDao;
import com.jd.bluedragon.distribution.send.domain.SendDSimple;
import com.jd.bluedragon.distribution.send.domain.SendDetail;
import com.jd.bluedragon.utils.BusinessHelper;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.jmq.common.message.Message;
import com.jd.jsf.gd.util.JsonUtils;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

/**
 * 待提明细初始化-取分拣发货数据
 * @Author zhengchengfa
 * @Date 2023/12/7 21:42
 * @Description
 */
@Service("pickingGoodDetailInitByDmsSendInfoServiceImpl")
public class PickingGoodDetailInitByDmsSendInfoServiceImpl implements PickingGoodDetailInitService, InitializingBean {

    private static final Logger log = LoggerFactory.getLogger(AviationPickingGoodTaskInit.class);
    @Autowired
    private SendDatailReadDao sendDatailReadDao;

    @Autowired
    @Qualifier(value = "jyPickingGoodDetailInitSplitProducer")
    private DefaultJMQProducer jyPickingGoodDetailInitSplitProducer;



    @Override
    public void afterPropertiesSet() throws Exception {
        PickingGoodDetailInitServiceFactory.registerPickingGoodDetailInitService(PickingGoodTaskDetailInitServiceEnum.DMS_SEND_DMS_PICKING.getTargetCode(), this);
    }


    private void logInfo(String message, Object... objects) {
        if (log.isInfoEnabled()) {
            log.info(message, objects);
        }
    }

    private void logWarn(String message, Object... objects) {
        if (log.isWarnEnabled()) {
            log.warn(message, objects);
        }
    }

    @Override
    @JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWEB,jKey = "DMS.PickingGoodDetailInitByDmsSendInfoServiceImpl.pickingGoodDetailInitSplit",mState = {JProEnum.TP, JProEnum.FunctionError})
    public boolean pickingGoodDetailInitSplit(PickingGoodTaskDetailInitDto initDto) {
        List<SendDetail> sendDetailList = sendDatailReadDao.querySendDSimpleInfoBySendCode(initDto.getBatchCode(), initDto.getStartSiteId().intValue());

        List<Message> messageList = new ArrayList<>();
        sendDetailList.forEach(sendDetail -> {
            PickingGoodTaskDetailInitDto detailSplitInitDto = new PickingGoodTaskDetailInitDto();
            BeanUtils.copyProperties(initDto, detailSplitInitDto);
            detailSplitInitDto.setPackageCode(sendDetail.getPackageBarcode());
            if(BusinessHelper.isBoxcode(sendDetail.getBoxCode())) {
                detailSplitInitDto.setBoxCode(sendDetail.getBoxCode());
                detailSplitInitDto.setScanIsBoxType(true);
            }
            String msgText = JsonUtils.toJSONString(detailSplitInitDto);
            logInfo("dmsToDms待提明细初始化拆分最小包裹维度，businessId={},msg={}", sendDetail.getPackageBarcode(), msgText);
            messageList.add(new Message(jyPickingGoodDetailInitSplitProducer.getTopic(), msgText, sendDetail.getPackageBarcode()));

        });
        jyPickingGoodDetailInitSplitProducer.batchSendOnFailPersistent(messageList);
        return true;
    }

    @Override
    public boolean pickingGoodDetailInit(Collection<PickingGoodTaskDetailInitDto> values) {
//        if(Objects.isNull(values)) {
//            logWarn("DmsToDms:pickingGoodDetailInit：参数为空不处理");
//            return true;
//        }
//        List<Message> messageList = new ArrayList<>();
//        values.forEach(detailInit -> {
//            String msgText = JsonUtils.toJSONString(detailInit);
//            String businessId = detailInit.getBatchCode();
//            logInfo("提货任务按批次走分拣sendD数据执行待提明细初始化，param={}", JsonHelper.toJson(detailInit));
//            messageList.add(new Message(jyPickingGoodDetailInitProducer.getTopic(), msgText, businessId));
//
//        });
//        jyPickingGoodDetailInitProducer.batchSendOnFailPersistent(messageList);
        return false;
    }


    public boolean pickingGoodDetailInit(PickingGoodTaskDetailInitDto detailInitDto) {
        //查询sendD
//        List<SendDetail> sendDetailList = sendDatailReadDao.querySendDSimpleInfoBySendCode(sendCode,createSiteCode);
//
//        //循环将SendDetail对象转换为SendDSimple对象
//        for(SendDetail sendDetail : sendDetailList){
//            SendDSimple sendDSimple = new SendDSimple();
//            sendDSimple.setSendCode(sendDetail.getSendCode());
//            sendDSimple.setWaybillCode(sendDetail.getWaybillCode());
//            sendDSimple.setPackageBarCode(sendDetail.getPackageBarcode());
//            sendDSimple.setCreateSiteCode(sendDetail.getCreateSiteCode());
//            sendDSimple.setReceiveSiteCode(sendDetail.getReceiveSiteCode());
//            sendDSimple.setCreateUserCode(sendDetail.getCreateUserCode());
//            sendDSimple.setCreateUserName(sendDetail.getCreateUser());
//            sendDSimpleList.add(sendDSimple);
//        }
        return false;
    }


}
