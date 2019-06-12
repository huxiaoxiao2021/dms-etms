package com.jd.bluedragon.distribution.external.gateway.service.impl;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.distribution.api.request.SortingRequest;
import com.jd.bluedragon.distribution.api.response.SortingResponse;
import com.jd.bluedragon.distribution.external.gateway.dto.request.SortingCancelRequest;
import com.jd.bluedragon.distribution.external.gateway.service.SortingGatewayService;
import com.jd.bluedragon.distribution.rest.sorting.SortingResource;
import com.jd.bluedragon.dms.utils.BusinessUtil;
import com.jd.ql.basic.util.DateUtil;
import com.jd.ql.dms.common.domain.JdResponse;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.util.Objects;

/**
 * @author : xumigen
 * @date : 2019/6/12
 */
public class SortingGatewayServiceImpl implements SortingGatewayService {

    @Autowired
    @Qualifier("sortingResource")
    private SortingResource sortingResource;

    @Override
    @JProfiler(jKey = "DMSWEB.SortingGatewayServiceImpl.sortingCancel", mState = JProEnum.TP, jAppName = Constants.UMP_APP_NAME_DMSWEB)
    public JdResponse sortingCancel(SortingCancelRequest request) {
        JdResponse response = new JdResponse();
        response.toFail("操作失败请联系IT");
        if(request.getUser() == null ){
            response.toFail("参数user不能为空");
            return response;
        }
        if(request.getCurrentOperate() == null){
            response.toFail("参数currentOperate不能为空");
            return response;
        }
        if(StringUtils.isEmpty(request.getPackageCode())){
            response.toFail("参数packageCode不能为空");
            return response;
        }
        SortingRequest params = new SortingRequest();
        params.setPackageCode(request.getPackageCode());
        params.setUserCode(request.getUser().getUserCode());
        params.setUserName(request.getUser().getUserName());
        params.setBusinessType(request.getBusinessType());
        params.setOperateTime(DateUtil.format(request.getCurrentOperate().getOperateTime(),DateUtil.FORMAT_DATE_TIME));
        params.setSiteCode(request.getCurrentOperate().getSiteCode());
        params.setSiteName(request.getCurrentOperate().getSiteName());
        if(BusinessUtil.isBoxcode(request.getPackageCode())){
            params.setBoxCode(request.getPackageCode());
        }
        SortingResponse sortingResponse = sortingResource.cancelPackage(params);
        if(Objects.equals(sortingResponse.getCode(),SortingResponse.CODE_OK)){
            response.toSucceed("操作成功");
            return response;
        }
        return response;
    }
}
