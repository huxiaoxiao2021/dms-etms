package com.jd.bluedragon.distribution.base.controller;

import java.util.ArrayList;
import java.util.List;

import IceInternal.Ex;
import com.jd.bluedragon.core.base.BaseMajorManager;
import com.jd.bluedragon.distribution.b2bRouter.domain.ProvinceAndCity;
import com.jd.bluedragon.distribution.base.service.ProvinceAndCityService;
import com.jd.bluedragon.domain.ProvinceNode;
import com.jd.bluedragon.utils.AreaHelper;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.jd.bluedragon.distribution.base.domain.DmsStorageArea;
import com.jd.bluedragon.distribution.base.domain.DmsStorageAreaCondition;
import com.jd.bluedragon.distribution.base.service.DmsStorageAreaService;
import com.jd.ql.dms.common.domain.JdResponse;
import com.jd.ql.dms.common.web.mvc.api.PagerResult;
import org.springframework.web.multipart.MultipartFile;

import javax.ws.rs.POST;
import javax.ws.rs.Path;

/**
 *
 * @author wuyoude
 *
 */
@Controller
@RequestMapping("base/dmsStorageArea")
public class DmsStorageAreaController {

	private static final Log logger = LogFactory.getLog(DmsStorageAreaController.class);

	@Autowired
	private DmsStorageAreaService dmsStorageAreaService;

	@Autowired
	private ProvinceAndCityService provinceAndCityService;


	/**
	 * 返回主页面
	 * @return
	 */
	@RequestMapping(value = "/toIndex")
	public String toIndex() throws Exception {
		return "/base/dmsStorageArea";
	}

	/**
	 * 获取所有的省份
	 * @return
	 */
	@RequestMapping("/getProvinceList")
	@ResponseBody
	public List getProvince() throws Exception{
		this.logger.info("获取所有的省份");
		List<ProvinceNode> provinces = new ArrayList<ProvinceNode>();
        try{
            provinces.addAll(AreaHelper.getAllProvince());
        }catch(Exception e){
            this.logger.warn("获取所有的省份失败" , e);
        }
        provinces.addAll(AreaHelper.getAllProvince());
		return provinces;
	}
	/**
	 * 根据省Id获得对应的市
	 * @param provinceId
	 * @return
	 */
	@RequestMapping("/getCityList")
	@ResponseBody
	public List getCity(Integer provinceId) throws Exception {
	    this.logger.info("获取对应省下的所有城市");
        List<ProvinceAndCity> cities = new ArrayList<ProvinceAndCity>();
        try{
            cities = provinceAndCityService.getCityByProvince(provinceId);
        }catch(Exception e){
            this.logger.warn("根据省份Id获取城市失败："+ provinceId , e);
        }
        cities = provinceAndCityService.getCityByProvince(provinceId);
        return cities;
    }

	/**
	 * 根据id获取基本信息
	 * @param id
	 * @return
	 */
	@RequestMapping(value = "/detail/{id}")
	public @ResponseBody JdResponse<DmsStorageArea> detail(@PathVariable("id") Long id) {
		JdResponse<DmsStorageArea> rest = new JdResponse<DmsStorageArea>();
		rest.setData(dmsStorageAreaService.findById(id));
		return rest;
	}
	/**
	 * 保存数据
	 * @param dmsStorageArea
	 * @return
	 */
	@RequestMapping(value = "/save")
	public @ResponseBody JdResponse<Boolean> save(DmsStorageArea dmsStorageArea) {
		JdResponse<Boolean> rest = new JdResponse<Boolean>();
		DmsStorageArea newDmsStorageArea = dmsStorageAreaService.findByProAndCity(dmsStorageArea);
		if(dmsStorageArea.getStorageCode() == newDmsStorageArea.getStorageCode()){
			rest.setCode(400);
			rest.setMessage("同一省市只有一个库位号！");
		}else{

			dmsStorageArea =dmsStorageAreaService.getUserInfo(dmsStorageArea);
			try {
				rest.setData(dmsStorageAreaService.saveOrUpdate(dmsStorageArea));
			} catch (Exception e) {
				logger.error("fail to save！"+e.getMessage(),e);
				rest.toError("保存失败，服务异常！");
			}
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
			rest.setData(dmsStorageAreaService.deleteByIds(ids));
		} catch (Exception e) {
			logger.error("fail to delete！"+e.getMessage(),e);
			rest.toError("删除失败，服务异常！");
		}
		return rest;
	}
	/**
	 * 根据条件分页查询数据信息
	 * @param dmsStorageAreaCondition
	 * @return
	 */
	@RequestMapping(value = "/listData")
	public @ResponseBody PagerResult<DmsStorageArea> listData(@RequestBody DmsStorageAreaCondition dmsStorageAreaCondition) {
		JdResponse<PagerResult<DmsStorageArea>> rest = new JdResponse<PagerResult<DmsStorageArea>>();
		rest.setData(dmsStorageAreaService.queryByPagerCondition(dmsStorageAreaCondition));
		return rest.getData();
	}

	/**
     * @param file
     * @return*/
	@RequestMapping("/uploadExcel")
    @ResponseBody
    public JdResponse uploadExcel( @RequestParam("importExcelFile") MultipartFile file) {

	    return null;
    }
    //baseentity
	//basemajormanager

}
