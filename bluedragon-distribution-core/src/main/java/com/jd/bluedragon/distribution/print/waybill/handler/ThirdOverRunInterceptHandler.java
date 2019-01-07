package com.jd.bluedragon.distribution.print.waybill.handler;

import java.util.Arrays;
import java.util.List;

import com.jd.bluedragon.distribution.api.request.WaybillPrintRequest;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.core.base.BaseMinorManager;
import com.jd.bluedragon.core.base.WaybillQueryManager;
import com.jd.bluedragon.distribution.api.domain.WeightOperFlow;
import com.jd.bluedragon.distribution.base.service.BaseService;
import com.jd.bluedragon.distribution.base.service.SiteService;
import com.jd.bluedragon.distribution.handler.InterceptHandler;
import com.jd.bluedragon.distribution.handler.InterceptResult;
import com.jd.bluedragon.distribution.print.domain.WaybillPrintOperateTypeEnum;
import com.jd.bluedragon.utils.BusinessHelper;
import com.jd.bluedragon.utils.NumberHelper;
import com.jd.etms.waybill.domain.DeliveryPackageD;
import com.jd.etms.waybill.dto.BigWaybillDto;
import com.jd.ql.basic.domain.BaseSiteGoods;
import com.jd.ql.basic.dto.BaseStaffSiteOrgDto;
/**
 * @ClassName: ThirdOverRunInterceptHandler
 * @Description: 三方运力超限验证
 * @author: wuyoude
 * @date: 2018年2月6日 下午2:08:38
 */
@Service
public class ThirdOverRunInterceptHandler implements InterceptHandler<WaybillPrintContext,String>{
	private static final Log logger= LogFactory.getLog(ThirdOverRunInterceptHandler.class);
    @Autowired
    private BaseMinorManager baseMinorManager;
    @Autowired
    private WaybillQueryManager waybillQueryManager;
    @Autowired
    private BaseService baseService;
    @Autowired
    private SiteService siteService;
	/**
	 * 信任商家重量误差值
	 * */
	private Double diffWeight;
	/**
	 * 信任商家体积误差值
	 * */
	private Double diffVolume;

	@Override
	public InterceptResult<String> handle(WaybillPrintContext context) {
		InterceptResult<String> result = context.getResult();
		//校验是否打开
		WeightOperFlow weightOperFlow = context.getRequest().getWeightOperFlow();
		double weight = 0d;
		double volume = 0d;
		double[] volumes = new double[3];
		if(weightOperFlow != null){
			if(weightOperFlow.getWeight()>0){
				weight = weightOperFlow.getWeight();
			}
			if(weightOperFlow.getVolume()>0){
				volume = weightOperFlow.getVolume();
			}
			if(weightOperFlow.getLength()>0){
				volumes[0] = weightOperFlow.getLength();
			}
			if(weightOperFlow.getWidth()>0){
				volumes[1] = weightOperFlow.getWidth();
			}
			if(weightOperFlow.getHigh()>0){
				volumes[2] = weightOperFlow.getHigh();
			}
		}
		double pWeight = weight;
		double pVolume = volumes[0] * volumes[1] * volumes[2];
		com.jd.etms.waybill.domain.Waybill waybill = context.getBigWaybillDto().getWaybill();
		//信任商家判断逻辑
		if(waybill !=null && context.getWaybill().getPackageNum() == 1 && context.getRequest().getTrustBusinessFlag()){
			double waybillWeight = 0;
			double waybillVolume = 0;
			if(waybill.getGoodWeight() != null){
				waybillWeight = waybill.getGoodWeight().doubleValue();
			}
			if(waybill.getGoodVolume() != null){
				waybillVolume = Double.valueOf(waybill.getGoodVolume());
			}

			String message = "";
			boolean flage = true;
			if((waybillWeight - pWeight) <= -diffWeight || (waybillWeight - pWeight) >= diffWeight){
				message = "实际称重重量与商家重量相比，误差值超过0.5公斤\r\n";
				flage = false;
			}
			if((waybillVolume - pVolume) <= -diffVolume || (waybillVolume - pVolume) >= diffVolume ){
				message = message + "实际测量体积与商家体积对比，误差值超过0.01立方米\r\n";
				flage = false;
			}
			if(!flage){
				String[] splitMessage = WaybillPrintMessages.WARN_MESSAGE_TRUST_BUSINESS.getMsgFormat().split("-");
				result.toWeakSuccess(WaybillPrintMessages.WARN_MESSAGE_TRUST_BUSINESS.getMsgCode(), String.format(splitMessage[1],message));
			}
		}
			//获取预分拣站点，校验是三方站点才走拦截
			BaseStaffSiteOrgDto prepareSiteInfo = getPrepareSiteInfo(context);
			if(BusinessHelper.isThirdSite(prepareSiteInfo)){
				String barCode = context.getRequest().getBarCode();

				//先校验是否上传了重量，用上传重量和目的站点取校验是否超限，否则获取运单的重量和体积
				BigWaybillDto waybillDto = context.getBigWaybillDto();
				//校验并从运单获取重量级体积信息
				if(waybillDto != null){
					//重量为0，先取AgainWeight然后取GoodsWeight
					if(weight == 0){
						weight = getMaxAgainWeight(waybillDto.getPackageList(),barCode);
						if(weight == 0){
							weight = getMaxGoodsWeight(waybillDto.getPackageList(),barCode);
						}
					}
					//上传体积为0并且上传长宽高为0，取运单体积
					if(volume == 0 && (volumes[0] + volumes[1] + volumes[2])==0){
						double[] newVolumes = BusinessHelper.convertVolumeFormula(waybillDto.getWaybill().getVolumeFormula());
						if(newVolumes != null){
							volumes = newVolumes;
						}
					}
					double newVolume = volumes[0] * volumes[1] * volumes[2];
					if(volume == 0 && newVolume>0){
						volume = newVolume;
					}
					Arrays.sort(volumes);
				}
				//查询三方站点超限标准
				BaseSiteGoods baseSiteGoods = baseMinorManager.getGoodsVolumeLimitBySiteCode(prepareSiteInfo.getSiteCode());

				//组织三方超限提示语
				String overRunUnit = "";
				String overRunMessage = "";
				if (baseSiteGoods != null) {
	                if (NumberHelper.gt(baseSiteGoods.getGoodsWeight(), Constants.DOUBLE_ZERO)
	                		&&NumberHelper.gt(weight, baseSiteGoods.getGoodsWeight())) {
	                	overRunUnit += "重量、";
						overRunMessage += "重量不得大于" + baseSiteGoods.getGoodsWeight() + "公斤、";
	                }
	                if (NumberHelper.gt(baseSiteGoods.getGoodsVolume(), Constants.DOUBLE_ZERO)
	                		&&NumberHelper.gt(volume,baseSiteGoods.getGoodsVolume())) {
	                	//为给用户比较好的体验，将立方厘米转换成立方米
						overRunUnit += "重量、";
						overRunMessage += "体积不得大于" + NumberHelper.doubleFormat(baseSiteGoods.getGoodsVolume()) + "立方米、";
	                }
	                if (NumberHelper.gt(baseSiteGoods.getGoodsLength(), Constants.DOUBLE_ZERO)
	                		&&NumberHelper.gt(volumes[2],baseSiteGoods.getGoodsLength())) {
						overRunUnit += "长度、";
						overRunMessage += "长度不得大于" + baseSiteGoods.getGoodsLength() + "厘米、";
	                }
	                if (NumberHelper.gt(baseSiteGoods.getGoodsWidth(), Constants.DOUBLE_ZERO)
	                		&&NumberHelper.gt(volumes[1],baseSiteGoods.getGoodsWidth())) {
						overRunUnit += "宽度、";
						overRunMessage += "宽度不得大于" + baseSiteGoods.getGoodsWidth() + "厘米、";
	                }
	                if (NumberHelper.gt(baseSiteGoods.getGoodsHeight(), Constants.DOUBLE_ZERO)
	                		&&NumberHelper.gt(volumes[0],baseSiteGoods.getGoodsHeight())) {
						overRunUnit += "高度、";
						overRunMessage += "高度不得大于" + baseSiteGoods.getGoodsHeight() + "厘米、";
	                }
				}

				if(StringUtils.isNotBlank(overRunUnit) && overRunUnit.length() > 1){
					overRunUnit = overRunUnit.substring(0, overRunUnit.length() -1);
				}
				if(StringUtils.isNotBlank(overRunMessage) && overRunMessage.length() > 1){
					overRunMessage = overRunMessage.substring(0,overRunMessage.length() - 1 );
				}

				WaybillPrintRequest request = context.getRequest();
				if(StringUtils.isNotBlank(overRunMessage)) {
					//平台打印和站点平台打印提示语个性设置
					if (WaybillPrintOperateTypeEnum.PLATE_PRINT_TYPE.equals(request.getOperateType())) {
						result.toFail(WaybillPrintMessages.FAIL_MESSAGE_THIRD_OVERRUN.getMsgCode(),
								WaybillPrintMessages.FAIL_MESSAGE_THIRD_OVERRUN.formatMsg(overRunUnit,
										WaybillPrintMessages.FAIL_MESSAGE_THIRD_OVERRUN_PLATE_PRINT,
										overRunMessage));
					} else if (WaybillPrintOperateTypeEnum.SITE_PLATE_PRINT.equals(request.getOperateType())) {
						result.toFail(WaybillPrintMessages.FAIL_MESSAGE_THIRD_OVERRUN.getMsgCode(),
								WaybillPrintMessages.FAIL_MESSAGE_THIRD_OVERRUN.formatMsg(overRunUnit,
										WaybillPrintMessages.FAIL_MESSAGE_THIRD_OVERRUN_SITE_PLATE_PRINT,
										overRunMessage));
					}
				}
			}
		return result;
	}
	/**
	 * 获取预分拣站点信息
	 * @param context
	 * @return
	 */
	private BaseStaffSiteOrgDto getPrepareSiteInfo(WaybillPrintContext context){
		Integer prepareSiteCode = null;
		//站点平台打印/批量分拣称重
		if(WaybillPrintOperateTypeEnum.SITE_PLATE_PRINT_TYPE.equals(context.getRequest().getOperateType())
				||WaybillPrintOperateTypeEnum.BATCH_SORT_WEIGH_PRINT_TYPE.equals(context.getRequest().getOperateType())){
			prepareSiteCode = context.getWaybill().getSiteCode();
		}else{
			prepareSiteCode = context.getResponse().getPrepareSiteCode();
		}
		BaseStaffSiteOrgDto prepareSiteInfo= baseService.getSiteBySiteID(prepareSiteCode);
        return prepareSiteInfo;
	}
	/**
	 * 获取包裹重量-传入包裹号则获取当前包裹重量，运单号-获取包裹中复量的最大值
	 * @param packageList 包裹列表
	 * @param barCode 扫描编码
	 * @return
	 */
	private double getMaxAgainWeight(List<DeliveryPackageD> packageList,String barCode){
		double maxWeight = 0d;
		if(packageList != null && packageList.size() > 0){
			for(DeliveryPackageD packageInfo:packageList){
				if(packageInfo.getAgainWeight()!=null
							&& packageInfo.getAgainWeight().doubleValue() > maxWeight){
					maxWeight = packageInfo.getAgainWeight().doubleValue();
				}
			}
		}
        return maxWeight;
	}
	/**
	 * 获取包裹重量-传入包裹号则获取当前包裹重量，运单号-获取包裹中复量的最大值
	 * @param packageList 包裹列表
	 * @param barCode 扫描编码
	 * @return
	 */
	private Double getMaxGoodsWeight(List<DeliveryPackageD> packageList,String barCode){
		double maxWeight = 0d;
		if(packageList != null && packageList.size() > 0){
			for(DeliveryPackageD packageInfo:packageList){
				if(packageInfo.getGoodWeight()!=null
						&& packageInfo.getGoodWeight().doubleValue() > maxWeight){
					maxWeight = packageInfo.getGoodWeight().doubleValue();
				}
			}
		}
        return maxWeight;
	}
	public Double getDiffWeight() {
		return diffWeight;
	}

	public void setDiffWeight(Double diffWeight) {
		this.diffWeight = diffWeight;
	}

	public Double getDiffVolume() {
		return diffVolume;
	}

	public void setDiffVolume(Double diffVolume) {
		this.diffVolume = diffVolume;
	}
}
