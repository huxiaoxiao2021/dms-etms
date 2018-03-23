package com.jd.bluedragon.distribution.base.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import IceInternal.Ex;
import com.jd.bluedragon.core.base.BaseMajorManager;
import com.jd.bluedragon.distribution.b2bRouter.domain.ProvinceAndCity;
import com.jd.bluedragon.distribution.base.service.ProvinceAndCityService;
import com.jd.bluedragon.distribution.basic.DataResolver;
import com.jd.bluedragon.distribution.basic.ExcelDataResolverFactory;
import com.jd.bluedragon.distribution.basic.PropertiesMetaDataFactory;
import com.jd.bluedragon.distribution.transport.domain.ArBookingSpace;
import com.jd.bluedragon.distribution.web.ErpUserClient;
import com.jd.bluedragon.domain.ProvinceNode;
import com.jd.bluedragon.utils.AreaHelper;
import com.jd.bluedragon.utils.StringHelper;
import com.jd.ql.basic.dto.BaseStaffSiteOrgDto;
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

	@Autowired
	private BaseMajorManager baseMajorManager;


	/**
	 * 返回主页面
	 * @return
	 */
	@RequestMapping(value = "/toIndex")
	public String toIndex()  {
		return "/base/dmsStorageArea";
	}

	/**
	 * 获取所有的省份
	 * @return
	 */
	@RequestMapping("/getProvinceList")
	@ResponseBody
	public List getProvince() {
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
	public List getCity(Integer provinceId)  {
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
		try{
			rest.setData(dmsStorageAreaService.findById(id));
		}catch (Exception e){
			this.logger.warn("通过id查询失败：" + id,e);
			rest.setCode(JdResponse.CODE_FAIL);
		}
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
		DmsStorageArea newDmsStorageArea = null;
		try{
			dmsStorageArea = dmsStorageAreaService.getUserInfo(dmsStorageArea);
			Integer dmsSiteCode = dmsStorageArea.getDmsSiteCode();
			Integer dmsProvinceCode = dmsStorageArea.getDesProvinceCode();
			Integer dmsCityCode = dmsStorageArea.getDesCityCode();
			newDmsStorageArea = dmsStorageAreaService.findByProAndCity(dmsSiteCode,dmsProvinceCode,dmsCityCode);
		}catch (Exception e){
			this.logger.warn("获取信息失败",e);
			rest.setCode(JdResponse.CODE_FAIL);
		}
		if(dmsStorageArea.getStorageCode() == newDmsStorageArea.getStorageCode()){
			rest.setCode(JdResponse.CODE_FAIL);
			rest.setMessage("同一省市只有一个库位号！");
		}else{
			try {
				rest.setData(dmsStorageAreaService.saveOrUpdate(dmsStorageArea));
				rest.setCode(JdResponse.CODE_SUCCESS);
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
		try{
			rest.setData(dmsStorageAreaService.queryByPagerCondition(dmsStorageAreaCondition));
		}catch (Exception e){
			e.printStackTrace();		}
		return rest.getData();
	}

	/**
     * @param file
     * @return*/
	@RequestMapping("/uploadExcel")
    @ResponseBody
    public JdResponse uploadExcel( @RequestParam("importExcelFile") MultipartFile file) {
		logger.debug("uploadExcelFile begin...");
		String errorString = "";
		try {
			ErpUserClient.ErpUser erpUser = ErpUserClient.getCurrUser();
			String createUserCode = "";
			String createUserName = "";
			Date createTime = new Date();

			if(erpUser!=null){
				createUserCode = erpUser.getUserCode();
				createUserName = erpUser.getUserName();
			}
			BaseStaffSiteOrgDto bssod = baseMajorManager.getBaseStaffByErpNoCache(createUserCode);
			String fileName = file.getOriginalFilename();

			int type = 0;
			if (fileName.endsWith("xls") || fileName.endsWith("XLS")) {
				type = 1;
			} else if(fileName.endsWith("xlsx") || fileName.endsWith("XLSX")){
				type = 2;
			}
			DataResolver dataResolver = ExcelDataResolverFactory.getDataResolver(type);
			List<DmsStorageArea> dataList = null;

			dataList = dataResolver.resolver(file.getInputStream(), DmsStorageArea.class, new PropertiesMetaDataFactory("/excel/dmsStorageArea.properties"));
			if (dataList != null && dataList.size() > 0) {
				if (dataList.size() > 1000) {
					errorString = "导入数据超出1000条";
					return new JdResponse(JdResponse.CODE_FAIL,errorString);
				}
				//批量插入数据
				dmsStorageAreaService.importExcel(dataList,createUserCode,createUserName,createTime);

			} else {
				errorString = "导入数据表格为空，请检查excel数据";
				return new JdResponse(JdResponse.CODE_FAIL,errorString);
			}
		} catch (Exception e) {
			if (e instanceof IllegalArgumentException) {
				errorString = e.getMessage();
			} else {
				logger.error("导入异常信息：", e);
				errorString = "导入出现异常";
			}
			return new JdResponse(JdResponse.CODE_FAIL,errorString);
		}
		return new JdResponse();
    }

}
