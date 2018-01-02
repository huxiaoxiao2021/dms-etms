package com.jd.bluedragon.distribution.transport.controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;
import java.util.List;
import java.util.zip.DataFormatException;

import com.jd.bluedragon.distribution.api.request.CrossBoxRequest;
import com.jd.bluedragon.distribution.api.utils.JsonHelper;
import com.jd.bluedragon.distribution.basic.DataResolver;
import com.jd.bluedragon.distribution.basic.ExcelDataResolverFactory;
import com.jd.bluedragon.distribution.basic.PropertiesMetaDataFactory;
import com.jd.bluedragon.distribution.web.ErpUserClient;
import com.jd.bluedragon.distribution.web.view.DefaultExcelView;
import com.jd.bluedragon.utils.DateHelper;
import com.jd.ql.dms.common.domain.JdResponse;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.poi.ss.usermodel.Sheet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.jd.bluedragon.distribution.transport.domain.ArBookingSpace;
import com.jd.bluedragon.distribution.transport.domain.ArBookingSpaceCondition;
import com.jd.bluedragon.distribution.transport.service.ArBookingSpaceService;

import com.jd.ql.dms.common.web.mvc.api.PagerResult;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletResponse;

/**
 * 
 * @ClassName: ArBookingSpaceController
 * @Description: TODO
 * @author: wuyoude
 * @date: 2017年12月23日 下午9:49:23
 *
 */
@Controller
@RequestMapping("transport/arBookingSpace")
public class ArBookingSpaceController {
	
	private static final Log logger = LogFactory.getLog(ArBookingSpaceController.class);
	
	@Autowired
	ArBookingSpaceService arBookingSpaceService;
	
    /**
     * 根据id获取实体基本信息
     * @ id
     * @return
     */
    @RequestMapping(value = "/toIndex")
    public String toIndex() {
    	return "/transport/arBookingSpace";
    }
    /**
     * 根据id获取实体基本信息
     * @param id
     * @return
     */
    @RequestMapping(value = "/detail/{id}")
    public @ResponseBody JdResponse<ArBookingSpace> detail(@PathVariable("id") Long id) {
    	JdResponse<ArBookingSpace> rest = new JdResponse<ArBookingSpace>();
    	rest.setData(arBookingSpaceService.findById(id));
    	return rest;
    }
    /**
     * 保存数据
     * @return
     */
    @RequestMapping(value = "/save")
    public @ResponseBody JdResponse<Boolean> save(ArBookingSpace arBookingSpace) {
    	JdResponse<Boolean> rest = new JdResponse<Boolean>();
		ErpUserClient.ErpUser erpUser = ErpUserClient.getCurrUser();
		String userCode = "";
		String userName = "";
		Long createSiteCode = new Long(-1);
		String createSiteName = "";

		if(erpUser!=null){
			 userCode = erpUser.getUserCode();
			 userName = erpUser.getUserName();
		}

    	try {
			rest.setData(arBookingSpaceService.saveOrUpdate(arBookingSpace,userCode,userName,createSiteCode,createSiteName));
		} catch (Exception e) {
			logger.error("fail to save！"+e.getMessage(),e);
			rest.toError("保存失败，服务异常！");
		}
    	return rest;
    }
    /**
     * 根据id删除一条数据
     * @return
     */
    @RequestMapping(value = "/deleteByIds")
    public @ResponseBody JdResponse<Integer> deleteByIds(@RequestBody List<Long> ids) {
    	JdResponse<Integer> rest = new JdResponse<Integer>();
    	try {
			rest.setData(arBookingSpaceService.deleteByIds(ids));
		} catch (Exception e) {
			logger.error("fail to delete！"+e.getMessage(),e);
			rest.toError("删除失败，服务异常！");
		}
    	return rest;
    }
    /**
     * 根据条件分页查询数据信息
     * @return
     */
    @RequestMapping(value = "/listData")
    public @ResponseBody PagerResult<ArBookingSpace> listData(@RequestBody ArBookingSpaceCondition arBookingSpaceCondition) {
    	JdResponse<PagerResult<ArBookingSpace>> rest = new JdResponse<PagerResult<ArBookingSpace>>();
    	//模糊字段 运力名称
		if(StringUtils.isNotBlank(arBookingSpaceCondition.getTransportName())){
			arBookingSpaceCondition.setTransportName("%"+arBookingSpaceCondition.getTransportName()+"%");
		}
    	rest.setData(arBookingSpaceService.queryByPagerCondition(arBookingSpaceCondition));
    	return rest.getData();
    }

	@RequestMapping(value = "/toExport")
	public ModelAndView toExport(ArBookingSpaceCondition arBookingSpaceCondition, Model model) {
		try {

			List<List<Object>> resultList = arBookingSpaceService.getExportData(arBookingSpaceCondition);

			model.addAttribute("filename", "arBookingSpaceExport.xls");
			model.addAttribute("sheetname", "订舱登记导出结果");
			model.addAttribute("contents", resultList);

			return new ModelAndView(new DefaultExcelView(), model.asMap());

		} catch (Exception e) {
			logger.error("toExport:" + e.getMessage(), e);
			return null;
		}
	}

	@RequestMapping(value = "/uploadExcel", method = RequestMethod.POST)
	public @ResponseBody JdResponse uploadExcel( @RequestParam("importExcelFile") MultipartFile file) {
		logger.debug("uploadExcelFile begin...");

		try {

			ErpUserClient.ErpUser erpUser = ErpUserClient.getCurrUser();
			String userCode = "";
			String userName = "";
			Long createSiteCode = new Long(-1);
			String createSiteName = "";

			if(erpUser!=null){
				userCode = erpUser.getUserCode();
				userName = erpUser.getUserName();
			}

			String fileName = file.getOriginalFilename();

			String errorString = null;
			int type = 0;
			if (fileName.endsWith("xls") || fileName.endsWith("XLS")) {
				type = 1;
			} else {
				type = 2;
			}
			DataResolver dataResolver = ExcelDataResolverFactory.getDataResolver(type);
			List<ArBookingSpace> dataList = null;
			try {
				dataList = dataResolver.resolver(file.getInputStream(), ArBookingSpace.class, new PropertiesMetaDataFactory("/excel/arBookingSpace.properties"));
				if (dataList != null && dataList.size() > 0) {
					if (dataList.size() > 1000) {
						errorString = "导入数据超出1000条";
						return new JdResponse(JdResponse.CODE_FAIL,errorString);
					}

					//批量插入数据
					arBookingSpaceService.importExcel(dataList,userCode,userName,createSiteCode,createSiteName);

				} else {
					errorString = "导入数据过多或者异常，请检查excel数据";
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
		} catch (Exception e) {
			logger.error("执行uploadExcelFile异常" + e.getMessage(), e);
			throw new RuntimeException(e.getMessage());
		}
		return new JdResponse();
	}


}