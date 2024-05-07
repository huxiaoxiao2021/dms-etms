package com.jd.bluedragon.distribution.external.gateway.service.impl;

import com.jd.bd.dms.automatic.sdk.common.dto.BaseDmsAutoJsfResponse;
import com.jd.bd.dms.automatic.sdk.modules.device.DeviceConfigInfoJsfService;
import com.jd.bluedragon.Constants;
import com.jd.bluedragon.common.dto.base.response.JdCResponse;
import com.jd.bluedragon.common.dto.cage.request.AutoCageRequest;
import com.jd.bluedragon.configuration.DmsConfigManager;
import com.jd.bluedragon.core.jmq.producer.DefaultJMQProducer;
import com.jd.bluedragon.distribution.api.utils.JsonHelper;
import com.jd.bluedragon.distribution.autoCage.domain.AutoCageMq;
import com.jd.bluedragon.distribution.board.SortBoardJsfService;
import com.jd.bluedragon.distribution.board.domain.AutoBoardCompleteRequest;
import com.jd.bluedragon.distribution.board.service.BoardCombinationService;
import com.jd.bluedragon.distribution.sdk.common.domain.InvokeResult;
import com.jd.bluedragon.distribution.sdk.modules.cage.DeviceCageJsfService;
import com.jd.bluedragon.external.gateway.service.DeviceCageGatewayService;
import com.jd.transboard.api.dto.Board;
import com.jd.transboard.api.dto.Response;
import com.jd.transboard.api.enums.BoardStatus;
import com.jd.transboard.api.service.GroupBoardService;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
public class DeviceCageGatewayServiceImpl implements DeviceCageGatewayService {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private DmsConfigManager dmsConfigManager;
    @Autowired
    private DeviceConfigInfoJsfService deviceConfigInfoJsfService;
    @Autowired
    private DeviceCageJsfService deviceCageJsfService;
    @Autowired
    private BoardCombinationService boardCombinationService;
    @Autowired
    private GroupBoardService groupBoardService;
    @Autowired
    private SortBoardJsfService sortBoardJsfService;
    @Autowired
    @Qualifier(value = "autoCageProducer")
    private DefaultJMQProducer autoCageProducer;


    @Override
    @JProfiler(jKey = "DMSWEB.DeviceCageGatewayServiceImpl.getSortMachineBySiteCode",jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
    public JdCResponse<List<String>> getSortMachineBySiteCode(Integer siteCode) {
        JdCResponse<List<String>> jdCResponse = new JdCResponse<List<String>>();
        jdCResponse.toSucceed();
        if(siteCode == null){
            jdCResponse.toFail("场地编码为空，请退出重试!");
        }

        BaseDmsAutoJsfResponse<List<String>> response =  deviceConfigInfoJsfService.getAutoMachineAndCheckCage(siteCode);
        if(response.getStatusCode() != 200){
            jdCResponse.toFail("查询设备编码失败，请退出重试!");
            return jdCResponse;
        }

        if(CollectionUtils.isEmpty(response.getData())){
            return jdCResponse;
        }
        jdCResponse.setData(response.getData());
        return jdCResponse;
    }

    @Override
    @JProfiler(jKey = "DMSWEB.DeviceCageGatewayServiceImpl.querySortingInfo",jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
    public JdCResponse<Boolean> querySortingInfo(AutoCageRequest request) {
        JdCResponse<Boolean> jdCResponse = new JdCResponse<Boolean>();
        jdCResponse.toSucceed();

        if(StringUtils.isEmpty(request.getBarcode())){
            jdCResponse.toFail("包裹号/箱号为空，请退出重试!");
        }

        if(StringUtils.isEmpty(request.getMachineCode())){
            jdCResponse.toFail("设备编号为空，请退出重试!");
        }
        com.jd.bluedragon.distribution.sdk.modules.cage.domain.AutoCageRequest autoCageRequest = new com.jd.bluedragon.distribution.sdk.modules.cage.domain.AutoCageRequest();
        BeanUtils.copyProperties(request, autoCageRequest);
        InvokeResult<Boolean> response =  deviceCageJsfService.querySortingInfo(autoCageRequest);
        if(response.getCode() != 200){
            jdCResponse.toFail("查询设备回传信息失败，请退出重试!");
            return jdCResponse;
        }

        jdCResponse.setData(response.getData());
        return jdCResponse;
    }

    @Override
    @JProfiler(jKey = "DMSWEB.DeviceCageGatewayServiceImpl.cage",jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
    public JdCResponse<Boolean> cage(AutoCageRequest request) {
        if (dmsConfigManager.getPropertyConfig().isCageSwitch()){
            //新逻辑
            return newCage(request);
        }else{
            //旧逻辑
            return oldCage(request);
        }
    }

    private JdCResponse<Boolean> newCage(AutoCageRequest request) {
        JdCResponse<Boolean> jdCResponse = new JdCResponse<Boolean>();
        jdCResponse.toSucceed();
        if(StringUtils.isEmpty(request.getCageCode())){
            jdCResponse.toFail("笼车编码为空，请退出重试!");
            return jdCResponse;
        }

        if(StringUtils.isEmpty(request.getCageBoxCode())){
            jdCResponse.toFail("笼车箱号为空，请退出重试!");
            return jdCResponse;
        }
        //通过包裹号查询板，并按板组笼发货
        Response<Board> boardResponse = boardCombinationService.getBoardByBoxCode(request.getSiteCode(), request.getBarcode());
        if(boardResponse.getCode() != 200){
            jdCResponse.toFail("未找到包裹"+request.getBarcode()+"绑定的板号，请退出重试!");
            return jdCResponse;
        }
        Board board = boardResponse.getData();
        //未完结板时，操作完结版
        if (board.getStatus() < BoardStatus.CLOSED.getIndex()){
            AutoBoardCompleteRequest domain = new AutoBoardCompleteRequest();
            domain.setBarcode(request.getBarcode());
            domain.setMachineCode(request.getMachineCode());
            domain.setSiteCode(request.getSiteCode());
            domain.setOperatorErp(request.getOperatorErp());
            com.jd.bluedragon.distribution.board.domain.Response<Void> autoBoardCompleteResponse = sortBoardJsfService.autoBoardComplete(domain);
            if(autoBoardCompleteResponse.getCode() != 200){
                jdCResponse.toFail("板["+board.getCode()+"]完结失败，请退出重试!");
                return jdCResponse;
            }
        }

        //查询板明细
        Response<Map<String, Date>> boardDetailReponse = groupBoardService.getBoardDetailByBoardCode(board.getCode());
        if(boardDetailReponse.getCode() != 200){
            jdCResponse.toFail("未找到板["+board.getCode()+"]的明细，请退出重试!");
            return jdCResponse;
        }
        //循环发送组板装笼消息
        for (Map.Entry<String,Date> entry:boardDetailReponse.getData().entrySet()){
            AutoCageMq mq = new AutoCageMq();
            mq.setBarcode(entry.getKey());
            mq.setOperatorTime(entry.getValue());
            mq.setCageBoxCode(request.getCageBoxCode());
            mq.setSiteCode(request.getSiteCode());
            mq.setMachineCode(request.getMachineCode());
            mq.setOperatorErp(request.getOperatorErp());
            mq.setBoardCode(board.getCode());
            mq.setDestinationId(board.getDestinationId());
            mq.setDestination(board.getDestination());
            mq.setOperatorData(request.getOperatorData());
            autoCageProducer.sendOnFailPersistent(mq.getCageBoxCode()+"-"+mq.getBarcode(), JsonHelper.toJson(mq));
        }
        jdCResponse.setData(true);
        return jdCResponse;
    }

    private JdCResponse<Boolean> oldCage(AutoCageRequest request) {
        JdCResponse<Boolean> jdCResponse = new JdCResponse<Boolean>();
        jdCResponse.toSucceed();
        if(StringUtils.isEmpty(request.getCageCode())){
            jdCResponse.toFail("笼车编码为空，请退出重试!");
            return jdCResponse;
        }

        if(StringUtils.isEmpty(request.getCageBoxCode())){
            jdCResponse.toFail("笼车箱号为空，请退出重试!");
            return jdCResponse;
        }
        JdCResponse<Boolean> booleanJdCResponse = this.querySortingInfo(request);
        if(booleanJdCResponse.isFail()){
            jdCResponse.toFail(booleanJdCResponse.getMessage());
            return jdCResponse;
        }
        com.jd.bluedragon.distribution.sdk.modules.cage.domain.AutoCageRequest autoCageRequest = new com.jd.bluedragon.distribution.sdk.modules.cage.domain.AutoCageRequest();
        BeanUtils.copyProperties(request, autoCageRequest);
        InvokeResult<Boolean> response =  deviceCageJsfService.cage(autoCageRequest);
        if(response.getCode() != 200){
            jdCResponse.toFail("装笼失败，请退出重试!");
            return jdCResponse;
        }

        if(!response.getData()){
            jdCResponse.toFail(response.getMessage());
            return jdCResponse;
        }

        jdCResponse.setData(response.getData());
        return jdCResponse;
    }

}
