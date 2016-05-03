package com.jd.bluedragon.distribution.task.asynBuffer.jmq;

import com.jd.bluedragon.distribution.task.domain.Task;
import com.jd.ql.framework.asynBuffer.protocol.JmqMessageJsonCoder;

/**
 * 任务类型的json格式消息编解码器。
 * @author yangwubing
 *
 */
public class TaskJmqMessageJsonCoder extends JmqMessageJsonCoder<Task> {

}
