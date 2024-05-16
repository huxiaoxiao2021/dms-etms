package com.jd.bluedragon.distribution.jy.service.common;

import com.jd.bluedragon.distribution.abnormalwaybill.domain.AbnormalWayBill;
import com.jd.bluedragon.distribution.api.request.DmsSealVehicleRequest;
import com.jd.bluedragon.distribution.api.request.NewSealVehicleRequest;
import com.jd.bluedragon.distribution.inspection.domain.Inspection;
import com.jd.bluedragon.distribution.jy.dto.common.JyOperateFlowDto;
import com.jd.bluedragon.distribution.jy.dto.common.JyOperateFlowMqData;
import com.jd.bluedragon.distribution.jy.enums.OperateBizSubTypeEnum;
import com.jd.bluedragon.distribution.receive.domain.ArReceive;
import com.jd.bluedragon.distribution.receive.domain.Receive;
import com.jd.bluedragon.distribution.send.domain.SendDetail;
import com.jd.bluedragon.distribution.sorting.domain.Sorting;
import com.jd.bluedragon.distribution.waybill.domain.WaybillStatus;
import com.jd.bluedragon.distribution.weightVolume.domain.WeightVolumeEntity;
import com.jd.etms.vos.dto.SealCarDto;
import com.jd.transboard.api.dto.BoardBoxResult;

import java.util.List;

/**
 * service接口
 *
 * @author wuyoude
 * @date 2023/4/19 8:48 PM
 */
public interface JyOperateFlowService {

    /**
     * 新增数据
     * 
     * @param data
     * 
     */
    int insert(JyOperateFlowDto data);
    /**
     * 根据分区键和id查询一条记录
     * @param data
     */
    JyOperateFlowDto findByOperateBizKeyAndId(JyOperateFlowDto data);
    /**
     * 发送mq服务
     * @param jyOperateFlow
     * @return
     */
    int sendMq(JyOperateFlowMqData jyOperateFlow);
    /**
     * 发送mq列表服务
     * @param jyOperateFlow
     * @return
     */
    int sendMqList(List<JyOperateFlowMqData> jyOperateFlow);

    /**
     * 生成操作流水主键
     */
    Long createOperateFlowId();

    /**
     * 发送操作轨迹
     */
    void sendOperateTrack(WaybillStatus waybillStatus);

    /**
     * 发送组板操作流水
     */
    <T> void sendBoardOperateFlowData(T t, BoardBoxResult boardBoxResult, OperateBizSubTypeEnum subTypeEnum);

    /**
     * 发送解封车操作流水
     */
    void sendUnsealOperateFlowData(SealCarDto sealCarDto, NewSealVehicleRequest request);

    /**
     * 发送验货操作流水
     */
    void sendInspectOperateFlowData(List<Inspection> inspectionList, OperateBizSubTypeEnum subTypeEnum);

    /**
     * 发送收货操作流水
     */
    void sendReceiveOperateFlowData(Receive receive, OperateBizSubTypeEnum subTypeEnum);

    /**
     * 发送分拣操作流水
     */
    void sendSoringOperateFlowData(Sorting sorting, WaybillStatus waybillStatus, OperateBizSubTypeEnum subTypeEnum);

    /**
     * 构建发货操作流水
     */
    JyOperateFlowMqData createDeliveryOperateFlowData(SendDetail sendDetail, WaybillStatus waybillStatus, OperateBizSubTypeEnum subTypeEnum);

    /**
     * 发送发货操作流水
     */
    void sendDeliveryOperateFlowData(SendDetail sendDetail, WaybillStatus waybillStatus, OperateBizSubTypeEnum subTypeEnum);

    /**
     * 发送配送异常操作流水
     */
    void sendAbnormalOperateFlowData(AbnormalWayBill abnormalWayBill, OperateBizSubTypeEnum subTypeEnum);

    /**
     * 发送称重操作流水
     */
    void sendWeightVolumeOperateFlowData(WeightVolumeEntity entity, OperateBizSubTypeEnum subTypeEnum);

    /**
     * 发送封车操作流水
     */
    void sendSealOperateFlowData(SealCarDto sealCarDto, DmsSealVehicleRequest request);

    /**
     * 发送空铁提货操作流水
     */
    void sendArReceiveOperateFlowData(ArReceive arReceive, OperateBizSubTypeEnum subTypeEnum);

}
