package com.jd.bluedragon.external.gateway.service;

import com.jd.bluedragon.common.dto.base.request.Pager;
import com.jd.bluedragon.common.dto.base.response.JdCResponse;
import com.jd.bluedragon.common.dto.basedata.request.StreamlinedBasicSiteQuery;
import com.jd.bluedragon.common.dto.basedata.response.BaseDataDictDto;
import com.jd.bluedragon.common.dto.sysConfig.request.MenuUsageConfigRequestDto;
import com.jd.bluedragon.common.dto.sysConfig.response.MenuUsageProcessDto;
import com.jd.bluedragon.common.dto.voice.request.HintVoiceReq;
import com.jd.bluedragon.common.dto.voice.response.HintVoiceResp;
import com.jd.ql.dms.report.domain.StreamlinedBasicSite;

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
}
