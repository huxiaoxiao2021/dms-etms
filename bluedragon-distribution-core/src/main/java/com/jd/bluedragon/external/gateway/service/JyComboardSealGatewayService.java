package com.jd.bluedragon.external.gateway.service;

import com.jd.bluedragon.common.dto.base.response.JdCResponse;
import com.jd.bluedragon.common.dto.comboard.request.BoardQueryReq;
import com.jd.bluedragon.common.dto.comboard.request.BoardReq;
import com.jd.bluedragon.common.dto.comboard.request.QueryBelongBoardReq;
import com.jd.bluedragon.common.dto.comboard.response.BoardDto;
import com.jd.bluedragon.common.dto.comboard.response.BoardQueryResp;
import com.jd.bluedragon.common.dto.comboard.response.GoodsCategoryDto;
import com.jd.bluedragon.common.dto.comboard.response.QueryBelongBoardResp;
import com.jd.bluedragon.common.dto.operation.workbench.send.request.SelectSealDestRequest;
import com.jd.bluedragon.common.dto.operation.workbench.send.request.SendAbnormalPackRequest;
import com.jd.bluedragon.common.dto.operation.workbench.send.request.SendDetailRequest;
import com.jd.bluedragon.common.dto.operation.workbench.send.request.SendVehicleInfoRequest;
import com.jd.bluedragon.common.dto.operation.workbench.send.request.SendVehicleProgressRequest;
import com.jd.bluedragon.common.dto.operation.workbench.send.request.SendVehicleTaskRequest;
import com.jd.bluedragon.common.dto.operation.workbench.send.response.SendAbnormalBarCode;
import com.jd.bluedragon.common.dto.operation.workbench.send.response.SendDestDetail;
import com.jd.bluedragon.common.dto.operation.workbench.send.response.SendTaskInfo;
import com.jd.bluedragon.common.dto.operation.workbench.send.response.SendVehicleInfo;
import com.jd.bluedragon.common.dto.operation.workbench.send.response.SendVehicleProgress;
import com.jd.bluedragon.common.dto.operation.workbench.send.response.SendVehicleTaskResponse;
import com.jd.bluedragon.common.dto.operation.workbench.send.response.ToSealDestAgg;
import com.jd.bluedragon.common.dto.seal.request.CancelSealRequest;
import com.jd.bluedragon.common.dto.seal.request.CheckTransportReq;
import com.jd.bluedragon.common.dto.seal.request.JyCancelSealRequest;
import com.jd.bluedragon.common.dto.seal.request.SealCodeReq;
import com.jd.bluedragon.common.dto.seal.request.SealVehicleInfoReq;
import com.jd.bluedragon.common.dto.seal.request.SealVehicleReq;
import com.jd.bluedragon.common.dto.seal.response.JyCancelSealInfoResp;
import com.jd.bluedragon.common.dto.seal.response.SealCodeResp;
import com.jd.bluedragon.common.dto.seal.response.SealVehicleInfoResp;
import com.jd.bluedragon.common.dto.seal.response.TransportResp;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import java.util.List;

public interface JyComboardSealGatewayService {

  /**
   * 拉取任务列表
   * @param request
   * @return
   */
  JdCResponse<SendVehicleTaskResponse> fetchSendVehicleTask(SendVehicleTaskRequest request);

  /**
   * 任务流向明细列表
   * @param request
   * @return
   */
  JdCResponse<List<SendDestDetail>> sendDestDetail(SendDetailRequest request);

  /**
   * 选择封车流向
   * @param request
   * @return
   */
  JdCResponse<ToSealDestAgg> selectSealDest(SelectSealDestRequest request);

  /**
   * 发货任务详情
   * @param request
   * @return
   */
  JdCResponse<SendVehicleInfo> sendVehicleInfo(SendVehicleInfoRequest request);

  /**
   * 发货进度
   * @param request
   * @return
   */
  JdCResponse<SendVehicleProgress> loadProgress(SendVehicleProgressRequest request);

  JdCResponse<SendAbnormalBarCode> interceptedBarCodeDetail(SendAbnormalPackRequest request);

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
   * 校验运力编码和当前流向是否一致
   * @param checkTransportReq
   * @return
   *
   */
  JdCResponse<TransportResp>  checkTransCode(CheckTransportReq checkTransportReq);

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

  /**
   * 查询板货物分类统计
   */
  JdCResponse<List<GoodsCategoryDto>> queryGoodsCategoryByBoardCode(BoardReq boardReq);


  /**
   * 查询派车任务详情
   */
  JdCResponse<SendTaskInfo> sendTaskDetail(SendVehicleInfoRequest request);

  /**
   * 取消封车
   * @param request
   * @return
   */
  JdCResponse cancelSeal(JyCancelSealRequest request);

  /***
   * 根据扫描单号获取 取消的场地和批次信息
   * @param request
   * @return
   */
  JdCResponse<JyCancelSealInfoResp> getCancelSealInfo(JyCancelSealRequest request);
}
