package com.jd.bluedragon.distribution.jy.service.send;

import com.jd.bluedragon.common.dto.comboard.response.BoardDto;
import com.jd.bluedragon.common.dto.comboard.response.SendFlowDto;
import com.jd.bluedragon.distribution.jy.comboard.JyBizTaskComboardEntity;
import com.jd.bluedragon.distribution.jy.dao.comboard.JyBizTaskComboardDao;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author liwenji
 * @date 2022-11-22 19:45
 */

@Service
@Slf4j
public class JyBizTaskComboardServiceImpl implements JyBizTaskComboardService{
    
    @Autowired
    private JyBizTaskComboardDao jyBizTaskComboardDao;
    
    @Override
    public BoardDto queryInProcessBoard(SendFlowDto sendFlowDto) {
        return null;
    }

    @Override
    public List<JyBizTaskComboardEntity> queryInProcessBoardListBySendFlowList(Integer startSiteCode, List<Integer> endSiteCodeList) {
        return jyBizTaskComboardDao.queryInProcessBoardListBySendFlowList(startSiteCode,endSiteCodeList);
    }

    @Override
    public boolean save(JyBizTaskComboardEntity entity) {
        return false;
    }
}
