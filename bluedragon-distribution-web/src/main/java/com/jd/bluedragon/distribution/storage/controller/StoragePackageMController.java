package com.jd.bluedragon.distribution.storage.controller;

import java.util.List;

import com.jd.bluedragon.core.base.BaseMajorManager;
import com.jd.bluedragon.distribution.storage.domain.PutawayDTO;
import com.jd.bluedragon.distribution.web.ErpUserClient;
import com.jd.ql.basic.dto.BaseStaffSiteOrgDto;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.jd.bluedragon.distribution.storage.domain.StoragePackageM;
import com.jd.bluedragon.distribution.storage.domain.StoragePackageMCondition;
import com.jd.bluedragon.distribution.storage.service.StoragePackageMService;
import com.jd.ql.dms.common.domain.JdResponse;
import com.jd.ql.dms.common.web.mvc.api.PagerResult;

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
public class StoragePackageMController {

	private static final Log logger = LogFactory.getLog(StoragePackageMController.class);

	@Autowired
	private BaseMajorManager baseMajorManager;

	@Autowired
	private StoragePackageMService storagePackageMService;

	/**
	 * 返回主页面
	 * @return
	 */
	@RequestMapping(value = "/toIndex")
	public String toIndex() {
		return "/storage/storagePackageM";
	}
	/**
	 * 根据id获取基本信息
	 * @param id
	 * @return
	 */
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
	@RequestMapping(value = "/save")
	public @ResponseBody JdResponse<Boolean> save(@RequestBody StoragePackageM storagePackageM) {
		JdResponse<Boolean> rest = new JdResponse<Boolean>();
		try {
			rest.setData(storagePackageMService.saveOrUpdate(storagePackageM));
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
			rest.setData(storagePackageMService.deleteByIds(ids));
		} catch (Exception e) {
			logger.error("fail to delete！"+e.getMessage(),e);
			rest.toError("删除失败，服务异常！");
		}
		return rest;
	}
	/**
	 * 根据条件分页查询数据信息
	 * @param storagePackageMCondition
	 * @return
	 */
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
	 * @param ids
	 * @return
	 */
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
			logger.error("fail to forceSend！"+e.getMessage(),e);
			rest.toError("强制发货失败，服务异常！");
		}
		return rest;
	}
}
