package com.jd.bluedragon.distribution.command;

import com.jd.bluedragon.distribution.api.request.WaybillPrintRequest;
import com.jd.bluedragon.distribution.command.handler.AbstractJsonCommandHandler;
import com.jd.bluedragon.distribution.handler.InterceptResult;
import com.jd.bluedragon.distribution.print.waybill.handler.WaybillPrintContext;
import com.jd.bluedragon.utils.JsonHelper;
import org.apache.commons.lang.StringUtils;

/**
 * 
 * @ClassName: WaybillPrintCommandHandler
 * @Description: 包裹标签打印-指令处理逻辑单元
 * @author: wuyoude
 * @date: 2018年1月25日 下午10:55:59
 *
 */
public class WaybillPrintCommandHandler extends AbstractJsonCommandHandler<WaybillPrintContext,InterceptResult<String>>{
	/**
	 * 将json请求内容转换为WaybillPrintContext对象
	 */
	@Override
	public WaybillPrintContext fromJson(JdCommand<String> target) {
		WaybillPrintContext context = new WaybillPrintContext();
		//初始化请求信息和返回结果信息
		InterceptResult<String> result = new InterceptResult<String>();
		result.toSuccess();
		WaybillPrintRequest waybillPrintRequest = JsonHelper.fromJson(target.getData(), WaybillPrintRequest.class);
		if(waybillPrintRequest != null){
			//如果操作编码为空则设置为外层JdCommand的请求编码
			if(waybillPrintRequest.getBusinessType() == null){
				waybillPrintRequest.setBusinessType(target.getBusinessType());
			}
			if(waybillPrintRequest.getOperateType() == null){
				waybillPrintRequest.setOperateType(target.getOperateType());
			}
			if(StringUtils.isBlank(waybillPrintRequest.getVersionCode())){
                waybillPrintRequest.setVersionCode(target.getVersionCode());
            }
		}
		context.setRequest(waybillPrintRequest);
		context.setResult(result);
		return context;
	}
}
