package com.jd.bluedragon.distribution.jy.service.send;


import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.configure.domain.GroupInfo;

/**
 * 组板发货岗基础服务
 */
public interface JyComBoardSendService {
    /**
     * 查询场地滑道列表数据
     *//*
    InvokeResult<CrossDataResp>  listCrossData(CrossDataReq request);

    *//**
     * 查询场地某个滑道下笼车列表数据
     *//*
    InvokeResult<TableTrolleyResp> listTableTrolleyUnderCross(TableTrolleyReq request);

    *//**
     * 创建本岗位(小组)的常用滑道笼车(CTT:CrossTableTrolley)集合（“混扫01”）
     * “常用流向任务SendFlowTask”
     *//*
    InvokeResult<CreateGroupCTTResp> createGroupCTTData(CreateGroupCTTReq request);
    *//**
     * 变更（添加或者移除滑道笼车流向）
     *//*
    InvokeResult addCTTFromGroup(AddCTTReq request);
    InvokeResult removeCTTFromGroup(RemoveCTTReq request);

    *//**
     * 查询(本岗位/本场地)下常用滑道笼车流向集合(“混扫01”，“混扫02”，“混扫03”)
     *//*
    InvokeResult<CTTGroupDataResp> listCTTGroupData(CTTGroupDataReq request);

    *//**
     * 查询某个混扫任务下的流向列表数据-支撑下钻1(流向下钻)
     * @param request  "listSendFlowTaskUnderCTTGroup"
     * @return
     *//*
    InvokeResult<TableTrolleyResp> listTableTrolleyUnderCTTGroup(TableTrolleyReq request);

    *//**
     *查询流向作业详情
     *//*
    InvokeResult<SendFlowResp> querySendFlowDetail(SendFlowReq request);
    *//**
     *查询板作业详情
     *//*
    InvokeResult<BoardResp> queryBoardDetail(BoardReq request);
    *//**
     * 完结板
     *//*
    InvokeResult finishBoard(BoardReq request);

    *//**
     * 结束混扫任务的所有流向的板
     *//*
    InvokeResult finishBoardsUnderCTTGroup(CTTGroupReq request);


    *//**
     * 扫描(组板+发货)
     *//*
    InvokeResult comboardScan(ComboardScanReq request);
    *//**
     * 查询岗位下人数和实际作业人数
     *//*
    InvokeResult<GroupUsersResp> queryGroupUsers(GroupInfoReq request);

    *//**
     *查询流向下组板统计信息列表
     *//*
    InvokeResult<BoardStatisticsResp> queryBoardStatisticsUnderSendFlow(BoardStatisticsReq request);


    *//**
     * 查询板下的已扫统计信息（已扫包裹、已扫箱子）
     * @param boardCode
     * @return
     *//*
    InvokeResult<HaveScanStatisticsResp> queryHaveScanStatisticsUnderBoard(String boardCode);
    *//**
     * 查询流向待扫统计数据
     *//*
    InvokeResult<WaitScanStatisticsResp> queryWaitScanStatisticsUnderSendFlow(SendFlowReq request);
    *//**
     * 查询板下的异常扫描统计数据
     *//*
    InvokeResult<ExcepScanStatisticsResp> queryExcepScanStatisticsUnderBoard(SendFlowReq request);*/
}
