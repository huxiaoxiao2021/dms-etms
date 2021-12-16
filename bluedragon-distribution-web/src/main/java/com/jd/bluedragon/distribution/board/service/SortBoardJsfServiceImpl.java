package com.jd.bluedragon.distribution.board.service;

import com.google.gson.reflect.TypeToken;
import com.jd.bluedragon.Constants;
import com.jd.bluedragon.common.dto.base.response.JdCResponse;
import com.jd.bluedragon.common.dto.board.request.CloseVirtualBoardPo;
import com.jd.bluedragon.common.dto.board.request.CombinationBoardRequest;
import com.jd.bluedragon.common.dto.board.response.BoardCheckDto;
import com.jd.bluedragon.common.utils.CacheKeyConstants;
import com.jd.bluedragon.distribution.api.dto.BoardDto;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.board.SortBoardJsfService;
import com.jd.bluedragon.distribution.board.domain.*;
import com.jd.bluedragon.distribution.loadAndUnload.exception.LoadIllegalException;
import com.jd.bluedragon.distribution.sdk.common.domain.BaseResult;
import com.jd.bluedragon.distribution.sdk.modules.board.BoardChuteJsfService;
import com.jd.bluedragon.distribution.sdk.modules.board.domain.BoardCompleteRequest;
import com.jd.bluedragon.distribution.send.domain.SendM;
import com.jd.bluedragon.distribution.send.service.DeliveryService;
import com.jd.bluedragon.distribution.send.utils.SendBizSourceEnum;
import com.jd.bluedragon.distribution.waybill.domain.WaybillStatus;
import com.jd.bluedragon.dms.utils.BusinessUtil;
import com.jd.bluedragon.dms.utils.WaybillUtil;
import com.jd.bluedragon.external.gateway.service.SortBoardGatewayService;
import com.jd.bluedragon.utils.DateHelper;
import com.jd.bluedragon.utils.JsonHelper;
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
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service("sortBoardJsfService")
public class SortBoardJsfServiceImpl implements SortBoardJsfService {
    private static final Logger log = LoggerFactory.getLogger(SortBoardJsfServiceImpl.class);

    private static String AUTO_COMPLETE_BOARD_KEY_PREFIX = "AUTO-COMPLETE-BOARD-KEY-";

    private static String AUTO_CREATE_BOARD_KEY_PREFIX = "AUTO-CREATE-BOARD-KEY-";
    @Autowired
    private SortBoardGatewayService sortBoardGatewayService;
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
                boards.add(initBoard(dto));
            }
            response.setData(boards);
        }

        return response;
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

    /**
     * 分拣机组板
     * 如果过该板已发货 则补发货
     * /
     *
     * @param request
     * @return
     */
    @Override
    @JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWEB, jKey = "DMS.WEB.SortBoardJsfServiceImpl.bindBoard", mState = JProEnum.TP)
    public Response<String> bindBoard(BindBoardRequest request) {
        Response<String> response = checkParma4BindBoard(request);
        if(!response.isSucceed()){
            return response;
        }
        //校验发货状态
        response = checkBarcodeSendStatus(request);
        //已发货不能再组板
        if(!response.isSucceed()){
            return response;
        }

        //板在改包裹或箱落格前发货，生成新板号
        if(StringUtils.isNotBlank(response.getData())){
            request.getBoard().setCode(response.getData());
        }

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
        com.jd.bluedragon.common.dto.base.request.OperatorInfo operatorInfo = initOperatorInfo(request.getOperatorInfo());

        virtualBoardService.sendWaybillTrace(request.getBarcode(), operatorInfo, request.getBoard().getCode(),
                request.getBoard().getDestination(), WaybillStatus.WAYBILL_TRACK_BOARD_COMBINATION);


        response.toSucceed();
        return response;

    }

    private com.jd.bluedragon.common.dto.base.request.OperatorInfo initOperatorInfo( OperatorInfo operatorInfo){
        com.jd.bluedragon.common.dto.base.request.OperatorInfo operator = new com.jd.bluedragon.common.dto.base.request.OperatorInfo();
        operator.setOperateTime(operatorInfo.getOperateTime());
        operator.setSiteCode(operatorInfo.getSiteCode());
        operator.setSiteName(operatorInfo.getSiteName());
        operator.setUserCode(operatorInfo.getUserCode());
        operator.setUserName(operatorInfo.getUserName());
        operator.setUserErp(operatorInfo.getUserErp());
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
            com.jd.bluedragon.distribution.sdk.common.domain.InvokeResult<String> baseResult =
                    boardChuteJsfService.boardComplete(boardCompleteRequest);
            if(!baseResult.isSuccess()){
                log.warn("修改板状态");
                response.toFail(baseResult.getMessage());
                return response;
            }

            //调板服务关闭板状态
            CloseVirtualBoardPo po = initCloseVirtualBoardPo(baseResult.getData());
            JdCResponse<Void> jdCResponse = virtualBoardService.closeBoard(po);
            response.setCode(jdCResponse.getCode());
            response.setMessage(jdCResponse.getMessage());
            return response;
        }finally {
            jimdbCacheService.del(key);
        }

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

    private CloseVirtualBoardPo initCloseVirtualBoardPo(String boardCode){
        CloseVirtualBoardPo po = new CloseVirtualBoardPo();
        po.setBoardCode(boardCode);
        po.setBizSource(BizSourceEnum.SORTING_MACHINE.getValue());
        return po;
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
        SendM sendM = virtualBoardService.getRecentSendMByParam(request.getBoard().getCode(),
                request.getOperatorInfo().getSiteCode(), null, null);
        if(sendM == null){
            response.toSucceed();
            return response;
        }

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

        domain.setYn(1);
        domain.setCreateTime(DateHelper.add(operatorInfo.getOperateTime(), Calendar.SECOND, 5));
        domain.setOperateTime(DateHelper.add(operatorInfo.getOperateTime(), Calendar.SECOND, 5));
        return domain;
    }


    public BoardChuteJsfService getBoardChuteJsfService() {
        return boardChuteJsfService;
    }
}
