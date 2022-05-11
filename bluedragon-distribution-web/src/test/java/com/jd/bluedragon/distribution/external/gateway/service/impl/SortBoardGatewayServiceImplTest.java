package com.jd.bluedragon.distribution.external.gateway.service.impl;


import com.alibaba.fastjson.JSON;
import com.jd.bluedragon.common.dto.base.response.JdCResponse;
import com.jd.bluedragon.common.dto.board.response.BoardInfoDto;
import com.jd.bluedragon.common.dto.board.response.VirtualBoardResultDto;
import com.jd.bluedragon.distribution.board.service.BoardCombinationService;
import com.jd.bluedragon.distribution.board.service.VirtualBoardService;
import com.jd.transboard.api.dto.BoardBoxInfoDto;
import com.jd.transboard.api.dto.Response;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.internal.matchers.Any;
import org.mockito.junit.MockitoJUnitRunner;

/**
 * 按板相关测试接口
 */
@RunWith(MockitoJUnitRunner.class)
public class SortBoardGatewayServiceImplTest {

    @InjectMocks
    private SortBoardGatewayServiceImpl sortBoardGatewayServiceImpl;

    @Mock
    private BoardCombinationService boardCombinationService;

    @Mock
    private VirtualBoardService virtualBoardService;

    @Test
    public void queryBoardInfoTest(){
        Integer siteCode = 38;
        String packageOrBoxCode ="JDV000488225619-1-5-";

        BoardBoxInfoDto dto = new BoardBoxInfoDto();
        dto.setCode("B123123123");
        dto.setDestination("海淀站");
        dto.setDestinationId(38);
        dto.setOperatorErp("chen");
        dto.setOperatorName("chenName");

        Response<BoardBoxInfoDto> response = new Response<>();
        response.setCode(200);
        response.setMesseage("成功！");
        response.setData(dto);

        VirtualBoardResultDto virtualBoardResultDto = new VirtualBoardResultDto();
        virtualBoardResultDto.setBoardCode("B123123123");
        virtualBoardResultDto.setDestinationId(38);
        virtualBoardResultDto.setPackageTotal(345);

        JdCResponse<VirtualBoardResultDto> virtualBoard = new JdCResponse<>();
        virtualBoard.setCode(200);
        virtualBoard.setMessage("success");
        virtualBoard.setData(virtualBoardResultDto);

        Mockito.when(boardCombinationService.getBoardBoxInfo(Mockito.<Integer>any(),Mockito.<String>any())).thenReturn(response);

        Mockito.when(virtualBoardService.getBoxCountByBoardCode(Mockito.<String>any())).thenReturn(virtualBoard);

        JdCResponse<BoardInfoDto> boardInfoDtoJdCResponse = sortBoardGatewayServiceImpl.queryBoardInfo(siteCode, packageOrBoxCode);
        System.out.println(JSON.toJSON(boardInfoDtoJdCResponse));
        Assert.assertEquals(virtualBoard.CODE_SUCCESS,boardInfoDtoJdCResponse.getCode());

    }
}
