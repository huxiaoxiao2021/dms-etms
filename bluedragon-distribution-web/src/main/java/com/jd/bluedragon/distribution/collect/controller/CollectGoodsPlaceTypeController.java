package com.jd.bluedragon.distribution.collect.controller;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.distribution.collect.domain.CollectGoodsPlaceType;
import com.jd.bluedragon.distribution.collect.domain.CollectGoodsPlaceTypeCondition;
import com.jd.bluedragon.distribution.collect.service.CollectGoodsPlaceTypeService;
import com.jd.ql.dms.common.domain.JdResponse;
import com.jd.ql.dms.common.web.mvc.api.PagerResult;
import com.jd.uim.annotation.Authorization;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

/**
 *
 * @ClassName: CollectGoodsPlaceTypeController
 * @Description: 集货位类型表--Controller实现
 * @author wuyoude
 * @date 2019年08月14日 13:56:21
 *
 */
@Controller
@RequestMapping("collect/collectGoodsPlaceType")
public class CollectGoodsPlaceTypeController {

	private static final Logger log = LoggerFactory.getLogger(CollectGoodsPlaceTypeController.class);

	@Autowired
	private CollectGoodsPlaceTypeService collectGoodsPlaceTypeService;

	/**
	 * 返回主页面
	 * @return
	 */
	@Authorization(Constants.DMS_WEB_COLLECT_SET)
	@RequestMapping(value = "/toIndex")
	public String toIndex() {
		return "/collect/collectGoodsPlaceType";
	}
	/**
	 * 根据id获取基本信息
	 * @param id
	 * @return
	 */
	@Authorization(Constants.DMS_WEB_COLLECT_SET)
	@RequestMapping(value = "/detail/{id}")
	public @ResponseBody JdResponse<CollectGoodsPlaceType> detail(@PathVariable("id") Long id) {
		JdResponse<CollectGoodsPlaceType> rest = new JdResponse<CollectGoodsPlaceType>();
		rest.setData(collectGoodsPlaceTypeService.findById(id));
		return rest;
	}

	/**
	 * 根据操作站点获取基本信息
	 * @param id
	 * @return
	 */
	@Authorization(Constants.DMS_WEB_COLLECT_SET)
	@RequestMapping(value = "/find/{createSiteCode}")
	public @ResponseBody JdResponse<List<CollectGoodsPlaceType>> detail(@PathVariable("createSiteCode") Integer createSiteCode) {
		JdResponse<List<CollectGoodsPlaceType>> rest = new JdResponse<List<CollectGoodsPlaceType>>();
		CollectGoodsPlaceType collectGoodsPlaceType = new CollectGoodsPlaceType();
		collectGoodsPlaceType.setCreateSiteCode(createSiteCode);
		rest.setData(collectGoodsPlaceTypeService.findByCreateSiteCode(collectGoodsPlaceType));
		return rest;
	}

	/**
	 * 保存数据
	 * @param collectGoodsPlaceType
	 * @return
	 */
	@Authorization(Constants.DMS_WEB_COLLECT_SET)
	@RequestMapping(value = "/save")
	public @ResponseBody JdResponse<Boolean> save(@RequestBody CollectGoodsPlaceType collectGoodsPlaceType) {
		JdResponse<Boolean> rest = new JdResponse<Boolean>();
		try {
			rest.setData(collectGoodsPlaceTypeService.saveOrUpdate(collectGoodsPlaceType));
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
	@Authorization(Constants.DMS_WEB_COLLECT_SET)
	@RequestMapping(value = "/deleteByIds")
	public @ResponseBody JdResponse<Integer> deleteByIds(@RequestBody List<Long> ids) {
		JdResponse<Integer> rest = new JdResponse<Integer>();
		try {
			rest.setData(collectGoodsPlaceTypeService.deleteByIds(ids));
		} catch (Exception e) {
			log.error("fail to delete！",e);
			rest.toError("删除失败，服务异常！");
		}
		return rest;
	}
	/**
	 * 根据条件分页查询数据信息
	 * @param collectGoodsPlaceTypeCondition
	 * @return
	 */
	@Authorization(Constants.DMS_WEB_COLLECT_SET)
	@RequestMapping(value = "/listData")
	public @ResponseBody PagerResult<CollectGoodsPlaceType> listData(@RequestBody CollectGoodsPlaceTypeCondition collectGoodsPlaceTypeCondition) {
		JdResponse<PagerResult<CollectGoodsPlaceType>> rest = new JdResponse<PagerResult<CollectGoodsPlaceType>>();
		rest.setData(collectGoodsPlaceTypeService.queryByPagerCondition(collectGoodsPlaceTypeCondition));
		return rest.getData();
	}
}
