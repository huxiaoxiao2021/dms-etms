package com.jd.bluedragon.distribution.board.service;

import com.google.gson.reflect.TypeToken;
import com.jd.bluedragon.Constants;
import com.jd.bluedragon.common.dto.base.response.JdCResponse;
import com.jd.bluedragon.common.dto.board.request.CombinationBoardRequest;
import com.jd.bluedragon.common.dto.board.response.BoardCheckDto;
import com.jd.bluedragon.common.dto.board.response.BoardInfoDto;
import com.jd.bluedragon.distribution.api.response.BoardResponse;
import com.jd.bluedragon.distribution.board.SortBoardJsfService;
import com.jd.bluedragon.distribution.board.domain.BoardRequestDto;
import com.jd.bluedragon.distribution.board.domain.Request;
import com.jd.bluedragon.distribution.board.domain.Response;
import com.jd.bluedragon.distribution.loadAndUnload.exception.LoadIllegalException;
import com.jd.bluedragon.external.gateway.service.SortBoardGatewayService;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.ql.dms.common.domain.JdResponse;
import com.jd.transboard.api.dto.Board;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("sortBoardJsfService")
public class SortBoardJsfServiceImpl implements SortBoardJsfService {
    @Autowired
    private SortBoardGatewayService sortBoardGatewayService;
    @Autowired
    BoardCombinationService boardCombinationService;
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
}
