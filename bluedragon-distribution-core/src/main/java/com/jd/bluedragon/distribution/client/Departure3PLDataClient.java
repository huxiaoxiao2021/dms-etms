package com.jd.bluedragon.distribution.client;

import java.net.URLEncoder;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.core.MediaType;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.xml.security.utils.Base64;
import org.jboss.resteasy.client.ClientRequest;
import org.jboss.resteasy.client.ClientResponse;

import com.jd.bluedragon.distribution.api.JdResponse;
import com.jd.bluedragon.distribution.failqueue.domain.DealData_Departure_3PL;
import com.jd.bluedragon.utils.PropertiesHelper;
import com.jd.etms.thirdpl.dto.OrderShipsDto;
import com.jd.etms.thirdpl.dto.ThirdShipIdDto;

/**
 * @author huangliang
 * 
 *         发送发车信息到3PL客户端 /rest/ThirdJobNoRoutEntry/insertOrderShips
 * 
 */
public class Departure3PLDataClient {
	private static final Log logger = LogFactory
			.getLog(Departure3PLDataClient.class);

	private static final String PROTOCOLS = "http://";
	private static final String CONTEXT = "/rest/ThirdJobNoRoutEntry/insertOrderShips";

	/**
	 * 发送发车信息到3PL,只向其传送批次号，运单号，承运商
	 * 
	 * @param waybillCode
	 * @param confirmNum
	 * @return
	 */
	public static int departure3PLData(DealData_Departure_3PL dealData) {

		try {
			//1. 发送数据到3pl, rest接口调用 , 参数拼装
			ThirdShipIdDto tsd = new ThirdShipIdDto();
			tsd.setFlagOrderType(2);
			List<String> shipIds = new ArrayList<String>();
			shipIds.add(dealData.getThirdWaybillCode());
			tsd.setShipIds(shipIds);
			tsd.setThirdId(dealData.getCarrierId());
			tsd.setThirdName(dealData.getCarrierName());

			OrderShipsDto osd = new OrderShipsDto();
			osd.setOrderId(dealData.getSendCode());
			List<ThirdShipIdDto> thirdShipIdDtos = new ArrayList<ThirdShipIdDto>();
			thirdShipIdDtos.add(tsd);
			osd.setThirdShipIdDto(thirdShipIdDtos);

			//2. 3pl侧要求以post的方式传送data(osd json串), data_validate(data
			// token值),以下是组织这些信息的逻辑
			String url = PROTOCOLS
					+ PropertiesHelper.newInstance().getValue("3PL_WEB_DOMAIN")
					+ CONTEXT;
			String validateStr = PropertiesHelper.newInstance().getValue(//生成验证码需要,在worker配置文件中
					"3pl.rest.insertorderships.validateStr");

			String osdJson = "["+JsonUtil.getInstance().object2Json(osd)+"]";//"["+JsonUtil.marshal(osd)+"]";
			String token = URLEncoder.encode( // 生成token值
					Base64.encode(MessageDigest.getInstance("md5").digest(
							(osdJson + validateStr).getBytes("UTF-8"))), "UTF-8");//重要：统一使用"UTF-8"编码，防止不同系统、容器造成的编码差别
			
			//重要：以下两句必须加，否则验证不通过
			osdJson = URLEncoder.encode(osdJson, "UTF-8");//进行url编码防止汉字乱码
			token = URLEncoder.encode(token, "UTF-8");//进行url编码防止汉字乱码
			
			//3.发送请求
			ClientRequest request = new ClientRequest(url);
			String bodyStr = "data=" + osdJson + "&data_validate=" + token;
			request.body(MediaType.APPLICATION_FORM_URLENCODED, bodyStr);

			logger.info("向3PL发送三方运单数据，三方运单号：" + dealData.getThirdWaybillCode()
					+ "，承运商编码：" + dealData.getCarrierId() + "，批次号："
					+ dealData.getSendCode() + ", 开始");
			ClientResponse<String> response = request.post(String.class);

			//4.处理请求结果
			if ("success".equals(response.getEntity())) {
				logger.info("向3PL发送三方运单数据，三方运单号："
						+ dealData.getThirdWaybillCode() + "，承运商编码："
						+ dealData.getCarrierId() + "，批次号："
						+ dealData.getSendCode() + ", 成功");
				return JdResponse.CODE_OK;
			} else {
				logger.error("向3PL发送三方运单数据，三方运单号："
						+ dealData.getThirdWaybillCode() + "，承运商编码："
						+ dealData.getCarrierId() + "，批次号："
						+ dealData.getSendCode() + ", 失败:" + response.getEntity());
				return JdResponse.CODE_SERVICE_ERROR;
			}
		} catch (Exception e) {
			logger.error(
					"向3PL发送三方运单数据，三方运单号：" + dealData.getThirdWaybillCode()
							+ "，承运商编码：" + dealData.getCarrierId() + "，批次号："
							+ dealData.getSendCode() + "，异常信息：", e);
			return JdResponse.CODE_SERVICE_ERROR;
		}
	}
}
