package com.jd.bluedragon.distribution.half.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.core.base.WaybillQueryManager;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.half.domain.*;
import com.jd.bluedragon.distribution.waybill.domain.WaybillStatus;
import com.jd.bluedragon.dms.utils.BusinessUtil;
import com.jd.bluedragon.utils.BusinessHelper;
import com.jd.etms.waybill.domain.BaseEntity;
import com.jd.etms.waybill.domain.DeliveryPackageD;
import com.jd.etms.waybill.domain.PackageWeigh;
import com.jd.etms.waybill.domain.WaybillOpe;
import com.jd.etms.waybill.dto.BigWaybillDto;
import com.jd.uim.annotation.Authorization;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.jd.bluedragon.distribution.half.service.PackageHalfDetailService;
import com.jd.ql.dms.common.domain.JdResponse;
import com.jd.ql.dms.common.web.mvc.api.PagerResult;

/**
 *
 * @ClassName: PackageHalfDetailController
 * @Description: 包裹半收操作明细表--Controller实现
 * @author wuyoude
 * @date 2018年03月20日 17:33:21
 *
 */
@Controller
@RequestMapping("half/packageHalfDetail")
public class PackageHalfDetailController {

	private static final Log logger = LogFactory.getLog(PackageHalfDetailController.class);

	@Autowired
	private PackageHalfDetailService packageHalfDetailService;

	@Autowired
	private WaybillQueryManager waybillQueryManager;

	/**
	 * 返回主页面
	 * @return
	 */
	@Authorization(Constants.DMS_WEB_EXPRESS_PACKAGEHALF_R)
	@RequestMapping(value = "/toIndex")
	public String toIndex() {
		return "/half/packageHalfDetail";
	}
	/**
	 * 根据id获取基本信息
	 * @param id
	 * @return
	 */
    @Authorization(Constants.DMS_WEB_EXPRESS_PACKAGEHALF_R)
	@RequestMapping(value = "/detail/{id}")
	public @ResponseBody JdResponse<PackageHalfDetail> detail(@PathVariable("id") Long id) {
		JdResponse<PackageHalfDetail> rest = new JdResponse<PackageHalfDetail>();
		rest.setData(packageHalfDetailService.findById(id));
		return rest;
	}
	/**
	 * 保存数据
	 * @param packageHalfDetail
	 * @return
	 */
    @Authorization(Constants.DMS_WEB_EXPRESS_PACKAGEHALF_R)
	@RequestMapping(value = "/save")
	public @ResponseBody JdResponse<Boolean> save(@RequestBody PackageHalfDetail packageHalfDetail) {
		JdResponse<Boolean> rest = new JdResponse<Boolean>();
		try {
			rest.setData(packageHalfDetailService.saveOrUpdate(packageHalfDetail));
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
    @Authorization(Constants.DMS_WEB_EXPRESS_PACKAGEHALF_R)
	@RequestMapping(value = "/deleteByIds")
	public @ResponseBody JdResponse<Integer> deleteByIds(@RequestBody List<Long> ids) {
		JdResponse<Integer> rest = new JdResponse<Integer>();
		try {
			rest.setData(packageHalfDetailService.deleteByIds(ids));
		} catch (Exception e) {
			logger.error("fail to delete！"+e.getMessage(),e);
			rest.toError("删除失败，服务异常！");
		}
		return rest;
	}
	/**
	 * 根据条件分页查询数据信息
	 * @param packageHalfDetailCondition
	 * @return
	 */
    @Authorization(Constants.DMS_WEB_EXPRESS_PACKAGEHALF_R)
	@RequestMapping(value = "/listData")
	public @ResponseBody PagerResult<PackageHalfDetail> listData(@RequestBody PackageHalfDetailCondition packageHalfDetailCondition) {
		JdResponse<PagerResult<PackageHalfDetail>> rest = new JdResponse<PagerResult<PackageHalfDetail>>();
		rest.setData(packageHalfDetailService.queryByPagerCondition(packageHalfDetailCondition));
		return rest.getData();
	}


	/**
	 *1、COD和运费到付的不允许在分拣操作
	 2、分拣判断运单标志27位2，5位3，审核完成后，只允许操作拒收或者报废（提示如果需要操作妥投请在一体机上操作）
	    ；如果还未发起协商再投，或者审核中，不允许操作且提示（提示如果需要操作包裹协商再投请在一体机上操作）
	  	PS 对于支持协商再投的运单（27位2，5位3），审核完成是前提
	 * @param waybillCode
	 * @return
	 */
    @Authorization(Constants.DMS_WEB_EXPRESS_PACKAGEHALF_R)
	@RequestMapping(value = "/getPackageStatus")
	public @ResponseBody InvokeResult<PackageHalfDetailResponseVO> getPackageStatus(String waybillCode) {
		InvokeResult<PackageHalfDetailResponseVO>  result = new InvokeResult<PackageHalfDetailResponseVO>();
		PackageHalfDetailResponseVO packageHalfDetailResponseVO = new PackageHalfDetailResponseVO();
		String resultMessageTemp = "";
		try {

			result.setCode(InvokeResult.RESULT_SUCCESS_CODE);
			packageHalfDetailResponseVO.setCanDelievered(true);
			packageHalfDetailResponseVO.setCanReject(true);
			List<PackageHalfDetail> packageHalfDetailsOfResult = new ArrayList<PackageHalfDetail>();
			packageHalfDetailResponseVO.setPackageList(packageHalfDetailsOfResult);
			result.setData(packageHalfDetailResponseVO);

			BaseEntity<BigWaybillDto> getDataByChoiceResult = waybillQueryManager.getDataByChoice(waybillCode, true, true, true, false, true, false, false);
			if (getDataByChoiceResult.getResultCode() == 1) {
				//成功获取包裹信息
				BigWaybillDto bigWaybillDto = getDataByChoiceResult.getData();
				if (bigWaybillDto == null || bigWaybillDto.getWaybill() == null) {
					result.setCode(InvokeResult.SERVER_ERROR_CODE);
					result.setMessage("无此运单");
					return result;
				}
				//判断包裹信息是否为半收包裹
				if (BusinessUtil.isPackageHalf(bigWaybillDto.getWaybill().getWaybillSign())) {
					//是半收包裹
					//判断是否是COD 或者 运费到付
					if(BusinessHelper.isCODOrFreightCollect(bigWaybillDto) ) {
						//不允许操作
						packageHalfDetailResponseVO.setCanDelievered(false);
						packageHalfDetailResponseVO.setCanReject(false);
						result.setMessage("此运单为到付运费或应收货款，请在一体机中操作！");
						return result;
					}

					//终端或分拣已经操作过
					Integer waybillState = bigWaybillDto.getWaybillState().getWaybillState();
					if(WaybillStatus.WAYBILL_TRACK_FC .equals(waybillState)|| WaybillStatus.WAYBILL_TRACK_RCD.equals(waybillState) ){ //|| WaybillStatus.WAYBILL_TRACK_PACKAGE_HALF.equals(waybillState)){
						packageHalfDetailResponseVO.setCanDelievered(false);
						packageHalfDetailResponseVO.setCanReject(false);
						resultMessageTemp = "\n此运单已完成操作！不可再次操作";
					}else{
						//支持协商在妥投的 并且 没有审核完成的
						if(BusinessUtil.isConsultationTo(bigWaybillDto.getWaybill().getWaybillSign())) {
							//不允许操作
							if(!WaybillStatus.WAYBILL_STATUS_CONSULT.equals(bigWaybillDto.getWaybillState().getWaybillState())
                                    && !WaybillStatus.WAYBILL_TRACK_PACKAGE_HALF.equals(bigWaybillDto.getWaybillState().getWaybillState())){
								packageHalfDetailResponseVO.setCanDelievered(false);
								packageHalfDetailResponseVO.setCanReject(false);
								result.setMessage("此运单未完成协商再投审核，请在一体机中操作！");
								return result;
							}else{
								//只允许操作拒收
								packageHalfDetailResponseVO.setCanDelievered(false);
								packageHalfDetailResponseVO.setCanReject(true);
								resultMessageTemp = "\n此运单支持协商再投,分拣中心只允许操作拒收";
							}

						}

					}

					//获取操作记录合并
					List<PackageHalfDetail> packageHalfDetails = packageHalfDetailService.getPackageHalfDetailByWaybillCode(waybillCode);


					if (bigWaybillDto != null && bigWaybillDto.getPackageList() != null && bigWaybillDto.getPackageList().size() > 0) {
						Map<String, PackageHalfDetail> packageResultType = new HashMap<String, PackageHalfDetail>();
						//组装 已有操作记录的 MAP
						if (packageHalfDetails.size() > 0) {
							for (PackageHalfDetail packageHalfDetail : packageHalfDetails) {
								packageResultType.put(packageHalfDetail.getPackageCode(), packageHalfDetail);
							}


						}
						//循环处理 运单获取的包裹信息
						for (DeliveryPackageD deliveryPackageD : bigWaybillDto.getPackageList()) {
							String packageBarcode = deliveryPackageD.getPackageBarcode();
							PackageHalfDetail packageHalfDetail = packageResultType.get(packageBarcode);
							if (packageHalfDetail != null) {
								packageHalfDetailsOfResult.add(packageHalfDetail);
							} else {
								PackageHalfDetail packageHalfDetailNew = new PackageHalfDetail();
								packageHalfDetailNew.setPackageCode(deliveryPackageD.getPackageBarcode());
								packageHalfDetailNew.setWaybillCode(deliveryPackageD.getWaybillCode());
								if (WaybillStatus.WAYBILL_TRACK_FC.equals(deliveryPackageD.getPackageState())) {
									//妥投
									packageHalfDetailNew.setResultType(Integer.valueOf(PackageHalfResultTypeEnum.DELIVERED_1.getCode()));
								} else if (WaybillStatus.WAYBILL_TRACK_RCD.equals(deliveryPackageD.getPackageState())) {
									//拒收
									packageHalfDetailNew.setResultType(Integer.valueOf(PackageHalfResultTypeEnum.REJECT_2.getCode()));
								}
								packageHalfDetailsOfResult.add(packageHalfDetailNew);
							}

						}
						//拼装正确提示语
						result.setMessage("此运单为包裹半收 总包裹数:" + packageHalfDetailsOfResult.size()+resultMessageTemp);

					} else {
						//无包裹信息
						result.setCode(InvokeResult.SERVER_ERROR_CODE);
						result.setMessage("此运单无包裹信息");
					}

				} else {
					//不是半收包裹
					result.setCode(InvokeResult.SERVER_ERROR_CODE);
					result.setMessage("此运单非包裹半收，不可操作！");
				}


			} else {
				result.setCode(InvokeResult.SERVER_ERROR_CODE);
				result.setMessage(getDataByChoiceResult.getMessage());
			}
			return result;

		} catch (Exception e) {
            logger.error("half/packageHalfDetail/getPackageStatus接口调用失败"+e.getMessage());
			result.setCode(InvokeResult.SERVER_ERROR_CODE);
			result.setMessage("获取运单信息失败"+e.getMessage());
			return result;
		}

	}


}
