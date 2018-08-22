package com.jd.bluedragon.distribution.consumable.controller;

import java.util.List;

import com.jd.bluedragon.distribution.base.controller.DmsBaseController;
import com.jd.bluedragon.distribution.consumable.domain.DmsConsumableRelationDetailInfo;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.jd.bluedragon.distribution.consumable.domain.DmsConsumableRelation;
import com.jd.bluedragon.distribution.consumable.domain.DmsConsumableRelationCondition;
import com.jd.bluedragon.distribution.consumable.service.DmsConsumableRelationService;
import com.jd.ql.dms.common.domain.JdResponse;
import com.jd.ql.dms.common.web.mvc.api.PagerResult;

/**
 *
 * @ClassName: DmsConsumableRelationController
 * @Description: 分拣中心耗材关系表--Controller实现
 * @author wuyoude
 * @date 2018年08月17日 10:14:27
 *
 */
@Controller
@RequestMapping("consumable/dmsConsumableRelation")
public class DmsConsumableRelationController extends DmsBaseController {

	private static final Log logger = LogFactory.getLog(DmsConsumableRelationController.class);

	@Autowired
	private DmsConsumableRelationService dmsConsumableRelationService;

	/**
	 * 返回主页面
	 * @return
	 */
	@RequestMapping(value = "/toIndex")
	public String toIndex() {
		return "/consumable/dmsConsumableRelation";
	}
	/**
	 * 根据id获取基本信息
	 * @param id
	 * @return
	 */
	@RequestMapping(value = "/detail/{id}")
	public @ResponseBody JdResponse<DmsConsumableRelation> detail(@PathVariable("id") Long id) {
		JdResponse<DmsConsumableRelation> rest = new JdResponse<DmsConsumableRelation>();
		rest.setData(dmsConsumableRelationService.findById(id));
		return rest;
	}
	/**
	 * 保存数据
	 * @param dmsConsumableRelation
	 * @return
	 */
	@RequestMapping(value = "/save")
	public @ResponseBody JdResponse<Boolean> save(@RequestBody DmsConsumableRelation dmsConsumableRelation) {
		JdResponse<Boolean> rest = new JdResponse<Boolean>();
		try {
			rest.setData(dmsConsumableRelationService.saveOrUpdate(dmsConsumableRelation));
	} catch (Exception e) {
			logger.error("fail to save！"+e.getMessage(),e);
			rest.toError("保存失败，服务异常！");
		}
		return rest;
	}
	/**
	 * 根据id删除多条数据
	 * @param ids
	 * @return
	 */
	@RequestMapping(value = "/deleteByIds")
	public @ResponseBody JdResponse<Integer> deleteByIds(@RequestBody List<Long> ids) {
		JdResponse<Integer> rest = new JdResponse<Integer>();
		try {
			rest.setData(dmsConsumableRelationService.deleteByIds(ids));
		} catch (Exception e) {
			logger.error("fail to delete！"+e.getMessage(),e);
			rest.toError("删除失败，服务异常！");
		}
		return rest;
	}
	/**
	 * 根据条件分页查询数据信息
	 * @param dmsConsumableRelationCondition
	 * @return
	 */
	@RequestMapping(value = "/listData")
	public @ResponseBody PagerResult<DmsConsumableRelationDetailInfo> listData(@RequestBody DmsConsumableRelationCondition dmsConsumableRelationCondition) {
		dmsConsumableRelationCondition.setDmsId(this.getLoginUser().getSiteCode());
		JdResponse<PagerResult<DmsConsumableRelationDetailInfo>> rest = new JdResponse<PagerResult<DmsConsumableRelationDetailInfo>>();
		rest.setData(dmsConsumableRelationService.queryDetailInfoByPagerCondition(dmsConsumableRelationCondition));
		return rest.getData();
	}

	/**
	 * 根据编号启用耗材信息
	 * @param codes
	 * @return
	 */
	@RequestMapping(value = "/enableByCodes")
	public @ResponseBody JdResponse<Boolean> enableByCodes(@RequestBody List<String> codes) {
		JdResponse<Boolean> rest = new JdResponse<Boolean>();
		try {
			DmsConsumableRelation dmsConsumableRelation = new DmsConsumableRelation();
			dmsConsumableRelation.setDmsId(this.getLoginUser().getSiteCode());
			dmsConsumableRelation.setOperateUserCode(this.getLoginUser().getStaffNo().toString());
			dmsConsumableRelation.setOperateUserErp(this.getLoginUser().getUserErp());
			dmsConsumableRelation.setDmsName(this.getLoginUser().getSiteName());
			rest.setData(dmsConsumableRelationService.enableByCodes(codes, dmsConsumableRelation));
		} catch (Exception e) {
			logger.error("fail to enableByCodes！"+e.getMessage(),e);
			rest.toError("启用失败，服务异常！");
		}
		return rest;
	}

	/**
	 * 根据编号启用耗材信息
	 * @param codes
	 * @return
	 */
	@RequestMapping(value = "/disableByCodes")
	public @ResponseBody JdResponse<Boolean> disableByCodes(@RequestBody List<String> codes) {
		JdResponse<Boolean> rest = new JdResponse<Boolean>();
		try {
			DmsConsumableRelation dmsConsumableRelation = new DmsConsumableRelation();
			dmsConsumableRelation.setDmsId(this.getLoginUser().getSiteCode());
			dmsConsumableRelation.setOperateUserCode(this.getLoginUser().getStaffNo().toString());
			dmsConsumableRelation.setOperateUserErp(this.getLoginUser().getUserErp());
			dmsConsumableRelation.setDmsName(this.getLoginUser().getSiteName());
			rest.setData(dmsConsumableRelationService.disableByCodes(codes, dmsConsumableRelation));
		} catch (Exception e) {
			logger.error("fail to disableByCodes！"+e.getMessage(),e);
			rest.toError("停用失败，服务异常！");
		}
		return rest;
	}
}
