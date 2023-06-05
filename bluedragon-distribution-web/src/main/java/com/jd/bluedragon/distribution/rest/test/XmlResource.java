package com.jd.bluedragon.distribution.rest.test;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.utils.XmlHelper;
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
	@Path("/test/xml/old")
	public boolean xmlOld(String xmlbody) {
		boolean result = Boolean.FALSE;
		try{
			result = XmlHelper.xmlToObjectOld(xmlbody, com.jd.bluedragon.distribution.reverse.domain.ReceiveRequest.class.getSimpleName(),
					com.jd.bluedragon.distribution.reverse.domain.ReceiveRequest.class, null) != null;
		}catch (Exception e){
			logger.error("xmlOld,{}",xmlbody,e);
		}
		return result;
	}

	@POST
	@Path("/test/xml/new")
	public boolean xmlNew(String xmlbody) {
		boolean result = Boolean.FALSE;
		try{
			result = XmlHelper.xmlToObject(xmlbody, com.jd.bluedragon.distribution.reverse.domain.ReceiveRequest.class.getSimpleName(),
					com.jd.bluedragon.distribution.reverse.domain.ReceiveRequest.class, null) != null;
		}catch (Exception e){
			logger.error("xmlNew,{}",xmlbody,e);
		}
		return result;
	}


}
