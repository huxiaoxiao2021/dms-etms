package com.jd.bluedragon.distribution.box.service;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.core.base.BaseMajorManager;
import com.jd.bluedragon.distribution.api.Response;
import com.jd.bluedragon.distribution.box.domain.Box;
import com.jd.bluedragon.distribution.third.domain.ThirdBoxDetail;
import com.jd.bluedragon.distribution.third.service.ThirdBoxDetailService;
import com.jd.bluedragon.dms.utils.BusinessUtil;
import com.jd.ql.basic.dto.BaseStaffSiteOrgDto;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

/**
 * 箱号查询服务
 * @author : xumigen
 * @date : 2020/7/2
 */
@Service("dmsBoxQueryService")
public class DmsBoxQueryServiceImpl implements DmsBoxQueryService{

    @Autowired
    private BoxService boxService;
    @Autowired
    private BaseMajorManager baseMajorManager;

    @Autowired
    private ThirdBoxDetailService thirdBoxDetailService;

    @Override
    @JProfiler(jKey = "DMSWEB.DmsBoxQueryServiceImpl.isEconomicNetBox",jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP})
    public Response<Boolean> isEconomicNetBox(String boxCode) {
        Response<Boolean> response = new Response<>();
        response.toSucceed();
        if(StringUtils.isEmpty(boxCode)){
            response.toWarn("箱号不能为空");
            return response;
        }
        if(!BusinessUtil.isBoxcode(boxCode)){
            response.toWarn(String.format("【%s】不符合箱号编码规则",boxCode));
            return response;
        }

        Box box = boxService.findBoxByCode(boxCode);
        if(box == null){
            response.toError("箱号不存在");
            return response;
        }

        BaseStaffSiteOrgDto startSite = baseMajorManager.getBaseSiteBySiteId(box.getCreateSiteCode());
        if(startSite == null){
            response.toError("箱号始发站点不存在");
            return response;
        }
        //校验始发为经济网站点
        if(Objects.equals(Constants.THIRD_ENET_SITE_TYPE,startSite.getSiteType())){
            response.setData(Boolean.TRUE);
            return response;
        }
        response.setData(Boolean.FALSE);
        response.setMessage(String.format("箱号始发网点【%s】不是众邮类型网点！",startSite.getSiteName()));
        return response;
    }

    @Override
    @JProfiler(jKey = "DMSWEB.DmsBoxQueryServiceImpl.isEconomicNetBoxAndNotBoundingPackage",jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP})
    public Response<Boolean> isEconomicNetBoxAndNotBoundingPackage(String boxCode) {
        Response<Boolean> response = new Response<>();
        response.toSucceed();
        if(StringUtils.isEmpty(boxCode)){
            response.toWarn("箱号不能为空");
            return response;
        }
        if(!BusinessUtil.isBoxcode(boxCode)){
            response.toWarn(String.format("【%s】不符合箱号编码规则",boxCode));
            return response;
        }

        Box box = boxService.findBoxByCode(boxCode);
        if(box == null){
            response.toError("箱号不存在");
            return response;
        }

        BaseStaffSiteOrgDto startSite = baseMajorManager.getBaseSiteBySiteId(box.getCreateSiteCode());
        if(startSite == null){
            response.toError("箱号始发站点不存在");
            return response;
        }

        List<ThirdBoxDetail> thirdBoxDetails = thirdBoxDetailService.queryByBoxCode(Constants.TENANT_CODE_ECONOMIC, startSite.getSiteCode(), box.getCode());
        if(!CollectionUtils.isEmpty(thirdBoxDetails)){
            response.setData(Boolean.FALSE);
            return response;
        }

        //校验始发为经济网站点 且 没有明细
        if(Objects.equals(Constants.THIRD_ENET_SITE_TYPE,startSite.getSiteType()) && CollectionUtils.isEmpty(thirdBoxDetails)){
            response.setData(Boolean.TRUE);
            return response;
        }
        response.setData(Boolean.FALSE);
        response.setMessage(String.format("箱号始发网点【%s】不是众邮类型网点！",startSite.getSiteName()));
        return response;
    }


}
