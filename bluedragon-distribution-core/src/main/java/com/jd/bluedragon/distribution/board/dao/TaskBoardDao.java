package com.jd.bluedragon.distribution.board.dao;

import com.jd.bluedragon.common.dao.BaseDao;
import com.jd.bluedragon.distribution.board.domain.TaskBoard;


public class TaskBoardDao  extends BaseDao<TaskBoard> {

    private static final String NAMESPACE = TaskBoardDao.class.getName();

    public boolean insert(TaskBoard record) {
        return super.getSqlSession().insert(NAMESPACE + ".add", record) > 0;
    }

    public TaskBoard findRecordByTaskIdAndBoardCode(TaskBoard record) {
        return super.getSqlSession().selectOne(NAMESPACE + ".findRecordByTaskIdAndBoardCode", record);
    }

}
