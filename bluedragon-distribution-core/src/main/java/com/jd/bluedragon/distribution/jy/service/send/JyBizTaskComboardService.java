package com.jd.bluedragon.distribution.jy.service.send;

import com.jd.bluedragon.common.dto.comboard.response.BoardDto;
import com.jd.bluedragon.common.dto.comboard.response.SendFlowDto;
import com.jd.bluedragon.distribution.jy.comboard.JyBizTaskComboardEntity;
import com.jd.bluedragon.distribution.jy.dto.comboard.BoardCountDto;
import com.jd.bluedragon.distribution.jy.dto.comboard.BoardCountReq;
import com.jd.bluedragon.distribution.jy.dto.comboard.CountBoardDto;
import com.jd.bluedragon.distribution.jy.dto.comboard.JyBizTaskComboardReq;

import com.jd.bluedragon.distribution.jy.enums.ComboardStatusEnum;
import java.util.Date;
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
  List<JyBizTaskComboardEntity> queryInProcessBoardListBySendFlowList(Integer startSiteCode, List<Integer> endSiteCode,String groupCode);

  boolean save(JyBizTaskComboardEntity entity);

  JyBizTaskComboardEntity queryBizTaskByBoardCode(JyBizTaskComboardEntity record);

  int updateBizTaskById(JyBizTaskComboardEntity record);

  /**
   * 批量完结组板
   * @param jyBizTaskComboardReq
   * @return
   */
  Boolean finishBoard(JyBizTaskComboardReq jyBizTaskComboardReq);

  /**
   * 根据流向完结板号
   * @param jyBizTaskComboardReq
   * @return
   */
  Boolean batchFinishBoardBySendFLowList(JyBizTaskComboardReq jyBizTaskComboardReq);

  /**
   * 查询流向下板列表(某个流向-7日内所有未封车的板任务列表)
   * @return
   */
  List<JyBizTaskComboardEntity> listBoardTaskBySendFlow(SendFlowDto sendFlowDto);

  /**
   * 查询流向下板列表(某个流向-7日内所有未封车 2日内所有已封车的板任务列表)
   * @return
   */
  List<JyBizTaskComboardEntity> listSealOrUnSealedBoardTaskBySendFlow(SendFlowDto sendFlowDto);

  List<JyBizTaskComboardEntity> listBoardTaskBySendCode(JyBizTaskComboardEntity entity);

  /**
   * 查询流向下板数量(某个流向-7日内所有未封车的板任务列表)
   * @return
   */
    List<BoardCountDto> boardCountTaskBySendFlowList(BoardCountReq boardCountReq);

  JyBizTaskComboardEntity queryBizTaskByBoardCode(int siteCode, String boardCode);

  /**
   * 取消封车
   * @param batchCode
   * @param operateUserCode
   * @param operateUserName
   * @return
   */
  boolean updateBoardStatusBySendCode(String batchCode, String operateUserCode, String operateUserName);


  boolean updateBoardStatusBySendCodeList(List<String> batchCodeList, String operateUserCode, String operateUserName, ComboardStatusEnum comboardStatusEnum);

  List<SendFlowDto> countBoardGroupBySendFlow(CountBoardDto countBoardDto);
}

