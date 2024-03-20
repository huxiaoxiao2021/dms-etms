package com.jd.bluedragon.distribution.workingConfig;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.dms.wb.report.api.dto.base.BaseEntity;
import com.jd.dms.wb.report.api.working.WorkingConfigJsfService;
import com.jd.dms.wb.report.api.working.dto.WorkingActionDto;
import com.jd.dms.wb.report.sdk.api.outsource.OutSourceProvisionQueryApi;
import com.jd.dms.wb.report.sdk.model.vo.working.data.SupplierVO;
import com.jd.tys.api.common.dto.Result;
import com.jd.tys.api.outsource.dto.OutsourceCountRuleDto;
import com.jd.tys.api.outsource.dto.OutsourceCountRuleResultDto;
import com.jd.tys.api.outsource.ws.OutsourceCountRuleServiceWS;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.Collections;
import java.util.List;

/**
 * @ProjectName：bluedragon-distribution
 * @Package： com.jd.bluedragon.distribution.workingConfig
 * @ClassName: WorkingConfigProxy
 * @Description:
 * @Author： wuzuxiang
 * @CreateDate 2022/5/15 22:12
 * @Copyright: Copyright (c)2020 JDL.CN All Right Reserved
 * @Since: JDK 1.8
 * @Version： V1.0
 */
@Service
public class WorkingConfigProxy {

    private static final Logger log = LoggerFactory.getLogger(WorkingConfigProxy.class);

    @Autowired
    private WorkingConfigJsfService workingConfigJsfService;

    @Autowired
    private OutsourceCountRuleServiceWS outsourceCountRuleServiceWS;
    @Autowired
    private OutSourceProvisionQueryApi outSourceProvisionQueryApi;

    @JProfiler(jKey = "DMSWEB.WorkingConfigProxy.queryDMSWorkingConfig", jAppName = Constants.UMP_APP_NAME_DMSWEB)
    public List<WorkingActionDto> queryDMSWorkingConfig(Integer siteId, Integer workingPositionAttr) {
        log.info("WorkingConfigProxy#queryDMSWorkingConfig查询参数：{}-{}", siteId, workingPositionAttr);
        BaseEntity<List<WorkingActionDto>> baseEntity = workingConfigJsfService.queryActionBySiteIdAndPositionCode(siteId, workingPositionAttr);
        log.info("WorkingConfigProxy#queryDMSWorkingConfig查询结果：{}", JsonHelper.toJson(baseEntity));
        if (baseEntity == null || CollectionUtils.isEmpty(baseEntity.getData())) {
            log.info("WorkingConfigProxy#queryDMSWorkingConfig查询失败，参数为{}-{}", siteId, workingPositionAttr);
            return Collections.emptyList();
        }
        return baseEntity.getData();

    }

    @JProfiler(jKey = "DMSWEB.WorkingConfigProxy.queryTYSWorkingConfig", jAppName = Constants.UMP_APP_NAME_DMSWEB)
    public List<OutsourceCountRuleDto> queryTYSWorkingConfig(Integer dmsCode, Integer businessNode) {
        log.info("WorkingConfigProxy#queryTYSWorkingConfig查询参数：{}-{}", dmsCode, businessNode);
        OutsourceCountRuleDto ruleDto = new OutsourceCountRuleDto();
        ruleDto.setStatus(2);
        ruleDto.setCreateSiteCode(dmsCode);
        ruleDto.setBusinessNode(businessNode);
        Result<OutsourceCountRuleResultDto> result = outsourceCountRuleServiceWS.getAllRules(ruleDto);
        log.info("WorkingConfigProxy#queryTYSWorkingConfig查询结果：{}",JsonHelper.toJson(result));
        if (result == null || result.getData() == null || CollectionUtils.isEmpty(result.getData().getCountRuleDtos())) {
            log.info("WorkingConfigProxy#queryTYSWorkingConfig查询失败，参数：{}-{}", dmsCode, businessNode);
            return Collections.emptyList();
        }
        return result.getData().getCountRuleDtos();
    }

    @JProfiler(jKey = "DMSWEB.WorkingConfigProxy.querySupplierBySiteCode", jAppName = Constants.UMP_APP_NAME_DMSWEB)
    public List<SupplierVO> querySupplierBySiteCode(Integer siteId) {
        log.info("根据场地id查询外包计提配置外包商信息,siteId:{}", siteId);
        com.jd.dms.wb.report.sdk.model.base.BaseEntity<List<SupplierVO>> listBaseEntity =
                null;
        try {
            listBaseEntity = outSourceProvisionQueryApi.querySupplierBySiteCode(siteId);
        } catch (Exception e) {
            log.error("根据场地id查询外包计提配置外包商信息异常,siteId:{}", siteId, e);
            return null;
        }
        if(listBaseEntity == null){
            log.info("根据场地id查询外包计提配置外包商信息,方法返回值为空,siteId:{}", siteId);
            return null;
        }
        if(CollectionUtils.isEmpty(listBaseEntity.getData())){
            log.info("根据场地id查询外包计提配置外包商信息,未查到外包商信息,siteId:{}", siteId);
            return null;
        }
        log.info("根据场地id查询外包计提配置外包商信息,查到外包商数量，siteId:{},count:{}", siteId, listBaseEntity.getData().size());
        return listBaseEntity.getData();
    }
}
