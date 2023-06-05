package com.jd.bluedragon.distribution.rest.test;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.utils.XmlHelper;
import org.springframework.stereotype.Component;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

@Component
@Path(Constants.REST_URL)
@Consumes({ MediaType.APPLICATION_JSON })
@Produces({ MediaType.APPLICATION_JSON })
public class XmlResource {


	@POST
	@Path("/test/xml/old")
	public boolean xmlOld(String xmlbody) {

		return XmlHelper.xmlToObjectOld(xmlbody, com.jd.bluedragon.distribution.reverse.domain.ReceiveRequest.class.getSimpleName(),
				com.jd.bluedragon.distribution.reverse.domain.ReceiveRequest.class, null) != null;
	}

	@POST
	@Path("/test/xml/new")
	public boolean xmlNew(String xmlbody) {
		return XmlHelper.xmlToObject(xmlbody, com.jd.bluedragon.distribution.reverse.domain.ReceiveRequest.class.getSimpleName(),
				com.jd.bluedragon.distribution.reverse.domain.ReceiveRequest.class, null) != null;
	}


}
