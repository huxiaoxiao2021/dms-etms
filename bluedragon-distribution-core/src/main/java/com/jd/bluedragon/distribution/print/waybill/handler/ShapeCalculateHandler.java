package com.jd.bluedragon.distribution.print.waybill.handler;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.TextConstants;
import com.jd.bluedragon.core.base.LbccOperationSignQueryApiManager;
import com.jd.bluedragon.core.base.WaybillQueryManager;
import com.jd.bluedragon.distribution.api.domain.WeightOperFlow;
import com.jd.bluedragon.distribution.command.JdResult;
import com.jd.bluedragon.distribution.handler.Handler;
import com.jd.bluedragon.dms.utils.WaybillUtil;
import com.jd.etms.waybill.dto.BigWaybillDto;
import com.jd.lbcc.rule.api.dto.request.PackageDetailDTO;
import com.jd.lbcc.rule.api.dto.request.PackageTypeQueryDTO;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 天官赐福 ◎ 百无禁忌
 *
 * 件型计算处理器
 * @author: 刘铎（liuduo8）
 * @date: 2022/7/14
 * @description: 使用相关数据计算件型标识，目前仅支持NC标识
 *
 * https://joyspace.jd.com/pages/svygGEdev97eg4Hu3cL2
 **/
@Service("shapeCalculateHandler")
@Slf4j
public class ShapeCalculateHandler implements Handler<WaybillPrintContext,JdResult<String>>{

	/**
	 * 中台件型试算服务
	 */
	@Autowired
	@Qualifier("lbccOperationSignQueryApiManager")
	private LbccOperationSignQueryApiManager lbccOperationSignQueryApiManager;

	/**
	 * 中台运单服务
	 */
	@Autowired
	private WaybillQueryManager waybillQueryManager;

	/**
	 * 处理逻辑
	 * @param context
	 * @return
	 */
	@Override
	@JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWEB,jKey = "DMSWEB.shapeCalculateHandler.handle",mState={JProEnum.TP,JProEnum.FunctionError})
	public JdResult<String> handle(WaybillPrintContext context) {
		try {
			//试算件型标识
			Map<String, String> shapeResult = lbccOperationSignQueryApiManager.shapeCalculate(makePackageTypeQueryDTO(context));
			//获取具体件型，目前是因为运单维度和包裹维度的时候都传入一条数据进行试算，所以map的key就是barCode，如果后续改造处理维度在做key的替换
			String shape =  shapeResult.get(context.getRequest().getBarCode());
			if(TextConstants.SPECIAL_MARK_NC.equals(shape)){
				//如果目前不存在NC标识时设置NC标识
				if(context.getResponse()!=null &&
						StringUtils.isNotBlank(context.getResponse().getSpecialMark()) &&
						!context.getResponse().getSpecialMark().contains(TextConstants.SPECIAL_MARK_NC)){
					log.info("通过件型计算服务计算后需要显示NC,{}",context.getRequest().getBarCode());
					//追加NC标识
					context.getResponse().appendSpecialMark(TextConstants.SPECIAL_MARK_NC);
				}
			}
		}catch (Exception e){
			log.error("件型计算异常,运单:{},异常信息:{}",context.getRequest().getBarCode(),e.getMessage(),e);
		}
		return context.getResult();
	}

	/**
	 * 构建试算件型的入参
	 * @return
	 */
	private PackageTypeQueryDTO makePackageTypeQueryDTO(WaybillPrintContext context){
		//获取基础信息
		Boolean isWaybillDimension = WaybillUtil.isPackageCode(context.getRequest().getBarCode());
		String waybillCode = WaybillUtil.getWaybillCode(context.getRequest().getBarCode());
		BigWaybillDto waybillDto = context.getBigWaybillDto();
		WeightOperFlow weightOperFlow = context.getRequest().getWeightOperFlow();

		//获取托托寄物
		String consignmentName = StringUtils.isBlank(context.getRequest().getConsignWare()) ?
				waybillQueryManager.getConsignmentNameFromWaybillExt(waybillDto.getWaybill().getWaybillExt()):context.getRequest().getConsignWare();

		//组装入参结构体
		PackageTypeQueryDTO dto = new PackageTypeQueryDTO();
		List<PackageDetailDTO> packageDetailDtoList = new ArrayList<>();
		PackageDetailDTO detailDTO = new PackageDetailDTO();
		packageDetailDtoList.add(detailDTO);
		dto.setPackageDetailDtoList(packageDetailDtoList);

		//拼装详细入参
		dto.setWaybillCode(waybillCode);
		dto.setGoodsName(consignmentName);
		//如果传入了包裹总数就使用此包裹总数，未传入时取运单的包裹总数
		dto.setPackageCount(context.getRequest().getPackageCount() == null ?
				waybillDto.getWaybill().getGoodNumber():context.getRequest().getPackageCount());
		dto.setCustomerCode(waybillDto.getWaybill().getCustomerCode());

		if(isWaybillDimension){
			//运单维度 包裹列表只传递一条运单数据即可
			dto.setWaybillWeight(String.valueOf(weightOperFlow.getWeight()));
			detailDTO.setPackCode(waybillCode);
		}else {
			detailDTO.setPackCode(context.getRequest().getBarCode());
		}
		//称重要素
		if(weightOperFlow != null){
			detailDTO.setLength(weightOperFlow.getLength() > 0 ? BigDecimal.valueOf(weightOperFlow.getLength()) :null);
			detailDTO.setHeight(weightOperFlow.getHigh() > 0 ? BigDecimal.valueOf(weightOperFlow.getHigh()) :null);
			detailDTO.setWidth(weightOperFlow.getWidth() > 0 ? BigDecimal.valueOf(weightOperFlow.getWidth()) :null);
			detailDTO.setPackWeight(weightOperFlow.getWeight() > 0 ? String.valueOf(weightOperFlow.getWeight()) :null);
			detailDTO.setPackVolume(weightOperFlow.getVolume() > 0 ? String.valueOf(weightOperFlow.getVolume()) :null);
		}


		return dto;
	}
}
