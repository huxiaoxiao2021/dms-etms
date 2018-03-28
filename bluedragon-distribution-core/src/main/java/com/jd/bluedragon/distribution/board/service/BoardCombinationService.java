package com.jd.bluedragon.distribution.board.service;

import com.jd.bluedragon.distribution.board.domain.Board;
import com.jd.bluedragon.distribution.send.domain.SendM;

import java.util.List;

/**
 * Created by xumei3 on 2018/3/27.
 */
public interface BoardCombinationService {
    /**
     * 根据板标获取板标基本信息
     * @param boardCode
     */
    public Board getBoardByBoardCode(String boardCode);

    /**
     * 根据板标获取板标绑定的箱号和包裹号
     * @param boardCode
     */
    public void getBoxesAndPackagesByBoardCode(String boardCode);

    /**
     * 回传板标绑定的箱号或包裹号
     */
    public String sendBoardBindings();

    /**
     * 回传板标的发货状态
     */
    public void sendBoardSendStatus();


    /**
     * 查询发货信息
     * @param sendM
     * @return
     */
    public List<SendM> selectBySendSiteCode(SendM sendM);
}
