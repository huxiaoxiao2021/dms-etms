package com.jd.bluedragon.distribution.consumable.controller;

import java.util.Map;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.distribution.base.controller.DmsBaseController;
import com.jd.bluedragon.distribution.consumable.domain.PackingTypeEnum;
import com.jd.uim.annotation.Authorization;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.jd.bluedragon.distribution.consumable.domain.PackingConsumableInfo;
import com.jd.bluedragon.distribution.consumable.domain.PackingConsumableInfoCondition;
import com.jd.bluedragon.distribution.consumable.service.PackingConsumableInfoService;
import com.jd.ql.dms.common.domain.JdResponse;
import com.jd.ql.dms.common.web.mvc.api.PagerResult;

/**
 *
 * @ClassName: PackingConsumableInfoController
 * @Description: 包装耗材信息表--Controller实现
 * @author wuyoude
 * @date 2018年08月17日 10:14:27
 *
 */
@Controller
@RequestMapping("consumable/packingConsumableInfo")
public class PackingConsumableInfoController extends DmsBaseController {

	private static final Log logger = LogFactory.getLog(PackingConsumableInfoController.class);

	@Autowired
	private PackingConsumableInfoService packingConsumableInfoService;

	/**
	 * 返回主页面
	 * @return
	 */
	@Authorization(Constants.DMS_WEB_EXPRESS_PACKINGCONSUMABLEINFO_R)
	@RequestMapping(value = "/toIndex")
	public String toIndex() {
		return "/consumable/packingConsumableInfo";
	}

	/**
	 * 保存数据
	 * @param packingConsumableInfo
	 * @return
	 */
	@Authorization(Constants.DMS_WEB_EXPRESS_PACKINGCONSUMABLEINFO_R)
	@RequestMapping(value = "/save")
	public @ResponseBody JdResponse<Boolean> save(@RequestBody PackingConsumableInfo packingConsumableInfo) {
		JdResponse<Boolean> rest = new JdResponse<Boolean>();
		try {
			packingConsumableInfo.setOperateUserErp(this.getLoginUser().getUserErp());
			packingConsumableInfo.setOperateUserCode(this.getLoginUser().getStaffNo().toString());
			rest.setData(packingConsumableInfoService.saveOrUpdate(packingConsumableInfo));
	} catch (Exception e) {
			logger.error("fail to save！"+e.getMessage(),e);
			rest.toError("保存失败，服务异常！");
		}
		return rest;
	}

	/**
	 * 根据条件分页查询数据信息
	 * @param packingConsumableInfoCondition
	 * @return
	 */
	@Authorization(Constants.DMS_WEB_EXPRESS_PACKINGCONSUMABLEINFO_R)
	@RequestMapping(value = "/listData")
	public @ResponseBody PagerResult<PackingConsumableInfo> listData(@RequestBody PackingConsumableInfoCondition packingConsumableInfoCondition) {
		JdResponse<PagerResult<PackingConsumableInfo>> rest = new JdResponse<PagerResult<PackingConsumableInfo>>();
		rest.setData(packingConsumableInfoService.queryByPagerCondition(packingConsumableInfoCondition));
		return rest.getData();
	}

	@Authorization(Constants.DMS_WEB_EXPRESS_PACKINGCONSUMABLEINFO_R)
	@RequestMapping("/getModifyPage")
	public String getModifyPage() {
		return "consumable/packingConsumableInfoModify";
	}

	@Authorization(Constants.DMS_WEB_EXPRESS_PACKINGCONSUMABLEINFO_R)
	@RequestMapping("/getAddPage")
	public String getAddPage() {
		return "consumable/packingConsumableInfoAdd";
	}

	@Authorization(Constants.DMS_WEB_EXPRESS_PACKINGCONSUMABLEINFO_R)
	@RequestMapping("/getAllPackingType")
	@ResponseBody
	public JdResponse getAllPackingType() {
		JdResponse<Map<String, String>> rest = new JdResponse<Map<String, String>>();
		try {
			rest.setData(PackingTypeEnum.getEnumMap());
		} catch (Exception e) {
			logger.error("获取包装类型失败" + e.getMessage(), e);
			rest.toError("获取包装类型失败，服务异常！");
		}
		return rest;
	}
}
