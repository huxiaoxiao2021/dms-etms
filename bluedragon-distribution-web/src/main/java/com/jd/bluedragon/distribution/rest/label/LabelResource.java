package com.jd.bluedragon.distribution.rest.label;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.jd.bluedragon.Constants;
import com.jd.bluedragon.distribution.api.JdResponse;
import com.jd.bluedragon.distribution.label.domain.Label;
import com.jd.bluedragon.distribution.label.service.LabelService;

@Component
@Path(Constants.REST_URL)
@Consumes({ MediaType.APPLICATION_JSON })
@Produces({ MediaType.APPLICATION_JSON })
public class LabelResource {

	@Autowired
	private LabelService labelService;


	@POST
	@Path("/reverse/label")
	public JdResponse reprint(Label request) {
		this.labelService.add(request);
		return this.ok();
	}
	
	private JdResponse ok() {
		return new JdResponse(JdResponse.CODE_OK, JdResponse.MESSAGE_OK);
	}

}
