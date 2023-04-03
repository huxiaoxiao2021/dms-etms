package com.jd.bluedragon.distribution.jy.service.send;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.common.dto.comboard.response.BoardDto;
import com.jd.bluedragon.common.dto.comboard.response.SendFlowDto;
import com.jd.bluedragon.configuration.ucc.UccPropertyConfiguration;
import com.jd.bluedragon.distribution.jy.comboard.JyBizTaskComboardEntity;
import com.jd.bluedragon.distribution.jy.dao.comboard.JyBizTaskComboardDao;
import com.jd.bluedragon.distribution.jy.dto.comboard.BoardCountDto;
import com.jd.bluedragon.distribution.jy.dto.comboard.BoardCountReq;
import com.jd.bluedragon.distribution.jy.dto.comboard.CountBoardDto;
import com.jd.bluedragon.distribution.jy.dto.comboard.JyBizTaskComboardReq;
import com.jd.bluedragon.distribution.jy.dto.comboard.UpdateBoardStatusDto;
import com.jd.bluedragon.distribution.jy.enums.ComboardStatusEnum;
import com.jd.bluedragon.distribution.jy.enums.JyBizTaskComboardSourceEnum;
import com.jd.bluedragon.utils.ObjectHelper;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.jd.common.annotation.CacheMethod;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
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

  @Autowired
  UccPropertyConfiguration ucc;

  @Override
  public BoardDto queryInProcessBoard(SendFlowDto sendFlowDto) {
    JyBizTaskComboardEntity condition = new JyBizTaskComboardEntity();
    condition.setStartSiteId(Long.valueOf(sendFlowDto.getStartSiteId()));
    condition.setEndSiteId(Long.valueOf(sendFlowDto.getEndSiteId()));
    condition.setBoardStatus(ComboardStatusEnum.PROCESSING.getCode());
    condition.setComboardSourceList(sendFlowDto.getComboardSourceList());
    condition.setGroupCode(sendFlowDto.getGroupCode());
    List<JyBizTaskComboardEntity> bizTaskList = jyBizTaskComboardDao.queryBoardTask(condition);
    if (ObjectHelper.isNotNull(bizTaskList) && bizTaskList.size() == 1) {
      BoardDto dto = new BoardDto();
      dto.setBoardCode(bizTaskList.get(0).getBoardCode());
      dto.setCount(bizTaskList.get(0).getHaveScanCount());
      dto.setStatus(bizTaskList.get(0).getBoardStatus());
      dto.setBulkFlag(bizTaskList.get(0).getBulkFlag());
      dto.setBizId(bizTaskList.get(0).getBizId());
      dto.setSendCode(bizTaskList.get(0).getSendCode());
      return dto;
    }
    return null;
  }

  @Override
  public List<JyBizTaskComboardEntity> queryInProcessBoardListBySendFlowList(Integer startSiteCode,
      List<Integer> endSiteCodeList,String groupCode) {
    JyBizTaskComboardReq req = new JyBizTaskComboardReq();
    req.setStartSiteId(startSiteCode);
    req.setEndSiteCodeList(endSiteCodeList);
    List<Integer> comboardSourceList = new ArrayList<>();
    comboardSourceList.add(JyBizTaskComboardSourceEnum.ARTIFICIAL.getCode());
    req.setComboardSourceList(comboardSourceList);
    req.setGroupCode(groupCode);
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
    if (sendFlowDto.getQueryTimeBegin() != null ) {
      condition.setCreateTime(sendFlowDto.getQueryTimeBegin());
    }
    condition.setStatusList(sendFlowDto.getStatusList());
    condition.setComboardSourceList(sendFlowDto.getComboardSourceList());
    if (ObjectHelper.isNotNull(sendFlowDto.getGroupCode())){
      condition.setGroupCode(sendFlowDto.getGroupCode());
    }
    return jyBizTaskComboardDao.listBoardTaskBySendFlow(condition);
  }

  @Override
  public List<JyBizTaskComboardEntity> listSealOrUnSealedBoardTaskBySendFlow(SendFlowDto sendFlowDto) {
    JyBizTaskComboardEntity condition = new JyBizTaskComboardEntity();
    condition.setStartSiteId(Long.valueOf(sendFlowDto.getStartSiteId()));
    condition.setEndSiteId(Long.valueOf(sendFlowDto.getEndSiteId()));
    condition.setCreateTime(sendFlowDto.getQueryTimeBegin());
    List<Integer> statusList = new ArrayList<>();
    statusList.add(ComboardStatusEnum.PROCESSING.getCode());
    statusList.add(ComboardStatusEnum.FINISHED.getCode());
    statusList.add(ComboardStatusEnum.CANCEL_SEAL.getCode());
    condition.setStatusList(statusList);
    List<Integer> sealStatusList = new ArrayList<>();
    sealStatusList.add(ComboardStatusEnum.SEALED.getCode());
    condition.setSealStatusList(sealStatusList);
    condition.setComboardSourceList(sendFlowDto.getComboardSourceList());
    condition.setSealTime(sendFlowDto.getQuerySealTimeBegin());
    // 执行sql开关，默认执行or
    if (ucc.getJyComboardListBoardSqlSwitch()) {
      return jyBizTaskComboardDao.listSealOrUnSealedBoardTaskBySendFlow(condition);
    }else {
      return jyBizTaskComboardDao.listSealOrUnSealedBoardTaskBySendFlowUnionAll(condition);
    }
  }

  @Override
  public List<JyBizTaskComboardEntity> listBoardTaskBySendCode(JyBizTaskComboardEntity entity) {
    return jyBizTaskComboardDao.listBoardTaskBySendCode(entity);
  }

  @Override
  public List<BoardCountDto> boardCountTaskBySendFlowList(BoardCountReq boardCountReq){
    return jyBizTaskComboardDao.boardCountTaskBySendFlowList(boardCountReq);
  }

  @Override
  @JProfiler(jKey = "DMSWEB.DmsBoxQueryServiceImpl.isEconomicNetBox",jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP})
  @CacheMethod(key="JyBizTaskComboardServiceImpl.queryBizTaskByBoardCodeNew-{0}-{1}", cacheBean="redisCache", timeout = 1000 * 60 * 5)
  public JyBizTaskComboardEntity queryBizTaskByBoardCode(int siteCode, String boardCode) {
    JyBizTaskComboardEntity condition = new JyBizTaskComboardEntity();
    condition.setStartSiteId(Long.valueOf(siteCode));
    condition.setBoardCode(boardCode);
    return queryBizTaskByBoardCode(condition);
  }

  @Override
  public boolean updateBoardStatusBySendCode(String batchCode, String operateUserCode, String operateUserName) {

    if (StringUtils.isEmpty(batchCode)) {
      return false;
    }

    // 查询任务id
    List<JyBizTaskComboardEntity> taskList = jyBizTaskComboardDao.queryTaskBySendCode(batchCode);

    if (CollectionUtils.isEmpty(taskList)) {
      return true;
    }

    List<Long> taskIds = new ArrayList<>();
    for (JyBizTaskComboardEntity entity : taskList) {
      taskIds.add(entity.getId());
    }
    UpdateBoardStatusDto boardStatusDto = new UpdateBoardStatusDto();
    boardStatusDto.setIds(taskIds);
    boardStatusDto.setUpdateUserErp(operateUserCode);
    boardStatusDto.setUpdateUserName(operateUserName);
    boardStatusDto.setBoardStatus(ComboardStatusEnum.CANCEL_SEAL.getCode());
    boardStatusDto.setUnsealTime(new Date());
    return jyBizTaskComboardDao.updateBoardStatus(boardStatusDto) > 0;
  }

  @Override
  public boolean updateBoardStatusBySendCodeList(List<String> batchCodeList, String operateUserCode,
      String operateUserName, ComboardStatusEnum comboardStatusEnum) {

    JyBizTaskComboardEntity condition =new JyBizTaskComboardEntity();
    condition.setSendCodeList(batchCodeList);
    List<JyBizTaskComboardEntity> taskList = jyBizTaskComboardDao.listBoardTaskBySendCode(condition);

    if (CollectionUtils.isEmpty(taskList)) {
      return true;
    }

    List<Long> taskIds = new ArrayList<>();
    for (JyBizTaskComboardEntity entity : taskList) {
      taskIds.add(entity.getId());
    }
    UpdateBoardStatusDto boardStatusDto = new UpdateBoardStatusDto();
    boardStatusDto.setIds(taskIds);
    boardStatusDto.setUpdateUserErp(operateUserCode);
    boardStatusDto.setUpdateUserName(operateUserName);
    boardStatusDto.setBoardStatus(comboardStatusEnum.getCode());
    boardStatusDto.setSealTime(new Date());
    return jyBizTaskComboardDao.updateBoardStatus(boardStatusDto) > 0;
  }

  @Override
  public List<SendFlowDto> countBoardGroupBySendFlow(CountBoardDto countBoardDto) {
    return jyBizTaskComboardDao.countBoardGroupBySendFlow(countBoardDto);
  }
}
