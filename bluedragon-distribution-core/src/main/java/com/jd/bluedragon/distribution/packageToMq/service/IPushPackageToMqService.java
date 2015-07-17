package com.jd.bluedragon.distribution.packageToMq.service;

import com.jd.bluedragon.distribution.api.request.WmsOrderPackagesRequest;

/**
 * @author yangpeng
 *
 */
public interface IPushPackageToMqService {
	
	/**
	 * 推送MQ消息（库房专用）
	 * @param orderPackages （库房包裹信息）
	 * @throws Exception
	 */
	public void pushPackageToMq(WmsOrderPackagesRequest orderPackages) throws Exception;
	
	/**
	 * 推送MQ消息
	 * @param key 
	 * @param body
	 * @param busiId
	 */
	public void pubshMq(String key,String body,String busiId);
	
	/**
	 * 出发UMP自定义预警
	 * @param key 自定义预警KEY
	 * @param msg 消息体
	 */
	public void pushAlert(String key,String msg);
}
