package com.jd.bluedragon.distribution.reverse.service;
/**
 * 
* 类描述： POP回传消息
* 创建者： libin
* 项目名称： bluedragon-distribution-core
* 创建时间： 2012-12-20 上午10:52:48
* 版本号： v1.0
 */
public interface  ReverseSendPopMessageService {
	public boolean sendPopMessage(String waybillCode);
	public String sendPopMessageForTest(String waybillCode);
}
