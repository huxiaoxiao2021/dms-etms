package com.jd.bluedragon.distribution.inspection.service.impl;

import com.google.common.base.Strings;
import com.jd.bluedragon.Constants;
import com.jd.bluedragon.common.domain.DmsRouter;
import com.jd.bluedragon.common.service.WaybillCommonService;
import com.jd.bluedragon.core.base.WaybillQueryManager;
import com.jd.bluedragon.core.jmq.producer.DefaultJMQProducer;
import com.jd.bluedragon.distribution.api.request.InspectionRequest;
import com.jd.bluedragon.distribution.auto.domain.UploadedPackage;
import com.jd.bluedragon.distribution.base.domain.DmsStorageArea;
import com.jd.bluedragon.distribution.base.service.DmsStorageAreaService;
import com.jd.bluedragon.distribution.base.service.SiteService;
import com.jd.bluedragon.distribution.inspection.dao.InspectionDao;
import com.jd.bluedragon.distribution.inspection.dao.InspectionECDao;
import com.jd.bluedragon.distribution.inspection.domain.*;
import com.jd.bluedragon.distribution.inspection.exception.InspectionException;
import com.jd.bluedragon.distribution.inspection.service.InspectionExceptionService;
import com.jd.bluedragon.distribution.inspection.service.InspectionService;
import com.jd.bluedragon.distribution.inspection.service.WaybillPackageBarcodeService;
import com.jd.bluedragon.distribution.operationLog.domain.OperationLog;
import com.jd.bluedragon.distribution.operationLog.service.OperationLogService;
import com.jd.bluedragon.distribution.order.ws.OrderWebService;
import com.jd.bluedragon.distribution.popPrint.domain.PopPrint;
import com.jd.bluedragon.distribution.popReveice.service.TaskPopRecieveCountService;
import com.jd.bluedragon.distribution.receive.service.CenConfirmService;
import com.jd.bluedragon.distribution.storage.service.StoragePackageMService;
import com.jd.bluedragon.distribution.task.domain.Task;
import com.jd.bluedragon.distribution.task.service.TaskService;
import com.jd.bluedragon.distribution.waybill.service.WaybillService;
import com.jd.bluedragon.dms.utils.BusinessUtil;
import com.jd.bluedragon.dms.utils.WaybillUtil;
import com.jd.bluedragon.utils.*;
import com.jd.etms.waybill.domain.BaseEntity;
import com.jd.etms.waybill.domain.DeliveryPackageD;
import com.jd.etms.waybill.domain.Waybill;
import com.jd.etms.waybill.dto.BigWaybillDto;
import com.jd.ioms.jsf.export.domain.Order;
import com.jd.ql.basic.dto.BaseStaffSiteOrgDto;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
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

	private final Logger log = LoggerFactory.getLogger(this.getClass());

	private final String PERFORMANCE_DMSSITECODE_SWITCH = "performance.dmsSiteCode.switch";

	@Autowired
	private InspectionDao inspectionDao;
	
	@Autowired
	private InspectionECDao inspectionECDao;

	@Autowired
	private OperationLogService operationLogService;

	@Autowired
	private CenConfirmService cenConfirmService;

    @Qualifier("dmsRouterMQ")
    @Autowired
    private DefaultJMQProducer dmsRouterMQ;

	@Autowired
	private OrderWebService orderWebService;

	@Autowired
	private TaskService taskService;
	
	@Autowired
	private InspectionExceptionService service;

    @Autowired
    private WaybillService waybillService;

    @Autowired
	private WaybillQueryManager waybillQueryManager;

    @Autowired
	private DmsStorageAreaService dmsStorageAreaService;

    @Autowired
	private WaybillCommonService waybillCommonService;

    @Autowired
    private StoragePackageMService storagePackageMService;

	/**
	 * 运单包裹关联信息
	 */
	@Autowired
	private WaybillPackageBarcodeService waybillPackageBarcodeService;

	@Autowired
	private TaskPopRecieveCountService taskPopRecieveCountService;

	@Autowired
	private SiteService siteService;

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
                String waybillCode = WaybillUtil.getWaybillCode(requestBean.getPackageBarOrWaybillCode());
                BigWaybillDto bigWaybillDto = getWaybill(waybillCode);
                if (bigWaybillDto != null && bigWaybillDto.getWaybill()!=null) {

                    log.debug("包裹交接50 订单号:{} waybillType:{} StoreID:{} task id:{} "
							,requestBean.getPackageBarOrWaybillCode() ,bigWaybillDto.getWaybill().getWaybillType() , bigWaybillDto.getWaybillState().getStoreId() , task.getId());

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
			if (WaybillUtil.isPackageCode(code)
					|| WaybillUtil.isSurfaceCode(code)) {
				requestBean.setPackageBarcode(code);
			} else if (WaybillUtil.isWaybillCode(code)) {// 否则为运单号
				requestBean.setWaybillCode(code);
			} else {
				log.warn("验货executeInspectionWorker，数据错误，非正常包裹号、取件单号或运单号，code: {} task id: {}" ,code, task.getId());
				continue;
			}
			inspections.addAll(prepareInspection(requestBean));
		}
		Collections.sort(inspections);
		return inspections;
	}

    public boolean isExists(Integer Storeid)
    {
        int value=(null==Storeid)?0:Storeid.intValue();
        switch (value){
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
	@JProfiler(jKey= "DMSWORKER.InspectionService.inspectionCore", mState = {JProEnum.TP, JProEnum.FunctionError})
	
	public void inspectionCore(List<Inspection> inspections) throws Exception {
		if (null == inspections) {
			log.warn(" 验货 inspectionCore 参数为空 : collection of inspection is null ");
			throw new InspectionException(" 验货 inspectionCore 参数为空 ");
		}
		for (Inspection inspection : inspections) {
			// 如果运单号为空，且取件单号，则根据规则匹配出运单号
			if (StringUtils.isBlank(inspection.getWaybillCode())
					&& !WaybillUtil.isSurfaceCode(inspection
							.getPackageBarcode())) {
				inspection.setWaybillCode(WaybillUtil.getWaybillCode(inspection.getPackageBarcode()));
			}
			//写入业务表数据和日志数据
			service.saveData(inspection);
			if(Constants.BUSSINESS_TYPE_OEM==inspection.getInspectionType()){
				// OEM同步wms
                pushOEMToWMS(inspection);//FIXME:51号库推送，需要检查是否在用
			}
			
		}
	}

	public void thirdPartyWorker(Inspection inspection){
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
			log.warn("包裹已经异常比较，再次分拣时不操作三方异常比对记录，包裹号：{}", inspection.getPackageBarcode());
			return;
		}

		InspectionEC inspectionEC = InspectionEC
				.toInspectionECByInspection(inspection);
		inspectionEC
				.setInspectionECType(InspectionEC.INSPECTIONEC_TYPE_LESS);
		inspectionEC
				.setStatus(InspectionEC.INSPECTION_EXCEPTION_STATUS_HANDLED);
		// update表示该记录已经存在，为分拣时插入，在此更新验货异常状态为正常
        /**
         * Fix wtw
         */
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

	public Integer insertOrUpdate(Inspection inspection) {
        /**
         * Fix wtw
         */
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
					WaybillUtil.getWaybillCode(requestBean
							.getPackageBarcode()));
			inspections.add(Inspection.toInspection(requestBean));
		} else {
			log.warn(" 验货prepareInspection，没有对应的包裹号或者箱号 ");
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
				BaseEntity<Waybill> baseEntity = waybillQueryManager
						.getWaybillByWaybillCode(waybillCode);
				if (baseEntity != null && baseEntity.getData() != null
						&& baseEntity.getData().getDistributeStoreId() != null) {
					requestBean.setReceiveSiteCode(baseEntity.getData()
							.getDistributeStoreId());// 库房编号
					this.log.warn("返仓交接:运单号【{}】调用运单WSS获取[库房编号]=[{}]"	,waybillCode, baseEntity.getData().getDistributeStoreId());
				} else {
					this.log.warn("返仓交接:运单号【{}】调用运单WSS获取[库房编号]异常:[baseEntity=null||baseEntity.getData()=null||baseEntity.getData().getDistributeStoreId()=null]",waybillCode);
				}
			} catch (Exception ex) {
				this.log.error("返仓交接:运单号【{}】调用运单WSS获取[库房编号]异常：",waybillCode, ex);
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
				.setOperateTime(inspection.getOperateTime() == null ? new Date()
						: inspection.getOperateTime());
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
			this.log.warn("POP 运单号为【{}】【{}】的验货数据为空，插入成功",inspectionPop.getWaybillCode(),inspectionPop.getCreateSiteCode());
			// //!!!!!!!!!!!!!!!!!!!!! be careful !!!!!!!!!!!!!!!!!!!!!////
			// 目前POP收货没有包裹号，目前将运单号设置为包裹号
			// inspectionPop.setPackageBarcode(inspectionPop.getWaybillCode());
			// //!!!!!!!!!!!!!!!!!!!!! be careful !!!!!!!!!!!!!!!!!!!!!////

			cenConfirmService.saveOrUpdateCenConfirm(cenConfirmService
					.createCenConfirmByInspection(inspectionPop));
		} else {
			this.log.debug("POP 运单号为【{}】【{}】的验货数据存在，更新成功",inspectionPop.getWaybillCode(),inspectionPop.getCreateSiteCode());
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

	public void pushOEMToWMS(Inspection inspection) {
        DmsRouter dmsRouter = new DmsRouter();
        try {

            String orderId = waybillQueryManager.getOrderCodeByWaybillCode(inspection.getWaybillCode(),true);
            if(!NumberUtils.isDigits(orderId)){
                log.warn("pushOEMToWMS根据运单号查询的订单号是非数字code[{}]orderId[{}]",inspection.getWaybillCode(),orderId);
                return;
            }
			Order order = orderWebService.getOrder(Long.parseLong(orderId));
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

			this.log.debug("分拣中心OEM收货推送WMS:MQ[{}]",json);
			//messageClient.sendMessage("dms_router", json,inspection.getWaybillCode());
            dmsRouterMQ.send(inspection.getWaybillCode(),json);
		} catch (Exception e) {
			this.log.error("分拣中心OEM收货推送WMS失败[{}]",JsonHelper.toJson(dmsRouter), e);
		}
	}

	
	@Override
	public boolean haveInspection(Inspection inspection) {
		return this.inspectionDao.haveInspection(inspection);
	}


	@Override
	public boolean dealHandoverPackages(Task task) {
		if(log.isDebugEnabled()){
			this.log.debug("自动分拣开始处理插入 task_inspection 任务 task = {}" , JsonHelper.toJson(task));
		}
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
			this.log.debug("从任务表里面抓取自动分拣机交接的包裹数据存入 task_inspection 表 ");
			taskService.add(taskInsp);
		} catch (Exception ex) {
			this.log.error("从任务表里面抓取自动分拣机交接的包裹数据存入 task_inspection 表失败。任务 task = {}" , JsonHelper.toJson(task) , ex);
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
        BigWaybillDto bigWaybillDto=waybillService.getWaybill(waybillCode, false);
        if(bigWaybillDto==null || bigWaybillDto.getWaybill()==null){
            this.log.warn(" 获取运单：【{}】数据为空",waybillCode);
            return null;
        }
        return bigWaybillDto;
    }

	/**
	 *  通过运单号获得库位号
	 * @param dmsSiteCode 分拣中心ID
	 * @param waybillCode 运单号ID
	 * @return
	 * */
	@JProfiler(jKey = "InspectionServiceImpl.getInspectionResult",mState = {JProEnum.TP,JProEnum.FunctionError})
	public InspectionResult getInspectionResult(Integer dmsSiteCode, String waybillCode) {
		BaseEntity<BigWaybillDto> baseEntity = waybillQueryManager.getDataByChoice(waybillCode, true, false, false, false);
		if (baseEntity != null && baseEntity.getData() != null && baseEntity.getData().getWaybill() != null) {
			// 获取运单信息
			Waybill waybill = baseEntity.getData().getWaybill();
			if(dmsSiteCode == null || waybill.getProvinceId() ==null){
				return new InspectionResult("");
			}
			DmsStorageArea newDmsStorageArea = dmsStorageAreaService.findByDmsSiteAndWaybillAdress(dmsSiteCode,waybill.getProvinceId(),waybill.getCityId());
			if(newDmsStorageArea != null){
				String storageCode = newDmsStorageArea.getStorageCode();
				return new InspectionResult(storageCode);
			}else {
				return new InspectionResult("");
			}
		}else{
			this.log.warn("通过运单号获取运单信息失败：{}" , waybillCode);
			return new InspectionResult("");
		}
	}

	/**
	 * 按条件查询验货记录
	 *
	 * */
	@Override
	public List<Inspection> queryByCondition(Inspection inspection) {
		return this.inspectionDao.queryByCondition(inspection);
	}
	/**
	 * 平台打印，补验货任务
	 * @param task
	 * @param ownSign
	 * @return
	 * @throws Exception
	 */
	public boolean popPrintInspection(Task task, String ownSign) throws Exception{
		String body = task.getBody().substring(1, task.getBody().length() - 1);
		PopPrint popPrint = com.jd.bluedragon.distribution.api.utils.JsonHelper.fromJson(body, PopPrint.class);
		if (!WaybillUtil.isWaybillCode(popPrint.getWaybillCode())) {
			log.warn("平台订单已打印未收货处理 --> 打印单号【{}】，运单号【{}】， 操作人SiteCode【{}】，为非平台订单"
					,popPrint.getPopPrintId(),popPrint.getWaybillCode(), popPrint.getCreateSiteCode());
			return true;
		}

		Inspection inspection = popPrintToInspection(popPrint);
		if (com.jd.bluedragon.common.domain.Waybill.isPopWaybillType(popPrint.getWaybillType())
				&& !Constants.POP_QUEUE_EXPRESS.equals(popPrint.getPopReceiveType())) {
			try {
				this.taskPopRecieveCountService.insert(inspection);
				this.log.debug("平台订单已打印未收货处理 --> 分拣中心-运单【{}-{}】收货补全回传POP成功",popPrint.getCreateSiteCode(),popPrint.getWaybillCode());
			} catch (Exception e) {
				this.log.error("平台订单已打印未收货处理 --> 分拣中心-运单【{}-{}】 收货补全回传POP，补全异常", popPrint.getCreateSiteCode(),popPrint.getWaybillCode(),e);
			}
		}
		try {
			this.addInspectionPop(inspection);
			this.log.debug("平台订单已打印未收货处理 --> 分拣中心-运单【{}-{}】 收货信息不存在，补全成功",popPrint.getCreateSiteCode(),popPrint.getWaybillCode());
		} catch (Exception e) {
			this.log.error("平台订单已打印未收货处理 --> 分拣中心-运单【{}-{}】 收货信息不存在，补全异常",popPrint.getCreateSiteCode(),popPrint.getWaybillCode(), e);
		}
		return true;
	}
	public Inspection popPrintToInspection(PopPrint popPrint) {
		try {
			Inspection inspection = new Inspection();
			inspection.setWaybillCode(popPrint.getWaybillCode());
			if (Constants.POP_QUEUE_SITE.equals(popPrint.getPopReceiveType())) {
				inspection.setInspectionType(Constants.BUSSINESS_TYPE_SITE);
			} else {
				inspection.setInspectionType(Constants.BUSSINESS_TYPE_POP);
			}
			inspection.setCreateUserCode(popPrint.getCreateUserCode());
			inspection.setCreateUser(popPrint.getCreateUser());
			inspection.setCreateTime((popPrint.getPrintPackTime() == null) ? popPrint.getPrintInvoiceTime() : popPrint.getPrintPackTime());
			inspection.setCreateSiteCode(popPrint.getCreateSiteCode());

			inspection.setUpdateTime(inspection.getCreateTime());
			inspection.setUpdateUser(inspection.getCreateUser());
			inspection.setUpdateUserCode(inspection.getCreateUserCode());

			inspection.setPopSupId(popPrint.getPopSupId());
			inspection.setPopSupName(popPrint.getPopSupName());
			inspection.setQuantity(popPrint.getQuantity());
			inspection.setCrossCode(popPrint.getCrossCode());
			inspection.setWaybillType(popPrint.getWaybillType());
			inspection.setPopReceiveType(popPrint.getPopReceiveType());
			inspection.setPopFlag(PopPrint.POP_FLAF_1);
			inspection.setThirdWaybillCode(popPrint.getThirdWaybillCode());
			inspection.setQueueNo(popPrint.getQueueNo());

			inspection.setPackageBarcode((popPrint.getPackageBarcode() == null) ? popPrint.getWaybillCode() : popPrint.getPackageBarcode());
			inspection.setBoxCode(popPrint.getBoxCode());
			inspection.setDriverCode(popPrint.getDriverCode());
			inspection.setDriverName(popPrint.getDriverName());
			inspection.setBusiId(popPrint.getBusiId());
			inspection.setBusiName(popPrint.getBusiName());
			inspection.setOperateTime(popPrint.getPrintPackTime());
			if (null == inspection.getOperateTime()) {
				inspection.setOperateTime(popPrint.getCreateTime());
			}
			if (com.jd.bluedragon.common.domain.Waybill.isPopWaybillType(inspection.getWaybillType())) {
				inspection.setBusiId(popPrint.getPopSupId());
				inspection.setBusiName(popPrint.getPopSupName());
			}

			return inspection;
		} catch (Exception e) {
			log.error("平台订单已打印未收货处理 --> 转换打印信息异常：popPrint={}",JsonHelper.toJson(popPrint), e);
			return null;
		}
	}

	@Override
	public String getHintMessage(Integer dmsSiteCode, String waybillCode) {

		String hintMessage = "";
		Integer preSiteCode = null;
		Integer destinationDmsId = null;
		com.jd.bluedragon.common.domain.Waybill waybill = waybillCommonService.findByWaybillCode(waybillCode);
		if(waybill != null && waybill.getWaybillSign() != null){
			//是否是金鹏订单
			if(BusinessUtil.isPerformanceOrder(waybill.getWaybillSign())){
				//预分拣站点
				preSiteCode = waybill.getSiteCode();
				BaseStaffSiteOrgDto bDto = siteService.getSite(preSiteCode);
				if(bDto != null && bDto.getDmsId() != null){
					//末级分拣中心
					destinationDmsId = bDto.getDmsId();
				}
				String dmsIds = PropertiesHelper.newInstance().getValue(PERFORMANCE_DMSSITECODE_SWITCH);
				String[] dmsCodes = dmsIds.split(",");
				List<String> dmsList = Arrays.asList(dmsCodes);
				if(dmsList != null && dmsList.size() > 0 && dmsList.contains(String.valueOf(destinationDmsId)) ||
						Strings.isNullOrEmpty(PropertiesHelper.newInstance().getValue(PERFORMANCE_DMSSITECODE_SWITCH))){
					//登陆人操作机构是否是末级分拣中心
					if(dmsSiteCode.equals(destinationDmsId)){
						//运单是否发货
						Boolean isCanSend = storagePackageMService.checkWaybillCanSend(waybillCode,waybill.getWaybillSign());
						if(!isCanSend){
							hintMessage = "暂存集齐后发货";
						}
					}
				}

			}
		}
		return hintMessage;
	}

	@Override
	public List<Inspection> findPageInspection(Map<String, Object> params) {
		log.debug("InspectionServiceImpl.findPageInspection begin...");
		return inspectionDao.findPageInspection(params);
	}

	@Override
	public InspectionPackProgress getWaybillCheckProgress(String waybillCode, Integer createSiteCode) {
        if (StringUtils.isBlank(waybillCode))
            return null;
        BaseEntity<BigWaybillDto> waybillDto = waybillQueryManager.getDataByChoice(waybillCode, true, false, false, true);
		if (null == waybillDto ||
            null == waybillDto.getData() ||
            null == waybillDto.getData().getWaybill() ||
            CollectionUtils.isEmpty(waybillDto.getData().getPackageList())
        )
		    return null;
        List<Inspection> inspections = inspectionDao.listInspectionByWaybillCode(waybillCode, createSiteCode);
        List<String> inspectedPackNos = new ArrayList<>();
        List<InspectionPackProgress.CheckPack> checkedPacks = new ArrayList<>();
        if (!CollectionUtils.isEmpty(inspections)) {
            for (Inspection inspection : inspections) {
                InspectionPackProgress.CheckPack checkPack = new InspectionPackProgress.CheckPack();
                checkPack.setPackNo(inspection.getPackageBarcode());
                checkedPacks.add(checkPack);
                inspectedPackNos.add(inspection.getPackageBarcode());
            }
        }

        List<DeliveryPackageD> packageList = waybillDto.getData().getPackageList();
        List<String> totalPackNos = new ArrayList<>(packageList.size());
        for (DeliveryPackageD deliveryPackageD : packageList) {
            totalPackNos.add(deliveryPackageD.getPackageBarcode());
        }
        List<InspectionPackProgress.CheckPack> unCheckedPacks = new ArrayList<>();
        if (!CollectionUtils.isEmpty(inspectedPackNos)) {
	        totalPackNos.removeAll(inspectedPackNos);
        }
        if (!CollectionUtils.isEmpty(totalPackNos)) {
            for (String packNo : totalPackNos) {
                InspectionPackProgress.CheckPack checkPack = new InspectionPackProgress.CheckPack();
                checkPack.setPackNo(packNo);
                unCheckedPacks.add(checkPack);
            }
        }

        InspectionPackProgress result = new InspectionPackProgress();
        result.setWaybillCode(waybillCode);
        result.setCheckedPackNos(checkedPacks);
        result.setUnCheckedPackNos(unCheckedPacks);
        return result;
	}
}
