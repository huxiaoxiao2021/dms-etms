package popPrint;

import com.jd.bluedragon.dms.utils.WaybillUtil;

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
        System.out.println(WaybillUtil.isPackageCode("ZYY000000154412-1-1-"));

	}
}
