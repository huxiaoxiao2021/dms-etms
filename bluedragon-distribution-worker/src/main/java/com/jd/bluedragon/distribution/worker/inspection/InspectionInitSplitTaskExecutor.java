package com.jd.bluedragon.distribution.worker.inspection;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.distribution.api.request.InspectionRequest;
import com.jd.bluedragon.distribution.inspection.service.InspectionNotifyService;
import com.jd.bluedragon.distribution.inspection.service.InspectionService;
import com.jd.bluedragon.distribution.task.domain.Task;
import com.jd.bluedragon.distribution.task.service.TaskService;
import com.jd.bluedragon.dms.utils.WaybillUtil;
import com.jd.bluedragon.utils.*;
import com.jd.etms.waybill.domain.BaseEntity;
import com.jd.etms.waybill.dto.BigWaybillDto;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;
import java.util.Date;

/**
 * @ClassName InspectionInitSplitTaskExecutor
 * @Description
 * @Author wyh
 * @Date 2020/9/27 20:02
 **/
@Service("inspectionInitSplitTaskExecutor")
public class InspectionInitSplitTaskExecutor extends InspectionTaskCommonExecutor {

    @Autowired
    private InspectionService inspectionService;

    @Autowired
    private InspectionNotifyService inspectionNotifyService;

    @Autowired
    private TaskService taskService;

    @Override
    public boolean process(InspectionRequest request) {

        // 运单多包裹拆分验货任务
        if (this.saveSplitWaybillTask(request)) {

            // 发送验单验货任务，运单/包裹验货不再发送该消息
            this.sendInspectionMQ(request);
        }

        return true;
    }

    /**
     * 发送验货MQ
     * @param request
     */
    private void sendInspectionMQ(InspectionRequest request) {
        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("运单多包裹拆分任务, 提前发送运单验货消息. body:[{}]", JsonHelper.toJson(request));
        }

        inspectionNotifyService.sendMQFromRequest(request);
    }

    /**
     * 运单多包裹验货拆分分批执行
     * @param request
     */
    private boolean saveSplitWaybillTask(InspectionRequest request) {

        String waybillCode = WaybillUtil.getWaybillCode(request.getPackageBarOrWaybillCode());

        BigWaybillDto bigWaybillDto = getWaybillPackage(waybillCode);
        if (bigWaybillDto == null || CollectionUtils.isEmpty(bigWaybillDto.getPackageList())) {
            LOGGER.warn("运单多包裹拆分任务, 查询包裹为空, body:[{}]", JsonHelper.toJson(request));
            return false;
        }

        int oneTaskSize = inspectionService.getInspectionTaskPackageSplitNum();
        int totalPackageNum = bigWaybillDto.getPackageList().size();

        int pageTotal = (totalPackageNum % oneTaskSize) == 0 ? (totalPackageNum / oneTaskSize) : (totalPackageNum / oneTaskSize) + 1;

        for (int i = 0; i < pageTotal; i++) {
            InspectionRequest splitTask = new InspectionRequest();
            BeanCopyUtil.copy(request, splitTask);
            splitTask.setPageNo(i + 1);
            splitTask.setPageSize(oneTaskSize);
            splitTask.setTotalPage(pageTotal);
            splitTask.setWaybillCode(waybillCode);

            Task task = new Task();
            task.setType(Task.TASK_TYPE_INSPECTION_SPLIT);
            task.setTableName(Task.getTableName(task.getType()));
            task.setSequenceName(Task.getSequenceName(task.getTableName()));
            task.setBoxCode(splitTask.getBoxCode());
            task.setCreateSiteCode(splitTask.getSiteCode());
            task.setReceiveSiteCode(splitTask.getReceiveSiteCode());
            task.setKeyword2(String.valueOf(splitTask.getSiteCode()));
            task.setKeyword1(splitTask.getWaybillCode());
            task.setOwnSign(BusinessHelper.getOwnSign());
            task.setBody(JsonHelper.toJson(splitTask));
            task.setBusinessType(splitTask.getBusinessType());
            task.setOperateType(splitTask.getOperateType());
            task.setOperateTime(StringUtils.isNotBlank(splitTask.getOperateTime()) ? DateHelper
                    .getSeverTime(splitTask.getOperateTime()) : new Date());

            StringBuilder fingerprint = new StringBuilder();
            fingerprint.append(task.getCreateSiteCode())
                    .append(Constants.UNDER_LINE).append(task.getReceiveSiteCode())
                    .append(Constants.UNDER_LINE).append(task.getBusinessType())
                    .append(Constants.UNDER_LINE).append(task.getBoxCode())
                    .append(Constants.UNDER_LINE).append(task.getKeyword1())
                    .append(Constants.UNDER_LINE).append(DateHelper.formatDateTimeMs(task.getOperateTime()));
            if (null != task.getOperateType()) {
                fingerprint.append(Constants.UNDER_LINE).append(task.getOperateType());
            }
            fingerprint.append(Constants.UNDER_LINE).append(splitTask.getPageNo());
            task.setFingerprint(Md5Helper.encode(fingerprint.toString()));

            taskService.add(task);

            if (LOGGER.isInfoEnabled()) {
                LOGGER.info("add inspection split task. [{}]", JsonHelper.toJson(task));
            }
        }

        return true;
    }

    private BigWaybillDto getWaybillPackage(String waybillCode) {
        BigWaybillDto result = null;
        BaseEntity<BigWaybillDto> baseEntity = waybillQueryManager.getDataByChoice(waybillCode,true, false, true, true);
        if (baseEntity != null) {
            result = baseEntity.getData();
        }
        if (LOGGER.isInfoEnabled()){
            LOGGER.info(MessageFormat.format("获取运单信息{0}, 结果为{1}", waybillCode, JsonHelper.toJson(result)));
        }

        return result;
    }
}
