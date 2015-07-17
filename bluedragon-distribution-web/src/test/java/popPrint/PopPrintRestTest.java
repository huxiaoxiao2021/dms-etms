package popPrint;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.springframework.web.client.RestTemplate;

import com.jd.bluedragon.distribution.api.request.PopPrintRequest;
import com.jd.bluedragon.distribution.api.response.PopPrintResponse;
import com.jd.bluedragon.utils.JsonHelper;

/**
 * @author zhaohc
 * @E-mail zhaohengchong@360buy.com
 * @createTime 2012-3-15 下午09:49:36
 * 
 *             类说明
 */
public class PopPrintRestTest {

	public static final String urlRoot = "http://10.10.237.69:18080/bluedragon-distribution-web/services/";

	public static void main(String[] args) {
		PopPrintRequest popPrintRequest = new PopPrintRequest();
		popPrintRequest.setOperateSiteCode(1000);
		popPrintRequest.setWaybillCode("10030");
		popPrintRequest.setOperatorCode(1234);
		popPrintRequest.setOperateTime(new SimpleDateFormat(
				"yyyy-MM-dd HH:mm:ss").format(new Date()));
		popPrintRequest.setOperateType(1);
		System.out.println(JsonHelper.toJson(popPrintRequest));

		RestTemplate template = new RestTemplate();
		String url = urlRoot + "popPrint/savePopPrint";
		PopPrintResponse response = template.postForObject(url,
				popPrintRequest, PopPrintResponse.class);
		System.out.println(response);

	}
}
