package com.jd.bluedragon.core.jsf.dms;

import com.jd.transboard.api.dto.*;
import com.jd.transboard.api.dto.base.OperatorInfo;

import java.util.List;

/**
 * 虚拟组板服务
 *
 * @author fanggang7
 * @copyright jd.com 京东物流JDL
 * @time 2021-08-22 10:59:38 周日
 */
public interface IVirtualBoardJsfManager {

    /**
     * 根据板标获取板信息
     * @param boardCode 板标
     * @return 板信息
     * @author fanggang7
     * @time 2021-08-22 18:47:19 周日
     */
    Response<Board> getBoardByCode(String boardCode);

    /**
     * 获取组板已存在的未完成数据
     * @param operatorInfo 操作人信息
     * @return 返回板结果
     * @author fanggang7
     * @time 2021-08-14 18:25:31 周六
     */
    Response<List<VirtualBoardResultDto>> getBoardUnFinishInfo(OperatorInfo operatorInfo);

    /**
     * 根据目的地创建新的板或得到已有的可用的板，目的地的板已存在且未完结，则直接返回该板号
     * @param addOrGetVirtualBoardPo 新建板
     * @return 返回板结果
     * @author fanggang7
     * @time 2021-08-14 18:25:31 周六
     */
    Response<VirtualBoardResultDto> createOrGetBoard(AddOrGetVirtualBoardPo addOrGetVirtualBoardPo);

    /**
     * 组板
     * @param bindToVirtualBoardPo 新建板
     * @return 返回板结果
     * @author fanggang7
     * @time 2021-08-14 18:25:31 周六
     */
    Response<VirtualBoardResultDto> bindToBoard(BindToVirtualBoardPo bindToVirtualBoardPo);

    /**
     * 删除流向
     * @param removeDestinationPo 删除流向请求参数
     * @return 返回板结果
     * @author fanggang7
     * @time 2021-08-14 18:25:31 周六
     */
    Response<Void> removeDestination(RemoveDestinationPo removeDestinationPo);

    /**
     * 完结板号
     * @param closeVirtualBoardPo 完结板请求参数
     * @return 返回板结果
     * @author fanggang7
     * @time 2021-08-14 18:25:31 周六
     */
    Response<Void> closeBoard(CloseVirtualBoardPo closeVirtualBoardPo);

    /**
     * 取消组板
     * @param unbindToVirtualBoardPo 取消组板请求参数
     * @return 返回板结果
     * @author fanggang7
     * @time 2021-08-14 18:25:31 周六
     */
    Response<UnbindVirtualBoardResultDto> unbindToBoard(UnbindToVirtualBoardPo unbindToVirtualBoardPo);

    /**
     * 交接板号
     * @param handoverVirtualBoardPo 交接板请求参数
     * @return 返回板结果
     * @author wzx
     * @time 2021-10-11 14:44:02
     */
    Response<Void> handoverBoard(HandoverVirtualBoardPo handoverVirtualBoardPo);

    /**
     * 根据板号获取箱号统计数据
     * @param boardCode
     * @return
     */
    Response<VirtualBoardResultDto> getBoxCountByBoardCode(String boardCode);
}
