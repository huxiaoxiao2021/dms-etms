package com.jd.bluedragon.distribution.consumer.jy.comboard;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.common.dto.base.request.OperatorInfo;
import com.jd.bluedragon.common.dto.board.BizSourceEnum;
import com.jd.bluedragon.common.dto.comboard.request.ComboardScanReq;
import com.jd.bluedragon.configuration.ucc.UccPropertyConfiguration;
import com.jd.bluedragon.core.base.WaybillQueryManager;
import com.jd.bluedragon.core.jmq.producer.DefaultJMQProducer;
import com.jd.bluedragon.core.message.base.MessageBaseConsumer;
import com.jd.bluedragon.distribution.board.service.VirtualBoardService;
import com.jd.bluedragon.distribution.jy.dto.comboard.CancelComboardSendTaskDto;
import com.jd.bluedragon.distribution.jy.dto.comboard.CancelComboardTaskDto;
import com.jd.bluedragon.distribution.jy.exception.JyBizException;
import com.jd.bluedragon.distribution.send.domain.SendM;
import com.jd.bluedragon.distribution.send.service.DeliveryService;
import com.jd.bluedragon.distribution.waybill.domain.WaybillStatus;
import com.jd.bluedragon.dms.utils.WaybillUtil;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.bluedragon.utils.ObjectHelper;
import com.jd.etms.waybill.domain.Waybill;
import com.jd.jmq.common.message.Message;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * @author liwenji
 * @date 2022-12-13 13:00
 */
@Service("cancelComboardSendConsumer")
@Slf4j
public class CancelComboardSendConsumer extends MessageBaseConsumer {

    @Autowired
    @Qualifier("waybillCancelComboardProducer")
    private DefaultJMQProducer waybillCancelComboardProducer;

    @Autowired
    private VirtualBoardService virtualBoardService;

    @Autowired
    UccPropertyConfiguration ucc;
    
    @Autowired
    private DeliveryService deliveryService;
    
    @Autowired
    protected WaybillQueryManager waybillQueryManager;
    
    private static final Integer COMBOARD_SPLIT_NUM = 1024;
    
    @Override
    public void consume(Message message) throws Exception {
        //获取包裹列表，批次租板，批量发货
        if (ObjectHelper.isEmpty(message)){
            log.error("CancelComboardSendConsumer 消息为空！");
            return;
        }
        
        CancelComboardSendTaskDto dto = JsonHelper.fromJson(message.getText(), CancelComboardSendTaskDto.class);
        if (dto == null) {
            log.error("cancelComboardSendConsumer body体为空！");
            return;
        }
        
        List<String> barCodeList = dto.getBarCodeList();
        SendM sendM = toSendM(dto);
        for (String barCode : barCodeList) {
            if (WaybillUtil.isWaybillCode(barCode)) {
                // 异步取消组板
                asyncSendComboardWaybillTrace(dto,barCode);
            }else {
                // 发送取消组板全程跟踪
                OperatorInfo operatorInfo = assembleComboardOperatorInfo(dto);
                virtualBoardService.sendWaybillTrace(barCode, operatorInfo, dto.getBoardCode(),
                        dto.getEndSiteName(), WaybillStatus.WAYBILL_TRACK_BOARD_COMBINATION_CANCEL,
                        BizSourceEnum.PDA.getValue());
            }
            sendM.setBoxCode(barCode);
            deliveryService.dellCancelDeliveryMessageWithServerTime(sendM,true);
        }
    }
    private void asyncSendComboardWaybillTrace(CancelComboardSendTaskDto request, String waybillCode) {
        // 获取运单包裹数
        Waybill waybill = waybillQueryManager.getOnlyWaybillByWaybillCode(waybillCode);
        if (waybill == null || waybill.getGoodNumber() == null) {
            log.error("[异步取消组板任务]获取运单包裹数失败! {}", waybillCode);
            return;
        }

        int totalNum = waybill.getGoodNumber();
        int onePageSize = ucc.getWaybillSplitPageSize() == 0 ? COMBOARD_SPLIT_NUM : ucc.getWaybillSplitPageSize();
        int pageTotal = (totalNum % onePageSize) == 0 ? (totalNum / onePageSize) : (totalNum / onePageSize) + 1;
        // 插入分页任务
        CancelComboardTaskDto taskDto = new CancelComboardTaskDto();
        taskDto.setBoardCode(request.getBoardCode());
        taskDto.setWaybillCode(waybillCode);
        taskDto.setEndSiteName(request.getEndSiteName());
        taskDto.setSiteCode(request.getSiteCode());
        taskDto.setUserErp(request.getUserErp());
        taskDto.setUserName(request.getUserName());
        taskDto.setSiteName(request.getSiteName());
        taskDto.setUserCode(request.getUserCode());
        for (int i = 0; i < pageTotal; i++) {
            taskDto.setPageNo(i + 1);
            taskDto.setPageSize(onePageSize);
            try {
                waybillCancelComboardProducer.send(waybillCode + "_" + i+1, JsonHelper.toJson(taskDto));
                log.info("JyComBoardSendServiceImpl asyncSendComboardWaybillTrace : {}", JsonHelper.toJson(taskDto));
            } catch (Exception e) {
                log.error("JyComBoardSendServiceImpl asyncSendComboardWaybillTrace exception {}", e.getMessage(), e);
                throw new JyBizException("异步发送全程跟踪失败");
            }
        }
    }

    private OperatorInfo assembleComboardOperatorInfo(CancelComboardSendTaskDto request) {
        OperatorInfo operatorInfo = new OperatorInfo();
        operatorInfo.setSiteCode(request.getSiteCode());
        operatorInfo.setSiteName(request.getSiteName());
        operatorInfo.setUserCode(request.getUserCode());
        operatorInfo.setOperateTime(new Date());
        return operatorInfo;
    }
    private SendM toSendM(CancelComboardSendTaskDto request) {
        SendM sendM = new SendM();
        sendM.setCreateSiteCode(request.getSiteCode());
        sendM.setUpdaterUser(request.getUserName());
        sendM.setSendType(Constants.BUSSINESS_TYPE_POSITIVE);
        sendM.setUpdateUserCode(request.getUserCode());
        Date now = new Date();
        sendM.setOperateTime(now);
        sendM.setUpdateTime(now);
        sendM.setYn(Constants.YN_NO);
        return sendM;
    }
}
