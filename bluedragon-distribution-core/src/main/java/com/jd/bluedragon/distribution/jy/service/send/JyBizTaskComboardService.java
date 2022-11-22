package com.jd.bluedragon.distribution.jy.service.send;

import com.jd.bluedragon.common.dto.comboard.response.BoardDto;
import com.jd.bluedragon.common.dto.comboard.response.SendFlowDto;

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
}
