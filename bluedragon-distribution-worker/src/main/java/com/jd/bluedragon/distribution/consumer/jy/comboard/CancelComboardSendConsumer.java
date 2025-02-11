package com.jd.bluedragon.distribution.consumer.jy.comboard;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.common.dto.base.request.OperatorInfo;
import com.jd.bluedragon.common.dto.base.response.JdCResponse;
import com.jd.bluedragon.common.dto.board.BizSourceEnum;
import com.jd.bluedragon.common.dto.comboard.request.ComboardScanReq;
import com.jd.bluedragon.configuration.DmsConfigManager;
import com.jd.bluedragon.core.base.WaybillQueryManager;
import com.jd.bluedragon.core.jmq.producer.DefaultJMQProducer;
import com.jd.bluedragon.core.jsf.dms.GroupBoardManager;
import com.jd.bluedragon.core.message.base.MessageBaseConsumer;
import com.jd.bluedragon.distribution.api.domain.OperatorData;
import com.jd.bluedragon.distribution.board.service.VirtualBoardService;
import com.jd.bluedragon.distribution.box.domain.Box;
import com.jd.bluedragon.distribution.box.service.BoxService;
import com.jd.bluedragon.distribution.jy.dto.comboard.CancelComboardSendTaskDto;
import com.jd.bluedragon.distribution.jy.dto.comboard.CancelComboardTaskDto;
import com.jd.bluedragon.distribution.jy.exception.JyBizException;
import com.jd.bluedragon.distribution.send.domain.SendM;
import com.jd.bluedragon.distribution.send.domain.ThreeDeliveryResponse;
import com.jd.bluedragon.distribution.send.service.DeliveryService;
import com.jd.bluedragon.distribution.waybill.domain.WaybillStatus;
import com.jd.bluedragon.dms.utils.BusinessUtil;
import com.jd.bluedragon.dms.utils.WaybillUtil;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.bluedragon.utils.ObjectHelper;
import com.jd.bluedragon.utils.converter.BeanConverter;
import com.jd.etms.waybill.domain.Waybill;
import com.jd.jmq.common.message.Message;
import com.jd.transboard.api.dto.RemoveBoardBoxDto;
import com.jd.transboard.api.dto.Response;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.jd.bluedragon.Constants.SUCCESS_CODE;

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
    DmsConfigManager dmsConfigManager;
    
    @Autowired
    private DeliveryService deliveryService;
    
    @Autowired
    protected WaybillQueryManager waybillQueryManager;

    @Autowired
    private BoxService boxService;

    @Autowired
    GroupBoardManager groupBoardManager;
    
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
            if (StringUtils.isEmpty(barCode)) {
                continue;
            }

            if (WaybillUtil.isWaybillCode(barCode)) {
                // 异步取消组板
                asyncSendComboardWaybillTrace(dto,barCode);
            }else {
                // 发送取消组板全程跟踪
                OperatorInfo operatorInfo = assembleComboardOperatorInfo(dto);
                virtualBoardService.sendWaybillTrace(barCode, operatorInfo,dto.getOperatorData(), dto.getBoardCode(),
                        dto.getEndSiteName(), WaybillStatus.WAYBILL_TRACK_BOARD_COMBINATION_CANCEL,
                        dto.getBizSource().getValue());
                checkIfNeedCancelInnerBox(barCode,dto,operatorInfo);
            }

            sendM.setBoxCode(barCode);
            ThreeDeliveryResponse response = deliveryService.dellCancelDeliveryMessageWithServerTime(sendM, true);
            if (response == null) {
                log.error("取消发货失败：{}",barCode);
                return;
            }
            if (!response.getCode().equals(SUCCESS_CODE)){
                log.error("取消发货失败：{},{}",barCode,response.getMessage());
                return;
            }
        }
    }

    private void checkIfNeedCancelInnerBox(String barCode,CancelComboardSendTaskDto dto,OperatorInfo operatorInfo) {
        try {
            if (BusinessUtil.isLLBoxcode(barCode)){
                Box query =new Box();
                query.setCode(barCode);
                List<Box> boxes =boxService.listAllDescendantsByParentBox(query);
                if (CollectionUtils.isNotEmpty(boxes)){
                    List<String> barCodeList = new ArrayList<>();
                    RemoveBoardBoxDto removeBoardBoxDto = new RemoveBoardBoxDto();
                    removeBoardBoxDto.setSiteCode(dto.getSiteCode());
                    removeBoardBoxDto.setOperatorErp(dto.getUserErp());
                    removeBoardBoxDto.setOperatorName(dto.getUserName());
                    removeBoardBoxDto.setBoardCode(dto.getBoardCode());
                    removeBoardBoxDto.setBoxCodeList(barCodeList);
                    for (Box box:boxes){
                        barCodeList.add(box.getCode());
                    }
                    Response removeBoardBoxRes = groupBoardManager.batchRemoveBardBoxByBoxCodes(removeBoardBoxDto);
                    if (removeBoardBoxRes == null || removeBoardBoxRes.getCode() != JdCResponse.CODE_SUCCESS) {
                        log.error("取消组板操作失败，接口参数：{}，异常返回结果：{}", JsonHelper.toJson(removeBoardBoxDto), JsonHelper.toJson(removeBoardBoxRes));
                        return;
                    }
                    for (Box box:boxes){
                        virtualBoardService.sendWaybillTrace(box.getCode(), operatorInfo,dto.getOperatorData(), dto.getBoardCode(),
                                dto.getEndSiteName(), WaybillStatus.WAYBILL_TRACK_BOARD_COMBINATION_CANCEL,
                                dto.getBizSource().getValue());
                    }
                }
            }
        } catch (Exception e) {
            log.error("大箱取消组板拆分小箱取消异常",e);
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
        int onePageSize = dmsConfigManager.getPropertyConfig().getWaybillSplitPageSize() == 0 ? COMBOARD_SPLIT_NUM : dmsConfigManager.getPropertyConfig().getWaybillSplitPageSize();
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
        
        OperatorData operatorData = BeanConverter.convertToOperatorData(request);
        taskDto.setOperatorTypeCode(operatorData.getOperatorTypeCode());
        taskDto.setOperatorId(operatorData.getOperatorId());
        taskDto.setOperatorData(operatorData); 
        
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
    	operatorInfo.setOperatorTypeCode(request.getOperatorTypeCode());
    	operatorInfo.setOperatorId(request.getOperatorId());
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
        OperatorData operatorData = BeanConverter.convertToOperatorData(request);
        sendM.setOperatorTypeCode(operatorData.getOperatorTypeCode());
        sendM.setOperatorId(operatorData.getOperatorId());
        sendM.setOperatorData(operatorData);
        
        return sendM;
    }
}
