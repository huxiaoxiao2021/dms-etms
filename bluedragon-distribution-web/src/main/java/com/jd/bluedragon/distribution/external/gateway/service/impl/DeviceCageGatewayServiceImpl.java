package com.jd.bluedragon.distribution.external.gateway.service.impl;

import com.jd.bd.dms.automatic.sdk.common.dto.BaseDmsAutoJsfResponse;
import com.jd.bd.dms.automatic.sdk.modules.device.DeviceConfigInfoJsfService;
import com.jd.bd.dms.automatic.sdk.modules.device.dto.DeviceConfigDto;
import com.jd.bluedragon.Constants;
import com.jd.bluedragon.common.dto.base.request.OperatorData;
import com.jd.bluedragon.common.dto.base.response.JdCResponse;
import com.jd.bluedragon.common.dto.cage.request.AutoCageRequest;
import com.jd.bluedragon.common.dto.comboard.request.ComboardScanReq;
import com.jd.bluedragon.configuration.DmsConfigManager;
import com.jd.bluedragon.core.jmq.producer.DefaultJMQProducer;
import com.jd.bluedragon.distribution.api.utils.JsonHelper;
import com.jd.bluedragon.distribution.autoCage.domain.AutoCageMq;
import com.jd.bluedragon.distribution.autoCage.domain.BaseAutoCageMq;
import com.jd.bluedragon.distribution.board.SortBoardJsfService;
import com.jd.bluedragon.distribution.board.domain.AutoBoardCompleteRequest;
import com.jd.bluedragon.distribution.board.service.BoardCombinationService;
import com.jd.bluedragon.distribution.busineCode.sendCode.service.SendCodeService;
import com.jd.bluedragon.distribution.businessCode.BusinessCodeAttributeKey;
import com.jd.bluedragon.distribution.businessCode.BusinessCodeFromSourceEnum;
import com.jd.bluedragon.distribution.cage.DmsDeviceCageJsfService;
import com.jd.bluedragon.distribution.cage.request.CollectPackageReq;
import com.jd.bluedragon.distribution.cage.response.CollectPackageResp;
import com.jd.bluedragon.distribution.jy.comboard.JyBizTaskComboardEntity;
import com.jd.bluedragon.distribution.jy.enums.ComboardStatusEnum;
import com.jd.bluedragon.distribution.jy.service.send.JyBizTaskComboardService;
import com.jd.bluedragon.distribution.jy.service.send.JyComBoardSendService;
import com.jd.bluedragon.distribution.sdk.common.domain.InvokeResult;
import com.jd.bluedragon.distribution.sdk.modules.cage.DeviceCageJsfService;
import com.jd.bluedragon.external.gateway.service.DeviceCageGatewayService;
import com.jd.bluedragon.utils.DateHelper;
import com.jd.bluedragon.utils.StringHelper;
import com.jd.coo.sa.sequence.JimdbSequenceGen;
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
import java.util.HashMap;
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
    @Autowired
    JyBizTaskComboardService jyBizTaskComboardService;
    @Autowired
    private DmsDeviceCageJsfService dmsDeviceCageJsfService;
    @Autowired
    private SendCodeService sendCodeService;
    @Autowired
    @Qualifier("redisJySendBizIdSequenceGen")
    private JimdbSequenceGen redisJyBizIdSequenceGen;

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
        JdCResponse<Boolean> jdcResponse = new JdCResponse<Boolean>();
        jdcResponse.toSucceed();
        if(StringUtils.isEmpty(request.getCageCode())){
            jdcResponse.toFail("笼车编码为空，请退出重试!");
            return jdcResponse;
        }

        if(StringUtils.isEmpty(request.getCageBoxCode())){
            jdcResponse.toFail("笼车箱号为空，请退出重试!");
            return jdcResponse;
        }
        //通过包裹号查询板，并按板组笼发货
        Response<Board> boardResponse = boardCombinationService.getBoardByBoxCode(request.getSiteCode(), request.getBarcode());
        if(boardResponse.getCode() != 200){
            jdcResponse.toFail("未找到包裹"+request.getBarcode()+"绑定的板号，请退出重试!");
            return jdcResponse;
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
                jdcResponse.toFail(autoBoardCompleteResponse.getMessage());
                return jdcResponse;
            }
        }

        //查询板明细
        Response<Map<String, Date>> boardDetailReponse = groupBoardService.getBoardDetailByBoardCode(board.getCode());
        if(boardDetailReponse.getCode() != 200){
            jdcResponse.toFail("未找到板["+board.getCode()+"]的明细，请退出重试!");
            return jdcResponse;
        }
        //获取设备操作人
        BaseAutoCageMq baseMq = createBaseAutoCageMq(request, board);
        //循环发送组板装笼消息
        for (Map.Entry<String,Date> entry:boardDetailReponse.getData().entrySet()){
            AutoCageMq mq = (AutoCageMq) baseMq;
            mq.setBarcode(entry.getKey());
            mq.setDeviceOperatorTime(entry.getValue());
            singleCage(mq);
        }
        autoCageProducer.sendOnFailPersistent(baseMq.getCageBoxCode(), JsonHelper.toJson(baseMq));
        jdcResponse.setData(true);
        return jdcResponse;
    }

    private BaseAutoCageMq createBaseAutoCageMq(AutoCageRequest request, Board board) {
        BaseAutoCageMq mq = new BaseAutoCageMq();
        DeviceConfigDto machine = deviceConfigInfoJsfService.findOneDeviceConfigByMachineCode(request.getMachineCode());
        String operator = machine.getOperatorErp();
        String operatorName = machine.getOperatorName();
        mq.setDeviceOperatorErp(operator);
        mq.setDeviceOperatorName(operatorName);
        mq.setCageBoxCode(request.getCageBoxCode());
        mq.setSiteCode(request.getSiteCode());
        mq.setMachineCode(request.getMachineCode());
        mq.setOperatorErp(request.getOperatorErp());
        mq.setOperatorName(request.getOperatorName());
        mq.setBoardCode(board.getCode());
        mq.setDestinationId(board.getDestinationId());
        mq.setDestination(board.getDestination());
        mq.setOperatorData(request.getOperatorData());
        mq.setOperatorTime(new Date());
        return mq;
    }

    private void singleCage(AutoCageMq mq) {

        //装笼
        CollectPackageReq req = createCollectPackageReq(mq);
        log.info("装笼参数："+ com.jd.bluedragon.utils.JsonHelper.toJson(req));
        com.jd.bluedragon.distribution.base.domain.InvokeResult<CollectPackageResp> cageRespose = dmsDeviceCageJsfService.cage(req);
        if(com.jd.bluedragon.distribution.base.domain.InvokeResult.RESULT_SUCCESS_CODE != cageRespose.getCode()){
            log.error("装笼失败，参数："+ com.jd.bluedragon.utils.JsonHelper.toJson(req)+ "返回值："+ com.jd.bluedragon.utils.JsonHelper.toJson(cageRespose));
            throw new RuntimeException("AutoCageConsumer 处理失败,jmq自动重试!");
        }
    }

    private static CollectPackageReq createCollectPackageReq(AutoCageMq mq) {
        CollectPackageReq req = new CollectPackageReq();
        req.setBoxCode(mq.getCageBoxCode());
        req.setBarCode(mq.getBarcode());
        req.setSiteCode(Long.valueOf(mq.getSiteCode()));
        req.setUserErp(mq.getOperatorErp());
        req.setUserName(mq.getOperatorName());
        req.setOperateTime(mq.getDeviceOperatorTime());
        OperatorData operatorData = mq.getOperatorData();
        req.setOperatorData(com.jd.bluedragon.utils.JsonHelper.fromJson(com.jd.bluedragon.utils.JsonHelper.toJson(operatorData),com.jd.bluedragon.distribution.api.domain.OperatorData.class));
        return req;
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
