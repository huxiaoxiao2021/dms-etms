package com.jd.bluedragon.distribution.task.service;

import com.jd.bluedragon.distribution.api.request.AutoSortingPackageDto;
import com.jd.bluedragon.distribution.api.request.TaskRequest;
import com.jd.bluedragon.distribution.task.domain.Task;

import java.util.List;


public interface TaskService {

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
}
