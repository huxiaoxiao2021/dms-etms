package com.jd.bluedragon.external.gateway.service;

import com.jd.bluedragon.common.dto.base.response.JdCResponse;
import com.jd.bluedragon.common.dto.board.request.CombinationBoardRequest;
import com.jd.bluedragon.common.dto.board.response.BoardCheckDto;
import com.jd.bluedragon.common.dto.board.response.BoardDetailDto;
import com.jd.bluedragon.common.dto.board.response.BoardInfoDto;

/**
 * SortBoardGatewayService
 * 组板服务
 *发布到物流网关 由安卓调用
 * @author jiaowenqiang
 * @date 2019/7/5
 */
public interface SortBoardGatewayService {

    JdCResponse<BoardCheckDto> validateBoardCode(String boardCode);

    JdCResponse combinationBoard(CombinationBoardRequest request);

    JdCResponse combinationBoardCancel(CombinationBoardRequest request);

    JdCResponse<BoardDetailDto> queryBoardDetail(String boardCode);

    JdCResponse<BoardInfoDto> queryBoardInfo(Integer siteCode, String packageOrBoxCode);

}
