package com.jd.bluedragon.core.base;

public interface DtcDataReceiverManager {

	/**
     * 参数说明
     * @param  target,methodName,outboundType,messageValue,priority,averageQty,source
     * target           目标库房信息 (如6,6,1)
     * methodName           业务标识 **①**
     * outboundType　  业务标识 **②**
     * priority                        特殊支持 ( 无特殊说明给”2”)
     * messageValue                    报文  
     * messageMd5Value   报文md5值,  可为null
     * source                         数据来源 (如OFC或者其他)
     * outboundNo     单号  (或其他报文追踪数据标识)
     * 注意:①②
		1.       如果目的库房是WMS2.0库房(包括普通大家电), ①②均使用原接口中outboundType字段
		2.       如果目的库房是WMS3.0库房(包括海尔协同仓,亚洲一号,wms3.0/wms5.0), ①②均使用原接口中methodName字段
		
		
		分拣不区分库房类型与乔洪佥确认，使用每一条规则，也就是说methodName参数使用outboundType值
     */
	com.jd.staig.receiver.rpc.Result downStreamHandle(String target, String methodName, String outboundType, int priority, String messageValue, String messageMd5Value,
			String source, String outboundNo);

	/**
	 * 按分拣中心的特点进行的优化接口
	 * priority         使用默认值2
	 * messageMd5Value  使用默认值null
	 * methodName内部使用outboundType值
	 * 
     * 参数说明
     * @param  target,methodName,outboundType,messageValue,priority,averageQty,source
     * target           目标库房信息 (如6,6,1)
     * outboundType　         业务标识 **②**
     * messageValue     报文  
     * source           数据来源 (如OFC或者其他)
     * outboundNo       单号  (或其他报文追踪数据标识)
     */
	com.jd.staig.receiver.rpc.Result downStreamHandle(String target, String outboundType, String messageValue, String source, String outboundNo);

}
