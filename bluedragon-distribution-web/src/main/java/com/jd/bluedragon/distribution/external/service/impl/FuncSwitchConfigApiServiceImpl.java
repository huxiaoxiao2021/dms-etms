package com.jd.bluedragon.distribution.external.service.impl;

import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.external.domain.DmsFuncSwitchDto;
import com.jd.bluedragon.distribution.external.service.FuncSwitchConfigApiService;
import com.jd.bluedragon.distribution.funcSwitchConfig.domain.FuncSwitchConfigResponse;
import com.jd.bluedragon.distribution.funcSwitchConfig.service.FuncSwitchConfigService;
import com.jd.bluedragon.distribution.whitelist.DimensionEnum;
import com.jd.ql.dms.common.domain.JdResponse;
import com.jd.ump.profiler.CallerInfo;
import com.jd.ump.profiler.proxy.Profiler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * @Author: liming522
 * @Description: 提供给分拣机同步站点拦截状态的接口
 * @Date: create in 2020/11/24 16:03
 */
@Service("funcSwitchConfigApiService")
public class FuncSwitchConfigApiServiceImpl implements FuncSwitchConfigApiService {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private FuncSwitchConfigService funcSwitchConfigService;

    @Override
    public FuncSwitchConfigResponse<List<DmsFuncSwitchDto>>  getSiteFilterStatus(Map<String,Object> siteCodeMap) {
        CallerInfo info = Profiler.registerInfo("DMS.WEB.FuncSwitchConfigApiServiceImpl.getSiteFilterStatus", false, true);
        FuncSwitchConfigResponse<List<DmsFuncSwitchDto>> response = new FuncSwitchConfigResponse<>();
        Integer siteCode = null;
        try {

            if(siteCodeMap.get("siteCode")== null){
                response.setCode(JdResponse.CODE_SUCCESS);
                response.setMessage("参数siteCode不能为空");
            }
            siteCode  = Integer.valueOf(String.valueOf(siteCodeMap.get("siteCode")));
            response  =  funcSwitchConfigService.getSiteFilterStatus(siteCode);
        }catch (Exception e){
            log.error("站点:{}分拣机拦截状态同步接口异常",siteCode,e);
            response.setCode(JdResponse.CODE_ERROR);
            response.setMessage("分拣机拦截状态同步接口异常");
            Profiler.functionError(info);
        }finally {
            Profiler.registerInfoEnd(info);
        }
        return response;
    }

    @Override
    public InvokeResult<Boolean> hasInspectOrSendFunction(Integer createSiteId, Integer menuCode, String userErp) {
        InvokeResult<Boolean> result = new InvokeResult<>();
        try {
            // 场地维度
            int dimensionCode = DimensionEnum.SITE.getCode();
            // 查询当前扫描人所在场地是否有验货权限
            boolean flag = funcSwitchConfigService.checkIsConfiguredWithCache(menuCode, createSiteId, dimensionCode, null);
            // 如果场地没有权限，则查询个人是否配置
            if (!flag) {
                // 个人维度
                dimensionCode = DimensionEnum.PERSON.getCode();
                flag = funcSwitchConfigService.checkIsConfiguredWithCache(menuCode, createSiteId, dimensionCode, userErp);
            }
            result.setCode(InvokeResult.RESULT_SUCCESS_CODE);
            result.setData(flag);
        } catch (Exception e) {
            log.error("hasInspectOrSendFunction|判断是否有装车或卸车的大宗权限：操作站点【{}】,菜单编码【{}】,用户erp【{}】发生异常", createSiteId, menuCode, userErp, e);
            result.customMessage(InvokeResult.SERVER_ERROR_CODE, InvokeResult.SERVER_ERROR_MESSAGE);
        }
        return result;
    }
}

