package com.jd.bluedragon.distribution.framework;

import com.jd.bluedragon.distribution.task.domain.Task;
import com.jd.bluedragon.distribution.task.domain.TaskResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dudong on 2014/12/11.
 */
public abstract class AbstractDBSingleScheduler extends DBSingleScheduler {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private String desc;

    private TaskHanlder taskHanlder = new DBTaskHanlder();

    @Deprecated
    @Override
    protected boolean executeSingleTask(Task task, String ownSign) throws Exception {
        throw new UnsupportedOperationException("The method is forbidden to use");
    }

    @Override
    public boolean execute(Object[] taskArray, String ownSign) throws Exception {
        List<Task> tasks = new ArrayList<Task>();
        for (Object task : taskArray) {
            if (task != null && task instanceof Task) {
                tasks.add((Task) task);
            }
        }
        log.info("{}抓取到[{}]条任务待处理",getWorkerDescPrefix(),tasks.size());

        int dealDataFail = 0;
        for (Task task : tasks) {
            boolean result = handleExtendSingleTask(task, ownSign);
            if (!result) {
                dealDataFail++;
            }
        }
        if (dealDataFail > 0) {
            log.warn("{}抓取到[{}]条任务待处理,{}条数据执行失败！",getWorkerDescPrefix(),tasks.size(),dealDataFail);
            return false;
        } else {
            return true;
        }
    }

    private boolean handleExtendSingleTask(Task task, String ownSign) throws Exception {
        if (task == null) {
            return false;
        }
        boolean result = false;
        try {
            result = taskHanlder.preHandle(task);
            if (result) {
                TaskResult exResult = executeExtendSingleTask(task, ownSign);
                switch (exResult) {
                    case SUCCESS: {
                        taskHanlder.handleSuccess(task);
                        break;
                    }
                    case REPEAT: {
                        taskHanlder.handleRedo(task);
                        break;
                    }
                    case FAILED: {
                        taskHanlder.handleError(task);
                        break;
                    }
                }
                result = TaskResult.toBoolean(exResult);
            }
        } catch (Throwable e) {
            taskHanlder.handleError(task);
            log.error("处理任务TaskId:{}失败! " ,task.getId(), e);
        }
        return result;
    }

    protected abstract TaskResult executeExtendSingleTask(Task task, String ownSign) throws Exception;
}
