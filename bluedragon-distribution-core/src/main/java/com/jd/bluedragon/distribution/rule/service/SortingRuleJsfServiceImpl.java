package com.jd.bluedragon.distribution.rule.service;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.distribution.api.Response;
import com.jd.bluedragon.distribution.base.domain.SysConfig;
import com.jd.bluedragon.distribution.base.service.SiteService;
import com.jd.bluedragon.distribution.base.service.SysConfigService;
import com.jd.bluedragon.distribution.rule.domain.Rule;
import com.jd.bluedragon.distribution.rule.dto.SortingRuleDto;
import com.jd.bluedragon.distribution.rule.dto.SortingRuleTypeEnum;
import com.jd.bluedragon.utils.BeanCopyUtil;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.framework.AopContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

import static com.jd.bluedragon.Constants.SEPARATOR_COMMA;

/**
 * 天官赐福 ◎ 百无禁忌
 *
 * @Auther: 刘铎（liuduo8）
 * @Date: 2021/2/21 19:18
 * @Description:
 */
@Service("sortingRuleJsfService")
public class SortingRuleJsfServiceImpl implements SortingRuleJsfService {

    private static final String SWITCH_ON = "1";

    private static final String SWITCH_OFF_DEFAULT_ZERO = "0";


    private Logger logger = LoggerFactory.getLogger(SortingRuleJsfServiceImpl.class);

    @Autowired
    private RuleService ruleService;

    @Autowired
    private SiteService siteService;

    @Autowired
    private SysConfigService sysConfigService;
    /**
     * 批量获取规则
     *
     * @param siteCode
     * @param ruleTypes
     * @return
     */
    @Override
    @JProfiler(jKey = "DMSWEB.SortingRuleJsfServiceImpl.findRules", mState = {JProEnum.TP, JProEnum.FunctionError})
    public Response<List<SortingRuleDto>> findRules(Integer siteCode, List<String> ruleTypes) {
        Response<List<SortingRuleDto>> response = new Response<>();
        response.toSucceed();
        List<SortingRuleDto> ruleDtos = new ArrayList<>();
        List<Rule> rules =ruleService.getByTypes(siteCode, ruleTypes);
        if(rules.isEmpty()){
            response.toError("未获取到对应分拣规则");
            return response;
        }
        Map<String,Rule> ruleMap = new HashMap<>();
        for(Rule rule : rules){
            ruleMap.put(String.valueOf(rule.getType()),rule);

        }

        for(String ruleType : ruleTypes){
            Rule rule = ruleMap.get(ruleType);
            SortingRuleDto ruleDto = new SortingRuleDto();
            ruleDto.setSiteCode(siteCode);
            if(rule!=null){
                BeanCopyUtil.copy(rule,ruleDto);
            }else {
                ruleDto.setType(Integer.valueOf(ruleType));
            }
            initOpenFlag(ruleDto,rule != null);
            ruleDtos.add(ruleDto);
        }
        response.setData(ruleDtos);

        return response;

    }

    /**
     * 批量变更规则
     * 全部成功才会返回成功
     *
     * @param sortingRuleDtos
     * @return
     */
    @Override
    @JProfiler(jKey = "DMSWEB.SortingRuleJsfServiceImpl.changeRules", mState = {JProEnum.TP, JProEnum.FunctionError})
    @Transactional(value = "main_base", rollbackFor = Throwable.class)
    public Response<Boolean> changeRules(Integer siteCode,String userErp,List<SortingRuleDto> sortingRuleDtos) {

        Response<Boolean> response = new Response<>();
        response.toSucceed();
        //组装所需数据结构
        List<String> ruleTypes = new ArrayList<>();
        for(SortingRuleDto sortingRuleDto : sortingRuleDtos){
            ruleTypes.add(String.valueOf(sortingRuleDto.getType()));
        }


        //已存在规则修改 不存在规则判断是否需要新增
        List<Rule> rules =ruleService.getByTypes(siteCode, ruleTypes);
        Map<String,Rule> ruleMap = new HashMap<>();
        for(Rule rule : rules){
            ruleMap.put(String.valueOf(rule.getType()),rule);
        }

        for(SortingRuleDto sortingRuleDto : sortingRuleDtos){
            boolean isExist = true;
            sortingRuleDto.setUpdateUser(userErp);
            //不存在 新增时需要补充字段
            Rule rule = ruleMap.get(String.valueOf(sortingRuleDto.getType()));
            if(rule == null){
                rule = new Rule();
                rule.setCreateUser(userErp);
                rule.setSiteCode(siteCode);
                rule.setType(sortingRuleDto.getType());
                isExist = false;
            }
            //有一个失败的就回滚事务
            if(!changeRule(rule,sortingRuleDto.getOpenFlag(),isExist)){
                throw new RuntimeException("更新规则失败！请稍后重试");
            }
        }

        response.setData(Boolean.TRUE);
        return response;
    }

    /**
     * 变更分拣规则
     * @param rule
     * @param openFlag
     * @param isExist
     * @return
     */
    private boolean changeRule(Rule rule,boolean openFlag,boolean isExist){
        String ruleType = String.valueOf(rule.getType());
        if(SortingRuleTypeEnum.RULE_1120.getCode().equals(ruleType)){
            return change1120Rule(rule,openFlag,isExist);
        }else if(SortingRuleTypeEnum.RULE_1121.getCode().equals(ruleType)){
            return change1121Rule(rule,openFlag,isExist);
        }else if(SortingRuleTypeEnum.RULE_1122.getCode().equals(ruleType)){
            return change1122Rule(rule,openFlag,isExist);
        }else if(SortingRuleTypeEnum.RULE_1125.getCode().equals(ruleType)){
            return change1125Rule(rule,openFlag,isExist);
        }else{
            logger.error("changeRule 不支持的分拣规则类型,默认发货成功 {}", JsonHelper.toJson(rule));
        }
        return true;
    }


    private boolean change1120Rule(Rule rule,boolean openFlag,boolean isExist){
        //1120 规则 判断开启条件 为 不存在此规则 或者 存在时inOut == IN
        if(openFlag){
            //开启
            if(isExist){
                //修改 inOut == IN
                rule.setInOut(Rule.IN);
                return ruleService.update(rule);
            }
        }else{
            rule.setInOut(Rule.OUT);
            //关闭
            if(isExist){
                //修改 inOut == OUT
                return ruleService.update(rule);
            }else{
                //不存在 新增
                return ruleService.add(rule);
            }
        }
        return true;
    }

    private boolean change1121Rule(Rule rule,boolean openFlag,boolean isExist){
        //1121 规则 判断开启条件 为 不存在此规则 或者 存在时 content 不等于 1

        return changeRuleOfNoExistOrContentNot1(rule,openFlag,isExist);
    }


    private boolean change1122Rule(Rule rule,boolean openFlag,boolean isExist){
        //1122 规则 判断开启条件 为 不存在此规则 或者 存在时 content 不等于 1

        if(changeRuleOfNoExistOrContentNot1(rule,openFlag,isExist)){
            //1122 规则需要继续修改config列表

            return siteService.changeRouterConfig(rule.getSiteCode(),openFlag);
        }

        return false;
    }


    private boolean change1125Rule(Rule rule,boolean openFlag,boolean isExist){
        //1125 规则 判断开启条件 为 存在此规则 并且 content 等于 1

        if(openFlag){
            //修改 content 等于 1
            rule.setContent(SWITCH_ON);
            //开启
            if(isExist){
                return ruleService.update(rule);
            }else{
                //不存在 新增
                return ruleService.add(rule);
            }
        }else{
            //关闭
            //修改 content 不等于 1
            rule.setContent(SWITCH_OFF_DEFAULT_ZERO);
            if(isExist){
                return ruleService.update(rule);
            }
        }
        return true;
    }

    /**
     * 判断开启条件 为 不存在此规则 或者 存在时 content 不等于 1
     * @return
     */
    private boolean changeRuleOfNoExistOrContentNot1(Rule rule,boolean openFlag,boolean isExist){
        if(openFlag){
            //开启
            if(isExist){
                //修改 content 不等于 1
                rule.setContent(SWITCH_OFF_DEFAULT_ZERO);
                return ruleService.update(rule);
            }
        }else{
            //关闭
            //修改 content 等于 1
            rule.setContent(SWITCH_ON);
            if(isExist){
                return ruleService.update(rule);
            }else{
                //不存在 新增
                rule.setInOut(Rule.IN);
                return ruleService.add(rule);
            }
        }
        return true;
    }

    /**
     * 初始化 open flag
     * @param rule
     */
    private void initOpenFlag(SortingRuleDto rule,boolean isExist){
        // 与分拣规则校验中使用的规则保持一致
        String ruleType = String.valueOf(rule.getType());
        boolean flag = false;
        if(SortingRuleTypeEnum.RULE_1120.getCode().equals(ruleType)){
            flag = !isExist || Rule.IN.equals(rule.getInOut());
        }else if(SortingRuleTypeEnum.RULE_1121.getCode().equals(ruleType)){
            flag = !isExist || !SWITCH_ON.equals(rule.getContent());
        }else if(SortingRuleTypeEnum.RULE_1122.getCode().equals(ruleType)){
            //路由校验比较特殊需要在去查询config配置
            flag = (!isExist || !SWITCH_ON.equals(rule.getContent())) && isExistOfRouterFlag(rule.getSiteCode());
        }else if(SortingRuleTypeEnum.RULE_1125.getCode().equals(ruleType)){
            flag = isExist && SWITCH_ON.equals(rule.getContent());
        }
        rule.setOpenFlag(flag);
    }

    /**
     * 检查路由校验配置是否存在
     * @param siteCode
     * @return
     */
    private boolean isExistOfRouterFlag(Integer siteCode){
        return siteService.getCRouterAllowedListNoCache().contains(siteCode);
    }
}
