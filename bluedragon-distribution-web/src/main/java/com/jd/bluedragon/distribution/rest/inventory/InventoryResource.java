package com.jd.bluedragon.distribution.rest.inventory;

import com.alibaba.fastjson.JSON;
import com.jd.bluedragon.Constants;
import com.jd.bluedragon.common.domain.SiteEntity;
import com.jd.bluedragon.distribution.api.request.inventory.InventoryTaskRequest;
import com.jd.bluedragon.distribution.api.response.inventory.InventoryTaskResponse;
import com.jd.bluedragon.distribution.command.JdResult;
import com.jd.bluedragon.distribution.inventory.domain.*;
import com.jd.bluedragon.distribution.inventory.service.InventoryInfoService;
import com.jd.bluedragon.distribution.inventory.service.InventoryScanDetailService;
import com.jd.bluedragon.distribution.inventory.service.InventoryTaskService;
import com.jd.bluedragon.distribution.inventory.service.PackageStatusService;
import com.jd.bluedragon.dms.utils.WaybillUtil;
import com.jd.bluedragon.utils.StringHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.*;

@Component
@Path(Constants.REST_URL)
@Consumes({ MediaType.APPLICATION_JSON })
@Produces({ MediaType.APPLICATION_JSON })
public class InventoryResource {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private InventoryInfoService inventoryInfoService;

    @Autowired
    private InventoryTaskService inventoryTaskService;

    @Autowired
	private PackageStatusService packageStatusService;

    @Autowired
    private InventoryScanDetailService inventoryScanDetailService;
    /**
     * 获取卡位列表
     * @param siteCode
     * @return
     */
    @GET
    @Path("/inventory/getDirectionList/{siteCode}")
    public JdResult<List<SiteEntity>> getDirectionList(@PathParam("siteCode") Integer siteCode) {
        JdResult<List<SiteEntity>> result = new JdResult<List<SiteEntity>>();
        result.toSuccess();

        if (siteCode == null || siteCode <= 0) {
            result.toError("操作站点不能为空.");
        }
        List<SiteEntity> siteEntityList = new ArrayList<>();

        //调service的接口获取列表
        result.setData(siteEntityList);
        return result;
    }

    /**
     * 查询当前操作人是否有正在进行的任务
     * @param request
     * @return
     */
    @POST
    @Path("/inventory/getUserDoingInventoryTask")
    public JdResult<InventoryTaskResponse> getUserDoingInventoryTask(InventoryTaskRequest request) {
        logger.info("查询当前操作人是否有正在进行的任务,参数:" + JSON.toJSONString(request));
        JdResult result = new JdResult();
        result.toSuccess();

        try {
            return inventoryTaskService.getUserDoingInventoryTask(request);
        } catch (Exception e) {
            logger.error("查询当前操作人是否有正在进行的任务异常.参数:"+JSON.toJSONString(request)+",异常原因:", e);
            result.toError("查询当前操作人是否有正在进行的任务异常");
        }
        return result;
    }

    /**
     * 验证所选流向是否有正在进行的任务
     * @param request
     * @return
     */
    @POST
    @Path("/inventory/directionVerify")
    public JdResult<InventoryTaskResponse> directionVerify(InventoryTaskRequest request) {
        logger.info("流向验证,参数:" + JSON.toJSONString(request));
        JdResult result = new JdResult();
        result.toSuccess();

        try {
            return inventoryTaskService.directionVerify(request);
        } catch (Exception e) {
            logger.error("流向验证异常.参数:"+JSON.toJSONString(request)+",异常原因:", e);
            result.toError("流向验证异常");
        }
        return result;
    }


    /**
     * 生成盘点任务
     * @param request
     * @return
     */
    @POST
    @Path("/inventory/addInventoryTask")
    public JdResult<InventoryTaskResponse> addInventoryTask(InventoryTaskRequest request) {
        logger.info("生成盘点任务参数:" + JSON.toJSONString(request));
        JdResult result = new JdResult();
        result.toSuccess();

        try {
            return inventoryTaskService.addInventoryTask(request);
        } catch (Exception e) {
            logger.error("生成盘点任务异常.参数:"+JSON.toJSONString(request)+",异常原因:", e);
            result.toError("生成盘点任务异常");
        }
        return result;
    }

	/**
	 * 盘点信息接口
	 */
	@POST
	@Path("/inventory/info")
	public JdResult<InventoryWaybillResponse> getInventoryInfo(InventoryBaseRequest inventoryBaseRequest){
		logger.info("获取盘点信息,参数:" + JSON.toJSONString(inventoryBaseRequest));
		JdResult<InventoryWaybillResponse> result = new JdResult<>();
		result.toSuccess();

		InventoryWaybillResponse inventoryWaybillResponse = new InventoryWaybillResponse();
		//参数校验
		if (! this.checkBaseParams(inventoryBaseRequest, result) || ! this.checkType(inventoryBaseRequest, result) ||
				! this.checkInventoryScopeType(inventoryBaseRequest, result)) {
			return result;
		}

		Integer type = inventoryBaseRequest.getType();
		if (type == 1) {
			//1：初始化
			inventoryWaybillResponse = inventoryInfoService.initNeedInventoryWaybillInfo(inventoryBaseRequest);
		}
		else if (type == 2) {
			//2：刷新数据
			inventoryWaybillResponse = inventoryInfoService.syncCurrInventoryWaybillInfo(inventoryBaseRequest);
		}

		result.setData(inventoryWaybillResponse);

		return result;
	}

	/**
	 * 盘点明细接口
	 */
	@POST
	@Path("/inventory/detail")
	public JdResult<List<InventoryWaybillDetail>> getInventoryWaybillDetail(InventoryBaseRequest inventoryBaseRequest){
		logger.info("获取盘点明细,参数:" + JSON.toJSONString(inventoryBaseRequest));
		JdResult<List<InventoryWaybillDetail>> result = new JdResult<>();
		result.toSuccess();

		//参数校验
		if (! this.checkBaseParams(inventoryBaseRequest, result) || ! this.checkType(inventoryBaseRequest, result) ||
				! this.checkInventoryScopeType(inventoryBaseRequest, result)) {
			return result;
		}

		List<InventoryWaybillDetail> inventoryWaybillDetailList = inventoryInfoService.getInventoryWaybillDetail(inventoryBaseRequest);
		result.setData(inventoryWaybillDetailList);

		return result;
	}

	/**
	 * 扫描包裹信息
	 */
	@POST
	@Path("/inventory/scan")
	public JdResult<Integer> scanInventoryInfo(InventoryBaseRequest inventoryBaseRequest){
		logger.info("扫描结果上次,参数:" + JSON.toJSONString(inventoryBaseRequest));
		JdResult<Integer> result = new JdResult<>();
		result.toSuccess();
		//参数校验
		if (! this.checkBaseParams(inventoryBaseRequest, result) || ! this.checkInventoryScopeType(inventoryBaseRequest, result)) {
			return result;
		}
		//判断任务是否完成
		if (inventoryInfoService.checkTaskIsComplete(inventoryBaseRequest.getInventoryTaskId())) {
			result.toFail("当前盘点任务已结束，请退出！");
			return result;
		}
		String waybillCode = null;
		String packageCode = null;
		String barCode = inventoryBaseRequest.getBarCode();

		//判断是运单号还是包裹号
		if (WaybillUtil.isWaybillCode(barCode)) {
			waybillCode = barCode;
		} else if (WaybillUtil.isPackageCode(barCode)) {
			packageCode = barCode;
			waybillCode = WaybillUtil.getWaybillCode(packageCode);
		} else {
			String message = "【" + barCode +"】既不是运单号，也不是包裹号，请重新扫描！";
			logger.warn(message);
			result.toFail(message);
			return result;
		}
		InventoryScanDetail inventoryScanDetail = inventoryScanDetailService.convert2InventoryBaseRequest(inventoryBaseRequest);
		inventoryScanDetail.setWaybillCode(waybillCode);
		inventoryScanDetail.setPackageCode(packageCode);

		SiteWithDirection receiveSite = packageStatusService.getReceiveSiteByWaybillCode(waybillCode, inventoryBaseRequest.getCreateSiteCode());
		//自定义需要判断是否是流向异常
		if (inventoryBaseRequest.getInventoryScope() == 1) {
			//获取运单或者包裹的流向，如果流向不在流向列表范围内，判定为流向异常
			if (receiveSite != null && receiveSite.getDirectionCode() != null) {
				inventoryScanDetail.setReceiveSiteCode(receiveSite.getSiteCode());
				inventoryScanDetail.setReceiveSiteName(receiveSite.getSiteName());
				inventoryScanDetail.setDirectionCode(receiveSite.getDirectionCode());
				inventoryScanDetail.setDirectionName(receiveSite.getDirectionName());

				List<Integer> directionCodeList = inventoryBaseRequest.getDirectionCodeList();
				//不在盘点流向内，输入盘点异常，PDA提示
				if (! directionCodeList.contains(receiveSite.getDirectionCode())) {
					String message = "流向异常，【" + barCode +"】的下一站是【" + receiveSite.getDirectionName() + "】不在盘点范围内！";
					logger.warn(message);
					//插入异常表，并返回PDA提示
					result.toError(message);
				}
			} else {
				logger.warn("【" + barCode +"】的下一站信息为空！");
			}
		}

		//包裹直接插入扫描表
		//运单需要获取包裹列表，遍历插入扫描表，将正确的扫描数返回给PDA
		int packageNum = 0;
		try {
			packageNum = inventoryScanDetailService.insert(inventoryScanDetail);
		} catch (InventoryCustomException e) {
			logger.error(e.getMessage());
			result.toError(e.getMessage());
		}
		result.setData(packageNum);
		return result;
	}

	/**
	 * 盘点完成接口
	 */
	@POST
	@Path("/inventory/complete")
	public JdResult completeInventoryTask(InventoryBaseRequest inventoryBaseRequest){
		logger.info("扫描结果上次,参数:" + JSON.toJSONString(inventoryBaseRequest));
		JdResult result = new JdResult<>();
		result.toSuccess();
		//参数校验
		if (! this.checkBaseParams(inventoryBaseRequest, result) || ! this.checkType(inventoryBaseRequest, result)) {
			return result;
		}

		inventoryInfoService.completeInventoryTask(inventoryBaseRequest);
		return result;
	}

	/**
	 * 检查任务编号和始发
	 */
	private boolean checkBaseParams(InventoryBaseRequest inventoryBaseRequest, JdResult jdResult) {
		String inventoryTaskId = inventoryBaseRequest.getInventoryTaskId();
		Integer createSiteCode = inventoryBaseRequest.getCreateSiteCode();
		if(StringHelper.isEmpty(inventoryTaskId) || createSiteCode == null) {
			jdResult.toFail("任务号和始发站点不能为空，请检查参数！");
			return false;
		}
		return true;
	}

	/**
	 * 检查盘点信息的类型范围，目前只允许1和2
	 */
	private boolean checkType(InventoryBaseRequest inventoryBaseRequest, JdResult jdResult) {
		Integer type = inventoryBaseRequest.getType();
		if (type == null || (type != 1 && type != 2)) {
			jdResult.toFail("请求类型值只允许为1或2，请检查参数type！");
			return false;
		}
		return true;
	}

	/**
	 * 检查盘点类型的范围
	 */
	private boolean checkInventoryScopeType(InventoryBaseRequest inventoryBaseRequest, JdResult jdResult) {
		Integer inventoryScope = inventoryBaseRequest.getInventoryScope();
		if (inventoryScope == null || (inventoryScope != 1 && inventoryScope != 2 && inventoryScope != 3)) {
			jdResult.toFail("请求类型值只允许为1、2或3，请检查参数inventoryScopeType！");
			return false;
		}
		if (inventoryScope == 1) {
			List<Integer> directionCodeList = inventoryBaseRequest.getDirectionCodeList();
			if (directionCodeList == null || directionCodeList.isEmpty()) {
				jdResult.toFail("自定义流向判断流向列表不能为空，请检查参数！");
				return false;
			}
		}
		return true;
	}

}
