package com.jd.bluedragon.distribution.jy.service.common;

import java.util.List;

import com.jd.bluedragon.distribution.jy.dto.common.JyOperateFlowDto;
import com.jd.bluedragon.distribution.jy.dto.common.JyOperateFlowMqData;
import com.jd.bluedragon.distribution.jy.enums.OperateBizSubTypeEnum;
import com.jd.bluedragon.distribution.receive.domain.Receive;
import com.jd.bluedragon.distribution.send.domain.SendDetail;
import com.jd.bluedragon.distribution.sorting.domain.Sorting;
import com.jd.bluedragon.distribution.waybill.domain.WaybillStatus;
import com.jd.transboard.api.dto.BoardBoxResult;

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
     * 发送分拣操作轨迹
     */
    void sendOperateTrack(WaybillStatus waybillStatus);

    /**
     * 发送组板操作流水
     */
    <T> void sendBoardOperateFlowData(T t, BoardBoxResult boardBoxResult, OperateBizSubTypeEnum subTypeEnum);

    /**
     * 发送收货操作流水
     */
    void sendReceiveOperateFlowData(Receive receive);

    /**
     * 发送分拣操作流水
     */
    void sendSoringOperateFlowData(Sorting sorting, WaybillStatus waybillStatus, OperateBizSubTypeEnum subTypeEnum);

    /**
     * 发送发货操作流水
     */
    void sendDeliveryOperateFlowData(SendDetail sendDetail, WaybillStatus waybillStatus, OperateBizSubTypeEnum subTypeEnum);


}
