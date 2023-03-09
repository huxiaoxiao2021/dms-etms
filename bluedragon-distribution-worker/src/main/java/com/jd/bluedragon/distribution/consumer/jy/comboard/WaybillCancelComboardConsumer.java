package com.jd.bluedragon.distribution.consumer.jy.comboard;

import com.jd.bluedragon.common.dto.base.request.OperatorInfo;
import com.jd.bluedragon.common.dto.board.BizSourceEnum;
import com.jd.bluedragon.core.base.WaybillPackageManager;
import com.jd.bluedragon.core.message.base.MessageBaseConsumer;
import com.jd.bluedragon.distribution.board.service.VirtualBoardService;
import com.jd.bluedragon.distribution.jy.dto.comboard.CancelComboardTaskDto;
import com.jd.bluedragon.distribution.waybill.domain.WaybillStatus;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.bluedragon.utils.ObjectHelper;
import com.jd.etms.waybill.domain.BaseEntity;
import com.jd.etms.waybill.domain.DeliveryPackageD;
import com.jd.jmq.common.message.Message;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * @author liwenji
 * @date 2022-12-09 11:11
 */
@Service("waybillCancelComboardConsumer")
@Slf4j
public class WaybillCancelComboardConsumer extends MessageBaseConsumer {

    @Autowired
    private VirtualBoardService virtualBoardService;

    @Autowired
    protected WaybillPackageManager waybillPackageManager;
    
    @Override
    public void consume(Message message) throws Exception {
        //获取包裹列表，批次租板，批量发货
        if (ObjectHelper.isEmpty(message)){
            log.error("waybillCancelComboardConsumer 消息为空！");
            return;
        }
        CancelComboardTaskDto dto = JsonHelper.fromJson(message.getText(),CancelComboardTaskDto.class);
        if (ObjectHelper.isEmpty(dto)){
            log.error("waybillCancelComboardConsumer body体为空！");
            return;
        }
        final int pageSize = dto.getPageSize();
        final int pageNo = dto.getPageNo();
        final String waybillCode = dto.getWaybillCode();
        BaseEntity<List<DeliveryPackageD>> baseEntity = waybillPackageManager.getPackListByWaybillCodeOfPage(waybillCode, pageNo, pageSize);
        if (baseEntity == null || CollectionUtils.isEmpty(baseEntity.getData())) {
            log.error("[取消组板]运单拆分任务分页获取包裹数量为空! waybillCode={}", waybillCode);
            return;
        }
        // 按包裹发送全程跟踪
        for (DeliveryPackageD packageD : baseEntity.getData()) {
            OperatorInfo operatorInfo = new OperatorInfo();
            operatorInfo.setSiteCode(dto.getSiteCode());
            operatorInfo.setSiteName(dto.getSiteName());
            operatorInfo.setUserCode(dto.getUserCode());
            operatorInfo.setOperateTime(new Date());
            operatorInfo.setOperatorTypeCode(dto.getOperatorTypeCode());
            operatorInfo.setOperatorId(dto.getOperatorId());   
            virtualBoardService.sendWaybillTrace(packageD.getPackageBarcode(),
                    operatorInfo,dto.getBoardCode(),dto.getSiteName(),
                    WaybillStatus.WAYBILL_TRACK_BOARD_COMBINATION_CANCEL,
                    BizSourceEnum.PDA.getValue());
        }
        log.info("运单异步执行取消组板{} 成功",JsonHelper.toJson(dto));
    }
}
