package com.jd.bluedragon.distribution.half.controller;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.jd.bluedragon.distribution.half.domain.PackageHalf;
import com.jd.bluedragon.distribution.half.domain.PackageHalfCondition;
import com.jd.bluedragon.distribution.half.service.PackageHalfService;
import com.jd.ql.dms.common.domain.JdResponse;
import com.jd.ql.dms.common.web.mvc.api.PagerResult;

/**
 *
 * @ClassName: PackageHalfController
 * @Description: 包裹半收操作--Controller实现
 * @author wuyoude
 * @date 2018年03月20日 17:33:21
 *
 */
@Controller
@RequestMapping("half/packageHalf")
public class PackageHalfController {

	private static final Log logger = LogFactory.getLog(PackageHalfController.class);

	@Autowired
	private PackageHalfService packageHalfService;

	/**
	 * 返回主页面
	 * @return
	 */
	@RequestMapping(value = "/toIndex")
	public String toIndex() {
		return "/half/packageHalf";
	}
	/**
	 * 根据id获取基本信息
	 * @param id
	 * @return
	 */
	@RequestMapping(value = "/detail/{id}")
	public @ResponseBody JdResponse<PackageHalf> detail(@PathVariable("id") Long id) {
		JdResponse<PackageHalf> rest = new JdResponse<PackageHalf>();
		rest.setData(packageHalfService.findById(id));
		return rest;
	}
	/**
	 * 保存数据
	 * @param packageHalf
	 * @return
	 */
	@RequestMapping(value = "/save")
	public @ResponseBody JdResponse<Boolean> save(@RequestBody PackageHalf packageHalf) {
		JdResponse<Boolean> rest = new JdResponse<Boolean>();
		try {
			rest.setData(packageHalfService.saveOrUpdate(packageHalf));
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
			rest.setData(packageHalfService.deleteByIds(ids));
		} catch (Exception e) {
			logger.error("fail to delete！"+e.getMessage(),e);
			rest.toError("删除失败，服务异常！");
		}
		return rest;
	}
	/**
	 * 根据条件分页查询数据信息
	 * @param packageHalfCondition
	 * @return
	 */
	@RequestMapping(value = "/listData")
	public @ResponseBody PagerResult<PackageHalf> listData(@RequestBody PackageHalfCondition packageHalfCondition) {
		JdResponse<PagerResult<PackageHalf>> rest = new JdResponse<PagerResult<PackageHalf>>();
		rest.setData(packageHalfService.queryByPagerCondition(packageHalfCondition));
		return rest.getData();
	}
}
