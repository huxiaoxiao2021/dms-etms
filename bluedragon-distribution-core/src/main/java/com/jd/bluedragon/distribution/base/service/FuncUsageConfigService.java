package com.jd.bluedragon.distribution.base.service;

import com.jd.bluedragon.common.dto.sysConfig.request.FuncUsageConfigRequestDto;
import com.jd.bluedragon.common.dto.sysConfig.response.FuncUsageProcessDto;
import com.jd.bluedragon.common.dto.sysConfig.response.GlobalFuncUsageControlDto;

/**
 * 天官赐福 ◎ 百无禁忌
 *
 * @Auther: 刘铎（liuduo8）
 * @Date: 2024/1/17
 * @Description:
 *
 *  获取是否允许使用配置服务，从BaseServcie中迁移原刚哥写的代码。调整位置，纯copy 。
 */
public interface FuncUsageConfigService {

    /**
     * 获取全局功能管控配置
     * @param funcUsageConfigRequestDto 请求参数
     * @return 功能可用性结果
     * @author fanggang7
     * @time 2023-03-22 19:59:20 周三
     */
    GlobalFuncUsageControlDto getGlobalFuncUsageControlConfig(FuncUsageConfigRequestDto funcUsageConfigRequestDto);

    /**
     * 根据功能编码获取功能可用性配置结果
     * @param funcUsageConfigRequestDto 请求参数
     * @return 菜单可用性结果
     * @author fanggang7
     * @time 2022-04-11 16:47:33 周一
     */
    FuncUsageProcessDto getFuncUsageConfig(FuncUsageConfigRequestDto funcUsageConfigRequestDto);
}
