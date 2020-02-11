package com.jd.bluedragon.distribution.offline.service.impl;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.distribution.log.BizOperateTypeConstants;
import com.jd.bluedragon.distribution.log.BizTypeConstants;
import com.jd.bluedragon.distribution.log.BusinessLogProfilerBuilder;
import com.jd.dms.logger.external.LogEngine;
import com.jd.bluedragon.distribution.log.OperateTypeConstants;
import com.jd.bluedragon.distribution.offline.dao.OfflineDao;
import com.jd.bluedragon.distribution.offline.domain.OfflineLog;
import com.jd.bluedragon.distribution.offline.service.OfflineLogService;
import com.jd.bluedragon.distribution.task.domain.Task;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.dms.logger.external.BusinessLogProfiler;
import com.jd.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.xml.crypto.Data;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Service("offlineLogService")
public class OfflineLogServiceImpl implements OfflineLogService {

    private final Logger log = LoggerFactory.getLogger(OfflineLogServiceImpl.class);

    @Autowired
    private OfflineDao offlineDao;

    @Autowired
    private LogEngine logEngine;

    @Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public Integer addOfflineLog(OfflineLog offlineLog) {
        if (offlineLog.getBoxCode() != null && offlineLog.getBoxCode().length() > Constants.BOX_CODE_DB_COLUMN_LENGTH_LIMIT) {
            log.warn("箱号超长，无法插入任务，参数：{}", JsonHelper.toJson(offlineLog));
            return -1;
        }


        JSONObject request = new JSONObject();

        request.put("waybillCode", offlineLog.getWaybillCode());
        request.put("packageCode", offlineLog.getPackageCode());
        request.put("boxCode", offlineLog.getBoxCode());
        request.put("sendCode", offlineLog.getSendCode());
        request.put("operatorName", offlineLog.getCreateUser());
        request.put("operatorCode", offlineLog.getCreateUserCode());

        JSONObject response = new JSONObject();
        response.put("创建站点名称", offlineLog.getCreateSiteCode());
        response.put("创建站点名称", offlineLog.getCreateSiteName());
        response.put("目的单位id", offlineLog.getReceiveSiteCode());
        response.put("周转箱号", offlineLog.getTurnoverBoxCode());
        response.put("体积", offlineLog.getVolume());
        response.put("重量", offlineLog.getWeight());
        response.put("封箱号", offlineLog.getSealBoxCode());
        response.put("封车号", offlineLog.getShieldsCarCode());
        response.put("车号", offlineLog.getVehicleCode());
        response.put("发货人", offlineLog.getSendUser());
        response.put("发货人id", offlineLog.getSendUserCode());


        BusinessLogProfiler businessLogProfiler = new BusinessLogProfilerBuilder()
                .operateRequest(request)
                .operateResponse(response)
                .timeStamp(offlineLog.getCreateTime() == null ? new Date().getTime() : offlineLog.getCreateTime().getTime())
                .build();


        Integer taskType = offlineLog.getTaskType();

        if (taskType != null && Task.TASK_TYPE_RECEIVE.equals(taskType)) {
            // 分拣中心收货
            businessLogProfiler.setBizType(BizTypeConstants.RECEIVE);
            businessLogProfiler.setOperateType(OperateTypeConstants.RECEIVE);
        } else if (taskType != null && Task.TASK_TYPE_INSPECTION.equals(taskType)) {
            // 分拣中心验货
            businessLogProfiler.setBizType(BizTypeConstants.INSPECT);
            businessLogProfiler.setOperateType(OperateTypeConstants.SORTING_CENTER_INSPECT);
        } else if (taskType != null && Task.TASK_TYPE_SORTING.equals(taskType)) {
            // 分拣
            businessLogProfiler.setBizType(BizTypeConstants.SORTING);
            businessLogProfiler.setOperateType(OperateTypeConstants.OFFLINE_SORTING);
        } else if (taskType != null && Task.TASK_TYPE_SEND_DELIVERY.equals(taskType)) {
            // 发货
            businessLogProfiler.setBizType(BizOperateTypeConstants.DELIVERY_DELIVERY.getBizTypeCode());
            businessLogProfiler.setOperateType(BizOperateTypeConstants.DELIVERY_DELIVERY.getOperateTypeCode());
        } else if (taskType != null && Task.TASK_TYPE_ACARABILL_SEND_DELIVERY.equals(taskType)) {
            // 一车一单发货
            businessLogProfiler.setBizType(BizTypeConstants.DELIVERY);
            businessLogProfiler.setOperateType(OperateTypeConstants.ONECARDELIVERY);
        } else if (taskType != null && Task.TASK_TYPE_SEAL_BOX.equals(taskType)) {
            // 分拣封箱
            businessLogProfiler.setBizType(BizTypeConstants.SORTING);
            businessLogProfiler.setOperateType(OperateTypeConstants.SORTING_SEAL);
        } else if (taskType != null && Task.TASK_TYPE_OFFLINE_EXCEEDAREA.equals(taskType)) {
            // 三方超区退货
            businessLogProfiler.setBizType(BizTypeConstants.RETURNS);
            businessLogProfiler.setOperateType(OperateTypeConstants.OVER_AREA_RETURN);
        } else if (taskType != null && Task.TASK_TYPE_BOUNDARY.equals(taskType)) {
            // pop上门接货
            businessLogProfiler.setBizType(BizTypeConstants.RECEIVE);
            businessLogProfiler.setOperateType(OperateTypeConstants.POP_RECEIVE);
        }

        logEngine.addLog(businessLogProfiler);

        return offlineDao.add(OfflineDao.namespace, offlineLog);
    }

    @Override
    public Integer totalSizeByParams(Map<String, Object> params) {
        return offlineDao.totalSizeByParams(params);
    }

    @Override
    public List<OfflineLog> queryByParams(Map<String, Object> params) {
        return offlineDao.queryByParams(params);
    }

    @Override
    public OfflineLog findByObj(OfflineLog offlineLog) {
        return offlineDao.findByObj(offlineLog);
    }

    @Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public Integer update(OfflineLog offlineLog) {
        return offlineDao.updateById(offlineLog);
    }

}
