package com.jd.bluedragon.distribution.storage.controller;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.distribution.storage.domain.StoragePackageD;
import com.jd.bluedragon.distribution.storage.domain.StoragePackageDCondition;
import com.jd.bluedragon.distribution.storage.service.StoragePackageDService;
import com.jd.ql.dms.common.domain.JdResponse;
import com.jd.ql.dms.common.web.mvc.api.PagerResult;
import com.jd.uim.annotation.Authorization;
import org.apache.commons.lang.StringUtils;
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
 * @ClassName: StoragePackageDController
 * @Description: 储位包裹明细表--Controller实现
 * @author wuyoude
 * @date 2018年08月15日 18:27:23
 *
 */
@Controller
@RequestMapping("storage/storagePackageD")
public class StoragePackageDController {

	private static final Logger log = LoggerFactory.getLogger(StoragePackageDController.class);

	@Autowired
	private StoragePackageDService storagePackageDService;

	/**
	 * 返回主页面
	 * @return
	 */
	@Authorization(Constants.DMS_WEB_EXPRESS_STORAGEPACKAGEM_R)
	@RequestMapping(value = "/toIndex")
	public String toIndex() {
		return "/storage/storagePackageD";
	}
	/**
	 * 根据id获取基本信息
	 * @param id
	 * @return
	 */
    @Authorization(Constants.DMS_WEB_EXPRESS_STORAGEPACKAGEM_R)
	@RequestMapping(value = "/detail/{id}")
	public @ResponseBody JdResponse<StoragePackageD> detail(@PathVariable("id") Long id) {
		JdResponse<StoragePackageD> rest = new JdResponse<StoragePackageD>();
		rest.setData(storagePackageDService.findById(id));
		return rest;
	}
	/**
	 * 保存数据
	 * @param storagePackageD
	 * @return
	 */
    @Authorization(Constants.DMS_WEB_EXPRESS_STORAGEPACKAGEM_R)
	@RequestMapping(value = "/save")
	public @ResponseBody JdResponse<Boolean> save(@RequestBody StoragePackageD storagePackageD) {
		JdResponse<Boolean> rest = new JdResponse<Boolean>();
		try {
			rest.setData(storagePackageDService.saveOrUpdate(storagePackageD));
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
			rest.setData(storagePackageDService.deleteByIds(ids));
		} catch (Exception e) {
			log.error("fail to delete！",e);
			rest.toError("删除失败，服务异常！");
		}
		return rest;
	}
	/**
	 * 根据条件分页查询数据信息
	 * @param storagePackageDCondition
	 * @return
	 */
    @Authorization(Constants.DMS_WEB_EXPRESS_STORAGEPACKAGEM_R)
	@RequestMapping(value = "/listData")
	public @ResponseBody PagerResult<StoragePackageD> listData(@RequestBody StoragePackageDCondition storagePackageDCondition) {
		JdResponse<PagerResult<StoragePackageD>> rest = new JdResponse<PagerResult<StoragePackageD>>();
		rest.setData(storagePackageDService.queryByPagerCondition(storagePackageDCondition));
		return rest.getData();
	}


	/**
	 * 通过运单号获取所有上架明细
	 * @param waybillCode
	 * @return
	 */
    @Authorization(Constants.DMS_WEB_EXPRESS_STORAGEPACKAGEM_R)
	@RequestMapping(value = "/showViews/{waybillCode}")
	public @ResponseBody JdResponse<List<StoragePackageD>> listData(@PathVariable("waybillCode") String waybillCode) {
		JdResponse<List<StoragePackageD>> rest = new JdResponse<List<StoragePackageD>>();
		if(StringUtils.isBlank(waybillCode)){
			rest.setCode(JdResponse.CODE_FAIL);
			rest.setMessage("查询条件运单号为空");
		}else{
			rest.setData(storagePackageDService.queryByWaybill(waybillCode));
		}
		return rest;
	}
}
