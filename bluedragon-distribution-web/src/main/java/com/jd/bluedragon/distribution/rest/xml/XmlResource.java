package com.jd.bluedragon.distribution.rest.xml;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.bluedragon.utils.XmlHelper;
import org.apache.avro.data.Json;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

@Component
@Path(Constants.REST_URL)
@Consumes({ MediaType.APPLICATION_JSON })
@Produces({ MediaType.APPLICATION_JSON })
public class XmlResource {

	private Logger logger = LoggerFactory.getLogger(XmlResource.class);

	@POST
	@Path("/test/xml")
	public boolean xml(String xmlbody) {
		boolean result = Boolean.FALSE;
		try{
			com.jd.bluedragon.distribution.reverse.domain.ReceiveRequest request  = XmlHelper.xmlToObject(xmlbody, com.jd.bluedragon.distribution.reverse.domain.ReceiveRequest.class.getSimpleName(),
					com.jd.bluedragon.distribution.reverse.domain.ReceiveRequest.class, null);
			logger.error("XmlResource xmlNew test {},{}",xmlbody, JsonHelper.toJson(request));
			result = request != null;
		}catch (Exception e){
			logger.error("xmlNew error,{}",xmlbody,e);
		}
		return result;
	}

	@POST
	@Path("/test/isXml")
	public boolean isXml(String xmlbody) {
		return XmlHelper.isXml(xmlbody, com.jd.bluedragon.distribution.reverse.domain.ReceiveRequest.class,null);
	}


}
