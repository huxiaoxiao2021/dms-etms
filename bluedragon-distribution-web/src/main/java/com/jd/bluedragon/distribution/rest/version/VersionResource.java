package com.jd.bluedragon.distribution.rest.version;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.distribution.api.JdResponse;
import com.jd.bluedragon.distribution.api.response.VersionResponse;
import com.jd.bluedragon.distribution.version.domain.VersionEntity;
import com.jd.bluedragon.distribution.version.service.ClientConfigService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

@Component
@Path(Constants.REST_URL)
@Consumes({ MediaType.APPLICATION_JSON })
@Produces({ MediaType.APPLICATION_JSON })
public class VersionResource {

	private final Logger log = LoggerFactory.getLogger(this.getClass());
	
    @Autowired
    private ClientConfigService clientConfigService;

    /**
     * 依据分拣中心编号和应用程序类型查询该分拣中心的可用版本和下载地址
     * @param siteCode
     * @param programType
     * @return
     */
    @GET
    @Path("/versions/get")
    public VersionResponse get(@QueryParam("siteCode") String siteCode,
            @QueryParam("programType") Integer programType) {
        Assert.notNull(siteCode, "siteCode must not be null");
        Assert.notNull(programType, "programType must not be null");

        this.log.info("siteCode {}", siteCode);
        this.log.info("programType {}", programType);

        VersionEntity versionEntity=new VersionEntity(siteCode,programType);
        VersionEntity entity = this.clientConfigService.getVersionEntity(versionEntity);
        if (null==entity) {
             return new VersionResponse(VersionResponse.CODE_Version_ERROR, VersionResponse.MESSAGE_Version_ERROR);
        }
      
        return this.toVersionResponse(entity);
    }

	private VersionResponse toVersionResponse(VersionEntity entity) {
		VersionResponse response=new VersionResponse(JdResponse.CODE_OK, JdResponse.MESSAGE_OK);
		response.setSiteCode(entity.getSiteCode());
		response.setProgramType(entity.getProgramType());
		response.setVersionCode(entity.getVersionCode());
		response.setDownloadUrl(entity.getDownloadUrl());
		return response;
	}

}
