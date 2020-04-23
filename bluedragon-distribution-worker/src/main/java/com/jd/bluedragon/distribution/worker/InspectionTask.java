package com.jd.bluedragon.distribution.worker;

import com.google.gson.reflect.TypeToken;
import com.jd.bluedragon.Constants;
import com.jd.bluedragon.common.utils.ProfilerHelper;
import com.jd.bluedragon.distribution.api.request.InspectionRequest;
import com.jd.bluedragon.distribution.framework.AbstractTaskExecute;
import com.jd.bluedragon.distribution.framework.DBSingleScheduler;
import com.jd.bluedragon.distribution.inspection.exception.InspectionException;
import com.jd.bluedragon.distribution.inspection.exception.WayBillCodeIllegalException;
import com.jd.bluedragon.distribution.inspection.service.InspectionService;
import com.jd.bluedragon.distribution.task.domain.Task;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.ump.profiler.CallerInfo;
import com.jd.ump.profiler.proxy.Profiler;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.lang.reflect.Type;
import java.util.List;

/**
 * 验货worker
 * 
 * @author wangzichen
 * 
 */
public class InspectionTask extends DBSingleScheduler {

	private final static Logger log = LoggerFactory.getLogger(InspectionTask.class);

    private static final Type LIST_INSPECTIONREQUEST_TYPE=new TypeToken<List<InspectionRequest>>(){}.getType();

	@Autowired
	private InspectionService inspectionService;
    private static final String SPLIT_CHAR="$";

    @Qualifier("inspectionTaskExecute")
    @Autowired()
    private AbstractTaskExecute taskExecute;
    
	@Override
	protected boolean executeSingleTask(Task task, String ownSign)
			throws Exception {
    		CallerInfo callerInfo = ProfilerHelper.registerInfo("DmsWorker.Task.InspectionTask.execute",
    				Constants.UMP_APP_NAME_DMSWORKER);
			try {
				log.info("验货work开始，task_id: {}" , task.getId());
				//List<Inspection> inspections = inspectionService.parseInspections(task);
				//inspectionService.inspectionCore(inspections);
                if (task == null || StringUtils.isBlank(task.getBody())) {
                    return true;
                }
                /**
                 * 此处理为消除列表情况，最早任务保存的是数组，此处拆为单条，以防万一
                 */
                List<InspectionRequest> middleRequests=JsonHelper.fromJsonUseGson(task.getBody(),LIST_INSPECTIONREQUEST_TYPE);
                if(null==middleRequests||middleRequests.size()==0){
                    return true;
                }
                Task domain=new Task();
                domain.setId(task.getId());
                for (InspectionRequest request:middleRequests){
                    domain.setBody(JsonHelper.toJson(request));
                    taskExecute.execute(domain);
                }
			}catch (WayBillCodeIllegalException wayBillCodeIllegalEx){
                StringBuilder sb=new StringBuilder("验货执行失败,已知异常");
                sb.append(wayBillCodeIllegalEx.getMessage());
                sb.append(SPLIT_CHAR).append(task.getBoxCode());
                sb.append(SPLIT_CHAR).append(task.getKeyword1());
                sb.append(SPLIT_CHAR).append(task.getKeyword2());
                log.warn(sb.toString());
                task.setExecuteCount(4);    //非法运单号异常讲执行次数设置为4
                return false;
			}catch (InspectionException inspectionEx){
                StringBuilder sb=new StringBuilder("验货执行失败,已知异常");
                sb.append(inspectionEx.getMessage());
                sb.append(SPLIT_CHAR).append(task.getBoxCode());
                sb.append(SPLIT_CHAR).append(task.getKeyword1());
                sb.append(SPLIT_CHAR).append(task.getKeyword2());
                log.warn(sb.toString());

                return false;
            }catch (Exception e) {
				log.error("验货worker失败, task id: {}. 异常信息:{} " ,task.getId(), e.getMessage(), e);
				Profiler.functionError(callerInfo);
				return false;
			}finally{
				Profiler.registerInfoEnd(callerInfo);
			}
		return true;
	}

}
