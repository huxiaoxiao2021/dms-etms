package com.jd.bluedragon.distribution.popAbnormal.rest.client;

import com.jd.bluedragon.distribution.api.JdResponse;
import com.jd.bluedragon.utils.PropertiesHelper;
import org.apache.commons.lang.StringUtils;
import org.jboss.resteasy.client.ClientRequest;
import org.jboss.resteasy.client.ClientResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.core.MediaType;

/**
 * @author zhaohc
 * @E-mail zhaohengchong@360buy.com
 * @createTime 2012-3-31 下午12:53:21
 * 
 *             更新缓存数据库 REST 客户端
 */
public class UpdateCacheWaybillClient {
	private static final Logger log = LoggerFactory.getLogger(UpdateCacheWaybillClient.class);

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
			log.warn("更新缓存数据库 REST --> updateConfirmNum，运单号：{}，数量：{}, 参数有误"
					,waybillCode,confirmNum);
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

			log.info("更新缓存数据库 REST --> updateConfirmNum，运单号：{}，数量：{}, 开始",waybillCode,confirmNum);
			ClientResponse<JdResponse> response = request.get(JdResponse.class);
			if (200 == response.getStatus()) {
				log.info("更新缓存数据库 REST --> updateConfirmNum，运单号：{}，数量：{}, 成功",waybillCode,confirmNum);
				return JdResponse.CODE_OK;
			} else {
				log.warn("更新缓存数据库 REST --> updateConfirmNum，运单号：{}，数量：{}, 失败, 返回编号：{}，信息：{}"
						,waybillCode,confirmNum,response.getEntity().getCode(),response.getEntity().getMessage());
				return response.getStatus();
			}
		} catch (Exception e) {
			log.error("更新缓存数据库 REST --> updateConfirmNum，运单号：{}，数量：{}, 异常",waybillCode,confirmNum,e);
			return JdResponse.CODE_SERVICE_ERROR;
		}
	}
}
