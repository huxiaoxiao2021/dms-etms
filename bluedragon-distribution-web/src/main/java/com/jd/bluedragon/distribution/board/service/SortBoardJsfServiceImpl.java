package com.jd.bluedragon.distribution.board.service;

import com.google.common.collect.Lists;
import com.google.gson.reflect.TypeToken;
import com.jd.bluedragon.Constants;
import com.jd.bluedragon.common.dto.base.request.BaseReq;
import com.jd.bluedragon.common.dto.base.response.JdCResponse;
import com.jd.bluedragon.common.dto.board.request.CloseVirtualBoardPo;
import com.jd.bluedragon.common.dto.board.request.CombinationBoardRequest;
import com.jd.bluedragon.common.dto.board.response.BoardCheckDto;
import com.jd.bluedragon.common.dto.comboard.request.BoardReq;
import com.jd.bluedragon.common.dto.comboard.request.CancelBoardReq;
import com.jd.bluedragon.common.dto.comboard.request.ComboardDetailDto;
import com.jd.bluedragon.common.dto.comboard.request.ComboardScanReq;
import com.jd.bluedragon.common.dto.comboard.response.ComboardScanResp;
import com.jd.bluedragon.core.base.BaseMajorManager;
import com.jd.bluedragon.distribution.api.dto.BoardDto;
import com.jd.bluedragon.distribution.api.enums.OperatorTypeEnum;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.board.SortBoardJsfService;
import com.jd.bluedragon.distribution.board.domain.*;
import com.jd.bluedragon.distribution.busineCode.sendCode.service.SendCodeService;
import com.jd.bluedragon.distribution.businessCode.BusinessCodeAttributeKey;
import com.jd.bluedragon.distribution.businessCode.BusinessCodeFromSourceEnum;
import com.jd.bluedragon.distribution.jy.comboard.JyBizTaskComboardEntity;
import com.jd.bluedragon.distribution.jy.service.send.JyBizTaskComboardService;
import com.jd.bluedragon.distribution.jy.service.send.JyComBoardSendService;
import com.jd.bluedragon.distribution.loadAndUnload.exception.LoadIllegalException;
import com.jd.bluedragon.distribution.sdk.modules.board.BoardChuteJsfService;
import com.jd.bluedragon.distribution.sdk.modules.board.domain.BoardCompleteRequest;
import com.jd.bluedragon.distribution.send.domain.SendM;
import com.jd.bluedragon.distribution.send.service.DeliveryService;
import com.jd.bluedragon.distribution.send.service.SendMService;
import com.jd.bluedragon.distribution.send.utils.SendBizSourceEnum;
import com.jd.bluedragon.distribution.waybill.domain.OperatorData;
import com.jd.bluedragon.distribution.waybill.domain.WaybillStatus;
import com.jd.bluedragon.dms.utils.BusinessUtil;
import com.jd.bluedragon.dms.utils.WaybillUtil;
import com.jd.bluedragon.external.gateway.service.SortBoardGatewayService;
import com.jd.bluedragon.utils.ArraysUtil;
import com.jd.bluedragon.utils.DateHelper;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.ql.basic.dto.BaseStaffSiteOrgDto;
import com.jd.ql.dms.common.cache.CacheService;
import com.jd.ql.dms.common.domain.JdResponse;
import com.jd.bluedragon.distribution.board.domain.AddBoardRequest;
import com.jd.transboard.api.dto.AddBoardBox;
import com.jd.transboard.api.dto.Board;
import com.jd.transboard.api.service.GroupBoardService;
import com.jd.transboard.api.service.IVirtualBoardService;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.text.MessageFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;

import static com.jd.bluedragon.distribution.base.domain.InvokeResult.RESULT_SUCCESS_CODE;
import static com.jd.bluedragon.distribution.base.domain.InvokeResult.RESULT_SUCCESS_MESSAGE;
import static com.jd.bluedragon.utils.DateHelper.DATE_FORMAT_YYYYMMDDHHmmss2;

@Service("sortBoardJsfService")
public class SortBoardJsfServiceImpl implements SortBoardJsfService {
    private static final Logger log = LoggerFactory.getLogger(SortBoardJsfServiceImpl.class);

    private static String AUTO_COMPLETE_BOARD_KEY_PREFIX = "AUTO-COMPLETE-BOARD-KEY-";

    private static String AUTO_CREATE_BOARD_KEY_PREFIX = "AUTO-CREATE-BOARD-KEY-";
    @Autowired
    private SortBoardGatewayService sortBoardGatewayService;
    @Autowired
    private SendCodeService sendCodeService;
    @Autowired
    BoardCombinationService boardCombinationService;
    @Autowired
    VirtualBoardService virtualBoardService;
    @Autowired
    GroupBoardService groupBoardService;
    @Autowired
    DeliveryService deliveryService;
    @Autowired(required = false)
    BoardChuteJsfService boardChuteJsfService;
    @Autowired
    IVirtualBoardService iVirtualBoardService;
    @Resource
    private CacheService jimdbCacheService;

    @Autowired
    private SendMService sendMService;
    @Autowired
    private BaseMajorManager baseMajorManager;
    @Autowired
    private JyComBoardSendService jyComBoardSendService;
    @Autowired
    private JyBizTaskComboardService jyBizTaskComboardService;

    @Override
    @JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWEB, jKey = "DMS.WEB.SortBoardJsfServiceImpl.combinationBoardNew", mState = JProEnum.TP)
    public Response<BoardRequestDto> combinationBoardNew(Request request) {
        JdCResponse<BoardCheckDto> jdcResponse = new JdCResponse<>();
        // 查询包裹号是否组过板
        com.jd.transboard.api.dto.Response<Board> bordInfoResponse = boardCombinationService.getBoardByBoxCode(request.getCurrentOperate().getSiteCode(), request.getBoxOrPackageCode());
        if (bordInfoResponse!=null&&bordInfoResponse.getData()!=null){
            jdcResponse.setCode(JdResponse.CODE_FAIL);
            jdcResponse.setMessage( String.format(LoadIllegalException.PACKAGE_IS_SCAN_INTERCEPT_MESSAGE,
                    request.getBoxOrPackageCode(), bordInfoResponse.getData().getCode()));
        }else{
            String str = com.jd.bluedragon.distribution.api.utils.JsonHelper.toJson(request);
            CombinationBoardRequest c = JsonHelper.fromJson(str, CombinationBoardRequest.class);
            c.setBizSource(BizSourceEnum.SORTING_MACHINE.getValue());
            jdcResponse = sortBoardGatewayService.combinationBoardNew(c);
        }
        return JsonHelper.fromJsonUseGson(JsonHelper.toJson(jdcResponse),new TypeToken<Response<BoardRequestDto>>(){}.getType());
    }

    /**
     * 创建板
     * @param request
     * @return
     */
    @Override
    @JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWEB, jKey = "DMS.WEB.SortBoardJsfServiceImpl.createBoard", mState = JProEnum.TP)
    public InvokeResult<List<com.jd.bluedragon.distribution.board.domain.Board>> createBoard(AddBoardRequest request){
        //参数校验
        InvokeResult<List<com.jd.bluedragon.distribution.board.domain.Board>> response = checkParma4CreateBoard(request);
        if(!response.codeSuccess()){
            return response;
        }
        //创建板
        com.jd.transboard.api.dto.AddBoardRequest addBoardRequest = new com.jd.transboard.api.dto.AddBoardRequest();
        BeanUtils.copyProperties(request, addBoardRequest);
        addBoardRequest.setOperatorName(request.getUserName());
        addBoardRequest.setOperatorErp(request.getUserErp());
        InvokeResult<List<BoardDto>> boardDtos = boardCombinationService.createBoard(addBoardRequest);

        //返回值转换
        BeanUtils.copyProperties(boardDtos, response);
        if(CollectionUtils.isNotEmpty(boardDtos.getData())){
            List<com.jd.bluedragon.distribution.board.domain.Board> boards = new ArrayList<>(boardDtos.getData().size());
            for(BoardDto dto : boardDtos.getData()){
                String sendCode = genSendCode(request);
                com.jd.bluedragon.distribution.board.domain.Board board = initBoard(dto);
                board.setSendCode(sendCode);
                boards.add(board);
            }
            response.setData(boards);
        }

        return response;
    }

    private String genSendCode(AddBoardRequest request) {
        Map<BusinessCodeAttributeKey.SendCodeAttributeKeyEnum, String> attributeKeyEnumObjectMap = new HashMap<>();
        attributeKeyEnumObjectMap.put(BusinessCodeAttributeKey.SendCodeAttributeKeyEnum.from_site_code,
                String.valueOf(request.getSiteCode()));
        attributeKeyEnumObjectMap.put(BusinessCodeAttributeKey.SendCodeAttributeKeyEnum.to_site_code,
                String.valueOf(request.getDestinationId()));
        String sendCode = sendCodeService
                .createSendCode(attributeKeyEnumObjectMap, BusinessCodeFromSourceEnum.DMS_AUTOMATIC_WORKER_SYS, request.getUserErp());
        return sendCode;
    }





    private InvokeResult<List<com.jd.bluedragon.distribution.board.domain.Board>> checkParma4CreateBoard(AddBoardRequest request){
        InvokeResult<List<com.jd.bluedragon.distribution.board.domain.Board>> response = new InvokeResult<>();
        if(StringUtils.isBlank(request.getDestination()) || request.getDestinationId() == null){
           response.parameterError("板目的场地名称或板目的场地id不能为空");
           return response;
        }

        if(StringUtils.isBlank(request.getSiteName()) || request.getSiteCode() == null){
            response.parameterError("板操作场地名称或板操作场地id不能为空");
            return response;
        }

        if(StringUtils.isBlank(request.getUserErp())){
            response.parameterError("操作人erp不能为空");
            return response;
        }
        return response;
    }


    private com.jd.bluedragon.distribution.board.domain.Board initBoard(BoardDto dto){
        com.jd.bluedragon.distribution.board.domain.Board board = new com.jd.bluedragon.distribution.board.domain.Board();
        board.setDestination(dto.getDestination());
        board.setDestinationId(dto.getDestinationId());
        board.setStatus(dto.getStatus());
        board.setCode(dto.getCode());
        board.setCreateTime(DateHelper.parseDateTime(dto.getDate() + " " + dto.getTime()));
        return board;
    }

    @Override
    public Response<BoardSendDto> addToBoard(BindBoardRequest request) {
        Response<BoardSendDto> response = new Response<>();
        try {
            //调板服务组板
            AddBoardBox addBoardBox = initAddBoardBox(request);
            com.jd.transboard.api.dto.Response<Integer>  bindResult = groupBoardService.addBoxToBoard(addBoardBox);
            log.info("分拣机组板调参数:{}，返回值:{}", JsonHelper.toJson(request), JsonHelper.toJson(bindResult));
            if(bindResult.getCode() != 200){
                response.toFail(MessageFormat.format("调板服务组板接口失败code:{0}，message:{1}", bindResult.getCode(),
                        bindResult.getMesseage()));
                log.warn("调板服务组板接口失败code:{}，message:{},请求参数:{}", bindResult.getCode(), bindResult.getMesseage(),
                        JsonHelper.toJson(addBoardBox));
                return response;
            }

            //发送全程跟踪
            com.jd.bluedragon.common.dto.base.request.OperatorInfo operatorInfo = initOperatorInfo(request);

            virtualBoardService.sendWaybillTrace(request.getBarcode(), operatorInfo, request.getBoard().getCode(),
                    request.getBoard().getDestination(), WaybillStatus.WAYBILL_TRACK_BOARD_COMBINATION,
                    request.getBizSource());
            response.toSucceed();
            return response;
        }catch (Exception e){
            log.error("自动化组板操作异常，组板信息：{}", JsonHelper.toJson(request));
            response.toFail("自动化组板操作异常异常");
            return response;
        }

    }

    @Override
    public Response<List<String>> calcBoard(AutoBoardCompleteRequest request) {
        Response<List<String>> response = new Response<List<String>>();
        BoardCompleteRequest boardCompleteRequest = new BoardCompleteRequest();
        BeanUtils.copyProperties(request, boardCompleteRequest);
        com.jd.bluedragon.distribution.sdk.common.domain.InvokeResult<List<String>> baseResult =
                boardChuteJsfService.calcBoards(boardCompleteRequest);
        log.info("计算格口获取板号信息,request:"+ JsonHelper.toJson(request)+",result:"+ JsonHelper.toJson(baseResult));
        if (baseResult.getCode()!=200){
            response.toFail(StringUtils.isEmpty(baseResult.getMessage())?"查询组板包裹(箱号)信息失败，请退出重试!":baseResult.getMessage());
            return response;
        }
        List<String> boardCode = baseResult.getData();
        if (CollectionUtils.isEmpty(boardCode)){
            response.toFail("查询组板包裹(箱号)信息失败，请退出重试!");
            return response;
        }
        response.toSucceed();
        response.setData(boardCode);
        return response;
    }

    @Override
    public Response<BoardSendDto> sortMachineComboard(BindBoardRequest request) {
        Response<BoardSendDto> response = new Response<>();
        try {
            ComboardScanReq req = createComboardScanReq(request);
            InvokeResult<ComboardScanResp> result = jyComBoardSendService.sortMachineComboard(req);
            log.info("分拣机组板发货调参数:{}，返回值:{}", JsonHelper.toJson(req), JsonHelper.toJson(result));
            if(result.getCode() != 200){
                response.toFail(MessageFormat.format("调板服务组板发货接口失败code:{0}，message:{1}", result.getCode(),
                        result.getMessage()));
                log.warn("调板服务组板接口失败code:{}，message:{},请求参数:{}", result.getCode(), result.getMessage(),
                        JsonHelper.toJson(req));
                return response;
            }
            BoardSendDto boardSendDto = new BoardSendDto();
            boardSendDto.setBoardCode(result.getData().getBoardCode());
            boardSendDto.setBoardSendEnum(result.getData().getScanDetailCount()+"");
            response.setData(boardSendDto);
            response.toSucceed();
            return response;
        }catch (Exception e){
            log.error("自动化组板操作发货异常，组板信息：{}", JsonHelper.toJson(request),e);
            response.toFail("自动化组板发货操作异常异常");
            return response;
        }
    }

    private void assembleBaseReq(BaseReq req,BindBoardRequest request) {
        com.jd.bluedragon.common.dto.base.request.User user = new com.jd.bluedragon.common.dto.base.request.User();
        if(request.getOperatorInfo().getUserCode() != null) {
        	user.setUserCode(request.getOperatorInfo().getUserCode());
        }
        user.setUserErp(request.getOperatorInfo().getUserErp());
        user.setUserName(request.getOperatorInfo().getUserName());
        req.setUser(user);

        com.jd.bluedragon.common.dto.base.request.CurrentOperate currentOperate = new com.jd.bluedragon.common.dto.base.request.CurrentOperate();
        if(request.getOperatorInfo().getSiteCode() != null) {
        	currentOperate.setSiteCode(request.getOperatorInfo().getSiteCode());
        }
        currentOperate.setSiteName(request.getOperatorInfo().getSiteName());
        currentOperate.setOperateTime(request.getOperatorInfo().getOperateTime());
        currentOperate.setOperatorTypeCode(OperatorTypeEnum.AUTO_MACHINE.getCode());
        currentOperate.setOperatorId(request.getMachineCode());
        req.setCurrentOperate(currentOperate);
    }

    private ComboardScanReq createComboardScanReq(BindBoardRequest request) {
        ComboardScanReq req = new ComboardScanReq();
        assembleBaseReq(req,request);
        req.setBarCode(request.getBarcode());
        req.setBizSource(BusinessCodeFromSourceEnum.DMS_AUTOMATIC_WORKER_SYS.name());
        req.setBoardCode(request.getBoard().getCode());
        req.setEndSiteId(request.getBoard().getDestinationId());
        req.setDestinationId(request.getBoard().getDestinationId());
        req.setEndSiteName(request.getBoard().getDestination());
        req.setSendCode(request.getBoard().getSendCode());
        return req;
    }

    private CancelBoardReq createCancelBoardReq(BindBoardRequest request) {
        CancelBoardReq req = new CancelBoardReq();
        req.setEndSiteName(request.getBoard().getDestination());
        req.setBoardCode(request.getBoard().getCode());
        assembleBaseReq(req,request);
        req.setBizSource(BusinessCodeFromSourceEnum.DMS_AUTOMATIC_WORKER_SYS.name());
        List<ComboardDetailDto>  cancelList = Lists.newArrayList();
        ComboardDetailDto dto = new ComboardDetailDto();
        dto.setBarCode(request.getBarcode());
        cancelList.add(dto);
        req.setCancelList(cancelList);

        return req;
    }

    @Override
    public Response<Void> cancelSortMachineComboard(BindBoardRequest request) {
        Response<Void> response = new Response<>();
        try {
            CancelBoardReq req = createCancelBoardReq(request);
            InvokeResult<Void> result = jyComBoardSendService.cancelSortMachineComboard(req);
            log.info("分拣机取消组板调参数:{}，返回值:{}", JsonHelper.toJson(req), JsonHelper.toJson(result));
            if(result.getCode() != 200){
                response.toFail(MessageFormat.format("调板服务组板发货接口失败code:{0}，message:{1}", result.getCode(),
                        result.getMessage()));
                log.warn("调分拣机取消组板接口失败code:{}，message:{},请求参数:{}", result.getCode(), result.getMessage(),
                        JsonHelper.toJson(req));
                return response;
            }
            response.toSucceed();
            return response;
        }catch (Exception e){
            log.error("自动化组板操作发货异常，组板信息：{}", JsonHelper.toJson(request),e);
            response.toFail("自动化组板发货操作异常异常");
            return response;
        }
    }


    private com.jd.bluedragon.common.dto.base.request.OperatorInfo initOperatorInfo(BindBoardRequest request){
    	if(request == null) {
    		return null;
    	}
    	OperatorInfo operatorInfo = request.getOperatorInfo();
        com.jd.bluedragon.common.dto.base.request.OperatorInfo operator = new com.jd.bluedragon.common.dto.base.request.OperatorInfo();
        if(operatorInfo != null) {
        operator.setOperateTime(operatorInfo.getOperateTime());
        operator.setSiteCode(operatorInfo.getSiteCode());
        operator.setSiteName(operatorInfo.getSiteName());
        operator.setUserCode(operatorInfo.getUserCode());
        operator.setUserName(operatorInfo.getUserName());
            operator.setUserErp(operatorInfo.getUserErp());
        }
        operator.setOperatorTypeCode(OperatorTypeEnum.AUTO_MACHINE.getCode());
        operator.setOperatorId(request.getMachineCode());
        return operator;
    }

    @Override
    @JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWEB, jKey = "DMS.WEB.SortBoardJsfServiceImpl.autoBoardComplete", mState = JProEnum.TP)
    public Response<Void> autoBoardComplete(AutoBoardCompleteRequest request) {
        //参数校验
        Response<Void> response = checkParam4AutoBoardComplete(request);
        if(!response.isSucceed()){
            return response;
        }
        String key = AUTO_COMPLETE_BOARD_KEY_PREFIX + request.getBarcode() + request.getMachineCode() + request.getOperatorErp();
        try {
            // 同一操作人及目的地加锁，解决并发问题
            boolean isExistHandling = jimdbCacheService.setNx(key, 1 + "", 60, TimeUnit.SECONDS);
            if(!isExistHandling){
                response.toFail("正在处理中,请勿重复提交");
                return response;
            }
        } catch (Exception e) {
            response.toFail("正在处理中,请勿重复提交");
            return response;
        }
        try {
            //调用自动化服务
            BoardCompleteRequest boardCompleteRequest = new BoardCompleteRequest();
            BeanUtils.copyProperties(request, boardCompleteRequest);
            com.jd.bluedragon.distribution.sdk.common.domain.InvokeResult<List<String>> baseResult =
                    boardChuteJsfService.completeBoard(boardCompleteRequest);
            if(!baseResult.isSuccess()){
                log.warn("调自动化服务修改板状态失败，请求参数：{},返回值:{}", JsonHelper.toJson(boardCompleteRequest),
                        JsonHelper.toJson(baseResult));
                response.toFail(baseResult.getMessage());
                return response;
            }

            List<String> boardCodes = baseResult.getData();
            if(CollectionUtils.isNotEmpty(boardCodes)){
                com.jd.bluedragon.common.dto.base.request.OperatorInfo operatorInfo =
                        initOperatorInfo(request.getOperatorErp(), request.getSiteCode());
                for (String code : boardCodes){
//                    BoardReq req = createFinishBoardReq(operatorInfo, code);
//                    jyComBoardSendService.finishBoard(req);
                    //调板服务关闭板状态
                    CloseVirtualBoardPo po = initCloseVirtualBoardPo(code, operatorInfo);
                    JdCResponse<Void> jdCResponse = virtualBoardService.closeBoard(po);
                    response.setCode(jdCResponse.getCode());
                    response.setMessage(jdCResponse.getMessage());
                }
            }

            return response;
        }catch (Exception e) {
            response.toFail("请求异常，请稍后重试");
            log.error("pda操作自动化组板完成异常,请求参数:{}", JsonHelper.toJson(request), e);
            return response;
        } finally {
            jimdbCacheService.del(key);
        }

    }

    private BoardReq createFinishBoardReq(com.jd.bluedragon.common.dto.base.request.OperatorInfo operatorInfo, String code) {
        BoardReq req = new BoardReq();
        req.setBizSource(BusinessCodeFromSourceEnum.DMS_AUTOMATIC_WORKER_SYS.name());
        req.setBoardCode(code);
        com.jd.bluedragon.common.dto.base.request.CurrentOperate currentOperate = new com.jd.bluedragon.common.dto.base.request.CurrentOperate();
        currentOperate.setOperateTime(new Date());
        currentOperate.setSiteCode(operatorInfo.getSiteCode());
        req.setCurrentOperate(currentOperate);

        com.jd.bluedragon.common.dto.base.request.User user = new com.jd.bluedragon.common.dto.base.request.User();
        user.setUserErp(operatorInfo.getUserErp());
        user.setUserName(operatorInfo.getUserName());
        req.setUser(user);
        return req;
    }


    private Response<Void> checkParam4AutoBoardComplete(AutoBoardCompleteRequest request){
        Response<Void> response = new Response<>();

        if(StringUtils.isBlank(request.getMachineCode())){
            response.toFail("分拣机编码不能为空");
            return response;
        }
        if(StringUtils.isBlank(request.getBarcode())){
            response.toFail("条码不能为空，请扫描落格的包裹号或箱号");
            return response;
        }
        if(StringUtils.isBlank(request.getOperatorErp())){
            response.toFail("操作人erp为空，请退出重新登陆");
            return response;
        }
        if(request.getSiteCode() == null){
            response.toFail("操作人所属场地为空，请退出重新登陆");
            return response;
        }
        response.toSucceed();
        return response;
    }

    private CloseVirtualBoardPo initCloseVirtualBoardPo(String boardCode, com.jd.bluedragon.common.dto.base.request.OperatorInfo operatorInfo ){
        CloseVirtualBoardPo po = new CloseVirtualBoardPo();
        po.setBoardCode(boardCode);
        po.setBizSource(BizSourceEnum.SORTING_MACHINE.getValue());
        po.setOperatorInfo(operatorInfo);
        return po;
    }
    private com.jd.bluedragon.common.dto.base.request.OperatorInfo initOperatorInfo(String userErp, Integer siteCode){
        com.jd.bluedragon.common.dto.base.request.OperatorInfo operatorInfo = new com.jd.bluedragon.common.dto.base.request.OperatorInfo();
        BaseStaffSiteOrgDto baseStaffSiteOrgDto = baseMajorManager.getBaseStaffByErpNoCache(userErp);
        if(baseStaffSiteOrgDto != null){
            operatorInfo.setUserName(baseStaffSiteOrgDto.getStaffName());
            operatorInfo.setSiteName(baseStaffSiteOrgDto.getSiteName());
        }
        operatorInfo.setUserErp(userErp);
        operatorInfo.setSiteCode(siteCode);
        return operatorInfo;
    }

    private AddBoardBox initAddBoardBox(BindBoardRequest request){
        com.jd.bluedragon.distribution.board.domain.Board board = request.getBoard();
        OperatorInfo operatorInfo = request.getOperatorInfo();
        AddBoardBox addBoardBox = new AddBoardBox();
        addBoardBox.setBoardCode(board.getCode());
        addBoardBox.setBoxCode(request.getBarcode());
        addBoardBox.setSiteCode(operatorInfo.getSiteCode());
        addBoardBox.setSiteName(operatorInfo.getSiteName());
        addBoardBox.setSiteType(operatorInfo.getSiteType());
        addBoardBox.setOperatorErp(operatorInfo.getUserErp());
        addBoardBox.setOperatorName(operatorInfo.getUserName());
        addBoardBox.setBizSource(request.getBizSource());
        return addBoardBox;
    }

    /**
     * 参数检查
     * @param request
     * @return
     */
    private Response<String> checkParma4BindBoard(BindBoardRequest request){
        Response<String> response = new Response<>();
        com.jd.bluedragon.distribution.board.domain.Board board = request.getBoard();
        OperatorInfo operatorInfo = request.getOperatorInfo();
        if(StringUtils.isBlank(request.getBarcode())){
            response.toFail("条码不能为空");
            return response;
        }

        if(!BusinessUtil.isBoxcode(request.getBarcode()) && !WaybillUtil.isPackageCode(request.getBarcode())){
            response.toFail("条码只能是箱号或包裹号");
            return response;
        }

        if(StringUtils.isBlank(board.getCode())){
            response.toFail("板编码不能为空");
            return response;
        }

        if(board.getDestinationId() == null){
            response.toFail("板目的场地id不能为空");
            return response;
        }

        if(StringUtils.isBlank(operatorInfo.getSiteName()) || operatorInfo.getSiteCode() == null){
            response.toFail("板操作场地名称或板操作场地id不能为空");
            return response;
        }

        if(operatorInfo.getOperateTime() == null){
            response.toFail("操作时间不能为空");
            return response;
        }

        response.toSucceed();
        return response;
    }


    private Response<String> checkBarcodeSendStatus(BindBoardRequest request){
        Response<String> response = new Response<>();
        //查询箱子发货记录
        /* 不直接使用domain的原因，SELECT语句有[test="createUserId!=null"]等其它 */
        SendM queryPara = new SendM();
        queryPara.setBoardCode(request.getBoard().getCode());
        queryPara.setCreateSiteCode(request.getOperatorInfo().getSiteCode());
        List<SendM> sendMList = sendMService.findByParams(queryPara);
        if (CollectionUtils.isEmpty(sendMList)) {
            response.toSucceed();
            return response;
        }
        boolean notSend = true;
        for(SendM m : sendMList){
            if(m.getSendmStatus() != null && m.getSendmStatus() == 1){
                notSend = false;
                break;
            }
        }
        //未发货
        if(notSend){
            response.toSucceed();
            return response;
        }

        SendM sendM = sendMList.get(0);

        //发货时间
        Date sendTime = sendM.getOperateTime();
        //操作时间
        Date operateTime = request.getOperatorInfo().getOperateTime();

        Long compareResult = sendTime.getTime() - operateTime.getTime();

        //发货时间晚于 操作时间 补发货
        if(compareResult >= 0){
            SendM domain= toSendMDomain(request, sendM.getSendCode());
            deliveryService.packageSend(SendBizSourceEnum.SORT_MACHINE_SEND, domain);
            response.toSucceed();
            return response;
        }

        //发货时间早于操作时间 需要新生成板

        com.jd.bluedragon.distribution.board.domain.Board board = request.getBoard();
        OperatorInfo operatorInfo = request.getOperatorInfo();
        //加锁防止并发
        String key = AUTO_CREATE_BOARD_KEY_PREFIX + board.getDestinationId() + operatorInfo.getUserErp();
        try {
            // 同一操作人及目的地加锁，解决并发问题
            boolean isExistHandling = jimdbCacheService.setNx(key, 1 + "", 60, TimeUnit.SECONDS);
            if(!isExistHandling){
                response.toFail("当前目的地:" + board.getDestinationId() + "的板号正在生成");
                return response;
            }
        } catch (Exception e) {
            response.toFail("当前目的地:" + board.getDestinationId() + "的板号正在生成");
            return response;
        }
        try {
            com.jd.transboard.api.dto.AddBoardRequest addBoardRequest = new com.jd.transboard.api.dto.AddBoardRequest();
            addBoardRequest.setBoardCount(1);
            addBoardRequest.setBizSource(BizSourceEnum.SORTING_MACHINE.getValue());
            addBoardRequest.setDestination(board.getDestination());
            addBoardRequest.setDestinationId(board.getDestinationId());
            addBoardRequest.setOperatorErp(operatorInfo.getUserErp());
            addBoardRequest.setOperatorName(operatorInfo.getUserName());
            addBoardRequest.setSiteCode(operatorInfo.getSiteCode());
            addBoardRequest.setSiteName(operatorInfo.getSiteName());
            InvokeResult<List<BoardDto>> boardDtos = boardCombinationService.createBoard(addBoardRequest);
            if(!boardDtos.codeSuccess() || CollectionUtils.isEmpty(boardDtos.getData())){
                log.warn("板已发货，而且发货时间早于包裹或箱的落格时间，新生成板标失败，请求参数:{},返回参数{}",
                        JsonHelper.toJson(addBoardRequest), JsonHelper.toJson(boardDtos));
                response.toFail("板已发货，而且发货时间早于包裹或箱的落格时间，新生成板标失败");
                return response;
            }
            response.toSucceed();
            response.setData(boardDtos.getData().get(0).getCode());
            return response;
        }finally {
            jimdbCacheService.del(key);
        }


    }

    /**
     *
     * @param request
     * @return ture : 已发货 false 未发货
     */
    @Override
    public List<BoardSendDto> checkAndReplenishDelivery(CheckBoardStatusDto request){
        List<BoardSendDto>  boardSendDtos = new ArrayList<>();
        if(CollectionUtils.isEmpty(request.getBoardCodes())){
            log.warn("自动化组板查板发货状态，传入的板号为空,包裹{}", request.getBarcode());
            return boardSendDtos;
        }
        List<String>  boardCodes = request.getBoardCodes();
        boolean hasReplenish = false;
        for(String boardCode : boardCodes){
            BoardSendDto dto = new BoardSendDto();
            dto.setBoardCode(boardCode);
            SendM sendM = sendMService.selectSendByBoardCode(request.getSiteCode(),
                    boardCode, 1);
            if(sendM == null){
                dto.setBoardSendEnum(BoardSendEnum.NOT_SEND.toString());
                boardSendDtos.add(dto);
                continue;
            }

            //发货时间
            Date sendTime = sendM.getOperateTime();
            dto.setSendTime(sendTime);
            //操作时间
            Date operateTime = request.getOperateTime();
            long compareResult = sendTime.getTime() - operateTime.getTime();
            //发货时间晚于 操作时间 补发货
            if(compareResult >= 0){
                SendM domain= convertToSendM(request, sendM);
                //只需补一次发货
                if(!hasReplenish){
                    hasReplenish = true;
                    deliveryService.packageSend(SendBizSourceEnum.SORT_MACHINE_SEND, domain);
                    log.info("自动化组板板的发货状态检查，板已经发货，包裹:{}的落格时间:{}在板:{}的发货时间:{}之前，包裹补发货", request.getBarcode(),
                            DateHelper.formatDate(operateTime, DATE_FORMAT_YYYYMMDDHHmmss2),boardCode,
                            DateHelper.formatDate(sendTime, DATE_FORMAT_YYYYMMDDHHmmss2));
                }
                dto.setBoardSendEnum(BoardSendEnum.SEND_AFTER_SORTING.toString());
            }else {
                log.info("自动化组板板的发货状态检查，板已经发货，包裹:{}的落格时间:{}在板:{}的发货时间:{}之后不再组板", request.getBarcode(),
                        DateHelper.formatDate(operateTime, DATE_FORMAT_YYYYMMDDHHmmss2), boardCode,
                        DateHelper.formatDate(sendTime, DATE_FORMAT_YYYYMMDDHHmmss2));
                dto.setBoardSendEnum(BoardSendEnum.SEND_BEFORE_SORTING.toString());
            }
            boardSendDtos.add(dto);
        }




        return boardSendDtos;

    }


    /**
     * 请求拼装SendM发货对象
     * @param request
     * @return
     */
    private SendM convertToSendM(CheckBoardStatusDto request, SendM sendM) {

        SendM domain = new SendM();
        domain.setReceiveSiteCode(sendM.getReceiveSiteCode());
        domain.setSendCode(sendM.getSendCode());
        domain.setCreateSiteCode(request.getSiteCode());
        domain.setCreateUser(request.getUserName());
        domain.setCreateUserCode(request.getUserCode());
        domain.setSendType(sendM.getSendType());
        domain.setBizSource(SendBizSourceEnum.BOARD_SEND.getCode());
        domain.setBoxCode(request.getBarcode());
        domain.setYn(1);
        domain.setCreateTime(new Date());
        domain.setOperateTime(sendM.getOperateTime());
        domain.setBoardCode(sendM.getBoardCode());

        return domain;
    }


    /**
     * 请求拼装SendM发货对象
     * @param request
     * @return
     */
    private SendM toSendMDomain(BindBoardRequest request, String sendCode) {
        com.jd.bluedragon.distribution.board.domain.Board board = request.getBoard();
        OperatorInfo operatorInfo = request.getOperatorInfo();

        SendM domain = new SendM();
        domain.setReceiveSiteCode(board.getDestinationId());
        domain.setSendCode(sendCode);
        domain.setCreateSiteCode(operatorInfo.getSiteCode());
        domain.setCreateUser(operatorInfo.getUserName());
        domain.setCreateUserCode(operatorInfo.getUserCode());
        domain.setSendType(Constants.BUSSINESS_TYPE_POSITIVE);
        domain.setBizSource(SendBizSourceEnum.SORT_MACHINE_SEND.getCode());
        domain.setBoxCode(request.getBarcode());
        domain.setYn(1);
        domain.setCreateTime(DateHelper.add(operatorInfo.getOperateTime(), Calendar.SECOND, 5));
        domain.setOperateTime(DateHelper.add(operatorInfo.getOperateTime(), Calendar.SECOND, 5));
        domain.setOperatorTypeCode(OperatorTypeEnum.AUTO_MACHINE.getCode());
        domain.setOperatorId(request.getMachineCode());
        return domain;
    }



    public BoardChuteJsfService getBoardChuteJsfService() {
        return boardChuteJsfService;
    }
}
