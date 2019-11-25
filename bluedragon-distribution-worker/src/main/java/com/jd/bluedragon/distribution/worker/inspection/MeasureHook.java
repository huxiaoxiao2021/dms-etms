package com.jd.bluedragon.distribution.worker.inspection;

import com.jd.bluedragon.distribution.framework.TaskHook;
import com.jd.bluedragon.distribution.inspection.domain.Inspection;
import com.jd.bluedragon.distribution.task.domain.Task;
import com.jd.bluedragon.distribution.task.service.TaskService;
import com.jd.bluedragon.distribution.weight.domain.OpeEntity;
import com.jd.bluedragon.distribution.weight.domain.OpeObject;
import com.jd.bluedragon.utils.BusinessHelper;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.ql.basic.dto.BaseStaffSiteOrgDto;
import com.jd.ump.annotation.JProfiler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

/**
 * 测量体积及称重
 * Created by wangtingwei on 2017/1/17.
 */
@Service("measureHook")
public class MeasureHook implements TaskHook<InspectionTaskExecuteContext> {

    private static final Logger log = LoggerFactory.getLogger(MeasureHook.class);

    @Autowired
    private TaskService taskService;

    @Override
    @JProfiler( jKey = "dmsworker.MeasureHook.hook")
    public int hook(InspectionTaskExecuteContext context) {
        for (Inspection inspection : context.getInspectionList()) {
                if ((inspection.getLength() != null && inspection.getLength() > 0)
                        || (inspection.getWidth() != null && inspection.getWidth() > 0)
                        || (inspection.getHigh() != null && inspection.getHigh() > 0)) {
                    if(log.isDebugEnabled()){
                        log.debug("龙门架:{}" , JsonHelper.toJson(inspection));
                    }
                    OpeEntity opeEntity = new OpeEntity();
                    opeEntity.setOpeType(1);//分拣中心称重
                    opeEntity.setWaybillCode(inspection.getWaybillCode());
                    opeEntity.setOpeDetails(new ArrayList<OpeObject>());

                    OpeObject obj = new OpeObject();
                    obj.setOpeSiteId(inspection.getCreateSiteCode());
                    BaseStaffSiteOrgDto dto = context.getCreateSite();
                    obj.setOpeSiteName(dto.getSiteName());
                    obj.setpWidth(inspection.getWidth());
                    obj.setpLength(inspection.getLength());
                    obj.setpHigh(inspection.getHigh());
                    obj.setPackageCode(inspection.getPackageBarcode());
                    obj.setOpeUserId(inspection.getCreateUserCode());
                    obj.setOpeUserName(inspection.getCreateUser());
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    obj.setOpeTime(simpleDateFormat.format(inspection.getCreateTime()));

                    opeEntity.getOpeDetails().add(obj);
                    String body = "[" + JsonHelper.toJson(opeEntity) + "]";
                    Task task = new Task();
                    task.setBody(body);
                    task.setType(Task.TASK_TYPE_WEIGHT);
                    task.setTableName(Task.getTableName(Task.TASK_TYPE_WEIGHT));
                    task.setCreateSiteCode(opeEntity.getOpeDetails().get(0).getOpeSiteId());
                    task.setKeyword1(String.valueOf(opeEntity.getOpeDetails().get(0).getOpeSiteId()));
                    task.setKeyword2("上传长宽高");
                    task.setBody(body);
                    task.setBoxCode("");
                    task.setSequenceName(Task.getSequenceName(task.getTableName()));
                    task.setReceiveSiteCode(opeEntity.getOpeDetails().get(0).getOpeSiteId());
                    task.setOwnSign(BusinessHelper.getOwnSign());
                    taskService.add(task);
                }

        }
        return 1;
    }
}
