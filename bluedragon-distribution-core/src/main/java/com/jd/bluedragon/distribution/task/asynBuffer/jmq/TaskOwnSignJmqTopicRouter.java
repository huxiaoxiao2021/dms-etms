package com.jd.bluedragon.distribution.task.asynBuffer.jmq;

import com.jd.bluedragon.distribution.task.domain.Task;
import com.jd.ql.framework.asynBuffer.producer.jmq.JmqTopicRouter;
import org.apache.commons.lang.StringUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * @author dudong
 * @version 1.0
 * @date 2016/5/10
 *
 * Tbschedule的task type与jmq的topic对应关系的路由器。
 * 区分OwnSign
 */
public class TaskOwnSignJmqTopicRouter implements JmqTopicRouter<Task> {
    private Map<String /** tablename_tasktype_ownsign **/, String /** topic **/> routerMap = new HashMap<String, String>();

    public TaskOwnSignJmqTopicRouter(Map<String, String> routerMap) {
        super();
        this.routerMap = routerMap;
    }

    @Override
    public String getTopic(Task task) {
        //检查参数。
        if(task==null || task.getType()==null){
            return null;
        }
        String ownSign = task.getOwnSign().trim();
        if(StringUtils.isBlank(ownSign)){
            ownSign = "DMS";
        }
        return routerMap.get(task.getTableName() + "_" + task.getType() + "_" + ownSign);
    }
}
