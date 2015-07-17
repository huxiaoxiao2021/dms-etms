package com.jd.bluedragon.distribution.saf;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.client.RestTemplate;

import com.jd.bluedragon.utils.PropertiesHelper;
import com.jd.bluedragon.utils.StringHelper;

public class WaybillSafServiceImpl implements WaybillSafService{

	private final Log logger = LogFactory.getLog(this.getClass());
	/**
	 * DMSVER_ADDRESS,查询取消订单地址
	 */
	private final static String DMSVER_ADDRESS = PropertiesHelper.newInstance().getValue("DMSVER_ADDRESS");
	
	private final RestTemplate template = new RestTemplate();

	/* (non-Javadoc)
	 * @see com.jd.bluedragon.distribution.saf.WaybillSafService#isCancel(java.lang.String)
	 */
	public WaybillSafResponse isCancel(String packageCode) {
		logger.info("com.jd.bluedragon.distribution.saf.WaybillSafServiceImpl.isCancel---start!");
		if(StringHelper.isEmpty(packageCode)) packageCode="";
		String url = DMSVER_ADDRESS+"/services/waybills/cancel?packageCode="+packageCode;

		WaybillSafResponse response = this.template.getForObject(url, WaybillSafResponse.class);
		logger.info("com.jd.bluedragon.distribution.saf.WaybillSafServiceImpl.isCancel---end!");
		return response;
	}

	
}
