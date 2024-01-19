package com.jd.bluedragon.distribution.base.service.impl;

import com.alibaba.fastjson.JSON;
import com.jd.bluedragon.Constants;
import com.jd.bluedragon.common.dto.base.request.OperateUser;
import com.jd.bluedragon.common.dto.sysConfig.request.FuncUsageConfigRequestDto;
import com.jd.bluedragon.common.dto.sysConfig.response.FuncUsageConditionConfigDto;
import com.jd.bluedragon.common.dto.sysConfig.response.FuncUsageConfigDto;
import com.jd.bluedragon.common.dto.sysConfig.response.FuncUsageProcessDto;
import com.jd.bluedragon.common.dto.sysConfig.response.GlobalFuncUsageControlDto;
import com.jd.bluedragon.core.base.BaseMajorManager;
import com.jd.bluedragon.distribution.base.domain.SysConfig;
import com.jd.bluedragon.distribution.base.service.FuncUsageConfigService;
import com.jd.bluedragon.distribution.base.service.SysConfigService;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.dms.workbench.utils.sdk.base.Result;
import com.jd.ql.basic.dto.BaseSiteInfoDto;
import com.jd.ql.basic.util.DateUtil;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * 天官赐福 ◎ 百无禁忌
 *
 * @Auther: 刘铎（liuduo8）
 * @Date: 2024/1/17
 * @Description:
 *  获取是否允许使用配置服务，为了解决父类依赖子类方法问题从BaseServcie中迁移原刚哥写的代码。调整位置，纯copy 。
 */
@Service("funcUsageConfigService")
public class FuncUsageConfigServiceImpl implements FuncUsageConfigService {

    private static final Logger log = LoggerFactory.getLogger(FuncUsageConfigServiceImpl.class);

    @Autowired
    SysConfigService sysConfigService;

    @Autowired
    private BaseMajorManager baseMajorManager;

    /**
     * 获取全局功能管控配置
     *
     * @param funcUsageConfigRequestDto 请求参数
     * @return 功能可用性结果
     * @author fanggang7
     * @time 2023-03-22 19:59:20 周三
     */
    @Override
    public GlobalFuncUsageControlDto getGlobalFuncUsageControlConfig(FuncUsageConfigRequestDto funcUsageConfigRequestDto) {
        log.info("BaseServiceImpl.getFuncUsageConfig param {}", JsonHelper.toJson(funcUsageConfigRequestDto));
        final SysConfig sysConfig = sysConfigService.findConfigContentByConfigName(Constants.SYS_CONFIG_GLOBAL_FUNC_USAGE_CONTROL + funcUsageConfigRequestDto.getSystemCode());
        // 如果配置都为空，则直接返回空
        if (sysConfig == null) {
            return null;
        }

        GlobalFuncUsageControlDto globalFuncUsageControlDto = new GlobalFuncUsageControlDto();
        final List<String> funcCodes = JSON.parseArray(sysConfig.getConfigContent(), String.class);
        globalFuncUsageControlDto.setFuncCodes(funcCodes);

        return globalFuncUsageControlDto;
    }

    /**
     * 根据功能编码获取功能可用性配置结果
     *
     * @param funcUsageConfigRequestDto 请求参数
     * @return 菜单可用性结果
     * @author fanggang7
     * @time 2022-04-11 16:47:33 周一
     */
    @Override
    public FuncUsageProcessDto getFuncUsageConfig(FuncUsageConfigRequestDto funcUsageConfigRequestDto) {
        log.info("BaseServiceImpl.getFuncUsageConfig param {}", JsonHelper.toJson(funcUsageConfigRequestDto));

        FuncUsageProcessDto funcUsageProcessDto = null;

        final OperateUser operateUser = funcUsageConfigRequestDto.getOperateUser();
        final int siteCode = operateUser.getSiteCode();

        final BaseSiteInfoDto siteInfo = baseMajorManager.getBaseSiteInfoBySiteId(siteCode);
        if(siteInfo == null){
            return null;
        }

        // 人员白名单
        final Result<FuncUsageConfigDto> whiteFuncUsageByCodeConfig4UserErpResult = getWhiteFuncUsageByCodeConfig4UserErp(funcUsageConfigRequestDto);
        if (whiteFuncUsageByCodeConfig4UserErpResult != null) {
            if(whiteFuncUsageByCodeConfig4UserErpResult.isSuccess()){
                return null;
            } else {
                if (whiteFuncUsageByCodeConfig4UserErpResult.getData() != null) {
                    return whiteFuncUsageByCodeConfig4UserErpResult.getData().getProcess();
                }
            }
        }

        // 场地ID白名单
        final Result<FuncUsageConfigDto> whiteFuncUsageByCodeConfig4SpecificListResult = getWhiteFuncUsageByCodeConfig4SpecificList(funcUsageConfigRequestDto);
        if (whiteFuncUsageByCodeConfig4SpecificListResult != null) {
            if(whiteFuncUsageByCodeConfig4SpecificListResult.isSuccess()){
                return null;
            } else {
                if (whiteFuncUsageByCodeConfig4SpecificListResult.getData() != null) {
                    return whiteFuncUsageByCodeConfig4SpecificListResult.getData().getProcess();
                }
            }
        }

        // 场地类型白名单
        final Result<FuncUsageConfigDto> whiteFuncUsageByCodeConfig4SiteTypeResult = getWhiteFuncUsageByCodeConfig4SiteType(funcUsageConfigRequestDto, siteInfo);
        if (whiteFuncUsageByCodeConfig4SiteTypeResult != null) {
            if(whiteFuncUsageByCodeConfig4SiteTypeResult.isSuccess()){
                return null;
            } else {
                if (whiteFuncUsageByCodeConfig4SiteTypeResult.getData() != null) {
                    return whiteFuncUsageByCodeConfig4SiteTypeResult.getData().getProcess();
                }
            }
        }

        // 人员黑名单
        final FuncUsageProcessDto clientMenuUsageByCodeConfigByUserErp = getFuncUsageByCodeConfig4UserErp(funcUsageConfigRequestDto);
        if (clientMenuUsageByCodeConfigByUserErp != null) {
            return clientMenuUsageByCodeConfigByUserErp;
        }

        // 场地ID黑名单
        final FuncUsageProcessDto clientMenuUsageByCodeConfig = getFuncUsageByCodeConfig4SpecificList(funcUsageConfigRequestDto);
        if (clientMenuUsageByCodeConfig != null) {
            return clientMenuUsageByCodeConfig;
        }

        // 场地类型黑名单
        final FuncUsageProcessDto clientMenuUsageByCodeConfigBySiteType = getFuncUsageByCodeConfig4SiteType(funcUsageConfigRequestDto, siteInfo);
        if (clientMenuUsageByCodeConfigBySiteType != null) {
            return clientMenuUsageByCodeConfigBySiteType;
        }


        return funcUsageProcessDto;
    }



    /**
     * 1. 未找到白名单数据 继续执行 false data = null
     * 2. 有白名单数据
     *     在白名单内
     *         仅允许白名单 允许执行 true
     *         非仅允许白名单 允许执行 true
     *     不在白名单内
     *         仅允许白名单 拦截 false data != null
     *         非仅允许白名单 继续执行 false data = null
     */
    private Result<FuncUsageConfigDto> getWhiteFuncUsageByCodeConfig4UserErp(FuncUsageConfigRequestDto funcUsageConfigRequestDto) {
        Result<FuncUsageConfigDto> result = Result.success();
        if (StringUtils.isEmpty(funcUsageConfigRequestDto.getOperateUser().getUserCode())) {
            return result.toFail();
        }

        final SysConfig sysConfig = sysConfigService.findConfigContentByConfigName(Constants.SYS_CONFIG_FUNC_USAGE_WHITE_BY_ERP + funcUsageConfigRequestDto.getFuncCode());
        if (sysConfig == null) {
            return result.toFail("无白名单");
        }

        final List<FuncUsageConfigDto> funcUsageConfigDtos = JSON.parseArray(sysConfig.getConfigContent(), FuncUsageConfigDto.class);

        final long currentTimeMillis = System.currentTimeMillis();
        for (FuncUsageConfigDto funcUsageConfigDto : funcUsageConfigDtos) {
            final FuncUsageConditionConfigDto conditionConfig = funcUsageConfigDto.getCondition();
            if (conditionConfig == null) {
                // 仅允许白名单
                if(Objects.equals(funcUsageConfigDto.getOnlyAllowWhiteList(), Boolean.TRUE)){
                    return result.setData(funcUsageConfigDto).toFail();
                } else {
                    continue;
                }
            }

            if (CollectionUtils.isNotEmpty(conditionConfig.getUserErps()) && conditionConfig.getUserErps().contains(funcUsageConfigRequestDto.getOperateUser().getUserCode())
                    && this.checkEffectiveTimeIsEffective(funcUsageConfigDto, currentTimeMillis)) {
                return result.toSuccess();
            }
            // 仅允许白名单
            if(Objects.equals(funcUsageConfigDto.getOnlyAllowWhiteList(), Boolean.TRUE)){
                return result.setData(funcUsageConfigDto).toFail();
            }
        }

        return result.toFail();
    }

    /**
     * 1. 未找到白名单数据 继续执行 false data = null
     * 2. 有白名单数据
     *     在白名单内
     *         仅允许白名单 允许执行 true
     *         非仅允许白名单 允许执行 true
     *     不在白名单内
     *         仅允许白名单 拦截 false data != null
     *         非仅允许白名单 继续执行 false data = null
     */
    private Result<FuncUsageConfigDto> getWhiteFuncUsageByCodeConfig4SpecificList(FuncUsageConfigRequestDto funcUsageConfigRequestDto) {
        Result<FuncUsageConfigDto> result = Result.success();

        final SysConfig sysConfig = sysConfigService.findConfigContentByConfigName(Constants.SYS_CONFIG_FUNC_USAGE_WHITE_BY_SITE_CODE + funcUsageConfigRequestDto.getFuncCode());
        if (sysConfig == null) {
            return result.toFail("无白名单");
        }

        final List<FuncUsageConfigDto> funcUsageConfigDtos = JSON.parseArray(sysConfig.getConfigContent(), FuncUsageConfigDto.class);

        final long currentTimeMillis = System.currentTimeMillis();
        for (FuncUsageConfigDto funcUsageConfigDto : funcUsageConfigDtos) {
            final FuncUsageConditionConfigDto conditionConfig = funcUsageConfigDto.getCondition();
            if (conditionConfig == null) {
                // 仅允许白名单
                if(Objects.equals(funcUsageConfigDto.getOnlyAllowWhiteList(), Boolean.TRUE)){
                    return result.setData(funcUsageConfigDto).toFail();
                } else {
                    continue;
                }
            }

            if((CollectionUtils.isEmpty(conditionConfig.getSiteCodes()) || conditionConfig.getSiteCodes().contains(funcUsageConfigRequestDto.getOperateUser().getSiteCode())
                    && this.checkEffectiveTimeIsEffective(funcUsageConfigDto, currentTimeMillis))){
                return result.toSuccess();
            }
            // 仅允许白名单
            if(Objects.equals(funcUsageConfigDto.getOnlyAllowWhiteList(), Boolean.TRUE)){
                return result.setData(funcUsageConfigDto).toFail();
            }
        }

        return result.toFail();
    }

    /**
     * 1. 未找到白名单数据 继续执行 false data = null
     * 2. 有白名单数据
     *     在白名单内
     *         仅允许白名单 允许执行 true
     *         非仅允许白名单 允许执行 true
     *     不在白名单内
     *         仅允许白名单 拦截 false data != null
     *         非仅允许白名单 继续执行 false data = null
     */
    private Result<FuncUsageConfigDto> getWhiteFuncUsageByCodeConfig4SiteType(FuncUsageConfigRequestDto funcUsageConfigRequestDto, BaseSiteInfoDto siteInfo) {
        Result<FuncUsageConfigDto> result = Result.success();

        final SysConfig sysConfig = sysConfigService.findConfigContentByConfigName(Constants.SYS_CONFIG_FUNC_USAGE_WHITE_BY_SITE_TYPE + funcUsageConfigRequestDto.getFuncCode());
        if (sysConfig == null) {
            return result.toFail("无白名单");
        }

        final List<FuncUsageConfigDto> funcUsageConfigDtos = JSON.parseArray(sysConfig.getConfigContent(), FuncUsageConfigDto.class);

        final long currentTimeMillis = System.currentTimeMillis();
        for (FuncUsageConfigDto funcUsageConfigDto : funcUsageConfigDtos) {
            final FuncUsageConditionConfigDto conditionConfig = funcUsageConfigDto.getCondition();
            if (conditionConfig == null) {
                // 仅允许白名单
                if(Objects.equals(funcUsageConfigDto.getOnlyAllowWhiteList(), Boolean.TRUE)){
                    return result.setData(funcUsageConfigDto).toFail();
                } else {
                    continue;
                }
            }

            if(CollectionUtils.isEmpty(conditionConfig.getSiteType()) && CollectionUtils.isEmpty(conditionConfig.getSiteSubType())
                    && CollectionUtils.isEmpty(conditionConfig.getSiteSortType()) && CollectionUtils.isEmpty(conditionConfig.getSiteSortSubType()) && CollectionUtils.isEmpty(conditionConfig.getSiteSortThirdType())
                    && this.checkEffectiveTimeIsEffective(funcUsageConfigDto, currentTimeMillis)){
                return result.toSuccess();
            }
            if((CollectionUtils.isEmpty(conditionConfig.getSiteType()) || (siteInfo.getSiteType() != null && conditionConfig.getSiteType().contains(siteInfo.getSiteType())))
                    && (CollectionUtils.isEmpty(conditionConfig.getSiteSubType()) || (siteInfo.getSubType() != null && conditionConfig.getSiteSubType().contains(siteInfo.getSubType())))
                    && (CollectionUtils.isEmpty(conditionConfig.getSiteThirdType()) || (siteInfo.getThirdType() != null && conditionConfig.getSiteThirdType().contains(siteInfo.getThirdType())))
                    && (CollectionUtils.isEmpty(conditionConfig.getSiteSortType()) || (siteInfo.getSortType() != null && conditionConfig.getSiteSortType().contains(siteInfo.getSortType())))
                    && (CollectionUtils.isEmpty(conditionConfig.getSiteSortSubType()) || (siteInfo.getSortSubType() != null && conditionConfig.getSiteSortSubType().contains(siteInfo.getSortSubType())))
                    && (CollectionUtils.isEmpty(conditionConfig.getSiteSortThirdType()) || (siteInfo.getSortThirdType() != null && conditionConfig.getSiteSortThirdType().contains(siteInfo.getSortThirdType())))
                    && this.checkEffectiveTimeIsEffective(funcUsageConfigDto, currentTimeMillis)){
                return result.toSuccess();
            }
            // 仅允许白名单
            if(Objects.equals(funcUsageConfigDto.getOnlyAllowWhiteList(), Boolean.TRUE)){
                return result.setData(funcUsageConfigDto).toFail();
            }
        }

        return result.toFail();
    }

    /**
     * 检查人员黑名单
     */
    private FuncUsageProcessDto getFuncUsageByCodeConfig4UserErp(FuncUsageConfigRequestDto funcUsageConfigRequestDto) {
        FuncUsageProcessDto funcUsageProcessDto = null;
        if (StringUtils.isEmpty(funcUsageConfigRequestDto.getOperateUser().getUserCode())) {
            return null;
        }

        final SysConfig sysConfig = sysConfigService.findConfigContentByConfigName(Constants.SYS_CONFIG_FUNC_USAGE_BY_ERP + funcUsageConfigRequestDto.getFuncCode());
        if (sysConfig == null) {
            return null;
        }

        final List<FuncUsageConfigDto> funcUsageConfigDtos = JSON.parseArray(sysConfig.getConfigContent(), FuncUsageConfigDto.class);

        final long currentTimeMillis = System.currentTimeMillis();
        for (FuncUsageConfigDto funcUsageConfigDto : funcUsageConfigDtos) {
            final FuncUsageConditionConfigDto conditionConfig = funcUsageConfigDto.getCondition();
            if (conditionConfig == null) {
                return funcUsageConfigDto.getProcess();
            }

            if (CollectionUtils.isNotEmpty(conditionConfig.getUserErps()) && conditionConfig.getUserErps().contains(funcUsageConfigRequestDto.getOperateUser().getUserCode())
                    && this.checkEffectiveTimeIsEffective(funcUsageConfigDto, currentTimeMillis)) {
                return funcUsageConfigDto.getProcess();
            }
        }

        return null;
    }

    /**
     * 获取指定具体场地或区域的配置
     */
    public FuncUsageProcessDto getFuncUsageByCodeConfig4SpecificList(FuncUsageConfigRequestDto funcUsageConfigRequestDto) {

        final SysConfig sysConfigByCode = sysConfigService.findConfigContentByConfigName(Constants.SYS_CONFIG_FUNC_USAGE_BY_SITE_CODE + funcUsageConfigRequestDto.getFuncCode());
        // 如果配置都为空，则直接返回空
        if (sysConfigByCode == null) {
            return null;
        }

        final List<FuncUsageConfigDto> funcUsageConfigDtos = JSON.parseArray(sysConfigByCode.getConfigContent(), FuncUsageConfigDto.class);
        if (CollectionUtils.isEmpty(funcUsageConfigDtos)) {
            return null;
        }
        final OperateUser operateUser = funcUsageConfigRequestDto.getOperateUser();
        final int siteCode = operateUser.getSiteCode();

        final long currentTimeMillis = System.currentTimeMillis();
        for (FuncUsageConfigDto funcUsageConfigDto : funcUsageConfigDtos) {
            final FuncUsageConditionConfigDto conditionConfig = funcUsageConfigDto.getCondition();
            // 没有条件表示全部匹配
            if (conditionConfig == null) {
                return funcUsageConfigDto.getProcess();
            }

            if((CollectionUtils.isEmpty(conditionConfig.getSiteCodes()) || conditionConfig.getSiteCodes().contains(siteCode)
                    && this.checkEffectiveTimeIsEffective(funcUsageConfigDto, currentTimeMillis))){
                return funcUsageConfigDto.getProcess();
            }
        }


        return null;
    }

    /**
     * 获取指定具体场地类型的配置
     */
    public FuncUsageProcessDto getFuncUsageByCodeConfig4SiteType(FuncUsageConfigRequestDto funcUsageConfigRequestDto, BaseSiteInfoDto siteInfo) {
        final SysConfig sysConfig = sysConfigService.findConfigContentByConfigName(Constants.SYS_CONFIG_FUNC_USAGE + funcUsageConfigRequestDto.getFuncCode());
        if (sysConfig == null) {
            return null;
        }

        final List<FuncUsageConfigDto> funcUsageConfigDtos = JSON.parseArray(sysConfig.getConfigContent(), FuncUsageConfigDto.class);

        final long currentTimeMillis = System.currentTimeMillis();
        for (FuncUsageConfigDto funcUsageConfigDto : funcUsageConfigDtos) {
            final FuncUsageConditionConfigDto conditionConfig = funcUsageConfigDto.getCondition();
            if (conditionConfig == null) {
                return funcUsageConfigDto.getProcess();
            }

            if(CollectionUtils.isEmpty(conditionConfig.getSiteType()) && CollectionUtils.isEmpty(conditionConfig.getSiteSubType())
                    && CollectionUtils.isEmpty(conditionConfig.getSiteSortType()) && CollectionUtils.isEmpty(conditionConfig.getSiteSortSubType()) && CollectionUtils.isEmpty(conditionConfig.getSiteSortThirdType())
                    && this.checkEffectiveTimeIsEffective(funcUsageConfigDto, currentTimeMillis)){
                return funcUsageConfigDto.getProcess();
            }
            if((CollectionUtils.isEmpty(conditionConfig.getSiteType()) || (siteInfo.getSiteType() != null && conditionConfig.getSiteType().contains(siteInfo.getSiteType())))
                    && (CollectionUtils.isEmpty(conditionConfig.getSiteSubType()) || (siteInfo.getSubType() != null && conditionConfig.getSiteSubType().contains(siteInfo.getSubType())))
                    && (CollectionUtils.isEmpty(conditionConfig.getSiteThirdType()) || (siteInfo.getThirdType() != null && conditionConfig.getSiteThirdType().contains(siteInfo.getThirdType())))
                    && (CollectionUtils.isEmpty(conditionConfig.getSiteSortType()) || (siteInfo.getSortType() != null && conditionConfig.getSiteSortType().contains(siteInfo.getSortType())))
                    && (CollectionUtils.isEmpty(conditionConfig.getSiteSortSubType()) || (siteInfo.getSortSubType() != null && conditionConfig.getSiteSortSubType().contains(siteInfo.getSortSubType())))
                    && (CollectionUtils.isEmpty(conditionConfig.getSiteSortThirdType()) || (siteInfo.getSortThirdType() != null && conditionConfig.getSiteSortThirdType().contains(siteInfo.getSortThirdType())))
                    && this.checkEffectiveTimeIsEffective(funcUsageConfigDto, currentTimeMillis)){
                return funcUsageConfigDto.getProcess();
            }
        }
        return null;
    }

    /**
     * 比较生效时间
     * @param funcUsageConfigDto 配置
     * @param compareTimeMillis 比较时间
     * @return 是否生效
     */
    private boolean checkEffectiveTimeIsEffective(FuncUsageConfigDto funcUsageConfigDto, long compareTimeMillis){
        if (funcUsageConfigDto.getEffectiveTime() == null) {
            return true;
        }
        // 计算生效时间
        if(funcUsageConfigDto.getEffectiveTimeFormatStr() == null){
            funcUsageConfigDto.setEffectiveTimeFormatStr(DateUtil.FORMAT_DATE_TIME);
        }
        final Date effectiveDate = DateUtil.parse(funcUsageConfigDto.getEffectiveTime(), funcUsageConfigDto.getEffectiveTimeFormatStr());
        if(effectiveDate.getTime() < compareTimeMillis){
            return true;
        }
        return false;
    }
}
