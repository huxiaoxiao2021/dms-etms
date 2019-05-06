package com.jd.bluedragon.distribution.reverse.service;

import com.jd.bluedragon.distribution.api.request.RejectRequest;
import com.jd.bluedragon.distribution.api.utils.JsonHelper;
import com.jd.bluedragon.distribution.operationLog.domain.OperationLog;
import com.jd.bluedragon.distribution.operationLog.service.OperationLogService;
import com.jd.bluedragon.distribution.reverse.dao.ReverseRejectDao;
import com.jd.bluedragon.distribution.reverse.domain.ReverseReject;
import com.jd.bluedragon.distribution.task.domain.Task;
import com.jd.bluedragon.distribution.task.service.TaskService;
import com.jd.bluedragon.distribution.waybill.domain.Pickware;
import com.jd.bluedragon.distribution.waybill.domain.WaybillStatus;
import com.jd.bluedragon.distribution.waybill.service.PickwareService;
import com.jd.bluedragon.dms.utils.WaybillUtil;
import com.jd.bluedragon.utils.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service("reverseRejectService")
public class ReverseRejectServiceImpl implements ReverseRejectService {
    
    private final Log logger = LogFactory.getLog(this.getClass());
    
    @Autowired
    private TaskService taskService;
    
    @Autowired
    private ReverseRejectDao reverseRejectDao;
    
    @Autowired
    private PickwareService pickwareService;
    
    @Autowired
    private OperationLogService operationLogService;
    
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public Integer add(ReverseReject reverseReject) {
        return this.reverseRejectDao.add(ReverseRejectDao.namespace, reverseReject);
    }
    
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public Integer update(ReverseReject reverseReject) {
        return this.reverseRejectDao.update(ReverseRejectDao.namespace, reverseReject);
    }
    
    public ReverseReject get(Integer businessType, String orderId, String packageCode) {
        ReverseReject reverseReject = new ReverseReject();
        reverseReject.setBusinessType(businessType);
        reverseReject.setOrderId(orderId);
        reverseReject.setPackageCode(packageCode);
        return this.reverseRejectDao.get(reverseReject);
    }
    
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public void reject(ReverseReject source) {
        if (!this.check(source)) {
            return;
        }
        
        ReverseReject reverseRejectPO = this.getReverseReject(source.getBusinessType(),
                source.getOrderId(), source.getPackageCode());
        if (reverseRejectPO == null) {
            if (this.isAms(source.getBusinessType())) {
                this.appentPickwareInfo(source, source.getPackageCode());
            }
            
            this.add(source);
            this.addOpetationLog(source, this.getRejectLogType(source.getBusinessType()));
        } else {
            ReverseReject reverseRejectVO = new ReverseReject();
            BeanHelper.copyProperties(reverseRejectVO, source);
            reverseRejectVO.setId(reverseRejectPO.getId());
            reverseRejectVO.setBusinessType(reverseRejectPO.getBusinessType());
            reverseRejectVO.setOperateTime(source.getOperateTime());
            
            if (this.isAms(source.getBusinessType())) {
                this.appentPickwareInfo(reverseRejectVO, source.getPackageCode());
            }
            
            this.update(reverseRejectVO);
            this.addOpetationLog(reverseRejectVO, this.getRejectLogType(source.getBusinessType()));
        }
    }
    
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public void rejectInspect(Task task) {
        String body = task.getBody().substring(1, task.getBody().length() - 1);
        RejectRequest request = JsonHelper.fromJson(body, RejectRequest.class);
        
        if (!this.check(task, request)) {
            return;
        }
        
        ReverseReject reverseRejectPO = this.getReverseReject(null,
                WaybillUtil.getWaybillCode(request.getPackageCode()),
                request.getPackageCode());
        
        if (reverseRejectPO == null) {
            ReverseReject reverseRejectVO = new ReverseReject();
            this.setReverseReject(reverseRejectVO, request);
            this.add(reverseRejectVO);
            this.addOpetationLog(reverseRejectVO,
                    this.getRejectInspectLogType(request.getBusinessType()));
            sendTask(reverseRejectVO);
        } else {
            ReverseReject reverseRejectVO = new ReverseReject();
            this.setReverseReject(reverseRejectVO, request);
            reverseRejectVO.setId(reverseRejectPO.getId());
            reverseRejectVO.setBusinessType(reverseRejectPO.getBusinessType());
            reverseRejectVO.setOperateTime(DateHelper.parseDateTime(request.getOperateTime()));
            this.update(reverseRejectVO);
            this.addOpetationLog(reverseRejectVO,
                    this.getRejectInspectLogType(request.getBusinessType()));
            sendTask(reverseRejectVO);
        }
        
        this.taskService.doDone(task);
    }
    
	private void sendTask(ReverseReject reverseRejectVO) {
		WaybillStatus tWaybillStatus = new WaybillStatus();
		tWaybillStatus.setOperator(reverseRejectVO.getInspector());
		tWaybillStatus.setOperateTime(reverseRejectVO.getInspectTime());
		tWaybillStatus.setWaybillCode(reverseRejectVO.getOrderId());
		tWaybillStatus.setCreateSiteCode(reverseRejectVO.getCreateSiteCode());
		tWaybillStatus.setCreateSiteName(reverseRejectVO.getCreateSiteName());
		tWaybillStatus.setOperateType(WaybillStatus.WAYBILL_TRACK_BHS);
		taskService.add(this.toTask(tWaybillStatus));
	}
	
	private Task toTask(WaybillStatus tWaybillStatus) {
		Task task = new Task();
		task.setTableName(Task.TABLE_NAME_POP);
		task.setSequenceName(Task.getSequenceName(task.getTableName()));
		task.setKeyword2(String.valueOf(tWaybillStatus.getOperateType()));
		task.setKeyword1(tWaybillStatus.getWaybillCode());
		task.setCreateSiteCode(tWaybillStatus.getCreateSiteCode());
		task.setBody(JsonHelper.toJson(tWaybillStatus));
		task.setType(Task.TASK_TYPE_WAYBILL_TRACK);
		task.setOwnSign(BusinessHelper.getOwnSign());
		StringBuffer fingerprint = new StringBuffer();
		fingerprint
				.append(tWaybillStatus.getCreateSiteCode())
				.append("_")
				.append((tWaybillStatus.getReceiveSiteCode() == null ? "-1"
						: tWaybillStatus.getReceiveSiteCode())).append("_")
				.append(tWaybillStatus.getOperateType()).append("_")
				.append(tWaybillStatus.getWaybillCode()).append("_")
				.append(tWaybillStatus.getOperateTime());
        task.setFingerprint(Md5Helper.encode(fingerprint.toString()));
		return task;
	}
    
    private ReverseReject getReverseReject(Integer businessType, String orderId, String packageCode) {
        ReverseReject reverseRejectPO = null;
        if (this.isAms(businessType)) {
            reverseRejectPO = this.get(businessType, null, packageCode);
        } else if (this.isWms(businessType) || this.isSpwms(businessType)) {
            reverseRejectPO = this.get(businessType, orderId, null);
        } else {
            reverseRejectPO = this.get(null, orderId, null);
        }
        return reverseRejectPO;
    }
    
    private void appentPickwareInfo(ReverseReject reverseReject, String code) {
        Pickware pickware = this.pickwareService.get(code);
        if (pickware != null) {
            reverseReject.setOrderId(pickware.getWaybillCode());
            reverseReject.setPickwareCode(pickware.getCode());
        }
    }
    
    public void addOpetationLog(ReverseReject reverseReject, Integer logType) {
        this.appentPickwareInfo(reverseReject, reverseReject.getPackageCode());
        OperationLog operationLog = new OperationLog();
        operationLog.setWaybillCode(reverseReject.getOrderId());
        operationLog.setPickupCode(reverseReject.getPickwareCode());
        operationLog.setPackageCode(reverseReject.getPackageCode());
        operationLog.setCreateSiteCode(reverseReject.getCreateSiteCode());
        operationLog.setCreateSiteName(reverseReject.getCreateSiteName());
        operationLog.setReceiveSiteCode(reverseReject.getOrgId());
        operationLog.setOperateTime(reverseReject.getOperateTime());
        operationLog.setPickupCode(reverseReject.getPickwareCode());
        if (this.isReject(logType)) {
            operationLog.setCreateUser(reverseReject.getOperator());
        } else if (this.isRejectInspect(logType)) {
            operationLog.setCreateUser(reverseReject.getInspector());
//            operationLog.setCreateUserCode(new Integer(reverseReject.getInspectorCode()));
        }
        
        operationLog.setLogType(logType);
        this.operationLogService.add(operationLog);
    }
    
    private Boolean check(ReverseReject source) {
        if (!NumberHelper.isPositiveNumber(source.getBusinessType())) {
            this.logger.info("数据不合法.");
            return Boolean.FALSE;
        } else if (this.isAms(source.getBusinessType())
                && StringHelper.isEmpty(source.getPackageCode())) {
            this.logger.info("数据不合法.");
            return Boolean.FALSE;
        } else if (this.isWms(source.getBusinessType())
                && StringHelper.isEmpty(source.getOrderId())) {
            this.logger.info("数据不合法.");
            return Boolean.FALSE;
        } else if (this.isSpwms(source.getBusinessType())
                && StringHelper.isEmpty(source.getOrderId())) {
            this.logger.info("数据不合法.");
            return Boolean.FALSE;
        }
        return Boolean.TRUE;
    }
    
    private Boolean check(Task task, RejectRequest request) {
        if (!NumberHelper.isPositiveNumber(request.getBusinessType())
                || StringHelper.isEmpty(request.getPackageCode())) {
            this.logger.info("数据不合法.");
            this.taskService.doError(task);
            return Boolean.FALSE;
        }
        
        return Boolean.TRUE;
    }
    
    private void setReverseReject(ReverseReject reverseReject, RejectRequest request) {
        BeanHelper.copyProperties(reverseReject, request);
        
        if (NumberHelper.isPositiveNumber(request.getSiteCode())) {
            reverseReject.setCreateSiteCode(request.getSiteCode());
        }
        
        if (StringHelper.isNotEmpty(request.getSiteName())) {
            reverseReject.setCreateSiteName(request.getSiteName());
        }
        
        if (NumberHelper.isPositiveNumber(request.getUserCode())) {
            reverseReject.setInspectorCode(String.valueOf(request.getUserCode()));
        }
        
        if (StringHelper.isNotEmpty(request.getUserName())) {
            reverseReject.setInspector(request.getUserName());
        }
        
        if (StringHelper.isNotEmpty(request.getOperateTime())) {
            reverseReject.setInspectTime(DateHelper.parseDateTime(request.getOperateTime()));
        }
        
        if (WaybillUtil.isPackageCode(request.getPackageCode())) {
            reverseReject.setPackageCode(request.getPackageCode());
            reverseReject.setOrderId(WaybillUtil.getWaybillCode(request.getPackageCode()));
        }
        
        if (WaybillUtil.isWaybillCode(request.getPackageCode())) {
            reverseReject.setOrderId(request.getPackageCode());
        }
    }
    
    private Boolean isSpwms(Integer businessType) {
        return ReverseReject.BUSINESS_TYPE_SPWMS.equals(businessType);
    }
    
    private Boolean isWms(Integer businessType) {
        return ReverseReject.BUSINESS_TYPE_WMS.equals(businessType);
    }
    
    private Boolean isAms(Integer businessType) {
        return ReverseReject.BUSINESS_TYPE_AMS.equals(businessType);
    }
    
    private Integer getRejectLogType(Integer businessType) {
        if (this.isAms(businessType)) {
            return OperationLog.TYPE_REVERSE_AMS_REJECT;
        }
        if (this.isWms(businessType)) {
            return OperationLog.TYPE_REVERSE_WMS_REJECT;
        }
        if (this.isSpwms(businessType)) {
            return OperationLog.TYPE_REVERSE_SPWMS_REJECT;
        } else {
            return null;
        }
    }
    
    private Integer getRejectInspectLogType(Integer businessType) {
        if (this.isAms(businessType)) {
            return OperationLog.TYPE_REVERSE_AMS_REJECT_INSPECT;
        }
        if (this.isWms(businessType)) {
            return OperationLog.TYPE_REVERSE_WMS_REJECT_INSPECT;
        }
        if (this.isSpwms(businessType)) {
            return OperationLog.TYPE_REVERSE_SPWMS_REJECT_INSPECT;
        } else {
            return null;
        }
    }
    
    private boolean isRejectInspect(Integer logType) {
        return OperationLog.TYPE_REVERSE_AMS_REJECT_INSPECT.equals(logType)
                || OperationLog.TYPE_REVERSE_WMS_REJECT_INSPECT.equals(logType)
                || OperationLog.TYPE_REVERSE_SPWMS_REJECT_INSPECT.equals(logType);
    }
    
    private boolean isReject(Integer logType) {
        return OperationLog.TYPE_REVERSE_AMS_REJECT.equals(logType)
                || OperationLog.TYPE_REVERSE_WMS_REJECT.equals(logType)
                || OperationLog.TYPE_REVERSE_SPWMS_REJECT.equals(logType);
    }
    
}
