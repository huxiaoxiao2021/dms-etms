package com.jd.bluedragon.external.gateway.service;

import com.jd.bluedragon.common.dto.base.request.Pager;
import com.jd.bluedragon.common.dto.base.response.JdCResponse;
import com.jd.bluedragon.common.dto.basedata.request.GetFlowDirectionQuery;
import com.jd.bluedragon.common.dto.basedata.request.StreamlinedBasicSiteQuery;
import com.jd.bluedragon.common.dto.basedata.response.BaseDataDictDto;
import com.jd.bluedragon.common.dto.basedata.response.StreamlinedBasicSite;
import com.jd.bluedragon.common.dto.sysConfig.request.FuncUsageConfigRequestDto;
import com.jd.bluedragon.common.dto.sysConfig.request.MenuUsageConfigRequestDto;
import com.jd.bluedragon.common.dto.sysConfig.response.FuncUsageProcessDto;
import com.jd.bluedragon.common.dto.sysConfig.response.GlobalFuncUsageControlDto;
import com.jd.bluedragon.common.dto.sysConfig.response.MenuUsageProcessDto;
import com.jd.bluedragon.common.dto.voice.request.HintVoiceReq;
import com.jd.bluedragon.common.dto.voice.response.HintVoiceResp;
import com.jd.bluedragon.distribution.api.request.client.DeviceInfo;
import com.jd.bluedragon.distribution.base.dto.BaseStaffData;
import com.jd.bluedragon.distribution.client.dto.ClientInitDataDto;

import java.util.List;

/**
 * 基础信息发布物流网关
 * 发布到物流网关 由安卓调用
 * @author : xumigen
 * @date : 2019/9/10
 */
public interface BaseDataGatewayService {

    JdCResponse<List<BaseDataDictDto>> getBaseDictionaryTree(int typeGroup);

    /**
     * 根据类型分组获取数据字典信息
     *
     * @param typeGroups
     * @return
     */
    JdCResponse<List<BaseDataDictDto>> getBaseDictByTypeGroups(List<Integer> typeGroups);

    /**
     * 安卓根据菜单编码获取菜单可用性结果
     * @param menuUsageConfigRequestDto 请求参数
     * @return 菜单可用性结果
     * @author fanggang7
     * @time 2022-04-11 16:47:33 周一
     */
    JdCResponse<MenuUsageProcessDto> getMenuUsageConfig(MenuUsageConfigRequestDto menuUsageConfigRequestDto);

    /**
     * 获取全局功能管控配置
     * @param funcUsageConfigRequestDto 请求参数
     * @return 功能可用性结果
     * @author fanggang7
     * @time 2023-03-22 19:59:20 周三
     */
    JdCResponse<GlobalFuncUsageControlDto> getGlobalFuncUsageControlConfig(FuncUsageConfigRequestDto funcUsageConfigRequestDto);

    /**
     * 根据功能编码获取功能可用性配置结果
     * @param funcUsageConfigRequestDto 请求参数
     * @return 功能可用性结果
     * @author fanggang7
     * @time 2023-03-22 19:59:20 周三
     */
    JdCResponse<FuncUsageProcessDto> getFuncUsageConfig(FuncUsageConfigRequestDto funcUsageConfigRequestDto);

    /**
     * 查询场地列表
     * @param request 请求参数
     * @return 返回结果
     * @author fanggang7
     * @time 2022-10-11 14:59:04 周二
     */
    JdCResponse<Pager<StreamlinedBasicSite>> selectSiteList(Pager<StreamlinedBasicSiteQuery> request);

    /**
     * 获取通用提示音
     *
     * @param hintVoiceReq
     * @return
     */
    JdCResponse<HintVoiceResp> getCommonHintVoice(HintVoiceReq hintVoiceReq);

    /**
     * 获取安卓初始化数据
     * @param deviceInfo 设备信息
     * @return 初始化数据
     * @author fanggang7
     * @time 2023-05-04 18:41:33 周四
     */
    JdCResponse<ClientInitDataDto> getAndroidInitData(DeviceInfo deviceInfo);

    /**
     * 获取流向
     *
     * @param request 请求参数
     * @return 返回结果
     */
    JdCResponse<Pager<StreamlinedBasicSite>> getFlowDirection(Pager<GetFlowDirectionQuery> request);

    /**
     * 获取用户信息
     * @param userErpOrIdCard erp或者身份证号
     * @return 用户数据
     * @author fanggang7
     * @time 2023-12-26 18:41:33 周四
     */
    JdCResponse<BaseStaffData> getBaseStaffDataByErpOrIdCard(String userErpOrIdCard);
}
