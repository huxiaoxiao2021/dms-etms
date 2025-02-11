package com.jd.bluedragon.distribution.task.service;

import com.jd.bluedragon.distribution.api.request.AutoSortingPackageDto;
import com.jd.bluedragon.distribution.api.request.TaskRequest;
import com.jd.bluedragon.distribution.api.response.TaskResponse;
import com.jd.bluedragon.distribution.jy.dto.work.TaskWorkGridManagerScanData;
import com.jd.bluedragon.distribution.jy.dto.work.TaskWorkGridManagerSiteScanData;
import com.jd.bluedragon.distribution.task.domain.Task;

import java.util.List;


public interface TaskService {

    TaskResponse add(TaskRequest request);

    void initFingerPrint(Task task);

    void addBatch(List<Task> tasks);

    void addBatch(List<Task> tasks, boolean ifCheckTaskMode);

    Integer add(Task task);
    
    Integer add(Task task, boolean isTaskModeCheck);

    Boolean doLock(Task task);

    Boolean doRevert(Task task);

    Boolean doError(Task task);

    Boolean doDone(Task task);

    Integer doAddWithStatus(Task task);
    
    List<Task> findTasks(Integer type);

    List<Task> findTasks(Integer type, String ownSign);

    List<Task> findLimitedTasks(Integer fetchNum,List<String> queueIds,String ownSign);

    List<Task> findLimitedTasks(Integer type, Integer fetchNum,List<String> queueIds);

    List<Task> findLimitedTasks(Integer type, Integer fetchNum, String ownSign,List<String> queueIds);

    List<Task> findSpecifiedTasks(Integer type, Integer fetchNum, String ownSign,List<String> queueIds);

    List<Task> findSendTasks(Integer type, Integer fetchNum, String key,List<String> queueIds,String ownSign, List<String> ownSigns);

    List<Task> findTasksUnderOptimizeSendTask(Integer type, Integer fetchNum, String key,List<String> queueIds,String ownSign, List<String> ownSigns,Integer executeCount);

    List<Task> findTasksByFingerprint(Task task);

    Boolean updateBySelective(Task task);

    Boolean updateBySelectiveWithBody(Task task);

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
    Integer findFailTasksNumsByType(Integer type, String ownSign,Integer keyword1);

    /**
     * 查询待处理的数据, sql不使用type过滤
     * @param type
     * @param ownSign
     * @return
     */
    Integer findTasksNumsIgnoreType(Integer type, String ownSign);

    /**
     * 查询处理的数据, sql不使用type过滤
     * @param type
     * @param ownSign
     * @return
     */
    Integer findFailTasksNumsIgnoreType(Integer type, String ownSign);

    /**
     * 自动分拣任务同时插入分拣和交接任务
     * @param request
     * @return void
     * */
    void addInspectSortingTask(TaskRequest request);


    /**
     * 上海邮通等直接插入分拣和验货task表
     * @param packageDtos
     * @return
     */
    void addInspectSortingTaskDirectly(AutoSortingPackageDto packageDtos) throws Exception;


    Integer doAddTask(Task task, boolean ifCheckTaskMode);

	List<Task> findTaskTypeByStatus(Integer type, int fetchNum, List<String> queueIds);

	Integer updateTaskStatus(Task task);

    /**
     * 第三方发货数据推财务，从delivery_to_finance_batch表查找任务
     * 让该任务延迟执行
     * @param type
     * @param fetchNum
     * @return
     */
    List<Task> findDeliveryToFinanceConvertTasks(Integer type, Integer fetchNum,List<String> queueIds);

    /**
     * 虚拟组板自动关闭任务
     * @return 任务结果列表
     * @author fanggang7
     * @time 2021-09-15 15:10:45 周三
     */
    List<Task> findVirtualBoardTasks(Integer type, Integer fetchNum, String ownSign, List<String> queueIds, Integer lazyExecuteDays);

    /**
     * 查找作业工作台自动关闭任务，一个封车编码只有一条
     * @author fanggang7
     * @time 2023-03-21 16:34:55 周二
     */
    List<Task> findJyBizAutoCloseTasks(Integer type, Integer fetchNum, List<String> ownSigns, List<String> queueIds);
    /**
     * 查询最近一次待执行的任务
     * @param taskData
     * @return
     */
	Task findLastWorkGridManagerScanTask(TaskWorkGridManagerScanData taskData);
    /**
     * 查询最近一次待执行的站点任务
     * @param taskData
     * @return
     */
	Task findLastWorkGridManagerSiteScanTask(TaskWorkGridManagerSiteScanData taskData);

    /**
     * 查找延迟调度任务
     * @author wuyoude
     * @time 2023-06-20 16:34:55 周二
     */
    List<Task> findListForDelayTask(Integer type, Integer fetchNum, String ownSign, List<String> queueIds);

    void initOthers(String jsonVal, Task task);
}
