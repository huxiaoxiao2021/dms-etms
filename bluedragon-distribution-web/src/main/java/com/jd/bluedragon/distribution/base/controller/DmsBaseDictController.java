package com.jd.bluedragon.distribution.base.controller;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.distribution.api.utils.JsonHelper;
import com.jd.bluedragon.distribution.base.domain.DmsBaseDict;
import com.jd.bluedragon.distribution.base.domain.DmsBaseDictCondition;
import com.jd.bluedragon.distribution.base.service.DmsBaseDictService;
import com.jd.bluedragon.distribution.send.domain.SendDetail;
import com.jd.bluedragon.distribution.sendprint.service.SendPrintService;
import com.jd.bluedragon.utils.Md5Helper;
import com.jd.dms.wb.report.api.dto.printhandover.PrintHandoverListDto;
import com.jd.ql.dms.common.domain.JdResponse;
import com.jd.ql.dms.common.web.mvc.api.PagerResult;
import com.jd.uim.annotation.Authorization;

import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.List;

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
	
	private static final Logger log = LoggerFactory.getLogger(DmsBaseDictController.class);
	
	@Autowired
	DmsBaseDictService dmsBaseDictService;
	
	@Autowired
	SendPrintService sendPrintService;
	
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
			log.error("fail to save！",e);
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
			log.error("fail to delete！",e);
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
    /**
     * 获取所有分组信息
     * @return
     */
    @Authorization(Constants.DMS_WEB_DEVELOP_DICT_R)
    @RequestMapping(value = "/getPrintRecords/{sign}")
    public @ResponseBody JdResponse<List<PrintHandoverListDto>> getPrintRecords(@PathVariable("sign") String sign,@RequestBody List<SendDetail> sendList) {
        JdResponse<List<PrintHandoverListDto>> rest = new JdResponse<List<PrintHandoverListDto>>();
        if(sign != null && sign.equals(Md5Helper.encode(Md5Helper.encode(JsonHelper.toJson(sendList))))) {
        	rest.toFail("签名认证失败！");
        	return rest;
        }
        if(CollectionUtils.isEmpty(sendList)) {
        	rest.toFail("列表不能为空！");
        	return rest;
        }
        rest.setData(new ArrayList<PrintHandoverListDto>());
        for(SendDetail sendDetail : sendList) {
        	rest.getData().add(sendPrintService.buildPrintHandoverListDtoTmp(sendDetail));
        }
        return rest;
    }
}