package com.jd.bluedragon.distribution.external.gateway.service.impl;

import com.jd.bd.dms.automatic.sdk.common.dto.BaseDmsAutoJsfResponse;
import com.jd.bd.dms.automatic.sdk.modules.device.DeviceConfigInfoJsfService;
import com.jd.bd.dms.automatic.sdk.modules.device.dto.DeviceConfigSimpleDto;
import com.jd.bluedragon.Constants;
import com.jd.bluedragon.common.dto.base.request.OperatorInfo;
import com.jd.bluedragon.common.dto.base.response.JdCResponse;
import com.jd.bluedragon.common.dto.board.request.*;
import com.jd.bluedragon.common.dto.board.response.UnbindVirtualBoardResultDto;
import com.jd.bluedragon.common.dto.board.response.VirtualBoardResultDto;
import com.jd.bluedragon.distribution.board.SortBoardJsfService;
import com.jd.bluedragon.distribution.board.domain.Response;
import com.jd.bluedragon.distribution.board.service.VirtualBoardService;
import com.jd.bluedragon.dms.utils.WaybillUtil;
import com.jd.bluedragon.external.gateway.service.VirtualBoardGatewayService;
import com.jd.bluedragon.utils.BusinessHelper;
import com.jd.transboard.api.dto.Board;
import com.jd.transboard.api.service.GroupBoardService;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 虚拟组板服务
 *
 * @author fanggang7
 * @copyright jd.com 京东物流JDL
 * @time 2021-08-20 12:30:07 周五
 */
@Service("virtualBoardGatewayServiceImpl")
public class VirtualBoardGatewayServiceImpl implements VirtualBoardGatewayService {

    @Autowired
    private VirtualBoardService virtualBoardService;
    @Autowired
    private GroupBoardService groupBoardService;
    @Autowired
    private SortBoardJsfService sortBoardJsfService;
    @Autowired
    private DeviceConfigInfoJsfService deviceConfigInfoJsfService;

    /**
     * 获取组板已存在的未完成数据
     * @param operatorInfo 操作人信息
     * @return 返回板结果
     * @author fanggang7
     * @time 2021-08-14 18:25:31 周六
     */
    @Override
    public JdCResponse<List<VirtualBoardResultDto>> getBoardUnFinishInfo(OperatorInfo operatorInfo) {
        return virtualBoardService.getBoardUnFinishInfo(operatorInfo);
    }

    /**
     * 根据目的地创建新的板或得到已有的可用的板，目的地的板已存在且未完结，则直接返回该板号
     * @param addOrGetVirtualBoardPo 新建板
     * @return 返回板结果
     * @author fanggang7
     * @time 2021-08-14 18:25:31 周六
     */
    @Override
    @JProfiler(jKey = "DMSWEB.VirtualBoardServiceImpl.createOrGetBoard",jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
    public JdCResponse<VirtualBoardResultDto> createOrGetBoard(AddOrGetVirtualBoardPo addOrGetVirtualBoardPo) {
        return virtualBoardService.createOrGetBoard(addOrGetVirtualBoardPo);
    }

    /**
     * 组板
     * @param bindToVirtualBoardPo 新建板
     * @return 返回板结果
     * @author fanggang7
     * @time 2021-08-14 18:25:31 周六
     */
    @Override
    @JProfiler(jKey = "DMSWEB.VirtualBoardServiceImpl.bindToBoard",jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
    public JdCResponse<VirtualBoardResultDto> bindToBoard(BindToVirtualBoardPo bindToVirtualBoardPo) {
        return virtualBoardService.bindToBoard(bindToVirtualBoardPo);
    }

    /**
     * 删除流向
     * @param removeDestinationPo 删除流向请求参数
     * @return 返回板结果
     * @author fanggang7
     * @time 2021-08-14 18:25:31 周六
     */
    @Override
    @JProfiler(jKey = "DMSWEB.VirtualBoardServiceImpl.removeDestination",jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
    public JdCResponse<Void> removeDestination(RemoveDestinationPo removeDestinationPo) {
        return virtualBoardService.removeDestination(removeDestinationPo);
    }

    /**
     * 完结板号
     * @param closeVirtualBoardPo 完结板请求参数
     * @return 返回板结果
     * @author fanggang7
     * @time 2021-08-14 18:25:31 周六
     */
    @Override
    @JProfiler(jKey = "DMSWEB.VirtualBoardServiceImpl.closeBoard",jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
    public JdCResponse<Void> closeBoard(CloseVirtualBoardPo closeVirtualBoardPo) {
        return virtualBoardService.closeBoard(closeVirtualBoardPo);
    }

    /**
     * 取消组板
     * @param unbindToVirtualBoardPo 取消组板请求参数
     * @return 返回板结果
     * @author fanggang7
     * @time 2021-08-14 18:25:31 周六
     */
    @Override
    @JProfiler(jKey = "DMSWEB.VirtualBoardServiceImpl.unbindToBoard",jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
    public JdCResponse<UnbindVirtualBoardResultDto> unbindToBoard(UnbindToVirtualBoardPo unbindToVirtualBoardPo) {
        return virtualBoardService.unbindToBoard(unbindToVirtualBoardPo);
    }

    /**
     * 查询是否开通组板功能
     * @param operatorInfo 操作人信息
     * @return 返回是否能使用结果
     * @author fanggang7
     * @time 2021-09-14 11:22:19 周二
     */
    @Override
    @JProfiler(jKey = "DMSWEB.VirtualBoardServiceImpl.canUseMenu",jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
    public JdCResponse<Boolean> canUseMenu(OperatorInfo operatorInfo) {
        return virtualBoardService.canUseMenu(operatorInfo);
    }

    /**
     * 交接板号信息
     * @param handoverVirtualBoardPo 包含板号以及交接到人信息
     * @return
     */
    @Override
    @JProfiler(jKey = "DMSWEB.VirtualBoardServiceImpl.handoverBoard",jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
    public JdCResponse<Void> handoverBoard(HandoverVirtualBoardPo handoverVirtualBoardPo) {
        return virtualBoardService.handoverBoard(handoverVirtualBoardPo);
    }

    @Override
    @JProfiler(jKey = "DMSWEB.VirtualBoardServiceImpl.autoBoardComplete",jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
    public JdCResponse<Void> autoBoardComplete(AutoBoardCompleteRequest request) {
        JdCResponse jdCResponse = new JdCResponse();
        jdCResponse.toSucceed();

        com.jd.bluedragon.distribution.board.domain.AutoBoardCompleteRequest domain =
                new com.jd.bluedragon.distribution.board.domain.AutoBoardCompleteRequest();
        BeanUtils.copyProperties(request, domain);
        Response<Void> response = sortBoardJsfService.autoBoardComplete(domain);
        jdCResponse.setCode(response.getCode());
        jdCResponse.setMessage(response.getMessage());
        return jdCResponse;


    }

    @Override
    public JdCResponse<VirtualBoardResultDto> autoBoardDetail(AutoBoardCompleteRequest request) {
        JdCResponse<VirtualBoardResultDto> jdCResponse = new JdCResponse<VirtualBoardResultDto>();
        jdCResponse.toSucceed();

        com.jd.transboard.api.dto.Response<Board> boardRes = groupBoardService.getBoardByBoxCode(request.getBarcode(), request.getSiteCode());
        if (boardRes.getCode()!=200){
            jdCResponse.toFail("查询组板信息失败，请退出重试!");
            return jdCResponse;
        }
        Board board = boardRes.getData();
        if (board==null){
            jdCResponse.toFail("查询组板信息失败，请退出重试!");
            return jdCResponse;
        }

        com.jd.transboard.api.dto.Response<List<String>> boxCodeRes = groupBoardService.getBoxesByBoardCode(board.getCode());
        if (boxCodeRes.getCode()!=200){
            jdCResponse.toFail("查询组板包裹(箱号)信息失败，请退出重试!");
            return jdCResponse;
        }
        List<String> boxes = boxCodeRes.getData();
        if (CollectionUtils.isEmpty(boxes)){
            jdCResponse.toFail("查询组板包裹(箱号)信息失败，请退出重试!");
            return jdCResponse;
        }
        VirtualBoardResultDto result = new VirtualBoardResultDto();
        result.setBoardCode(board.getCode());
        result.setBoardStatus(board.getStatus());
        result.setDestinationId(board.getDestinationId());
        result.setDestinationName(board.getDestination());
        int packageCount = 0;
        int boxCount = 0;
        for (String code:boxes){
            if (WaybillUtil.isPackageCode(code)){
                packageCount++;
            }
            if (BusinessHelper.isBoxcode(code)){
                boxCount++;
            }
        }
        result.setPackageTotal(packageCount);
        result.setBoxTotal(boxCount);
        return jdCResponse;
    }

    @Override
    public JdCResponse<Map<String, Boolean>> getSortMachineBySiteCode(Integer siteCode) {
        JdCResponse<Map<String, Boolean>> jdCResponse = new JdCResponse<Map<String, Boolean>>();
        jdCResponse.toSucceed();
        if(siteCode == null){
            jdCResponse.toFail("场地编码为空，请退出重试!");
        }
        deviceConfigInfoJsfService.findDeviceConfigListByCondition(siteCode.toString(), null);
        BaseDmsAutoJsfResponse<Map<String, Boolean>> response =  deviceConfigInfoJsfService.getAutoMachineAndCheckCombinationBoard(siteCode);
        if(response.getStatusCode() != 200){
             jdCResponse.toFail("查询设备编码失败，请退出重试!");
             return jdCResponse;
        }

        if(MapUtils.isEmpty(response.getData())){
            return jdCResponse;
        }
        jdCResponse.setData(response.getData());
        return jdCResponse;
    }


}
