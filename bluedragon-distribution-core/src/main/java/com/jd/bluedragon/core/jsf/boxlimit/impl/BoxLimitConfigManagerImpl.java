package com.jd.bluedragon.core.jsf.boxlimit.impl;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.core.jsf.boxlimit.BoxLimitConfigManager;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import com.jdl.basic.api.domain.boxFlow.CollectBoxFlowDirectionConf;
import com.jdl.basic.api.enums.FlowDirectionTypeEnum;
import com.jdl.basic.api.service.boxFlow.CollectBoxFlowDirectionConfJsfService;
import com.jdl.basic.api.service.boxLimit.BoxlimitConfigJsfService;
import com.jdl.basic.common.utils.JsonHelper;
import com.jdl.basic.common.utils.ObjectHelper;
import com.jdl.basic.common.utils.Result;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.jdl.basic.common.utils.Pager;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import static com.jdl.basic.api.domain.boxFlow.CollectBoxFlowDirectionConf.COLLECT_CLAIM_MIX;
import static org.apache.commons.lang3.math.NumberUtils.INTEGER_ONE;

/**
 * @Author: chenyaguo@jd.com
 * @Date: 2022/8/2 14:27
 * @Description:
 */
@Slf4j
@Service("boxLimitConfigManager")
public class BoxLimitConfigManagerImpl implements BoxLimitConfigManager {

    @Autowired
    private BoxlimitConfigJsfService basicBoxlimitConfigJsfService;
    @Autowired
    CollectBoxFlowDirectionConfJsfService collectBoxFlowDirectionConfJsfService;

    @Override
    @JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWEB,jKey = "BoxLimitConfigManagerImpl.getLimitNums",mState={JProEnum.TP,JProEnum.FunctionError})
    public Integer getLimitNums(Integer createSiteCode, String type) {
        log.info("调用拣运基础服务集箱包裹配置信息 入参 {}-{}",createSiteCode,type);
        try{
            Result<Integer> response = basicBoxlimitConfigJsfService.getLimitNums(createSiteCode, type);
            if(response != null && response.getData() != null){
                return response.getData();
            }
        }catch (Exception e){
            log.info("调用拣运基础服务集箱包裹配置信息异常 {}",e.getMessage(),e);
        }
        return null;
    }

    @Override
    public List<CollectBoxFlowDirectionConf> listCollectBoxFlowDirection(CollectBoxFlowDirectionConf collectBoxFlowDirectionConf, List<Integer> collectClaimList) {
        if (log.isInfoEnabled()){
            log.info("小件集包查询集包规则参数：{}", JsonHelper.toJSONString(collectBoxFlowDirectionConf));
        }
        Result<String> versionRs =collectBoxFlowDirectionConfJsfService.getCurrentVersion();
        if (log.isInfoEnabled()){
            log.info("小件集包查询集包规则获取最新版本：{}",JsonHelper.toJSONString(versionRs));
        }
        if (ObjectHelper.isNotNull(versionRs) && versionRs.isSuccess() && ObjectHelper.isNotNull(versionRs.getData())){
            Pager pager = getPager(collectBoxFlowDirectionConf, versionRs);
            pager.setSearchVo(collectBoxFlowDirectionConf);
            // 查询主建包流向
            List<CollectBoxFlowDirectionConf> mainFlowList = getCollectBoxFlowDirectionConfs(collectClaimList, pager);

            // 查询备用建包流向
            Pager deputyPager = getDeputyPager(collectBoxFlowDirectionConf, versionRs);
            List<CollectBoxFlowDirectionConf> deputyFlowList = getCollectBoxFlowDirectionConfs(collectClaimList, deputyPager);

            // 合并主备建包流向
            return mergeFlowList(mainFlowList, deputyFlowList);
        }
        return null;
    }

    /**
     * 根据目的地id去重
     * @param mainFlowList
     * @param deputyFlowList
     * @return
     */
    private List<CollectBoxFlowDirectionConf> mergeFlowList(List<CollectBoxFlowDirectionConf> mainFlowList, List<CollectBoxFlowDirectionConf> deputyFlowList) {
        List<CollectBoxFlowDirectionConf> flowList = new ArrayList<>();
        HashSet<Integer> endSiteSet = new HashSet<>();
        for (CollectBoxFlowDirectionConf conf : mainFlowList) {
            if (endSiteSet.contains(conf.getEndSiteId())) {
                continue;
            }
            endSiteSet.add(conf.getEndSiteId());
            flowList.add(conf);
        }
        for (CollectBoxFlowDirectionConf conf : deputyFlowList) {
            if (endSiteSet.contains(conf.getEndSiteId())) {
                continue;
            }
            endSiteSet.add(conf.getEndSiteId());
            flowList.add(conf);
        }
        return flowList;
    }

    private static Pager getDeputyPager(CollectBoxFlowDirectionConf collectBoxFlowDirectionConf, Result<String> versionRs) {
        CollectBoxFlowDirectionConf deputyConf = new CollectBoxFlowDirectionConf();
        Pager deputyPager = getPager(deputyConf, versionRs);
        deputyConf.setStartSiteId(collectBoxFlowDirectionConf.getStartSiteId());
        deputyConf.setDeputyBoxReceiveId(collectBoxFlowDirectionConf.getBoxReceiveId());
        deputyConf.setFlowType(FlowDirectionTypeEnum.OUT_SITE.getCode());
        deputyConf.setEndSiteId(collectBoxFlowDirectionConf.getEndSiteId());
        deputyConf.setEndSiteName(collectBoxFlowDirectionConf.getEndSiteName());
        deputyConf.setSupportDeputyReceiveSite(INTEGER_ONE);
        deputyConf.setTransportType(collectBoxFlowDirectionConf.getTransportType());
        deputyPager.setSearchVo(deputyConf);
        return deputyPager;
    }

    private static Pager getPager(CollectBoxFlowDirectionConf collectBoxFlowDirectionConf, Result<String> versionRs) {
        Pager pager =new Pager();
        pager.setPageNo(Constants.DEFAULT_PAGE_NO);
        pager.setPageSize(Constants.DEFAULT_PAGE_SIZE_LIMIT);
        collectBoxFlowDirectionConf.setVersion(versionRs.getData());
        return pager;
    }


    private List<CollectBoxFlowDirectionConf> getCollectBoxFlowDirectionConfs(List<Integer> collectClaimList, Pager pager) {
        List<CollectBoxFlowDirectionConf> list = new ArrayList<>();
        Result<Pager<CollectBoxFlowDirectionConf>> rs =collectBoxFlowDirectionConfJsfService.listByParamAndWhetherConfiged(pager,null);
        if (log.isInfoEnabled()){
            log.info("小件集包查询集包规则结果：{}",JsonHelper.toJSONString(rs));
        }
        if (ObjectHelper.isNotNull(rs) && rs.isSuccess() && ObjectHelper.isNotNull(rs.getData()) && CollectionUtils.isNotEmpty(rs.getData().getData())){

            if (CollectionUtils.isEmpty(collectClaimList)){
                return rs.getData().getData();
            }

            for (CollectBoxFlowDirectionConf datum : rs.getData().getData()) {
                if (collectClaimList.contains(datum.getCollectClaim())) {
                    list.add(datum);
                }
            }
        }
        return list;
    }
}
