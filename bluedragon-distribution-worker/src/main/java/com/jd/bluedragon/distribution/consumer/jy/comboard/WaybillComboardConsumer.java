package com.jd.bluedragon.distribution.consumer.jy.comboard;

import com.jd.bluedragon.common.dto.base.request.OperatorInfo;
import com.jd.bluedragon.common.dto.board.BizSourceEnum;
import com.jd.bluedragon.core.base.BoardCommonManagerImpl;
import com.jd.bluedragon.core.base.WaybillPackageManager;
import com.jd.bluedragon.core.jsf.dms.GroupBoardManager;
import com.jd.bluedragon.core.message.base.MessageBaseConsumer;
import com.jd.bluedragon.distribution.board.service.VirtualBoardService;
import com.jd.bluedragon.distribution.jy.dto.comboard.ComboardTaskDto;
import com.jd.bluedragon.distribution.jy.enums.ComboardBarCodeTypeEnum;
import com.jd.bluedragon.distribution.jy.enums.OperateBizSubTypeEnum;
import com.jd.bluedragon.distribution.jy.service.common.JyOperateFlowService;
import com.jd.bluedragon.distribution.waybill.domain.WaybillStatus;
import com.jd.bluedragon.dms.utils.WaybillUtil;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.bluedragon.utils.ObjectHelper;
import com.jd.etms.waybill.domain.BaseEntity;
import com.jd.etms.waybill.domain.DeliveryPackageD;
import com.jd.jmq.common.message.Message;
import com.jd.transboard.api.dto.AddBoardBoxes;
import com.jd.transboard.api.dto.BoardBoxResult;
import com.jd.transboard.api.dto.Response;
import com.jd.transboard.api.enums.ResponseEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.jd.bluedragon.distribution.loadAndUnload.exception.LoadIllegalException.BOARD_TOTC_FAIL_INTERCEPT_MESSAGE;

/**
 * @author liwenji
 * @date 2022-12-10 11:45
 */
@Service("waybillComboardConsumer")
@Slf4j
public class WaybillComboardConsumer extends MessageBaseConsumer  {

    @Autowired
    private VirtualBoardService virtualBoardService;

    @Autowired
    protected WaybillPackageManager waybillPackageManager;

    @Autowired
    GroupBoardManager groupBoardManager;

    @Autowired
    private JyOperateFlowService jyOperateFlowService;
    
    @Override
    public void consume(Message message) throws Exception {
        //获取包裹列表，批次租板，批量发货
        if (ObjectHelper.isEmpty(message)){
            log.error("WaybillComboardConsumer异常 任务信息为空！");
            return;
        }
        ComboardTaskDto dto = JsonHelper.fromJson(message.getText(),ComboardTaskDto.class);
        if (ObjectHelper.isEmpty(dto)){
            log.error("WaybillComboardConsumer异常 body体为空！");
            return;
        }
        log.info("=====================WaybillComboardConsumer====================,{}",JsonHelper.toJson(dto));
        final int pageSize = dto.getPageSize();
        final int pageNo = dto.getPageNo();
        final String waybillCode = dto.getWaybillCode();
        BaseEntity<List<DeliveryPackageD>> baseEntity = waybillPackageManager.getPackListByWaybillCodeOfPage(waybillCode, pageNo, pageSize);
        if (baseEntity == null || CollectionUtils.isEmpty(baseEntity.getData())) {
            log.error("[组板+发货]运单拆分任务分页获取包裹数量为空! waybillCode={}", waybillCode);
            return;
        }
        List<DeliveryPackageD> packageDList =baseEntity.getData();
        //批量组板
        List<String> packageList =new ArrayList<>();
        for (DeliveryPackageD deliveryPackageD:packageDList){
            packageList.add(deliveryPackageD.getPackageBarcode());
        }
        AddBoardBoxes addBoardBoxes =new AddBoardBoxes();
        addBoardBoxes.setBoardCode(dto.getBoardCode());
        addBoardBoxes.setBoxCodes(packageList);
        addBoardBoxes.setBarcodeType(getBarCodeType(packageList.get(0)));
        addBoardBoxes.setBizSource(BizSourceEnum.PDA.getValue());
        addBoardBoxes.setOperatorErp(dto.getUserErp());
        addBoardBoxes.setOperatorName(dto.getUserName());
        addBoardBoxes.setSiteCode(dto.getStartSiteId());
        addBoardBoxes.setSiteName(dto.getStartSiteName());
        addBoardBoxes.setSiteType(BoardCommonManagerImpl.BOARD_COMBINATION_SITE_TYPE);
        Response<BoardBoxResult> response = groupBoardManager.addBoxesToBoardReturnId(addBoardBoxes);
        if (response.getCode() != ResponseEnum.SUCCESS.getIndex()) {
            log.error("异步执行大宗组板"+response.getMesseage()!=null?response.getMesseage():BOARD_TOTC_FAIL_INTERCEPT_MESSAGE);
            return;
        }
        // 记录组板操作流水
        jyOperateFlowService.sendBoardOperateFlowData(dto, response.getData(), OperateBizSubTypeEnum.JY_BOARD_WAYBILL_SCAN);
        // <包裹号, 操作流水表主键>
        Map<String, Long> map = dto.getOperateFlowMap();

        for (String packageCode : packageList) {
            // 操作流水表业务主键
            Long operateFlowId = map.get(packageCode);
            dto.setBarCode(packageCode);
            dto.setOperateFlowId(operateFlowId);
            // 发送全程跟踪
            sendComboardWaybillTrace(dto);
        }
        log.info("运单异步执行组板{} 成功",JsonHelper.toJson(dto));
    }
    private void sendComboardWaybillTrace(ComboardTaskDto dto) {
        OperatorInfo operatorInfo = assembleComboardOperatorInfo(dto);
        virtualBoardService.sendWaybillTrace(dto.getBarCode(), operatorInfo,dto.getOperatorData(), dto.getBoardCode(),
                dto.getEndSiteName(), WaybillStatus.WAYBILL_TRACK_BOARD_COMBINATION, BizSourceEnum.PDA.getValue());
    }

    private OperatorInfo assembleComboardOperatorInfo(ComboardTaskDto dto) {
        OperatorInfo operatorInfo = new OperatorInfo();
        operatorInfo.setSiteCode(dto.getStartSiteId());
        operatorInfo.setSiteName(dto.getStartSiteName());
        operatorInfo.setUserCode(dto.getUserCode());
        operatorInfo.setOperateTime(dto.getOperateTime());
        operatorInfo.setOperatorTypeCode(dto.getOperatorTypeCode());
        operatorInfo.setOperatorId(dto.getOperatorId());
        operatorInfo.setOperateFlowId(dto.getOperateFlowId());
        return operatorInfo;
    }

    private Integer getBarCodeType(String barCode) {
        if (WaybillUtil.isPackageCode(barCode)) {
            return ComboardBarCodeTypeEnum.PACKAGE.getCode();
        } else if (WaybillUtil.isWaybillCode(barCode)) {
            return ComboardBarCodeTypeEnum.WAYBILL.getCode();
        } else {
            return ComboardBarCodeTypeEnum.BOX.getCode();
        }
    }

}
