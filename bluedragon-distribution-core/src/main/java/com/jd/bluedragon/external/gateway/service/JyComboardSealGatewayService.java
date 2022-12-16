package com.jd.bluedragon.external.gateway.service;

import com.jd.bluedragon.common.dto.base.response.JdCResponse;
import com.jd.bluedragon.common.dto.blockcar.response.TransportInfoDto;
import com.jd.bluedragon.common.dto.comboard.request.BoardQueryReq;
import com.jd.bluedragon.common.dto.comboard.request.QueryBelongBoardReq;
import com.jd.bluedragon.common.dto.comboard.response.BoardQueryResp;
import com.jd.bluedragon.common.dto.comboard.response.QueryBelongBoardResp;
import com.jd.bluedragon.common.dto.operation.workbench.send.request.SendDetailRequest;
import com.jd.bluedragon.common.dto.operation.workbench.send.request.SendVehicleTaskRequest;
import com.jd.bluedragon.common.dto.operation.workbench.send.response.SendDestDetail;
import com.jd.bluedragon.common.dto.operation.workbench.send.response.SendVehicleTaskResponse;
import com.jd.bluedragon.common.dto.seal.request.SealCodeReq;
import com.jd.bluedragon.common.dto.seal.request.SealVehicleInfoReq;
import com.jd.bluedragon.common.dto.seal.request.SealVehicleReq;
import com.jd.bluedragon.common.dto.seal.request.TransportReq;
import com.jd.bluedragon.common.dto.seal.response.SealCodeResp;
import com.jd.bluedragon.common.dto.seal.response.SealVehicleInfoResp;
import java.util.List;

public interface JyComboardSealGatewayService {

  /**
   * 拉取任务列表
   * @param request
   * @return
   */
  JdCResponse<SendVehicleTaskResponse> fetchSendVehicleTask(SendVehicleTaskRequest request);

  /**
   * 发货任务流向明细列表
   * @param request
   * @return
   */
  JdCResponse<List<SendDestDetail>> sendDestDetail(SendDetailRequest request);

  /**
   * 查询流向任务封车数据详情
   * @param sealVehicleInfoReq
   * @return
   */
  JdCResponse<SealVehicleInfoResp> getSealVehicleInfo(SealVehicleInfoReq sealVehicleInfoReq);

  /**
   * 根据运输任务bizId查询车的封签号列表
   * @param sealCodeReq
   * @return
   */
  JdCResponse<SealCodeResp> listSealCodeByBizId(SealCodeReq sealCodeReq);

  /**
   * 根据运力编码查询运输信息
   * @param transportReq
   * @return
   *
   */
  JdCResponse<TransportInfoDto>  getTransportResourceByTransCode(TransportReq transportReq);

  /**
   * 封车数据暂存
   *
   */
  JdCResponse saveSealVehicle(SealVehicleReq sealVehicleReq);


  /**
   * 提交封车
   *
   */
  JdCResponse sealVehicle(SealVehicleReq sealVehicleReq);
  /**
   * 根据包裹号或者箱号 定位所在的板
   */
  JdCResponse<QueryBelongBoardResp> queryBelongBoardByBarCode(QueryBelongBoardReq request);


  /**
   * 根据流向查询组板列表
   */
  JdCResponse<BoardQueryResp> listComboardBySendFlow(BoardQueryReq request);


}
