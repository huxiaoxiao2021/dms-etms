package com.jd.bluedragon.distribution.jy.service.send;


import com.jd.bluedragon.common.dto.base.request.BaseReq;
import com.jd.bluedragon.common.dto.comboard.request.*;
import com.jd.bluedragon.common.dto.comboard.response.*;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;

/**
 * 组板发货岗基础服务
 */
public interface JyComBoardSendService {

  /**
   * 查询场地滑道列表数据
   */
  InvokeResult<CrossDataResp> listCrossData(CrossDataReq request);

  /**
   * 查询场地某个滑道下笼车列表数据
   */
  InvokeResult<TableTrolleyResp> listTableTrolleyUnderCross(TableTrolleyReq request);

  /**
   * 创建本岗位(小组)的常用滑道笼车(CTT:CrossTableTrolley)集合（“混扫01”）
   */
  InvokeResult<CreateGroupCTTResp> createGroupCTTData(CreateGroupCTTReq request);

  /**
   * 获取默认混扫任务名称
   */
  InvokeResult<CreateGroupCTTResp> getDefaultGroupCTTName(BaseReq request);

  /**
   * 根据包裹号或者滑道-笼车编号 定位流向信息
   * @param request
   * @return
   */
  InvokeResult<TableTrolleyResp> querySendFlowByBarCode(QuerySendFlowReq request);

  /**
   * 变更（添加或者移除滑道笼车流向）
   */
  InvokeResult addCTT2Group(AddCTTReq request);

  InvokeResult removeCTTFromGroup(RemoveCTTReq request);

  /**
   * 查询(本岗位或本场地)常用滑道笼车流向集合(【“混扫01”，“混扫02”，“混扫03”】)
   */
  InvokeResult<CTTGroupDataResp> listCTTGroupData(CTTGroupDataReq request);

  /**
   * 根据包裹号或者滑道-笼车编号 定位混扫任务信息
   */
  InvokeResult<CTTGroupDataResp> queryCTTGroupByBarCode(QueryCTTGroupReq request);

  /**
   * 查询某个混扫任务下的流向列表数据（包含流向的基础数据和统计相关数据）-支撑下钻1(流向下钻)和混扫切换页面
   */
  InvokeResult<SendFlowDataResp> listSendFlowUnderCTTGroup(SendFlowDataReq request);

  /**
   * 查询流向作业详情(支撑扫描页的流向详情查询)
   */
  InvokeResult<SendFlowDetailResp> querySendFlowDetail(SendFlowDetailReq request);

  /**
   * 查询板作业详情
   */
  InvokeResult<BoardResp> queryBoardDetail(BoardReq request);

  /**
   * 完结板
   */
  InvokeResult finishBoard(BoardReq request);

  /**
   * 结束混扫任务的所有流向的板
   */
  InvokeResult finishBoardsUnderCTTGroup(CTTGroupReq request);


  /**
   * 扫描(组板+发货)
   */
  InvokeResult<ComboardScanResp> comboardScan(ComboardScanReq request);

  /**
   * 扫描(组板+发货)
   */
  InvokeResult<ComboardScanResp> sortMachineComboard(ComboardScanReq request);


  /**
   * 查询流向下组板统计信息列表-支撑下钻2
   */
  InvokeResult<BoardStatisticsResp> queryBoardStatisticsUnderSendFlow(BoardStatisticsReq request);


  /**
   * 查询板下的已扫统计信息（已扫包裹、已扫箱子）-支撑下钻4
   */
  InvokeResult<HaveScanStatisticsResp> queryHaveScanStatisticsUnderBoard(
      HaveScanStatisticsReq request);

  /**
   * 查询箱子内部的包裹详情
   */
  InvokeResult<PackageDetailResp> listPackageDetailRespUnderBox(BoxQueryReq request);

  /**
   * 查询流向待扫统计数据-支撑下钻5
   */
  InvokeResult<WaitScanStatisticsResp> queryWaitScanStatisticsUnderSendFlow(
      WaitScanStatisticsReq request);

  /**
   * 查询流向下待扫包裹明细
   */
  InvokeResult<PackageDetailResp> listPackageDetailRespUnderSendFlow(SendFlowQueryReq request);

  /**
   * 查询某个板下的异常扫描统计数据--支撑板异常下钻3和下钻6
   */
  InvokeResult<BoardExcepStatisticsResp> queryExcepScanStatisticsUnderBoard(
      BoardExcepStatisticsReq request);

  /**
   * 查询“混扫01”多个流向下的异常扫描统计数据-支撑下钻7
   */
  InvokeResult<SendFlowExcepStatisticsResp> queryExcepScanStatisticsUnderCTTGroup(
      SendFlowExcepStatisticsReq request);

  /**
   * 查询板内件明细
   * @param request
   * @return
   */
  InvokeResult<ComboardDetailResp> listPackageOrBoxUnderBoard(BoardReq request);

  /**
   * 取消组板
   * @return
   */
  InvokeResult<Void> cancelComboard(CancelBoardReq request);

  /**
   * 取消组板
   * @return
   */
  InvokeResult<Void> cancelSortMachineComboard(CancelBoardReq request);

  /**
   * 根据包裹号或者箱号 定位所在的板
   * @param request
   * @return
   */
  InvokeResult<QueryBelongBoardResp> queryBelongBoardByBarCode(QueryBelongBoardReq request);

  InvokeResult<PackageDetailResp> listPackageDetailUnderSendFlow(SendFlowQueryReq request);
}
