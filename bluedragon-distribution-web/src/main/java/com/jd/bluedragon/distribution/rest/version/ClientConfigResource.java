package com.jd.bluedragon.distribution.rest.version;

import com.jd.bd.dms.automatic.sdk.modules.dmslocalserverinfo.entity.VipInfoJsfEntity;
import com.jd.bluedragon.Constants;
import com.jd.bluedragon.common.utils.ProfilerHelper;
import com.jd.bluedragon.distribution.api.JdResponse;
import com.jd.bluedragon.distribution.api.request.ClientConfigRequest;
import com.jd.bluedragon.distribution.api.response.VersionResponse;
import com.jd.bluedragon.distribution.rest.version.resp.ClientConfigResponse;
import com.jd.bluedragon.distribution.rest.version.resp.ServerVIPConfigResponse;
import com.jd.bluedragon.distribution.version.domain.ClientConfig;
import com.jd.bluedragon.distribution.version.domain.VersionEntity;
import com.jd.bluedragon.distribution.version.service.ClientConfigService;
import com.jd.ump.profiler.CallerInfo;
import com.jd.ump.profiler.proxy.Profiler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.ArrayList;
import java.util.List;

import static com.jd.bluedragon.distribution.api.JdResponse.*;

@Component
@Path(Constants.REST_URL)
@Consumes( { MediaType.APPLICATION_JSON })
@Produces( { MediaType.APPLICATION_JSON })
public class ClientConfigResource {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private ClientConfigService clientConfigService;
    /**
     * 查询所有的配置信息
     * @return
     */
    @GET
    @Path("/versions/config/getAll")
    public ClientConfigResponse getAll(){
        this.log.debug("get all config " );
        
        List<ClientConfig> list =clientConfigService.getAll();
        if (null != list) {
            ClientConfigResponse response=new ClientConfigResponse(CODE_OK,MESSAGE_OK);
            response.setDatas(list);
            return response;
        }
        return new ClientConfigResponse(CODE_SERVICE_ERROR,MESSAGE_SERVICE_ERROR);
    }
    
    /**
     * 依据ID查询配置信息
     * @param id
     * @return
     */
    @GET
    @Path("/versions/config/getById/{id}")
    public ClientConfigResponse getById(@PathParam("id") Long id){
        Assert.notNull(id, "id must not be null");

        ClientConfig clientVersion =clientConfigService.getById(id);
        if (null != clientVersion) {
            ClientConfigResponse response=new ClientConfigResponse(CODE_OK,MESSAGE_OK);
            List<ClientConfig> list =new ArrayList<ClientConfig>();
            list.add(clientVersion);
            response.setDatas(list);
            return response;
        }
        return new ClientConfigResponse(CODE_SERVICE_ERROR,MESSAGE_SERVICE_ERROR);
    }
    
    /**
     * 依据分拣中心编号查询配置信息
     * @param siteCode
     * @return
     */
    @GET
    @Path("/versions/config/getBySiteCode/{siteCode}")
    public ClientConfigResponse getBySiteCode(@PathParam("siteCode") String siteCode){
        Assert.notNull(siteCode, "siteCode must not be null");

        this.log.debug("siteCode {}", siteCode);

        List<ClientConfig> list =clientConfigService.getBySiteCode(siteCode);
        if (null != list) {
            ClientConfigResponse response=new ClientConfigResponse(CODE_OK,MESSAGE_OK);
            response.setDatas(list);
            return response;
        }
        return new ClientConfigResponse(CODE_SERVICE_ERROR,MESSAGE_SERVICE_ERROR);
    }
    
    /**
     * 依据应用程序类型查询所有的配置信息
     * @param programType
     * @return
     */
    @GET
    @Path("/versions/config/getByProgramType/{programType}")
    public ClientConfigResponse getByProgramType(@PathParam("programType") Integer programType){
        Assert.notNull(programType, "programType must not be null");

        this.log.debug("programType :{}", programType);
        
        List<ClientConfig> list =clientConfigService.getByProgramType(programType);
        if (null != list) {
            ClientConfigResponse response=new ClientConfigResponse(CODE_OK,MESSAGE_OK);
            response.setDatas(list);
            return response;
        }
        return new ClientConfigResponse(CODE_SERVICE_ERROR,MESSAGE_SERVICE_ERROR);
    }
    
    
    /**
     * 添加配置信息
     * @param request
     * @return
     */
    @POST
    @Path("/versions/config/add")
    public ClientConfigResponse add(ClientConfigRequest request){
        Assert.notNull(request, "request must not be null");

        ClientConfig clientConfig=toClientConfig(request);
        if (clientConfigService.exists(clientConfig)) {
            return new ClientConfigResponse(30000, "添加失败，版本配置已存在。");
        }
        if(clientConfigService.add(clientConfig)){
            return new ClientConfigResponse(CODE_OK, MESSAGE_OK);
        }
        return new ClientConfigResponse(CODE_SERVICE_ERROR, MESSAGE_SERVICE_ERROR);
    }
    
    /**
     * 修改配置信息
     * @param request
     * @return
     */
    @PUT
    @Path("/versions/config/update")
    public ClientConfigResponse update(ClientConfigRequest request){
        Assert.notNull(request, "request must not be null");

        ClientConfig clientConfig=toClientConfig(request);
        if(clientConfigService.update(clientConfig)){
            return new ClientConfigResponse(CODE_OK, MESSAGE_OK);
        }
        return new ClientConfigResponse(CODE_SERVICE_ERROR, MESSAGE_SERVICE_ERROR);
    }
    
    private ClientConfig toClientConfig(ClientConfigRequest request) {
        ClientConfig clientConfig=new ClientConfig();
        clientConfig.setConfigId(request.getConfigId());
        clientConfig.setVersionCode(request.getVersionCode());
        clientConfig.setProgramType(request.getProgramType());
        clientConfig.setSiteCode(request.getSiteCode());
        clientConfig.setYn(request.getYn());
        return clientConfig;
    }
    /**
     * 新接口-依据分拣中心编号和应用程序类型查询该分拣中心的可用版本和下载地址
     * 
     * @param siteCode
     * @param programType
     * @return
     */
    @GET
    @Path("/versions/config/getCurrentVersionNew/{siteCode}/{programType}")
    public VersionResponse getCurrentVersionNew(
            @PathParam("siteCode") String siteCode,
            @PathParam("programType") Integer programType) {
    	return getCurrentVersion(siteCode,programType,true);
    }
    /**
     * 旧接口-依据分拣中心编号和应用程序类型查询该分拣中心的可用版本和下载地址
     * 
     * @param siteCode
     * @param programType
     * @return
     */
    @GET
    @Path("/versions/config/getCurrentVersion/{siteCode}/{programType}")
    public VersionResponse getCurrentVersionOld(
            @PathParam("siteCode") String siteCode,
            @PathParam("programType") Integer programType) {
    	return getCurrentVersion(siteCode,programType,false);
    }
    private VersionResponse getCurrentVersion(String siteCode,Integer programType,boolean isNew) {
    	//加入ump监控，动态生成key，监控每种客户端的调用量
    	String umpKey = "DMS.Web.ClientConfigResource.getCurrentVersion";
    	if(isNew){
    		umpKey += "New"+programType;
    	}else{
    		umpKey += programType;
    	}
    	CallerInfo callerInfo = ProfilerHelper.registerInfo(umpKey);
        Assert.notNull(siteCode, "siteCode must not be null");
        Assert.notNull(programType, "programType must not be null");

        this.log.debug("siteCode :{}", siteCode);
        this.log.debug("programType :{}", programType);
        Profiler.registerInfoEnd(callerInfo);
        VersionEntity versionEntity = new VersionEntity(siteCode, programType);
        VersionEntity entity = this.clientConfigService
                .getVersionEntity(versionEntity);
        if (null == entity) {
            return new VersionResponse(VersionResponse.CODE_Version_ERROR,
                    VersionResponse.MESSAGE_Version_ERROR);
        }

        return this.toVersionResponse(entity);
    }
    private VersionResponse toVersionResponse(VersionEntity entity) {
        VersionResponse response = new VersionResponse(JdResponse.CODE_OK,
                JdResponse.MESSAGE_OK);
        response.setSiteCode(entity.getSiteCode());
        response.setProgramType(entity.getProgramType());
        response.setVersionCode(entity.getVersionCode());
        response.setDownloadUrl(entity.getDownloadUrl());
        return response;
    }

    /**
     * 根据分拣中心ID获取本地服务器VIP地址
     *
     * @param siteCode
     * @return
     */
    @POST
    @Path("/versions/config/getServerVIPByDmsId/{siteCode}")
    public ServerVIPConfigResponse getServerVIPByDmsId(@PathParam("siteCode") Integer siteCode) {
        Assert.notNull(siteCode, "siteCode must not be null");
        return this.getServerVIPConfigResponse(this.clientConfigService.getVipListByDmsId(siteCode));
    }

    /**
     * 获取所有的分拣中心本地服务器VIP地址
     *
     * @return
     */
    @POST
    @Path("/versions/config/getAllServerVIP")
    public ServerVIPConfigResponse getAllServerVIP() {
        return this.getServerVIPConfigResponse(this.clientConfigService.getAllVipList());
    }

    private ServerVIPConfigResponse getServerVIPConfigResponse(List<VipInfoJsfEntity> datas) {
        if (datas == null) {
            return new ServerVIPConfigResponse(JdResponse.CODE_SERVICE_ERROR, JdResponse.MESSAGE_SERVICE_ERROR);
        } else {
            ServerVIPConfigResponse response = new ServerVIPConfigResponse(JdResponse.CODE_OK, JdResponse.MESSAGE_OK);
            response.setDatas(datas);
            return response;
        }
    }
}
