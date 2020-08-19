package com.jd.bluedragon.distribution.external.gateway.service.impl;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.common.dto.base.response.JdCResponse;
import com.jd.bluedragon.common.dto.search.request.PackBoxRequest;
import com.jd.bluedragon.common.dto.search.response.PackBoxResponse;
import com.jd.bluedragon.distribution.api.JdResponse;
import com.jd.bluedragon.distribution.api.response.BoxPackResponse;
import com.jd.bluedragon.distribution.rest.box.BoxPackResource;
import com.jd.bluedragon.external.gateway.service.SearchGateWayService;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * 安卓查询接口服务实现
 */
public class SearchGateWayServiceImpl implements SearchGateWayService {

    private final Logger logger = LoggerFactory.getLogger(SearchGateWayServiceImpl.class);

    @Autowired
    BoxPackResource boxPackResource;

    /**
     * 根据运单号或包裹号查询箱号包裹信息
     * @param request
     * @return
     */
    @Override
    @JProfiler(jKey = "DMSWEB.SearchGateWayServiceImpl.getBoxPackList",jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
    public JdCResponse<List<PackBoxResponse>> getBoxPackList(PackBoxRequest request){
        JdCResponse<List<PackBoxResponse>> res=new JdCResponse<>();
        res.toSucceed();

        if(null==request){
            res.toFail("入参为空");
            return res;
        }

        if(null==request.getCreateSiteCode()){
            res.toFail("操作站点ID为空");
            return res;
        }

        if(StringUtils.isBlank(request.getWaybillNoOrPackNo())){
            res.toFail("包裹或者运单为空");
            return res;
        }

        if(null==request.getType()){
            res.toFail("分拣类型为空");
            return res;
        }

        BoxPackResponse response=boxPackResource.getBoxPackList(request.getCreateSiteCode(),request.getWaybillNoOrPackNo(),request.getType());
        if(JdResponse.CODE_OK.equals(response.getCode())){
            res.toSucceed(response.getMessage());
            String datastr= JsonHelper.toJson(response.getBoxPackList());
            res.setData(JsonHelper.jsonToList(datastr,PackBoxResponse.class));
        }else {
            res.toFail(response.getMessage());
        }

        return res;
    }

}
