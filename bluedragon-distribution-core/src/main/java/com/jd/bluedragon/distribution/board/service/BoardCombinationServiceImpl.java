package com.jd.bluedragon.distribution.board.service;

import com.jd.bluedragon.distribution.board.domain.Board;
import com.jd.bluedragon.distribution.send.dao.SendMDao;
import com.jd.bluedragon.distribution.send.domain.SendM;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by xumei3 on 2018/3/27.
 */

@Service("boardCombinationService")
public class BoardCombinationServiceImpl implements BoardCombinationService {
    @Autowired
    private SendMDao sendMDao;

    @Override
    public Board getBoardByBoardCode(String boardCode) {

        return null;
    }

    @Override
    public void getBoxesAndPackagesByBoardCode(String boardCode) {

    }

    @Override
    public String sendBoardBindings() {

        String errorStr = null;
        return errorStr;
    }

    @Override
    public void sendBoardSendStatus() {

    }

    /**
     * 查询发货信息
     * @param sendM
     * @return
     */
    public List<SendM> selectBySendSiteCode(SendM sendM){
        //加上参数校验
        return this.sendMDao.selectBySendSiteCode(sendM);
    }
}
