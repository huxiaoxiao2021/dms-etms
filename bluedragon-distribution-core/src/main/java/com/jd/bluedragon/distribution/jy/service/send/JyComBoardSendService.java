package com.jd.bluedragon.distribution.jy.service.send;


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
    InvokeResult<CrossDataResp>  listCrossData(CrossDataReq request);

    /**
     * 查询场地某个滑道下笼车列表数据
     */
    InvokeResult<TableTrolleyResp> listTableTrolleyUnderCross(TableTrolleyReq request);

    /**
     * 创建本岗位(小组)的常用滑道笼车(CTT:CrossTableTrolley)集合（“混扫01”）
     */
    InvokeResult<CreateGroupCTTResp> createGroupCTTData(CreateGroupCTTReq request);
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
     * 查询某个混扫任务下的流向列表数据（包含流向的基础数据和统计相关数据）-支撑下钻1(流向下钻)和混扫切换页面
     * @param request
     * @return
     */
    InvokeResult<SendFlowDataResp> listSendFlowUnderCTTGroup(SendFlowDataReq request);

    /**
     *查询流向作业详情(支撑扫描页的流向详情查询)
     */
    InvokeResult<SendFlowDetailResp> querySendFlowDetail(SendFlowDetailReq request);
    /**
     *查询板作业详情
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
     *查询流向下组板统计信息列表-支撑下钻2
     */
    InvokeResult<BoardStatisticsResp> queryBoardStatisticsUnderSendFlow(BoardStatisticsReq request);


    /**
     * 查询板下的已扫统计信息（已扫包裹、已扫箱子）-支撑下钻4
     * @param request
     * @return
     */
    InvokeResult<HaveScanStatisticsResp> queryHaveScanStatisticsUnderBoard(HaveScanStatisticsReq request);
    /**
     * 查询箱子内部的包裹详情
     */
    InvokeResult<PackageDetailResp> listPackageDetailRespUnderBox(BoxQueryReq request);
    /**
     * 查询流向待扫统计数据-支撑下钻5
     */
    InvokeResult<WaitScanStatisticsResp> queryWaitScanStatisticsUnderSendFlow(WaitScanStatisticsReq request);

    /**
     * 查询流向下待扫包裹明细
     * @param request
     * @return
     */
    InvokeResult<PackageDetailResp> listPackageDetailRespUnderSendFlow(SendFlowQueryReq request);

    /**
     * 查询某个板下的异常扫描统计数据--支撑板异常下钻3和下钻6
     */
    InvokeResult<BoardExcepStatisticsResp> queryExcepScanStatisticsUnderBoard(BoardExcepStatisticsReq request);

    /**
     * 查询“混扫01”多个流向下的异常扫描统计数据-支撑下钻7
     * @param request
     * @return
     */
    InvokeResult<SendFlowExcepStatisticsResp> queryExcepScanStatisticsUnderCTTGroup(SendFlowExcepStatisticsReq request);

    /**
     * 取消组板
     */
}
