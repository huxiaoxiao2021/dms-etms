package com.jd.bluedragon.distribution.print.waybill.handler;

import com.jd.bluedragon.core.base.BaseMajorManager;
import com.jd.bluedragon.distribution.api.JdResponse;
import com.jd.bluedragon.distribution.api.domain.WeightOperFlow;
import com.jd.bluedragon.distribution.api.request.WaybillPrintRequest;
import com.jd.bluedragon.distribution.command.JdResult;
import com.jd.bluedragon.distribution.handler.Handler;
import com.jd.bluedragon.distribution.handler.InterceptHandler;
import com.jd.bluedragon.distribution.handler.InterceptResult;
import com.jd.bluedragon.distribution.print.domain.BasePrintWaybill;
import com.jd.bluedragon.dms.utils.BusinessUtil;
import com.jd.bluedragon.utils.StringHelper;
import com.jd.ql.basic.dto.BaseSiteInfoDto;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Objects;

/**
 * C网称重，异常重量拦截
 * 功能入口：
 * 标签打印-站点平台打印（４个sheet页）、平台打印
 * 分拣中心-包裹称重、批量分拣称重
 *
 * 1.当包裹重量＞60kg and ≤1000kg时，提示“请确认包裹重量是否正确！”。点击“否”，系统不记录；点击“是”，系统记录。
 * 2.当包裹重量＞1000kg时，提示“重量错误，请重新称重！”。系统不记录。
 */
@Service("abnormalWeightForCHandler")
public class AbnormalWeightForCHandler implements InterceptHandler<WaybillPrintContext,String> {
	private static final String splitRegex = ",";

	// 拦截的 操作类型
	@Value("${print.abnormal.weight.printOperateTypes:100101,100102,100105,100107}")
	private String printOperateTypes;

	// 重量的阈值范围
	@Value("${print.abnormal.weight.weightThresholds:60,1000}")
	private String weightThresholds;

	@Override
	public InterceptResult<String> handle(WaybillPrintContext context) {

		WaybillPrintRequest request = context.getRequest();
		if (request != null) {
			if (checkOperateType(request.getOperateType())) {
				// 判断waybillSign是否 C 网
				if (!BusinessUtil.isCInternet(context.getWaybillSign())) {
					return context.getResult();
				}
				if (request.hasWeighted()) {
					// 重量异常 阈值
					String[] split = weightThresholds.split(splitRegex);

					WeightOperFlow weightOperFlow = request.getWeightOperFlow();
					double weight = weightOperFlow.getWeight();
					double thresholdLow = Double.parseDouble(split[0]);
					double thresholdHigh = Double.parseDouble(split[1]);

					// 强拦截状态，提示确认重量
					if (weight > thresholdLow && weight <= thresholdHigh) {
						if (!Objects.equals(Boolean.TRUE, request.getConfirm())) {
							context.getResult().toNeedConfirmStatus(JdResponse.CODE_PRINT_WEIGHT_WARNING, JdResponse.MESSAGE_CODE_PRINT_WEIGHT_WARNING);
						}
					}
					// 强制拦截状态，不通过
					if (weight > thresholdHigh) {
						context.getResult().toFail(JdResponse.CODE_PRINT_WEIGHT_ALTER, JdResponse.MESSAGE_CODE_PRINT_WEIGHT_ALTER);
					}
				}
			}
		}
		return context.getResult();
	}

	private boolean checkOperateType(Integer operateType) {
		String[] split = printOperateTypes.split(splitRegex);
		for (String s : split) {
			if (Objects.equals(s, String.valueOf(operateType))) {
				return true;
			}
		}
		return false;
	}
}
