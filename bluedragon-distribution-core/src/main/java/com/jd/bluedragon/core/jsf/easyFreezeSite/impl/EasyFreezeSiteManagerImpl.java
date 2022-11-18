package com.jd.bluedragon.core.jsf.easyFreezeSite.impl;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.common.dto.easyFreeze.EasyFreezeSiteDto;
import com.jd.bluedragon.core.jsf.easyFreezeSite.EasyFreezeSiteManager;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import com.jdl.basic.api.service.easyFreeze.EasyFreezeSiteJsfService;
import com.jdl.basic.common.contants.ResultCodeConstant;
import com.jdl.basic.common.utils.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @Author: chenyaguo@jd.com
 * @Date: 2022/11/16 18:09
 * @Description:
 */
@Slf4j
@Service
public class EasyFreezeSiteManagerImpl implements EasyFreezeSiteManager {

    @Autowired
    private EasyFreezeSiteJsfService easyFreezeSiteJsfService;

    @Override
    @JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWEB,jKey = "EasyFreezeSiteManagerImpl.selectOneBysiteCode",mState={JProEnum.TP,JProEnum.FunctionError})
    public EasyFreezeSiteDto selectOneBysiteCode(Integer siteCode) {
        log.info("获取单个易冻品站点配置入参-{}",siteCode);
        try{
            Result<com.jdl.basic.api.domain.easyFreeze.EasyFreezeSiteDto> result = easyFreezeSiteJsfService.selectOneBysiteCode(siteCode);
            if(ResultCodeConstant.SUCCESS == result.getCode() && null != result.getData()){
                EasyFreezeSiteDto dto = new EasyFreezeSiteDto();
                dto.setRemindStartTime(result.getData().getRemindStartTime());
                dto.setRemindEndTime(result.getData().getRemindEndTime());
                dto.setSiteCode(result.getData().getSiteCode());
                return  dto;
            }
        }catch (Exception e){
            log.error("获取单个易冻品站点配置异常 -{}",e.getMessage(),e);
        }
        return null;
    }
}
