package com.jd.bluedragon.distribution.external.gateway.service.impl;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.jd.bluedragon.Constants;
import com.jd.bluedragon.common.dto.base.request.Pager;
import com.jd.bluedragon.common.dto.base.response.JdCResponse;
import com.jd.bluedragon.common.dto.basedata.request.StreamlinedBasicSiteQuery;
import com.jd.bluedragon.common.dto.basedata.response.BaseDataDictDto;
import com.jd.bluedragon.common.dto.sysConfig.request.FuncUsageConfigRequestDto;
import com.jd.bluedragon.common.dto.sysConfig.request.MenuUsageConfigRequestDto;
import com.jd.bluedragon.common.dto.sysConfig.response.FuncUsageProcessDto;
import com.jd.bluedragon.common.dto.sysConfig.response.GlobalFuncUsageControlDto;
import com.jd.bluedragon.common.dto.sysConfig.response.MenuUsageProcessDto;
import com.jd.bluedragon.common.dto.voice.request.HintVoiceReq;
import com.jd.bluedragon.common.dto.voice.response.HintVoiceConfig;
import com.jd.bluedragon.common.dto.voice.response.HintVoiceResp;
import com.jd.bluedragon.core.base.WaybillQueryManager;
import com.jd.bluedragon.core.hint.manager.IHintApiUnwrapManager;
import com.jd.bluedragon.distribution.api.request.client.DeviceInfo;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.base.service.BaseService;
import com.jd.bluedragon.distribution.client.dto.ClientInitDataDto;
import com.jd.bluedragon.distribution.rest.base.BaseResource;
import com.jd.bluedragon.dms.utils.WaybillUtil;
import com.jd.bluedragon.external.gateway.service.BaseDataGatewayService;
import com.jd.bluedragon.utils.BeanUtils;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.bluedragon.utils.StringHelper;
import com.jd.bluedragon.utils.converter.ResultConverter;
import com.jd.dms.workbench.utils.sdk.base.Result;
import com.jd.etms.waybill.domain.Waybill;
import com.jd.ql.basic.domain.BaseDataDict;
import com.jd.ql.dms.report.domain.StreamlinedBasicSite;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import com.jdl.basic.common.utils.ObjectHelper;
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

    @Autowired
    private IHintApiUnwrapManager hintApiUnwrapManager;
    @Autowired
    private WaybillQueryManager waybillQueryManager;

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
     * 获取全局功能管控配置
     *
     * @param funcUsageConfigRequestDto 请求参数
     * @return 功能可用性结果
     * @author fanggang7
     * @time 2023-03-22 19:59:20 周三
     */
    @Override
    public JdCResponse<GlobalFuncUsageControlDto> getGlobalFuncUsageControlConfig(FuncUsageConfigRequestDto funcUsageConfigRequestDto) {
        JdCResponse<GlobalFuncUsageControlDto> response = new JdCResponse<>();
        response.toSucceed();
        try {
            final GlobalFuncUsageControlDto globalFuncUsageControlConfig = baseService.getGlobalFuncUsageControlConfig(funcUsageConfigRequestDto);
            response.setData(globalFuncUsageControlConfig);
        } catch (Exception e) {
            log.error("BaseDataGatewayServiceImpl.getGlobalFuncUsageControlConfig exception ", e);
            response.toError("接口处理异常");
        }
        return response;
    }

    /**
     * 根据功能编码获取功能可用性配置结果
     *
     * @param funcUsageConfigRequestDto 请求参数
     * @return 功能可用性结果
     * @author fanggang7
     * @time 2023-03-22 19:59:20 周三
     */
    @Override
    public JdCResponse<FuncUsageProcessDto> getFuncUsageConfig(FuncUsageConfigRequestDto funcUsageConfigRequestDto) {
        JdCResponse<FuncUsageProcessDto> response = new JdCResponse<>();
        response.toSucceed();
        try {
            final FuncUsageProcessDto funcUsageConfigDto = baseService.getFuncUsageConfig(funcUsageConfigRequestDto);
            response.setData(funcUsageConfigDto);
        } catch (Exception e) {
            log.error("BaseDataGatewayServiceImpl.getFuncUsageConfig exception ", e);
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
        Pager<StreamlinedBasicSite> pageData = new Pager<>(request.getPageNo(), request.getPageSize(), 0L);
        response.setData(pageData);
        String searchStr = request.getSearchVo().getSearchStr();
        if (StringHelper.isEmpty(searchStr) && (WaybillUtil.isPackageCode(searchStr) || WaybillUtil.isWaybillCode(searchStr))) {
            try {
                if (WaybillUtil.isPackageCode(searchStr)) {
                    searchStr = WaybillUtil.getWaybillCode(searchStr);
                }
                Waybill waybill = waybillQueryManager.queryWaybillByWaybillCode(searchStr);
                if (ObjectHelper.isEmpty(waybill)) {
                    response.toError("该包裹未获取到流向");
                    return response;
                }
                convertParam(pageData, waybill);
            } catch (Exception e) {
                log.error("BaseDataGatewayServiceImpl.selectSiteList exception ", e);
                response.toError("该包裹未获取到流向");
            }
        } else {
            try {
                request.setSearchVo(JSON.parseObject(JSON.toJSONString(request.getSearchVo()), StreamlinedBasicSiteQuery.class));
                final Result<Pager<StreamlinedBasicSite>> pagerResult = baseService.selectSiteList(request);
                if (!pagerResult.isSuccess()) {
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
                log.error("BaseDataGatewayServiceImpl.selectSiteList exception ", e);
                response.toError("接口处理异常");
            }
        }
        return response;
    }

    /**
     * 返回参数转换
     * @param pageData
     * @param waybill
     */
    private void convertParam(Pager<StreamlinedBasicSite> pageData, Waybill waybill) {
        List<StreamlinedBasicSite> StreamlinedBasicSiteList = new ArrayList<>();
        StreamlinedBasicSite streamlinedBasicSite = new StreamlinedBasicSite();
        streamlinedBasicSite.setSiteCode(waybill.getSiteId());
        streamlinedBasicSite.setSiteName(waybill.getSiteName());
        StreamlinedBasicSiteList.add(streamlinedBasicSite);
        pageData.setData(StreamlinedBasicSiteList);
    }

    @Override
    public JdCResponse<HintVoiceResp> getCommonHintVoice(HintVoiceReq hintVoiceReq) {
        JdCResponse<HintVoiceResp> jdCResponse = new JdCResponse<>();
        jdCResponse.toSucceed();
        if(hintVoiceReq == null){
            jdCResponse.toFail();
            return jdCResponse;
        }
        com.jd.dms.comp.api.hint.vo.HintVoiceResp hintVoiceResp
                = hintApiUnwrapManager.getCommonHintVoiceConfig(BeanUtils.copy(hintVoiceReq, com.jd.dms.comp.api.hint.vo.HintVoiceReq.class));
        if(hintVoiceResp == null){
            jdCResponse.toFail("未查询到通用提示音配置，请联系分拣小秘!");
            return jdCResponse;
        }
        HintVoiceResp resp = new HintVoiceResp();
        resp.setVersion(hintVoiceResp.getVersion());
        resp.setHintVoiceConfigList(CollectionUtils.isEmpty(hintVoiceResp.getHintVoiceList())
                ? Lists.<HintVoiceConfig>newArrayList() : BeanUtils.copy(hintVoiceResp.getHintVoiceList(), HintVoiceConfig.class));
        jdCResponse.setData(resp);
        return jdCResponse;
    }

    /**
     * 获取安卓初始化数据
     * @param deviceInfo 设备信息
     * @return 初始化数据
     * @author fanggang7
     * @time 2023-05-04 18:41:33 周四
     */
    @Override
    public JdCResponse<ClientInitDataDto> getAndroidInitData(DeviceInfo deviceInfo) {
        return ResultConverter.convertResultToJdcResponse(baseService.getAndroidInitData(deviceInfo));
    }
}
