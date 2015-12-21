package com.jd.bluedragon.distribution.inspection.service.impl;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.common.domain.DmsRouter;
import com.jd.bluedragon.distribution.api.request.InspectionRequest;
import com.jd.bluedragon.distribution.auto.domain.UploadedPackage;
import com.jd.bluedragon.distribution.inspection.dao.InspectionDao;
import com.jd.bluedragon.distribution.inspection.dao.InspectionECDao;
import com.jd.bluedragon.distribution.inspection.domain.Inspection;
import com.jd.bluedragon.distribution.inspection.domain.InspectionAS;
import com.jd.bluedragon.distribution.inspection.domain.InspectionEC;
import com.jd.bluedragon.distribution.inspection.exception.InspectionException;
import com.jd.bluedragon.distribution.inspection.service.InspectionExceptionService;
import com.jd.bluedragon.distribution.inspection.service.InspectionService;
import com.jd.bluedragon.distribution.inspection.service.WaybillPackageBarcodeService;
import com.jd.bluedragon.distribution.operationLog.domain.OperationLog;
import com.jd.bluedragon.distribution.operationLog.service.OperationLogService;
import com.jd.bluedragon.distribution.order.ws.OrderWebService;
import com.jd.bluedragon.distribution.receive.service.CenConfirmService;
import com.jd.bluedragon.distribution.task.domain.Task;
import com.jd.bluedragon.distribution.task.service.TaskService;
import com.jd.bluedragon.distribution.waybill.service.WaybillService;
import com.jd.bluedragon.utils.*;
import com.jd.etms.message.produce.client.MessageClient;
import com.jd.etms.waybill.api.WaybillQueryApi;
import com.jd.etms.waybill.domain.BaseEntity;
import com.jd.etms.waybill.domain.DeliveryPackageD;
import com.jd.etms.waybill.domain.Waybill;
import com.jd.etms.waybill.dto.BigWaybillDto;
import com.jd.ump.annotation.JProfiler;
import jd.oom.client.clientbean.Order;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * 验货Service
 * 
 * @author wangzichen
 * 
 */
@Service
public class InspectionServiceImpl implements InspectionService {

	private final Log logger = LogFactory.getLog(this.getClass());

	// name space
	public static final String inspectionDaoNamespace = InspectionDao.class
			.getName();

	@Autowired
	private InspectionDao inspectionDao;
	
	@Autowired
	private InspectionECDao inspectionECDao;

	@Autowired
	private OperationLogService operationLogService;

	@Autowired
	private CenConfirmService cenConfirmService;

	@Autowired
	private MessageClient messageClient;

	@Autowired
	private OrderWebService orderWebService;

	@Autowired
	private TaskService taskService;
	
	@Autowired
	private InspectionExceptionService service;

    @Autowired
    private WaybillService waybillService;

	/**
	 * 运单包裹关联信息
	 */
	@Autowired
	private WaybillPackageBarcodeService waybillPackageBarcodeService;

	/* 运单查询 */
	@Autowired
	WaybillQueryApi waybillQueryApi;

	public List<Inspection> parseInspections(Task task) {
		if (task == null || StringUtils.isBlank(task.getBody())) {
			return null;
		}

		InspectionRequest[] arrays = JsonHelper.jsonToArray(task.getBody(),
				InspectionRequest[].class);//FIXME:问下配送序列化高性能方法
		List<InspectionRequest> requestList = Arrays.asList(arrays);

		List<Inspection> inspections = new ArrayList<Inspection>();

		for (InspectionRequest requestBean : requestList) {
			String code = requestBean.getPackageBarOrWaybillCode();

            //pda不再区分用户入口， 所有订单都可以扫描， 由后台获取运单ordertype和storeid 判断以前的类型  2014年12月16日16:21:55 by guoyongzhi
            if(requestBean.getBusinessType()==Constants.BUSSINESS_TYPE_NEWTRANSFER)//包裹交接类型 以前是 1130： 50库房， 51夺宝岛 52协同仓  现在是50
            {
                String waybillCode = BusinessHelper.getWaybillCode(requestBean.getPackageBarOrWaybillCode());
                BigWaybillDto bigWaybillDto = getWaybill(waybillCode);
                if (bigWaybillDto != null && bigWaybillDto.getWaybill()!=null) {

                    logger.error("包裹交接50 订单号: " + requestBean.getPackageBarOrWaybillCode() +
                            "  waybillType:" + bigWaybillDto.getWaybill().getWaybillType() +
                            "  StoreID:" + bigWaybillDto.getWaybillState().getStoreId() +
                            " task id: " + task.getId());

                    if (bigWaybillDto.getWaybill().getWaybillType() == Constants.BUSSINESS_TYPE_DBD_ORDERTYPE) { //夺宝岛交接 51 : 2
                        requestBean.setBusinessType(Constants.BUSSINESS_TYPE_BDB);
                    } else if (bigWaybillDto.getWaybill().getWaybillType() == Constants.BUSSINESS_TYPE_ZY_ORDERTYPE) {  //正向库房或者协同仓交接
                        if (isExists(bigWaybillDto.getWaybillState().getStoreId())) {
                            requestBean.setBusinessType(Constants.BUSSINESS_TYPE_OEM_52);
                        } else {
                            requestBean.setBusinessType(Constants.BUSSINESS_TYPE_TRANSFER);
                        }
                    }
                }
            }


			// 如果是包裹号获取取件单号，则存在包裹号属性中
			if (BusinessHelper.isPackageCode(code)
					|| BusinessHelper.isPickupCode(code)) {
				requestBean.setPackageBarcode(code);
			} else if (BusinessHelper.isWaybillCode(code)) {// 否则为运单号
				requestBean.setWaybillCode(code);
			} else {
				logger.error("验货executeInspectionWorker，数据错误，非正常包裹号、取件单号或运单号，code: "
						+ code + " task id: " + task.getId());
				continue;
			}
			inspections.addAll(prepareInspection(requestBean));
		}
		Collections.sort(inspections);
		return inspections;
	}

    public boolean isExists(Integer Storeid)
    {
        switch (Storeid){
            case 52:
                return true;
            case 54:
                return true;
            case 53:
                return true;
            case 55:
                return true;
            case 56:
                return true;
            case 58:
                return true;
            case 59:
                return true;
            default:
                return false;
        }

    }

	/**
	 * 验货核心操作逻辑
	 * 
	 * @param inspections
	 * @return
	 * @throws Exception
	 * 
	 */
	@Override
	@JProfiler(jKey= "DMSWORKER.InspectionService.inspectionCore")
	
	public void inspectionCore(List<Inspection> inspections) throws Exception {
		if (null == inspections) {
			logger.info(" 验货 inspectionCore 参数为空 : collection of inspection is null ");
			throw new InspectionException(" 验货 inspectionCore 参数为空 ");
		}
		for (Inspection inspection : inspections) {
			// 如果运单号为空，且取件单号，则根据规则匹配出运单号
			if (StringUtils.isBlank(inspection.getWaybillCode())
					&& !BusinessHelper.isPickupCode(inspection
							.getPackageBarcode())) {
				inspection.setWaybillCode(BusinessHelper
						.getWaybillCodeByPackageBarcode(inspection
								.getPackageBarcode()));
			}
			//写入业务表数据和日志数据
			service.saveData(inspection);
			if(Constants.BUSSINESS_TYPE_OEM==inspection.getInspectionType()){
				// OEM同步wms
				try {
					pushOEMToWMS(inspection);//FIXME:51号库推送，需要检查是否在用
				} catch (Exception e) {
					e.printStackTrace();
					logger.error(" 验货 inspectionCore调用OEM服务异常",e);
				}	
			}
			
		}
	}

	@Transactional(readOnly = false, propagation = Propagation.REQUIRED)
	public void saveData(Inspection inspection) {//FIXME:private
		this.insertOrUpdate(inspection);
		addOperationLog(inspection);

		cenConfirmService.saveOrUpdateCenConfirm(cenConfirmService
				.createCenConfirmByInspection(inspection));
		// 如果是三方,则插入异常信息inspection_e_c表
		if (inspection.getInspectionType().equals(Inspection.BUSSINESS_TYPE_THIRD_PARTY)) 
			this.thirdPartyWorker(inspection);
	}

	private void thirdPartyWorker(Inspection inspection){
		InspectionEC inspectionECSel = new InspectionEC.Builder(
				inspection.getPackageBarcode(),
				inspection.getCreateSiteCode())
				.boxCode(inspection.getBoxCode())
				.receiveSiteCode(inspection.getReceiveSiteCode())
				.inspectionType(inspection.getInspectionType()).yn(1)
				.build();
		List<InspectionEC> preInspectionEC = inspectionECDao
				.selectSelective(inspectionECSel);
		if (!preInspectionEC.isEmpty()
				&& InspectionEC.INSPECTION_EXCEPTION_STATUS_HANDLED <= preInspectionEC
						.get(0).getStatus()) {
			logger.info("包裹已经异常比较，再次分拣时不操作三方异常比对记录，包裹号："
					+ inspection.getPackageBarcode());
			return;
		}

		InspectionEC inspectionEC = InspectionEC
				.toInspectionECByInspection(inspection);
		inspectionEC
				.setInspectionECType(InspectionEC.INSPECTIONEC_TYPE_LESS);
		inspectionEC
				.setStatus(InspectionEC.INSPECTION_EXCEPTION_STATUS_HANDLED);
		// update表示该记录已经存在，为分拣时插入，在此更新验货异常状态为正常
		int updateResult = inspectionECDao.updateOne(inspectionEC);
		if (Constants.NO_MATCH_DATA == updateResult
				&& preInspectionEC.isEmpty()) {
			// insert表示该记录还不存在，分拣时没有插入，在此验货异常类型为多验
			inspectionEC
					.setInspectionECType(InspectionEC.INSPECTIONEC_TYPE_MORE);
			inspectionEC
					.setStatus(InspectionEC.INSPECTION_EXCEPTION_STATUS_UNHANDLED);
			inspectionECDao.add(InspectionECDao.namespace, inspectionEC);
		}
	}

	public Integer insertOrUpdate(Inspection inspection) {
		int result = inspectionDao.update(InspectionDao.namespace, inspection);
		if (Constants.NO_MATCH_DATA == result) {
			result = inspectionDao.add(InspectionDao.namespace, inspection);
		}
		return result;
	}

	/**
	 * 根据运单号或包裹号来返回Inspection集合
	 * 
	 * @param requestBean
	 */
	@Override
	public List<Inspection> prepareInspection(InspectionRequest requestBean) {
		Set<Inspection> inspections = new HashSet<Inspection>();
		if (StringUtils.isNotBlank(requestBean.getWaybillCode())) {
			String waybillCode = requestBean.getWaybillCode();
			List<DeliveryPackageD> packages = waybillPackageBarcodeService
					.getPackageBarcodeByWaybillCode(waybillCode);
			requestBean = getStoreIdByWaybillCode(requestBean, waybillCode);
			if (null == packages) {
				return null;
			}
			if (BusinessHelper.checkIntNumRange(packages.size())) {
				for (DeliveryPackageD pack : packages) {
					requestBean.setPackageBarcode(pack.getPackageBarcode());
					inspections.add(Inspection.toInspection(requestBean));
				}
			}
		} else if (StringUtils.isNotEmpty(requestBean.getPackageBarcode())) {
			requestBean = getStoreIdByWaybillCode(requestBean,
					BusinessHelper.getWaybillCodeByPackageBarcode(requestBean
							.getPackageBarcode()));
			inspections.add(Inspection.toInspection(requestBean));
		} else {
			logger.info(" 验货prepareInspection，没有对应的包裹号或者箱号 ");
		}
		CollectionHelper<Inspection> helper = new CollectionHelper<Inspection>();
		return helper.toList(inspections);
	}

	private InspectionRequest getStoreIdByWaybillCode(
			InspectionRequest requestBean, String waybillCode) {
		// 返仓交接，根据运单号获取库房号
		if (Constants.BUSSINESS_TYPE_FC == requestBean.getBusinessType()
				.intValue()) {
			try {
				BaseEntity<Waybill> baseEntity = waybillQueryApi
						.getWaybillByWaybillCode(waybillCode);
				if (baseEntity != null && baseEntity.getData() != null
						&& baseEntity.getData().getDistributeStoreId() != null) {
					requestBean.setReceiveSiteCode(baseEntity.getData()
							.getDistributeStoreId());// 库房编号
					this.logger
							.info("返仓交接:运单号【 "
									+ waybillCode
									+ "】调用运单WSS获取[库房编号]=["
									+ baseEntity.getData()
											.getDistributeStoreId() + "]");
				} else {
					this.logger
							.error("返仓交接:运单号【 "
									+ waybillCode
									+ "】调用运单WSS获取[库房编号]异常:[baseEntity=null||baseEntity.getData()=null||baseEntity.getData().getDistributeStoreId()=null]");
				}
			} catch (Exception ex) {
				this.logger.error("返仓交接:运单号【 " + waybillCode
						+ "】调用运单WSS获取[库房编号]异常：", ex);
			}
		}
		return requestBean;
	}

	public Integer inspectionCount(Inspection inspection) {
		return inspectionDao.inspectionCount(inspection);
	}

	/**
	 * 插入pda操作日志表
	 * 
	 * @param inspection
	 */
	private void addOperationLog(Inspection inspection) {
		OperationLog operationLog = new OperationLog();
		operationLog.setBoxCode(inspection.getBoxCode());
		operationLog.setCreateSiteCode(inspection.getCreateSiteCode());
		operationLog.setCreateUser(inspection.getCreateUser());
		operationLog.setCreateUserCode(inspection.getCreateUserCode());
		if ((Constants.BUSSINESS_TYPE_TRANSFER == inspection.getInspectionType()) || isExists(inspection.getInspectionType())) {
			operationLog.setLogType(OperationLog.LOG_TYPE_TRANSFER);
		} else {
			operationLog.setLogType(OperationLog.LOG_TYPE_INSPECTION);
		}
		operationLog
				.setOperateTime(inspection.getCreateTime() == null ? new Date()
						: inspection.getCreateTime());
		operationLog.setPackageCode(inspection.getPackageBarcode());
		operationLog.setReceiveSiteCode(inspection.getReceiveSiteCode());
		operationLog.setWaybillCode(inspection.getWaybillCode());
		operationLogService.add(operationLog);
	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED)
	public int addInspectionPop(Inspection inspectionPop) {
		int result = inspectionDao.updatePop(inspectionPop);
		if (Constants.NO_MATCH_DATA == result) {
			result = inspectionDao.add(InspectionDao.namespace, inspectionPop);
			this.logger.info("POP 运单号为【" + inspectionPop.getWaybillCode()
					+ "】【" + inspectionPop.getCreateSiteCode()
					+ "】的验货数据为空，插入成功");
			// //!!!!!!!!!!!!!!!!!!!!! be careful !!!!!!!!!!!!!!!!!!!!!////
			// 目前POP收货没有包裹号，目前将运单号设置为包裹号
			// inspectionPop.setPackageBarcode(inspectionPop.getWaybillCode());
			// //!!!!!!!!!!!!!!!!!!!!! be careful !!!!!!!!!!!!!!!!!!!!!////

			cenConfirmService.saveOrUpdateCenConfirm(cenConfirmService
					.createCenConfirmByInspection(inspectionPop));
		} else {
			this.logger.info("POP 运单号为【" + inspectionPop.getWaybillCode()
					+ "】【" + inspectionPop.getCreateSiteCode()
					+ "】的验货数据存在，更新成功");
		}

		addOperationLog(inspectionPop);

		return result;
	}

	@Override
	public int findPopJoinTotalCount(Map<String, Object> paramMap) {
		return this.inspectionDao.findPopJoinTotalCount(paramMap);
	}

	@Override
	public List<Inspection> findPopJoinList(Map<String, Object> paramMap) {
		return this.inspectionDao.findPopJoinList(paramMap);
	}
	
	@Override
	public List<Inspection> findBPopJoinList(Map<String, Object> paramMap) {
		return this.inspectionDao.findBPopJoinList(paramMap);
	}

	@Override
	public int findBPopJoinTotalCount(Map<String, Object> paramMap) {
		return this.inspectionDao.findBPopJoinTotalCount(paramMap);
	}

	@Override
	public List<Inspection> findPopByWaybillCodes(List<String> waybillCodes) {
		return this.inspectionDao.findPopByWaybillCodes(waybillCodes);
	}

	private void pushOEMToWMS(Inspection inspection) {
		DmsRouter dmsRouter = new DmsRouter();
		Order order = orderWebService.getOrder(Long.parseLong(inspection
				.getWaybillCode()));
		StringBuffer fingerprint = new StringBuffer();
		fingerprint.append(order.getIdCompanyBranch()).append("_")
				.append(order.getDeliveryCenterID()).append("_")
				.append(order.getStoreId()).append("_")
				.append(inspection.getWaybillCode()).append("_")
				.append(inspection.getPackageBarcode()).append("_")
				.append(DateHelper.formatDateTime(inspection.getCreateTime()))
				.append("_").append(inspection.getCreateUserCode()).append("|")
				.append(inspection.getCreateUser());

		StringBuffer jsonBuffer = new StringBuffer();
		jsonBuffer.append("{\"orgId\":").append(order.getIdCompanyBranch())
				.append(",\"cky2\":").append(order.getDeliveryCenterID())
				.append(",\"storeId\":").append(order.getStoreId())
				.append(",\"orderId\":").append(inspection.getWaybillCode())
				.append(",\"packageCode\":\"")
				.append(inspection.getPackageBarcode())
				.append("\",\"operateTime\":\"")
				.append(DateHelper.formatDateTime(inspection.getCreateTime()))
				.append("\",\"operator\":\"")
				.append(inspection.getCreateUserCode()).append("|")
				.append(inspection.getCreateUser())
				.append("\",\"fingerprint\":\"")
				.append(Md5Helper.encode(fingerprint.toString())).append("\"}");

		dmsRouter.setBody(jsonBuffer.toString());
		dmsRouter.setType(80);
		String json = JsonHelper.toJson(dmsRouter);

		this.logger.info("分拣中心OEM收货推送WMS:MQ[" + json + "]");
		try {
			messageClient.sendMessage("dms_router", json,
					inspection.getWaybillCode());
		} catch (Exception e) {
			this.logger.error(
					"分拣中心OEM收货推送WMS失败[" + json + "]:" + e.getMessage(), e);
		}
	}

	
	@Override
	public boolean haveInspection(Inspection inspection) {
		return this.inspectionDao.haveInspection(inspection);
	}


	@Override
	public boolean dealHandoverPackages(Task task) {
		this.logger.info("自动分拣开始处理插入 task_inspection 任务 task = " + JsonHelper.toJson(task));
		UploadedPackage uPackage = JsonHelper.fromJson(task.getBody(), UploadedPackage.class);
		try {
			Task taskInsp = new Task();
			taskInsp.setCreateSiteCode(uPackage.getSortCenterNo());
			taskInsp.setKeyword1(String.valueOf(uPackage.getSortCenterNo()));
			taskInsp.setKeyword2(uPackage.getBarcode());
			taskInsp.setType(Task.TASK_TYPE_INSPECTION);
			taskInsp.setTableName(Task.getTableName(taskInsp.getType()));
			taskInsp.setSequenceName(Task.getSequenceName(taskInsp.getTableName()));
			taskInsp.setBody(JsonHelper.toJson(toInspectionAS(uPackage)));
			taskInsp.setCreateTime(new Date());
			taskInsp.setExecuteCount(0);
			taskInsp.setOwnSign(BusinessHelper.getOwnSign());
			taskInsp.setStatus(Task.TASK_STATUS_UNHANDLED);
			StringBuilder fingerprint = new StringBuilder("");
			fingerprint.append(task.getCreateSiteCode()).append("_")
					.append(task.getReceiveSiteCode()).append("_").append(task.getBusinessType())
					.append("_").append(task.getBoxCode()).append("_").append(task.getKeyword2())
					.append("_").append(DateHelper.formatDateTimeMs(task.getOperateTime()))
					.append("_").append(task.getOperateType());
			taskInsp.setFingerprint(Md5Helper.encode(fingerprint.toString()));
			this.logger.info("从任务表里面抓取自动分拣机交接的包裹数据存入 task_inspection 表 ");
			taskService.add(taskInsp);
		} catch (Exception ex) {
			this.logger.error("从任务表里面抓取自动分拣机交接的包裹数据存入 task_inspection 表失败。原因 " + ex);
			return false;
		}
		return true;
	}

	public List<InspectionAS> toInspectionAS(UploadedPackage uPackage){
		List<InspectionAS> inspASs = new ArrayList<InspectionAS>();
		InspectionAS inspAS = new InspectionAS();
		inspAS.setBoxCode("");
		inspAS.setBusinessType(50);  //fixme 自动分拣交接默认是正向
		inspAS.setExceptionType("");
		inspAS.setId(0);
		inspAS.setOperateTime(uPackage.getTimeStamp());
		inspAS.setOperateType(0);
		inspAS.setPackageBarOrWaybillCode(uPackage.getBarcode());
		inspAS.setReceiveSiteCode(0);
		inspAS.setSiteCode(uPackage.getSortCenterNo());
		inspAS.setSiteName(uPackage.getSortCenterName());
		inspAS.setUserCode(uPackage.getOperatorID());
		inspAS.setUserName(uPackage.getOperatorName());
		inspASs.add(inspAS);
		return inspASs;
	}
    /**
     * 获取运单信息
     */
    private BigWaybillDto getWaybill(String waybillCode)
    {
        BigWaybillDto bigWaybillDto=waybillService.getWaybill(waybillCode);
        if(bigWaybillDto==null || bigWaybillDto.getWaybill()==null){
            this.logger.error(" 获取运单：【 " + waybillCode + " 】数据为空");
            return null;
        }
        return bigWaybillDto;
    }
}
