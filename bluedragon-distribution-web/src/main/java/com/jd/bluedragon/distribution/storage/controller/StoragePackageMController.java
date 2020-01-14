package com.jd.bluedragon.distribution.storage.controller;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.core.base.BaseMajorManager;
import com.jd.bluedragon.core.base.WaybillQueryManager;
import com.jd.bluedragon.core.exception.StorageException;
import com.jd.bluedragon.distribution.storage.domain.FulfillmentOrderDto;
import com.jd.bluedragon.distribution.storage.domain.PutawayDTO;
import com.jd.bluedragon.distribution.storage.domain.StoragePackageM;
import com.jd.bluedragon.distribution.storage.domain.StoragePackageMCondition;
import com.jd.bluedragon.distribution.storage.service.StoragePackageMService;
import com.jd.bluedragon.distribution.web.ErpUserClient;
import com.jd.bluedragon.dms.utils.BusinessUtil;
import com.jd.etms.waybill.domain.BaseEntity;
import com.jd.etms.waybill.dto.BigWaybillDto;
import com.jd.ql.basic.dto.BaseStaffSiteOrgDto;
import com.jd.ql.dms.common.domain.JdResponse;
import com.jd.ql.dms.common.web.mvc.api.PagerResult;
import com.jd.uim.annotation.Authorization;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @ClassName: StoragePackageMController
 * @Description: 储位包裹主表--Controller实现
 * @author wuyoude
 * @date 2018年08月15日 18:27:23
 *
 */
@Controller
@RequestMapping("storage/storagePackageM")
public class StoragePackageMController {

	private static final Logger log = LoggerFactory.getLogger(StoragePackageMController.class);

	@Autowired
	private BaseMajorManager baseMajorManager;

	@Autowired
	private StoragePackageMService storagePackageMService;

	@Autowired
	private WaybillQueryManager waybillQueryManager;

	/**
	 * 返回主页面
	 * @return
	 */
	@Authorization(Constants.DMS_WEB_EXPRESS_STORAGEPACKAGEM_R)
	@RequestMapping(value = "/toIndex")
	public String toIndex(Model model) {

		ErpUserClient.ErpUser erpUser = ErpUserClient.getCurrUser();
		String userCode = "";
		Long createSiteCode = new Long(-1);
		Integer orgId = new Integer(-1);

		if(erpUser!=null){
			userCode = erpUser.getUserCode();
			BaseStaffSiteOrgDto bssod = baseMajorManager.getBaseStaffByErpNoCache(userCode);
			if (bssod!=null && bssod.getSiteType() == 64) {/** 站点类型为64的时候为分拣中心 **/
				createSiteCode = new Long(bssod.getSiteCode());
				orgId = bssod.getOrgId();
			}
		}

		model.addAttribute("orgId",orgId).addAttribute("createSiteCode",createSiteCode);

		return "/storage/storagePackageM";
	}
	/**
	 * 根据id获取基本信息
	 * @param id
	 * @return
	 */
	@Authorization(Constants.DMS_WEB_EXPRESS_STORAGEPACKAGEM_R)
	@RequestMapping(value = "/detail/{id}")
	public @ResponseBody JdResponse<StoragePackageM> detail(@PathVariable("id") Long id) {
		JdResponse<StoragePackageM> rest = new JdResponse<StoragePackageM>();
		rest.setData(storagePackageMService.findById(id));
		return rest;
	}
	/**
	 * 保存数据
	 * @param storagePackageM
	 * @return
	 */
	@Authorization(Constants.DMS_WEB_EXPRESS_STORAGEPACKAGEM_R)
	@RequestMapping(value = "/save")
	public @ResponseBody JdResponse<Boolean> save(@RequestBody StoragePackageM storagePackageM) {
		JdResponse<Boolean> rest = new JdResponse<Boolean>();
		try {
			rest.setData(storagePackageMService.saveOrUpdate(storagePackageM));
	} catch (Exception e) {
			log.error("fail to save！",e);
			rest.toError("保存失败，服务异常！");
		}
		return rest;
	}
	/**
	 * 根据id删除多条数据
	 * @param ids
	 * @return
	 */
	@Authorization(Constants.DMS_WEB_EXPRESS_STORAGEPACKAGEM_R)
	@RequestMapping(value = "/deleteByIds")
	public @ResponseBody JdResponse<Integer> deleteByIds(@RequestBody List<Long> ids) {
		JdResponse<Integer> rest = new JdResponse<Integer>();
		try {
			rest.setData(storagePackageMService.deleteByIds(ids));
		} catch (Exception e) {
			log.error("fail to delete！",e);
			rest.toError("删除失败，服务异常！");
		}
		return rest;
	}
	/**
	 * 根据条件分页查询数据信息
	 * @param storagePackageMCondition
	 * @return
	 */
	@Authorization(Constants.DMS_WEB_EXPRESS_STORAGEPACKAGEM_R)
	@RequestMapping(value = "/listData")
	public @ResponseBody PagerResult<StoragePackageM> listData(@RequestBody StoragePackageMCondition storagePackageMCondition) {
		JdResponse<PagerResult<StoragePackageM>> rest = new JdResponse<PagerResult<StoragePackageM>>();
		rest.setData(storagePackageMService.queryByPagerCondition(storagePackageMCondition));
		return rest.getData();
	}


	/**
	 *  暂存管理检查
	 * @param storagePackageMCondition
	 * @return
	 */
	private boolean check(StoragePackageMCondition storagePackageMCondition,JdResponse<PagerResult<StoragePackageM>> response){


		return true;
	}

	/**
	 * 强制发货  作废（防止二期会继续做，暂时先保留代码）

	 * @return
	 */
	@Authorization(Constants.DMS_WEB_EXPRESS_STORAGEPACKAGEM_R)
	@Deprecated
	@RequestMapping(value = "/forceSend")
	public @ResponseBody JdResponse<Boolean> forceSend(@RequestBody List<String> performanceCodes) {
		JdResponse<Boolean> rest = new JdResponse<Boolean>();

		ErpUserClient.ErpUser erpUser = ErpUserClient.getCurrUser();
		String userCode = "";
		Integer createSiteCode = new Integer(-1);
		String createSiteName = "未获取到";

		if(erpUser!=null){
			userCode = erpUser.getUserCode();
			BaseStaffSiteOrgDto bssod = baseMajorManager.getBaseStaffByErpNoCache(userCode);
			if (bssod!=null && bssod.getSiteType() == 64) {/** 站点类型为64的时候为分拣中心 **/
				createSiteCode = bssod.getSiteCode();
				createSiteName = bssod.getSiteName();
			}
		}else{
			rest.toError("强制发货失败，未获取到当前登录人信息！");
			return rest;
		}

		//组装 上架请求体
		PutawayDTO putawayDTO = new PutawayDTO();
		putawayDTO.setOperatorId(erpUser.getUserId());
		putawayDTO.setOperatorErp(erpUser.getUserCode());
		putawayDTO.setOperatorName(erpUser.getUserName());
		putawayDTO.setCreateSiteCode(createSiteCode);
		putawayDTO.setCreateSiteName(createSiteName);
		putawayDTO.setOperateTime(System.currentTimeMillis());


		try {
			rest.setData(storagePackageMService.forceSend(performanceCodes,putawayDTO));
		} catch (Exception e) {
			log.error("fail to forceSend！",e);
			rest.toError("强制发货失败，服务异常！");
		}
		return rest;
	}


    /**
     * 撤销上架功能
     * @param ids
     * @return
     */
	@Authorization(Constants.DMS_WEB_EXPRESS_STORAGEPACKAGEM_R)
    @RequestMapping(value = "/cancelPutaway")
    public @ResponseBody JdResponse<Boolean> cancelPutaway(@RequestBody List<Long> ids) {
        JdResponse<Boolean> rest = new JdResponse<Boolean>();
        try {
            rest.setData(storagePackageMService.cancelPutaway(ids));
        } catch (Exception e) {
            log.error("fail to delete！",e);
            rest.toError("删除失败，服务异常！");
        }
        return rest;
    }


	/**
	 * 刷新状态
	 * @param waybillCode 运单号
	 * @return
	 */
	@Authorization(Constants.DMS_WEB_EXPRESS_STORAGEPACKAGEM_R)
	@RequestMapping(value = "/refreshSendStatus/{waybillCode}")
	public @ResponseBody JdResponse<Boolean> refreshSendStatus(@PathVariable("waybillCode") String waybillCode) {
		JdResponse<Boolean> rest = new JdResponse<Boolean>();
		rest.setData(false);
		try {
			storagePackageMService.updateStoragePackageMStatusForSend(waybillCode);
			rest.setData(true);
		} catch (StorageException e) {
			rest.setMessage(e.getMessage());
		}catch (Exception e) {
			log.error("刷新履约单下运单发货状态失败！",e);
			rest.toError("刷新履约单下运单发货状态失败，服务异常！");
		}
		return rest;
	}

	/**
	 * 获取履约单下所有运单数据
	 * @param waybillCode 运单号
	 * @return
	 */
	@Authorization(Constants.DMS_WEB_EXPRESS_STORAGEPACKAGEM_R)
	@RequestMapping(value = "/queryWaybills/{waybillCode}")
	public @ResponseBody JdResponse<List<FulfillmentOrderDto>> queryWaybills(@PathVariable("waybillCode") String waybillCode) {
		JdResponse<List<FulfillmentOrderDto>> result = new JdResponse<List<FulfillmentOrderDto>>();
		List<FulfillmentOrderDto> fulfillmentOrderDtos = new ArrayList<>();
		result.setData(fulfillmentOrderDtos);
		try {

			BaseEntity<BigWaybillDto> baseEntity = waybillQueryManager.getDataByChoice(waybillCode, true,true, true,false);
			if(baseEntity == null || baseEntity.getData() == null || baseEntity.getData().getWaybill() == null){
				result.setCode(JdResponse.CODE_ERROR);
				result.setMessage("无运单信息");
				return result;
			}

			if(!BusinessUtil.isPerformanceOrder(baseEntity.getData().getWaybill().getWaybillSign())){
				result.setCode(JdResponse.CODE_ERROR);
				result.setMessage("非加履中心订单");
				return result;
			}

			String	parentOrderId = baseEntity.getData().getWaybill().getParentOrderId();

			List<String> childWaybillCodes = waybillQueryManager.getOrderParentChildList(parentOrderId);
			for(String childWaybillCode : childWaybillCodes){
				FulfillmentOrderDto f = new FulfillmentOrderDto();
				f.setDeliveryOrderId(childWaybillCode);
				f.setFulfillmentOrderId(parentOrderId);
				fulfillmentOrderDtos.add(f);
			}

		}catch (Exception e) {
			log.error("获取履约单下所有运单数据异常！",e);
			result.toError("获取履约单下所有运单数据异常，服务异常！");
		}
		return result;
	}
}
