package com.jd.bluedragon.distribution.consumable.controller;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.core.base.BaseMajorManager;
import com.jd.bluedragon.distribution.base.controller.DmsBaseController;
import com.jd.bluedragon.distribution.consumable.domain.*;
import com.jd.bluedragon.distribution.consumable.service.WaybillConsumableRecordService;
import com.jd.bluedragon.distribution.consumable.service.WaybillConsumableRelationService;
import com.jd.ql.basic.dto.BaseStaffSiteOrgDto;
import com.jd.ql.dms.common.domain.JdResponse;
import com.jd.ql.dms.common.web.mvc.api.PagerResult;
import com.jd.uim.annotation.Authorization;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Date;
import java.util.List;

/**
 *
 * @ClassName: WaybillConsumableRelationController
 * @Description: 运单耗材关系表--Controller实现
 * @author wuyoude
 * @date 2018年08月17日 10:14:27
 *
 */
@Controller
@RequestMapping("consumable/waybillConsumableRelation")
public class WaybillConsumableRelationController extends DmsBaseController{

	private static final Log logger = LogFactory.getLog(WaybillConsumableRelationController.class);

	@Autowired
	private WaybillConsumableRelationService waybillConsumableRelationService;

	@Autowired
	private WaybillConsumableRecordService waybillConsumableRecordService;

	@Autowired
	private BaseMajorManager baseMajorManager;
	/**
	 * 返回主页面
	 * @return
	 */
	@Authorization(Constants.DMS_WEB_EXPRESS_WAYBILLCONSUMABLERECORD_R)
	@RequestMapping(value = "/toIndex")
	public String toIndex() {
		return "/consumable/waybillConsumableRelation";
	}
	/**
	 * 根据id获取基本信息
	 * @param id
	 * @return
	 */
    @Authorization(Constants.DMS_WEB_EXPRESS_WAYBILLCONSUMABLERECORD_R)
	@RequestMapping(value = "/detail/{id}")
	public @ResponseBody JdResponse<WaybillConsumableRelation> detail(@PathVariable("id") Long id) {
		JdResponse<WaybillConsumableRelation> rest = new JdResponse<WaybillConsumableRelation>();
		rest.setData(waybillConsumableRelationService.findById(id));
		return rest;
	}
	/**
	 * 保存数据
	 * @param waybillConsumableRelation
	 * @return
	 */
    @Authorization(Constants.DMS_WEB_EXPRESS_WAYBILLCONSUMABLERECORD_R)
	@RequestMapping(value = "/save")
	public @ResponseBody JdResponse<Boolean> save(@RequestBody WaybillConsumableRelation waybillConsumableRelation) {
		JdResponse<Boolean> rest = new JdResponse<Boolean>();
		try {
			String packUserErp = waybillConsumableRelation.getPackUserErp();
			//验证ERP是否存在
			BaseStaffSiteOrgDto userOrgInfo = baseMajorManager.getBaseStaffByErpNoCache(packUserErp);
			if (userOrgInfo == null){
				rest.toError("【" + packUserErp + "】不存在与青龙基础资料中，请核实后录入！");
				return rest;
			}

			Date date = new Date();
			waybillConsumableRelation.setUpdateTime(date);
			waybillConsumableRelation.setOperateTime(date);
			waybillConsumableRelation.setOperateUserCode(this.getLoginUser().getStaffNo().toString());
			waybillConsumableRelation.setOperateUserErp(this.getLoginUser().getUserErp());
			boolean result = waybillConsumableRelationService.saveOrUpdate(waybillConsumableRelation);
			rest.setData(result);
			if (result) {
				//更新record表的状态
				WaybillConsumableRecord waybillConsumableRecord = new WaybillConsumableRecord();
				waybillConsumableRecord.setWaybillCode(waybillConsumableRelation.getWaybillCode());
				waybillConsumableRecord.setModifyStatus(WaybillConsumableRecordService.TREATED_STATE);
				waybillConsumableRecordService.updateByCondition(waybillConsumableRecord);
			}
	} catch (Exception e) {
			logger.error("fail to save！"+e.getMessage(),e);
			rest.toError("保存失败，服务异常！");
		}
		return rest;
	}
	/**
	 * 根据id删除多条数据
	 * @param waybillConsumableRelationBatchDelete
	 * @return
	 */
    @Authorization(Constants.DMS_WEB_EXPRESS_WAYBILLCONSUMABLERECORD_R)
	@RequestMapping(value = "/deleteByIds")
	public @ResponseBody JdResponse<Integer> deleteByIds(@RequestBody WaybillConsumableRelationBatchDelete waybillConsumableRelationBatchDelete) {
		JdResponse<Integer> rest = new JdResponse<Integer>();
		try {
			rest.setData(waybillConsumableRelationService.deleteByIds(waybillConsumableRelationBatchDelete.getIds()));
			//更新record表的状态
			WaybillConsumableRecord waybillConsumableRecord = new WaybillConsumableRecord();
			waybillConsumableRecord.setWaybillCode(waybillConsumableRelationBatchDelete.getWaybillCode());
			waybillConsumableRecord.setModifyStatus(WaybillConsumableRecordService.TREATED_STATE);
			waybillConsumableRecordService.updateByCondition(waybillConsumableRecord);
		} catch (Exception e) {
			logger.error("fail to delete！"+e.getMessage(),e);
			rest.toError("删除失败，服务异常！");
		}
		return rest;
	}
	/**
	 * 根据条件分页查询数据信息
	 * @param waybillConsumableRelationCondition
	 * @return
	 */
    @Authorization(Constants.DMS_WEB_EXPRESS_WAYBILLCONSUMABLERECORD_R)
	@RequestMapping(value = "/listData")
	public @ResponseBody PagerResult<WaybillConsumableDetailInfo> listData(@RequestBody WaybillConsumableRelationCondition waybillConsumableRelationCondition) {
		JdResponse<PagerResult<WaybillConsumableDetailInfo>> rest = new JdResponse<PagerResult<WaybillConsumableDetailInfo>>();
		rest.setData(waybillConsumableRelationService.queryDetailInfoByPagerCondition(waybillConsumableRelationCondition));
		return rest.getData();
	}

    @Authorization(Constants.DMS_WEB_EXPRESS_WAYBILLCONSUMABLERECORD_R)
	@RequestMapping("/getModifyPage")
	public String getModifyPage() {
		return "consumable/waybillConsumableDetailModify";
	}

    @Authorization(Constants.DMS_WEB_EXPRESS_WAYBILLCONSUMABLERECORD_R)
	@RequestMapping("/getAddPage")
	public String getAddPage() {
		return "consumable/waybillConsumableDetailAdd";
	}

	/**
	 * 批量更新包装人Erp
	 * @param
	 * @return
	 */
	@Authorization(Constants.DMS_WEB_EXPRESS_WAYBILLCONSUMABLERECORD_R)
	@RequestMapping(value = "/updatePackUserErp")
	public @ResponseBody JdResponse<Integer> updatePackUserErp(@RequestBody WaybillConsumablePackUserRequest waybillConsumablePackUserRequest) {
		JdResponse<Integer> result = new JdResponse<>();
		try {
			String packUserErp = waybillConsumablePackUserRequest.getPackUserErp();
			//验证ERP是否存在
			BaseStaffSiteOrgDto userOrgInfo = baseMajorManager.getBaseStaffByErpNoCache(packUserErp);
			if (userOrgInfo == null){
				result.toError("【" + packUserErp + "】不存在与青龙基础资料中，请核实后录入！");
				return result;
			}
			Integer count = 0;
			if (waybillConsumablePackUserRequest.getIds() != null) {
				count = waybillConsumableRelationService.updatePackUserErpById(waybillConsumablePackUserRequest.getIds(), packUserErp, this.getLoginUser());
			} else if (waybillConsumablePackUserRequest.getWaybillCodeList() != null) {
				count = waybillConsumableRelationService.updatePackUserErpByWaybillCode(waybillConsumablePackUserRequest.getWaybillCodeList(), packUserErp, this.getLoginUser());

			}
			result.setData(count);
		} catch (Exception e) {
			logger.error("fail to updatePackUserErp！"+ e.getMessage(), e);
			result.toError("更新失败，服务异常！");
		}
		return result;
	}
}
