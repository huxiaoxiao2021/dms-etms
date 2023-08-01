package com.jd.bluedragon.distribution.board.service;


import com.jd.bluedragon.Constants;
import com.jd.bluedragon.common.dto.board.request.CombinationBoardRequest;
import com.jd.bluedragon.core.base.BaseMajorManager;
import com.jd.bluedragon.distribution.board.dao.TaskBoardDao;
import com.jd.bluedragon.distribution.board.domain.TaskBoard;
import com.jd.bluedragon.enums.WageBusinessEnum;
import com.jd.ql.basic.dto.BaseStaffSiteOrgDto;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;

/**
 * Created by xumei3 on 2018/3/27.
 */

@Service("taskBoardService")
public class TaskBoardServiceImpl implements TaskBoardService {

    @Resource
    private TaskBoardDao taskBoardDao;

    @Resource
    private BaseMajorManager baseMajorManager;


    @Override
    @JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWEB, jKey = "DMSWEB.TaskBoardServiceImpl.saveTaskBoard", mState = {JProEnum.TP, JProEnum.FunctionError})
    public void saveTaskBoard(CombinationBoardRequest request) {
        if (request.getTaskId() == null) {
            return;
        }
        TaskBoard params = new TaskBoard();
        params.setTaskId(request.getTaskId());
        params.setBusinessId(WageBusinessEnum.UNLOAD_CAR_BOARD.getCode());
        params.setBoardCode(request.getBoardCode());
        TaskBoard taskBoardPo = taskBoardDao.findRecordByTaskIdAndBoardCode(params);
        if (taskBoardPo == null) {
            taskBoardPo = createNewTaskBoard(request);
            taskBoardDao.insert(taskBoardPo);
        }
    }

    private TaskBoard createNewTaskBoard(CombinationBoardRequest request) {
        TaskBoard taskBoardPo = new TaskBoard();
        taskBoardPo.setTaskId(request.getTaskId());
        taskBoardPo.setBusinessId(WageBusinessEnum.UNLOAD_CAR_BOARD.getCode());
        taskBoardPo.setBoardCode(request.getBoardCode());
        taskBoardPo.setCurrentSiteName(request.getCurrentOperate().getSiteName());
        taskBoardPo.setCurrentSiteCode(request.getCurrentOperate().getSiteCode());
        // 设置其他青龙基础资料
        initBasicData(taskBoardPo);
        taskBoardPo.setCreateUserName(request.getUser().getUserName());
        taskBoardPo.setCreateUserErp(request.getUser().getUserErp());
        taskBoardPo.setUpdateUserName(request.getUser().getUserName());
        taskBoardPo.setUpdateUserErp(request.getUser().getUserErp());
        taskBoardPo.setCreateTime(new Date());
        taskBoardPo.setUpdateTime(new Date());
        return taskBoardPo;
    }

    private void initBasicData(TaskBoard taskBoardPo) {
        // 初始化基础数据
        BaseStaffSiteOrgDto site = baseMajorManager.getBaseSiteBySiteId(taskBoardPo.getCurrentSiteCode());
        if (site != null) {
            taskBoardPo.setCurrentDmsCode(site.getDmsSiteCode());
            taskBoardPo.setOrgCode(site.getOrgId());
            taskBoardPo.setOrgName(site.getOrgName());
        }
    }


}
