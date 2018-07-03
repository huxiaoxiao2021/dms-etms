package com.jd.bluedragon.distribution.base.controller;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.core.base.BaseMajorManager;
import com.jd.bluedragon.distribution.b2bRouter.domain.ProvinceAndCity;
import com.jd.bluedragon.distribution.base.domain.DmsStorageArea;
import com.jd.bluedragon.distribution.base.domain.DmsStorageAreaCondition;
import com.jd.bluedragon.distribution.base.service.DmsStorageAreaService;
import com.jd.bluedragon.distribution.base.service.ProvinceAndCityService;
import com.jd.bluedragon.distribution.base.service.SiteService;
import com.jd.bluedragon.distribution.basic.DataResolver;
import com.jd.bluedragon.distribution.basic.ExcelDataResolverFactory;
import com.jd.bluedragon.distribution.basic.PropertiesMetaDataFactory;
import com.jd.bluedragon.distribution.web.ErpUserClient;
import com.jd.bluedragon.domain.AreaNode;
import com.jd.bluedragon.domain.ProvinceNode;
import com.jd.bluedragon.utils.AreaHelper;
import com.jd.bluedragon.utils.ObjectHelper;
import com.jd.bluedragon.utils.StringHelper;
import com.jd.ql.basic.dto.BaseStaffSiteOrgDto;
import com.jd.ql.basic.ws.BasicPrimaryWS;
import com.jd.ql.dms.common.domain.JdResponse;
import com.jd.ql.dms.common.web.mvc.api.PagerResult;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author wuyoude
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

    @Autowired
    private SiteService siteService;

    @Autowired
    @Qualifier("basicPrimaryWS")
    private BasicPrimaryWS basicPrimaryWS;


    /**
     * 返回主页面
     *
     * @return
     */
    @RequestMapping(value = "/toIndex")
    public String toIndex() {
        return "/base/dmsStorageArea";
    }

    /**
     * 获取所有的省份
     *
     * @return
     */
    @RequestMapping("/getProvinceList")
    @ResponseBody
    public List getProvince() {
        this.logger.info("获取所有的省份");
        List<ProvinceNode> provinces = new ArrayList<ProvinceNode>();
        try {
            provinces.addAll(AreaHelper.getAllProvince());
        } catch (Exception e) {
            this.logger.warn("获取所有的省份失败", e);
        }
        return provinces;
    }

    /**
     * 根据省Id获得对应的市
     *
     * @param provinceId
     * @return
     */
    @RequestMapping("/getCityList")
    @ResponseBody
    public List getCity(Integer provinceId) {
        this.logger.info("获取对应省下的所有城市");
        List<ProvinceAndCity> cities = new ArrayList<ProvinceAndCity>();
        try {
            cities = provinceAndCityService.getCityByProvince(provinceId);
        } catch (Exception e) {
            this.logger.warn("根据省份Id获取城市失败：" + provinceId, e);
        }
        return cities;
    }

    @ResponseBody
    @RequestMapping("/getAllArea")
    public Object getAllArea(String isDefault) {
        List<AreaNode> areas = new ArrayList<AreaNode>();
        ErpUserClient.ErpUser erpUser = ErpUserClient.getCurrUser();
        BaseStaffSiteOrgDto dto = basicPrimaryWS.getBaseStaffByStaffId(10053);
//            BaseStaffSiteOrgDto dto = basicPrimaryWS.getBaseStaffByStaffId(erpUser.getUserId());
        if (dto.getSiteType() == Constants.BASE_SITE_DISTRIBUTION_CENTER) {//分拣中心的人 只能看本地的
            return areas;
        } else {
            if (StringHelper.isEmpty(isDefault) || isDefault.equals("true")) {
                areas.add(new AreaNode(-1, "全部"));
            }
            areas.addAll(AreaHelper.getAllArea());
        }
        return areas;
    }

    /**
     * 根据条件获取城市信息
     * 1、选了省，获取省下属的城市
     * 2、不选省，选择了区域，获取区域内的所有城市
     * 3、不选区域，获取全国所有的城市
     *
     * @param areaId
     * @param provinceId
     * @param isDefault
     * @return
     */
    @ResponseBody
    @RequestMapping("/getCityListByKey")
    public Object getCityList(Integer areaId, Integer provinceId, String isDefault) {
        List<ProvinceAndCity> cities = new ArrayList<ProvinceAndCity>();
        ErpUserClient.ErpUser erpUser = ErpUserClient.getCurrUser();
        BaseStaffSiteOrgDto dto = basicPrimaryWS.getBaseStaffByStaffId(erpUser.getUserId());
        //        BaseStaffSiteOrgDto dto = basicPrimaryWS.getBaseStaffByStaffId(10053);
        if (dto.getSiteType() == Constants.BASE_SITE_DISTRIBUTION_CENTER) {//分拣中心的人 只能看本地的
            return cities;
        } else {
            if (StringHelper.isEmpty(isDefault) || isDefault.equals("true")) {
                cities.add(new ProvinceAndCity("-1", "全部"));
            }
            if (provinceId != null && provinceId != -1) {
                cities.addAll(provinceAndCityService.getCityByProvince(provinceId));
            } else if (areaId != null && areaId != -1) {
                //获取区域下所有的省
                List<Integer> provinceIdList = new ArrayList<Integer>(AreaHelper.getProvinceIdsByAreaId(areaId));
                cities.addAll(provinceAndCityService.getCityByProvince(provinceIdList));
            } else {
                List<Integer> provinceIdList = new ArrayList<Integer>(AreaHelper.getAllProvinceIds());
                cities.addAll(provinceAndCityService.getCityByProvince(provinceIdList));
            }
        }
        return cities;
    }

    /**
     * 根据条件获取省
     * 选择了区域orgId不为空且不为-1，加载区域内的省
     * 不选区域，加载全国所有的省
     *
     * @param areaId
     * @param isDefault
     * @return
     */
    @ResponseBody
    @RequestMapping("/getProvinceListByKey")
    public Object getProvinceList(Integer areaId, String isDefault) {
        ErpUserClient.ErpUser erpUser = ErpUserClient.getCurrUser();
//        BaseStaffSiteOrgDto dto = basicPrimaryWS.getBaseStaffByStaffId(10053);
            BaseStaffSiteOrgDto dto = basicPrimaryWS.getBaseStaffByStaffId(erpUser.getUserId());
        List<ProvinceNode> provinces = new ArrayList<ProvinceNode>();
        if (dto.getSiteType() == Constants.BASE_SITE_DISTRIBUTION_CENTER) {//分拣中心的人 只能看本地的
            return provinces;
        } else {
            //区域不选，加载全国所有的省
            if (StringHelper.isEmpty(isDefault) || isDefault.equals("true")) {
                provinces.add(new ProvinceNode(-1, "全部"));
            }
            if (areaId != null && areaId != -1) {
                provinces.addAll(AreaHelper.getProvincesByAreaId(areaId));
            } else {
                provinces.addAll(AreaHelper.getAllProvince());
            }
        }
        return provinces;
    }

    /**
     * 根据条件获取分拣中心信息
     * 1、如果选择了城市cityId不为空且不为-1，加载城市下的分拣中心
     * 2、不选城市，选择了省provinceId不为空且不为-1，加载省下面的分拣中心
     * 3、不选省，选择了区域orgId不为空且不为-1，加载区域下面的分拣中心
     * 4、不选区域，加载全国的分拣中心
     *
     * @param areaId
     * @param provinceId
     * @param cityId
     * @return
     */
    @ResponseBody
    @RequestMapping("/getSiteListByKey")
    public Object getSiteList(Integer areaId, Integer provinceId, Integer cityId, String isDefault) {
        List<BaseStaffSiteOrgDto> allDms = new ArrayList<BaseStaffSiteOrgDto>();
        try {
            ErpUserClient.ErpUser erpUser = ErpUserClient.getCurrUser();
//            BaseStaffSiteOrgDto dto = basicPrimaryWS.getBaseStaffByStaffId(10053);
            BaseStaffSiteOrgDto dto = basicPrimaryWS.getBaseStaffByStaffId(erpUser.getUserId());
            if (dto.getSiteType() == Constants.BASE_SITE_DISTRIBUTION_CENTER) {//分拣中心的人 只能看本地的
                allDms.add(dto);
            } else {
                if (StringHelper.isEmpty(isDefault) || isDefault.equals("true")) {
                    BaseStaffSiteOrgDto all = new BaseStaffSiteOrgDto();
                    all.setDmsSiteCode("-1");
                    all.setSiteName("全部");
                    allDms.add(all);
                }
                if (cityId != null && cityId != -1) {
                    allDms.addAll(siteService.getDmsListByCity(cityId));
                } else if (provinceId != null && provinceId != -1) {
                    allDms.addAll(siteService.getDmsListByProvince(provinceId));
                } else if (areaId != null && areaId != -1) {
                    allDms.addAll(siteService.getDmsListByAreaId(areaId));
                } else {
                    allDms.addAll(siteService.getAllDmsSite());
                }
            }
        } catch (Exception e) {
            this.logger.error("加载站点失败areaId：" + areaId + "provinceId:" + provinceId + "cityId:" + cityId, e);
        }
        return allDms;
    }

    /**
     * 根据id获取基本信息
     *
     * @param id
     * @return
     */
    @RequestMapping(value = "/detail/{id}")
    public @ResponseBody
    JdResponse<DmsStorageArea> detail(@PathVariable("id") Long id) {
        JdResponse<DmsStorageArea> rest = new JdResponse<DmsStorageArea>();
        try {
            rest.setData(dmsStorageAreaService.findById(id));
        } catch (Exception e) {
            this.logger.warn("通过id查询失败：" + id, e);
            rest.setCode(JdResponse.CODE_FAIL);
        }
        return rest;
    }

    /**
     * 保存数据
     *
     * @param dmsStorageArea
     * @return
     */
    @RequestMapping(value = "/save")
    public @ResponseBody
    JdResponse<Boolean> save(DmsStorageArea dmsStorageArea) {
        JdResponse<Boolean> rest = new JdResponse<Boolean>();
        try {
            ErpUserClient.ErpUser erpUser = ErpUserClient.getCurrUser();

            BaseStaffSiteOrgDto baseStaffByErpNoCache = baseMajorManager.getBaseStaffByErpNoCache(erpUser.getUserCode());
            Integer dmsSiteCode = baseStaffByErpNoCache.getSiteCode();
            Integer siteType = baseStaffByErpNoCache.getSiteType();
            if (siteType != 64) {
                rest.setCode(JdResponse.CODE_FAIL);
                rest.setMessage("该操作机构不是分拣中心！");
                return rest;
            }
            String dmsSiteName = baseStaffByErpNoCache.getSiteName();
            dmsStorageArea.setStorageType(1);
            dmsStorageArea.setDmsSiteCode(dmsSiteCode);
            dmsStorageArea.setDmsSiteName(dmsSiteName);
            if (dmsStorageArea.getId() == null) {
                dmsStorageArea.setCreateUser(erpUser.getUserCode());
                dmsStorageArea.setCreateUserName(erpUser.getUserName());
                dmsStorageArea.setUpdateUser(erpUser.getUserCode());
                dmsStorageArea.setUpdateUserName(erpUser.getUserName());
                dmsStorageArea.setCreateTime(new Date());
                dmsStorageArea.setUpdateTime(new Date());
                if (dmsStorageAreaService.isExist(dmsStorageArea)) {
                    rest.setCode(JdResponse.CODE_FAIL);
                    rest.setMessage("同一省+市只能对应一个库位号！");
                    return rest;
                }
            } else {
                DmsStorageArea oldData = dmsStorageAreaService.findById(dmsStorageArea.getId());
                if (oldData != null && !dmsSiteCode.equals(oldData.getDmsSiteCode())) {
                    rest.toFail("只能修改本分拣中心的数据！");
                    return rest;
                }
                if (this.isProvinceOrCityChanged(dmsStorageArea, oldData)
                        && dmsStorageAreaService.isExist(dmsStorageArea)) {
                    rest.toFail("同一省+市只能对应一个库位号！");
                    return rest;
                }
                dmsStorageArea.setUpdateTime(new Date());
                dmsStorageArea.setUpdateUser(erpUser.getUserCode());
                dmsStorageArea.setUpdateUserName(erpUser.getUserName());
            }
        } catch (Exception e) {
            this.logger.warn("获取信息失败", e);
            rest.setCode(JdResponse.CODE_FAIL);
            rest.setMessage("获取信息失败");
            return rest;
        }
        try {
            rest.setData(dmsStorageAreaService.saveOrUpdate(dmsStorageArea));
            rest.setCode(JdResponse.CODE_SUCCESS);
        } catch (Exception e) {
            logger.error("fail to save！" + e.getMessage(), e);
            rest.setCode(JdResponse.CODE_FAIL);
            rest.setMessage("保存失败，服务异常！");
        }
        return rest;
    }

    /**
     * 判断城市/省是否已变更
     *
     * @param newData
     * @param oldData
     * @return
     */
    private boolean isProvinceOrCityChanged(DmsStorageArea newData, DmsStorageArea oldData) {
        if (newData != null && oldData != null) {
            return (ObjectHelper.compare(oldData.getDesProvinceCode(), newData.getDesProvinceCode()) != 0
                    || ObjectHelper.compare(oldData.getDesCityCode(), newData.getDesCityCode()) != 0);
        }
        return false;
    }

    /**
     * 根据id删除多条数据
     *
     * @param ids
     * @return
     */
    @RequestMapping(value = "/deleteByIds")
    public @ResponseBody
    JdResponse<Integer> deleteByIds(@RequestBody List<Long> ids) {
        JdResponse<Integer> rest = new JdResponse<Integer>();
        try {
            rest.setData(dmsStorageAreaService.deleteByIds(ids));
        } catch (Exception e) {
            logger.error("fail to delete！" + e.getMessage(), e);
            rest.toError("删除失败，服务异常！");
        }
        return rest;
    }

    /**
     * 根据条件分页查询数据信息
     *
     * @param dmsStorageAreaCondition
     * @return
     */
    @RequestMapping(value = "/listData")
    public @ResponseBody
    PagerResult<DmsStorageArea> listData(@RequestBody DmsStorageAreaCondition dmsStorageAreaCondition) {
        JdResponse<PagerResult<DmsStorageArea>> rest = new JdResponse<PagerResult<DmsStorageArea>>();
        try {
            rest.setData(dmsStorageAreaService.queryByPagerCondition(dmsStorageAreaCondition));
        } catch (Exception e) {
            this.logger.error("服务异常查询失败！");
        }
        return rest.getData();
    }

    /**
     * 导入excel表格
     *
     * @param file
     * @return
     */
    @RequestMapping("/uploadExcel")
    @ResponseBody
    public JdResponse uploadExcel(@RequestParam("importExcelFile") MultipartFile file) {
        logger.debug("uploadExcelFile begin...");
        String errorString = "";
        try {
            ErpUserClient.ErpUser erpUser = ErpUserClient.getCurrUser();

            String createUser = "";
            String createUserName = "";
            Integer dmsSiteCode;
            String dmsSiteName = "";
            Date createTime = new Date();

            if (erpUser != null) {
                createUser = erpUser.getUserCode();
                createUserName = erpUser.getUserName();
            }
            BaseStaffSiteOrgDto bssod = baseMajorManager.getBaseStaffByErpNoCache(createUser);
            dmsSiteCode = bssod.getSiteCode();
            Integer siteType = bssod.getSiteType();
            if (siteType != 64) {
                this.logger.warn("该操作机构不是分拣中心");
                errorString = "该操作机构不是分拣中心！";
                return new JdResponse(JdResponse.CODE_FAIL, errorString);
            }
            dmsSiteName = bssod.getSiteName();
            String fileName = file.getOriginalFilename();

            int type = 0;
            if (fileName.endsWith("xls") || fileName.endsWith("XLS")) {
                type = 1;
            } else if (fileName.endsWith("xlsx") || fileName.endsWith("XLSX")) {
                type = 2;
            }
            DataResolver dataResolver = ExcelDataResolverFactory.getDataResolver(type);
            List<DmsStorageArea> dataList = null;

            dataList = dataResolver.resolver(file.getInputStream(), DmsStorageArea.class, new PropertiesMetaDataFactory("/excel/dmsStorageArea.properties"));
            //对导入的数据进行校验
            errorString = dmsStorageAreaService.checkExportData(dataList, dmsSiteCode, dmsSiteName);
            if (!"".equals(errorString)) {
                return new JdResponse(JdResponse.CODE_FAIL, errorString);
            }
            if (dataList != null && dataList.size() > 0) {
                if (dataList.size() > 1000) {
                    errorString = "导入数据超出1000条";
                    return new JdResponse(JdResponse.CODE_FAIL, errorString);
                }
                //批量插入数据
                Boolean aBoolean = dmsStorageAreaService.importExcel(dataList, createUser, createUserName, createTime);
                if (!aBoolean) {
                    errorString = "导入数据失败";
                    return new JdResponse(JdResponse.CODE_FAIL, errorString);
                }
            } else {
                errorString = "导入数据表格为空，请检查excel数据";
                return new JdResponse(JdResponse.CODE_FAIL, errorString);
            }
        } catch (Exception e) {
            if (e instanceof IllegalArgumentException) {
                errorString = e.getMessage();
            } else {
                this.logger.error("导入异常信息：", e);
                errorString = "导入出现异常";
            }
            return new JdResponse(JdResponse.CODE_FAIL, errorString);
        }
        return new JdResponse();
    }
}
