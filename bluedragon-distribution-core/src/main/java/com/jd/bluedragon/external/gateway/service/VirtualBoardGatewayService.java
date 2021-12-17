package com.jd.bluedragon.external.gateway.service;

import com.jd.bluedragon.common.dto.base.request.OperatorInfo;
import com.jd.bluedragon.common.dto.base.response.JdCResponse;
import com.jd.bluedragon.common.dto.board.request.*;
import com.jd.bluedragon.common.dto.board.response.UnbindVirtualBoardResultDto;
import com.jd.bluedragon.common.dto.board.response.VirtualBoardResultDto;

import java.util.List;

/**
 * 分拣虚拟组板服务
 *
 * @author fanggang7
 * @copyright jd.com 京东物流JDL
 * @time 2021-08-20 12:18:28 周五
 */
public interface VirtualBoardGatewayService {

    /**
     * 获取组板已存在的未完成数据
     * @param operatorInfo 操作人信息
     * @return 返回板结果
     * @author fanggang7
     * @time 2021-08-14 18:25:31 周六
     */
    JdCResponse<List<VirtualBoardResultDto>> getBoardUnFinishInfo(OperatorInfo operatorInfo);

    /**
     * 根据目的地创建新的板或得到已有的可用的板，目的地的板已存在且未完结，则直接返回该板号
     * @param addOrGetVirtualBoardPo 新建板
     * @return 返回板结果
     * @author fanggang7
     * @time 2021-08-14 18:25:31 周六
     */
    JdCResponse<VirtualBoardResultDto> createOrGetBoard(AddOrGetVirtualBoardPo addOrGetVirtualBoardPo);

    /**
     * 组板`
     * @param bindToVirtualBoardPo 新建板
     * @return 返回板结果
     * @author fanggang7
     * @time 2021-08-14 18:25:31 周六
     */
    JdCResponse<VirtualBoardResultDto> bindToBoard(BindToVirtualBoardPo bindToVirtualBoardPo);

    /**
     * 删除流向
     * @param removeDestinationPo 删除流向请求参数
     * @return 返回板结果
     * @author fanggang7
     * @time 2021-08-14 18:25:31 周六
     */
    JdCResponse<Void> removeDestination(RemoveDestinationPo removeDestinationPo);

    /**
     * 完结板号
     * @param closeVirtualBoardPo 完结板请求参数
     * @return 返回板结果
     * @author fanggang7
     * @time 2021-08-14 18:25:31 周六
     */
    JdCResponse<Void> closeBoard(CloseVirtualBoardPo closeVirtualBoardPo);

    /**
     * 取消组板
     * @param unbindToVirtualBoardPo 取消组板请求参数
     * @return 返回板结果
     * @author fanggang7
     * @time 2021-08-14 18:25:31 周六
     */
    JdCResponse<UnbindVirtualBoardResultDto> unbindToBoard(UnbindToVirtualBoardPo unbindToVirtualBoardPo);

    /**
     * 查询是否开通组板功能
     * @param operatorInfo 操作人信息
     * @return 返回是否能使用结果
     * @author fanggang7
     * @time 2021-09-14 11:22:19 周二
     */
    JdCResponse<Boolean> canUseMenu(OperatorInfo operatorInfo);

    /**
     * 交接板号
     * @param handoverVirtualBoardPo 包含板号以及交接到人信息
     * @return
     */
    JdCResponse<Void> handoverBoard(HandoverVirtualBoardPo handoverVirtualBoardPo);


    /**
     * 自动化完结板
     * @param request
     * @return
     */
    JdCResponse<Void> autoBoardComplete(AutoBoardCompleteRequest request);

    /**
     * 更加场地 查询分拣机编码
     * @param siteCode
     * @return
     */
    JdCResponse<List<String>> getSortMachineBySiteCode(Integer siteCode);
}
