package com.jd.bluedragon.external.service;

/**
 * 
 * @ClassName: EmsServiceManager
 * @Description: 推送全国邮政服务管理
 * @author: wuyoude
 * @date: 2018年5月9日 下午3:16:10
 *
 */
public interface EmsServiceManager {
	/**
	 * 推送EMS的消息
	 * @param xmlStr
	 * @return
	 */
    public String printEMSDatas(String xmlStr);
}
