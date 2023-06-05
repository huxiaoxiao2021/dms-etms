package com.jd.bluedragon.distribution.rest.test;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.Pager;
import com.jd.bluedragon.common.utils.ProfilerHelper;
import com.jd.bluedragon.configuration.ucc.UccPropertyConfiguration;
import com.jd.bluedragon.core.base.BaseMajorManager;
import com.jd.bluedragon.distribution.api.JdResponse;
import com.jd.bluedragon.distribution.api.request.BasicSiteDto;
import com.jd.bluedragon.distribution.api.request.CapacityCodeRequest;
import com.jd.bluedragon.distribution.api.request.SiteQueryRequest;
import com.jd.bluedragon.distribution.api.response.RouteTypeResponse;
import com.jd.bluedragon.distribution.base.domain.CreateAndReceiveSiteInfo;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.base.domain.SiteWareHouseMerchant;
import com.jd.bluedragon.distribution.base.service.SiteService;
import com.jd.bluedragon.distribution.departure.domain.CapacityCodeResponse;
import com.jd.bluedragon.dms.utils.BusinessUtil;
import com.jd.bluedragon.utils.NumberHelper;
import com.jd.bluedragon.utils.StringHelper;
import com.jd.bluedragon.utils.XmlHelper;
import com.jd.ql.basic.dto.BaseStaffSiteOrgDto;
import com.jd.ql.dms.report.domain.StreamlinedBasicSite;
import com.jd.ql.dms.report.domain.StreamlinedSiteQueryCondition;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import com.jd.ump.profiler.CallerInfo;
import com.jd.ump.profiler.proxy.Profiler;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.jboss.resteasy.annotations.GZIP;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Component
@Path(Constants.REST_URL)
@Consumes({ MediaType.APPLICATION_JSON })
@Produces({ MediaType.APPLICATION_JSON })
public class TestResource {


	@GET
	@GZIP
	@Path("/test/xml/old/{xmlbody}")
	public boolean xmlOld(@PathParam("xmlbody") String xmlbody) {

		return XmlHelper.xmlToObjectOld(xmlbody, com.jd.bluedragon.distribution.reverse.domain.ReceiveRequest.class.getSimpleName(),
				com.jd.bluedragon.distribution.reverse.domain.ReceiveRequest.class, null) != null;
	}

	@GET
	@GZIP
	@Path("/test/xml/new/{xmlbody}")
	public boolean xmlNew(@PathParam("xmlbody") String xmlbody) {
		return XmlHelper.xmlToObject(xmlbody, com.jd.bluedragon.distribution.reverse.domain.ReceiveRequest.class.getSimpleName(),
				com.jd.bluedragon.distribution.reverse.domain.ReceiveRequest.class, null) != null;
	}


}
