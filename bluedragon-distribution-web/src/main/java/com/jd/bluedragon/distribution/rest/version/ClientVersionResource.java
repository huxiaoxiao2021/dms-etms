package com.jd.bluedragon.distribution.rest.version;

import static com.jd.bluedragon.distribution.api.JdResponse.CODE_OK;
import static com.jd.bluedragon.distribution.api.JdResponse.CODE_SERVICE_ERROR;
import static com.jd.bluedragon.distribution.api.JdResponse.MESSAGE_OK;
import static com.jd.bluedragon.distribution.api.JdResponse.MESSAGE_SERVICE_ERROR;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import com.jd.bluedragon.Constants; 
import com.jd.bluedragon.distribution.api.request.ClientVersionRequest;
import com.jd.bluedragon.distribution.rest.version.resp.ClientVersionResponse;
import com.jd.bluedragon.distribution.version.domain.ClientVersion;
import com.jd.bluedragon.distribution.version.service.ClientVersionService;

@Component
@Path(Constants.REST_URL)
@Consumes( { MediaType.APPLICATION_JSON })
@Produces( { MediaType.APPLICATION_JSON })
public class ClientVersionResource {

	private final Log logger = LogFactory.getLog(this.getClass());

	@Autowired
	private ClientVersionService clientVersionService;

	/**
	 * 查询所有的版本信息
	 * 
	 * @return
	 */
	@GET
	@Path("/versions/version/getAll")
	public ClientVersionResponse getAll() {
	    this.logger.info("get all version " );
        
        List<ClientVersion> list =clientVersionService.getAll();
        if (null != list) {
            ClientVersionResponse response=new ClientVersionResponse(CODE_OK,MESSAGE_OK);
            response.setDatas(list);
            return response;
        }
        return new ClientVersionResponse(CODE_SERVICE_ERROR,MESSAGE_SERVICE_ERROR);
    }

	/**
	 * 查询所有可用的版本信息
	 * 
	 * @return
	 */
	@GET
	@Path("/versions/version/getAllAvailable")
	public ClientVersionResponse getAllAvailable() {
	    this.logger.info("get all available version " );
        
        List<ClientVersion> list =clientVersionService.getAllAvailable();
        if (null != list) {
            ClientVersionResponse response=new ClientVersionResponse(CODE_OK,MESSAGE_OK);
            response.setDatas(list);
            return response;
        }
        return new ClientVersionResponse(CODE_SERVICE_ERROR,MESSAGE_SERVICE_ERROR);
    }

	/**
	 * 依据ID查询版本信息
	 * 
	 * @param id
	 * @return
	 */
	@GET
	@Path("/versions/version/getById/{id}")
	public ClientVersionResponse getById(@PathParam("id") Long id) {
	    Assert.notNull(id, "id must not be null");

        this.logger.info("id " + id);

        ClientVersion clientVersion =clientVersionService.getById(id);        
        if (null != clientVersion) {
            ClientVersionResponse response=new ClientVersionResponse(CODE_OK,MESSAGE_OK);
            List<ClientVersion> list =new ArrayList<ClientVersion>();
            list.add(clientVersion);
            response.setDatas(list);
            return response;
        }
        return new ClientVersionResponse(CODE_SERVICE_ERROR,MESSAGE_SERVICE_ERROR);
	}

	/**
	 * 依据版本号查询版本信息
	 * 
	 * @param versionCode
	 * @return
	 */
	@GET
	@Path("/versions/version/getByVersionCode/{versionCode}")
	public ClientVersionResponse getByVersionCode(
			@PathParam("versionCode") String versionCode) {
	    Assert.notNull(versionCode, "versionCode must not be null");

        this.logger.info("versionCode " + versionCode);
        
        List<ClientVersion> list =clientVersionService.getByVersionCode(versionCode);
        if (null != list) {
            ClientVersionResponse response=new ClientVersionResponse(CODE_OK,MESSAGE_OK);
            response.setDatas(list);
            return response;
        }
        return new ClientVersionResponse(CODE_SERVICE_ERROR,MESSAGE_SERVICE_ERROR);
	}

	/**
	 * 依据版本类型查询所有的版本信息
	 * 
	 * @param versionType
	 * @return
	 */
	@GET
	@Path("/versions/version/getByVersionType/{versionType}")
	public ClientVersionResponse getByVersionType(
			@PathParam("versionType") Integer versionType) {
	    Assert.notNull(versionType, "versionType must not be null");

        this.logger.info("versionType " + versionType);
        
        List<ClientVersion> list =clientVersionService.getByVersionType(versionType);
        if (null !=list) {
            ClientVersionResponse response=new ClientVersionResponse(CODE_OK,MESSAGE_OK);
            response.setDatas(list);
            return response;
        }
        return new ClientVersionResponse(CODE_SERVICE_ERROR,MESSAGE_SERVICE_ERROR);
	}

	 
	/**
	 * 添加版本信息
	 * 
	 * @param clientVersion
	 * @return
	 */
	@POST
    @Path("/versions/version/add")
    public ClientVersionResponse  add(ClientVersionRequest request){	    
	    Assert.notNull(request, "request must not be null");

        this.logger.info("request " + request);
        
        ClientVersion clientVersion=toClientVersion(request);
        if (clientVersionService.exists(clientVersion)) {
            return new ClientVersionResponse(30000, "添加失败，版本信息已存在。");
        }
        if(clientVersionService.add(clientVersion)){
            return new ClientVersionResponse(CODE_OK, MESSAGE_OK);
        }
        return new ClientVersionResponse(CODE_SERVICE_ERROR, MESSAGE_SERVICE_ERROR);
    }

	/**
	 * 修改版本信息
	 * 
	 * @param clientVersion
	 * @return
	 */
	@PUT
    @Path("/versions/version/update")
    public ClientVersionResponse update(ClientVersionRequest request){	    
	    Assert.notNull(request, "request must not be null");

        this.logger.info("request " + request);
        
        ClientVersion clientVersion=toClientVersion(request);
        if(clientVersionService.update(clientVersion)){
            return new ClientVersionResponse(CODE_OK, MESSAGE_OK);
        }
        return new ClientVersionResponse(CODE_SERVICE_ERROR, MESSAGE_SERVICE_ERROR);
    }

    private ClientVersion toClientVersion(ClientVersionRequest request) {
        ClientVersion clientVersion=new ClientVersion();
        clientVersion.setVersionId(request.getVersionId());
        clientVersion.setVersionCode(request.getVersionCode());
        clientVersion.setVersionType(request.getVersionType());
        clientVersion.setDownloadUrl(request.getDownloadUrl());
        clientVersion.setMemo(request.getMemo());
        clientVersion.setYn(request.getYn());
        return clientVersion;
    }

}
