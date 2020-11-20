package com.jd.bluedragon.distribution.worker.inspection;

import com.jd.bluedragon.distribution.api.request.InspectionRequest;
import com.jd.bluedragon.distribution.api.utils.JsonHelper;
import com.jd.bluedragon.distribution.framework.TaskExecuteContext;
import com.jd.bluedragon.distribution.framework.TaskHook;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Resource;
import java.text.MessageFormat;
import java.util.List;

/**
 * @ClassName AbstractInspectionTaskExecutor
 * @Description
 * @Author wyh
 * @Date 2020/9/25 9:33
 **/
public abstract class AbstractInspectionTaskExecutor<T extends TaskExecuteContext> implements InspectionTaskExecutor<InspectionRequest> {

    protected final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    @Resource(name = "hooks")
    private List<TaskHook<T>> hooks;

    protected abstract T prepare(InspectionRequest request);

    protected abstract boolean executeCoreFlow(T t);

    protected abstract boolean otherOperation(InspectionRequest request, T t);

    @Override
    public boolean process(InspectionRequest request) {

        T context = prepare(request);

        if (context.isPassCheck()) {

            executeCoreFlow(context);

            if (CollectionUtils.isNotEmpty(hooks)) {
                for (TaskHook<T> hook : hooks) {
                    if (hook.escape(context)) {
                        continue;
                    }

                    hook.hook(context);
                }
            }

            otherOperation(request, context);
        }
        else {
            if (LOGGER.isWarnEnabled()) {
                LOGGER.warn(MessageFormat.format("任务执行失败, 未通过验证{0}", JsonHelper.toJson(request)));
            }
            return false;
        }

        return true;
    }

}
