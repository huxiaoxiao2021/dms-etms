package com.jd.bluedragon.distribution.jy.service.send;

import com.jd.bluedragon.common.dto.comboard.response.BoardDto;
import com.jd.bluedragon.common.dto.comboard.response.SendFlowDto;
import com.jd.bluedragon.distribution.jy.comboard.JyBizTaskComboardEntity;
import com.jd.bluedragon.distribution.jy.dao.comboard.JyBizTaskComboardDao;
import com.jd.bluedragon.distribution.jy.dto.comboard.JyBizTaskComboardReq;
import com.jd.bluedragon.distribution.jy.enums.ComboardStatusEnum;
import com.jd.bluedragon.utils.DateHelper;
import com.jd.bluedragon.utils.ObjectHelper;
import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


/**
 * @author liwenji
 * @date 2022-11-22 19:45
 */

@Service
@Slf4j
public class JyBizTaskComboardServiceImpl implements JyBizTaskComboardService {

  @Autowired
  JyBizTaskComboardDao jyBizTaskComboardDao;

  @Override
  public BoardDto queryInProcessBoard(SendFlowDto sendFlowDto) {
    JyBizTaskComboardEntity condition = new JyBizTaskComboardEntity();
    condition.setStartSiteId(Long.valueOf(sendFlowDto.getStartSiteId()));
    condition.setEndSiteId(Long.valueOf(sendFlowDto.getEndSiteId()));
    condition.setStatus(ComboardStatusEnum.PROCESSING.getCode());
    List<JyBizTaskComboardEntity> bizTaskList = jyBizTaskComboardDao.queryBoardTask(condition);
    if (ObjectHelper.isNotNull(bizTaskList) && bizTaskList.size() == 1) {
      BoardDto dto = new BoardDto();
      dto.setBoardCode(bizTaskList.get(0).getBoardCode());
      dto.setCount(bizTaskList.get(0).getCount());
      dto.setStatus(bizTaskList.get(0).getStatus());
      dto.setBulkFlag(bizTaskList.get(0).getBulkFlag());
      dto.setBizId(bizTaskList.get(0).getBizId());
      dto.setSendCode(bizTaskList.get(0).getSendCode());
      return dto;
    }
    return null;
  }

  @Override
  public List<JyBizTaskComboardEntity> queryInProcessBoardListBySendFlowList(Integer startSiteCode,
      List<Integer> endSiteCodeList) {
    JyBizTaskComboardReq req = new JyBizTaskComboardReq();
    req.setStartSiteId(startSiteCode);
    req.setEndSiteCodeList(endSiteCodeList);
    return jyBizTaskComboardDao.queryInProcessBoardListBySendFlowList(req);
  }

  @Override
  public boolean save(JyBizTaskComboardEntity entity) {
    return jyBizTaskComboardDao.insertSelective(entity) > 0;
  }

  @Override
  public JyBizTaskComboardEntity queryBizTaskByBoardCode(JyBizTaskComboardEntity record) {
    List<JyBizTaskComboardEntity> bizTaskList = jyBizTaskComboardDao.queryBoardTask(record);
    if (ObjectHelper.isNotNull(bizTaskList) && bizTaskList.size() == 1) {
      return bizTaskList.get(0);
    }
    return null;
  }

  @Override
  public int updateBizTaskById(JyBizTaskComboardEntity record) {
    return jyBizTaskComboardDao.updateByPrimaryKeySelective(record);
  }

  @Override
  public Boolean finishBoard(JyBizTaskComboardReq jyBizTaskComboardReq) {
    return jyBizTaskComboardDao.finishBoard(jyBizTaskComboardReq) > 0;
  }

  @Override
  public Boolean batchFinishBoardBySendFLowList(JyBizTaskComboardReq jyBizTaskComboardReq) {
    return jyBizTaskComboardDao.batchFinishBoardBySendFLowList(jyBizTaskComboardReq) > 0;
  }

  @Override
  public List<JyBizTaskComboardEntity> listBoardTaskBySendFlow(SendFlowDto sendFlowDto) {
    JyBizTaskComboardEntity condition = new JyBizTaskComboardEntity();
    condition.setStartSiteId(Long.valueOf(sendFlowDto.getStartSiteId()));
    condition.setEndSiteId(Long.valueOf(sendFlowDto.getEndSiteId()));
    condition.setCreateTime(sendFlowDto.getQueryTimeBegin());
    List<Integer> statusList = new ArrayList<>();
    statusList.add(ComboardStatusEnum.PROCESSING.getCode());
    statusList.add(ComboardStatusEnum.FINISHED.getCode());
    statusList.add(ComboardStatusEnum.CANCEL_SEAL.getCode());
    condition.setStatusList(statusList);
    return jyBizTaskComboardDao.listBoardTaskBySendFlow(condition);
  }
}
