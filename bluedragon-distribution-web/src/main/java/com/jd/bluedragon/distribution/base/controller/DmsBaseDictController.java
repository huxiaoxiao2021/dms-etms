package com.jd.bluedragon.distribution.base.controller;

import java.util.List;

import com.jd.bluedragon.Constants;
import com.jd.uim.annotation.Authorization;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.jd.bluedragon.distribution.base.domain.DmsBaseDict;
import com.jd.bluedragon.distribution.base.domain.DmsBaseDictCondition;
import com.jd.bluedragon.distribution.base.service.DmsBaseDictService;
import com.jd.ql.dms.common.domain.JdResponse;
import com.jd.ql.dms.common.web.mvc.api.PagerResult;

/**
 * 
 * @ClassName: DmsBaseDictController
 * @Description: 字典管理
 * @author: wuyoude
 * @date: 2017年12月23日 下午9:49:23
 *
 */
@Controller
@RequestMapping("base/dmsBaseDict")
public class DmsBaseDictController {
	
	private static final Log logger = LogFactory.getLog(DmsBaseDictController.class);
	
	@Autowired
	DmsBaseDictService dmsBaseDictService;
	
    /**
     * 根据id获取实体基本信息
     * @return
     */
    @Authorization(Constants.DMS_WEB_DEVELOP_DICT_R)
    @RequestMapping(value = "/toIndex")
    public String toIndex() {
    	String rest ="/infoSite/infoSiteList";
    	rest ="/base/dmsBaseDict";
    	return rest;
    }
    /**
     * 根据id获取实体基本信息
     * @param id
     * @return
     */
    @Authorization(Constants.DMS_WEB_DEVELOP_DICT_R)
    @RequestMapping(value = "/detail/{id}")
    public @ResponseBody JdResponse<DmsBaseDict> detail(@PathVariable("id") Long id) {
    	JdResponse<DmsBaseDict> rest = new JdResponse<DmsBaseDict>();
    	rest.setData(dmsBaseDictService.findById(id));
    	return rest;
    }
    /**
     * 保存数据
     * @param dmsBaseDict
     * @return
     */
    @Authorization(Constants.DMS_WEB_DEVELOP_DICT_R)
    @RequestMapping(value = "/save")
    public @ResponseBody JdResponse<Boolean> save(@RequestBody DmsBaseDict dmsBaseDict) {
    	JdResponse<Boolean> rest = new JdResponse<Boolean>();
    	try {
			rest.setData(dmsBaseDictService.saveOrUpdate(dmsBaseDict));
		} catch (Exception e) {
			logger.error("fail to save！"+e.getMessage(),e);
			rest.toError("保存失败，服务异常！");
		}
    	return rest;
    }
    /**
     * 根据id删除一条数据
     * @param ids
     * @return
     */
    @Authorization(Constants.DMS_WEB_DEVELOP_DICT_R)
    @RequestMapping(value = "/deleteByIds")
    public @ResponseBody JdResponse<Integer> deleteByIds(@RequestBody List<Long> ids) {
    	JdResponse<Integer> rest = new JdResponse<Integer>();
    	try {
			rest.setData(dmsBaseDictService.deleteByIds(ids));
		} catch (Exception e) {
			logger.error("fail to delete！"+e.getMessage(),e);
			rest.toError("删除失败，服务异常！");
		}
    	return rest;
    }
    /**
     * 根据条件分页查询数据信息
     * @param dmsBaseDictCondition
     * @return
     */
    @Authorization(Constants.DMS_WEB_DEVELOP_DICT_R)
    @RequestMapping(value = "/listData")
    public @ResponseBody PagerResult<DmsBaseDict> listData(@RequestBody DmsBaseDictCondition dmsBaseDictCondition) {
    	JdResponse<PagerResult<DmsBaseDict>> rest = new JdResponse<PagerResult<DmsBaseDict>>();
    	rest.setData(dmsBaseDictService.queryByPagerCondition(dmsBaseDictCondition));
    	return rest.getData();
    }

    /**
     * 根据typeCode获取页面选择框、业务枚举值
     * @return
     */
    @Authorization(Constants.DMS_WEB_DEVELOP_DICT_R)
    @RequestMapping(value = "/selectAndEnum/{typeCode}")
    public @ResponseBody JdResponse<List<DmsBaseDict>> getSelect(@PathVariable("typeCode") Integer typeCode) {
        JdResponse<List<DmsBaseDict>> rest = new JdResponse<List<DmsBaseDict>>();
        Long id = dmsBaseDictService.queryByTypeCodeAndParentId(typeCode, DmsBaseDictService.DIC_ROOT_ENUM_SELECT_TYPE_GROUPS).getId();
        rest.setData(dmsBaseDictService.queryListByParentId(id.intValue()));
        return rest;
    }

    /**
     * 根据条件分页查询数据信息
     * @return
     */
//    @Authorization(Constants.DMS_WEB_DEVELOP_DICT_R)
    @RequestMapping(value = "/airRailwayExceptionType")
    public @ResponseBody JdResponse<List<DmsBaseDict>> getAirRailwayExceptionType() {
        JdResponse<List<DmsBaseDict>> rest = new JdResponse<List<DmsBaseDict>>();
        rest.setData(dmsBaseDictService.queryByParentIdAndTypeGroup(1, null));
        return rest;
    }
    /**
     * 根据条件分页查询数据信息
     * @return
     */
//    @Authorization(Constants.DMS_WEB_DEVELOP_DICT_R)
    @RequestMapping(value = "/airRailwayExceptionReason/{typeGroup}")
    public @ResponseBody JdResponse<List<DmsBaseDict>> getAirRailwayExceptionReason(@PathVariable("typeGroup") Integer typeGroup) {
    	JdResponse<List<DmsBaseDict>> rest = new JdResponse<List<DmsBaseDict>>();
    	rest.setData(dmsBaseDictService.queryByParentIdAndTypeGroup(2, typeGroup));
    	return rest;
    }

    /**
     * 根据条件分页查询数据信息
     * @return
     */
//    @Authorization(Constants.DMS_WEB_DEVELOP_DICT_R)
    @RequestMapping(value = "/airRailwayExceptionResult/{typeGroup}")
    public @ResponseBody JdResponse<List<DmsBaseDict>> getAirRailwayExceptionResult(@PathVariable("typeGroup") Integer typeGroup) {
        JdResponse<List<DmsBaseDict>> rest = new JdResponse<List<DmsBaseDict>>();
        rest.setData(dmsBaseDictService.queryByParentIdAndTypeGroup(3, typeGroup));
        return rest;
    }
    /**
     * 根据节点层级获取字典列表
     * @param nodeLevel
     * @return
     */
    @Authorization(Constants.DMS_WEB_DEVELOP_DICT_R)
    @RequestMapping(value = "/getDicListByNodeLevel/{nodeLevel}")
    public @ResponseBody JdResponse<List<DmsBaseDict>> getDicListByNodeLevel(@PathVariable("nodeLevel") Integer nodeLevel) {
        JdResponse<List<DmsBaseDict>> rest = new JdResponse<List<DmsBaseDict>>();
        DmsBaseDictCondition dmsBaseDictCondition = new DmsBaseDictCondition();
        dmsBaseDictCondition.setLimit(Integer.MAX_VALUE);
        if(nodeLevel != null && nodeLevel >= 0){
        	dmsBaseDictCondition.setNodeLevel(nodeLevel);
        }
        rest.setData(dmsBaseDictService.queryByCondition(dmsBaseDictCondition));
        return rest;
    }
    /**
     * 获取所有分组信息
     * @return
     */
    @Authorization(Constants.DMS_WEB_DEVELOP_DICT_R)
    @RequestMapping(value = "/getAllDicGroups")
    public @ResponseBody JdResponse<List<DmsBaseDict>> getAllDicGroups() {
        JdResponse<List<DmsBaseDict>> rest = new JdResponse<List<DmsBaseDict>>();
        rest.setData(dmsBaseDictService.queryAllGroups());
        return rest;
    }
}