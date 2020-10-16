package com.jd.bluedragon.distribution.funcSwitchConfig.service.impl;

import com.jd.bd.dms.automatic.sdk.common.constant.WeightValidateSwitchEnum;
import com.jd.bd.dms.automatic.sdk.common.dto.BaseDmsAutoJsfResponse;
import com.jd.bd.dms.automatic.sdk.modules.device.DeviceConfigInfoJsfService;
import com.jd.bd.dms.automatic.sdk.modules.device.dto.DeviceConfigDto;
import com.jd.bluedragon.core.base.BaseMajorManager;
import com.jd.bluedragon.distribution.api.domain.LoginUser;
import com.jd.bluedragon.distribution.funcSwitchConfig.FuncSwitchConfigDto;
import com.jd.bluedragon.distribution.funcSwitchConfig.FuncSwitchConfigEnum;
import com.jd.bluedragon.distribution.funcSwitchConfig.dao.FuncSwitchConfigDao;
import com.jd.bluedragon.distribution.funcSwitchConfig.domain.FuncSwitchConfigCondition;
import com.jd.bluedragon.distribution.funcSwitchConfig.service.FuncSwitchConfigService;
import com.jd.bluedragon.distribution.rule.dao.RuleDao;
import com.jd.bluedragon.distribution.rule.domain.Rule;
import com.jd.bluedragon.distribution.whitelist.DimensionEnum;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.ql.basic.dto.BaseStaffSiteOrgDto;
import com.jd.ql.dms.common.domain.JdResponse;
import com.jd.ql.dms.common.web.mvc.api.PagerResult;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 功能开关配置SERVICE
 *
 * @author: hujiping
 * @date: 2020/9/17 10:24
 */
@Service("funcSwitchConfigService")
public class FuncSwitchConfigServiceImpl implements FuncSwitchConfigService {

    private static final Logger logger = LoggerFactory.getLogger(FuncSwitchConfigServiceImpl.class);

    // 导入限制 1000条
    private static final Integer IMPORT_LIMIT_MAX = 1000;
    //预售暂存分拣规则：2100 content为1表示开启
    private static final Integer PRE_SELL_RULE_TYPE = 2100;
    private static final String PRE_SELL_RULE_OPEN = "1";
    private static final String PRE_SELL_RULE_CONTENT = "预售暂存分拣规则，1表示开启";
    private static final Integer YN_OFF = 1;//开关有效标识

    @Value("${checkAuthoritySwitch:true}")
    private boolean checkAuthoritySwitch;

    @Autowired
    private FuncSwitchConfigDao funcSwitchConfigDao;

    @Autowired
    private RuleDao ruleDao;

//    @Autowired
//    private HrmPrivilegeHelper hrmPrivilegeHelper;

    @Autowired
    private BaseMajorManager baseMajorManager;

    @Autowired
    private DeviceConfigInfoJsfService deviceConfigInfoJsfService;

    /**
     * 根据条件分页查询
     * @param condition
     * @return
     */
    @Override
    public PagerResult<FuncSwitchConfigDto> queryByCondition(FuncSwitchConfigCondition condition) {
        PagerResult<FuncSwitchConfigDto> result = new PagerResult<FuncSwitchConfigDto>();
        result.setRows(funcSwitchConfigDao.queryByCondition(condition));
        result.setTotal(funcSwitchConfigDao.queryCountByCondition(condition));
        return result;
    }

    /**
     * 新增
     * @param funcSwitchConfigDto
     * @return
     */
    @Transactional
    @Override
    public JdResponse insert(FuncSwitchConfigDto funcSwitchConfigDto) {
        JdResponse jdResponse = new JdResponse();
        try {
            if(checkAuthority(funcSwitchConfigDto.getMenuCode(),funcSwitchConfigDto.getCreateErp(),jdResponse)){
                return jdResponse;
            }
            if(CollectionUtils.isNotEmpty(funcSwitchConfigDao.selectByFuncSwitchConfig(funcSwitchConfigDto))){
                jdResponse.toFail("已存在相同配置,请勿重复添加!");
                return jdResponse;
            }
            Date date = new Date();
            funcSwitchConfigDto.setCreateTime(date);
            funcSwitchConfigDto.setUpdateTime(date);

            //众邮调度分拣机开关
            if(FuncSwitchConfigEnum.FUNCTION_ALL_MAIL.getCode()==funcSwitchConfigDto.getMenuCode()){
                BaseDmsAutoJsfResponse<Long> longBaseDmsAutoJsfResponse = null;
                if(funcSwitchConfigDto.getDimensionCode()==DimensionEnum.NATIONAL.getCode()){
                    longBaseDmsAutoJsfResponse = deviceConfigInfoJsfService.maintainWeightSwitch(funcSwitchConfigDto.getYn()==YN_OFF?WeightValidateSwitchEnum.OFF:WeightValidateSwitchEnum.ON);
                    if(longBaseDmsAutoJsfResponse.getStatusCode()!=BaseDmsAutoJsfResponse.SUCCESS_CODE){
                        jdResponse.toFail("众邮分拣机开关调用失败,全国");
                        return jdResponse;
                    }
                }else {
                    Integer[] siteCodes = {funcSwitchConfigDto.getSiteCode()};
                    if(!(siteCodes.length>0)){
                        jdResponse.toFail("众邮分拣机开关调用失败,缺少站点编码:");
                        return jdResponse;
                    }
                    longBaseDmsAutoJsfResponse = deviceConfigInfoJsfService.maintainSiteWeightSwitch(siteCodes,funcSwitchConfigDto.getYn()==YN_OFF?WeightValidateSwitchEnum.OFF:WeightValidateSwitchEnum.ON);
                    if(longBaseDmsAutoJsfResponse.getStatusCode()!=BaseDmsAutoJsfResponse.SUCCESS_CODE){
                        jdResponse.toFail("众邮分拣机开关调用失败,站点:"+siteCodes);
                        return jdResponse;
                    }
                }
            }

            funcSwitchConfigDao.add(funcSwitchConfigDto);
            if(FuncSwitchConfigEnum.FUNCTION_PRE_SELL.getCode() == funcSwitchConfigDto.getMenuCode()){
                // 预售分拣暂存功能 记录预售的分拣规则
                Rule rule = new Rule();
                rule.setSiteCode(funcSwitchConfigDto.getSiteCode());
                rule.setType(PRE_SELL_RULE_TYPE);
                rule.setContent(PRE_SELL_RULE_OPEN);
                rule.setInOut(Rule.IN);
                rule.setMemo(PRE_SELL_RULE_CONTENT);
                rule.setCreateUser(funcSwitchConfigDto.getCreateErp());
                rule.setUpdateUser(funcSwitchConfigDto.getCreateErp());
                ruleDao.add(rule);
            }
        }catch (Exception e){
            logger.error("新增功能开关配置异常,入参【】", JsonHelper.toJson(funcSwitchConfigDto),e);
        }
        return jdResponse;
    }

    /**
     * 批量新增
     * @param list
     * @return
     */
    @Override
    public JdResponse batchInsert(List<FuncSwitchConfigDto> list) {
        JdResponse jdResponse = new JdResponse();
        if(CollectionUtils.isEmpty(list)){
            jdResponse.toFail("参数为空!");
            return jdResponse;
        }

        int count = funcSwitchConfigDao.batchAdd(list);
        if(count != list.size()){
            jdResponse.toFail("批量新增部分成功!");
        }
        return jdResponse;
    }

    /**
     * 变更
     * @param funcSwitchConfigDto
     * @return
     */
    @Override
    public JdResponse update(FuncSwitchConfigDto funcSwitchConfigDto) {
        JdResponse jdResponse = new JdResponse();
        try {
//            if(checkAuthority(funcSwitchConfigDto,jdResponse)){
//                return jdResponse;
//            }
//            funcSwitchConfigDao.updateByFuncSwitchConfig(funcSwitchConfigDto);
        }catch (Exception e){
            logger.error("变更功能开关配置异常,入参【】", JsonHelper.toJson(funcSwitchConfigDto),e);
        }
        return jdResponse;
    }

    /**
     * 校验用户权限
     * @param menuCode
     * @param loginErp
     * @param jdResponse
     * @param jdResponse
     */
    private boolean checkAuthority(Integer menuCode, String loginErp, JdResponse jdResponse) {
        FuncSwitchConfigEnum funcSwitchConfigEnum = null;
        if(FuncSwitchConfigEnum.codeMap.containsKey(menuCode)){
            funcSwitchConfigEnum = FuncSwitchConfigEnum.codeMap.get(menuCode);
        }
        if(funcSwitchConfigEnum == null){
            jdResponse.toFail("无用的功能编码!");
            return true;
        }
//        // 测试环境跳过权限验证
//        if(!checkAuthoritySwitch){
//            return false;
//        }
//        if(!hrmPrivilegeHelper.hasHrmPrivilege(loginErp, funcSwitchConfigEnum.getAuthCode())){
//            jdResponse.toFail(String.format("无权限操作，请申请【%s】的权限!", funcSwitchConfigEnum.getAuthCode()));
//            return true;
//        }
        return false;
    }

    /**
     * 逻辑删除
     *   置为无效
     * @param funcSwitchConfigDtos
     * @param loginErp
     * @return
     */
    @Override
    public JdResponse logicalDelete(List<FuncSwitchConfigDto> funcSwitchConfigDtos,String loginErp) {
        JdResponse jdResponse = new JdResponse();
        try {
            if(CollectionUtils.isEmpty(funcSwitchConfigDtos)){
                jdResponse.toFail("参数为空!");
                return jdResponse;
            }
            List<Long> ids = new ArrayList<>();

            //站点编码集合
            List<Integer> siteCodes = new ArrayList<>();
            BaseDmsAutoJsfResponse  longBaseDmsAutoJsfResponse = null;
            for(FuncSwitchConfigDto dto : funcSwitchConfigDtos){
                if(checkAuthority(dto.getMenuCode(),loginErp,jdResponse)){
                    return jdResponse;
                }
                //众邮-调用分拣机开关进行更新
                if(FuncSwitchConfigEnum.FUNCTION_ALL_MAIL.getCode()==dto.getMenuCode()){
                    if(dto.getDimensionCode()==DimensionEnum.NATIONAL.getCode()){
                        longBaseDmsAutoJsfResponse = deviceConfigInfoJsfService.maintainWeightSwitch(WeightValidateSwitchEnum.ON);
                        if(longBaseDmsAutoJsfResponse.getStatusCode()!=BaseDmsAutoJsfResponse.SUCCESS_CODE){
                            jdResponse.toFail("众邮分拣机开关调用失败,全国");
                            return jdResponse;
                        }
                    }else {
                        siteCodes.add(dto.getSiteCode());
                    }
                }
                ids.add(dto.getId());
            }

            if(siteCodes.size()>0){
                Integer[] siteCodesArray = (Integer[]) siteCodes.toArray();
                longBaseDmsAutoJsfResponse = deviceConfigInfoJsfService.maintainSiteWeightSwitch(siteCodesArray,WeightValidateSwitchEnum.ON);
                if(longBaseDmsAutoJsfResponse.getStatusCode()!=BaseDmsAutoJsfResponse.SUCCESS_CODE){
                    jdResponse.toFail("众邮分拣机开关调用失败,站点:"+siteCodes);
                    return jdResponse;
                }
            }

            if(ids.size()>0){
                funcSwitchConfigDao.logicalDelete(ids);
            }
        }catch (Exception e){
            logger.error("逻辑删除异常,入参【{}】", JsonHelper.toJson(funcSwitchConfigDtos),e);
        }
        return jdResponse;
    }

    /**
     * 校验表单数据
     * @param dataList
     * @param loginErp
     * @param jdResponse
     */
    @Override
    public void checkExportData(List<FuncSwitchConfigDto> dataList, String loginErp, JdResponse jdResponse) {
        if (CollectionUtils.isEmpty(dataList)) {
            jdResponse.toFail("导入表格数据为空，请检查excel数据!");
            return;
        }
        if (dataList.size() > IMPORT_LIMIT_MAX) {
            jdResponse.toFail(String.format("导入数据超出%s条,请分批导入!",IMPORT_LIMIT_MAX));
            return;
        }
        try {
            int rowIndex = 1;
            for (FuncSwitchConfigDto dto : dataList){
                if(checkAuthority(dto.getMenuCode(),loginErp,jdResponse)){
                    return;
                }
                if(!DimensionEnum.codeMap.containsKey(dto.getDimensionCode())){
                    jdResponse.toFail(String.format("第%s行维度编码不存在,请重新录入!",rowIndex));
                    return;
                }
                BaseStaffSiteOrgDto baseDto = baseMajorManager.getBaseSiteBySiteId(dto.getSiteCode());
                if(baseDto == null){
                    jdResponse.toFail(String.format("第%s行站点ID不存在,请重新录入!",rowIndex));
                    return;
                }
                if(!dto.getSiteName().equals(baseDto.getSiteName())){
                    jdResponse.toFail(String.format("第%s行站点名称不正确,请重新录入!",rowIndex));
                    return;
                }
                if(!dto.getOrgId().equals(baseDto.getOrgId())
                        || !dto.getOrgName().equals(baseDto.getOrgName())){
                    jdResponse.toFail(String.format("第%s行区域不正确,请重新录入!",rowIndex));
                    return;
                }
                if(CollectionUtils.isNotEmpty(funcSwitchConfigDao.selectByFuncSwitchConfig(dto))){
                    jdResponse.toFail(String.format("第%s行已存在相同配置,请勿重复添加!",rowIndex));
                    return;
                }
                rowIndex ++;
            }
        }catch (Exception e){
            logger.error("校验表格数据异常!");
            jdResponse.toError(JdResponse.MESSAGE_ERROR);
        }
    }

    /**
     * 批量插入
     * @param dataList
     * @param loginUser
     */
    @Override
    public void importExcel(List<FuncSwitchConfigDto> dataList, LoginUser loginUser) {
        if(CollectionUtils.isEmpty(dataList)){
            return;
        }
        // 设置默认参数
        for (FuncSwitchConfigDto dto : dataList){
            dto.setMenuName(FuncSwitchConfigEnum.interceptMenuEnumMap.get(dto.getMenuCode()));
            dto.setDimensionName(DimensionEnum.dimensionEnumMap.get(dto.getDimensionCode()));
            dto.setCreateErp(loginUser.getUserErp());
            dto.setCreateUser(loginUser.getUserName());
            Date date = new Date();
            dto.setCreateTime(date);
            dto.setUpdateTime(date);
        }
        funcSwitchConfigDao.batchAdd(dataList);
    }

    /**
     * 根据条件查询
     * @param dto
     * @return
     */
    @Override
    public List<FuncSwitchConfigDto> getFuncSwitchConfigs(FuncSwitchConfigDto dto) {
        if(dto == null || dto.getMenuCode() == null
                || !FuncSwitchConfigEnum.codeMap.containsKey(dto.getMenuCode())){
            return null;
        }
        return funcSwitchConfigDao.selectByFuncSwitchConfig(dto);
    }

    /**
     * 校验是否配置功能
     * @param dto
     * @return
     */
    @Override
    public boolean checkIsConfigured(FuncSwitchConfigDto dto) {
        if(dto == null || dto.getMenuCode() == null
                || dto.getDimensionCode() == null || dto.getSiteCode() == null){
            return false;
        }
        return funcSwitchConfigDao.selectConfiguredCount(dto) > 0;
    }

}
