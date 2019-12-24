package com.jd.bluedragon.distribution.collect.controller;

import java.util.List;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.core.base.BaseMajorManager;
import com.jd.bluedragon.distribution.collect.domain.*;
import com.jd.bluedragon.distribution.collect.service.CollectGoodsAreaService;
import com.jd.bluedragon.distribution.collect.service.CollectGoodsDetailService;
import com.jd.bluedragon.distribution.web.ErpUserClient;
import com.jd.ql.basic.dto.BaseStaffSiteOrgDto;
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

import com.jd.bluedragon.distribution.collect.service.CollectGoodsPlaceService;
import com.jd.ql.dms.common.domain.JdResponse;
import com.jd.ql.dms.common.web.mvc.api.PagerResult;

/**
 *
 * @ClassName: CollectGoodsPlaceController
 * @Description: 集货位表--Controller实现
 * @author wuyoude
 * @date 2019年08月14日 13:56:21
 *
 */
@Controller
@RequestMapping("collect/collectGoodsPlace")
public class CollectGoodsPlaceController {

	private static final Logger log = LoggerFactory.getLogger(CollectGoodsPlaceController.class);

	@Autowired
	private CollectGoodsPlaceService collectGoodsPlaceService;

	@Autowired
	private CollectGoodsDetailService collectGoodsDetailService;

	@Autowired
	private CollectGoodsAreaService collectGoodsAreaService;

	@Autowired
	BaseMajorManager baseMajorManager;

	/**
	 * 返回主页面
	 * @return
	 */
	@Authorization(Constants.DMS_WEB_COLLECT_SET)
	@RequestMapping(value = "/toIndex")
	public String toIndex(Model model) {


		ErpUserClient.ErpUser erpUser = ErpUserClient.getCurrUser();
		String userCode = "";
		Long createSiteCode = new Long(-1);
		Integer orgId = new Integer(-1);
        String createSiteName = "";
		if(erpUser!=null){
			userCode = erpUser.getUserCode();
			BaseStaffSiteOrgDto bssod = baseMajorManager.getBaseStaffByErpNoCache(userCode);
			if (bssod!=null && bssod.getSiteType() == 64) {/** 站点类型为64的时候为分拣中心 **/
				createSiteCode = new Long(bssod.getSiteCode());
				orgId = bssod.getOrgId();
                createSiteName = bssod.getSiteName();
			}
		}
		model.addAttribute("orgId",orgId).addAttribute("createSiteCode",createSiteCode).addAttribute("createSiteName",createSiteName);
		return "/collect/collectGoodsPlace";
	}
	/**
	 * 根据id获取基本信息
	 * @param id
	 * @return
	 */
	@Authorization(Constants.DMS_WEB_COLLECT_SET)
	@RequestMapping(value = "/detail/{id}")
	public @ResponseBody JdResponse<CollectGoodsPlace> detail(@PathVariable("id") Long id) {
		JdResponse<CollectGoodsPlace> rest = new JdResponse<CollectGoodsPlace>();
		rest.setData(collectGoodsPlaceService.findById(id));
		return rest;
	}
	/**
	 * 保存数据
	 * @param collectGoodsPlaces
	 * @return
	 */
	@Authorization(Constants.DMS_WEB_COLLECT_SET)
	@RequestMapping(value = "/save")
	public @ResponseBody JdResponse<Boolean> save(@RequestBody List<CollectGoodsPlace> collectGoodsPlaces) {
		JdResponse<Boolean> rest = new JdResponse<Boolean>();
		try {
			ErpUserClient.ErpUser erpUser = ErpUserClient.getCurrUser();
			String userCode = "";
			String userName = "";
			Integer createSiteCode = new Integer(0);
			String createSiteName = "";

			if(erpUser!=null){
				userCode = erpUser.getUserCode();
				userName = erpUser.getUserName();
				BaseStaffSiteOrgDto bssod = baseMajorManager.getBaseStaffByErpNoCache(userCode);
				if (bssod!=null && bssod.getSiteType() == 64) {/** 站点类型为64的时候为分拣中心 **/
					createSiteCode = bssod.getSiteCode();
					createSiteName = bssod.getSiteName();
				}
			}



			for(CollectGoodsPlace collectGoodsPlace : collectGoodsPlaces){

				collectGoodsPlace.setCreateSiteCode(createSiteCode);
				collectGoodsPlace.setCreateSiteName(createSiteName);
				collectGoodsPlace.setCreateUser(userCode+"|"+userName);

			}

			collectGoodsPlaceService.saveAll(collectGoodsPlaces);

		} catch (Exception e) {
			log.error("fail to save！",e);
			rest.toError("保存失败，服务异常！");
		}
		return rest;
	}


	/**
	 * 修改数据
	 * @param collectGoodsPlace
	 * @return
	 */
	@Authorization(Constants.DMS_WEB_COLLECT_SET)
	@RequestMapping(value = "/update")
	public @ResponseBody JdResponse<Boolean> update(@RequestBody CollectGoodsPlace collectGoodsPlace) {
		JdResponse<Boolean> rest = new JdResponse<Boolean>();
		try {
			ErpUserClient.ErpUser erpUser = ErpUserClient.getCurrUser();
			String userCode = "";
			String userName = "";

			CollectGoodsPlace updatePlace = collectGoodsPlaceService.findById(collectGoodsPlace.getId());
			if(!updatePlace.getCollectGoodsPlaceStatus().toString().equals(CollectGoodsPlaceStatusEnum.FREE_0.getCode())){
				rest.toError("操作失败，集货位存在包裹,请将集货区包裹操作完后在执行！");
				return rest;
			}

			if(erpUser!=null){
				userCode = erpUser.getUserCode();
				userName = erpUser.getUserName();
			}

			collectGoodsPlace.setUpdateUser(userCode+"|"+userName);
			collectGoodsPlaceService.saveOrUpdate(collectGoodsPlace);

		} catch (Exception e) {
			log.error("fail to save！",e);
			rest.toError("保存失败，服务异常！");
		}
		return rest;
	}
	@Authorization(Constants.DMS_WEB_COLLECT_SET)
	@RequestMapping(value = "/saveType")
	public @ResponseBody JdResponse<Boolean> saveType(@RequestBody CollectGoodsPlaceCondition condition) {
		JdResponse<Boolean> rest = new JdResponse<Boolean>();
		try {

			ErpUserClient.ErpUser erpUser = ErpUserClient.getCurrUser();
			String userCode = "";
			String userName = "";
			Integer createSiteCode = new Integer(0);
			String createSiteName = "";



			if(erpUser!=null){
				userCode = erpUser.getUserCode();
				userName = erpUser.getUserName();
				BaseStaffSiteOrgDto bssod = baseMajorManager.getBaseStaffByErpNoCache(userCode);
				if (bssod!=null && bssod.getSiteType() == 64) {/** 站点类型为64的时候为分拣中心 **/
					createSiteCode = bssod.getSiteCode();
					createSiteName = bssod.getSiteName();
				}
			}

			condition.setCreateSiteCode(createSiteCode);
			condition.setCreateSiteName(createSiteName);
			condition.setCreateUser(userCode+"|"+userName);
			rest.setData(collectGoodsPlaceService.savePalceType(condition));

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
			for(Long id : ids){
				CollectGoodsPlace collectGoodsPlace = collectGoodsPlaceService.findById(id);
				if(!collectGoodsPlace.getCollectGoodsPlaceStatus().toString().equals(CollectGoodsPlaceStatusEnum.FREE_0.getCode())){
				    rest.toError("操作失败，集货位存在包裹,请将集货区包裹操作完后在执行！");
					return rest;
				}
			}


			rest.setData(collectGoodsPlaceService.deleteByIds(ids));
		} catch (Exception e) {
			log.error("fail to delete！",e);
			rest.toError("删除失败，服务异常！");
		}
		return rest;
	}
	/**
	 * 根据条件分页查询数据信息
	 * @param collectGoodsPlaceCondition
	 * @return
	 */
	@Authorization(Constants.DMS_WEB_COLLECT_SET)
	@RequestMapping(value = "/listData")
	public @ResponseBody PagerResult<CollectGoodsPlace> listData(@RequestBody CollectGoodsPlaceCondition collectGoodsPlaceCondition) {
		JdResponse<PagerResult<CollectGoodsPlace>> rest = new JdResponse<PagerResult<CollectGoodsPlace>>();
		rest.setData(collectGoodsPlaceService.queryByPagerCondition(collectGoodsPlaceCondition));
		return rest.getData();
	}


}
