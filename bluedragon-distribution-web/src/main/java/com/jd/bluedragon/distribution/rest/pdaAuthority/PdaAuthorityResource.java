package com.jd.bluedragon.distribution.rest.pdaAuthority;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.distribution.api.JdResponse;
import com.jd.bluedragon.distribution.command.JdResult;
import com.jd.bluedragon.distribution.funcSwitchConfig.FuncSwitchConfigDto;
import com.jd.bluedragon.distribution.funcSwitchConfig.FuncSwitchConfigEnum;
import com.jd.bluedragon.distribution.funcSwitchConfig.service.FuncSwitchConfigService;
import com.jd.bluedragon.distribution.whitelist.DimensionEnum;
import com.jd.bluedragon.distribution.whitelist.WhiteList;
import com.jd.bluedragon.utils.JsonHelper;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

/**
 * @author lijie
 * @date 2020/3/18 9:59
 */
@Component
@Path(Constants.REST_URL)
@Consumes({ MediaType.APPLICATION_JSON })
@Produces({ MediaType.APPLICATION_JSON })
public class PdaAuthorityResource {

    private static final Logger log = LoggerFactory.getLogger(PdaAuthorityResource.class);

    @Autowired
    private FuncSwitchConfigService funcSwitchConfigService;

    @POST
    @Path("/pdaAuthority/inspectionAuthority")
    public JdResult<Boolean> inspectionAuthority(WhiteList whiteListrequest){

        JdResult<Boolean> result = new JdResult<>();
        result.toSuccess();

        if (null == whiteListrequest
                || null == whiteListrequest.getMenuId()
                || null == whiteListrequest.getDimensionId()
                || null == whiteListrequest.getErp()) {
            result.setCode(JdResponse.CODE_PARAM_ERROR);
            result.setMessage(JdResponse.MESSAGE_PARAM_ERROR);
            return result;
        }

        //过滤掉场地的参数
        if(whiteListrequest.getSiteCode() == 0){
            whiteListrequest.setSiteCode(null);
        }
        try{
            //查询PDA登陆人是否有验货权限
            FuncSwitchConfigDto dto = new FuncSwitchConfigDto();
            dto.setMenuCode(FuncSwitchConfigEnum.FUNCTION_INSPECTION.getCode());
            dto.setDimensionCode(DimensionEnum.PERSON.getCode());
            dto.setOperateErp(whiteListrequest.getErp());
            dto.setSiteCode(whiteListrequest.getSiteCode());
            if(CollectionUtils.isEmpty(funcSwitchConfigService.getFuncSwitchConfigs(dto))){
                result.setData(true);
            }else {
                result.setData(false);
            }

        }catch (Exception ex){
            log.error("验证验货权限失败. req:[{}]", JsonHelper.toJson(whiteListrequest), ex);
            result.setCode(JdResponse.CODE_INTERNAL_ERROR);
            result.setMessage(JdResponse.MESSAGE_SERVICE_ERROR);
        }

        return result;
    }
}
