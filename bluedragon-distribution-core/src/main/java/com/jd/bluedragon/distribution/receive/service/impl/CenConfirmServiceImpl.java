package com.jd.bluedragon.distribution.receive.service.impl;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.distribution.base.service.BaseService;
import com.jd.bluedragon.distribution.inspection.domain.Inspection;
import com.jd.bluedragon.distribution.inspection.domain.InspectionMQBody;
import com.jd.bluedragon.distribution.inspection.service.InspectionNotifyService;
import com.jd.bluedragon.distribution.receive.dao.CenConfirmDao;
import com.jd.bluedragon.distribution.receive.domain.CenConfirm;
import com.jd.bluedragon.distribution.receive.domain.Receive;
import com.jd.bluedragon.distribution.receive.service.CenConfirmService;
import com.jd.bluedragon.distribution.send.domain.SendDetail;
import com.jd.bluedragon.distribution.send.service.DeliveryService;
import com.jd.bluedragon.distribution.task.domain.Task;
import com.jd.bluedragon.distribution.task.service.TaskService;
import com.jd.bluedragon.distribution.waybill.domain.WaybillStatus;
import com.jd.bluedragon.dms.utils.WaybillUtil;
import com.jd.bluedragon.utils.*;
import com.jd.etms.waybill.api.WaybillPickupTaskApi;
import com.jd.etms.waybill.domain.BaseEntity;
import com.jd.etms.waybill.domain.PickupTask;
import com.jd.ql.basic.dto.BaseStaffSiteOrgDto;
import com.jd.ump.annotation.JProfiler;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service("cenConfirmService")
public class CenConfirmServiceImpl implements CenConfirmService {

	private Logger log = LoggerFactory.getLogger(CenConfirmServiceImpl.class);

	@Autowired
	private CenConfirmDao cenConfirmDao;

	@Autowired
	private WaybillPickupTaskApi waybillPickupTaskApi;

	@Autowired
	private DeliveryService deliveryService;

	@Autowired
	private BaseService baseService;

	@Autowired
	private TaskService taskService;

    @Autowired
    private InspectionNotifyService inspectionNotifyService;
	
	@Override
	public CenConfirm createCenConfirmByReceive(Receive receive) {
		return createCenConfirm(null, receive);
	}

	@Override
	public CenConfirm createCenConfirmByInspection(Inspection inspection) {
		return createCenConfirm(inspection, null);
	}


    public  void updateOrInsert(CenConfirm cenConfirm){
        if (Constants.NO_MATCH_DATA == cenConfirmDao
                .updateFillField(cenConfirm)) {
            cenConfirmDao.add(CenConfirmDao.namespace, cenConfirm);// 不存在添加
        }
    }

	public void saveOrUpdateCenConfirm(CenConfirm cenConfirm) {
        //取消同步方法，取消事务（上层调用方已经设置事务为required）

        InspectionMQBody inspectionMQBody=new InspectionMQBody();
        inspectionMQBody.setWaybillCode(null!=cenConfirm.getWaybillCode()?cenConfirm.getWaybillCode(): SerialRuleUtil.getWaybillCode(cenConfirm.getPackageBarcode()));
        inspectionMQBody.setInspectionSiteCode(cenConfirm.getCreateSiteCode());
		inspectionMQBody.setCreateUserCode(cenConfirm.getInspectionUserCode());
		inspectionMQBody.setCreateUserName(cenConfirm.getInspectionUser());
		inspectionMQBody.setOperateTime(null != cenConfirm.getInspectionTime() ?cenConfirm.getInspectionTime() : new Date());
        try {
            /**
             * fix wtw 任务监控
             */
            inspectionNotifyService.send(inspectionMQBody);
        }catch (Throwable throwable){
            log.error("推送验货MQ异常,WaybillCode:{}",inspectionMQBody.getWaybillCode(),throwable);
        }
		if (Constants.BUSSINESS_TYPE_POSITIVE == cenConfirm.getType()
				|| Constants.BUSSINESS_TYPE_REVERSE == cenConfirm.getType()) {
			if (WaybillUtil.isSurfaceCode(cenConfirm.getPackageBarcode())) {
				cenConfirm = fillPickupCode(cenConfirm);// 根据取件单序列号获取取件单号和运单号
                /**
                 * fix wtw 外部接口包装，及UMP
                 */
				cenConfirm.setOperateType(Constants.PICKUP_OPERATE_TYPE);
			} else {
				cenConfirm = fillOperateType(cenConfirm);// 根据运单号调用运单接口判断操作类型
			}
		}else if(Constants.BUSSINESS_TYPE_SITE==cenConfirm.getType()){
			cenConfirm.setOperateType(Constants.OPERATE_TYPE_PSY);
		}else if(Constants.BUSSINESS_TYPE_InFactory==cenConfirm.getType()){
            cenConfirm.setOperateType(Constants.OPERATE_TYPE_In);
        }
		if (Constants.NO_MATCH_DATA == cenConfirmDao
				.updateFillField(cenConfirm)) {
            /**
             * Fix wtw
             */

			cenConfirmDao.add(CenConfirmDao.namespace, cenConfirm);// 不存在添加
			// 插入回传运单状态任务表/全程跟踪任务表
			syncWaybillStatusTask(cenConfirm);
		}
	}

	private CenConfirm createCenConfirm(Inspection inspection, Receive receive) {
		CenConfirm cenConfirm = new CenConfirm();
		if (inspection == null && receive == null) {
			return cenConfirm;
		} else if (inspection != null) {
			cenConfirm
					.setWaybillCode(((inspection.getWaybillCode() == null) ? "-1"
							: inspection.getWaybillCode()));
			cenConfirm.setBoxCode(inspection.getBoxCode());
			cenConfirm
					.setPackageBarcode(((inspection.getPackageBarcode() == null) ? "-1"
							: inspection.getPackageBarcode()));
			cenConfirm.setType(inspection.getInspectionType().shortValue());
			cenConfirm.setCreateTime(inspection.getCreateTime());// 以后不会更新
			cenConfirm.setUpdateTime(inspection.getUpdateTime());// 以后更新取系统时间
			cenConfirm.setThirdWaybillCode(inspection.getThirdWaybillCode());
			cenConfirm.setReceiveSiteCode(inspection.getReceiveSiteCode());
			cenConfirm.setInspectionUser(inspection.getCreateUser());
			cenConfirm.setInspectionUserCode(inspection.getCreateUserCode());
			cenConfirm.setInspectionTime(inspection.getCreateTime());
			cenConfirm.setCreateSiteCode(inspection.getCreateSiteCode());
			// 操作人/ID/时间 每次都更新
			cenConfirm.setOperateTime(inspection.getOperateTime());
			cenConfirm.setOperateUser(inspection.getUpdateUser());
			cenConfirm.setOperateUserCode(inspection.getUpdateUserCode());

			if (Constants.BUSSINESS_TYPE_POP == cenConfirm.getType().intValue()) {
				cenConfirm.setOperateType(Constants.POP_HANDOVER_OPERATE_TYPE);
			}
            else if (Constants.BUSSINESS_TYPE_InFactory == cenConfirm.getType().intValue())
            {
                cenConfirm.setOperateType(Constants.OPERATE_TYPE_In);
            }
            else if (Constants.BUSSINESS_TYPE_SITE == cenConfirm
					.getType().intValue()) {
				cenConfirm.setOperateType(Constants.OPERATE_TYPE_PSY);
			} else if (Constants.BUSSINESS_TYPE_TRANSFER == cenConfirm
					.getType().intValue()) {
				cenConfirm
						.setOperateType(Constants.WAREHOUSE_HANDOVER_OPERATE_TYPE);
			} else if (Constants.BUSSINESS_TYPE_THIRD_PARTY == cenConfirm
					.getType().intValue()) {
				cenConfirm
						.setOperateType(Constants.PARTNER_INSPECTION_OPERATE_TYPE);
			} else if (Constants.BUSSINESS_TYPE_BDB == cenConfirm.getType()
					.intValue()) {
				cenConfirm.setOperateType(Constants.OPERATE_TYPE_BDB);
			} else if (Constants.BUSSINESS_TYPE_OEM == cenConfirm.getType()
					.intValue()) {
				cenConfirm.setOperateType(Constants.OPERATE_TYPE_OEM);
			}else if(Constants.BUSSINESS_TYPE_FC == cenConfirm.getType()
					.intValue()){
				cenConfirm.setOperateType(Constants.OPERATE_TYPE_FC);
			}else if(Constants.BUSSINESS_TYPE_REVERSE == cenConfirm.getType()
					.intValue()){
				cenConfirm.setExceptionType(inspection.getExceptionType());
			}
		} else {
			// 收货表中只抽取大件包裹数据
			cenConfirm
					.setWaybillCode(((receive.getWaybillCode() == null) ? "-1"
                            : receive.getWaybillCode()));
			cenConfirm.setBoxCode(receive.getBoxCode());
			cenConfirm
					.setPackageBarcode(((receive.getPackageBarcode() == null) ? "-1"
                            : receive.getPackageBarcode()));
			cenConfirm.setType(receive.getReceiveType());
			cenConfirm.setCreateTime(receive.getCreateTime());
			cenConfirm.setUpdateTime(receive.getUpdateTime());

			cenConfirm.setReceiveUser(receive.getCreateUser());
			cenConfirm.setReceiveUserCode(receive.getCreateUserCode());
			cenConfirm.setReceiveTime(receive.getCreateTime());
			cenConfirm.setCreateSiteCode(receive.getCreateSiteCode());
			// 操作人/ID/时间 每次都更新
			cenConfirm.setOperateTime(receive.getCreateTime());
			cenConfirm.setOperateUser(receive.getCreateUser());
			cenConfirm.setOperateUserCode(receive.getCreateUserCode());
			cenConfirm.setInspectionTime(receive.getCreateTime());
		}
		return cenConfirm;
	}

    @JProfiler(jKey = "CenConfirmServiceImpl.fillPickupCode")
	public CenConfirm fillPickupCode(CenConfirm cenConfirm) {
		BaseEntity<PickupTask> baseEntity = waybillPickupTaskApi
				.getDataBySfCode(cenConfirm.getPackageBarcode());
		if (baseEntity.getResultCode() == Constants.INTERFACE_CALL_SUCCESS) {
			PickupTask ptask = baseEntity.getData();
			if (ptask != null) {
				String pickupCode = ptask.getPickupCode();
				String waybillCode = ptask.getOldWaybillCode();
				cenConfirm.setPickupCode(pickupCode);
				cenConfirm.setWaybillCode(waybillCode);
				log.info("调用获取取件单号接口成功:取件单序列号={}取件单号={}运单号={}",
						cenConfirm.getPackageBarcode(),pickupCode,waybillCode);
				return cenConfirm;
			} else {
				log.warn("调用获取取件单号接口失败(返回data=null):取件单序列号={}:{}",
						cenConfirm.getPackageBarcode(), baseEntity.getMessage());
			}
		} else {
			log.warn("获取[取件单号]接口失败:取件单序列号={}:{}",
					cenConfirm.getPackageBarcode(), baseEntity.getMessage());
		}
		return cenConfirm;
	}

	@Override
	public List<CenConfirm> queryHandoverInfo(CenConfirm cenConfirm) {
		if(cenConfirm.getCreateSiteCode()==null){
			log.warn("[CenConfirmServiceImpl.queryHandoverInfo]create_site_code为null");
			return null;
		}
		return cenConfirmDao.queryHandoverInfo(cenConfirm);
	}

	/**
	 * W +10位流水号=取件 站点类型=64&&业务类型=10(正向)是跨分拣中心收货 站点类型=16&&业务类型=20(逆向)是三方退货
	 * 站点类型!=64&&业务类型=10(正向)是返调度在投 站点类型!=16&&业务类型=20(逆向)是站点退货
	 * 操作类型(1.跨分拣中心收货2.库房交接3.pop交接4. 三方验货5.返调度再投6.取件7.站点退货8.三方退货 9.夺宝岛 10.OEM)
	 */
	public CenConfirm fillOperateType(CenConfirm cenConfirm) {

		//当前操作验货的站点编号，作为计算操作类型的依据
		Integer cenConfirmSiteCode = cenConfirm.getCreateSiteCode();
		BaseStaffSiteOrgDto bDto = baseService.getSiteBySiteID(cenConfirmSiteCode);
		if (bDto == null) {
			log.warn("fillOperateType------>根据[SiteCode={}]获取基础资料[站点类型]baseService.getSiteBySiteID返回null",cenConfirmSiteCode);
			cenConfirm.setOperateType(-3);// 失败(根据目的站点调用基础资料获取站点类型返回null)
			return cenConfirm;
		} else {
			if (bDto.getSiteType() != null) {
				return setOperateTypeByWaybill(cenConfirm, bDto.getSiteType());
			} else {
				log.warn("fillOperateType------>[SiteCode={}]获取基础资料baseService.getSiteBySiteID返回对象bDto.getSiteType()=null",cenConfirmSiteCode);
				cenConfirm.setOperateType(-4);// 失败(根据目的站点调用基础资料获取站点类型返回对象bDto.getSiteType()=null)
				return cenConfirm;
			}
		}
	}

	private CenConfirm setOperateTypeByWaybill(CenConfirm cenConfirm,
			int siteType) {
		if (siteType == Constants.RETURN_PARTNER_SITE_TYPE
				&& cenConfirm.getType().intValue() == Constants.BUSSINESS_TYPE_REVERSE) {
			cenConfirm.setOperateType(Constants.RETURN_PARTNER_OPERATE_TYPE);// 三方退货
		} else if (siteType != Constants.TRANS_SORTING_SITE_TYPE
				&& siteType != Constants.TRANS_SORTING_SITE_TYPE_SECOND
				&& cenConfirm.getType().intValue() == Constants.BUSSINESS_TYPE_POSITIVE) {
			cenConfirm.setOperateType(Constants.RETURN_SCHEDULING_OPERATE_TYPE);// 返调度再投
		} else if (siteType != Constants.RETURN_PARTNER_SITE_TYPE
				&& cenConfirm.getType().intValue() == Constants.BUSSINESS_TYPE_REVERSE) {
			cenConfirm.setOperateType(Constants.SITE_RETURN_OPERATE_TYPE);// 站点退货
		} else if ((siteType == Constants.TRANS_SORTING_SITE_TYPE || siteType == Constants.TRANS_SORTING_SITE_TYPE_SECOND)
				&& cenConfirm.getType().intValue() == Constants.BUSSINESS_TYPE_POSITIVE) {
			cenConfirm.setOperateType(Constants.TRANS_SORTING_OPERATE_TYPE);// 跨分拣中心收货
		} else {
			log.warn("[收货确认ID={}]的数据设置[OPERATE_TYPE]失败", cenConfirm.getConfirmId());
			// 设置默认值
			cenConfirm.setOperateType(-1);// 根据站点类型匹配异常
		}
		return cenConfirm;
	}
	public void syncWaybillStatusTask(CenConfirm cenConfirm) {
		BaseStaffSiteOrgDto bDto = baseService.getSiteBySiteID(cenConfirm.getCreateSiteCode());
		BaseStaffSiteOrgDto rDto = null;
        //cenConfirm.setReceiveSiteCode(39);
		String message = getTipsMessage(cenConfirm);
		if (cenConfirm.getReceiveSiteCode() != null&&Constants.BUSSINESS_TYPE_FC!=cenConfirm.getType().intValue()) {
			rDto = baseService.getSiteBySiteID(cenConfirm.getReceiveSiteCode());
		}
		if (bDto == null) {
			log.warn("[PackageBarcode={}]根据[siteCode={}]获取基础资料站点信息[getSiteBySiteID]返回null,不再插入{}",
					cenConfirm.getPackageBarcode(),cenConfirm.getCreateSiteCode(),message);
		} else {
			WaybillStatus tWaybillStatus = createWaybillStatus(cenConfirm,
					bDto, rDto);
			if (checkFormat(tWaybillStatus, cenConfirm.getType())) {
				// 添加到task表
				taskService.add(this.toTask(tWaybillStatus, cenConfirm.getOperateType()));
			} else {
				log.warn("[PackageCode={} WaybillCode={}][参数信息不全],不再插入{}",
						tWaybillStatus.getPackageCode(), tWaybillStatus.getWaybillCode(), message);
			}

		}
	}

	public String getTipsMessage(CenConfirm cenConfirm) {
		String message="回传运单状态任务表";
		if(Constants.BUSSINESS_TYPE_FC==cenConfirm.getType().intValue()||
				Constants.BUSSINESS_TYPE_RCD==cenConfirm.getType().intValue()){
			message="回传全程跟踪任务表";
		}
		return message;
	}

	public WaybillStatus createWaybillStatus(CenConfirm cenConfirm,
			BaseStaffSiteOrgDto bDto, BaseStaffSiteOrgDto rDto) {
		WaybillStatus tWaybillStatus = createBasicWaybillStatus(cenConfirm,
				bDto, rDto);
		setOperateType(cenConfirm, tWaybillStatus);
		// 设置POP包裹号为运单号
		if (Constants.BUSSINESS_TYPE_POP == cenConfirm.getType() || Constants.BUSSINESS_TYPE_SITE == cenConfirm.getType()|| Constants.BUSSINESS_TYPE_InFactory == cenConfirm.getType()) {
			if (StringUtils.isBlank(tWaybillStatus.getPackageCode()) || "-1".equals(tWaybillStatus.getPackageCode())) {
				tWaybillStatus.setPackageCode(tWaybillStatus.getWaybillCode());
			}
		}
		return tWaybillStatus;
	}

	public WaybillStatus createBasicWaybillStatus(CenConfirm cenConfirm,
			BaseStaffSiteOrgDto bDto, BaseStaffSiteOrgDto rDto) {
		WaybillStatus tWaybillStatus = new WaybillStatus();
		tWaybillStatus.setPackageCode(cenConfirm.getPackageBarcode());
		
		//设置站点相关属性
		tWaybillStatus = setSiteRelated(tWaybillStatus,cenConfirm,bDto,rDto);
		
		tWaybillStatus.setOperatorId(cenConfirm.getOperateUserCode());
		tWaybillStatus.setOperateTime(cenConfirm.getOperateTime());
		tWaybillStatus.setOperator(cenConfirm.getOperateUser());
		tWaybillStatus.setOrgId(bDto.getOrgId());
		tWaybillStatus.setOrgName(bDto.getOrgName());
		tWaybillStatus.setWaybillCode(cenConfirm.getWaybillCode());
		tWaybillStatus.setBoxCode(cenConfirm.getBoxCode());
		tWaybillStatus.setRemark(cenConfirm.getExceptionType());
		
		return tWaybillStatus;
	}
	
	private WaybillStatus setOperateType(CenConfirm cenConfirm,
			WaybillStatus tWaybillStatus) {
		if (Constants.BUSSINESS_TYPE_POSITIVE == cenConfirm.getType()
				.intValue()
				|| Constants.BUSSINESS_TYPE_TRANSFER == cenConfirm
						.getType().intValue()
				|| Constants.BUSSINESS_TYPE_BDB == cenConfirm.getType()
						.intValue()
				|| Constants.BUSSINESS_TYPE_OEM == cenConfirm.getType()
						.intValue()
				|| Constants.BUSSINESS_TYPE_POP == cenConfirm.getType()
						.intValue()
				||Constants.BUSSINESS_TYPE_SITE==cenConfirm.getType()
				        .intValue()) {
			tWaybillStatus
					.setOperateType(WaybillStatus.WAYBILL_STATUS_CODE_FORWARD_INSPECTION);
		} else if (Constants.BUSSINESS_TYPE_REVERSE == cenConfirm.getType().intValue()) {
			tWaybillStatus
					.setOperateType(WaybillStatus.WAYBILL_STATUS_CODE_REVERSE_INSPECTION);
		} else if (Constants.BUSSINESS_TYPE_THIRD_PARTY == cenConfirm
				.getType().intValue()) {
			tWaybillStatus
					.setOperateType(WaybillStatus.WAYBILL_STATUS_CODE_FORWARD_INSPECTION_THIRDPARTY);
		}else if(Constants.BUSSINESS_TYPE_FC == cenConfirm
				.getType().intValue()){
			tWaybillStatus
			.setOperateType(WaybillStatus.WAYBILL_TRACK_FC);
		}else if(Constants.BUSSINESS_TYPE_RCD == cenConfirm
				.getType().intValue()){
			tWaybillStatus
			.setOperateType(WaybillStatus.WAYBILL_TRACK_RCD);
		}else if(Constants.BUSSINESS_TYPE_InFactory == cenConfirm
                .getType().intValue()){
            tWaybillStatus
                    .setOperateType(WaybillStatus.WAYBILL_STATUS_CODE_POP_InFactory);
        }
		return tWaybillStatus;
	}

	/**
	 * 设置站点相关属性
	 * @param tWaybillStatus
	 * @param cenConfirm
	 * @param bDto
	 * @param rDto
	 * @return
	 */
	private WaybillStatus setSiteRelated(WaybillStatus tWaybillStatus,
			CenConfirm cenConfirm, BaseStaffSiteOrgDto bDto, BaseStaffSiteOrgDto rDto) {
		//如果是三方，则createSiteCode和receiveSiteCode互换
		//业务方要求，全程跟踪时操作单位 显示为 三方人员所属的三方物流单位，date:20/08/2013
		if(Constants.BUSSINESS_TYPE_THIRD_PARTY == cenConfirm.getType().intValue()){
			tWaybillStatus.setCreateSiteCode(cenConfirm.getReceiveSiteCode());
			tWaybillStatus.setCreateSiteName(rDto.getSiteName());
			tWaybillStatus.setCreateSiteType(rDto.getSiteType());
		}else{
			tWaybillStatus.setCreateSiteCode(cenConfirm.getCreateSiteCode());
			tWaybillStatus.setCreateSiteName(bDto.getSiteName());
			tWaybillStatus.setCreateSiteType(bDto.getSiteType());
		}
		if (rDto != null) {
			if(Constants.BUSSINESS_TYPE_THIRD_PARTY == cenConfirm.getType().intValue()){
				tWaybillStatus.setReceiveSiteCode(cenConfirm.getCreateSiteCode());
				tWaybillStatus.setReceiveSiteName(bDto.getSiteName());
				tWaybillStatus.setReceiveSiteType(bDto.getSiteType());
			}else{
				tWaybillStatus.setReceiveSiteCode(cenConfirm.getReceiveSiteCode());
				tWaybillStatus.setReceiveSiteName(rDto.getSiteName());
				tWaybillStatus.setReceiveSiteType(rDto.getSiteType());
			}
		}
		return tWaybillStatus;
	}

	public Boolean checkFormat(WaybillStatus tWaybillStatus, Short popType) {
		if (Constants.BUSSINESS_TYPE_POP != popType
				&& (tWaybillStatus.getPackageCode() == null
						|| "".equals(tWaybillStatus.getPackageCode()) || "-1"
							.equals(tWaybillStatus.getPackageCode()))) {
			log.warn("CenConfirmServiceImpl.checkFormat[PackageCode]return false");
			return Boolean.FALSE;
		} else if (tWaybillStatus.getCreateSiteCode() == null) {
            log.warn("CenConfirmServiceImpl.checkFormat[CreateSiteCode]return false");
			return Boolean.FALSE;
		} else if (tWaybillStatus.getCreateSiteName() == null
				|| "".equals(tWaybillStatus.getCreateSiteName())) {
			log.warn("CenConfirmServiceImpl.checkFormat[CreateSiteName]return false");
			return Boolean.FALSE;
		} else if (tWaybillStatus.getCreateSiteType() == null) {
			log.warn("CenConfirmServiceImpl.checkFormat[CreateSiteType]return false");
			return Boolean.FALSE;
		} else if (tWaybillStatus.getOperatorId() == null) {
			log.warn("CenConfirmServiceImpl.checkFormat[OperatorId]return false");
			return Boolean.FALSE;
		} else if (tWaybillStatus.getOperator() == null
				|| "".equals(tWaybillStatus.getOperator())) {
			log.warn("CenConfirmServiceImpl.checkFormat[Operator]return false");
			return Boolean.FALSE;
		} else if (tWaybillStatus.getOperateTime() == null) {
			log.warn("CenConfirmServiceImpl.checkFormat[OperateTime]return false");
			return Boolean.FALSE;
		} else if (tWaybillStatus.getOrgId() == null) {
			log.warn("CenConfirmServiceImpl.checkFormat[OrgId]return false");
			return Boolean.FALSE;
		} else {
			return Boolean.TRUE;
		}
	}

	public Task toTask(WaybillStatus tWaybillStatus,
			Integer smallType) {
		Task task = new Task();
		task.setTableName(getTableByType(smallType));
		task.setSequenceName(Task.getSequenceName(task.getTableName()));
		task.setKeyword1(tWaybillStatus.getWaybillCode());
		task.setKeyword2(tWaybillStatus.getPackageCode());
		task.setCreateSiteCode(tWaybillStatus.getCreateSiteCode());
		task.setBody(JsonHelper.toJson(tWaybillStatus));
		task.setType(separateType(tWaybillStatus, smallType));
		task.setOwnSign(BusinessHelper.getOwnSign());
		StringBuffer fingerprint = new StringBuffer();
		fingerprint
				.append(tWaybillStatus.getCreateSiteCode())
				.append("_")
				.append((tWaybillStatus.getReceiveSiteCode() == null ? "-1"
						: tWaybillStatus.getReceiveSiteCode())).append("_")
				.append(tWaybillStatus.getOperateType()).append("_")
				.append(tWaybillStatus.getWaybillCode()).append("_")
				.append(tWaybillStatus.getOperateTime()).append("_")
				.append(smallType);
		if (tWaybillStatus.getPackageCode() != null
				&& !"".equals(tWaybillStatus.getPackageCode())) {
			fingerprint.append("_").append(tWaybillStatus.getPackageCode());
		}
        task.setFingerprint(Md5Helper.encode(fingerprint.toString()));
		return task;
	}

	private Integer separateType(WaybillStatus tWaybillStatus, Integer smallType) {
		 if(Constants.OPERATE_TYPE_FC==smallType.intValue()||Constants.OPERATE_TYPE_RCD==smallType.intValue()||Constants.OPERATE_TYPE_SH==smallType.intValue() ||Constants.OPERATE_TYPE_In==smallType.intValue() ){
			return Task.TASK_TYPE_WAYBILL_TRACK;
		 }else{
			return tWaybillStatus.getOperateType();
		 }
	}

	private String getTableByType(Integer smallType) {
		if(Constants.OPERATE_TYPE_FC==smallType.intValue()||Constants.OPERATE_TYPE_RCD==smallType.intValue()||Constants.OPERATE_TYPE_SH==smallType.intValue() ||Constants.OPERATE_TYPE_In==smallType.intValue()){
			return Task.TABLE_NAME_POP;
		}else{
		 return	Task.TABLE_NAME_WAYBILL;
		}	
	}
}
