package com.jd.bluedragon.distribution.half.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.core.base.BaseMajorManager;
import com.jd.bluedragon.distribution.half.domain.*;
import com.jd.bluedragon.distribution.web.ErpUserClient;
import com.jd.ql.basic.dto.BaseStaffSiteOrgDto;
import com.jd.uim.annotation.Authorization;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

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

	private static final Logger log = LoggerFactory.getLogger(PackageHalfController.class);

	@Autowired
	private PackageHalfService packageHalfService;

	@Autowired
	BaseMajorManager baseMajorManager;

	/**
	 * 返回主页面
	 * @return
	 */
	@Authorization(Constants.DMS_WEB_EXPRESS_PACKAGEHALF_R)
	@RequestMapping(value = "/toIndex")
	public String toIndex() {
		return "/half/packageHalf";
	}
	/**
	 * 根据id获取基本信息
	 * @param id
	 * @return
	 */
	@Authorization(Constants.DMS_WEB_EXPRESS_PACKAGEHALF_R)
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
	@Authorization(Constants.DMS_WEB_EXPRESS_PACKAGEHALF_R)
	@RequestMapping(value = "/save")
	public @ResponseBody JdResponse<Boolean> save(@RequestBody PackageHalfVO packageHalfVO) {
		JdResponse<Boolean> rest = new JdResponse<Boolean>();
		rest.setCode(JdResponse.CODE_SUCCESS);
		try {
			//获取基础信息
			ErpUserClient.ErpUser erpUser = ErpUserClient.getCurrUser();
			String userCode = "";
			Integer createSiteCode = new Integer(-1);
			String createSiteName = "";
			Integer orgId  = new Integer(-1);
			Integer OperatorId = new Integer(-1);
			if(erpUser!=null){
				userCode = erpUser.getUserCode();
				BaseStaffSiteOrgDto bssod = baseMajorManager.getBaseStaffByErpNoCache(userCode);
				if (bssod!=null && bssod.getSiteType() == 64) {/** 站点类型为64的时候为分拣中心 **/
					createSiteCode = bssod.getSiteCode();
					createSiteName = bssod.getSiteName();
					orgId = bssod.getOrgId();
					OperatorId = bssod.getStaffNo();
				}else{
					//当前登录人无所属分拣中心  不允许操作  外单接口必须
					rest.toError("保存失败！当前登录人无所属分拣中心，不可操作！");
					return rest;
				}
			}

			//组装插入对象
			PackageHalf packageHalf = new PackageHalf();
			List<PackageHalfDetail> packageHalfDetails = new ArrayList<PackageHalfDetail>();
			makeSavePOJO(packageHalfVO,packageHalf,packageHalfDetails,userCode,createSiteCode,createSiteName);
			Integer waybillOpeType = packageHalfVO.getWaybillOpeType();
			String OperatorName = erpUser==null?"":erpUser.getUserName();
			Date operateTime = packageHalfVO.getOperateTime();

			//具体业务处理
			boolean saveResult = packageHalfService.save(packageHalf,packageHalfDetails,waybillOpeType,OperatorId,OperatorName,operateTime,packageHalfVO.getRejectPackageCount(),orgId,createSiteCode,rest);
			rest.setData(saveResult);
			if(!saveResult){
				//保存失败
				log.warn("half/packageHalf/save  fail to save！：{}", rest.getMessage());
				//packageHalfService.deleteOfSaveFail(packageHalfVO.getWaybillCode());
				rest.toError("保存失败！失败原因:"+rest.getMessage());
			}


		} catch (Exception e) {
			log.error("half/packageHalf/save  fail to save！",e);
			//packageHalfService.deleteOfSaveFail(packageHalfVO.getWaybillCode());
			rest.toError("保存失败，服务异常！");
		}
		return rest;
	}

	private void makeSavePOJO(PackageHalfVO packageHalfVO, PackageHalf packageHalf , List<PackageHalfDetail> packageHalfDetails,String userCode,Integer createSiteCode, String createSiteName){

		packageHalf.setWaybillCode(packageHalfVO.getWaybillCode());
        packageHalf.setCreateUser(userCode);
        packageHalf.setHalfType(Integer.valueOf(packageHalfVO.getHalfType()));
        packageHalf.setOperateSiteCode(Long.valueOf(createSiteCode));
        packageHalf.setOperateSiteName(createSiteName);


        List<PackageHalfDetailVO> PackageHalfDetailVOs = packageHalfVO.getPackageList();
        if(PackageHalfDetailVOs != null){
			for(PackageHalfDetailVO packageHalfDetailVO : PackageHalfDetailVOs){
				PackageHalfDetail packageHalfDetail = new PackageHalfDetail();
				packageHalfDetail.setWaybillCode(packageHalfVO.getWaybillCode());
				packageHalfDetail.setPackageCode(packageHalfDetailVO.getPackageCode());
				packageHalfDetail.setReasonType(Integer.valueOf(StringUtils.isBlank(packageHalfDetailVO.getReasonType())?"-1":packageHalfDetailVO.getReasonType()));
				packageHalfDetail.setResultType(Integer.valueOf(packageHalfDetailVO.getResultType()));
				packageHalfDetail.setCreateUser(userCode);
				packageHalfDetail.setOperateSiteCode(Long.valueOf(createSiteCode));
				packageHalfDetail.setOperateSiteName(createSiteName);
				packageHalfDetail.setHalfType(Integer.valueOf(packageHalfVO.getHalfType()));

				packageHalfDetails.add(packageHalfDetail);
			}
		}


	}

	/**
	 * 根据id删除多条数据
	 * @param ids
	 * @return
	 */
	@Authorization(Constants.DMS_WEB_EXPRESS_PACKAGEHALF_R)
	@RequestMapping(value = "/deleteByIds")
	public @ResponseBody JdResponse<Integer> deleteByIds(@RequestBody List<Long> ids) {
		JdResponse<Integer> rest = new JdResponse<Integer>();
		try {
			rest.setData(packageHalfService.deleteByIds(ids));
		} catch (Exception e) {
			log.error("fail to delete！",e);
			rest.toError("删除失败，服务异常！");
		}
		return rest;
	}
	/**
	 * 根据条件分页查询数据信息
	 * @param packageHalfCondition
	 * @return
	 */
	@Authorization(Constants.DMS_WEB_EXPRESS_PACKAGEHALF_R)
	@RequestMapping(value = "/listData")
	public @ResponseBody PagerResult<PackageHalf> listData(@RequestBody PackageHalfCondition packageHalfCondition) {
		JdResponse<PagerResult<PackageHalf>> rest = new JdResponse<PagerResult<PackageHalf>>();
		rest.setData(packageHalfService.queryByPagerCondition(packageHalfCondition));
		return rest.getData();
	}
}
