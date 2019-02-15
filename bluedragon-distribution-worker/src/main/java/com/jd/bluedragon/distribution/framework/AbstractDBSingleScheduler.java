package com.jd.bluedragon.distribution.framework;

import com.jd.bluedragon.distribution.task.domain.Task;
import com.jd.bluedragon.distribution.task.domain.TaskResult;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dudong on 2014/12/11.
 */
public abstract class AbstractDBSingleScheduler extends DBSingleScheduler {

    private final Log logger = LogFactory.getLog(this.getClass());

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
        logger.info(getWorkerDescPrefix() + "抓取到[" + tasks.size() + "]条任务待处理");

        int dealDataFail = 0;
        for (Task task : tasks) {
            boolean result = handleExtendSingleTask(task, ownSign);
            if (!result) {
                dealDataFail++;
            }
        }
        if (dealDataFail > 0) {
            logger.error(getWorkerDescPrefix() + "抓取" + tasks.size() + "条任务，"
                    + dealDataFail + "条数据执行失败！");
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
            logger.error("处理任务TaskId:" + task.getId() + "失败! " + e.getMessage(), e);
        }
        return result;
    }

    protected abstract TaskResult executeExtendSingleTask(Task task, String ownSign) throws Exception;
}
