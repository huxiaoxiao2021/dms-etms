package com.jd.bluedragon.core.jsf.dms.impl;

import com.jd.bluedragon.core.jsf.dms.IVirtualBoardJsfManager;
import com.jd.transboard.api.dto.*;
import com.jd.transboard.api.dto.base.OperatorInfo;
import com.jd.transboard.api.service.IVirtualBoardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 分拣虚拟组板服务
 *
 * @author fanggang7
 * @copyright jd.com 京东物流JDL
 * @time 2021-08-22 11:04:22 周日
 */
@Component
public class VirtualBoardJsfManagerImpl implements IVirtualBoardJsfManager {

    @Autowired
    private IVirtualBoardService virtualBoardJsfService;


    /**
     * 根据板标获取板信息
     * @param boardCode 板标
     * @return 板信息
     * @author fanggang7
     * @time 2021-08-22 18:47:19 周日
     */
    @Override
    public Response<Board> getBoardByCode(String boardCode) {
        return virtualBoardJsfService.getBoardByCode(boardCode);
    }

    /**
     * 获取组板已存在的未完成数据
     * @param operatorInfo 操作人信息
     * @return 返回板结果
     * @author fanggang7
     * @time 2021-08-14 18:25:31 周六
     */
    @Override
    public Response<List<VirtualBoardResultDto>> getBoardUnFinishInfo(OperatorInfo operatorInfo) {
        return virtualBoardJsfService.getBoardUnFinishInfo(operatorInfo);
    }

    /**
     * 根据目的地创建新的板或得到已有的可用的板，目的地的板已存在且未完结，则直接返回该板号
     * @param addOrGetVirtualBoardPo 新建板
     * @return 返回板结果
     * @author fanggang7
     * @time 2021-08-14 18:25:31 周六
     */
    @Override
    public Response<VirtualBoardResultDto> createOrGetBoard(AddOrGetVirtualBoardPo addOrGetVirtualBoardPo) {
        return virtualBoardJsfService.createOrGetBoard(addOrGetVirtualBoardPo);
    }

    /**
     * 组板
     * @param bindToVirtualBoardPo 新建板
     * @return 返回板结果
     * @author fanggang7
     * @time 2021-08-14 18:25:31 周六
     */
    @Override
    public Response<VirtualBoardResultDto> bindToBoard(BindToVirtualBoardPo bindToVirtualBoardPo) {
        return virtualBoardJsfService.bindToBoard(bindToVirtualBoardPo);
    }

    /**
     * 删除流向
     * @param removeDestinationPo 删除流向请求参数
     * @return 返回板结果
     * @author fanggang7
     * @time 2021-08-14 18:25:31 周六
     */
    @Override
    public Response<Void> removeDestination(RemoveDestinationPo removeDestinationPo) {
        return virtualBoardJsfService.removeDestination(removeDestinationPo);
    }

    /**
     * 完结板号
     * @param closeVirtualBoardPo 完结板请求参数
     * @return 返回板结果
     * @author fanggang7
     * @time 2021-08-14 18:25:31 周六
     */
    @Override
    public Response<Void> closeBoard(CloseVirtualBoardPo closeVirtualBoardPo) {
        return virtualBoardJsfService.closeBoard(closeVirtualBoardPo);
    }

    /**
     * 取消组板
     * @param unbindToVirtualBoardPo 取消组板请求参数
     * @return 返回板结果
     * @author fanggang7
     * @time 2021-08-14 18:25:31 周六
     */
    @Override
    public Response<UnbindVirtualBoardResultDto> unbindToBoard(UnbindToVirtualBoardPo unbindToVirtualBoardPo) {
        return virtualBoardJsfService.unbindToBoard(unbindToVirtualBoardPo);
    }
}
