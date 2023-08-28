package com.jd.bluedragon.distribution.storage.controller;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.common.domain.ExportConcurrencyLimitEnum;
import com.jd.bluedragon.common.service.ExportConcurrencyLimitService;
import com.jd.bluedragon.core.base.BaseMajorManager;
import com.jd.bluedragon.core.base.WaybillQueryManager;
import com.jd.bluedragon.core.exception.StorageException;
import com.jd.bluedragon.distribution.base.controller.DmsBaseController;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.storage.domain.FulfillmentOrderDto;
import com.jd.bluedragon.distribution.storage.domain.PutawayDTO;
import com.jd.bluedragon.distribution.storage.domain.StoragePackageM;
import com.jd.bluedragon.distribution.storage.domain.StoragePackageMCondition;
import com.jd.bluedragon.distribution.storage.service.StoragePackageMService;
import com.jd.bluedragon.distribution.web.ErpUserClient;
import com.jd.bluedragon.distribution.web.view.DefaultExcelView;
import com.jd.bluedragon.dms.utils.BusinessUtil;
import com.jd.bluedragon.utils.CsvExporterUtils;
import com.jd.bluedragon.utils.DateHelper;
import com.jd.etms.waybill.domain.BaseEntity;
import com.jd.etms.waybill.dto.BigWaybillDto;
import com.jd.ql.basic.dto.BaseStaffSiteOrgDto;
import com.jd.ql.dms.common.domain.JdResponse;
import com.jd.ql.dms.common.web.mvc.api.PagerResult;
import com.jd.uim.annotation.Authorization;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 *
 * @ClassName: StoragePackageMController
 * @Description: 储位包裹主表--Controller实现
 * @author wuyoude
 * @date 2018年08月15日 18:27:23
 *
 */
@Controller
@RequestMapping("storage/storagePackageM")
public class StoragePackageMController extends DmsBaseController {

	private static final Logger log = LoggerFactory.getLogger(StoragePackageMController.class);

	@Autowired
	private BaseMajorManager baseMajorManager;

	@Autowired
	private StoragePackageMService storagePackageMService;

	@Autowired
	private WaybillQueryManager waybillQueryManager;

	@Autowired
	private ExportConcurrencyLimitService exportConcurrencyLimitService;

	/**
	 * 返回主页面
	 * @return
	 */
	@Authorization(Constants.DMS_WEB_EXPRESS_STORAGEPACKAGEM_R)
	@RequestMapping(value = "/toIndex")
	public String toIndex(Model model) {
		// 设置基础信息
		setBaseModelInfo(model);
		return "/storage/storagePackageM";
	}
	/**
	 * 根据id获取基本信息
	 * @param id
	 * @return
	 */
	@Authorization(Constants.DMS_WEB_EXPRESS_STORAGEPACKAGEM_R)
	@RequestMapping(value = "/detail/{id}")
	public @ResponseBody JdResponse<StoragePackageM> detail(@PathVariable("id") Long id) {
		JdResponse<StoragePackageM> rest = new JdResponse<StoragePackageM>();
		rest.setData(storagePackageMService.findById(id));
		return rest;
	}
	/**
	 * 保存数据
	 * @param storagePackageM
	 * @return
	 */
	@Authorization(Constants.DMS_WEB_EXPRESS_STORAGEPACKAGEM_R)
	@RequestMapping(value = "/save")
	public @ResponseBody JdResponse<Boolean> save(@RequestBody StoragePackageM storagePackageM) {
		JdResponse<Boolean> rest = new JdResponse<Boolean>();
		try {
			rest.setData(storagePackageMService.saveOrUpdate(storagePackageM));
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
			rest.setData(storagePackageMService.deleteByIds(ids));
		} catch (Exception e) {
			log.error("fail to delete！",e);
			rest.toError("删除失败，服务异常！");
		}
		return rest;
	}
	/**
	 * 根据条件分页查询数据信息
	 * @param storagePackageMCondition
	 * @return
	 */
	@Authorization(Constants.DMS_WEB_EXPRESS_STORAGEPACKAGEM_R)
	@RequestMapping(value = "/listData")
	public @ResponseBody PagerResult<StoragePackageM> listData(@RequestBody StoragePackageMCondition storagePackageMCondition) {
		JdResponse<PagerResult<StoragePackageM>> rest = new JdResponse<PagerResult<StoragePackageM>>();
		rest.setData(storagePackageMService.queryByPagerCondition(storagePackageMCondition));
		return rest.getData();
	}


	/**
	 *  暂存管理检查
	 * @param storagePackageMCondition
	 * @return
	 */
	private boolean check(StoragePackageMCondition storagePackageMCondition,JdResponse<PagerResult<StoragePackageM>> response){


		return true;
	}

	/**
	 * 强制发货  作废（防止二期会继续做，暂时先保留代码）

	 * @return
	 */
	@Authorization(Constants.DMS_WEB_EXPRESS_STORAGEPACKAGEM_R)
	@Deprecated
	@RequestMapping(value = "/forceSend")
	public @ResponseBody JdResponse<Boolean> forceSend(@RequestBody List<String> performanceCodes) {
		JdResponse<Boolean> rest = new JdResponse<Boolean>();

		ErpUserClient.ErpUser erpUser = ErpUserClient.getCurrUser();
		String userCode = "";
		Integer createSiteCode = new Integer(-1);
		String createSiteName = "未获取到";

		if(erpUser!=null){
			userCode = erpUser.getUserCode();
			BaseStaffSiteOrgDto bssod = baseMajorManager.getBaseStaffByErpNoCache(userCode);
			if (bssod!=null && bssod.getSiteType() == 64) {/** 站点类型为64的时候为分拣中心 **/
				createSiteCode = bssod.getSiteCode();
				createSiteName = bssod.getSiteName();
			}
		}else{
			rest.toError("强制发货失败，未获取到当前登录人信息！");
			return rest;
		}

		//组装 上架请求体
		PutawayDTO putawayDTO = new PutawayDTO();
		putawayDTO.setOperatorId(erpUser.getUserId());
		putawayDTO.setOperatorErp(erpUser.getUserCode());
		putawayDTO.setOperatorName(erpUser.getUserName());
		putawayDTO.setCreateSiteCode(createSiteCode);
		putawayDTO.setCreateSiteName(createSiteName);
		putawayDTO.setOperateTime(System.currentTimeMillis());


		try {
			rest.setData(storagePackageMService.forceSend(performanceCodes,putawayDTO));
		} catch (Exception e) {
			log.error("fail to forceSend！",e);
			rest.toError("强制发货失败，服务异常！");
		}
		return rest;
	}


    /**
     * 撤销上架功能
     * @param ids
     * @return
     */
	@Authorization(Constants.DMS_WEB_EXPRESS_STORAGEPACKAGEM_R)
    @RequestMapping(value = "/cancelPutaway")
    public @ResponseBody JdResponse<Boolean> cancelPutaway(@RequestBody List<Long> ids) {
        JdResponse<Boolean> rest = new JdResponse<Boolean>();
        try {
            rest.setData(storagePackageMService.cancelPutaway(ids));
        } catch (Exception e) {
            log.error("fail to delete！",e);
            rest.toError("删除失败，服务异常！");
        }
        return rest;
    }


	/**
	 * 刷新状态
	 * @param waybillCode 运单号
	 * @return
	 */
	@Authorization(Constants.DMS_WEB_EXPRESS_STORAGEPACKAGEM_R)
	@RequestMapping(value = "/refreshSendStatus/{waybillCode}")
	public @ResponseBody JdResponse<Boolean> refreshSendStatus(@PathVariable("waybillCode") String waybillCode) {
		JdResponse<Boolean> rest = new JdResponse<Boolean>();
		rest.setData(false);
		try {
			storagePackageMService.updateStoragePackageMStatusForSend(waybillCode);
			rest.setData(true);
		} catch (StorageException e) {
			rest.setMessage(e.getMessage());
		}catch (Exception e) {
			log.error("刷新履约单下运单发货状态失败！",e);
			rest.toError("刷新履约单下运单发货状态失败，服务异常！");
		}
		return rest;
	}

	/**
	 * 获取履约单下所有运单数据
	 * @param waybillCode 运单号
	 * @return
	 */
	@Authorization(Constants.DMS_WEB_EXPRESS_STORAGEPACKAGEM_R)
	@RequestMapping(value = "/queryWaybills/{waybillCode}")
	public @ResponseBody JdResponse<List<FulfillmentOrderDto>> queryWaybills(@PathVariable("waybillCode") String waybillCode) {
		JdResponse<List<FulfillmentOrderDto>> result = new JdResponse<List<FulfillmentOrderDto>>();
		List<FulfillmentOrderDto> fulfillmentOrderDtos = new ArrayList<>();
		result.setData(fulfillmentOrderDtos);
		try {

			BaseEntity<BigWaybillDto> baseEntity = waybillQueryManager.getDataByChoice(waybillCode, true,true, true,false);
			if(baseEntity == null || baseEntity.getData() == null || baseEntity.getData().getWaybill() == null){
				result.setCode(JdResponse.CODE_ERROR);
				result.setMessage("无运单信息");
				return result;
			}

			if(!BusinessUtil.isPerformanceOrder(baseEntity.getData().getWaybill().getWaybillSign())){
				result.setCode(JdResponse.CODE_ERROR);
				result.setMessage("非加履中心订单");
				return result;
			}

			String	parentOrderId = baseEntity.getData().getWaybill().getParentOrderId();

			List<String> childWaybillCodes = waybillQueryManager.getOrderParentChildList(parentOrderId);
			for(String childWaybillCode : childWaybillCodes){
				FulfillmentOrderDto f = new FulfillmentOrderDto();
				f.setDeliveryOrderId(childWaybillCode);
				f.setFulfillmentOrderId(parentOrderId);
				fulfillmentOrderDtos.add(f);
			}

		}catch (Exception e) {
			log.error("获取履约单下所有运单数据异常！",e);
			result.toError("获取履约单下所有运单数据异常，服务异常！");
		}
		return result;
	}

    /**
     * 导出
     * @return
     */
    @Authorization(Constants.DMS_WEB_EXPRESS_STORAGEPACKAGEM_R)
    @RequestMapping(value = "/toExport", method = RequestMethod.POST)
	@ResponseBody
	@JProfiler(jKey = "com.jd.bluedragon.distribution.storage.controller.StoragePackageMController.toExport", jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP})
    public InvokeResult toExport(StoragePackageMCondition condition, HttpServletResponse response) {
		InvokeResult result = new InvokeResult();
		BufferedWriter bfw = null;
        this.log.info("暂存管理记录统计表...");
        try{
        	exportConcurrencyLimitService.incrKey(ExportConcurrencyLimitEnum.STORAGE_PACKAGE_M_REPORT.getCode());

			String fileName = "暂存记录统计表";
			//设置文件后缀
			String fn = fileName.concat(DateHelper.formatDate(new Date(),DateHelper.DATE_FORMAT_YYYYMMDDHHmmssSSS) + ".csv");
			bfw = new BufferedWriter(new OutputStreamWriter(response.getOutputStream(), "GBK"));
			//设置响应
			CsvExporterUtils.setResponseHeader(response, fn);
            storagePackageMService.getExportData(condition,bfw);
			exportConcurrencyLimitService.decrKey(ExportConcurrencyLimitEnum.STORAGE_PACKAGE_M_REPORT.getCode());
        }catch (Exception e){
            this.log.error("导出暂存记录统计表失败:", e);
			result.customMessage(InvokeResult.SERVER_ERROR_CODE,InvokeResult.RESULT_EXPORT_MESSAGE);
        }finally {
			try {
				if (bfw != null) {
					bfw.flush();
					bfw.close();
				}
			} catch (IOException es) {
				log.error("暂存管理记录统计表 流关闭异常", es);
				result.customMessage(InvokeResult.SERVER_ERROR_CODE,InvokeResult.RESULT_EXPORT_MESSAGE+"流关闭异常");
			}
		}
        return result;
    }

    /**
     * 获取分拣中心储位状态
     * @param siteCode 运单号
     * @return
     */
    @Authorization(Constants.DMS_WEB_EXPRESS_STORAGEPACKAGEM_R)
    @RequestMapping(value = "/getStorageStatusBySiteCode/{siteCode}")
    public @ResponseBody JdResponse<Boolean> getStorageStatusBySiteCode(@PathVariable("siteCode") Integer siteCode) {
        JdResponse<Boolean> rest = new JdResponse<Boolean>();
        rest.setData(storagePackageMService.getStorageStatusBySiteCode(siteCode));
        return rest;
    }

    /**
     * 获取分拣中心储位状态
     * @param siteCode 运单号
     * @return
     */
    @Authorization(Constants.DMS_WEB_EXPRESS_STORAGEPACKAGEM_R)
    @RequestMapping(value = "/updateStorageStatusBySiteCode/{siteCode}/{isEnough}")
    public @ResponseBody JdResponse<Boolean> updateStorageStatusBySiteCode(@PathVariable("siteCode") Integer siteCode,
                                                                           @PathVariable("isEnough") Integer isEnough) {
        JdResponse<Boolean> rest = new JdResponse<Boolean>();
        if(siteCode == null || isEnough == null){
            rest.toFail(InvokeResult.PARAM_ERROR);
            return rest;
        }
        String userErp = ErpUserClient.getCurrUser()==null?null:ErpUserClient.getCurrUser().getUserCode();
        rest.setData(storagePackageMService.updateStorageStatusBySiteCode(siteCode,isEnough,userErp));
        return rest;
    }
}
