package com.jd.bluedragon.distribution.external.gateway.service.impl;

import com.google.common.collect.Lists;
import com.jd.bd.dms.automatic.sdk.common.dto.BaseDmsAutoJsfResponse;
import com.jd.bd.dms.automatic.sdk.modules.device.DeviceConfigInfoJsfService;
import com.jd.bluedragon.Constants;
import com.jd.bluedragon.common.dto.base.request.OperatorInfo;
import com.jd.bluedragon.common.dto.base.response.JdCResponse;
import com.jd.bluedragon.common.dto.board.BizSourceEnum;
import com.jd.bluedragon.common.dto.board.request.*;
import com.jd.bluedragon.common.dto.board.response.UnbindVirtualBoardResultDto;
import com.jd.bluedragon.common.dto.board.response.VirtualBoardResultDto;
import com.jd.bluedragon.distribution.api.utils.JsonHelper;
import com.jd.bluedragon.distribution.board.SortBoardJsfService;
import com.jd.bluedragon.distribution.board.domain.Response;
import com.jd.bluedragon.distribution.board.service.VirtualBoardService;
import com.jd.bluedragon.dms.utils.WaybillUtil;
import com.jd.bluedragon.external.gateway.service.VirtualBoardGatewayService;
import com.jd.bluedragon.utils.BusinessHelper;
import com.jd.dms.workbench.utils.sdk.base.Result;
import com.jd.ldop.utils.StringUtils;
import com.jd.transboard.api.dto.Board;
import com.jd.transboard.api.service.GroupBoardService;
import com.jd.transboard.api.service.IVirtualBoardService;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
@Slf4j
public class VirtualBoardGatewayServiceImpl implements VirtualBoardGatewayService {

    @Autowired
    private VirtualBoardService virtualBoardService;
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
    @JProfiler(jKey = "DMSWEB.VirtualBoardGatewayServiceImpl.getBoardUnFinishInfo",jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
    public JdCResponse<List<VirtualBoardResultDto>> getBoardUnFinishInfo(OperatorInfo operatorInfo) {
        if(operatorInfo.getBizSource() == null){
            operatorInfo.setBizSource(BizSourceEnum.PDA.getValue());
        }
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
    @JProfiler(jKey = "DMSWEB.VirtualBoardGatewayServiceImpl.createOrGetBoard",jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
    public JdCResponse<VirtualBoardResultDto> createOrGetBoard(AddOrGetVirtualBoardPo addOrGetVirtualBoardPo) {
        if(addOrGetVirtualBoardPo.getBizSource() == null){
            addOrGetVirtualBoardPo.setBizSource(BizSourceEnum.PDA.getValue());
        }
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
    @JProfiler(jKey = "DMSWEB.VirtualBoardGatewayServiceImpl.bindToBoard",jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
    public JdCResponse<VirtualBoardResultDto> bindToBoard(BindToVirtualBoardPo bindToVirtualBoardPo) {
        if(bindToVirtualBoardPo.getBizSource() == null){
            bindToVirtualBoardPo.setBizSource(BizSourceEnum.PDA.getValue());
        }
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
    @JProfiler(jKey = "DMSWEB.VirtualBoardGatewayServiceImpl.removeDestination",jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
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
    @JProfiler(jKey = "DMSWEB.VirtualBoardGatewayServiceImpl.closeBoard",jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
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
    @JProfiler(jKey = "DMSWEB.VirtualBoardGatewayServiceImpl.unbindToBoard",jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
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
    @JProfiler(jKey = "DMSWEB.VirtualBoardGatewayServiceImpl.canUseMenu",jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
    public JdCResponse<Boolean> canUseMenu(OperatorInfo operatorInfo) {
        return virtualBoardService.canUseMenu(operatorInfo);
    }

    /**
     * 交接板号信息
     * @param handoverVirtualBoardPo 包含板号以及交接到人信息
     * @return
     */
    @Override
    @JProfiler(jKey = "DMSWEB.VirtualBoardGatewayServiceImpl.handoverBoard",jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
    public JdCResponse<Void> handoverBoard(HandoverVirtualBoardPo handoverVirtualBoardPo) {
        return virtualBoardService.handoverBoard(handoverVirtualBoardPo);
    }

    @Override
    @JProfiler(jKey = "DMSWEB.VirtualBoardGatewayServiceImpl.autoBoardComplete",jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
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
    @JProfiler(jKey = "DMSWEB.VirtualBoardGatewayServiceImpl.autoBoardDetail",jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
    public JdCResponse<List<VirtualBoardResultDto>> autoBoardDetail(AutoBoardCompleteRequest request) {
        JdCResponse<List<VirtualBoardResultDto>> jdCResponse = new JdCResponse<List<VirtualBoardResultDto>>();
        jdCResponse.toSucceed();
        try{
            com.jd.bluedragon.distribution.board.domain.AutoBoardCompleteRequest domain =
                    new com.jd.bluedragon.distribution.board.domain.AutoBoardCompleteRequest();
            BeanUtils.copyProperties(request, domain);
            log.info("计算格口获取板号信息,request:"+ JsonHelper.toJson(request));
            Response<List<String>> boardCodeRes = sortBoardJsfService.calcBoard(domain);
            log.info("计算格口获取板号信息,request:"+ JsonHelper.toJson(request)+",result:"+JsonHelper.toJson(boardCodeRes));

            if (boardCodeRes.getCode()!=200){
                jdCResponse.toFail(StringUtils.isEmpty(boardCodeRes.getMessage())?"计算板号失败，请退出重试!":boardCodeRes.getMessage());
                return jdCResponse;
            }
            List<VirtualBoardResultDto> results = Lists.newArrayList();

            List<String> boardCodes = boardCodeRes.getData();
            for (String boardCode:boardCodes){
                log.info("获取板号统计信息,request:"+ boardCode);
                JdCResponse<VirtualBoardResultDto> result = virtualBoardService.getBoxCountByBoardCode(boardCode);
                    log.info("获取板号统计信息,request:"+ boardCodes+",result:"+JsonHelper.toJson(result));
                if (result.getCode()!=200){
                    jdCResponse.toFail("查询组板信息失败，请退出重试!");
                    return jdCResponse;
                }
                VirtualBoardResultDto data = result.getData();
                results.add(data);
            }
            jdCResponse.setData(results);
            return jdCResponse;
        }catch(Exception e){
            log.error("组板查询接口异常,request:"+ JsonHelper.toJson(request),e);
            jdCResponse.toFail("组板查询接口异常，请联系分拣小秘排查");
            return jdCResponse;
        }
    }

    @Override
    @JProfiler(jKey = "DMSWEB.VirtualBoardGatewayServiceImpl.getSortMachineBySiteCode",jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
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
