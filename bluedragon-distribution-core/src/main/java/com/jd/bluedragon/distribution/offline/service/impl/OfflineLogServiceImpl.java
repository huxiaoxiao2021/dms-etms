package com.jd.bluedragon.distribution.offline.service.impl;

import com.jd.bluedragon.Constants;

import com.jd.bluedragon.distribution.log.BusinessLogProfilerBuilder;
import com.jd.bluedragon.distribution.offline.dao.OfflineDao;
import com.jd.bluedragon.distribution.offline.domain.OfflineLog;
import com.jd.bluedragon.distribution.offline.service.OfflineLogService;
import com.jd.bluedragon.distribution.task.domain.Task;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.bluedragon.utils.log.BusinessLogConstans;
import com.jd.dms.logger.external.BusinessLogProfiler;
import com.jd.dms.logger.external.LogEngine;
import com.jd.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Map;


/**
 * 1.不要删除此类；删除前请确认类中 logEngine接口保存的日志是否还需要或者迁移走。
 *
 * 2.不要使用此接口保存日志了。请使用统一的日志日志接口com.jd.bluedragon.distribution.log.impl.LogEngineImpl。
 *  com.jd.bluedragon.distribution.log.impl.LogEngineImpl 此接口保存的日志会存储到business.log.jd.com 中;
 *
 */
@Service("offlineLogService")
@Deprecated
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


        BusinessLogProfiler businessLogProfiler = new BusinessLogProfilerBuilder(BusinessLogConstans.SourceSys.DMS_OPERATE)
                .operateRequest(request)
                .operateResponse(response)
                .timeStamp(offlineLog.getCreateTime() == null ? new Date().getTime() : offlineLog.getCreateTime().getTime())
                .build();


        Integer taskType = offlineLog.getTaskType();

        if (taskType != null && Task.TASK_TYPE_RECEIVE.equals(taskType)) {
            // 分拣中心收货
            businessLogProfiler.setBizType(BusinessLogConstans.OperateTypeEnum.RECEIVE_RECEIVE_OFFLINE.getBizTypeCode());
            businessLogProfiler.setOperateType(BusinessLogConstans.OperateTypeEnum.RECEIVE_RECEIVE_OFFLINE.getCode());
        } else if (taskType != null && Task.TASK_TYPE_INSPECTION.equals(taskType)) {
            // 分拣中心验货
            businessLogProfiler.setBizType(BusinessLogConstans.OperateTypeEnum.INSPECTION_SORT_CENTER_INSPECTION_OFFLINE.getBizTypeCode());
            businessLogProfiler.setOperateType(BusinessLogConstans.OperateTypeEnum.INSPECTION_SORT_CENTER_INSPECTION_OFFLINE.getCode());
        } else if (taskType != null && Task.TASK_TYPE_SORTING.equals(taskType)) {
            // 分拣
            businessLogProfiler.setBizType(BusinessLogConstans.OperateTypeEnum.SORTING_OFFLINE_SORTING.getBizTypeCode());
            businessLogProfiler.setOperateType(BusinessLogConstans.OperateTypeEnum.SORTING_OFFLINE_SORTING.getCode());
        } else if (taskType != null && Task.TASK_TYPE_SEND_DELIVERY.equals(taskType)) {
            // 发货
            businessLogProfiler.setBizType(BusinessLogConstans.OperateTypeEnum.SEND_OFFLINE.getBizTypeCode());
            businessLogProfiler.setOperateType(BusinessLogConstans.OperateTypeEnum.SEND_OFFLINE.getCode());
        } else if (taskType != null && Task.TASK_TYPE_ACARABILL_SEND_DELIVERY.equals(taskType)) {
            // 一车一单发货
            businessLogProfiler.setBizType(BusinessLogConstans.OperateTypeEnum.SEND_ONECAR_OFFLINE.getBizTypeCode());
            businessLogProfiler.setOperateType(BusinessLogConstans.OperateTypeEnum.SEND_ONECAR_OFFLINE.getCode());
        } else if (taskType != null && Task.TASK_TYPE_SEAL_BOX.equals(taskType)) {
            // 分拣封箱
            businessLogProfiler.setBizType(BusinessLogConstans.OperateTypeEnum.SORTING_SORTING_SEAL_OFFLINE.getBizTypeCode());
            businessLogProfiler.setOperateType(BusinessLogConstans.OperateTypeEnum.SORTING_SORTING_SEAL_OFFLINE.getCode());
        } else if (taskType != null && Task.TASK_TYPE_OFFLINE_EXCEEDAREA.equals(taskType)) {
            // 三方超区退货
            businessLogProfiler.setBizType(BusinessLogConstans.OperateTypeEnum.RETURNS_OVER_AREA_RETURN_OFFLINE.getBizTypeCode());
            businessLogProfiler.setOperateType(BusinessLogConstans.OperateTypeEnum.RETURNS_OVER_AREA_RETURN_OFFLINE.getCode());
        } else if (taskType != null && Task.TASK_TYPE_BOUNDARY.equals(taskType)) {
            // pop上门接货
            businessLogProfiler.setBizType(BusinessLogConstans.OperateTypeEnum.RECEIVE_POP_RECEIVE.getBizTypeCode());
            businessLogProfiler.setOperateType(BusinessLogConstans.OperateTypeEnum.RECEIVE_POP_RECEIVE.getCode());
        }else {
            businessLogProfiler.setBizType(BusinessLogConstans.OperateTypeEnum.OTHER_OTHER_OFFLINE.getBizTypeCode());
            businessLogProfiler.setOperateType(BusinessLogConstans.OperateTypeEnum.OTHER_OTHER_OFFLINE.getCode());
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
