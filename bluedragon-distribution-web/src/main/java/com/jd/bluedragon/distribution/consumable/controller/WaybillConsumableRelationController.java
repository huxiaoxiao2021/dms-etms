package com.jd.bluedragon.distribution.consumable.controller;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.configuration.ucc.UccPropertyConfiguration;
import com.jd.bluedragon.core.base.BaseMajorManager;
import com.jd.bluedragon.distribution.base.controller.DmsBaseController;
import com.jd.bluedragon.distribution.consumable.domain.*;
import com.jd.bluedragon.distribution.consumable.service.WaybillConsumableRecordService;
import com.jd.bluedragon.distribution.consumable.service.WaybillConsumableRelationService;
import com.jd.ql.basic.dto.BaseStaffSiteOrgDto;
import com.jd.ql.dms.common.domain.JdResponse;
import com.jd.ql.dms.common.web.mvc.api.PagerResult;
import com.jd.uim.annotation.Authorization;
import com.jd.ump.annotation.JProfiler;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.Objects;

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

	private static final Logger log = LoggerFactory.getLogger(WaybillConsumableRelationController.class);

	@Autowired
	private WaybillConsumableRelationService waybillConsumableRelationService;

	@Autowired
	private WaybillConsumableRecordService waybillConsumableRecordService;

	@Autowired
	private BaseMajorManager baseMajorManager;

	@Autowired
	private UccPropertyConfiguration uccPropertyConfiguration;

	/**
	 * 返回主页面
	 * @return
	 */
	@Authorization(Constants.DMS_WEB_EXPRESS_WAYBILLCONSUMABLERECORD_R)
	@RequestMapping(value = "/toIndex", method = RequestMethod.GET)
	public String toIndex(Model model) {
		boolean flag = Objects.equals(uccPropertyConfiguration.getPackConsumableSwitch(),2)
				|| Objects.equals(uccPropertyConfiguration.getPackConsumableSwitch(), 3);

		model.addAttribute("banAddAndDeleteFlag", flag);
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
	@JProfiler(jKey = "dms.web.WaybillConsumableRelationController.save" , jAppName = Constants.UMP_APP_NAME_DMSWEB)
	public @ResponseBody JdResponse<Boolean> save(@RequestBody WaybillConsumableRelation waybillConsumableRelation) {
		JdResponse<Boolean> rest = new JdResponse<Boolean>();
		try {
			String packUserErp = waybillConsumableRelation.getPackUserErp();
			if (StringUtils.isEmpty(packUserErp)) {
				rest.toError("打包人的ERP为空，请检查！");
				return rest;
			}
			//验证ERP是否存在
			BaseStaffSiteOrgDto userOrgInfo = baseMajorManager.getBaseStaffByErpNoCache(packUserErp);
			if (userOrgInfo == null){
				rest.toError("【" + packUserErp + "】不存在与青龙基础资料中，请核实后录入！");
				return rest;
			}

			WaybillConsumableRelation waybillConsumableRelation1 = waybillConsumableRelationService.findById(waybillConsumableRelation.getId());
			//如果是打木架，则打包后体积必传
			if ((ConsumableCodeEnums.isWoodenConsumable(waybillConsumableRelation1.getConsumableCode()) || PackingTypeEnum.isWoodenConsumable(waybillConsumableRelation1.getConsumableType()))
					&& (waybillConsumableRelation.getConfirmVolume() == null || waybillConsumableRelation.getConfirmVolume() <= 0 || waybillConsumableRelation.getConfirmVolume() > 999.999d)) {
				rest.toError("数据格式不对，请录入大于0小于1000的数据");
				return rest;
			}
			if (!waybillConsumableRecordService.canModify(waybillConsumableRelation1.getWaybillCode())
					&& !Objects.equals(waybillConsumableRelation1.getConfirmQuantity(),waybillConsumableRelation.getConfirmQuantity())) {
				rest.toError("【已确认】或为【寄付运费运单】，不允许修改耗材使用数量！");
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
				if (!Objects.equals(waybillConsumableRelation1.getConfirmQuantity(),waybillConsumableRelation.getConfirmQuantity())) {
					waybillConsumableRecord.setModifyStatus(WaybillConsumableRecordService.TREATED_STATE);
				}
				waybillConsumableRecordService.updateByCondition(waybillConsumableRecord);
			}
		} catch (Exception e) {
			log.error("fail to save！",e);
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
	@JProfiler(jKey = "dms.web.WaybillConsumableRelationController.deleteByIds" , jAppName = Constants.UMP_APP_NAME_DMSWEB)
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
			log.error("fail to delete！",e);
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
	@JProfiler(jKey = "dms.web.WaybillConsumableRelationController.getAddPage" , jAppName = Constants.UMP_APP_NAME_DMSWEB)
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

			if (StringUtils.isEmpty(packUserErp)) {
				result.toError("打包人的ERP为空，请检查！");
				return result;
			}

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
			log.error("fail to updatePackUserErp！", e);
			result.toError("更新失败，服务异常！");
		}
		return result;
	}
}
