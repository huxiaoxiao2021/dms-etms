package com.jd.bluedragon.core.base;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.common.annotation.CacheMethod;
import com.jd.ql.basic.dto.BaseGoodsAreaNextSiteDto;
import com.jd.ql.basic.dto.ResultDTO;
import com.jd.ql.basic.enums.ResultCodeEnum;
import com.jd.ql.basic.ws.BasicGoodsAreaWS;
import com.jd.ump.profiler.CallerInfo;
import com.jd.ump.profiler.proxy.Profiler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Objects;

/**
 * 货区编码查询包装类
 *
 * @author hujiping
 * @date 2021/11/15 3:51 下午
 */
@Service("basicGoodsAreaManager")
public class BasicGoodsAreaManagerImpl implements BasicGoodsAreaManager {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private BasicGoodsAreaWS basicGoodsAreaWS;

    @CacheMethod(key = "BasicGoodsAreaManagerImpl.getGoodsAreaNextSite-{0}-{1}",
            cacheBean = "redisCache", nullTimeout = 1000 * 60 * 10, timeout = 1000 * 60 * 10)
    @Override
    public String getGoodsAreaNextSite(Integer siteCode, Integer nextSiteCode) {
        CallerInfo callerInfo = Profiler.registerInfo("dmsWeb.jsf.BasicGoodsAreaManager.getGoodsAreaNextSite",
                Constants.UMP_APP_NAME_DMSWEB,false,true);
        try {
            if(siteCode == null || siteCode <= Constants.NUMBER_ZERO || nextSiteCode == null || nextSiteCode <= Constants.NUMBER_ZERO){
                return null;
            }
            BaseGoodsAreaNextSiteDto baseGoodsAreaNextSiteDto = new BaseGoodsAreaNextSiteDto();
            baseGoodsAreaNextSiteDto.setSiteCode(siteCode);
            baseGoodsAreaNextSiteDto.setNextSiteCode(nextSiteCode);
            ResultDTO<BaseGoodsAreaNextSiteDto> resultDTO = basicGoodsAreaWS.getGoodsAreaNextSite(baseGoodsAreaNextSiteDto);
            if(resultDTO == null || !Objects.equals(ResultCodeEnum.SUCCESS.getCode(), resultDTO.getCode()) || resultDTO.getData() == null){
                logger.warn("根据当前站点:{}和下一站点:{}查询货区编码失败!{}", siteCode, nextSiteCode, resultDTO == null ? InvokeResult.RESULT_NULL_MESSAGE : resultDTO.getMessage());
                return null;
            }
            return resultDTO.getData().getAreaNo();
        }catch (Exception e){
            logger.error("根据当前站点:{}和下一站点:{}查询货区编码异常!", siteCode, nextSiteCode, e);
            Profiler.functionError(callerInfo);
        }finally {
            Profiler.registerInfoEnd(callerInfo);
        }
        return null;
    }
}
