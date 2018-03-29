package com.jd.bluedragon.distribution.board.service;

import com.jd.transboard.api.dto.Board;
import com.jd.transboard.api.dto.Response;
import com.jd.transboard.api.service.GroupBoardService;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * Created by xumei3 on 2018/3/29.
 */

@Service("groupBoardService")
public class GroupBoardServiceImpl implements GroupBoardService {
    @Override
    public Response<Integer> addBoxToBoard(String s, String s1) {
        Integer count = 1;
        Response response = new Response();
        response.setCode(200);
        response.setMesseage("success");
        response.setData(count);

        return response;
    }

    @Override
    public Response<Boolean> closeBoard(String s) {
        return null;
    }

    @Override
    public Response<List<String>> getBoxesByBoardCode(String s) {
        return null;
    }

    @Override
    public Response<Board> getBoardByCode(String s) {
        Board board = new Board();
        board.setCode(s);
        board.setCreateTime(new Date());
        board.setDestination("test");

        if(s.charAt(s.length()-1)=='1'){
            board.setStatus(2);
        }else{
            board.setStatus(1);
        }
        Response response = new Response();
        response.setCode(200);
        response.setMesseage("success");
        response.setData(board);

        return response;
    }
}
