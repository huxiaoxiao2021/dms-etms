package com.jd.bluedragon.distribution.rest.sorting;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.core.base.WaybillQueryManager;
import com.jd.bluedragon.distribution.api.JdResponse;
import com.jd.bluedragon.distribution.api.request.ReturnsRequest;
import com.jd.bluedragon.distribution.api.request.SortingRequest;
import com.jd.bluedragon.distribution.api.response.BoxResponse;
import com.jd.bluedragon.distribution.api.response.SortingResponse;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.inspection.domain.Inspection;
import com.jd.bluedragon.distribution.inspection.domain.InspectionEC;
import com.jd.bluedragon.distribution.inspection.service.InspectionExceptionService;
import com.jd.bluedragon.distribution.inspection.service.InspectionService;
import com.jd.bluedragon.distribution.middleend.sorting.dao.DynamicSortingQueryDao;
import com.jd.bluedragon.distribution.operationLog.domain.OperationLog;
import com.jd.bluedragon.distribution.middleend.SortingServiceFactory;
import com.jd.bluedragon.distribution.send.dao.SendMDao;
import com.jd.bluedragon.distribution.send.domain.SendM;
import com.jd.bluedragon.distribution.sorting.domain.Sorting;
import com.jd.bluedragon.distribution.sorting.domain.SortingReturn;
import com.jd.bluedragon.distribution.sorting.service.SortingReturnService;
import com.jd.bluedragon.distribution.sorting.service.SortingService;
import com.jd.bluedragon.distribution.task.domain.Task;
import com.jd.bluedragon.distribution.task.service.TaskService;
import com.jd.bluedragon.dms.utils.DmsConstants;
import com.jd.bluedragon.dms.utils.WaybillUtil;
import com.jd.bluedragon.utils.BusinessHelper;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.bluedragon.utils.StringHelper;
import com.jd.dms.logger.annotation.BusinessLog;
import com.jd.ql.dms.common.cache.CacheService;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Component
@Path(Constants.REST_URL)
@Consumes({ MediaType.APPLICATION_JSON })
@Produces({ MediaType.APPLICATION_JSON })
public class SortingResource {

	private final Logger log = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private TaskService taskService;

	@Autowired
	private SortingService sortingService;

	@Autowired
	private SortingReturnService returnsService;

	@Autowired
	private InspectionExceptionService inspectionExcpetionService;

	@Autowired
	private InspectionService inspectionService;

	@Autowired
	private SendMDao sendMDao;

	@Autowired
	private DynamicSortingQueryDao dynamicSortingQueryDao;

    @Autowired
    @Qualifier("jimdbCacheService")
    private CacheService cacheService;

    @Autowired
	private SortingServiceFactory sortingServiceFactory;
	/**
	 * 取消分拣
	 * 
	 * @param request
	 * @return
	 */
	@PUT
	@Path("/sorting/cancel")
	@BusinessLog(sourceSys = 1,bizType = 2002)
	@JProfiler(jKey = "DMSWEB.SortingResource.cancelPackage", mState = JProEnum.TP, jAppName = Constants.UMP_APP_NAME_DMSWEB)
	public SortingResponse cancelPackage(SortingRequest request) {
		if(log.isDebugEnabled()){
			this.log.debug("取消分拣参数：{}" , JsonHelper.toJson(request));
		}
		if (StringHelper.isEmpty(request.getPackageCode())) {
			return SortingResponse.paramIsNull();
		}
		if (!WaybillUtil.isPackageCode(request.getPackageCode())
				&& !WaybillUtil.isWaybillCode(request.getPackageCode())
				&& !BusinessHelper.isBoxcode(request.getBoxCode())
				&& !BusinessHelper.isBoxcode(request.getPackageCode())) {
			return SortingResponse.paramIsError();
		}

		List<Task> tasks = this.findWaitingProcessSortingTasks(request);
		if (!tasks.isEmpty()) {
			return SortingResponse.waitingProcess();
		}

		boolean isSuccess = false;
        String fingerPrintKey = "SORTING_CANCEL" + request.getSiteCode() +"|"+ request.getPackageCode();
        try {
            //判断是否重复取消分拣, 5分钟内如果同操作场地、同扫描号码只允许取消一次分拣。
            isSuccess = cacheService.setNx(fingerPrintKey, "1", 5*60*1000, TimeUnit.SECONDS);
			//说明key存在
			if (! isSuccess) {
				this.log.warn("{}正在执行取消分拣任务！",request.getPackageCode() );
				return SortingResponse.waitingCancelProcess();
			}
        } catch (Exception e) {
            this.log.error("{}获取取消发货任务缓存失败！", request.getPackageCode(), e);
        }

		Sorting sorting = Sorting.toSorting2(request);

		try {
			return sortingServiceFactory.getSortingService(sorting.getCreateSiteCode()).cancelSorting(sorting);
		} catch (Exception e) {
			log.error("{}取消分拣服务异常",request.getPackageCode(), e);
		} finally {
			if (isSuccess) {
				cacheService.del(fingerPrintKey);
			}
		}

		return SortingResponse.sortingNotFund();
	}

	/**
	 * 亚一取消分拣
	 * 
	 * @param sortingRequest
	 * @return
	 */
	@POST
	@Path("/sorting/batchCancel")
	public SortingResponse cancelWarehouse(SortingRequest sortingRequest) {

		Map<String, Integer> results = new HashMap<String, Integer>();

		if (sortingRequest.getPackages() == null || sortingRequest.getPackages().isEmpty()) {
			return SortingResponse.paramIsNull();
		}

		try {
			for (String packageCode : sortingRequest.getPackages()) {
				SortingRequest request = new SortingRequest();
				BeanUtils.copyProperties(request, sortingRequest);
				request.setPackageCode(packageCode);

				if (!WaybillUtil.isPackageCode(request.getPackageCode())
						&& !WaybillUtil.isWaybillCode(request.getPackageCode())
						&& !BusinessHelper.isBoxcode(request.getBoxCode())
						&& !BusinessHelper.isBoxcode(request.getPackageCode())) {
					results.put(packageCode, JdResponse.CODE_OK);
					continue;
				}

				List<Task> tasks = this.findWaitingProcessSortingTasks(request);
				if (!tasks.isEmpty()) {
					results.put(packageCode, JdResponse.CODE_WRONG_STATUS);
					continue;
				}

				Sorting sorting = Sorting.toSorting2(request);

				if (StringUtils.isNotBlank(sorting.getBoxCode())) {
					// 校验是否发货，如果已经发货，则提示不能取消分拣
					SendM sendM = new SendM();
					sendM.setBoxCode(sorting.getBoxCode());
					sendM.setCreateSiteCode(sorting.getCreateSiteCode());
					List<SendM> sendMList = sendMDao.findSendMByBoxCode(sendM);
					if (null != sendMList && !sendMList.isEmpty() && sendMList.size() > 0) {
						results.put(packageCode, SortingResponse.CODE_SORTING_SENDED);
						continue;
					}

					if (this.sortingService.canCancel(sorting)) {
						results.put(packageCode, JdResponse.CODE_OK);
					} else {
						results.put(packageCode, SortingResponse.CODE_SORTING_RECORD_NOT_FOUND);
						continue;
					}
				}
			}
			return new SortingResponse(JdResponse.CODE_OK, JdResponse.MESSAGE_OK, results);
		} catch (Throwable e) {
			this.log.error("取消分拣异常:{}",JsonHelper.toJson(sortingRequest), e);
			return new SortingResponse(JdResponse.CODE_PARAM_ERROR, JdResponse.MESSAGE_PARAM_ERROR);
		}
	}

	private List<Task> findWaitingProcessSortingTasks(SortingRequest request) {
		Task task = new Task();
		task.setTableName(Task.TABLE_NAME_SORTING);
		task.setCreateSiteCode(request.getSiteCode());
		task.setBoxCode(request.getBoxCode());
		List<Integer> statusesList = new ArrayList<>(2);
        statusesList.add(Task.TASK_STATUS_UNHANDLED);
        statusesList.add(Task.TASK_STATUS_PROCESSING);
        task.setStatusesList(statusesList);
		return this.taskService.findTasks(task);
	}


	@GET
	@Path("/sorting/box")
	public List<SortingResponse> queryPackages(@QueryParam("boxCode") String boxCode,
			@QueryParam("siteCode") Integer siteCode) {
		this.log.debug("boxCode is {}" , boxCode);
		this.log.debug("siteCode is {}" , siteCode);

		Sorting sorting = new Sorting();
		sorting.setBoxCode(boxCode);
		sorting.setCreateSiteCode(siteCode);

		List<Sorting> sortingPackages = this.sortingService.findByBoxCode(sorting);

		this.log.debug("sortingPackages's size : {}" , sortingPackages.size());
		List<SortingResponse> sortingResponses = new ArrayList<SortingResponse>();
		for (Sorting waybillPackage : sortingPackages) {
			sortingResponses.add(this.toSortingResponse(waybillPackage));
		}

		return sortingResponses;
	}

	/**
	 * 处理分拣退货的业务
	 * 
	 * @param request
	 * @return
	 */
	// @POST
	// @Path("/sorting/return")
	public SortingResponse dealReturn(ReturnsRequest request) {
		this.log.debug("packagecode is {}" , request.getPackageCode());
		this.log.debug("开始进行分拣退货");
		SortingReturn returns = SortingReturn.parse(request);

		try {
			Integer result = this.returnsService.doAddReturn(returns);
			this.log.debug("分拣退货处理结果返回 ：{}" , result);
			if (this.returnsService.ADDRETURN_OK.equals(result)) {
				/** 操作成功 */
				SortingResponse response = new SortingResponse(JdResponse.CODE_OK, JdResponse.MESSAGE_OK);
				return response;
			} else if (this.returnsService.ADDRETURN_ERROR.equals(result)) {
				/** 数据库异常 */
				SortingResponse response = new SortingResponse(JdResponse.CODE_RETURN_ERROR,
						JdResponse.MESSAGE_RETURN_ERROR);
				return response;
			} else if (this.returnsService.ADDRETURN_ISSEND.equals(result)) {
				/** 已经发货不允许退货 */
				SortingResponse response = new SortingResponse(JdResponse.CODE_WRONG_STATUS,
						JdResponse.MESSAGE_RETURN_ISSEND);
				return response;
			} else {
				/** 服务异常 */
				SortingResponse response = new SortingResponse(JdResponse.CODE_SERVICE_ERROR,
						JdResponse.MESSAGE_SERVICE_ERROR);
				return response;
			}
		} catch (Exception e) {
			this.log.error("调用ReturnsService服务异常:{}",JsonHelper.toJson(request), e);
			/** 服务异常 */
			SortingResponse response = new SortingResponse(JdResponse.CODE_SERVICE_ERROR,
					JdResponse.MESSAGE_SERVICE_ERROR);
			return response;
		}
	}

	/**
	 * 是否已经操作了分拣退库
	 * 
	 * @return
	 */
	@GET
	@Path("/sortingRet/haveSortingRet")
	public SortingResponse isSortingRet(@QueryParam("packageCode") String packageCodeOrWaybillCode) {
		try {
			this.log.debug("调用SortingResource.haveSortingRet 判断是否已经操作分拣退货[{}]", packageCodeOrWaybillCode);
			Boolean result = returnsService.exists(packageCodeOrWaybillCode);
			SortingResponse response = null;
			if (!result) {
				this.log.warn("调用SortingResource.haveSortingRet 未操作分拣退货[{}]", packageCodeOrWaybillCode);
				response = new SortingResponse(SortingResponse.CODE_NOT_EXIST_SORTINGRET,
						SortingResponse.MESSAGE_NOT_EXIST_SORTINGRET);
			} else {
				this.log.warn("调用SortingResource.haveSortingRet 已操作分拣退货[{}]", packageCodeOrWaybillCode);
				response = SortingResponse.ok();
			}
			return response;
		} catch (Exception e) {
			log.error("SortingResource.isSortingRet:packageCodeOrWaybillCode={}",packageCodeOrWaybillCode, e);
			SortingResponse response = new SortingResponse(SortingResponse.CODE_SERVICE_ERROR,
					SortingResponse.MESSAGE_SERVICE_ERROR);
			return response;
		}
	}

	/**
	 * 查询包裹是否进行过站点发调度操作
	 * 
	 * @param packageCode
	 * @return
	 */
	@GET
	@Path("/sortingRet/checkReDispatch")
	public SortingResponse checkReDispatch(@QueryParam("packageCode") String packageCode) {
		try {
			this.log.debug("调用SortingResource.checkReDispatch 判断是否已经操作站点反调度[{}]", packageCode);
			Integer result = returnsService.checkReDispatch(packageCode);
			SortingResponse response = null;
			if (result.equals(WaybillQueryManager.REDISPATCH_ERROR)) {
				this.log.warn("调用SortingResource.checkReDispatch WSS接口操作失败[{}]", packageCode);
				response = new SortingResponse(SortingResponse.CODE_SERVICE_ERROR,
						SortingResponse.MESSAGE_SERVICE_ERROR);
			} else if (result.equals(WaybillQueryManager.REDISPATCH_NO)) {
				this.log.warn("调用SortingResource.checkReDispatch 未操作站点反调度[{}]", packageCode);
				response = SortingResponse.ok();
			} else {
				this.log.warn("调用SortingResource.checkReDispatch 已操作站点反调度[{}]", packageCode);
				response = new SortingResponse(SortingResponse.CODE_REDISPATCH, SortingResponse.MESSAGE_REDISPATCH);
			}
			return response;
		} catch (Exception e) {
			log.error("SortingResource.checkReDispatch:packageCode={}",packageCode, e);
			SortingResponse response = new SortingResponse(SortingResponse.CODE_SERVICE_ERROR,
					SortingResponse.MESSAGE_SERVICE_ERROR);
			return response;
		}
	}

	private SortingResponse toSortingResponse(Sorting sorting) {
		SortingResponse response = SortingResponse.ok();
		response.setBoxCode(sorting.getBoxCode());
		response.setCreateSiteCode(sorting.getCreateSiteCode());
		response.setCreateTime(sorting.getCreateTime());
		response.setCreateUserCode(sorting.getCreateUserCode());
		response.setPackageCode(sorting.getPackageCode());
		response.setWaybillCode(sorting.getWaybillCode());
		response.setReceiveSiteCode(sorting.getReceiveSiteCode());
		return response;
	}


    @POST
    @Path("/sorting/pushStatusTask")
    public InvokeResult<Boolean> pushSortingTask(Sorting sorting) {
        InvokeResult<Boolean> res = new InvokeResult<Boolean>();
        sortingService.addSortingAdditionalTask(sorting);
        return res;
    }

	/**
	 * 根据箱号获取该箱号下的运单列表
	 *
	 * @param boxCode
	 * @return
	 */
	@GET
	@Path("/sorting/getWaybillCodes/{boxCode}")
	public InvokeResult<List<String>> getWaybillCodes(@PathParam("boxCode") String boxCode) {
		Assert.notNull(boxCode, "boxCode must not be null");
		this.log.debug("box code's {}" , boxCode);
		InvokeResult result = new InvokeResult();
		List<String> waybillList = sortingService.getWaybillCodeListByBoxCode(boxCode);
		if (waybillList == null) {
			result.customMessage(BoxResponse.CODE_BOX_NOT_FOUND, BoxResponse.MESSAGE_BOX_NOT_FOUND);
		}
		return result;
	}

}
