package com.jd.bluedragon.distribution.rest.mail;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.resteasy.annotations.GZIP;
import org.springframework.stereotype.Component;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.utils.SendMailUtil;
import com.jd.bluedragon.utils.StringHelper;

@Component
@Path(Constants.REST_URL)
@Consumes({ MediaType.APPLICATION_JSON })
@Produces({ MediaType.APPLICATION_JSON })
public class MailTestResource {

	private final Log logger = LogFactory.getLog(this.getClass());

	@GET
	@Path("/mailTest/hello")
	@GZIP
	public Boolean getRemoteRes(@QueryParam("address") String address) {
		if(!StringHelper.isMailAddress(address)){
			return false;
		}else{
			List<String> addresses = new ArrayList<String>();
			addresses.add(address);
			SendMailUtil.sendSimpleEmail("Test", "Hello world!", addresses);
		}
        return true;
	}
}
