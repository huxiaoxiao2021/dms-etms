package com.jd.bluedragon.distribution.popAbnormal.rest.client;

import javax.ws.rs.core.MediaType;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.resteasy.client.ClientRequest;
import org.jboss.resteasy.client.ClientResponse;

import com.jd.bluedragon.distribution.api.JdResponse;
import com.jd.bluedragon.utils.PropertiesHelper;

/**
 * @author zhaohc
 * @E-mail zhaohengchong@360buy.com
 * @createTime 2012-3-31 下午12:53:21
 * 
 *             更新缓存数据库 REST 客户端
 */
public class UpdateCacheWaybillClient {
	private static final Log logger = LogFactory
			.getLog(UpdateCacheWaybillClient.class);

	private static final String PROTOCOLS = "http://";
	private static final String CONTEXT = "/services/waybill/quantity/{0}/{1}";

	/**
	 * 更新缓存数据库中运单包裹数
	 * 
	 * @param waybillCode
	 * @param confirmNum
	 * @return
	 */
	public static int updateConfirmNum(String waybillCode, int confirmNum) {
		if (StringUtils.isBlank(waybillCode) || confirmNum <= 0) {
			logger.error("更新缓存数据库 REST --> updateConfirmNum，运单号：" + waybillCode
					+ "，数量：" + confirmNum + ", 参数有误");
			return JdResponse.CODE_PARAM_ERROR;
		}
		try {
			String url = PROTOCOLS
					+ PropertiesHelper.newInstance().getValue(
							"UPDATE_CACHE_QUANTITY_DOMAIN")
					+ CONTEXT.replace("{0}", waybillCode).replace("{1}",
							String.valueOf(confirmNum));
			ClientRequest request = new ClientRequest(url);
			request.accept(MediaType.APPLICATION_JSON);

			logger.info("更新缓存数据库 REST --> updateConfirmNum，运单号：" + waybillCode
					+ "，数量：" + confirmNum + ", 开始");
			ClientResponse<JdResponse> response = request.get(JdResponse.class);
			if (200 == response.getStatus()) {
				logger.info("更新缓存数据库 REST --> updateConfirmNum，运单号："
						+ waybillCode + "，数量：" + confirmNum + ", 成功");
				return JdResponse.CODE_OK;
			} else {
				logger.error("更新缓存数据库 REST --> updateConfirmNum，运单号："
						+ waybillCode + "，数量：" + confirmNum + ", 失败"
						+ ", 返回编号：" + response.getEntity().getCode() + "，信息："
						+ response.getEntity().getMessage());
				return response.getStatus();
			}
		} catch (Exception e) {
			logger.error("更新缓存数据库 REST --> updateConfirmNum，运单号：" + waybillCode
					+ "，数量：" + confirmNum + "，异常信息：" + e.getMessage());
			return JdResponse.CODE_SERVICE_ERROR;
		}
	}
}
