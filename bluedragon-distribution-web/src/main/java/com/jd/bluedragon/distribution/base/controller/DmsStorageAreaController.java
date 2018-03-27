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
import com.sun.org.apache.xpath.internal.operations.Bool;
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
			ErpUserClient.ErpUser erpUser = ErpUserClient.getCurrUser();
			BaseStaffSiteOrgDto baseStaffByErpNoCache = baseMajorManager.getBaseStaffByErpNoCache(erpUser.getUserCode());
			Integer dmsSiteCode = baseStaffByErpNoCache.getSiteCode();
			Integer siteType = baseStaffByErpNoCache.getSiteType();
			if(siteType != 64){
				rest.setCode(JdResponse.CODE_FAIL);
				rest.setMessage("该操作机构不是分拣中心！");
				return rest;
			}
			String dmsSiteName = baseStaffByErpNoCache.getSiteName();
			Integer dmsProvinceCode = dmsStorageArea.getDesProvinceCode();
			Integer dmsCityCode = dmsStorageArea.getDesCityCode();
			dmsStorageArea.setDmsSiteCode(dmsSiteCode);
			dmsStorageArea.setDmsSiteName(dmsSiteName);
			dmsStorageArea.setStorageType(1);
			if(dmsStorageArea.getId() == null){
				dmsStorageArea.setCreateUser(erpUser.getUserCode());
				dmsStorageArea.setCreateUserName(erpUser.getUserName());
				dmsStorageArea.setUpdateUser(erpUser.getUserCode());
				dmsStorageArea.setUpdateUserName(erpUser.getUserName());
				dmsStorageArea.setCreateTime(new Date());
				dmsStorageArea.setUpdateTime(new Date());
			}else {
				dmsStorageArea.setUpdateTime(new Date());
				dmsStorageArea.setUpdateUser(erpUser.getUserCode());
				dmsStorageArea.setUpdateUserName(erpUser.getUserName());
			}
			newDmsStorageArea = dmsStorageAreaService.findByProAndCity(dmsSiteCode,dmsProvinceCode,dmsCityCode);
		}catch (Exception e){
			e.printStackTrace();
			this.logger.warn("获取信息失败",e);
			rest.setCode(JdResponse.CODE_FAIL);
			rest.setMessage("获取信息失败");
			return rest;
		}
		if(newDmsStorageArea != null && dmsStorageArea.getStorageCode().trim().equals(newDmsStorageArea.getStorageCode().trim()) ){
			rest.setCode(JdResponse.CODE_FAIL);
			rest.setMessage("同一省+市只对应一个库位号！");
		}else{
			try {
				rest.setData(dmsStorageAreaService.saveOrUpdate(dmsStorageArea));
				rest.setCode(JdResponse.CODE_SUCCESS);
			} catch (Exception e) {
				e.printStackTrace();
				logger.error("fail to save！"+e.getMessage(),e);
				rest.setCode(JdResponse.CODE_FAIL);
				rest.setMessage("保存失败，服务异常！");
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
			e.printStackTrace();
		}
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
			String createUser = "";
			String createUserName = "";
			Integer dmsSiteCode ;
			String dmsSiteName = "";
			Date createTime = new Date();

			if(erpUser!=null){
				createUser = erpUser.getUserCode();
				createUserName = erpUser.getUserName();
			}
			BaseStaffSiteOrgDto bssod = baseMajorManager.getBaseStaffByErpNoCache(createUser);
			dmsSiteCode = bssod.getDmsId();
			Integer siteType = bssod.getSiteType();
			if(siteType != 64){
				errorString = "该操作机构不是分拣中心！";
				return new JdResponse(JdResponse.CODE_FAIL,errorString);
			}
			dmsSiteName = bssod.getSiteName();
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
			//对导入的数据进行校验
			checkExportData(dataList,dmsSiteCode,dmsSiteName,errorString);
			if(errorString != ""){
				return new JdResponse(JdResponse.CODE_FAIL,errorString);
			}
			if (dataList != null && dataList.size() > 0) {
				if (dataList.size() > 1000) {
					errorString = "导入数据超出1000条";
					return new JdResponse(JdResponse.CODE_FAIL,errorString);
				}
				//批量插入数据
				Boolean aBoolean = dmsStorageAreaService.importExcel(dataList, createUser, createUserName, createTime);
				if(!aBoolean){
					errorString = "导入数据失败";
					return new JdResponse(JdResponse.CODE_FAIL,errorString);
				}
			} else {
				errorString = "导入数据表格为空，请检查excel数据";
				return new JdResponse(JdResponse.CODE_FAIL,errorString);
			}
		} catch (Exception e) {
			e.printStackTrace();
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

	/**
	 *	对导入的数据进行校验
	 * @param dataList
	 * @return*/
	private void checkExportData(List<DmsStorageArea> dataList,Integer dmsSiteCode,String dmsSiteName,String errorString) {
     	for (DmsStorageArea dmsStorageArea : dataList){
     		dmsStorageArea.setDmsSiteCode(dmsSiteCode);
			dmsStorageArea.setDmsSiteName(dmsSiteName);
			Integer proId = AreaHelper.getProIdByProName(dmsStorageArea.getDesProvinceName());
			if(proId == -1){
				errorString = "导入的省不存在！";
				return ;
			}else {
				dmsStorageArea.setDesProvinceCode(proId);
				List<ProvinceAndCity> cityList = provinceAndCityService.getCityByProvince(proId);
				for (ProvinceAndCity c : cityList){
					if(c.getAssortName().equals(dmsStorageArea.getDesCityName())){
						dmsStorageArea.setDesCityCode(Integer.parseInt(c.getAssortCode()));
					}else {
						errorString = "导入的市不存在！";
						return ;
					}
					DmsStorageArea byProAndCity = dmsStorageAreaService.findByProAndCity(dmsSiteCode, dmsStorageArea.getDesProvinceCode(), dmsStorageArea.getDesCityCode());
					if(byProAndCity != null && byProAndCity.getStorageCode().trim() == dmsStorageArea.getStorageCode().trim()){
						errorString = "同一省+市只能对应一个库位号！";
					}
				}
			}
		}
	}

}
