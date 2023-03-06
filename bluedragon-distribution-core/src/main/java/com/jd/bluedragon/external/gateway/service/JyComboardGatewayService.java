package com.jd.bluedragon.external.gateway.service;

import com.jd.bluedragon.common.dto.base.request.BaseReq;
import com.jd.bluedragon.common.dto.comboard.request.*;
import com.jd.bluedragon.common.dto.comboard.response.*;
import com.jd.bluedragon.common.dto.base.response.JdCResponse;

public interface JyComboardGatewayService {

  /**
   * 查询场地滑道列表数据
   */
  JdCResponse<CrossDataResp> listCrossData(CrossDataReq request);

  /**
   * 查询场地某个滑道下笼车列表数据
   */
  JdCResponse<TableTrolleyResp> listTableTrolleyUnderCross(TableTrolleyReq request);

  /**
   * 根据包裹号或者滑道-笼车编号 定位流向信息
   */
  JdCResponse<TableTrolleyResp> querySendFlowByBarCode(QuerySendFlowReq request);

  /**
   * 创建本岗位(小组)的常用滑道笼车(CTT:CrossTableTrolley)集合（“混扫01”）
   */
  JdCResponse<CreateGroupCTTResp> createGroupCTTData(CreateGroupCTTReq request);

  /**
   * 获取默认混扫任务名称
   */
  JdCResponse<CreateGroupCTTResp> getDefaultGroupCTTName(BaseReq request);

  /**
   * 变更（添加或者移除滑道笼车流向）
   */
  JdCResponse<Void> addCTT2Group(AddCTTReq request);

  JdCResponse<Void> removeCTTFromGroup(RemoveCTTReq request);

  /**
   * 查询(本岗位或本场地)常用滑道笼车流向集合(【“混扫01”，“混扫02”，“混扫03”】)
   */
  JdCResponse<CTTGroupDataResp> listCTTGroupData(CTTGroupDataReq request);

  /**
   * 根据包裹号或者滑道-笼车编号 定位混扫任务信息
   */
  JdCResponse<CTTGroupDataResp> queryCTTGroupByBarCode(QueryCTTGroupReq request);

  /**
   * 查询某个混扫任务下的流向列表数据（包含流向的基础数据和统计相关数据）-支撑下钻1(流向下钻)和混扫切换页面
   */
  JdCResponse<SendFlowDataResp> listSendFlowUnderCTTGroup(SendFlowDataReq request);

  /**
   * 查询流向下板作业详情(支撑扫描页的流向详情查询)
   */
  JdCResponse<SendFlowDetailResp> querySendFlowDetail(SendFlowDetailReq request);

  /**
   * 查询板作业详情
   */
  JdCResponse<BoardResp> queryBoardDetail(BoardReq request);

  /**
   * 完结板
   * @return
   */
  JdCResponse<String> finishBoard(BoardReq request);

  /**
   * 结束混扫任务的所有流向的板
   * @return
   */
  JdCResponse<Void> finishBoardsUnderCTTGroup(CTTGroupReq request);


  /**
   * 扫描(组板+发货)
   */
  JdCResponse<ComboardScanResp> comboardScan(ComboardScanReq request);

  /**
   * 查询流向下组板统计信息列表-支撑下钻2
   */
  JdCResponse<BoardStatisticsResp> queryBoardStatisticsUnderSendFlow(BoardStatisticsReq request);

  /**
   * 根据包裹号或者箱号 定位所在的板
   */
  JdCResponse<QueryBelongBoardResp> queryBelongBoardByBarCode(QueryBelongBoardReq request);


  /**
   * 查询板下的已扫统计信息（已扫包裹、已扫箱子）-支撑下钻4
   */
  JdCResponse<HaveScanStatisticsResp> queryHaveScanStatisticsUnderBoard(
      HaveScanStatisticsReq request);

  /**
   * 查询箱子内部的包裹详情
   */
  JdCResponse<PackageDetailResp> listPackageDetailUnderBox(BoxQueryReq request);

  /**
   * 查询流向待扫统计数据-支撑下钻5
   */
  JdCResponse<WaitScanStatisticsResp> queryWaitScanStatisticsUnderSendFlow(
      WaitScanStatisticsReq request);

  /**
   * 查询流向下待扫、拦截等包裹明细
   */
  JdCResponse<PackageDetailResp> listPackageDetailUnderSendFlow(SendFlowQueryReq request);

  /**
   * 查询某个板下的异常扫描统计数据--支撑板异常下钻3和下钻6
   */
  JdCResponse<BoardExcepStatisticsResp> queryExcepScanStatisticsUnderBoard(
      BoardExcepStatisticsReq request);

  /**
   * 查询“混扫01”多个流向下的异常扫描统计数据-支撑下钻7
   */
  JdCResponse<SendFlowExcepStatisticsResp> queryExcepScanStatisticsUnderCTTGroup(
      SendFlowExcepStatisticsReq request);

  /**
   * 查询板内件明细
   */
  JdCResponse<ComboardDetailResp> listPackageOrBoxUnderBoard(BoardReq request);

  /**
   * 取消组板
   * @param request
   * @return
   */
  JdCResponse<Void> cancelComboard(CancelBoardReq request);

  /**
   * 校验用户名和密码逻辑
   * @param request
   * @return
   */
  JdCResponse erpPasswdCheck(UserInfoReq request);


  /**
   * 删除混扫任务逻辑
   * @param request
   * @return
   */
  JdCResponse<String> deleteCTTGroup(DeleteCTTGroupReq request);

  /**
   * 获取扫描人员详情
   * @param req
   * @return
   */
  JdCResponse<SendFlowDataResp> queryUserByStartSiteCode(BoardReq req);

}
