package com.jd.bluedragon.distribution.jy.service.send;

import com.jd.bluedragon.common.dto.comboard.response.BoardDto;
import com.jd.bluedragon.common.dto.comboard.response.SendFlowDto;
import com.jd.bluedragon.distribution.jy.comboard.JyBizTaskComboardEntity;

import java.util.List;

/**
 * 分拣租板任务服务
 */
public interface JyBizTaskComboardService {

  /**
   * 查询流向下进行中的板
   * @param sendFlowDto
   * @return
   */
  BoardDto queryInProcessBoard(SendFlowDto sendFlowDto);

  /**
   * 根据始发和目的地站点批量获取进行中的板号
   * @param startSiteCode
   * @param endSiteCode
   * @return
   */
  List<JyBizTaskComboardEntity> queryInProcessBoardListBySendFlowList(Integer startSiteCode, List<Integer> endSiteCode);

  boolean save(JyBizTaskComboardEntity entity);

  JyBizTaskComboardEntity queryBizTaskByBoardCode(JyBizTaskComboardEntity record);

  int updateBizTaskById(JyBizTaskComboardEntity record);
}
