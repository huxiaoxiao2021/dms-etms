package com.jd.bluedragon.distribution.task.service;

import java.util.List;

import com.jd.bluedragon.distribution.api.request.TaskRequest;
import com.jd.bluedragon.distribution.task.domain.Task;

public interface TaskService {

    void initFingerPrint(Task task);

    Integer add(Task task);
    
    Integer add(Task task, boolean isTaskModeCheck);

    Boolean doLock(Task task);

    Boolean doRevert(Task task);

    Boolean doError(Task task);

    Boolean doDone(Task task);

    Integer doAddWithStatus(Task task);
    
    List<Task> findTasks(Integer type);

    List<Task> findTasks(Integer type, String ownSign);

    List<Task> findLimitedTasks(Integer fetchNum);

    List<Task> findLimitedTasks(Integer type, Integer fetchNum);

    List<Task> findLimitedTasks(Integer type, Integer fetchNum, String ownSign);

    List<Task> findSpecifiedTasks(Integer type, Integer fetchNum, String ownSign);

    List<Task> findSendTasks(Integer type, Integer fetchNum, String key);

    List<Task> findTasksByFingerprint(Task task);

    Boolean updateBySelective(Task task);

    List<Task> findTasks(Task task);
	
	Task findReverseSendTask(String sendCode);
	
	Task findWaybillSendTask(String sendCode);

    Boolean doRepeat(Task task);

    Task toTask(TaskRequest request, String jsonVal); 
    
    /**
     * 查询待处理的数据
     * @param type
     * @param ownSign
     * @return
     */
    Integer findTasksNumsByType(Integer type, String ownSign);
    
    /**
     * 查询处理的数据
     * @param type
     * @param ownSign
     * @return
     */
    Integer findFailTasksNumsByType(Integer type, String ownSign);

    /**
     * 自动分拣任务同时插入分拣和交接任务
     * @param request
     * @return void
     * */
    void addInspectSortingTask(TaskRequest request);
    
}
