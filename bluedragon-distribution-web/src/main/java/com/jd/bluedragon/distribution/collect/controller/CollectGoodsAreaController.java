package com.jd.bluedragon.distribution.collect.controller;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.core.base.BaseMajorManager;
import com.jd.bluedragon.distribution.collect.domain.CollectGoodsArea;
import com.jd.bluedragon.distribution.collect.domain.CollectGoodsAreaCondition;
import com.jd.bluedragon.distribution.collect.domain.CollectGoodsPlace;
import com.jd.bluedragon.distribution.collect.domain.CollectGoodsPlaceStatusEnum;
import com.jd.bluedragon.distribution.collect.service.CollectGoodsAreaService;
import com.jd.bluedragon.distribution.collect.service.CollectGoodsPlaceService;
import com.jd.bluedragon.distribution.web.ErpUserClient;
import com.jd.ql.basic.dto.BaseStaffSiteOrgDto;
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
 * @ClassName: CollectGoodsAreaController
 * @Description: 集货区表--Controller实现
 * @author wuyoude
 * @date 2019年08月14日 13:56:21
 *
 */
@Controller
@RequestMapping("collect/collectGoodsArea")
public class CollectGoodsAreaController {

	private static final Logger log = LoggerFactory.getLogger(CollectGoodsAreaController.class);

	@Autowired
	private CollectGoodsAreaService collectGoodsAreaService;

	@Autowired
	private CollectGoodsPlaceService collectGoodsPlaceService;

	@Autowired
	BaseMajorManager baseMajorManager;
	/**
	 * 返回主页面
	 * @return
	 */
	@Authorization(Constants.DMS_WEB_COLLECT_SET)
	@RequestMapping(value = "/toIndex")
	public String toIndex() {
		return "/collect/collectGoodsArea";
	}
	/**
	 * 根据id获取基本信息
	 * @param id
	 * @return
	 */
	@Authorization(Constants.DMS_WEB_COLLECT_SET)
	@RequestMapping(value = "/detail/{id}")
	public @ResponseBody JdResponse<CollectGoodsArea> detail(@PathVariable("id") Long id) {
		JdResponse<CollectGoodsArea> rest = new JdResponse<CollectGoodsArea>();
		rest.setData(collectGoodsAreaService.findById(id));
		return rest;
	}
	/**
	 * 保存数据
	 * @param collectGoodsArea
	 * @return
	 */
	@Authorization(Constants.DMS_WEB_COLLECT_SET)
	@RequestMapping(value = "/save")
	public @ResponseBody JdResponse<Boolean> save(@RequestBody CollectGoodsArea collectGoodsArea) {
		JdResponse<Boolean> rest = new JdResponse<Boolean>();
		try {
			rest.setData(collectGoodsAreaService.saveOrUpdate(collectGoodsArea));
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
			rest.setData(collectGoodsAreaService.deleteByIds(ids));
		} catch (Exception e) {
			log.error("fail to delete！",e);
			rest.toError("删除失败，服务异常！");
		}
		return rest;
	}

	/**
	 * 根据code删除多条数据
	 * @return
	 */
	@Authorization(Constants.DMS_WEB_COLLECT_SET)
	@RequestMapping(value = "/deleteByCodes")
	public @ResponseBody JdResponse<Boolean> deleteByCodes(@RequestBody List<String> codes) {
		JdResponse<Boolean> rest = new JdResponse<>();
		try {
			Integer createSiteCode = 0;

			ErpUserClient.ErpUser erpUser = ErpUserClient.getCurrUser();
			String userCode;
			if(erpUser!=null){
				userCode = erpUser.getUserCode();
				BaseStaffSiteOrgDto bssod = baseMajorManager.getBaseStaffByErpNoCache(userCode);
				if (bssod!=null && bssod.getSiteType() == 64) {/** 站点类型为64的时候为分拣中心 **/
					createSiteCode = bssod.getSiteCode();
				}
			}else {
				rest.toError("未获取到登录人信息！");
				return rest;
			}

			for(String code : codes){
				//校验货区是否有货
				CollectGoodsPlace queryPlaceParam = new CollectGoodsPlace();
				queryPlaceParam.setCreateSiteCode(createSiteCode);
				queryPlaceParam.setCollectGoodsAreaCode(code);
				List<CollectGoodsPlace> allPlaces = collectGoodsPlaceService.findPlaceByAreaCode(queryPlaceParam);
				for(CollectGoodsPlace collectGoodsPlace : allPlaces){
					if(!collectGoodsPlace.getCollectGoodsPlaceStatus().toString().equals(CollectGoodsPlaceStatusEnum.FREE_0.getCode())){
						rest.toError("操作失败，集货位存在包裹,请将集货区包裹操作完后在执行！");
						return rest;
					}
				}
			}
			// 没有删除权限，直接返回
            if(collectGoodsAreaService.checkAuthority(createSiteCode)){
                rest.toFail("不可删除，可邮件express_dms@jd.com申请开启场地的删除权限!");
                return rest;
            }
			rest.setData(collectGoodsAreaService.deleteByCode(codes));
            // 记录businessLog日志
            collectGoodsAreaService.writeLog(userCode,createSiteCode,codes);
		} catch (Exception e) {
			log.error("fail to delete！",e);
			rest.toError("删除失败，服务异常！");
		}
		return rest;
	}

	/**
	 * 根据条件分页查询数据信息
	 * @param collectGoodsAreaCondition
	 * @return
	 */
	@Authorization(Constants.DMS_WEB_COLLECT_SET)
	@RequestMapping(value = "/listData")
	public @ResponseBody PagerResult<CollectGoodsArea> listData(@RequestBody CollectGoodsAreaCondition collectGoodsAreaCondition) {
		JdResponse<PagerResult<CollectGoodsArea>> rest = new JdResponse<PagerResult<CollectGoodsArea>>();
		rest.setData(collectGoodsAreaService.queryByPagerCondition(collectGoodsAreaCondition));
		return rest.getData();
	}

	/**
	 * 校验集货区编码是否已存在
	 * @param collectGoodsArea
	 * @return
	 */
	@Authorization(Constants.DMS_WEB_COLLECT_SET)
	@RequestMapping(value = "/check")
	public @ResponseBody JdResponse<Boolean> check(@RequestBody CollectGoodsArea collectGoodsArea) {
		JdResponse<Boolean> rest = new JdResponse<Boolean>();
		try {
			Integer createSiteCode = collectGoodsArea.getCreateSiteCode();
			if(collectGoodsArea.getCreateSiteCode() == null || new Integer(0).equals(collectGoodsArea.getCreateSiteCode())){
				ErpUserClient.ErpUser erpUser = ErpUserClient.getCurrUser();
				String userCode;
				if(erpUser!=null){
					userCode = erpUser.getUserCode();
					BaseStaffSiteOrgDto bssod = baseMajorManager.getBaseStaffByErpNoCache(userCode);
					if (bssod!=null && bssod.getSiteType() == 64) {/** 站点类型为64的时候为分拣中心 **/
						createSiteCode = bssod.getSiteCode();
					}
				}
			}

			collectGoodsArea.setCreateSiteCode(createSiteCode);

			rest.setData(collectGoodsAreaService.findExistByAreaCode(collectGoodsArea));
		} catch (Exception e) {
			log.error("fail to save！",e);
			rest.toError("服务异常！");
		}
		return rest;
	}

}
