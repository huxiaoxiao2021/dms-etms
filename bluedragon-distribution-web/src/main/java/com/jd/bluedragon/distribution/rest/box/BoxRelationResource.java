package com.jd.bluedragon.distribution.rest.box;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.distribution.api.JdResponse;
import com.jd.bluedragon.distribution.api.request.box.BoxRelationRequest;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.box.domain.BoxRelation;
import com.jd.bluedragon.distribution.box.service.BoxRelationService;
import com.jd.bluedragon.distribution.command.JdResult;
import com.jd.bluedragon.utils.JsonHelper;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.List;

/**
 * @ClassName BoxRelationResource
 * @Description
 * @Author wyh
 * @Date 2020/12/14 18:32
 **/
@Component
@Path(Constants.REST_URL)
@Consumes({MediaType.APPLICATION_JSON})
@Produces({MediaType.APPLICATION_JSON})
public class BoxRelationResource {

    private static final Logger LOGGER = LoggerFactory.getLogger(BoxRelationResource.class);

    @Autowired
    private BoxRelationService boxRelationService;

    @POST
    @Path("/boxRelation/getRelation")
    public JdResult<List<BoxRelation>> getBoxRelation(BoxRelationRequest request) {

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("BoxRelationResource-getBoxRelation. param:{}", JsonHelper.toJson(request));
        }

        JdResult<List<BoxRelation>> response = new JdResult<>();
        response.toSuccess();

        if (null == request.getSiteCode() ||
                (StringUtils.isBlank(request.getBoxCode()) && StringUtils.isBlank(request.getRelationBoxCode()))) {
            response.setCode(JdResponse.CODE_PARAM_ERROR);
            response.setMessage(JdResponse.MESSAGE_PARAM_ERROR);
            return response;
        }

        BoxRelation query = new BoxRelation();
        query.setCreateSiteCode(Long.valueOf(request.getSiteCode()));
        if (StringUtils.isNotBlank(request.getBoxCode())) {
            query.setBoxCode(request.getBoxCode());
        }
        else if (StringUtils.isNotBlank(request.getRelationBoxCode())) {
            query.setRelationBoxCode(request.getRelationBoxCode());
        }
        InvokeResult<List<BoxRelation>> sr = boxRelationService.queryBoxRelation(query);
        if (sr.codeSuccess() && CollectionUtils.isNotEmpty(sr.getData())) {
            response.setData(sr.getData());
        }

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("BoxRelationResource-getBoxRelation. response:{}", JsonHelper.toJson(response));
        }

        return response;
    }

    @POST
    @Path("/boxRelation/bind")
    public JdResult<Boolean> boxBind(BoxRelationRequest request) {
        JdResult<Boolean> result = new JdResult<>();
        result.toSuccess();

        BoxRelation relation = BoxRelation.genEntity(request);

        InvokeResult<Boolean> sr = boxRelationService.saveBoxRelation(relation);
        if (sr.codeSuccess()) {
            result.setData(Boolean.TRUE);
            result.setMessage("绑定成功！");
        }
        else {
            result.toFail("绑定失败！" + sr.getMessage());
        }

        return result;
    }
}
