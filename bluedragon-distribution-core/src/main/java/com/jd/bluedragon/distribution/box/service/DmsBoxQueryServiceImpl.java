package com.jd.bluedragon.distribution.box.service;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.core.base.BaseMajorManager;
import com.jd.bluedragon.distribution.api.Response;
import com.jd.bluedragon.distribution.api.request.box.BoxReq;
import com.jd.bluedragon.distribution.api.request.box.BoxTypeReq;
import com.jd.bluedragon.distribution.api.request.box.CreateBoxReq;
import com.jd.bluedragon.distribution.api.response.box.BoxDto;
import com.jd.bluedragon.distribution.api.response.box.BoxTypeDto;
import com.jd.bluedragon.distribution.api.response.box.CreateBoxInfo;
import com.jd.bluedragon.distribution.box.domain.Box;
import com.jd.bluedragon.distribution.jy.dto.JySelectOption;
import com.jd.bluedragon.dms.utils.BusinessUtil;
import com.jd.common.annotation.CacheMethod;
import com.jd.dms.java.utils.sdk.base.Result;
import com.jd.etms.framework.utils.cache.annotation.Cache;
import com.jd.ql.basic.dto.BaseStaffSiteOrgDto;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
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

    @Override
    @JProfiler(jKey = "DMSWEB.DmsBoxQueryServiceImpl.isEconomicNetBox",jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP})
    @CacheMethod(key="DmsBoxQueryServiceImpl.isEconomicNetBox-{0}", cacheBean="redisCache", timeout = 1000 * 60 * 5)
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

    /**
     * 根据箱号查询箱信息
     * @param boxCode
     * @return
     */
    @Override
    public BoxDto getBoxByBoxCode(String boxCode) {
        Box box = boxService.findBoxByCode(boxCode);
        if (null == box){
            return null;
        }
        BoxDto result = new BoxDto();
        BeanUtils.copyProperties(box,result);
        result.setSubType(box.getBoxSubType());
        return result;
    }

    /**
     * 更新箱状态；状态有：可用，不可用
     * @param boxReq
     * @return
     */
    @Override
    public Boolean updateBoxStatus(BoxReq boxReq) {
        return boxService.updateBoxStatus(boxReq);
    }

    /**
     * 查询箱类型
     *
     * @param boxTypeReq 查询箱类型入参
     * @return 箱号类型列表
     * @author fanggang7
     * @time 2023-10-24 14:14:24 周二
     */
    @Override
    public Result<List<BoxTypeDto>> getBoxTypeList(BoxTypeReq boxTypeReq) {
        return boxService.getBoxTypeList(boxTypeReq);
    }
}
