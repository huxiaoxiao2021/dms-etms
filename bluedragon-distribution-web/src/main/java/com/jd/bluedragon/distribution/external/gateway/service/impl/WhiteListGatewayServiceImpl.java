package com.jd.bluedragon.distribution.external.gateway.service.impl;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.common.dto.base.response.JdCResponse;
import com.jd.bluedragon.common.dto.whitelist.request.WhiteList;
import com.jd.bluedragon.distribution.api.JdResponse;
import com.jd.bluedragon.distribution.command.JdResult;
import com.jd.bluedragon.distribution.rest.pdaAuthority.PdaAuthorityResource;
import com.jd.bluedragon.external.gateway.service.WhiteListGatewayService;
import com.jd.dms.logger.annotation.BusinessLog;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

public class WhiteListGatewayServiceImpl  implements WhiteListGatewayService {

    @Autowired
    @Qualifier("pdaAuthorityResource")
    private PdaAuthorityResource pdaAuthorityResource;

    @Override
    @BusinessLog(sourceSys = 1, bizType = 500, operateType = 50011)
    @JProfiler(jKey = "DMSWEB.WhiteListGatewayServiceImpl.inspectionAuthority", jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
    public JdCResponse<Boolean> inspectionAuthority(WhiteList request){
        JdCResponse<Boolean> res=new  JdCResponse<>();
        res.toSucceed();

        if (null == request){
            res.toFail("入参不能为空");
            return res;
        }
        if(null == request.getMenuId() || request.getMenuId()<=0)
        {
            res.toFail("功能ID不能为空");
            return res;
        }
        if(null == request.getDimensionId() || request.getDimensionId()<=0)
        {
            res.toFail("维度ID不能为空");
            return res;
        }
        if(StringUtils.isEmpty(request.getErp()))
        {
            res.toFail("操作人不能为空");
            return res;
        }

        com.jd.bluedragon.distribution.whitelist.WhiteList whiteListrequest=new com.jd.bluedragon.distribution.whitelist.WhiteList();
        BeanUtils.copyProperties(request, whiteListrequest);

        JdResult<Boolean> response = pdaAuthorityResource.inspectionAuthority(whiteListrequest);
        if (JdResult.CODE_SUC.equals(response.getCode())){
            res.setMessage(response.getMessage());
            res.setData(response.getData());
        }
        else {
            res.toFail(response.getMessage());
        }

        return res;
    }
}
