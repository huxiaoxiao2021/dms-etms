package com.jd.bluedragon.distribution.print.waybill.handler;

import java.util.Arrays;
import java.util.List;

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
     * 三方超限拦截开关-默认为true
     */
    private boolean open = true;
    
	@Override
	public InterceptResult<String> handle(WaybillPrintContext context) {
		InterceptResult<String> result = context.getResult();
		//校验是否打开
			//获取预分拣站点，校验是三方站点才走拦截
			BaseStaffSiteOrgDto prepareSiteInfo = getPrepareSiteInfo(context);
			if(open && BusinessHelper.isThirdSite(prepareSiteInfo)){
				String barCode = context.getRequest().getBarCode();
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
				if (baseSiteGoods != null) {
	                if (NumberHelper.gt(weight, baseSiteGoods.getGoodsWeight())) {
	                	result.toFail(WaybillPrintMessages.FAIL_MESSAGE_THIRD_OVERRUN.getMsgCode(), WaybillPrintMessages.FAIL_MESSAGE_THIRD_OVERRUN.formatMsg("重量"));
	                }else if (NumberHelper.gt(volume,baseSiteGoods.getGoodsVolume())) {
	                	result.toFail(WaybillPrintMessages.FAIL_MESSAGE_THIRD_OVERRUN.getMsgCode(), WaybillPrintMessages.FAIL_MESSAGE_THIRD_OVERRUN.formatMsg("体积"));
	                }else if (NumberHelper.gt(volumes[0],baseSiteGoods.getGoodsLength())) {
	                	result.toFail(WaybillPrintMessages.FAIL_MESSAGE_THIRD_OVERRUN.getMsgCode(), WaybillPrintMessages.FAIL_MESSAGE_THIRD_OVERRUN.formatMsg("长度"));
	                }else if (NumberHelper.gt(volumes[1],baseSiteGoods.getGoodsWidth())) {
	                	result.toFail(WaybillPrintMessages.FAIL_MESSAGE_THIRD_OVERRUN.getMsgCode(), WaybillPrintMessages.FAIL_MESSAGE_THIRD_OVERRUN.formatMsg("宽度"));
	                }else if (NumberHelper.gt(volumes[2],baseSiteGoods.getGoodsHeight())) {
	                	result.toFail(WaybillPrintMessages.FAIL_MESSAGE_THIRD_OVERRUN.getMsgCode(), WaybillPrintMessages.FAIL_MESSAGE_THIRD_OVERRUN.formatMsg("高度"));
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
		Integer prepareSiteCode = context.getResponse().getPrepareSiteCode();
		//站点平台打印/批量分拣称重
		if(WaybillPrintOperateTypeEnum.SITE_PLATE_PRINT_TYPE.equals(context.getRequest().getOperateType())
				||WaybillPrintOperateTypeEnum.BATCH_SORT_WEIGH_PRINT_TYPE.equals(context.getRequest().getOperateType())){
			prepareSiteCode = context.getWaybill().getSiteCode();
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
	/**
	 * @return the open
	 */
	public boolean isOpen() {
		return open;
	}
	/**
	 * @param open the open to set
	 */
	public void setOpen(boolean open) {
		this.open = open;
	}
}
