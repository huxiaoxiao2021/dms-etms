package com.jd.bluedragon.distribution.external.gateway.service.impl;

import com.alibaba.fastjson.JSON;
import com.jd.bluedragon.Constants;
import com.jd.bluedragon.common.dto.base.request.Pager;
import com.jd.bluedragon.common.dto.base.response.JdCResponse;
import com.jd.bluedragon.common.dto.basedata.request.StreamlinedBasicSiteQuery;
import com.jd.bluedragon.common.dto.basedata.response.BaseDataDictDto;
import com.jd.bluedragon.common.dto.sysConfig.request.MenuUsageConfigRequestDto;
import com.jd.bluedragon.common.dto.sysConfig.response.MenuUsageProcessDto;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.base.service.BaseService;
import com.jd.bluedragon.distribution.rest.base.BaseResource;
import com.jd.bluedragon.external.gateway.service.BaseDataGatewayService;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.dms.workbench.utils.sdk.base.Result;
import com.jd.ql.basic.domain.BaseDataDict;
import com.jd.ql.dms.report.domain.StreamlinedBasicSite;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * @author : xumigen
 * @date : 2019/9/10
 */
public class BaseDataGatewayServiceImpl implements BaseDataGatewayService {

    @Resource
    private BaseResource baseResource;

    private Logger log = Logger.getLogger(BaseDataGatewayServiceImpl.class);

    @Autowired
    @Qualifier("baseService")
    private BaseService baseService;

    @Override
    @JProfiler(jKey = "DMSWEB.BaseDataGatewayServiceImpl.getBaseDictionaryTree",jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
    public JdCResponse<List<BaseDataDictDto>> getBaseDictionaryTree(int typeGroup){
        InvokeResult<List<BaseDataDict>> invokeResult = baseResource.getBaseDictionaryTree(typeGroup);
        JdCResponse<List<BaseDataDictDto>> jdCResponse = new JdCResponse<>();
        jdCResponse.toSucceed();
        if(InvokeResult.RESULT_SUCCESS_CODE != invokeResult.getCode()){
            jdCResponse.toError(invokeResult.getMessage());
            return jdCResponse;
        }
        List<BaseDataDictDto> dataDictDtos = new ArrayList<>();
        if(CollectionUtils.isNotEmpty(invokeResult.getData())){
            for(BaseDataDict item : invokeResult.getData()){
                BaseDataDictDto dto = new BaseDataDictDto();
                dto.setId(item.getId());
                dto.setTypeCode(item.getTypeCode());
                dto.setTypeGroup(item.getTypeGroup());
                dto.setTypeName(item.getTypeName());
                dataDictDtos.add(dto);
            }
        }
        jdCResponse.setData(dataDictDtos);
        return jdCResponse;
    }

    @Override
    public JdCResponse<List<BaseDataDictDto>> getBaseDictByTypeGroups(List<Integer> typeGroups) {
        JdCResponse<List<BaseDataDictDto>> response = new JdCResponse<>();
        if (CollectionUtils.isEmpty(typeGroups)) {
            response.toFail("参数错误");
            return response;
        }
        try {
            List<BaseDataDictDto> result = new ArrayList<>();
            for (Integer typeGroup : typeGroups) {
                List<BaseDataDictDto> dataDictList = this.beanHandler(baseService.getBaseDictionaryTree(typeGroup));
                if (dataDictList != null) {
                    result.addAll(dataDictList);
                }
            }
            response.toSucceed();
            response.setData(result);
        } catch (Exception e) {
            log.error("[网关接口]查询基础字典信息时发生异常，request:" + JsonHelper.toJson(typeGroups), e);
            response.toError("[网关接口]查询基础字典信息时发生异常");
        }
        if(log.isDebugEnabled()) {
        	log.debug("异常上报-获取异常原因getBaseDictByTypeGroups:请求typeGroups="+JsonHelper.toJson(typeGroups)+"，返回结果："+JsonHelper.toJson(response));
        }
        return response;
    }

    private List<BaseDataDictDto> beanHandler(List<BaseDataDict> baseDataDictList) {
        if (baseDataDictList != null && baseDataDictList.size() > 0) {
            List<BaseDataDictDto> result = new ArrayList<>(baseDataDictList.size());
            for (BaseDataDict baseDataDict : baseDataDictList) {
                BaseDataDictDto dataDict = new BaseDataDictDto();
                dataDict.setId(baseDataDict.getId());
                dataDict.setTypeCode(baseDataDict.getTypeCode());
                dataDict.setTypeName(baseDataDict.getTypeName());
                dataDict.setTypeGroup(baseDataDict.getTypeGroup());
                dataDict.setParentId(baseDataDict.getParentId());
                result.add(dataDict);
            }
            return result;
        }
        return null;
    }

    /**
     * 安卓根据菜单编码获取菜单可用性结果
     * @param menuUsageConfigRequestDto 请求参数
     * @return 菜单可用性结果
     * @author fanggang7
     * @time 2022-04-11 16:47:33 周一
     */
    @Override
    public JdCResponse<MenuUsageProcessDto> getMenuUsageConfig(MenuUsageConfigRequestDto menuUsageConfigRequestDto) {
        JdCResponse<MenuUsageProcessDto> response = new JdCResponse<>();
        response.toSucceed();
        try {
            final MenuUsageProcessDto menuUsageProcessDto = baseService.getClientMenuUsageConfig(menuUsageConfigRequestDto);
            response.setData(menuUsageProcessDto);
        } catch (Exception e) {
            log.error("BaseDataGatewayServiceImpl.getMenuUsageProcessByMenuCode exception ", e);
            response.toError("接口处理异常");
        }
        return response;
    }

    /**
     * 场地列表
     *
     * @param request 请求参数
     * @return 返回结果
     * @author fanggang7
     * @time 2022-10-11 14:59:04 周二
     */
    @Override
    public JdCResponse<Pager<StreamlinedBasicSite>> selectSiteList(Pager<StreamlinedBasicSiteQuery> request) {
        JdCResponse<Pager<StreamlinedBasicSite>> response = new JdCResponse<>();
        response.toSucceed();
        Pager<StreamlinedBasicSite> pageData = new Pager<>();
        pageData.setPageNo(request.getPageNo());
        pageData.setPageSize(request.getPageSize());
        pageData.setTotal(0L);
        response.setData(pageData);
        try {
            request.setSearchVo(JSON.parseObject(JSON.toJSONString(request.getSearchVo()), StreamlinedBasicSiteQuery.class));
            final Result<Pager<StreamlinedBasicSite>> pagerResult = baseService.selectSiteList(request);
            if(!pagerResult.isSuccess()){
                log.warn("BaseService.selectSiteList error " + JsonHelper.toJson(pagerResult));
                response.toFail("查询站点信息异常");
                return response;
            }
            if (pagerResult.getData() != null) {
                final Pager<StreamlinedBasicSite> queryPageData = pagerResult.getData();
                pageData.setData(queryPageData.getData());
                pageData.setTotal(queryPageData.getTotal());
            }
        } catch (Exception e) {
            log.error("BaseDataGatewayServiceImpl.getMenuUsageProcessByMenuCode exception ", e);
            response.toError("接口处理异常");
        }
        return response;
    }
}
