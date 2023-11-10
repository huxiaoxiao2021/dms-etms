package com.jd.bluedragon.distribution.rest.crosssorting;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.configuration.DmsConfigManager;
import com.jd.bluedragon.core.jsf.boxlimit.BoxLimitConfigManager;
import com.jd.bluedragon.distribution.api.JdResponse;
import com.jd.bluedragon.distribution.api.request.CrossSortingRequest;
import com.jd.bluedragon.distribution.cross.domain.CrossSorting;
import com.jd.bluedragon.distribution.cross.domain.CrossSortingResponse;
import com.jd.bluedragon.distribution.cross.service.CrossSortingService;
import com.jd.bluedragon.distribution.jsf.service.JsfSortingResourceService;
import com.jd.bluedragon.distribution.mixedPackageConfig.domain.MixedPackageConfigResponse;
import com.jd.bluedragon.distribution.mixedPackageConfig.enums.RuleTypeEnum;
import com.jd.bluedragon.distribution.mixedPackageConfig.enums.YNEnum;
import com.jd.bluedragon.utils.JsonHelper;
import com.jdl.basic.api.domain.boxFlow.CollectBoxFlowDirectionConf;
import com.jdl.basic.api.enums.FlowDirectionTypeEnum;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.*;

import static com.jdl.basic.api.domain.boxFlow.CollectBoxFlowDirectionConf.COLLECT_CLAIM_MIX;

/**
 * Created by yanghongqiang on 2015/7/8.
 */

@Component
@Path(Constants.REST_URL)
@Consumes({MediaType.APPLICATION_JSON})
@Produces({MediaType.APPLICATION_JSON})
public class CrossSortingResource {

    private final Logger log = LoggerFactory.getLogger(this.getClass());
    @Autowired
    private CrossSortingService crossSortingService;

    @Autowired(required = false)
    private JsfSortingResourceService jsfSortingResourceService;

    @Value("${useNewMixedConfig}")
    private String useNewMixedConfig;

    @Autowired
    private DmsConfigManager dmsConfigManager;

    @Autowired
    private BoxLimitConfigManager boxLimitConfigManager;

    @POST
    @Path("/crosssorting/queryMixBoxSite")
    public CrossSortingResponse queryMixBoxSite(CrossSortingRequest request) {
        if(log.isInfoEnabled()){
            log.info("查询建包发货规则:{}", JsonHelper.toJson(request));
        }
        CrossSortingResponse response = new CrossSortingResponse();
        List<CrossSorting> mixDmsList;
        try {
            if (null == request || null == request.getCreateDmsCode()
                    || request.getCreateDmsCode() < 1
                    || null == request.getDestinationDmsCode()
                    || request.getDestinationDmsCode() < 1
                    || null == request.getType()
                    || request.getType() < 1) {
                log.warn(JsonHelper.toJson(request));
                response.setCode(JdResponse.CODE_PARAM_ERROR);
                response.setMessage(JdResponse.MESSAGE_PARAM_ERROR);
                return response;
            }
            //如果使用新混装规则，则走新的混装规则
            log.info("是否使用新混装规则：{}", useNewMixedConfig);
            if (dmsConfigManager.getPropertyConfig().getMixedConfigUseBasicNew()) {
                // 使用分拣工作台配置的新规则
                log.info("混包校验使用分拣工作台配置规则");
                mixDmsList = getMixedConfigUseBasicNew(request);
            } else if (YNEnum.Y.getCode().equals(useNewMixedConfig)) {
                mixDmsList = getMixedConfigsBySitesAndTypes(request.getCreateDmsCode(), request.getDestinationDmsCode(), request.getTransportType(), RuleTypeEnum.BUILD_PACKAGE.getCode());
            } else {
                Map<String, Object> params = new HashMap<String, Object>();
                params.put("createDmsCode", request.getCreateDmsCode());
                params.put("destinationDmsCode", request.getDestinationDmsCode());
                params.put("type", request.getType());
                mixDmsList = crossSortingService.findMixDms(params);
            }
            response.setCode(JdResponse.CODE_OK);
            response.setMessage(JdResponse.MESSAGE_OK);
            response.setData(mixDmsList);
        } catch (Exception e) {
            log.error("查询建包规则:{}" , JsonHelper.toJson(request), e);
            response.setCode(JdResponse.CODE_PARAM_ERROR);
            response.setMessage(JdResponse.MESSAGE_PARAM_ERROR);
        }
        return response;
    }

    private List<CrossSorting> getMixedConfigUseBasicNew(CrossSortingRequest request) {
        List<CollectBoxFlowDirectionConf> flowConfList = boxLimitConfigManager.listCollectBoxFlowDirection(assembleCollectBoxFlowDirectionConf(request), Collections.singletonList(COLLECT_CLAIM_MIX));
        List<CrossSorting> mixDmsList = new ArrayList<>();
        if (!CollectionUtils.isEmpty(flowConfList)) {
            for (CollectBoxFlowDirectionConf collectBoxFlowDirectionConf : flowConfList) {
                CrossSorting crossSorting = new CrossSorting();
                crossSorting.setCreateDmsCode(collectBoxFlowDirectionConf.getStartSiteId());
                crossSorting.setDestinationDmsCode(request.getDestinationDmsCode());
                crossSorting.setMixDmsCode(collectBoxFlowDirectionConf.getEndSiteId());
                crossSorting.setMixDmsName(collectBoxFlowDirectionConf.getEndSiteName());
                mixDmsList.add(crossSorting);
            }
        }
        return mixDmsList;
    }

    private CollectBoxFlowDirectionConf assembleCollectBoxFlowDirectionConf(CrossSortingRequest request) {
        CollectBoxFlowDirectionConf conf =new CollectBoxFlowDirectionConf();
        conf.setStartSiteId(request.getCreateDmsCode());
        conf.setBoxReceiveId(request.getDestinationDmsCode());
        conf.setFlowType(FlowDirectionTypeEnum.OUT_SITE.getCode());
        return conf;
    }

    /**
     * 查询新混装规则列表
     *
     * @param createSiteCode  建包分拣中心
     * @param receiveSiteCode 目的分拣中心
     * @param transportType   传输类型
     * @param ruleType        规则类型
     * @return 混装集合
     */
    private List<CrossSorting> getMixedConfigsBySitesAndTypes(Integer createSiteCode, Integer receiveSiteCode, Integer transportType, Integer ruleType) {
        List<MixedPackageConfigResponse> mixedPackageConfigList = jsfSortingResourceService.getMixedConfigsBySitesAndTypes(createSiteCode, receiveSiteCode, transportType, ruleType);
        List<CrossSorting> mixDmsList = new ArrayList<CrossSorting>();
        for (MixedPackageConfigResponse mixedPackageConfigResponse : mixedPackageConfigList) {
            CrossSorting crossSorting = new CrossSorting();
            crossSorting.setCreateDmsCode(mixedPackageConfigResponse.getCreateSiteCode());
            crossSorting.setDestinationDmsCode(mixedPackageConfigResponse.getReceiveSiteCode());
            crossSorting.setMixDmsCode(mixedPackageConfigResponse.getMixedSiteCode());
            crossSorting.setMixDmsName(mixedPackageConfigResponse.getMixedSiteName());
            mixDmsList.add(crossSorting);
        }
        return mixDmsList;
    }
}