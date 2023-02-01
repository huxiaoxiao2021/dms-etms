package com.jd.bluedragon.distribution.inspection.service.impl;

import com.alibaba.fastjson.JSON;
import com.google.common.base.Strings;
import com.jd.bluedragon.Constants;
import com.jd.bluedragon.common.domain.DmsRouter;
import com.jd.bluedragon.common.dto.easyFreeze.EasyFreezeSiteDto;
import com.jd.bluedragon.common.dto.base.response.JdCResponse;
import com.jd.bluedragon.common.dto.base.response.JdVerifyResponse;
import com.jd.bluedragon.common.dto.inspection.response.ConsumableRecordResponseDto;
import com.jd.bluedragon.common.dto.inspection.response.InspectionCheckResultDto;
import com.jd.bluedragon.common.dto.inspection.response.InspectionResultDto;
import com.jd.bluedragon.common.service.WaybillCommonService;
import com.jd.bluedragon.configuration.ucc.UccPropertyConfiguration;
import com.jd.bluedragon.core.base.AssertQueryManager;
import com.jd.bluedragon.core.base.WaybillPackageManager;
import com.jd.bluedragon.core.base.WaybillQueryManager;
import com.jd.bluedragon.core.base.WaybillTraceManager;
import com.jd.bluedragon.core.hint.constants.HintCodeConstants;
import com.jd.bluedragon.core.hint.service.HintService;
import com.jd.bluedragon.core.jmq.producer.DefaultJMQProducer;
import com.jd.bluedragon.distribution.abnormal.domain.DmsOperateHintTrack;
import com.jd.bluedragon.distribution.abnormal.service.DmsOperateHintService;
import com.jd.bluedragon.distribution.api.enums.OperatorTypeEnum;
import com.jd.bluedragon.distribution.alliance.service.AllianceBusiDeliveryDetailService;
import com.jd.bluedragon.distribution.api.request.HintCheckRequest;
import com.jd.bluedragon.distribution.api.request.InspectionRequest;
import com.jd.bluedragon.distribution.api.request.TaskRequest;
import com.jd.bluedragon.distribution.api.response.SortingResponse;
import com.jd.bluedragon.distribution.api.response.TaskResponse;
import com.jd.bluedragon.distribution.auto.domain.UploadedPackage;
import com.jd.bluedragon.distribution.base.domain.DmsStorageArea;
import com.jd.bluedragon.distribution.base.domain.JdCancelWaybillResponse;
import com.jd.bluedragon.distribution.base.service.BaseService;
import com.jd.bluedragon.distribution.base.service.DmsStorageAreaService;
import com.jd.bluedragon.distribution.base.service.SiteService;
import com.jd.bluedragon.distribution.client.domain.PdaOperateRequest;
import com.jd.bluedragon.distribution.coldChain.domain.InspectionCheckResult;
import com.jd.bluedragon.distribution.coldChain.domain.InspectionVO;
import com.jd.bluedragon.distribution.external.service.DmsPackingConsumableService;
import com.jd.bluedragon.distribution.inspection.InsepctionCheckDto;
import com.jd.bluedragon.distribution.inspection.InspectionBizSourceEnum;
import com.jd.bluedragon.distribution.inspection.InspectionCheckCondition;
import com.jd.bluedragon.distribution.inspection.constants.InspectionExeModeEnum;
import com.jd.bluedragon.distribution.inspection.dao.InspectionDao;
import com.jd.bluedragon.distribution.inspection.dao.InspectionECDao;
import com.jd.bluedragon.distribution.inspection.domain.*;
import com.jd.bluedragon.distribution.inspection.exception.InspectionException;
import com.jd.bluedragon.distribution.inspection.service.InspectionExceptionService;
import com.jd.bluedragon.distribution.inspection.service.InspectionJsfService;
import com.jd.bluedragon.distribution.inspection.service.InspectionService;
import com.jd.bluedragon.distribution.jsf.domain.InvokeResult;
import com.jd.bluedragon.distribution.jsf.domain.SortingJsfResponse;
import com.jd.bluedragon.distribution.operationLog.domain.OperationLog;
import com.jd.bluedragon.distribution.operationLog.service.OperationLogService;
import com.jd.bluedragon.distribution.order.ws.OrderWebService;
import com.jd.bluedragon.distribution.popPrint.domain.PopPrint;
import com.jd.bluedragon.distribution.popReveice.service.TaskPopRecieveCountService;
import com.jd.bluedragon.distribution.receive.service.CenConfirmService;
import com.jd.bluedragon.distribution.router.RouterService;
import com.jd.bluedragon.distribution.storage.service.StoragePackageMService;
import com.jd.bluedragon.distribution.task.domain.Task;
import com.jd.bluedragon.distribution.task.service.TaskService;
import com.jd.bluedragon.distribution.waybill.service.WaybillService;
import com.jd.bluedragon.dms.utils.BusinessUtil;
import com.jd.bluedragon.dms.utils.WaybillUtil;
import com.jd.bluedragon.utils.*;
import com.jd.dms.workbench.utils.sdk.base.Result;
import com.jd.etms.asset.material.base.ResultData;
import com.jd.etms.asset.material.base.ResultStateEnum;
import com.jd.etms.asset.material.dto.MatterPackageRelationDto;
import com.jd.etms.cache.util.EnumBusiCode;
import com.jd.etms.waybill.domain.BaseEntity;
import com.jd.etms.waybill.domain.DeliveryPackageD;
import com.jd.etms.waybill.domain.Waybill;
import com.jd.etms.waybill.dto.BigWaybillDto;
import com.jd.ioms.jsf.export.domain.Order;
import com.jd.ql.basic.domain.SortCrossDetail;
import com.jd.ql.basic.dto.BaseStaffSiteOrgDto;
import com.jd.ql.basic.ws.BasicPrimaryWS;
import com.jd.ql.dms.common.domain.JdResponse;
import com.jd.ql.dms.common.web.mvc.api.PagerResult;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * 验货Service
 *
 * @author wangzichen
 *
 */
@Service
public class InspectionServiceImpl implements InspectionService , InspectionJsfService {

	private final Logger log = LoggerFactory.getLogger(this.getClass());

	private final String PERFORMANCE_DMSSITECODE_SWITCH = "performance.dmsSiteCode.switch";

	@Autowired
	private UccPropertyConfiguration uccPropertyConfiguration;

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

    @Autowired
    private AssertQueryManager assertQueryManager;

	@Autowired
	private TaskPopRecieveCountService taskPopRecieveCountService;

	@Autowired
	private SiteService siteService;

	@Autowired
    private WaybillPackageManager waybillPackageManager;

    @Autowired
    private UccPropertyConfiguration uccConfig;

	@Autowired
	private DmsOperateHintService dmsOperateHintService;

	@Autowired
	private RouterService routerService;

	@Autowired
	@Qualifier("operateHintTrackMQ")
	private DefaultJMQProducer operateHintTrackMQ;

	@Autowired
	private BasicPrimaryWS basicPrimaryWS;

	@Autowired
	private WaybillTraceManager waybillTraceManager;
    @Autowired
    private AllianceBusiDeliveryDetailService allianceBusiDeliveryDetailService;
    @Autowired
    private DmsPackingConsumableService dmsPackingConsumableService;
    @Autowired
    private BaseService baseService;

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

	@Override
	public JdResponse getStorageCode(String barCode, Integer siteCode) {
		com.jd.ql.dms.common.domain.JdResponse jdResponse = new com.jd.ql.dms.common.domain.JdResponse();
		//判断是运单号还是包裹号
		Integer dmsSiteCode = siteCode;
		String waybillCode = barCode;
		String packageCode = barCode;
		boolean isPack = false;
		if(WaybillUtil.isPackageCode(barCode)){
			isPack = true;
			waybillCode = WaybillUtil.getWaybillCode(barCode);
		}
		// 运单绑定集包袋校验
		if(WaybillUtil.isWaybillCode(barCode)
				&& checkIsBindMaterial(waybillCode)){
			jdResponse.toFail(HintService.getHint(HintCodeConstants.WAYBILL_BIND_RECYCLE_BAG));
			return jdResponse;
		}
		InspectionResult inspectionResult = new InspectionResult("");
		//提取获取操作站点信息
		BaseStaffSiteOrgDto siteOrgDto = siteService.getSite(dmsSiteCode);

		try{
			inspectionResult = getInspectionResult(dmsSiteCode, waybillCode);
		}catch (Exception e){
			log.error("获取库位号失败，异常原因：", e);
		}

		String hintMessage = "";
		try{
			hintMessage = dmsOperateHintService.getInspectHintMessageByWaybillCode(waybillCode);
			inspectionResult.setHintMessage(hintMessage);
			log.info("验货redis查询运单提示语，运单号：{},结果：{}",waybillCode, hintMessage);
			if(StringHelper.isNotEmpty(hintMessage)){
				try {
					DmsOperateHintTrack dmsOperateHintTrack = new DmsOperateHintTrack();
					dmsOperateHintTrack.setWaybillCode(waybillCode);
					dmsOperateHintTrack.setHintDmsCode(siteCode);
					dmsOperateHintTrack.setHintOperateNode(DmsOperateHintTrack.OPERATE_NODE_INSPECTION);
					dmsOperateHintTrack.setHintTime(new Date());
					this.log.info("发送MQ[{}],业务ID[{}]",operateHintTrackMQ.getTopic(),dmsOperateHintTrack.getWaybillCode());
					operateHintTrackMQ.sendOnFailPersistent(dmsOperateHintTrack.getWaybillCode(), JSON.toJSONString(dmsOperateHintTrack));
				}catch(Exception e){
					log.error("发货提示语发mq异常,异常原因:" ,e);
				}
			}
		}catch (Exception e){
			log.error("验货redis查询运单提示语异常，改DB查询，运单号：{}",waybillCode, e);
		}
		//金鹏订单拦截提示
		hintMessage += getHintMessage(dmsSiteCode, waybillCode);
		//获取路由下一节点
		BaseStaffSiteOrgDto baseDto = routerService.getRouterNextSite(dmsSiteCode, waybillCode);
		inspectionResult.setNextRouterSiteName(baseDto==null?null:baseDto.getSiteName());
		inspectionResult.setHintMessage(hintMessage);

		// B网验货增加笼车号显示
		this.setTabletrolleyCode(inspectionResult, siteOrgDto, baseDto);

		//增加已验内容
		if(isPack){
			setScanPackageSize(inspectionResult,packageCode,waybillCode,dmsSiteCode);
		}
		jdResponse.toSucceed();//这里设置为成功，取不到值时记录warn日志
		jdResponse.setData(inspectionResult);
		return jdResponse;
	}

	/**
	 * B网验货显示下一节点的笼车号
	 *
	 * @param inspectionResult
	 * @param dmsSiteCode
	 * @param nextDest
	 */
	private void setTabletrolleyCode(InspectionResult inspectionResult, BaseStaffSiteOrgDto siteOrgDto, BaseStaffSiteOrgDto nextDest) {
		if (null == siteOrgDto || null == nextDest)
			return;
		if (null != siteOrgDto && siteOrgDto.getSubType() != null && siteOrgDto.getSubType() == Constants.B2B_SITE_TYPE) {
			SortCrossDetail sortCrossDetail = basicPrimaryWS.getCrossCodeDetailByDmsID(siteOrgDto.getSiteCode(), String.valueOf(nextDest.getSiteCode()));
			if (log.isInfoEnabled()) {
				log.info("Get table trolley code from basic. dmsId:{}, siteCode:{}, ret:[{}]", siteOrgDto.getSiteCode(), nextDest.getSiteCode(), JsonHelper.toJson(sortCrossDetail));
			}
			if (null != sortCrossDetail) {
				inspectionResult.setTabletrolleyCode(sortCrossDetail.getTabletrolleyCode());
			}
		}
	}

	/**
	 * 设置已扫包裹数据
	 * @param inspectionResult
	 * @param packageCode
	 * @param waybillCode
	 * @param siteCode
	 */
	private void setScanPackageSize(InspectionResult inspectionResult,String packageCode,String waybillCode,Integer siteCode){
		Inspection inspection = new Inspection();
		inspection.setWaybillCode(waybillCode);
		inspection.setCreateSiteCode(siteCode);
		inspection.setPackageBarcode(packageCode);
		//此时返回的已验数据不包含此次扫描包裹
		Integer scanSize = inspectionCountByWaybill(inspection);
		inspectionResult.setSacnPackageSize((scanSize==null?0:scanSize)+1);

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
			service.saveData(inspection,"InspectionServiceImpl#inspectionCore");
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

    public void saveData(Inspection inspection, String methodName) {//FIXME:private
        this.insertOrUpdate(inspection);
        addOperationLog(inspection,methodName);

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
	 * 分批查询时间跨度小时数
	 */
	private int timeRangeOneBatch = 1;
	public PagerResult<InsepctionCheckDto> findInspectionGather(InspectionCheckCondition condition){

		PagerResult<InsepctionCheckDto> result = new PagerResult<>();
		List<InsepctionCheckDto> insepctionCheckDtos = new ArrayList<>();

        final Result<Boolean> checkResult = this.checkParam4FindInspectionGather(condition);
        if (!checkResult.isSuccess()) {
            log.error("验货集齐查询参数校验不通过!");
            result.setRows(new ArrayList<InsepctionCheckDto>());
            result.setTotal(0);
            return result;
        }

        String format = "yyyy-MM-dd HH:mm:ss";
		Date start = DateHelper.parseDate(condition.getStartTime(),format);
		Date end = DateHelper.parseDate(condition.getEndTime(),format);
		int days = DateHelper.daysBetween(start,end);

		if(days > 1){
			log.error("验货集齐查询失败!");
			result.setRows(new ArrayList<InsepctionCheckDto>());
			result.setTotal(0);
			return result;
		}
		try{
			//查询总数
			// 分时间段多次汇总
            int hoursOffset = (int) (end.getTime() - start.getTime()) / 1000 / 3600;

            int timeRangeOneBatchTemp = timeRangeOneBatch;
            if(hoursOffset < timeRangeOneBatch){
                timeRangeOneBatchTemp = hoursOffset;
            }
            int timeRangeBatchTotal = hoursOffset / timeRangeOneBatchTemp;
            if(hoursOffset % timeRangeOneBatchTemp != 0){
                timeRangeBatchTotal++;
            }
            int total = 0;
            InspectionCheckCondition conditionTemp = new InspectionCheckCondition();
            BeanUtils.copyProperties(condition, conditionTemp);
            for(int i = 1; i <= timeRangeBatchTotal; i++) {
                Calendar calendarStart = Calendar.getInstance();
                calendarStart.setTime(start);
                Calendar calendarEnd = Calendar.getInstance();
                calendarEnd.setTime(start);
                calendarStart.add(Calendar.HOUR_OF_DAY, timeRangeOneBatchTemp * (i - 1));
                if(i < timeRangeBatchTotal){
                    calendarEnd.add(Calendar.HOUR_OF_DAY, timeRangeOneBatchTemp * i);
                } else {
                    calendarEnd.setTime(end);
                }
                Date startTimeDate = calendarStart.getTime();
                Date endTimeDate = calendarEnd.getTime();
                conditionTemp.setStartTime(DateHelper.formatDateTime(startTimeDate));
                conditionTemp.setEndTime(DateHelper.formatDateTime(endTimeDate));
                int totalTemp = inspectionDao.findInspectionGatherPageCount(conditionTemp);
                total += totalTemp;

                if(insepctionCheckDtos.size() < condition.getLimit()){
                    insepctionCheckDtos.addAll(inspectionDao.findInspectionGather(conditionTemp));
                }
            }
			result.setTotal(total);

			result.setRows(insepctionCheckDtos);

		}catch (Exception e){
			log.error("查询失败!",e);
			result.setRows(new ArrayList<InsepctionCheckDto>());
			result.setTotal(0);
		}
		return result;
	}

    private Result<Boolean> checkParam4FindInspectionGather(InspectionCheckCondition condition) {
        Result<Boolean> result = Result.success();
        if (condition == null) {
            return result.toFail("参数错误，参数不能为空");
        }
        if (condition.getStartTime() == null) {
            return result.toFail("参数错误，startTime不能为空");
        }
        if (condition.getEndTime() == null) {
            return result.toFail("参数错误，endTime不能为空");
        }
        return result;
    }

	public PagerResult<Inspection> findInspetionedPacks(Inspection inspection){

		PagerResult<Inspection> result = new PagerResult<>();
		List<Inspection> inspections = new ArrayList<>();
		try{
			inspections = inspectionDao.findInspetionedPacks(inspection);
			result.setRows(inspections);
			result.setTotal(inspections.size());
		}catch(Exception e){
			log.error("查询失败!",e);
			result.setRows(new ArrayList<Inspection>());
			result.setTotal(0);
		}
		return result;
	}

	@JProfiler(jKey = "DMSWEB.InspectionServiceImpl.gatherCheck", mState = JProEnum.TP, jAppName = Constants.UMP_APP_NAME_DMSWEB)
    public SortingJsfResponse gatherCheck(PdaOperateRequest pdaOperateRequest,SortingJsfResponse sortingJsfResponse){

        //校验运单验货是否集齐
        if(pdaOperateRequest != null && pdaOperateRequest.getIsGather() != null && pdaOperateRequest.getIsGather() == 1){
            String packageCode = pdaOperateRequest.getPackageCode();
            String waybillCode = WaybillUtil.getWaybillCode(packageCode);
            Integer createSiteCode = pdaOperateRequest.getCreateSiteCode();

            Inspection inspection = new Inspection();
            inspection.setWaybillCode(waybillCode);
            inspection.setCreateSiteCode(createSiteCode);

            try{
                InsepctionCheckDto insepctionCheckDto = inspectionDao.verifyReverseInspectionGather(inspection);
                if(insepctionCheckDto == null || insepctionCheckDto.getInspectionedPackNum() == null
                || insepctionCheckDto.getInspectionedPackNum() == 0){
                    this.log.info("未查到验货信息，校验运单号：{}，分拣中心ID：{}",waybillCode,createSiteCode);
                    sortingJsfResponse = new SortingJsfResponse();
                    sortingJsfResponse.setCode(SortingResponse.CODE_31123);
                    sortingJsfResponse.setMessage(SortingResponse.MESSAGE_31123);
                    return sortingJsfResponse;
                }
                if(insepctionCheckDto.getPackageNum()>insepctionCheckDto.getInspectionedPackNum()){
                    this.log.info("逆向验货未集齐，校验运单号：{}，分拣中心ID：{}",waybillCode,createSiteCode);
                    sortingJsfResponse = new SortingJsfResponse();
                    sortingJsfResponse.setCode(SortingResponse.CODE_31123);
                    sortingJsfResponse.setMessage(SortingResponse.MESSAGE_31123);
                    return sortingJsfResponse;
                }
            }catch (Exception e){
                this.log.error("逆向验货校验失败，校验运单号：{}，分拣中心ID：{}",waybillCode,createSiteCode,e);
                sortingJsfResponse.setCode(SortingJsfResponse.CODE_SERVICE_ERROR);
                sortingJsfResponse.setMessage(SortingJsfResponse.MESSAGE_SERVICE_ERROR_C);
            }

        }
        return sortingJsfResponse;
    }

	public Integer inspectionCount(Inspection inspection) {
		return inspectionDao.inspectionCount(inspection);
	}
	public Integer inspectionCountByWaybill(Inspection inspection) {
		return inspectionDao.inspectionCountByWaybill(inspection);
	}
	/**
	 * 插入pda操作日志表
	 *
	 * @param inspection
	 */
	private void addOperationLog(Inspection inspection,String methodName) {
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
		operationLog.setMethodName(methodName);
		operationLogService.add(operationLog);
	}

	@Override
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

		addOperationLog(inspectionPop,"InspectionServiceImpl#addInspectionPop");

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
		inspAS.setBizSource(uPackage.getBizSource());
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
		InspectionResult result =  new InspectionResult("");
		BaseEntity<BigWaybillDto> baseEntity = waybillQueryManager.getDataByChoice(waybillCode, true, false, false, false);
		if (baseEntity != null && baseEntity.getData() != null && baseEntity.getData().getWaybill() != null) {
			// 获取运单信息
			Waybill waybill = baseEntity.getData().getWaybill();
			if(waybill.getGoodNumber() != null){
				result.setPackageSize(waybill.getGoodNumber());
			}
			if(dmsSiteCode == null || waybill.getProvinceId() ==null){
				return result;
			}
			DmsStorageArea newDmsStorageArea = dmsStorageAreaService.findByDmsSiteAndWaybillAdress(dmsSiteCode,waybill.getProvinceId(),waybill.getCityId());
			if(newDmsStorageArea != null) {
				result.setStorageCode(newDmsStorageArea.getStorageCode());
			}
		}else{
			this.log.warn("通过运单号获取运单信息失败：{}" , waybillCode);
		}
		return result;
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
		if(waybill != null){
			//预分拣站点
			preSiteCode = waybill.getSiteCode();
			BaseStaffSiteOrgDto bDto = siteService.getSite(preSiteCode);
			if(bDto != null && bDto.getDmsId() != null){
				//末级分拣中心
				destinationDmsId = bDto.getDmsId();
			}
			//是否是金鹏订单
			if(BusinessUtil.isPerformanceOrder(waybill.getWaybillSign())){
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
							hintMessage = HintService.getHint(HintCodeConstants.JP_TEMP_STORE_TOGETHER);
						}
					}
				}

			}else if(BusinessUtil.isEdn(waybill.getSendPay(), waybill.getWaybillSign())){
				BaseStaffSiteOrgDto destinationDmsInfo = siteService.getSite(destinationDmsId);
				//判断末级分拣中心、企配仓类型
				if(destinationDmsInfo != null
						&& Objects.equals(destinationDmsId,dmsSiteCode)
                        && BusinessUtil.isEdnDmsSite(destinationDmsInfo.getSubType())){
					hintMessage = HintService.getHint(HintCodeConstants.QPC_TEMP_STORE);
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

		BaseEntity<List<DeliveryPackageD>> packageListRet = waybillPackageManager.getPackListByWaybillCode(waybillCode);
        if (null == packageListRet ||
            EnumBusiCode.BUSI_SUCCESS.getCode() != packageListRet.getResultCode() ||
            CollectionUtils.isEmpty(packageListRet.getData())
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

        List<DeliveryPackageD> packageList = packageListRet.getData();
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

    /**
     * 校验运单号是否绑定集包袋
     *
     * @param waybillCode
     * @return
     */
    @Override
    public boolean checkIsBindMaterial(String waybillCode) {

        try {
        	if(uccPropertyConfiguration.getInspectionAssertDemotion()){
        		//降级 不依赖集包袋服务
        		return false;
			}
            MatterPackageRelationDto dto = new MatterPackageRelationDto();
            dto.setWaybillCode(waybillCode);
            ResultData<List<String>> result = assertQueryManager.queryBindMaterialByCode(dto);
            if(result == null){
                log.warn("校验运单号【{}】是否绑定集包袋返回值为空...",waybillCode);
                return false;
            }
            if(!ResultStateEnum.RESULT_SUCCESS.getStatusCode().equals(result.getStatusCode())){
                log.warn("校验运单号【{}】是否绑定集包袋失败,失败信息:【{}】",waybillCode,result.getResultMsg());
                return false;
            }
            if(CollectionUtils.isNotEmpty(result.getData())){
                return true;
            }else {
                return false;
            }
        }catch (Exception e){
            log.error("校验运单号【{}】是否绑定集包袋异常",waybillCode,e);
			return false;
        }
    }

    @Override
    public InspectionExeModeEnum findInspectionExeMode(InspectionRequest request) {

        if (request.getPageNo() > 0 && request.getPageSize() > 0) {
            return InspectionExeModeEnum.PACKAGE_PAGE_MODE;
        }

        String code = request.getPackageBarOrWaybillCode();
        boolean isByWayBillCode = WaybillUtil.isWaybillCode(code);
		if(isByWayBillCode) {
			request.setWaybillInspectionFlag(true);
		}

        // 大运单包裹数超过上限，验货拆分任务执行
        boolean executeBySplitTask = isByWayBillCode && satisfyWaybillSplitCondition(request);

        if (executeBySplitTask) {
            return InspectionExeModeEnum.INIT_SPLIT_MODE;
        }
        else {
            return InspectionExeModeEnum.NONE_SPLIT_MODE;
        }
    }

    /**
     * 满足拆分验货任务的条件
     * <ul>
     *     <li>UCC配置站点开启拆分任务开关</li>
     *     <li>运单包裹数量超过配置的数量</li>
     * </ul>
     * @param request
     * @return
     */
    private boolean satisfyWaybillSplitCondition(InspectionRequest request) {
        return siteEnableSplitWaybill(request.getSiteCode())
                && bigWaybillTask(request);
    }

    /**
     * 判断运单是否是大包裹
     * @param request
     * @return
     */
    private boolean bigWaybillTask(InspectionRequest request) {

        String waybillCode = WaybillUtil.getWaybillCode(request.getPackageBarOrWaybillCode());
        BigWaybillDto bigWaybillDto = getWaybill(waybillCode);

        if (bigWaybillDto != null
                && bigWaybillDto.getWaybill() != null
                && bigWaybillDto.getWaybill().getGoodNumber() != null) {

            return bigWaybillDto.getWaybill().getGoodNumber() >= getInspectionTaskPackageSplitNum();
        }

        return false;
    }

    private boolean siteEnableSplitWaybill(Integer siteCode) {

        return true;
    }

    /**
     * 运单多包裹限制数量
     */
    private static final int BIG_WAYBILL_LIMIT_NUM = 100;

    /**
     * 取得运单多包裹数量触发上限
     * @return
     */
    @Override
    public int getInspectionTaskPackageSplitNum() {
        return 0 == uccConfig.getWaybillSplitPageSize() ?
                BIG_WAYBILL_LIMIT_NUM :
                uccConfig.getWaybillSplitPageSize();
    }

    @Override
    public boolean siteEnableInspectionAgg(Integer siteCode) {
        String configSite = uccConfig.getInspectionAggEffectiveSites();
        if (StringUtils.isBlank(configSite)) {
            return false;
        }

        if (Constants.STR_ALL.equalsIgnoreCase(configSite)) {
            return true;
        }

        List<String> sites = null;
        try {
            sites = Arrays.asList(StringUtils.split(configSite, Constants.SEPARATOR_COMMA));
        }
        catch (Exception ex) {
            log.error("transfer inspection split waybill site error.", ex);
        }

        if (CollectionUtils.isEmpty(sites) || null == siteCode) {
            return false;
        }

        return sites.contains(siteCode.toString());
    }

	@Override
	public InvokeResult<Boolean> addInspection(InspectionVO vo, InspectionBizSourceEnum inspectionBizSourceEnum) {
		return inspection(vo, inspectionBizSourceEnum);
	}


	@Override
    public InvokeResult<Boolean> inspection(InspectionVO vo, InspectionBizSourceEnum inspectionBizSourceEnum){
		InvokeResult<Boolean> result = new InvokeResult<>();
		result.setData(Boolean.TRUE);
		result.success();

		//拆分运单包裹列表  拆分箱号列表
		List<TaskRequest> reqs =  changeToInspectionReq(vo, inspectionBizSourceEnum);

		for(TaskRequest req : reqs){
			TaskResponse taskResponse = taskService.add(req);
			if(!com.jd.bluedragon.distribution.api.JdResponse.CODE_OK.equals(taskResponse.getCode())){
				//失败阻断 允许重试幂等即可
				result.setData(Boolean.FALSE);
				result.customMessage(JdResponse.CODE_FAIL,taskResponse.getMessage());
				return result;
			}
		}
		return result;
	}


	/**
	 * {"sealBoxCode":null,"boxCode":null,"packageBarOrWaybillCode":"JDVA00182404142-1-1-","exceptionType":null,"operateType":0,"receiveSiteCode":0,"bizSource":31,"id":0,"businessType":10,"userCode":10053,"userName":"刑松","siteCode":39,"siteName":"石景山营业部","operateTime":"2021-05-03 20:57:34.000"}
	 * {"shieldsCarCode":null,"carCode":null,"sealBoxCode":null,"packOrBox":"BC1001210427200014348517","turnoverBoxCode":null,"queueNo":null,"departureCarId":null,"shieldsCarTime":null,"id":0,"businessType":10,"userCode":10053,"userName":"刑松","siteCode":39,"siteName":"石景山营业部","operateTime":"2021-05-03 21:00:00.642"}
	 * @param vo
	 * @return
	 */
	private List<TaskRequest> changeToInspectionReq(InspectionVO vo, InspectionBizSourceEnum inspectionBizSourceEnum){

		List<Map<String,String>> boxes = new ArrayList<>();
		List<Map<String,String>> others = new ArrayList<>();
		makeToInspectionBody( vo,boxes,others, inspectionBizSourceEnum);

		List<TaskRequest> requests = new ArrayList<>();
		for(Map<String,String> boxBody : boxes){
			TaskRequest request = new TaskRequest();
			request.setType(Task.TASK_TYPE_RECEIVE);
			request.setBody(Constants.PUNCTUATION_OPEN_BRACKET
					+ JsonHelper.toJson(boxBody)
					+ Constants.PUNCTUATION_CLOSE_BRACKET);
			request.setKeyword1(String.valueOf(vo.getSiteCode()));
			request.setReceiveSiteCode(vo.getSiteCode());
			request.setSiteCode(vo.getSiteCode());
			request.setBoxCode(boxBody.get("packOrBox"));
			requests.add(request);
		}

		for(Map<String,String> otherBody : others){
			TaskRequest request = new TaskRequest();
			request.setType(Task.TASK_TYPE_INSPECTION);
			request.setBody(Constants.PUNCTUATION_OPEN_BRACKET
					+ JsonHelper.toJson(otherBody)
					+ Constants.PUNCTUATION_CLOSE_BRACKET);
			request.setKeyword1(String.valueOf(vo.getSiteCode()));
			request.setReceiveSiteCode(vo.getSiteCode());
			request.setSiteCode(vo.getSiteCode());
			requests.add(request);
		}

		return requests;

	}

	/**
	 * {"sealBoxCode":null,"boxCode":null,"packageBarOrWaybillCode":"JDVA00182404142-1-1-","exceptionType":null,"operateType":0,"receiveSiteCode":0,"bizSource":31,"id":0,"businessType":10,"userCode":10053,"userName":"刑松","siteCode":39,"siteName":"石景山营业部","operateTime":"2021-05-03 20:57:34.000"}
	 * {"shieldsCarCode":null,"carCode":null,"sealBoxCode":null,"packOrBox":"BC1001210427200014348517","turnoverBoxCode":null,"queueNo":null,"departureCarId":null,"shieldsCarTime":null,"id":0,"businessType":10,"userCode":10053,"userName":"刑松","siteCode":39,"siteName":"石景山营业部","operateTime":"2021-05-03 21:00:00.642"}
	 * @param vo
	 * @return
	 */
	private void makeToInspectionBody(InspectionVO vo,List<Map<String,String>> boxes,List<Map<String,String>> others,
									  InspectionBizSourceEnum inspectionBizSourceEnum){

		for(String barCode : vo.getBarCodes()){
			Map<String,String> map = new HashMap<>();
			map.put("userCode",String.valueOf(vo.getUserCode()));
			map.put("userName",vo.getUserName());
			map.put("siteCode",String.valueOf(vo.getSiteCode()));
			map.put("siteName",vo.getSiteName());
			map.put("operateTime",vo.getOperateTime());
			map.put("businessType",String.valueOf(Constants.BUSSINESS_TYPE_POSITIVE));
			if(inspectionBizSourceEnum != null){
				map.put("bizSource",inspectionBizSourceEnum.getCode().toString());
			}
			if (StringUtils.isNotEmpty(vo.getMachineCode())){
				map.put("machineCode",vo.getMachineCode());
			}
			if(BusinessUtil.isBoxcode(barCode)){
				//箱号
				map.put("packOrBox",barCode);
				boxes.add(map);
			}else{
				//非箱号
				map.put("packageBarOrWaybillCode",barCode);

				others.add(map);
			}
		}
	}

	/**
	 * @param request
	 * @return InvokeResult
	 * @throws Exception
	 * @author chenlingfeng 2022/08/03-revised
	 * @description 将web包中的autoAddInspectionTask的调用方式由REST改为JSF
	 */
	@Override
	public InvokeResult<Void> autoAddInspectionTask(TaskRequest request) {
		TaskResponse response = null;
		InvokeResult<Void> invokeResult = new InvokeResult<Void>();
		if (StringUtils.isBlank(request.getBody())) {
			invokeResult.customMessage(InvokeResult.PARAMETER_ERROR_CODE,"参数错误,body内容为空");
			this.log.info("参数错误，body内容为空，request:[{}]", JsonHelper.toJson(request));
			return invokeResult;
		}
		List<InspectionAS> inspections = JsonHelper.jsonToList(request.getBody(), InspectionAS.class);
		if (CollectionUtils.isEmpty(inspections)) {
			invokeResult.customMessage(InvokeResult.PARAMETER_ERROR_CODE,"body格式错误，内容反序列化后为空");
			this.log.info("body格式错误，body:[{}]，内容反序列化后为空，request:[{}]", inspections.toString(), JsonHelper.toJson(request));
			return invokeResult;
		}
		//过滤妥投的运单
		Iterator<InspectionAS> it = inspections.iterator();
		while (it.hasNext()) {
			InspectionAS inspection = it.next();
			String waybillCode = WaybillUtil.getWaybillCode(inspection.getPackageBarOrWaybillCode());
			if (StringUtils.isBlank(waybillCode)) {
				log.warn("验货数据{}非包裹或运单号", inspection.getPackageBarOrWaybillCode());
				it.remove();
			}
			if (waybillTraceManager.isWaybillFinished(waybillCode)) {
				log.warn("运单{}已妥投", waybillCode);
				it.remove();
			}

			//inspection.setBizSource(InspectionBizSourceEnum.AUTOMATIC_SORTING_MACHINE_INSPECTION.getCode());
			inspection.setOperatorTypeCode(OperatorTypeEnum.AUTO_MACHINE.getCode());
			inspection.setOperatorId(inspection.getMachineCode());
		}
		if (inspections.size() == 0) {
			invokeResult.customMessage(com.jd.bluedragon.distribution.api.JdResponse.CODE_OK, com.jd.bluedragon.distribution.api.JdResponse.MESSAGE_OK);
			return invokeResult;
		}
		request.setBody(JsonHelper.toJson(inspections));
		try {
			response = taskService.add(request);
			if (Objects.equals(response.getCode(), com.jd.bluedragon.distribution.api.JdResponse.CODE_OK)) {
				invokeResult.success();
			} else {
				invokeResult.customMessage(InvokeResult.SERVICE_FAIL_CODE, InvokeResult.SERVICE_FAIL_MESSAGE);
				this.log.info("InspectionService调用taskService.add返回响应码错误，request:[{}]，response code:[{}], response message:[{}]",
						JsonHelper.toJson(request), response.getCode(), response.getMessage());
			}
			return invokeResult;
		} catch (Exception e) {
			log.error("InspectionService调用taskService.add发生异常，request:[{}]，response code:[{}], response message:[{}]",
					JsonHelper.toJson(request), response.getCode(), response.getMessage(), e);
			invokeResult.customMessage(InvokeResult.SERVICE_ERROR_CODE, InvokeResult.SERVICE_ERROR_MESSAGE);
			return invokeResult;
		}
	}

    @Override
    public JdVerifyResponse<InspectionCheckResultDto> checkBeforeInspection(com.jd.bluedragon.common.dto.inspection.request.InspectionRequest request) {
        JdVerifyResponse<InspectionCheckResultDto> response = new JdVerifyResponse<>();
        response.toSuccess();

        String barCode = request.getBarCode();

        // 加盟商余额校验
        checkAllianceMoney(response, request);

        // 暂存校验
        tempStorageCheck(request, response);

        // 提示语校验
        HintCheckRequest hintCheckRequest = new HintCheckRequest();
        hintCheckRequest.setPackageCode(barCode);
        hintCheckRequest.setCreateSiteCode(request.getCreateSiteCode());
        hintCheckRequest.setNewInspectionCheck(true);

        JdCResponse<InspectionCheckResultDto> hintCheckResult = hintCheck(hintCheckRequest);
        if (!Objects.equals(hintCheckResult.getCode(), com.jd.bluedragon.distribution.wss.dto.BaseEntity.CODE_SUCCESS)) {
            response.toError(hintCheckResult.getMessage());
            return response;
        }
        else {
            response.setData(hintCheckResult.getData());

            if (StringUtils.isNotBlank(hintCheckResult.getData().getInspectionResultDto().getHintMessage())) {
                response.addWarningBox(0, hintCheckResult.getData().getInspectionResultDto().getHintMessage());
            }
            if (StringUtils.isNotBlank(hintCheckResult.getData().getConsumableRecordResponseDto().getHintMessage())) {
                response.addWarningBox(0, hintCheckResult.getData().getConsumableRecordResponseDto().getHintMessage());
            }

            // 拦截校验
            checkWaybillCancel(request, response);
        }

        return response;
    }

    /**
     * 加盟商余额校验
     * @param response
     * @param request
     * @return
     */
    private void checkAllianceMoney(JdVerifyResponse<InspectionCheckResultDto> response, com.jd.bluedragon.common.dto.inspection.request.InspectionRequest request) {
        String waybillCode = WaybillUtil.getWaybillCode(request.getBarCode());
        if (StringUtils.isNotBlank(waybillCode)) {
            if (!allianceBusiDeliveryDetailService.checkExist(waybillCode)) {
                if (!allianceBusiDeliveryDetailService.checkMoney(waybillCode)) {
                    response.addWarningBox(0, InspectionCheckResult.ALLIANCE_INTERCEPT_MESSAGE);
                }
            }
        }
    }

    /**
     * 暂存校验
     * @param request
     * @param response
     */
    private void tempStorageCheck(com.jd.bluedragon.common.dto.inspection.request.InspectionRequest request, JdVerifyResponse<InspectionCheckResultDto> response) {
        String waybillCode = WaybillUtil.getWaybillCode(request.getBarCode());
        if (StringUtils.isBlank(waybillCode)) {
            return;
        }

        com.jd.bluedragon.distribution.base.domain.InvokeResult<Boolean> tempStorageResult = storagePackageMService.checkIsNeedStorage(waybillCode, request.getCreateSiteCode());
        if (tempStorageResult.getCode() == 201) {
            if (tempStorageResult.getData()) {
                response.addWarningBox(0, tempStorageResult.getMessage());
            }
            else {
                response.addPromptBox(0, tempStorageResult.getMessage());
            }
        }
        else if (response.getCode() == JdCResponse.CODE_FAIL
                || response.getCode() == JdCResponse.CODE_ERROR) {
            response.addWarningBox(0, tempStorageResult.getMessage());
        }
    }

    public JdCResponse<InspectionCheckResultDto> hintCheck(HintCheckRequest request) {

        JdCResponse<InspectionCheckResultDto> resultDto = new JdCResponse<InspectionCheckResultDto>();
        resultDto.toSucceed();
        InspectionCheckResultDto inspectionCheckResultDto = new InspectionCheckResultDto();
        resultDto.setData(inspectionCheckResultDto);

        // 老验货需校验菜单是否可用
        if(!request.getNewInspectionCheck()){
            ImmutablePair<Boolean, String> checkResult = baseService.checkMenuIsAvailable(Constants.MENU_CODE_INSPECTION, request.getCreateSiteCode());
            if(!checkResult.getLeft()){
                resultDto.toError(checkResult.getRight());
                return resultDto;
            }
        }

        //获取储位号
        JdResponse<InspectionResult> storageResponse = this.getStorageCode(request.getPackageCode(), request.getCreateSiteCode());
        if (!Objects.equals(storageResponse.getCode(), JdResponse.CODE_SUCCESS)) {
            resultDto.toError(storageResponse.getMessage());
            return resultDto;
        }
        if (storageResponse.getData() != null) {
            InspectionResultDto dto = new InspectionResultDto();
            dto.setStorageCode(storageResponse.getData().getStorageCode());
            dto.setHintMessage(storageResponse.getData().getHintMessage());
            dto.setTabletrolleyCode(storageResponse.getData().getTabletrolleyCode());
            inspectionCheckResultDto.setInspectionResultDto(dto);
        }

        //运单是否存在待确认的包装任务
        String waybillCode = request.getPackageCode();
        if (WaybillUtil.isPackageCode(request.getPackageCode())) {
            waybillCode = WaybillUtil.getWaybillCode(request.getPackageCode());
        }
        JdCResponse<ConsumableRecordResponseDto> jdCResponse = this.isExistConsumableRecord(waybillCode);
        inspectionCheckResultDto.setConsumableRecordResponseDto(jdCResponse.getData());

        return resultDto;
    }

    public JdCResponse<ConsumableRecordResponseDto> isExistConsumableRecord(String waybillCode) {
        JdCResponse<ConsumableRecordResponseDto> jdCResponse = new JdCResponse<>();
        ConsumableRecordResponseDto consumableRecordResponseDto = new ConsumableRecordResponseDto();
        jdCResponse.setData(consumableRecordResponseDto);
        jdCResponse.toSucceed();
        if (StringUtils.isEmpty(waybillCode)) {
            jdCResponse.toFail("单号不能为空");
            return jdCResponse;
        }

        JdResponse<Boolean> jdResponse = dmsPackingConsumableService.getConfirmStatusByWaybillCode(waybillCode);
        if (jdCResponse.isSucceed() && jdResponse.getData() != null) {
            consumableRecordResponseDto.setExistConsumableRecord(jdResponse.getData());
            consumableRecordResponseDto.setHintMessage(jdResponse.getMessage());
        }

        return jdCResponse;
    }

    private void checkWaybillCancel(com.jd.bluedragon.common.dto.inspection.request.InspectionRequest request, JdVerifyResponse<InspectionCheckResultDto> response) {
        PdaOperateRequest pdaOperateRequest = new PdaOperateRequest();
        pdaOperateRequest.setPackageCode(request.getBarCode());
        pdaOperateRequest.setBusinessType(request.getBusinessType());
        pdaOperateRequest.setCreateSiteCode(request.getCreateSiteCode());
        pdaOperateRequest.setCreateSiteName(request.getCreateSiteName());
        pdaOperateRequest.setOperateUserCode(request.getOperateUserCode());
        pdaOperateRequest.setOperateUserName(request.getOperateUserName());
        pdaOperateRequest.setOperateTime(request.getOperateTime());
        pdaOperateRequest.setOperateType(request.getOperateType());

        JdCancelWaybillResponse cancelWaybillResponse = waybillService.dealCancelWaybill(pdaOperateRequest);
        if (!Objects.equals(JdResponse.CODE_SUCCESS, cancelWaybillResponse.getCode())) {
            response.addWarningBox(0, cancelWaybillResponse.getMessage());
        }
    }
}
