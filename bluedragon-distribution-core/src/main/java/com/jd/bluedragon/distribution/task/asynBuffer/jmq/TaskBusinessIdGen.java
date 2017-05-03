package com.jd.bluedragon.distribution.task.asynBuffer.jmq;

import com.jd.bluedragon.distribution.task.domain.Task;
import com.jd.ql.framework.asynBuffer.producer.jmq.BusinessIdGen;

/**
 * task的业务id生成器。
 *
 * @author yangwubing
 */
public class TaskBusinessIdGen implements BusinessIdGen<Task> {
    private static final String DELIMITER = "-";

    /**
     * 生成task对应的业务id，生成规则是：[type]-[keyword1].
     */
    @Override
    public String genId(Task task) {
//        return task.getType() + DELIMITER + task.getKeyword1();
        return task.getKeyword2();
    }


}
